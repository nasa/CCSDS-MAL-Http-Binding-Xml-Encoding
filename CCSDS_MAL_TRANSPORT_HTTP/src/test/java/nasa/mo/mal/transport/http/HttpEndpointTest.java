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
import nasa.mo.mal.transport.http.util.MALTransmitErrorBuilder;
import org.ccsds.moims.mo.mal.*;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.ccsds.moims.mo.mal.transport.MALTransmitMultipleErrorException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * @author wphyo
 *         Created on 7/14/17.
 * HttpEndpoint-Test cases for send & receive methods
 */
public class HttpEndpointTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private HttpEndpoint endpoint;
    @Mock
    private HttpTransport transport;
    @Mock
    private GENMessage message;
    @Mock
    private GENMessage failedMessage;
    @Mock
    private MALMessageListener listener;
    @Mock
    private MALElementStreamFactory factory;

    protected URI uri = new URI("http://test:8080/");
    protected Blob auth = new Blob(new byte[] {1,2, 3});
    protected UShort uShort = new UShort(1);
    protected UOctet uOctet = new UOctet((short) 1);
    protected UInteger uInteger = new UInteger(1L);
    protected Time time = new Time(987654321234567898L);
    protected static Identifier identifier = new Identifier("Test Identifier");
    protected static IdentifierList identifiers = new IdentifierList();

    private static short serviceAreaNumber = 5678;
    @BeforeClass
    public static void oneTimeSetUp() {
        identifiers.add(identifier);
    }
    @Before
    public void setUp() throws Exception {
        endpoint = new HttpEndpoint(transport, "TestService", "TestService", "http://127.0.0.1:8888/TestService", false);
    }

    /**
     * Endpoint with NOT Http-Transport
     * expect to throw an error about wrong Instance.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void sendMessageTest01() throws Exception {
        HttpEndpoint singleEndpoint = new HttpEndpoint(null, "", "", "", false);
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Wrong Transport Instance");
        singleEndpoint.sendMessage(message);
    }

    /**
     * Endpoint sending a message, and gets a false flag.
     * expecting an error to say the message has failed to send.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void sendMessageTest03() throws Exception {
        errorReturn();
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Some Error");
        endpoint.sendMessage(failedMessage);
    }

    /**
     * Verifying error when sendMessage() throws an error
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void sendMessageTest04() throws Exception {
        doThrow(MALTransmitErrorBuilder.create().build()).when(transport).sendMessage(null, true, message);
        thrown.expect(MALTransmitErrorException.class);
        endpoint.sendMessage(message);
    }

    /**
     * Endpoint sending a null message.
     * expecting an error
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void sendMessageTest06() throws Exception {
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Null MAL Message");
        endpoint.sendMessage(null);
    }

    /**
     * Call method with null parameter
     * expecting an error
     *
     * @throws Exception any unexpected error
     */
    @Test
    public void sendMessagesTest01() throws Exception {
        thrown.expect(MALTransmitMultipleErrorException.class);
        try {
            endpoint.sendMessages(null);
        } catch (MALTransmitMultipleErrorException exp) {
            Assert.assertEquals("Null MAL Msg array", exp.getTransmitExceptions()[0].getMessage());
            throw exp;
        }
    }

    /**
     * Call method with empty array
     * execute correctly.(not executing anything)
     *
     * @throws Exception any unexpected error
     */
    @Test
    public void sendMessagesTest02() throws Exception {
        endpoint.sendMessages(new GENMessage[0]);
    }

    /**
     * Call method with array with 1 message
     * execute correctly.
     *
     * @throws Exception any unexpected error
     */
    @Test
    public void sendMessagesTest03() throws Exception {
        GENMessage[] messages = new GENMessage[]{message};
        endpoint.sendMessages(messages);
        verify(transport, times(1)).sendMessage(null, true, message);
    }

    /**
     * Call method with array with multiple message
     * execute correctly.
     * calling sendMessage correct amount of time.
     *
     * @throws Exception any unexpected error
     */
    @Test
    public void sendMessagesTest04() throws Exception {
        int count = 100;
        GENMessage[] messages = new GENMessage[count];
        for (int i = 0; i < count; i++) {
            messages[i] = message;
        }
        endpoint.sendMessages(messages);
        verify(transport, times(count)).sendMessage(null, true, message);
    }


    /**
     * Call method with array with 1 message
     * send failed.
     * Checking exception:
     * 1.   exception type
     * 2.   exception count
     * 3.   exception message
     *
     * @throws Exception any unexpected error
     */
    @Test
    public void sendMessagesTest05() throws Exception {
        errorReturn();
        GENMessage[] messages = new GENMessage[]{failedMessage};
        thrown.expect(MALTransmitMultipleErrorException.class);
        try {
            endpoint.sendMessages(messages);
        } catch (MALTransmitMultipleErrorException exp) {
            Assert.assertEquals(1, exp.getTransmitExceptions().length);
            Assert.assertEquals("Some Error", exp.getTransmitExceptions()[0].getMessage());
            throw exp;
        }
        verify(transport, times(1)).sendMessage(null, true, failedMessage);
    }

    /**
     * Call method with array with multiple message
     * the last 2 failed. others succeeds.
     * Checking exception for failures:
     * 1.   exception type
     * 2.   exception count
     * 3.   exception message
     *
     * @throws Exception any unexpected error
     */
    @Test
    public void sendMessagesTest06() throws Exception {
        errorReturn();
        int count = 100;
        GENMessage[] messages = new GENMessage[count];
        for (int i = 0; i < count - 2; i++) {
            messages[i] = message;
        }
        messages[count - 1] = failedMessage;
        messages[count - 2] = failedMessage;
        thrown.expect(MALTransmitMultipleErrorException.class);
        try {
            endpoint.sendMessages(messages);
        } catch (MALTransmitMultipleErrorException exp) {
            Assert.assertEquals(2, exp.getTransmitExceptions().length);
            Assert.assertEquals("Some Error", exp.getTransmitExceptions()[0].getMessage());
            Assert.assertEquals("Some Error", exp.getTransmitExceptions()[1].getMessage());
            throw exp;
        }
        verify(transport, times(2)).sendMessage(null, true, failedMessage);
        verify(transport, times(count - 2)).sendMessage(null, true, message);
    }

    /**
     * Testing exceptions.
     * Failed messages will throw some error which is converted to Interaction Error
     * when passed to send multiple messages.
     *
     * @throws Exception any unexpected error
     */
    @Test
    public void sendMessagesTest07() throws Exception {
        exceptionReturn();
        int count = 100;
        GENMessage[] messages = new GENMessage[count];
        for (int i = 0; i < count - 2; i++) {
            messages[i] = message;
        }
        messages[count - 1] = failedMessage;
        messages[count - 2] = failedMessage;
        thrown.expect(MALTransmitMultipleErrorException.class);
        try {
            endpoint.sendMessages(messages);
            Assert.assertTrue(false);
        } catch (MALTransmitMultipleErrorException exp) {
            Assert.assertEquals(2, exp.getTransmitExceptions().length);
            String expectedMsg = "Testing Exception";
            Assert.assertEquals(expectedMsg, exp.getTransmitExceptions()[0].getMessage());
            Assert.assertEquals(expectedMsg, exp.getTransmitExceptions()[1].getMessage());
            throw exp;
        }
        verify(transport, times(2)).sendMessage(null, true, failedMessage);
        verify(transport, times(count - 2)).sendMessage(null, true, message);
    }

    /**
     * Testing exceptions
     * Failed messages will throw non Interaction Error which is coverted to Interaction Error
     * when passed to send multiple messages.
     *
     * @throws Exception any unexpected error
     */
    @Test
    public void sendMessagesTest08() throws Exception {
        exceptionReturn();
        int count = 100;
        GENMessage[] messages = new GENMessage[count];
        for (int i = 0; i < count - 2; i++) {
            messages[i] = message;
        }
        messages[count - 1] = failedMessage;
        messages[count - 2] = failedMessage;
        thrown.expect(MALTransmitMultipleErrorException.class);
        doThrow(new RuntimeException("Testing Runtime")).when(transport).sendMessage(null, true, failedMessage);
        try {
            endpoint.sendMessages(messages);
            Assert.assertTrue(false);
        } catch (MALTransmitMultipleErrorException exp) {
            Assert.assertEquals(2, exp.getTransmitExceptions().length);
            String expectedMsg = "java.lang.RuntimeException: Testing Runtime";
            Assert.assertEquals(expectedMsg, exp.getTransmitExceptions()[0].getMessage());
            Assert.assertEquals(expectedMsg, exp.getTransmitExceptions()[1].getMessage());
            throw exp;
        }
        verify(transport, times(2)).sendMessage(null, true, failedMessage);
        verify(transport, times(count - 2)).sendMessage(null, true, message);
    }

    /**
     * Testing unsupported exception for Receiving multiple messages
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void receiveMessagesTest01() throws Exception {
        thrown.expect(MALException.class);
        thrown.expectMessage("Not Supported for Http Transport Protocol");
        endpoint.receiveMessages(null);
    }

    /**
     * Passing null to receive single message method
     * expecting error
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void receiveMessageTest01() throws Exception {
        thrown.expect(MALException.class);
        thrown.expectMessage("Http Endpoint received a null MAL Message");
        endpoint.receiveMessage(null);
    }

    /**
     * Passing null to receive single message method
     * expecting error
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void receiveMessageTest02() throws Exception {
        thrown.expect(MALException.class);
        thrown.expectMessage("Null MAL Message Listener.");
        endpoint.receiveMessage(message);
    }

    /**
     * Passing null to receive single message method
     * expecting error
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void receiveMessageTest03() throws Exception {
        endpoint.setMessageListener(listener);
        endpoint.receiveMessage(message);
        verify(listener, times(1)).onMessage(endpoint, message);
    }

    /**
     * Implement Future object which returns false.
     *
     * @throws MALTransmitErrorException any error
     */
    private void errorReturn() throws MALTransmitErrorException {
        doThrow(MALTransmitErrorBuilder.create().setExtraInfo("Some Error").build()).when(transport).sendMessage(null, true, failedMessage);
    }

    /**
     * Implement Future object which returns false.
     *
     * @throws MALTransmitErrorException any error
     */
    private void exceptionReturn() throws MALTransmitErrorException {
        doThrow(MALTransmitErrorBuilder.create().setExtraInfo("Testing Exception").build()).when(transport).sendMessage(null, true, failedMessage);
    }

    /**
     * Testing success in Creating messages with all parameters provided & normal body element array
     *
     * @throws Exception unexpected error
     */
    @Test
    public void createMessageTest01() throws Exception {
        doReturn(factory).when(transport).getStreamFactory();
        UShort serviceArea = getServiceArea();
        Object[] shortForms = new Object[0];
        MALSendOperation sendOperation = new MALSendOperation(uShort, identifier, false, uShort,
                new MALOperationStage(uOctet, shortForms, shortForms));
        generateMALOperationSteps(serviceArea, sendOperation, identifier);
        GENMessage result = (GENMessage) endpoint.createMessage(auth, uri, time, QoSLevel.ASSURED, uInteger,
                identifiers, identifier, SessionType.LIVE, identifier, InteractionType.SEND, uOctet,
                uInteger.getValue(), serviceArea, uShort, uShort, uOctet, false, null);
        Assert.assertTrue(result != null);
        Assert.assertTrue(result.getBody().getElementCount() == 0);
        Assert.assertTrue(result.getHeader().getURIFrom().equals(endpoint.getURI()));
        Assert.assertTrue(result.getHeader().getURITo().equals(uri));
        Assert.assertTrue(result.getHeader().getInteractionType().equals(InteractionType.SEND));
        Assert.assertTrue(result.getHeader().getSession().equals(SessionType.LIVE));
        Assert.assertTrue(result.getHeader().getQoSlevel().equals(QoSLevel.ASSURED));
        Assert.assertTrue(result.getHeader().getIsErrorMessage().equals(false));
    }

    /**
     * Testing exception when something is failed.
     * in Creating messages with all parameters provided & normal body element array
     *
     * @throws Exception unexpected error
     */
    @Test
    public void createMessageTest02() throws Exception {
        doReturn(factory).when(transport).getStreamFactory();
        UShort serviceArea = getServiceArea();
        thrown.expect(MALException.class);
        thrown.expectMessage("Error in creating decoded message");
        endpoint.createMessage(auth, uri, time, QoSLevel.ASSURED, uInteger,
                identifiers, identifier, SessionType.LIVE, identifier, InteractionType.SEND, uOctet,
                uInteger.getValue(), serviceArea, uShort, uShort, uOctet, false, null);
    }

    /**
     * Testing exception when something is failed
     * in Creating messages with all parameters provided & MAL Encoded Body
     *
     * @throws Exception unexpected error
     */
    @Test
    public void createMessageTest03() throws Exception {
        doReturn(factory).when(transport).getStreamFactory();
        UShort serviceArea = getServiceArea();
        thrown.expect(MALException.class);
        thrown.expectMessage("Error in creating decoded message");
        endpoint.createMessage(auth, uri, time, QoSLevel.ASSURED, uInteger, identifiers, identifier,
                SessionType.LIVE, identifier, InteractionType.SEND, uOctet, uInteger.getValue(), serviceArea, uShort,
                uShort, uOctet, false, null, new MALEncodedBody(auth));
    }

    /**
     * Testing exception when something is failed
     * in Creating messages with all parameters provided & MAL Encoded Body
     *
     * @throws Exception unexpected error
     */
    @Test
    public void createMessageTest04() throws Exception {
        doReturn(factory).when(transport).getStreamFactory();
        UShort serviceArea = getServiceArea();
        Object[] shortForms = new Object[0];
        MALSendOperation sendOperation = new MALSendOperation(uShort, identifier, false, uShort,
                new MALOperationStage(uOctet, shortForms, shortForms));
        generateMALOperationSteps(serviceArea, sendOperation, identifier);
        GENMessage result = (GENMessage) endpoint.createMessage(auth, uri, time, QoSLevel.ASSURED, uInteger,
                identifiers, identifier, SessionType.LIVE, identifier, InteractionType.SEND, uOctet,
                uInteger.getValue(), serviceArea, uShort, uShort, uOctet, false, null, new MALEncodedBody(auth));
        Assert.assertTrue(result != null);
        Assert.assertTrue(result.getBody().getElementCount() == 1);
        Assert.assertTrue(result.getHeader().getURIFrom().equals(endpoint.getURI()));
        Assert.assertTrue(result.getHeader().getURITo().equals(uri));
        Assert.assertTrue(result.getHeader().getInteractionType().equals(InteractionType.SEND));
        Assert.assertTrue(result.getHeader().getSession().equals(SessionType.LIVE));
        Assert.assertTrue(result.getHeader().getQoSlevel().equals(QoSLevel.ASSURED));
        Assert.assertTrue(result.getHeader().getIsErrorMessage().equals(false));
    }

    /**
     * Testing success in Creating messages with some parameters provided & MAL Encoded Body
     *
     * @throws Exception unexpected error
     */
    @Test
    public void createMessageTest05() throws Exception {
        doReturn(factory).when(transport).getStreamFactory();
        UShort serviceArea = getServiceArea();
        Object[] shortForms = new Object[0];
        MALSendOperation sendOperation = new MALSendOperation(uShort, identifier, false, uShort,
                new MALOperationStage(uOctet, shortForms, shortForms));
        generateMALOperationSteps(serviceArea, sendOperation, identifier);
        GENMessage result = (GENMessage) endpoint.createMessage(auth, uri, time, QoSLevel.ASSURED, uInteger, identifiers, identifier, SessionType.LIVE, identifier, uInteger.getValue(), false, sendOperation, uOctet, null);
        Assert.assertTrue(result != null);
        Assert.assertTrue(result.getBody().getElementCount() == 0);
        Assert.assertTrue(result.getHeader().getURIFrom().equals(endpoint.getURI()));
        Assert.assertTrue(result.getHeader().getURITo().equals(uri));
        Assert.assertTrue(result.getHeader().getInteractionType().equals(InteractionType.SEND));
        Assert.assertTrue(result.getHeader().getSession().equals(SessionType.LIVE));
        Assert.assertTrue(result.getHeader().getQoSlevel().equals(QoSLevel.ASSURED));
        Assert.assertTrue(result.getHeader().getIsErrorMessage().equals(false));
    }

    /**
     * Testing success in Creating messages with some parameters provided & MAL Encoded Body
     *
     * @throws Exception unexpected error
     */
    @Test
    public void createMessageTest06() throws Exception {
        doReturn(factory).when(transport).getStreamFactory();
        UShort serviceArea = getServiceArea();
        Object[] shortForms = new Object[0];
        MALSendOperation sendOperation = new MALSendOperation(uShort, identifier, false, uShort,
                new MALOperationStage(uOctet, shortForms, shortForms));
        generateMALOperationSteps(serviceArea, sendOperation, identifier);
        GENMessage result = (GENMessage) endpoint.createMessage(auth, uri, time, QoSLevel.ASSURED, uInteger, identifiers, identifier, SessionType.LIVE, identifier, uInteger.getValue(), false, sendOperation, uOctet, null, new MALEncodedBody(auth));
        Assert.assertTrue(result != null);
        Assert.assertTrue(result.getBody().getElementCount() == 1);
        Assert.assertTrue(result.getHeader().getURIFrom().equals(endpoint.getURI()));
        Assert.assertTrue(result.getHeader().getURITo().equals(uri));
        Assert.assertTrue(result.getHeader().getInteractionType().equals(InteractionType.SEND));
        Assert.assertTrue(result.getHeader().getSession().equals(SessionType.LIVE));
        Assert.assertTrue(result.getHeader().getQoSlevel().equals(QoSLevel.ASSURED));
        Assert.assertTrue(result.getHeader().getIsErrorMessage().equals(false));
    }

    /**
     * Creating new service area numbers
     * @return Unsigned Short Service Area
     */
    private UShort getServiceArea() {
        return new UShort(serviceAreaNumber++);
    }

    /**
     * Creating MAL Operation and stored it to the Context Map
     * @param serviceArea Unsigned Short
     * @param operation MAL Operation
     * @param identifier Identifier
     * @throws MALException any exception
     */
    private void generateMALOperationSteps(UShort serviceArea, MALOperation operation, Identifier identifier)
            throws MALException {
        MALService submitService = new MALService(uShort, identifier);
        MALArea malArea = new MALArea(serviceArea, new Identifier(UUID.randomUUID().toString()), uOctet);
        submitService.addOperation(operation);
        malArea.addService(submitService);
        MALContextFactory.registerArea(malArea);
    }
}
