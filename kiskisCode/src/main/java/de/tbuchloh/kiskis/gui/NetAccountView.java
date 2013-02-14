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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import de.tbuchloh.kiskis.gui.common.Application;
import de.tbuchloh.kiskis.gui.common.Application.ProgramNotFoundException;
import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.kiskis.gui.widgets.BasicTextField;
import de.tbuchloh.kiskis.gui.widgets.BasicUrlField;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.NetAccount;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.swing.actions.ActionCallback;

/**
 * <b>NetAccountView</b>:
 * 
 * @author gandalf
 * @version $Id: NetAccountView.java,v 1.7 2007/02/18 14:37:47 tbuchloh Exp $
 */
public final class NetAccountView extends AbstractAccountDetailView {

    private static final long serialVersionUID = 1L;

    public static void main(final String[] args) {
        final JFrame frame = new JFrame();
        frame.getContentPane().add(new NetAccountView(new NetAccount()));
        frame.pack();
        frame.setVisible(true);
    }

    protected NetAccount _na;

    protected JTextField _username;

    protected JTextField _url;

    protected JTextField _email;

    private final ActionCallback _openUrlAction;

    /**
     * creates a new netaccount view
     * 
     * @param na
     *            account to edit
     */
    public NetAccountView(final ModelNode na) {
        super();
        _na = (NetAccount) na;
        _username = new BasicTextField(_na.getUsername());
        _username.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (!_username.getText().equals(_na.getUsername())) {
                    fireContentChangedEvent(true);
                }
            }
        });
        _url = new BasicUrlField(_na.getUrl());
        _url.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (!_url.getText().equals(_na.getUrl())) {
                    fireContentChangedEvent(true);
                }
            }
        });
        _email = new BasicTextField(_na.getEmail());
        _email.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (!_email.getText().equals(_na.getEmail())) {
                    fireContentChangedEvent(true);
                }
            }
        });
        _openUrlAction = new ActionCallback(this, "openUrl", //$NON-NLS-1$
                Messages.getString("NetAccountView.openUrlAction_title")); //$NON-NLS-1$
        _openUrlAction.setTooltip(Messages.getString("NetAccountView.openUrlAction_tooltip")); //$NON-NLS-1$
        init();
    }

    private void init() {
        this.setLayout(new BorderLayout());

        final JPanel main = new JPanel(new SpringLayout());
        addRow(main, Messages.getString("NetAccountView.user_name_label"), _username);
        _username.setToolTipText(Messages.getString("NetAccountView.user_name_tt")); //$NON-NLS-1$

        addRow(main, Messages.getString("NetAccountView.url_label"), _url);
        _url.setToolTipText(Messages.getString("NetAccountView.url_tt")); //$NON-NLS-1$

        addRow(main, Messages.getString("NetAccountView.email_label"), _email);
        _email.setToolTipText(Messages.getString("NetAccountView.email_tt")); //$NON-NLS-1$

        makeGrid(main, 3);

        this.add(main, BorderLayout.NORTH);
    }

    /**
     * @see de.tbuchloh.kiskis.gui.SpecialView#storeValues()
     */
    @Override
    protected void store() {
        _na.setEmail(_email.getText());
        _na.setUrl(_url.getText());
        _na.setUsername(_username.getText());
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.SpecialView#getSpecialActions()
     */
    @Override
    public Collection getSpecialActions() {
        return Collections.singletonList(_openUrlAction);
    }

    /**
     * Opens the associated URL with an assigned application.
     */
    public void openUrl() {
        try {
            final Application app = Application.create(_na.getUrl(), Settings.getPreferences());
            app.setUsername(_na.getUsername());
            app.setPassword(_na.getPwd().getPwd());
            app.setEmail(_na.getEmail());
            app.start();
        } catch (final ProgramNotFoundException e) {
            MessageBox.showErrorMessage(e.getMessage());
        }
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.AbstractAccountDetailView#getTitle()
     */
    @Override
    public String getTitle() {
        return Messages.getString("NetAccountView.title"); //$NON-NLS-1$;
    }
}
