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

package de.tbuchloh.kiskis.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.SpecialView;
import de.tbuchloh.kiskis.gui.common.ClipboardHelper;
import de.tbuchloh.kiskis.gui.dialogs.CrackPasswordDialog;
import de.tbuchloh.kiskis.gui.dialogs.PasswordGeneratorDialog;
import de.tbuchloh.kiskis.model.Password;
import de.tbuchloh.kiskis.model.PasswordCallFactory;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.dialogs.MessagePane;
import de.tbuchloh.util.swing.widgets.LinkLabel;

/**
 * <b>PasswordElement</b>:
 * 
 * @author gandalf
 * @version $Id: PasswordElement.java,v 1.14 2007/12/02 12:44:08 tbuchloh Exp $
 */
public final class PasswordElement extends SpecialView {

    private final class PasswordActionListener implements ActionListener {

        private final int _pwdMode;

        public PasswordActionListener(final int mode) {
            _pwdMode = mode;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            int length = P.getInt(K_NEW_PWD_LENGTH, D_NEW_PWD_LENGTH);
            final String value = MessagePane.showInputDialog(PasswordElement.this,
                    PasswordElement.M.getString("MSG_PWD_INPUT"), INT_REGEX, String.valueOf(length));
            if (value != null) {
                length = Integer.parseInt(value);
                P.putInt(K_NEW_PWD_LENGTH, length);
                createPassword(_pwdMode, length);
            }
        }
    }

    private static final int D_NEW_PWD_LENGTH = 10;

    private static final int GENERATE = 3;

    private static final int HIDDEN = 1;

    protected static final String INT_REGEX = "[1-9][0-9]{0,8}"; //$NON-NLS-1$

    private static final String K_NEW_PWD_LENGTH = "k_new_pwd_length"; //$NON-NLS-1$

    /**
     * Commons Logger for this class
     */
    protected static final Log LOG = LogFactory.getLog(PasswordElement.class);

    protected static final Messages M = new Messages(PasswordElement.class);

    protected static final Preferences P = Preferences.userNodeForPackage(PasswordElement.class);

    private static final long serialVersionUID = -7174842153684923470L;

    private static final int SHOW = 2;

    /**
     * @return the font for Password-Fields
     */
    public static Font createFont() {
        return new Font(Settings.getPasswordFieldFont(), Font.PLAIN, 12);
    }

    public static void main(final String[] args) {
        final JFrame frame = new JFrame();
        final PasswordElement p = new PasswordElement(new char[0]);
        p.setShowQualityLabel(true);
        frame.getContentPane().add(p);
        frame.pack();
        frame.setVisible(true);
    }

    private JPanel _buttonPanel;

    private int _mode;

    private final PasswordQualityBar _progressBar;

    private final JButton _pwdButton;

    private JPopupMenu _pwdCreateMenu;

    protected JPasswordField _pwdField;

    protected JPopupMenu _specialActionsMenu;

    private final JButton _testButton;

    /**
     * @param pwd
     *            the password to edit
     */
    public PasswordElement(final char[] pwd) {
        _pwdField = new JPasswordField(new String(pwd));
        _pwdField.setFont(createFont());
        _pwdField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(final DocumentEvent e) {
                notifyListeners();
            }

            @Override
            public void insertUpdate(final DocumentEvent e) {
                notifyListeners();
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                notifyListeners();
            }
        });
        _pwdButton = new LinkLabel(M.createAction(this, "onShowPassword"));
        _pwdButton.setHorizontalAlignment(SwingConstants.RIGHT);

        _testButton = new LinkLabel(M.createAction(this, "onTestPassword"));
        _testButton.setHorizontalAlignment(SwingConstants.RIGHT);

        _mode = HIDDEN;

        _progressBar = new PasswordQualityBar();
        _progressBar.setSmallFont();

        init();
        initCreatePopup();
    }

    protected void createPassword(final int mode, final int length) {
        final Password pwd = PasswordCallFactory.create(mode, length);
        _pwdField.setText(new String(pwd.getPwd()));
        fireContentChangedEvent(true);
        updatePwdField();
    }

    /**
     * @return the password object
     */
    public char[] getPwd() {
        return _pwdField.getPassword();
    }

    private void init() {
        this.setLayout(new BorderLayout());

        final JPanel main = new JPanel(new BorderLayout());
        main.add(_pwdField, BorderLayout.CENTER);
        main.add(_progressBar, BorderLayout.SOUTH);
        this.add(main, BorderLayout.CENTER);

        _buttonPanel = new JPanel(new GridLayout(2, 1));
        _buttonPanel.add(_pwdButton);
        _buttonPanel.add(_testButton);
        this.add(_buttonPanel, BorderLayout.EAST);

        _pwdField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                notifyListeners();
            }
        });
        _pwdField.setColumns(50);

        updatePwdButton(HIDDEN);
        updatePwdField();
    }

    private void initCreatePopup() {
        _pwdCreateMenu = new JPopupMenu();
        final JMenuItem secure = new JMenuItem(M.getString("secure_item")); //$NON-NLS-1$
        secure.addActionListener(new PasswordActionListener(PasswordCallFactory.SECURE));
        _pwdCreateMenu.add(secure);

        final JMenuItem human = new JMenuItem(M.getString("human_readable_item")); //$NON-NLS-1$
        human.addActionListener(new PasswordActionListener(PasswordCallFactory.HUMAN_READABLE));
        _pwdCreateMenu.add(human);

        final JMenuItem template = new JMenuItem(M.createAction(this, "onCreateByTemplate"));
        _pwdCreateMenu.add(template);

        _specialActionsMenu = new JPopupMenu();
        _specialActionsMenu.add(M.createAction(this, "onCopyToClipboard"));

        _pwdField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON3) {
                    return;
                }
                final int x = e.getX(); // _pwdField.getWidth()
                // - _specialActionsMenu.getWidth();
                final int y = e.getY();

                LOG.debug("Show context menu x=" + x + ", y=" + y);
                _specialActionsMenu.show(_pwdField, x, y);
            }
        });
    }

    protected void notifyListeners() {
        fireContentChangedEvent(true);
        updatePwdField();
    }

    /**
     * Callback:
     */
    protected void onCopyToClipboard() {
        ClipboardHelper.copyToClipboard(new String(_pwdField.getPassword()));
    }

    protected void onCreateByTemplate() {
        final Window parent = SwingUtilities.getWindowAncestor(this);
        final PasswordGeneratorDialog dlg = new PasswordGeneratorDialog((Frame) parent);
        dlg.setVisible(true);
        final String pwd = dlg.getPassword();
        if (pwd != null) {
            _pwdField.setText(pwd);
            fireContentChangedEvent(true);
            updatePwdField();
        }
    }

    protected void onGeneratePassword() {
        switch (_mode) {
        case HIDDEN:
            updatePwdButton(SHOW);
            break;
        case SHOW:
            updatePwdButton(HIDDEN);
            break;
        case GENERATE:
            _pwdCreateMenu.show(_pwdButton, 0, _pwdButton.getHeight());
            break;
        default:
            throw new Error("unknown mode!"); //$NON-NLS-1$
        }

        updatePwdField();
    }

    protected void onHidePassword() {
        setShowPassword(false);
    }

    protected void onShowPassword() {
        setShowPassword(true);
    }

    protected void onTestPassword() {
        final CrackPasswordDialog dlg = new CrackPasswordDialog((Frame) SwingUtilities.getWindowAncestor(this),
                getPwd());
        dlg.setVisible(true);
    }

    /**
     * @param cs
     *            is the password to set.
     */
    public void setPassword(final char[] cs) {
        _pwdField.setText(String.valueOf(cs));
        updatePwdField();
    }

    /**
     * @param fontName
     *            is the fontName to use
     */
    public void setPasswordFont(String fontName) {
        _pwdField.setFont(new Font(fontName, Font.PLAIN, 12));
    }

    /**
     * @param show
     *            true, if the main button should not be shown.
     */
    public void setShowButtons(final boolean show) {
        _buttonPanel.setVisible(show);
    }

    /**
     * @param show
     *            true, if the password should be shown
     */
    public void setShowPassword(boolean show) {
        if (show) {
            _mode = SHOW;
        } else {
            _mode = HIDDEN;
        }
        updatePwdButton(_mode);
    }

    /**
     * @param show
     *            true, if the entropy label should be shown.
     */
    public void setShowQualityLabel(final boolean show) {
        _progressBar.setVisible(show);
    }

    /**
     * @see de.tbuchloh.kiskis.gui.SpecialView#storeValues()
     */
    @Override
    protected void store() {
        // do nothing
    }

    /**
     * @param newMode
     *            is the next mode to use
     */
    private void updatePwdButton(final int newMode) {
        char echo = 0;
        Action a = null;
        switch (newMode) {
        case HIDDEN:
            echo = '*';
            a = M.createAction(this, "onShowPassword");
            break;
        case SHOW:
            echo = 0;
            a = M.createAction(this, "onHidePassword");
            break;
        case GENERATE:
            echo = '*';
            a = M.createAction(this, "onGeneratePassword");
            break;
        default:
            throw new Error("unknown mode!"); //$NON-NLS-1$
        }
        _mode = newMode;
        _pwdField.setEchoChar(echo);
        _pwdButton.setAction(a);
    }

    void updatePwdField() {
        if (_mode == GENERATE && _pwdField.getPassword().length > 0) {
            updatePwdButton(HIDDEN);
        } else if (_pwdField.getPassword().length == 0) {
            updatePwdButton(GENERATE);
        }

        _progressBar.update(getPwd());
    }

    /**
     * @param value
     *            true if the button should be shown
     */
    public void setShowTestButton(boolean value) {
        _testButton.setVisible(value);
        if (value) {
            final GridLayout layout = (GridLayout) _buttonPanel.getLayout();
            layout.setRows(layout.getRows() + 1);
            _buttonPanel.add(_testButton);
        } else {
            final GridLayout layout = (GridLayout) _buttonPanel.getLayout();
            layout.setRows(layout.getRows() - 1);
            _buttonPanel.remove(_testButton);

        }
    }
}
