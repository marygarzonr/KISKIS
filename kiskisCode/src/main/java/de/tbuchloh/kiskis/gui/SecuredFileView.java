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
 * http://www.sourceforge.net/projects/kiskis
 */

package de.tbuchloh.kiskis.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.SecuredFile;
import de.tbuchloh.kiskis.persistence.FileFormats;
import de.tbuchloh.kiskis.persistence.PersistenceException;
import de.tbuchloh.kiskis.persistence.PersistenceManager;
import de.tbuchloh.kiskis.util.FileTools;
import de.tbuchloh.kiskis.util.KisKisRuntimeException;
import de.tbuchloh.util.crypto.CryptoException;
import de.tbuchloh.util.event.ContentChangedEvent;
import de.tbuchloh.util.event.ContentListener;
import de.tbuchloh.util.io.FileProcessor;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.widgets.SelectFileBox;

/**
 * <b>SecuredFileView</b>:
 * 
 * @author gandalf
 * @version $Id: SecuredFileView.java,v 1.10 2007/02/18 14:37:47 tbuchloh Exp $
 */
public final class SecuredFileView extends AbstractAccountDetailView {

    private static final long serialVersionUID = 1L;

    private static final Messages M = new Messages(SecuredFileView.class);

    private final SecuredFile _sf;

    private final SelectFileBox _sb;

    private final JLabel _statusField;

    private final Action _encryptAction;

    private final Action _decryptAction;

    /**
     * creates a new SecuredFileView
     * 
     * @param sf
     *            is the element to display
     */
    public SecuredFileView(final ModelNode sf) {
        super();
        _sf = (SecuredFile) sf;
        _sb = new SelectFileBox(_sf.getFile());
        _sb.addContentListener(new ContentListener() {
            @Override
            public void contentChanged(final ContentChangedEvent e) {
                updateStatusLabel();
            }
        });
        _statusField = new JLabel();
        _encryptAction = M.createAction(this, "onEncrypt");
        _decryptAction = M.createAction(this, "onDecrypt");

        this.setLayout(new BorderLayout());

        final JPanel main = new JPanel(new SpringLayout());

        addRow(main, M.getString("SecuredFileView.file_label"), _sb);

        addRow(main, M.getString("SecuredFileView.status_label"), _statusField);

        makeGrid(main, 2);

        this.add(main, BorderLayout.NORTH);

        updateStatusLabel();
    }

    protected void updateStatusLabel() {
        String msg = null;
        if (!_sb.getSelectedFile().exists()) {
            msg = M.getString("SecuredFileView.MSG_NOT_EXISTING"); //$NON-NLS-1$
        } else if (_sb.getSelectedFile().isDirectory()) {
            msg = M.getString("SecuredFileView.MSG_IS_DIRECTORY"); //$NON-NLS-1$
        } else {
            msg = M.getString("SecuredFileView.MSG_IS_EXISTING"); //$NON-NLS-1$
            toggleButtons();
        }
        _statusField.setText(msg);

    }

    private void toggleButtons() {
        try {
            boolean encrypt = true;
            if (PersistenceManager.checkMimeType(_sb.getSelectedFile()) == FileFormats.PGP_FILE) {
                encrypt = false;
            }
            _decryptAction.setEnabled(!encrypt);
            _encryptAction.setEnabled(encrypt);
        } catch (final PersistenceException e) {
            MessageBox.showErrorMessage(e.getMessage());
        }
    }

    /**
     * @see de.tbuchloh.kiskis.gui.SpecialView#storeValues()
     */
    @Override
    protected void store() {
        _sf.setFile(_sb.getSelectedFileAsString());
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.AbstractAccountDetailView#getSpecialActions()
     */
    @Override
    public Collection<Action> getSpecialActions() {
        return Arrays.asList(_decryptAction, _encryptAction);
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.AbstractAccountDetailView#getTitle()
     */
    @Override
    public String getTitle() {
        return M.getString("SecuredFileView.border_title");
    }

    protected void onEncrypt() {
        // TODO remove duplicate code!!!

        if (!isStoreValuesConfirmed()) {
            return;
        }

        getRootView().storeValues();

        if (_sf.getPwd().isEmpty()) {
            MessageBox.showErrorMessage(M.getString("SecuredFileView.err_no_pwd_entered"));
            return;
        }

        final File source = new File(_sf.getFile());
        if (!source.exists()) {
            MessageBox.showErrorMessage(M.format("SecuredFileView.err_file_not_exists", source));
            return;
        }

        try {
            final File target = File.createTempFile("kiskis-", ".tmp");
            FileTools.encrypt(_sf.getPwd().getPwd(), source, target);
            FileProcessor.copy(target, source);
            if (!target.delete()) {
                throw new KisKisRuntimeException(String.format("Cannot delete %1$s", target));
            }
        } catch (final CryptoException e) {
            LOG.debug("Could not encrypt file " + source, e);
            MessageBox.showErrorMessage(e.getMessage());
        } catch (final IOException e) {
            LOG.debug("Could not encrypt file " + source, e);
            MessageBox.showErrorMessage(e.getMessage());
        }
        updateStatusLabel();
    }

    /**
     * @return true, if the view has not been modified or the user confirmed the action
     */
    private boolean isStoreValuesConfirmed() {
        return !getRootView().isModified()
        || MessageBox.showConfirmDialog(this, M.getString("SecuredFileView.q_store_values"));
    }

    protected void onDecrypt() {
        // TODO remove duplicate code!!!

        if (!isStoreValuesConfirmed()) {
            return;
        }

        getRootView().storeValues();

        if (_sf.getPwd().isEmpty()) {
            MessageBox.showErrorMessage(M.getString("SecuredFileView.err_no_pwd_entered"));
            return;
        }

        final File source = new File(_sf.getFile());
        if (!source.exists()) {
            MessageBox.showErrorMessage(M.format("SecuredFileView.err_file_not_exists", source));
            return;
        }

        try {
            final File target = File.createTempFile("kiskis-", ".tmp");
            FileTools.decrypt(_sf.getPwd().getPwd(), source, target);
            FileProcessor.copy(target, source);
            if (!target.delete()) {
                throw new KisKisRuntimeException(String.format("Cannot delete %1$s", target));
            }
        } catch (final CryptoException e) {
            LOG.debug("Could not decrypt file " + source, e);
            MessageBox.showErrorMessage(e.getMessage());
        } catch (final IOException e) {
            LOG.debug("Could not decrypt file " + source, e);
            MessageBox.showErrorMessage(e.getMessage());
        }
        updateStatusLabel();
    }
}
