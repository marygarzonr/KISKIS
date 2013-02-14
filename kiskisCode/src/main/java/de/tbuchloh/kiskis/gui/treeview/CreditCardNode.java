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

package de.tbuchloh.kiskis.gui.treeview;

import java.awt.Font;

import javax.swing.ImageIcon;

import de.tbuchloh.util.swing.Toolkit;

/**
 * <b>NetAccountNode</b>:
 * 
 * @author gandalf
 * @version $Id: CreditCardNode.java,v 1.4 2007/02/18 14:37:52 tbuchloh Exp $
 */
public final class CreditCardNode extends MyTreeNode {

	private static final ImageIcon IMAGE = Toolkit.loadImageIcon(
			"credit_card_node.png", CreditCardNode.class); //$NON-NLS-1$

	/**
	 * creates a new CreditCardNode
	 */
	protected CreditCardNode() {
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.treeview.MyTreeNode#getDefaultFont()
	 */
	@Override
	protected Font getDefaultFont() {
		return NodeFonts.ITEM;
	}

	/**
	 * @see de.tbuchloh.util.swing.tree.SpecialNode#getDefaultIcon()
	 */
	public ImageIcon getDefaultIcon() {
		return IMAGE;
	}
}
