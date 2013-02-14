/*
 * Copyright (C) 2012 by Tobias Buchloh.
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
package de.tbuchloh.kiskis.util;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 19.02.2012
 */
public aspect EnforceSecureByteArrayAspect {

    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLog(EnforceSecureByteArrayAspect.class);
    
//    declare error : call(java.io.ByteArrayInputStream.new(..)) : "Use SecureByteArrayInputStream";
//    
//    declare error : call(java.io.ByteArrayOutputStream.new(..)) : "Use SecureByteArrayOutputStream";
    
    private AtomicLong secureByteArrayOutputStreamInstances = new AtomicLong();
    
    private AtomicLong secureByteArrayInputStreamInstances = new AtomicLong();
    
//    after() : call(de.tbuchloh.util.io.SecureByteArrayOutputStream.new(..)) {
//        long count = secureByteArrayOutputStreamInstances.incrementAndGet();
//        LOG.debug(String.format("secureByteArrayOutputStreamInstances=%1$s", count));
//    }
//    
//    after() : call(de.tbuchloh.util.io.SecureByteArrayInputStream.new(..)) {
//        long count = secureByteArrayInputStreamInstances.incrementAndGet();
//        LOG.debug(String.format("secureByteArrayInputStreamInstances=%1$s", count));
//    }
//    
//    after() : execution(protected void de.tbuchloh.util.io.SecureByteArrayOutputStream.cleanup() throws java.io.IOException) {
//        long count = secureByteArrayOutputStreamInstances.decrementAndGet();
//        LOG.debug(String.format("secureByteArrayOutputStreamInstances=%1$s", count));
//    }
//    
//    after() : call(protected void de.tbuchloh.util.io.SecureByteArrayInputStream.cleanup() throws java.io.IOException) {
//        long count = secureByteArrayInputStreamInstances.decrementAndGet();
//        LOG.debug(String.format("secureByteArrayInputStreamInstances=%1$s", count));
//    }
}
