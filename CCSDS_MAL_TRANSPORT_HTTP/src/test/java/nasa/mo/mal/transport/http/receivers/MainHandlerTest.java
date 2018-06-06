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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import nasa.mo.mal.encoder.Header.MalToHttpMapper;
import esa.mo.mal.transport.gen.GENMessageHeader;
import nasa.mo.mal.transport.http.HttpTransport;
import nasa.mo.mal.transport.http.MessageTestHelper;
import nasa.mo.mal.transport.http.util.Constants;
import nasa.mo.mal.transport.http.util.MALTransmitErrorBuilder;
import nasa.mo.mal.transport.http.util.ResponseCodes;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * @author wphyo
 *         Created on 7/6/17.
 */
public class MainHandlerTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private HttpTransport transport;
    @Mock
    private HttpExchange exchange;

    /**
     * Testing if request method is not POST, return forbidden error (403) with simple text body
     *
     * @throws IOException unused.
     */
    @Test
    public void handleTest01() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getResponseBody()).thenReturn(outputStream);
        MainHandler handler = new MainHandler(transport);
        handler.handle(exchange);
        verify(exchange, times(1)).sendResponseHeaders(ResponseCodes.METHOD_NOT_ALLOWED.getCode(), Constants.DEFAULT_ERROR_RESPONSE_BODY.getBytes().length);
        Assert.assertTrue(Arrays.equals(outputStream.toByteArray(), Constants.DEFAULT_ERROR_RESPONSE_BODY.getBytes()));
    }

    /**
     * Testing if request method is POST, call transport's addNewIncomingMessageToProcess method
     *
     * @throws IOException unused.
     */
    @Test
    public void handleTest02() throws IOException, URISyntaxException, MALException, MALTransmitErrorException {
        Headers headers = new Headers();
        MessageTestHelper.fillResponseHeader(headers);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(String.valueOf("").getBytes());
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestHeaders()).thenReturn(headers);
        when(exchange.getRequestBody()).thenReturn(inputStream);
        MainHandler handler = new MainHandler(transport);
        handler.handle(exchange);
        verify(transport, times(1)).createMessage(any(byte[].class), any(GENMessageHeader.class));
        verify(transport, times(1)).addNewIncomingMessageToProcess(any(IncomingMessageProcessor.class));
    }

    /**
     * Testing if decoder throws an exception, return error.
     *
     * @throws IOException unused.
     */
    @Test
    public void handleTest03() throws IOException, URISyntaxException, MALException {
        Headers headers = new Headers();
        MessageTestHelper.fillResponseHeader(headers);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(String.valueOf("").getBytes());
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestHeaders()).thenReturn(headers);
        when(exchange.getRequestBody()).thenReturn(inputStream);
        MainHandler handler = new MainHandler(transport);
        doThrow(new MALException("Encoding Error: sample")).when(transport).createMessage(any(), any());
        when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());
        handler.handle(exchange);
        verify(exchange, times(1))
                .sendResponseHeaders(400, Constants.DEFAULT_ERROR_RESPONSE_BODY.getBytes().length);
    }

    /**
     * Testing error response when Transport's addNewIncomingMessageToProcess has throws exception
     *
     * @throws Exception other unexpected exception
     */
    @Test
    public void exceptionResponseTest01() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);
        doThrow(MALTransmitErrorBuilder.create().setExtraInfo("Testing").build()).when(transport).addNewIncomingMessageToProcess(any(IncomingMessageProcessor.class));
        Headers headers = new Headers();
        MessageTestHelper.fillResponseHeader(headers);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(String.valueOf("").getBytes());
        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestHeaders()).thenReturn(headers);
        when(exchange.getRequestBody()).thenReturn(inputStream);
        MainHandler handler = new MainHandler(transport);
        handler.handle(exchange);
        verify(transport, times(1)).createMessage(any(byte[].class), any(GENMessageHeader.class));
        verify(transport, times(1)).addNewIncomingMessageToProcess(any(IncomingMessageProcessor.class));
        verify(exchange, times(1)).sendResponseHeaders(ResponseCodes.INTERNAL_SERVER_ERROR.getCode(), 0);
    }

    /**
     * Testing if incoming headers' HOST value is http or https, it is converted to malhttp
     * Testing for both http & https. The result should be the same as both will be translated to malhttp.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void checkingMalHttpInHost() throws Exception {
        MainHandler handler = new MainHandler(transport);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(String.valueOf("").getBytes());
        when(exchange.getRequestBody()).thenReturn(inputStream);
        when(exchange.getRequestMethod()).thenReturn("POST");
        Headers headers = new Headers();
        GENMessageHeader header = new GENMessageHeader(new URI("http://localhost:8080"),
                new Blob(new byte[] {1}), new URI("http://localhost:8080"), new Time(0), QoSLevel.ASSURED,
                new UInteger(1L), new IdentifierList(), new Identifier("test"), SessionType.LIVE,
                new Identifier("test"), InteractionType.SEND, null, 1L,
                new UShort(1), new UShort(1), new UShort(1), new UOctet((short) 1), true);
        Map<String, String> headerMap = new HashMap<>();
        MalToHttpMapper.getInstance().generateHeader(header, headerMap, new URI("http://localhost:8080"), true, null);
        headerMap.keySet().forEach(e -> headers.set(e, headerMap.get(e)));
        when(exchange.getRequestHeaders()).thenReturn(headers);

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


        handler.handle(exchange);
        verify(transport, times(1)).createMessage(eq(String.valueOf("").getBytes()),
                MessageTestHelper.genMessageHeaderEq(receivedHeader));
        reset(transport);
        doReturn(true).when(transport).isSecureServer();
        header.setURITo(new URI("https://localhost:8080"));
        headerMap.clear();
        MalToHttpMapper.getInstance().generateHeader(header, headerMap, new URI("https://localhost:8080"), true, null);
        Headers headers1 = new Headers();
        headerMap.keySet().forEach(e -> headers1.set(e, headerMap.get(e)));
        when(exchange.getRequestHeaders()).thenReturn(headers1);

        handler.handle(exchange);
        verify(transport, times(1)).createMessage(eq(String.valueOf("").getBytes()),
                MessageTestHelper.genMessageHeaderEq(receivedHeader));
    }
}


