package de.tbuchloh.kiskis.model;

public class PasswordModeFactory  {   
    
    public static final int SECURE = 0;
    public static final int HUMAN_READABLE = 1;
    public static final int TEMPLATE = 2;
    
    public Password createPasswordMode(int modePassword, int length, PasswordTemplate template, boolean mixCase){        
        
        Password passwordMode = null;
        
        if(modePassword == HUMAN_READABLE){            
            PasswordMode human = new HumanReadablePassword(length);            
            return new Password(human.getPasswordMode());           
                       
        }else if(modePassword == SECURE){
            PasswordMode secure =  new SecurePassword(length);            
            return new Password(secure.getPasswordMode());            
            
        }else if(modePassword == TEMPLATE){
            PasswordMode templ =  new TemplatePassword(template,mixCase);
            return new Password(templ.getPasswordMode());
        }
        
        return passwordMode;        
    }
}
