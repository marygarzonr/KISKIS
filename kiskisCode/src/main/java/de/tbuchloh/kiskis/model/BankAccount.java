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

package de.tbuchloh.kiskis.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.tbuchloh.kiskis.model.annotations.SecretInfo;

/**
 * <b>BankAccount</b>:
 * 
 * @author gandalf
 * @version $Id: BankAccount.java,v 1.9 2007/02/18 14:37:29 tbuchloh Exp $
 */
public final class BankAccount extends SecuredElement {

    private static final long serialVersionUID = 1L;

    // TODO: refactor into a bank entity
    @SecretInfo
    private String _bankID;

    // TODO: refactor into a bank entity
    @SecretInfo
    private String _bankName;

    @SecretInfo
    private String _number;

    private final List<TANList> _tanLists;

    @SecretInfo
    private char[] _telebankingPin;

    /**
     * creates a new BankAccount
     */
    public BankAccount() {
        super();
        _number = "";
        _bankName = "";
        _bankID = "";
        _tanLists = new ArrayList<TANList>();
        _telebankingPin = new char[0];
    }

    /**
     * @param list
     *            the new List
     */
    public void addTANList(final TANList list) {
        _tanLists.add(list);
    }

    /**
     * removes all TAN Lists.
     */
    public void clearTANLists() {
        _tanLists.clear();
    }

    /**
     * @return Returns the bankID.
     */
    public final String getBankID() {
        return _bankID;
    }

    /**
     * @return Returns the bank.
     */
    public String getBankName() {
        return _bankName;
    }

    /**
     * @return Returns the number.
     */
    public String getNumber() {
        return _number;
    }

    /**
     * @return Returns the tanLists.
     */
    public List<TANList> getTanLists() {
        return Collections.unmodifiableList(_tanLists);
    }

    /**
     * @return Returns the telebankingPin.
     */
    public final char[] getTelebankingPin() {
        return _telebankingPin.clone();
    }

    @Override
    public boolean matches(final SecuredElement other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BankAccount)) {
            return false;
        }
        final BankAccount castOther = (BankAccount) other;
        return super.matches(other) && _number.equals(castOther._number)
        && _bankName.equals(castOther._bankName)
        && _bankID.equals(castOther._bankID)
        && _tanLists.equals(castOther._tanLists)
        && Arrays.equals(_telebankingPin, castOther._telebankingPin);
    }

    /**
     * @param bankID
     *            The bankID to set.
     */
    public final void setBankID(final String bankID) {
        _bankID = bankID;
    }

    /**
     * @param bank
     *            The bank to set.
     */
    public void setBankName(final String bank) {
        _bankName = bank;
    }

    /**
     * @param number
     *            The number to set.
     */
    public void setNumber(final String number) {
        _number = number;
    }

    /**
     * @param telebankingPin
     *            The telebankingPin to set.
     */
    public final void setTelebankingPin(final char[] telebankingPin) {
        _telebankingPin = telebankingPin.clone();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.ModelNode#visit(de.tbuchloh.kiskis.model.ModelNodeVisitor)
     */
    @Override
    public void visit(final ModelNodeVisitor visitor) {
        visitor.visit(this);
    }
}