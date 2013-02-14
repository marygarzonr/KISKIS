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
package de.tbuchloh.kiskis.cracker;

/**
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 02.12.2010
 */
public interface IPasswordCreationStrategy {

    /**
     * Creates a new password.
     * 
     * @return the newly created password or <code>null</code> if there is no new password.
     */
    String create();

    /**
     * @return the estimation for the total count or <code>null</code> if not computable.
     */
    Long estimateTotalCount();
}
