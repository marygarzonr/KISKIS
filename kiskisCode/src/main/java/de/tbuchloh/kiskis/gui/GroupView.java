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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.tbuchloh.kiskis.gui.widgets.BasicTextField;
import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.util.event.ContentChangedEvent;
import de.tbuchloh.util.event.ContentListener;
import de.tbuchloh.util.swing.widgets.ObservableTextArea;

/**
 * <b>GroupView</b>: Allows editing of group capabilities.
 * 
 * @author gandalf
 * @version $Id: GroupView.java,v 1.10 2006/09/02 09:00:53 tbuchloh Exp $
 */
public final class GroupView extends DetailChildView implements SpecialActions {

    private static final long serialVersionUID = 1L;

    protected Group _group;

    protected JTextField _name;

    protected JTextArea _comment;

    /**
     * creates a new group view.
     * 
     * @param el
     *            item to display.
     */
    public GroupView(final Group el) {
	super();
	_group = el;
	final BasicTextField name = new BasicTextField(_group.getName());
	name.addContentListener(new ContentListener() {
	    @Override
	    public void contentChanged(final ContentChangedEvent e) {
		fireContentChangedEvent(true);
	    }
	});
	_name = name;
	final ObservableTextArea comment = new ObservableTextArea(
		_group.getComment());
	comment.setLineWrap(true);
	comment.setWrapStyleWord(true);
	comment.addContentListener(new ContentListener() {
	    @Override
	    public void contentChanged(final ContentChangedEvent e) {
		fireContentChangedEvent(true);
	    }
	});
	_comment = comment;

	init();
    }

    private void init() {
	this.setLayout(new BorderLayout(5, 10));

	final GridBagLayout bag = new GridBagLayout();
	final GridBagConstraints c = new GridBagConstraints();

	c.fill = GridBagConstraints.BOTH;
	c.insets = new Insets(5, 5, 5, 5);

	final JPanel fields = new JPanel(bag);
	c.weightx = 0;
	c.gridwidth = 1;
	final JLabel nameLabel = new JLabel(
		Messages.getString("GroupView.name_label")); //$NON-NLS-1$
	bag.setConstraints(nameLabel, c);
	fields.add(nameLabel);

	c.weightx = 1;
	c.gridwidth = GridBagConstraints.REMAINDER;
	bag.setConstraints(_name, c);
	fields.add(_name);
	this.add(
		createTitledPanel(Messages.getString("GroupView.border_title"),
			fields), BorderLayout.NORTH);

	final JPanel other = new JPanel(new BorderLayout());
	final JScrollPane sp = new JScrollPane();
	sp.getViewport().add(_comment);
	other.add(sp, BorderLayout.CENTER);
	this.add(
		createTitledPanel(
			Messages.getString("GroupView.comment_border_title"),
			other), BorderLayout.CENTER);
    }

    /**
     * @see de.tbuchloh.kiskis.gui.SpecialView#storeValues()
     */
    @Override
    protected void store() {
	_group.setName(_name.getText());
	_group.setComment(_comment.getText());
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.SpecialActions#getSpecialActions()
     */
    @Override
    public Collection getSpecialActions() {
	return new ArrayList();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.DetailChildView#setNameField(java.lang.String)
     */
    @Override
    public void setNameField(final String name) {
	_name.setText(name);
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.treeview.ItemRenamer#getNameField()
     */
    @Override
    public String getNameField() {
	return _name.getText();
    }

    /**
     * Overridden!
     * 
     * @see javax.swing.JComponent#grabFocus()
     */
    @Override
    public void grabFocus() {
	_name.selectAll();
	_name.grabFocus();
    }
}
