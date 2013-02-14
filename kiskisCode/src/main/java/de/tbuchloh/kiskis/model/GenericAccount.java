package de.tbuchloh.kiskis.model;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.tbuchloh.kiskis.model.template.AccountProperty;
import de.tbuchloh.kiskis.model.template.AccountType;

/**
 * <b>GenericAccount</b>: implements an user template account.
 * 
 * it can contain various named and typed properties such as: - String - URL - TANList - Pin - Date - DateTime - Number
 * 
 * @see de.tbuchloh.kiskis.model.template.AccountPropertyTypes
 * 
 *      Further attributes may be set by external components such as the LOG, i. e. URL's for graphical icons can be set
 *      as well.
 * 
 * @author gandalf
 * @version $Id$
 */
public final class GenericAccount extends SecuredElement {

    private static final long serialVersionUID = 1L;

    private static boolean equalsValues(final Map<AccountProperty, Object> lhs, final Map<AccountProperty, Object> rhs) {
        if (lhs.size() != rhs.size()) {
            return false;
        }

        for (final Map.Entry<AccountProperty, Object> entry : lhs.entrySet()) {
            if (!entry.getValue().equals(rhs.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    private AccountType _type;

    /**
     * (AccountProperty, Object)
     */
    private Map<AccountProperty, Object> _values;

    /**
     * creates a new GenericAccount
     */
    public GenericAccount() {
        super();
        _values = new HashMap<AccountProperty, Object>();
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.SecuredElement#clone()
     */
    @Override
    public GenericAccount clone() {
        final GenericAccount ga = (GenericAccount) super.clone();
        ga._type = _type;
        ga._values = new HashMap<AccountProperty, Object>();
        for (final Map.Entry<AccountProperty, Object> entry : _values.entrySet()) {
            final AccountProperty p = entry.getKey();
            Object v = entry.getValue();
            if (v instanceof Calendar) {
                v = ((Calendar) v).clone();
            } else if (v instanceof char[]) {
                v = ((char[]) v).clone();
            } else {
                assert v instanceof String;
            }
            ga.setValue(p, v);
        }
        assert this != ga;
        assert !this.equals(ga);
        return ga;
    }

    /**
     * @return the name of the associated template.
     */
    public String getTemplateName() {
        return getType().getName();
    }

    /**
     * @return Returns the type.
     */
    public final AccountType getType() {
        return _type;
    }

    /**
     * @param p
     *            is the property.
     * @return is the associated value or null if any
     */
    public Object getValue(final AccountProperty p) {
        final Object ret = _values.get(p);
        assert p.isValid(ret);
        return ret;
    }

    /**
     * @return Returns the values.
     */
    public final Map<AccountProperty, Object> getValues() {
        return Collections.unmodifiableMap(_values);
    }

    /**
     * @param p
     *            is the Property to look up.
     * @return true, if the account has a value for this property.
     */
    public boolean hasProperty(final AccountProperty p) {
        return getValue(p) != null;
    }

    @Override
    public boolean matches(final SecuredElement other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GenericAccount)) {
            return false;
        }

        final GenericAccount castOther = (GenericAccount) other;
        return super.matches(other) && _type.equals(castOther._type) && equalsValues(_values, castOther._values);
    }

    /**
     * @param type
     *            The type to set.
     */
    public final void setType(final AccountType type) {
        _type = type;
    }

    /**
     * @param p
     *            is the the property.
     * @param value
     *            is the value to set.
     */
    public void setValue(final AccountProperty p, final Object value) {
        assert p.isValid(value) : "Property: " + p + "=" + value;
        _values.put(p, value);
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.model.ModelNode#visit(de.tbuchloh.kiskis.model.ModelNodeVisitor)
     */
    @Override
    public void visit(final ModelNodeVisitor visitor) {
        visitor.visit(this);
    }
}
