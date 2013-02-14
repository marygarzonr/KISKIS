/*
 * Copyright (C) 2010 by Tobias Buchloh.
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
package de.tbuchloh.kiskis.gui.feedback;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.awt.Desktop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import de.tbuchloh.util.swing.TextMessageBox;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 24.11.2010
 */
public class SubmitFeatureRequestTest {

    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLog(SubmitFeatureRequestTest.class);

    private final AbstractSubmitFeedback object = new SubmitFeatureRequest();

    @Test
    public void testCreateMailToLinkWithException() throws Exception {
        final String s = object.createMailToString(TextMessageBox.exceptionToString(new Exception()));
        LOG.info(s);
    }

    @Test
    public void testCreateMailToLinkWithInfo() throws Exception {
        final String s = object.createMailToString("My error");
        LOG.info(s);
        Assert.assertThat(s, not(containsString("My%20error")));
    }

    @Test
    public void testCreateMailSubject() throws Exception {
        final String s = object.createMailSubject();
        LOG.info(s);
        Assert.assertThat(s, containsString("[Feature] Keep It Secret! Keep It Safe!"));
    }

    @Test
    public void testCreateMailBody() throws Exception {
        final String s = object.createMailBody("My information");
        LOG.info(s);
        Assert.assertThat(s, not(containsString("My information")));
    }

    public static void main(String[] args) throws Exception {
        Desktop.getDesktop().mail();
        new SubmitFeatureRequest().openMail(TextMessageBox.exceptionToString(new Exception()));
    }
}
