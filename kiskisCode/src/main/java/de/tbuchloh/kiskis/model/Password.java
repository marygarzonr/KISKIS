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

package de.tbuchloh.kiskis.model;

import java.io.File;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;

import de.tbuchloh.kiskis.model.annotations.Observable;
import de.tbuchloh.kiskis.model.annotations.SecretInfo;
import de.tbuchloh.kiskis.persistence.PasswordProxy;
import de.tbuchloh.kiskis.util.DateUtils;

/**
 * <b>Password</b>: Represents a normal password.
 * 
 * @author gandalf
 * @version $Id: Password.java,v 1.7 2007/02/18 14:37:29 tbuchloh Exp $
 */
@Observable
public final class Password implements ModelConstants, Cloneable, Serializable, PasswordProxy {

    private static final String MSG_STRING = "{0} created {1} expires {2,date,short}";

    private static final String PUNCT_SET = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~ ";

    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param pwd
     *            is the Password to Test
     * @return the equivalent key size which corresponds to the complexetiy of
     *         pwd.
     */
    public static double checkEffectiveBitSize(final char[] pwd) {
        int charSet = 0;

        charSet = getCharSetUsed(pwd);

        if (charSet == 0) {
            return 0;
        }

        final double result = Math.log(Math.pow(charSet, pwd.length))
        / Math.log(2);

        return result;
    }

    private static boolean containsLowerCaseChars(final char[] str) {
        for (int i = 0; i < str.length; ++i) {
            if (Character.isLowerCase(str[i])) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsNumbers(final char[] str) {
        for (int i = 0; i < str.length; ++i) {
            if (Character.isDigit(str[i])) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsPunctuation(final char[] str) {
        final String punct = PUNCT_SET;
        for (int i = 0; i < str.length; ++i) {
            // TODO naive approach, can be optimized
            for (int j = 0; j < punct.length(); ++j) {
                if (punct.charAt(j) == str[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean containsUpperCaseChars(final char[] str) {
        for (int i = 0; i < str.length; ++i) {
            if (Character.isUpperCase(str[i])) {
                return true;
            }
        }
        return false;
    }

    private static int getCharSetUsed(final char[] pass) {
        int ret = 0;

        if (containsNumbers(pass)) {
            ret += 10;
        }

        if (containsLowerCaseChars(pass)) {
            ret += 26;
        }

        if (containsUpperCaseChars(pass)) {
            ret += 26;
        }

        if (containsPunctuation(pass)) {
            ret += PUNCT_SET.length();
        }

        return ret;
    }

    private final Calendar _created, _expires;

    @SecretInfo
    private final char[] _pwd;

    /**
     * creates a new Password
     */
    protected Password() {
        _created = DateUtils.getCurrentDateTime();
        _expires = DateUtils.getCurrentDate();
        _pwd = new char[0];
    }

    /**
     * creates a new Password
     * 
     * @param pwd
     *            the new password
     */
    public Password(final char[] pwd) {
        this(pwd, DateUtils.getCurrentDate());
    }

    /**
     * @param pwd
     *            is the represented password
     * @param expires
     *            is the date when the password has to be changed
     */
    public Password(final char[] pwd, final Calendar expires) {
        this(pwd, expires, DateUtils.getCurrentDateTime());
    }

    public Password(final char[] pwd, final Calendar expires,
            final Calendar created) {
        _expires = (Calendar) expires.clone();
        _created = (Calendar) created.clone();
        _pwd = pwd.clone();
    }

    /**
     * @param pwd
     *            is the represented password
     * @param daysToExpire
     *            is the date when the password has to be changed
     */
    public Password(final char[] pwd, final int daysToExpire) {
        this(pwd, DateUtils.getCurrentDate());
        _expires.add(Calendar.DAY_OF_YEAR, daysToExpire);
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Password clone() {
        final Password inst = new Password(this._pwd, this._expires,
                this._created);
        return inst;
    }

    /**
     * Returns <code>true</code> if this <code>Password</code> is the same as
     * the o argument.
     * 
     * @return <code>true</code> if this <code>Password</code> is the same as
     *         the o argument.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        final Password castedObj = (Password) o;
        return this._created.equals(castedObj._created)
        && this._expires.equals(castedObj._expires)
        && java.util.Arrays.equals(this._pwd, castedObj._pwd);
    }

    /**
     * @return the creation time of the password
     */
    public Calendar getCreationDate() {
        return (Calendar) _created.clone();
    }

    /**
     * @return the expiry date
     */
    public Calendar getExpires() {
        return (Calendar) _expires.clone();
    }

    /**
     * @return the represented password
     */
    public char[] getPwd() {
        return _pwd.clone();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this._pwd.hashCode();
    }

    /**
     * @return true, if the password represents an empty password
     */
    public boolean isEmpty() {
        return _pwd.length == 0;
    }

    /**
     * @param password
     *            the password to compare with
     * @return true, if the passphrases are equal
     */
    public boolean isSame(final Password password) {
        return Arrays.equals(_pwd, password._pwd);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final Object[] p = new Object[] { _pwd, _created.getTime(),
                _expires.getTime() };
        return MessageFormat.format(MSG_STRING, p);
    }

    @Override
    public char[] getPassword(File file) {
        // TODO Auto-generated method stub
        return getPassword(file, false);
    }

    @Override
    public char[] getPassword(File file, boolean confirm) {
        // TODO Auto-generated method stub
        return _pwd;
    }
}
