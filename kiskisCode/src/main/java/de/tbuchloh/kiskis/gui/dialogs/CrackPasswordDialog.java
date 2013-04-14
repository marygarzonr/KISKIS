package de.tbuchloh.kiskis.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;

import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.kiskis.gui.common.UIConstants;
import de.tbuchloh.kiskis.model.ModelConstants;
import de.tbuchloh.kiskis.model.cracklib.Dictionary;
import de.tbuchloh.kiskis.model.validation.DictionaryPasswordValidator;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.logging.LogFactory;
import de.tbuchloh.util.swing.actions.ActionItem;

/**
 * @author Patrick Spinler, Ruth Betcher, Brent Vrieze
 * @version $Id: CrackPasswordDialog.java,v 1.2 2007/12/09 11:24:41 tbuchloh Exp $
 */
public class CrackPasswordDialog extends KisKisDialog implements ModelConstants, UIConstants {

    private class RunCrackLib extends Thread {

        public RunCrackLib() {
            super("run-cracklib");
        }

        public void endCracklib() {
            LOG.trace("Stopping cracklib thread " + this + " from " + Thread.currentThread());
            crackLibResponse.setText(M.getString("test_stopped"));
            crackLibTime.setText("");
            _stopAction.setEnabled(false);
            // TODO: thats a little bit to hard.
            this.stop();
            LOG.trace("Cracklib Thread " + this + " has been stopped by " + Thread.currentThread());
        }

        @Override
        public void run() {
            LOG.trace("Thread " + this + " has started!");
            final String cracklibDict = Settings.getCracklibDict();
            LOG.info("Using cracklib dictionary=" + cracklibDict);

            final long crackTime = -System.currentTimeMillis();
            
            DictionaryPasswordValidator ipv = new DictionaryPasswordValidator();
            final String value = ipv.validatePassword(_pwd);
            if(value == null) {
                crackLibResponse.setText(M.getString("good_password"));
            } else {
                crackLibResponse.setText(value);
            }
                
            crackLibTime.setText(M.format("cracktime", Long.valueOf(crackTime + System.currentTimeMillis())));
            _stopAction.setEnabled(false);
            LOG.trace("Thread " + this + " has finished!");
        }

    }

    protected static final Log LOG = LogFactory.getLogger();

    protected static final Messages M = new Messages(CrackPasswordDialog.class);

    private static final long serialVersionUID = 1L;

    public static void main(final String[] args) {
        final CrackPasswordDialog dlg = new CrackPasswordDialog(new JFrame(), "germany".toCharArray());
        dlg.setVisible(true);
    }

    protected Action _closeAction = new ActionItem(M.getString("closeAction_title")) { //$NON-NLS-1$

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(final ActionEvent e) {
            close();
        }
    };

    protected char[] _pwd;

    protected Action _stopAction = new ActionItem(M.getString("stopAction_title")) { //$NON-NLS-1$

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(final ActionEvent e) {
            crackLib.endCracklib();
        }
    };

    protected RunCrackLib crackLib;

    protected JLabel crackLibResponse;

    protected JLabel crackLibTime;

    protected JPanel crackPasswordPanel;

    /**
     * creates a new HistoryDialog
     */
    public CrackPasswordDialog(final Frame parent, final char[] pwd) {
        super(parent, M.getString("title"), true); //$NON-NLS-1$
        _pwd = pwd;
        crackLib = new RunCrackLib();
        crackLib.start();
    }

    /**
     * @inheritDoc
     */
    @Override
    protected Component createMainPanel() {
        final JPanel contentPane = new JPanel(new BorderLayout());
        crackPasswordPanel = new JPanel();
        crackPasswordPanel.setLayout(new GridLayout(2, 1));
        crackLibResponse = new JLabel();
        crackLibResponse.setHorizontalAlignment(SwingConstants.CENTER);
        crackLibTime = new JLabel();
        crackLibTime.setHorizontalAlignment(SwingConstants.CENTER);
        crackPasswordPanel.add(crackLibResponse, BorderLayout.NORTH);
        crackPasswordPanel.add(crackLibTime, BorderLayout.SOUTH);
        crackLibResponse.setText(M.getString("testing"));

        contentPane.add(crackPasswordPanel);

        return contentPane;
    }

    /**
     * @inheritDoc
     */
    @Override
    protected List getActions() {
        return Arrays.asList(_closeAction, _stopAction);
    }

}
