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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>SystemTrayFactory</b>:
 * 
 * @author gandalf
 * @version $Id: SystemTrayFactory.java,v 1.8 2007/02/18 14:38:16 tbuchloh Exp $
 */
public final class SystemTrayFactory {

	private static final class DummySystray implements ISystemTray {

		/**
		 * Overridden!
		 * 
		 * @see de.tbuchloh.kiskis.gui.systray.ISystemTray#dispose()
		 */
		public void dispose() {
			// does nothing
		}

		/**
		 * Overridden!
		 * 
		 * @see de.tbuchloh.kiskis.gui.systray.ISystemTray#isValid()
		 */
		public boolean isValid() {
			return false;
		}

		/**
		 * Overridden!
		 * 
		 * @see de.tbuchloh.kiskis.gui.systray.ISystemTray#show()
		 */
		public void show() {
			// does nothing
		}

	}

	private static final Log LOG = LogFactory.getLog(SystemTrayFactory.class);

	/**
	 * @param main
	 *            is the frame to control.
	 * @return a corresponding system tray depending on the underlying os.
	 */
	public static ISystemTray create(final IMainFrame main) {
		try {
			return new Java6SystemTray(main);
		} catch (final Throwable e) {
			LOG.warn("Could not instantiate system tray "
					+ "- perhaps SWT is not installed!\n" + e.getMessage());
			return new DummySystray();
		}
	}

	/**
	 * creates a new SystemTrayFactory
	 */
	protected SystemTrayFactory() {
		super();
	}

}
