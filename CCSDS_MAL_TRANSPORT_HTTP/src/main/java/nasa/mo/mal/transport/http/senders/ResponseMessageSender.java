/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.http.senders;

import com.sun.net.httpserver.HttpExchange;
import nasa.mo.mal.encoder.Header.HttpHeaderKeys;
import esa.mo.mal.transport.gen.GENMessage;
import nasa.mo.mal.transport.http.HttpTransport;
import nasa.mo.mal.transport.http.util.Constants;
import nasa.mo.mal.transport.http.util.HttpTransportHelper;
import nasa.mo.mal.transport.http.util.MALTransmitErrorBuilder;
import nasa.mo.mal.transport.http.util.ResponseCodes;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.UInteger;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;

/**
 * @author wphyo
 *         Created on 6/27/17.
 * Thread with a result responsible for sending back response
 */
public class ResponseMessageSender implements Callable<Boolean> {

    private HttpTransport transport;
    private GENMessage message;
    private byte[] encoded;
    private Map<String, String> headers;

    /**
     *
     * @param transport Http Transport which has everything.
     * @param message new MAL Message which will be the response message
     * @param encoded Encoded version of new MAL Message
     * @param headers Headers to go to Http Headers
     */
    public ResponseMessageSender(HttpTransport transport, GENMessage message,
                                 byte[] encoded, Map<String, String> headers) {
        this.transport = transport;
        this.message = message;
        this.encoded = encoded;
        this.headers = headers;
    }

    /**
     * Finding correct Http Exchange object and attach bodies to it
     *
     * Steps:
     * 1.   find Http Exchange stored in Http Transport
     * 1.1. if cannot find, throw error.
     * 2.   check if the message is error.
     * 2.1. if error, TODO get server error code as response code, & return body
     * 3.   if it is not error, get correct response code, and return body
     * @return boolean if the method succeeds. For now, it will either succeed or throw exception
     * @throws Exception any Exception
     */
    @Override
    public Boolean call() throws Exception {
        if (message == null || message.getHeader() ==null || message.getBody() == null) {
            throw MALTransmitErrorBuilder.create().setExtraInfo("Null MAL Message.").build();
        }
        HttpExchange exchange = transport.getResponseMessage(message.getHeader());
        if (exchange == null) {
            throw MALTransmitErrorBuilder.create().setHeader(message.getHeader())
                    .setExtraInfo("Empty Http Exchange to reply").build();
        }
        int responseCode;
        if (headers.containsKey(HttpHeaderKeys.HOST.toString())) {
            headers.put(HttpHeaderKeys.HOST.toString(), headers.get(HttpHeaderKeys.HOST.toString()).replaceAll(
                    "^malhttp", transport.isSecureServer() ? Constants.SECURE_HTTP : Constants.PLAIN_HTTP));
        }
        if (message.getHeader().getIsErrorMessage()) {
            HttpTransport.LOGGER.log(Level.FINE, "MAL response is an error message. getting http error code.");
            try {
                responseCode = HttpTransportHelper.getStatusCodeFromMALError((UInteger) message.getBody()
                        .getBodyElement(0, new UInteger())).getCode();
            } catch (MALException exp) {
                HttpTransport.LOGGER.log(Level.WARNING, "Error getting error code from MAL msg.", exp);
                responseCode = ResponseCodes.INTERNAL_SERVER_ERROR.getCode();
            }

        } else {
            HttpTransport.LOGGER.log(Level.FINE, "MAL response is NOT error message. getting http response code.");
            responseCode = HttpTransportHelper.getHttpResponseCode(message.getHeader().getInteractionType(),
                    message.getHeader().getInteractionStage());
        }

        try {
            HttpTransport.LOGGER.log(Level.FINE, "sending a response message for message:\n" + new String(encoded));
            HttpTransportHelper.fillResponse(exchange, responseCode,
                    encoded, headers);
            return true;
        } catch (MALException exp) {
            throw MALTransmitErrorBuilder.create().setHeader(message.getHeader()).setExtraInfo(exp).build();
        }
    }
}
