/**
 *
 */
package de.tbuchloh.kiskis.model.cracklib;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Tobias Buchloh (gandalf)
 * @version $Id: DictionaryTest.java,v 1.2 2007/12/02 13:18:43 tbuchloh Exp $
 */
public class DictionaryTest {

    private static final String VALID_WORDLIST_DICT = "target/valid_wordlist_dict";

    private static final String VALID_WORDLIST = "tools/dictionaries/test_words";

    private static final String INVALID_WORDLIST = "tools/dictionaries/test_invalid_words";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for
     * {@link de.tbuchloh.kiskis.model.cracklib.Dictionary#createDictionary(java.io.File, java.lang.String)} .
     */
    @Test
    public void testCreateDictionaryFromUnknownWordlist() throws Exception {
        final File srcfile = new File("target/unknownFile");
        try {
            Dictionary.createDictionary(srcfile, "target/unknown");
            fail();
        } catch (final FileNotFoundException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(srcfile.getName()));
        }
    }

    @Test
    public void testCreateDictionaryWithInvalidTargetFile() throws Exception {
        final String target = "/ballaballa/boo";
        try {
            Dictionary.createDictionary(new File(VALID_WORDLIST), target);
        } catch (final FileNotFoundException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(target));
        }
    }

    @Test
    public void testCreateDictionaryWithInvalidWordlist() throws Exception {
        Dictionary.createDictionary(new File(INVALID_WORDLIST), "target/invalid_wordlist_dic");
    }

    @Test
    public void testCreateDictionaryWithValidWordlist() throws Exception {
        final String target = VALID_WORDLIST_DICT;
        final Dictionary dic = Dictionary.createDictionary(new File(VALID_WORDLIST), target);
        assertTrue(dic.toString(), dic.getDirectory().contains(target));
        System.out.println(dic.toString());
    }

    /**
     * Test method for {@link de.tbuchloh.kiskis.model.cracklib.Dictionary#open(java.lang.String)} .
     */
    @Test
    public void testOpen() {
        final String word = "secret";
        try {
            final Dictionary dic = Dictionary.open(VALID_WORDLIST_DICT);
            final String value = dic.lookup(word);
            assertNotNull(value);
            assertTrue(value, value.contains(word));

            final String strongPwd = "73623ghjebgwqdg222";
            assertNull(dic.lookup(strongPwd));
        } catch (final IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void testOpenDeletedDictionary() throws Exception {
        Dictionary.open(INVALID_WORDLIST + ".bak");
    }

    @Test
    public void testOpenInvalidDictionary() throws Exception {
        // is defect, but should open
        Dictionary.open("target/invalid_wordlist_dic");
    }

}
