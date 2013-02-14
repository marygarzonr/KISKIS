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
package de.tbuchloh.kiskis.util;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 19.10.2010
 */
public class FileToolsTest {

    /**
     * Test method for {@link FileTools#getShortAbsoluteFilename(java.io.File)}.
     */
    @Test
    public void testGetShortAbsoluteFilename() {
	assertShortFilename("/test.xml", "/test.xml");
	assertShortFilename("/tmp/test.xml", "/tmp/test.xml");
	assertShortFilename("/tmp/foo/test.xml", "/tmp/foo/test.xml");
	assertShortFilename("/tmp/.../bar/test.xml", "/tmp/foo/bar/test.xml");
	assertShortFilename("/tmp/.../hugo/test.xml",
	"/tmp/foo/bar/hugo/test.xml");
    }

    private void assertShortFilename(String expected, String path) {
	assertEquals(expected,
		FileTools.getShortAbsoluteFilename(new File(path)));
    }

}
