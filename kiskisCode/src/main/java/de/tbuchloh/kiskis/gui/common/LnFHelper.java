/*
 * Copyright (C) 2010 by Tobias Buchloh.
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
package de.tbuchloh.kiskis.gui.common;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 08.11.2010
 */
public final class LnFHelper {

    public static Border createDefaultBorder() {
        return BorderFactory.createEmptyBorder(10, 10, 10, 10);
    }

    /**
     * @param title
     *            the title to display
     * @return the component
     */
    public static JLabel createLabel(String title) {
        return new JLabel(title + ":", SwingConstants.TRAILING);
    }

    /**
     * Private Constructor
     */
    private LnFHelper() {
        // empty
    }
}
