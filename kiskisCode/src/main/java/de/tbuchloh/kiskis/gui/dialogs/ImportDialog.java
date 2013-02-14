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

package de.tbuchloh.kiskis.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.kiskis.gui.widgets.BasicTextField;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.persistence.importer.CSVImport;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.event.MessageListener;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.GridBagBuilder;
import de.tbuchloh.util.swing.widgets.ObservableTextField;
import de.tbuchloh.util.swing.widgets.SelectFileBox;
import de.tbuchloh.util.swing.widgets.SimpleInternalFrame;
import de.tbuchloh.util.text.StringTools;

/**
 * <b>ImportDialog</b>:
 * 
 * @author gandalf
 * @version $Id: ImportDialog.java,v 1.6 2007/02/18 14:37:35 tbuchloh Exp $
 */
public final class ImportDialog extends KisKisDialog {

    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLog(ImportDialog.class);

    private static final long serialVersionUID = 1L;

    private static final Messages M = new Messages(ImportDialog.class);

    private static final String D_FIELD_SEP = M.getString("D_FIELD_SEP");

    private static final String MSG_CHOOSE_FIELD_SEP = M.getString("MSG_CHOOSE_FIELD_SEP");

    private static final String MSG_CONFIRM_START = "MSG_CONFIRM_START";

    private static final String NOTE = M.getString("NOTE");

    private static final String SETTINGS = M.getString("SETTINGS");

    private static final String TITLE = M.getString("dialog.title");

    private static void addPadding(final JPanel panel) {
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
    }

    private Action _cancelAction;

    private final TPMDocument _doc;

    private ObservableTextField _fieldSepBox;

    private SelectFileBox _fileBox;

    private Action _okAction;

    /**
     * creates a new ImportDialog
     * 
     * @param owner
     */
    public ImportDialog(final Dialog owner, final TPMDocument doc) {
        super(owner, TITLE, true);
        init();
        _doc = doc;
    }

    /**
     * creates a new ImportDialog
     * 
     * @param owner
     */
    public ImportDialog(final Frame owner, final TPMDocument doc) {
        super(owner, TITLE, true);
        init();
        _doc = doc;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.dialogs.KisKisDialog#createMainPanel()
     */
    @Override
    protected Component createMainPanel() {
        final SimpleInternalFrame settings = createSettingsPanel();

        final JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        root.add(settings, BorderLayout.NORTH);
        final SimpleInternalFrame note = createNotePanel();
        root.add(note, BorderLayout.SOUTH);

        return root;
    }

    private SimpleInternalFrame createNotePanel() {
        final SimpleInternalFrame note = new SimpleInternalFrame(NOTE);
        final String[] lines = StringTools.getLines(MSG_CHOOSE_FIELD_SEP);
        final JPanel linePanel = new JPanel(new GridLayout(lines.length, 1, 5, 5));
        for (final String line : lines) {
            linePanel.add(new JLabel(line));
        }
        addPadding(linePanel);
        note.add(linePanel);
        return note;
    }

    private SimpleInternalFrame createSettingsPanel() {
        final SimpleInternalFrame settings = new SimpleInternalFrame(SETTINGS);
        final JPanel panel = new JPanel();
        final GridBagBuilder builder = new GridBagBuilder(panel);
        builder.add(new JLabel(M.getString("fileBox.label")));
        builder.addLast(_fileBox);
        builder.add(new JLabel(M.getString("fieldSepBox.label")));
        builder.addLast(_fieldSepBox);
        settings.add(panel);
        addPadding(panel);
        return settings;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.dialogs.KisKisDialog#getActions()
     */
    @Override
    protected List getActions() {
        return Arrays.asList(new Action[] {
                _okAction, _cancelAction
        });
    }

    private void init() {
        _okAction = M.createAction(this, "onOK");
        _cancelAction = M.createAction(this, "onCancel");
        _fileBox = new SelectFileBox();
        _fileBox.setSelectedFile(Settings.getLastFile());
        _fileBox.setSelectionMode(JFileChooser.FILES_ONLY);
        _fieldSepBox = new BasicTextField(D_FIELD_SEP);
    }

    protected void onCancel() {
        close();
    }

    protected void onOK() {
        final File file = _fileBox.getSelectedFile();
        final String fsep = _fieldSepBox.getText();
        if (!MessageBox.showConfirmDialog(M.format(MSG_CONFIRM_START, file.getName()))) {
            return;
        }

        final MessageListener msg = new MessageListener() {
            @Override
            public void showMessage(final String message) {
                MessageBox.showErrorMessage(message);
            }
        };
        final CSVImport imp = new CSVImport(msg, _doc);
        try {
            imp.doImport(file, fsep.charAt(0));
            close();
        } catch (final IOException e) {
            LOG.debug(e, e);
            MessageBox.showErrorMessage(e.getMessage());
        }
    }
}
