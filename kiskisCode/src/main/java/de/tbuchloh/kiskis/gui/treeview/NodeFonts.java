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

/**
 * <b>NodeFonts</b>:
 * 
 * @author gandalf
 * @version $Id: NodeFonts.java,v 1.3 2006/04/14 07:56:07 tbuchloh Exp $
 */
interface NodeFonts {

    final Color DEF_TEXT_COLOR = Color.BLACK;

    final Font GROUP = new Font("Helvetica", Font.BOLD, 14); //$NON-NLS-1$

    final Font ITEM = new Font("Helvetica", Font.PLAIN, 14); //$NON-NLS-1$

    final Color SEARCH_RESULT_TEXT_COLOR = Color.RED;

    final Color ARCHIVED_TEXT_COLOR = Color.DARK_GRAY;
}
