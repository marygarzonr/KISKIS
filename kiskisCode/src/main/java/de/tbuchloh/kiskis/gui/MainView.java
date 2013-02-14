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

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.kiskis.gui.common.SearchListener;
import de.tbuchloh.kiskis.gui.dialogs.SecuredElementCreationDlg;
import de.tbuchloh.kiskis.gui.treeview.GroupNode;
import de.tbuchloh.kiskis.gui.treeview.MyTreeNode;
import de.tbuchloh.kiskis.gui.treeview.NodeFactory;
import de.tbuchloh.kiskis.gui.treeview.TreeView;
import de.tbuchloh.kiskis.gui.widgets.PersistentSplitPane;
import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.SecuredElement;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.swing.actions.ActionItem;

/**
 * <b>MainView</b>:
 * 
 * @author gandalf
 * @version $Id: MainView.java,v 1.29 2007/02/18 17:20:11 tbuchloh Exp $
 */
class MainView extends JComponent implements TreeSelectionListener {

    /**
     * @author Tobias Buchloh (gandalf)
     * @since 31.10.2010
     */
    private final class TreeDragAndDropListener implements DropTargetListener, DragSourceListener, DragGestureListener {

        private final DragSource _dragSource;

        /**
         * @return {@link #dragSource}
         */
        public DragSource getDragSource() {
            return _dragSource;
        }

        private DragSourceContext _dragSourceContext;
        private TreePath _dragPath;

        /**
         * Standardkonstruktor
         */
        public TreeDragAndDropListener() {
            _dragSource = DragSource.getDefaultDragSource();
        }

        /**
         * Overridden!
         * 
         * @see java.awt.dnd.DragSourceListener#dragDropEnd(java.awt.dnd.DragSourceDropEvent)
         */
        @Override
        public void dragDropEnd(final DragSourceDropEvent dsde) {
            // does nothing
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Entering - dragDropEnd(dsde)=%1$s", //
                        Arrays.asList(dsde)));
            }
            _dragPath = null;
        }

        /**
         * Overridden!
         * 
         * @see java.awt.dnd.DragSourceListener#dragEnter(java.awt.dnd.DragSourceDragEvent)
         */
        @Override
        public void dragEnter(final DragSourceDragEvent dsde) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Entering - dragEnter(dsde)=%1$s", //
                        Arrays.asList(dsde)));
            }

            LOG.debug("Setting _dragSourceContext=" + dsde.getDragSourceContext());
            _dragSourceContext = dsde.getDragSourceContext();
        }

        /**
         * Overridden!
         * 
         * @see java.awt.dnd.DragSourceListener#dragExit(java.awt.dnd.DragSourceEvent)
         */
        @Override
        public void dragExit(final DragSourceEvent dse) {
            // does nothing
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Entering - dragExit(dse)=%1$s", //
                        Arrays.asList(dse)));
            }
        }

        /**
         * Overridden!
         * 
         * @see java.awt.dnd.DragSourceListener#dragOver(java.awt.dnd.DragSourceDragEvent)
         */
        @Override
        public void dragOver(final DragSourceDragEvent e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("dragOver(dsde) entered");
            }

            LOG.debug("Setting _dragSourceContext=" + e.getDragSourceContext());
            _dragSourceContext = e.getDragSourceContext();
        }

        /**
         * Overridden!
         * 
         * @see java.awt.dnd.DragSourceListener#dropActionChanged(java.awt.dnd.DragSourceDragEvent)
         */
        @Override
        public void dropActionChanged(final DragSourceDragEvent dsde) {
            // does nothing
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Entering - dropActionChanged(dsde)=%1$s", //
                        Arrays.asList(dsde)));
            }
        }

        /**
         * Overridden!
         * 
         * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
         */
        @Override
        public void dragGestureRecognized(final DragGestureEvent e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Entering - dragGestureRecognized(e)=%1$s", //
                        Arrays.asList(e)));
            }
            _dragPath = _tree.getPathForLocation(e.getDragOrigin().x, e.getDragOrigin().y);

            // e.startDrag(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR), new StringSelection("dummy"));
        }

        /**
         * Overridden!
         * 
         * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dragEnter(final DropTargetDragEvent dtde) {
            // does nothing
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Entering - dragEnter(dtde)=%1$s", //
                        Arrays.asList(dtde)));
            }
        }

        /**
         * Overridden!
         * 
         * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
         */
        @Override
        public void dragExit(final DropTargetEvent dte) {
            // does nothing
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Entering - dragExit(dte)=%1$s", //
                        Arrays.asList(dte)));
            }
        }

        /**
         * Overridden!
         * 
         * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dragOver(final DropTargetDragEvent e) {
            // set cursor location. Needed in setCursor method
            final Point cursorLocationBis = e.getLocation();
            final TreePath destinationPath = _tree.getPathForLocation(cursorLocationBis.x, cursorLocationBis.y);

            // if destination path is okay accept drop...
            if (testDropTarget(destinationPath, _dragPath) == null) {
                LOG.debug("dragOver: drop would be accepted, copy=" //
                        + ((e.getSourceActions() & DnDConstants.ACTION_COPY) > 0) //
                        + ", move=" //
                        + ((e.getSourceActions() & DnDConstants.ACTION_MOVE) > 0));
                if (_dragSourceContext != null) {
                    _dragSourceContext.setCursor(DragSource.DefaultMoveDrop);
                }
                // e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
                e.acceptDrag(e.getDropAction());
            }
            // ...otherwise reject drop
            else {
                LOG.debug("dragOver: drop would be rejected");
                if (_dragSourceContext != null) {
                    _dragSourceContext.setCursor(DragSource.DefaultMoveNoDrop);
                }
                e.rejectDrag();
            }

        }

        /**
         * Overridden!
         * 
         * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
         */
        @Override
        public void dropActionChanged(final DropTargetDragEvent dtde) {
            // does nothing
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Entering - dropActionChanged(dtde)=%1$s", //
                        Arrays.asList(dtde)));
            }
        }

        /**
         * Overridden!
         * 
         * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
         */
        @Override
        public void drop(final DropTargetDropEvent e) {
            LOG.debug("drop e=" + e);
            // get new parent node
            final Point loc = e.getLocation();
            final TreePath destinationPath = _tree.getPathForLocation(loc.x, loc.y);

            final String msg = testDropTarget(destinationPath, _dragPath);
            if (msg != null) {
                e.rejectDrop();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(MainView.this, msg, "Error Dialog", JOptionPane.ERROR_MESSAGE);
                    }
                });
                return;
            }

            final GroupNode newParent = (GroupNode) destinationPath.getLastPathComponent();

            try {
                LOG.debug("dropAction=" + e.getDropAction());

                e.acceptDrop(e.getDropAction());

                final MyTreeNode draggedNode = (MyTreeNode) _dragPath.getLastPathComponent();

                final MyTreeNode oldParent = (MyTreeNode) draggedNode.getParent();

                LOG.debug("moving object=" + draggedNode);

                // Change model
                ((Group) newParent.getModelNode()).add(draggedNode.getModelNode());

                // Change tree items
                draggedNode.removeFromParent();
                newParent.add(draggedNode);

                _tree.reload(oldParent);
                _tree.reload(newParent);

                selectNode(draggedNode);

                e.dropComplete(true);
            } catch (final java.lang.IllegalStateException ils) {
                ils.printStackTrace();
                e.rejectDrop();
            }
        }

        // /**
        // * Overridden!
        // *
        // * @see
        // java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
        // */
        // @Override
        // public void drop(final DropTargetDropEvent e) {
        // LOG.debug("drop e=" + e);
        // // get new parent node
        // final Point loc = e.getLocation();
        // final TreePath destinationPath = _tree.getPathForLocation(loc.x,
        // loc.y);
        //
        // final String msg = testDropTarget(destinationPath, _selectedPath);
        // if (msg != null) {
        // e.rejectDrop();
        //
        // SwingUtilities.invokeLater(new Runnable() {
        // @Override
        // public void run() {
        // JOptionPane.showMessageDialog(MainView.this, msg,
        // "Error Dialog", JOptionPane.ERROR_MESSAGE);
        // }
        // });
        // return;
        // }
        //
        // final GroupNode newParent = (GroupNode) destinationPath
        // .getLastPathComponent();
        //
        // // final int action = e.getDropAction();
        // final boolean copyAction = false; // ((action &
        // // DnDConstants.ACTION_COPY) != 0);
        //
        // // make new child node
        //
        // try {
        // LOG.debug("dropAction=" + e.getDropAction());
        // if (!copyAction) {
        // LOG.debug("moving object=" + e.getSource());
        // cutNode();
        // } else {
        // LOG.debug("copy object=" + e.getSource());
        // copyNode();
        // }
        //
        // _lastSelected = newParent;
        //
        // pasteNode();
        //
        // if (copyAction) {
        // e.acceptDrop(DnDConstants.ACTION_COPY);
        // } else {
        // e.acceptDrop(DnDConstants.ACTION_MOVE);
        // }
        // } catch (final java.lang.IllegalStateException ils) {
        // e.rejectDrop();
        // }
        //
        // e.getDropTargetContext().dropComplete(true);
        //
        // }

        private String testDropTarget(final TreePath destination, final TreePath dropper) {
            if ((destination == null)) {
                return "Invalid drop location.";
            }

            if (dropper == null) {
                return "Invalid dropper location.";
            }

            // Test 2.
            final MyTreeNode node = (MyTreeNode) destination.getLastPathComponent();
            if (!node.getAllowsChildren()) {
                return "This node does not allow children";
            }

            if (destination.equals(dropper)) {
                return "Destination cannot be same as source";
            }

            // Test 3.
            if (dropper.isDescendant(destination)) {
                return "Destination node cannot be a descendant.";
            }

            // Test 4.
            if (dropper.getParentPath().equals(destination)) {
                return "Destination node cannot be a parent.";
            }

            return null;
        }

        /**
         * @param tree
         *            the tree
         */
        public void setTree(TreeView tree) {
            _dragSource.addDragSourceListener(this);

            final DragGestureRecognizer dgr = _dragSource.createDefaultDragGestureRecognizer(tree,
                    DnDConstants.ACTION_MOVE, this);

            /*
             * Eliminates right mouse clicks as valid actions - useful especially if you implement a JPopupMenu for the
             * JTree
             */
            dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);

            /*
             * First argument: Component to associate the target with Second argument: DropTargetListener
             */
            final DropTarget dropTarget = new DropTarget(tree, this);
            tree.setDropTarget(dropTarget);
        }

    }

    private final class SearchBox extends JComponent {

        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = -3376454105017902252L;

        private final Action _clearAction;

        JTextField _searchField;

        SearchListener _searchListener;

        public SearchBox(final SearchListener sl) {
            class SearchKeyListener extends KeyAdapter {
                @Override
                public void keyReleased(final KeyEvent e) {
                    search();
                }
            }
            class ClearAction extends ActionItem {
                /**
                 * Comment for <code>serialVersionUID</code>
                 */
                private static final long serialVersionUID = 1L;

                public ClearAction() {
                    super("", MainFrame.loadImage("clear.png"));
                }

                @Override
                public void actionPerformed(final ActionEvent e) {
                    reset();
                }
            }
            class SearchFocusListener extends FocusAdapter {
                @Override
                public void focusGained(final FocusEvent e) {
                    if (_searchField.getText().equals(SEARCH)) {
                        _searchField.setText("");
                    }
                }

                @Override
                public void focusLost(final FocusEvent e) {
                    if (_searchField.getText().equals("")) {
                        _searchField.setText(SEARCH);
                    }
                }
            }

            _searchListener = sl;
            this.setLayout(new BorderLayout(5, 0));
            this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            _searchField = new JTextField(SEARCH);
            _searchField.addKeyListener(new SearchKeyListener());
            _searchField.addFocusListener(new SearchFocusListener());
            _clearAction = new ClearAction();

            this.add(_searchField);
            this.add(new JButton(_clearAction), BorderLayout.EAST);
        }

        protected void reset() {
            _searchField.setText("");
            _searchListener.resetSearch();
            refreshView();
            _searchField.grabFocus();
        }

        protected void search() {
            final Runnable run = new Runnable() {
                @Override
                public void run() {
                    final String text = _searchField.getText();
                    if (text.length() == 0) {
                        reset();
                        return;
                    }

                    final Pattern query = SearchWorker.createKeywordQuery(text);
                    LOG.debug("Searching query=" + query);

                    final List<ModelNode> nodes = _doc.getGroups().findAll(query);

                    LOG.debug("found nodes=" + nodes);
                    _searchListener.found(nodes);
                    _searchField.grabFocus();
                }
            };
            SwingUtilities.invokeLater(run);
        }

    }

    static final Log LOG = LogFactory.getLog(MainView.class);

    private static final MessageFormat MSG_CONFIRM_DELETE = new MessageFormat(
            Messages.getString("MainView.confirmDelete")); //$NON-NLS-1$

    private static final String MSG_DEL_ROOT = Messages.getString("MainView.MSG_DEL_ROOT"); //$NON-NLS-1$

    protected static final String SEARCH = Messages.getString("SearchDialog.title");

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 4295935344589425129L;

    private MyTreeNode _clipboard;

    private DetailView _detailView;

    private TPMDocument _doc;

    private MyTreeNode _lastSelected;

    protected JSplitPane _main;

    private SearchBox _searchBox;

    private TreePath _selectedPath;

    private TreeView _tree;

    public MainView(final JPopupMenu treeContext) {
        _clipboard = null;
        _main = new PersistentSplitPane();

        _detailView = new DetailView(null, new Group());

        createTree(treeContext);

        this.setLayout(new BorderLayout());
        this.add(_main);
    }

    /**
     * copy the current node to clipboard.
     */
    public void copyNode() {
        _clipboard = NodeFactory.create(_lastSelected.getModelNode().clone());

        if (_clipboard instanceof GroupNode) {
            _tree.expandTreeNodes((GroupNode) _clipboard);
        }
    }

    private void createDetailView() {
        if (_detailView.isModified()) {
            if (MessageBox.showConfirmDialog(Messages.getString("MainView.msgConfirmChanges"))) //$NON-NLS-1$
            {
                _detailView.storeValues();
            } else {
                _detailView.revertChanges();
            }
        }
        _main.remove(_detailView);
        _detailView = new DetailView(_doc, _lastSelected.getModelNode());
        _tree.setItemRenamer(_detailView);
        _main.add(_detailView, JSplitPane.RIGHT);
    }

    /**
     * creates a new Group Node.
     */
    public void createNewGroupNode() {
        if (_lastSelected == null) {
            return;
        }

        final GroupNode parent = getSelectedGroupNode();
        final Group newGroup = new Group();
        newGroup.setName(Messages.getString("MainView.unnamedNode"));
        parent.getGroup().add(newGroup);
        final MyTreeNode newGroupNode = NodeFactory.create(newGroup);
        parent.add(newGroupNode);
        _tree.reload(parent);
        selectNode(newGroupNode);
        _detailView.grabFocus();
    }

    public void createNewNode() {
        if (_lastSelected == null) {
            return;
        }
        final int daysToExpire = Settings.getDefaultPwdExpiryDays();
        final SecuredElement el = SecuredElementCreationDlg.newInstance(MainFrame.getInstance(), _doc, daysToExpire);
        LOG.debug(el + " created!"); //$NON-NLS-1$
        if (el != null) {
            el.setName(Messages.getString("MainView.unnamedNode"));
            final GroupNode parent = getSelectedGroupNode();
            parent.getGroup().add(el);
            final MyTreeNode node = NodeFactory.create(el);
            parent.add(node);
            _tree.reload(parent);
            selectNode(node);
            _detailView.grabFocus();
        }
    }

    private void createTree(final JPopupMenu treeContext) {
        final JPanel panel = new JPanel(new BorderLayout());

        _tree = new TreeView(treeContext);
        _tree.addTreeSelectionListener(this);

        final TreeDragAndDropListener dsl = new TreeDragAndDropListener();
        dsl.setTree(_tree);

        _searchBox = new SearchBox(_tree);
        panel.add(new JScrollPane(_tree));
        panel.add(_searchBox, BorderLayout.NORTH);

        _main.add(panel, JSplitPane.LEFT);
    }

    /**
     * copy the current node to clipboard and remove it from tree.
     */
    public void cutNode() {
        _clipboard = _lastSelected;
        deleteNode(false);
    }

    /**
     * delete the current selected node item.
     */
    public void deleteNode() {
        deleteNode(true);
    }

    /**
     * delete the current selected node item.
     */
    private void deleteNode(final boolean confirm) {
        if (_lastSelected != null) {
            final GroupNode parent = (GroupNode) _lastSelected.getParent();
            if (parent == null) {
                MessageBox.showMessageDialog(this, MSG_DEL_ROOT);
                return;
            }

            final Group g = parent.getGroup();
            LOG.debug("current group is " + g); //$NON-NLS-1$
            final Object[] p = {
                    _lastSelected.getModelNode().getName()
            };
            if (!confirm || MessageBox.showConfirmDialog(MSG_CONFIRM_DELETE.format(p))) {
                g.remove(_lastSelected.getModelNode());

                LOG.debug("removing " + _lastSelected); //$NON-NLS-1$
                _lastSelected.removeFromParent();

                _tree.reload(parent);

                selectNode(parent);
            }
        }
    }

    /**
     * searches occurences of specified node attributes.
     */
    public void findNode() {
        final SearchDialog dlg = new SearchDialog(_tree, _doc.getGroups());
        dlg.setVisible(true);
    }

    private GroupNode getSelectedGroupNode() {
        if (_lastSelected instanceof GroupNode) {
            return (GroupNode) _lastSelected;
        }
        return (GroupNode) ((MutableTreeNode) _lastSelected.getParent());
    }

    /**
     * insert the current clipboard node to the tree branch.
     */
    public void pasteNode() {
        if (_clipboard != null && _lastSelected != null) {
            final GroupNode parent = getSelectedGroupNode();

            final ModelNode copy = _clipboard.getModelNode();
            parent.getGroup().add(copy);

            final MyTreeNode copyNode = _clipboard;
            parent.add(copyNode);

            _tree.display(parent);

            selectNode(copyNode);
        }
    }

    /**
     * Repaints the treeview
     */
    public void refreshView() {
        ModelNode lastSelected = null;
        if (_lastSelected != null) {
            lastSelected = _lastSelected.getModelNode();
            _lastSelected = null;
        }

        _tree.setRootNode((GroupNode) NodeFactory.create(_doc.getGroups()));

        if (lastSelected != null) {
            _tree.found(lastSelected);
        }
    }

    /**
     * renames the current node.
     */
    public void renameNode() {
        _tree.startEditingAtPath(_tree.getSelectionPath());
    }

    private void selectNode(final MyTreeNode node) {
        _tree.display(node);
        _lastSelected = node;
    }

    /**
     * @param root
     *            is the document to display.
     */
    public void setRootNode(final TPMDocument doc) {
        _doc = doc;
        final GroupNode root = (GroupNode) NodeFactory.create(doc.getGroups());
        _tree.setRootNode(root);
        selectNode(root);
        if (Settings.isDisplayLastViewedElement()) {
            final SecuredElement found = doc.findLastViewedElement();
            if (found != null) {
                showNode(found);
            }
        }
        _tree.grabFocus();
    }

    /**
     * @param el
     *            is the element to select.
     */
    public void showNode(final SecuredElement el) {
        _tree.showNode(el);
    }

    public void updateTree() {
        _tree.display((MyTreeNode) _tree.getLastSelectedPathComponent());
    }

    /**
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        _selectedPath = e.getPath();
        if (_selectedPath != null && _lastSelected != _selectedPath.getLastPathComponent()) {
            _lastSelected = (MyTreeNode) _selectedPath.getLastPathComponent();
            createDetailView();
        }
        LOG.debug(_lastSelected + " selected."); //$NON-NLS-1$
    }

}
