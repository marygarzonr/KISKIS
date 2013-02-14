/*
 * Copyright (C) 2010 by Tobias Buchloh.
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
package de.tbuchloh.kiskis.gui.widgets;

import java.awt.BorderLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.tbuchloh.kiskis.util.DateUtils;
import de.tbuchloh.util.event.ContentChangedEvent;
import de.tbuchloh.util.event.ContentListener;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.swing.widgets.ObservableCheckBox;
import de.tbuchloh.util.swing.widgets.ObservableWidget;

/**
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 20.10.2010
 */
public class ArchivableElement extends JPanel implements ObservableWidget {

    private static final Messages M = new Messages(ArchivableElement.class);

    /**
     * Die serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
	final JFrame frame = new JFrame();
	frame.getContentPane().setLayout(new BorderLayout());
	frame.getContentPane().add(
		new ArchivableElement(DateUtils.getCurrentDateTime()));
	frame.pack();
	frame.setVisible(true);
    }

    protected ObservableCheckBox _checkBox;

    protected JLabel _dateLabel;

    protected Date _object;

    private final Set<ContentListener> listeners = new LinkedHashSet<ContentListener>();

    public ArchivableElement(Calendar calendar) {
	super(new BorderLayout(5, 0));
	if (calendar != null) {
	    _object = calendar.getTime();
	}
	init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addContentListener(ContentListener ch) {
	listeners.add(ch);
    }

    private void init() {
	_checkBox = new ObservableCheckBox();
	_checkBox.setToolTipText(M.format("tooltip"));
	_checkBox.setSelected(_object != null);
	_checkBox.addContentListener(new ContentListener() {

	    @Override
	    public void contentChanged(ContentChangedEvent e) {
		updateModel(_checkBox.isSelected());
		refreshWidgets();
		notifyListeners();
	    }
	});
	_dateLabel = new JLabel(M.format("labelText"));

	this.setToolTipText(M.format("tooltip"));
	this.add(_checkBox, BorderLayout.EAST);
	this.add(_dateLabel);

	refreshWidgets();
    }

    protected void notifyListeners() {
	final ContentChangedEvent ev = new ContentChangedEvent(this, _object);
	for (final ContentListener cl : listeners) {
	    cl.contentChanged(ev);
	}
    }

    protected void refreshWidgets() {
	final boolean selected = _object != null;
	_checkBox.setSelected(selected);
	if (selected) {
	    final Date archivedOnDate = _object;
	    _dateLabel.setToolTipText(M
		    .format("archivedOnText", archivedOnDate));
	} else {
	    _dateLabel.setToolTipText(M.format("notArchivedText"));
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeContentListener(ContentListener cl) {
	listeners.remove(cl);
    }

    private void updateModel(boolean value) {
	if (value) {
	    _object = DateUtils.getCurrentDateTime().getTime();
	} else {
	    _object = null;
	}
    }

    /**
     * @return the selected Date
     */
    public Calendar getArchivedOn() {
	if (_object == null) {
	    return null;
	}
	final Calendar cal = DateUtils.getCurrentDateTime();
	cal.setTime(_object);
	return cal;
    }
}
