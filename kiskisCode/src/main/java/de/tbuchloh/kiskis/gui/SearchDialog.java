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

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.common.SearchListener;
import de.tbuchloh.kiskis.gui.dialogs.KisKisDialog;
import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.util.swing.Toolkit;
import de.tbuchloh.util.swing.actions.ActionCallback;

/**
 * <b>SearchDialog</b>:
 * 
 * @author gandalf
 * @version $Id: SearchDialog.java,v 1.11 2007/02/18 14:37:48 tbuchloh Exp $
 */
public final class SearchDialog extends KisKisDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(SearchDialog.class);

	private final Action _closeAction;

	protected Action _findNextAction;

	private JTextField _query;

	private final SearchWorker _searcher;

	public SearchDialog(final SearchListener parent, final Group root) {
		super(MainFrame.getInstance(),
				Messages.getString("SearchDialog.title"), //$NON-NLS-1$
				false);
		_searcher = new SearchWorker(parent, root);
		_closeAction = new ActionCallback(this,
				"close", Messages.getString("SearchDialog.closeAction_title")); //$NON-NLS-1$ //$NON-NLS-2$
		_findNextAction = new ActionCallback(
				this,
				"findNext", Messages.getString("SearchDialog.findNextAction_title")); //$NON-NLS-1$ //$NON-NLS-2$
		Toolkit.restoreWindowState(this);
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.dialogs.KisKisDialog#getActions()
	 */
	@Override
	protected List getActions() {
		final Action[] actions = { _findNextAction, _closeAction };
		_findNextAction.setEnabled(false);
		return Arrays.asList(actions);
	}

	@Override
	protected Component createMainPanel() {
		_query = new JTextField(25);
		_query.addActionListener(_findNextAction);
		_query.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(final KeyEvent e) {
				_findNextAction.setEnabled(_query.getText().length() > 0);
			}
		});
		final JPanel panel = new JPanel();
		panel.add(new JLabel(Messages
				.getString("SearchDialog.queryLabel_title"))); //$NON-NLS-1$
		panel.add(_query);
		return panel;
	}

	protected void findNext() {
		_searcher.findNext(getQuery());
	}

	protected Pattern getQuery() {
		final String keyword = _query.getText();
		return SearchWorker.createKeywordQuery(keyword);
	}
}
