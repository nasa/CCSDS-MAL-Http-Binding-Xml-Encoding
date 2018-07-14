/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.http.util;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author wphyo
 *         Created on 6/20/17.
 *         <p>
 *         Simple tests for HttpTransportHelper class
 */
public class HttpTransportHelperTest {
    @Test
    public void defaultReplyTest() {
        Assert.assertTrue(HttpTransportHelper.isDefaultReply(InteractionType.SEND, new UOctet((short) 2)));
        Assert.assertTrue(HttpTransportHelper.isDefaultReply(InteractionType.SEND, new UOctet((short) 23)));

        Assert.assertTrue(HttpTransportHelper.isDefaultReply(InteractionType.INVOKE, new UOctet((short) 3)));
        Assert.assertFalse(HttpTransportHelper.isDefaultReply(InteractionType.INVOKE, new UOctet((short) 1)));

        Assert.assertTrue(HttpTransportHelper.isDefaultReply(InteractionType.PROGRESS, new UOctet((short) 3)));
        Assert.assertTrue(HttpTransportHelper.isDefaultReply(InteractionType.PROGRESS, new UOctet((short) 4)));
        Assert.assertFalse(HttpTransportHelper.isDefaultReply(InteractionType.PROGRESS, new UOctet((short) 5)));
        Assert.assertFalse(HttpTransportHelper.isDefaultReply(InteractionType.PROGRESS, new UOctet((short) 1)));
        Assert.assertFalse(HttpTransportHelper.isDefaultReply(InteractionType.PROGRESS, new UOctet((short) 2)));

        Assert.assertTrue(HttpTransportHelper.isDefaultReply(InteractionType.PUBSUB, new UOctet((short) 5)));
        Assert.assertTrue(HttpTransportHelper.isDefaultReply(InteractionType.PUBSUB, new UOctet((short) 6)));
        Assert.assertFalse(HttpTransportHelper.isDefaultReply(InteractionType.PUBSUB, new UOctet((short) 3)));
        Assert.assertFalse(HttpTransportHelper.isDefaultReply(InteractionType.PUBSUB, new UOctet((short) 4)));
        Assert.assertFalse(HttpTransportHelper.isDefaultReply(InteractionType.PUBSUB, new UOctet((short) 7)));

    }

    @Test
    public void responseReplyTest() {
        Assert.assertTrue(HttpTransportHelper.isResponseReply(InteractionType.PUBSUB, new UOctet((short) 2)));
        Assert.assertTrue(HttpTransportHelper.isResponseReply(InteractionType.PUBSUB, new UOctet((short) 4)));
        Assert.assertTrue(HttpTransportHelper.isResponseReply(InteractionType.PUBSUB, new UOctet((short) 8)));
        Assert.assertTrue(HttpTransportHelper.isResponseReply(InteractionType.PUBSUB, new UOctet((short) 10)));
        Assert.assertFalse(HttpTransportHelper.isResponseReply(InteractionType.PUBSUB, new UOctet((short) 9)));

        Assert.assertTrue(HttpTransportHelper.isResponseReply(InteractionType.INVOKE, new UOctet((short) 2)));
        Assert.assertFalse(HttpTransportHelper.isResponseReply(InteractionType.INVOKE, new UOctet((short) 3)));

        Assert.assertTrue(HttpTransportHelper.isResponseReply(InteractionType.REQUEST, new UOctet((short) 2)));
        Assert.assertFalse(HttpTransportHelper.isResponseReply(InteractionType.REQUEST, new UOctet((short) 1)));

        Assert.assertTrue(HttpTransportHelper.isResponseReply(InteractionType.SUBMIT, new UOctet((short) 2)));
        Assert.assertFalse(HttpTransportHelper.isResponseReply(InteractionType.SUBMIT, new UOctet((short) 1)));

        Assert.assertTrue(HttpTransportHelper.isResponseReply(InteractionType.PROGRESS, new UOctet((short) 2)));
        Assert.assertFalse(HttpTransportHelper.isResponseReply(InteractionType.PROGRESS, new UOctet((short) 4)));
        Assert.assertFalse(HttpTransportHelper.isResponseReply(InteractionType.PROGRESS, new UOctet((short) 5)));
    }

    /**
     * Testing REGEX for port only 1 to 65535
     * 1.   alphabet = false
     * 2.   empty string = false
     * 3.   number with alphabet = false
     * 4.   negative numbers = false
     * 5.   0 to 255 = true
     * 6.   256 onward = false
     * 7.   invalid numbers = false
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void portTest01() throws Exception {
        Field field = HttpTransportHelper.class.getDeclaredField("PORT");
        field.setAccessible(true);
        String port = (String) field.get(String.class);
        Pattern portPattern = Pattern.compile("^" + port + "$");
        Assert.assertFalse(portPattern.matcher("alphabet").matches());
        Assert.assertFalse(portPattern.matcher("").matches());
        Assert.assertFalse(portPattern.matcher("12e").matches());
        Assert.assertFalse(portPattern.matcher("a12").matches());


        for (int i = -255; i < 1; i++) {
            Assert.assertFalse(portPattern.matcher(Integer.valueOf(i).toString()).matches());
        }

        for (int i = 1; i < 65536; i++) {
            if (! portPattern.matcher(Integer.valueOf(i).toString()).matches()) {
                System.out.println(i);
            }
            Assert.assertTrue(portPattern.matcher(Integer.valueOf(i).toString()).matches());
        }

        for (int i = 65536; i < 70001; i++) {
            Assert.assertFalse(portPattern.matcher(Integer.valueOf(i).toString()).matches());
        }
        Assert.assertFalse(portPattern.matcher("00").matches());
        Assert.assertFalse(portPattern.matcher("01").matches());
        Assert.assertFalse(portPattern.matcher("0100").matches());
    }

    /**
     * TODO Update Unit test and add more unit tests.
     */
    @Test
    public void ip4Test01() {
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://127.0.0.1:8080/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://0.0.0.0:8080/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://255.255.255.255:8080/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://127.0.0.1:8080/Test"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://0.0.0.0:8080/Test"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://255.255.255.255:8080/Test"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttps://127.0.0.1:8080/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttps://0.0.0.0:8080/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttps://255.255.255.255:8080/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttp://255.255.255.255:abcd/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malgttp://255.255.255.255:8080/"));

        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://127.0.0.1:8080/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://0.0.0.0:8080/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://255.255.255.255:8080/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://127.0.0.1:8080/Test"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://0.0.0.0:8080/Test"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://255.255.255.255:8080/Test"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttps://127.0.0.1:8080/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttps://0.0.0.0:8080/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttps://255.255.255.255:8080/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttp://255.255.255.255:abcd/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("mahttp://255.255.255.255:8080/"));
    }

    /**
     * Testing IP-V6
     * using examples from RFC 4291 Section 2.2 based on Red-Book
     * https://www.ietf.org/rfc/rfc4291.txt
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void ip6Test01() throws Exception {
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[2001:DB8:0:0:8:800:200C:417A]:8000/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttp://[2001:DB8:0:0:8:800:200C:417A]:abcd/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[2001:DB8:0:0:8:800:200C:417A]:8000/Test"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[2001:DB8:0:0:8:800:200C:417A]:8000/Test1"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttps://[2001:DB8:0:0:8:800:200C:417A]:8000/Test1"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttp://[2001:DB8:0:0:8:800:200C:417A:1]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[FF01:0:0:0:0:0:0:101]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[0:0:0:0:0:0:0:1]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[0:0:0:0:0:0:0:0]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[2001:DB8::8:800:200C:417A]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[FF01::101]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[::1]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[0:0:0:0:0:0:13.1.68.3]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[0:0:0:0:0:FFFF:129.144.52.38]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[::13.1.68.3]:8000/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttp://[::13.1.680.3]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[::FFFF:129.144.52.38]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[::ffff:129.144.52.38]:8000/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttp://[::gfff:129.144.52.38]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[::1]:3009"));

        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[2001:DB8:0:0:8:800:200C:417A]:8000/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttp://[2001:DB8:0:0:8:800:200C:417A]:abcd/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[2001:DB8:0:0:8:800:200C:417A]:8000/Test"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[2001:DB8:0:0:8:800:200C:417A]:8000/Test1"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttps://[2001:DB8:0:0:8:800:200C:417A]:8000/Test1"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttp://[2001:DB8:0:0:8:800:200C:417A:1]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[FF01:0:0:0:0:0:0:101]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[0:0:0:0:0:0:0:1]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[0:0:0:0:0:0:0:0]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[2001:DB8::8:800:200C:417A]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[FF01::101]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[::1]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[0:0:0:0:0:0:13.1.68.3]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[0:0:0:0:0:FFFF:129.144.52.38]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[::13.1.68.3]:8000/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttp://[::13.1.680.3]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[::FFFF:129.144.52.38]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[::ffff:129.144.52.38]:8000/"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("malhttp://[::gfff:129.144.52.38]:8000/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://[::1]:3009"));

        Assert.assertFalse(HttpTransportHelper.isValidURI("mahttp://[2001:DB8:0:0:8:800:200C:417A]:8000/"));
    }

    /**
     * Testing HostNames
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void hostNameTest01() throws Exception {
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://localhost:8008/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://localhost:8008"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://speed.of.me:8008"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://123Test.localhost:8008"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://123Test.456.localhost:8008"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://123Test.456-2.localhost:8008"));
        Assert.assertFalse(HttpTransportHelper.isValidURI("fakeHttp://localhost:8080/Service2"));

        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://localhost:8008/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://malhttp-test:8008/"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://localhost:8008"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://speed.of.me:8008"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://123Test.localhost:8008"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://123Test.456.localhost:8008"));
        Assert.assertTrue(HttpTransportHelper.isValidURI("malhttp://123Test.456-2.localhost:8008"));
    }

    /**
     * Testing correct MAL error codes based on http error codes
     * @throws Exception any unexpected exception
     */
    @Test
    public void getMALErrorFromHttpTest01() throws Exception {
        Assert.assertEquals(MALHelper.BAD_ENCODING_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(400));
        Assert.assertEquals(MALHelper.AUTHORISATION_FAIL_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(401));
        Assert.assertEquals(MALHelper.AUTHORISATION_FAIL_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(403));
        Assert.assertEquals(MALHelper.DESTINATION_UNKNOWN_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(404));
        Assert.assertEquals(MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(405));
        Assert.assertEquals(MALHelper.DELIVERY_TIMEDOUT_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(408));
        Assert.assertEquals(MALHelper.DESTINATION_TRANSIENT_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(410));
        Assert.assertEquals(MALHelper.TOO_MANY_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(429));
        Assert.assertEquals(MALHelper.INTERNAL_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(500));
        Assert.assertEquals(MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(501));
        Assert.assertEquals(MALHelper.DELIVERY_FAILED_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(502));
        Assert.assertEquals(MALHelper.DESTINATION_TRANSIENT_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(503));
        Assert.assertEquals(MALHelper.DELIVERY_TIMEDOUT_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(504));
        Assert.assertEquals(MALHelper.AUTHENTICATION_FAIL_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(511));
        Assert.assertEquals(MALHelper.INTERNAL_ERROR_NUMBER, HttpTransportHelper.getMALErrorFromHttp(533));
    }

    /**
     * Testing exception when passing invalid error code
     * @throws Exception any unexpected exception
     */
    @Test(expected = RuntimeException.class)
    public void getMALErrorFromHttpTest02() throws Exception {
        HttpTransportHelper.getMALErrorFromHttp(200);
    }

    /**
     * Testing correct Http Response Codes based on MAL Helper
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void getStatusCodeFromMALErrorTest01() throws Exception {
        Assert.assertEquals(ResponseCodes.NETWORK_AUTHENTICATION_REQUIRED, HttpTransportHelper.getStatusCodeFromMALError(MALHelper.AUTHENTICATION_FAIL_ERROR_NUMBER));
        Assert.assertEquals(ResponseCodes.FORBIDDEN, HttpTransportHelper.getStatusCodeFromMALError(MALHelper.AUTHORISATION_FAIL_ERROR_NUMBER));
        Assert.assertEquals(ResponseCodes.GATEWAY_TIMEOUT, HttpTransportHelper.getStatusCodeFromMALError(MALHelper.DELIVERY_TIMEDOUT_ERROR_NUMBER));
        Assert.assertEquals(ResponseCodes.SERVICE_UNAVAILABLE, HttpTransportHelper.getStatusCodeFromMALError(MALHelper.DESTINATION_TRANSIENT_ERROR_NUMBER));
        Assert.assertEquals(ResponseCodes.BAD_GATEWAY, HttpTransportHelper.getStatusCodeFromMALError(MALHelper.DELIVERY_FAILED_ERROR_NUMBER));
        Assert.assertEquals(ResponseCodes.NOT_IMPLEMENTED, HttpTransportHelper.getStatusCodeFromMALError(MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER));
        Assert.assertEquals(ResponseCodes.INTERNAL_SERVER_ERROR, HttpTransportHelper.getStatusCodeFromMALError(MALHelper.INTERNAL_ERROR_NUMBER));
        Assert.assertEquals(ResponseCodes.TOO_MANY_REQUEST, HttpTransportHelper.getStatusCodeFromMALError(MALHelper.TOO_MANY_ERROR_NUMBER));
        Assert.assertEquals(ResponseCodes.NOT_FOUND, HttpTransportHelper.getStatusCodeFromMALError(MALHelper.DESTINATION_UNKNOWN_ERROR_NUMBER));
        Assert.assertEquals(ResponseCodes.BAD_REQUEST, HttpTransportHelper.getStatusCodeFromMALError(MALHelper.BAD_ENCODING_ERROR_NUMBER));
        Assert.assertEquals(ResponseCodes.INTERNAL_SERVER_ERROR, HttpTransportHelper.getStatusCodeFromMALError(new UInteger(4294967295L)));
    }

    /**
     * Testing Http Response codes for valid Interaction stage and type
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void getHttpResponseCodeTest01() throws Exception {
        Assert.assertEquals(204, HttpTransportHelper.getHttpResponseCode(InteractionType.SEND, null));
        Assert.assertEquals(200, HttpTransportHelper.getHttpResponseCode(InteractionType.SUBMIT, new UOctet((short) 2)));
        Assert.assertEquals(200, HttpTransportHelper.getHttpResponseCode(InteractionType.REQUEST, new UOctet((short) 2)));
        Assert.assertEquals(202, HttpTransportHelper.getHttpResponseCode(InteractionType.INVOKE, new UOctet((short) 2)));
        Assert.assertEquals(200, HttpTransportHelper.getHttpResponseCode(InteractionType.INVOKE, new UOctet((short) 3)));
        Assert.assertEquals(200, HttpTransportHelper.getHttpResponseCode(InteractionType.PROGRESS, new UOctet((short) 2)));
        Assert.assertEquals(204, HttpTransportHelper.getHttpResponseCode(InteractionType.PROGRESS, new UOctet((short) 3)));
        Assert.assertEquals(204, HttpTransportHelper.getHttpResponseCode(InteractionType.PROGRESS, new UOctet((short) 4)));
        Assert.assertEquals(200, HttpTransportHelper.getHttpResponseCode(InteractionType.PUBSUB, new UOctet((short) 2)));
        Assert.assertEquals(200, HttpTransportHelper.getHttpResponseCode(InteractionType.PUBSUB, new UOctet((short) 4)));
        Assert.assertEquals(200, HttpTransportHelper.getHttpResponseCode(InteractionType.PUBSUB, new UOctet((short) 8)));
        Assert.assertEquals(200, HttpTransportHelper.getHttpResponseCode(InteractionType.PUBSUB, new UOctet((short) 10)));
        Assert.assertEquals(204, HttpTransportHelper.getHttpResponseCode(InteractionType.PUBSUB, new UOctet((short) 5)));
        Assert.assertEquals(204, HttpTransportHelper.getHttpResponseCode(InteractionType.PUBSUB, new UOctet((short) 6)));
    }

    /**
     * Testing exception for invalid Interaction type and stage
     * expecting MALException
     *
     * @throws Exception any unexpected exception
     */
    @Test(expected = MALException.class)
    public void getHttpResponseCodeTest02() throws Exception {
        HttpTransportHelper.getHttpResponseCode(InteractionType.SUBMIT, new UOctet((short) 1));
    }

    /**
     * Testing exception for invalid Interaction type and stage
     * expecting MALException
     *
     * @throws Exception any unexpected exception
     */
    @Test(expected = MALException.class)
    public void getHttpResponseCodeTest03() throws Exception {
        HttpTransportHelper.getHttpResponseCode(InteractionType.SUBMIT, new UOctet((short) 100));
    }

    /**
     * Testing exception for invalid Interaction type and stage
     * expecting MALException
     *
     * @throws Exception any unexpected exception
     */
    @Test(expected = MALException.class)
    public void getHttpResponseCodeTest04() throws Exception {
        HttpTransportHelper.getHttpResponseCode(InteractionType.REQUEST, new UOctet((short) 1));
    }

    /**
     * Testing exception for invalid Interaction type and stage
     * expecting MALException
     *
     * @throws Exception any unexpected exception
     */
    @Test(expected = MALException.class)
    public void getHttpResponseCodeTest05() throws Exception {
        HttpTransportHelper.getHttpResponseCode(InteractionType.REQUEST, new UOctet((short) 100));
    }

    /**
     * Testing exception for invalid Interaction type and stage
     * expecting MALException
     *
     * @throws Exception any unexpected exception
     */
    @Test(expected = MALException.class)
    public void getHttpResponseCodeTest06() throws Exception {
        HttpTransportHelper.getHttpResponseCode(InteractionType.INVOKE, new UOctet((short) 1));
    }

    /**
     * Testing exception for invalid Interaction type and stage
     * expecting MALException
     *
     * @throws Exception any unexpected exception
     */
    @Test(expected = MALException.class)
    public void getHttpResponseCodeTest07() throws Exception {
        HttpTransportHelper.getHttpResponseCode(InteractionType.INVOKE, new UOctet((short) 100));
    }

    @Mock
    private HttpExchange exchange;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Testing Filling Http Exchange object with response
     * expecting exception with message since exchange object is null
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void fillResponseTest01() throws Exception {
        thrown.expect(MALException.class);
        thrown.expectMessage("Null Http Exchange Object.");
        HttpTransportHelper.fillResponse(null, 100, null, null);
    }

    /**
     * Testing Filling Http Exchange object with response
     * return code is OTHER. so BODY is empty.
     * expecting to call 204 with -1 for body length
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void fillResponseTest02() throws Exception {
        doReturn(new ByteArrayOutputStream()).when(exchange).getResponseBody();
        HttpTransportHelper.fillResponse(exchange, 204, null, null);
        verify(exchange, times(1)).sendResponseHeaders(204, -1);
    }

    /**
     * Testing Filling Http Exchange object with response
     * return code is ACCEPTED. Since BODY is null, create an empty body
     * expecting to call 200 with 0 for body length
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void fillResponseTest03() throws Exception {
        doReturn(new ByteArrayOutputStream()).when(exchange).getResponseBody();
        HttpTransportHelper.fillResponse(exchange, 200, null, null);
        verify(exchange, times(1)).sendResponseHeaders(200, 0);
    }

    /**
     * Testing Filling Http Exchange object with response
     * return code is ACCEPTED. Since BODY has some length
     * expecting to call 204 with body length
     * Compare accepted byte and sending byte.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void fillResponseTest04() throws Exception {
        byte[] body = new byte[] {1, 2, 3};
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        doReturn(result).when(exchange).getResponseBody();
        HttpTransportHelper.fillResponse(exchange, 200, body, null);
        verify(exchange, times(1)).sendResponseHeaders(200, 3);
        byte[] fromResult = result.toByteArray();
        for (int i = 0; i < body.length; i++) {
            Assert.assertEquals(body[i], fromResult[i]);
        }
    }

    /**
     * Testing Filling Http Exchange object with response
     * return code is ACCEPTED. Since BODY has some length
     * expecting to call 204 with body length
     * Compare accepted byte and sending byte.
     *
     * Include the headers which expects to call Response's header to fill the values.
     * And compare the results.
     *
     * @throws Exception any unexpected exception
     */
    @Test
    public void fillResponseTest05() throws Exception {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Abc", "def");
        headerMap.put("Ghi", "jkl");
        Headers headers = new Headers();
        doReturn(headers).when(exchange).getResponseHeaders();
        doReturn(new ByteArrayOutputStream()).when(exchange).getResponseBody();
        HttpTransportHelper.fillResponse(exchange, 200, null, headerMap);
        verify(exchange, times(1)).sendResponseHeaders(200, 0);
        headerMap.keySet().forEach(e -> Assert.assertEquals(headerMap.get(e), headers.getFirst(e)));
    }
}
