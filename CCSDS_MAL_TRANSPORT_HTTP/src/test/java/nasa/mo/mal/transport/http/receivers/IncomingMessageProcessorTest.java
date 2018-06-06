/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.http.receivers;

import com.sun.net.httpserver.HttpExchange;
import esa.mo.mal.transport.gen.GENMessage;
import esa.mo.mal.transport.gen.GENMessageHeader;
import esa.mo.mal.transport.gen.body.GENMessageBody;
import nasa.mo.mal.transport.http.HttpEndpoint;
import nasa.mo.mal.transport.http.HttpTransport;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

import static org.mockito.Mockito.*;

/**
 * @author wphyo
 *         Created on 7/13/17.
 */
public class IncomingMessageProcessorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private HttpTransport transport;
    @Mock
    private HttpExchange exchange;
    @Mock
    private HttpEndpoint endpoint;
    @Mock
    private GENMessage message;
    @Mock
    private GENMessageHeader header;
    @Mock
    private GENMessageBody body;

    /**
     * Internally sent message.
     * Everything is expected to go through.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void internalTest01() throws Exception {
        doReturn(header).when(message).getHeader();
        doReturn(new GENMessageBody(null, null, null)).when(message).getBody();
        org.ccsds.moims.mo.mal.structures.URI uriTo = null;
        doReturn(endpoint).when(transport).getEndpoint(uriTo);
        HttpIncomingMessageHolder holder = new HttpIncomingMessageHolder(exchange, message, true);
        IncomingMessageProcessor processor = new IncomingMessageProcessor(holder, transport);
        processor.call();
        verify(endpoint, times(1)).receiveMessage(message);
    }

    /**
     * Internally sent message.
     * decoded message is null validation throws an exception
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void internalTest02() throws Exception {
        HttpIncomingMessageHolder holder = new HttpIncomingMessageHolder(exchange, message, true);
        IncomingMessageProcessor processor = new IncomingMessageProcessor(holder, transport);
        thrown.expect(MALTransmitErrorException.class);
        processor.call();
    }

    /**
     * Internally sent message.
     * no endpoint exception
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void internalTest03() throws Exception {
        doReturn(header).when(message).getHeader();
        HttpIncomingMessageHolder holder = new HttpIncomingMessageHolder(exchange, message, true);
        IncomingMessageProcessor processor = new IncomingMessageProcessor(holder, transport);
        thrown.expect(MALTransmitErrorException.class);
        processor.call();
    }

    /**
     * Internally sent message.
     * endpoint's receiveMessage method throws exception
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void internalTest04() throws Exception {
        doThrow(MALException.class).when(endpoint).receiveMessage(message);
        doReturn(header).when(message).getHeader();
        org.ccsds.moims.mo.mal.structures.URI uriTo = null;
        doReturn(endpoint).when(transport).getEndpoint(uriTo);
        HttpIncomingMessageHolder holder = new HttpIncomingMessageHolder(exchange, message, true);
        IncomingMessageProcessor processor = new IncomingMessageProcessor(holder, transport);
        thrown.expect(MALInteractionException.class);
        processor.call();
        verify(endpoint, times(1)).receiveMessage(message);
    }

    /**
     * Processing Http Request
     * Expecting error response with code 404 when URI-TO is null.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void externalTest01() throws Exception {
        org.ccsds.moims.mo.mal.structures.URI uriTo = null;
        doReturn(header).when(message).getHeader();
        doReturn(new ByteArrayOutputStream()).when(exchange).getResponseBody();
        doReturn(uriTo).when(header).getURITo();
        doReturn(endpoint).when(transport).getEndpoint(uriTo);
        HttpIncomingMessageHolder holder = new HttpIncomingMessageHolder(exchange, message, false);
        IncomingMessageProcessor processor = new IncomingMessageProcessor(holder, transport);
        processor.call();
        verify(exchange, times(1)).sendResponseHeaders(404, 0);
    }

    /**
     * Processing Http Request
     * Expecting error response with code 500 when endpoint is null
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void externalTest02() throws Exception {
        org.ccsds.moims.mo.mal.structures.URI uriTo = new org.ccsds.moims.mo.mal.structures.URI("http://localhost:8080/UnitTesting/");
        doReturn(new ByteArrayOutputStream()).when(exchange).getResponseBody();
        doReturn(header).when(message).getHeader();
        doReturn(uriTo).when(header).getURITo();
        doReturn(null).when(transport).getEndpoint(uriTo);
        HttpIncomingMessageHolder holder = new HttpIncomingMessageHolder(exchange, message, false);
        IncomingMessageProcessor processor = new IncomingMessageProcessor(holder, transport);
        processor.call();
        verify(exchange, times(1)).sendResponseHeaders(500, 0);
    }

    /**
     * Processing Http Request
     * This is send only type. Expecting response with 204 code.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void externalTest03() throws Exception {
        doReturn(header).when(message).getHeader();
        doReturn(InteractionType.SEND).when(header).getInteractionType();
        doReturn(new org.ccsds.moims.mo.mal.structures.URI("http://localhost:8080/UnitTesting/")).when(header).getURITo();
        doReturn(new UOctet((short) 1)).when(header).getInteractionStage();
        doReturn(new ByteArrayOutputStream()).when(exchange).getResponseBody();
        doReturn(endpoint).when(transport).getEndpoint(new org.ccsds.moims.mo.mal.structures.URI("http://localhost:8080/UnitTesting/"));
        HttpIncomingMessageHolder holder = new HttpIncomingMessageHolder(exchange, message, false);
        IncomingMessageProcessor processor = new IncomingMessageProcessor(holder, transport);
        processor.call();
        verify(exchange, times(1)).sendResponseHeaders(204, -1);
    }

    /**
     * Processing Http Request
     * This is send only type. Expecting exception when sending 204 response has triggered an exception
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void externalTest04() throws Exception {
        doReturn(header).when(message).getHeader();
        doReturn(InteractionType.SEND).when(header).getInteractionType();
        doReturn(new org.ccsds.moims.mo.mal.structures.URI("http://localhost:8080/UnitTesting/")).when(header).getURITo();
        doReturn(new UOctet((short) 1)).when(header).getInteractionStage();
        doReturn(new ByteArrayOutputStream()).when(exchange).getResponseBody();
        doReturn(new URI("/UnitTesting/")).when(exchange).getRequestURI();
        doReturn(endpoint).when(transport).getEndpoint(new org.ccsds.moims.mo.mal.structures.URI("http://localhost:8080/UnitTesting/"));
        doThrow(new IOException("testing exception")).when(exchange).sendResponseHeaders(204, -1);
        HttpIncomingMessageHolder holder = new HttpIncomingMessageHolder(exchange, message, false);
        IncomingMessageProcessor processor = new IncomingMessageProcessor(holder, transport);
        thrown.expect(MALTransmitErrorException.class);
        processor.call();
    }

    /**
     * Processing Http Request
     * This is send-response type. It has to store the message.
     * Hence Transport's storeProcessingMessage is called.
     * Then endpoint's receiveMessage is called.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void externalTest05() throws Exception {
        doReturn(header).when(message).getHeader();
        doReturn(InteractionType.SUBMIT).when(header).getInteractionType();
        doReturn(new org.ccsds.moims.mo.mal.structures.URI("http://localhost:8080/UnitTesting/")).when(header).getURITo();
        doReturn(new UOctet((short) 1)).when(header).getInteractionStage();
        doReturn(endpoint).when(transport).getEndpoint(new org.ccsds.moims.mo.mal.structures.URI("http://localhost:8080/UnitTesting/"));
        HttpIncomingMessageHolder holder = new HttpIncomingMessageHolder(exchange, message, false);
        IncomingMessageProcessor processor = new IncomingMessageProcessor(holder, transport);
        processor.call();
        verify(transport, times(1)).storeProcessingMessage(header, exchange);
        verify(endpoint, times(1)).receiveMessage(message);
    }

    /**
     * Processing Http Request
     * This is send-response type. It has to store the message.
     * Hence Transport's storeProcessingMessage is called.
     * Then endpoint's receiveMessage is called.
     * <p>
     * Throwing exception in endpoint's receiveMessage.
     * So it will respond with error code 500 & throw an MALTransmitError
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void externalTest06() throws Exception {
        doReturn(header).when(message).getHeader();
        doReturn(InteractionType.SUBMIT).when(header).getInteractionType();
        doReturn(new org.ccsds.moims.mo.mal.structures.URI("http://localhost:8080/UnitTesting/")).when(header).getURITo();
        doReturn(new UOctet((short) 1)).when(header).getInteractionStage();
        doReturn(new ByteArrayOutputStream()).when(exchange).getResponseBody();
        doReturn(new URI("/UnitTesting/")).when(exchange).getRequestURI();
        doReturn(endpoint).when(transport).getEndpoint(new org.ccsds.moims.mo.mal.structures.URI("http://localhost:8080/UnitTesting/"));
        doThrow(new MALException("Testing Exception")).when(endpoint).receiveMessage(message);
        HttpIncomingMessageHolder holder = new HttpIncomingMessageHolder(exchange, message, false);
        IncomingMessageProcessor processor = new IncomingMessageProcessor(holder, transport);
        thrown.expect(MALTransmitErrorException.class);
        processor.call();
        verify(transport, times(1)).storeProcessingMessage(header, exchange);
        verify(endpoint, times(1)).receiveMessage(message);
        verify(exchange, times(1)).sendResponseHeaders(500, 0);
    }

    /**
     * Testing null transport in constructor.
     * expecting IO Exception.
     *
     * @throws Exception other unexpected Exception
     */
    @Test
    public void nullTransportTest01() throws Exception {
        thrown.expect(IOException.class);
        thrown.expectMessage("Constructor with NULL transport in IncomingMessageProcessor");
        IncomingMessageProcessor processor = new IncomingMessageProcessor(new HttpIncomingMessageHolder(exchange, message, false), null);
    }

    /**
     * Testing Null Endpoint for internal Send Logic
     * expecting MALInteractionException
     *
     * @throws Exception other unexpected Exception
     */
    @Test
    public void nullEndpointTest101() throws Exception {
        doReturn(header).when(message).getHeader();
        doReturn(body).when(message).getBody();
        doReturn(InteractionType.SUBMIT).when(header).getInteractionType();
        doReturn(new org.ccsds.moims.mo.mal.structures.URI("http://localhost:8080/UnitTesting/")).when(header).getURITo();
        doReturn(new UOctet((short) 1)).when(header).getInteractionStage();
        doReturn(null).when(transport).getEndpoint(new org.ccsds.moims.mo.mal.structures.URI("http://localhost:8080/UnitTesting/"));
        HttpIncomingMessageHolder holder = new HttpIncomingMessageHolder(exchange, message, true);
        IncomingMessageProcessor processor = new IncomingMessageProcessor(holder, transport);
        thrown.expect(MALInteractionException.class);
        thrown.expectMessage("Null Endpoint");
        processor.call();
    }
}
