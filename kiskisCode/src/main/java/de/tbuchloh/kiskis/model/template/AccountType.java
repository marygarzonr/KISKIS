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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tbuchloh.util.localization.Messages;

/**
 * <b>AccountType</b>:
 * 
 * @author gandalf
 * @version $Id: AccountType.java,v 1.6 2007/02/18 14:38:11 tbuchloh Exp $
 */
public final class AccountType implements Comparable<AccountType> {

    private static final MessageFormat ERR_PROP_NOT_FOUND;

    static {
        final Messages msg = new Messages(AccountType.class);
        ERR_PROP_NOT_FOUND = msg.getFormat("propertyNotFound");
    }

    private String _name;

    private List<AccountProperty> _properties;

    /**
     * creates a new AccountType
     */
    public AccountType() {
        super();
        _name = "";
        _properties = new ArrayList<AccountProperty>();
    }

    /**
     * @param property
     *            the property to add
     */
    public void addProperty(AccountProperty property) {
        _properties.add(property);
    }

    /**
     * Overridden!
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final AccountType rhs) {
        return _name.compareTo(rhs.getName());
    }

    /**
     * @return true, if the name is equal!
     * 
     *         Overridden!
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        final AccountType castedObj = (AccountType) o;
        return this._name.equals(castedObj._name);
    }

    /**
     * @return Returns the name.
     */
    public final String getName() {
        return _name;
    }

    /**
     * @return Returns the properties.
     */
    public final List<AccountProperty> getProperties() {
        return Collections.unmodifiableList(_properties);
    }

    /**
     * @param type
     *            TODO
     * @param name
     *            is the unique id of this property.
     * @return the valid property.
     * @throws ElementNotFoundException
     *             if there is no property with such a name
     */
    public AccountProperty getProperty(final String type, final String name)
    throws ElementNotFoundException {
        for (final Object element : _properties) {
            final AccountProperty p = (AccountProperty) element;
            if (p.getName().equals(name) && p.getType().equals(type)) {
                return p;
            }
        }
        final String msg = ERR_PROP_NOT_FOUND.format(new Object[] {
                this.getName(), name });
        throw new ElementNotFoundException(msg);
    }

    /**
     * Override hashCode.
     * 
     * @return the Objects hashcode.
     */
    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + _name.hashCode();
        return hashCode;
    }

    /**
     * @param name
     *            The name to set.
     */
    public final void setName(final String name) {
        _name = name;
    }

    /**
     * @param list
     *            are the AccountProperty objects.
     */
    public void setProperties(final List<AccountProperty> list) {
        _properties = list;
    }

    /**
     * Overridden!
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("[AccountType:");
        buffer.append(" _name: ");
        buffer.append(_name);
        buffer.append(" _properties: ");
        buffer.append(_properties);
        buffer.append("]");
        return buffer.toString();
    }

}
