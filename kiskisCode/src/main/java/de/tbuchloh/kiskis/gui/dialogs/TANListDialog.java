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
package de.tbuchloh.kiskis.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import de.tbuchloh.kiskis.gui.common.LnFHelper;
import de.tbuchloh.kiskis.gui.widgets.BasicTextField;
import de.tbuchloh.kiskis.model.TAN;
import de.tbuchloh.kiskis.model.TANList;
import de.tbuchloh.kiskis.model.annotations.IgnoresObservable;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.dialogs.MessagePane;
import de.tbuchloh.util.swing.table.celleditors.BooleanCellEditor;
import de.tbuchloh.util.swing.table.celleditors.DateCellEditor;
import de.wannawork.jcalendar.JCalendarComboBox;

/**
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 08.11.2010
 */
public final class TANListDialog extends KisKisDialog {

    private static class Column {
        final static int ID_COL = 0, NUMBER_COL = 1, USED_CHECK_COL = 2, USED_DATE_COL = 3;
    }

    private static final class TANListTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        protected String[] _cols = {
                TANListDialog.M.getString("tan_id_label"), TANListDialog.M.getString("NUMBER_TITLE"),
                TANListDialog.M.getString("USED_CHECK_TITLE"), TANListDialog.M.getString("USED_DATE_TITLE")
        };

        private final TANList _copiedList, _original;

        public TANListTableModel(final TANList list) {
            _original = list;
            _copiedList = list.clone();
        }

        /**
         * @param tan
         *            is the new TAN which is appended to the end.
         */
        @IgnoresObservable
        public void addTAN(final TAN tan) {
            tan.setId(_copiedList.getNextId());
            _copiedList.addTAN(tan);
            fireTableDataChanged();
        }

        /**
         * Overridden!
         * 
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            Class<?> ret = super.getColumnClass(columnIndex);
            switch (columnIndex) {
            case Column.ID_COL:
                ret = Integer.class;
                break;
            case Column.USED_CHECK_COL:
                ret = Boolean.class;
                break;
            case Column.USED_DATE_COL:
                ret = Date.class;
                break;
            }
            return ret;
        }

        /**
         * Overridden!
         * 
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return _cols.length;
        }

        /**
         * Overridden!
         * 
         * @see javax.swing.table.AbstractTableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(final int column) {
            return _cols[column];
        }

        /**
         * Overridden!
         * 
         * @see javax.swing.table.TableModel#getRowCount()
         */
        @Override
        public int getRowCount() {
            return _copiedList.getTans().size();
        }

        /**
         * Overridden!
         * 
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            Object value = null;
            final TAN tan = _copiedList.getTans().get(rowIndex);
            switch (columnIndex) {
            case Column.ID_COL:
                value = tan.getId();
                break;
            case Column.NUMBER_COL:
                value = tan.getNumber();
                break;
            case Column.USED_CHECK_COL:
                value = Boolean.valueOf(tan.wasUsed());
                break;
            case Column.USED_DATE_COL:
                if (tan.wasUsed()) {
                    value = tan.getUsed().getTime();
                }
                break;
            default:
                throw new Error("to be implemented! " + columnIndex); //$NON-NLS-1$
            }
            return value;
        }

        /**
         * Overridden!
         * 
         * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(final int rowIndex, final int columnIndex) {
            return columnIndex < NON_EDITABLE_COL;
        }

        /**
         * @param row
         *            the position of the TAN.
         */
        public void removeRow(final int row) {
            if (_copiedList.getTans().size() > row && row >= 0) {
                _copiedList.remove(_copiedList.getTans().get(row));
                fireTableRowsDeleted(row, row);
            }
        }

        /**
         * Overridden!
         * 
         * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
         */
        @Override
        @IgnoresObservable
        public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
            final TAN tan = _copiedList.getTans().get(rowIndex);
            switch (columnIndex) {
            case Column.ID_COL:
                tan.setId((Integer) aValue);
                break;
            case Column.NUMBER_COL:
                tan.setNumber((String) aValue);
                break;
            case Column.USED_CHECK_COL:
                final boolean isUsed = ((Boolean) aValue).booleanValue();
                tan.setUsed(isUsed);
                break;
            default:
                throw new Error("to be implemented! " + columnIndex); //$NON-NLS-1$
            }
        }

        /**
         * stores the modified data in the original object.
         */
        public void store() {
            _original.clearTANs();
            for (final TAN element : _copiedList.getTans()) {
                _original.addTAN(element);
            }
        }
    }

    private static final Messages M = new Messages(TANListDialog.class);

    private static final int NON_EDITABLE_COL = Column.USED_DATE_COL;

    private static final long serialVersionUID = 1L;

    private static final DateFormat USED_DATE_FORMAT = new SimpleDateFormat(M.getString("USED_DATE_FORMAT")); //$NON-NLS-1$

    @IgnoresObservable
    public static void main(final String[] args) {
        final TANList list = new TANList();
        final TAN tan1 = new TAN();
        tan1.setId(1);
        tan1.setNumber("12345");
        list.addTAN(tan1);
        final TANListDialog dlg = new TANListDialog(new JFrame(), list);
        dlg.setVisible(true);
    }

    private Action _cancelAction;

    private JCalendarComboBox _createdBox;

    private final TANList _list;

    protected TANListTableModel _model;

    private Action _newAction;

    private JTextField _listIdBox;

    private Action _removeAction;

    private Action _storeAction;

    protected JTable _table;

    public TANListDialog(JFrame parent, final TANList list) {
        super(parent, MessageFormat.format(M.getString("TITLE"), new Object[] {
            list
        }), true);
        _list = list;
        initFields();
        initActions();
        initList(list);
    }

    private Component createCloseButtons() {
        final JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JButton(_storeAction));
        panel.add(new JButton(_cancelAction));
        return panel;
    }

    private Component createEditButtons() {
        final JPanel buttons = new JPanel(new GridLayout(2, 1));
        buttons.add(new JButton(_newAction));
        buttons.add(new JButton(_removeAction));

        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttons, BorderLayout.NORTH);
        return panel;
    }

    private Component createFieldPane() {
        final JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.add(new JLabel(M.getString("list_id_label")));
        panel.add(_listIdBox);
        panel.add(new JLabel(M.getString("created_label")));
        panel.add(_createdBox);
        return panel;
    }

    private Component createScrollpane(final JTable table) {
        final JScrollPane panel = new JScrollPane();
        panel.getViewport().add(table);
        return panel;
    }

    private void initActions() {
        _newAction = M.createAction(this, "onNew");

        _removeAction = M.createAction(this, "onDelete");

        _storeAction = M.createAction(this, "onOK");

        _cancelAction = M.createAction(this, "onCancel");
    }

    protected void onNew() {
        final String question = M.getString("MSG_NEW_ASK_HOW_MANY");
        final String regex = "[0-9]+";
        final String numberAsString = MessagePane.showInputDialog(this, question, regex);
        if (numberAsString != null) {
            for (int i = 0; i < Integer.parseInt(numberAsString); ++i) {
                _model.addTAN(new TAN());
            }
        }
    }

    protected void onOK() {
        if (store()) {
            close();
        }
    }

    protected void onCancel() {
        close();
    }

    protected void onDelete() {
        final int row = _table.getSelectedRow();
        _model.removeRow(row);
    }

    private void initFields() {
        _listIdBox = new BasicTextField(_list.getId());
        _createdBox = new JCalendarComboBox(_list.getCreated());
    }

    private void initList(final TANList list) {
        _model = new TANListTableModel(list);
        _table = new JTable(_model);
        _table.setDefaultEditor(Boolean.class, new BooleanCellEditor());
        _table.setDefaultEditor(Date.class, new DateCellEditor(USED_DATE_FORMAT));
    }

    protected boolean store() {
        final boolean ret = true;
        _model.store();
        _list.setId(_listIdBox.getText());
        _list.setCreated(_createdBox.getCalendar());
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Component createMainPanel() {
        final JPanel p = new JPanel();
        p.setBorder(LnFHelper.createDefaultBorder());

        final BorderLayout layout = new BorderLayout();
        layout.setHgap(15);
        layout.setVgap(15);
        p.setLayout(layout);
        p.add(createFieldPane(), BorderLayout.NORTH);
        p.add(createScrollpane(_table), BorderLayout.CENTER);
        p.add(createEditButtons(), BorderLayout.EAST);

        return p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Action> getActions() {
        return Arrays.asList(_storeAction, _cancelAction);
    }

}
