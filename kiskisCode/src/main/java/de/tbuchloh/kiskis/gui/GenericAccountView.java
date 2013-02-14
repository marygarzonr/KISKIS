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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import de.tbuchloh.kiskis.model.GenericAccount;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.template.AccountProperty;
import de.tbuchloh.kiskis.model.template.AccountPropertyTypes;

/**
 * <b>GenericAccountView</b>:
 * 
 * @author gandalf
 * @version $Id: GenericAccountView.java,v 1.5 2007/02/18 14:37:47 tbuchloh Exp
 *          $
 */
public final class GenericAccountView extends AbstractAccountDetailView implements
AccountPropertyTypes {

    private static final long serialVersionUID = 1L;

    private final Map<AccountProperty, TypedField> _fields;

    protected GenericAccount _na;

    /**
     * creates a new netaccount view
     * 
     * @param na
     *            account to edit
     */
    public GenericAccountView(final ModelNode na) {
	super();
	_na = (GenericAccount) na;
	_fields = new HashMap<AccountProperty, TypedField>();
	init();
    }

    private TypedField createField(final AccountProperty p, final Object value) {
	TypedField comp = null;
	if (T_PWD.equals(p.getType())) {
	    comp = new PasswordField();
	} else if (T_STRING.equals(p.getType())) {
	    comp = new StringField();
	} else if (T_DATE.equals(p.getType())) {
	    comp = new DateField();
	} else if (T_TEXT.equals(p.getType())) {
	    comp = new TextField();
	} else if (T_URL.equals(p.getType())) {
	    comp = new UrlField();
	} else {
	    throw new Error("unknown property type: " + p.getType());
	}
	comp.setValue(value);
	comp.addContentListener(this);
	return comp;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.SpecialView#getSpecialActions()
     */
    @Override
    public Collection<Action> getSpecialActions() {
	return Collections.emptyList();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.gui.AbstractAccountDetailView#getTitle()
     */
    @Override
    public String getTitle() {
	return _na.getTemplateName();
    }

    private void init() {
	this.setLayout(new BorderLayout());

	final JPanel main = new JPanel(new SpringLayout());
	for (final AccountProperty p : _na.getType().getProperties()) {
	    final TypedField field = createField(p, _na.getValue(p));

	    addRow(main, p.getName() + ":", field.getComponent());

	    _fields.put(p, field);
	}

	makeGrid(main, _fields.size());

	this.add(main, BorderLayout.NORTH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHidden() {
	return _fields.isEmpty();
    }

    /**
     * @see de.tbuchloh.kiskis.gui.SpecialView#storeValues()
     */
    @Override
    protected void store() {
	for (final Map.Entry<AccountProperty, TypedField> entry : _fields
		.entrySet()) {
	    final AccountProperty key = entry.getKey();
	    final TypedField val = entry.getValue();
	    _na.setValue(key, val.getValue());
	}
    }
}
