package de.tbuchloh.kiskis.persistence;

import java.io.File;

/**
 * <b>SimplePasswordProxy</b>:
 * 
 * @author Tobias Buchloh
 * @version $Id$
 */
public final class SimplePasswordProxy implements PasswordProxy {

    private final char[] _pwd;

    /**
     * creates a new SimplePasswordProxy
     */
    public SimplePasswordProxy(final char[] pwd) {
	super();
	_pwd = pwd;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.persistence.PasswordProxy#getPassword()
     */
    @Override
    public char[] getPassword(File file) {
	return _pwd;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.persistence.PasswordProxy#getPassword(boolean)
     */
    @Override
    public char[] getPassword(File file, final boolean confirm) {
	return getPassword(file);
    }

}
