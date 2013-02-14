package de.tbuchloh.kiskis.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.kiskis.model.Attachment;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.model.annotations.IgnoresObservable;
import de.tbuchloh.kiskis.persistence.PersistenceException;
import de.tbuchloh.kiskis.persistence.PersistenceManager;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.event.ContentListener;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.dialogs.MessagePane;
import de.tbuchloh.util.swing.widgets.OrderableListWidget;

/**
 * <b>AttachmentBox</b>: models the attachment gui element.
 * 
 * provides adding, saving and deleteing operations.
 * 
 * @author gandalf
 * @version $Id: SecuredElementView.java,v 1.21 2007/12/02 12:44:03 tbuchloh Exp
 *          $
 */
final class AttachmentBox extends JComponent {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    private static final Log LOG = LogFactory.getLog(AttachmentBox.class);

    private static final Messages M = new Messages(AttachmentBox.class);

    private static final MessageFormat MSG_CONFIRM_DELETE = new MessageFormat(
            M.getString("MSG_CONFIRM_DELETE")); //$NON-NLS-1$

    private static final MessageFormat MSG_OVERWRITE = new MessageFormat(
            M.getString("MSG_OVERWRITE")); //$NON-NLS-1$

    private static final String ERR_CHOOSE_ATTACHMENT = M
    .getString("ERR_CHOOSE_ATTACHMENT"); //$NON-NLS-1$

    private static final String ERR_FILE_EXISTS = M
    .getString("ERR_FILE_EXISTS"); //$NON-NLS-1$

    private Action _newAction, _saveAction, _deleteAction;

    private final OrderableListWidget _attachments;

    private final TPMDocument _doc;

    /**
     * creates a new AttachmentBox
     * 
     * @param doc
     *            is the current document.
     */
    public AttachmentBox(final TPMDocument doc) {
        initActions();
        _doc = doc;
        _attachments = new OrderableListWidget();
        _attachments.addSpecialAction(_newAction);
        _attachments.addSpecialAction(_saveAction, true);
        _attachments.addSpecialAction(_deleteAction);
        this.setLayout(new BorderLayout());
        this.add(_attachments);
    }

    public void addContentListener(final ContentListener l) {
        _attachments.addContentListener(l);
    }

    private File chooseSaveFile(final String name) {
        LOG.debug("file=" + name);

        final JFileChooser fc = new JFileChooser(Settings.getLastPath());
        fc.setSelectedFile(new File(name));
        if (fc.showSaveDialog(this) == JFileChooser.CANCEL_OPTION) {
            return null;
        }

        final File selected = fc.getSelectedFile();
        Settings.setLastPath(selected);

        return selected;
    }

    private void doSave(final Attachment a) {
        final File selected = chooseSaveFile(a.getName());
        if (selected != null) {
            if (selected.exists()
                    && !MessageBox.showConfirmDialog(MSG_OVERWRITE
                            .format(new Object[] { selected.getName() }))) {
                return;
            }
            try {
                PersistenceManager.saveAttachmentAs(a, selected);
            } catch (final PersistenceException e) {
                LOG.debug(e, e);
                MessageBox.showErrorMessage(e.getMessage());
            }
        }
    }

    /**
     * @return all the attachments.
     */
    @SuppressWarnings("unchecked")
    public Collection<Attachment> getAttachments() {
        return _attachments.getData();
    }

    private void initActions() {
        _newAction = M.createAction(this, "onNew"); //$NON-NLS-1$
        _saveAction = M.createAction(this, "onSave"); //$NON-NLS-1$
        _deleteAction = M.createAction(this, "onDelete"); //$NON-NLS-1$
    }

    protected void onDelete() {
        final Attachment selected = (Attachment) _attachments
        .getSelectedObject();
        if (selected != null
                && MessageBox.showConfirmDialog(MSG_CONFIRM_DELETE
                        .format(new Object[] { selected.getDescription() }))) {
            _attachments.removeObject(selected);
        }
    }

    @IgnoresObservable
    protected void onNew() {
        final JFileChooser fc = new JFileChooser(Settings.getLastPath());
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.showOpenDialog(this);
        final File selected = fc.getSelectedFile();
        if (selected == null) {
            return;
        }
        if (!selected.isFile()) {
            MessageBox.showErrorMessage(ERR_FILE_EXISTS);
        } else {
            Settings.setLastPath(selected);
            final Attachment att = new Attachment(_doc);
            att.setAttachedFile(selected);
            att.setDescription(selected.getName());
            _attachments.addObject(att);
            MessagePane.showInfoMessage(this,
                    M.format("MSG_ATTACHMENT_SAVED", att.getDescription()), //$NON-NLS-1$
            "MSG_ATTACHMENT_SAVED"); //$NON-NLS-1$
        }
    }

    protected void onSave() {
        final Attachment a = (Attachment) _attachments.getSelectedObject();
        if (a != null) {
            doSave(a);
        } else {
            MessageBox.showErrorMessage(ERR_CHOOSE_ATTACHMENT);
        }
    }

    /**
     * delete all new created attachments.
     */
    public void revertChanges() {
        // does nothing
    }

    /**
     * @param attachments
     */
    @SuppressWarnings("unchecked")
    public void setAttachments(final Collection<Attachment> attachments) {
        _attachments.setData(new ArrayList(attachments));
    }
}