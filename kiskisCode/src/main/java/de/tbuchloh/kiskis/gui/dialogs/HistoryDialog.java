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
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import de.tbuchloh.kiskis.gui.common.UIConstants;
import de.tbuchloh.kiskis.model.ModelConstants;
import de.tbuchloh.kiskis.model.Password;
import de.tbuchloh.kiskis.model.PasswordHistory;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.Toolkit;
import de.tbuchloh.util.swing.actions.ActionItem;

/**
 * <b>HistoryDialog</b>:
 * 
 * @author gandalf
 * @version $Id: HistoryDialog.java,v 1.2 2006/09/08 14:44:19 tbuchloh Exp $
 */
public final class HistoryDialog extends JDialog implements ModelConstants,
		UIConstants {
	private static final long serialVersionUID = 1L;

	private static final class HistoryTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		private HistoryTableModel(final int rowCount) {
			super(rowCount, C_NAMES.length);
		}

		@Override
		public String getColumnName(final int column) {
			return C_NAMES[column];
		}
	}

	private static final Messages M = new Messages(HistoryDialog.class);

	protected static final int C_CREATED = 0, C_PWD = 1, C_EXPIRES = 2;

	protected static final String[] C_NAMES = { M.getString("c_created"), //$NON-NLS-1$
			M.getString("c_password"), //$NON-NLS-1$
			M.getString("c_expires") //$NON-NLS-1$
	};

	private final Action _closeAction = new ActionItem(
			M.getString("closeAction_title")) { //$NON-NLS-1$
		/**
				 * 
				 */
		private static final long serialVersionUID = 1L;

		@Override
        public void actionPerformed(final ActionEvent e) {
			Toolkit.saveWindowState(HistoryDialog.this);
			setVisible(false);
			dispose();
		}
	};

	private final PasswordHistory _pwdHistory;

	private final JTable _table;

	private final TableModel _tableModel;

	/**
	 * creates a new HistoryDialog
	 * 
	 * @throws java.awt.HeadlessException
	 */
	public HistoryDialog(final Frame parent, final PasswordHistory pwdHistory)
			throws HeadlessException {
		super(parent, M.getString("title")); //$NON-NLS-1$
		_pwdHistory = pwdHistory;
		_tableModel = new HistoryTableModel(_pwdHistory.getPasswords().size());
		_table = new JTable(_tableModel);
		init();
		initTableModel();
	}

	private void init() {
		this.getContentPane().setLayout(new BorderLayout());
		final JScrollPane tablePanel = new JScrollPane();
		tablePanel.getViewport().add(_table);
		this.getContentPane().add(tablePanel, BorderLayout.CENTER);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.add(new JButton(_closeAction));
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		Toolkit.restoreWindowState(this);
	}

	private void initTableModel() {
		int row = 0;

		for (final Object element : _pwdHistory.getPasswords()) {
			final Password p = (Password) element;
			_tableModel.setValueAt(new String(p.getPwd()), row, C_PWD);
			_tableModel.setValueAt(
					LONG_DATE.format(p.getCreationDate().getTime()), row,
					C_CREATED);
			_tableModel.setValueAt(SHORT.format(p.getExpires().getTime()), row,
					C_EXPIRES);
			++row;
		}
	}
}
