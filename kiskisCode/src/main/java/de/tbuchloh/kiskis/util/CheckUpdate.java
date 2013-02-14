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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.PropertyResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.util.swing.dialogs.AboutDialog;
import de.tbuchloh.util.swing.dialogs.AboutDialog.BuildInfo;

/**
 * <b>CheckUpdate</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public final class CheckUpdate {
    /**
     * Commons Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(CheckUpdate.class);

    private static final String VERSION_FILE = BuildProperties
    .getValue("update.file");

    private BuildInfo _version;

    /**
     * creates a new CheckUpdate
     */
    public CheckUpdate() {
        super();
    }

    /**
     * checks the version file and says false, if the version does not match.
     */
    public boolean check() throws IOException {
        LOG.info("Calling " + VERSION_FILE);

        final URL url = new URL(VERSION_FILE);
        final InputStream os = url.openStream();
        if (os == null) {
            return true;
        }

        final PropertyResourceBundle p = new PropertyResourceBundle(
                os);
        os.close();
        _version = AboutDialog.createBuildInfo(p);

        LOG.info("Found version=" + _version);

        return _version.getVersion().equals(BuildProperties.getVersion());
    }

    public String getDate() {
        return _version.getDate();
    }

    public String getTime() {
        return _version.getTime();
    }

    public String getUpdateSite() {
        return _version.getWebsite();
    }

    public String getVersion() {
        return _version.getVersion();
    }

}
