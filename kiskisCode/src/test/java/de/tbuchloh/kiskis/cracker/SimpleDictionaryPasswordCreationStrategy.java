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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.tbuchloh.kiskis.util.KisKisRuntimeException;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 02.12.2010
 */
public class SimpleDictionaryPasswordCreationStrategy implements IPasswordCreationStrategy {

    private final BufferedReader _br;

    private final File _dictionary;

    /**
     * Standardkonstruktor
     */
    public SimpleDictionaryPasswordCreationStrategy(File dictionary) {
        _dictionary = dictionary;
        try {
            _br = new BufferedReader(new FileReader(dictionary));
        } catch (final FileNotFoundException e) {
            throw new KisKisRuntimeException("File not found!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String create() {
        try {
            return _br.readLine();
        } catch (final IOException e) {
            throw new KisKisRuntimeException("Could not read file!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long estimateTotalCount() {
        long r = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(_dictionary));
            while (br.readLine() != null) {
                r += 1;
            }
            return r;
        } catch (final IOException e) {
            throw new KisKisRuntimeException("Could not estimate number!", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

}
