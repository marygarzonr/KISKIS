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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.tbuchloh.kiskis.model.annotations.Observable;
import de.tbuchloh.kiskis.util.DateUtils;

/**
 * <b>TANList</b>:
 * 
 * @author gandalf
 * @version $Id: TANList.java,v 1.7 2007/03/12 19:05:58 tbuchloh Exp $
 */
@Observable
@SuppressWarnings("unchecked")
public final class TANList implements Cloneable {

	private static final MessageFormat TO_STRING = new MessageFormat(
			Messages.getString("TANList.TO_STRING")); //$NON-NLS-1$

	private Calendar _created;

	private String _id;

	private List<TAN> _tans;

	/**
	 * creates a new TANList
	 */
	public TANList() {
		super();
		_id = ""; //$NON-NLS-1$
		_tans = new ArrayList<TAN>();
		_created = DateUtils.getCurrentDateTime();
	}

	/**
	 * @param tan
	 *            the new TAN
	 */
	public void addTAN(final TAN tan) {
		_tans.add(tan);
	}

	/**
	 * removes all the TANs from the list.
	 */
	public void clearTANs() {
		_tans.clear();
	}

	/**
	 * Overridden!
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public TANList clone() {
		try {
			final TANList inst = (TANList) super.clone();
			inst._created = (Calendar) inst._created.clone();
			inst._tans = new ArrayList<TAN>();
			for (final Object element : this._tans) {
				final TAN tan = (TAN) element;
				inst.addTAN(tan.clone());
			}
			return inst;
		} catch (final CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TANList)) {
			return false;
		}

		final TANList castOther = (TANList) other;
		return _created.equals(castOther._created) && _id.equals(castOther._id)
				&& _tans.equals(castOther._tans);
	}

	/**
	 * @return Returns the created.
	 */
	public Calendar getCreated() {
		return _created;
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return _id;
	}

	/**
	 * @return the next ID of the TANs
	 */
	public int getNextId() {
		int max = 0;
		for (final TAN tan : _tans) {
			max = Math.max(max, tan.getId());
		}
		return max + 1;
	}

	/**
	 * @return Returns the tans.
	 */
	public List<TAN> getTans() {
		return Collections.unmodifiableList(_tans);
	}

	/**
	 * @return the number of used TANs
	 */
	public int getUsedCnt() {
		int cnt = 0;
		for (final Object element : _tans) {
			final TAN tan = (TAN) element;
			if (tan.wasUsed()) {
				cnt++;
			}
		}
		return cnt;
	}

	/**
	 * @param tan
	 *            the object to remove
	 * @return true, if the object has been removed.
	 */
	public boolean remove(final TAN tan) {
		return _tans.remove(tan);
	}

	/**
	 * @param created
	 *            The created to set.
	 */
	public void setCreated(final Calendar created) {
		_created = created;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(final String id) {
		_id = id;
	}

	/**
	 * @return the number of TANs
	 */
	public int size() {
		return _tans.size();
	}

	/**
	 * Overridden!
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final Object[] p = { ModelConstants.SHORT.format(_created.getTime()),
				_id, Integer.valueOf(size() - getUsedCnt()),
				Integer.valueOf(getUsedCnt()), Integer.valueOf(size()) };
		return TO_STRING.format(p);
	}
}
