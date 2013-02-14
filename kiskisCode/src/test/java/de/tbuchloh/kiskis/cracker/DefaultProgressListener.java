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

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;

import de.tbuchloh.util.logging.LogFactory;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 02.12.2010
 */
public class DefaultProgressListener implements ICrackProgressListener {

    private static final Log LOG = LogFactory.getLogger();

    private final AtomicLong _counter = new AtomicLong();

    private final AtomicLong _total = new AtomicLong();

    private long _startTime;

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyTry(String pwd) {
        final long cnt = _counter.incrementAndGet();
        final long total = _total.get();
        if (cnt % 10000 == 0) {
            final double percent = ((double) cnt) / total * 100;
            final long perSecond = cnt / ((System.currentTimeMillis() - _startTime) / 1000);
            LOG.info(String.format("[%2$f] %1$d tries, %3$d per second, last guess %4$s ...", cnt, percent, perSecond,
                    pwd));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyTotalCount(Long totalEstimation) {
        if (totalEstimation != null) {
            final long total = _total.addAndGet(totalEstimation);
            LOG.info(String.format("Found %1$d total", total));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyStartTime(long timemillis) {
        _startTime = timemillis;
    }

}
