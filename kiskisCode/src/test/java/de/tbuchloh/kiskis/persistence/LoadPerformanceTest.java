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

import java.io.File;

import junit.framework.TestCase;
import de.tbuchloh.kiskis.model.TPMDocument;
import de.tbuchloh.kiskis.persistence.PersistenceManagerTest.MyErrorHandler;
import de.tbuchloh.util.test.PerformanceTest;
import de.tbuchloh.util.test.PerformanceTestRunner;

/**
 * <b>LoadPerformanceTest</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public class LoadPerformanceTest extends TestCase {

    public void testLoad() throws Exception {

	final PerformanceTest test = new PerformanceTest("load doc") {
	    @Override
	    public void run() throws Exception {
		load();
	    }

	};
	final PerformanceTestRunner runner = new PerformanceTestRunner(1);
	runner.addTest(test);
	runner.start();
    }

    public void testSave() throws Exception {
	final File file = new File("target/loadtest");// File.createTempFile("test",
	// "gpg");
	final TPMDocument doc = load();
	final PerformanceTest test = new PerformanceTest("load doc") {
	    @Override
	    public void run() throws Exception {
		PersistenceManager.saveAs(doc,
			PGPEncryptionTest.createContext(file, true), false);
	    }

	};
	final PerformanceTestRunner runner = new PerformanceTestRunner(5);
	runner.addTest(test);
	runner.start();
    }

    public TPMDocument load() throws Exception {
	final MyErrorHandler err = new MyErrorHandler();
	return PersistenceManager.load(
		PGPEncryptionTest.createContext(PersistenceManagerTest.FILE),
		err);
    }

    public static void main(final String[] args) throws Exception {
	// new LoadPerformanceTest().testLoad();
	new LoadPerformanceTest().testSave();
    }
}
