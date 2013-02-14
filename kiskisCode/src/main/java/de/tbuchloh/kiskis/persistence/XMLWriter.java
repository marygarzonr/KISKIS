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

import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import nu.xom.Text;
import de.tbuchloh.kiskis.model.Attachment;
import de.tbuchloh.kiskis.model.BankAccount;
import de.tbuchloh.kiskis.model.CreditCard;
import de.tbuchloh.kiskis.model.GenericAccount;
import de.tbuchloh.kiskis.model.Group;
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
import de.tbuchloh.kiskis.util.BuildProperties;

/**
 * <b>XMLWriter</b>: stores the document into a xml-file.
 * 
 * @author gandalf
 * @version $Id$
 */
public final class XMLWriter extends XMLProcessing implements
AccountPropertyTypes {

    private static final String NS_XML = "http://www.w3.org/XML/1998/namespace";

    private static final String ENCODING = "UTF-8";

    /**
     * creates a new XMLWriter
     * 
     */
    public XMLWriter() {
        super();
    }

    private void addGroup(final Element parent, final Group group) {
        final Element gNode = newElement(GROUP_TAG);
        gNode.addAttribute(new Attribute(UUID_ATTR, group.getUuid()));
        gNode.addAttribute(new Attribute("name", group.getName()));
        if (group.getArchivedOnDate() != null) {
            gNode.addAttribute(new Attribute(ARCHIVE_DATE_ATTR,
                    createLongDate(group.getArchivedOnDate())));
        }
        createComment(gNode, group.getComment());
        for (final Object element : group.getGroups()) {
            final Group tmp = (Group) element;
            addGroup(gNode, tmp);
        }
        for (final Object element2 : group.getElements()) {
            final SecuredElement element = (SecuredElement) element2;
            addSecuredElement(gNode, element);
        }
        parent.appendChild(gNode);
    }

    private void addSecuredElement(final Element parent,
            final SecuredElement element) {
        final Class<? extends SecuredElement> clazz = element.getClass();
        Element newEl = null;
        if (BankAccount.class == clazz) {
            newEl = createBankAccount(parent, (BankAccount) element);
        } else if (CreditCard.class == clazz) {
            newEl = createCreditCard(parent, (CreditCard) element);
        } else if (GenericAccount.class == clazz) {
            newEl = createGenericAccount(parent, (GenericAccount) element);
        } else if (NetAccount.class == clazz) {
            newEl = createNetAccount(parent, (NetAccount) element);
        } else if (SecuredFile.class == clazz) {
            newEl = createSecuredFile(parent, (SecuredFile) element);
        } else {
            assert false;
        }
        createSecuredElement(newEl, element);
    }

    private void createAccountProperties(final Element parent,
            final List<AccountProperty> properties) {
        for (final AccountProperty p : properties) {
            final Element n = newElement(ACC_PROP_TAG);
            n.addAttribute(new Attribute("name", p.getName()));
            n.addAttribute(new Attribute("type", p.getType()));
            parent.appendChild(n);
        }
    }

    private void createAccountTypes(final Element root,
            final Collection<AccountType> types) {
        for (final AccountType type : types) {
            final Element n = newElement(ACC_TYPE_TAG);
            n.addAttribute(new Attribute("name", type.getName()));
            createAccountProperties(n, type.getProperties());
            root.appendChild(n);
        }
    }

    private Element createAttachment(final Element parent, final Attachment a) {
        final Element n = newElement(ATTACHMENT_TAG);
        n.addAttribute(new Attribute("id", String.valueOf(a.getId())));
        n.addAttribute(new Attribute(UUID_ATTR, a.getUuid()));
        n.addAttribute(new Attribute("key", String.valueOf(a.getKey())));
        n.addAttribute(new Attribute("description", a.getDescription()));
        parent.appendChild(n);
        return n;
    }

    private void createAttributePropertyValues(final Element parent,
            final GenericAccount acc) {
        for (final Map.Entry<AccountProperty, Object> entry : acc.getValues()
                .entrySet()) {
            final AccountProperty key = entry.getKey();
            final Object value = entry.getValue();
            final Element n = newElement(ACC_PROP_VAL_TAG);
            n.addAttribute(new Attribute("name", key.getName()));
            setPropertyValue(n, key.getType(), value);
            LOG.debug("set property type=" + key.getType());
            parent.appendChild(n);
        }
    }

    private Element createBankAccount(final Element parent, final BankAccount el) {
        final Element n = newElement(BA_TAG);
        n.addAttribute(new Attribute("bank", el.getBankName()));
        n.addAttribute(new Attribute("bankID", el.getBankID()));
        n.addAttribute(new Attribute("number", el.getNumber()));
        final char[] telPin = el.getTelebankingPin();
        n.addAttribute(new Attribute("telebankingPin", new String(telPin)));

        for (final Object element : el.getTanLists()) {
            final TANList l = (TANList) element;
            createTANList(n, l);
        }
        parent.appendChild(n);
        return n;
    }

    private void createComment(final Element se, final String comment) {
        final Element ct = newElement(COMMENT_TAG);
        ct.addAttribute(new Attribute("xml:space", NS_XML, "preserve"));
        if (!comment.equals("")) {
            ct.appendChild(new Text(comment));
        }
        se.appendChild(ct);
    }

    private Element createCreditCard(final Element parent,
            final CreditCard element) {
        final Element n = newElement(CC_TAG);
        n.addAttribute(new Attribute("deployer", element.getDeployer()));
        final String cvc = element.getCardValidationCode();
        if (cvc == null) {
            n.addAttribute(new Attribute("cardValidationCode", ""));
        } else {
            // TODO remove check after release
            n.addAttribute(new Attribute("cardValidationCode", cvc));
        }
        n.addAttribute(new Attribute("number", element.getNumber()));
        n.addAttribute(new Attribute("pin", new String(element.getPin())));
        parent.appendChild(n);
        return n;
    }

    private String createLongDate(final Calendar creationDate) {
        return DATE_LONG.format(creationDate.getTime());
    }

    private Document createDocument(final TPMDocument doc) {
        final Element root = newElement(DOC_TAG);
        root.addAttribute(new Attribute(UUID_ATTR, doc.getUuid()));
        root.addAttribute(new Attribute("version", BuildProperties
                .getVersion()));
        createAccountTypes(root, doc.getAccountTypes());
        addGroup(root, doc.getGroups());
        createAttachments(root, doc.getAttachments());

        final String xsiURI = "http://www.w3.org/2001/XMLSchema-instance";
        root.addNamespaceDeclaration("xsi", xsiURI);
        root.addNamespaceDeclaration("", NS_MODEL);
        root.addAttribute(new Attribute("xsi:schemaLocation", xsiURI,//
                NS_MODEL + " " + CURRENT_XSD));
        return new Document(root);
    }

    /**
     * @param root
     *            the document
     * @param attachments
     *            all attachments
     */
    private void createAttachments(Element root,
            Collection<Attachment> attachments) {
        final Element attachmentsNode = newElement(ATTACHMENTS_TAG);
        root.appendChild(attachmentsNode);

        for (final Attachment attachment : attachments) {
            createAttachment(attachmentsNode, attachment);
        }

    }

    private Element createGenericAccount(final Element parent,
            final GenericAccount acc) {
        final Element n = newElement(GENERIC_TAG);
        n.addAttribute(new Attribute("type", acc.getType().getName()));
        createAttributePropertyValues(n, acc);
        parent.appendChild(n);
        return n;
    }

    private Element createNetAccount(final Element parent,
            final NetAccount account) {
        final Element n = newElement(NA_TAG);
        n.addAttribute(new Attribute("email", account.getEmail()));
        n.addAttribute(new Attribute("username", account.getUsername()));
        n.addAttribute(new Attribute("url", account.getUrl()));
        parent.appendChild(n);
        return n;
    }

    private Element createPassword(final Element parent, final Password pwd) {
        final Element pwdNode = newElement(PWD_TAG);
        pwdNode.addAttribute(new Attribute("pwd", new String(pwd.getPwd())));
        pwdNode.addAttribute(new Attribute("expires", createShortDate(pwd
                .getExpires())));
        pwdNode.addAttribute(new Attribute("created", createLongDate(pwd
                .getCreationDate())));
        parent.appendChild(pwdNode);
        return pwdNode;
    }

    private Element createPwdHistory(final Element parent,
            final PasswordHistory ph) {
        final Element phNode = newElement(PH_TAG);
        for (final Object element : ph.getPasswords()) {
            final Password pwd = (Password) element;
            createPassword(phNode, pwd);
        }
        parent.appendChild(phNode);
        return phNode;
    }

    private Element createSecuredElement(final Element parent,
            final SecuredElement el) {
        final Element se = newElement(SE_TAG);
        se.addAttribute(new Attribute(UUID_ATTR, el.getUuid()));
        se.addAttribute(new Attribute("name", el.getName()));
        se.addAttribute(new Attribute("creationDate", createLongDate(el
                .getCreationDate())));
        se.addAttribute(new Attribute("lastChangeDate", createLongDate(el
                .getLastChangeDate())));
        se.addAttribute(new Attribute("expiresNever", String.valueOf(el
                .expiresNever())));
        se.addAttribute(new Attribute("lastViewedDate", createLongDate(el
                .getLastViewedDate())));
        se.addAttribute(new Attribute("viewCounter", String.valueOf(el
                .getViewCounter())));
        if (el.getArchivedOnDate() != null) {
            se.addAttribute(new Attribute(ARCHIVE_DATE_ATTR, createLongDate(el
                    .getArchivedOnDate())));
        }
        createComment(se, el.getComment());
        createPassword(se, el.getPwd());
        createPwdHistory(se, el.getPwdHistory());
        for (final Attachment element : el.getAttachments()) {
            createAttachmentRef(se, element);
        }
        parent.appendChild(se);
        return se;
    }

    /**
     * @param se
     *            the xml node
     * @param attachment
     *            the {@link Attachment}
     */
    private void createAttachmentRef(Element se, Attachment attachment) {
        final Element attRefNode = newElement(ATTACHMENT_REF_TAG);
        attRefNode.addAttribute(new Attribute(ATTACHMENT_UUID_ATTR, attachment
                .getUuid()));
        se.appendChild(attRefNode);
    }

    private Element createSecuredFile(final Element parent,
            final SecuredFile file) {
        final Element n = newElement(SF_TAG);
        n.addAttribute(new Attribute("file", file.getFile()));
        parent.appendChild(n);
        return n;
    }

    private Element createTAN(final Element parent, final TAN tan) {
        final Element n = newElement(TAN_TAG);
        n.addAttribute(new Attribute("id", String.valueOf(tan.getId())));
        n.addAttribute(new Attribute("number", tan.getNumber()));
        if (tan.getUsed() != null) {
            n.addAttribute(new Attribute("used", createLongDate(tan.getUsed())));
        }
        parent.appendChild(n);
        return n;
    }

    private Element createTANList(final Element parent, final TANList l) {
        final Element n = newElement(TANLIST_TAG);
        n.addAttribute(new Attribute("id", l.getId()));
        n.addAttribute(new Attribute("created", createLongDate(l.getCreated())));
        for (final Object element : l.getTans()) {
            final TAN tan = (TAN) element;
            createTAN(n, tan);
        }
        parent.appendChild(n);
        return n;
    }

    /**
     * @param name
     *            the name of the element
     * @return the created element
     */
    private static Element newElement(String name) {
        return new Element(name, NS_MODEL);
    }

    /**
     * @param doc
     *            the document to store.
     * @param os
     *            the stream to write the data.
     * @throws PersistenceException
     *             if anything is wrong.
     */
    public void save(final TPMDocument doc, final OutputStream os)
    throws PersistenceException {
        write(createDocument(doc), os);

        // there may be sensitive data in memory which should not be there
        // so lets clean up
        System.gc();
    }

    private void setPropertyValue(final Element node, final String type,
            Object value) {
        if (T_DATE.equals(type)) {
            LOG.debug("converting to string type=" + type);
            value = DATE_LONG.format(((Calendar) value).getTime());
        } else if (T_PWD.equals(type)) {
            LOG.debug("converting string type=" + type);
            value = value.toString();
        }
        node.addAttribute(new Attribute("type", type));
        node.addAttribute(new Attribute("value", value.toString()));
    }

    private void write(final Document xdoc, final OutputStream os)
    throws PersistenceException {
        try {
            final Serializer format = new Serializer(os, ENCODING);
            format.setIndent(2);

            format.write(xdoc);
        } catch (final Exception e) {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

}
