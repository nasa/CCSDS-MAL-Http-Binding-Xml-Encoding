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
import com.sun.net.httpserver.HttpHandler;
import nasa.mo.mal.encoder.Header.HttpHeaderKeys;
import nasa.mo.mal.encoder.Header.HttpToMalMapper;
import esa.mo.mal.transport.gen.GENMessage;
import esa.mo.mal.transport.gen.GENMessageHeader;
import nasa.mo.mal.transport.http.HttpTransport;
import nasa.mo.mal.transport.http.util.Constants;
import nasa.mo.mal.transport.http.util.HttpTransportHelper;
import nasa.mo.mal.transport.http.util.ResponseCodes;
import org.apache.commons.io.IOUtils;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author wphyo
 *         Created on 6/16/17.
 * Simple handler which will create a Message ready to decode, and store it to the container.
 */
public class MainHandler implements HttpHandler {
    private HttpTransport transport;

    public MainHandler(HttpTransport transport) {
        this.transport = transport;
    }

    /**
     * Handling incoming requests.
     *
     * Validations:
     * 1.   if it is not post, return forbidden error.
     *          set response code as Forbidden (status-code of status-line)
     *          Http-Version of status-line is not required to be set as it is using the request's Http-Version
     *          reason-phrase of status-line is not mandatory (ignored at this moment).
     * 2.   TODO throw MAL Error
     * 3.   TODO other validations?
     *
     * Steps:
     * 1.   get Header Map & Xml Body from Http Object.
     * 2.   create Serializable Encoded Message from those.
     * 3.   create a message processing thread and pass to transport.
     *
     * @param httpExchange Http Exchange object ~ a request object from client
     * @throws IOException any error
     */
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (!httpExchange.getRequestMethod().equals(Constants.VALID_REQUEST_METHOD)) {
            returnErrorResponse(httpExchange, ResponseCodes.METHOD_NOT_ALLOWED);
            HttpTransport.LOGGER.log(Level.WARNING, "Invalid Request Method");
            // TODO store invalid message? or error message?
            return;
        }
        byte[] encodedMessage = IOUtils.toByteArray(httpExchange.getRequestBody());
        Map<String, String> allHeaders = new HashMap<>();
        httpExchange.getRequestHeaders().keySet()
                .forEach(k -> allHeaders.put(k.toLowerCase(), httpExchange.getRequestHeaders().getFirst(k)));
        allHeaders.put(HttpHeaderKeys.HOST.toString(), allHeaders.get(HttpHeaderKeys.HOST.toString()).replaceAll(
                "^" + (transport.isSecureServer() ? Constants.SECURE_HTTP : Constants.PLAIN_HTTP), "malhttp"));
        GENMessageHeader malMessageHeader = new GENMessageHeader();
        GENMessage genMessage;
        try {
            HttpToMalMapper.getInstance().fillMalMessageHeader(allHeaders, malMessageHeader);
            genMessage = transport.createMessage(encodedMessage, malMessageHeader);
            HttpTransport.LOGGER.log(Level.FINE, "Created Encoded Message from Http:\n " + genMessage);
        } catch (MALException exp) {
            returnErrorResponse(httpExchange, ResponseCodes.BAD_REQUEST);
            HttpTransport.LOGGER.log(Level.WARNING, "Incoming message has error decoding.", exp);
            return;
        }

        try {
            transport.addNewIncomingMessageToProcess(new IncomingMessageProcessor(
                    new HttpIncomingMessageHolder(httpExchange, genMessage, false), transport));
            HttpTransport.LOGGER.log(Level.FINE, "Created Message Processor Thread & added to Executor Service");
        } catch (MALTransmitErrorException exp) {
            try {
                HttpTransportHelper.fillResponse(httpExchange, ResponseCodes.INTERNAL_SERVER_ERROR.getCode(),
                        null, null);
            } catch (MALException malExp) {
                HttpTransport.LOGGER.log(Level.WARNING, "Error while responding error code", malExp);
            }
            HttpTransport.LOGGER.log(Level.SEVERE, "Error while addNewIncomingMessageToProcess.", exp);
        }
    }

    /**
     * Helper method to send an error message.
     *
     * @param httpExchange Http Exchange Object which has response header & body
     * @param responseCodes Http Error Code
     * @throws IOException Http related exceptions
     */
    private void returnErrorResponse(HttpExchange httpExchange, ResponseCodes responseCodes) throws IOException {
        try {
            HttpTransportHelper.fillResponse(httpExchange, responseCodes.getCode(),
                    Constants.DEFAULT_ERROR_RESPONSE_BODY.getBytes(),
                    null);
        } catch (MALException exp) {
            throw new IOException(exp);
        }
    }


}
