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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

/**
 * <b>PasswordHistory</b>:
 * 
 * @author gandalf
 * @version $Id: PasswordHistory.java,v 1.6 2007/02/18 14:37:30 tbuchloh Exp $
 */
public final class PasswordHistory implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	private Collection<Password> _passwords;

	/**
	 * creates a new PasswordHistory
	 */
	public PasswordHistory() {
		_passwords = new Vector<Password>();
	}

	/**
	 * @param pwd
	 *            the password to append
	 */
	public void addPassword(final Password pwd) {
		_passwords.add(pwd);
	}

	/**
	 * Overridden!
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		PasswordHistory p;
		try {
			p = (PasswordHistory) super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new Error(e);
		}
		p._passwords = new Vector<Password>();
		for (final Object element : getPasswords()) {
			final Password pwd = (Password) element;
			p.addPassword(pwd.clone());
		}
		return p;
	}

	/**
	 * Returns <code>true</code> if this <code>PasswordHistory</code> is the
	 * same as the o argument.
	 * 
	 * @return <code>true</code> if this <code>PasswordHistory</code> is the
	 *         same as the o argument.
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
		final PasswordHistory castedObj = (PasswordHistory) o;
		return this._passwords.equals(castedObj._passwords);
	}

	/**
	 * @return all used passwords
	 */
	public Collection<Password> getPasswords() {
		return Collections.unmodifiableCollection(_passwords);
	}

	/**
	 * @param pwd
	 *            password to remove
	 * @return true, if the password could be removed
	 */
	public boolean removePassword(final Password pwd) {
		return _passwords.remove(pwd);
	}
}
