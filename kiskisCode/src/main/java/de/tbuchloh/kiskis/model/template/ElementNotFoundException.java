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

import de.tbuchloh.kiskis.util.KisKisException;

/**
 * <b>ElementNotFoundException</b>:
 * 
 * @author gandalf
 * @version $Id: ElementNotFoundException.java,v 1.2 2007/02/18 14:38:11
 *          tbuchloh Exp $
 */
public final class ElementNotFoundException extends KisKisException {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * creates a new ElementNotFoundException
	 * 
	 * @param message
	 *            a tiny message.
	 */
	public ElementNotFoundException(final String message) {
		super(message);
	}

}
