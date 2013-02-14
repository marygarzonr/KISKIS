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

package de.tbuchloh.kiskis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import de.tbuchloh.util.crypto.CryptoException;
import de.tbuchloh.util.crypto.SymmetricAlgo;

/**
 * <b>FileTools</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public abstract class FileTools {

    private static final String CRYPTO_ENGINE_CLASSNAME = "cryptoEngineClass";

    private static final Preferences P = Preferences.userNodeForPackage(FileTools.class);

    public static void decrypt(final char[] key, final File source, final File target) throws CryptoException,
    IOException, FileNotFoundException {
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException("No key specified!");
        }
        final FileInputStream fis = new FileInputStream(source);
        final FileOutputStream fos = new FileOutputStream(target);
        try {
            getCryptoEngine().decrypt(key, fis, fos);
        } finally {
            fis.close();
            fos.close();
        }
    }

    public static void encrypt(final char[] key, final File source, final File target) throws CryptoException,
    IOException, FileNotFoundException {
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException("No key specified!");
        }
        final FileInputStream fis = new FileInputStream(source);
        final FileOutputStream fos = new FileOutputStream(target);
        try {
            getCryptoEngine().encrypt(key, fis, fos);
        } finally {
            fis.close();
            fos.close();
        }
    }

    /**
     * @return Returns the crypto algorithm.
     */
    public static SymmetricAlgo getCryptoEngine() {
        final String clazz = Settings.getCryptoEngineClass();
        try {
            return (SymmetricAlgo) Class.forName(clazz).newInstance();
        } catch (final Exception e) {
            throw new Error(e);
        }
    }

    /**
     * Gets a shortened filename
     * 
     * <ul>
     * <li>C:\test.xml => C:\test.xml</li>
     * <li>C:\foo\test.xml => C:\foo\test.xml</li>
     * <li>C:\foo\bar\test.xml => C:\...\bar\test.xml</li>
     * <li>C:\foo\bar\hugo\test.xml => C:\...\hugo\test.xml</li>
     * </ul>
     * 
     * @param file
     *            the file
     * @return a shortened filename
     */
    public static String getShortAbsoluteFilename(File file) {
        File f = file.getAbsoluteFile();
        final List<File> path = new LinkedList<File>();
        while (f != null) {
            path.add(0, f);
            f = f.getParentFile();
        }

        if (path.size() <= 4) {
            return file.getAbsolutePath();
        }

        final StringBuilder sb = new StringBuilder();
        // complicated thing for Windows machines because C:\ is a path element, but file.getName() does not return C:\
        // as expected.
        sb.append(path.get(0).getAbsolutePath().replaceAll(Pattern.quote(String.valueOf(File.separatorChar)) + "$", ""));
        sb.append(File.separatorChar);
        sb.append(path.get(1).getName());
        sb.append(File.separatorChar);
        sb.append("...");
        sb.append(File.separatorChar);
        sb.append(path.get(path.size() - 2).getName());
        sb.append(File.separatorChar);
        sb.append(path.get(path.size() - 1).getName());
        return sb.toString();
    }

    /**
     * @param dir
     *            is the directory
     * @param filter
     *            is the filename regular expression, e. g. (.*)\\.xml
     * @return the collected files
     */
    public static Collection<File> listFiles(File dir, final String filter) {
        assert dir.isDirectory() : dir.toString();

        final String[] names = dir.list(new FilenameFilter() {

            @Override
            public boolean accept(File d, String name) {
                return name.matches(filter);
            }
        });
        final Collection<File> files = new ArrayList<File>(names.length);
        for (final String n : names) {
            files.add(new File(dir, n));
        }
        return files;
    }

    public static String readFirstLine(final File file) throws IOException {
        BufferedReader sr = null;
        try {
            sr = new BufferedReader(new FileReader(file));
            final String line = sr.readLine();
            return line;
        } finally {
            if (sr != null) {
                sr.close();
            }
        }
    }

    /**
     * @param crypto
     *            The algorithm to set.
     */
    public static void setCryptoEngine(final SymmetricAlgo crypto) {
        P.put(CRYPTO_ENGINE_CLASSNAME, crypto.getClass().getName());
    }

    /**
     * creates a new FileTools
     */
    private FileTools() {
        super();
    }

}
