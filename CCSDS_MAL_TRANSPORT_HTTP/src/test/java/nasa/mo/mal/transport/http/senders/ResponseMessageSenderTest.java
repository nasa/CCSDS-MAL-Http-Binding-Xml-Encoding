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

import com.sun.net.httpserver.HttpExchange;
import nasa.mo.mal.encoder.Header.HttpHeaderKeys;
import esa.mo.mal.transport.gen.GENMessage;
import esa.mo.mal.transport.gen.GENMessageHeader;
import esa.mo.mal.transport.gen.body.GENMessageBody;
import nasa.mo.mal.transport.http.HttpTransport;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * @author wphyo
 *         Created on 7/14/17.
 */
public class ResponseMessageSenderTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private HttpTransport transport;
    @Mock
    private HttpExchange exchange;
    @Mock
    private GENMessageHeader header;
    @Mock
    private GENMessage malMessage;
    @Mock
    private GENMessageBody body;
    private ResponseMessageSender sender;
    private Map<String, String> headerMap;

    @Before
    public void setUp() throws Exception {
        headerMap = new HashMap<>();
        sender = new ResponseMessageSender(transport, malMessage, String.valueOf("").getBytes(), headerMap);
    }

    /**
     * Null Message Exception testing
     * Expecting to throw an exception
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void nullMessageTest01() throws Exception {
        malMessage = null;
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Null MAL Message.");
        sender.call();
    }

    /**
     * Null Message header Exception testing
     * Expecting to throw an exception
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void nullMessageTest02() throws Exception {
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Null MAL Message.");
        sender.call();
    }


    /**
     * Null Message body Exception testing
     * Expecting to throw an exception
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void nullMessageTest03() throws Exception {
        doReturn(header).when(malMessage).getHeader();
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Null MAL Message.");
        sender.call();
    }

    /**
     * Testing exception when Exchange is empty.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void nullExchangeTest01() throws Exception {
        doReturn(header).when(malMessage).getHeader();
        doReturn(body).when(malMessage).getBody();
        doReturn(null).when(transport).getResponseMessage(header);
        thrown.expect(MALTransmitErrorException.class);
        thrown.expectMessage("Empty Http Exchange to reply");
        sender.call();
    }

    /**
     * reply from MAL is an error message with Bad-Encoding error. So there should be a response with bad-request.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void test02() throws Exception {
        doReturn(header).when(malMessage).getHeader();
        doReturn(exchange).when(transport).getResponseMessage(header);
        doReturn(true).when(header).getIsErrorMessage();
        doReturn(body).when(malMessage).getBody();
        doReturn(MALHelper.BAD_ENCODING_ERROR_NUMBER).when(body).getBodyElement(eq(0), any(UInteger.class));
        doReturn(new ByteArrayOutputStream()).when(exchange).getResponseBody();
        sender.call();
        verify(exchange, times(1)).sendResponseHeaders(400, 0);
    }

    /**
     * reply from MAL is an error message but exception is thrown when getting MAL error number from MAL body.
     * use default 500 to respond.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void test03() throws Exception {
        doReturn(header).when(malMessage).getHeader();
        doReturn(exchange).when(transport).getResponseMessage(header);
        doReturn(true).when(header).getIsErrorMessage();
        doThrow(new MALException("Testing MAL Exception")).when(body).getBodyElement(eq(0), any(UInteger.class));
        doReturn(body).when(malMessage).getBody();
        doReturn(new ByteArrayOutputStream()).when(exchange).getResponseBody();
        sender.call();
        verify(exchange, times(1)).sendResponseHeaders(500, 0);
    }

    /**
     * Null header in MAL Message. Expecting Exception
     * Expecting to throw an exception
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void test04() throws Exception {
        doReturn(null).when(malMessage).getHeader();
        thrown.expect(MALTransmitErrorException.class);
        sender.call();
    }

    /**
     * Null body in MAL Message. Expecting Exception
     * Expecting to throw an exception
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void test05() throws Exception {
        doReturn(header).when(malMessage).getHeader();
        doReturn(null).when(malMessage).getBody();
        thrown.expect(MALTransmitErrorException.class);
        sender.call();
    }

    /**
     * reply from MAL is NOT error message.
     * Reply is Invoke, ACk stage.
     * Response with code 202, Accepted.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void test06() throws Exception {
        doReturn(header).when(malMessage).getHeader();
        doReturn(InteractionType.INVOKE).when(header).getInteractionType();
        doReturn(new UOctet((short) 2)).when(header).getInteractionStage();
        doReturn(exchange).when(transport).getResponseMessage(header);
        doReturn(false).when(header).getIsErrorMessage();
        doReturn(body).when(malMessage).getBody();
        doReturn(new ByteArrayOutputStream()).when(exchange).getResponseBody();
        sender.call();
        verify(exchange, times(1)).sendResponseHeaders(202, 0);
    }

    /**
     * reply from MAL is NOT error message.
     * Reply is Submit, ACk stage.
     * Response with code 200, OK.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void test07() throws Exception {
        doReturn(header).when(malMessage).getHeader();
        doReturn(InteractionType.SUBMIT).when(header).getInteractionType();
        doReturn(new UOctet((short) 2)).when(header).getInteractionStage();
        doReturn(exchange).when(transport).getResponseMessage(header);
        doReturn(false).when(header).getIsErrorMessage();
        doReturn(body).when(malMessage).getBody();
        doReturn(MALHelper.BAD_ENCODING_ERROR_NUMBER).when(body).getBodyElement(eq(0), any(UInteger.class));
        doReturn(new ByteArrayOutputStream()).when(exchange).getResponseBody();
        doThrow(new IOException("Testing IO Exception")).when(exchange).sendResponseHeaders(200, 0);
        thrown.expect(MALTransmitErrorException.class);
        sender.call();
    }

    /**
     * Testing if malhttp is switched to http or https respectively.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void malHttpToHttpTest01() throws Exception {
        doReturn(header).when(malMessage).getHeader();
        doReturn(InteractionType.SUBMIT).when(header).getInteractionType();
        doReturn(new UOctet((short) 2)).when(header).getInteractionStage();
        doReturn(exchange).when(transport).getResponseMessage(header);
        doReturn(false).when(header).getIsErrorMessage();
        doReturn(body).when(malMessage).getBody();

        headerMap.put(HttpHeaderKeys.HOST.toString(), "malhttp://localhost:8080");
        doReturn(false).when(transport).isSecureServer();
        try {
            sender.call();
            Assert.assertEquals("http://localhost:8080", headerMap.get(HttpHeaderKeys.HOST.toString()));
        } catch (NullPointerException exp) {
            // expecting this. do nothing
        }

        headerMap.put(HttpHeaderKeys.HOST.toString(), "malhttp://localhost:8080");
        doReturn(true).when(transport).isSecureServer();
        try {
            sender.call();
            Assert.assertEquals("https://localhost:8080", headerMap.get(HttpHeaderKeys.HOST.toString()));
        } catch (NullPointerException exp) {
            // expecting this. do nothing
        }
    }
}
