/*
 * Copyright (C) 2004 by Tobias Buchloh.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.sourceforge.net/projects/kiskis
 */

package de.tbuchloh.kiskis.model;

/**
 * <b>PasswordFactory</b>: Creates new password instances.
 * 
 * @author gandalf
 * @version $Id: PasswordFactory.java,v 1.5 2007/02/18 14:37:33 tbuchloh Exp $
 */
public abstract class PasswordFactory {

    private static final String CONSONANT_CHARS = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";

    private static final String EXTRA_CHARS = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~ ";

    public static final int HUMAN_READABLE = 1;

    private static final String NUMBER_CHARS = "0123456789";

    public static final int SECURE = 0;

    private static final String VOCAL_CHARS = "aeiouAEIOU";

    /**
     * Factory: creates new passwords.
     * 
     * @param creationMode
     *            SECURE or HUMAN_READABLE
     * @param length
     *            desired length
     * @return the new password object
     */

    public static Password create(final int creationMode, final int length) {
        assert length > 0;
        Password pwd = null;
        switch (creationMode) {
        case HUMAN_READABLE:
            pwd = createHumanReadable(length);
            break;
        case SECURE:
            pwd = createSecure(length);
            break;
        default:
            throw new Error("implementation missing!");
        }
        // MODEL.debug(pwd.toString());
        assert pwd.getPwd().length == length;
        return pwd;
    }

    /**
     * @param template
     *            the template
     * @return a newly generated Password
     */
    public static Password create(final PasswordTemplate template) {
        return create(template, false);
    }

    /**
     * @param length
     *            is the intended password length
     * @return the generated readable password
     */
    private static Password createHumanReadable(final int length) {
        int pc = (int) (Math.random() * 3);
        final char[] pwd = new char[length];
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
        return new Password(pwd);
    }

    /**
     * @param length
     *            is the intended password length
     * @return the randomly generated secure password
     */
    private static Password createSecure(final int length) {
        final String pChars = CONSONANT_CHARS + NUMBER_CHARS + VOCAL_CHARS + EXTRA_CHARS;
        final char[] pwd = new char[length];

        for (int i = 0; i < length; ++i) {
            final char random = getRandomChar(pChars);
            pwd[i] = random;
        }
        return new Password(pwd);
    }

    private static char getRandomChar(final String pChars) {
        final int high = pChars.length() - 1;
        final int c = (int) (high * Math.random());
        final char random = pChars.charAt(c);
        return random;
    }

    private static char randomCharFromClass(final char c, boolean useMixCase) {
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

    /**
     * @param template
     *            the template to use
     * @param useMixCase
     *            true if a random order of lower and upper case characters should be used. False if the case should be
     *            used from the template.
     * @return the
     */
    public static Password create(PasswordTemplate template, boolean useMixCase) {
        final String tmp = template.getTemplate();
        final char[] pwd = new char[tmp.length()];
        for (int i = 0; i < tmp.length(); ++i) {
            pwd[i] = randomCharFromClass(tmp.charAt(i), useMixCase);
        }
        return new Password(pwd);
    }
}
