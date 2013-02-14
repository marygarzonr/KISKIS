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

package de.tbuchloh.kiskis.model;

import java.util.Calendar;

import de.tbuchloh.kiskis.model.annotations.Observable;
import de.tbuchloh.kiskis.model.annotations.SecretInfo;
import de.tbuchloh.kiskis.util.DateUtils;

/**
 * <b>TAN</b>: represents a single transaction number.
 * 
 * @author gandalf
 * @version $Id: TAN.java,v 1.7 2007/03/12 19:05:58 tbuchloh Exp $
 */
@Observable
public final class TAN implements Cloneable, ModelConstants {

    @SecretInfo
    private String _id;

    @SecretInfo
    private String _number;

    private Calendar _used;

    /**
     * creates a new TAN
     */
    public TAN() {
        super();
        _used = null;
        _number = "";
        _id = "0";
    }

    /**
     * Overridden!
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public TAN clone() {
        try {
            final TAN inst = (TAN) super.clone();
            inst._used = this._used == null ? null : (Calendar) this._used
                    .clone();
            return inst;
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TAN)) {
            return false;
        }

        final TAN castOther = (TAN) other;
        return (_used == null || _used.equals(castOther._used))
        && _number.equals(castOther._number);
    }

    /**
     * @return the id
     */
    public int getId() {
        return Integer.valueOf(this._id);
    }

    /**
     * @return Returns the number.
     */
    public String getNumber() {
        return _number;
    }

    /**
     * @return Returns the used. can be null if it was not used before.
     */
    public Calendar getUsed() {
        return _used;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final int id) {
        this._id = String.valueOf(id);
    }

    /**
     * @param number
     *            The number to set.
     */
    public void setNumber(final String number) {
        _number = number;
    }

    /**
     * @param isUsed
     *            true, if the TAN was used, false otherwise.
     */
    public void setUsed(final boolean isUsed) {
        if (isUsed) {
            _used = DateUtils.getCurrentDateTime();
        } else {
            _used = null;
        }
    }

    /**
     * @param used
     *            The used to set.
     */
    public void setUsed(final Calendar used) {
        _used = used;
    }

    /**
     * @return true, if the TAN was used.
     */
    public boolean wasUsed() {
        return _used != null;
    }
}
