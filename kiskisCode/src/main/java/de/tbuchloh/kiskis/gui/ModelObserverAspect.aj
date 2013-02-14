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

package de.tbuchloh.kiskis.gui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.model.annotations.IgnoresObservable;
import de.tbuchloh.kiskis.model.annotations.Observable;

/**
 * <b>ModelObserverAspect</b>:
 * 
 * @author gandalf
 * @version $Id$
 */
public aspect ModelObserverAspect {

    private static final Log LOG = LogFactory.getLog(ModelObserverAspect.class);

    pointcut observedMethods() : 
        call(public void @Observable *.set*(..))
        || call(public * @Observable *.add*(..))
        || call(public * @Observable *.remove*(..))
        || call(public * @Observable *.update*(..));

    pointcut ignoredMethods() : 
	!withincode(@IgnoresObservable * *(..));

    pointcut update(Observable param) : 
        @target(param)
        && observedMethods()
        && within(de.tbuchloh.kiskis.gui.* || de.tbuchloh.kiskis.gui.dialogs.*  || de.tbuchloh.kiskis.persistence.importer.*)
        && ignoredMethods();

    pointcut restrictedMethods() : 
        call(void MainFrame.contentChanged(boolean)) 
        && !within(ModelObserverAspect);

    declare error : restrictedMethods() : "Use ModelObserverAspect";

    after(final Observable obs) : update(obs) {
	if (LOG.isDebugEnabled()) {
	    LOG.debug("Model-object obs=" + obs + " changed");
	}
	MainFrame.getInstance().contentChanged(true);
    }
}
