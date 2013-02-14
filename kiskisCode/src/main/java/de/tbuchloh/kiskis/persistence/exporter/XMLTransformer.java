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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Serializer;
import nu.xom.xslt.XSLTransform;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.tbuchloh.util.xml.XMLException;

/**
 * <b>XMLTransformer</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public final class XMLTransformer {
    /**
     * Commons Logger for this class
     */
    protected static final Log LOG = LogFactory.getLog(XMLTransformer.class);

    /**
     * creates a new XMLTransformer
     */
    public XMLTransformer() {
	super();
    }

    /**
     * @param stylesheet
     *            is the stylesheet to use.
     * @param is
     *            is the input source.
     * @param os
     *            is the output sink.
     */
    public void transform(final URL stylesheet, final InputStream is,
	    final OutputStream os) throws TransformException {
	try {
	    LOG.debug("transforming with stylesheet=" + stylesheet);

	    final XMLReader reader = XMLReaderFactory.createXMLReader();
	    reader.setEntityResolver(new EntityResolver() {
		@Override
		public InputSource resolveEntity(final String publicId,
			final String systemId) throws SAXException, IOException {
		    if (systemId == null) {
			throw new IllegalArgumentException(
			"systemId must not be null!");
		    }
		    final int pos = systemId.lastIndexOf(File.separator);
		    String resource = systemId;
		    if (pos >= 0) {
			resource = resource.substring(pos + 1);
		    }
		    final URL dtd = ClassLoader.getSystemResource(resource);

		    LOG.debug("Resolving " + "systemId=" + systemId
			    + ", resource=" + resource + ", dtd=" + dtd);

		    return new InputSource(dtd.openStream());
		}
	    });
	    try {
		final Builder builder = new Builder(reader, false);
		final Document s = builder.build(stylesheet.openStream());

		final Document source = builder.build(is);

		final XSLTransform transform = new XSLTransform(s);
		final Document target = XSLTransform.toDocument(transform
			.transform(source));

		final Serializer ser = new Serializer(os);
		ser.setIndent(2);
		ser.write(target);
	    } catch (final Exception e) {
		LOG.error(e, e);
		throw new XMLException(e.getMessage());
	    }
	} catch (final Exception e) {
	    throw new TransformException(e.getMessage(), e);
	}
    }

}
