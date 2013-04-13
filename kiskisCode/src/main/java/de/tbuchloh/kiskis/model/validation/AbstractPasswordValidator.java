/**
 * 
 */
package de.tbuchloh.kiskis.model.validation;

import de.tbuchloh.util.localization.Messages;

/**
 * @author Group Mary
 *
 */
public abstract class AbstractPasswordValidator implements IPasswordValidator {

    Messages M;
    
    void initializeMessages(Class<?> clazz) {
        M = new Messages(clazz);
    }
}
