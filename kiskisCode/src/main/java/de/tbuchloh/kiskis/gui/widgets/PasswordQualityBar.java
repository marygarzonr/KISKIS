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
package de.tbuchloh.kiskis.gui.widgets;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JProgressBar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.kiskis.model.Password;
import de.tbuchloh.kiskis.model.validation.PasswordQualityValidator;
import de.tbuchloh.util.localization.Messages;

/**
 * 
 * @author Tobias Buchloh (gandalf)
 * @version $Id: $
 * @since 02.11.2010
 */
public class PasswordQualityBar extends JComponent {

    /**
     * Die serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLog(PasswordQualityBar.class);

    protected static final Messages M = new Messages(PasswordQualityBar.class);

    /**
     * Die {@link String}
     */
    private static final String DEF_MESSAGE = M.getString("MSG_NO_PWD");

    private final JProgressBar _progressBar;

    /**
     * Standardkonstruktor
     */
    public PasswordQualityBar() {
        super();
        this.setLayout(new BorderLayout());

        _progressBar = new JProgressBar();
        _progressBar.setString(DEF_MESSAGE);
        _progressBar.setBorderPainted(false);
        _progressBar.setStringPainted(true);
        _progressBar.setMaximum(128);

        add(_progressBar, BorderLayout.CENTER);
    }

    public void update(char[] pwd) {
        final double entropy = Password.checkEffectiveBitSize(pwd);
        if (pwd.length == 0) {
            _progressBar.setStringPainted(true);
            _progressBar.setString(DEF_MESSAGE);
            _progressBar.setToolTipText("");
            _progressBar.setValue(0);
            return;
        }
        
        PasswordQualityValidator pqv = new PasswordQualityValidator();
        
        String pwdStrength = pqv.validatePassword(pwd); 

        final int bit = (int) (Math.round(entropy));
        try {
            final BigDecimal cnt = new BigDecimal(2).pow(bit);
            _progressBar.setToolTipText(M.format("MSG_BIT_SIZE", bit, cnt));
        } catch (final ArithmeticException e) {
            LOG.error("exponent is " + bit, e);
        }
        _progressBar.setStringPainted(true);
        _progressBar.setString(MessageFormat.format(M.getString("MSG_ENTROPY"), pwdStrength));
        _progressBar.setValue(bit);
    }

    public void setSmallFont() {
        _progressBar.setFont(_progressBar.getFont().deriveFont(9f));
    }
}
