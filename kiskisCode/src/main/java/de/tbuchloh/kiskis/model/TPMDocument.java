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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.tbuchloh.kiskis.model.annotations.Observable;
import de.tbuchloh.kiskis.model.template.AccountType;
import de.tbuchloh.kiskis.model.template.ElementNotFoundException;

/**
 * <b>TPMDocument</b>:
 * 
 * @author gandalf
 * @version $Id: TPMDocument.java,v 1.24 2007/02/18 14:37:33 tbuchloh Exp $
 */
@Observable
public final class TPMDocument implements Serializable {

    /**
     * <b>ExpiredComparator</b>: compares SecureElements by Expired-Date-Criteria.
     * 
     * @author gandalf
     * @version $Id: TPMDocument.java,v 1.24 2007/02/18 14:37:33 tbuchloh Exp $
     */
    private static final class ExpiredComparator implements Comparator<SecuredElement> {

        @Override
        public int compare(final SecuredElement lhs, final SecuredElement rhs) {
            final Calendar lhsExp = lhs.getPwd().getExpires();
            final Calendar rhsExp = rhs.getPwd().getExpires();
            if (lhsExp.before(rhsExp)) {
                return -1;
            } else if (lhsExp.equals(rhsExp)) {
                return 0;
            }
            return 1;
        }

    }

    /**
     * <b>LVComparator</b>: compares SecureElements by LastViewed-Criteria.
     * 
     * @author gandalf
     * @version $Id: TPMDocument.java,v 1.24 2007/02/18 14:37:33 tbuchloh Exp $
     */
    private static final class LVComparator implements Comparator<SecuredElement> {

        /**
         * creates a new MRVComparator
         */
        public LVComparator() {
            super();
        }

        /**
         * Overridden!
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final SecuredElement lhs, final SecuredElement rhs) {
            if (lhs.getLastViewedDate().before(rhs.getLastViewedDate())) {
                return 1;
            } else if (lhs.getLastViewedDate().equals(rhs.getLastViewedDate())) {
                return 0;
            }
            return -1;
        }
    }

    /**
     * <b>MRVComparator</b>: compares SecureElements by MostRecentlyViewed-Criteria.
     * 
     * @author gandalf
     * @version $Id: TPMDocument.java,v 1.24 2007/02/18 14:37:33 tbuchloh Exp $
     */
    private static final class MRVComparator implements Comparator<SecuredElement> {

        /**
         * creates a new MRVComparator
         */
        public MRVComparator() {
            super();
        }

        /**
         * Overridden!
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final SecuredElement lhs, final SecuredElement rhs) {
            if (lhs.getViewCounter() < rhs.getViewCounter()) {
                return 1;
            } else if (lhs.getViewCounter() == rhs.getViewCounter()) {
                return 0;
            }
            return -1;
        }
    }

    private static final long serialVersionUID = 1L;

    private transient File _file;

    private Group _groups;

    private transient int _nextAttachmentId;

    /**
     * (String, AccountType)
     */
    private transient Map<String, AccountType> _types;

    /**
     * Unique and persistent id
     */
    private String _uuid;

    /**
     * creates a new TPMDocument with an unnamed file
     */
    public TPMDocument() {
        this(null);
    }

    /**
     * creates a new TPMDocument with a named file
     * 
     * @param file
     *            the file location
     */
    public TPMDocument(final File file) {
        _groups = new Group();
        _nextAttachmentId = 0;
        _file = file;
        _types = new HashMap<String, AccountType>();
        _uuid = IdGenerator.generate();
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
        if (!(obj instanceof TPMDocument)) {
            return false;
        }
        final TPMDocument other = (TPMDocument) obj;
        if (_groups == null) {
            if (other._groups != null) {
                return false;
            }
        } else if (!_groups.equals(other._groups)) {
            return false;
        }
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
     * @return all known AccountTypes.
     */
    public Set<AccountType> getAccountTypes() {
        return new TreeSet<AccountType>(_types.values());
    }

    protected List<SecuredElement> getAllElements() {
        final List<ModelNode> desc = getGroups().preOrder();
        final List<SecuredElement> ret = new ArrayList<SecuredElement>();
        for (final Object element : desc) {
            final ModelNode m = (ModelNode) element;
            if (m instanceof SecuredElement) {
                ret.add((SecuredElement) m);
            }
        }
        return ret;
    }

    /**
     * @return all known attachments.
     */
    public Collection<Attachment> getAttachments() {
        final Collection<Attachment> c = new ArrayList<Attachment>();
        final List<Group> l = new ArrayList<Group>();
        l.add(getGroups());
        while (!l.isEmpty()) {
            final Group g = l.remove(0);
            l.addAll(g.getGroups());
            for (final Object element : g.getElements()) {
                final SecuredElement el = (SecuredElement) element;
                c.addAll(el.getAttachments());
            }
        }
        return c;
    }

    /**
     * @return a List of SecuredElements where the first element is the earliest expired element.
     */
    public List<SecuredElement> getExpiredElements() {
        final List<SecuredElement> all = getSortedElements(-1, new ExpiredComparator());
        int index = 0;
        for (final Object element : all) {
            final SecuredElement el = (SecuredElement) element;
            if (!el.isExpired()) {
                break;
            }
            index++;
        }
        if (index > 0) {
            return all.subList(0, index);
        }
        return new ArrayList<SecuredElement>();
    }

    /**
     * @return Returns the file.
     */
    public synchronized File getFile() {
        return _file;
    }

    /**
     * @param type
     *            is the AccountType to look for.
     * @return a List of GenericAccounts with the defined type.
     */
    public List<GenericAccount> getGenericAccounts(final AccountType type) {
        final List<GenericAccount> ret = new ArrayList<GenericAccount>();
        for (final Object element : getAllElements()) {
            final SecuredElement se = (SecuredElement) element;
            if (se instanceof GenericAccount) {
                final GenericAccount ga = (GenericAccount) se;
                if (ga.getType().equals(type)) {
                    ret.add(ga);
                }
            }
        }
        return ret;
    }

    /**
     * @return the groups
     */
    public Group getGroups() {
        return _groups;
    }

    /**
     * @param max
     *            the number of elements
     * @return a List of SecuredElements where the first element is the last viewed element.
     */
    public List<SecuredElement> getLastViewedElements(final int max) {
        return getSortedElements(max, new LVComparator());
    }

    /**
     * @param max
     *            the number of elements
     * @return a List of SecuredElements where the first element is the most viewed.
     */
    public List<SecuredElement> getMostRecentlyViewedElements(final int max) {
        return getSortedElements(max, new MRVComparator());
    }

    /**
     * @return the next attachment id. Will be incremented!
     */
    public int getNextAttachmentId() {
        return _nextAttachmentId++;
    }

    private List<SecuredElement> getSortedElements(final int max, final Comparator<SecuredElement> comp) {
        final List<SecuredElement> ret = getAllElements();
        Collections.sort(ret, comp);
        if (max > 0) {
            return ret.subList(0, Math.min(max, ret.size()));
        }
        return ret;
    }

    /**
     * @param name
     *            is the unqiue name of the account type.
     * @return the corresponding instances. Will be a singleton.
     * @throws ElementNotFoundException
     *             if there is no AccountType with such a name.
     */
    public AccountType getType(final String name) throws ElementNotFoundException {
        final AccountType ret = _types.get(name);
        if (ret == null) {
            final String msg = Messages.format("TPMDocument.typeNotFound", name);
            throw new ElementNotFoundException(msg);
        }
        return ret;
    }

    public String getUuid() {
        return _uuid;
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

    public boolean matches(final TPMDocument other) {
        if (this == other) {
            return true;
        }

        final TPMDocument castOther = other;
        return this.getGroups().matches(castOther.getGroups());
    }

    /**
     * @param types
     *            is a set of AccountType Objects.
     */
    public void setAccountTypes(final Set<AccountType> types) {
        _types.clear();
        for (final Object element : types) {
            final AccountType t = (AccountType) element;
            _types.put(t.getName(), t);
        }
    }

    public void setFile(final File file) {
        _file = file;
    }

    /**
     * @param groups
     *            The groups to set.
     */
    public final void setGroups(final Group groups) {
        _groups = groups;
    }

    /**
     * @param nextAttachmentId
     *            The nextAttachmentId to set.
     */
    public final void setNextAttachmentId(final int nextAttachmentId) {
        _nextAttachmentId = nextAttachmentId;
    }

    public void setUuid(final String id) {
        _uuid = id;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getFile().getName();
    }

    /**
     * @return the last accessed element or null if there is no element
     */
    public SecuredElement findLastViewedElement() {
        final List<SecuredElement> l = getLastViewedElements(1);
        if (l.isEmpty()) {
            return null;
        }
        return l.get(0);
    }
}
