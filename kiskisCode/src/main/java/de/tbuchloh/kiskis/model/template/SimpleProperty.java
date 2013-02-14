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

import de.tbuchloh.util.reflection.ClasspathInspector;

/**
 * <b>SimpleProperty</b>: is a String-based property. Like texts, date, url, and
 * so on.
 * 
 * @author gandalf
 * @version $Id: SimpleProperty.java,v 1.2 2007/02/18 14:38:11 tbuchloh Exp $
 */
public final class SimpleProperty extends AccountProperty {

    private final Class<?> _valueClass;

    /**
     * creates a new SimpleProperty
     * 
     * @param clazz
     *            is the expected value class.
     */
    public SimpleProperty(final Class<?> clazz) {
	_valueClass = clazz;
    }

    /**
     * creates a new SimpleProperty
     * 
     * @param clazz
     *            is the expected value class.
     */
    public SimpleProperty(String type, final Class<?> clazz) {
	_valueClass = clazz;
	setType(type);
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.template.AccountProperty#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(final Object value) {
	return value == null
	|| ClasspathInspector.checkClass(value.getClass(), _valueClass);
    }

}
