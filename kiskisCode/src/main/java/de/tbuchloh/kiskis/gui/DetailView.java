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
import java.awt.FlowLayout;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.tbuchloh.kiskis.gui.common.LnFHelper;
import de.tbuchloh.kiskis.gui.treeview.ItemRenamer;
import de.tbuchloh.kiskis.gui.widgets.ArchivableElement;
import de.tbuchloh.kiskis.model.Archivable;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.util.event.ContentChangedEvent;
import de.tbuchloh.util.event.ContentListener;
import de.tbuchloh.util.swing.actions.ActionCallback;

/**
 * <b>DetailView</b>: a detailed view consists of an SecuredElementView.<br>
 * According the type of SecuredElement the detailed view generates a view for the concrete type and adds it to the
 * SecuredElementView.
 * 
 * @author gandalf
 * @version $Id: DetailView.java,v 1.7 2007/02/18 14:37:49 tbuchloh Exp $
 */
public final class DetailView extends JPanel implements ItemRenamer, IEditableView {

    private static final long serialVersionUID = 1L;

    private Archivable _archivable;

    protected ArchivableElement _archivableElement;

    private final ContentChangedListener _contentChanged = new ContentChangedListener() {
        @Override
        public void contentChanged(final boolean changed) {
            _saveAction.setEnabled(changed);
        }
    };

    protected Action _saveAction;

    private DetailChildView _special;

    /**
     * @param doc
     *            is the current document.
     * @param el
     *            element to display.
     */
    public DetailView(final TPMDocument doc, final ModelNode node) {
        init(DetailChildView.create(this, doc, node), node);
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.treeview.ItemRenamer#getNameField()
     */
    @Override
    public String getNameField() {
        return _special.getNameField();
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.JComponent#grabFocus()
     */
    @Override
    public void grabFocus() {
        _special.grabFocus();
    }

    private void init(final DetailChildView view, ModelNode node) {
        this.setLayout(new BorderLayout());
        _saveAction = new ActionCallback(this, "storeValues", //$NON-NLS-1$
                Messages.getString("DetailView.save_button_label")); //$NON-NLS-1$)
        _saveAction.setEnabled(false);

        _special = view;
        _special.addContentChangedListener(_contentChanged);
        _special.setBorder(LnFHelper.createDefaultBorder());
        this.add(_special, BorderLayout.CENTER);

        final JPanel bottomPanel = new JPanel(new BorderLayout());
        final JPanel archivablePanel = new JPanel(new FlowLayout());
        archivablePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        if (node instanceof Archivable) {
            final Archivable archivable = (Archivable) node;
            if (archivable.isArchivable()) {
                _archivableElement = new ArchivableElement(archivable.getArchivedOnDate());
                _archivableElement.addContentListener(new ContentListener() {

                    @Override
                    public void contentChanged(ContentChangedEvent e) {
                        _special.fireContentChangedEvent(true);
                        _contentChanged.contentChanged(true);
                    }
                });
                archivablePanel.add(_archivableElement);
                _archivable = archivable;
            }
        }
        bottomPanel.add(archivablePanel, BorderLayout.WEST);

        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        for (final Action a : _special.getSpecialActions()) {
            buttons.add(new JButton(a));
        }
        buttons.add(new JButton(_saveAction));
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        bottomPanel.add(buttons, BorderLayout.CENTER);

        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * @see SpecialView#isModified()
     */
    @Override
    public boolean isModified() {
        return _special.isModified();
    }

    /**
     * reset the data structure to the value before.
     */
    @Override
    public void revertChanges() {
        _special.revertChanges();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.treeview.ItemRenamer#setNameField(java.lang.String)
     */
    @Override
    public void setNameField(final String name) {
        final boolean isModified = isModified();
        _special.setNameField(name);
        if (!isModified) {
            storeValues();
        }
    }

    /**
     * @see de.tbuchloh.kiskis.gui.SpecialView#storeValues()
     */
    @Override
    public void storeValues() {
        if (_archivable != null) {
            _archivable.setArchivedOnDate(_archivableElement.getArchivedOn());
        }
        _special.storeValues();
        _saveAction.setEnabled(false);
    }
}
