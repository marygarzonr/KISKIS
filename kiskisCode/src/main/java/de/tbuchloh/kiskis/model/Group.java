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

package de.tbuchloh.kiskis.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import de.tbuchloh.kiskis.model.annotations.Observable;
import de.tbuchloh.kiskis.model.annotations.SecretInfo;

/**
 * <b>Group</b>: Groups accounts and other Groups together in a tree hierarchy.
 * 
 * @author gandalf
 * @version $Id: Group.java,v 1.11 2007/03/11 22:14:14 tbuchloh Exp $
 */
@Observable
public final class Group implements Cloneable, Comparable<Group>, ModelNode, Archivable {

    private static final long serialVersionUID = 1L;

    /**
     * the date when this item has been archived. Can be null.
     */
    private Calendar _archivedOnDate;

    @SecretInfo
    private String _comment;

    private final List<SecuredElement> _elements;

    private final List<Group> _groups;

    @SecretInfo
    private String _name;

    private transient ModelNode _parent;

    /**
     * Unique and persistent id
     */
    private String _uuid;

    /**
     * creates a new Group
     */
    public Group() {
        this("unnamed");
    }

    /**
     * @param name
     *            is the group identifier
     */
    public Group(final String name) {
        _uuid = IdGenerator.generate();
        _name = name;
        _comment = "";
        _groups = new ArrayList<Group>();
        _elements = new ArrayList<SecuredElement>();
    }

    /**
     * @param g
     *            the new group
     */
    private void add(final Group g) {
        assert g != null;

        _groups.add(g);
        Collections.sort(_groups);
    }

    /**
     * @param node
     *            is the new node to be added.
     */
    public void add(final ModelNode node) {
        assert node != null;
        if (node.getParent() != null) {
            ((Group) node.getParent()).remove(node);
        }

        if (node instanceof Group) {
            this.add((Group) node);
        } else {
            add((SecuredElement) node);
        }
        node.setParent(this);
    }

    /**
     * @param el
     *            the new Element
     */
    private void add(final SecuredElement el) {
        _elements.add(el);
        Collections.sort(_elements);
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Group clone() {
        final Group g = new Group(_name);
        g._comment = this._comment;

        if (this._archivedOnDate != null) {
            g._archivedOnDate = (Calendar) this._archivedOnDate.clone();
        }

        for (final Group el : _groups) {
            g.add(el.clone());
        }

        for (final SecuredElement el : _elements) {
            g.add(el.clone());
        }

        return g;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Group rhs) {
        return _name.toLowerCase().compareTo(rhs._name.toLowerCase());
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.ModelNode#contains(Pattern)
     */
    @Override
    public boolean contains(final Pattern regex) {
        return regex.matcher(_name).matches() || regex.matcher(_comment).matches();
    }

    public List<ModelNode> depthFirst() {
        final List<ModelNode> ret = new ArrayList<ModelNode>();

        for (final Object element : _groups) {
            ret.addAll(((Group) element).depthFirst());
        }

        ret.add(this);
        ret.addAll(_elements);
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Group)) {
            return false;
        }
        final Group other = (Group) obj;
        if (_uuid == null) {
            if (other._uuid != null) {
                return false;
            }
        } else if (!_uuid.equals(other._uuid)) {
            return false;
        }
        return true;
    }

    /**
     * @param startFrom
     *            is the child from which the search is started.
     * @param regex
     *            is the string to find.
     * @return the node which contains the text as a value.
     */
    public ModelNode find(final ModelNode startFrom, final Pattern regex) {
        ModelNode found = null;
        final List<ModelNode> preOrder = preOrder();
        final int pos = preOrder.indexOf(startFrom) + 1;
        if (pos > 0 && pos < preOrder.size()) {
            for (final Iterator<ModelNode> i = preOrder.listIterator(pos); i.hasNext();) {
                final ModelNode n = i.next();
                if (n.contains(regex)) {
                    found = n;
                    break;
                }
            }
        }
        return found;
    }

    /**
     * @see #find(ModelNode, Pattern)
     */
    public ModelNode find(final Pattern regex) {
        return this.find(null, regex);
    }

    /**
     * finds all nodes with the specific text.
     * 
     * @param regex
     *            is the regex to find.
     * @return a list with the results.
     */
    public List<ModelNode> findAll(final Pattern regex) {
        final List<ModelNode> allNodes = depthFirst();
        final List<ModelNode> result = new ArrayList<ModelNode>();
        if (!allNodes.isEmpty()) {
            for (final Object element : allNodes) {
                final ModelNode n = (ModelNode) element;
                if (n.contains(regex)) {
                    result.add(n);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * @return {@link #_archivedOnDate}
     */
    @Override
    public Calendar getArchivedOnDate() {
        return _archivedOnDate;
    }

    public Collection<ModelNode> getChildren() {
        final Collection<ModelNode> ret = new ArrayList<ModelNode>();
        ret.addAll(_groups);
        ret.addAll(_elements);
        return ret;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return _comment;
    }

    /**
     * @return the secured elements
     */
    public Collection<SecuredElement> getElements() {
        return _elements;
    }

    /**
     * @return the Groups
     */
    public Collection<Group> getGroups() {
        return _groups;
    }

    /**
     * @return the group identifier
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.ModelNode#getParent()
     */
    @Override
    public ModelNode getParent() {
        return _parent;
    }

    @Override
    public String getUuid() {
        return _uuid;
    }

    /**
     * @return true, if there are any elements in the group
     */
    public boolean hasChildren() {
        return !(_groups.isEmpty() && _elements.isEmpty());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_uuid == null) ? 0 : _uuid.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isArchivable() {
        return getParent() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isArchived() {
        return _archivedOnDate != null;
    }

    public boolean matches(final Group obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (!_name.equals(obj._name)) {
            return false;
        }
        if (!_comment.equals(obj._comment)) {
            return false;
        }
        if (_archivedOnDate == null) {
            if (obj._archivedOnDate != null) {
                return false;
            }
        } else if (!_archivedOnDate.equals(obj._archivedOnDate)) {
            return false;
        }
        for (int i = 0; i < _elements.size(); ++i) {
            final boolean r = _elements.get(i).matches(obj._elements.get(i));
            if (!r) {
                return false;
            }
        }

        for (int i = 0; i < _groups.size(); ++i) {
            final boolean r = _groups.get(i).matches(obj._groups.get(i));
            if (!r) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return self and descending childs of this node in preorder.
     */
    public List<ModelNode> preOrder() {
        final List<ModelNode> ret = new ArrayList<ModelNode>();
        ret.add(this);
        ret.addAll(_elements);

        for (final Object element : _groups) {
            ret.addAll(((Group) element).preOrder());
        }
        return ret;
    }

    /**
     * @param node
     * @return true, if the node was removed successfully.
     */
    public boolean remove(final ModelNode node) {
        assert node.getParent() == this;

        if (_groups.remove(node) || _elements.remove(node)) {
            node.setParent(null);
            return true;
        }
        return false;
    }

    /**
     * @param _archivedOnDate
     *            {@link _archivedOnDate}
     */
    @Override
    public void setArchivedOnDate(Calendar archiveDate) {
        this._archivedOnDate = archiveDate;
    }

    /**
     * @param comment
     *            the associated comment
     */
    public void setComment(final String comment) {
        _comment = comment;
    }

    /**
     * @param name
     *            is the identifier of this group
     */
    @Override
    public void setName(final String name) {
        _name = name;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.ModelNode#setParent(de.tbuchloh.kiskis.model.ModelNode)
     */
    @Override
    public void setParent(final ModelNode el) {
        _parent = el;
    }

    public void setUuid(final String id) {
        _uuid = id;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _name;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.ModelNode#visit(de.tbuchloh.kiskis.model.ModelNodeVisitor)
     */
    @Override
    public void visit(final ModelNodeVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @param visitor
     *            the action to use.
     */
    public void visitPreOrder(final ModelNodeVisitor visitor) {
        final List<ModelNode> ret = preOrder();
        for (final ModelNode node : ret) {
            node.visit(visitor);
        }
    }
}
