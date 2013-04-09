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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import de.tbuchloh.kiskis.gui.common.LnFHelper;
import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.kiskis.gui.widgets.BasicTextField;
import de.tbuchloh.kiskis.gui.widgets.PasswordElement;
import de.tbuchloh.kiskis.model.PasswordCallFactory;
import de.tbuchloh.kiskis.model.PasswordTemplate;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.SpringUtilities;
import de.tbuchloh.util.swing.widgets.LinkLabel;
import de.tbuchloh.util.swing.widgets.ObservableCheckBox;
import de.tbuchloh.util.swing.widgets.ObservableTextField;

/**
 * <b>PasswordGeneratorDialog</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public class PasswordGeneratorDialog extends KisKisDialog {

    private static final long serialVersionUID = 1L;

    protected static final Messages M = new Messages(PasswordGeneratorDialog.class);

    private static final Preferences P = Preferences.userNodeForPackage(PasswordGeneratorDialog.class);

    /**
     * Comment for <code>P_INTEGER</code>
     */
    private static final Pattern P_INTEGER = Pattern.compile("^[1-9][0-9]*");

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final PasswordGeneratorDialog dlg = new PasswordGeneratorDialog(new JFrame());
        dlg.setVisible(true);

    }

    private Action _closeAction;

    private Action _generatePasswordsAction;

    private Action _helpAction;

    private ObservableTextField _lengthField;

    private Action _okAction;

    private String _password;

    private JList _passwordList;

    private ObservableTextField _templateField;

    private ObservableCheckBox _mixCaseBox;

    /**
     * creates a new PasswordGeneratorDialog
     * 
     * @param owner
     * @param title
     * @param modal
     */
    public PasswordGeneratorDialog(final Dialog owner) {
        super(owner, M.getString("title"), true);
        init();
    }

    /**
     * creates a new PasswordGeneratorDialog
     * 
     * @param owner
     */
    public PasswordGeneratorDialog(final Frame owner) {
        super(owner, M.getString("title"), true);
        init();
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

        final JPanel top = new JPanel(new BorderLayout());
        top.setBorder(LnFHelper.createDefaultBorder());

        final JPanel fields = new JPanel(new SpringLayout());
        fields.add(LnFHelper.createLabel(M.getString("template.label")));
        fields.add(_templateField);

        fields.add(LnFHelper.createLabel(M.getString("password_length.label")));
        fields.add(_lengthField);

        final JLabel mixCaseLabel = LnFHelper.createLabel(M.getLabel("mixCase"));
        mixCaseLabel.setToolTipText(M.getTooltip("mixCase"));
        fields.add(mixCaseLabel);
        fields.add(_mixCaseBox);

        SpringUtilities.makeCompactGrid(fields, 3, 2, 0, 0, 5, 5);

        top.add(fields);

        top.add(new LinkLabel(_generatePasswordsAction), BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);

        panel.add(new JScrollPane(_passwordList), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.dialogs.KisKisDialog#getActions()
     */
    @Override
    protected List<Action> getActions() {
        final List<Action> actions = new ArrayList<Action>();
        actions.add(_helpAction);
        actions.add(_okAction);
        actions.add(_closeAction);
        return actions;
    }

    private String getLastLength() {
        return P.get("lastLength", "5");
    }

    private String getLastTemplate() {
        return P.get("lastTemplate", "99aaaa??99");
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return _password;
    }

    private void init() {
        _templateField = new BasicTextField(getLastTemplate());
        _lengthField = new BasicTextField(getLastLength());
        _lengthField.setValidator(P_INTEGER);
        _generatePasswordsAction = M.createAction(this, "onGenerate");
        _passwordList = new JList();
        _passwordList.setFont(//
                PasswordElement.createFont().deriveFont(14f));
        _okAction = M.createAction(this, "onOK");
        _okAction.setEnabled(false);
        _closeAction = M.createAction(this, "onCancel");
        _helpAction = M.createAction(this, "onHelp");
        _mixCaseBox = new ObservableCheckBox();
        _mixCaseBox.setToolTipText(M.getTooltip("mixCase"));

        onGenerate();
    }

    protected void onCancel() {
        this.close();
        _password = null;
    }

    protected void onGenerate() {
        final String tmp = _templateField.getText();
        if (tmp.length() == 0) {
            MessageBox.showErrorMessage(M.getString("errInvalidPasswordLength"));
            return;
        }

        final String length = _lengthField.getText();
        if (!P_INTEGER.matcher(length).matches()) {
            MessageBox.showErrorMessage(M.getString("errInvalidPasswordCount"));
            return;
        }

        final PasswordTemplate template = new PasswordTemplate();
        template.setTemplate(tmp);

        final Vector<String> pwds = new Vector<String>();
        final int len = Integer.parseInt(length);
        for (int i = 0; i < len; ++i) {
            pwds.add(new String(PasswordCallFactory.create(template, _mixCaseBox.isSelected()).getPwd()));
        }
        _passwordList.setListData(pwds);
        _passwordList.setSelectedIndex(0);
        _okAction.setEnabled(true);
    }

    protected void onHelp() {
        MessageBox.showMessageDialog(this, M.getString("template.charset"));
    }

    protected void onOK() {
        _password = (String) _passwordList.getSelectedValue();
        P.put("lastLength", _lengthField.getText());
        P.put("lastTemplate", _templateField.getText());
        this.close();
    }

}
