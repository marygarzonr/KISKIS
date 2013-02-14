/*
 * Copyright (C) 2004 by Tobias Buchloh. This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Library General
 * Public License as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details. You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA If you didn't download
 * this code from the following link, you should check if you aren't using an
 * obsolete version: http://www.sourceforge.net/projects/KisKis
 */

package de.tbuchloh.kiskis.util;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.MainFrame;

/**
 * <b>Settings</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public final class Settings {

    private static final String D_CRYPTO_ENGINE_CLASS = "de.tbuchloh.util.crypto.OpenPGPwithAES256";

    private static final String D_DEFAULT_LNF = "com.incors.plaf.kunststoff.KunststoffLookAndFeel"; //$NON-NLS-1$

    private static final String D_PASSWORD_FIELD_FONT = "Monospaced";

    private static final String K_AUTOSAVE_DELAY = "autoSaveDelay"; //$NON-NLS-1$

    private static final String K_AUTOSAVE_ISENABLED = "autosaveIsEnabled"; //$NON-NLS-1$

    private static final String K_BUFFER_PWD = "bufferPwd"; //$NON-NLS-1$

    private static final String K_CHECK_UPDATES = "checkUpdates";

    private static final String K_CRACKLIB_DICT = "cracklibDict";

    private static final String K_CRYPTO_ENGINE_CLASS = "cryptoEngineClass";

    private static final String K_DEFAULT_LNF = "defaultLookAndFeel"; //$NON-NLS-1$

    private static final String K_DEFAULT_PWD_DURATION_DAYS = "defaultPwdExpiryDays"; //$NON-NLS-1$

    private static final String K_DISPLAY_LAST_VIEWED_ELEMENT = "displayLastViewedElement";

    private static final String K_IS_CHECKING_MASTER_PASSWORD = "isCheckingMasterPassword";

    private static final String K_IS_SHOWING_ARCHIVED_ITEMS = "isShowingArchivedItems";

    private static final String K_LAST_FILE = "lastFile"; //$NON-NLS-1$

    private static final String K_LAST_PATH = "lastPath";

    private static final String K_LOCK_AFTER_MIN = "lockAfterMin"; //$NON-NLS-1$

    private static final String K_PASSWORD_FIELD_FONT = "passwordFieldFont";

    private static final String K_PROGRAM_VERSION = "programVersion";

    private static final String K_PWD_DISPOSE_DELAY = "pwdDisposeDelay"; //$NON-NLS-1$

    /**
     * contains the information for starting external applications selected by an URL
     */
    public static final String K_URL_ALLPROGS_NODE = "k_url_allprogs_node"; //$NON-NLS-1$

    /**
     * defines the command line for an application
     */
    public static final String K_URL_PROG_CMD = "k_url_progs_cmd"; //$NON-NLS-1$

    /**
     * contains prefix of an URL which identifies the application to use
     */
    public static final String K_URL_PROG_PREFIX = "k_url_prog_prefix"; //$NON-NLS-1$

    private static final String K_VIEW_COUNTER_DELAY = "viewCounterDelay";

    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLog(Settings.class);

    private static final Preferences PREF = initPreferences();

    /**
     * @return the delay between auto save actions in MSEC.
     */
    public static long getAutoSaveDelay() {
        return PREF.getLong(K_AUTOSAVE_DELAY, 5 * 60 * 1000);
    }

    /**
     * Get the current cracklib dictionary
     */
    public static String getCracklibDict() {
        return PREF.get(K_CRACKLIB_DICT, "tools/dictionaries/default");
    }

    /**
     * @return the fully qualified classname.
     */
    public static String getCryptoEngineClass() {
        final String ret = PREF.get(K_CRYPTO_ENGINE_CLASS, D_CRYPTO_ENGINE_CLASS);
        if (ret == null) {
            throw new NoSuchElementException(K_CRYPTO_ENGINE_CLASS);
        }
        return ret;
    }

    /**
     * @return the currently used L&F.
     */
    public static String getDefaultLookAndFeel() {
        final String os = System.getProperty("os.name");
        String def = null;
        if (os.indexOf("Mac") != -1) {
            def = UIManager.getCrossPlatformLookAndFeelClassName();
        } else {
            def = D_DEFAULT_LNF;
        }
        return PREF.get(K_DEFAULT_LNF, def);
    }

    /**
     * @return the number of days a new created password will be valid.
     */
    public static int getDefaultPwdExpiryDays() {
        return PREF.getInt(K_DEFAULT_PWD_DURATION_DAYS, 365);
    }

    public static String getLastFile() {
        return PREF.get(K_LAST_FILE, "kiskis-passwords.xml.gpg"); //$NON-NLS-1$
    }

    public static String getLastPath() {
        return PREF.get(K_LAST_PATH, ".");
    }

    /**
     * @return Returns the lockAfterMin.
     */
    public static int getLockAfterMin() {
        return PREF.getInt(K_LOCK_AFTER_MIN, 5);
    }

    /**
     * @return the font name to use for password-fields. Default is {@value #D_PASSWORD_FIELD_FONT}
     */
    public static String getPasswordFieldFont() {
        return PREF.get(K_PASSWORD_FIELD_FONT, D_PASSWORD_FIELD_FONT);
    }

    public static Preferences getPreferences() {
        return PREF;
    }

    /**
     * @return time in MSEC until the password is disposed.
     */
    public static long getPwdDisposeDelay() {
        return PREF.getLong(K_PWD_DISPOSE_DELAY, 0);
    }

    /**
     * @return the time in MSEC until the view counter of an element will be increased.
     */
    public static long getViewCounterDelay() {
        return PREF.getLong(K_VIEW_COUNTER_DELAY, 10 * 1000);
    }

    public static Preferences initPreferences() {
        final Preferences n = Preferences.userNodeForPackage(Settings.class);
        final String newVersion = BuildProperties.getVersion();
        final String previousVersion = n.get(K_PROGRAM_VERSION, newVersion);

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("oldVersion=%1$s, newVersion=%2$s", previousVersion, newVersion));
        }

        if (!VersionTools.isCompatible(previousVersion, newVersion)) {
            LOG.debug("Versions not compatible! Recreating preference node for version=" + newVersion);
            try {
                n.removeNode();
            } catch (final BackingStoreException e) {
                LOG.error("Could not remove preference node", e);
            }

            final Preferences newN = Preferences.userNodeForPackage(Settings.class);
            newN.put(K_PROGRAM_VERSION, newVersion);
            return newN;
        }

        LOG.debug("Preferences seem to be compatible");
        return n;
    }

    /**
     * @return true, if the auto save feature is activated.
     */
    public static boolean isAutoSaveEnabled() {
        return PREF.getBoolean(K_AUTOSAVE_ISENABLED, true);
    }

    /**
     * @return true, if the master password should be buffered.
     */
    public static boolean isBufferingPwd() {
        return PREF.getBoolean(K_BUFFER_PWD, true);
    }

    /**
     * @return {@link #K_IS_CHECKING_MASTER_PASSWORD}
     */
    public static boolean isCheckingMasterPassword() {
        return PREF.getBoolean(K_IS_CHECKING_MASTER_PASSWORD, true);
    }

    public static boolean isCheckingUpdates() {
        return PREF.getBoolean(K_CHECK_UPDATES, true);
    }

    /**
     * @param prefVersion
     * @return
     */
    public static boolean isCompatible(final String prefVersion) {
        final Pattern versionPattern = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)(\\-.+)?");
        final Matcher prefVersionMatcher = versionPattern.matcher(prefVersion);
        if (!prefVersionMatcher.matches()) {
            throw new KisKisRuntimeException(String.format("Preference version %1$s has an invalid format!",
                    prefVersion));
        }

        final Matcher buildVersionMatcher = versionPattern.matcher(BuildProperties.getVersion());
        if (!buildVersionMatcher.matches()) {
            throw new KisKisRuntimeException(String.format("Build version %1$s has an invalid format!",
                    BuildProperties.getVersion()));
        }

        final boolean isCompatible = buildVersionMatcher.group(1).equals(prefVersionMatcher.group(1))
        && buildVersionMatcher.group(2).equals(prefVersionMatcher.group(2));
        return isCompatible;
    }

    /**
     * @return true if the last viewed element should be opened
     */
    public static boolean isDisplayLastViewedElement() {
        return PREF.getBoolean(K_DISPLAY_LAST_VIEWED_ELEMENT, true);
    }

    /**
     * @return
     * @see MainFrame#setExportPreferences(boolean)
     */
    public static boolean isExportingPrefs() {
        return PREF.getBoolean("exportPrefs", false); //$NON-NLS-1$
    }

    /**
     * @return {@link #K_IS_SHOWING_ARCHIVED_ITEMS}
     */
    public static boolean isShowingArchivedItems() {
        return PREF.getBoolean(K_IS_SHOWING_ARCHIVED_ITEMS, false);
    }

    /**
     * @param enabled
     *            true, if the feature should be used.
     * @param delay
     *            time in MSEC between actions.
     */
    public static void setAutoSave(final boolean enabled, final long delay) {
        PREF.putBoolean(K_AUTOSAVE_ISENABLED, enabled);
        PREF.putLong(K_AUTOSAVE_DELAY, delay);
    }

    /**
     * @param enabled
     *            true, if the feature should be used.
     * @param delay
     *            is the time in MSEC until the passsword will be disposed.
     */
    public static void setBufferPwd(final boolean enabled, final long delay) {
        PREF.putBoolean(K_BUFFER_PWD, enabled);
        PREF.putLong(K_PWD_DISPOSE_DELAY, delay);
    }

    /**
     * @param value
     *            {@link #K_IS_CHECKING_MASTER_PASSWORD}
     */
    public static void setCheckingMasterPassword(boolean value) {
        PREF.putBoolean(K_IS_CHECKING_MASTER_PASSWORD, value);
    }

    public static void setCheckingUpdates(final boolean value) {
        PREF.putBoolean(K_CHECK_UPDATES, value);
    }

    /**
     * @param dictname
     *            String filename of dictionary file
     */
    public static void setCracklibDict(final String dictname) {
        PREF.put(K_CRACKLIB_DICT, dictname);
    }

    /**
     * @param clazz
     *            is the fully qualified classname of the crypto engine to use.
     */
    public static void setCryptoEngineClass(final String clazz) {
        PREF.put(K_CRYPTO_ENGINE_CLASS, clazz);
    }

    /**
     * @param clazz
     *            is the class name.
     */
    public static void setDefaultLookAndFeel(final String clazz) {
        PREF.put(K_DEFAULT_LNF, clazz);
    }

    /**
     * @param days
     *            the number of days a new created pwd will be valid.
     */
    public static void setDefaultPwdExpiryDays(final int days) {
        PREF.putInt(K_DEFAULT_PWD_DURATION_DAYS, days);
    }

    public static void setDisplayLastViewedElement(boolean value) {
        PREF.putBoolean(K_DISPLAY_LAST_VIEWED_ELEMENT, value);
    }

    /**
     * @param enabled
     *            true, if the preferences should be saved on exit.
     */
    public static void setExportPreferences(final boolean enabled) {
        PREF.putBoolean("exportPrefs", enabled); //$NON-NLS-1$
    }

    public static void setLastFile(final File file) {
        setLastPath(file);
        PREF.put(K_LAST_FILE, file.getAbsolutePath());
    }

    public static void setLastPath(final File file) {
        PREF.put(K_LAST_PATH, file.getAbsolutePath());
    }

    /**
     * @param lockAfterMin
     *            The lockAfterMin to set.
     */
    public static void setLockAfterMin(final int lockAfterMin) {
        PREF.putInt(K_LOCK_AFTER_MIN, lockAfterMin);
    }

    /**
     * @param fontName
     *            is the font name to use for password-fields
     */
    public static void setPasswordFieldFont(final String fontName) {
        PREF.put(K_PASSWORD_FIELD_FONT, fontName);
    }

    /**
     * @param show
     *            {@link #K_IS_SHOWING_ARCHIVED_ITEMS}
     */
    public static void setShowingArchivedItems(boolean show) {
        PREF.putBoolean(K_IS_SHOWING_ARCHIVED_ITEMS, show);
    }

    /**
     * @param delay
     *            is the time in MSEC after an item is marked as viewed.
     */
    public static void setViewCounterDelay(final long delay) {
        PREF.putLong(K_VIEW_COUNTER_DELAY, delay);
    }

    /**
     * creates a new Settings
     */
    private Settings() {
        super();
    }
}
