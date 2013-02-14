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

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.util.KisKisRuntimeException;
import de.tbuchloh.util.crypto.CryptoException;
import de.tbuchloh.util.crypto.OpenPGPwith3DES;
import de.tbuchloh.util.crypto.SymmetricAlgo;
import de.tbuchloh.util.crypto.TripleDES;

/**
 * <b>SecretInfoEncryptionAspect</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public aspect SecretInfoEncryptionAspect {

    private static final Log LOG = LogFactory.getLog(SecretInfoEncryptionAspect.class);
    
    private final char[] sessionKey = RandomStringUtils.randomAscii(64).toCharArray();

    private final SymmetricAlgo algo = new OpenPGPwith3DES();
    
    void around(String value) : if(false) && args(value) && set(@de.tbuchloh.kiskis.model.annotations.SecretInfo String de.tbuchloh.kiskis.model..*.*) {
        if (value == null) {
            proceed(null);
            return;
        }
        
        try {
            byte[] encrypted = algo.encrypt(sessionKey, value.getBytes());
            proceed(new String(encrypted));
        } catch (CryptoException e) {
            throw new KisKisRuntimeException("In memory encryption failed!", e);
        }
    }
    
    String around() : if(false) && get(@de.tbuchloh.kiskis.model.annotations.SecretInfo String de.tbuchloh.kiskis.model..*.*) {
        String encryptedString = proceed();
        if (encryptedString == null) {
            return null;
        }
        
        try {

            byte[] decrypted = algo.decrypt(sessionKey, encryptedString.getBytes());
            return new String(decrypted);
        } catch (CryptoException e) {
            throw new KisKisRuntimeException("In memory encryption failed!", e);
        }
    }
    
    void around(char[] value) : if(false) && args(value) && set(@de.tbuchloh.kiskis.model.annotations.SecretInfo char[] de.tbuchloh.kiskis.model..*.*) {
        if (value == null) {
            proceed(null);
            return;
        }
        
        try {
            byte[] encrypted = algo.encrypt(sessionKey, new String(value).getBytes());
            proceed(new String(encrypted).toCharArray());
        } catch (CryptoException e) {
            throw new KisKisRuntimeException("In memory encryption failed!", e);
        }
    }
    
    char[] around() : if(false) && get(@de.tbuchloh.kiskis.model.annotations.SecretInfo char[] de.tbuchloh.kiskis.model..*.*) {
        char[] encryptedString = proceed();
        if (encryptedString == null) {
            return null;
        }
        
        try {
            byte[] decrypted = algo.decrypt(sessionKey, new String(encryptedString).getBytes());
            return new String(decrypted).toCharArray();
        } catch (CryptoException e) {
            throw new KisKisRuntimeException("In memory encryption failed!", e);
        }
    }
}
