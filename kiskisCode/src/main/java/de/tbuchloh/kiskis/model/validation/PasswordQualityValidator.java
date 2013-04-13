package de.tbuchloh.kiskis.model.validation;

import de.tbuchloh.kiskis.model.Password;

public class PasswordQualityValidator extends AbstractPasswordValidator {

    @Override
    public String validatePassword(char[] pwd) {
        // TODO Auto-generated method stub
        final double entropy = Password.checkEffectiveBitSize(pwd);
        
//        String pwdStrength = null;
        if (entropy <= 32) {
            return M.getString("weak"); //$NON-NLS-1$
        } else if (entropy <= 64) {
            return M.getString("medium"); //$NON-NLS-1$
        } else if (entropy <= 128) {
            return M.getString("good"); //$NON-NLS-1$
        } else {
            // > 128
            return M.getString("excellent"); //$NON-NLS-1$
        }
    }

}
