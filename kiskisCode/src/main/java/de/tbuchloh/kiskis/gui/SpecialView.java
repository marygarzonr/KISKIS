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

import java.awt.Component;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

import de.tbuchloh.kiskis.gui.common.LnFHelper;
import de.tbuchloh.util.swing.widgets.SimpleInternalFrame;

/**
 * <b>SpecialView</b>:
 * 
 * @author gandalf
 * @version $Id: SpecialView.java,v 1.4 2006/04/08 17:46:21 tbuchloh Exp $
 */
public abstract class SpecialView extends JPanel implements IEditableView {

    private static final long serialVersionUID = 1L;

    /**
     * TODO this is no fine software engineering. Remove bidirectional association.
     */
    private IEditableView _rootView;

    private final Vector _contentListener;

    private boolean _isModified;

    protected SpecialView() {
        _contentListener = new Vector();
        _isModified = false;
    }

    /**
     * add a new listener which is notified whether the content of the view was
     * changed.
     * 
     * @param c
     *            the new listener
     */
    public final void addContentChangedListener(final ContentChangedListener c) {
        _contentListener.add(c);
    }

    /**
     * notifies the clients that the content was changed or not
     * 
     * @param changed
     *            true if content was changed.
     */
    public final void fireContentChangedEvent(final boolean changed) {
        _isModified = changed;
        final Iterator i = _contentListener.iterator();
        while (i.hasNext()) {
            final ContentChangedListener c = (ContentChangedListener) i.next();
            c.contentChanged(changed);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isModified() {
        return _isModified;
    }

    /**
     * stores the field values in the model structure.
     */
    protected abstract void store();

    /**
     * {@inheritDoc}
     */
    @Override
    public final void storeValues() {
        _isModified = false;
        store();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void revertChanges() {
        // does nothing
    }

    /**
     * @param title
     *            is the title to be displayed at the top of the panel.
     * @param content
     *            the content widgets.
     * @return the padded panel.
     */
    protected static Component createTitledPanel(final String title,
            final JComponent content) {
        final SimpleInternalFrame frame = new SimpleInternalFrame(title);
        frame.setContent(content);
        content.setBorder(LnFHelper.createDefaultBorder());
        return frame;
    }

    /**
     * @param _rootView
     *            {@link _rootView}
     */
    public void setRootView(IEditableView rootView) {
        this._rootView = rootView;
    }

    /**
     * @return {@link #_rootView}
     */
    protected IEditableView getRootView() {
        return _rootView;
    }
}
