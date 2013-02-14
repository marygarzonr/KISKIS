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
package de.tbuchloh.kiskis.model;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import de.tbuchloh.kiskis.model.template.AccountProperty;
import de.tbuchloh.kiskis.model.template.AccountPropertyTypes;
import de.tbuchloh.kiskis.model.template.AccountType;
import de.tbuchloh.kiskis.model.template.SimpleProperty;
import de.tbuchloh.util.localization.Messages;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 23.10.2010
 */
public final class StandardDocumentFactory {

    private static final Messages M = new Messages(
	    StandardDocumentFactory.class);

    /**
     * @param nameKey
     *            is the message key
     * @param propertyType
     *            is the property type
     * @param propertyClass
     *            is the property class
     * @return the created {@link AccountProperty}
     */
    public static AccountProperty createAccountProperty(String nameKey,
	    String propertyType, Class<?> propertyClass) {
	final AccountProperty stringField = new SimpleProperty(propertyType,
		propertyClass);
	stringField.setName(M.getString(nameKey));
	return stringField;
    }

    /**
     * @return an example with all fields available
     */
    private static AccountType createExampleType() {
	final AccountType type = new AccountType();
	type.setName(M.getString("complexAccountType.name"));
	type.addProperty(createAccountProperty(
		"complexAccountType.stringFieldName",
		AccountPropertyTypes.T_STRING, String.class));
	type.addProperty(createAccountProperty(
		"complexAccountType.pwdFieldName", AccountPropertyTypes.T_PWD,
		String.class));
	type.addProperty(createAccountProperty(
		"complexAccountType.dateFieldName",
		AccountPropertyTypes.T_DATE, Calendar.class));
	type.addProperty(createAccountProperty(
		"complexAccountType.urlFieldName", AccountPropertyTypes.T_URL,
		String.class));
	type.addProperty(createAccountProperty(
		"complexAccountType.textFieldName",
		AccountPropertyTypes.T_TEXT, String.class));
	return type;
    }

    /**
     * @return a very simple type without any new fields
     */
    private static AccountType createSimpleType() {
	final AccountType type = new AccountType();
	type.setName(M.getString("simpleAccountType.name"));
	return type;
    }

    /**
     * @param file
     *            the file to use. Should not exist.
     * @return the newly created transient document with its standard structure.
     */
    public static TPMDocument createStandardDocument(final File file) {
	final TPMDocument doc = new TPMDocument(file);
	final Group root = doc.getGroups();
	root.setName(M.getString("rootNodeName"));
	final String standardGroupNames = M.getString("standardGroupNames");
	final String[] groupNamesWithComment = standardGroupNames.trim().split(
	";");
	Arrays.sort(groupNamesWithComment);
	for (final String name : groupNamesWithComment) {
	    final Group g = new Group();
	    final String[] entry = name.split("\\$\\$");
	    if (entry.length == 1) {
		g.setName(name);
	    } else {
		g.setName(entry[0]);
		g.setComment(entry[1]);
	    }
	    root.add(g);
	}

	final Set<AccountType> accountTypes = new HashSet<AccountType>();
	accountTypes.add(createSimpleType());
	accountTypes.add(createExampleType());
	doc.setAccountTypes(accountTypes);

	return doc;
    }

    /**
     * Private Constructor
     */
    private StandardDocumentFactory() {
	// empty
    }
}
