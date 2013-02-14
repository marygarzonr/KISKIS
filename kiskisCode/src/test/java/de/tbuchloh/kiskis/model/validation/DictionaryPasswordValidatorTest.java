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
package de.tbuchloh.kiskis.model.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 01.11.2010
 */
public class DictionaryPasswordValidatorTest extends AbstractPasswordValidatorTest<DictionaryPasswordValidator> {

    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLog(DictionaryPasswordValidatorTest.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected DictionaryPasswordValidator createValidator() {
        return new DictionaryPasswordValidator();
    }

    /**
     * Test für Validate
     * 
     * @throws Exception
     *             wenn unvorhergesehene Fehler auftreten.
     */
    public void testValidate() throws Exception {
        assertNotValid("Attacke");
        LOG.info(getValidator().getMatch());

        assertNotValid("GeHeim");
        LOG.info(getValidator().getMatch());

        assertNotValid("PassworT");
        LOG.info(getValidator().getMatch());

        assertNotValid("Schatzi");
        LOG.info(getValidator().getMatch());

        assertNotValid("Mein Hotel");
        LOG.info(getValidator().getMatch());

        assertNotValid("Liebling");
        LOG.info(getValidator().getMatch());

        assertNotValid("New York");
        LOG.info(getValidator().getMatch());

        assertNotValid("Holiday");
        LOG.info(getValidator().getMatch());

        assertNotValid("Tobias77");
        LOG.info(getValidator().getMatch());
    }
}
