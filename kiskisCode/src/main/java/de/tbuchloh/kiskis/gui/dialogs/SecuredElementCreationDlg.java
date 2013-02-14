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
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.prefs.Preferences;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.model.BankAccount;
import de.tbuchloh.kiskis.model.CreditCard;
import de.tbuchloh.kiskis.model.GenericAccount;
import de.tbuchloh.kiskis.model.NetAccount;
import de.tbuchloh.kiskis.model.Password;
import de.tbuchloh.kiskis.model.SecuredElement;
import de.tbuchloh.kiskis.model.SecuredFile;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.model.template.AccountType;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.DescriptiveItem;
import de.tbuchloh.util.swing.Toolkit;

/**
 * <b>SecuredElementCreationDlg</b>:
 * 
 * @author gandalf
 * @version $Id: SecuredElementCreationDlg.java,v 1.7 2006/09/08 14:23:47
 *          tbuchloh Exp $
 */
public final class SecuredElementCreationDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <b>RadioItemListener</b>:
	 * 
	 * @author gandalf
	 * @version $Id: SecuredElementCreationDlg.java,v 1.7 2006/09/08 14:23:47
	 *          tbuchloh Exp $
	 */
	private static final class RadioItemListener implements ItemListener {

		private final AbstractButton _comp;

		/**
		 * creates a new RadioItemListener
		 * 
		 */
		public RadioItemListener(final AbstractButton comp) {
			super();
			_comp = comp;
		}

		/**
		 * Overridden!
		 * 
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		public void itemStateChanged(final ItemEvent e) {
			_comp.setSelected(true);
		}

	}

	private static final class Type {

		protected Class _clazz;

		protected String _label;

		/**
		 * creates a new Item
		 * 
		 * @param label
		 *            is the label of this class
		 * @param clazz
		 *            is the class to instanciate.
		 */
		public Type(final String label, final Class clazz) {
			_label = label;
			_clazz = clazz;
		}
	}

	private static final int APPROVED_BUTTON = 1;

	private static final int CANCEL_BUTTON = 2;

	/**
	 * stores the last selected value for the SecuredElementCreationDialog
	 */
	private static final String K_LAST_CREATED_SECURED_ELEMENT = "k_last_created_secured_element";

	private static final Log LOG = LogFactory
			.getLog(SecuredElementCreationDlg.class);

	private static final Messages M = new Messages(
			SecuredElementCreationDlg.class);

	private static final Preferences P = Preferences
			.userNodeForPackage(SecuredElementCreationDlg.class);

	private static final Type[] TYPES = {
			new Type(M.getString("Bank_Account_Label"), //$NON-NLS-1$ 
					BankAccount.class),
			new Type(M.getString("Credit_Card_Label"), //$NON-NLS-1$
					CreditCard.class),
			new Type(M.getString("Network_Account_Label"), //$NON-NLS-1$
					NetAccount.class), new Type(M.getString("File_Label"), //$NON-NLS-1$
					SecuredFile.class),
			new Type(M.getString("Generic_Account_Label"), //$NON-NLS-1$
					GenericAccount.class) };

	/**
	 * Factory ...
	 * 
	 * @param owner
	 *            dialog owner.
	 * @param defaultExpiryTime
	 *            time after the password epires in days
	 * @return the generated element or null if nothing was selected
	 */
	public static SecuredElement newInstance(final JFrame owner,
			final TPMDocument doc, final int defaultExpiryTime) {
		final SecuredElementCreationDlg dlg = new SecuredElementCreationDlg(
				owner, doc);
		dlg.setVisible(true);
		if (dlg._pressedButton == APPROVED_BUTTON) {

			final Class clazz = dlg.getSelectedClass();

			LOG.info("Creating new element - class=" + clazz + ", expiryTime="
					+ defaultExpiryTime);

			if (clazz != null) {
				try {
					final SecuredElement el = (SecuredElement) clazz
							.newInstance();
					el.setPwd(new Password(new char[0], defaultExpiryTime));
					if (el instanceof GenericAccount) {
						final GenericAccount a = (GenericAccount) el;
						a.setType(dlg.getSelectedTemplate());
					}
					return el;
				} catch (final InstantiationException e) {
					throw new Error("Should not happen"); //$NON-NLS-1$
				} catch (final IllegalAccessException e) {
					throw new Error("Should not happen"); //$NON-NLS-1$
				}
			}
		}
		return null;
	}

	private ButtonGroup _bg;

	private JRadioButton[] _buttons;

	private final Action _cancelAction;

	private final TPMDocument _doc;

	private int _pressedButton;

	private final Action _selectAction;

	private JComboBox _templates;

	private SecuredElementCreationDlg(final JFrame owner, final TPMDocument doc) {
		super(owner, M.getString("title"), true); //$NON-NLS-1$
		_selectAction = M.createAction(this, "handleSelect"); //$NON-NLS-1$
		_cancelAction = M.createAction(this, "cancel"); //$NON-NLS-1$
		_pressedButton = 2;
		_doc = doc;
		initTemplatesBox();

		init();
		this.pack();
		Toolkit.centerWindow(owner, this);
	}

	protected void cancel() {
		_pressedButton = CANCEL_BUTTON;
		setVisible(false);
		dispose();
	}

	private JPanel createButtonGroup() {
		final JPanel main = new JPanel(new GridLayout(TYPES.length, 1, 5, 5));
		main.setBorder(BorderFactory.createEmptyBorder(15, 50, 5, 150));
		_bg = new ButtonGroup();
		_buttons = new JRadioButton[TYPES.length];
		final String lastSelected = P.get(K_LAST_CREATED_SECURED_ELEMENT,
				NetAccount.class.getName());
		LOG.debug("Last created class: " + lastSelected);
		for (int i = 0; i < TYPES.length; ++i) {
			_buttons[i] = new JRadioButton(TYPES[i]._label);
			final Class c = TYPES[i]._clazz;
			if (c.getName().equals(lastSelected)) {
				_buttons[i].setSelected(true);
			}
			_bg.add(_buttons[i]);
			Component comp = _buttons[i];
			if (c == GenericAccount.class) {
				final JPanel p = new JPanel(new BorderLayout(10, 0));
				p.add(comp);
				p.add(_templates, BorderLayout.EAST);
				_templates.addItemListener(new RadioItemListener(_buttons[i]));
				comp.setEnabled(_templates.isEnabled());
				comp = p;
			}
			main.add(comp);
		}
		return main;
	}

	private JPanel createButtonPanel() {
		final JPanel buttons = new JPanel(new GridLayout(1, 2, 5, 5));
		buttons.add(new JButton(_selectAction));
		buttons.add(new JButton(_cancelAction));

		final JPanel pad = new JPanel();
		pad.add(buttons);
		return pad;
	}

	/**
	 * @return the selected class object or null
	 */
	private Class getSelectedClass() {
		for (int i = 0; i < _buttons.length; ++i) {
			if (_buttons[i].isSelected()) {
				final Class c = TYPES[i]._clazz;
				P.put(K_LAST_CREATED_SECURED_ELEMENT, c.getName());
				return c;
			}
		}
		return null;
	}

	private AccountType getSelectedTemplate() {
		final DescriptiveItem item = (DescriptiveItem) _templates
				.getSelectedItem();
		return (AccountType) (item).getValue();
	}

	protected void handleSelect() {
		_pressedButton = APPROVED_BUTTON;
		setVisible(false);
		dispose();
	}

	private void init() {
		final JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(createButtonGroup(), BorderLayout.CENTER);
		contentPane.add(createButtonPanel(), BorderLayout.SOUTH);
		setContentPane(contentPane);
	}

	private void initTemplatesBox() {
		_templates = new JComboBox();
		_templates.setEditable(false);
		for (final Object element : _doc.getAccountTypes()) {
			final AccountType t = (AccountType) element;
			_templates.addItem(new DescriptiveItem(t.getName(), t));
		}
		if (_doc.getAccountTypes().isEmpty()) {
			_templates.setEnabled(false);
		}
	}
}
