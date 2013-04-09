package de.tbuchloh.kiskis.model;

public abstract class PasswordMode{
    
    public static final String CONSONANT_CHARS = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";

    public static final String EXTRA_CHARS = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~ ";
    
    public static final String NUMBER_CHARS = "0123456789";
    
    public static final String VOCAL_CHARS = "aeiouAEIOU";
    
    private char[] pwd =  null;
    
    public char[] getPasswordMode (){
        return pwd; 
    }
    
    public void setPasswordMode(char[] pwdP){
        pwd = pwdP;
    }
    
    public  char getRandomChar(final String pChars) {
        final int high = pChars.length() - 1;
        final int c = (int) (high * Math.random());
        final char random = pChars.charAt(c);
        return random;
    }

}
