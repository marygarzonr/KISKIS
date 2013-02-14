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

package de.tbuchloh.kiskis.gui.common;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Collections;

import javax.swing.Action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.feedback.FeedbackException;
import de.tbuchloh.kiskis.gui.feedback.SubmitBug;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.TextMessageBox;
import de.tbuchloh.util.swing.actions.ActionItem;
import de.tbuchloh.util.swing.dialogs.MessagePane;

/**
 * <b>MessageBox</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public abstract class MessageBox {

    /**
     * Die {@link Messages}
     */
    private static final Messages M = new Messages(MessageBox.class);

    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLog(MessageBox.class);

    private static final String TITLE_ERROR;

    private static final String TITLE_MSG;

    private static final String TITLE_QUESTION;

    private static final String TITLE_UNHANDLED_EXCEPTION;

    private static final String MSG_UNHANDLED_EXCEPTION;

    static {
        final Messages msg = M;
        TITLE_ERROR = msg.getString("ERROR_TITLE"); //$NON-NLS-1$
        TITLE_QUESTION = msg.getString("QUESTION_TITLE"); //$NON-NLS-1$
        TITLE_MSG = msg.getString("MSG_TITLE"); //$NON-NLS-1$
        TITLE_UNHANDLED_EXCEPTION = msg.getString("UNHANDLED_EXCEPTION_TITLE"); //$NON-NLS-1$
        MSG_UNHANDLED_EXCEPTION = msg.getString("UNHANDLED_EXCEPTION_MSG");
    }

    /**
     * @param msg
     *            is the message to display.
     * @return true, if YES was chosen, false otherwise.
     */
    public static boolean showConfirmDialog(final String msg) {
        return MessagePane.showConfirmDialog(findActiveFrame(), msg, null, MessagePane.YES_OPTION
                | MessagePane.CANCEL_OPTION) == MessagePane.YES_OPTION;
    }

    /**
     * @param msg
     *            is the message to display.
     * @return true, if YES was chosen, false otherwise.
     */
    public static boolean showConfirmDialog(Component parent, final String msg) {
        return MessagePane.showConfirmDialog(parent, msg, null, MessagePane.YES_OPTION | MessagePane.CANCEL_OPTION,
                TITLE_QUESTION) == MessagePane.YES_OPTION;
    }

    /**
     * @param msg
     *            is the message to display.
     */
    public static void showErrorMessage(final String msg) {
        MessagePane.showErrorMessage(findActiveFrame(), msg);
    }

    public static void showInformationMessage(final String key, final String msg) {
        MessagePane.showInfoMessage(findActiveFrame(), msg, key);
    }

    /**
     * @param msg
     *            is the message to display.
     */
    public static void showMessageDialog(Component parent, final String msg) {
        MessagePane.showInfoMessage(parent, msg);
    }

    /**
     * @param e
     *            the Exception
     */
    public static void showException(Throwable e) {
        LOG.error(e, e);
        final String msg = String.format(MSG_UNHANDLED_EXCEPTION, //
                TextMessageBox.exceptionToString(e));
        final ActionItem submitBugAction = new ActionItem() {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent ev) {
                try {
                    new SubmitBug().openMail(msg);
                } catch (final FeedbackException e1) {
                    showErrorMessage(e1.getMessage());
                }
            }

        };

        M.fillAction(submitBugAction, "submitBug", null);
        TextMessageBox.showError(null, TITLE_UNHANDLED_EXCEPTION, msg,
                Collections.<Action> singletonList(submitBugAction));
    }

    public static void showErrorTextMessageBox(final String msg) {
        final ActionItem copyAction = new ActionItem() {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                ClipboardHelper.copyToClipboard(msg);
            }

        };
        M.fillAction(copyAction, "copyToClipboard", null);
        TextMessageBox.showError(findActiveFrame(), TITLE_ERROR, msg, Collections.<Action> singletonList(copyAction));
    }

    private static Frame findActiveFrame() {
        final Frame[] frames = Frame.getFrames();
        for (int i = 0; i < frames.length; i++) {
            if (frames[i].isVisible()) {
                return frames[i];
            }
        }
        return null;
    }

}
