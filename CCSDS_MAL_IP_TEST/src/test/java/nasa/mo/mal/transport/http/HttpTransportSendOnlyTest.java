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
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.structures.factory.FineTimeFactory;
import org.ccsds.moims.mo.mal.structures.factory.IdentifierFactory;
import org.ccsds.moims.mo.mal.structures.factory.StringFactory;
import org.ccsds.moims.mo.mal.structures.factory.UpdateHeaderListFactory;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * @author wphyo
 *         Created on 7/12/17.
 */
public abstract class HttpTransportSendOnlyTest extends HttpTransportTestBase {

    @BeforeClass
    public static void oneTimeSetUpSendOnly() throws Exception {
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
                sendOnlyServerAnalyzer(incomingMessage);
            } else {
                Assert.assertTrue(false);
            }
            return null;
        }).when(serverListener).onMessage(any(), any());
        serverEndpoint.setMessageListener(serverListener);
    }

    @Override
    public void tearDown() {
        super.tearDown();
        elements.clear();
        shortForms.clear();
    }

    private void internalTestError(Map<String, String> properties, String endPointName, InteractionType type, UOctet stage)
            throws MALException, MALTransmitErrorException {
        clientTransport = (HttpTransport) factory.createTransport(null, properties);
        clientEndpoint = (HttpEndpoint) clientTransport.createEndpoint(endPointName, properties);
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
                type,
                stage,
                currentTransactionId,
                serviceArea,
                new UShort(1),
                new UShort(1),
                new UOctet((short) 1),
                true,
                properties,
                MALHelper.UNKNOWN_ERROR_NUMBER,
                new Union("Testing Error"));
        clientEndpoint.sendMessage(message);
    }

    @Test
    public void sendActionTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateSendOp();
        internalTest(clientProperties, "Service1", InteractionType.SEND, new UOctet((short) 1));
    }

    @Test
    public void sendActionTest02() throws MALException, MALTransmitErrorException, InterruptedException {
        generateSendOp();
        internalTest(clientProperties, "Service1", InteractionType.SEND, new UOctet((short) 1));
    }

    @Test
    public void sendActionTest03() throws MALException, MALTransmitErrorException {
        generateSendOp();
        internalTest(clientProperties, "", InteractionType.SEND, new UOctet((short) 1));
    }

    @Test
    public void invokeActionResponseStageTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateInvokeOp();
        internalTest(clientProperties, "Service3", InteractionType.INVOKE, new UOctet((short) 3));
    }

    @Test
    public void invokeActionResponseStageErrorTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateInvokeOp();
        internalTestError(clientProperties, "Service3", InteractionType.INVOKE, new UOctet((short) 3));
    }

    @Test
    public void progressActionUpdateStageTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateProgressOp();
        internalTest(clientProperties, "Service3", InteractionType.PROGRESS, new UOctet((short) 3));
    }

    @Test
    public void progressActionResponseStageTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateProgressOp();
        internalTest(clientProperties, "Service3", InteractionType.PROGRESS, new UOctet((short) 4));
    }

    @Test
    public void progressActionUpdateStageErrorTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateProgressOp();
        internalTestError(clientProperties, "Service3", InteractionType.PROGRESS, new UOctet((short) 3));
    }

    @Test
    public void progressActionResponseStageErrorTest01() throws MALException, MALTransmitErrorException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateProgressOp();
        internalTestError(clientProperties, "Service3", InteractionType.PROGRESS, new UOctet((short) 4));
    }

    @Test
    public void pubsubActionPublishStageTest01() throws MALException, MALTransmitErrorException, InterruptedException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        shortForms.clear();
        shortForms.add(UpdateHeaderList.SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UpdateHeaderList.SHORT_FORM, new UpdateHeaderListFactory());
        generatePubSubOp();
        elements.clear();
        elements.add(new UpdateHeaderList());
        elements.add(new UpdateHeaderList());
        internalTest(clientProperties, "Service3", InteractionType.PUBSUB, new UOctet((short) 5));
    }

    @Test
    public void pubsubActionPublishStageErrorTest01() throws MALException, MALTransmitErrorException, InterruptedException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generatePubSubOp();
        internalTestError(clientProperties, "Service3", InteractionType.PUBSUB, new UOctet((short) 5));
    }

    @Test
    public void pubsubActionNotifyStageTest01() throws MALException, MALTransmitErrorException, InterruptedException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        shortForms.clear();
        shortForms.add(UpdateHeaderList.SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.IDENTIFIER_SHORT_FORM, new IdentifierFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UpdateHeaderList.SHORT_FORM, new UpdateHeaderListFactory());
        generatePubSubOp();
        elements.clear();
        elements.add(new Identifier("Test ID for Notify Test"));
        elements.add(new UpdateHeaderList());
        elements.add(new UpdateHeaderList());
        internalTest(clientProperties, "Service3", InteractionType.PUBSUB, new UOctet((short) 6));
    }

    @Test
    public void pubsubActionNotifyStageErrorTest01() throws MALException, MALTransmitErrorException, InterruptedException {
        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generatePubSubOp();
        internalTestError(clientProperties, "Service3", InteractionType.PUBSUB, new UOctet((short) 6));
    }


}
