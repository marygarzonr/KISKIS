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

package de.tbuchloh.kiskis.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.common.LnFHelper;
import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.kiskis.gui.widgets.BasicTextField;
import de.tbuchloh.kiskis.model.GenericAccount;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.model.template.AccountProperty;
import de.tbuchloh.kiskis.model.template.AccountType;
import de.tbuchloh.kiskis.persistence.ICryptoContext;
import de.tbuchloh.kiskis.persistence.PasswordCryptoContext;
import de.tbuchloh.kiskis.persistence.PersistenceManager;
import de.tbuchloh.kiskis.persistence.SimplePasswordProxy;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.event.ContentListener;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.PasswordDialog;
import de.tbuchloh.util.swing.PropertyListCellRenderer;
import de.tbuchloh.util.swing.SpringUtilities;
import de.tbuchloh.util.swing.Toolkit;
import de.tbuchloh.util.swing.widgets.ObservableComboBox;
import de.tbuchloh.util.swing.widgets.ObservableTextField;
import de.tbuchloh.util.swing.widgets.OrderableListWidget;
import de.tbuchloh.util.swing.widgets.SimpleInternalFrame;

final class PropertyEditor extends KisKisDialog implements ContentListener {

    private static final long serialVersionUID = 1L;

    private static final Messages M = new Messages(PropertyEditor.class);

    private final Action _closeAction;

    private final ObservableTextField _nameBox;

    private AccountProperty _p;

    private final Action _saveAction;

    private final ObservableComboBox _typeBox;

    /**
     * creates a new PropertyEditor
     * 
     * @see PropertyEditor#PropertyEditor(Dialog, AccountProperty)
     */
    public PropertyEditor(final Dialog parent) {
        this(parent, null);
    }

    /**
     * creates a new PropertyEditor
     * 
     * @param parent
     *            is the parent window.
     * @param p
     *            is the property to edit.
     */
    public PropertyEditor(final Dialog parent, final AccountProperty p) {
        super(parent, M.getString("title"), true);
        _p = p;
        _saveAction = M.createAction(this, "onSave");
        _closeAction = M.createAction(this, "onCancel");
        _nameBox = new BasicTextField();
        _typeBox = new ObservableComboBox();
        for (final Object element : AccountProperty.getTypes()) {
            final String type = (String) element;
            _typeBox.addItem(type);
        }
        if (_p != null) {
            _nameBox.setText(_p.getName());
            _typeBox.setSelectedItem(_p.getType());
            _typeBox.setEnabled(false);
        }
        _nameBox.addContentListener(this);
        _typeBox.addContentListener(this);
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.dialogs.KisKisDialog#createMainPanel()
     */
    @Override
    protected Component createMainPanel() {
        final JPanel p = new JPanel(new SpringLayout());
        p.setBorder(LnFHelper.createDefaultBorder());

        p.add(new JLabel(M.getString("nameBox.label")));
        p.add(_nameBox);
        p.add(new JLabel(M.getString("typeBox.label")));
        p.add(_typeBox);

        SpringUtilities.makeCompactGrid(p, 2, 2, 0, 0, 5, 5);

        return p;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.dialogs.KisKisDialog#getActions()
     */
    @Override
    protected List<Action> getActions() {
        return Arrays.asList(new Action[] {
                _saveAction, _closeAction
        });
    }

    /**
     * @return the property.
     */
    public AccountProperty getProperty() {
        if (_p == null) {
            try {
                _p = AccountProperty.create(_nameBox.getText(), (String) _typeBox.getSelectedItem());
            } catch (final Exception e) {
                assert false : e.getMessage();
            }
        }
        return _p;
    }

    protected void onCancel() {
        if (!isChanged() || confirmClose()) {
            close();
        }
    }

    protected void onSave() {
        if (_nameBox.getText().length() == 0) {
            MessageBox.showMessageDialog(this, M.getString("msgEmptyName"));
            return;
        }
        storeValues();
        close();
    }

    private void storeValues() {
        if (_p != null) {
            _p.setName(_nameBox.getText());
            _p.setType((String) _typeBox.getSelectedItem());
        }
    }

}

/**
 * <b>TemplateEditorDialog</b>: an editor for an AccountType.
 * 
 * @author gandalf
 * @version $Id: TemplateOverviewDialog.java,v 1.12 2007/02/18 14:37:35 tbuchloh Exp $
 */
final class TemplateEditorDialog extends KisKisDialog implements ContentListener {

    private static final long serialVersionUID = 1L;

    private static final class PropertyListRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;

        /**
         * Overridden!
         * 
         * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object,
         *      int, boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index,
                final boolean isSelected, final boolean cellHasFocus) {
            final Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            final AccountProperty p = (AccountProperty) value;
            setText(p.getName() + " (" + p.getType() + ")");
            return comp;
        }
    }

    private static final Messages M = new Messages(TemplateEditorDialog.class);

    private final TPMDocument _doc;

    private ObservableTextField _nameBox;

    private OrderableListWidget _properties;

    private final Action _saveAction, _closeAction, _newAction, _editAction, _deleteAction;

    private final AccountType _type;

    /**
     * creates a new TemplateOverviewDialog.TemplateEditorDialog
     * 
     * @param parent
     *            is the parent window.
     * @param doc
     *            is used for integrity checks.
     * @param type
     *            is the type to edit.
     */
    public TemplateEditorDialog(final Dialog parent, final TPMDocument doc, final AccountType type) {
        super(parent, M.getString("title"), true);
        _doc = doc;
        _type = type;
        _saveAction = M.createAction(this, "onSave");
        _closeAction = M.createAction(this, "onClose");
        _newAction = M.createAction(this, "onNew");
        _editAction = M.createAction(this, "onEdit");
        _deleteAction = M.createAction(this, "onDelete");
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.dialogs.KisKisDialog#createMainPanel()
     */
    @Override
    protected Component createMainPanel() {
        final JPanel panel = new JPanel(new BorderLayout(5, 15));
        panel.setBorder(LnFHelper.createDefaultBorder());
        panel.add(createNameBox(), BorderLayout.NORTH);
        panel.add(createTable());
        return panel;
    }

    private Component createNameBox() {
        _nameBox = new BasicTextField();
        _nameBox.addContentListener(this);
        _nameBox.setText(_type.getName());

        final JPanel p = new JPanel(new SpringLayout()); // new SimpleInternalFrame(M.getString("nameBox.label"));
        p.add(LnFHelper.createLabel(M.getString("nameBox.label")));
        p.add(_nameBox);

        SpringUtilities.makeCompactGrid(p, 1, 2, 0, 0, 5, 5);

        return p;
    }

    private Component createTable() {
        final JPanel p = new SimpleInternalFrame(M.getString("table.label"));

        final JPanel inset = new JPanel(new BorderLayout());
        inset.setBorder(LnFHelper.createDefaultBorder());

        _properties = new OrderableListWidget();
        _properties.addContentListener(this);
        _properties.addSpecialAction(_newAction);
        _properties.addSpecialAction(_editAction, true);
        _properties.addSpecialAction(_deleteAction);
        _properties.setCellRenderer(new PropertyListRenderer());
        _properties.setData(_type.getProperties());
        inset.add(_properties);

        p.add(inset);
        return p;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.dialogs.KisKisDialog#getActions()
     */
    @Override
    protected List<Action> getActions() {
        return Arrays.asList(new Action[] {
                _saveAction, _closeAction
        });
    }

    private AccountProperty getSelected() {
        final AccountProperty p = (AccountProperty) _properties.getSelectedObject();
        return p;
    }

    protected void onClose() {
        if (!isChanged() || confirmClose()) {
            close();
        }
    }

    protected void onDelete() {
        final AccountProperty p = getSelected();
        if (p != null) {
            boolean delete = true;
            final List<GenericAccount> acc = _doc.getGenericAccounts(_type);
            for (final GenericAccount ga : acc) {
                if (ga.hasProperty(p)) {
                    delete = false;
                    break;
                }
            }
            if (delete || MessageBox.showConfirmDialog(M.format("msgConfirmDelete", p.getName()))) {
                _properties.removeObject(p);
            }
        }
    }

    protected void onEdit() {
        final AccountProperty p = getSelected();
        if (p != null) {
            final PropertyEditor dlg = new PropertyEditor(this, p);
            dlg.setVisible(true);
        }
    }

    protected void onNew() {
        final PropertyEditor dlg = new PropertyEditor(this);
        dlg.setVisible(true);
        final AccountProperty prop = dlg.getProperty();
        if (_properties.getData().contains(prop)) {
            MessageBox.showErrorMessage(M.getString("errPropertyExists"));
            return;
        }
        _properties.addObject(prop);
    }

    @SuppressWarnings("unchecked")
    protected void onSave() {
        final Set<AccountProperty> check = new HashSet<AccountProperty>();
        for (final Iterator<AccountProperty> i = _properties.getData().iterator(); i.hasNext();) {
            final AccountProperty p = i.next();
            if (check.contains(p)) {
                MessageBox.showErrorMessage(M.getString("msgDuplicateName"));
                return;
            }
            check.add(p);
        }
        _type.setName(_nameBox.getText());
        _type.setProperties(_properties.getData());
        close();
    }
}

/**
 * <b>TemplateOverviewDialog</b>:
 * 
 * @author gandalf
 * @version $Id: TemplateOverviewDialog.java,v 1.12 2007/02/18 14:37:35 tbuchloh Exp $
 */
public final class TemplateOverviewDialog extends KisKisDialog implements ContentListener {

    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLog(TemplateOverviewDialog.class);

    protected static final Messages M = new Messages(TemplateOverviewDialog.class);

    private static final long serialVersionUID = 1L;

    private final Action _closeAction;

    private final TPMDocument _doc;

    private final Action _saveAction;

    private OrderableListWidget _templates;

    /**
     * creates a new TemplateOverviewDialog
     * 
     * @param parent
     *            TODO
     */
    public TemplateOverviewDialog(final Frame parent, final TPMDocument doc) {
        super(parent, M.getString("title"), false);
        _doc = doc;
        _saveAction = M.createAction(this, "onSave");
        _closeAction = M.createAction(this, "onClose");
        initList();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.dialogs.KisKisDialog#createMainPanel()
     */
    @Override
    protected Component createMainPanel() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(LnFHelper.createDefaultBorder());
        panel.add(_templates);
        return panel;
    }

    @SuppressWarnings("unchecked")
    private void doImportAccountTypes(final ICryptoContext ctx) {
        try {
            final TPMDocument doc = PersistenceManager.load(ctx, new MessageBoxErrorHandler());
            final Set<AccountType> types = doc.getAccountTypes();
            final List existing = new ArrayList(_templates.getData());
            existing.addAll(types);
            _templates.setData(existing);

        } catch (final Exception e) {
            LOG.debug(e, e);
            MessageBox.showErrorMessage(e.getMessage());
        }
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.dialogs.KisKisDialog#getActions()
     */
    @Override
    protected List<Action> getActions() {
        return Arrays.asList(new Action[] {
                _saveAction, _closeAction
        });
    }

    @SuppressWarnings("unchecked")
    private void initList() {
        _templates = new OrderableListWidget();
        _templates.setOrderable(false);
        _templates.addSpecialAction(M.createAction(this, "onNew"));
        _templates.addSpecialAction(M.createAction(this, "onEdit"), true);
        _templates.addSpecialAction(M.createAction(this, "onDelete"));
        _templates.addSpecialAction(M.createAction(this, "onImport"));
        _templates.addContentListener(this);
        _templates.setData(new ArrayList(_doc.getAccountTypes()));
        _templates.setCellRenderer(new PropertyListCellRenderer("name"));
        // _templates.addSpecialAction(M.createAction(this, "onDelete"));
    }

    protected void onClose() {
        if (!isChanged() || confirmClose()) {
            close();
        }
    }

    protected void onDelete() {
        final AccountType type = (AccountType) _templates.getSelectedObject();
        if (type != null) {
            final List<GenericAccount> acc = _doc.getGenericAccounts(type);
            if (acc.isEmpty() || MessageBox.showConfirmDialog(M.format("msgConfirmDelete", type.getName()))) {
                _templates.removeObject(type);
            }
        }
    }

    protected void onEdit() {
        final AccountType type = (AccountType) _templates.getSelectedObject();
        if (type != null) {
            final TemplateEditorDialog dlg = new TemplateEditorDialog(this, _doc, type);
            dlg.setVisible(true);
        }
    }

    protected void onImport() {
        final FileDialog dlg = new FileDialog(this);
        dlg.setSelectedFile(new File(Settings.getLastFile()));
        dlg.setVisible(true);
        final File file = dlg.getSelectedFile();
        if (file != null) {
            final PasswordDialog pdlg = new PasswordDialog(this, false);
            Toolkit.centerWindow(this, pdlg);
            pdlg.setVisible(true);
            if (pdlg.getButton() != PasswordDialog.CANCEL_BUTTON) {
                final char[] pwd = pdlg.getPassword();
                final ICryptoContext ctx = new PasswordCryptoContext(dlg.getSelectedAlgo(),
                        new SimplePasswordProxy(pwd), file);
                doImportAccountTypes(ctx);
            }

        }
    }

    protected void onNew() {
        final AccountType type = new AccountType();
        type.setName("new template");
        _templates.addObject(type);
    }

    @SuppressWarnings("unchecked")
    protected void onSave() {
        final Set<AccountType> check = new HashSet<AccountType>();
        for (final Iterator<AccountType> i = _templates.getData().iterator(); i.hasNext();) {
            final AccountType type = i.next();
            if (check.contains(type)) {
                showDuplicateMsg(type);
                return;
            }
            check.add(type);
        }
        _doc.setAccountTypes(new HashSet<AccountType>(_templates.getData()));
        close();
    }

    private void showDuplicateMsg(final AccountType type) {
        MessageBox.showErrorMessage(M.format("msgDuplicateName", type.getName()));
    }

}
