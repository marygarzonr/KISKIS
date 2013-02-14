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

import java.awt.AWTException;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.util.BuildProperties;

/**
 * <b>Java6SystemTray</b>: Uses the SystemTray-Feature of Java 6.
 * 
 * @author gandalf
 * @version $Id$
 */
public class Java6SystemTray implements ISystemTray {

	private static final Log LOG = LogFactory.getLog(Java6SystemTray.class);

	protected final IMainFrame _main;

	protected TrayIcon _trayIcon;

	/**
	 * creates a new Java6SystemTray
	 * 
	 * @param main
	 */
	public Java6SystemTray(final IMainFrame main) {
		_main = main;
	}

	/**
	 * @see de.tbuchloh.kiskis.gui.systray.ISystemTray#dispose()
	 */
	public void dispose() {
		if (_trayIcon != null) {
			SystemTray.getSystemTray().remove(_trayIcon);
			_trayIcon = null;
		}
	}

	/**
	 * @see de.tbuchloh.kiskis.gui.systray.ISystemTray#isValid()
	 */
	public boolean isValid() {
		return _trayIcon != null;
	}

	/**
	 * @see de.tbuchloh.kiskis.gui.systray.ISystemTray#show()
	 */
	public void show() {
		if (!SystemTray.isSupported()) {
			LOG.error("System tray is not supported!");
			return;
		}

		final SystemTray tray = SystemTray.getSystemTray();
		final Image image = Toolkit.getDefaultToolkit().getImage(
				_main.getTrayIconURL());

		final MouseListener mouseListener = new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() >= 2) {
					_main.setVisible(!_main.isVisible());
				}
			}

		};

		final PopupMenu popup = _main.getPopupMenu();

		_trayIcon = new TrayIcon(image, BuildProperties.getFullTitle(), popup);

		_trayIcon.setImageAutoSize(true);
		_trayIcon.addMouseListener(mouseListener);

		try {
			tray.add(_trayIcon);
		} catch (final AWTException e) {
			e.printStackTrace();
		}
	}

}
