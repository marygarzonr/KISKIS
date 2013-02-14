/*
 * Copyright (C) 2010 by Tobias Buchloh.
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
package de.tbuchloh.kiskis.persistence.importer;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.kiskis.model.ModelConstants;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.NetAccount;
import de.tbuchloh.kiskis.model.SecuredElement;
import de.tbuchloh.kiskis.model.StandardDocumentFactory;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.testutils.TestMessageListener;
import de.tbuchloh.kiskis.util.DateUtils;
import de.tbuchloh.kiskis.util.Settings;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 20.11.2010
 */
public class CSVImportTest extends TestCase {

    /**
     * Der Logger
     */
    static final Log LOG = LogFactory.getLog(CSVImportTest.class);

    private Locale _locale;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _locale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Locale.setDefault(_locale);
    }

    /**
     * Test für ImportExample
     * 
     * @throws Exception
     *             wenn unvorhergesehene Fehler auftreten.
     */
    public void testImportExample() throws Exception {
        final TPMDocument doc = StandardDocumentFactory.createStandardDocument(new File(""));
        final int initialSize = 10;
        final Group rootNode = doc.getGroups();
        assertEquals(initialSize, rootNode.preOrder().size());

        final TestMessageListener msg = new TestMessageListener();
        final CSVImport cvs = new CSVImport(msg, doc);

        cvs.doImport(new File("xml/example-import.csv"), ',');
        assertEquals(2, msg.getMessages().size());

        final int accounts = 11, groups = 5;
        assertEquals(initialSize + accounts + groups, rootNode.preOrder().size());

        assertUsername(rootNode, "Amazon", "amazon.foo");
        assertEmail(rootNode, "Amazon", "mail@bar.de");
        assertUrl(rootNode, "Amazon", "http://www.amazon.de");
        assertCreatedOn(rootNode, "Enterprise Password", "2009-12-24");
        assertCreatedOn(rootNode, "Amazon", ModelConstants.SHORT.format(new Date()));
        assertCreatedOn(rootNode, "Wrong creation date", ModelConstants.SHORT.format(new Date()));
        assertExpiresOn(rootNode, "Enterprise Password", "2010-11-23");

        final Calendar expiresOn = DateUtils.getCurrentDate();
        expiresOn.add(Calendar.DAY_OF_YEAR, Settings.getDefaultPwdExpiryDays());
        assertExpiresOn(rootNode, "Wrong Expiration date", ModelConstants.SHORT.format(expiresOn.getTime()));

        assertPassword(rootNode, "Account placed to the root", "hhsgww2l");
        assertComment(rootNode, "Enterprise Password", "That is just a comment.\nWith\nMultiple lines\nImported");
    }

    private void assertPassword(Group rootNode, final String nodeLabel, final String expected) {
        final List<ModelNode> r1 = rootNode.findAll(Pattern.compile(nodeLabel));
        assertEquals(1, r1.size());
        final String actual = new String(((SecuredElement) r1.get(0)).getPwd().getPwd());
        assertEquals(expected, actual);
    }

    private void assertEmail(Group rootNode, final String nodeLabel, final String expected) {
        final List<ModelNode> r1 = rootNode.findAll(Pattern.compile(nodeLabel));
        assertEquals(1, r1.size());
        final String actual = ((NetAccount) r1.get(0)).getEmail();
        assertEquals(expected, actual);
    }

    private void assertCreatedOn(Group rootNode, final String nodeLabel, final String expected) {
        final List<ModelNode> r1 = rootNode.findAll(Pattern.compile(nodeLabel));
        assertEquals(1, r1.size());
        final String actual = ModelConstants.SHORT.format(((NetAccount) r1.get(0)).getCreationDate().getTime());
        assertEquals(expected, actual);
    }

    private void assertExpiresOn(Group rootNode, final String nodeLabel, final String expected) {
        final List<ModelNode> r1 = rootNode.findAll(Pattern.compile(nodeLabel));
        assertEquals(1, r1.size());
        final String actual = ModelConstants.SHORT.format(((NetAccount) r1.get(0)).getPwd().getExpires().getTime());
        assertEquals(expected, actual);
    }

    private void assertUrl(Group rootNode, final String nodeLabel, final String expected) {
        final List<ModelNode> r1 = rootNode.findAll(Pattern.compile(nodeLabel));
        assertEquals(1, r1.size());
        final String actual = ((NetAccount) r1.get(0)).getUrl();
        assertEquals(expected, actual);
    }

    private void assertUsername(Group rootNode, final String nodeLabel, final String expected) {
        final List<ModelNode> r1 = rootNode.findAll(Pattern.compile(nodeLabel));
        assertEquals(1, r1.size());
        final String actual = ((NetAccount) r1.get(0)).getUsername();
        assertEquals(expected, actual);
    }

    void assertComment(final Group rootNode, final String nodeLabel, final String expected) {
        final List<ModelNode> r1 = rootNode.findAll(Pattern.compile(nodeLabel));
        assertEquals(1, r1.size());
        final String comment = ((SecuredElement) r1.get(0)).getComment();
        assertTrue(comment, comment.startsWith(expected));
    }
}
