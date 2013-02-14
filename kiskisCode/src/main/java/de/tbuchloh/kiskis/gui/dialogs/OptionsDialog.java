/*
 * Copyright (C) 2004 by Tobias Buchloh. This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Library General
 * Public License as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details. You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA If you didn't download
 * this code from the following link, you should check if you aren't using an
 * obsolete version: http://www.sourceforge.net/projects/kiskis
 */

package de.tbuchloh.kiskis.gui.dialogs;

import static de.tbuchloh.kiskis.gui.common.LnFHelper.createLabel;
import static java.awt.Toolkit.getDefaultToolkit;
import static java.util.Arrays.asList;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;

import de.tbuchloh.kiskis.gui.common.Application;
import de.tbuchloh.kiskis.gui.common.Application.Program;
import de.tbuchloh.kiskis.gui.common.LnFHelper;
import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.kiskis.gui.widgets.BasicTextField;
import de.tbuchloh.kiskis.gui.widgets.PasswordElement;
import de.tbuchloh.kiskis.model.cracklib.Dictionary;
import de.tbuchloh.kiskis.model.cracklib.InvalidDictionaryException;
import de.tbuchloh.kiskis.persistence.PersistenceManager;
import de.tbuchloh.kiskis.persistence.SupportedAlgorithmsUtil;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.kiskis.util.ValidationException;
import de.tbuchloh.util.crypto.SymmetricAlgo;
import de.tbuchloh.util.event.ContentChangedEvent;
import de.tbuchloh.util.event.ContentListener;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.logging.LogFactory;
import de.tbuchloh.util.swing.DescriptiveItem;
import de.tbuchloh.util.swing.GridBagBuilder;
import de.tbuchloh.util.swing.SpringUtilities;
import de.tbuchloh.util.swing.SwingWorker;
import de.tbuchloh.util.swing.Toolkit;
import de.tbuchloh.util.swing.actions.ActionCallback;
import de.tbuchloh.util.swing.dialogs.MessagePane;
import de.tbuchloh.util.swing.widgets.LinkLabel;
import de.tbuchloh.util.swing.widgets.ObservableCheckBox;
import de.tbuchloh.util.swing.widgets.ObservableComboBox;
import de.tbuchloh.util.swing.widgets.ObservableTextField;
import de.tbuchloh.util.swing.widgets.OrderableListWidget;

/**
 * <b>OptionsDialog</b>:
 * 
 * @author gandalf
 * @version $Id: OptionsDialog.java,v 1.18 2007/12/08 13:53:46 tbuchloh Exp $
 */
public final class OptionsDialog extends KisKisDialog implements ContentListener {

    private final class AppearanceTab extends Tab {

        private static final long serialVersionUID = 1L;

        private JCheckBox _bufferPwdBox;

        private ObservableCheckBox _checkUpdatesBox;

        private ObservableTextField _defaultPwdExpiryBox;

        private ObservableCheckBox _exportPrefsBox;

        private ObservableTextField _lockAfterBox;

        private ObservableComboBox _looksBox;

        private ObservableComboBox _passwordFieldFontBox;

        private ObservableTextField _pwdDisposeTimeBox;

        private ObservableTextField _viewCounterDelayBox;

        private PasswordElement _samplePasswordBox;

        public AppearanceTab() {
            createLooksBox();
            createPwdDisposeBox();
            createDefaultPwdExpiryBox();
            createExportPrefsBox();
            createLockAfterBox();
            createViewCounterDelayBox();
            createCheckUpdatesBox();
            createPasswordFieldFontBox();
            initValues();
            initLayout();
        }

        private void createPasswordFieldFontBox() {
            final Vector<String> fonts = new Vector<String>();
            fonts.addAll(asList("TimesRoman", "Helvetica", "Courier", "Courier New"));
            for (final String font : getDefaultToolkit().getFontList()) {
                fonts.add(font);
            }

            Collections.sort(fonts);
            _passwordFieldFontBox = new ObservableComboBox(fonts);
            _passwordFieldFontBox.addContentListener(OptionsDialog.this);
            _passwordFieldFontBox.addContentListener(new ContentListener() {

                @Override
                public void contentChanged(final ContentChangedEvent event) {
                    final String fontName = (String) _passwordFieldFontBox.getSelectedItem();
                    _samplePasswordBox.setPasswordFont(fontName);
                }
            });
            _samplePasswordBox = new PasswordElement("1MySecretLlIiJjOo08".toCharArray());
            _samplePasswordBox.setShowButtons(false);
            _samplePasswordBox.setShowQualityLabel(false);
            _samplePasswordBox.setShowPassword(true);
        }

        private void createCheckUpdatesBox() {
            _checkUpdatesBox = new ObservableCheckBox();
            _checkUpdatesBox.addContentListener(OptionsDialog.this);
        }

        private void createDefaultPwdExpiryBox() {
            _defaultPwdExpiryBox = new BasicTextField(5);
            _defaultPwdExpiryBox.addContentListener(OptionsDialog.this);
            _defaultPwdExpiryBox.setValidator(Pattern.compile(NUMBER_PATTERN));
        }

        private void createExportPrefsBox() {
            _exportPrefsBox = new ObservableCheckBox();
            _exportPrefsBox.addContentListener(OptionsDialog.this);
        }

        private void createLockAfterBox() {
            _lockAfterBox = new BasicTextField(5);
            _lockAfterBox.addContentListener(OptionsDialog.this);
            _lockAfterBox.setValidator(Pattern.compile(NUMBER_PATTERN));
        }

        private void createLooksBox() {
            final Vector<String> vals = new Vector<String>();
            final LookAndFeelInfo[] f = UIManager.getInstalledLookAndFeels();
            for (final LookAndFeelInfo info : f) {
                vals.add(info.getClassName());
            }
            _looksBox = new ObservableComboBox(vals);
            _looksBox.setEditable(true);
            _looksBox.setSelectedItem(Settings.getDefaultLookAndFeel());
            _looksBox.addContentListener(OptionsDialog.this);
        }

        private void createPwdDisposeBox() {
            _bufferPwdBox = new JCheckBox(new ActionCallback(this, "toggleBufferFields")); //$NON-NLS-1$
            _pwdDisposeTimeBox = new BasicTextField(5);
            _pwdDisposeTimeBox.addContentListener(OptionsDialog.this);
            _pwdDisposeTimeBox.setValidator(Pattern.compile(NUMBER_PATTERN));
        }

        private void createViewCounterDelayBox() {
            _viewCounterDelayBox = new BasicTextField();
            _viewCounterDelayBox.addContentListener(OptionsDialog.this);
            _viewCounterDelayBox.setValidator(Pattern.compile(NUMBER_PATTERN));
        }

        private void initLayout() {
            final JPanel panel = new JPanel();
            final GridBagBuilder builder = new GridBagBuilder(panel);
            builder.setInsets(new Insets(5, 5, 5, 5));

            builder.add(createLabel(M.getString("OptionsDialog.lookAndFeel")));
            builder.addLast(_looksBox);

            builder.add(createLabel(M.getString("OptionsDialog.passwordFieldFont")));
            final JPanel pwdFieldPanel = new JPanel(new GridLayout(1, 2));
            pwdFieldPanel.add(_passwordFieldFontBox);
            pwdFieldPanel.add(_samplePasswordBox);
            builder.addLast(pwdFieldPanel);

            builder.add(createLabel(M.getString("OptionsDialog.lockScreenAfter")), 1); //$NON-NLS-1$
            builder.add(_lockAfterBox, 3);
            builder.addLast(new JLabel(M.getString("OptionsDialog.minutes")));

            builder.add(createLabel(M.getString("OptionsDialog.viewCounterDelay")));
            builder.add(_viewCounterDelayBox, 3);
            builder.addLast(new JLabel(M.getString("OptionsDialog.sec")));

            builder.add(createLabel(M.getString("OptionsDialog.bufferPassword")), 1); //$NON-NLS-1$
            builder.addLast(_bufferPwdBox);

            builder.add(createLabel(M.getString("OptionsDialog.disposePasswordAfter")), 1); //$NON-NLS-1$
            builder.add(_pwdDisposeTimeBox, 3);
            builder.addLast(new JLabel(M.getString("OptionsDialog.minutes"))); //$NON-NLS-1$

            builder.add(createLabel(M.getString("OptionsDialog.defaultPasswordExpiryTime")), 1); //$NON-NLS-1$
            builder.add(_defaultPwdExpiryBox, 3);
            builder.addLast(new JLabel(M.getString("OptionsDialog.days"))); //$NON-NLS-1$

            builder.add(createLabel(M.getString("OptionsDialog.exportPrefs")));
            builder.addLast(_exportPrefsBox);

            builder.add(createLabel(M.getString("OptionsDialog.checkUpdates")));
            builder.addLast(_checkUpdatesBox);

            this.add(panel, BorderLayout.NORTH);
        }

        private void initValues() {
            _lockAfterBox.setText(String.valueOf(Settings.getLockAfterMin()));
            _bufferPwdBox.setSelected(Settings.isBufferingPwd());

            final long pwdDisposeDelay = Settings.getPwdDisposeDelay();
            _pwdDisposeTimeBox.setText(getMinuteString(pwdDisposeDelay));

            final int pwdExpiry = Settings.getDefaultPwdExpiryDays();
            _defaultPwdExpiryBox.setText(String.valueOf(pwdExpiry));
            toggleBufferFields();

            _viewCounterDelayBox.setText(getSecondsString(Settings.getViewCounterDelay()));

            _exportPrefsBox.setSelected(Settings.isExportingPrefs());

            _checkUpdatesBox.setSelected(Settings.isCheckingUpdates());

            _passwordFieldFontBox.setSelectedItem(Settings.getPasswordFieldFont());

            setChanged(false);
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.gui.dialogs.OptionsDialog.Tab#store()
         */
        @Override
        public void store() throws ValidationException {
            check(new ObservableTextField[] {
                    _defaultPwdExpiryBox, _lockAfterBox, _pwdDisposeTimeBox, _viewCounterDelayBox
            });

            Settings.setBufferPwd(_bufferPwdBox.isSelected(), convertMinToMilli(_pwdDisposeTimeBox.getText()));

            Settings.setDefaultPwdExpiryDays(Integer.parseInt(_defaultPwdExpiryBox.getText()));

            Settings.setLockAfterMin(Integer.parseInt(_lockAfterBox.getText()));

            Settings.setViewCounterDelay(convertSecToMilli(_viewCounterDelayBox.getText()));

            Settings.setExportPreferences(_exportPrefsBox.isSelected());

            Settings.setDefaultLookAndFeel((String) _looksBox.getSelectedItem());

            Settings.setCheckingUpdates(_checkUpdatesBox.isSelected());

            Settings.setPasswordFieldFont(//
                    (String) _passwordFieldFontBox.getSelectedItem());
        }

        public void toggleBufferFields() {
            _pwdDisposeTimeBox.setEnabled(_bufferPwdBox.isSelected());
        }

    }

    private final class CracklibTab extends Tab {

        public class DictFileFilter extends FileFilter {

            @Override
            public boolean accept(final File f) {
                try {
                    if (f.isDirectory()) {
                        return true;
                    }
                    if (f.getCanonicalPath().endsWith(".pwd")) {
                        return true;
                    }
                } catch (final Exception e1) {
                    // Don't do anything, just return false
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Cracklib Packer Password Dictionary";
            }
        }

        /**
         * @author Tobias Buchloh (gandalf)
         * @version $Id: OptionsDialog.java,v 1.18 2007/12/08 13:53:46 tbuchloh Exp $
         */
        abstract class DictionaryWorker extends SwingWorker {

            /**
             * @inheritDoc
             */
            @Override
            public void finished() {
                super.finished();

                progressBar.setVisible(false);
                OptionsDialog.this.setEnabled(true);
            }

            /**
             * @inheritDoc
             */
            @Override
            public void start() {
                progressBar.setVisible(true);
                OptionsDialog.this.setEnabled(false);

                super.start();
            }
        }

        private static final long serialVersionUID = 1L;

        ObservableTextField _cracklibDictDirBox;

        Action dictNameSelectLink;

        Action fileToDictSelectLink;

        Component progressBar;

        public CracklibTab() {
            createCracklibControls();
            initValues();
            initLayout();
        }

        private boolean checkOverwriteDictionary(final String dirDict) {
            if ("".equals(dirDict)) {
                MessageBox.showErrorMessage(M.getString("OptionsDialog.choose_dictionary"));
                return false;
            }
            if (Dictionary.exists(dirDict) && !MessageBox.showConfirmDialog(OptionsDialog.this, //
                    M.format("OptionsDialog.dictionary_exists", dirDict))) {
                return false;
            } else if (!MessageBox.showConfirmDialog(OptionsDialog.this,
                    M.format("OptionsDialog.create_dictionary", dirDict))) {
                return false;
            }
            return true;
        }

        private void createCracklibControls() {
            dictNameSelectLink = M.createAction(this, "onShowDictionarySelectDialog");
            fileToDictSelectLink = M.createAction(this, "onShowTextFileSelectDialog");
            _cracklibDictDirBox = new BasicTextField(40);
            _cracklibDictDirBox.addContentListener(OptionsDialog.this);
            _cracklibDictDirBox.addContentListener(new ContentListener() {

                @Override
                public void contentChanged(ContentChangedEvent e) {
                    updateFileToDictSelectLink();
                }
            });
            _cracklibDictDirBox.setToolTipText(M.getString("OptionsDialog.dictFileName_tooltip"));
            progressBar = createProgressBar();
        }

        private Component createProgressBar() {
            final JPanel panel = new JPanel(new BorderLayout(5, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            panel.add(new JLabel(M.getString("progress_label")), BorderLayout.WEST);
            final JProgressBar progress = new JProgressBar();
            panel.setVisible(false);
            progress.setIndeterminate(true);
            panel.add(progress);
            return panel;
        }

        private void initLayout() {
            final JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));

            final JPanel filenamePanel = new JPanel(new BorderLayout(10, 10));
            filenamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            filenamePanel.add(new JLabel(M.getString("OptionsDialog.dictFileName")), BorderLayout.WEST);
            filenamePanel.add(_cracklibDictDirBox);
            panel.add(filenamePanel);

            panel.add(new LinkLabel(dictNameSelectLink));

            panel.add(new LinkLabel(fileToDictSelectLink));

            panel.add(progressBar);

            this.add(panel, BorderLayout.NORTH);
        }

        private void initValues() {
            try {
                open(Settings.getCracklibDict());
            } catch (final ValidationException e) {
                MessagePane.showErrorMessage(OptionsDialog.this, e.getMessage());
            }

            _cracklibDictDirBox.setText(Settings.getCracklibDict());

            updateFileToDictSelectLink();

            setChanged(false);
        }

        public void onShowDictionarySelectDialog() {
            final JFileChooser selectFile = new JFileChooser(new File(_cracklibDictDirBox.getText()));
            selectFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            final int returnval = selectFile.showOpenDialog(this);
            if (returnval == JFileChooser.APPROVE_OPTION) {
                final SwingWorker worker = new DictionaryWorker() {

                    /**
                     * @inheritDoc
                     */
                    @Override
                    public Object construct() {
                        try {
                            final String newDictFileName = selectFile.getSelectedFile().getPath();
                            return open(newDictFileName);
                        } catch (final Exception e) {
                            LOG.error(e, e);
                            MessageBox.showErrorMessage(e.getMessage());
                        }
                        return null;
                    }

                    /**
                     * @inheritDoc
                     */
                    @Override
                    public void finished() {
                        super.finished();
                        final Dictionary dictionary = (Dictionary) get();
                        if (dictionary != null) {
                            _cracklibDictDirBox.setText(dictionary.getDirectory());
                        }
                    }
                };
                worker.start();
            }
        }

        public void onShowTextFileSelectDialog() {
            final String dictDir = _cracklibDictDirBox.getText();
            if (checkOverwriteDictionary(dictDir)) {
                final JFileChooser selectFile = new JFileChooser();
                selectFile.setDialogTitle(M.getString("OptionsDialog.select_textfile"));
                selectFile.setSelectedFile(new File("*"));
                selectFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
                final int returnval = selectFile.showOpenDialog(this);
                if (returnval == JFileChooser.APPROVE_OPTION) {
                    final File selectedFile = selectFile.getSelectedFile();

                    final SwingWorker worker = new DictionaryWorker() {

                        /**
                         * @inheritDoc
                         */
                        @Override
                        public Object construct() {
                            selectedFile.getParentFile().getAbsoluteFile().mkdirs();
                            try {
                                return Dictionary.createDictionary(selectedFile, dictDir);
                            } catch (final InvalidDictionaryException e) {
                                LOG.error(e, e);
                                MessageBox.showErrorMessage(M.format("OptionsDialog.invalid_dictionary", new Object[] {
                                        selectedFile.getName(), e.getMessage()
                                }));
                            } catch (final IOException e) {
                                LOG.error(e, e);
                                MessageBox.showErrorMessage(e.getMessage());
                            }
                            return null;
                        }

                    };
                    worker.start();
                }
            }
        }

        Dictionary open(final String dictName) throws ValidationException {
            if ("".equals(dictName)) {
                return null;
            }

            try {
                return Dictionary.open(dictName);
            } catch (final IOException e) {
                LOG.debug(e, e);
                final String msg = M.format("OptionsDialog.invalid_dictionary", new Object[] {
                        dictName, e.getMessage()
                });
                throw new ValidationException(msg);
            }
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.gui.dialogs.OptionsDialog.Tab#store()
         */
        @Override
        public void store() throws ValidationException {
            final String filename = _cracklibDictDirBox.getText();
            Settings.setCracklibDict(filename);
        }

        void updateFileToDictSelectLink() {
            fileToDictSelectLink.setEnabled(_cracklibDictDirBox.getText().length() > 0);
        }
    }

    private final class FileTab extends Tab {

        private static final long serialVersionUID = 1L;

        private ObservableCheckBox _autoSaveBox;

        private ObservableCheckBox _displayLastViewedElementBox;

        private ObservableTextField _autoSaveDelayBox;

        private ObservableComboBox _enginesBox;

        private ObservableTextField _maxBackupsBox;

        private ObservableCheckBox _checkMasterPasswordBox;

        public FileTab() {
            createAutoSaveFeatures();
            createMaxBackupsBox();
            createEnginesBox();
            createCheckMasterPasswordBox();
            createDisplayLastElementBox();
            initValues();
            initLayout();
        }

        private void createDisplayLastElementBox() {
            _displayLastViewedElementBox = new ObservableCheckBox();
            _displayLastViewedElementBox.addContentListener(OptionsDialog.this);
        }

        private void createCheckMasterPasswordBox() {
            _checkMasterPasswordBox = new ObservableCheckBox();
            _checkMasterPasswordBox.addContentListener(OptionsDialog.this);
        }

        private void createAutoSaveFeatures() {
            _autoSaveBox = new ObservableCheckBox(new ActionCallback(this, "toggleAutoSaveFields")); //$NON-NLS-1$
            _autoSaveBox.addContentListener(OptionsDialog.this);
            _autoSaveDelayBox = new BasicTextField(5);
            _autoSaveDelayBox.addContentListener(OptionsDialog.this);
            _autoSaveDelayBox.setValidator(Pattern.compile(NUMBER_PATTERN));
        }

        private Vector<DescriptiveItem> createEngineItems() {
            final Vector<DescriptiveItem> items = new Vector<DescriptiveItem>();
            for (final Map.Entry<String, SymmetricAlgo> entry : SupportedAlgorithmsUtil.getSupportedAlgorithms()
                    .entrySet()) {
                items.add(new DescriptiveItem(entry.getKey(), entry.getValue().getClass().getName()));
            }
            return items;
        }

        private void createEnginesBox() {
            final Vector<DescriptiveItem> items = createEngineItems();
            _enginesBox = new ObservableComboBox(items);
            _enginesBox.addContentListener(OptionsDialog.this);
        }

        private void createMaxBackupsBox() {
            _maxBackupsBox = new BasicTextField(5);
            _maxBackupsBox.addContentListener(OptionsDialog.this);
            _maxBackupsBox.setValidator(Pattern.compile(NUMBER_PATTERN));
        }

        private void initLayout() {
            final JPanel panel = new JPanel();
            final GridBagBuilder builder = new GridBagBuilder(panel);
            builder.setInsets(new Insets(5, 5, 5, 5));

            builder.add(createLabel(M.getString("OptionsDialog.cryptoEngineClass")), 1);
            builder.addLast(_enginesBox);

            builder.add(createLabel(M.getString("OptionsDialog.enableAutoSave")), 1); //$NON-NLS-1$
            builder.addLast(_autoSaveBox);

            builder.add(createLabel(M.getString("OptionsDialog.saveEvery")), 1); //$NON-NLS-1$
            builder.add(_autoSaveDelayBox, 3);
            builder.addLast(new JLabel(M.getString("OptionsDialog.minutes"))); //$NON-NLS-1$

            builder.add(createLabel(M.getString("OptionsDialog.maxBackups"))); //$NON-NLS-1$
            builder.addLast(_maxBackupsBox);

            builder.add(createLabel(M.getString("OptionsDialog.checkMasterPassword"))); //$NON-NLS-1$
            builder.addLast(_checkMasterPasswordBox);

            builder.add(createLabel(M.getString("OptionsDialog.displayLastViewedElement"))); //$NON-NLS-1$
            builder.addLast(_displayLastViewedElementBox);

            this.add(panel, BorderLayout.NORTH);
        }

        private void initValues() {
            _autoSaveBox.setSelected(Settings.isAutoSaveEnabled());
            final long autoSaveDelay = Settings.getAutoSaveDelay();
            _autoSaveDelayBox.setText(getMinuteString(autoSaveDelay));
            toggleAutoSaveFields();
            _maxBackupsBox.setText(String.valueOf(PersistenceManager.getMaxBackups()));
            for (int i = 0; i < _enginesBox.getItemCount(); ++i) {
                final DescriptiveItem item = (DescriptiveItem) _enginesBox.getItemAt(i);
                if (item.getValue().equals(Settings.getCryptoEngineClass())) {
                    _enginesBox.setSelectedItem(item);
                }
            }

            _checkMasterPasswordBox.setSelected(Settings.isCheckingMasterPassword());

            _displayLastViewedElementBox.setSelected(Settings.isDisplayLastViewedElement());

            setChanged(false);
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.gui.dialogs.OptionsDialog.Tab#store()
         */
        @Override
        public void store() throws ValidationException {
            check(new ObservableTextField[] {
                    _maxBackupsBox, _autoSaveDelayBox
            });
            Settings.setAutoSave(_autoSaveBox.isSelected(), convertMinToMilli(_autoSaveDelayBox.getText()));

            PersistenceManager.setMaxBackups(Integer.parseInt(_maxBackupsBox.getText()));

            final DescriptiveItem selectedAlgoItem = (DescriptiveItem) _enginesBox.getSelectedItem();
            Settings.setCryptoEngineClass(selectedAlgoItem.getValue().toString());

            Settings.setDisplayLastViewedElement(_displayLastViewedElementBox.isSelected());

            Settings.setCheckingMasterPassword(_checkMasterPasswordBox.isSelected());
        }

        public void toggleAutoSaveFields() {
            _autoSaveDelayBox.setEnabled(_autoSaveBox.isSelected());
        }

    }

    public static final class ProgramEditDialog extends JDialog {

        private static final long serialVersionUID = 1L;

        private Action _cancelAction;

        private JTextField _commandBox;

        private final ContentListener _listener;

        private Action _okAction;

        private final Program _prog;

        private JTextField _urlRegExBox;

        /**
         * creates a new ProgramEditDialog
         * 
         * @param p
         *            si the program to edit.
         */
        public ProgramEditDialog(final Frame parent, final Program p, final ContentListener listener) {
            super(parent, M.getString("ProgramEditDialog.title"), true); //$NON-NLS-1$
            _prog = p;
            _listener = listener;
            final JPanel contentPane = new JPanel(new BorderLayout());
            contentPane.setBorder(LnFHelper.createDefaultBorder());
            contentPane.add(initFields(), BorderLayout.CENTER);
            contentPane.add(initActions(), BorderLayout.SOUTH);
            setContentPane(contentPane);
            Toolkit.restoreWindowState(this);
        }

        /**
         * closes this dialog.
         */
        public void close() {
            Toolkit.saveWindowState(this);
            this.setVisible(false);
            this.dispose();
        }

        private JPanel initActions() {
            _okAction = new ActionCallback(this, "store", M.getString("ProgramEditDialog.okAction_label")); //$NON-NLS-1$ //$NON-NLS-2$
            _cancelAction = new ActionCallback(this, "close", M.getString("ProgramEditDialog.cancelAction_label")); //$NON-NLS-1$ //$NON-NLS-2$

            final JPanel panel = new JPanel();
            panel.add(new JButton(_okAction));
            panel.add(new JButton(_cancelAction));
            return panel;
        }

        private JPanel initFields() {
            _commandBox = new JTextField(_prog.getCommand());
            _commandBox.setToolTipText(M.getString("ProgramEditDialog.commandBox_tt")); //$NON-NLS-1$
            _urlRegExBox = new JTextField(_prog.getUrlRegex());
            _urlRegExBox.setToolTipText(M.getString("ProgramEditDialog.urlRegexBox_tt")); //$NON-NLS-1$

            final JPanel panel = new JPanel(new SpringLayout());
            panel.add(createLabel(M.getString("ProgramEditDialog.urlRegex_label"))); //$NON-NLS-1$
            panel.add(_urlRegExBox);
            panel.add(createLabel(M.getString("ProgramEditDialog.command_label"))); //$NON-NLS-1$
            panel.add(_commandBox);

            SpringUtilities.makeCompactGrid(panel, 2, 2, 5, 5, 5, 5);

            return panel;
        }

        /**
         * stores the values and closes the dialog.
         */
        public void store() {
            _prog.setCommand(_commandBox.getText());
            _prog.setUrlRegex(_urlRegExBox.getText());
            _listener.contentChanged(new ContentChangedEvent(this));
            close();
        }
    }

    public final class ProgramModel extends Tab {

        private static final long serialVersionUID = 1L;

        private final OrderableListWidget _list;

        private final ContentListener _listener;

        private Action _newAction, _editAction, _deleteAction;

        public ProgramModel(final ContentListener listener) {
            _listener = listener;
            initActions();
            _list = new OrderableListWidget();
            _list.addSpecialAction(_newAction);
            _list.addSpecialAction(_editAction, true);
            _list.addSpecialAction(_deleteAction);
            this.setLayout(new BorderLayout());
            this.add(_list, BorderLayout.CENTER);
            load(Settings.getPreferences());
            _list.addContentListener(listener);
        }

        public void deleteProgram() {
            _list.removeObject(_list.getSelectedObject());
            _listener.contentChanged(new ContentChangedEvent(this));
        }

        public void editProgram() {
            final Application.Program p = (Application.Program) _list.getSelectedObject();
            if (p != null) {
                final ProgramEditDialog dlg = new ProgramEditDialog((Frame) OptionsDialog.this.getParent(), p,
                        _listener);
                dlg.setVisible(true);
            }
        }

        private void initActions() {
            _newAction = new ActionCallback(this, "newProgram", M.getString("ProgramModel.new_label")); //$NON-NLS-1$ //$NON-NLS-2$
            _editAction = new ActionCallback(this, "editProgram", M.getString("ProgramModel.edit_label")); //$NON-NLS-1$ //$NON-NLS-2$
            _deleteAction = new ActionCallback(this, "deleteProgram", M.getString("ProgramModel.delete_label")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        private void load(final Preferences prefs) {
            final List progs = Application.loadPrograms(prefs);
            _list.setData(progs, 0);
        }

        public void newProgram() {
            final Application.Program p = new Application.Program();
            _list.addObject(p);
            _listener.contentChanged(new ContentChangedEvent(this));
        }

        /**
         * Overridden!
         * 
         * @see de.tbuchloh.kiskis.gui.dialogs.OptionsDialog.Tab#store()
         */
        @Override
        public void store() throws ValidationException {
            store(Settings.getPreferences());
        }

        private void store(final Preferences prefs) {
            Application.storePrograms(prefs, _list.getData());
        }

    }

    private abstract class Tab extends JComponent {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public Tab() {
            setLayout(new BorderLayout());
            setBorder(LnFHelper.createDefaultBorder());
        }

        protected void check(final ObservableTextField... fields) throws ValidationException {
            for (final ObservableTextField o : fields) {
                if (!o.isValidValue()) {
                    o.grabFocus();
                    throw new ValidationException(M.getString("OptionsDialog.enterNNNumber"));
                }
            }
        }

        protected long convertMinToMilli(final String value) {
            return Long.parseLong(value) * 60 * 1000;
        }

        protected long convertSecToMilli(final String value) {
            return Long.parseLong(value) * 1000;
        }

        protected String getMinuteString(final long pwdDisposeDelay) {
            return String.valueOf(pwdDisposeDelay / 1000 / 60);
        }

        protected String getSecondsString(final long msec) {
            return String.valueOf(msec / 1000);
        }

        public abstract void store() throws ValidationException;
    }

    protected static final Log LOG = LogFactory.getLogger();

    protected static final Messages M = new Messages(OptionsDialog.class);

    private static final String NUMBER_PATTERN = "[0-9]+"; //$NON-NLS-1$

    private static final long serialVersionUID = 1L;

    public static void main(final String[] args) {
        final OptionsDialog dlg = new OptionsDialog(new JFrame());
        dlg.setVisible(true);
        // try {
        //
        // final InputStream is = Runtime.getRuntime()
        //		    .exec("kfmclient exec http://www.kk.de").getErrorStream(); //$NON-NLS-1$
        // int c;
        // while ((c = is.read()) != -1) {
        // System.out.print((char) c);
        // }
        // } catch (final IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    private ActionCallback _closeAction;

    private ActionCallback _saveAction;

    private final JTabbedPane _tabPanel;

    private Tab[] _tabs;

    /**
     * creates a new OptionsDialog
     * 
     * @param owner
     *            is the parent window
     */
    public OptionsDialog(final Frame owner) {
        super(owner, M.getString("OptionsDialog.title"), true); //$NON-NLS-1$
        _tabPanel = new JTabbedPane();
        initActions();
    }

    @Override
    protected Component createMainPanel() {
        _tabs = new Tab[] {
                new AppearanceTab(), new FileTab(), new ProgramModel(this), new CracklibTab()
        };
        _tabPanel.addTab(M.getString("OptionsDialog.common_tab_label"), _tabs[0]);
        _tabPanel.addTab(M.getString("OptionsDialog.fileTab.label"), _tabs[1]);
        _tabPanel.addTab(M.getString("OptionsDialog.app_tab_label"), _tabs[2]);
        _tabPanel.addTab(M.getString("OptionsDialog.crackLibDict_tab_label"), _tabs[3]);
        return _tabPanel;
    }

    @Override
    protected List getActions() {
        return Arrays.asList(new Action[] {
                _saveAction, _closeAction
        });
    }

    private void initActions() {
        _closeAction = new ActionCallback(this, "onClose", M.getString("OptionsDialog.closeAction_title")); //$NON-NLS-1$ //$NON-NLS-2$
        _saveAction = new ActionCallback(this, "save", M.getString("OptionsDialog.saveAction_title")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void onClose() {
        if (!isChanged() || confirmClose()) {
            close();
        }
    }

    public void save() {
        try {
            for (final Tab t : _tabs) {
                t.store();
            }
            setChanged(false);
            close();
        } catch (final ValidationException e) {
            MessageBox.showErrorMessage(e.getMessage());
        }
    }
}
