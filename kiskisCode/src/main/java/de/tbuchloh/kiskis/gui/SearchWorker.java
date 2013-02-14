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

package de.tbuchloh.kiskis.gui;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.common.SearchListener;
import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.kiskis.model.ModelNode;

/**
 * <b>SearchWorker</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
final class SearchWorker {
	/**
	 * Commons Logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(SearchWorker.class);

	private final Group _root;

	private final SearchListener _searchListener;

	private ModelNode _found;

	/**
	 * creates a new SearchWorker
	 * 
	 * @param root
	 * @param listener
	 */
	public SearchWorker(final SearchListener listener, final Group root) {
		super();
		_searchListener = listener;
		_root = root;
		_found = _root;
	}

	/**
	 * @param query
	 *            is the query to use
	 */
	public void findNext(final Pattern query) {
		_found = _root.find(_found, query);
		if (_found != null) {
			LOG.debug("found " + _found); //$NON-NLS-1$
			_searchListener.found(_found);
		}
	}

	/**
	 * @param keyword
	 *            is the keyword to search
	 * @return a new regex.
	 */
	public static Pattern createKeywordQuery(final String keyword) {
		return Pattern.compile("[\\p{ASCII}]*" + keyword + "[\\p{ASCII}]*", //$NON-NLS-1$ //$NON-NLS-2$
				Pattern.CASE_INSENSITIVE);
	}

}
