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

package de.tbuchloh.kiskis.gui;

/**
 * <b>ContentChangedListener</b>: notifies the main application that the
 * document was changed or a parent panel that a value of an element has
 * triggered.
 * 
 * @author gandalf
 * @version $Id: ContentChangedListener.java,v 1.1 2004/06/10 07:55:15 tbuchloh
 *          Exp $
 */
interface ContentChangedListener {
	/**
	 * @param changed
	 *            true, if an document element was changed. False if the
	 *            document was saved or changes were undone.
	 */
	public void contentChanged(boolean changed);
}
