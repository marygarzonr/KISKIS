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
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;

import javax.swing.JSplitPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>PersistentSplitPane</b>: remembers the last dividerposition.
 * 
 * @author gandalf
 * @version $Id: PersistentSplitPane.java,v 1.2 2006/04/16 09:22:32 tbuchloh Exp $
 */
public final class PersistentSplitPane extends JSplitPane {

    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLog(PersistentSplitPane.class);

    private final class Persister implements PropertyChangeListener, ContainerListener {
        private int _active;

        @Override
        public void componentAdded(final ContainerEvent e) {
            _active++;
            if (_active == 1) {
                resetDividerLocation();
            }
        }

        @Override
        public void componentRemoved(final ContainerEvent e) {
            _active = Math.max(0, --_active);
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if (_active == 1) {
                saveDividerLocation();
            }
        }
    }

    private static final String DIVIDER_LOCATION = "lastDividerLocation";

    private static final float DIVIDER_RESIZE_FACTOR = 0.5f;

    protected static final Preferences P = Preferences.userNodeForPackage(PersistentSplitPane.class);

    private static final long serialVersionUID = 416491469077285110L;

    /**
     * creates a new PersistentSplitPane
     * 
     */
    public PersistentSplitPane() {
        super();
        init();
    }

    /**
     * creates a new PersistentSplitPane
     * 
     * @param newOrientation
     */
    public PersistentSplitPane(final int newOrientation) {
        super(newOrientation);
        init();
    }

    /**
     * creates a new PersistentSplitPane
     * 
     * @param newOrientation
     * @param newContinuousLayout
     */
    public PersistentSplitPane(final int newOrientation, final boolean newContinuousLayout) {
        super(newOrientation, newContinuousLayout);
        init();
    }

    /**
     * creates a new PersistentSplitPane
     * 
     * @param newOrientation
     * @param newContinuousLayout
     * @param newLeftComponent
     * @param newRightComponent
     */
    public PersistentSplitPane(final int newOrientation, final boolean newContinuousLayout,
            final Component newLeftComponent, final Component newRightComponent) {
        super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
        init();
    }

    /**
     * creates a new PersistentSplitPane
     * 
     * @param newOrientation
     * @param newLeftComponent
     * @param newRightComponent
     */
    public PersistentSplitPane(final int newOrientation, final Component newLeftComponent,
            final Component newRightComponent) {
        super(newOrientation, newLeftComponent, newRightComponent);
        init();
    }

    private void init() {
        setOneTouchExpandable(true);
        this.setResizeWeight(DIVIDER_RESIZE_FACTOR);
        final Persister p = new Persister();
        this.addPropertyChangeListener(DIVIDER_LOCATION, p);
        this.addContainerListener(p);
    }

    protected void resetDividerLocation() {
        this.setDividerLocation(P.getInt(DIVIDER_LOCATION, 200));
    }

    protected void saveDividerLocation() {
        final int dividerLocation = getDividerLocation();

        final int minSize = 5;
        if (minSize < dividerLocation && dividerLocation < getMaximumDividerLocation() - minSize) {
            if (LOG.isDebugEnabled()) {
                final String msg = String.format("New divider location is %1$s", dividerLocation);
                LOG.debug(msg);
            }
            P.putInt(DIVIDER_LOCATION, dividerLocation);
        }
    }

}
