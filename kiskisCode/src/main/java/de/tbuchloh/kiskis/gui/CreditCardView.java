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

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import de.tbuchloh.kiskis.gui.common.UIConstants;
import de.tbuchloh.kiskis.gui.widgets.BasicTextField;
import de.tbuchloh.kiskis.gui.widgets.PasswordElement;
import de.tbuchloh.kiskis.model.CreditCard;
import de.tbuchloh.kiskis.model.ModelNode;

/**
 * <b>CreditCardView</b>:
 * 
 * @author gandalf
 * @version $Id: CreditCardView.java,v 1.6 2007/02/18 14:37:49 tbuchloh Exp $
 */
public final class CreditCardView extends AbstractAccountDetailView implements UIConstants, ContentChangedListener {

    private static final long serialVersionUID = 1L;

    protected CreditCard _cc;

    private JTextField _deployer;

    private JTextField _number;

    private PasswordElement _pin;

    private BasicTextField _checksumBox;

    /**
     * creates a new CreditCardView
     * 
     * @param cl
     *            will be notified if a field changes
     * @param cc
     *            is the credit card object
     */
    public CreditCardView(final ModelNode cc) {
        super();
        _cc = (CreditCard) cc;
        createDeployerField();
        createNumberField();
        createPinBox();
        createChecksumBox();
        init();
    }

    private void createPinBox() {
        _pin = new PasswordElement(_cc.getPin());
        _pin.setShowQualityLabel(false);
        _pin.setShowTestButton(false);
        _pin.addContentChangedListener(this);
    }

    private void createChecksumBox() {
        _checksumBox = new BasicTextField(_cc.getCardValidationCode());
        _checksumBox.setToolTipText(Messages.getString("CreditCardView.cvc_tooltip"));
        _checksumBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                final JTextField field = (JTextField) e.getSource();
                if (!field.getText().equals(_cc.getCardValidationCode())) {
                    fireContentChangedEvent(true);
                }
            }
        });
    }

    private void createDeployerField() {
        _deployer = new BasicTextField(_cc.getDeployer());
        _deployer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                final JTextField field = (JTextField) e.getSource();
                if (!field.getText().equals(_cc.getDeployer())) {
                    fireContentChangedEvent(true);
                }
            }
        });
    }

    private void createNumberField() {
        _number = new BasicTextField(_cc.getNumber());
        _number.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                final JTextField field = (JTextField) e.getSource();
                if (!field.getText().equals(_cc.getNumber())) {
                    fireContentChangedEvent(true);
                }
            }
        });
    }

    private void init() {
        this.setLayout(new BorderLayout());

        final JPanel main = new JPanel(new SpringLayout());

        addRow(main, Messages.getString("CreditCardView.bank_label"), _deployer);
        addRow(main, Messages.getString("CreditCardView.card_number_label"), _number);
        addRow(main, Messages.getString("CreditCardView.pin_label"), _pin);
        addRow(main, Messages.getString("CreditCardView.cvc_label"), _checksumBox);

        makeGrid(main, 4);

        this.add(main, BorderLayout.NORTH);
    }

    /**
     * @see de.tbuchloh.kiskis.gui.SpecialView#storeValues()
     */
    @Override
    protected void store() {
        _cc.setDeployer(_deployer.getText());
        _cc.setNumber(_number.getText());
        _cc.setPin(_pin.getPwd());
        _cc.setCardValidationCode(_checksumBox.getText());
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.AbstractAccountDetailView#getSpecialActions()
     */
    @Override
    public Collection<Action> getSpecialActions() {
        return Collections.emptyList();
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
        return Messages.getString("CreditCardView.title");
    }
}
