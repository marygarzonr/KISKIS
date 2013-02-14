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
package de.tbuchloh.kiskis.persistence;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;

import nu.xom.Document;
import nu.xom.XPathContext;

import org.junit.Test;

import de.tbuchloh.kiskis.util.BuildProperties;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 28.10.2010
 */
public class XMLReaderTest {

    /**
     * Test für Transform024To0241
     * 
     * @throws Exception
     *             wenn unvorhergesehene Fehler auftreten.
     */
    @Test
    public void testTransform024ToCurrent() throws Exception {
	BuildProperties.VALIDATE_DOCS = true;

	final File file = new File(
		"src/test/resources/kiskis-0.24-w-attachments.xml");
	final XMLReader reader = new XMLReader(file);
	final Document doc = reader.parseDocument(new FileInputStream(file));
	final Document tdoc = reader.transformDoc(doc);

	System.out.println(tdoc.toXML());

	final XPathContext ctx = new XPathContext("ns", XMLProcessing.NS_MODEL);
	assertEquals(6, tdoc.query("//ns:Attachment", ctx).size());

	assertEquals("Kommentar mit\nZeilenumbrüchen,\n1\n2\n3\n",
		tdoc.query("//ns:TPMDocument/ns:Group/ns:Comment", ctx).get(0)
		.getValue());
	assertEquals(
		6,
		tdoc.query("//ns:TPMDocument/ns:Attachments/ns:Attachment", ctx)
		.size());

	assertEquals(6, tdoc.query("//ns:AttachmentRef", ctx).size());

	assertEquals(6, tdoc.query("//ns:SecuredElement/ns:AttachmentRef", ctx)
		.size());

	assertEquals(0, tdoc.query("//ns:SecuredElement/ns:Attachment", ctx)
		.size());
    }
}
