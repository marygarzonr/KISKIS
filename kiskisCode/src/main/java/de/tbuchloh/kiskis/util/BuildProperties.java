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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <b>BuildProperties</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public abstract class BuildProperties {

    private static ResourceBundle buildProperties;

    private static ResourceBundle kiskisSettings;

    public static boolean VALIDATE_DOCS = false;

    public static ResourceBundle getBuildProperties() {
        if (buildProperties == null) {
            buildProperties = ResourceBundle.getBundle("kiskis_build"); //$NON-NLS-1$
        }
        return buildProperties;
    }

    public static String getBuildTimeStamp() {
        return getBuildProperties().getString("build.timestamp");
    }

    /**
     * @return The E-Mail address
     */
    public static String getEmail() {
        return getBuildProperties().getString("build.authors");
    }

    /**
     * @return the title - version of the program
     */
    public static String getFullTitle() {
        return getTitle() + " - " + getVersion();
    }

    protected static ResourceBundle getKiskisSettings() {
        if (kiskisSettings == null) {
            kiskisSettings = ResourceBundle.getBundle("kiskis-settings");
        }
        return kiskisSettings;
    }

    public static String getTitle() {
        return getBuildProperties().getString("build.title");
    }

    /**
     * @param key
     *            the key to lookup
     * @return the value as string.
     */
    public static String getValue(final String key)
    throws MissingResourceException {
        return getKiskisSettings().getString(key);
    }

    /**
     * @return the version of the program
     */
    public static String getVersion() {
        return getBuildProperties().getString("build.version");
    }

    /**
     * @return the projects website
     */
    public static String getWebsite() {
        return getBuildProperties().getString("build.website");
    }

    /**
     * @return true, if documents should be validated during the load-process.
     */
    public static boolean isValidatingDocs() {
        return VALIDATE_DOCS;
    }

    /**
     * creates a new BuildProperties
     */
    public BuildProperties() {
        super();
    }

}
