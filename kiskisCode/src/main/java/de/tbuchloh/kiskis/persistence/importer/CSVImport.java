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

package de.tbuchloh.kiskis.persistence.importer;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.model.Group;
import de.tbuchloh.kiskis.model.ModelConstants;
import de.tbuchloh.kiskis.model.ModelNode;
import de.tbuchloh.kiskis.model.NetAccount;
import de.tbuchloh.kiskis.model.Password;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.util.DateUtils;
import de.tbuchloh.kiskis.util.Settings;
import de.tbuchloh.util.event.MessageListener;
import de.tbuchloh.util.localization.Messages;
import de.tbuchloh.util.text.StringTools;
import de.tbuchloh.util.text.csv.CSVFile;
import de.tbuchloh.util.text.csv.CSVFile.CSVEntry;

/**
 * <b>CSVImport</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public final class CSVImport {

    public static final String GROUP_SEP = "##";

    public static final String COMMENT = "Comment";

    public static final String CREATED = "Created On";

    public static final String EMAIL = "Email";

    private static final MessageFormat ERR_PARSE_DATE;

    private static final MessageFormat MSG_IMPORTED_ON;

    public static final String EXPIRATION = "Expires On";

    public static final String GROUP = "Group";

    private static final Log LOG = LogFactory.getLog(CSVImport.class);

    public static final String LABEL = "Label";

    public static final String PWD = "Password";

    public static final String URL = "URL";

    public static final String USERNAME = "User Name";

    static {
        final Messages msg = new Messages(CSVImport.class);
        ERR_PARSE_DATE = msg.getFormat("ERR_PARSE_DATE");
        MSG_IMPORTED_ON = msg.getFormat("MSG_IMPORTED_ON");
    }

    private final TPMDocument _doc;

    private final MessageListener _msgListener;

    /**
     * creates a new CSVImport
     */
    public CSVImport(final MessageListener msg, final TPMDocument doc) {
        super();
        _msgListener = msg;
        _doc = doc;
    }

    private ModelNode createAccount(final CSVEntry entry) {
        final NetAccount na = new NetAccount();
        String name = entry.get(LABEL);
        if (StringTools.isBlank(name)) {
            name = "<unknown>";
        }
        na.setName(name);

        String password = entry.get(PWD);
        if (StringTools.isBlank(password)) {
            password = "";
        }

        final Calendar creaDate = DateUtils.getCurrentDateTime();
        final String created = entry.get(CREATED);
        if (!StringTools.isBlank(created)) {
            try {
                creaDate.setTime(ModelConstants.SHORT.parse(created));
            } catch (final ParseException e) {
                _msgListener.showMessage(ERR_PARSE_DATE.format(new Object[] {
                        created
                }));
            }
        }

        final String expires = entry.get(EXPIRATION);
        final Calendar expDate = DateUtils.getCurrentDateTime();
        if (!StringTools.isBlank(expires)) {
            try {
                expDate.setTime(ModelConstants.SHORT.parse(expires));
            } catch (final ParseException e) {
                expDate.add(Calendar.DAY_OF_YEAR, Settings.getDefaultPwdExpiryDays());
                na.setExpiresNever(true);
                _msgListener.showMessage(ERR_PARSE_DATE.format(new Object[] {
                        expires
                }));
            }
        } else {
            expDate.add(Calendar.DAY_OF_YEAR, Settings.getDefaultPwdExpiryDays());
            na.setExpiresNever(true);
        }
        na.setCreationDate(creaDate);
        final Password pwd = new Password(password.toCharArray(), expDate, creaDate);
        na.setPwd(pwd);

        final String userName = entry.get(USERNAME);
        na.setUsername(userName == null ? "" : userName);

        final String email = entry.get(EMAIL);
        na.setEmail(email == null ? "" : email);

        final String url = entry.get(URL);
        na.setUrl(url == null ? "" : url);

        final String commentField = entry.get(COMMENT);
        final StringBuilder comment = new StringBuilder(commentField == null ? "" : commentField);
        if (comment.length() > 0) {
            comment.append('\n');
        }
        comment.append(MSG_IMPORTED_ON.format(new Object[] {
                DateUtils.getCurrentDateTime().getTime()
        }));
        na.setComment(comment.toString());

        LOG.debug("Created account: " + na);
        return na;
    }

    public void doImport(final File file, final char fieldSep) throws IOException {
        final CSVFile csv = CSVFile.parse(_msgListener, file, fieldSep);
        for (final Iterator i = csv.getEntries().iterator(); i.hasNext();) {
            final CSVEntry entry = (CSVEntry) i.next();
            insert(entry);
        }
    }

    private Group getGroup(Group parent, final String groupPath) {
        Group ret = null;
        final String[] path = groupPath.split(GROUP_SEP, 2);
        final String groupName = path[0];

        if (StringTools.isBlank(groupName)) {
            // A blank group name is an error. So we use the parent group.
            ret = parent;
        } else {
            // lookup the group by name
            for (final Group g : parent.getGroups()) {
                if (g.getName().equals(groupName)) {
                    ret = g;
                    break;
                }
            }

            if (ret == null) {
                // we did not find the group, so we need to create a new one
                final Group newGroup = new Group(groupName);
                parent.add(newGroup);
                ret = newGroup;
            }
        }

        // we have some more path elements so we need a recursion
        if (path.length > 1) {
            ret = getGroup(ret, path[1]);
        }

        assert ret != null;
        LOG.debug("Got group: " + ret);

        return ret;
    }

    private void insert(final CSVEntry entry) {
        final Group group = getGroup(_doc.getGroups(), entry.get(GROUP));
        group.add(createAccount(entry));
    }

}
