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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import de.tbuchloh.util.crypto.OpenPGPwithAES;
import de.tbuchloh.util.crypto.OpenPGPwithAES256;
import de.tbuchloh.util.crypto.OpenPGPwithBlowfish;
import de.tbuchloh.util.crypto.OpenPGPwithCAST5;
import de.tbuchloh.util.crypto.OpenPGPwithTwofish;
import de.tbuchloh.util.crypto.SymmetricAlgo;
import de.tbuchloh.util.crypto.TripleDES;

/**
 * @author gandalf
 */
public final class SupportedAlgorithmsUtil {
    /**
     * @return all supported algorithms, key=name, value=algorithm
     */
    public static Map<String, SymmetricAlgo> getSupportedAlgorithms() {
	final Map<String, SymmetricAlgo> algos = new LinkedHashMap<String, SymmetricAlgo>();
	algos.put("OpenPGP - AES (256)", new OpenPGPwithAES256());
	algos.put("OpenPGP - AES (128)", new OpenPGPwithAES());
	algos.put("OpenPGP - Blowfish", new OpenPGPwithBlowfish());
	algos.put("OpenPGP - CAST5", new OpenPGPwithCAST5());
	algos.put("OpenPGP - Twofish", new OpenPGPwithTwofish());
	algos.put("3DES", new TripleDES());
	return Collections.unmodifiableMap(algos);
    }
}
