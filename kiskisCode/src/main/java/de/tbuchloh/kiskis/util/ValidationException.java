/**
 *
 */
package de.tbuchloh.kiskis.util;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: ValidationException.java,v 1.1 2007/12/02 12:44:03 tbuchloh Exp
 *          $
 */
public class ValidationException extends KisKisException {

	private static final long serialVersionUID = 1L;

	public ValidationException(final String msg) {
		super(msg);
	}

}
