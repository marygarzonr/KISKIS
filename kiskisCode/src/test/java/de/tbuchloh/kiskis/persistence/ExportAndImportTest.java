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
import java.util.Locale;

import org.junit.Test;

import de.tbuchloh.kiskis.model.StandardDocumentFactory;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.persistence.exporter.CSVExporter;
import de.tbuchloh.kiskis.persistence.exporter.CSVExporterTest;
import de.tbuchloh.kiskis.persistence.importer.CSVImport;
import de.tbuchloh.kiskis.testutils.TestMessageListener;

/**
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 22.11.2010
 */
public class ExportAndImportTest {

    @Test
    public void exportAndImportCsv() throws Exception {
        final Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);

        try {
            final TPMDocument doc = CSVExporterTest.initDocument();
            assertEquals(30, doc.getGroups().preOrder().size());

            final File targetFile = new File("target/exportAndImportCsv.csv");
            new CSVExporter().export(doc, targetFile);

            final TPMDocument newDoc = StandardDocumentFactory.createStandardDocument(new File(""));
            final int groupCnt = 10;
            assertEquals(groupCnt, newDoc.getGroups().preOrder().size());

            final TestMessageListener msg = new TestMessageListener();
            new CSVImport(msg, newDoc).doImport(targetFile, ',');
            assertEquals(0, msg.getMessages().size());

            final int newAccounts = 17, newGroups = 3;
            assertEquals(groupCnt + newAccounts + newGroups, newDoc.getGroups().preOrder().size());
        } finally {
            Locale.setDefault(locale);
        }
    }
}
