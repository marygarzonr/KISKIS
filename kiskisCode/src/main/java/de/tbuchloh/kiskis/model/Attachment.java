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

package de.tbuchloh.kiskis.model;

import static de.tbuchloh.kiskis.model.PasswordCallFactory.SECURE;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.model.annotations.Observable;
import de.tbuchloh.kiskis.model.annotations.SecretInfo;

/**
 * <b>Attachment</b>: Models an attached file. The attachments will be
 * serialized and encrypted separately. Each Attachment has an own key which is
 * stored within the TPMDocument.
 * 
 * @author gandalf
 * @version $Id: Attachment.java,v 1.15 2007/02/18 14:37:29 tbuchloh Exp $
 */
@Observable
public final class Attachment implements ModelNode {

    private static final int KEY_LENGTH = 12;

    private static final Log LOG = LogFactory.getLog(Attachment.class);

    private static final long serialVersionUID = 1L;

    @SecretInfo
    private String _description;

    private final TPMDocument _doc;

    private int _id;

    @SecretInfo
    private char[] _key;

    private transient ModelNode _parent;

    private String _uuid;

    private transient File _attachedFile;

    /**
     * creates a new Attachment
     * 
     * @param doc
     *            is the associated document for this attachment.
     */
    public Attachment(final TPMDocument doc) {
        super();
        _doc = doc;
        _id = doc.getNextAttachmentId();
        _key = PasswordCallFactory.create(SECURE, KEY_LENGTH).getPwd();
        _description = "";
        _uuid = IdGenerator.generate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attachment clone() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.ModelNode#contains(Pattern)
     */
    @Override
    public boolean contains(final Pattern regex) {
        return regex.matcher(_description).matches()
        || regex.matcher(String.valueOf(getId())).matches();
    }

    /**
     * @return {@link #attachedFile}
     */
    public File getAttachedFile() {
        return _attachedFile;
    }

    /**
     * Returns <code>true</code> if this <code>Attachment</code> is the same as
     * the o argument.
     * 
     * @return <code>true</code> if this <code>Attachment</code> is the same as
     *         the o argument.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        final Attachment castedObj = (Attachment) o;
        return this._uuid.equals(castedObj._uuid);
    }

    /**
     * @return {@link #doc}
     */
    public TPMDocument getDocument() {
        return _doc;
    }

    /**
     * @return Returns the description.
     */
    public final String getDescription() {
        return _description;
    }

    /**
     * @return Returns the id.
     */
    public final int getId() {
        return _id;
    }

    /**
     * @return Returns the key.
     */
    public final char[] getKey() {
        return _key.clone();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.ModelNode#getName()
     */
    @Override
    public String getName() {
        return getDescription();
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
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return _uuid.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isArchived() {
        return false;
    }

    /**
     * @param rhs
     *            the object to compare with
     * @return true if the contents match
     */
    public boolean matches(Attachment rhs) {
        if (this == rhs) {
            return true;
        }
        if (rhs == null) {
            return false;
        }
        return this._id == rhs._id && this._uuid.equals(rhs._uuid)
        && java.util.Arrays.equals(this._key, rhs._key)
        && this._description.equals(rhs._description);
    }

    /**
     * @param description
     *            The description to set.
     */
    public final void setDescription(final String description) {
        _description = description;
    }

    /**
     * @param file
     *            is the file to attach. Will be encrypted afterwards by the
     *            persister.
     */
    public void setAttachedFile(final File file) {
        _attachedFile = file;
    }

    /**
     * @param id
     *            The id to set.
     */
    public final void setId(final int id) {
        _id = id;
    }

    /**
     * @param key
     *            The key to set.
     */
    public final void setKey(final char[] key) {
        _key = key.clone();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.ModelNode#setName(java.lang.String)
     */
    @Override
    public void setName(final String text) {
        _description = text;
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

    public void setUuid(final String uuid) {
        _uuid = uuid;
    }

    /**
     * Overridden!
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getDescription();
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

}
