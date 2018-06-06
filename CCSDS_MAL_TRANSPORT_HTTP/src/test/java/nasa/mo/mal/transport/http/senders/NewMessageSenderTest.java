/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.http.senders;

import com.sun.net.httpserver.HttpServer;
import nasa.mo.mal.encoder.Header.HttpHeaderKeys;
import nasa.mo.mal.encoder.Header.MalToHttpMapper;
import esa.mo.mal.transport.gen.GENMessage;
import esa.mo.mal.transport.gen.GENMessageHeader;
import nasa.mo.mal.transport.http.HttpEndpoint;
import nasa.mo.mal.transport.http.HttpTransport;
import nasa.mo.mal.transport.http.MessageTestHelper;
import nasa.mo.mal.transport.junitcategories.PortDependentTest;
import org.apache.http.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * @author wphyo
 *         Created on 7/14/17.
 */
public class NewMessageSenderTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private static HttpServer server;
    @Mock
    private HttpTransport transport;
    @Mock
    private GENMessage malMessage;
    @Mock
    private GENMessageHeader header;
    @Mock
    private HttpEndpoint endpoint;
    @Mock
    private HttpResponse response;
    @Mock
    private StatusLine statusLine;
    @Mock
    private HttpEntity entity;
    private NewMessageSender sender;
    private Map<String, String> headers;
    private byte[] encodedMessage;

    private Blob authenticationId = new Blob(new byte[]{1, 2, 3});
    private UOctet interactionStage = new UOctet((short) 1);
    private Long transactionId = 1L;
    private UShort uShort = new UShort(1);
    private UOctet uOctet = new UOctet((short) 1);
    private Identifier identifier = new Identifier("");
    private IdentifierList identifiers = new IdentifierList();
    private UInteger uInteger = new UInteger(1L);
    private QoSLevel qoSLevel = QoSLevel.ASSURED;
    private SessionType sessionType = SessionType.LIVE;
    private Time time = new Time(1);
    private static int freePort;

    @BeforeClass
    public static void oneTimeSetup() throws Exception {
        freePort = MessageTestHelper.getAvailablePort();
        server = HttpServer.create(new InetSocketAddress(InetAddress.getByName("localhost"), freePort), 0);
        server.start();
    }

    @AfterClass
    public static void oneTimeTearDown() throws Exception {
        server.stop(0);
    }

    @Before
    public void setUp() throws Exception {
        encodedMessage = String.valueOf("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                "<malxml:Body xmlns:malxml=\"http://www.ccsds.org/schema/malxml/MAL\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>").getBytes();
        headers = new HashMap<>();
        headers.put(HttpHeaderKeys.HOST.toString(), "http://localhost:" + freePort);
        headers.put(HttpHeaderKeys.REQUEST_TARGET.toString(), "/Service1");
        sender = new NewMessageSender(transport, malMessage, encodedMessage, headers);
    }



    /**
     * Http Post object is created.
     * When sending the message, there is no server.
     * Expecting HttpHostConnectException captured by IOException
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void wrongHostTest01() throws Exception {
        headers.put(HttpHeaderKeys.HOST.toString(), "http://localhost:8081");
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("org.apache.http.conn.HttpHostConnectException: Connect to localhost:8081");
        sender.call();
    }


    /**
     * Http Post object is created.
     * When sending the message, there is no server.
     * Expecting HttpHostConnectException captured by IOException
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void wrongHostTest02() throws Exception {
        headers.put(HttpHeaderKeys.HOST.toString(), "abcd");
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Host name may not be null");
        sender.call();
    }

    /**
     * Http Server is created. But throws an error.
     * Expecting NoHttpResponseException
     *
     * @throws Exception any unexpected exception
     */
    @Category(PortDependentTest.class)
    @Test
    public void test06() throws Exception {
        headers.put(HttpHeaderKeys.REQUEST_TARGET.toString(), "/test06");
        server.createContext("/test06", httpExchange -> {
            throw new IOException("Testing IOException");
        });
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("org.apache.http.NoHttpResponseException");
        sender.call();
    }

    /**
     * Http Server is created. Return code 204.
     * Interaction Pattern is send-only
     * Successful response.
     *
     * @throws Exception any unexpected exception
     */
    @Category(PortDependentTest.class)
    @Test
    public void test07() throws Exception {
        headers.put(HttpHeaderKeys.REQUEST_TARGET.toString(), "/test07");
        doReturn(header).when(malMessage).getHeader();
        doReturn(InteractionType.SEND).when(header).getInteractionType();
        doReturn(new UOctet((short) 1)).when(header).getInteractionStage();
        server.createContext("/test07", httpExchange -> httpExchange.sendResponseHeaders(204, -1));
        Assert.assertTrue(sender.call());
    }

    /**
     * Http Server is created. Return code 204.
     * Interaction Pattern is send-response.
     * Getting endpoint which will return null.
     * Expecting MALTransmitErrorException
     *
     * @throws Exception any unexpected exception
     */
    @Category(PortDependentTest.class)
    @Test
    public void test08() throws Exception {
        headers.put(HttpHeaderKeys.REQUEST_TARGET.toString(), "/test08");
        doReturn(header).when(malMessage).getHeader();
        doReturn(InteractionType.SUBMIT).when(header).getInteractionType();
        doReturn(new UOctet((short) 1)).when(header).getInteractionStage();
        server.createContext("/test08", httpExchange -> httpExchange.sendResponseHeaders(204, -1));
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Null Http Endpoint");
        sender.call();
    }

    /**
     * Http Server is created. Return code 204.
     * Interaction Pattern is send-response.
     * Getting endpoint which will return mock object.
     * Expecting MALTransmitErrorException when retrieving body from response.
     * (Null body since code 204 doesn't have body).
     *
     * @throws Exception any unexpected exception
     */
    @Category(PortDependentTest.class)
    @Test
    public void test09() throws Exception {
        URI uriFrom = new URI("http://localhost:8080/client09");
        headers.put(HttpHeaderKeys.REQUEST_TARGET.toString(), "/test09");
        doReturn(header).when(malMessage).getHeader();
        doReturn(InteractionType.REQUEST).when(header).getInteractionType();
        doReturn(new UOctet((short) 1)).when(header).getInteractionStage();
        doReturn(uriFrom).when(header).getURIFrom();
        doReturn(endpoint).when(transport).getEndpoint(uriFrom);
        server.createContext("/test09", httpExchange -> httpExchange.sendResponseHeaders(204, -1));
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("java.lang.NullPointerException");
        sender.call();
    }

    /**
     * Http Server is created. Return code 200.
     * Interaction Pattern is send-response.
     * Returning http error response, but with all necessary headers.
     * checking normal workflow to create mal message and send it
     * Sending it as malhttp
     *
     * Successful response
     *
     * @throws Exception any unexpected exception
     */
    @Category(PortDependentTest.class)
    @Test
    public void test17() throws Exception {
        URI uriFrom = new URI("malhttp://localhost:8080/client09");
        headers.put(HttpHeaderKeys.HOST.toString(), "malhttp://localhost:" + freePort);
        headers.put(HttpHeaderKeys.REQUEST_TARGET.toString(), "/test17");
        doReturn(header).when(malMessage).getHeader();

        doReturn(uriFrom).when(header).getURIFrom();
        doReturn(InteractionType.REQUEST).when(header).getInteractionType();
        doReturn(interactionStage).when(header).getInteractionStage();


        doReturn(endpoint).when(transport).getEndpoint(uriFrom);

        server.createContext("/test17", httpExchange -> {
            Assert.assertTrue(httpExchange.getRequestHeaders().get("Host").toString().startsWith("[http"));
            MessageTestHelper.fillResponseHeader(httpExchange.getResponseHeaders());
            httpExchange.sendResponseHeaders(200, encodedMessage.length);
            httpExchange.getResponseBody().write(encodedMessage);
            httpExchange.getResponseBody().close();
        });
        Assert.assertTrue(sender.call());
        verify(transport, times(1)).createMessage(eq(encodedMessage), any(GENMessageHeader.class));
        verify(endpoint, times(1)).receiveMessage(any());
    }

    /**
     * Checking null object in encoded message object.
     * null header Map
     * Expecting excpetion with specific message.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void validationTest01() throws Exception {
        headers = null;
        sender = new NewMessageSender(transport, malMessage, encodedMessage, headers);
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Null objects in not-nullable fields in Encoded Message");
        sender.call();
    }

    /**
     * Checking null object in encoded message object.
     * null message body
     * Expecting excpetion with specific message.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void validationTest02() throws Exception {
        encodedMessage = null;
        headers = new HashMap<>();
        sender = new NewMessageSender(transport, malMessage, encodedMessage, headers);
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Null objects in not-nullable fields in Encoded Message");
        sender.call();
    }

    /**
     * Checking null object in encoded message object.
     * headers has no host
     * Expecting excpetion with specific message.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void validationTest03() throws Exception {
        headers.remove(HttpHeaderKeys.HOST.toString());
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Null objects in not-nullable fields in Encoded Message");
        sender.call();
    }

    /**
     * Checking null object in encoded message object.
     * null message body
     * Expecting excpetion with specific message.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void validationTest04() throws Exception {
        headers.remove(HttpHeaderKeys.REQUEST_TARGET.toString());
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Null objects in not-nullable fields in Encoded Message");
        sender.call();
    }

    /**
     * Testing helper method isPureHttpError.
     * Testing
     * 1.   null Http Response
     * 2.   null headers
     * 3.   Empty headers
     * 4.   Incomplete header array
     * 5.   Complete header array
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void isPureHttpErrorTest01() throws Exception {
        Method isPureHttpErrorMethod = sender.getClass().getDeclaredMethod("isPureHttpError", HttpResponse.class);
        isPureHttpErrorMethod.setAccessible(true);
        Assert.assertFalse((boolean) isPureHttpErrorMethod.invoke(sender, response));
        doReturn(null).when(response).getAllHeaders();
        Assert.assertFalse((boolean) isPureHttpErrorMethod.invoke(sender, response));
        doReturn(new ApacheHeaderForTest[0]).when(response).getAllHeaders();
        Assert.assertTrue((boolean) isPureHttpErrorMethod.invoke(sender, response));
        List<ApacheHeaderForTest> headers = new ArrayList<>();
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.IS_ERROR_MSG.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.AREA_VERSION.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.OPERATION.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.SERVICE.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.SERVICE_AREA.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.TRANSACTION_ID.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.INTERACTION_STAGE.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.INTERACTION_TYPE.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.SESSION_NAME.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.SESSION.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.NETWORK_ZONE.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.DOMAIN.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.PRIORITY.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.QOS_LEVEL.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.TIMESTAMP.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.URI_FROM.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.AUTH_ID.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.HOST.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.REQUEST_TARGET.toString(), ""));
        doReturn(headers.toArray(new ApacheHeaderForTest[headers.size()])).when(response).getAllHeaders();
        Assert.assertTrue((boolean) isPureHttpErrorMethod.invoke(sender, response));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.VERSION.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.CONTENT_TYPE.toString(), ""));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.CONTENT_LENGTH.toString(), ""));
        doReturn(headers.toArray(new ApacheHeaderForTest[headers.size()])).when(response).getAllHeaders();
        Assert.assertFalse((boolean) isPureHttpErrorMethod.invoke(sender, response));
    }

    /**
     * Testing private method processResponse.
     * Testing
     * 1.   return boolean if it is sender doesn't need response logic.
     * 2.   exception when endpoint is null
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void processResponseTest01() throws Exception {
        Method processResponseMethod = sender.getClass().getDeclaredMethod("processResponse", HttpResponse.class);
        processResponseMethod.setAccessible(true);
        doReturn(header).when(malMessage).getHeader();
        doReturn(InteractionType.SEND).when(header).getInteractionType();
        doReturn(uOctet).when(header).getInteractionStage();
        Assert.assertTrue((boolean) processResponseMethod.invoke(sender, response));
        URI testUri = new URI("Test.URI");
        doReturn(InteractionType.REQUEST).when(header).getInteractionType();
        doReturn(testUri).when(header).getURIFrom();
        doReturn(null).when(transport).getEndpoint(testUri);
        try {
            processResponseMethod.invoke(sender, response);
            Assert.assertTrue(false);
        } catch (InvocationTargetException exp) {
            Assert.assertTrue(exp.getCause() instanceof MALTransmitErrorException);
            Assert.assertTrue(exp.getCause().getMessage().contains("Null Http Endpoint"));
        }
    }

    /**
     * Testing private method processResponse.
     * Testing
     * 1.   http pure error to exception in tranport's create-message
     * 2.   http pure error to successful endpoint's receive-message
     * 3.   http pure error to exception in endpoint's receive-message
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void processResponseTest02() throws Exception {
        Method processResponseMethod = sender.getClass().getDeclaredMethod("processResponse", HttpResponse.class);
        processResponseMethod.setAccessible(true);
        doReturn(new ApacheHeaderForTest[0]).when(response).getAllHeaders();
        doReturn(header).when(malMessage).getHeader();
        URI testUri = new URI("Test.URI");
        doReturn(InteractionType.REQUEST).when(header).getInteractionType();
        doReturn(testUri).when(header).getURIFrom();
        doReturn(uOctet).when(header).getInteractionStage();
        doReturn(authenticationId).when(header).getAuthenticationId();
        doReturn(time).when(header).getTimestamp();
        doReturn(QoSLevel.BESTEFFORT).when(header).getQoSlevel();
        doReturn(uInteger).when(header).getPriority();
        doReturn(identifiers).when(header).getDomain();
        doReturn(identifier).when(header).getNetworkZone();
        doReturn(SessionType.LIVE).when(header).getSession();
        doReturn(identifier).when(header).getSessionName();
        doReturn(uInteger.getValue()).when(header).getTransactionId();
        doReturn(uShort).when(header).getServiceArea();
        doReturn(uShort).when(header).getService();
        doReturn(uShort).when(header).getOperation();
        doReturn(uOctet).when(header).getAreaVersion();
        doReturn(endpoint).when(transport).getEndpoint(testUri);

        doReturn(statusLine).when(response).getStatusLine();
        doReturn(400).when(statusLine).getStatusCode();

        doThrow(new MALException("MALException in createMessage1")).when(endpoint).createMessage(authenticationId, testUri, time, QoSLevel.BESTEFFORT, uInteger  , identifiers, identifier, SessionType.LIVE, identifier, InteractionType.REQUEST, new UOctet((short) (uOctet.getValue() + 1)), uInteger.getValue(), uShort, uShort, uShort, uOctet, true, null, MALHelper.BAD_ENCODING_ERROR_NUMBER, new Union("Created from Http Status Code"));
        try {
            processResponseMethod.invoke(sender, response);
            Assert.assertTrue(false);
        } catch (InvocationTargetException exp) {
            Assert.assertTrue(exp.getCause() instanceof MALTransmitErrorException);
            Assert.assertTrue(exp.getCause().getMessage().contains("MALException in createMessage1"));
        }

        doReturn(malMessage).when(endpoint).createMessage(authenticationId, testUri, time, QoSLevel.BESTEFFORT, uInteger  , identifiers, identifier, SessionType.LIVE, identifier, InteractionType.REQUEST, new UOctet((short) (uOctet.getValue() + 1)), uInteger.getValue(), uShort, uShort, uShort, uOctet, true, null, MALHelper.BAD_ENCODING_ERROR_NUMBER, new Union("Created from Http Status Code"));
        Assert.assertTrue((boolean) processResponseMethod.invoke(sender, response));
        verify(endpoint, times(1)).receiveMessage(eq(malMessage));

        doThrow(new MALException("Testing MALException in Endpoint")).when(endpoint).receiveMessage(malMessage);
        try {
            processResponseMethod.invoke(sender, response);
            Assert.assertTrue(false);
        } catch (InvocationTargetException exp) {
            Assert.assertTrue(exp.getCause() instanceof MALTransmitErrorException);
            Assert.assertTrue(exp.getCause().getMessage().contains("Testing MALException in Endpoint"));
        }
    }

    /**
     * Testing private method processResponse.
     * Testing
     * 1.   http response to exception in tranport's create-message
     * 2.   http response to successful endpoint's receive-message
     * 3.   http response to exception in endpoint's receive-message
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void processResponseTest03() throws Exception {
        Method processResponseMethod = sender.getClass().getDeclaredMethod("processResponse", HttpResponse.class);
        processResponseMethod.setAccessible(true);
        List<ApacheHeaderForTest> headers = new ArrayList<>();
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.IS_ERROR_MSG.toString(), "False"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.AREA_VERSION.toString(), "1"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.OPERATION.toString(), "1"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.SERVICE.toString(), "1"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.SERVICE_AREA.toString(), "1"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.TRANSACTION_ID.toString(), "1"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.INTERACTION_STAGE.toString(), "1"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.INTERACTION_TYPE.toString(), "REQUEST"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.SESSION_NAME.toString(), "1"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.SESSION.toString(), "LIVE"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.NETWORK_ZONE.toString(), "1"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.DOMAIN.toString(), "1"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.PRIORITY.toString(), "1"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.QOS_LEVEL.toString(), "BESTEFFORT"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.TIMESTAMP.toString(), "1970-001T00:00:00.000"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.URI_FROM.toString(), "http://localhost:8081/"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.AUTH_ID.toString(), "01"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.HOST.toString(), "http://localhost:8080"));
        headers.add(new ApacheHeaderForTest(HttpHeaderKeys.REQUEST_TARGET.toString(), "/"));

        doReturn(headers.toArray(new ApacheHeaderForTest[headers.size()])).when(response).getAllHeaders();
        doReturn(header).when(malMessage).getHeader();
        URI testUri = new URI("Test.URI");
        doReturn(InteractionType.REQUEST).when(header).getInteractionType();
        doReturn(testUri).when(header).getURIFrom();
        doReturn(uOctet).when(header).getInteractionStage();

        doReturn(endpoint).when(transport).getEndpoint(testUri);

        doReturn(statusLine).when(response).getStatusLine();
        doReturn(204).when(statusLine).getStatusCode();

        HttpEntity mockedHttpEntity = mock(HttpEntity.class);
        doReturn(mockedHttpEntity).when(response).getEntity();
        ByteArrayInputStream inputStream = new ByteArrayInputStream("".getBytes());
        doReturn(inputStream).when(mockedHttpEntity).getContent();

        doThrow(new MALException("MALException in createMessage")).when(transport).createMessage(eq("".getBytes()), any(GENMessageHeader.class));
        try {
            processResponseMethod.invoke(sender, response);
            Assert.assertTrue(false);
        } catch (InvocationTargetException exp) {
            Assert.assertTrue(exp.getCause() instanceof MALTransmitErrorException);
            Assert.assertTrue(exp.getCause().getMessage().contains("MALException in createMessage"));
        }

        doReturn(malMessage).when(transport).createMessage(eq("".getBytes()), any(GENMessageHeader.class));
        processResponseMethod.invoke(sender, response);
        verify(endpoint, times(1)).receiveMessage(malMessage);

        doThrow(new MALException("Testing MALException")).when(endpoint).receiveMessage(malMessage);
        try {
            processResponseMethod.invoke(sender, response);
            Assert.assertTrue(false);
        } catch (InvocationTargetException exp) {
            Assert.assertTrue(exp.getCause() instanceof MALTransmitErrorException);
            Assert.assertTrue(exp.getCause().getMessage().contains("Testing MALException"));
        }
    }

    /**
     * Testing time-out variable values
     * 1.   if not set, they are all 0.
     * 2.   if set, they are set accordingly
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void verifyTimeoutVariablesTest() throws Exception {
        sender = new NewMessageSender(transport, malMessage, encodedMessage, headers);
        Field connectionTimeoutInMilliSecondField = sender.getClass().getDeclaredField("connectionTimeoutInMilliSecond");
        Field connectionRequestTimeoutInMilliSecondField = sender.getClass().getDeclaredField("connectionRequestTimeoutInMilliSecond");
        Field socketTimeoutInMilliSecondField = sender.getClass().getDeclaredField("socketTimeoutInMilliSecond");
        connectionTimeoutInMilliSecondField.setAccessible(true);
        connectionRequestTimeoutInMilliSecondField.setAccessible(true);
        socketTimeoutInMilliSecondField.setAccessible(true);
        Assert.assertEquals(0, connectionTimeoutInMilliSecondField.get(sender));
        Assert.assertEquals(0, connectionRequestTimeoutInMilliSecondField.get(sender));
        Assert.assertEquals(0, socketTimeoutInMilliSecondField.get(sender));

        sender = new NewMessageSender(transport, 1, 2, 3, malMessage, encodedMessage, headers);
        connectionTimeoutInMilliSecondField = sender.getClass().getDeclaredField("connectionTimeoutInMilliSecond");
        connectionRequestTimeoutInMilliSecondField = sender.getClass().getDeclaredField("connectionRequestTimeoutInMilliSecond");
        socketTimeoutInMilliSecondField = sender.getClass().getDeclaredField("socketTimeoutInMilliSecond");
        connectionTimeoutInMilliSecondField.setAccessible(true);
        connectionRequestTimeoutInMilliSecondField.setAccessible(true);
        socketTimeoutInMilliSecondField.setAccessible(true);
        Assert.assertEquals(1, connectionTimeoutInMilliSecondField.get(sender));
        Assert.assertEquals(2, connectionRequestTimeoutInMilliSecondField.get(sender));
        Assert.assertEquals(3, socketTimeoutInMilliSecondField.get(sender));
    }

    /**
     * Testing if response headers' HOST value is http or https, it is converted to malhttp
     * Testing for both http & https. The result should be the same as both will be translated to malhttp.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void malHttpOnResponseTest01() throws Exception {
        GENMessageHeader header = new GENMessageHeader(new URI("http://localhost:8080"),
                new Blob(new byte[] {1}), new URI("http://localhost:8080"), new Time(0), QoSLevel.ASSURED,
                new UInteger(1L), new IdentifierList(), new Identifier("test"), SessionType.LIVE,
                new Identifier("test"), InteractionType.SUBMIT, new UOctet((short) 1), 1L,
                new UShort(1), new UShort(1), new UShort(1), new UOctet((short) 1), true);

        doReturn(header).when(malMessage).getHeader();
        doReturn(endpoint).when(transport).getEndpoint(header.getURIFrom());
        doReturn(statusLine).when(response).getStatusLine();
        doReturn(200).when(statusLine).getStatusCode();
        ByteArrayInputStream stream = new ByteArrayInputStream(String.valueOf("").getBytes());
        doReturn(entity).when(response).getEntity();
        doReturn(stream).when(entity).getContent();
        doReturn(false).when(transport).isSecureServer();

        MalToHttpMapper.getInstance().generateHeader(header, headers, new URI("http://localhost:8080"), true, null);
        List<ApacheHeaderForTest> headerList = new ArrayList<>();
        headers.keySet().forEach(e -> headerList.add(new ApacheHeaderForTest(e, headers.get(e))));
        doReturn(headerList.toArray(new ApacheHeaderForTest[headerList.size()])).when(response).getAllHeaders();

        GENMessageHeader receivedHeader = new GENMessageHeader();
        receivedHeader.setURIFrom(header.getURIFrom());
        receivedHeader.setAuthenticationId(header.getAuthenticationId());
        receivedHeader.setURITo(new URI("malhttp://localhost:8080/"));
        receivedHeader.setTimestamp(header.getTimestamp());
        receivedHeader.setQoSlevel(header.getQoSlevel());
        receivedHeader.setPriority(header.getPriority());
        receivedHeader.setDomain(header.getDomain());
        receivedHeader.setNetworkZone(header.getNetworkZone());
        receivedHeader.setSession(header.getSession());
        receivedHeader.setSessionName(header.getSessionName());
        receivedHeader.setInteractionType(header.getInteractionType());
        receivedHeader.setInteractionStage(header.getInteractionStage());
        receivedHeader.setTransactionId(header.getTransactionId());
        receivedHeader.setServiceArea(header.getServiceArea());
        receivedHeader.setService(header.getService());
        receivedHeader.setOperation(header.getOperation());
        receivedHeader.setAreaVersion(header.getAreaVersion());
        receivedHeader.setIsErrorMessage(header.getIsErrorMessage());

        sender = new NewMessageSender(transport, malMessage, encodedMessage, headers);
        Method processResponseMethod = sender.getClass().getDeclaredMethod("processResponse", HttpResponse.class);
        processResponseMethod.setAccessible(true);

        processResponseMethod.invoke(sender, response);
        verify(transport, times(1)).createMessage(eq(String.valueOf("").getBytes()),
                MessageTestHelper.genMessageHeaderEq(receivedHeader));
        reset(transport);

        doReturn(endpoint).when(transport).getEndpoint(header.getURIFrom());
        doReturn(true).when(transport).isSecureServer();
        header.setURITo(new URI("https://localhost:8080"));
        headers.clear();
        MalToHttpMapper.getInstance().generateHeader(header, headers, new URI("https://localhost:8080"), true, null);
        List<ApacheHeaderForTest> headerList1 = new ArrayList<>();
        headers.keySet().forEach(e -> headerList1.add(new ApacheHeaderForTest(e, headers.get(e))));
        doReturn(headerList1.toArray(new ApacheHeaderForTest[headerList1.size()])).when(response).getAllHeaders();
        processResponseMethod.invoke(sender, response);
        verify(transport, times(1)).createMessage(eq(String.valueOf("").getBytes()),
                MessageTestHelper.genMessageHeaderEq(receivedHeader));
    }

    private class ApacheHeaderForTest implements Header {
        String name;
        String value;

        public ApacheHeaderForTest(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public HeaderElement[] getElements() throws ParseException {
            return new HeaderElement[0];
        }
    }
}
