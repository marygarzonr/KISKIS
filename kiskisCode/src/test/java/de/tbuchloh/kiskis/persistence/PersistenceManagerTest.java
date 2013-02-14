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

import static de.tbuchloh.kiskis.persistence.PGPEncryptionTest.createContext;
import static de.tbuchloh.kiskis.testutils.CollectionUtils.get;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import de.tbuchloh.kiskis.model.Attachment;
import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.kiskis.model.SecuredElement;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.util.FileTools;
import de.tbuchloh.kiskis.util.BuildProperties;
import de.tbuchloh.kiskis.util.KisKisException;
import de.tbuchloh.util.io.FileProcessor;

/**
 * <b>PersistenceManagerTest</b>: Reads a previously stored file, saves it again
 * and reloads it. After reloading the newly stored file is compared against the
 * original.
 * 
 * The original file contains all possible account types.
 * 
 * @author gandalf
 * @version $Id$
 */
public class PersistenceManagerTest extends TestCase {

    protected static final class MyErrorHandler implements IErrorHandler {

	final List<String> errors = new ArrayList<String>();

	final List<String> warnings = new ArrayList<String>();

	@Override
	public void error(final String message) {
	    errors.add(message);
	}

	@Override
	public void warning(final String message) {
	    warnings.add(message);
	}
    }

    private static File bak;

    private static TPMDocument doc;

    protected static final File FILE = new File(
    "src/test/resources/testfile-all.xml.gpg");

    static {
	BuildProperties.VALIDATE_DOCS = true;
    }

    private static void assertMessages(final MyErrorHandler handler,
	    final int warnings, final int errors) {
	assertEquals(handler.warnings.toString(), warnings,
		handler.warnings.size());
	assertEquals(handler.errors.toString(), errors, handler.errors.size());
    }

    private void printDoc(final TPMDocument d) throws PersistenceException {
	new XMLWriter().save(d, System.out);
    }

    public void test01LoadDocument() throws Exception {
	final File file = FILE;
	final MyErrorHandler err = new MyErrorHandler();
	doc = PersistenceManager.load(PGPEncryptionTest.createContext(file),
		err);

	assertEquals(0, err.errors.size());
	assertTrue(err.warnings.size() <= 1); // at most a version warning
    }

    public void test02SaveDocument() throws Exception {
	bak = new File("target/testSaveDocument.xml");
	PersistenceManager.saveAs(doc,
		PGPEncryptionTest.createContext(bak, false), false);
	printDoc(doc);
    }

    public void test03ReloadDocument() throws Exception {
	final TPMDocument reloaded = PersistenceManager.load(
		PGPEncryptionTest.createContext(bak),
		new PGPEncryptionTest.FailTestErrorHandler());
	printDoc(reloaded);
	assertEquals(doc, reloaded);
	assertTrue(doc.matches(reloaded));
	bak.deleteOnExit();
    }

    public void testLoadInvalidDocument() throws Exception {
	final File invdoc = new File("src/test/resources/invalid-document.xml");
	MyErrorHandler handler = new MyErrorHandler();
	final ICryptoContext ctx = PGPEncryptionTest.createContext(invdoc,
		false);

	// load file with validation enabled
	BuildProperties.VALIDATE_DOCS = true;
	try {
	    PersistenceManager.load(ctx, handler);
	    fail("Validation should fail!");
	} catch (final KisKisException e) {
	    // juhuu
	}
	assertMessages(handler, 0, 0);

	// turn off validation and load again
	// the document is not valid but can be processed correctly
	handler = new MyErrorHandler();
	BuildProperties.VALIDATE_DOCS = false;
	PersistenceManager.load(ctx, handler);
	assertMessages(handler, 1, 0);

	// TODO: add some assertions
    }

    public void testLoadInvalidDTDDocument() throws Exception {
	final File invdoc = new File("src/test/resources/invalid-doctype.xml");
	final MyErrorHandler handler = new MyErrorHandler();
	final ICryptoContext ctx = PGPEncryptionTest.createContext(invdoc,
		false);

	// load file with validation enabled
	BuildProperties.VALIDATE_DOCS = true;
	try {
	    PersistenceManager.load(ctx, handler);
	    fail("Validation should fail!");
	} catch (final KisKisException e) {
	    // juhuu
	    assertEquals("Element type \"TPMDocument\" must be declared.",
		    e.getMessage());
	}
	assertMessages(handler, 0, 1);

	// turn off validation and load again
	BuildProperties.VALIDATE_DOCS = false;
	try {
	    PersistenceManager.load(ctx, new MyErrorHandler());
	    fail("Exception erwartet!");
	} catch (final PersistenceException e) {
	    final String schluesselwort = "Daten konnten nicht importiert werden";
	    final String msg = String.format(
		    "Nachricht enthält nicht '%1$s'! e=%2$s", schluesselwort,
		    e.getMessage());
	    assertTrue(msg, //
		    e.getMessage().contains(schluesselwort));
	}
    }

    public void testTransformDocument() throws Exception {
	final File file = new File("src/test/resources/kiskis-0.13.xml");
	final MyErrorHandler handler = new MyErrorHandler();
	PersistenceManager.load(PGPEncryptionTest.createContext(file, false),
		handler);
	assertEquals(1, handler.warnings.size());
	assertEquals(0, handler.errors.size());

	// TODO: add some assertions
    }

    /**
     * Test für BackupAttachments
     * 
     * @throws Exception
     *             wenn unvorhergesehene Fehler auftreten.
     */
    public void testAttachments() throws Exception {
	PersistenceManager.setMaxBackups(2);

	final File destFile = initDocument();

	// load an check if there are enough attachments
	final TPMDocument doc2 = PersistenceManager.load(
		createContext(destFile),
		new PGPEncryptionTest.FailTestErrorHandler(true));
	assertEquals(6, doc2.getAttachments().size());

	int expectedFileCnt = 7;
	assertFileCount(expectedFileCnt, destFile);

	// remove two attachments, save the document, load it again and check
	// if the backup worked fine
	final Group g1 = get(doc2.getGroups().getGroups(), 0);
	final SecuredElement s1 = get(g1.getElements(), 0);
	assertEquals("Konto 1", s1.getName());
	assertEquals(2, s1.getAttachments().size());
	s1.setAttachments(new ArrayList<Attachment>());
	assertEquals(4, doc2.getAttachments().size());
	saveAndWait(destFile, doc2);

	final TPMDocument doc3 = PersistenceManager.load(
		createContext(destFile),
		new PGPEncryptionTest.FailTestErrorHandler(true));
	assertEquals(4, doc3.getAttachments().size());

	expectedFileCnt = expectedFileCnt + 5;
	assertFileCount(expectedFileCnt, destFile);

	// Add one attachment, remove one attachment, save the document, load it
	// again and check
	// if the backup worked fine
	final Group doc3Root = get(doc3.getGroups().getGroups(), 5);
	final SecuredElement doc3Se = get(doc3Root.getElements(), 0);
	assertEquals("Konto 2", doc3Se.getName());
	assertEquals(1, doc3Se.getAttachments().size());
	doc3Se.setAttachments(new ArrayList<Attachment>());
	final Attachment attNew = new Attachment(doc3);
	attNew.setDescription("New attachment");
	attNew.setAttachedFile(new File("build.xml"));
	doc3Se.addAttachment(attNew);
	assertEquals(4, doc3.getAttachments().size());
	saveAndWait(destFile, doc3);

	final TPMDocument doc4 = PersistenceManager.load(
		createContext(destFile),
		new PGPEncryptionTest.FailTestErrorHandler(true));
	final Group doc4Root = get(doc4.getGroups().getGroups(), 5);
	final SecuredElement doc4Se = get(doc4Root.getElements(), 0);
	final Attachment attReloaded = get(doc4Se.getAttachments(), 0);
	assertAttachmentFile(new File("build.xml"), attReloaded);

	expectedFileCnt = expectedFileCnt + 5;
	assertFileCount(expectedFileCnt, destFile);

	// Let us save again to see if the first backup will be removed
	saveAndWait(destFile, doc4);

	expectedFileCnt = expectedFileCnt - 7 + 5;
	// Now we need to check if the first backup was removed
	assertFileCount(expectedFileCnt, destFile);

	// let us save again to see if the second backup was removed
	saveAndWait(destFile, doc4);

	expectedFileCnt = expectedFileCnt - 5 + 5;
	assertFileCount(expectedFileCnt, destFile);

	// let us remove all backups
	PersistenceManager.setMaxBackups(0);
	saveAndWait(destFile, doc4);

	expectedFileCnt = 5;
	assertFileCount(expectedFileCnt, destFile);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
	PersistenceManager.setMaxBackups(5);
    }

    /**
     * @param expectedFile
     *            the unencrypted source file
     * @param actualAtt
     *            the actual attachment
     */
    private void assertAttachmentFile(File expectedFile, Attachment actualAtt)
    throws Exception {
	assertNotNull(actualAtt);

	final String expectedContent = FileProcessor.readFile(expectedFile);
	final String encryptedContent = FileProcessor
	.readFile(PersistenceManager.createAttachmentFile(actualAtt));

	assertFalse(encryptedContent, expectedContent.equals(encryptedContent));

	final File targetFile = File.createTempFile("assertAttachmentFile",
		"txt", new File("target"));
	PersistenceManager.saveAttachmentAs(actualAtt, targetFile);

	final String decryptedContent = FileProcessor.readFile(targetFile);
	assertEquals(expectedContent, decryptedContent);
    }

    /**
     * Test für BackupExtension
     * 
     * @throws Exception
     *             wenn unvorhergesehene Fehler auftreten.
     */
    public void testBackupExtension() throws Exception {
	assertEquals(".backup.20101026124352",
		PersistenceManager.getBackupExtension(new File(
		"foo.xml.gpg.backup.20101026124352")));

	assertEquals(".backup.20110126000000",
		PersistenceManager.getBackupExtension(new File(
		"f.backup.20110126000000")));

	assertEquals(".backup.20110126235959",
		PersistenceManager.getBackupExtension(new File(
		"f.a.b.c.backup.1.backup.20110126235959")));

	try {
	    PersistenceManager.getBackupExtension(new File(
	    "f.a.b.c.backup.1.backup.201101262359591"));
	    fail("Exception erwartet!");
	} catch (final IllegalArgumentException e) {
	    final String schluesselwort = "invalid extension";
	    final String msg = String.format(
		    "Nachricht enthält nicht '%1$s'! e=%2$s", schluesselwort,
		    e.getMessage());
	    assertTrue(msg, //
		    e.getMessage().contains(schluesselwort));
	}
    }

    /**
     * @param expectedFileCnt
     *            the expected file count for the destFile
     * @param destFile
     *            the actual data file
     */
    private void assertFileCount(int expectedFileCnt, File destFile) {
	final Collection<File> files5 = FileTools.listFiles(
		destFile.getParentFile(), //
		Pattern.quote(destFile.getName()) + "(.*)");
	assertEquals(files5.toString(), expectedFileCnt, files5.size());
    }

    /**
     * @param destFile
     *            the destination
     * @param document
     *            the document
     */
    private void saveAndWait(final File destFile, final TPMDocument document)
    throws Exception {
	PersistenceManager.saveAs(document, createContext(destFile), true);
	Thread.sleep(1000);
    }

    /**
     * @return the newly created file
     */
    public File initDocument() throws Exception {
	final File srcFile = new File(
	"src/test/resources/kiskis-0.24-w-attachments.xml");
	final MyErrorHandler handler = new MyErrorHandler();
	final TPMDocument doc1 = PersistenceManager.load(
		createContext(srcFile), handler);
	assertEquals(6, doc1.getAttachments().size());
	assertEquals(0, handler.errors.size());
	assertEquals(1, handler.warnings.size());

	// Save as another file
	final File dir = new File("target/"
		+ new SimpleDateFormat("yyyyMMdd-HHmmSS").format(new Date()));
	dir.mkdirs();

	final File destFile = new File(dir,
	"PersistenceManagerTest.testAttachments.xml");
	saveAndWait(destFile, doc1);
	return destFile;
    }
}
