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
package de.tbuchloh.kiskis.cracker;

import java.io.File;
import java.io.IOException;

import de.tbuchloh.kiskis.util.KisKisRuntimeException;
import de.tbuchloh.util.crypto.CryptoException;
import de.tbuchloh.util.crypto.openpgp.PBEStreamProcessor;
import de.tbuchloh.util.io.FileProcessor;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 02.12.2010
 */
public class DefaultPasswordTester implements IPasswordTester {

    private final byte[] _fileContent;

    /**
     * Standardkonstruktor
     */
    public DefaultPasswordTester(File file) {
        try {
            _fileContent = FileProcessor.readFile(file).getBytes();
        } catch (final IOException e) {
            throw new KisKisRuntimeException("Could not read file!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(String pwd) {
        try {
            PBEStreamProcessor.decrypt(pwd.toCharArray(), _fileContent);
            return true;
        } catch (final CryptoException e) {
            return false;
        }
    }

}
