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

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * <b>WelcomeView</b>:
 * 
 * @author gandalf
 * @version $Id: WelcomeView.java,v 1.1 2004/06/10 07:55:15 tbuchloh Exp $
 */
final class WelcomeView extends SpecialView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JLabel _defaultMessage;

	/**
	 * creates a new WelcomeView
	 */
	public WelcomeView() {
		super();
		this.setLayout(new BorderLayout());
		_defaultMessage = new JLabel(
				Messages.getString("WelcomeView.welcome_msg")); //$NON-NLS-1$
		_defaultMessage.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(_defaultMessage, BorderLayout.CENTER);
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.SpecialView#storeValues()
	 */
	@Override
	protected void store() {
		// do nothing
	}
}
