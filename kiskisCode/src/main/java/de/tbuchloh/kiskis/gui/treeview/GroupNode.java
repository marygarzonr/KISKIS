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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.util.swing.Toolkit;

/**
 * <b>GroupNode</b>:
 * 
 * @author gandalf
 * @version $Id: GroupNode.java,v 1.4 2007/02/18 14:37:52 tbuchloh Exp $
 */
public final class GroupNode extends MyTreeNode {
    private static final ImageIcon IMAGE = Toolkit.loadImageIcon(
	    "group_closed.png", GroupNode.class); //$NON-NLS-1$

    private final List<MyTreeNode> _childnodes;

    /**
     * creates a new GroupNode
     */
    protected GroupNode() {
	_childnodes = new ArrayList<MyTreeNode>();
    }

    /**
     * @param node
     *            is the node to add.
     */
    public final void add(final MyTreeNode node) {
	_childnodes.add(node);
	node.setParent(this);
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.TreeNode#children()
     */
    @Override
    public Enumeration<MyTreeNode> children() {
	return Collections.enumeration(_childnodes);
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    @Override
    public boolean getAllowsChildren() {
	return true;
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.TreeNode#getChildAt(int)
     */
    @Override
    public TreeNode getChildAt(final int childIndex) {
	return _childnodes.get(childIndex);
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.TreeNode#getChildCount()
     */
    @Override
    public int getChildCount() {
	return _childnodes.size();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.treeview.MyTreeNode#getDefaultFont()
     */
    @Override
    protected Font getDefaultFont() {
	return NodeFonts.GROUP;
    }

    /**
     * @see de.tbuchloh.util.swing.tree.SpecialNode#getDefaultIcon()
     */
    @Override
    public ImageIcon getDefaultIcon() {
	return IMAGE;
    }

    /**
     * @return the representing model object.
     */
    public Group getGroup() {
	return (Group) getModelNode();
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     */
    @Override
    public int getIndex(final TreeNode node) {
	return _childnodes.indexOf(node);
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.MutableTreeNode#insert(javax.swing.tree.MutableTreeNode,
     *      int)
     */
    @Override
    public void insert(final MutableTreeNode child, final int index) {
	_childnodes.add(index, (MyTreeNode) child);
	child.setParent(this);
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.DefaultMutableTreeNode#isLeaf()
     */
    @Override
    public boolean isLeaf() {
	return _childnodes.isEmpty();
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.MutableTreeNode#remove(int)
     */
    @Override
    public void remove(final int index) {
	final MyTreeNode n = _childnodes.remove(index);
	if (n != null) {
	    n.setParent(null);
	}
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.MutableTreeNode#remove(javax.swing.tree.MutableTreeNode)
     */
    @Override
    public void remove(final MutableTreeNode node) {
	if (_childnodes.remove(node)) {
	    node.setParent(null);
	}
    }

}
