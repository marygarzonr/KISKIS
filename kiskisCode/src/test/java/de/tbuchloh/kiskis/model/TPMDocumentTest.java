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
 * http://www.sourceforge.net/projects/KisKis
 */

package de.tbuchloh.kiskis.model;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;

import junit.framework.TestCase;
import de.tbuchloh.kiskis.persistence.XMLProcessing;
import de.tbuchloh.kiskis.util.DateUtils;
import de.tbuchloh.util.reflection.DeepCopyUtil;

/**
 * <b>PGPEncryptionTest</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public class TPMDocumentTest extends TestCase {

    private static final File FILE = new File("target/PGPEncryptionTest.xml");

    private static final TPMDocument DOC = initDocument();

    /**
     * @return create a simple net account
     */
    private static ModelNode createNetAccount(final String name, String xmlDateTime) {
        final NetAccount node = new NetAccount();
        fillSecuredElement(name, node, xmlDateTime);

        node.setEmail("email@mail.de");
        node.setUrl("http://www.google.de/");
        node.setUsername("myuser_" + name);
        return node;
    }

    private static void fillSecuredElement(final String name,
            final SecuredElement node, String xmlDateTime) {
        node.setName(name);
        node.setComment("comment for " + name);
        node.setCreationDate(getCalendar("2006-06-01T18:00:00"));
        node.setLastChangeDate(getCalendar("2006-06-01T18:00:00"));
        node.setLastViewedDate(getCalendar(xmlDateTime));
        node.setExpiresNever(true);
        node.setPwd(createPassword(name));
        node.setPwd(createPassword(name + " v2"));
        node.setViewCounter((int) (Math.random() * 100));
    }

    private static Password createPassword(final String name) {
        final Password p = new Password(("password" + name).toCharArray(),
                getShortCalendar(), getShortCalendar());
        return p;
    }

    private static Calendar getCalendar(String xmlDateTime) {
        try {
            final Calendar cal = DateUtils.getCurrentDateTime();
            cal.setTime(XMLProcessing.DATE_LONG.parse(xmlDateTime));
            return cal;
        } catch (final ParseException e) {
            throw new Error();
        }
    }

    private static Calendar getShortCalendar() {
        try {
            final Calendar cal = DateUtils.getCurrentDateTime();
            cal.setTime(XMLProcessing.DATE_SHORT.parse("2006-06-01"));
            return cal;
        } catch (final ParseException e) {
            throw new Error();
        }
    }

    /**
     * @return the test document to compare against.
     */
    private static TPMDocument initDocument() {
        final TPMDocument doc = new TPMDocument(FILE);
        final Group g1 = new Group("root");
        doc.setGroups(g1);

        g1.add(createNetAccount("Net 1", "2010-10-31T21:43:00"));
        g1.add(createNetAccount("Net 2", "2010-10-30T21:43:00"));

        final Group c1 = new Group("child 1");
        g1.add(c1);

        c1.add(createNetAccount("Net 3", "2010-11-30T21:43:00"));

        // last viewed
        c1.add(createNetAccount("Net 4", "2010-11-30T21:43:01"));
        c1.add(createCreditCard("Credit Card 1", "2010-11-30T21:43:00"));

        final Group c12 = new Group("child 1.2");
        c1.add(c12);

        c12.add(createNetAccount("Net 5", "2010-11-28T00:00:59"));
        c12.add(createSecuredFile("File 1", "2010-11-30T21:43:00"));
        c12.add(createBankAccount("Bank Account", "2010-11-30T21:43:00"));

        // g1.add(create)
        return doc;
    }

    private static ModelNode createCreditCard(final String name, String xmlDateTime) {
        final CreditCard cc = new CreditCard();
        fillSecuredElement(name, cc, xmlDateTime);
        cc.setDeployer("COMMERZBANK");
        cc.setExpDate(getCalendar("2006-06-01T18:00:00"));
        cc.setNumber("544552");
        cc.setPin("9876".toCharArray());
        return cc;
    }

    private static ModelNode createBankAccount(final String name, String xmlDateTime) {
        final BankAccount ba = new BankAccount();
        fillSecuredElement(name, ba, xmlDateTime);
        ba.setBankID("123456");
        ba.setBankName("DEUTSCHE BANK");
        ba.setNumber("12345678");
        ba.setTelebankingPin("4321".toCharArray());
        return ba;
    }

    private static ModelNode createSecuredFile(final String name, String xmlDateTime) {
        final SecuredFile sf = new SecuredFile();
        fillSecuredElement(name, sf, xmlDateTime);
        sf.setFile(new File("build.xml").getAbsolutePath());
        return sf;
    }

    /**
     * test the equals-method
     */
    public void testEquals() throws Exception {
        assertEquals(DOC, DeepCopyUtil.cloneSerializable(DOC));
    }

    public void testMatches() throws Exception {
        assertTrue(DOC.matches((TPMDocument) DeepCopyUtil
                .cloneSerializable(DOC)));
    }

    public void testFindLastUsedElement() throws Exception {
        assertEquals("Net 4", DOC.findLastViewedElement().getName());
    }
}
