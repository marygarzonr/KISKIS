package de.tbuchloh.kiskis.model.cracklib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.solinger.cracklib.CrackLib;
import org.solinger.cracklib.Packer;

import de.tbuchloh.kiskis.util.FileTools;
import de.tbuchloh.util.logging.LogFactory;

/**
 * @author Tobias Buchloh (gandalf)
 * @version $Id: Dictionary.java,v 1.3 2007/12/08 13:41:30 tbuchloh Exp $
 */
public class Dictionary {

    private static final Log LOG = LogFactory.getLogger();

    /**
     * Creates an new dictionary from a wordlist.
     * 
     * @param wordlistFile
     *            the wordlist file.
     * @param dictFileDir
     *            the target cracklib dictionary directory
     * @return the newly created dictionary.
     * @throws InvalidDictionaryException
     */
    public static Dictionary createDictionary(final File wordlistFile, final String dictFileDir) throws IOException,
    InvalidDictionaryException {
        Packer p = null;
        try {
            final File dirFile = new File(dictFileDir);
            if (dirFile.exists() && !dirFile.isDirectory()) {
                throw new InvalidDictionaryException(String.format("The given path \"%1$s\" is not a directory",
                        FileTools.getShortAbsoluteFilename(dirFile)));
            }
            dirFile.getAbsoluteFile().mkdirs();
            final BufferedReader br = new BufferedReader(new FileReader(wordlistFile));
            String line = null;

            final Set<String> sortedLines = new TreeSet<String>();
            while ((line = br.readLine()) != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Add " + line);
                }
                if (line.length() == 0) {
                    continue;
                }
                sortedLines.add(line);
            }
            br.close();

            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Read %1$s words", sortedLines.size()));
            }

            final String packerFileName = dictFileDir + "/cracklib";
            p = new Packer(packerFileName, "rw");
            for (final String ln : sortedLines) {
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Put " + ln);
                    }
                    p.put(ln);
                } catch (final IllegalArgumentException e) {
                    LOG.debug(e, e);
                    throw new InvalidDictionaryException(e.getMessage());
                }
            }

            p.close();
            p = null;

            return new Dictionary(dictFileDir);
        } finally {
            if (p != null) {
                try {
                    p.close();
                } catch (final Exception e) {
                    // TODO: fix nullpointer in cracklib.Packer.flush
                    LOG.debug(e, e);
                }
            }
        }
    }

    /**
     * @param dirname
     *            the dictionary directory
     * @return true if [dirname]/cracklib.pwd does already exist
     */
    public static boolean exists(String dirname) {
        return new File(dirname + "/cracklib.pwd").exists();
    }

    /**
     * @param cracklibDir
     *            the cracklib dictionary directory
     * @return the dictionary file
     */
    public static Dictionary open(final String cracklibDir) throws IOException {
        final String fullFilename = cracklibDir + "/cracklib.pwd";
        final File file = new File(fullFilename);
        if (file.isDirectory()) {
            throw new IOException(String.format("The cracklib dictionary \"%1$s\" does not exist!",
                    FileTools.getShortAbsoluteFilename(file)));
        }
        return new Dictionary(cracklibDir);
    }

    private final String _directory;

    private final Packer _packer;

    protected Dictionary(final String directory) throws IOException {
        this(directory, new Packer(directory + "/cracklib", "r"));
    }

    protected Dictionary(final String directory, final Packer packer) {
        _directory = directory;
        _packer = packer;
    }

    /**
     * @return the filename of the cracklib dictionary. without .pwd-Extension.
     */
    public String getDirectory() {
        return _directory;
    }

    /**
     * @param word
     *            the word to lookup
     * @return a message from cracklib if the word was in the wordlist, null otherwise.
     * @throws IOException
     *             if something is wrong with the _packer
     */
    public String lookup(final String word) throws IOException {
        final String value = CrackLib.fascistLook(_packer, word.toLowerCase());
        // if this actually reaches this spot
        // Change some dialog element to indicate if we were able
        // to crack the password
        return value;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Dictionary[filename=");
        sb.append(_directory);
        sb.append(",packer=");
        sb.append(_packer);
        sb.append("]");
        return sb.toString();
    }
}
