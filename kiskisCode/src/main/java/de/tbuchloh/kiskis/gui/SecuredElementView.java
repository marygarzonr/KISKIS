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

import static de.tbuchloh.kiskis.gui.common.LnFHelper.createLabel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.common.LnFHelper;
import de.tbuchloh.kiskis.gui.common.UIConstants;
import de.tbuchloh.kiskis.gui.dialogs.CrackPasswordDialog;
import de.tbuchloh.kiskis.gui.dialogs.HistoryDialog;
import de.tbuchloh.kiskis.gui.widgets.BasicTextField;
import de.tbuchloh.kiskis.gui.widgets.PasswordElement;
import de.tbuchloh.kiskis.gui.widgets.PersistentTabPane;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.Password;
import de.tbuchloh.kiskis.model.SecuredElement;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.util.DateUtils;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.TimerTaskCallback;
import de.tbuchloh.util.event.ContentChangedEvent;
import de.tbuchloh.util.event.ContentListener;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.GridBagBuilder;
import de.tbuchloh.util.swing.SpringUtilities;
import de.tbuchloh.util.swing.actions.ActionCallback;
import de.tbuchloh.util.swing.dialogs.MessagePane;
import de.tbuchloh.util.swing.widgets.LinkLabel;
import de.wannawork.jcalendar.JCalendarComboBox;

/**
 * <b>SecuredElementView</b>:
 * 
 * @author gandalf
 * @version $Id: SecuredElementView.java,v 1.21 2007/12/02 12:44:03 tbuchloh Exp $
 */
public final class SecuredElementView extends SpecialView implements SpecialActions, UIConstants {

    private static final long serialVersionUID = 1L;

    private static final Timer TIMER = new Timer();

    private static final Log LOG = LogFactory.getLog(SecuredElementView.class);

    protected static final Messages M = new Messages(SecuredElementView.class);

    protected JTextArea _comment;

    protected JCalendarComboBox _dateField;

    protected SecuredElement _element;

    private Action _historyAction;

    private JButton _historyLink;

    protected JTextField _name;

    protected JCheckBox _neverBox;

    private final ContentChangedListener _passwordChanged = new ContentChangedListener() {
        @Override
        public void contentChanged(final boolean changed) {
            fireContentChangedEvent(changed);
        }
    };

    private PasswordElement _pwdField;

    private Collection<Action> _specialActions;

    private AttachmentBox _attachmentBox;

    private SecuredElementView() {
        // nothing
    }

    /**
     * @param doc
     *            is the current document.
     * @param el
     *            is the object which will be displayed.
     */
    protected SecuredElementView(final TPMDocument doc, final ModelNode el) {
        _element = (SecuredElement) el;
        initAttachmentBox(doc);
        initSpecialActions();
        initNameField();
        initPwdField();
        initHistoryButton();
        initDateField();
        initCommentField();
        init();
        scheduleUpdateViewTask();
    }

    private Component createCommentTab() {
        final JPanel other = new JPanel(new BorderLayout());
        final JScrollPane sp = new JScrollPane();
        sp.getViewport().add(_comment);
        other.add(sp);
        return other;
    }

    private void createNeverBox() {
        _neverBox = new JCheckBox(M.getString("never_label")); //$NON-NLS-1$
        _neverBox.setToolTipText(M.getString("never_tt")); //$NON-NLS-1$
        _neverBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                _dateField.setEnabled(!_neverBox.isSelected());
                fireContentChangedEvent(true);
            }
        });
        _neverBox.setSelected(_element.expiresNever());
    }

    private Component createStatisticTab() {
        final JPanel panel = new JPanel(new SpringLayout());

        // Creation and last change
        panel.add(createLabel(M.getString("created_label"))); //$NON-NLS-1$
        Date time = _element.getCreationDate().getTime();
        panel.add(new JLabel(LONG_DATE.format(time)));

        panel.add(createLabel(M.getString("changed_label"))); //$NON-NLS-1$
        time = _element.getLastChangeDate().getTime();
        panel.add(new JLabel(LONG_DATE.format(time)));

        panel.add(createLabel(M.getString("lastViewed.label"))); //$NON-NLS-1$
        time = _element.getLastViewedDate().getTime();
        final Object[] p = {
                LONG_DATE.format(time), new Integer(_element.getViewCounter())
        };
        panel.add(new JLabel(M.format("lastViewed.text", p))); //$NON-NLS-1$

        SpringUtilities.makeCompactGrid(panel, 3, 2, 25, 25, 25, 25);

        return panel;
    }

    private Component createTabs() {
        final JTabbedPane p = new PersistentTabPane(getClass().getName() + ".bottomFields", //$NON-NLS-1$
                SwingConstants.BOTTOM);
        p.addTab(M.getString("NAME"), createMainTab()); //$NON-NLS-1$
        p.addTab(M.getString("attachments.title"), //$NON-NLS-1$
                _attachmentBox);
        p.addTab(M.getString("STATISTICS"), //$NON-NLS-1$
                createStatisticTab());
        p.addTab(M.getString("comment_border_title"), //$NON-NLS-1$
                createCommentTab());
        p.setBorder(BorderFactory.createEmptyBorder());
        return createTitledPanel(M.getString("border_title"), p); //$NON-NLS-1$
    }

    private Component createMainTab() {
        final JPanel textFields = new JPanel(new SpringLayout());
        textFields.setBorder(LnFHelper.createDefaultBorder());

        final JLabel nameLabel = LnFHelper.createLabel(M.getString("name_label")); //$NON-NLS-1$
        nameLabel.setVerticalAlignment(SwingConstants.TOP);
        nameLabel.setLabelFor(_name);
        textFields.add(nameLabel);
        textFields.add(_name);

        final JLabel pwdLabel = LnFHelper.createLabel(M.getString("password_label")); //$NON-NLS-1$
        pwdLabel.setVerticalAlignment(SwingConstants.TOP);
        pwdLabel.setLabelFor(_pwdField);
        textFields.add(pwdLabel);
        textFields.add(_pwdField);

        final JPanel datePanel = new JPanel();
        final GridBagBuilder builder = new GridBagBuilder(datePanel);
        builder.setAlignment(GridBagConstraints.NORTHWEST);
        builder.add(_dateField);
        builder.add(_neverBox);

        builder.setFill(GridBagConstraints.NONE);
        builder.setAlignment(GridBagConstraints.EAST);
        builder.addLast(_historyLink);
        builder.setFill(GridBagConstraints.BOTH);
        builder.setAlignment(GridBagConstraints.NORTHWEST);

        final JLabel expiresLabel = LnFHelper.createLabel(M.getString("expires_label")); //$NON-NLS-1$
        expiresLabel.setVerticalAlignment(SwingConstants.TOP);
        expiresLabel.setLabelFor(datePanel);
        textFields.add(expiresLabel);
        textFields.add(datePanel);

        SpringUtilities.makeCompactGrid(textFields, 3, 2, 5, 5, 5, 5);

        return textFields;
    }

    /**
     * @return the current name field value.
     */
    public String getNameField() {
        return _name.getText();
    }

    /**
     * @return an empty set
     */
    @Override
    public Collection<Action> getSpecialActions() {
        return _specialActions;
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.JComponent#grabFocus()
     */
    @Override
    public void grabFocus() {
        super.grabFocus();
        _name.selectAll();
        _name.grabFocus();
    }

    private void init() {
        this.setLayout(new BorderLayout(5, 10));
        // this.add(createTextFields(), BorderLayout.NORTH);
        this.add(createTabs(), BorderLayout.NORTH);

        _name.setText(_element.getName());
        _comment.setText(_element.getComment());
    }

    private void initAttachmentBox(final TPMDocument doc) {
        _attachmentBox = new AttachmentBox(doc);
        _attachmentBox.setAttachments(_element.getAttachments());
        _attachmentBox.addContentListener(new ContentListener() {
            @Override
            public void contentChanged(final ContentChangedEvent e) {
                fireContentChangedEvent(true);
            }
        });
    }

    private void initCommentField() {
        _comment = new JTextArea(10, 30);
        _comment.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (!_comment.getText().equals(_element.getComment())) {
                    fireContentChangedEvent(true);
                }
            }
        });
    }

    private void initDateField() {
        _dateField = new JCalendarComboBox(_element.getPwd().getExpires());
        _dateField.setBorder(BorderFactory.createEmptyBorder());
        _dateField.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                fireContentChangedEvent(true);
            }
        });
        createNeverBox();
    }

    private void initHistoryButton() {
        _historyAction = new ActionCallback(this, "showHistoryDialog", //$NON-NLS-1$
                M.getString("historyAction_title")); //$NON-NLS-1$
        _historyLink = new LinkLabel(_historyAction);
    }

    private void initNameField() {
        final BasicTextField name = new BasicTextField();
        name.addContentListener(new ContentListener() {
            @Override
            public void contentChanged(final ContentChangedEvent e) {
                fireContentChangedEvent(true);
            }
        });
        _name = name;
    }

    private void initPwdField() {
        _pwdField = new PasswordElement(_element.getPwd().getPwd());
        _pwdField.addContentChangedListener(_passwordChanged);
    }

    private void initSpecialActions() {
        _specialActions = new ArrayList<Action>();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.SpecialView#revertChanges()
     */
    @Override
    public void revertChanges() {
        _attachmentBox.revertChanges();
    }

    private void scheduleUpdateViewTask() {
        final long viewCounterDelay = Settings.getViewCounterDelay();
        if (viewCounterDelay > 0) {
            TIMER.schedule(new TimerTaskCallback(this, "updateViewProperties"), //$NON-NLS-1$
                    viewCounterDelay);
        }
    }

    /**
     * @param name
     *            the value of the name-field.
     */
    public void setNameField(final String name) {
        _name.setText(name);
    }

    /**
     * @param view
     *            the view that will appear in the center of this element.
     */
    public void setSpecialView(final AbstractAccountDetailView view) {
        if (!view.isHidden()) {
            this.add(createTitledPanel(view.getTitle(), view), BorderLayout.CENTER);
        }
    }

    /**
     * Shows dialog running password cracker. Callback for the button.
     */
    public void showCrackPasswordDialog() {
        final CrackPasswordDialog dlg = new CrackPasswordDialog(MainFrame.getInstance(), _pwdField.getPwd()); // editing
        // here
        dlg.setVisible(true);
    }

    /**
     * shows the latest history of this item. Callback for the button.
     */
    public void showHistoryDialog() {
        final HistoryDialog dlg = new HistoryDialog(MainFrame.getInstance(), _element.getPwdHistory());
        dlg.setVisible(true);
    }

    /**
     * stores the field values in the object fields.
     */
    @Override
    protected void store() {
        _element.setName(_name.getText());
        final Password pwd = new Password(_pwdField.getPwd(), _dateField.getCalendar());
        _element.setPwd(pwd);
        _element.setExpiresNever(_neverBox.isSelected());
        _element.setComment(_comment.getText());
        _element.setLastChangeDate(DateUtils.getCurrentDateTime());
        _element.setAttachments(_attachmentBox.getAttachments());
    }

    protected synchronized void updateViewProperties() {
        if (isShowing()) {
            LOG.debug("set lastViewedDate for: " + _element.getName()); //$NON-NLS-1$
            _element.updateLastViewed();
            MessagePane.showInfoMessage(this, M.format("updateViewProperties.info"), //
            "updateViewProperties");
            MainFrame.showStatus(M.getString("updateViewProperties.finished"));
        } else {
            LOG.debug("does NOT set lastViewedDate for: " + _element.getName()); //$NON-NLS-1$
        }
    }
}
