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

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;

import junit.framework.TestCase;
import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.NetAccount;
import de.tbuchloh.kiskis.model.Password;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.testutils.CollectionUtils;
import de.tbuchloh.kiskis.util.DateUtils;
import de.tbuchloh.util.crypto.OpenPGPwithAES256;
import de.tbuchloh.util.io.FileSearcher;
import de.tbuchloh.util.reflection.DeepCopyUtil;

/**
 * <b>PGPEncryptionTest</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public class PGPEncryptionTest extends TestCase {

    /**
     * <b>FailTestErrorHandler</b>:
     * 
     * @author gandalf
     * @version $Id$
     */
    protected static class FailTestErrorHandler implements IErrorHandler {

        private final boolean failOnWarning;

        /**
         * Creates an empty PGPEncryptionTest.FailTestErrorHandler
         */
        public FailTestErrorHandler() {
            this(true);
        }

        public FailTestErrorHandler(final boolean failOnWarning) {
            this.failOnWarning = failOnWarning;
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.persistence.IErrorHandler#error(java.lang.String)
         */
        @Override
        public void error(final String message) {
            fail(message);
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.persistence.IErrorHandler#warning(java.lang.String)
         */
        @Override
        public void warning(final String message) {
            assertFalse(message, failOnWarning);
        }

    }

    private static final File FILE = new File("target/PGPEncryptionTest.xml");

    private static final ICryptoContext CTX = createContext();

    private static final TPMDocument DOC = initDocument();

    /**
     * @return the simple cryptocontext. Uses AES with geheim as password.
     */
    private static ICryptoContext createContext() {
        return createContext(FILE);
    }

    /**
     * @return the simple cryptocontext. Uses AES with geheim as password.
     */
    protected static ICryptoContext createContext(final File file) {
        return createContext(file, true);
    }

    protected static ICryptoContext createContext(final File file,
            final boolean encrypt) {
        return new PasswordCryptoContext(
                new OpenPGPwithAES256(), new SimplePasswordProxy(
                        (encrypt ? "geheim" : "").toCharArray()), file);
    }

    /**
     * @return create a simple net account
     */
    private static ModelNode createNetAccount(final String name) {
        final NetAccount node = new NetAccount();
        node.setComment("comment for " + name);
        node.setCreationDate(getCalendar());
        node.setEmail("email@mail.de");
        node.setExpiresNever(true);
        node.setLastChangeDate(getCalendar());
        node.setLastViewedDate(getCalendar());
        node.setName(name);
        node.setPwd(createPassword(name));
        node.setPwd(createPassword(name + " v2"));
        node.setUrl("http://www.google.de/");
        node.setUsername("myuser_" + name);
        node.setViewCounter(0);
        return node;
    }

    private static Password createPassword(final String name) {
        final Password p = new Password(("password" + name).toCharArray(),
                getShortCalendar(), getShortCalendar());
        return p;
    }

    private static Calendar getCalendar() {
        try {
            final Calendar cal = DateUtils.getCurrentDateTime();
            cal.setTime(XMLProcessing.DATE_LONG.parse("2006-06-01T18:00:00"));
            return cal;
        } catch (final ParseException e) {
            throw new Error();
        }
    }

    private static Calendar getShortCalendar() {
        try {
            final Calendar cal = DateUtils.getCurrentDate();
            cal.setTime(XMLProcessing.DATE_SHORT.parse("2006-06-01"));
            return cal;
        } catch (final ParseException e) {
            throw new Error();
        }
    }

    /**
     * @return the test document to compare against.
     */
    private static TPMDocument initDocument() {
        final TPMDocument doc = new TPMDocument(CTX.getFile());
        final Group root = new Group("root");
        doc.setGroups(root);

        root.add(createNetAccount("Net 1"));
        final NetAccount na = (NetAccount) createNetAccount("Net 2");
        na.setArchivedOnDate(DateUtils.getCurrentDateTime());
        root.add(na);

        final Group c1 = new Group("child 1");
        c1.setArchivedOnDate(DateUtils.getCurrentDateTime());
        root.add(c1);

        c1.add(createNetAccount("Net 3"));
        c1.add(createNetAccount("Net 4"));

        final Group c12 = new Group("child 1.2");
        c1.add(c12);

        c12.add(createNetAccount("Net 5"));

        // g1.add(create)
        return doc;
    }

    /**
     * test the save method. use open pgp with aes.
     */
    public void test01SavePGPDocument() throws Exception {
        FILE.delete();

        PersistenceManager.saveAs(DOC, createContext(), false);

        assertTrue(FILE.exists());

        assertEquals(FileFormats.PGP_FILE,
                PersistenceManager.checkMimeType(FILE));
    }

    /**
     * test the save method. use open pgp with aes.
     */
    public void test02SavePGPDocumentWithBackup() throws Exception {
        assertTrue(FILE.exists());

        PersistenceManager.saveAs(DOC, createContext(), true);

        assertTrue(FILE.exists());

        assertEquals(FileFormats.PGP_FILE,
                PersistenceManager.checkMimeType(FILE));

        File bak = null;
        final FileSearcher fs = new FileSearcher(FILE.getParentFile());
        for (final Object element : fs.getFiles(false)) {
            final File f = (File) element;
            if (!f.getAbsolutePath().equals(FILE.getAbsolutePath())
                    && f.getName().startsWith(FILE.getName())) {
                assertNull(f.toString(), bak);
                bak = f;
                bak.deleteOnExit();
            }
        }
        assertNotNull("No backup file found for file " + FILE, bak);

        assertEquals(DOC, PersistenceManager.load(createContext(bak),
                new FailTestErrorHandler()));
    }

    /**
     * load the previously stored document
     */
    public void test03LoadPGPDocument() throws Exception {
        assertTrue(FILE.exists());

        final TPMDocument copy = PersistenceManager.load(createContext(),
                new FailTestErrorHandler());

        final ModelNode na1 = CollectionUtils.get(copy.getGroups().getElements(), 0);
        assertEquals(false, na1.isArchived());

        final ModelNode na2 = CollectionUtils.get(copy.getGroups().getElements(), 1);
        assertEquals(true, na2.isArchived());

        final Group g1 = CollectionUtils.get(copy.getGroups().getGroups(), 0);
        assertEquals(true, g1.isArchived());

        final Group g12 = CollectionUtils.get(g1.getGroups(), 0);
        assertEquals(false, g12.isArchived());

        assertTrue(DOC.matches(copy));
    }

    /**
     * test the equals-method
     */
    public void testEquals() throws Exception {
        assertEquals(DOC, DeepCopyUtil.cloneSerializable(DOC));
        assertTrue(DOC.matches(DOC));
        assertTrue(DOC.matches((TPMDocument) DeepCopyUtil
                .cloneSerializable(DOC)));
    }
}
