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
import esa.mo.mal.transport.gen.GENMessageHeader;
import esa.mo.mal.transport.gen.body.GENMessageBody;
import nasa.mo.mal.transport.http.receivers.IncomingMessageProcessor;
import nasa.mo.mal.transport.http.util.Constants;
import nasa.mo.mal.transport.http.util.MALTransmitErrorBuilder;
import org.apache.commons.io.FileUtils;
import org.ccsds.moims.mo.mal.*;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import static org.mockito.Mockito.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * @author wphyo
 *         Created on 7/5/17.
 */
public class HttpTransportTest {
    private static HttpTransport transport;
    private static Map<String, String> properties;
    @Mock
    private GENMessage message;
    @Mock
    private GENMessageHeader header;
    @Mock
    private GENMessageBody body;
    @Mock
    private ExecutorService service;
    @Mock
    private MALMessageListener listener;
    @Mock
    private static HttpTransportFactoryImpl factory;
    @Mock
    private static CustomStreamFactory streamFactory;
    @Mock
    private HttpMiniServer server;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();private ByteArrayOutputStream loggerContent;

    private static final Blob authenticationId = new Blob(new byte[]{1, 2, 3});
    private static final UOctet interactionStage = new UOctet((short) 1);
    private static final Long transactionId = 1L;
    private static final UShort uShort = new UShort(1);
    private static final UOctet uOctet = new UOctet((short) 1);
    private static final Identifier identifier = new Identifier("");
    private static final IdentifierList identifiers = new IdentifierList();
    private static final UInteger uInteger = new UInteger(1L);
    private static final QoSLevel qoSLevel = QoSLevel.ASSURED;
    private static final SessionType sessionType = SessionType.LIVE;
    private static final Boolean aBoolean = false;
    private static final Time time = new Time(1);
    private static final URI uriFrom = new URI("malhttp://127.0.0.1:8888/Service1");

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        Field field = MALElementStreamFactory.class.getDeclaredField("_FACTORY_MAP");
        field.setAccessible(true);
        System.setProperty("org.ccsds.moims.mo.mal.encoding.protocol.malhttp", "nasa.mo.mal.transport.http.CustomStreamFactory");
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        HttpTransport.LOGGER.addHandler(consoleHandler);
        HttpTransport.LOGGER.setLevel(Level.FINE);
        properties = new HashMap<>();
        properties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        properties.put(Constants.SERVER_PORT_KEY, "8888");
        properties.put(Constants.HTTP_DESTINATION_ENDPOINT_KEY, "malhttp://127.0.0.1:8888/Service1");

    }

    @Before
    public void setUp() throws Exception {
        transport = new HttpTransport("malhttp", '/', false, false, factory, properties);
        transport.init();
        Field serverField = transport.getClass().getDeclaredField("server");
        serverField.setAccessible(true);
        HttpMiniServer miniServer = (HttpMiniServer) serverField.get(transport);
        miniServer.stop();
        serverField.set(transport, server);
    }

    @After
    public void tearDown() throws MALException {
        transport.close();
    }

    /**
     * Calling method with null message.
     * expect error
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void sendMessageTest01() throws Exception {
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Null Http Message");
        transport.sendMessage(null, true, null);
    }

    /**
     * Calling method with null message.
     * expect error
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void sendHttpMessageTest01() throws Exception {
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Null Http Message");
        transport.sendMessage(null, true, null);
    }

    /**
     * Calling method with message with null header.
     * expect error
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void sendHttpMessageTest02() throws Exception {
        doReturn(null).when(message).getHeader();
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("MAL header has null values");
        transport.sendMessage(null, true, message);;
    }

    /**
     * Calling method with message with header with null values.
     * expect error
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void sendHttpMessageTest03() throws Exception {
        doReturn(header).when(message).getHeader();
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("MAL header has null values");
        transport.sendMessage(null, true, message);;
    }

    /**
     * Calling method with message with header with NOT-NULL values.
     * But URL is not valid.
     * expect error
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void sendHttpMessageTest04() throws Exception {

        URI uriFrom = new URI("http://127.0.256.1:8080/client09");
        prepHeader();
        doReturn(uriFrom).when(header).getURIFrom();
        doReturn(uriFrom).when(header).getURITo();

        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("URI format is not valid");
        transport.sendMessage(null, true, message);;
    }

    /**
     * Internal Sent Message.
     * Expecitng error when Executive-Service throws IOException
     *
     * @throws Exception other unexpected exception
     */
    @Test
    public void sendHttpMessageInternalTest01() throws Exception {
        transport.createEndpoint("Service1", properties);
        Field field = transport.getClass().getDeclaredField("incomingMessageProcessors");
        ExecutorService incomingMessageProcessors = mock(ExecutorService.class);
        field.setAccessible(true);
        field.set(transport, incomingMessageProcessors);
        doThrow(IOException.class).when(incomingMessageProcessors).submit(any(IncomingMessageProcessor.class));

        prepHeader();

        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("java.io.IOException");

        transport.sendMessage(null, true, message);;
    }

    /**
     * Internal Sent Message.
     * Expecitng error when Executive-Service throws RejectedExecutionException
     *
     * @throws Exception other unexpected exception
     */
    @Test
    public void sendHttpMessageInternalTest02() throws Exception {
        transport.createEndpoint("Service1", properties);
        Field field = transport.getClass().getDeclaredField("incomingMessageProcessors");
        ExecutorService incomingMessageProcessors = mock(ExecutorService.class);
        field.setAccessible(true);
        field.set(transport, incomingMessageProcessors);
        doThrow(RejectedExecutionException.class).when(incomingMessageProcessors).submit(any(IncomingMessageProcessor.class));

        prepHeader();

        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("java.util.concurrent.RejectedExecutionException");

        transport.sendMessage(null, true, message);;
    }
    /**
     * Calling method with message with header with NOT-NULL values. & URL is valid.
     * It is Internal Send.
     * Using Reflection is get the Executor Service & shut it down.
     * expecting thrown error with correct message
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void sendHttpMessageInternalTest03() throws Exception {
        transport.createEndpoint("Service1", properties);

        Field field = transport.getClass().getDeclaredField("incomingMessageProcessors");
        field.setAccessible(true);
        ExecutorService incomingMessageProcessors = (ExecutorService) field.get(transport);
        incomingMessageProcessors.shutdown();

        prepHeader();

        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Executor Service: incomingMessageProcessors is closed.");

        transport.sendMessage(null, true, message);;
    }


    /**
     * Calling method with message with header with NOT-NULL values. & URL is valid.
     * It is Internal Send.
     * Success
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void sendHttpMessageInternalTest04() throws Exception {
        doReturn(body).when(message).getBody();
        transport.createEndpoint("Service1", properties);
        prepHeader();

        HttpEndpoint endpoint = (HttpEndpoint) transport.getEndpoint(header.getURITo());
        endpoint.setMessageListener(listener);
        transport.sendMessage(null, true, message);;
    }

    /**
     * Using valid message to test sending externally.
     * Executor will throw an error since encoder throws an error.
     * Expecting exception
     *
     * @throws Exception any other unexpected exception
     */
    @Test
    public void sendHttpMessageExternalTest01() throws Exception {
        doReturn(body).when(message).getBody();
        doThrow(MALException.class).when(message).encodeMessage(any(CustomStreamFactory.class), any(MALElementOutputStream.class), any(ByteArrayOutputStream.class), eq(false));
        transport.createEndpoint("Service1", properties);
        URI uriFrom = new URI("malhttp://127.0.0.1:8889/Service1");
        prepHeader();
        doReturn(uriFrom).when(header).getURIFrom();
        doReturn(uriFrom).when(header).getURITo();

        HttpEndpoint endpoint = (HttpEndpoint) transport.getEndpoint(header.getURITo());
        endpoint.setMessageListener(listener);
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("org.ccsds.moims.mo.mal.MALException");
        transport.sendMessage(null, true, message);;
    }

    /**
     * Using valid message to test sending externally.
     * Executor will throw an error since encoder throws an error.
     * Expecting exception
     *
     * @throws Exception any other unexpected exception
     */
    @Test
    public void sendHttpMessageExternalTest04() throws Exception {
        doReturn(body).when(message).getBody();
        doThrow(NullPointerException.class).when(message).encodeMessage(any(CustomStreamFactory.class), any(MALElementOutputStream.class), any(ByteArrayOutputStream.class), eq(false));
        transport.createEndpoint("Service1", properties);
        URI uriFrom = new URI("malhttp://127.0.0.1:8889/Service1");
        prepHeader();
        doReturn(uriFrom).when(header).getURIFrom();
        doReturn(uriFrom).when(header).getURITo();

        HttpEndpoint endpoint = (HttpEndpoint) transport.getEndpoint(header.getURITo());
        endpoint.setMessageListener(listener);
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("java.lang.NullPointerException");
        transport.sendMessage(null, true, message);;
    }

    /**
     * Testing correct response message.
     * Expecting error since there is no stored message in the map to return to.
     *
     * @throws Exception any other unexpected exception
     */
    @Test
    public void sendHttpMessageExternalTest02() throws Exception {
        doReturn(body).when(message).getBody();
        transport.createEndpoint("Service1", properties);
        URI uriFrom = new URI("malhttp://127.0.0.1:8889/Service1");

        UOctet interactionStage = new UOctet((short) 2);
        prepHeader();
        doReturn(uriFrom).when(header).getURIFrom();
        doReturn(uriFrom).when(header).getURITo();
        doReturn(interactionStage).when(header).getInteractionStage();

        HttpEndpoint endpoint = (HttpEndpoint) transport.getEndpoint(header.getURITo());
        endpoint.setMessageListener(listener);
        transport.sendMessage(null, true, message);;
    }


    /**
     * Testing correct response message.
     * Expecting error since there is no stored message in the map to return to.
     *
     * @throws Exception any other unexpected exception
     */
    @Test
    public void sendHttpMessageExternalTest03() throws Exception {
        doReturn(body).when(message).getBody();
        transport.createEndpoint("Service1", properties);
        URI uriFrom = new URI("malhttp://127.0.0.1:8889/Service1");

        Field isClientOnlyField = transport.getClass().getDeclaredField("isClientOnly");
        isClientOnlyField.setAccessible(true);
        isClientOnlyField.set(transport, true);
        UOctet interactionStage = new UOctet((short) 2);
        prepHeader();
        doReturn(uriFrom).when(header).getURIFrom();
        doReturn(uriFrom).when(header).getURITo();
        doReturn(interactionStage).when(header).getInteractionStage();

        HttpEndpoint endpoint = (HttpEndpoint) transport.getEndpoint(header.getURITo());
        endpoint.setMessageListener(listener);
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("This is client only transport. Responding message is not valid");
        transport.sendMessage(null, true, message);
    }

    /**
     * Testing getRootURI method in GENTransport.
     * if it is compatible with http
     * Testing:
     * <p>
     * 1.   http
     * 2.   https
     * 3.   IPv4
     * 4.   IPv6
     * 5.   domain name
     * 6.   default port
     * 7.   custom port
     * 8.   URL with path
     * 9.   URL with path & parameters
     * 10.  empty text
     *
     * @throws MALException No exception
     */
    @Test
    public void getRootURITest01() throws MALException {
        Assert.assertEquals(transport.getRootURI(""), "");
        Assert.assertEquals(transport.getRootURI("http://localhost/"), "http://localhost");
        Assert.assertEquals(transport.getRootURI("https://localhost/"), "https://localhost");
        Assert.assertEquals(transport.getRootURI("http://localhost:1800/"), "http://localhost:1800");
        Assert.assertEquals(transport.getRootURI("https://localhost:1800/"), "https://localhost:1800");
        Assert.assertEquals(transport.getRootURI("https://localhost:1800/TestApplication"), "https://localhost:1800");
        Assert.assertEquals(transport.getRootURI("https://localhost:1800/TestApplication?id=test"), "https://localhost:1800");
        Assert.assertEquals(transport.getRootURI("http://[2001:0db8:85a3:0000:0000:8a2e:0370:7334]:1800/"),
                "http://[2001:0db8:85a3:0000:0000:8a2e:0370:7334]:1800");
        Assert.assertEquals(transport.getRootURI("https://[2001:db8:85a3:8d3:1319:8a2e:370:7348]:1800/"),
                "https://[2001:db8:85a3:8d3:1319:8a2e:370:7348]:1800");
    }

    /**
     * Testing getRootURI method in GENTransport.
     * TODO vulnerability when passing Null string in GENTransport
     *
     * @throws MALException NULL
     */
    @Test(expected = NullPointerException.class)
    public void getRootURITest02() throws MALException {
        transport.getRootURI(null);
    }

    @Test
    public void getRoutingPartTest01() throws MALException {
        Assert.assertEquals(transport.getRoutingPart(""), "");
        Assert.assertEquals(transport.getRoutingPart("http://localhost/"), "");
        Assert.assertEquals(transport.getRoutingPart("https://localhost/"), "");
        Assert.assertEquals(transport.getRoutingPart("http://localhost:1800/"), "");
        Assert.assertEquals(transport.getRoutingPart("https://localhost:1800/"), "");
        Assert.assertEquals(transport.getRoutingPart("https://localhost:1800/TestApplication"), "TestApplication");
        Assert.assertEquals(transport.getRoutingPart("https://localhost:1800/TestApplication?id=test"), "TestApplication?id=test");
        Assert.assertEquals(transport.getRoutingPart("http://[2001:0db8:85a3:0000:0000:8a2e:0370:7334]:1800/TestApplication"),
                "TestApplication");
        Assert.assertEquals(transport.getRoutingPart("https://[2001:db8:85a3:8d3:1319:8a2e:370:7348]:1800/TestApplication?id=test"),
                "TestApplication?id=test");
        Assert.assertEquals(transport.getRoutingPart("http://[2001:0db8:85a3:0000:0000:8a2e:0370:7334]:1800/TestApplication"),
                "TestApplication");
        Assert.assertEquals(transport.getRoutingPart("https://[2001:db8:85a3:8d3:1319:8a2e:370:7348]:1800/TestApplication?id=test&name=test#EndOfPage"),
                "TestApplication?id=test&name=test#EndOfPage");
        Assert.assertEquals(transport.getRoutingPart("http://[2001:0db8:85a3:0000:0000:8a2e:0370:7334]:1800/TestApplication/TestService"),
                "TestApplication/TestService");
        Assert.assertEquals(transport.getRoutingPart("https://[2001:db8:85a3:8d3:1319:8a2e:370:7348]:1800/TestApplication/TestService?id=test"),
                "TestApplication/TestService?id=test");
    }

    /**
     * Testing getRootURI method in GENTransport.
     * TODO vulnerability when passing Null string in GENTransport
     *
     * @throws MALException NULL
     */
    @Test(expected = NullPointerException.class)
    public void getRoutingPartTest02() throws MALException {
        transport.getRoutingPart(null);
    }

    @Test
    public void resultLoggerTest01() throws Exception {
        StreamHandler handler = getStreamHandler();
        Class<?> resultLoggerClass = HttpTransport.class.getDeclaredClasses()[0];
        Constructor<?> resultLoggerConstructor = resultLoggerClass.getDeclaredConstructors()[0];
        resultLoggerConstructor.setAccessible(true);
        Runnable newObj = (Runnable) resultLoggerConstructor.newInstance(transport, getSuccessFuture(), "Test Class");
        newObj.run();
        handler.flush();
        Assert.assertTrue(new String(loggerContent.toByteArray()).isEmpty());
    }

    @Test
    public void resultLoggerTest02() throws Exception {
        StreamHandler handler = getStreamHandler();
        Class<?> resultLoggerClass = HttpTransport.class.getDeclaredClasses()[0];
        Constructor<?> resultLoggerConstructor = resultLoggerClass.getDeclaredConstructors()[0];
        resultLoggerConstructor.setAccessible(true);
        Runnable newObj = (Runnable) resultLoggerConstructor.newInstance(transport, getFailedFuture(), "Test Class");
        newObj.run();
        handler.flush();
        Assert.assertTrue(new String(loggerContent.toByteArray()).contains("Executor Service failed @ Test Class"));
    }

    private StreamHandler getStreamHandler() {
        loggerContent = new ByteArrayOutputStream();
        StreamHandler handler = new StreamHandler(new PrintStream(loggerContent), new SimpleFormatter());
        handler.setLevel(Level.FINE);
        HttpTransport.LOGGER.addHandler(handler);
        return handler;
    }

    @Test
    public void resultLoggerTest03() throws Exception {
        StreamHandler handler = getStreamHandler();
        Class<?> resultLoggerClass = HttpTransport.class.getDeclaredClasses()[0];
        Constructor<?> resultLoggerConstructor = resultLoggerClass.getDeclaredConstructors()[0];
        resultLoggerConstructor.setAccessible(true);
        Runnable newObj = (Runnable) resultLoggerConstructor.newInstance(transport, getExceptionFuture(), "Test Class");
        newObj.run();
        handler.flush();
        Assert.assertTrue(new String(loggerContent.toByteArray()).contains("Executor Service throws an error"));
    }

    @Test
    public void isSupportedInteractionTypeTest01() {
        Assert.assertFalse(transport.isSupportedInteractionType(InteractionType.PUBSUB));
        Assert.assertTrue(transport.isSupportedInteractionType(InteractionType.SEND));
        Assert.assertTrue(transport.isSupportedInteractionType(InteractionType.SUBMIT));
        Assert.assertTrue(transport.isSupportedInteractionType(InteractionType.REQUEST));
        Assert.assertTrue(transport.isSupportedInteractionType(InteractionType.INVOKE));
        Assert.assertTrue(transport.isSupportedInteractionType(InteractionType.PROGRESS));
    }

    @Test
    public void isSupportedQoSLevelTest01() {
        Assert.assertTrue(transport.isSupportedQoSLevel(QoSLevel.ASSURED));
        Assert.assertTrue(transport.isSupportedQoSLevel(QoSLevel.BESTEFFORT));
        Assert.assertTrue(transport.isSupportedQoSLevel(QoSLevel.QUEUED));
        Assert.assertTrue(transport.isSupportedQoSLevel(QoSLevel.TIMELY));
    }

    @Test
    public void createBrokerTest01() throws Exception {
        StreamHandler handler = getStreamHandler();
        Assert.assertEquals(null, transport.createBroker("", null, null, null, null));
        handler.flush();
        Assert.assertTrue(new String(loggerContent.toByteArray()).contains("SEVERE: attempting to create broker in Http Transport. UNSUPPORTED"));
    }

    @Test
    public void createBrokerTest02() throws Exception {
        StreamHandler handler = getStreamHandler();
        Assert.assertEquals(null, transport.createBroker(mock(MALEndpoint.class), null, null, null, null));
        handler.flush();
        Assert.assertTrue(new String(loggerContent.toByteArray()).contains("SEVERE: attempting to create broker in Http Transport. UNSUPPORTED"));
    }

    @Test
    public void createMessageTest01() throws Exception {
        thrown.expect(NullPointerException.class);
        transport.createMessage("This is a test String".getBytes());
    }

    @Test
    public void createMessageSenderTest01() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Http Transport is not using GENMessageSender");
        transport.createMessageSender(message, "");
    }

    @Test
    public void internalEncodeMessageTest01() throws Exception {
        Method internalEncodeMessageMethod = transport.getClass().getDeclaredMethod("internalEncodeMessage", String.class, String.class, Object.class, boolean.class, String.class, GENMessage.class);
        internalEncodeMessageMethod.setAccessible(true);
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Not supported for Http Transport");
        transport.internalEncodeMessage("", "", new Object(), false, "", null);
    }

    @Test
    public void createMessasgeTest02() throws Exception {
        thrown.expect(NullPointerException.class);
        transport.createMessage("This is a test String".getBytes(), new GENMessageHeader());
    }

    /**
     * Checking isSecureServer Field of Transport when
     *
     * 1.   org.ccsds.moims.mo.mal.transport.http.isssl is not set
     * 2.   org.ccsds.moims.mo.mal.transport.http.isssl is false
     * 3.   org.ccsds.moims.mo.mal.transport.http.isssl is False
     * 4.   org.ccsds.moims.mo.mal.transport.http.isssl is empty
     * 5.   org.ccsds.moims.mo.mal.transport.http.isssl is true
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void isSSLPropertyTest01() throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        properties.put(Constants.SERVER_PORT_KEY, "8888");
        properties.put(Constants.HTTP_DESTINATION_ENDPOINT_KEY, "malhttp://127.0.0.1:8888/Service1");

        HttpTransport transport = new HttpTransport("malhttp", '/', false, false, factory, properties);
        Field isSecureServerField = transport.getClass().getDeclaredField("isSecureServer");
        isSecureServerField.setAccessible(true);
        Assert.assertFalse((boolean) isSecureServerField.get(transport));

        properties.put(Constants.IS_SSL_KEY, "false");
        transport = new HttpTransport("malhttp", '/', false, false, factory, properties);
        isSecureServerField = transport.getClass().getDeclaredField("isSecureServer");
        isSecureServerField.setAccessible(true);
        Assert.assertFalse((boolean) isSecureServerField.get(transport));

        properties.put(Constants.IS_SSL_KEY, "False");
        transport = new HttpTransport("malhttp", '/', false, false, factory, properties);
        isSecureServerField = transport.getClass().getDeclaredField("isSecureServer");
        isSecureServerField.setAccessible(true);
        Assert.assertFalse((boolean) isSecureServerField.get(transport));

        properties.put(Constants.IS_SSL_KEY, "");
        transport = new HttpTransport("malhttp", '/', false, false, factory, properties);
        isSecureServerField = transport.getClass().getDeclaredField("isSecureServer");
        isSecureServerField.setAccessible(true);
        Assert.assertFalse((boolean) isSecureServerField.get(transport));

        properties.put(Constants.IS_SSL_KEY, "true");
        transport = new HttpTransport("malhttp", '/', false, false, factory, properties);
        isSecureServerField = transport.getClass().getDeclaredField("isSecureServer");
        isSecureServerField.setAccessible(true);
        Assert.assertTrue((boolean) isSecureServerField.get(transport));
    }

    /**
     * Creating new Transport with all properties for SSL keystores, but setting isSSL to false
     * It should create plain http and all keystore fields are null.
     *
     * @throws Exception unexpected Exception
     */
    @Test
    public void isSSLPropertyTest02() throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        properties.put(Constants.SERVER_PORT_KEY, "8888");
        properties.put(Constants.HTTP_DESTINATION_ENDPOINT_KEY, "malhttp://127.0.0.1:8888/Service1");
        properties.put(Constants.IS_SSL_KEY, "false");
        properties.put(Constants.KEY_STORE_FILE_KEY, "test");
        properties.put(Constants.KEY_STORE_TYPE_KEY, "test");
        properties.put(Constants.KEY_STORE_PWD_FILE_KEY, "");
        properties.put(Constants.TRUST_STORE_PWD_FILE_KEY, "");
        properties.put(Constants.TRUST_STORE_FILE_KEY, "test");
        HttpTransport transport = new HttpTransport("malhttp", '/', false, false, factory, properties);
        transport.init();
        Field serverField = transport.getClass().getDeclaredField("server");
        serverField.setAccessible(true);
        HttpMiniServer server = (HttpMiniServer) serverField.get(transport);
        Assert.assertTrue(server != null);
        Field field = server.getClass().getDeclaredField("keyStoreFile");
        field.setAccessible(true);
        Assert.assertEquals(null, field.get(server));

        field = server.getClass().getDeclaredField("trustStoreFile");
        field.setAccessible(true);
        Assert.assertEquals(null, field.get(server));

        field = server.getClass().getDeclaredField("keyStoreType");
        field.setAccessible(true);
        Assert.assertEquals(null, field.get(server));

        transport.close();
    }

    /**
     * Creating new Transport with all properties for SSL keystores, but setting isSSL to true
     * Expecting error as keystore files don't exist. that means it is creating SSL server
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void isSSLPropertyTest03() throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        properties.put(Constants.SERVER_PORT_KEY, "8888");
        properties.put(Constants.HTTP_DESTINATION_ENDPOINT_KEY, "malhttp://127.0.0.1:8888/Service1");
        properties.put(Constants.IS_SSL_KEY, "true");
        properties.put(Constants.KEY_STORE_FILE_KEY, "test");
        properties.put(Constants.KEY_STORE_TYPE_KEY, "test");
        properties.put(Constants.KEY_STORE_PWD_FILE_KEY, "");
        properties.put(Constants.TRUST_STORE_PWD_FILE_KEY, "");
        properties.put(Constants.TRUST_STORE_FILE_KEY, "test");
        HttpTransport transport = new HttpTransport("malhttp", '/', false, false, factory, properties);
        thrown.expect(MALException.class);
        thrown.expectMessage("Error while creating new SSL Context");
        transport.init();
        transport.close();
    }

    /**
     * Testing if the keys are present in map, they are assigned to correct fields & translating correctly.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void captureInfoTest01() throws Exception {
        java.io.File tempFile = tempFolder.newFile("store.pass");
        FileUtils.writeStringToFile(tempFile, "Test3");

        java.io.File tempFile1 = tempFolder.newFile("store1.pass");
        FileUtils.writeStringToFile(tempFile1, "Test5");

        properties.put(Constants.KEY_STORE_TYPE_KEY, "Test1");
        properties.put(Constants.TRUST_STORE_FILE_KEY, "Test2");
        properties.put(Constants.TRUST_STORE_PWD_FILE_KEY, tempFile.getAbsolutePath());
        properties.put(Constants.KEY_STORE_FILE_KEY, "Test4");
        properties.put(Constants.KEY_STORE_PWD_FILE_KEY, tempFile1.getAbsolutePath());
        properties.put(Constants.SOCKET_BACKLOG_KEY, "999");
        properties.put(Constants.THREAD_POOL_KEY, "888");

        transport = new HttpTransport("malhttp", '/', false, false, factory, properties);
        Field threadPoolKeyField = transport.getClass().getDeclaredField("newFixedThreadPool");
        Field socketField = transport.getClass().getDeclaredField("socketBacklog");

        threadPoolKeyField.setAccessible(true);

        socketField.setAccessible(true);


        Assert.assertEquals("Test1", transport.getKeyStoreType());
        Assert.assertEquals("Test2", transport.getTrustStoreFile());
        Assert.assertEquals("Test3", transport.getTrustStorePassword());
        Assert.assertEquals("Test4", transport.getKeyStoreFile());
        Assert.assertEquals("Test5", transport.getKeyStorePassword());
        Assert.assertEquals(999, socketField.get(transport));
        Assert.assertEquals(888, threadPoolKeyField .get(transport));

        properties.remove(Constants.KEY_STORE_TYPE_KEY);
        properties.remove(Constants.TRUST_STORE_FILE_KEY);
        properties.remove(Constants.TRUST_STORE_PWD_FILE_KEY);
        properties.remove(Constants.KEY_STORE_FILE_KEY);
        properties.remove(Constants.KEY_STORE_PWD_FILE_KEY);
        properties.remove(Constants.SOCKET_BACKLOG_KEY);
        properties.remove(Constants.THREAD_POOL_KEY);
    }

    /**
     * If port is not configured, using port number from constant file.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void missingServerPortTest01() throws Exception {
        properties.remove(Constants.SERVER_PORT_KEY);
        transport = new HttpTransport("malhttp", '/', false, false, factory, properties);
        transport.init();
        Field serverPortField = transport.getClass().getDeclaredField("serverPort");
        serverPortField.setAccessible(true);
        Assert.assertEquals(Constants.DEFAULT_SERVER_PORT, serverPortField.get(transport));

        properties.put(Constants.SERVER_PORT_KEY, "8888");

    }

    private Future<Boolean> getSuccessFuture() {
        return new Future<Boolean>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public Boolean get() throws InterruptedException, ExecutionException {
                return true;
            }

            @Override
            public Boolean get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return true;
            }
        };
    }

    private Future<Boolean> getFailedFuture() {
        return new Future<Boolean>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public Boolean get() throws InterruptedException, ExecutionException {
                return false;
            }

            @Override
            public Boolean get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return false;
            }
        };
    }

    private Future<Boolean> getExceptionFuture() {
        return new Future<Boolean>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public Boolean get() throws InterruptedException, ExecutionException {
                throw new ExecutionException(MALTransmitErrorBuilder.create().build());
            }

            @Override
            public Boolean get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                throw new ExecutionException(MALTransmitErrorBuilder.create().build());
            }
        };
    }

    private void prepHeader() {
        doReturn(header).when(message).getHeader();
        doReturn(authenticationId).when(header).getAuthenticationId();
        doReturn(uriFrom).when(header).getURIFrom();
        doReturn(uriFrom).when(header).getURITo();
        doReturn(time).when(header).getTimestamp();
        doReturn(qoSLevel).when(header).getQoSlevel();
        doReturn(uInteger).when(header).getPriority();
        doReturn(identifiers).when(header).getDomain();
        doReturn(identifier).when(header).getNetworkZone();
        doReturn(sessionType).when(header).getSession();
        doReturn(identifier).when(header).getSessionName();
        doReturn(InteractionType.REQUEST).when(header).getInteractionType();
        doReturn(interactionStage).when(header).getInteractionStage();
        doReturn(transactionId).when(header).getTransactionId();
        doReturn(uShort).when(header).getServiceArea();
        doReturn(uShort).when(header).getService();
        doReturn(uShort).when(header).getOperation();
        doReturn(uOctet).when(header).getAreaVersion();
        doReturn(aBoolean).when(header).getIsErrorMessage();
    }
}
