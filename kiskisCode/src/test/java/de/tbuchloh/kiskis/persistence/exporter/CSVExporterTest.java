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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.persistence.IErrorHandler;
import de.tbuchloh.kiskis.persistence.PasswordCryptoContext;
import de.tbuchloh.kiskis.persistence.PersistenceManager;
import de.tbuchloh.kiskis.persistence.SimplePasswordProxy;
import de.tbuchloh.kiskis.testutils.TestErrorHandler;
import de.tbuchloh.util.crypto.OpenPGPwithAES256;
import de.tbuchloh.util.event.DefaultMessageListener;
import de.tbuchloh.util.io.FileProcessor;
import de.tbuchloh.util.text.csv.CSVFile;

/**
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 22.10.2010
 */
public class CSVExporterTest {
    /**
     * Der Logger
     */
    static final Log LOG = LogFactory.getLog(CSVExporterTest.class);

    /**
     * @return the test document to compare against.
     */
    public static TPMDocument initDocument() throws Exception {
        final File file = new File(CSVExporterTest.class.getResource("export.xml").toURI());
        final IErrorHandler handler = new TestErrorHandler();
        return PersistenceManager.load(new PasswordCryptoContext(new OpenPGPwithAES256(),
                new SimplePasswordProxy(null), file), handler);
    }

    /**
     * Test method for
     * {@link de.tbuchloh.kiskis.persistence.exporter.CSVExporter#export(de.tbuchloh.kiskis.model.TPMDocument, java.io.File)}
     * .
     */
    @Test
    public void testExport() throws Exception {
        final TPMDocument doc = initDocument();
        final File targetFile = new File("target/testExport.csv");
        new CSVExporter().export(doc, targetFile);

        final CSVFile csv = CSVFile.parse(new DefaultMessageListener(), targetFile, ',');
        assertEquals(9, csv.getHeader().size());
        assertEquals(17, csv.getEntries().size());

        // we just need a random sample
        final String content = FileProcessor.readFile(targetFile);
        final String[] expected = {
                "Banking & Finance##Deutsche Bank", // Group
                "My PGP Key", // Label
                "144434365tfhgcfghcv", // Password
                "googlemailer", // user name
                "foo@bar.com", // email
                "https://mail.google.com/", // url
                "2008-01-16", // created on
                "2021-10-19", // expires on
                "Bank Account\nComment with\nmultiple lines", // Comment
        };
        for (final String c : expected) {
            assertTrue(c, content.contains(c));
        }
    }

}
