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

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * <b>ModelNode</b>:
 * 
 * @author gandalf
 * @version $Id: ModelNode.java,v 1.8 2007/02/18 14:37:29 tbuchloh Exp $
 */
public interface ModelNode extends Cloneable, Serializable {

    public ModelNode clone();

    /**
     * @param regex
     *            to search
     * @return true, if the string is part of any field value.
     */
    public boolean contains(Pattern regex);

    /**
     * @return the name of the node.
     */
    public String getName();

    /**
     * @return is the parent element or null if root node.
     */
    public ModelNode getParent();

    /**
     * @return a unique id which identifies this object uniquely
     */
    public String getUuid();

    /**
     * @param text
     *            sets the name of the node.
     */
    public void setName(String text);

    /**
     * @param el
     *            is the parent element
     */
    public void setParent(ModelNode el);

    /**
     * Implements the visitor-pattern
     * 
     * @param visitor
     *            the visitor implementation
     */
    public void visit(ModelNodeVisitor visitor);

    /**
     * @return true, if the node has been marked for the archive.
     */
    public boolean isArchived();
}
