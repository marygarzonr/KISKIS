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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import de.tbuchloh.util.crypto.CryptoException;
import de.tbuchloh.util.crypto.SymmetricAlgo;
import de.tbuchloh.util.localization.Messages;

/**
 * <b>PasswordCryptoContext</b>: encrypts some streams to a file with a
 * password-based algorithm.
 * 
 * @author gandalf
 * @version $Id$
 */
public final class PasswordCryptoContext implements ICryptoContext {

    private static final MessageFormat ERR_MISSING_PWD;

    static {
        final Messages m = new Messages(PasswordCryptoContext.class);
        ERR_MISSING_PWD = m.getFormat("ERR_MISSING_PWD"); //$NON-NLS-1$
    }

    private final SymmetricAlgo _cryptoEngine;

    private final File _file;

    private final PasswordProxy _password;

    /**
     * creates a new Context
     * 
     * @param algo
     *            the encryption algorithm to use
     * @param pwd
     *            the password.
     * @param file
     *            is the file to use.
     */
    public PasswordCryptoContext(final SymmetricAlgo algo,
            final PasswordProxy pwd, final File file) {
        super();
        _cryptoEngine = algo;
        _password = pwd;
        _file = file;
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.persistence.ICryptoContext#decrypt(java.io.InputStream,
     *      java.io.OutputStream)
     */
    @Override
    public void decrypt(final InputStream in, final OutputStream out)
    throws CryptoException, IOException {
        final char[] pwd = _password.getPassword(_file);
        if (pwd == null || pwd.length == 0) {
            final Object[] p = { _file.getName() };
            throw new CryptoException(ERR_MISSING_PWD.format(p));
        }

        _cryptoEngine.decrypt(pwd, in, out);
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.persistence.ICryptoContext#encrypt(java.io.ByteArrayOutputStream,
     *      java.io.OutputStream)
     */
    @Override
    public void encrypt(final ByteArrayOutputStream in, final OutputStream out)
    throws IOException, CryptoException {
        final char[] pwd = _password.getPassword(_file, true);
        if (pwd != null && pwd.length > 0) {
            in.flush();
            final ByteArrayInputStream bis = new ByteArrayInputStream(
                    in.toByteArray());

            _cryptoEngine.encrypt(pwd, bis, out);

            bis.close();
            out.flush();
        } else {
            in.writeTo(out);
            out.flush();
        }
    }

    /**
     * Overridden!
     * 
     * @see de.tbuchloh.kiskis.persistence.ICryptoContext#getFile()
     */
    @Override
    public final File getFile() {
        return _file;
    }

    /**
     * Overridden!
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("[PasswordCryptoContext:");
        buffer.append(" _cryptoEngine: ");
        buffer.append(_cryptoEngine);
        buffer.append(" _password: ");
        buffer.append(_password);
        buffer.append(" _file: ");
        buffer.append(_file);
        buffer.append("]");
        return buffer.toString();
    }

}