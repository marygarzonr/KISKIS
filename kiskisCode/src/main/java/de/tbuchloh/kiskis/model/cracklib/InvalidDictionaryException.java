package de.tbuchloh.kiskis.model.cracklib;

import de.tbuchloh.kiskis.util.KisKisException;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: InvalidDictionaryException.java,v 1.1 2007/12/02 12:44:05
 *          tbuchloh Exp $
 */
public class InvalidDictionaryException extends KisKisException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an empty InvalidDictionaryException
	 */
	public InvalidDictionaryException(final String message) {
		super(message);
	}

	/**
	 * Creates an empty InvalidDictionaryException
	 */
	public InvalidDictionaryException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

}
