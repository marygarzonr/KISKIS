package de.tbuchloh.kiskis.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.AWTEventListener;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.kiskis.gui.common.UIConstants;
import de.tbuchloh.kiskis.gui.dialogs.FileDialog;
import de.tbuchloh.kiskis.gui.dialogs.ImportDialog;
import de.tbuchloh.kiskis.gui.dialogs.MessageBoxErrorHandler;
import de.tbuchloh.kiskis.gui.dialogs.OptionsDialog;
import de.tbuchloh.kiskis.gui.dialogs.PasswordDialog;
import de.tbuchloh.kiskis.gui.dialogs.PasswordDialog.Buttons;
import de.tbuchloh.kiskis.gui.dialogs.TemplateOverviewDialog;
import de.tbuchloh.kiskis.gui.feedback.FeedbackException;
import de.tbuchloh.kiskis.gui.feedback.SubmitBug;
import de.tbuchloh.kiskis.gui.feedback.SubmitFeatureRequest;
import de.tbuchloh.kiskis.gui.systray.IMainFrame;
import de.tbuchloh.kiskis.model.Password;
import de.tbuchloh.kiskis.model.SecuredElement;
import de.tbuchloh.kiskis.model.StandardDocumentFactory;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.model.cracklib.Dictionary;
import de.tbuchloh.kiskis.model.validation.DictionaryPasswordValidator;
import de.tbuchloh.kiskis.model.validation.EmptyPasswordValidator;
import de.tbuchloh.kiskis.model.validation.WeakPasswordValidator;
import de.tbuchloh.kiskis.persistence.FileFormats;
import de.tbuchloh.kiskis.persistence.ICryptoContext;
import de.tbuchloh.kiskis.persistence.PasswordCryptoContext;
import de.tbuchloh.kiskis.persistence.PasswordProxy;
import de.tbuchloh.kiskis.persistence.PersistenceException;
import de.tbuchloh.kiskis.persistence.PersistenceManager;
import de.tbuchloh.kiskis.persistence.exporter.CSVExporter;
import de.tbuchloh.kiskis.persistence.exporter.ExportException;
import de.tbuchloh.kiskis.persistence.exporter.IExporter;
import de.tbuchloh.kiskis.persistence.exporter.XSLExporter;
import de.tbuchloh.kiskis.util.BuildProperties;
import de.tbuchloh.kiskis.util.FileTools;
import de.tbuchloh.kiskis.util.KisKisRuntimeException;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.StopWatch;
import de.tbuchloh.util.TimerTaskCallback;
import de.tbuchloh.util.crypto.CryptoException;
import de.tbuchloh.util.crypto.DigestFactory;
import de.tbuchloh.util.crypto.InvalidCryptoInstallationException;
import de.tbuchloh.util.crypto.SymmetricAlgo;
import de.tbuchloh.util.crypto.TripleDES;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.DescriptiveItem;
import de.tbuchloh.util.swing.ListSelectionDlg;
import de.tbuchloh.util.swing.TextMessageBox;
import de.tbuchloh.util.swing.Toolkit;
import de.tbuchloh.util.swing.actions.ActionCallback;
import de.tbuchloh.util.swing.dialogs.AboutDialog;
import de.tbuchloh.util.swing.dialogs.HTMLViewDialog;
import de.tbuchloh.util.swing.dialogs.MessagePane;
import de.tbuchloh.util.swing.dialogs.TipOfTheDayDialog;
import de.tbuchloh.util.swing.widgets.LRUFilesMenu;

/**
 * <b>MainFramePanel</b>: the main component for the frame.
 * 
 * @author gandalf
 * @version $Id: MainFrame.java,v 1.43 2007/04/27 11:03:14 tbuchloh Exp $
 */
public final class MainFramePanel extends JPanel implements ContentChangedListener {

    /**
     * This task tries to evict sensitive information out of memory. Allocates as much memory as possible and writes 1
     * into each byte.
     * 
     * @author Tobias Buchloh (gandalf)
     * @version $Id: $
     * @since 18.02.2012
     */
    private static final class CleanupMemoryTimerTask extends TimerTask {

        private static final Object LOCK = new Object();

        @Override
        public void run() {
            synchronized (LOCK) {
                final StopWatch stopWatch = new StopWatch();
                LOG.info("cleanupMemory entering ...");
                final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

                MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
                final long used = heapMemoryUsage.getUsed();
                final long committed = heapMemoryUsage.getCommitted();
                final long dummySize = committed - used;
                LOG.info(String.format("writing dummy objects. dummySize=%1$s, memoryUsage=%2$s ...", dummySize,
                        heapMemoryUsage));
                try {
                    // we want to fill memory up to the end, but don't want to get an overrun
                    final int chunkSize = 1024;
                    final List<byte[]> chunks = new LinkedList<byte[]>();
                    while (heapMemoryUsage.getUsed() + 2 * chunkSize < heapMemoryUsage.getCommitted() * 0.95) {
                        final byte[] chunk = new byte[chunkSize];
                        for (int i = 0; i < chunk.length; ++i) {
                            chunk[i] = 0;
                        }
                        // System.out.println(memoryMXBean.getHeapMemoryUsage());
                        chunks.add(chunk);
                        heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
                    }
                } catch (final OutOfMemoryError e) {
                    LOG.warn("OutOfMemoryError: " + e);
                } finally {
                    LOG.info("Running gc ...");
                    memoryMXBean.gc();
                    memoryMXBean.gc();
                }

                LOG.info(String.format("cleanupMemory leaving ... time=%1$s, memoryUsage=%2$s",
                        stopWatch.getDuration(), memoryMXBean.getHeapMemoryUsage()));
            }
        }

    }

    private final class AutoLockTimer implements AWTEventListener {

        private final AtomicBoolean _isActive;

        private TimerTaskCallback _last;

        public AutoLockTimer() {
            java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(this,
                    AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
            _isActive = new AtomicBoolean(true);
        }

        /**
         * Overridden!
         * 
         * @see java.awt.event.AWTEventListener#eventDispatched(java.awt.AWTEvent)
         */
        @Override
        public void eventDispatched(final AWTEvent event) {
            reschedule();
        }

        protected void onLock() {
            LOG.debug("locking screen"); //$NON-NLS-1$
            if (!_pwdBuffer.isValidPassword(new char[0])) {
                final JButton button = new JButton(M.createAction(this, "onUnlock")); //$NON-NLS-1$
                _isActive.set(false);
                _parent.setMainView(Toolkit.centerWidget(button));
                _parent.getJMenuBar().setVisible(false);
                if (_parent.getSystemTray().isValid()) {
                    _parent.setVisible(false);
                    _parent.onClosing();
                }
                LOG.debug("screen locked ..."); //$NON-NLS-1$
            } else {
                LOG.debug("screen not locked ..."); //$NON-NLS-1$
            }
        }

        protected void onUnlock() {
            LOG.debug("try unlock"); //$NON-NLS-1$
            final PasswordDialog dlg = new PasswordDialog(_parent, false);
            Toolkit.centerWindow(_parent, dlg);
            dlg.setVisible(true);
            if (dlg.getButton() == Buttons.APPROVED_BUTTON && _pwdBuffer.isValidPassword(dlg.getPassword())) {
                LOG.debug("unlock successful!"); //$NON-NLS-1$
                _parent.setMainView(MainFramePanel.this);
                _parent.getJMenuBar().setVisible(true);
                _isActive.set(true);
                reschedule();
            }
        }

        private synchronized void reschedule() {
            if (_last != null) {
                _last.cancel();
                _last = null;
            }
            if (!isLocked() && Settings.getLockAfterMin() > 0) {
                _last = new TimerTaskCallback(this, "onLock"); //$NON-NLS-1$
                _timer.schedule(_last, Settings.getLockAfterMin() * 60 * 1000);
            }
        }

        /**
         * @return true if the screen is locked currently
         */
        public boolean isLocked() {
            return !_isActive.get();
        }

    }

    private final class AutoSaveTimer {

        private TimerTask _autoSaveTask;

        public AutoSaveTimer() {
            _autoSaveTask = createTask();
            scheduleTask();
        }

        private TimerTaskCallback createTask() {
            return new TimerTaskCallback(this, "save"); //$NON-NLS-1$
        }

        protected void save() {
            LOG.debug("Auto-save triggered!");
            // if autolock is active we need a buffered password
            if (Settings.isAutoSaveEnabled() && !(_autoLock.isLocked() && !_pwdBuffer.hasBufferedPwd())) {
                if (MainFramePanel.this.saveDocument()) {
                    final String msg = M.getString("AutoSaveTimer.showStatus");
                    MainFrame.showStatus(msg);
                }
            } else {
                LOG.debug("Auto-save skipped");
            }

            scheduleTask();

            LOG.debug("Auto-save done");
        }

        private void scheduleTask() {
            _autoSaveTask.cancel();
            final long autoSaveDelay = Settings.getAutoSaveDelay();
            if (Settings.isAutoSaveEnabled() && autoSaveDelay > 0) {
                LOG.debug("reschedule auto save feature in " + autoSaveDelay); //$NON-NLS-1$
                _autoSaveTask = createTask();
                _timer.schedule(_autoSaveTask, autoSaveDelay);
            }
        }
    }

    private final class EditHandler {

        private final Action _copyAction = M.createAction(this, "copyNode"); //$NON-NLS-1$

        private final Action _cutAction = M.createAction(this, "onCutNode"); //$NON-NLS-1$

        private final Action _deleteAction = M.createAction(this, "onDeleteNode"); //$NON-NLS-1$

        private final Action _editTemplatesAction = M.createAction(this, "onEditTemplates"); //$NON-NLS-1$

        private final Action _newGroupNodeAction = M.createAction(this, "onNewGroupNode"); //$NON-NLS-1$

        private final Action _newNodeAction = M.createAction(this, "onNewNode"); //$NON-NLS-1$

        private final Action _pasteAction = M.createAction(this, "onPasteNode"); //$NON-NLS-1$

        private final Action _renameAction = M.createAction(this, "onRenameNode"); //$NON-NLS-1$

        private final Action _searchAction = M.createAction(this, "onSearchNode"); //$NON-NLS-1$

        protected void copyNode() {
            _mainView.copyNode();
        }

        /**
         * @return the associated menu.
         */
        public JMenu getMenu() {
            final JMenu fileMenu = new JMenu(M.getString("edit_menu")); //$NON-NLS-1$
            fileMenu.setMnemonic(M.getChar("edit_menu_mne")); //$NON-NLS-1$
            final Action[] actions = {
                    _editTemplatesAction, _renameAction, null, _newGroupNodeAction, _newNodeAction, _deleteAction,
                    null, _copyAction, _cutAction, _pasteAction, null, _searchAction, null, _optionsAction
            };
            addActions(fileMenu, actions);
            return fileMenu;
        }

        /**
         * @return the associated toolbar.
         */
        public JToolBar getToolBar() {
            final JToolBar bar = new JToolBar();
            bar.setFloatable(false);
            final Action[] actions = {
                    _newGroupNodeAction, _newNodeAction, _deleteAction, null, _searchAction
            };
            addActions(bar, actions);
            return bar;
        }

        protected void onCutNode() {
            _mainView.cutNode();
            contentChanged(true);
        }

        protected void onDeleteNode() {
            _mainView.deleteNode();
            contentChanged(true);
        }

        protected void onEditTemplates() {
            final TemplateOverviewDialog dlg = new TemplateOverviewDialog(MainFrame.getInstance(), getDoc());
            dlg.setVisible(true);
        }

        protected void onNewGroupNode() {
            _mainView.createNewGroupNode();
            contentChanged(true);
        }

        protected void onNewNode() {
            _mainView.createNewNode();
            contentChanged(true);
        }

        protected void onPasteNode() {
            _mainView.pasteNode();
            contentChanged(true);
        }

        protected void onRenameNode() {
            _mainView.renameNode();
            contentChanged(true);
        }

        protected void onSearchNode() {
            _mainView.findNode();
        }

    }

    private final class HelpHandler {

        protected void onCheckUpdates() {
            MainFrame.getInstance().checkUpdates(true);
        }

        protected void onOpenManual() {
            final File localFile = new File("doc/manual/index.html");
            if (localFile.exists()) {
                // prefer local installation
                try {
                    Desktop.getDesktop().open(localFile);
                } catch (final IOException e) {
                    LOG.debug("Cannot use Desktop.open for file=" + localFile, e);

                    final HTMLViewDialog dlg = new HTMLViewDialog(MainFrame.getInstance(), M.getLabel("onOpenManual")
                            .replaceAll("&", ""), false);
                    try {
                        dlg.setDocument(localFile.toURL());
                        dlg.setVisible(true);
                    } catch (final Exception e1) {
                        MessageBox.showException(e1);
                    }
                }
            } else {
                final String website = BuildProperties.getWebsite();
                try {
                    try {
                        Desktop.getDesktop().browse(new URI(website));
                    } catch (final IOException e) {
                        LOG.debug(e, e);
                        MessageBox.showErrorMessage(e.getMessage());
                    }
                } catch (final URISyntaxException e) {
                    throw new KisKisRuntimeException("Website URL could not be parsed. website=" + website, e);
                }
            }

        }

        protected void showAbout() {
            try {
                final AboutDialog dlg = AboutDialog.create(_parent,
                        M.getString("AboutDialog.title"), //$NON-NLS-1$
                        BuildProperties.getBuildProperties(), MainFrame.class.getResource(LICENSE),
                        MainFrame.class.getResource(README), MainFrame.class.getResource(CHANGELOG));
                dlg.setSystemInfo();
                Toolkit.resizeToDesktop(dlg, 0.5f);
                Toolkit.centerWindow(MainFramePanel.this, dlg);
                dlg.setVisible(true);
            } catch (final IOException e) {
                throw new KisKisRuntimeException("Could not open AboutDialog", e);
            }
        }

        /**
         * @return
         */
        public JMenu getMenu() {
            final JMenu fileMenu = new JMenu(M.getString("help_menu")); //$NON-NLS-1$
            fileMenu.setMnemonic(M.getChar("help_menu_mne")); //$NON-NLS-1$

            final Action[] helpMenuActions = {
                    M.createAction(this, "onOpenManual"), M.createAction(this, "onShowTips"), null,
                    M.createAction(this, "onCheckUpdates"), M.createAction(this, "onShowSelftest"), null,
                    M.createAction(this, "onSubmitBug"), M.createAction(this, "onSubmitFeatureRequest"), null,
                    M.createAction(this, "showAbout")
            };
            addActions(fileMenu, helpMenuActions);
            return fileMenu;
        }

        protected void onShowTips() {
            showTipOfTheDay(true);
        }

        protected void onShowSelftest() {
            showSelftest();
        }

        protected void onSubmitBug() {
            try {
                new SubmitBug().openMail(null);
            } catch (final FeedbackException e1) {
                MessageBox.showErrorTextMessageBox(e1.getMessage());
            }
        }

        protected void onSubmitFeatureRequest() {
            try {
                new SubmitFeatureRequest().openMail(null);
            } catch (final FeedbackException e1) {
                MessageBox.showErrorTextMessageBox(e1.getMessage());
            }
        }

    }

    private final class ExportHandler {

        private final Action _exportCSV;

        private final Action _exportHTML;

        private final Action _exportUserDefinedXSL;

        private final Action _exportXML;

        public ExportHandler() {
            _exportCSV = M.createAction(this, "onExportCSV");
            _exportHTML = M.createAction(this, "onExportHTML");
            _exportXML = M.createAction(this, "onExportXML");
            _exportUserDefinedXSL = M.createAction(this, "onExportUserDefinedXSL");
        }

        protected void export(final IExporter exporter) {
            final String key = "ExportHandler.warn_msg";
            MessagePane.showWarningMessage(MainFramePanel.this, M.getString(key), key);
            try {
                final File f = getFile(M.getString("ExportHandler.choose_outputfile_title"));
                if (f != null) {
                    exporter.export(_doc, f);
                    MainFrame.showStatus(M.format("ExportHandler.finished", //
                            FileTools.getShortAbsoluteFilename(f)));
                }
            } catch (final ExportException e) {
                LOG.debug(e, e);
                MessageBox.showErrorMessage(e.getMessage());
            }
        }

        private File getFile(final String title) {
            final JFileChooser fc = new JFileChooser(Settings.getLastFile());
            fc.setDialogTitle(title);
            if (fc.showSaveDialog(MainFramePanel.this) == JFileChooser.APPROVE_OPTION) {
                final File selectedFile = fc.getSelectedFile();
                return selectedFile;
            }
            return null;
        }

        public JMenu getMenu() {
            final JMenu menu = new JMenu(M.getString("ExportHandler.menu_label")); //$NON-NLS-1$
            menu.setMnemonic(M.getChar("ExportHandler.menu_mne")); //$NON-NLS-1$
            final Action[] actions = {
                    _exportCSV, _exportHTML, _exportXML, _exportUserDefinedXSL
            };
            addActions(menu, actions);
            menu.setIcon(loadIcon(M.getString("ExportHandler.menu_icon")));
            return menu;
        }

        protected void onExportCSV() {
            export(new CSVExporter());
        }

        protected void onExportHTML() {
            export(new XSLExporter(ClassLoader.getSystemResource("export/kiskis-html.xsl")));
        }

        protected void onExportUserDefinedXSL() {
            final File xsl = getFile(M.getString("ExportHandler.choose_stylesheet_title"));
            if (xsl != null) {
                try {
                    export(new XSLExporter(xsl.toURL()));
                } catch (final MalformedURLException e) {
                    throw new Error(e);
                }
            }
        }

        protected void onExportXML() {
            export(new XSLExporter(ClassLoader.getSystemResource("transform/kiskis-id.xsl")));
        }

    }

    private final class PasswordBuffer implements PasswordProxy {

        private Password _bufferedPwd;

        private Password _givenPwd;;

        private TimerTask _disposeTask;

        private byte[] _pwdHash;

        public PasswordBuffer() {
            _disposeTask = createTask();
            reset();
            scheduleTask();
        }

        /**
         * @return true if a password is buffered
         */
        public synchronized boolean hasBufferedPwd() {
            return _bufferedPwd != null;
        }

        private boolean checkPassword(final char[] pwd) {            
            if(pwd != null && pwd.length <= 0) {
                // confirm null-password
                System.out.println("Password is empty............");
                return MessageBox.showConfirmDialog(MainFrame.getInstance(), //
                        M.getString("no_pwd_warning"));
            }

            if (Settings.isCheckingMasterPassword()) {
                Double bitSize = Password.checkEffectiveBitSize(pwd);
                if(bitSize < 40) {
                    final BigDecimal two = new BigDecimal(2);
                    return MessageBox.showConfirmDialog(MainFrame.getInstance(), //
                            M.format("weak_pwd_warning", two.pow(bitSize.intValue())));
                }
                
                try {
                   Dictionary dic = Dictionary.open(Settings.getCracklibDict());
                   String value = dic.lookup(new String(pwd));
                   if(value != null) {
                       return MessageBox.showConfirmDialog(MainFrame.getInstance(), //
                             M.getString("dictionary_pwd_warning"));
                   }
                } catch (IOException e) {
                    LOG.warn("Could not open dictionary! Maybe not installed", e);
                }
            }
            return true;
        }

        private TimerTaskCallback createTask() {
            return new TimerTaskCallback(this, "reset"); //$NON-NLS-1$
        }

        private synchronized char[] doAskForPassword(File file, final boolean confirm) {
            LOG.debug("retrieve password!"); //$NON-NLS-1$
            if (confirm) {
                final String key = "MainFramePanel.info_msg";
                MessagePane.showInfoMessage(MainFrame.getInstance(), M.getString(key), key);
            }
            final PasswordDialog pdlg = new PasswordDialog(MainFrame.getInstance(), confirm);
            final String filename = FileTools.getShortAbsoluteFilename(file);
            pdlg.setMessage(M.format("enter_password_message", filename));
            pdlg.setMessageTooltip(file.getAbsolutePath());
            pdlg.setVisible(true);
            if (pdlg.getButton() == Buttons.APPROVED_BUTTON) {
                final char[] pwd = pdlg.getPassword();

                if (!confirm || checkPassword(pwd)) {
                    _pwdHash = hash(pwd);
                    _givenPwd = new Password(pwd);
                    if (Settings.isBufferingPwd()) {
                        LOG.debug("Buffering entered password!"); //$NON-NLS-1$
                        _bufferedPwd = new Password(pwd);
                        scheduleTask();
                    }
                    return pwd;
                }
            }
            return null;
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.persistence.PasswordProxy#getPassword()
         */
        @Override
        public synchronized char[] getPassword(File file) {
            return getPassword(file, false);
        }

        @Override
        public synchronized char[] getPassword(File file, final boolean confirm) {
            // TODO 09.11.2010, gandalf: too complex code
            if (hasGivenPassword()) {
                return _givenPwd.getPassword(file);
            } else if (hasBufferedPwd()) {
                return _bufferedPwd.getPassword(file);
            } else {
                return doAskForPassword(file, confirm);
            }
        }

        public boolean hasGivenPassword() {
            return _givenPwd != null;
        }

        private byte[] hash(final char[] input) {
            return DigestFactory.digest(DigestFactory.MD5, input);
        }

        public synchronized void forgetGivenPassword() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Forgetting given password");
            }
            _givenPwd = null;
        }

        /**
         * @param password
         *            the password to compare with
         * @return true, if the hashes are equal.
         */
        public boolean isValidPassword(final char[] password) {
            return Arrays.equals(_pwdHash, hash(password));
        }

        public synchronized void reset() {
            LOG.debug("dispose password buffer!"); //$NON-NLS-1$
            _bufferedPwd = null;
            _givenPwd = null;
        }

        /**
         * reset hash and password buffer.
         */
        public synchronized void resetAll() {
            reset();
            resetHash();
        }

        private synchronized void resetHash() {
            _pwdHash = hash(new char[0]);
        }

        private synchronized void scheduleTask() {
            _disposeTask.cancel();
            if (Settings.getPwdDisposeDelay() > 0) {
                LOG.debug("reschedule password disposal feature"); //$NON-NLS-1$
                _disposeTask = createTask();
                _timer.schedule(_disposeTask, Settings.getPwdDisposeDelay());
            }
        }
    }

    final class ReportHandler {

        private final Action _expiredNodesAction = M.createAction(this, "onExpiredNodes"); //$NON-NLS-1$

        private final Action _lvNodesAction = M.createAction(this, "onLvNodes"); //$NON-NLS-1$

        private final Action _mruNodesAction = M.createAction(this, "onMRUNodes"); //$NON-NLS-1$

        public JMenu getMenu() {
            final JMenu menu = new JMenu(M.getString("REPORT_MENU_TITLE")); //$NON-NLS-1$
            menu.setMnemonic(M.getChar("REPORT_MENU_MNE")); //$NON-NLS-1$
            menu.add(_mruNodesAction);
            menu.add(_lvNodesAction);
            menu.add(_expiredNodesAction);
            return menu;
        }

        public JToolBar getToolBar() {
            final JToolBar bar = new JToolBar();
            bar.setFloatable(false);
            bar.add(_mruNodesAction);
            bar.add(_lvNodesAction);
            return bar;
        }

        protected void onExpiredNodes() {
            final List<SecuredElement> l = getDoc().getExpiredElements();
            final List<DescriptiveItem> items = new ArrayList<DescriptiveItem>();
            for (final SecuredElement s : l) {
                final String name = M.format("EXPIRED_ITEM", s.getName(),
                        UIConstants.SHORT_DATE.format(s.getPwd().getExpires().getTime()));
                final DescriptiveItem item = new DescriptiveItem(name, s);
                items.add(item);
            }
            selectAndShow(items, M.getString("onExpiredNodes.label")); //$NON-NLS-1$
        }

        protected void onLvNodes() {
            final List<SecuredElement> l = getDoc().getLastViewedElements(10);
            final List<DescriptiveItem> items = new ArrayList<DescriptiveItem>();
            for (final SecuredElement s : l) {
                final String name = M.format("LV_ITEM", s.getName(),
                        UIConstants.LONG_DATE.format(s.getLastViewedDate().getTime()));
                final DescriptiveItem item = new DescriptiveItem(name, s);
                items.add(item);
            }
            selectAndShow(items, M.getString("onLvNodes.label")); //$NON-NLS-1$
        }

        protected void onMRUNodes() {
            final List<SecuredElement> l = getDoc().getMostRecentlyViewedElements(10);
            final List<DescriptiveItem> items = new ArrayList<DescriptiveItem>();
            for (final SecuredElement s : l) {
                final String name = M.format("MRV_ITEM", s.getName(), String.valueOf(s.getViewCounter()));
                final DescriptiveItem item = new DescriptiveItem(name, s);
                items.add(item);
            }
            selectAndShow(items, M.getString("onMRUNodes.label")); //$NON-NLS-1$
        }

    }

    /**
     * @author Tobias Buchloh (gandalf)
     * @since 21.10.2010
     */
    private class ViewHandler {

        private JCheckBoxMenuItem _showingArchivedItemsBox;

        /**
         * @return the menu
         */
        public JMenu getMenu() {
            final JMenu fileMenu = new JMenu(M.getString("view_menu")); //$NON-NLS-1$
            fileMenu.setMnemonic(M.getChar("view_menu_mne")); //$NON-NLS-1$
            _showingArchivedItemsBox = new JCheckBoxMenuItem(//
                    M.createAction(this, "onShowingArchivedItems"));
            _showingArchivedItemsBox.setSelected(Settings.isShowingArchivedItems());
            fileMenu.add(_showingArchivedItemsBox);
            return fileMenu;
        }

        /**
         * Hook method
         */
        protected void onShowingArchivedItems() {
            Settings.setShowingArchivedItems(_showingArchivedItemsBox.isSelected());
            _mainView.refreshView();
        }
    }

    private static final String CHANGELOG = "CHANGELOG"; //$NON-NLS-1$

    private static final String LICENSE = "LICENSE"; //$NON-NLS-1$

    protected static final Log LOG = LogFactory.getLog(MainFramePanel.class);

    public static final Messages M = MainFrame.M;

    private static final MessageFormat MSG_FILE_NOT_FOUND = new MessageFormat(M.getString("MSG_FILE_NOT_FOUND")); //$NON-NLS-1$

    private static final String README = "README"; //$NON-NLS-1$

    private static final long serialVersionUID = 1L;

    private static final String TITLE = M.getString("window_title"); //$NON-NLS-1$

    protected static void addActions(final JMenu fileMenu, final Action[] actions) {
        for (int i = 0; i < actions.length; ++i) {
            if (actions[i] == null) {
                fileMenu.addSeparator();
            } else {
                fileMenu.add(actions[i]);
            }
        }
    }

    /**
     * Displays the selftest result
     */
    public void showSelftest() {
        final String result = PersistenceManager.checkCryptography();
        TextMessageBox.showInformation(_parent, result);
    }

    protected static void addActions(final JToolBar fileMenu, final Action[] actions) {
        for (int i = 0; i < actions.length; ++i) {
            if (actions[i] == null) {
                fileMenu.addSeparator();
            } else {
                fileMenu.add(actions[i]);
            }
        }
    }

    public static Icon loadIcon(final String filename) {
        final URL url = MainFrame.class.getResource(filename);
        return new ImageIcon(url);
    }

    private final AutoLockTimer _autoLock;

    private final AutoSaveTimer _autoSave;

    protected Action _changeMasterPwdAction = M.createAction(this, "onChangeMasterPwd"); //$NON-NLS-1$

    private final Action _closeDocAction = M.createAction(this, "closeDoc"); //$NON-NLS-1$

    private ICryptoContext _cryptoContext;

    private TPMDocument _doc;

    private final EditHandler _editHandler;

    private final ExportHandler _exportHandler;

    private final Action _importAction = M.createAction(this, "onImport"); //$NON-NLS-1$

    private final Action _lockScreenAction = M.createAction(this, IMainFrame.LOCK_SCREEN);

    private final LRUFilesMenu _lruFileMenu;

    protected MainView _mainView;

    private boolean _modified;

    private final Action _newFileAction = M.createAction(this, "newDocument"); //$NON-NLS-1$

    private final Action _openFileAction = M.createAction(this, IMainFrame.OPEN_DOCUMENT);

    private final Action _optionsAction = M.createAction(this, "onShowOptions"); //$NON-NLS-1$

    protected final MainFrame _parent;

    protected PasswordBuffer _pwdBuffer;

    private final Action _quitAction = M.createAction(this, IMainFrame.QUIT);

    private final ReportHandler _reportHandler;

    private final Action _saveAction = M.createAction(this, "saveDocument"); //$NON-NLS-1$

    private final Action _saveAsAction = M.createAction(this, "saveDocumentAs"); //$NON-NLS-1$

    protected Timer _timer;

    private JToolBar _toolbar;

    private final ViewHandler _viewHandler;

    private final HelpHandler _helpHandler;

    public MainFramePanel(final MainFrame parent) {
        super();
        setLayout(new BorderLayout());
        _parent = parent;
        _timer = new Timer();
        _pwdBuffer = new PasswordBuffer();
        _autoSave = new AutoSaveTimer();
        _autoLock = new AutoLockTimer();
        _reportHandler = new ReportHandler();
        _editHandler = new EditHandler();
        _viewHandler = new ViewHandler();
        _exportHandler = new ExportHandler();
        _helpHandler = new HelpHandler();
        _lruFileMenu = new LRUFilesMenu(5);
        _lruFileMenu.addListener(new LRUFilesMenu.FileSelectionListener() {

            @Override
            public void selected(final File file) {
                loadFile(file);
            }

        });

        init();
        initDocument();
        initToolBar();

        _lruFileMenu.loadLRUFiles(Settings.getPreferences());
    }

    protected void closeDoc() {
        if (confirmCloseDoc()) {
            initDocument();
        }
    }

    public synchronized boolean confirmCloseDoc() {
        if (_modified) {
            return MessageBox.showConfirmDialog(_parent, M.format("save_on_close_msg", getDoc()));
        }
        // has not been modified => OK
        return true;
    }

    @Override
    public void contentChanged(final boolean changed) {
        LOG.debug("Content changed called!");
        _modified = changed;
        updateTitle();
        _mainView.updateTree();
    }

    private ICryptoContext createCryptoContext(final File file) {
        SymmetricAlgo algo = getCryptoAlgorithm();
        if (file.getName().endsWith("3des")) {
            algo = new TripleDES();
        }
        return new PasswordCryptoContext(algo, _pwdBuffer, file);
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu(M.getString("file_menu")); //$NON-NLS-1$
        fileMenu.setMnemonic(M.getChar("file_menu_mne")); //$NON-NLS-1$
        final Action[] first = {
                _newFileAction, _openFileAction, _saveAction, _saveAsAction, _closeDocAction, null,
                _changeMasterPwdAction, _importAction
        };
        addActions(fileMenu, first);
        fileMenu.add(_exportHandler.getMenu());
        final Action[] second = {
                null, _lockScreenAction, null, _quitAction
        };
        addActions(fileMenu, second);
        fileMenu.addSeparator();
        _lruFileMenu.addToMenu(fileMenu);

        _changeMasterPwdAction.setEnabled(Settings.isBufferingPwd());

        return fileMenu;
    }

    /**
     * @param ctx
     *            the {@link ICryptoContext}
     */
    protected synchronized void doLoad(final ICryptoContext ctx) {
        final File selectedFile = ctx.getFile();
        if (selectedFile.exists()) {
            _lruFileMenu.newFile(selectedFile);
            Settings.setLastFile(selectedFile);
            try {
                setDoc(PersistenceManager.load(ctx, new MessageBoxErrorHandler()));
                _cryptoContext = ctx;
                updateTitle();
                _mainView.setRootNode(getDoc());
            } catch (final PersistenceException e) {
                LOG.debug(e, e);
                _pwdBuffer.resetAll();
                MessageBox.showErrorMessage(e.getMessage());
            } catch (final CryptoException e) {
                LOG.debug(e, e);
                _pwdBuffer.resetAll();
                MessageBox.showErrorMessage(e.getMessage());
            } catch (final InvalidCryptoInstallationException e) {
                LOG.debug(e, e);
                _pwdBuffer.resetAll();
                MessageBox.showMessageDialog(this, e.getMessage());
            } finally {
                _pwdBuffer.forgetGivenPassword();
                cleanupMemory();
            }
        } else {
            JOptionPane.showMessageDialog(MainFramePanel.this, MSG_FILE_NOT_FOUND.format(new Object[] {
                    selectedFile
            }));
        }
    }

    /**
     * Trys to evict sensitive information out of memory
     */
    private void cleanupMemory() {
        _timer.schedule(new CleanupMemoryTimerTask(), 5000);
    }

    /**
     * @param ctx
     *            the {@link ICryptoContext}
     */
    protected void doSave(final ICryptoContext ctx) {
        final File newFile = ctx.getFile();
        // we need to ask for the password
        final char[] pwd = _pwdBuffer.getPassword(newFile, true);
        synchronized (this) {
            if (pwd != null) {
                try {
                    // OK, password was given so let's continue
                    PersistenceManager.saveAs(getDoc(), ctx, true);
                    cleanupMemory();

                    _lruFileMenu.newFile(newFile);

                    contentChanged(false);

                    final String succMsg = M.format("doSave.successful", newFile.getName());
                    MainFrame.showStatus(succMsg);

                    if (PersistenceManager.checkMimeType(newFile) != FileFormats.PGP_FILE) {
                        final String statusMsg = M.format("doSave.notEncrypted", newFile.getName());
                        MessagePane.showWarningMessage(this, statusMsg);
                    }
                } catch (final PersistenceException e) {
                    LOG.debug(e, e);
                    MessageBox.showErrorMessage(e.getMessage());
                } catch (final CryptoException e) {
                    LOG.debug(e, e);
                    MessageBox.showErrorMessage(e.getMessage());
                } catch (final InvalidCryptoInstallationException e) {
                    LOG.debug(e, e);
                    MessageBox.showMessageDialog(this, e.getMessage());
                } finally {
                    _pwdBuffer.forgetGivenPassword();
                }
            }
        }
    }

    /**
     * @see MainFrame#getAction(String)
     */
    public ActionCallback getAction(final String key) {
        return (ActionCallback) M.createAction(this, key);
    }

    private ICryptoContext getContext() {
        if (_cryptoContext == null) {
            throw new NullPointerException();
        }
        return _cryptoContext;
    }

    private SymmetricAlgo getCryptoAlgorithm() {
        try {
            final String algoClassName = Settings.getCryptoEngineClass();
            final Class<?> algoClass = Class.forName(algoClassName);
            return (SymmetricAlgo) algoClass.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return Returns the doc.
     */
    protected TPMDocument getDoc() {
        return _doc;
    }

    private void saveFileInBackground(final ICryptoContext ctx) {
        final Runnable run = new Runnable() {
            @Override
            public void run() {
                doSave(ctx);
            }
        };
        SwingUtilities.invokeLater(run);
    }

    protected List<Action> getPopupActions() {
        return Arrays.asList(_newFileAction, _openFileAction, _saveAction, _saveAsAction, _closeDocAction, null,
                _lockScreenAction, null, _quitAction);
    }

    private void init() {
        _mainView = new MainView(_editHandler.getMenu().getPopupMenu());
        this.add(_mainView, BorderLayout.CENTER);
    }

    private synchronized void initDocument() {
        _pwdBuffer.resetAll();
        final File file = new File("");
        _cryptoContext = createCryptoContext(file);
        _modified = false;
        setDoc(StandardDocumentFactory.createStandardDocument(file));
        updateTitle();
        _mainView.setRootNode(getDoc());
    }

    protected JMenuBar initMenu() {
        final JMenuBar mb = new JMenuBar();
        final JMenu menu = createFileMenu();
        mb.add(menu);
        mb.add(_editHandler.getMenu());
        mb.add(_viewHandler.getMenu());
        mb.add(_reportHandler.getMenu());
        mb.add(_helpHandler.getMenu());
        return mb;
    }

    private void initToolBar() {
        final JPanel bars = new JPanel(new FlowLayout(FlowLayout.LEFT));
        _toolbar = new JToolBar();
        _toolbar.setFloatable(false);
        final Action[] actions = {
                _newFileAction, _openFileAction, _saveAction, _saveAsAction, _closeDocAction, null, _lockScreenAction,
                null, _quitAction
        };
        addActions(_toolbar, actions);
        _toolbar.addSeparator();
        bars.add(_toolbar);
        bars.add(_editHandler.getToolBar());
        bars.add(_reportHandler.getToolBar());
        this.add(bars, BorderLayout.NORTH);
    }

    /**
     * @param ctx
     *            is the data-file to load.
     */
    public void loadFile(final File file) {

        final ICryptoContext ctx = createCryptoContext(file);
        _pwdBuffer.reset();
        loadFileInBackground(ctx);

    }

    private void loadFileInBackground(final ICryptoContext ctx) {
        final Runnable run = new Runnable() {
            @Override
            public void run() {
                doLoad(ctx);
            }
        };
        SwingUtilities.invokeLater(run);
    }

    public void loadLastFile() {
        final List files = LRUFilesMenu.getLRUFiles(Settings.getPreferences(), 1);
        if (!files.isEmpty()) {
            loadFile((File) files.get(0));
        }
    }

    void newDocument() {
        if (confirmCloseDoc()) {
            initDocument();
        }
    }

    protected void onChangeMasterPwd() {
        _pwdBuffer.resetAll();
        contentChanged(true);
        saveDocument();
    }

    protected void onImport() {
        final ImportDialog dlg = new ImportDialog(MainFrame.getInstance(), _doc);
        dlg.setVisible(true);
        if (_modified) {
            _mainView.setRootNode(_doc);
            contentChanged(true);
        }
    }

    protected void onLockScreen() {
        _autoLock.onLock();
    }

    protected void onQuit() {
        if (confirmCloseDoc()) {
            _parent.exit();
        }
    }

    protected void onShowOptions() {
        final OptionsDialog dlg = new OptionsDialog(_parent);
        dlg.setVisible(true);
    }

    void openDocument() {
        if (confirmCloseDoc()) {
            final ICryptoContext ctx = selectFile(false);
            if (ctx != null) {
                loadFileInBackground(ctx);
            }
        }
    }

    public void exit() {
        _lruFileMenu.saveLRUFiles(Settings.getPreferences());
    }

    public synchronized boolean saveDocument() {
        if (_modified) {
            final File file = getDoc().getFile();
            if (file.exists()) {
                saveFileInBackground(getContext());
            } else {
                saveDocumentAs();
            }
            return !_modified;
        }
        return false;
    }

    public synchronized void saveDocumentAs() {
        final ICryptoContext ctx = selectFile(true);
        if (ctx != null) {
            _pwdBuffer.reset();
            _cryptoContext = ctx;
            saveFileInBackground(ctx);
        }
    }

    protected void selectAndShow(final List<DescriptiveItem> nodes, final String title) {
        final ListSelectionDlg dlg = new ListSelectionDlg(MainFrame.getInstance(), title.replaceAll("&", ""));
        Toolkit.resizeToDesktop(dlg, 0.3f);
        Toolkit.centerWindow(this, dlg);
        dlg.setListData(nodes);
        dlg.setVisible(true);

        final DescriptiveItem el = (DescriptiveItem) dlg.getSelectedValue();
        if (el != null) {
            _mainView.showNode((SecuredElement) el.getValue());
        }
    }

    private ICryptoContext selectFile(final boolean isSaveDialog) {
        final FileDialog dlg = new FileDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        dlg.setSelectedFile(new File(Settings.getLastFile()));
        dlg.setSaveDialog(isSaveDialog);
        dlg.setVisible(true);
        final File selectedFile = dlg.getSelectedFile();
        if (selectedFile == null) {
            return null;
        }
        return new PasswordCryptoContext(dlg.getSelectedAlgo(), _pwdBuffer, selectedFile);
    }

    /**
     * @param doc
     *            The doc to set.
     */
    protected void setDoc(final TPMDocument doc) {
        _doc = doc;
    }

    protected void updateTitle() {
        final Object[] p = new Object[] {
                getDoc().getFile().getName(), _modified ? M.getString("modified_char") //$NON-NLS-1$
                        : M.getString("not_modified_char")}; //$NON-NLS-1$
        _parent.setTitle(MessageFormat.format(TITLE, p));
    }

    /**
     * @param force
     *            force to show the dialog
     */
    public void showTipOfTheDay(boolean force) {
        final TipOfTheDayDialog dlg = new TipOfTheDayDialog(_parent, //
                getClass().getResource("tip-of-the-day.xml"));
        dlg.showTips(force);
    }

}
