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
import nasa.mo.mal.transport.http.util.HttpTransportHelper;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * @author wphyo
 *         Created on 7/13/17.
 */
public abstract class HttpTransportInternalSendTest extends HttpTransportTestBase {


    @Override
    public void setUp() throws Exception {
        super.setUp();
        clientProperties = new HashMap<>();
        elements.add(new FineTime(982345998765432134L));
        shortForms.add(Attribute.FINETIME_SHORT_FORM);

        doAnswer(invocationOnMock -> {
            System.out.println("client MAL Message Listener onMessage");
            Object[] arguments = invocationOnMock.getArguments();
            if (arguments != null && arguments.length == 2 && arguments[0] != null && arguments[1] != null) {

                HttpEndpoint paramEndpoint = (HttpEndpoint) arguments[0];
                GENMessage incomingMessage = (GENMessage) arguments[1];
                Assert.assertEquals(currentTransactionId, incomingMessage.getHeader().getTransactionId());
                Assert.assertEquals(paramEndpoint, clientEndpoint);
                printIncomingMessage(incomingMessage);
                System.out.println("Response Message is submitted to MAL. ");
                if (HttpTransportHelper.isDefaultReply(incomingMessage.getHeader().getInteractionType(),
                        incomingMessage.getHeader().getInteractionStage())) {
                    System.out.println("No Reply is necessary. ");
                } else {
                    if (HttpTransportHelper.isResponseReply(incomingMessage.getHeader().getInteractionType(),
                            new UOctet((short) (incomingMessage.getHeader().getInteractionStage().getValue() + 1)))) {
                        System.out.println("Reply is required. Replying Ack now.");
                        shortForms.clear();
                        GENMessage message = (GENMessage) clientEndpoint.createMessage(new Blob(new byte[]{1, 2, 3}),
                                incomingMessage.getHeader().getURIFrom(),
                                new Time(Calendar.getInstance().getTimeInMillis()),
                                QoSLevel.ASSURED,
                                new UInteger(1L),
                                new IdentifierList(),
                                new Identifier("Test Zone"),
                                SessionType.LIVE,
                                new Identifier("Test Session Name"),
                                incomingMessage.getHeader().getInteractionType(),
                                new UOctet((short) (incomingMessage.getHeader().getInteractionStage().getValue() + 1)),
                                currentTransactionId,
                                serviceArea,
                                new UShort(1),
                                new UShort(1),
                                new UOctet((short) 1),
                                false,
                                serverProperties);
                        clientEndpoint.sendMessage(message);
                    } else {
                        System.out.println("Invalid Stage");
                    }
                }
            } else {
                System.out.println("Invalid parameters on MALMessageListener onMessage()");
                Assert.assertTrue(false);
            }
            return null;
        }).when(clientListener).onMessage(any(), any());
    }

    @Override
    protected void internalTest(Map<String, String> properties, String endPointName, InteractionType type, UOctet stage) throws MALException, MALTransmitErrorException {
        clientTransport = (HttpTransport) factory.createTransport(null, properties);
        clientEndpoint = (HttpEndpoint) clientTransport.createEndpoint(endPointName, properties);
        clientEndpoint.setMessageListener(clientListener);
        GENMessage message = (GENMessage) clientEndpoint.createMessage(new Blob(new byte[]{1, 2, 3}),
                new URI("malhttp://127.0.0.1:" + properties.get(Constants.SERVER_PORT_KEY) + "/ClientService"),
                new Time(Calendar.getInstance().getTimeInMillis()),
                QoSLevel.ASSURED,
                new UInteger(1L),
                new IdentifierList(),
                new Identifier("Test Zone"),
                SessionType.LIVE,
                new Identifier("Test Session Name"),
                type,
                stage,
                currentTransactionId,
                serviceArea,
                new UShort(1),
                new UShort(1),
                new UOctet((short) 1),
                false,
                properties,
                elements.toArray());
        clientEndpoint.sendMessage(message);
    }

    @Test
    public void sendActionTestInternal01() throws MALException, MALTransmitErrorException {

        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateSendOp();
        internalTest(clientProperties, "ClientService", InteractionType.SEND, new UOctet((short) 1));
    }

    @Test
    public void submitActionTestInternal01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateSubmitOp();
        internalTest(clientProperties, "ClientService", InteractionType.SUBMIT, new UOctet((short) 1));
    }

    @Test
    public void pubsubActionTestInternal01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generatePubSubOp();
        internalTest(clientProperties, "ClientService", InteractionType.PUBSUB, new UOctet((short) 1));
    }


    @Test
    public void pubsubActionTestInternal02() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generatePubSubOp();
        internalTest(clientProperties, "ClientService", InteractionType.PUBSUB, new UOctet((short) 3));
    }


    @Test
    public void pubsubActionTestInternal03() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generatePubSubOp();
        internalTest(clientProperties, "ClientService", InteractionType.PUBSUB, new UOctet((short) 5));
    }

    @Test
    public void pubsubActionTestInternal04() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generatePubSubOp();
        internalTest(clientProperties, "ClientService", InteractionType.PUBSUB, new UOctet((short) 6));
    }

    @Test
    public void pubsubActionTestInternal05() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generatePubSubOp();
        internalTest(clientProperties, "ClientService", InteractionType.PUBSUB, new UOctet((short) 7));
    }

    @Test
    public void pubsubActionTestInternal06() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generatePubSubOp();
        internalTest(clientProperties, "ClientService", InteractionType.PUBSUB, new UOctet((short) 9));
    }
}
