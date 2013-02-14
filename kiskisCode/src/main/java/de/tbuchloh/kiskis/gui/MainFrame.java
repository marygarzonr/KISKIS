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
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.systray.IMainFrame;
import de.tbuchloh.kiskis.gui.systray.ISystemTray;
import de.tbuchloh.kiskis.gui.systray.SystemTrayFactory;
import de.tbuchloh.kiskis.util.CheckUpdate;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.StatusBar;
import de.tbuchloh.util.swing.StatusBar.SimpleTextField;
import de.tbuchloh.util.swing.StatusBar.StatusBarField;
import de.tbuchloh.util.swing.SwingWorker;
import de.tbuchloh.util.swing.Toolkit;
import de.tbuchloh.util.swing.actions.ActionCallback;
import de.tbuchloh.util.swing.dialogs.MessagePane;

/**
 * <b>MainFrame</b>: Represents the main window.
 * 
 * @author gandalf
 * @version $Id: MainFrame.java,v 1.43 2007/04/27 11:03:14 tbuchloh Exp $
 */
public class MainFrame extends JFrame implements IMainFrame {

    private static final long serialVersionUID = 1L;

    public static final String ICON_PNG = "icon.png";

    protected static final Log LOG = LogFactory.getLog(MainFrame.class);

    static final Messages M = new Messages(MainFrame.class);

    static {
        try {
            UIManager.setLookAndFeel(Settings.getDefaultLookAndFeel());
        } catch (final Exception e) {
            LOG.error(e + ": using standard Look&Feel!"); //$NON-NLS-1$
        }
    }

    private static final MainFrame SINGLETON = new MainFrame();

    /**
     * @return the one and only
     */
    public static MainFrame getInstance() {
        return SINGLETON;
    }

    private ISystemTray _tray;

    private MainFramePanel _main;

    private JComponent _currentView;

    private StatusBarField _statusMsg;

    private MainFrame() {
        init();
        setJMenuBar(_main.initMenu());
        Toolkit.restoreWindowState(this);

        if (Settings.isCheckingUpdates()) {
            checkUpdates(true);
        }
    }

    void checkUpdates(final boolean showMsgWhenMatches) {
        final SwingWorker worker = new SwingWorker() {
            /**
             * Overridden!
             * 
             * @see de.tbuchloh.util.swing.SwingWorker#construct()
             */
            @Override
            public Object construct() {
                LOG.debug("Creating new thread for update-check!");
                try {
                    final CheckUpdate checker = new CheckUpdate();
                    if (!checker.check()) {
                        MainFrame.showStatus(M.format("new_version", new Object[] {
                                checker.getVersion(), checker.getUpdateSite()
                        }));
                    } else if (showMsgWhenMatches) {
                        MainFrame.showStatus(M.getString("version_up2date"));
                    }
                } catch (final IOException e) {
                    MainFrame.showStatus(M.format("version_error", e.getMessage()));
                }
                LOG.debug("update-check thread finished!");
                return null;
            }
        };
        worker.start();
    }

    public static void showStatus(final String msg) {
        getInstance()._statusMsg.display(msg);
    }

    public void showTray() {
        _tray.show();
    }

    private void init() {
        _tray = SystemTrayFactory.create(this);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setIconImage(loadImage(ICON_PNG).getImage());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                onClosing();
            }
        });
        _main = new MainFramePanel(this);
        this.getContentPane().setLayout(new BorderLayout());
        setMainView(_main);

        final StatusBar statusBar = new StatusBar();
        _statusMsg = new SimpleTextField(SimpleTextField.LEFT);
        statusBar.add(_statusMsg, 0, 1);
        this.getContentPane().add(statusBar, BorderLayout.SOUTH);
        this.pack();
    }

    protected static ImageIcon loadImage(final String filename) {
        return new ImageIcon(MainFrame.class.getResource(filename));
    }

    protected void setMainView(final JComponent view) {
        if (_currentView != null) {
            this.getContentPane().remove(_currentView);
        }
        this.getContentPane().add(view, BorderLayout.CENTER);
        this.repaint();
        _currentView = view;
    }

    protected void exit() {
        _main.exit();
        Toolkit.saveWindowState(this);
        setVisible(false);
        _tray.dispose();
        dispose();
        System.exit(0);
    }

    protected void onClosing() {
        LOG.debug("Closing window!");
        if (!_tray.isValid()) {
            quit();
        } else {
            final String key = "onClosing.hide_info";
            MessagePane.showInfoMessage(this, M.getString(key), key);
            dispose();
        }
    }

    /**
     * @param file
     *            is the file to load.
     */
    @Override
    public void loadFile(final File file) {
        _main.loadFile(file);
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.systray.IMainFrame#quit()
     */
    protected void quit() {
        if (_main.confirmCloseDoc()) {
            exit();
        }
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.systray.IMainFrame#getAction(java.lang.String)
     */
    @Override
    public ActionCallback getAction(final String key) {
        return _main.getAction(key);
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.systray.IMainFrame#getTrayIconURL()
     */
    @Override
    public URL getTrayIconURL() {
        return getClass().getResource(ICON_PNG);
    }

    /**
     * load the last opened file.
     */
    public void loadLastFile() {
        _main.loadLastFile();
    }

    public void contentChanged(final boolean changed) {
        _main.contentChanged(changed);
    }

    public ISystemTray getSystemTray() {
        return _tray;
    }

    /**
     * @see de.tbuchloh.kiskis.gui.systray.IMainFrame#getPopupMenu()
     */
    @Override
    public PopupMenu getPopupMenu() {
        final PopupMenu popup = new PopupMenu();
        for (final Action act : _main.getPopupActions()) {
            if (act == null) {
                popup.addSeparator();
            } else {
                final MenuItem mi = new MenuItem((String) act.getValue(Action.NAME));
                mi.setEnabled(act.isEnabled());
                mi.addActionListener(act);
                mi.setFont(new Font("Arial", Font.BOLD, 12));
                popup.add(mi);
            }
        }
        return popup;
    }

    /**
     * show the tip of the day
     */
    public void showTipOfTheDay() {
        _main.showTipOfTheDay(false);
    }

    /**
     * Displays the selftest result
     */
    public void showSelftest() {
        _main.showSelftest();
    }

}
