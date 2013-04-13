/*
 * Copyright (C) 2010 by Tobias Buchloh.
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
package de.tbuchloh.kiskis.model.validation;

import java.io.IOException;

import org.apache.commons.logging.Log;

import de.tbuchloh.kiskis.model.cracklib.Dictionary;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.logging.LogFactory;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 01.11.2010
 */
public class DictionaryPasswordValidator extends AbstractPasswordValidator {

    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLogger();

    private String _match;

    /**
     * {@inheritDoc}
     */
    @Override
    public String validatePassword(char[] pwd) {
        try {
            final Dictionary d = Dictionary.open(Settings.getCracklibDict());
            _match = d.lookup(new String(pwd));
            if(_match != null) {
                return _match;
            }
        } catch (final IOException e) {
            LOG.warn("Could not open dictionary! Maybe not installed", e);
        }
        return null;
    }

    /**
     * @return {@link #foundWord}
     */
    public String getMatch() {
        return _match;
    }

}
