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

import java.util.List;

import javax.swing.Action;

import de.tbuchloh.kiskis.gui.common.Application;
import de.tbuchloh.kiskis.gui.common.Application.ProgramNotFoundException;
import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.localization.Messages;

/**
 * <b>BasicUrlField</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public class BasicUrlField extends BasicTextField {

    protected static final Messages M = new Messages(BasicUrlField.class);

    private static final long serialVersionUID = 4667098573350069265L;

    private Action _openURLAction;

    /**
     * creates a new BasicUrlField
     */
    public BasicUrlField() {
        super();
        init();
    }

    /**
     * creates a new BasicUrlField
     * 
     * @param text
     */
    public BasicUrlField(final String text) {
        super(text);
        init();
    }

    private Action createOpenUrlAction() {
        return M.createAction(this, "onOpenUrl");
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.widgets.BasicTextField#getContextMenuActions()
     */
    @Override
    protected List<Action> getContextMenuActions() {
        final List<Action> actions = super.getContextMenuActions();
        _openURLAction = createOpenUrlAction();
        actions.add(_openURLAction);
        return actions;
    }

    /**
     * @return the action which will start the open URL process.
     */
    public Action getOpenURLAction() {
        return _openURLAction;
    }

    private void init() {

    }

    /**
     * Opens the associated URL with an assigned application.
     */
    protected void onOpenUrl() {
        try {
            final Application app = Application.create(getText(), Settings.getPreferences());
            // TODO: bind variables
            // app.setUsername(_na.getUsername());
            // app.setPassword(_na.getPwd().getPwd());
            // app.setEmail(_na.getEmail());
            app.start();
        } catch (final ProgramNotFoundException e) {
            MessageBox.showErrorMessage(e.getMessage());
        }
    }
}
