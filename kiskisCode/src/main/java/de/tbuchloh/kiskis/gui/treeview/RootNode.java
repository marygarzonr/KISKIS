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

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

import de.tbuchloh.util.swing.Toolkit;
import de.tbuchloh.util.swing.tree.SpecialNode;

/**
 * <b>RootNode</b>: represents the document root node and displays the document
 * title.
 * 
 * @author gandalf
 * @version $Id: RootNode.java,v 1.3 2007/02/18 14:37:52 tbuchloh Exp $
 */
public final class RootNode extends DefaultMutableTreeNode implements
		SpecialNode {

	private static final ImageIcon IMAGE = Toolkit.loadImageIcon(
			"root_node.png", RootNode.class); //$NON-NLS-1$

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * creates a new root node
	 * 
	 * @param docTitle
	 *            is the text to display
	 */
	protected RootNode(final String docTitle) {
		super(docTitle);
	}

	/**
	 * @see de.tbuchloh.util.swing.tree.SpecialNode#getDefaultIcon()
	 */
	public ImageIcon getDefaultIcon() {
		return IMAGE;
	}

	/**
	 * @see de.tbuchloh.util.swing.tree.SpecialNode#getFont()
	 */
	public Font getFont() {
		return NodeFonts.ITEM;
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.util.swing.tree.SpecialNode#getForeground()
	 */
	public Color getForeground() {
		return NodeFonts.DEF_TEXT_COLOR;
	}
}
