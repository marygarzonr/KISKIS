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

import java.util.regex.Pattern;

import de.tbuchloh.kiskis.model.annotations.SecretInfo;

/**
 * <b>NetAccount</b>: A net account has a user name and an associated email
 * address.<br>
 * It can point to any specific URL.
 * 
 * @author gandalf
 * @version $Id: NetAccount.java,v 1.6 2007/02/18 14:37:29 tbuchloh Exp $
 */
public class NetAccount extends SecuredElement {

    private static final long serialVersionUID = 1L;

    @SecretInfo
    private String _email;

    @SecretInfo
    private String _url;

    @SecretInfo
    private String _username;

    /**
     * creates a new network account.
     */
    public NetAccount() {
        _username = "";
        _url = "";
        _email = "";
    }

    /**
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public NetAccount clone() {
        final NetAccount inst = (NetAccount) super.clone();
        inst._username = this._username == null ? null : this._username;
        inst._url = this._url == null ? null : this._url;
        inst._email = this._email == null ? null : this._email;
        return inst;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.ModelNode#contains(Pattern)
     */
    @Override
    public boolean contains(final Pattern regex) {
        return super.contains(regex) || regex.matcher(_email).matches()
        || regex.matcher(_url).matches()
        || regex.matcher(_username).matches();
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return _email;
    }

    /**
     * @return the server url
     */
    public String getUrl() {
        return _url;
    }

    /**
     * @return the user name
     */
    public String getUsername() {
        return _username;
    }

    @Override
    public boolean matches(final SecuredElement obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NetAccount)) {
            return false;
        }

        if (!super.matches(obj)) {
            return false;
        }

        final NetAccount other = (NetAccount) obj;
        if (!_email.equals(other._email)) {
            return false;
        }
        if (!_url.equals(other._url)) {
            return false;
        }
        if (!_username.equals(other._username)) {
            return false;
        }
        return true;
    }

    /**
     * @param email
     *            the email
     */
    public void setEmail(final String email) {
        _email = email;
    }

    /**
     * @param url
     *            the server url
     */
    public void setUrl(final String url) {
        _url = url;
    }

    /**
     * @param name
     *            the user name
     */
    public void setUsername(final String name) {
        _username = name;
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
