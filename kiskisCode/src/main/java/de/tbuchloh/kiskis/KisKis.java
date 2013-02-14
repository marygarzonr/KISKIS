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
 * http://www.sourceforge.net/projects/kiskis
 */

package de.tbuchloh.kiskis;

import static java.lang.String.format;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.MainFrame;
import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.kiskis.model.cracklib.Dictionary;
import de.tbuchloh.kiskis.util.BuildProperties;
import de.tbuchloh.kiskis.util.KisKisException;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.StopWatch;
import de.tbuchloh.util.concurrent.deadlock.DefaultDeadlockListener;
import de.tbuchloh.util.concurrent.deadlock.ThreadDeadlockDetector;
import de.tbuchloh.util.io.FileProcessor;
import de.tbuchloh.util.localization.Messages;

/**
 * <b>KisKis</b>: starts the main-application and LOG!
 * 
 * @author gandalf
 * @version $Id: KisKis.java,v 1.18 2007/02/18 14:38:18 tbuchloh Exp $
 */
public abstract class KisKis {

    private static final Log LOG = LogFactory.getLog(KisKis.class);

    private static final Messages M = new Messages(KisKis.class);

    private static final File PREF_FILE = new File("kiskis.preferences");

    /**
     * @throws KisKisException
     *             exports the preferences
     */
    public static void exportPreferences() throws KisKisException {
        try {
            LOG.info("exporting preferences to " + KisKis.PREF_FILE);
            Preferences.userNodeForPackage(KisKis.class).exportSubtree(new FileOutputStream(KisKis.PREF_FILE));
        } catch (final Exception e) {
            throw new KisKisException(e.getMessage(), e);
        }
    }

    private static void initPreferences() {
        final File p = KisKis.PREF_FILE;
        if (p.exists()) {
            try {
                LOG.debug("importing preferences from " + p);
                Preferences.importPreferences(new FileInputStream(p));
            } catch (final Exception e) {
                LOG.error("The preference file " + p + " could not be imported!", e);
            }
        }
    }

    public static void main(final String[] args) {
        logSystemInfo();

        final StopWatch w = new StopWatch();
        initThreadMonitors();

        initPreferences();

        final MainFrame wnd = MainFrame.getInstance();
        LOG.info("Window created in: " + w.getDuration());
        final LongOpt[] longs = {
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, '?'),
                new LongOpt("file", LongOpt.REQUIRED_ARGUMENT, null, 'f'),
                new LongOpt("validate", LongOpt.NO_ARGUMENT, null, 'v'),
                new LongOpt("hide", LongOpt.NO_ARGUMENT, null, 'h'),
                new LongOpt("installCracklibDict", LongOpt.NO_ARGUMENT, null, 'i'),
                new LongOpt("reset", LongOpt.NO_ARGUMENT, null, 'r'),
                new LongOpt("selftest", LongOpt.NO_ARGUMENT, null, 's'),
                new LongOpt("lastFile", LongOpt.NO_ARGUMENT, null, 'l')
        };
        final Getopt g = new Getopt("KisKis", args, "", longs, true);
        int c = 0;
        boolean show = true;
        while ((c = g.getopt()) != -1) {
            switch (c) {
            case '?':
                showHelp();
                System.exit(1);
                break;
            case 'f':
                // we need to show the window
                wnd.setVisible(true);
                wnd.loadFile(new File(g.getOptarg()));
                break;
            case 'h':
                show = false;
                break;
            case 'i':
                installCracklibDict();
                break;
            case 'v':
                BuildProperties.VALIDATE_DOCS = true;
                break;
            case 'r':
                resetPreferences();
                System.exit(0);
                break;
            case 's':
                wnd.showSelftest();
                break;
            case 'l':
                // we need to show the window
                wnd.setVisible(true);
                wnd.loadLastFile();
                break;
            default:
                showHelp();
                System.exit(1);
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (Settings.isExportingPrefs()) {
                    try {
                        exportPreferences();
                    } catch (final KisKisException e) {
                        MessageBox.showException(e);
                    }
                }
            }
        });
        wnd.showTray();
        wnd.setVisible(show);
        wnd.showTipOfTheDay();
    }

    /**
     * Write system properties to console
     */
    @SuppressWarnings({
        "unchecked"
    })
    private static void logSystemInfo() {
        final StringBuilder sb = new StringBuilder("System Properties:\n");
        final Map<String, String> props = new TreeMap(System.getProperties());
        for (final java.util.Iterator<Map.Entry<String, String>> i = props.entrySet().iterator(); i.hasNext();) {
            final Map.Entry<String, String> entry = i.next();
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append("\n");
        }
        LOG.info(sb.toString());
    }

    private static void initThreadMonitors() {
        final UncaughtExceptionHandler eh = new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOG.debug("Uncaught Exception occurred! e=" + e.getMessage());
                try {
                    MessageBox.showException(e);
                } catch (final Throwable throwable) {
                    LOG.error("Uncaught Exception could not be displayed due to another exception! anotherThrowable="
                            + throwable + ", rootCause=" + e, throwable);
                }
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(eh);

        final ThreadDeadlockDetector detector = new ThreadDeadlockDetector();
        detector.addListener(new DefaultDeadlockListener());
        detector.addListener(new DefaultDeadlockListener() {

            private volatile boolean fired = false;

            @Override
            public void deadlockDetected(Thread[] deadlockedThreads) {
                if (fired) {
                    return;
                }
                fired = true;

                final File file = new File(System.getProperty("java.io.tmpdir"), "kiskis-deadlock.txt");
                final String msg = format(
                        "Threads were blocked!\nYou need to restart the application.\nPlease mail file \"%2$s\" as bug.\nthreads=%1$s",
                        Arrays.toString(deadlockedThreads), file.getAbsolutePath());
                JOptionPane.showMessageDialog(null, msg, "Deadlock detected!", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * a hack for Java Web Start. The dictionary is delivered in a Jar and needs to be unpacked into the directory.
     */
    private static void installCracklibDict() {
        final File dir = new File(Settings.getCracklibDict());
        if (!Dictionary.exists(dir.getName())) {
            final File absoluteDir = new File(System.getProperty("java.io.tmpdir"), "kiskis-dictionary");
            LOG.info("The dictionary " + dir + " does not exist. Creating tmp dictionary dir="
                    + absoluteDir.getAbsolutePath());
            absoluteDir.mkdirs();

            // we need to install the dictionary
            final String[] resources = {
                    "cracklib.pwi", "cracklib.hwm", "cracklib.pwd"
            };
            try {
                for (final String s : resources) {
                    final String resourceName = "/" + s;
                    final URL url = KisKis.class.getResource(resourceName);
                    if (url == null) {
                        throw new KisKisException("The resource name " + resourceName + " does not exist!");
                    }

                    LOG.info("Getting url=" + url);

                    final File target = new File(absoluteDir, s);
                    try {
                        FileProcessor.copyStream(url.openStream(), new FileOutputStream(target));
                        LOG.info("File " + target + " created.");
                    } catch (final IOException e) {
                        throw new KisKisException("Could not copy " + url + " to " + target, e);
                    }
                }

                Settings.setCracklibDict(absoluteDir.getAbsolutePath());
            } catch (final KisKisException e) {
                LOG.error("Could not install dictionary in file system", e);
            }
        } else {
            LOG.info("The dictionary does already exist. Skipping ...");
        }
    }

    private static void resetPreferences() {
        try {
            final Preferences p = Preferences.userNodeForPackage(KisKis.class);
            p.removeNode();
            p.flush();

            if (PREF_FILE.exists()) {
                PREF_FILE.delete();
            }
            System.out.println("All preferences for package '" + KisKis.class.getPackage().getName()
                    + "' were removed!\nYou can restart the application now!");
        } catch (final BackingStoreException e) {
            throw new Error(e);
        }
    }

    private static void showHelp() {
        final StringBuffer sb = new StringBuffer();

        sb.append("\n");
        sb.append(BuildProperties.getFullTitle());
        sb.append(" by ");
        sb.append(BuildProperties.getBuildProperties().getString("build.authors"));
        sb.append("\n");

        sb.append("Build: \t\t");
        sb.append(BuildProperties.getBuildProperties().getString("build.timestamp"));
        sb.append('\n');

        sb.append("Website: \t");
        sb.append(BuildProperties.getBuildProperties().getString("build.website"));
        sb.append("\n\n");

        sb.append("Usage: \t\tKisKis <options>*\n\n");
        sb.append("Options:\n");
        sb.append("\t-file <file>\topens this data file immediately.\n");
        sb.append("\t-help\t\tshows this help message.\n");
        sb.append("\t-hide hides the application in system tray if supported.\n");
        sb.append("\t-lastFile\topens the last opened data file immediately.\n");
        sb.append("\t-reset\tdeletes all stored user preferences.\n");
        sb.append("\t-selftest \tchecks all supported cryptographic algorithms.\n");
        sb.append("\t-validate\tenables the validation mechanism while loading a document.\n");

        System.err.println(sb.toString());
    }
}
