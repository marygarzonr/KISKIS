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

package de.tbuchloh.kiskis.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.util.event.ContentChangedEvent;
import de.tbuchloh.util.event.ContentListener;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.Toolkit;

/**
 * <b>KisKisDialog</b>: is the base of all KisKis-Dialogs.
 * 
 * @author gandalf
 * @version $Id: KisKisDialog.java,v 1.3 2006/09/08 15:12:05 tbuchloh Exp $
 */
public abstract class KisKisDialog extends JDialog implements ContentListener {

    private static final long serialVersionUID = 1L;

    private static final Messages M = new Messages(KisKisDialog.class);

    protected boolean confirmClose() {
        return MessageBox.showConfirmDialog(this, M.getString("msgConfirmChanges"));
    }

    /**
     * @param list
     *            a List of Action objects
     * @return a panel with associated buttons. All buttons have the same width.
     */
    private static JPanel createButtons(final List<Action> list) {
        final JPanel panel = new JPanel(new GridLayout(1, list.size(), 5, 5));
        for (final Iterator<Action> i = list.iterator(); i.hasNext();) {
            final Action a = i.next();
            panel.add(new JButton(a));
        }
        final JPanel pad = new JPanel();
        pad.add(panel);
        return pad;
    }

    private boolean _changed;

    private boolean _restoreLayout = true;

    /**
     * creates a new KisKisDialog
     * 
     * @see JDialog#JDialog(java.awt.Dialog, java.lang.String, boolean)
     */
    public KisKisDialog(final Dialog owner, final String title, final boolean modal) {
        super(owner, title, modal);
    }

    /**
     * @param restoreLayout
     *            true if the layout should be saved
     */
    public void setRestoreLayout(boolean restoreLayout) {
        _restoreLayout = restoreLayout;
    }

    /**
     * creates a new KisKisDialog
     * 
     * @param owner
     *            is the owner of the dialog.
     * @param title
     *            is the title to display.
     * @param modal
     *            should be modal or not
     */
    public KisKisDialog(final Frame owner, final String title, final boolean modal) {
        super(owner, title, modal);
    }

    /**
     * stores the window state and closes the window.
     */
    public final void close() {
        if (_restoreLayout) {
            Toolkit.saveWindowState(this);
        }

        this.setVisible(false);
        this.dispose();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.util.event.ContentListener#contentChanged(de.tbuchloh.util.event.ContentChangedEvent)
     */
    @Override
    public final void contentChanged(final ContentChangedEvent e) {
        setChanged(true);
    }

    /**
     * @return the button panel.
     */
    private final JPanel createButtons() {
        return createButtons(getActions());
    }

    /**
     * @return the main widget.
     */
    protected abstract Component createMainPanel();

    /**
     * @return the list of actions for the button-panel.
     */
    protected abstract List<Action> getActions();

    private final void initLayout() {
        final JPanel main = new JPanel(new BorderLayout());
        main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        main.add(createMainPanel(), BorderLayout.CENTER);
        main.add(createButtons(), BorderLayout.SOUTH);
        this.setContentPane(main);

        if (_restoreLayout) {
            Toolkit.restoreWindowState(getOwner(), this);
        } else {
            this.pack();
            Toolkit.centerWindow(getOwner(), this);
        }
    }

    /**
     * @return Returns the changed.
     */
    protected boolean isChanged() {
        return _changed;
    }

    /**
     * @param changed
     *            The changed to set.
     */
    protected void setChanged(final boolean changed) {
        _changed = changed;
    }

    /**
     * Overridden!
     * 
     * @see java.awt.Component#setVisible(boolean)
     */
    @Override
    public void setVisible(final boolean b) {
        if (b) {
            initLayout();
        }
        super.setVisible(b);
    }
}
