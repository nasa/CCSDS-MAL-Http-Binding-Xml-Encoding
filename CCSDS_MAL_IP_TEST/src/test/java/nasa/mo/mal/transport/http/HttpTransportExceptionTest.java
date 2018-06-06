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

import esa.mo.mal.transport.gen.GENMessage;
import nasa.mo.mal.transport.http.util.Constants;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.junit.Test;

import java.util.Calendar;
import java.util.HashMap;

/**
 * @author wphyo
 *         Created on 7/12/17.
 */
public abstract class HttpTransportExceptionTest extends HttpTransportTestBase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        clientProperties = new HashMap<>();
    }

    /**
     * Connection Refused Exception Testing
     *
     * @throws MALException              MAL & encoding errors
     * @throws MALTransmitErrorException Transport errors
     */
    @Test
    public void sendActionFailedTest01() throws MALException, MALTransmitErrorException {
        try {
            clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
            clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
            elements.add(new FineTime(123456789L));
            shortForms.add(Attribute.FINETIME_SHORT_FORM);
            generateSendOp();
            clientTransport = (HttpTransport) factory.createTransport(null, clientProperties);
            clientEndpoint = (HttpEndpoint) clientTransport.createEndpoint("", clientProperties);
            clientEndpoint.setMessageListener(clientListener);
            GENMessage message = (GENMessage) clientEndpoint.createMessage(new Blob(new byte[]{1, 2, 3}),
                    new URI("malhttp://127.0.0.1:8080/Service1"),
                    new Time(Calendar.getInstance().getTimeInMillis()),
                    QoSLevel.ASSURED,
                    new UInteger(1L),
                    new IdentifierList(),
                    new Identifier("Test Zone"),
                    SessionType.LIVE,
                    new Identifier("Test Session Name"),
                    InteractionType.SEND,
                    new UOctet((short) 1),
                    currentTransactionId,
                    serviceArea,
                    new UShort(1),
                    new UShort(1),
                    new UOctet((short) 1),
                    false,
                    clientProperties,
                    elements.toArray());
            clientEndpoint.sendMessage(message);
            elements.clear();
            shortForms.clear();
        } catch (MALTransmitErrorException exp) {
            System.out.println("sendActionFailedTest01: " + exp);
            throw exp;
        }
    }

    /**
     * Trying to send a response stage as a new message in client only transport.
     * It will throw an MALTransmitErrorException which is converted to IO for comparison
     *
     * @throws MALException              MAL & encoding errors
     * @throws MALTransmitErrorException Transport errors
     */
    @Test
    public void pubsubActionPubDeregAckStageTest01() throws MALException, MALTransmitErrorException {
        generatePubSubOp();
        clientTransport = (HttpTransport) factory.createTransport(null, clientProperties);
        clientEndpoint = (HttpEndpoint) clientTransport.createEndpoint("", clientProperties);
        clientEndpoint.setMessageListener(clientListener);
        GENMessage message = (GENMessage) clientEndpoint.createMessage(new Blob(new byte[]{1, 2, 3}),
                new URI(targetURI),
                new Time(Calendar.getInstance().getTimeInMillis()),
                QoSLevel.ASSURED,
                new UInteger(1L),
                new IdentifierList(),
                new Identifier("Test Zone"),
                SessionType.LIVE,
                new Identifier("Test Session Name"),
                InteractionType.PUBSUB,
                new UOctet((short) 10),
                currentTransactionId,
                serviceArea,
                new UShort(1),
                new UShort(1),
                new UOctet((short) 1),
                false,
                clientProperties,
                elements.toArray());
        thrown.expect(MALTransmitErrorException.class);
        clientEndpoint.sendMessage(message);
    }

    /**
     * Trying to send a response stage as a new message.
     * It will try to find the
     *
     * @throws MALException              MAL & encoding errors
     * @throws MALTransmitErrorException Transport errors
     */
    @Test
    public void pubsubActionPubDeregAckStageTest02() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generatePubSubOp();
        clientTransport = (HttpTransport) factory.createTransport(null, clientProperties);
        clientEndpoint = (HttpEndpoint) clientTransport.createEndpoint("", clientProperties);
        clientEndpoint.setMessageListener(clientListener);
        GENMessage message = (GENMessage) clientEndpoint.createMessage(new Blob(new byte[]{1, 2, 3}),
                new URI(targetURI),
                new Time(Calendar.getInstance().getTimeInMillis()),
                QoSLevel.ASSURED,
                new UInteger(1L),
                new IdentifierList(),
                new Identifier("Test Zone"),
                SessionType.LIVE,
                new Identifier("Test Session Name"),
                InteractionType.PUBSUB,
                new UOctet((short) 10),
                currentTransactionId,
                serviceArea,
                new UShort(1),
                new UShort(1),
                new UOctet((short) 1),
                false,
                clientProperties,
                elements.toArray());
//        thrown.expect(MALTransmitErrorException.class);
        clientEndpoint.sendMessage(message);
    }
}
