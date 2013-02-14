/*
 * Copyright (C) 2004 by Tobias Buchloh. This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Library General
 * Public License as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details. You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA If you didn't download
 * this code from the following link, you should check if you aren't using an
 * obsolete version: http://www.sourceforge.net/projects/kiskis
 */

package de.tbuchloh.kiskis.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import de.tbuchloh.kiskis.gui.dialogs.TANListDialog;
import de.tbuchloh.kiskis.gui.widgets.BasicTextField;
import de.tbuchloh.kiskis.gui.widgets.PasswordElement;
import de.tbuchloh.kiskis.model.BankAccount;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.TANList;
import de.tbuchloh.util.swing.actions.ActionCallback;
import de.tbuchloh.util.swing.widgets.ObservableTextField;
import de.tbuchloh.util.swing.widgets.OrderableListWidget;

/**
 * <b>BankAccountView</b>:
 * 
 * @author gandalf
 * @version $Id: BankAccountView.java,v 1.13 2007/03/11 22:23:44 tbuchloh Exp $
 */
public final class BankAccountView extends AbstractAccountDetailView implements
ContentChangedListener // TODO: refactor me
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected static final String REMOVE_LABEL = Messages
    .getString("BankAccountView.remove_label"); //$NON-NLS-1$

    protected static final String EDIT_LABEL = Messages
    .getString("BankAccountView.edit_label"); //$NON-NLS-1$

    protected static final String NEW_LABEL = Messages
    .getString("BankAccountView.new_label"); //$NON-NLS-1$

    private static final String TELE_PIN_TOOLTIP = Messages
    .getString("BankAccountView.TELE_PIN_TOOLTIP"); //$NON-NLS-1$

    private static final String BANK_ID_TOOLTIP = Messages
    .getString("BankAccountView.BANK_ID_TOOLTIP"); //$NON-NLS-1$

    private static final String TITLE = Messages
    .getString("BankAccountView.title"); //$NON-NLS-1$

    private static final String TAN_LISTS_TITLE = Messages
    .getString("BankAccountView.tan_lists_title"); //$NON-NLS-1$

    private static final String TELE_PIN_LABEL = Messages
    .getString("BankAccountView.TELE_PIN_LABEL"); //$NON-NLS-1$

    private static final String BANK_ID_LABEL = Messages
    .getString("BankAccountView.BANK_ID_LABEL"); //$NON-NLS-1$

    private static final String NUMBER_LABEL = Messages
    .getString("BankAccountView.number_label"); //$NON-NLS-1$

    private static final String BANK_LABEL = Messages
    .getString("BankAccountView.bank_label"); //$NON-NLS-1$

    protected BankAccount _ba;

    protected ObservableTextField _bank;

    protected ObservableTextField _bankIDField;

    protected PasswordElement _telePinField;

    protected ObservableTextField _accountNumberField;

    protected OrderableListWidget _tanLists;

    private Action _newListAction;

    private Action _editListAction;

    private Action _removeListAction;

    /**
     * creates a new BankAccountView view
     * 
     * @param na
     *            account to edit
     */
    public BankAccountView(final ModelNode na) {
        super();
        _ba = (BankAccount) na;
        _bank = new BasicTextField(_ba.getBankName());
        _bank.addContentListener(this);
        _accountNumberField = new BasicTextField(_ba.getNumber());
        _accountNumberField.setToolTipText(NUMBER_LABEL);
        _accountNumberField.addContentListener(this);
        _bankIDField = new BasicTextField(_ba.getBankID());
        _bankIDField.setToolTipText(BANK_ID_TOOLTIP);
        _bankIDField.addContentListener(this);
        _telePinField = new PasswordElement(_ba.getTelebankingPin());
        _telePinField.setToolTipText(TELE_PIN_TOOLTIP);
        _telePinField.addContentChangedListener(this);
        _telePinField.setShowQualityLabel(false);
        _telePinField.setShowTestButton(false);
        _tanLists = new OrderableListWidget();
        _tanLists.setData(_ba.getTanLists());
        _tanLists.addContentListener(this);
        init();
        initActions();
    }

    public void newList() {
        LOG.debug("new TAN-List ..."); //$NON-NLS-1$
        _tanLists.addObject(new TANList());
    }

    public void editList() {
        final TANList list = (TANList) _tanLists.getSelectedObject();
        if (list != null) {
            LOG.debug("editing " + list); //$NON-NLS-1$
            final TANListDialog dlg = new TANListDialog(MainFrame.getInstance(), list);
            dlg.setVisible(true);
        }
    }

    public void removeList() {
        final Object list = _tanLists.getSelectedObject();
        LOG.debug("remove List " + list); //$NON-NLS-1$
        _tanLists.removeObject(list);
    }

    private void initActions() {
        _newListAction = new ActionCallback(this, "newList", NEW_LABEL); //$NON-NLS-1$
        _tanLists.addSpecialAction(_newListAction);
        _editListAction = new ActionCallback(this, "editList", EDIT_LABEL); //$NON-NLS-1$
        _tanLists.addSpecialAction(_editListAction, true);
        _removeListAction = new ActionCallback(this, "removeList", REMOVE_LABEL); //$NON-NLS-1$
        _tanLists.addSpecialAction(_removeListAction);
    }

    private void init() {
        final BorderLayout layout = new BorderLayout(0, 5);
        this.setLayout(layout);

        final JPanel fields = new JPanel(new SpringLayout());

        addRow(fields, BANK_LABEL, _bank);
        addRow(fields, BANK_ID_LABEL, _bankIDField);
        addRow(fields, TELE_PIN_LABEL, _telePinField);
        addRow(fields, NUMBER_LABEL, _accountNumberField);

        makeGrid(fields, 4);

        this.add(fields, BorderLayout.NORTH);
        // fields.setPreferredSize(this.getPreferredSize());

        final JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(new TitledBorder(TAN_LISTS_TITLE));
        listPanel.add(_tanLists, BorderLayout.CENTER);
        this.add(listPanel, BorderLayout.CENTER);
    }

    /**
     * @see de.tbuchloh.kiskis.gui.SpecialView#storeValues()
     */
    @Override
    protected void store() {
        LOG.debug("storing"); //$NON-NLS-1$
        _ba.setBankName(_bank.getText());
        _ba.setNumber(_accountNumberField.getText());
        _ba.setBankID(_bankIDField.getText());
        _ba.setTelebankingPin(_telePinField.getPwd());
        _ba.clearTANLists();
        for (final Iterator i = _tanLists.getData().iterator(); i.hasNext();) {
            final TANList list = (TANList) i.next();
            _ba.addTANList(list);
        }
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.SpecialView#getSpecialActions()
     */
    @Override
    public Collection getSpecialActions() {
        return new ArrayList();
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
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.AbstractAccountDetailView#getTitle()
     */
    @Override
    public String getTitle() {
        return TITLE;
    }
}
