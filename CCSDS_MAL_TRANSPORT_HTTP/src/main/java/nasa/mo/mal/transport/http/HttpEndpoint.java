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

import esa.mo.mal.transport.gen.GENEndpoint;
import esa.mo.mal.transport.gen.GENMessage;
import esa.mo.mal.transport.gen.GENMessageHeader;
import esa.mo.mal.transport.gen.GENTransport;
import nasa.mo.mal.transport.http.util.MALTransmitErrorBuilder;
import org.ccsds.moims.mo.mal.*;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.ccsds.moims.mo.mal.transport.MALTransmitMultipleErrorException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author wphyo
 *         Created on 6/22/17.
 */
public class HttpEndpoint extends GENEndpoint {

    /**
     * Constructor for GENEndpoint.
     * @param transport Parent transport.
     * @param localName Endpoint local MAL name.
     * @param routingName Endpoint local routing name.
     * @param uri The URI string for this end point.
     * @param wrapBodyParts True if the encoded body parts should be wrapped in BLOBs.
     */
    public HttpEndpoint(GENTransport transport, String localName, String routingName, String uri, boolean wrapBodyParts) {
        super(transport, localName, routingName, uri, wrapBodyParts);
    }

    /**
     * The method instantiates a new MALMessage object.
     * Constructor to create decoded message in an array
     *
     * @param authenticationId  Authentication identifier of the message
     * @param uriTo             URI of the message destination
     * @param timestamp         Timestamp of the message
     * @param qosLevel          QoS level of the message
     * @param priority          Priority of the message
     * @param domain            Domain of the service provider
     * @param networkZone       Network zone of the service provider
     * @param session           Session of the service provider
     * @param sessionName       Session name of the service provider
     * @param interactionType   Interaction type of the operation
     * @param interactionStage  Interaction stage of the interaction
     * @param transactionId     Transaction identifier of the interaction, may be null.
     * @param serviceAreaNumber Area number of the service
     * @param serviceNumber     Service number
     * @param operationNumber   Operation number
     * @param areaVersion       Area version number
     * @param isErrorMessage    Flag indicating if the message conveys an error
     * @param qosProperties     QoS properties of the message, may be null.
     * @param body              Message body elements
     * @return The created message.
     * @throws IllegalArgumentException If any of the parameters except ‘transactionId’ or ‘qosProperties’ are
     *                                  NULL
     * @throws MALException             If the MALEndpoint is closed
     */
    @Override
    public MALMessage createMessage(Blob authenticationId, URI uriTo, Time timestamp, QoSLevel qosLevel,
                                    UInteger priority, IdentifierList domain, Identifier networkZone,
                                    SessionType session, Identifier sessionName, InteractionType interactionType,
                                    UOctet interactionStage, Long transactionId, UShort serviceAreaNumber,
                                    UShort serviceNumber, UShort operationNumber, UOctet areaVersion,
                                    Boolean isErrorMessage, Map qosProperties, Object... body)
            throws IllegalArgumentException, MALException {
        try {
            return new GENMessage(wrapBodyParts, new GENMessageHeader(getURI(), authenticationId, uriTo, timestamp,
                    qosLevel, priority, domain, networkZone, session, sessionName, interactionType, interactionStage,
                    transactionId, serviceAreaNumber, serviceNumber, operationNumber, areaVersion, isErrorMessage),
                    qosProperties,
                    null,
                    transport.getStreamFactory(),
                    body);
        } catch (MALInteractionException exp) {
            throw new MALException("Error in creating decoded message", exp);
        }
    }

    /**
     * The method instantiates a new MALMessage object.
     * Constructor to create decoded message in an MAL-Encoded-Body
     *
     * @param authenticationId  Authentication identifier of the message
     * @param uriTo             URI of the message destination
     * @param timestamp         Timestamp of the message
     * @param qosLevel          QoS level of the message
     * @param priority          Priority of the message
     * @param domain            Domain of the service provider
     * @param networkZone       Network zone of the service provider
     * @param session           Session of the service provider
     * @param sessionName       Session name of the service provider
     * @param interactionType   Interaction type of the operation
     * @param interactionStage  Interaction stage of the interaction
     * @param transactionId     Transaction identifier of the interaction, may be null.
     * @param serviceAreaNumber Area number of the service
     * @param serviceNumber     Service number
     * @param operationNumber   Operation number
     * @param areaVersion       Area version number
     * @param isErrorMessage    Flag indicating if the message conveys an error
     * @param qosProperties     QoS properties of the message, may be null.
     * @param body              Message body elements
     * @return The created message.
     * @throws IllegalArgumentException If any of the parameters except ‘transactionId’ or ‘qosProperties’ are
     *                                  NULL
     * @throws MALException             If the MALEndpoint is closed
     */
    @Override
    public MALMessage createMessage(Blob authenticationId, URI uriTo, Time timestamp, QoSLevel qosLevel,
                                    UInteger priority, IdentifierList domain, Identifier networkZone,
                                    SessionType session, Identifier sessionName, InteractionType interactionType,
                                    UOctet interactionStage, Long transactionId, UShort serviceAreaNumber,
                                    UShort serviceNumber, UShort operationNumber, UOctet areaVersion,
                                    Boolean isErrorMessage, Map qosProperties, MALEncodedBody body)
            throws IllegalArgumentException, MALException {
        try {
            return new GENMessage(wrapBodyParts, new GENMessageHeader(getURI(), authenticationId, uriTo, timestamp,
                    qosLevel, priority, domain, networkZone, session, sessionName, interactionType,
                    interactionStage, transactionId, serviceAreaNumber, serviceNumber,
                    operationNumber, areaVersion, isErrorMessage),
                    qosProperties,
                    null,
                    transport.getStreamFactory(),
                    body);
        } catch (MALInteractionException exp) {
            throw new MALException("Error in creating decoded message", exp);
        }
    }

    /**
     * The method instantiates a new MALMessage object.
     *
     * @param authenticationId Authentication identifier of the message
     * @param uriTo            URI of the message destination
     * @param timestamp        Timestamp of the message
     * @param qosLevel         QoS level of the message
     * @param priority         Priority of the message
     * @param domain           Domain of the service provider
     * @param networkZone      Network zone of the service provider
     * @param session          Session of the service provider
     * @param sessionName      Session name of the service provider
     * @param transactionId    Transaction identifier of the interaction, may be null.
     * @param isErrorMessage   Flag indicating if the message conveys an error
     * @param op               Operation represented as a MALOperation
     * @param interactionStage Interaction stage of the interaction
     * @param qosProperties    QoS properties of the message, may be null.
     * @param body             Message body elements
     * @return The created message.
     * @throws IllegalArgumentException If any of the parameters except ‘transactionId’ or ‘qosProperties’ are
     *                                  NULL
     * @throws MALException             If the MALEndpoint is closed
     */
    @Override
    public MALMessage createMessage(Blob authenticationId, URI uriTo, Time timestamp, QoSLevel qosLevel,
                                    UInteger priority, IdentifierList domain, Identifier networkZone,
                                    SessionType session, Identifier sessionName, Long transactionId,
                                    Boolean isErrorMessage, MALOperation op, UOctet interactionStage,
                                    Map qosProperties, Object... body)
            throws IllegalArgumentException, MALException {
        return createMessage(authenticationId, uriTo, timestamp, qosLevel, priority, domain, networkZone, session,
                sessionName, op.getInteractionType(), interactionStage, transactionId,
                op.getService().getArea().getNumber(),
                op.getService().getNumber(),
                op.getNumber(),
                op.getService().getArea().getVersion(),
                isErrorMessage, qosProperties, body);
    }

    /**
     * The method instantiates a new MALMessage object.
     *
     * @param authenticationId Authentication identifier of the message
     * @param uriTo            URI of the message destination
     * @param timestamp        Timestamp of the message
     * @param qosLevel         QoS level of the message
     * @param priority         Priority of the message
     * @param domain           Domain of the service provider
     * @param networkZone      Network zone of the service provider
     * @param session          Session of the service provider
     * @param sessionName      Session name of the service provider
     * @param transactionId    Transaction identifier of the interaction, may be null.
     * @param isErrorMessage   Flag indicating if the message conveys an error
     * @param op               Operation represented as a MALOperation
     * @param interactionStage Interaction stage of the interaction
     * @param qosProperties    QoS properties of the message, may be null.
     * @param body             The already encoded message body
     * @return The created message.
     * @throws IllegalArgumentException If any of the parameters except ‘transactionId’ or ‘qosProperties’ are
     *                                  NULL
     * @throws MALException             If the MALEndpoint is closed
     */
    @Override
    public MALMessage createMessage(
            Blob authenticationId, URI uriTo, Time timestamp, QoSLevel qosLevel,
            UInteger priority, IdentifierList domain, Identifier networkZone,
            SessionType session, Identifier sessionName, Long transactionId,
            Boolean isErrorMessage, MALOperation op, UOctet interactionStage,
            Map qosProperties, MALEncodedBody body)
            throws IllegalArgumentException, MALException {
        return createMessage(authenticationId, uriTo, timestamp, qosLevel, priority, domain, networkZone, session,
                sessionName, op.getInteractionType(), interactionStage, transactionId,
                op.getService().getArea().getNumber(),
                op.getService().getNumber(),
                op.getNumber(),
                op.getService().getArea().getVersion(),
                isErrorMessage, qosProperties, body);
    }

    /**
     * The method sends a MALMessage.
     *
     * Steps:
     * 1.   Validations on message and transport instance
     * 2.   Call SendMessage from Transport.
     * 3.   NOTE: no need to check the status of message sending process as this is done asynchronously.
     *
     * @param msg The message to be sent.
     * @throws MALTransmitErrorException If a TRANSMIT ERROR occurs
     */
    @Override
    public void sendMessage(MALMessage msg) throws MALTransmitErrorException {
        if (msg == null) {
            throw MALTransmitErrorBuilder.create().setExtraInfo("Null MAL Message").build();
        }
        if (!(transport instanceof HttpTransport)) {
            throw MALTransmitErrorBuilder.create().setHeader(msg.getHeader())
                    .setExtraInfo("Wrong Transport Instance").build();
        }
        transport.sendMessage(null, true, (GENMessage) msg);
    }

    /**
     * The method sends a list of MALMessages. Throws MALTransmitMultipleErrorException
     * if a MULTIPLETRANSMIT ERROR occurs
     *
     * @param msgList List of messages to send.
     * @throws IllegalArgumentException If the parameter is NULL
     */
    @Override
    public void sendMessages(MALMessage[] msgList) throws MALTransmitMultipleErrorException {
        if (msgList == null) {
            throw new MALTransmitMultipleErrorException(new MALTransmitErrorException[] {
                    MALTransmitErrorBuilder.create().setExtraInfo("Null MAL Msg array").build()
            });
        }
        final List<MALTransmitErrorException> errorExceptions = new LinkedList<>();
        Arrays.stream(msgList).forEach(e -> {
            try {
                sendMessage(e);
            } catch (MALTransmitErrorException exp) {
                errorExceptions.add(exp);
            } catch (Exception exp) {
                errorExceptions.add(MALTransmitErrorBuilder.create().setHeader(e.getHeader())
                        .setExtraInfo(exp).build());
            }
        });
        if (!errorExceptions.isEmpty()) {
            errorExceptions.forEach(System.out::println);
            throw new MALTransmitMultipleErrorException(errorExceptions
                    .toArray(new MALTransmitErrorException[errorExceptions.size()]));
        }
    }

    /**
     * When this method is called, it is assumed that the messages are filtered.
     *
     * @param incomingMessage Received Message from other applications
     * @throws MALException any error
     */
    @Override
    public void receiveMessage(MALMessage incomingMessage) throws MALException {
        if (incomingMessage == null) {
            throw new MALException("Http Endpoint received a null MAL Message");
        }

        // TODO 4.6.5: URI-TO is null, respond error
        if (getMessageListener() == null) {
            throw new MALException("Null MAL Message Listener.");
        }
        getMessageListener().onMessage(this, incomingMessage);
    }

    /**
     * NOT SUPPORTED based on White Book.
     *
     * @param incomingMessageList array of Received Messages from other applications
     * @throws MALException any error
     */
    @Override
    public void receiveMessages(GENMessage[] incomingMessageList) throws MALException {
        throw new MALException("Not Supported for Http Transport Protocol, " + this.getClass().getName());
    }
}
