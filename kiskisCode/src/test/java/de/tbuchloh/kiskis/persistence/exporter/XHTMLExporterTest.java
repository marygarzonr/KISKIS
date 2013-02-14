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
package de.tbuchloh.kiskis.persistence.exporter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.logging.Log;
import org.junit.Test;

import de.tbuchloh.util.io.FileProcessor;
import de.tbuchloh.util.logging.LogFactory;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 22.10.2010
 */
public class XHTMLExporterTest {

    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLogger();

    /**
     * Test für Export
     * 
     * @throws Exception
     *             wenn unvorhergesehene Fehler auftreten.
     */
    @Test
    public void testExportHtml() throws Exception {
        final File file = new File("target/test.html");
        new XSLExporter(ClassLoader.getSystemResource("export/kiskis-html.xsl")).export(CSVExporterTest.initDocument(),
                file);
        final String content = FileProcessor.readFile(file);
        LOG.debug(content);
        final String[] contents = {
                "e-mail related stuff here", // Group comment
                "ebay", // Label
                "144434365tfhgcfghcv", // password
                "googlemailer", // user name
                "Comment with multiple lines", // account comment
                "2010-11-16T15:19:45", // created on,
                "2010-11-16T15:17:43", // last changed
                "2010-11-16T15:17:35", // last viewed
                "<td>7</td>", // view count
                "2011-11-16", // expires on
                "2009-11-12T15:15:43", // archived On
                "foobar@gmx.eu", // email
                "http://www.amazon.com", // url
        };

        for (final String s : contents) {
            assertTrue(s, content.contains(s));
        }

        // expiresNever = true
        assertFalse(content.contains("2028-11-16"));
    }
}
