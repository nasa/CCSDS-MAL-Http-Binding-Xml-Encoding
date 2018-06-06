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
import org.ccsds.moims.mo.mal.structures.factory.*;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * @author wphyo
 *         Created on 7/12/17.
 */
public abstract class HttpTransportRequestExceptionTest extends HttpTransportTestBase {

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

    protected void manualSetUp1() throws Exception {
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
                    System.out.println("Reply is required. Replying Ack now.");
                    throw new MALException("some error");
                }
            } else {
                Assert.assertTrue(false);
            }
            return null;
        }).when(serverListener).onMessage(any(), any());
        serverEndpoint.setMessageListener(serverListener);
    }

    @Test
    public void submitActionTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateSubmitOp();
        internalTest(clientProperties, "Service2", InteractionType.SUBMIT, new UOctet((short) 1));
    }

    @Test
    public void submitActionTest02() throws MALException, MALTransmitErrorException {
        generateSubmitOp();
        internalTest(clientProperties, "Service3", InteractionType.SUBMIT, new UOctet((short) 1));
    }

    @Test
    public void submitActionTest03() throws MALException, MALTransmitErrorException {
        generateSubmitOp();
        internalTest(clientProperties, "", InteractionType.SUBMIT, new UOctet((short) 1));
    }

    //TODO move this
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

    @Test
    public void pubsubActionInitStageTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        elements.clear();
        shortForms.clear();
        elements.add(new Subscription(identifier, new EntityRequestList()));
        shortForms.add(Subscription.SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Subscription.SHORT_FORM, new SubscriptionFactory());
        generatePubSubOp();
        internalTest(clientProperties, "Service3", InteractionType.PUBSUB, new UOctet((short) 1));
        elements.clear();
        shortForms.clear();
    }

    @Test
    public void pubsubActionPubRegStageTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        elements.clear();
        shortForms.clear();
        elements.add(new EntityKeyList());
        shortForms.add(EntityKeyList.SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(EntityKeyList.SHORT_FORM, new EntityKeyListFactory());
        generatePubSubOp();
        internalTest(clientProperties, "Service3", InteractionType.PUBSUB, new UOctet((short) 3));
        elements.clear();
        shortForms.clear();
    }

    @Test
    public void pubsubActionDeregStageTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        elements.clear();
        shortForms.clear();
        elements.add(identifiers);
        shortForms.add(IdentifierList.SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(IdentifierList.SHORT_FORM, new IdentifierListFactory());
        generatePubSubOp();
        internalTest(clientProperties, "Service3", InteractionType.PUBSUB, new UOctet((short) 7));
        MALContextFactory.getElementFactoryRegistry().deregisterElementFactory(IdentifierList.SHORT_FORM);
        elements.clear();
        shortForms.clear();
    }


    @Test
    public void pubsubActionPubDeregStageTest01() throws MALException, MALTransmitErrorException, InterruptedException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        shortForms.add(Attribute.FINETIME_SHORT_FORM);
        generatePubSubOp();
        elements.clear();
        shortForms.clear();
        internalTest(clientProperties, "Service3", InteractionType.PUBSUB, new UOctet((short) 9));
    }

}
