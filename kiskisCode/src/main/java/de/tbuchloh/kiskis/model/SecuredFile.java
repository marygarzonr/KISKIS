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

package de.tbuchloh.kiskis.model;

import java.util.regex.Pattern;

import de.tbuchloh.kiskis.model.annotations.SecretInfo;

/**
 * <b>SecuredFile</b>: a secured file points to the location in the file system.
 * 
 * @author gandalf
 * @version $Id: SecuredFile.java,v 1.6 2007/02/18 14:37:29 tbuchloh Exp $
 */
public class SecuredFile extends SecuredElement {

    private static final long serialVersionUID = 1L;

    @SecretInfo
    private String _file;

    /**
     * creates a new secured file.
     */
    public SecuredFile() {
        super();
        _file = "";
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public SecuredFile clone() {
        final SecuredFile inst = (SecuredFile) super.clone();
        inst._file = this._file;
        return inst;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.ModelNode#contains(Pattern)
     */
    @Override
    public boolean contains(final Pattern regex) {
        return super.contains(regex) || regex.matcher(_file).matches();
    }

    /**
     * @return the protected file
     */
    public String getFile() {
        return _file;
    }

    @Override
    public boolean matches(final SecuredElement other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SecuredFile)) {
            return false;
        }
        final SecuredFile castOther = (SecuredFile) other;
        return super.equals(other) && _file.equals(castOther._file);
    }

    /**
     * @param file
     *            the protected file
     */
    public void setFile(final String file) {
        _file = file;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.ModelNode#visit(de.tbuchloh.kiskis.model.ModelNodeVisitor)
     */
    @Override
    public void visit(final ModelNodeVisitor visitor) {
        visitor.visit(this);
    }
}
