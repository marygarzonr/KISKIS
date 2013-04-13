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

import java.math.BigDecimal;

import org.apache.commons.logging.Log;

import de.tbuchloh.kiskis.model.Password;
import de.tbuchloh.util.logging.LogFactory;

/**
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 01.11.2010
 */
public class WeakPasswordValidator extends AbstractPasswordValidator {

    /**
     * The minimum bit size means 2^40 possible variations.
     */
    private static final int MIN_BIT_SIZE = 40;

    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLogger();

    private Double _bitSize;

    /**
     * {@inheritDoc}
     */
    @Override
    public String validatePassword(char[] pwd) {
        _bitSize = Password.checkEffectiveBitSize(pwd);

        LOG.debug("Found bit size " + _bitSize);
        
        if(_bitSize < MIN_BIT_SIZE) {
            return  M.format("weak_pwd_warning", getVariationCnt());
        }
        
        return null;

//        return _bitSize >= MIN_BIT_SIZE;
    }

    public BigDecimal getVariationCnt() {
        final BigDecimal two = new BigDecimal(2);
        return two.pow(_bitSize.intValue());
    }
}
