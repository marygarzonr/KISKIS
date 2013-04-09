package de.tbuchloh.kiskis.model;

public class SecurePassword extends PasswordMode{
    
    private final char[] pwd;
    
    
    /**
     * @param length
     *            is the intended password length
     * @return the randomly generated secure password
     */
    public SecurePassword(int length) {        
        pwd = new char[length];   
        createSecurePassword(length);
        setPasswordMode(pwd);
        
    }
    
    private void createSecurePassword(int length){        
        final String pChars = CONSONANT_CHARS + NUMBER_CHARS + VOCAL_CHARS + EXTRA_CHARS;
        for (int i = 0; i < length; ++i) {
            final char random = getRandomChar(pChars);
            pwd[i] = random;
        }
        
    }
}
