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

import esa.mo.mal.transport.gen.GENMessage;
import nasa.mo.mal.transport.http.HttpEndpoint;
import nasa.mo.mal.transport.http.HttpTransport;
import nasa.mo.mal.transport.http.util.HttpTransportHelper;
import nasa.mo.mal.transport.http.util.MALTransmitErrorBuilder;
import nasa.mo.mal.transport.http.util.ResponseCodes;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;

/**
 * @author wphyo
 *         Created on 6/22/17.
 * A thread to process a single message.
 */
public class IncomingMessageProcessor implements Callable<Boolean> {
    private HttpIncomingMessageHolder incomingMessageHolder;
    private HttpTransport transport;
    private GENMessage decodedMessage;
    /**
     * Constructor for this thread
     * TODO how to create endpoint
     * @param incomingMessageHolder Received Http Message Holder with all necessary objects
     * @param transport Http Transport to create endpoint and store messages
     * @throws IOException any error
     */
    public IncomingMessageProcessor(HttpIncomingMessageHolder incomingMessageHolder,
                                    HttpTransport transport) throws IOException {
        this.incomingMessageHolder = incomingMessageHolder;
        decodedMessage = incomingMessageHolder.getIncomingMessage();
        if (transport == null) {
            throw new IOException("Constructor with NULL transport in IncomingMessageProcessor");
        }
        this.transport = transport;
    }

    /**
     * Processing a single received http message.
     * Steps:
     * 1.   get Http Message to get header & body
     * 2.   check if the message is in the category of not needing actual response.
     *          if so, send default message with default response code.
     * 3.   if not, store it in transport to be used later.
     * 4.   call Listener's onMessage via Endpoint.
     *
     * @throws Exception for any IO or MAL Exception, a new MAL Exception is thrown
     */
    @Override
    public Boolean call() throws Exception {
        if (incomingMessageHolder.isInternalSend()) {
            if (decodedMessage == null || decodedMessage.getHeader() == null || decodedMessage.getBody() == null) {
                throw MALTransmitErrorBuilder.create().setExtraInfo("Decoded Message is null.").build();
            }
            HttpEndpoint endpoint = (HttpEndpoint) transport.getEndpoint(decodedMessage.getHeader().getURITo());
            if (endpoint == null) {
                HttpTransport.LOGGER.log(Level.FINE, "Null Endpoint. Unable to send to MAL layer. throw error.");
                throw MALTransmitErrorBuilder.create().setHeader(decodedMessage.getHeader())
                        .setExtraInfo("Null Endpoint.").build();
            }
            endpoint.receiveMessage(decodedMessage);
            return true;
        } else {
            return externalMessageLogic();
        }
    }

    private Boolean externalMessageLogic() throws MALTransmitErrorException, MALException {
        HttpEndpoint endpoint;
        try {
            endpoint = (HttpEndpoint) this.transport.getEndpoint(decodedMessage.getHeader().getURITo());
            if (endpoint == null) {
                HttpTransport.LOGGER.log(Level.FINE, "Null Endpoint. Unable to send to MAL layer. throw error.");
                HttpTransportHelper.fillResponse(incomingMessageHolder.getHttpExchange(),
                        ResponseCodes.INTERNAL_SERVER_ERROR.getCode(),
                        null,
                        null);
                return false;
            }
            MALMessage httpMessage = incomingMessageHolder.getIncomingMessage();
            if (httpMessage.getHeader().getURITo() == null) {
                HttpTransportHelper.fillResponse(incomingMessageHolder.getHttpExchange(),
                        HttpTransportHelper.getStatusCodeFromMALError(MALHelper.DESTINATION_UNKNOWN_ERROR_NUMBER).getCode(),
                        null,
                        null);
                return false;
            }
            if (HttpTransportHelper.isDefaultReply(httpMessage.getHeader().getInteractionType(),
                    httpMessage.getHeader().getInteractionStage())) {
                HttpTransport.LOGGER.log(Level.FINE, "Message is a default reply type. No need to get response from MAL.");
                try {
                    HttpTransportHelper.fillResponse(incomingMessageHolder.getHttpExchange(),
                            ResponseCodes.OTHER.getCode(),null, null);
                } catch (MALException exp) {
                    throw MALTransmitErrorBuilder.create().setHeader(decodedMessage.getHeader())
                            .setExtraInfo(exp).build();
                }
            } else {
                HttpTransport.LOGGER.log(Level.FINE, "Message needs a response from MAL. Stored in Transport");
                transport.storeProcessingMessage(httpMessage.getHeader(), incomingMessageHolder.getHttpExchange());
            }
            endpoint.receiveMessage(httpMessage);
            HttpTransport.LOGGER.log(Level.FINE, "Message is sent to MAL Layer.");
            return true;
        } catch (MALException exp) {
            HttpTransport.LOGGER.log(Level.FINE, "Some error occurred.", exp);
            HttpTransportHelper.fillResponse(incomingMessageHolder.getHttpExchange(),
                    ResponseCodes.INTERNAL_SERVER_ERROR.getCode(),
                    null,
                    null);
            throw MALTransmitErrorBuilder.create().setHeader(decodedMessage.getHeader())
                    .setExtraInfo(exp).build();
        }
    }
}
