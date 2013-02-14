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

/**
 * <b>PasswordTemplate</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public class PasswordTemplate {

	public static final char ALPHA = 'a';

	public static final char ALPHA_NUM = 'n';

	public static final char ANY = '?';

	public static final char CONSONANT = 'c';

	public static final char DIGIT = '9';

	public static final char LETTER = 'l';

	public static final char SPECIAL = '#';

	public static final char VOCAL = 'v';

	private String _template;

	/**
	 * creates a new PasswordTemplate
	 */
	public PasswordTemplate() {
		super();
	}

	/**
	 * @return Returns the template.
	 */
	public final String getTemplate() {
		return _template;
	}

	/**
	 * @param template
	 *            The template to set.
	 */
	public final void setTemplate(final String template) {
		_template = template;
	}

}
