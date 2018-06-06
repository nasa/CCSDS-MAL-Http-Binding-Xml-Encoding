/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.http.receivers;

import com.sun.net.httpserver.HttpExchange;
import esa.mo.mal.transport.gen.GENMessage;

/**
 * @author wphyo
 *         Created on 6/16/17.
 *
 * Container class holding the message ready to decode and a response object to return.
 */
public class HttpIncomingMessageHolder {
    private HttpExchange httpExchange;
    private GENMessage incomingMessage;
    private boolean internalSend;

    public HttpIncomingMessageHolder(HttpExchange httpExchange, GENMessage incomingMessage, boolean internalSend) {
        this.httpExchange = httpExchange;
        this.incomingMessage = incomingMessage;
        this.internalSend = internalSend;
    }

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

    public GENMessage getIncomingMessage() {
        return incomingMessage;
    }

    public boolean isInternalSend() {
        return internalSend;
    }
}
