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

package de.tbuchloh.kiskis.persistence;

import java.text.DateFormat;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.model.ModelConstants;
import de.tbuchloh.kiskis.util.BuildProperties;

/**
 * <b>XMLProcessing</b>: provides common variables for XML-input and output.
 * 
 * @author gandalf
 * @version $Id$
 */
public abstract class XMLProcessing {

    private static final class DefaultErrorHandler implements IErrorHandler {

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.persistence.IErrorHandler#error(java.lang.String)
	 */
	@Override
	public void error(final String message) {
	    // does nothing
	}

	/**
	 * Overridden!
	 * 
	 * @see de.tbuchloh.kiskis.persistence.IErrorHandler#warning(java.lang.String)
	 */
	@Override
	public void warning(final String message) {
	    // does nothing
	}

    }

    protected static final String ACC_PROP_TAG = "AccountProperty";

    protected static final String ACC_PROP_VAL_TAG = "AccountPropertyValue";

    protected static final String ACC_TYPE_TAG = "AccountType";

    protected static final String ATTACHMENTS_TAG = "Attachments";

    protected static final String ATTACHMENT_TAG = "Attachment";

    protected static final String BA_TAG = "BankAccount";

    protected static final String CC_TAG = "CreditCard";

    protected static final String COMMENT_TAG = "Comment";

    protected static final String ATTACHMENT_REF_TAG = "AttachmentRef";

    public static final DateFormat DATE_LONG = ModelConstants.XML_LONG;

    public static final DateFormat DATE_SHORT = ModelConstants.SHORT;

    protected static final String DOC_TAG = "TPMDocument";

    // protected static final String DOCTYPE_ID = "kiskis-0.24.1.dtd";

    protected static final String NS_MODEL = "http://kiskis.sourceforge.net/model/v1.0/";

    protected static final String GENERIC_TAG = "GenericAccount";

    protected static final String GROUP_TAG = "Group";

    protected static final Log LOG = LogFactory.getLog(XMLProcessing.class);

    protected static final String NA_TAG = "NetAccount";

    protected static final String PH_TAG = "PasswordHistory";

    protected static final String PWD_TAG = "Password";

    protected static final String SE_TAG = "SecuredElement";

    protected static final String SF_TAG = "SecuredFile";

    protected static final String TAN_TAG = "TAN";

    protected static final String TANLIST_TAG = "TANList";

    protected static final String UUID_ATTR = "uuid";

    protected static final String ARCHIVE_DATE_ATTR = "archivedOnDate";

    protected static final String ATTACHMENT_UUID_ATTR = "attachmentUuid";

    protected static final String VERSION = BuildProperties.getVersion();

    protected static final String CURRENT_XSD = "kiskis-1.0.xsd";

    public static String createShortDate(final Calendar cal) {
	return DATE_SHORT.format(cal.getTime());
    }

    private IErrorHandler _errorHandler;

    /**
     * creates a new XMLProcessing
     */
    protected XMLProcessing() {
	super();
	_errorHandler = new DefaultErrorHandler();
    }

    protected void logError(final String msg) {
	LOG.error(msg);
	_errorHandler.error(msg);
    }

    protected void logWarning(final String msg) {
	LOG.warn(msg);
	_errorHandler.warning(msg);
    }

    /**
     * @param errorHandler
     *            The errorHandler to set.
     */
    public final void setErrorHandler(final IErrorHandler errorHandler) {
	_errorHandler = errorHandler;
    }
}
