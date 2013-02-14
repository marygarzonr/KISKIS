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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.common.SearchListener;
import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.SecuredElement;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.swing.tree.SpecialNodeCellRenderer;

/**
 * <b>TreeView</b>:
 * 
 * @author gandalf
 * @version $Id: TreeView.java,v 1.13 2007/02/18 14:37:51 tbuchloh Exp $
 */
public final class TreeView extends JTree implements SearchListener,
PropertyChangeListener {

    /**
     * 
     * @author Tobias Buchloh (gandalf)
     * @version $Id: $
     * @since 22.10.2010
     */
    private final class TreeNodeFilter implements ITreeNodeFilter {

        private Set<ModelNode> _searchResult = new HashSet<ModelNode>();

        private boolean _searchFilterEnabled = false;

        @Override
        public boolean isDisplayed(ModelNode node) {
            if (_searchFilterEnabled) {
                if (_searchResult.contains(node)) {
                    return true;
                } else if (node instanceof Group) {
                    final Group group = (Group) node;
                    for (final ModelNode n : group.depthFirst()) {
                        if (_searchResult.contains(n)) {
                            return true;
                        }
                    }
                }
                return false;
            }
            return Settings.isShowingArchivedItems() || !node.isArchived();
        }

        /**
         * @param _searchResult
         *            {@link _searchResult}
         */
        public void enableSearchFilter(Collection<ModelNode> searchResult) {
            this._searchFilterEnabled = true;
            this._searchResult = new HashSet<ModelNode>(searchResult);
        }

        /**
         * Clears the search result
         */
        public void disableSearchFilter() {
            _searchFilterEnabled = false;
            _searchResult.clear();
        }
    }

    private final class NameCellEditor extends DefaultCellEditor {

        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 1L;

        /**
         * creates a new TreeView.NameCellEditor
         */
        public NameCellEditor() {
            super(new JTextField());
        }

        /**
         * Overridden!
         * 
         * @see javax.swing.DefaultCellEditor#getClickCountToStart()
         */
        @Override
        public int getClickCountToStart() {
            return 1;
        }

        /**
         * Overridden!
         * 
         * @see javax.swing.DefaultCellEditor#getTreeCellEditorComponent(javax.swing.JTree,
         *      java.lang.Object, boolean, boolean, boolean, int)
         */
        @Override
        public Component getTreeCellEditorComponent(final JTree tree,
                final Object value, final boolean isSelected,
                final boolean expanded, final boolean leaf, final int row) {
            LOG.debug("editing startet ...");
            final JTextField textField = (JTextField) super
            .getTreeCellEditorComponent(tree, value, isSelected,
                    expanded, leaf, row);
            final MyTreeNode node = (MyTreeNode) value;
            final String nameVal = node.getModelNode().getName();
            assert getItemRenamer().getNameField().equals(nameVal);
            textField.setText(nameVal);
            return textField;
        }

        /**
         * Overridden!
         * 
         * @see javax.swing.DefaultCellEditor#stopCellEditing()
         */
        @Override
        public boolean stopCellEditing() {
            LOG.debug("editing stopped!");
            final boolean ret = super.stopCellEditing();
            if (ret) {
                final TreePath p = getSelectionPath();
                if (p != null) {
                    final MyTreeNode n = (MyTreeNode) p.getLastPathComponent();
                    getItemRenamer().setNameField(n.getModelNode().getName());
                }
            }
            return ret;
        }
    }

    protected static final Log LOG = LogFactory.getLog(TreeView.class);

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    private ItemRenamer _itemRenamer;

    private final DefaultTreeModel _treeModel;

    /**
     * Test whether specific nodes should be displayed or hidden
     */
    private final TreeNodeFilter _treeNodeFilter = new TreeNodeFilter();

    /**
     * @param treeContext
     */
    public TreeView(final JPopupMenu treeContext) {
        final GroupNode root = new GroupNode();
        root.setModelNode(new Group());
        _treeModel = new DefaultTreeModel(root);
        setModel(_treeModel);

        this.setEditable(true);
        this.setCellRenderer(new SpecialNodeCellRenderer());
        this.setCellEditor(new NameCellEditor());
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    LOG.debug("Context Menu invoked!"); //$NON-NLS-1$
                    treeContext.show(TreeView.this, e.getX(), e.getY());
                }
            }
        });
        setDragEnabled(true);
        setAutoscrolls(true);
    }

    private void collapseAll() {
        final MyTreeNode root = NodeFactory.create(getRoot().getModelNode());
        _treeModel.setRoot(null);
        _treeModel.reload();
        _treeModel.setRoot(root);
        _treeModel.reload();
    }

    /**
     * @param node
     *            to make visible
     */
    public void display(final MyTreeNode node) {
        LOG.debug("displaying node=" + node);
        final TreeNode[] path = _treeModel.getPathToRoot(node);
        _treeModel.reload(node);
        if (path != null) {
            final TreePath treePath = new TreePath(path);
            this.makeVisible(treePath);
            this.setSelectionPath(treePath);
        }
    }

    /**
     * @param groupNode
     *            the group node to expand
     */
    public void expandTreeNodes(GroupNode groupNode) {
        final ModelNode modelNode = groupNode.getModelNode();
        for (final ModelNode child : ((Group) modelNode).getChildren()) {
            if (_treeNodeFilter.isDisplayed(child)) {
                final MyTreeNode newNode = NodeFactory.create(child);
                groupNode.add(newNode);
                if (newNode instanceof GroupNode) {
                    expandTreeNodes((GroupNode) newNode);
                }
            }
        }
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.common.SearchListener#found(java.util.List)
     */
    @Override
    public void found(final List<ModelNode> nodes) {
        LOG.debug("found nodes.size()=" + nodes.size());
        if (nodes.isEmpty()) {
            resetSearch();
            return;
        }

        collapseAll();

        boolean first = true;
        final Set<ModelNode> nodesSet = new HashSet<ModelNode>(nodes);
        _treeNodeFilter.enableSearchFilter(nodes);

        expandTreeNodes(getRoot());

        for (final MyTreeNode node : getAllNodes()) {
            if (nodesSet.contains(node.getModelNode())) {
                node.setSearchResult(true);
                LOG.debug("displaying node=" + node);
                final TreeNode[] path = _treeModel.getPathToRoot(node);
                if (path != null) {
                    final TreePath treePath = new TreePath(path);
                    this.makeVisible(treePath);

                    if (first) {
                        this.setSelectionPath(treePath);
                        first = false;
                    }
                }
            } else {
                node.setSearchResult(false);
            }
        }

        _treeNodeFilter.disableSearchFilter();
    }

    /**
     * @return gets all the displayed tree nodes
     */
    protected Collection<MyTreeNode> getAllNodes() {
        final Queue<MyTreeNode> treeNodes = new LinkedList<MyTreeNode>();
        treeNodes.add(getRoot());
        final Collection<MyTreeNode> r = new LinkedList<MyTreeNode>();
        r.add(getRoot());
        while (!treeNodes.isEmpty()) {
            final MyTreeNode node = treeNodes.poll();
            final List<MyTreeNode> children = Collections.list(node.children());
            treeNodes.addAll(children);
            r.addAll(children);
        }
        return r;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.common.SearchListener#found(de.tbuchloh.kiskis.model.ModelNode)
     */
    @Override
    public void found(final ModelNode node) {
        display(getTreeNode(node));
    }

    /**
     * @param node
     *            the model node to look up
     * @return the corresponding tree node
     */
    private MyTreeNode getTreeNode(ModelNode node) {
        for (final MyTreeNode tn : getAllNodes()) {
            if (tn.getModelNode().equals(node)) {
                return tn;
            }
        }
        return null;
    }

    /**
     * @return Returns the itemRenamer.
     */
    protected final ItemRenamer getItemRenamer() {
        return _itemRenamer;
    }

    private GroupNode getRoot() {
        return (GroupNode) _treeModel.getRoot();
    }

    /**
     * @see DefaultTreeModel#nodeChanged(javax.swing.tree.MyTreeNode)
     */
    public void nodeChanged(final MyTreeNode node) {
        _treeModel.nodeChanged(node);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final Object src = evt.getSource();
        LOG.debug("TreeView - property changed object=" + src);
        if (src instanceof MyTreeNode) {
            // We have to force repaint with selecting another node and
            // reselecting the current one

            final MyTreeNode n = (MyTreeNode) src;
            display(getRoot());
            display(n);
            invalidate();
            repaint();
        }
    }

    /**
     * @see DefaultTreeModel#reload(javax.swing.tree.MyTreeNode)
     */
    public void reload(final MyTreeNode node) {
        _treeModel.reload(node);
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.common.SearchListener#resetSearch()
     */
    @Override
    public void resetSearch() {
        collapseAll();
        expandTreeNodes(getRoot());
        _treeModel.reload();
    }

    /**
     * @param itemRenamer
     *            The itemRenamer to set.
     */
    public final void setItemRenamer(final ItemRenamer itemRenamer) {
        _itemRenamer = itemRenamer;
    }

    /**
     * @param node
     *            is the new top of the tree
     */
    public void setRootNode(final GroupNode node) {
        expandTreeNodes(node);
        _treeModel.setRoot(node);
        _treeModel.reload();
    }

    /**
     * @param el
     *            is the node to show.
     */
    public void showNode(final SecuredElement el) {
        found(el);
    }

}
