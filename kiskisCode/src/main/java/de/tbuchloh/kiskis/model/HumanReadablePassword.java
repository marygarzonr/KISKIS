package de.tbuchloh.kiskis.model;

public class HumanReadablePassword extends PasswordMode{
    
    private final char[] pwd;
    
    /**
     * @param length
     *            is the intended password length
     * @return the generated readable password
     */
    public HumanReadablePassword(int length){
        
        pwd = new char[length];
        createHumanReadable(length);        
        setPasswordMode(pwd);
    }
    
    
    private void createHumanReadable(int length){
        
        int pc = (int) (Math.random() * 3);        
        final String[] pool = {
                CONSONANT_CHARS, VOCAL_CHARS, NUMBER_CHARS
        };
        final int digits = (int) ((length / 2) * Math.random()) + 1;
        for (int i = 0; i < length; ++i) {
            // toggle between vocals and consonants or pad with numbers
            if (i >= length - digits) {
                pc = 2;
            } else if (pc == 0) {
                pc = 1;
            } else {
                pc = 0;
            }

            final int c = (int) (pool[pc].length() * Math.random());
            pwd[i] = pool[pc].charAt(c);
        }
        
    }

}
