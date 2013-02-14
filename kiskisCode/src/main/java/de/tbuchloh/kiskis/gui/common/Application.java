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

package de.tbuchloh.kiskis.gui.common;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.kiskis.util.KisKisException;
import de.tbuchloh.util.localization.Messages;

/**
 * <b>Application</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public final class Application {

    private static final String DEFAULT_BROWSER = "<default browser>";

    private final class Process implements Runnable {

        @Override
        public void run() {
            try {
                final String command = _program.getCommand();
                LOG.debug("executing " + command); //$NON-NLS-1$
                if (command.startsWith(DEFAULT_BROWSER)) {
                    try {
                        Desktop.getDesktop()
                                .browse(new URL(_program.getCommand().replace(DEFAULT_BROWSER, "")).toURI());
                    } catch (final URISyntaxException e) {
                        LOG.error(e, e);
                    }
                } else {
                    final InputStream is = Runtime.getRuntime().exec(command).getErrorStream();
                    int c;
                    while ((c = is.read()) != -1) {
                        System.err.print((char) c);
                    }
                }
                LOG.debug(command + " exited ..."); //$NON-NLS-1$
            } catch (final IOException e) {
                LOG.error(e, e);
            }
        }
    }

    public static final class Program implements Cloneable {

        private String _command;

        private String _urlRegex;

        public Program() {
            _command = ""; //$NON-NLS-1$
            _urlRegex = ""; //$NON-NLS-1$
        }

        public Program(final String urlRegex, final String command) {
            _urlRegex = urlRegex;
            _command = command;
        }

        public void bindVariable(final String key, final String value) {
            _command = _command.replaceAll(key, value);
        }

        /**
         * Overridden!
         * 
         * @see java.lang.Object#clone()
         */
        @Override
        public Object clone() {
            final Program inst = new Program(_urlRegex, _command);
            return inst;
        }

        /**
         * @return Returns the command.
         */
        public String getCommand() {
            return _command;
        }

        /**
         * @return Returns the urlRegex.
         */
        public String getUrlRegex() {
            return _urlRegex;
        }

        /**
         * @param command
         *            The command to set.
         */
        public void setCommand(final String command) {
            _command = command;
        }

        /**
         * @param urlRegex
         *            The urlRegex to set.
         */
        public void setUrlRegex(final String urlRegex) {
            _urlRegex = urlRegex;
        }

        /**
         * Overridden!
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append(_urlRegex);
            sb.append(PART_SEP);
            sb.append(_command);
            return sb.toString();
        }
    }

    public static final class ProgramNotFoundException extends KisKisException {

        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 1L;

        /**
         * creates a new ProgramNotFoundException
         * 
         * @param message
         *            to display
         */
        public ProgramNotFoundException(final String message) {
            super(message);
        }
    }

    protected static final Log LOG = LogFactory.getLog(Application.class);

    private static final String MAIL_KEY;

    private static final String MSG_NO_PROG_FOUND;

    protected static final String PART_SEP;

    private static final String PWD_KEY;

    private static final String URL_KEY;

    private static final String USERNAME_KEY;

    static {
        final Messages m = new Messages(Application.class);
        MAIL_KEY = m.getString("MAIL_KEY"); //$NON-NLS-1$
        MSG_NO_PROG_FOUND = m.getString("MSG_NO_PROG_FOUND"); //$NON-NLS-1$
        PWD_KEY = m.getString("PWD_KEY"); //$NON-NLS-1$
        URL_KEY = m.getString("URL_KEY"); //$NON-NLS-1$
        USERNAME_KEY = m.getString("USERNAME_KEY"); //$NON-NLS-1$
        PART_SEP = m.getString("partSeparator");
    }

    /**
     * @param url
     *            is the URL to open
     * @param prefs
     *            contains the information about the programs.
     * @return a valid Applcation which must be started explicitly
     */
    public static Application create(final String url, final Preferences prefs) throws ProgramNotFoundException {
        return new Application(findProgram(prefs, url), url);
    }

    private static Program findProgram(final Preferences prefs, final String url) throws ProgramNotFoundException {
        final Program[] progs = initPrograms(prefs);
        for (final Program p : progs) {
            final Pattern pattern = Pattern.compile(p.getUrlRegex(), Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(url).matches() || url.startsWith(p.getUrlRegex())) {
                return (Program) p.clone();
            }
        }
        final String format = MessageFormat.format(MSG_NO_PROG_FOUND, new Object[] {
                url
        });
        throw new ProgramNotFoundException(format);
    }

    private static Program[] initPrograms(final Preferences prefs) {
        final List allProgs = loadPrograms(prefs);
        final Program[] p = new Program[allProgs.size()];
        for (int i = 0; i < p.length; i++) {
            p[i] = (Program) allProgs.get(i);
        }
        return p;
    }

    /**
     * @param prefs
     *            the preferences object to load the program from
     * @return a list with Program-objects
     */
    public static List<Program> loadPrograms(final Preferences prefs) {
        final List<Program> allProgs = new ArrayList<Program>();
        final Preferences n = prefs.node(Settings.K_URL_ALLPROGS_NODE);
        String[] progs;
        try {
            progs = n.childrenNames();
        } catch (final BackingStoreException e) {
            throw new Error(e);
        }
        for (final String prog : progs) {
            final Preferences child = n.node(prog);
            final String prefix = child.get(Settings.K_URL_PROG_PREFIX, null);
            final String cmd = child.get(Settings.K_URL_PROG_CMD, null);
            if (prefix == null || cmd == null) {
                LOG.error("The application " //$NON-NLS-1$
                        + prog + " is not specified correctly!"); //$NON-NLS-1$
                continue;
            }
            LOG.debug("command: " + cmd + " for prefix " + prefix); //$NON-NLS-1$ //$NON-NLS-2$
            allProgs.add(new Program(prefix, cmd));
        }

        if (progs.length == 0) {
            allProgs.add(new Program("http", DEFAULT_BROWSER + " " + URL_KEY));
        }
        return allProgs;
    }

    /**
     * @param prefs
     *            is the location to store the program information
     * @param list
     *            is a list of Program objects
     */
    public static void storePrograms(final Preferences prefs, final Collection list) {
        Preferences n = prefs.node(Settings.K_URL_ALLPROGS_NODE);
        try {
            n.removeNode();
        } catch (final BackingStoreException e) {
            throw new Error(e);
        }

        n = prefs.node(Settings.K_URL_ALLPROGS_NODE);
        int cnt = 0;
        for (final Iterator i = list.iterator(); i.hasNext();) {
            final Program p = (Program) i.next();
            final Preferences child = n.node(String.valueOf(cnt++));
            child.put(Settings.K_URL_PROG_PREFIX, p.getUrlRegex());
            child.put(Settings.K_URL_PROG_CMD, p.getCommand());
            LOG.debug("storing " + p); //$NON-NLS-1$
        }
        try {
            n.flush();
        } catch (final BackingStoreException e1) {
            throw new Error(e1);
        }
    }

    protected Program _program;

    /**
     * creates a new Application
     */
    protected Application(final Program p, final String url) {
        super();
        _program = p;
        _program.bindVariable(URL_KEY, url);
    }

    /**
     * @param email
     *            the email to add to the commandline
     */
    public void setEmail(final String email) {
        _program.bindVariable(MAIL_KEY, email);
    }

    /**
     * @param pwd
     *            the password to add to the commandline
     */
    public void setPassword(final char[] pwd) {
        _program.bindVariable(PWD_KEY, new String(pwd));
    }

    /**
     * @param userName
     *            the username to add to the commandline
     */
    public void setUsername(final String userName) {
        _program.bindVariable(USERNAME_KEY, userName);
    }

    /**
     * start the application in a new Thread.
     */
    public void start() {
        final Thread runner = new Thread(new Process());
        runner.start();
    }
}
