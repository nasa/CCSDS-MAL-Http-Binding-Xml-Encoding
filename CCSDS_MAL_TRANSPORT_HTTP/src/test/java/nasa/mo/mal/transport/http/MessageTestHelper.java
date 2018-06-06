/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.http;

import com.sun.net.httpserver.Headers;
import nasa.mo.mal.encoder.Header.HttpHeaderKeys;
import esa.mo.mal.transport.gen.GENMessageHeader;
import org.mockito.ArgumentMatcher;

import java.net.ServerSocket;

import static org.mockito.ArgumentMatchers.argThat;

/**
 * @author wphyo
 *         Created on 6/15/17.
 */
public class MessageTestHelper {
    public static void fillResponseHeader(Headers headers) {
        try {
            headers.set(HttpHeaderKeys.REQUEST_TARGET.toString(), "/client09");
            headers.set(HttpHeaderKeys.URI_FROM.toString(), "http://localhost:8080/responseMessageTest02");
            headers.set(HttpHeaderKeys.AUTH_ID.toString(), "010203");
            headers.set(HttpHeaderKeys.TIMESTAMP.toString(), "1970-001T00:00:00.000");
            headers.set(HttpHeaderKeys.QOS_LEVEL.toString(), "ASSURED");
            headers.set(HttpHeaderKeys.PRIORITY.toString(), "1");
            headers.set(HttpHeaderKeys.DOMAIN.toString(), "");
            headers.set(HttpHeaderKeys.NETWORK_ZONE.toString(), "1");
            headers.set(HttpHeaderKeys.SESSION.toString(), "LIVE");
            headers.set(HttpHeaderKeys.SESSION_NAME.toString(), "1");
            headers.set(HttpHeaderKeys.INTERACTION_TYPE.toString(), "SEND");
            headers.set(HttpHeaderKeys.INTERACTION_STAGE.toString(), "1");
            headers.set(HttpHeaderKeys.TRANSACTION_ID.toString(), "1");
            headers.set(HttpHeaderKeys.SERVICE_AREA.toString(), "1");
            headers.set(HttpHeaderKeys.SERVICE.toString(), "1");
            headers.set(HttpHeaderKeys.OPERATION.toString(), "1");
            headers.set(HttpHeaderKeys.AREA_VERSION.toString(), "1");
            headers.set(HttpHeaderKeys.IS_ERROR_MSG.toString(), "false");
            headers.set(HttpHeaderKeys.VERSION.toString(), "1");
            headers.set(HttpHeaderKeys.CONTENT_TYPE.toString(), "application/mal-xml");
            headers.set(HttpHeaderKeys.HOST.toString(), "http://localhost:8080");
        } catch (Exception exp) {
            System.out.println("fillResponseHeader : " + exp);
        }
    }

    public static int getAvailablePort() {
        int freeport = -1;
        try {
            ServerSocket socket = new ServerSocket(0);
            freeport = socket.getLocalPort();
            socket.close();
        } catch (Exception exp) {

        }
        return freeport;
    }

    /**
     * Helper method to compare GENMessageHeader
     *
     * Need to use this instead of builtin eq() SINCE GENMessageHeader has no 'equals' method.
     * @param expected correct GENMessageHeader
     * @return flag if they are equal
     */
    public static GENMessageHeader genMessageHeaderEq(GENMessageHeader expected) {
        return argThat(new GENMessageHeaderMatcher(expected));
    }

    /**
     * Custom Matcher for GENMessageHeader
     */
    static class GENMessageHeaderMatcher implements ArgumentMatcher<GENMessageHeader> {
        private GENMessageHeader header;

        public GENMessageHeaderMatcher(GENMessageHeader header) {
            this.header = header;
        }

        /**
         * Matching individual fields of GENMessageHeader
         *
         * @param header real GENMessageHeader
         * @return flag if they all match
         */
        @Override
        public boolean matches(GENMessageHeader header) {
            return this.header.getURIFrom().equals(header.getURIFrom()) &&
                    this.header.getAuthenticationId().equals(header.getAuthenticationId()) &&
                    this.header.getURITo().equals(header.getURITo()) &&
                    this.header.getTimestamp().equals(header.getTimestamp()) &&
                    this.header.getQoSlevel().equals(header.getQoSlevel()) &&
                    this.header.getPriority().equals(header.getPriority()) &&
                    this.header.getDomain().equals(header.getDomain()) &&
                    this.header.getNetworkZone().equals(header.getNetworkZone()) &&
                    this.header.getSession().equals(header.getSession()) &&
                    this.header.getSessionName().equals(header.getSessionName()) &&
                    this.header.getInteractionType().equals(header.getInteractionType()) &&
                    ((this.header.getInteractionStage() == null && header.getInteractionStage() == null) ||
                            this.header.getInteractionStage().equals(header.getInteractionStage())) &&
                    this.header.getTransactionId().equals(header.getTransactionId()) &&
                    this.header.getServiceArea().equals(header.getServiceArea()) &&
                    this.header.getService().equals(header.getService()) &&
                    this.header.getOperation().equals(header.getOperation()) &&
                    this.header.getAreaVersion().equals(header.getAreaVersion()) &&
                    this.header.getIsErrorMessage().equals(header.getIsErrorMessage());
        }
    }
}
