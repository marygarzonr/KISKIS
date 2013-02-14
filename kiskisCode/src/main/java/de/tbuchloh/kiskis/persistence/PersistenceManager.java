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

import static de.tbuchloh.kiskis.util.FileTools.listFiles;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.model.Attachment;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.util.FileTools;
import de.tbuchloh.kiskis.util.KisKisException;
import de.tbuchloh.kiskis.util.KisKisRuntimeException;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.StopWatch;
import de.tbuchloh.util.crypto.CryptoException;
import de.tbuchloh.util.crypto.CryptoInstallationTester;
import de.tbuchloh.util.crypto.SymmetricAlgo;
import de.tbuchloh.util.exceptions.ExceptionConverter;
import de.tbuchloh.util.io.FileProcessor;
import de.tbuchloh.util.localization.Messages;

/**
 * <b>PersistenceManager</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public abstract class PersistenceManager {

    private static final class BackupFilter implements FilenameFilter {

        private final String _prefix;

        /**
         * creates a new BackupFilter
         * 
         * @param file
         *            is the original file
         */
        public BackupFilter(final File file) {
            _prefix = file.getName() + BAK_ID;
        }

        /**
         * Overridden!
         * 
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        @Override
        public boolean accept(final File dir, final String name) {
            if (name.startsWith(_prefix)) {
                // there has to be at least one char after the last dot.
                return (name.lastIndexOf('.') + 1) < name.length();
            }
            return false;
        }

    }

    public static final String ATTACHMENT_EXT = ".attachment.";

    protected static final SimpleDateFormat BAK_EXT = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final String BAK_ID = ".backup.";

    private static final MessageFormat ERR_ATT_NOT_EXISTS;

    private static final MessageFormat ERR_NOT_DECRYPTED;

    private static final MessageFormat ERR_UNKNOWN_TYPE;

    private static final Comparator<File> FILE_BY_DATE = new Comparator<File>() {

        @Override
        public int compare(final File lhs, final File rhs) {
            final Date lhsDate = getDate(lhs.getName());
            final Date rhsDate = getDate(rhs.getName());
            if (lhsDate.before(rhsDate)) {
                return -1;
            }
            return 1;
        }

        private String extractDate(final String name) {
            int offset = name.lastIndexOf(BAK_ID);
            offset += BAK_ID.length();
            return name.substring(offset);
        }

        private Date getDate(final String name) {
            try {
                final String date = extractDate(name);
                assert date.length() == BAK_EXT.toPattern().length();
                return BAK_EXT.parse(date);
            } catch (final ParseException e) {
                throw new Error("should never happen!", e);
            }
        }

    };

    private static final boolean IS_DELETING_ORPHANS = false;

    private static final String K_MAX_BACKUPS = "maxBackups";

    private static final Log LOG = LogFactory.getLog(PersistenceManager.class);

    private static int maxBackups;

    private static final MessageFormat ERR_ATT_DISAPPEARED;

    private static final Preferences P;

    private static final String PGP_HEADER = "-----BEGIN PGP MESSAGE-----";

    private static final String XML_HEADER = "<?xml";

    private static final MessageFormat ERR_UNKOWN_FILE;

    private static final Messages M = new Messages(PersistenceManager.class);

    static {
        ERR_UNKNOWN_TYPE = M.getFormat("ERR_UNKNOWN_TYPE"); //$NON-NLS-1$
        ERR_NOT_DECRYPTED = M.getFormat("ERR_NOT_DECRYPTED"); //$NON-NLS-1$
        ERR_ATT_NOT_EXISTS = M.getFormat("ERR_ATT_NOT_EXISTS"); //$NON-NLS-1$
        ERR_ATT_DISAPPEARED = M.getFormat("ERR_ATT_DISAPPEARED"); //$NON-NLS-1$
        ERR_UNKOWN_FILE = M.getFormat("ERR_UNKOWN_FILE"); //$NON-NLS-1$

        P = Preferences.userNodeForPackage(PersistenceManager.class);
        maxBackups = P.getInt(K_MAX_BACKUPS, 5);
    }

    /**
     * @param docFile
     *            the documents file
     * @param bakExt
     *            the extension of the backup files
     * @throws IOException
     *             if the attachment cannot be copied
     */
    private static void backupAttachments(File docFile, String bakExt) throws IOException {
        assert docFile.exists();

        for (final File f : FileTools.listFiles(docFile.getParentFile(), createAttachmentNamePattern(docFile))) {
            final File to = new File(f.getAbsolutePath() + bakExt);
            LOG.debug(String.format("Creating backup for attachment %1$s to %2$s", f.getName(), to.getName()));
            FileProcessor.copy(f, to);
        }
    }

    private static void checkAttachments(TPMDocument doc, IErrorHandler handler) {
        // check for missing attachment files
        for (final Attachment att : doc.getAttachments()) {
            final File attFile = createAttachmentFile(att);
            if (!attFile.isFile()) {
                handler.error("The attachment " + att.getId() + ", " + attFile + " (" + att.getDescription()
                        + ") does not exist!");
            }
        }

        // check for attachment files without any data representation
        for (final File f : getOrphanedAttachmentFiles(doc)) {
            handler.error("The file " + f + " is orphaned!");
            if (isDeletingOrphans() && f.delete()) {
                handler.error(f + " DELETED!");
            }
        }
    }

    public static FileFormats checkMimeType(final File file) throws PersistenceException {
        try {
            final String line = FileTools.readFirstLine(file);
            if (line == null) {
                LOG.debug("found empty file ...");
                return FileFormats.UNKNOWN;
            } else if (line.startsWith(PGP_HEADER)) {
                LOG.debug("found PGP-file ...");
                return FileFormats.PGP_FILE;
            } else if (line.startsWith(XML_HEADER)) {
                LOG.debug("found XML-file ... ");
                return FileFormats.XML_FILE;
            } else if (file.getName().endsWith("3des")) {
                LOG.debug("found TripeDES-file ... ");
                return FileFormats.TRIPLEDES_FILE;
            }
            LOG.debug("found unkown file line=" + line);
            return FileFormats.UNKNOWN;
        } catch (final IOException e) {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    private static void checkXmlHeader(final File file, final byte[] decrypted) throws PersistenceException {
        try {
            final String line = new String(decrypted, 0, Math.min(16, decrypted.length), "UTF-8");
            if (!line.startsWith(XML_HEADER)) {
                final Object[] p = {
                        file.getName(), line
                };
                throw new PersistenceException(ERR_NOT_DECRYPTED.format(p));
            }
        } catch (final UnsupportedEncodingException e) {
            throw new KisKisRuntimeException("Unsupported encoding!", e);
        }

    }

    private static void copyAttachments(final TPMDocument doc, final File dataFile) {
        for (final Attachment att : doc.getAttachments()) {
            try {
                copyAttachmentToDocument(att, dataFile);
            } catch (final IOException e) {
                LOG.error("Could not copy attachment " + att, e);
            }
        }
    }

    /**
     * @param att
     *            is the attachment to be copied
     * @param document
     *            is the document file which will own the attachments copied.
     * @throws IOException
     *             if the attachment could not be copied.
     */
    private static void copyAttachmentToDocument(Attachment att, File document) throws IOException {
        final File source = createAttachmentFile(att);
        final File target = createAttachmentFile(document.getAbsolutePath(), att.getId());
        FileProcessor.copy(source, target);
    }

    /**
     * @param att
     *            the attachment
     * @return the file to the attachment
     */
    static File createAttachmentFile(Attachment att) {
        return createAttachmentFile(att.getDocument().getFile().getAbsolutePath(), att.getId());
    }

    /**
     * @param documentFile
     *            the path incl. file prefix
     * @param id
     *            the attachment id
     * @return the file
     */
    private static File createAttachmentFile(final String documentFile, final int id) {
        return new File(documentFile + ATTACHMENT_EXT + id);
    }

    /**
     * @param docFile
     *            the document file
     * @return the pattern which attachment files need to fulfill
     */
    private static String createAttachmentNamePattern(File docFile) {
        return Pattern.quote(docFile.getName()) + "\\.attachment\\.[0-9]+";
    }

    private static void createBackup(final TPMDocument doc) throws IOException {
        if (doc.getFile().exists()) {
            deleteOldBackups(doc.getFile());
            if (maxBackups > 0) {
                final String orig = doc.getFile().getAbsolutePath();
                final String bakExt = BAK_ID + BAK_EXT.format(new Date());
                final File bak = new File(orig + bakExt);
                LOG.debug("creating backup: " + bak.getName());

                backupAttachments(doc.getFile(), bakExt);

                final File tmp = new File(doc.getFile().getAbsolutePath());
                if (!tmp.renameTo(bak)) {
                    throw new IOException(String.format("The file %1$s cannot be renamed to %2$s!", tmp, bak));
                }
                assert bak.exists() && !doc.getFile().exists();
            }
        }
    }

    /**
     * @param doc
     *            the document
     */
    private static void createNewAttachments(TPMDocument doc) throws IOException, CryptoException {
        final List<File> disappearedFiles = new ArrayList<File>();
        for (final Attachment att : doc.getAttachments()) {
            if (att.getAttachedFile() != null) {
                final File target = createAttachmentFile(att);
                LOG.debug(String.format("Encrypting attachment %1$s to %2$s", att.getAttachedFile(), target));
                try {
                    FileTools.encrypt(att.getKey(), att.getAttachedFile(), target);
                    att.setAttachedFile(null);
                } catch (final FileNotFoundException e) {
                    disappearedFiles.add(att.getAttachedFile());
                }
            }
        }

        if (!disappearedFiles.isEmpty()) {
            // we use just the first file for the error message and bail out
            final String filename = FileTools.getShortAbsoluteFilename(disappearedFiles.get(0));
            throw new CryptoException(ERR_ATT_DISAPPEARED.format(new Object[] {
                    filename
            }));
        }
    }

    /**
     * @param doc
     *            the document
     */
    private static void checkNewAttachments(TPMDocument doc) throws IOException, CryptoException {
        for (final Attachment att : doc.getAttachments()) {
            if (att.getAttachedFile() != null && !att.getAttachedFile().exists()) {
                final String filename = FileTools.getShortAbsoluteFilename(att.getAttachedFile());
                throw new CryptoException(ERR_ATT_DISAPPEARED.format(new Object[] {
                        filename
                }));
            }
        }
    }

    public static InputStream decrypt(final File file, InputStream is, final ICryptoContext ctx)
    throws PersistenceException, CryptoException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(estimateLength(file));

            ctx.decrypt(is, bos);

            is.close();

            final byte[] decrypted = bos.toByteArray();
            // final Field bufField = ByteArrayOutputStream.class.getDeclaredField("buf");
            // bufField.setAccessible(true);
            // final byte[] bufValue = (byte[]) bufField.get(bos);
            // for (int i = 0; i < bufValue.length; i++) {
            // bufValue[i] = -1;
            // }
            bos = null;

            checkXmlHeader(file, decrypted);
            return new ByteArrayInputStream(decrypted);
        } catch (final IOException e) {
            throw new PersistenceException(e.getMessage(), e);
        } catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /**
     * removes the attachment from disk.
     * 
     * @param att
     *            the attachment to delete from disk
     */
    private static void deleteAttachment(Attachment att) {
        final File file = createAttachmentFile(att);
        if (!file.delete()) {
            LOG.error("Could not delete the file" + file.getName());
        } else {
            LOG.debug("File " + file.getName() + " deleted!");
        }
    }

    private static void deleteOldBackups(final File file) {
        final File[] files = file.getParentFile().listFiles(new BackupFilter(file));
        LOG.debug("found " + files.length + " backup files");
        final List<File> sorted = new ArrayList<File>(Arrays.asList(files));
        Collections.sort(sorted, FILE_BY_DATE);

        int toDelete = files.length - maxBackups + 1;
        for (int i = 0; i < sorted.size() && toDelete > 0; i++, toDelete--) {
            final File documentFile = sorted.get(i);
            LOG.debug("deleting document backup file " + documentFile.getName());

            final String extension = getBackupExtension(documentFile);
            final String backupExtensionFilter = "(.*)" + Pattern.quote(extension);

            LOG.debug("backupExtensionFilter is " + backupExtensionFilter);

            for (final File f : listFiles(documentFile.getParentFile(), backupExtensionFilter)) {
                LOG.debug("Deleting backup file " + f);
                f.delete();
            }
        }
    }

    /**
     * @param documentFile
     *            the document backup file
     * @return the extension incl. BAK_ID
     */
    static String getBackupExtension(File documentFile) {
        final Pattern pattern = Pattern.compile("(.*?)(" + Pattern.quote(BAK_ID) + "[0-9]{14})$");
        final Matcher m = pattern.matcher(documentFile.getName());
        if (!m.matches()) {
            throw new IllegalArgumentException(//
                    String.format("The backup file %1$s has an invalid extension!", documentFile.getName()));
        }
        return m.group(2);
    }

    /**
     * @param doc
     *            the document
     */
    private static void deleteOrphanedAttachmentFiles(TPMDocument doc) {
        for (final File f : getOrphanedAttachmentFiles(doc)) {
            LOG.info("Deleting orphaned attachment file " + f);
            if (!f.delete()) {
                final String msg = String.format("The orphaned attachment file \"%1$s\" cannot be deleted!");
                LOG.error(msg);
            }
        }
    }

    private static int estimateLength(final File file) {
        return (int) (10 * file.length());
    }

    /**
     * @return the maximum number of backup files.
     */
    public static final int getMaxBackups() {
        return maxBackups;
    }

    /**
     * @return all files without a representing attachment object.
     */
    private static Collection<File> getOrphanedAttachmentFiles(TPMDocument doc) {
        final Set<File> knownFiles = new HashSet<File>();
        for (final Attachment a : doc.getAttachments()) {
            final File attFile = createAttachmentFile(a);
            knownFiles.add(attFile.getAbsoluteFile());
        }

        final Collection<File> listFiles = new HashSet<File>();
        for (final File f : listAttachments(doc)) {
            listFiles.add(f.getAbsoluteFile());
        }
        listFiles.removeAll(knownFiles);
        return listFiles;
    }

    /**
     * @return {@value #IS_DELETING_ORPHANS}
     */
    private static boolean isDeletingOrphans() {
        return IS_DELETING_ORPHANS;
    }

    /**
     * @return all the attachments file objects in the directory, which are associated with this documents. Orphaned
     *         files are listed as well.
     */
    private static Collection<File> listAttachments(TPMDocument doc) {
        final File file = doc.getFile();
        assert file.getParentFile().isDirectory();
        return FileTools.listFiles(file.getParentFile(), createAttachmentNamePattern(file));
    }

    /**
     * load the file.
     * 
     * @param ctx
     *            is the file to load.
     * @param handler
     *            will report the warning and error warnings.
     * @throws KisKisException
     *             if the password is not correct or the file could not be read.
     */
    public static TPMDocument load(final ICryptoContext ctx, final IErrorHandler handler) throws PersistenceException,
    CryptoException {
        LOG.debug("loading " + ctx);

        InputStream is = null;
        final StopWatch w = new StopWatch();
        final File file = ctx.getFile();
        try {
            final FileFormats type = checkMimeType(file);
            is = new FileInputStream(file);
            switch (type) {
            case XML_FILE:
                break;
            case TRIPLEDES_FILE:
            case PGP_FILE:
                is = decrypt(file, is, ctx);
                break;
            default:
                throw new PersistenceException(ERR_UNKOWN_FILE.format(new Object[] {
                        FileTools.getShortAbsoluteFilename(file)
                }));
            }
            final XMLReader xml = new XMLReader(ctx.getFile());
            xml.setErrorHandler(handler);
            final TPMDocument doc = xml.load(is);

            checkAttachments(doc, handler);

            LOG.info("loaded in [length=" + file.length() + "]: " + w.getDuration());
            return doc;
        } catch (final IOException e) {
            LOG.debug("load failed!", e);
            throw new PersistenceException(e.getMessage(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (final IOException e) {
                    LOG.error("close failed!", e);
                }
            }
        }
    }

    /**
     * saves the file to disc.
     * 
     * @param pwd
     *            the associated password or null if it should be stored as plain text.
     * @param createBackup
     *            true, if a copy of the doc should be created.
     * @throws KisKisException
     *             if anything is wrong.
     */
    private static void save(final TPMDocument doc, final ICryptoContext ctx, final boolean createBackup)
    throws CryptoException, PersistenceException {
        LOG.debug("saving ctx=" + ctx + ", createBackup=" + createBackup);
        try {
            assert ctx.getFile().equals(doc.getFile());

            final StopWatch w = new StopWatch();

            checkNewAttachments(doc);

            LOG.info("new attachments checked in " + w.reset());

            if (createBackup) {
                createBackup(doc);
                LOG.info("backup created in " + w.reset());
            }

            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final XMLWriter writer = new XMLWriter();
            writer.save(doc, bos);

            LOG.info("XML generated in " + w.reset());

            final OutputStream os = new BufferedOutputStream(new FileOutputStream(doc.getFile()));
            ctx.encrypt(bos, os);
            os.flush();
            os.close();

            LOG.info("file written in " + w.reset());

            createNewAttachments(doc);

            LOG.info("new attachments created in " + w.reset());

            deleteOrphanedAttachmentFiles(doc);

            LOG.info("orphaned attachments cleaned in " + w.reset());
        } catch (final IOException e) {
            throw new PersistenceException(e.getMessage(), e);
        }

        // set chmod on unix if possible
        final String chmod = "chmod 600 " + doc.getFile();
        try {
            Runtime.getRuntime().exec(chmod);
        } catch (final IOException e) {
            LOG.info("cannot execute " + chmod + " ! msg=" + e.getMessage());
        }
    }

    /**
     * save the file under a different name.
     * 
     * @param ctx
     *            is the crypto context to use for encryption.
     * @param createBackup
     *            true, if a copy should be created.
     * @throws KisKisException
     *             if anything is wrong
     */
    public static void saveAs(final TPMDocument doc, final ICryptoContext ctx, final boolean createBackup)
    throws PersistenceException, CryptoException {
        final File file = ctx.getFile();

        // we need to make sure that we can roll back this operation
        final File tmp = doc.getFile();
        try {
            if (!doc.getFile().equals(file)) {
                copyAttachments(doc, file);
            }
            doc.setFile(file);
            save(doc, ctx, createBackup);
        } catch (final PersistenceException e) {
            doc.setFile(tmp);
            throw e;
        } catch (final CryptoException e) {
            doc.setFile(tmp);
            throw e;
        }
    }

    /**
     * @param target
     *            the target file to store the decrypted data.
     * @throws KisKisException
     *             if anything went wrong!:
     */
    public static void saveAttachmentAs(Attachment att, final File target) throws PersistenceException {
        try {
            final File source = createAttachmentFile(att);
            if (!source.isFile()) {
                LOG.debug("Attachment " + source + " does not exist");
                if (att.getAttachedFile() == null) {
                    throw new PersistenceException(ERR_ATT_NOT_EXISTS.format(new Object[] {
                            att.getDescription(), att.getId()
                    }));
                }
                LOG.debug("Copying just attached file " + att.getAttachedFile());
                FileProcessor.copy(att.getAttachedFile(), target);
            } else {
                LOG.debug("Decrypting file " + source + " to " + target);
                final char[] key = att.getKey();
                FileTools.decrypt(key, source, target);
            }
        } catch (final Exception e) {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    /**
     * @param maxBackups
     *            the maximum number of backup files.
     */
    public static final void setMaxBackups(final int maxBackups) {
        PersistenceManager.maxBackups = maxBackups;
        P.putInt(K_MAX_BACKUPS, maxBackups);
    }

    /**
     * @return the result
     */
    public static String checkCryptography() {
        final Collection<CryptoInstallationTester> all = new ArrayList<CryptoInstallationTester>();
        final Map<String, CryptoInstallationTester> failed = new LinkedHashMap<String, CryptoInstallationTester>();

        Map.Entry<String, SymmetricAlgo> firstOK = null;
        for (final Map.Entry<String, SymmetricAlgo> entry : SupportedAlgorithmsUtil.getSupportedAlgorithms().entrySet()) {
            final SymmetricAlgo algo = entry.getValue();
            final CryptoInstallationTester tester = new CryptoInstallationTester(entry.getKey(), algo);
            all.add(tester);
            tester.check();
            if (tester.getException() != null) {
                tester.getException().printStackTrace();
                failed.put(entry.getKey(), tester);
            } else if (firstOK == null) {
                firstOK = entry;
            }
        }

        final StringBuilder sb = new StringBuilder(M.getString("checkCryptography.PROLOG"));
        if (failed.isEmpty()) {
            sb.append(M.getString("checkCryptography.OK"));
        } else {
            final StringBuilder algos = new StringBuilder();
            for (final Map.Entry<String, CryptoInstallationTester> t : failed.entrySet()) {
                final String msg = M.format("checkCryptography.FAILED_ALGO", //
                        t.getKey());
                algos.append(msg);
            }
            String defaultAlgo = "Not available";
            if (firstOK != null) {
                defaultAlgo = firstOK.getKey();
                Settings.setCryptoEngineClass(firstOK.getValue().getClass().getName());
            }
            sb.append(M.format("checkCryptography.FAILED", algos.toString(), failed.size(), defaultAlgo));
        }

        sb.append(M.getString("checkCryptography.OUTPUT_LABEL"));

        for (final CryptoInstallationTester tester : all) {
            sb.append(tester.getResult());
            sb.append('\n');
        }

        return sb.toString();
    }

}
