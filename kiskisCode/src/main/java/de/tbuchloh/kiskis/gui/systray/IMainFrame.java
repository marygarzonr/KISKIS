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

package de.tbuchloh.kiskis.gui.systray;

import java.awt.PopupMenu;
import java.io.File;
import java.net.URL;

import de.tbuchloh.util.swing.actions.ActionCallback;

/**
 * <b>IMainFrame</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public interface IMainFrame {

	@Deprecated
	public static final String LOCK_SCREEN = "onLockScreen";

	@Deprecated
	public static final String OPEN_DOCUMENT = "openDocument";

	@Deprecated
	public static final String QUIT = "onQuit";

	// public void showTray();

	/**
	 * @param key
	 *            is the action-key to look up
	 * @return the associated action-object.
	 */
	@Deprecated
	public ActionCallback getAction(final String key);

	/**
	 * @return the popup menu for opening files and so on.
	 */
	public PopupMenu getPopupMenu();

	/**
	 * @return the tray icon to display.
	 */
	public URL getTrayIconURL();

	/**
	 * @return true, if the window is visible.
	 */
	public boolean isVisible();

	/**
	 * @param file
	 *            is the file to load.
	 */
	@Deprecated
	public void loadFile(File file);

	/**
	 * @param b
	 *            true, if the window should be shown.
	 */
	public void setVisible(boolean b);
}