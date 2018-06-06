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
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.structures.factory.FineTimeFactory;
import org.ccsds.moims.mo.mal.structures.factory.StringFactory;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.junit.*;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * @author wphyo
 *         Created on 7/12/17.
 */
public abstract class HttpTransportMultipleRequestTest extends HttpTransportTestBase {

    private Queue<Long> transactionIds;
    private Queue<Long> incomingTransactionIds;
    private List<MALMessage> malMessages;
    private int messageLength = 30;

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

    protected void manualSetUp1() {
        transactionIds = new LinkedList<>();
        incomingTransactionIds = new LinkedList<>();
        malMessages = new ArrayList<>();
        elements.add(new FineTime(982345998765432134L));
        shortForms.add(Attribute.FINETIME_SHORT_FORM);

        doAnswer(invocationOnMock -> {
            System.out.println("client MAL Message Listener onMessage");
            Object[] arguments = invocationOnMock.getArguments();
            if (arguments != null && arguments.length == 2 && arguments[0] != null && arguments[1] != null) {

                HttpEndpoint paramEndpoint = (HttpEndpoint) arguments[0];
                GENMessage incomingMessage = (GENMessage) arguments[1];
                Assert.assertEquals(incomingTransactionIds.poll(), incomingMessage.getHeader().getTransactionId());
                Assert.assertEquals(paramEndpoint, clientEndpoint);
                printIncomingMessage(incomingMessage);
                System.out.println("Response Message is submitted to MAL. ");
            } else {
                System.out.println("Invalid parameters on MALMessageListener onMessage()");
                Assert.assertTrue(false);
            }
            return null;
        }).when(clientListener).onMessage(any(), any());
    }

    @After
    public void tearDown1() {
        transactionIds.clear();
        malMessages.clear();
        elements.clear();
        shortForms.clear();
    }

    /**
     * Generate 30 Send MAL messages and use Endpoint's sendMessages() to send them.
     * All 30 should be received by server.
     * Send doesn't care about response as long as it reaches the server.
     *
     * @throws MALException              MAL & encoding errors
     * @throws MALTransmitErrorException Transport errors
     */
    @Test
    public void sendActionTest01() throws MALException, MALTransmitErrorException {
        doAnswer(invocationOnMock -> {
            System.out.println("server MAL Message Listener onMessage");
            Object[] arguments = invocationOnMock.getArguments();
            if (arguments != null && arguments.length == 2 && arguments[0] != null && arguments[1] != null) {
                HttpEndpoint paramEndpoint = (HttpEndpoint) arguments[0];
                GENMessage incomingMessage = (GENMessage) arguments[1];
                Assert.assertEquals(transactionIds.poll(), incomingMessage.getHeader().getTransactionId());
                Assert.assertEquals(paramEndpoint, serverEndpoint);
                printIncomingMessage(incomingMessage);
                sendOnlyServerAnalyzer(incomingMessage);
            } else {
                Assert.assertTrue(false);
            }
            return null;
        }).when(serverListener).onMessage(any(), any());
        serverEndpoint.setMessageListener(serverListener);

        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateSendOp();

        clientTransport = (HttpTransport) factory.createTransport(null, clientProperties);
        clientEndpoint = (HttpEndpoint) clientTransport.createEndpoint("Service1", clientProperties);
        clientEndpoint.setMessageListener(clientListener);
        for (int i = 0; i < messageLength; i++) {
            currentTransactionId = random.nextLong();
            transactionIds.offer(currentTransactionId);
            malMessages.add(clientEndpoint.createMessage(new Blob(new byte[]{1, 2, 3}),
                    new URI(targetURI),
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
                    elements.toArray()));
        }
        clientEndpoint.sendMessages(malMessages.toArray(new MALMessage[0]));
    }

    /**
     * Generate 30 Submit MAL messages.
     * Each of them expects an acknowledgement.
     *
     * @throws MALException              MAL & encoding errors
     * @throws MALTransmitErrorException Transport errors
     */
    @Test
    public void submitActionTest01() throws MALException, MALTransmitErrorException {
        doAnswer(invocationOnMock -> {
            System.out.println("server MAL Message Listener onMessage");
            Object[] arguments = invocationOnMock.getArguments();
            if (arguments != null && arguments.length == 2 && arguments[0] != null && arguments[1] != null) {
                HttpEndpoint paramEndpoint = (HttpEndpoint) arguments[0];
                GENMessage incomingMessage = (GENMessage) arguments[1];
                Long transactionId = transactionIds.poll();
                Assert.assertEquals(transactionId, incomingMessage.getHeader().getTransactionId());
                Assert.assertEquals(paramEndpoint, serverEndpoint);
                printIncomingMessage(incomingMessage);
                if (HttpTransportHelper.isDefaultReply(incomingMessage.getHeader().getInteractionType(),
                        incomingMessage.getHeader().getInteractionStage())) {
                    System.out.println("No Reply is necessary. ");
                    Assert.assertTrue(false);
                } else {
                    System.out.println("Reply is required. Replying Ack now.");
                    Assert.assertTrue(true);
                    incomingTransactionIds.offer(transactionId);
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
                            transactionId,
                            serviceArea,
                            new UShort(1),
                            new UShort(1),
                            new UOctet((short) 1),
                            false,
                            serverProperties);
                    serverEndpoint.sendMessage(message);
                }
            } else {
                Assert.assertTrue(false);
            }
            return null;
        }).when(serverListener).onMessage(any(), any());
        serverEndpoint.setMessageListener(serverListener);

        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateSubmitOp();

        clientTransport = (HttpTransport) factory.createTransport(null, clientProperties);
        clientEndpoint = (HttpEndpoint) clientTransport.createEndpoint("Service1", clientProperties);
        clientEndpoint.setMessageListener(clientListener);
        for (int i = 0; i < messageLength; i++) {
            currentTransactionId = random.nextLong();
            transactionIds.offer(currentTransactionId);
            malMessages.add(clientEndpoint.createMessage(new Blob(new byte[]{1, 2, 3}),
                    new URI(targetURI),
                    new Time(Calendar.getInstance().getTimeInMillis()),
                    QoSLevel.ASSURED,
                    new UInteger(1L),
                    new IdentifierList(),
                    new Identifier("Test Zone"),
                    SessionType.LIVE,
                    new Identifier("Test Session Name"),
                    InteractionType.SUBMIT,
                    new UOctet((short) 1),
                    currentTransactionId,
                    serviceArea,
                    new UShort(1),
                    new UShort(1),
                    new UOctet((short) 1),
                    false,
                    clientProperties,
                    elements.toArray()));
        }
        clientEndpoint.sendMessages(malMessages.toArray(new MALMessage[0]));
    }

    /**
     * Generate 30 Request MAL messages.
     * Each of the expects a MAL error by server.
     *
     * @throws MALException              MAL & encoding errors
     * @throws MALTransmitErrorException Transport errors
     */
    @Test
    public void requestActionTest01() throws MALException, MALTransmitErrorException {
        doAnswer(invocationOnMock -> {
            System.out.println("server MAL Message Listener onMessage");
            Object[] arguments = invocationOnMock.getArguments();
            if (arguments != null && arguments.length == 2 && arguments[0] != null && arguments[1] != null) {
                HttpEndpoint paramEndpoint = (HttpEndpoint) arguments[0];
                GENMessage incomingMessage = (GENMessage) arguments[1];
                Long transactionId = transactionIds.poll();
                Assert.assertEquals(transactionId, incomingMessage.getHeader().getTransactionId());
                Assert.assertEquals(paramEndpoint, serverEndpoint);
                printIncomingMessage(incomingMessage);
                if (HttpTransportHelper.isDefaultReply(incomingMessage.getHeader().getInteractionType(),
                        incomingMessage.getHeader().getInteractionStage())) {
                    System.out.println("No Reply is necessary. ");
                    Assert.assertTrue(false);
                } else {
                    System.out.println("Reply is required. Replying Ack now.");
                    Assert.assertTrue(true);
                    incomingTransactionIds.offer(transactionId);
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
                            transactionId,
                            serviceArea,
                            new UShort(1),
                            new UShort(1),
                            new UOctet((short) 1),
                            true,
                            serverProperties,
                            MALHelper.BAD_ENCODING_ERROR_NUMBER,
                            new Union("Sending Error back"));
                    serverEndpoint.sendMessage(message);
                }
            } else {
                Assert.assertTrue(false);
            }
            return null;
        }).when(serverListener).onMessage(any(), any());
        serverEndpoint.setMessageListener(serverListener);

        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateRequestOp();

        clientTransport = (HttpTransport) factory.createTransport(null, clientProperties);
        clientEndpoint = (HttpEndpoint) clientTransport.createEndpoint("Service1", clientProperties);
        clientEndpoint.setMessageListener(clientListener);
        for (int i = 0; i < messageLength; i++) {
            currentTransactionId = random.nextLong();
            transactionIds.offer(currentTransactionId);
            malMessages.add(clientEndpoint.createMessage(new Blob(new byte[]{1, 2, 3}),
                    new URI(targetURI),
                    new Time(Calendar.getInstance().getTimeInMillis()),
                    QoSLevel.ASSURED,
                    new UInteger(1L),
                    new IdentifierList(),
                    new Identifier("Test Zone"),
                    SessionType.LIVE,
                    new Identifier("Test Session Name"),
                    InteractionType.REQUEST,
                    new UOctet((short) 1),
                    currentTransactionId,
                    serviceArea,
                    new UShort(1),
                    new UShort(1),
                    new UOctet((short) 1),
                    false,
                    clientProperties,
                    elements.toArray()));
        }
        clientEndpoint.sendMessages(malMessages.toArray(new MALMessage[0]));
    }

    /**
     * Generate 30 MAL invoke messages.
     * Each of them expects an Http error which was translated to MAL error by client side
     *
     * @throws MALException              MAL & encoding errors
     * @throws MALTransmitErrorException Transport errors
     */
    @Test
    public void invokeActionTest01() throws MALException, MALTransmitErrorException {
        doAnswer(invocationOnMock -> {
            System.out.println("server MAL Message Listener onMessage");
            Object[] arguments = invocationOnMock.getArguments();
            if (arguments != null && arguments.length == 2 && arguments[0] != null && arguments[1] != null) {
                HttpEndpoint paramEndpoint = (HttpEndpoint) arguments[0];
                GENMessage incomingMessage = (GENMessage) arguments[1];
                Long transactionId = transactionIds.poll();
                Assert.assertEquals(transactionId, incomingMessage.getHeader().getTransactionId());
                Assert.assertEquals(paramEndpoint, serverEndpoint);
                printIncomingMessage(incomingMessage);
                if (HttpTransportHelper.isDefaultReply(incomingMessage.getHeader().getInteractionType(),
                        incomingMessage.getHeader().getInteractionStage())) {
                    System.out.println("No Reply is necessary. ");
                    Assert.assertTrue(false);
                } else {
                    System.out.println("Reply is required. Replying Ack now.");
                    Assert.assertTrue(true);
                    incomingTransactionIds.offer(transactionId);
                    throw new MALException("Some error");
                }
            } else {
                Assert.assertTrue(false);
            }
            return null;
        }).when(serverListener).onMessage(any(), any());
        serverEndpoint.setMessageListener(serverListener);

        clientProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        clientProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(getAvailablePort()).toString());
        generateInvokeOp();

        clientTransport = (HttpTransport) factory.createTransport(null, clientProperties);
        clientEndpoint = (HttpEndpoint) clientTransport.createEndpoint("Service1", clientProperties);
        clientEndpoint.setMessageListener(clientListener);
        for (int i = 0; i < messageLength; i++) {
            currentTransactionId = random.nextLong();
            transactionIds.offer(currentTransactionId);
            malMessages.add(clientEndpoint.createMessage(new Blob(new byte[]{1, 2, 3}),
                    new URI(targetURI),
                    new Time(Calendar.getInstance().getTimeInMillis()),
                    QoSLevel.ASSURED,
                    new UInteger(1L),
                    new IdentifierList(),
                    new Identifier("Test Zone"),
                    SessionType.LIVE,
                    new Identifier("Test Session Name"),
                    InteractionType.INVOKE,
                    new UOctet((short) 1),
                    currentTransactionId,
                    serviceArea,
                    new UShort(1),
                    new UShort(1),
                    new UOctet((short) 1),
                    false,
                    clientProperties,
                    elements.toArray()));
        }
        clientEndpoint.sendMessages(malMessages.toArray(new MALMessage[0]));
    }
}
