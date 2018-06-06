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
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.structures.factory.FineTimeFactory;
import org.ccsds.moims.mo.mal.structures.factory.StringFactory;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * @author wphyo
 *         Created on 7/12/17.
 */
public abstract class HttpTransportRequestRespondTest extends HttpTransportTestBase {

    @BeforeClass
    public static void oneTimeSetUp1() {
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.FINETIME_SHORT_FORM, new FineTimeFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.STRING_SHORT_FORM, new StringFactory());
    }

    @Before
    public void setUp1() throws Exception {
        clientProperties = new HashMap<>();
        clientProperties.put(Constants.HTTP_DESTINATION_ENDPOINT_KEY, targetURI);
        manualSetUp1();
    }

    protected void manualSetUp1() throws MALException {
        elements.add(new FineTime(982345998765432134L));
        shortForms.add(Attribute.FINETIME_SHORT_FORM);
        doAnswer(invocationOnMock -> {
            System.out.println("server MAL Message Listener onMessage");
            Object[] arguments = invocationOnMock.getArguments();
            if (arguments != null && arguments.length == 2 && arguments[0] != null && arguments[1] != null) {
                HttpEndpoint paramEndpoint = (HttpEndpoint) arguments[0];
                GENMessage incomingMessage = (GENMessage) arguments[1];
                Assert.assertEquals(currentTransactionId, incomingMessage.getHeader().getTransactionId());
                Assert.assertEquals(paramEndpoint, serverEndpoint);
                printIncomingMessage(incomingMessage);
                if (HttpTransportHelper.isDefaultReply(incomingMessage.getHeader().getInteractionType(),
                        incomingMessage.getHeader().getInteractionStage())) {
                    System.out.println("No Reply is necessary. ");
                    Assert.assertTrue(false);
                } else {
                    System.out.println("Reply is required. Replying Respond now.");
                    Assert.assertTrue(true);
                    GENMessage message = (GENMessage) serverEndpoint.createMessage(new Blob(new byte[]{1, 2, 3}),
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
                            serverProperties,
                            new FineTime(0L));
                    serverEndpoint.sendMessage(message);
                }
            } else {
                Assert.assertTrue(false);
            }
            return null;
        }).when(serverListener).onMessage(any(), any());
        serverEndpoint.setMessageListener(serverListener);
    }

    @Test
    public void requestActionTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateRequestOp();
        internalTest(clientProperties, "Service3", InteractionType.INVOKE, new UOctet((short) 1));
    }

    @Test
    public void invokeActionInitStageTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateInvokeOp();
        internalTest(clientProperties, "Service3", InteractionType.INVOKE, new UOctet((short) 1));
    }

    @Test
    public void progressActionInitStageTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateProgressOp();
        internalTest(clientProperties, "Service3", InteractionType.PROGRESS, new UOctet((short) 1));
    }

}
