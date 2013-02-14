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

package de.tbuchloh.kiskis.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.gui.widgets.BasicTextField;
import de.tbuchloh.kiskis.gui.widgets.BasicUrlField;
import de.tbuchloh.kiskis.gui.widgets.PasswordElement;
import de.tbuchloh.kiskis.model.BankAccount;
import de.tbuchloh.kiskis.model.CreditCard;
import de.tbuchloh.kiskis.model.GenericAccount;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.NetAccount;
import de.tbuchloh.kiskis.model.SecuredFile;
import de.tbuchloh.kiskis.util.DateUtils;
import de.tbuchloh.util.event.ContentChangedEvent;
import de.tbuchloh.util.event.ContentListener;
import de.tbuchloh.util.event.ContentListenerList;
import de.tbuchloh.util.swing.SpringUtilities;
import de.tbuchloh.util.swing.widgets.LinkLabel;
import de.tbuchloh.util.swing.widgets.ObservableTextArea;
import de.tbuchloh.util.swing.widgets.ObservableTextField;
import de.wannawork.jcalendar.JCalendarComboBox;

/**
 * <b>AbstractAccountDetailView</b>:
 * 
 * @author gandalf
 * @version $Id: AbstractAccountDetailView.java,v 1.10 2007/02/18 14:37:48 tbuchloh Exp
 *          $
 */
public abstract class AbstractAccountDetailView extends SpecialView implements
SpecialActions, ContentListener {
    protected static final class DateField implements TypedField,
    ChangeListener {

	private final JCalendarComboBox _comp;

	private final ContentListenerList _listener;

	/**
	 * creates a new DateField
	 */
	public DateField() {
	    super();
	    _comp = new JCalendarComboBox();
	    _comp.addChangeListener(this);
	    _listener = new ContentListenerList();
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#addContentListener(de.tbuchloh.util.event.ContentListener)
	 */
	@Override
	public void addContentListener(final ContentListener l) {
	    _listener.addListener(l);
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#getComponent()
	 */
	@Override
	public Component getComponent() {
	    return _comp;
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#getValue()
	 */
	@Override
	public Object getValue() {
	    return _comp.getCalendar();
	}

	/**
	 * Overridden!
	 * 
	 * @param obj
	 *            has to be a Calendar
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object obj) {
	    if (obj == null) {
		obj = DateUtils.getCurrentDateTime();
	    }
	    _comp.setCalendar((Calendar) obj);
	}

	/**
	 * Overridden!
	 * 
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(final ChangeEvent e) {
	    _listener.fireEvent(new ContentChangedEvent(this));
	}

    }
    protected static final class PasswordField implements TypedField,
    ContentChangedListener {

	private final PasswordElement _comp;

	private final ContentListenerList _listener;

	/**
	 * creates a new PasswordField
	 */
	public PasswordField() {
	    _comp = new PasswordElement(new char[0]);
	    _comp.addContentChangedListener(this);
	    _comp.setShowQualityLabel(false);
	    _listener = new ContentListenerList();
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#addContentListener(de.tbuchloh.util.event.ContentListener)
	 */
	@Override
	public void addContentListener(final ContentListener l) {
	    _listener.addListener(l);
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.ContentChangedListener#contentChanged(boolean)
	 */
	@Override
	public void contentChanged(final boolean changed) {
	    _listener.fireEvent(new ContentChangedEvent(this));
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#getComponent()
	 */
	@Override
	public Component getComponent() {
	    return _comp;
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#getValue()
	 */
	@Override
	public Object getValue() {
	    return String.valueOf(_comp.getPwd());
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object obj) {
	    char[] pwd;
	    if (obj == null) {
		pwd = new char[0];
	    } else if (obj instanceof String) {
		pwd = ((String) obj).toCharArray();
	    } else if (obj instanceof char[]) {
		pwd = (char[]) obj;
	    } else {
		throw new IllegalArgumentException(obj.getClass().getName());
	    }
	    _comp.setPassword(pwd);
	}
    }

    protected static final class StringField implements TypedField {

	private final ObservableTextField _comp;

	public StringField() {
	    _comp = new BasicTextField();
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#addContentListener(de.tbuchloh.util.event.ContentListener)
	 */
	@Override
	public void addContentListener(final ContentListener l) {
	    _comp.addContentListener(l);
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#getComponent()
	 */
	@Override
	public Component getComponent() {
	    return _comp;
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#getValue()
	 */
	@Override
	public Object getValue() {
	    return _comp.getText();
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(final Object obj) {
	    _comp.setText((String) obj);
	}

    }

    protected static final class TextField implements TypedField {

	private final ObservableTextArea _comp;

	/**
	 * creates a new TextField
	 */
	public TextField() {
	    super();
	    _comp = new ObservableTextArea();
	    _comp.setRows(5);
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#addContentListener(de.tbuchloh.util.event.ContentListener)
	 */
	@Override
	public void addContentListener(final ContentListener l) {
	    _comp.addContentListener(l);
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#getComponent()
	 */
	@Override
	public Component getComponent() {
	    final JScrollPane sp = new JScrollPane(_comp);
	    return sp;
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#getValue()
	 */
	@Override
	public Object getValue() {
	    return _comp.getText();
	}

	/**
	 * Overridden!
	 * 
	 * @param obj
	 *            has to be a String.
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(final Object obj) {
	    _comp.setText((String) obj);
	    _comp.setCaretPosition(0);
	}

    }

    protected static interface TypedField {

	public void addContentListener(ContentListener l);

	public Component getComponent();

	public Object getValue();

	public void setValue(Object obj);

    }

    protected static final class UrlField implements TypedField,
    ContentListener {

	private final BasicUrlField _comp;

	/**
	 * creates a new UrlField
	 */
	public UrlField() {
	    super();
	    _comp = new BasicUrlField();
	    _comp.addContentListener(this);
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#addContentListener(de.tbuchloh.util.event.ContentListener)
	 */
	@Override
	public void addContentListener(final ContentListener l) {
	    _comp.addContentListener(l);
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.util.event.ContentListener#contentChanged(de.tbuchloh.util.event.ContentChangedEvent)
	 */
	@Override
	public void contentChanged(final ContentChangedEvent e) {
	    if (e.getSource() == _comp) {
		_comp.getOpenURLAction().setEnabled(
			_comp.getText().length() > 0);
	    }
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#getComponent()
	 */
	@Override
	public Component getComponent() {
	    final JPanel panel = new JPanel(new BorderLayout(10, 0));
	    panel.add(_comp);
	    panel.add(new LinkLabel(_comp.getOpenURLAction()),
		    BorderLayout.EAST);
	    return panel;
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#getValue()
	 */
	@Override
	public Object getValue() {
	    return _comp.getText();
	}

	/**
	 * Overridden!
	 * 
	 * @param obj
	 *            has to be a String.
	 * @see de.tbuchloh.kiskis.gui.GenericAccountView.TypedField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(final Object obj) {
	    _comp.setText((String) obj);
	}

    }

    protected static final Log LOG = LogFactory.getLog(AbstractAccountDetailView.class);

    private static final Hashtable<Class, Class> NODE_MAP = initNodeMap();

    private static final long serialVersionUID = 1L;

    public static AbstractAccountDetailView create(final ModelNode node) {
	final Class<?> c = NODE_MAP.get(node.getClass());
	try {
	    final Constructor<?> builder = c
	    .getConstructor(new Class[] { ModelNode.class });
	    return (AbstractAccountDetailView) builder
	    .newInstance(new Object[] { node });
	} catch (final Exception e) {
	    throw new Error("should never happen!", e); //$NON-NLS-1$
	}
    }

    /**
     * initializes the map which stores (Class, Class)-pairs. The key is the
     * model class, the value is the view class.
     * 
     * @return the map
     */
    private static Hashtable<Class, Class> initNodeMap() {
	final Hashtable<Class, Class> map = new Hashtable<Class, Class>();
	map.put(BankAccount.class, BankAccountView.class);
	map.put(CreditCard.class, CreditCardView.class);
	map.put(GenericAccount.class, GenericAccountView.class);
	map.put(NetAccount.class, NetAccountView.class);
	map.put(SecuredFile.class, SecuredFileView.class);
	return map;
    }

    /**
     * creates a new AbstractAccountDetailView
     * 
     */
    public AbstractAccountDetailView() {
	super();
    }

    protected void addRow(final JPanel panel, final String labelText,
	    final Component comp) {
	final JLabel label = new JLabel(labelText, SwingConstants.TRAILING);
	panel.add(label);

	label.setLabelFor(comp);
	panel.add(comp);
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.util.event.ContentListener#contentChanged(de.tbuchloh.util.event.ContentChangedEvent)
     */
    @Override
    public final void contentChanged(final ContentChangedEvent e) {
	fireContentChangedEvent(true);
    }

    /**
     * @return the title of this detailed view.
     */
    public abstract String getTitle();

    /**
     * @return true if the {@link AbstractAccountDetailView} should be hidden, false if
     *         it should be visible
     */
    public boolean isHidden() {
	return false;
    }

    protected void makeGrid(final JPanel panel, final int rows) {
	SpringUtilities.makeCompactGrid(panel, rows, 2, 20, 10, 10, 10);
    }
}
