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

package de.tbuchloh.kiskis.gui.widgets;

import java.awt.Component;
import java.util.prefs.Preferences;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>PersistentTabPane</b>: remembers the last picked tab.
 * 
 * @author gandalf
 * @version $Id: PersistentTabPane.java,v 1.3 2007/02/18 14:37:34 tbuchloh Exp $
 */
public final class PersistentTabPane extends JTabbedPane {

	private static final Log LOG = LogFactory.getLog(PersistentTabPane.class);

	private static final Preferences P = Preferences
			.userNodeForPackage(PersistentTabPane.class);

	private static final long serialVersionUID = 6181212813889894420L;

	private String _id;

	/**
	 * creates a new PersistentTabPane
	 * 
	 * @param id
	 *            is the unique identifier for this tabbed pane.
	 */
	public PersistentTabPane(final String id) {
		super();
		init(id);
	}

	/**
	 * creates a new PersistentTabPane
	 * 
	 * @see JTabbedPane#JTabbedPane(int)
	 */
	public PersistentTabPane(final String id, final int tabPlacement) {
		super(tabPlacement);
		init(id);
	}

	/**
	 * creates a new PersistentTabPane
	 * 
	 * @see JTabbedPane#JTabbedPane(int, int)
	 */
	public PersistentTabPane(final String id, final int tabPlacement,
			final int tabLayoutPolicy) {
		super(tabPlacement, tabLayoutPolicy);
		init(id);
	}

	/**
	 * Overridden!
	 * 
	 * @see javax.swing.JTabbedPane#addTab(java.lang.String, java.awt.Component)
	 */
	@Override
	public void addTab(final String title, final Component component) {
		super.addTab(title, component);
		showLastSelectedTab();
	}

	private String getId() {
		return _id + "#selectedIndex";
	}

	protected int getLastSelectedTab() {
		return P.getInt(getId(), 0);
	}

	private void init(final String id) {
		_id = id;
		this.getModel().addChangeListener(new ChangeListener() {

			private boolean _isActive;

			public void stateChanged(final ChangeEvent e) {
				if (_isActive && getSelectedIndex() != getLastSelectedTab()) {
					setLastSelectedTab(getSelectedIndex());
				}
				_isActive = true;
			}
		});
	}

	protected void setLastSelectedTab(final int tab) {
		LOG.debug("storing selected tab=" + tab);
		P.putInt(getId(), tab);
	}

	private void showLastSelectedTab() {
		final int i = Math.max(0, getLastSelectedTab());
		// LOG.debug("show last selected tab=" + i);
		if (i < getTabCount()) {
			setSelectedIndex(i);
		}
	}
}
