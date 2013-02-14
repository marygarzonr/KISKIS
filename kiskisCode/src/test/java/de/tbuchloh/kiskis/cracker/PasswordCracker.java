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
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;

import de.tbuchloh.kiskis.util.KisKisRuntimeException;
import de.tbuchloh.util.logging.LogFactory;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 02.12.2010
 */
public class PasswordCracker {

    private static final Log LOG = LogFactory.getLogger();

    private IPasswordCreationStrategy _passwordCreator;

    private IPasswordTester _tester;

    private ICrackProgressListener _progressListener;

    /**
     * @param _passwordCreator
     *            {@link _passwordCreator}
     */
    public void setPasswordCreator(IPasswordCreationStrategy passwordCreator) {
        this._passwordCreator = passwordCreator;
    }

    /**
     * @param tester
     *            {@link tester}
     */
    public void setTester(IPasswordTester tester) {
        _tester = tester;
    }

    /**
     * @param progressListener
     *            {@link progressListener}
     */
    public void setProgressListener(ICrackProgressListener progressListener) {
        _progressListener = progressListener;
    }

    public static void main(String[] args) {
        final PasswordCracker main = new PasswordCracker();
        main.setPasswordCreator(new SimpleDictionaryPasswordCreationStrategy(new File(
        "tools/dictionaries/raw/allwords.txt")));
        main.setTester(new DefaultPasswordTester(new File("xml/example.xml.gpg")));
        main.setProgressListener(new DefaultProgressListener());
        System.out.println(main.crackPassword());
    }

    public String crackPassword() {
        final Long totalEstimation = _passwordCreator.estimateTotalCount();
        _progressListener.notifyTotalCount(totalEstimation);
        _progressListener.notifyStartTime(System.currentTimeMillis());


        final AtomicBoolean found = new AtomicBoolean(false);
        final Callable<String> callable = new Callable<String>() {

            @Override
            public String call() throws Exception {
                String guess;
                while (!found.get() && (guess = _passwordCreator.create()) != null) {
                    _progressListener.notifyTry(guess);
                    if (_tester.test(guess)) {
                        found.set(true);
                        return guess;
                    }
                }
                return null;
            }
        };

        final int cpus = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
        LOG.info(String.format("Found %1$d cpus ...", cpus));
        final ExecutorService threadPool = Executors.newFixedThreadPool(cpus);
        final Collection<Callable<String>> tasks = new ArrayList<Callable<String>>();
        for (int i = 0; i < cpus; ++i) {
            tasks.add(callable);
        }
        try {
            final List<Future<String>> futures = threadPool.invokeAll(tasks);
            for (final Future<String> future : futures) {
                final String guessedPwd = future.get();
                if (guessedPwd != null) {
                    return guessedPwd;
                }
            }
            return null;
        } catch (final InterruptedException e) {
            throw new KisKisRuntimeException("InterruptedException", e);
        } catch (final ExecutionException e) {
            throw new KisKisRuntimeException("ExecutionException", e);
        }
    }
}
