/*
 * Copyright (C) 2004 by Tobias Buchloh.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package de.tbuchloh.kiskis.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import de.tbuchloh.kiskis.gui.common.LnFHelper;
import de.tbuchloh.kiskis.gui.widgets.PasswordQualityBar;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.SpringUtilities;
import de.tbuchloh.util.swing.actions.ActionCallback;

/**
 * <b>PasswordDialog</b>: shows a modal password dialog which allows confirmation of passwords.
 * 
 * @author gandalf
 * @version $Id: PasswordDialog.java 2660 2010-10-22 12:06:33Z gandalf $
 */
public final class PasswordDialog extends KisKisDialog {

    public static enum Buttons {
        APPROVED_BUTTON, CANCEL_BUTTON
    }

    private static final Messages M = new Messages(PasswordDialog.class);

    /**
     * Die serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public static void main(final String[] args) {
        final PasswordDialog dlg = new PasswordDialog(new JFrame(), true);
        // dlg.setMessage("Datei: 'datei.txt'");
        //
        // dlg.setMessage("Bitte geben Sie das Passwort für die Datei 'datei.txt' an:");
        dlg.setMessage("<html>Bitte geben Sie das Passwort für die Datei<br/><br/><center><b><font color='blue'>datei.txt</font></b></center><br/></br> an:</html>");
        dlg.setVisible(true);
        System.out.println(dlg.getButton());
    }

    private Buttons _button;

    private Action _cancelAction;

    private final boolean _confirm;

    private JPasswordField _first;

    private JLabel _messageLabel;

    private ActionCallback _okAction;

    private JPasswordField _second;

    private JCheckBox _showPasswordsBox;

    private PasswordQualityBar _progressBar;

    /**
     * @param owner
     *            is the parent window
     * @param confirm
     *            show a second password field for confirmation.
     * @throws java.awt.HeadlessException
     */
    public PasswordDialog(final Dialog owner, final boolean confirm) throws HeadlessException {
        super(owner, M.getString("TITLE"), true);
        _confirm = confirm;
        init();
    }

    /**
     * @param owner
     *            is the parent window.
     * @param confirm
     *            show a second password field for confirmation
     * @throws java.awt.HeadlessException
     */
    public PasswordDialog(final Frame owner, final boolean confirm) throws HeadlessException {
        super(owner, M.getString("TITLE"), true);
        _confirm = confirm;
        init();
    }

    private void checkPwdEqual() {
        boolean enabled = false;
        if (Arrays.equals(_first.getPassword(), _second.getPassword())) {
            enabled = true;
            _okAction.setTooltip("");
        } else {
            _okAction.setTooltip(M.getString("NOT_EQUAL"));
        }
        _okAction.setEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Component createMainPanel() {
        onShowPasswords();

        final JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 15));

        final JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        messagePanel.add(_messageLabel);
        contentPane.add(messagePanel, BorderLayout.NORTH);

        final JPanel main = new JPanel(new SpringLayout());

        int rows = 2;
        main.setBorder(LnFHelper.createDefaultBorder());
        main.add(new JLabel(M.getString("PASSWORD"), SwingConstants.TRAILING));
        main.add(_first);
        if (_confirm) {
            main.add(new JLabel());
            main.add(_progressBar);

            _first.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    _progressBar.update(_first.getPassword());
                }

            });

            final KeyListener pwdMatcher = new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    checkPwdEqual();
                }
            };
            _first.addKeyListener(pwdMatcher);
            _second.addKeyListener(pwdMatcher);

            main.add(new JLabel(M.getString("CONFIRM"), SwingConstants.TRAILING));
            main.add(_second);

            rows += 2;
        }
        main.add(new JLabel());
        main.add(_showPasswordsBox);

        SpringUtilities.makeCompactGrid(main, rows, 2, 5, 5, 5, 5);
        contentPane.add(main, BorderLayout.CENTER);
        return contentPane;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Action> getActions() {
        return Arrays.asList(_okAction, _cancelAction);
    }

    /**
     * @return the pressed button which closed the dialog.
     */
    public Buttons getButton() {
        return _button;
    }

    /**
     * @return the entered password
     */
    public char[] getPassword() {
        return _first.getPassword();
    }

    private void init() {
        setRestoreLayout(false);

        _okAction = (ActionCallback) M.createAction(this, "onOK");
        _cancelAction = M.createAction(this, "onCancel");
        _messageLabel = new JLabel(M.getString("ENTER_PWD"), SwingConstants.CENTER);
        _button = Buttons.CANCEL_BUTTON;

        _first = new JPasswordField(15);
        _first.addActionListener(_okAction);

        _second = new JPasswordField(15);
        _second.addActionListener(_okAction);

        _showPasswordsBox = new JCheckBox(M.createAction(this, "onShowPasswords"));

        _progressBar = new PasswordQualityBar();

        checkPwdEqual();
    }

    protected void onCancel() {
        _button = Buttons.CANCEL_BUTTON;
        close();
    }

    protected void onOK() {
        if (checkPwd()) {
            _button = Buttons.APPROVED_BUTTON;
            close();
        }
    }

    /**
     * @return true if there is no problem with the passwords
     */
    private boolean checkPwd() {
        if (!_confirm) {
            return true;
        }

        return true;
    }

    protected void onShowPasswords() {
        char echoChar = 0;
        if (!_showPasswordsBox.isSelected()) {
            echoChar = '*';
        }
        _first.setEchoChar(echoChar);
        _second.setEchoChar(echoChar);
    }

    /**
     * @param message
     *            the message to display
     */
    public void setMessage(final String message) {
        _messageLabel.setText(message);
        _messageLabel.setVisible(message.length() == 0 ? false : true);
    }

    /**
     * @param text
     *            the tooltip to display
     */
    public void setMessageTooltip(final String text) {
        _messageLabel.setToolTipText(text);
    }
}
