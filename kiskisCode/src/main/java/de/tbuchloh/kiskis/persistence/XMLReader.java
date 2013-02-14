/*
 * Copyright (C) 2004 by Tobias Buchloh.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.sourceforge.net/projects/KisKis
 */

package de.tbuchloh.kiskis.persistence;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

import de.tbuchloh.kiskis.model.Attachment;
import de.tbuchloh.kiskis.model.BankAccount;
import de.tbuchloh.kiskis.model.CreditCard;
import de.tbuchloh.kiskis.model.GenericAccount;
import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.NetAccount;
import de.tbuchloh.kiskis.model.Password;
import de.tbuchloh.kiskis.model.PasswordHistory;
import de.tbuchloh.kiskis.model.SecuredElement;
import de.tbuchloh.kiskis.model.SecuredFile;
import de.tbuchloh.kiskis.model.TAN;
import de.tbuchloh.kiskis.model.TANList;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.model.template.AccountProperty;
import de.tbuchloh.kiskis.model.template.AccountPropertyTypes;
import de.tbuchloh.kiskis.model.template.AccountType;
import de.tbuchloh.kiskis.model.template.ElementNotFoundException;
import de.tbuchloh.kiskis.util.BuildProperties;
import de.tbuchloh.kiskis.util.DateUtils;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.xml.XMLException;
import de.tbuchloh.util.xml.migration.DocTransformer;

/**
 * <b>XMLReader</b>: loads the data model from a xml file.
 * 
 * @author gandalf
 * @version $Id$
 */
public final class XMLReader extends XMLProcessing implements AccountPropertyTypes {

    private final class MyEntityResolver implements EntityResolver {
        private String convert(final String systemId) {
            final String prefix = "file:";
            if (systemId.startsWith(prefix)) {
                LOG.debug("invalid systemId " + systemId);
                return systemId.substring(systemId.lastIndexOf("/") + 1);
            }
            return systemId;
        }

        private InputSource createNullEntity() {
            return new InputSource(new ByteArrayInputStream(new byte[0]));
        }

        /**
         * @param convertedId
         *            the converted system-id, like xml.xsd or kiskis.dtd
         * @return the URL where to get the resource
         */
        public URL getUrl(final String convertedId) {
            final StringBuilder idWithPath = new StringBuilder();
            if (convertedId.endsWith("dtd")) {
                idWithPath.append("dtd");
            } else {
                idWithPath.append("xsd");
            }
            idWithPath.append('/');
            idWithPath.append(convertedId);
            return getClass().getClassLoader().getResource(idWithPath.toString());
        }

        private void logDTDError(final String publicId, final String systemId) {
            logError("DTD for " + publicId + ", " + systemId + " not found!");
        }

        @Override
        public InputSource resolveEntity(final String publicId, final String systemId) {
            try {
                final String convertedId = convert(systemId);
                LOG.info("Trying to load entity: " + systemId + "=" + convertedId);
                final URL url = getUrl(convertedId);
                if (url == null) {
                    logDTDError(publicId, convertedId);
                    return createNullEntity();
                }
                LOG.debug("Found entity at url=" + url);
                return new InputSource(url.openStream());
            } catch (final IOException e) {
                LOG.error(e);
                logDTDError(publicId, systemId);
                return createNullEntity();
            }
        }
    }

    protected static final Messages M = new Messages(XMLReader.class);

    private static final DocTransformer TRANSFORMER = initTransformer();

    private static final MessageFormat VERSION_MISMATCH_WARN = new MessageFormat(M.getString("VERSION_MISMATCH_WARN")); //$NON-NLS-1$

    private static Calendar createLongCalendar(final Element el, final String attribute) throws ParseException {
        final String attributeValue = el.getAttributeValue(attribute);
        if (attributeValue == null) {
            return null;
        }

        final Calendar cal = DateUtils.getCurrentDateTime();
        cal.setTime(DATE_LONG.parse(attributeValue));
        return cal;
    }

    /**
     * @param element
     *            the parent element
     * @param name
     *            the name of the element to look for
     * @return the child elements
     */
    public static Elements getChildElements(Element element, String name) {
        return element.getChildElements(name, NS_MODEL);
    }

    /**
     * @param element
     *            the parent element
     * @param name
     *            the name of the element to look for
     * @return the first child element
     */
    public static Element getFirstChildElement(Element element, String name) {
        return element.getFirstChildElement(name, NS_MODEL);
    }

    private static DocTransformer initTransformer() {
        final DocTransformer[] t = {
                new DocTransformer("kiskis-0.24.1.dtd",
                        XMLReader.class.getResource("/transform/kiskis-0.24.1-to-1.0.xsl")),
                        new DocTransformer("kiskis-0.24.dtd",
                                XMLReader.class.getResource("/transform/kiskis-0.24-to-0.24.1.xsl")),
                                new DocTransformer("kiskis-0.21.dtd", XMLReader.class.getResource("/transform/kiskis-0.21-to-0.24.xsl")),
                                new DocTransformer("kiskis-0.15.dtd", XMLReader.class.getResource("/transform/kiskis-0.15-to-0.21.xsl")),
                                new DocTransformer("kiskis-0.14.dtd", XMLReader.class.getResource("/transform/kiskis-0.14-to-0.15.xsl")),
                                new DocTransformer("kiskis-0.13.dtd", XMLReader.class.getResource("/transform/kiskis-0.13-to-0.14.xsl")),
                                new DocTransformer("kiskis-0.12.dtd", XMLReader.class.getResource("/transform/kiskis-id.xsl")),
                                new DocTransformer("kiskis-0.11.dtd", XMLReader.class.getResource("/transform/kiskis-0.11-to-0.12.xsl")),
                                new DocTransformer("kiskis.dtd", XMLReader.class.getResource("/transform/kiskis-0.10-to-0.11.xsl")),
                                new DocTransformer("", XMLReader.class.getResource("/transform/kiskis-0.10-to-0.11.xsl"))
        };
        for (int i = 0; i < t.length - 1; i++) {
            t[i].setNext(t[i + 1]);
        }
        return t[0];
    }

    private final Map<String, Attachment> _attachments;

    private final TPMDocument _doc;

    private final File _file;

    private int _maxId;

    /**
     * creates a new XMLReader
     */
    public XMLReader(final File file) {
        super();

        assert file != null;

        _file = file;
        _attachments = new HashMap<String, Attachment>();
        _doc = new TPMDocument(file);
        _maxId = 0;
    }

    private List<AccountProperty> createAccountProperties(final Elements apn) {
        final List<AccountProperty> ret = new ArrayList<AccountProperty>();
        for (int i = 0; i < apn.size(); ++i) {
            final Element e = apn.get(i);
            try {
                final AccountProperty p = AccountProperty.create(e.getAttributeValue("name"),
                        e.getAttributeValue("type"));
                ret.add(p);
            } catch (final ElementNotFoundException ex) {
                final String msg = String.format("GenericAccountProperty broken! error=%1$s, el=%2$s", ex.getMessage(),
                        e.toXML());
                logError(msg);
            }
        }
        return ret;
    }

    private Set<AccountType> createAccountTypes(final Elements typeNodes) {
        final Set<AccountType> ret = new HashSet<AccountType>();
        for (int i = 0; i < typeNodes.size(); ++i) {
            final Element e = typeNodes.get(i);
            final AccountType t = new AccountType();
            t.setName(e.getAttributeValue("name"));
            final Elements apn = XMLReader.getChildElements(e, ACC_PROP_TAG);
            t.setProperties(createAccountProperties(apn));
            ret.add(t);
        }
        return ret;
    }

    private Attachment createAttachment(final Element element) {
        final Attachment att = new Attachment(_doc);
        final int id = Integer.parseInt(element.getAttributeValue("id"));
        _maxId = Math.max(id, _maxId);
        att.setId(id);
        att.setKey(element.getAttributeValue("key").toCharArray());
        att.setDescription(element.getAttributeValue("description"));
        att.setUuid(element.getAttributeValue(UUID_ATTR));
        return att;
    }

    /**
     * @param attachmentRef
     *            is the attachment-ref tag
     * @return is the corresponding attachment
     */
    private Attachment createAttachmentRef(Element attachmentRef) {
        final String uuid = attachmentRef.getAttributeValue(ATTACHMENT_UUID_ATTR);
        return _attachments.get(uuid);
    }

    /**
     * @param attachments
     *            the attachment nodes
     */
    private void createAttachments(Element attachments) {
        final Elements attList = XMLReader.getChildElements(attachments, ATTACHMENT_TAG);

        for (int i = 0; i < attList.size(); ++i) {
            final Element attNode = attList.get(i);
            final Attachment att = createAttachment(attNode);
            _attachments.put(att.getUuid(), att);
        }
        LOG.debug(String.format("Found %1$s attachments", _attachments.size()));
    }

    private ModelNode createBankAccount(final Element el) {
        final BankAccount ba = new BankAccount();
        ba.setNumber(el.getAttributeValue("number"));
        ba.setBankName(el.getAttributeValue("bank"));
        ba.setBankID(el.getAttributeValue("bankID"));
        final Elements lists = XMLReader.getChildElements(el, TANLIST_TAG);
        for (int i = 0; i < lists.size(); ++i) {
            ba.addTANList(createTANList(lists.get(i)));
        }
        assert lists.size() == ba.getTanLists().size();
        ba.setTelebankingPin(el.getAttributeValue("telebankingPin").toCharArray());
        return createSecuredElement(ba, el);
    }

    private ModelNode createCreditCard(final Element el) {
        final CreditCard cc = new CreditCard();
        cc.setNumber(el.getAttributeValue("number"));
        cc.setDeployer(el.getAttributeValue("deployer"));
        cc.setCardValidationCode(el.getAttributeValue("cardValidationCode"));
        cc.setPin(el.getAttributeValue("pin").toCharArray());
        return createSecuredElement(cc, el);
    }

    private TPMDocument createDocument(final Element docElement) throws PersistenceException {
        if (docElement == null) {
            throw new PersistenceException(M.format("ERR_TRANSFORM_FAILED", _file.getName()));
        }

        if (!DOC_TAG.equals(docElement.getLocalName())) {
            throw new PersistenceException(M.format("ERR_WRONG_ROOT_ELEMENT", _file.getName()));
        }
        final String version = docElement.getAttributeValue("version"); //$NON-NLS-1$
        if (!version.equals(VERSION)) {
            final Object[] p = {
                    _file.getName(), version, VERSION
            };
            logWarning(VERSION_MISMATCH_WARN.format(p));
        }

        final Elements typeNodes = getChildElements(docElement, ACC_TYPE_TAG);
        _doc.setAccountTypes(createAccountTypes(typeNodes));

        final Elements attachments = getChildElements(docElement, ATTACHMENTS_TAG);
        if (attachments.size() == 0) {
            LOG.info("Attachments-Tag is missing.");
        } else {
            createAttachments(attachments.get(0));
        }

        final Element root = XMLReader.getFirstChildElement(docElement, GROUP_TAG);
        _doc.setGroups(createGroup(root));
        _doc.setNextAttachmentId(_maxId + 1);
        _doc.setUuid(docElement.getAttributeValue(UUID_ATTR));

        return _doc;
    }

    private EntityResolver createEntityResolver() {
        return new MyEntityResolver();
    }

    private ModelNode createGenericAccount(final Element el) throws PersistenceException {
        try {
            final GenericAccount a = new GenericAccount();
            final AccountType type = _doc.getType(el.getAttributeValue("type"));
            a.setType(type);
            final Elements vals = XMLReader.getChildElements(el, ACC_PROP_VAL_TAG);
            for (int i = 0; i < vals.size(); ++i) {
                final Element v = vals.get(i);
                final String propType = v.getAttributeValue("type");
                final String propName = v.getAttributeValue("name");
                try {
                    final AccountProperty accProp = type.getProperty(propType, propName);
                    a.setValue(accProp, createPropertyValue(accProp, v));
                } catch (final Exception e) {
                    logError(e.getMessage());
                }
            }
            return createSecuredElement(a, el);
        } catch (final ElementNotFoundException e) {
            final String msg = String.format("GenericAccount broken! error=%1$s, el=%2$s", e.getMessage(), el.toXML());
            throw new PersistenceException(msg);
        }
    }

    private Group createGroup(final Element group) {
        final Group g = new Group();
        g.setUuid(group.getAttributeValue(UUID_ATTR));
        g.setName(group.getAttributeValue("name"));
        g.setComment(getComment(group));
        try {
            g.setArchivedOnDate(createLongCalendar(group, ARCHIVE_DATE_ATTR));
        } catch (final ParseException e1) {
            logError("Unknown time format in '" + group.toString() + "'!");
        }
        final Elements childs = group.getChildElements();
        for (int i = 0; i < childs.size(); ++i) {
            final Element el = childs.get(i);
            try {
                g.add(createModelNode(el));
            } catch (final PersistenceException e) {
                logError(e.getMessage());
            }
        }
        return g;
    }

    private ModelNode createModelNode(final Element el) throws PersistenceException {
        final String n = el.getLocalName();
        if (GROUP_TAG.equals(n)) {
            return createGroup(el);
        } else if (BA_TAG.equals(n)) {
            return createBankAccount(el);
        } else if (CC_TAG.equals(n)) {
            return createCreditCard(el);
        } else if (GENERIC_TAG.equals(n)) {
            return createGenericAccount(el);
        } else if (NA_TAG.equals(n)) {
            return createNetAccount(el);
        } else if (SF_TAG.equals(n)) {
            return createSecuredFile(el);
        } else {
            final MessageFormat msg = new MessageFormat("Unknown element type {0}");
            final Object[] p = {
                    el.toXML()
            };
            throw new PersistenceException(msg.format(p));
        }
    }

    private ModelNode createNetAccount(final Element el) {
        final NetAccount na = new NetAccount();
        na.setUsername(el.getAttributeValue("username"));
        na.setUrl(el.getAttributeValue("url"));
        na.setEmail(el.getAttributeValue("email"));
        return createSecuredElement(na, el);
    }

    private Object createPropertyValue(final AccountProperty p, final Element value) throws ParseException {
        Object val = value.getAttributeValue("value");
        if (T_DATE.equals(p.getType())) {
            final Calendar cal = DateUtils.getCurrentDateTime();
            cal.setTime(DATE_LONG.parse((String) val));
            val = cal;
        } else if (T_PWD.equals(p.getType())) {
            val = val.toString();
        }
        return val;
    }

    private Password createPwd(final Element pwdNode) {
        try {
            final Calendar created = createLongCalendar(pwdNode, "created");
            final Calendar expires = DateUtils.getCurrentDate();
            expires.setTime(DATE_SHORT.parse(pwdNode.getAttributeValue("expires")));
            return new Password(pwdNode.getAttributeValue("pwd").toCharArray(), expires, created);
        } catch (final ParseException e) {
            throw new Error("should never happen if XML is valid!");
        }
    }

    private PasswordHistory createPwdHistory(final Element element) {
        final PasswordHistory ph = new PasswordHistory();
        final Elements list = XMLReader.getChildElements(element, PWD_TAG);
        for (int i = 0; i < list.size(); ++i) {
            ph.addPassword(createPwd(list.get(i)));
        }
        assert ph.getPasswords().size() == list.size();
        return ph;
    }

    private org.xml.sax.XMLReader createReader() throws SAXException {
        final org.xml.sax.XMLReader reader = XMLReaderFactory.createXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", BuildProperties.isValidatingDocs());
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        reader.setFeature("http://apache.org/xml/features/validation/schema", true);

        final EntityResolver resolver = createEntityResolver();
        reader.setEntityResolver(resolver);
        return reader;
    }

    private ModelNode createSecuredElement(final SecuredElement se, final Element el) {
        final Element deNode = XMLReader.getFirstChildElement(el, SE_TAG);
        se.setUuid(deNode.getAttributeValue(UUID_ATTR));
        se.setName(deNode.getAttributeValue("name"));
        try {
            se.setArchivedOnDate(createLongCalendar(deNode, ARCHIVE_DATE_ATTR));
        } catch (final ParseException e1) {
            logError("Unknown time format in '" + deNode.toString() + "'!");
        }
        se.setExpiresNever(Boolean.valueOf(deNode.getAttributeValue("expiresNever")).booleanValue());
        se.setComment(getComment(deNode));

        try {
            final String value = deNode.getAttributeValue("viewCounter");
            se.setViewCounter(Integer.parseInt(value));
        } catch (final NumberFormatException e) {
            logError(e.getMessage());
        }

        try {
            final Calendar cal = createLongCalendar(deNode, "creationDate");
            se.setCreationDate(cal);
        } catch (final Throwable e) {
            logError("Unknown time format in '" + deNode.toString() + "'!");
        }

        try {
            final Calendar cal = createLongCalendar(deNode, "lastChangeDate");
            se.setLastChangeDate(cal);
        } catch (final Throwable e) {
            logError("Unknown time format in '" + deNode.toString() + "'!");
        }

        try {
            final Calendar cal = createLongCalendar(deNode, "lastViewedDate");
            se.setLastViewedDate(cal);
        } catch (final Throwable e) {
            logError("Unknown time format in '" + deNode.toString() + "'!");
        }

        se.setPwd(createPwd(XMLReader.getFirstChildElement(deNode, PWD_TAG)));

        se.setPwdHistory(createPwdHistory(XMLReader.getFirstChildElement(deNode, PH_TAG)));

        final Elements l = XMLReader.getChildElements(deNode, ATTACHMENT_REF_TAG);
        for (int i = 0; i < l.size(); ++i) {
            final Element attachmentRef = l.get(i);
            final Attachment a = createAttachmentRef(attachmentRef);
            if (a == null) {
                final String msg = String.format("The element %1$s of %3$s references an unknown attachment! "
                        + "Known attachments are %2$s.", attachmentRef.toXML(), _attachments, el);
                logError(msg);
            } else {
                se.addAttachment(a);
            }
        }
        return se;
    }

    private ModelNode createSecuredFile(final Element el) {
        final SecuredFile f = new SecuredFile();
        f.setFile(el.getAttributeValue("file"));
        return createSecuredElement(f, el);
    }

    private TAN createTAN(final Element element) {
        final TAN tan = new TAN();
        tan.setId(Integer.parseInt(element.getAttributeValue("id")));
        tan.setNumber(element.getAttributeValue("number"));
        if (element.getAttributeValue("used") != null) {
            try {
                final Calendar used = createLongCalendar(element, "used");
                tan.setUsed(used);
            } catch (final ParseException e) {
                // should never happen!
                throw new Error(e);
            }

        }
        return tan;
    }

    private TANList createTANList(final Element el) {
        final TANList tl = new TANList();
        tl.setId(el.getAttributeValue("id")); //$NON-NLS-1$

        try {
            final Calendar cal = createLongCalendar(el, "created");
            tl.setCreated(cal);
        } catch (final ParseException e) {
            logError("Unknown time format in '" //$NON-NLS-1$
                    + el.toString() + "'!"); //$NON-NLS-1$
        }

        final Elements tans = XMLReader.getChildElements(el, TAN_TAG);
        for (int i = 0; i < tans.size(); i++) {
            tl.addTAN(createTAN(tans.get(i)));
        }
        assert tans.size() == tl.getTans().size();
        return tl;
    }

    private String getComment(final Element element) {
        final Element comment = XMLReader.getFirstChildElement(element, COMMENT_TAG);
        if (comment == null) {
            return "";
        }
        element.removeChild(comment);
        return comment.getValue();
    }

    /**
     * @param is
     *            is the input source. Should be a valid XML-source.
     * @return the new created document.
     */
    public TPMDocument load(final InputStream is) throws PersistenceException {
        try {

            Document doc = parseDocument(is);
            final DocType docType = doc.getDocType();
            LOG.debug("Found DOCTYPE: " + (docType == null ? "unknown" : docType.getSystemID()));

            final String currentNS = doc.getRootElement().getNamespaceURI();
            if (NS_MODEL.equals(currentNS)) {
                LOG.debug("Current Namespace found! No transformation needed.");
            } else {
                LOG.debug("Found namespace " + currentNS);
                doc = transformDoc(doc);
            }
            return createDocument(doc.getRootElement());
        } catch (final Exception e) {
            LOG.error(e, e);
            throw new PersistenceException(e.getMessage(), e);
        } finally {
            // there may be sensitive data in memory which should not be there
            // so lets clean up
            System.gc();
            System.gc();
        }
    }

    /**
     * @param is
     *            the input stream to read
     * @return the document
     */
    protected Document parseDocument(final InputStream is) throws SAXException, ParsingException, ValidityException,
    IOException {
        final Builder parser = new Builder(createReader(), BuildProperties.isValidatingDocs());

        return parser.build(is);
    }

    protected Document transformDoc(final Document ds) throws XMLException {
        return TRANSFORMER.transform(ds);
    }

}
