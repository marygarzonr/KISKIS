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

package de.tbuchloh.kiskis.model.template;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.tbuchloh.kiskis.model.annotations.SecretInfo;

/**
 * <b>AccountProperty</b>: a property is decribed by a name and a type for the
 * value.
 * 
 * @author gandalf
 * @version $Id: AccountProperty.java,v 1.7 2007/02/18 14:38:11 tbuchloh Exp $
 */
public abstract class AccountProperty implements AccountPropertyTypes {

    /**
     * @param name
     *            is the name of the property.
     * @param type
     *            out of @see AccountPropertyTypes
     * @return the appropriate property.
     */
    public static AccountProperty create(final String name, final String type)
    throws ElementNotFoundException {
        AccountProperty p = null;
        if (T_DATE.equals(type)) {
            p = new SimpleProperty(Calendar.class);
        } else if (T_PWD.equals(type)) {
            p = new SimpleProperty(String.class);
        } else if (T_STRING.equals(type)) {
            p = new SimpleProperty(String.class);
        } else if (T_TEXT.equals(type)) {
            p = new SimpleProperty(String.class);
        } else if (T_URL.equals(type)) {
            p = new SimpleProperty(String.class);
        } else {
            // TODO: localize me
            throw new ElementNotFoundException("No such property type: " + type);
        }
        p.setName(name);
        p.setType(type);
        return p;
    }

    /**
     * @return all known property types.
     */
    public static List<String> getTypes() {
        final List<String> ret = new ArrayList<String>();
        ret.add(T_DATE);
        ret.add(T_PWD);
        ret.add(T_STRING);
        ret.add(T_TEXT);
        ret.add(T_URL);
        Collections.sort(ret);
        return ret;
    }

    @SecretInfo
    private String _name;

    @SecretInfo
    private String _type;

    /**
     * creates a new AccountProperty
     */
    protected AccountProperty() {
        super();
        _name = "";
        _type = "";
    }

    /**
     * Overridden!
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }

        final AccountProperty castedObj = (AccountProperty) o;
        return this._name.equals(castedObj._name)
        && this._type.equals(castedObj._type);
    }

    /**
     * @return Returns the name.
     */
    public final String getName() {
        return _name;
    }

    /**
     * @return Returns the type.
     */
    public final String getType() {
        return _type;
    }

    /**
     * Override hashCode.
     * 
     * @return the Objects hashcode.
     */
    @Override
    public final int hashCode() {
        final int hash = 31 * this._name.hashCode();
        return hash + this._type.hashCode();
    }

    /**
     * @param value
     *            is the value to check.
     * @return true, if the value is a valid one.
     */
    public abstract boolean isValid(Object value);

    /**
     * @param name
     *            The name to set.
     */
    public final void setName(final String name) {
        _name = name;
    }

    /**
     * @param type
     *            The type to set.
     */
    public final void setType(final String type) {
        _type = type;
    }

    /**
     * Overridden!
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("[AccountProperty:");
        buffer.append(" _name: ");
        buffer.append(_name);
        buffer.append(" _type: ");
        buffer.append(_type);
        buffer.append("]");
        return buffer.toString();
    }
}
