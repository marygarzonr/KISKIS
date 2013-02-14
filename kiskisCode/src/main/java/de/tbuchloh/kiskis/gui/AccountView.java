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

package de.tbuchloh.kiskis.gui;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.Action;

import de.tbuchloh.kiskis.model.SecuredElement;
import de.tbuchloh.kiskis.model.TPMDocument;

/**
 * <b>AccountView</b>:
 * 
 * @author gandalf
 * @version $Id: AccountView.java,v 1.6 2006/04/22 16:37:58 tbuchloh Exp $
 */
public final class AccountView extends DetailChildView implements
ContentChangedListener {
    private static final long serialVersionUID = 1L;

    private final SecuredElementView _pwdView;

    private final AbstractAccountDetailView _special;

    /**
     * creates a new AccountView
     */
    public AccountView(final TPMDocument doc, final SecuredElement el) {
        super();
        this.setLayout(new BorderLayout(5, 20));
        _pwdView = new SecuredElementView(doc, el);
        _pwdView.addContentChangedListener(this);

        _special = AbstractAccountDetailView.create(el);
        _special.addContentChangedListener(this);
        _pwdView.setSpecialView(_special);
        this.add(_pwdView, BorderLayout.CENTER);
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.SpecialView#storeValues()
     */
    @Override
    protected final void store() {
        _pwdView.storeValues();
        _special.storeValues();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.ContentChangedListener#contentChanged(boolean)
     */
    @Override
    public void contentChanged(final boolean changed) {
        fireContentChangedEvent(changed);
    }

    /**
     * 
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.SpecialActions#getSpecialActions()
     * 
     *      the first are the actiona from the password view, the latter are
     *      from the special view.
     */
    @Override
    public Collection<Action> getSpecialActions() {
        final Collection<Action> actions = _pwdView.getSpecialActions();
        actions.addAll(_special.getSpecialActions());
        return actions;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.SpecialView#revertChanges()
     */
    @Override
    public void revertChanges() {
        _pwdView.revertChanges();
        _special.revertChanges();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.DetailChildView#setNameField(java.lang.String)
     */
    @Override
    public void setNameField(final String name) {
        _pwdView.setNameField(name);
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.treeview.ItemRenamer#getNameField()
     */
    @Override
    public String getNameField() {
        return _pwdView.getNameField();
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.JComponent#grabFocus()
     */
    @Override
    public void grabFocus() {
        _pwdView.grabFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRootView(IEditableView rootView) {
        super.setRootView(rootView);
        _pwdView.setSpecialView(_special);
        _special.setRootView(rootView);
    }
}
