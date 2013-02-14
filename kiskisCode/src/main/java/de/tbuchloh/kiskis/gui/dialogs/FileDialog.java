package de.tbuchloh.kiskis.gui.dialogs;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.common.MessageBox;
import de.tbuchloh.kiskis.persistence.SupportedAlgorithmsUtil;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.crypto.SymmetricAlgo;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.DescriptiveItem;
import de.tbuchloh.util.swing.dialogs.MessagePane;

/**
 * <b>FileDialog</b>:
 * 
 * @author Tobias Buchloh
 * @version $Id: FileDialog.java,v 1.7 2007/04/27 11:03:17 tbuchloh Exp $
 */
public final class FileDialog extends KisKisDialog implements AWTEventListener {

    /**
     * @author Tobias Buchloh (gandalf)
     * @since 23.10.2010
     */
    private static class KiskisFileFilter extends FileFilter {

	private final String _description;

	private final String _extension;

	/**
	 * Standardkonstruktor
	 * 
	 * @param description
	 *            is the description
	 * @param extensionPattern
	 *            is the extension pattern
	 */
	public KiskisFileFilter(String description, String extension) {
	    _description = description;
	    _extension = extension;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(File f) {
	    return f.isDirectory()
	    || f.getName().endsWith(_extension.replace("*", ""));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
	    return _description;
	}

	/**
	 * @return
	 */
	public String getExtension() {
	    return _extension;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
	    return "KiskisFileFilter [_description=" + _description
	    + ", _extension=" + _extension + "]";
	}

    }

    /**
     * @author Tobias Buchloh (gandalf)
     * @version $Id: FileDialog.java,v 1.7 2007/04/27 11:03:17 tbuchloh Exp $
     */
    protected static class MyFileChooser extends JFileChooser {
	private static final long serialVersionUID = 1L;

	/**
	 * @param keyEvent
	 *            the key event
	 */
	public boolean fireEnterPressed(KeyEvent e) {
	    return super.processKeyBinding(
		    KeyStroke.getKeyStroke("pressed ENTER"), e,
		    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, true);
	}
    }

    static final Log LOG = LogFactory.getLog(FileDialog.class);

    private static final Messages M = new Messages(FileDialog.class);

    /**
     * Die {@link String}
     */
    private static final String MSG_EXTENSION_MISSING = "msgExtensionMissing";

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    private static final String TITLE = M.getString("title");

    /**
     * @param args
     */
    public static void main(final String[] args) {
	final FileDialog dlg = new FileDialog(new JFrame());
	dlg.setSelectedFile(new File("/tmp/"));
	dlg.setSaveDialog(true);
	dlg.setVisible(true);
	System.out.println("Selected file=" + dlg.getSelectedFile());
    }

    private JComboBox _algoBox;

    private MyFileChooser _chooser;

    private JPanel _encryptionModePanel;

    private boolean _isSaveMode;

    private Map<String, DescriptiveItem> _items;

    private Action _okAction;

    private SymmetricAlgo _selectedAlgo;

    private File _selectedFile;

    private FileFilter _selectedFileFilter;

    /**
     * creates a new FileDialog
     * 
     * @param owner
     * @param title
     * @param modal
     */
    public FileDialog(final Dialog owner) {
	super(owner, TITLE, true);
	init();

    }

    /**
     * creates a new FileDialog
     * 
     * @param owner
     * @param title
     * @param modal
     */
    public FileDialog(final Frame owner) {
	super(owner, TITLE, true);
	init();
    }

    private void addAlgo(final String label, final SymmetricAlgo algo) {
	final DescriptiveItem item = new DescriptiveItem(label, algo);
	_algoBox.addItem(item);
	_items.put(algo.getClass().getName(), item);
    }

    private boolean confirmOverwrite() {
	LOG.debug("Confirm overwrite ...");
	final String msg = M.format("msgOverwriteExisting",
		_selectedFile.getName());
	return !_selectedFile.exists() || MessageBox.showConfirmDialog(msg);
    }

    private Component createEncryptionModesBox() {
	_encryptionModePanel = new JPanel(new BorderLayout());
	final JLabel label = new JLabel(M.getString("encryptionMode"));
	_encryptionModePanel.add(label, BorderLayout.WEST);
	for (final Map.Entry<String, SymmetricAlgo> entry : SupportedAlgorithmsUtil
		.getSupportedAlgorithms().entrySet()) {
	    addAlgo(entry.getKey(), entry.getValue());
	}
	_algoBox.setSelectedItem(_items.get(Settings.getCryptoEngineClass()));
	_encryptionModePanel.add(_algoBox);
	_encryptionModePanel.setBorder(_chooser.getBorder());
	_encryptionModePanel.setVisible(_isSaveMode);
	return _encryptionModePanel;
    }

    private JComponent createFileChooser() {
	_chooser.addPropertyChangeListener(
		JFileChooser.SELECTED_FILE_CHANGED_PROPERTY,
		new PropertyChangeListener() {
		    @Override
		    public void propertyChange(final PropertyChangeEvent evt) {
			selectionChanged();
		    }
		});
	_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	_chooser.setMultiSelectionEnabled(false);
	_chooser.setControlButtonsAreShown(false);
	_chooser.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(final MouseEvent e) {
		selectionChanged();
	    }
	});
	final String[] fileFilters = M.getString("fileFilters").split(";");
	FileFilter first = null;
	for (final String filter : fileFilters) {
	    final String[] parts = filter.split("\\$\\$");
	    final KiskisFileFilter f = new KiskisFileFilter(parts[0], parts[1]);
	    _chooser.addChoosableFileFilter(f);
	    if (first == null) {
		first = f;
	    }
	}
	_chooser.setFileFilter(first);

	_chooser.fireEnterPressed(new KeyEvent(_chooser,
		(int) (Math.random() * Integer.MAX_VALUE), System
		.currentTimeMillis(), KeyEvent.KEY_PRESSED,
		KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED));

	return _chooser;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.dialogs.KisKisDialog#createMainPanel()
     */
    @Override
    protected Component createMainPanel() {
	final JPanel panel = new JPanel(new BorderLayout());
	panel.add(createFileChooser(), BorderLayout.CENTER);
	panel.add(createEncryptionModesBox(), BorderLayout.SOUTH);
	return panel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
	LOG.debug("Removing AWT-Event-Listener " + this);
	java.awt.Toolkit.getDefaultToolkit().removeAWTEventListener(this);
	super.dispose();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void eventDispatched(final AWTEvent event) {
	if (!(event instanceof KeyEvent)) {
	    return;
	}

	final Object source = event.getSource();
	if (source instanceof TextField) {
	    final TextField text = (TextField) source;
	    final int curPos = text.getCaretPosition();
	    final KeyEvent keyEvent = (KeyEvent) event;
	    _chooser.fireEnterPressed(keyEvent);
	    text.setCaretPosition(curPos);
	} else if (source instanceof JTextField) {
	    final JTextField text = (JTextField) source;
	    final int curPos = text.getCaretPosition();
	    final KeyEvent keyEvent = (KeyEvent) event;
	    _chooser.fireEnterPressed(keyEvent);
	    text.setCaretPosition(curPos);
	}
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.dialogs.KisKisDialog#getActions()
     */
    @Override
    protected List<Action> getActions() {
	final Action[] actions = { M.createAction(this, "onAbort"), _okAction };
	return Arrays.asList(actions);
    }

    /**
     * @return Returns the selectedAlgo.
     */
    public final SymmetricAlgo getSelectedAlgo() {
	assert _selectedAlgo != null;
	return _selectedAlgo;
    }

    /**
     * @return Returns the selectedFile or null if the dialog was cancelled.
     */
    public final File getSelectedFile() {
	File file = _selectedFile;
	if (file == null) {
	    return null;
	}

	final FileFilter fileFilter = _selectedFileFilter;
	if (fileFilter instanceof KiskisFileFilter) {
	    final KiskisFileFilter kff = (KiskisFileFilter) fileFilter;
	    if (_isSaveMode && !kff.accept(file)) {
		final String newName = file.getName() + kff.getExtension();
		final String msg = M.format(MSG_EXTENSION_MISSING,
			file.getName(), kff.getExtension(), newName);
		final int option = MessagePane.showConfirmDialog(this, msg,
			MSG_EXTENSION_MISSING + kff.getExtension());
		if (MessagePane.YES_OPTION == option) {
		    // add file extension
		    file = new File(file.getParentFile(), newName);
		}
	    }
	}
	return file;
    }

    private void init() {
	_algoBox = new JComboBox();
	_chooser = new MyFileChooser();
	_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	_chooser.setMultiSelectionEnabled(false);
	_okAction = M.createAction(this, "onOK");
	_okAction.setEnabled(false);
	_items = new HashMap<String, DescriptiveItem>();
    }

    protected void onAbort() {
	this.setVisible(false);
	this.dispose();
    }

    protected void onOK() {
	_selectedFileFilter = _chooser.getFileFilter();

	_chooser.approveSelection(); // does not work!

	_selectedFile = _chooser.getSelectedFile();
	LOG.debug("OK pressed - file=" + _selectedFile + ", isSaveMode="
		+ _isSaveMode);
	assert _selectedFile != null;
	if (!_isSaveMode || confirmOverwrite()) {
	    final DescriptiveItem item = (DescriptiveItem) _algoBox
	    .getSelectedItem();
	    _selectedAlgo = (SymmetricAlgo) item.getValue();

	    this.setVisible(false);
	    this.dispose();
	}
    }

    protected void selectionChanged() {
	final File file = _chooser.getSelectedFile();
	LOG.info("selectedFile changed! file=" + file);
	if (file == null) {
	    _okAction.setEnabled(false);
	} else {
	    _okAction.setEnabled(true);
	}
    }

    /**
     * @param isSaveDialog
     *            true, if it's a save dialog.
     */
    public void setSaveDialog(final boolean isSaveDialog) {
	_isSaveMode = isSaveDialog;
    }

    /**
     * @param lastFile
     *            the selected file.
     */
    public void setSelectedFile(final File lastFile) {
	if (lastFile != null && lastFile.exists()) {
	    if (lastFile.isDirectory()) {
		_chooser.setCurrentDirectory(lastFile);
	    } else {
		_okAction.setEnabled(true);
		_chooser.setSelectedFile(lastFile);
	    }
	}
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setVisible(final boolean b) {
	if (b) {
	    LOG.debug("Adding AWT-Event-Listener " + this);
	    java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(this,
		    AWTEvent.KEY_EVENT_MASK);
	} else {
	    LOG.debug("Removing AWT-Event-Listener " + this);
	    java.awt.Toolkit.getDefaultToolkit().removeAWTEventListener(this);
	}
	super.setVisible(b);
    }
}
