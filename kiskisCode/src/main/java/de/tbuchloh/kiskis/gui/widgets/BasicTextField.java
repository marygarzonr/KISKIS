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

package de.tbuchloh.kiskis.gui.widgets;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPopupMenu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.common.ClipboardHelper;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.widgets.ObservableTextField;

/**
 * <b>BasicTextField</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public class BasicTextField extends ObservableTextField {

    /**
     * Commons Logger for this class
     */
    public static final Log LOG = LogFactory.getLog(BasicTextField.class);

    private static final Messages M = new Messages(BasicTextField.class);

    private static final long serialVersionUID = -6948124799725169099L;

    private Action _copyAction;

    protected JPopupMenu _specialActionsMenu;

    /**
     * creates a new BasicTextField
     */
    public BasicTextField() {
	super();
	init();
    }

    /**
     * creates a new BasicTextField
     * 
     * @param columns
     */
    public BasicTextField(final int columns) {
	super(columns);
	init();
    }

    /**
     * creates a new BasicTextField
     * 
     * @param text
     */
    public BasicTextField(final String text) {
	super(text);
	init();
    }

    /**
     * creates a new BasicTextField
     * 
     * @param text
     * @param columns
     */
    public BasicTextField(final String text, final int columns) {
	super(text, columns);
	init();
    }

    protected List<Action> getContextMenuActions() {
	final List<Action> actions = new ArrayList<Action>();
	actions.add(_copyAction);
	return actions;
    }

    private void init() {
	_copyAction = M.createAction(this, "onCopyToClipboard");
	_specialActionsMenu = new JPopupMenu();
	for (final Object element : getContextMenuActions()) {
	    final Action act = (Action) element;
	    _specialActionsMenu.add(act);
	}

	addMouseListener(new MouseAdapter() {
	    @Override
	    public void mousePressed(final MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
		    return;
		}
		final int x = 0; // _pwdField.getWidth()
		// - _specialActionsMenu.getWidth();
		final int y = getHeight();

		LOG.debug("Show context menu x=" + x + ", y=" + y);
		_specialActionsMenu.show(BasicTextField.this, x, y);
	    }
	});
    }

    /**
     * Callback:
     */
    protected void onCopyToClipboard() {
	ClipboardHelper.copyToClipboard(getText());
    }

}
