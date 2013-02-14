/**
 *
 */
package de.tbuchloh.kiskis.persistence;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import de.tbuchloh.kiskis.model.Attachment;
import de.tbuchloh.kiskis.model.BankAccount;
import de.tbuchloh.kiskis.model.CreditCard;
import de.tbuchloh.kiskis.model.GenericAccount;
import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.ModelNodeVisitor;
import de.tbuchloh.kiskis.model.ModelNodeVisitorAdapter;
import de.tbuchloh.kiskis.model.NetAccount;
import de.tbuchloh.kiskis.model.Password;
import de.tbuchloh.kiskis.model.SecuredElement;
import de.tbuchloh.kiskis.model.SecuredFile;
import de.tbuchloh.kiskis.model.TAN;
import de.tbuchloh.kiskis.model.TANList;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.testutils.CollectionUtils;
import de.tbuchloh.kiskis.util.BuildProperties;
import de.tbuchloh.util.io.FileProcessor;

/**
 * @author Tobias Buchloh (gandalf)
 * @version $Id: FileFormatMigrationTest.java,v 1.2 2007/03/11 22:23:43 tbuchloh Exp $
 */
public class FileFormatMigrationTest extends TestCase {

    static final class TANListChecker extends ModelNodeVisitorAdapter {

        public boolean found;

        @Override
        public void visit(final BankAccount account) {
            for (final TANList tanList : account.getTanLists()) {
                final List<TAN> tans = tanList.getTans();
                for (int i = 0; i < tans.size(); ++i) {
                    System.out.println(tans.get(i));
                    assertEquals(i + 1, tans.get(i).getId());
                    found = true;
                }
            }
        }
    }

    private static class UuidCheckerVisitor implements ModelNodeVisitor {

        private void check(final ModelNode account) {
            assertTrue("Invalid uuid=" + account.getUuid(), //
                    account.getUuid().matches("N[0-9]+"));
        }

        @Override
        public void visit(final Attachment attachment) {
            check(attachment);
        }

        @Override
        public void visit(final BankAccount account) {
            check(account);
        }

        @Override
        public void visit(final CreditCard card) {
            check(card);
        }

        @Override
        public void visit(final GenericAccount account) {
            check(account);
        }

        @Override
        public void visit(final Group group) {
            check(group);
        }

        @Override
        public void visit(final NetAccount account) {
            check(account);
        }

        @Override
        public void visit(final SecuredFile account) {
            check(account);
        }

    }

    private static final File V15 = new File("src/test/resources/kiskis-0.15.xml");

    /**
     * Die {@link File}
     */
    private static final File V24 = new File("src/test/resources/kiskis-0.24-w-attachments.xml");

    /**
     * @param lhs
     *            the expected attachments
     * @param rhs
     *            the actual attachments
     */
    private void assertAttachments(Collection<Attachment> lhs, Collection<Attachment> rhs) throws IOException {
        assertEquals(lhs.size(), rhs.size());

        for (Iterator<Attachment> i = lhs.iterator(), j = rhs.iterator(); i.hasNext();) {
            final Attachment attLhs = i.next();
            final Attachment attRhs = j.next();
            assertTrue(String.format("%1$s does not match %2$s", attLhs, attRhs), attLhs.matches(attRhs));
            assertEquals(FileProcessor.readFile(PersistenceManager.createAttachmentFile(attLhs)),
                    FileProcessor.readFile(PersistenceManager.createAttachmentFile(attRhs)));
        }
    }

    /**
     * @param root
     *            is the root node
     */
    public void assertKonto1(final Group root) {
        final Group g1 = CollectionUtils.get(root.getGroups(), 0);
        final SecuredElement s1 = CollectionUtils.get(g1.getElements(), 0);
        assertEquals("Konto 1", s1.getName());
        assertEquals(2, s1.getAttachments().size());

        final Attachment a1 = CollectionUtils.get(s1.getAttachments(), 0);
        assertEquals(1, a1.getId());
        assertEquals("CHANGELOG", a1.getName());
        assertEquals("a2Fn_Z>yENCh", new String(a1.getKey()));
        assertEquals("04dacafe-c5b5-4cc2-829b-67164403b14a", a1.getUuid());

        final Attachment a2 = CollectionUtils.get(s1.getAttachments(), 1);
        assertEquals(2, a2.getId());
        assertEquals("AUTHORS", a2.getName());
        assertEquals("K}gY\"6CaGV+y", new String(a2.getKey()));
        assertEquals("08439fd5-92c1-4285-95c8-e3674c711089", a2.getUuid());
    }

    /**
     * @param root
     *            is the root node
     */
    public void assertKonto2(final Group root) {
        final Group g1 = CollectionUtils.get(root.getGroups(), 5);
        final SecuredElement s1 = CollectionUtils.get(g1.getElements(), 0);
        assertEquals("Konto 2", s1.getName());
        assertEquals(1, s1.getAttachments().size());

        final Attachment a1 = CollectionUtils.get(s1.getAttachments(), 0);
        assertEquals(3, a1.getId());
        assertEquals("LICENSE", a1.getName());
        assertEquals("B=_$D?z7vq4N", new String(a1.getKey()));
        assertEquals("fd4fedae-c341-479b-8bc1-ad22a142baa1", a1.getUuid());
    }

    /**
     * @param root
     *            is the root node
     */
    public void assertKonto3(final Group root) {
        final Group g1 = CollectionUtils.get(root.getGroups(), 3);
        final SecuredElement s1 = CollectionUtils.get(g1.getElements(), 0);
        assertEquals("Konto 3", s1.getName());
        assertEquals(3, s1.getAttachments().size());

        final Attachment a1 = CollectionUtils.get(s1.getAttachments(), 0);
        assertEquals(4, a1.getId());
        assertEquals("README", a1.getName());
        assertEquals("oJKbB+I.%w3?", new String(a1.getKey()));
        assertEquals("321d0aa6-655d-4a71-8344-bd4713b6e8cd", a1.getUuid());

        final Attachment a2 = CollectionUtils.get(s1.getAttachments(), 1);
        assertEquals(5, a2.getId());
        assertEquals("LICENSE", a2.getName());
        assertEquals("%\\`VoZT+6Ewp", new String(a2.getKey()));
        assertEquals("b188c1ee-262a-45c6-9187-999a4fb86e28", a2.getUuid());

        final Attachment a3 = CollectionUtils.get(s1.getAttachments(), 2);
        assertEquals(6, a3.getId());
        assertEquals("build.xml", a3.getName());
        assertEquals("J@@0{g97Ks:u", new String(a3.getKey()));
        assertEquals("a3c86196-793e-4724-a109-df3bb363f97f", a3.getUuid());
    }

    private void checkTans(final TPMDocument doc) {
        final TANListChecker listChecker = new TANListChecker();
        doc.getGroups().visitPreOrder(listChecker);
        assertTrue(listChecker.found);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        BuildProperties.VALIDATE_DOCS = false;
        super.tearDown();
    }

    public void testVersion015ToCurrent() throws Exception {
        BuildProperties.VALIDATE_DOCS = false;
        final TPMDocument doc = PersistenceManager.load(PGPEncryptionTest.createContext(V15),
                new PGPEncryptionTest.FailTestErrorHandler(false));
        checkTans(doc);

        assertNotNull(doc.getUuid());
        doc.getGroups().visit(new UuidCheckerVisitor());

        final Group newGroup = new Group("123");
        final NetAccount na = new NetAccount();
        na.setEmail("mymail@foo.de");
        na.setName("new account");
        na.setPwd(new Password("12345".toCharArray()));
        newGroup.add(na);
        doc.getGroups().add(newGroup);

        final File tmp = new File("target/FileFormatMigrationTest.testVersion015ToCurrent.xml");
        PersistenceManager.saveAs(doc, PGPEncryptionTest.createContext(tmp), false);

        final TPMDocument reloaded = PersistenceManager.load(PGPEncryptionTest.createContext(tmp),
                new PGPEncryptionTest.FailTestErrorHandler(true));
        checkTans(reloaded);

        assertTrue(doc.equals(reloaded));
        assertTrue(doc.matches(reloaded));
    }

    /**
     * Test für testVersion024ToCurrent
     * 
     * @throws Exception
     *             wenn unvorhergesehene Fehler auftreten.
     */
    public void testVersion024ToCurrent() throws Exception {
        BuildProperties.VALIDATE_DOCS = true;
        final TPMDocument doc = PersistenceManager.load(PGPEncryptionTest.createContext(V24),
                new PGPEncryptionTest.FailTestErrorHandler(false));
        assertEquals(6, doc.getAttachments().size());

        assertEquals("Kommentar mit\nZeilenumbrüchen,\n1\n2\n3\n", doc.getGroups().getComment());

        assertKonto1(doc.getGroups());
        assertKonto2(doc.getGroups());
        assertKonto3(doc.getGroups());

        final File tofile = new File("target/FileFormatMigrationTest.testVersion024ToCurrent.xml");
        PersistenceManager.saveAs(doc, PGPEncryptionTest.createContext(tofile, false), false);

        final TPMDocument doc2 = PersistenceManager.load(PGPEncryptionTest.createContext(tofile, false),
                new PGPEncryptionTest.FailTestErrorHandler(true));

        assertKonto1(doc2.getGroups());
        assertKonto2(doc2.getGroups());
        assertKonto3(doc2.getGroups());

        assertAttachments(doc.getAttachments(), doc2.getAttachments());

        assertTrue(doc.matches(doc2));
    }

}
