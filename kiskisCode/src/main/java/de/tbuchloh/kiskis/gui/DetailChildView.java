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

import java.util.Collection;

import javax.swing.Action;

import de.tbuchloh.kiskis.gui.treeview.ItemRenamer;
import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.SecuredElement;
import de.tbuchloh.kiskis.model.TPMDocument;

/**
 * <b>DetailChildView</b>:
 * 
 * @author gandalf
 * @version $Id: DetailChildView.java,v 1.4 2006/04/22 16:37:58 tbuchloh Exp $
 */
public abstract class DetailChildView extends SpecialView implements SpecialActions, ItemRenamer {

    private static final long serialVersionUID = 1L;

    /**
     * @param detailView
     *            TODO boooh, bidirectional association sucks!
     * @param doc
     *            is the associated document.
     * @param el
     *            is the model node to display.
     * @return the newly created view
     */
    public static DetailChildView create(IEditableView detailView, final TPMDocument doc, final ModelNode el) {
        DetailChildView detailChildView;
        if (el instanceof Group) {
            detailChildView = new GroupView((Group) el);
        } else {
            detailChildView = new AccountView(doc, (SecuredElement) el);
        }
        detailChildView.setRootView(detailView);
        return detailChildView;
    }

    /**
     * creates a new DetailChildView
     */
    public DetailChildView() {
        super();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.SpecialActions#getSpecialActions()
     */
    @Override
    public abstract Collection<Action> getSpecialActions();

}
