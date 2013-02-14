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

import static de.tbuchloh.kiskis.testutils.CollectionUtils.get;
import junit.framework.TestCase;
import de.tbuchloh.kiskis.util.DateUtils;

/**
 * @author gandalf
 * 
 */
public class GroupTest extends TestCase {

    public void testClone() throws Exception {
	// see Bug 3088703
	final Group g1 = new Group("Test1");
	g1.setArchivedOnDate(DateUtils.getCurrentDateTime());
	g1.setComment("My comment");

	final Group g11 = new Group("Test1.1");
	g11.setComment("My comment 1.1");
	g1.add(g11);

	final Group g12 = new Group("Test1.2");
	g1.add(g12);

	final Group g121 = new Group("Test1.2.1");
	g121.setComment("My comment 1.2.1");

	final NetAccount na121 = new NetAccount();
	na121.setName("Network Account 1.2.1");
	na121.setArchivedOnDate(DateUtils.getCurrentDateTime());
	g121.add(na121);
	g12.add(g121);

	final Group c1 = g1.clone();
	assertEquals(g1.getArchivedOnDate(), c1.getArchivedOnDate());
	assertEquals(g1.getComment(), c1.getComment());
	assertEquals(g1.getChildren().size(), c1.getChildren().size());

	final Group c11 = get(g1.getGroups(), 0);
	assertEquals(g11.getComment(), c11.getComment());
	assertEquals(g11.getChildren().size(), c11.getChildren().size());

	final Group c12 = get(g1.getGroups(), 1);
	assertEquals(g12.getComment(), c12.getComment());
	assertEquals(g12.getChildren().size(), c12.getChildren().size());

	final Group c121 = get(g12.getGroups(), 0);
	assertEquals(g121.getComment(), c121.getComment());
	assertEquals(g121.getChildren().size(), c121.getChildren().size());

	final NetAccount cna121 = get(g121.getElements(), 0);
	assertEquals(na121.getArchivedOnDate(), cna121.getArchivedOnDate());
	assertEquals(na121.getComment(), cna121.getComment());

    }
}
