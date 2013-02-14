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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * <b>ModelConstants</b>: provides some constants for the model package
 * 
 * @author gandalf
 * @version $Id: ModelConstants.java,v 1.1 2004/06/10 07:55:15 tbuchloh Exp $
 */
public interface ModelConstants {
    /**
     * Short time format
     */
    public static final DateFormat SHORT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Long ISO-Format
     */
    public static final DateFormat LONG = new SimpleDateFormat(
	    "yyyy-MM-dd HH:mm:ss");

    /**
     * Long time format in SQL-Style
     */
    public static final DateFormat XML_LONG = new SimpleDateFormat(
    "yyyy-MM-dd'T'HH:mm:ss");
}
