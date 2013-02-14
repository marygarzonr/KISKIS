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

import java.awt.Desktop;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.util.BuildProperties;
import de.tbuchloh.kiskis.util.KisKisRuntimeException;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.text.StringTools;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 24.11.2010
 */
public abstract class AbstractSubmitFeedback {

    /**
     * Der Logger
     */
    private final Log LOG = LogFactory.getLog(AbstractSubmitFeedback.class);

    private final Messages M = new Messages(AbstractSubmitFeedback.class);

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    protected String createBuildInformation() {
        final StringBuilder sb = new StringBuilder();
        final Set<String> props = new TreeSet(Collections.list(BuildProperties.getBuildProperties().getKeys()));
        for (final String key : props) {
            sb.append(key);
            sb.append("=");
            sb.append(BuildProperties.getBuildProperties().getString(key));
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * @param errorInformation
     *            {@link #openMail(String)}
     * @return
     * @throws UnsupportedEncodingException
     */
    protected String createMailToString(String errorInformation) {
        final StringBuilder mailtoURI = new StringBuilder("mailto:");
        mailtoURI.append(encode(BuildProperties.getEmail()));
        mailtoURI.append("?subject=");
        mailtoURI.append(encode(createMailSubject()));
        mailtoURI.append("&body=");
        mailtoURI.append(encode(createMailBody(errorInformation)));
        final String mailtoString = mailtoURI.toString();

        if (LOG.isDebugEnabled()) {
            LOG.debug("mailtoString=" + mailtoString);
        }
        return mailtoString;
    }

    /**
     * @param errorInformation
     *            some additional information
     * @return the body
     */
    protected abstract String createMailBody(String errorInformation);

    /**
     * @return the mail subject line
     */
    protected abstract String createMailSubject();

    /**
     * @return the system information as string
     */
    @SuppressWarnings({
        "unchecked"
    })
    protected String createSysteminformation() {
        final StringBuilder sb = new StringBuilder();
        final Map<String, String> props = new TreeMap(System.getProperties());
        for (final Map.Entry<String, String> entry : props.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(StringTools.maxLength(entry.getValue(), 100));
            sb.append("\n");
        }
        return sb.toString();
    }

    protected String encode(String s) {
        try {
            // URL-encode does not work for mailto: we need to replace '+' with '%20'
            return URLEncoder.encode(s, "UTF-8").replace("+", "%20").replace("%0A", "%0D%0A");
        } catch (final UnsupportedEncodingException e) {
            throw new KisKisRuntimeException("Cannot encode s=" + s, e);
        }
    }

    /**
     * @param defaultBody
     *            the default bug body
     * @throws FeedbackException
     *             if the mail program cannot be opened
     */
    public void openMail(String defaultBody) throws FeedbackException {
        try {
            final String mailToString = createMailToString(defaultBody);
            Desktop.getDesktop().mail(new URI(mailToString));
        } catch (final IOException e1) {
            throwFeedbackException(e1, defaultBody);
        } catch (final URISyntaxException e1) {
            throwFeedbackException(e1, defaultBody);
        }
    }

    /**
     * @param message
     *            the error message
     * @param defaultBody
     *            the default body
     * @throws FeedbackException
     *             the exception
     */
    protected void throwFeedbackException(Throwable message, String defaultBody) throws FeedbackException {
        LOG.error(message, message);
        throw new FeedbackException(M.format("mail.error", StringTools.maxLength(message.getMessage(), 100),
                BuildProperties.getEmail(),
                createMailSubject(),
                createMailBody(defaultBody)));
    }
}
