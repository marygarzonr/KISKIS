package de.tbuchloh.kiskis.testutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tbuchloh.util.event.MessageListener;

/**
 * @author Tobias Buchloh (gandalf)
 * @since 21.11.2010
 */
public final class TestMessageListener implements MessageListener {

    /**
     * Der Logger
     */
    private static final Log LOG = LogFactory.getLog(TestMessageListener.class);

    private final List<String> _messages = new ArrayList<String>();

    @Override
    public void showMessage(String message) {
        LOG.error(message);
        _messages.add(message);
    }

    /**
     * @return {@link #messages}
     */
    public List<String> getMessages() {
        return Collections.unmodifiableList(_messages);
    }
}