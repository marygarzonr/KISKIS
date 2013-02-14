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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <b>Messages</b>:
 * 
 * @author gandalf
 * @version $Id: Messages.java,v 1.2 2007/12/02 12:44:03 tbuchloh Exp $
 */
public class Messages {

	private static final String BUNDLE_NAME = "de.tbuchloh.kiskis.gui.Messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public Messages messages_0 = null;

	/**
	 * creates a new message object
	 */
	private Messages() {
		// nothing todo
	}

	/**
	 * @param key
	 *            the named item
	 * @return the corresponding value
	 */
	public static String getString(final String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * @param key
	 *            the named item
	 * @return the first char of the value, 0 if none exists
	 */
	public static char getChar(final String key) {
		try {
			return RESOURCE_BUNDLE.getString(key).charAt(0);
		} catch (final MissingResourceException e) {
			return 0;
		}
	}
}
