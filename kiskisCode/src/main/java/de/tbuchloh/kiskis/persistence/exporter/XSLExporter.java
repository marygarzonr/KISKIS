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

package de.tbuchloh.kiskis.persistence.exporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;

import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.persistence.PersistenceException;
import de.tbuchloh.kiskis.persistence.XMLWriter;

/**
 * <b>XSLExporter</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public class XSLExporter implements IExporter {

    /**
     * @param stylesheet
     *            is the stylesheet to use.
     * @param exportFile
     *            is the outputfile
     */
    public static void transform(final TPMDocument doc, final URL stylesheet, final File exportFile)
    throws TransformException {
        if (stylesheet == null) {
            throw new IllegalArgumentException("Stylesheet not found!");
        }

        final XMLWriter w = new XMLWriter();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            w.save(doc, bos);
        } catch (final PersistenceException e) {
            throw new TransformException(e.getMessage(), e);
        }

        final XMLTransformer t = new XMLTransformer();
        try {
            t.transform(stylesheet, new ByteArrayInputStream(bos.toByteArray()), new FileOutputStream(exportFile));
        } catch (final TransformException e1) {
            throw e1;
        } catch (final FileNotFoundException e1) {
            throw new TransformException(e1.getMessage(), e1);
        }
    }

    private final URL _stylesheet;

    /**
     * creates a new XSLExporter
     * 
     * @param systemResource
     */
    public XSLExporter(final URL stylesheet) {
        _stylesheet = stylesheet;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.persistence.exporter.IExporter#export(de.tbuchloh.kiskis.model.TPMDocument, java.io.File)
     */
    @Override
    public void export(final TPMDocument doc, final File file) throws ExportException {
        try {
            transform(doc, _stylesheet, file);
        } catch (final TransformException e) {
            throw new ExportException(e.getMessage());
        }
    }
}
