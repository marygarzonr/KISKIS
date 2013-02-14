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
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.util.swing.tree.SpecialNode;

/**
 * <b>MyTreeNode</b>:
 * 
 * @author gandalf
 * @version $Id: MyTreeNode.java,v 1.7 2007/02/18 14:37:52 tbuchloh Exp $
 */
public abstract class MyTreeNode implements SpecialNode, MutableTreeNode {
    private boolean _isSearchResult;

    /**
     * Log4J Logger for this class
     */
    // private static final Log LOG = LogFactory.getLog(MyTreeNode.class);

    private ModelNode _node;

    private MyTreeNode _parent;

    /**
     * creates a new MyTreeNode
     */
    public MyTreeNode() {
	// does nothing
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.TreeNode#children()
     */
    @Override
    public Enumeration<MyTreeNode> children() {
	return new Vector<MyTreeNode>().elements();
    }

    /**
     * Overridden!
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
	return this == obj;
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    @Override
    public boolean getAllowsChildren() {
	return false;
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.TreeNode#getChildAt(int)
     */
    @Override
    public TreeNode getChildAt(final int childIndex) {
	return null;
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.TreeNode#getChildCount()
     */
    @Override
    public int getChildCount() {
	return 0;
    }

    /**
     * @return the default font for the tree view.
     */
    protected abstract Font getDefaultFont();

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.util.swing.tree.SpecialNode#getFont()
     */
    @Override
    public final Font getFont() {
	final Font def = getDefaultFont();
	if (isSearchResult() || isArchived()) {
	    return def.deriveFont(Font.ITALIC | def.getStyle());
	}
	return def;
    }

    /**
     * @return {@link ModelNode#isArchived()}
     */
    private boolean isArchived() {
	return getModelNode().isArchived();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.util.swing.tree.SpecialNode#getForeground()
     */
    @Override
    public final Color getForeground() {
	if (isSearchResult()) {
	    return NodeFonts.SEARCH_RESULT_TEXT_COLOR;
	}
	if (isArchived()) {
	    return NodeFonts.ARCHIVED_TEXT_COLOR;
	}
	return NodeFonts.DEF_TEXT_COLOR;
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     */
    @Override
    public int getIndex(final TreeNode node) {
	return -1;
    }

    /**
     * @return the corresponding model object.
     */
    public final ModelNode getModelNode() {
	return _node;
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.TreeNode#getParent()
     */
    @Override
    public final TreeNode getParent() {
	return _parent;
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.DefaultMutableTreeNode#getUserObject()
     */
    public final Object getUserObject() {
	return _node.getName();
    }

    /**
     * Overridden!
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	return super.hashCode();
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.MutableTreeNode#insert(javax.swing.tree.MutableTreeNode,
     *      int)
     */
    @Override
    public void insert(final MutableTreeNode child, final int index) {
	// does nothing
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.TreeNode#isLeaf()
     */
    @Override
    public boolean isLeaf() {
	return true;
    }

    /**
     * @return Returns the isSearchResult.
     */
    public final boolean isSearchResult() {
	return _isSearchResult;
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.MutableTreeNode#remove(int)
     */
    @Override
    public void remove(final int index) {
	// does nothing
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.MutableTreeNode#remove(javax.swing.tree.MutableTreeNode)
     */
    @Override
    public void remove(final MutableTreeNode node) {
	// does nothing
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.MutableTreeNode#removeFromParent()
     */
    @Override
    public final void removeFromParent() {
	_parent.remove(this);
    }

    /**
     * traverses the tree and set the search result flag to false.
     */
    public void resetSearchResult() {
	setSearchResult(false);
    }

    /**
     * @param n
     *            the node to model.
     */
    public void setModelNode(final ModelNode n) {
	_node = n;
	setUserObject(n.getName());
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.MutableTreeNode#setParent(javax.swing.tree.MutableTreeNode)
     */
    @Override
    public final void setParent(final MutableTreeNode newParent) {
	_parent = (MyTreeNode) newParent;
    }

    /**
     * @param isResult
     *            true, if the node is in a search result.
     */
    public void setSearchResult(final boolean isResult) {
	_isSearchResult = isResult;
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.tree.MutableTreeNode#setUserObject(java.lang.Object)
     */
    @Override
    public final void setUserObject(final Object userObject) {
	if (userObject instanceof String) {
	    _node.setName((String) userObject);
	} else {
	    assert false;
	}
    }

    /**
     * Overridden!
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return getModelNode().getName();
    }

}
