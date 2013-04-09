package de.tbuchloh.kiskis.model;

public class TemplatePassword extends PasswordMode{

    private final char[] pwd;
    
    public TemplatePassword(PasswordTemplate templateP, boolean mixCaseP){
        
        final String templateStringP = templateP.getTemplate();
        pwd = new char[templateStringP.length()];   
        
        createTemplatePassword(templateStringP, mixCaseP);
        setPasswordMode(pwd);
    }
    
    
    
    /**
     * @param template
     *            the template to use
     * @param useMixCase
     *            true if a random order of lower and upper case characters should be used. False if the case should be
     *            used from the template.
     * @return the
     */
    private  void createTemplatePassword(String template, boolean useMixCase) {

        for (int i = 0; i < template.length(); ++i) {
            pwd[i] = randomCharFromClass(template.charAt(i), useMixCase);
        }

    }
    
    private  char randomCharFromClass(final char c, boolean useMixCase) {
        final char mode = Character.toLowerCase(c);

        char random = 0;
        switch (mode) {
        case PasswordTemplate.CONSONANT:
            random = getRandomChar(CONSONANT_CHARS);
            break;
        case PasswordTemplate.VOCAL:
            random = getRandomChar(VOCAL_CHARS);
            break;
        case PasswordTemplate.DIGIT:
            random = getRandomChar(NUMBER_CHARS);
            break;
        case PasswordTemplate.SPECIAL:
            random = getRandomChar(EXTRA_CHARS);
            break;
        case PasswordTemplate.ANY:
            random = getRandomChar(VOCAL_CHARS + CONSONANT_CHARS + EXTRA_CHARS + NUMBER_CHARS);
            break;
        case PasswordTemplate.ALPHA:
            random = getRandomChar(VOCAL_CHARS + CONSONANT_CHARS);
            break;
        case PasswordTemplate.ALPHA_NUM:
            random = getRandomChar(VOCAL_CHARS + CONSONANT_CHARS + NUMBER_CHARS);
            break;
        default:
            random = c;
        }

        if (useMixCase) {
            if (Math.random() <= 0.5) {
                return Character.toUpperCase(random);
            }
            return Character.toLowerCase(random);
        }

        if (Character.isUpperCase(c)) {
            return Character.toUpperCase(random);
        }
        return Character.toLowerCase(random);
    }
    
    
    
}
