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
package de.tbuchloh.kiskis.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 29.11.2010
 */
public final class VersionTools {

    /**
     * @param previousVersion
     *            the previous version
     * @param newVersion
     *            the new version
     * @return true if both versions are compatible
     */
    public static boolean isCompatible(final String previousVersion, final String newVersion) {
        final Pattern versionPattern = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)(\\-.+)?");
        final Matcher prevVersionMatcher = versionPattern.matcher(previousVersion);
        if (!prevVersionMatcher.matches()) {
            throw new KisKisRuntimeException(String.format("Previous version %1$s has an invalid format!",
                    previousVersion));
        }

        final Matcher newVersionMatcher = versionPattern.matcher(newVersion);
        if (!newVersionMatcher.matches()) {
            throw new KisKisRuntimeException(String.format("New version %1$s has an invalid format!", newVersion));
        }

        final int[] prev = getVersionArray(prevVersionMatcher);
        final int[] curr = getVersionArray(newVersionMatcher);
        return prev[0] == curr[0] && prev[1] == curr[1] && prev[2] <= curr[2];
    }

    private static int[] getVersionArray(final Matcher matcher) {
        final int[] curr = new int[] {
                Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                Integer.parseInt(matcher.group(3))
        };
        return curr;
    }

    /**
     * Private Constructor
     */
    private VersionTools() {
        // empty
    }

}
