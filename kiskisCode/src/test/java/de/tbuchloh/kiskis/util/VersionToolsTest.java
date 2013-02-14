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

import static de.tbuchloh.kiskis.util.VersionTools.isCompatible;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 29.11.2010
 */
public class VersionToolsTest {

    @Test
    public void isCompatibleOK() {
        assertThat(isCompatible("1.0.0", "1.0.0"), is(true));
        assertThat(isCompatible("1.0.0-RC1", "1.0.0"), is(true));
        assertThat(isCompatible("1.0.0", "1.0.0-RC1"), is(true));
        assertThat(isCompatible("1.0.0", "1.0.1"), is(true));
        assertThat(isCompatible("1.0.0", "1.0.1-RC1"), is(true));

        assertThat(isCompatible("1.0.1-RC99", "1.0.0"), is(false));
        assertThat(isCompatible("1.0.1", "1.0.0"), is(false));

        assertThat(isCompatible("1.0.0", "1.1.0"), is(false));
        assertThat(isCompatible("1.1.0", "1.0.0"), is(false));
        assertThat(isCompatible("2.0.0", "1.99999.99999-RC9999999"), is(false));
    }

    @Test(expected = KisKisRuntimeException.class)
    public void isCompatibleError1() {
        isCompatible("1", "1.0.0");
    }

    @Test(expected = KisKisRuntimeException.class)
    public void isCompatibleError2() {
        isCompatible("1.0", "1.0.0");
    }

    @Test(expected = KisKisRuntimeException.class)
    public void isCompatibleError3() {
        isCompatible("1.A", "1.0.0");
    }

    @Test(expected = KisKisRuntimeException.class)
    public void isCompatibleError4() {
        isCompatible("1.0-RC", "1.0.0");
    }
}
