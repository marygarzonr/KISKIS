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
import java.util.Vector;
import java.util.regex.Pattern;

import de.tbuchloh.kiskis.model.annotations.Observable;
import de.tbuchloh.kiskis.model.annotations.SecretInfo;
import de.tbuchloh.kiskis.util.DateUtils;

/**
 * <b>de.tbuchloh.kiskis.model.SecuredElement</b>: Models an abstract element
 * which is secured by a password.<br>
 * It can also have a name and a comment.
 * 
 * @author gandalf
 * @version $Id: SecuredElement.java,v 1.14 2007/02/18 14:37:29 tbuchloh Exp $
 */
@Observable
public abstract class SecuredElement implements Comparable<SecuredElement>,
ModelConstants, Cloneable, ModelNode, Archivable {

    private static final long serialVersionUID = 1L;

    /**
     * the date when this item has been archived. Can be null.
     */
    private Calendar _archivedOnDate;

    private Collection<Attachment> _attachments;

    @SecretInfo
    private String _comment;

    private Calendar _creationDate;

    private boolean _expiresNever;

    private Calendar _lastChangeDate;

    private Calendar _lastViewedDate;

    @SecretInfo
    private String _name;

    private transient ModelNode _parent;

    private Password _pwd;

    private PasswordHistory _pwdHistory;

    /**
     * Unique and persistent id
     */
    private String _uuid;

    private int _viewCounter;

    protected SecuredElement() {
        _uuid = IdGenerator.generate();
        _name = "unnamed";
        _pwd = new Password();
        _pwdHistory = new PasswordHistory();
        _expiresNever = true;
        _comment = "";
        _creationDate = DateUtils.getCurrentDateTime();
        _lastChangeDate = DateUtils.getCurrentDateTime();
        _lastViewedDate = DateUtils.getCurrentDateTime();
        _viewCounter = 0;
        _attachments = new Vector<Attachment>();
    }

    /**
     * @param attachment
     *            the new attachment.
     */
    public void addAttachment(final Attachment attachment) {
        if (attachment != null) {
            _attachments.add(attachment);
        }
    }

    /**
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public SecuredElement clone() {
        SecuredElement inst = null;
        try {
            inst = (SecuredElement) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new Error(e);
        }

        inst._uuid = IdGenerator.generate();
        inst._expiresNever = this._expiresNever;
        inst._name = this._name;
        inst._pwd = this._pwd.clone();
        inst._comment = this._comment;
        inst._creationDate = DateUtils.getCurrentDateTime();
        inst._lastChangeDate = DateUtils.getCurrentDateTime();
        inst._pwdHistory = (PasswordHistory) _pwdHistory.clone();
        inst._lastViewedDate = DateUtils.getCurrentDateTime();
        inst._viewCounter = 0;
        inst._parent = null;
        if (_archivedOnDate != null) {
            inst._archivedOnDate = (Calendar) this._archivedOnDate.clone();
        }

        inst._attachments = new ArrayList<Attachment>(this._attachments.size());
        for (final Attachment a : getAttachments()) {
            // Attachments will not be cloned
            inst.addAttachment(a);
        }
        return inst;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public final int compareTo(final SecuredElement o) {
        return _name.toLowerCase().compareTo(o._name.toLowerCase());
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.ModelNode#contains(Pattern)
     */
    @Override
    public boolean contains(final Pattern regex) {
        final boolean ret = regex.matcher(_name).matches()
        || regex.matcher(_comment).matches();
        if (!ret) {
            for (final Object element : getAttachments()) {
                final Attachment a = (Attachment) element;
                if (a.contains(regex)) {
                    return true;
                }
            }
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SecuredElement)) {
            return false;
        }
        final SecuredElement other = (SecuredElement) obj;
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
     * @return true, if the password never expires
     */
    public boolean expiresNever() {
        return _expiresNever;
    }

    /**
     * @return {@link #_archivedOnDate}
     */
    @Override
    public Calendar getArchivedOnDate() {
        return _archivedOnDate;
    }

    /**
     * @return Returns the attachments.
     */
    public final Collection<Attachment> getAttachments() {
        return Collections.unmodifiableCollection(_attachments);
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return _comment;
    }

    /**
     * @return the date of creation
     */
    public Calendar getCreationDate() {
        return _creationDate;
    }

    /**
     * @return the date when this item was changed
     */
    public Calendar getLastChangeDate() {
        return _lastChangeDate;
    }

    /**
     * @return Returns the lastViewedDate.
     */
    public final Calendar getLastViewedDate() {
        return _lastViewedDate;
    }

    /**
     * @return the item name
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

    /**
     * @return the associated password
     */
    public Password getPwd() {
        return _pwd;
    }

    /**
     * @return Returns the pwdHistory.
     */
    public final PasswordHistory getPwdHistory() {
        return _pwdHistory;
    }

    @Override
    public String getUuid() {
        return _uuid;
    }

    /**
     * @return Returns the viewCounter.
     */
    public final int getViewCounter() {
        return _viewCounter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
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
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isArchived() {
        return _archivedOnDate != null;
    }

    /**
     * @return true, if the password is expired.
     */
    public boolean isExpired() {
        final boolean ret = !_expiresNever
        && getPwd().getExpires().before(DateUtils.getCurrentDateTime());
        return ret;
    }

    public boolean matches(final SecuredElement obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        final SecuredElement other = obj;
        if (_attachments == null) {
            if (other._attachments != null) {
                return false;
            }
        } else {
            for (Iterator<Attachment> i = _attachments.iterator(), j = other._attachments
                    .iterator(); i.hasNext();) {
                final Attachment attLhs = i.next();
                final Attachment attRhs = j.next();
                if (!attLhs.matches(attRhs)) {
                    return false;
                }
            }
        }
        if (_comment == null) {
            if (other._comment != null) {
                return false;
            }
        } else if (!_comment.equals(other._comment)) {
            return false;
        }
        if (_creationDate == null) {
            if (other._creationDate != null) {
                return false;
            }
        } else if (!_creationDate.equals(other._creationDate)) {
            return false;
        }
        if (_expiresNever != other._expiresNever) {
            return false;
        }
        if (_lastChangeDate == null) {
            if (other._lastChangeDate != null) {
                return false;
            }
        } else if (!_lastChangeDate.equals(other._lastChangeDate)) {
            return false;
        }
        if (_lastViewedDate == null) {
            if (other._lastViewedDate != null) {
                return false;
            }
        } else if (!_lastViewedDate.equals(other._lastViewedDate)) {
            return false;
        }
        if (_name == null) {
            if (other._name != null) {
                return false;
            }
        } else if (!_name.equals(other._name)) {
            return false;
        }
        if (_pwd == null) {
            if (other._pwd != null) {
                return false;
            }
        } else if (!_pwd.equals(other._pwd)) {
            return false;
        }
        if (_pwdHistory == null) {
            if (other._pwdHistory != null) {
                return false;
            }
        } else if (!_pwdHistory.equals(other._pwdHistory)) {
            return false;
        }
        if (_viewCounter != other._viewCounter) {
            return false;
        }
        if (_archivedOnDate == null) {
            if (other._archivedOnDate != null) {
                return false;
            }
        } else if (!_archivedOnDate.equals(other._archivedOnDate)) {
            return false;
        }
        return true;
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
     * @param attachments
     *            The attachments to set.
     */
    public final void setAttachments(final Collection<Attachment> attachments) {
        _attachments = attachments;
    }

    /**
     * @param comment
     *            the comment
     */
    public void setComment(final String comment) {
        _comment = comment;
    }

    /**
     * @param calendar
     *            the creation date of this item
     */
    public void setCreationDate(final Calendar calendar) {
        _creationDate = calendar;
    }

    /**
     * @param never
     *            true, if the password never expires
     */
    public void setExpiresNever(final boolean never) {
        _expiresNever = never;
    }

    /**
     * @param calendar
     *            the date of the last change
     */
    public void setLastChangeDate(final Calendar calendar) {
        _lastChangeDate = calendar;
    }

    /**
     * @param lastViewedDate
     *            The lastViewedDate to set.
     */
    public final void setLastViewedDate(final Calendar lastViewedDate) {
        _lastViewedDate = lastViewedDate;
    }

    /**
     * @param name
     *            the item name
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

    /**
     * @param password
     *            the new password
     */
    public void setPwd(final Password password) {
        if (!_pwd.isSame(password) && !_pwd.isEmpty()) {
            _pwdHistory.addPassword(_pwd);
        }
        _pwd = password;
    }

    /**
     * @param history
     *            the new History.
     */
    public void setPwdHistory(final PasswordHistory history) {
        _pwdHistory = history;
    }

    public void setUuid(final String id) {
        if (id != null) {
            _uuid = id;
        }
    }

    /**
     * @param viewCounter
     *            The viewCounter to set.
     */
    public final void setViewCounter(final int viewCounter) {
        _viewCounter = viewCounter;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _name;
    }

    /**
     * increments the internal viewCounter and sets the lastViewedDate to the
     * current time.
     */
    public void updateLastViewed() {
        setViewCounter(getViewCounter() + 1);
        setLastViewedDate(DateUtils.getCurrentDateTime());
    }
}
