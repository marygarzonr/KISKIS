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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.com.bytecode.opencsv.CSVWriter;
import de.tbuchloh.kiskis.model.Attachment;
import de.tbuchloh.kiskis.model.BankAccount;
import de.tbuchloh.kiskis.model.CreditCard;
import de.tbuchloh.kiskis.model.GenericAccount;
import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.kiskis.model.ModelConstants;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.ModelNodeVisitor;
import de.tbuchloh.kiskis.model.NetAccount;
import de.tbuchloh.kiskis.model.SecuredElement;
import de.tbuchloh.kiskis.model.SecuredFile;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.persistence.importer.CSVImport;
import de.tbuchloh.util.text.csv.CSVFile;
import de.tbuchloh.util.text.csv.CSVFile.CSVEntry;

/**
 * <b>CSVExporter</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public class CSVExporter implements IExporter {

    /**
     * <b>CSVModelVisitor</b>:
     * 
     * @author gandalf
     * @version $Id$
     */
    private static class CSVModelVisitor implements ModelNodeVisitor {

        private final CSVFile _csv;

        /**
         * creates a new CSVModelVisitor
         */
        public CSVModelVisitor(final CSVFile csv) {
            _csv = csv;
        }

        private CSVEntry appendSecuredElement(final SecuredElement account) {
            final CSVEntry entry = new CSVEntry();
            ModelNode group = account.getParent();
            final StringBuilder groupName = new StringBuilder(group.getName());
            // we do not want "My Passwords" in the group path
            while ((group = group.getParent()) != null && group.getParent() != null) {
                groupName.insert(0, CSVImport.GROUP_SEP);
                groupName.insert(0, group.getName());
            }
            entry.put(CSVImport.GROUP, groupName.toString());
            entry.put(CSVImport.LABEL, account.getName());
            entry.put(CSVImport.PWD, new String(account.getPwd().getPwd()));
            entry.put(CSVImport.CREATED, ModelConstants.SHORT.format(account.getCreationDate().getTime()));
            if (!account.expiresNever()) {
                entry.put(CSVImport.EXPIRATION, ModelConstants.SHORT.format(account.getPwd().getExpires().getTime()));
            }
            entry.put(CSVImport.COMMENT, account.getComment());

            _csv.addEntry(entry);
            return entry;
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model.Attachment)
         */
        @Override
        public void visit(final Attachment attachment) {
            // does nothing
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model.BankAccount)
         */
        @Override
        public void visit(final BankAccount account) {
            appendSecuredElement(account);
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model.CreditCard)
         */
        @Override
        public void visit(final CreditCard card) {
            appendSecuredElement(card);
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model.GenericAccount)
         */
        @Override
        public void visit(final GenericAccount account) {
            appendSecuredElement(account);
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model.Group)
         */
        @Override
        public void visit(final Group group) {
            // does nothing
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model.NetAccount)
         */
        @Override
        public void visit(final NetAccount account) {
            final CSVEntry entry = appendSecuredElement(account);
            entry.put(CSVImport.USERNAME, account.getUsername());
            entry.put(CSVImport.EMAIL, account.getEmail());
            entry.put(CSVImport.URL, account.getUrl());
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.model.ModelNodeVisitor#visit(de.tbuchloh.kiskis.model.SecuredFile)
         */
        @Override
        public void visit(final SecuredFile account) {
            appendSecuredElement(account);
        }

    }

    private static final Log LOG = LogFactory.getLog(CSVExporter.class);

    private List<String> createHeader() {

        return Arrays.asList(new String[] {
                CSVImport.GROUP, CSVImport.LABEL, CSVImport.PWD, CSVImport.USERNAME, CSVImport.EMAIL, CSVImport.URL,
                CSVImport.CREATED, CSVImport.EXPIRATION, CSVImport.COMMENT
        });
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.persistence.exporter.IExporter#export(de.tbuchloh.kiskis.model.TPMDocument, java.io.File)
     */
    @Override
    public void export(final TPMDocument doc, final File file) throws ExportException {
        final CSVFile csv = new CSVFile();
        final List<String> header = createHeader();
        csv.setHeader(header);
        final CSVModelVisitor v = new CSVModelVisitor(csv);
        for (final Object element : doc.getGroups().preOrder()) {
            final ModelNode n = (ModelNode) element;
            n.visit(v);
        }
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            final CSVWriter writer = new CSVWriter(bw);
            writer.writeNext(csv.getHeader().toArray(new String[0]));

            for (final CSVEntry entry : csv.getEntries()) {
                final String[] fields = getFieldValues(entry, header);
                writer.writeNext(fields);
            }
            bw.close();
        } catch (final IOException e) {
            LOG.error(e, e);
            throw new ExportException(e.getMessage());
        }
    }

    /**
     * @param entry
     *            the entry
     * @param header
     *            the header fields
     * @return the string array
     */
    private String[] getFieldValues(CSVEntry entry, List<String> header) {
        final String[] r = new String[header.size()];
        for (int i = 0; i < header.size(); ++i) {
            String v = entry.get(header.get(i));
            if (v == null) {
                v = "";
            }
            r[i] = v;
        }
        return r;
    }
}
