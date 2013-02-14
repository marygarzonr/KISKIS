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

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * <b>PasswordFactoryTest</b>:
 * 
 * @author gandalf
 * @version $Id: PasswordFactoryTest.java,v 1.3 2007/02/18 14:37:53 tbuchloh Exp
 *          $
 */
public final class PasswordFactoryTest extends TestCase {

	private static final int MAX_PWDS = 1000;

	private static final int MAX_PWD_LENGTH = 12;

	private static final int MIN_PWD_LENGTH = 3;

	/**
	 * Constructor for PasswordFactoryTest.
	 * 
	 * @param arg0
	 *            nothing
	 */
	public PasswordFactoryTest(final String arg0) {
		super(arg0);
	}

	/**
	 * create a huge amount of passwords and test uniqueness.
	 */
	public void testCreate() {
		final Set<Password> map = new HashSet<Password>();
		for (int i = 0; i < MAX_PWDS; ++i) {
			int mode = -1;
			if (i % 2 == 0) {
				mode = PasswordFactory.SECURE;
			} else {
				mode = PasswordFactory.HUMAN_READABLE;
			}

			final int length = Math.max(i % MAX_PWD_LENGTH, MIN_PWD_LENGTH);
			final Password pwd = PasswordFactory.create(mode, length);
			if (map.contains(pwd)) {
				fail("map '" + map + "' already contains '" + pwd + "'!");
			} else {
				map.add(pwd);
			}
			// System.out.println(mode + ", " + pwd);
		}
	}

}
