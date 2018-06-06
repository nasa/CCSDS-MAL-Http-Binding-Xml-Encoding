/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.encoder.Header;

import nasa.mo.mal.encoder.util.HeaderMappingHelper;
import nasa.mo.mal.encoder.util.ObjectFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.HashMap;
import java.util.Map;
import static org.mockito.Mockito.*;
/**
 * @author wphyo
 *         Created on 6/27/17.
 *
 * Test cases for Map URI-To logic
 */
public class MalToHttpMapperTest {
    @Mock
    private MALMessageHeader header;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Test
    public void mapUrlToTest1() throws MALException {
        StringBuffer host = ObjectFactory.createStringBuffer();
        StringBuffer requestTarget = ObjectFactory.createStringBuffer();
        StringBuffer mapUrlTo = ObjectFactory.createStringBuffer();

        URI uriTo = new URI("http://localhost:8081/Test1/");
        URI httpEndpoint = new URI("http://localhost:8081/Test1/");

        MalToHttpMapper.getInstance().mapUrlTo(uriTo, httpEndpoint, host, requestTarget, mapUrlTo);

        Assert.assertTrue(host.toString().equals(HeaderMappingHelper.encodeURI("http://localhost:8081")));
        Assert.assertTrue(requestTarget.toString().equals(HeaderMappingHelper.encodeURI("/Test1/")));
        Assert.assertTrue(mapUrlTo.toString().equals(""));
    }

    @Test
    public void mapUrlToTest2() throws MALException {
        StringBuffer host = ObjectFactory.createStringBuffer();
        StringBuffer requestTarget = ObjectFactory.createStringBuffer();
        StringBuffer mapUrlTo = ObjectFactory.createStringBuffer();

        URI uriTo = new URI("http://localhost:8081");
        URI httpEndpoint = new URI("http://localhost:8081");

        MalToHttpMapper.getInstance().mapUrlTo(uriTo, httpEndpoint, host, requestTarget, mapUrlTo);

        Assert.assertTrue(host.toString().equals(HeaderMappingHelper.encodeURI("http://localhost:8081")));
        Assert.assertTrue(requestTarget.toString().equals(HeaderMappingHelper.encodeURI("/")));
        Assert.assertTrue(mapUrlTo.toString().equals(""));
    }

    @Test
    public void mapUrlToTest3() throws MALException {
        StringBuffer host = ObjectFactory.createStringBuffer();
        StringBuffer requestTarget = ObjectFactory.createStringBuffer();
        StringBuffer mapUrlTo = ObjectFactory.createStringBuffer();

        URI uriTo = new URI("http://localhost:8083/Test1/");
        URI httpEndpoint = new URI("http://localhost:8081");

        MalToHttpMapper.getInstance().mapUrlTo(uriTo, httpEndpoint, host, requestTarget, mapUrlTo);

        Assert.assertTrue(host.toString().equals(HeaderMappingHelper.encodeURI("http://localhost:8081")));
        Assert.assertTrue(requestTarget.toString().equals(""));
        Assert.assertTrue(mapUrlTo.toString().equals(HeaderMappingHelper.encodeURI("http://localhost:8083/Test1/")));
    }

    @Test(expected = MALException.class)
    public void mapUrlToTest4() throws MALException {
        StringBuffer host = ObjectFactory.createStringBuffer();
        StringBuffer requestTarget = ObjectFactory.createStringBuffer();
        StringBuffer mapUrlTo = ObjectFactory.createStringBuffer();

        URI uriTo = null;
        URI httpEndpoint = new URI("http://localhost:8081");

        MalToHttpMapper.getInstance().mapUrlTo(uriTo, httpEndpoint, host, requestTarget, mapUrlTo);
    }

    @Test(expected = MALException.class)
    public void mapUrlToTest5() throws MALException {
        StringBuffer host = ObjectFactory.createStringBuffer();
        StringBuffer requestTarget = ObjectFactory.createStringBuffer();
        StringBuffer mapUrlTo = ObjectFactory.createStringBuffer();

        URI uriTo = new URI("http://localhost:8081");
        URI httpEndpoint = null;

        MalToHttpMapper.getInstance().mapUrlTo(uriTo, httpEndpoint, host, requestTarget, mapUrlTo);
    }

    @Test(expected = MALException.class)
    public void mapUrlToTest6() throws MALException {
        StringBuffer host = ObjectFactory.createStringBuffer();
        StringBuffer requestTarget = ObjectFactory.createStringBuffer();
        StringBuffer mapUrlTo = ObjectFactory.createStringBuffer();

        URI uriTo = null;
        URI httpEndpoint = null;

        MalToHttpMapper.getInstance().mapUrlTo(uriTo, httpEndpoint, host, requestTarget, mapUrlTo);
    }

    @Test(expected = MALException.class)
    public void mapUrlToTest7() throws MALException {
        StringBuffer host = ObjectFactory.createStringBuffer();
        StringBuffer requestTarget = ObjectFactory.createStringBuffer();
        StringBuffer mapUrlTo = ObjectFactory.createStringBuffer();

        URI uriTo = new URI(null);
        URI httpEndpoint = new URI("http://localhost:8081");

        MalToHttpMapper.getInstance().mapUrlTo(uriTo, httpEndpoint, host, requestTarget, mapUrlTo);
    }

    @Test(expected = MALException.class)
    public void mapUrlToTest8() throws MALException {
        StringBuffer host = ObjectFactory.createStringBuffer();
        StringBuffer requestTarget = ObjectFactory.createStringBuffer();
        StringBuffer mapUrlTo = ObjectFactory.createStringBuffer();

        URI uriTo = new URI("http://localhost:8081");
        URI httpEndpoint = new URI(null);

        MalToHttpMapper.getInstance().mapUrlTo(uriTo, httpEndpoint, host, requestTarget, mapUrlTo);
    }

    @Test(expected = MALException.class)
    public void mapUrlToTest9() throws MALException {
        StringBuffer host = ObjectFactory.createStringBuffer();
        StringBuffer requestTarget = ObjectFactory.createStringBuffer();
        StringBuffer mapUrlTo = ObjectFactory.createStringBuffer();

        URI uriTo = new URI(null);
        URI httpEndpoint = new URI(null);

        MalToHttpMapper.getInstance().mapUrlTo(uriTo, httpEndpoint, host, requestTarget, mapUrlTo);
    }

    /**
     * Testing Mapped Header logic.
     * 1.   Checking if the values are encoded based on specification
     * 2.   Checking if the key names are correct
     * 3.   Checking correct behavior for URI-TO
     * 4.   Checking correct behavior for xml encoding v. other encodings.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void headerTest01() throws Exception {
        IdentifierList identifiers = new IdentifierList();
        identifiers.add(new Identifier("This"));
        identifiers.add(new Identifier("Is"));
        identifiers.add(new Identifier("A"));
        identifiers.add(new Identifier("Test"));
        Map<String, String> result = new HashMap<>();
        doReturn(new URI("malhttp://localhost:8989/Source")).when(header).getURIFrom();
        doReturn(new URI("malhttp://[::1]:9898/Destination")).when(header).getURITo();
        doReturn(new Blob(new byte[] {1, 2, 3, 4, 10, 11, 12, 13, 14, 15})).when(header).getAuthenticationId();
        doReturn(new Time(1L)).when(header).getTimestamp();
        doReturn(QoSLevel.BESTEFFORT).when(header).getQoSlevel();
        doReturn(new UInteger(969L)).when(header).getPriority();
        doReturn(identifiers).when(header).getDomain();
        doReturn(new Identifier("这是一个测试。")).when(header).getNetworkZone();
        doReturn(SessionType.LIVE).when(header).getSession();
        doReturn(new Identifier("ეს ტესტია.")).when(header).getSessionName();
        doReturn(InteractionType.SEND).when(header).getInteractionType();
        doReturn(new UOctet((short) 2)).when(header).getInteractionStage();
        doReturn(987L).when(header).getTransactionId();
        doReturn(new UShort(98)).when(header).getServiceArea();
        doReturn(new UShort(87)).when(header).getService();
        doReturn(new UShort(76)).when(header).getOperation();
        doReturn(new UOctet((short) 3)).when(header).getAreaVersion();
        doReturn(true).when(header).getIsErrorMessage();
        MalToHttpMapper.getInstance().generateHeader(header, result, new URI("http://127.0.0.1:8080/"), true, null);

        Assert.assertEquals("True", result.get("x-mal-is-error-message"));
        Assert.assertEquals("3", result.get("x-mal-area-version"));
        Assert.assertEquals("76", result.get("x-mal-operation"));
        Assert.assertEquals("87", result.get("x-mal-service"));
        Assert.assertEquals("98", result.get("x-mal-service-area"));
        Assert.assertEquals("987", result.get("x-mal-transaction-id"));
        Assert.assertEquals("2", result.get("x-mal-interaction-stage"));
        Assert.assertEquals("SEND", result.get("x-mal-interaction-type"));
        Assert.assertTrue(result.get("x-mal-session-name").startsWith("=?UTF-8?"));
        Assert.assertEquals("LIVE", result.get("x-mal-session"));
        Assert.assertTrue(result.get("x-mal-network-zone").startsWith("=?UTF-8?"));
        Assert.assertEquals("This.Is.A.Test", result.get("x-mal-domain"));
        Assert.assertEquals("969", result.get("x-mal-priority"));
        Assert.assertEquals("BESTEFFORT", result.get("x-mal-qoslevel"));
        Assert.assertEquals("1970-001T00:00:00.001", result.get("x-mal-timestamp"));
        Assert.assertEquals("010203040a0b0c0d0e0f", result.get("x-mal-authentication-id"));
        Assert.assertEquals("1", result.get("x-mal-version-number"));
        Assert.assertEquals("malhttp%3A%2F%2Flocalhost%3A8989%2FSource", result.get("x-mal-uri-from"));
        Assert.assertEquals("malhttp%3A%2F%2F%5B%3A%3A1%5D%3A9898%2FDestination", result.get("x-mal-uri-to"));
        Assert.assertEquals("application/mal-xml", result.get("content-type"));
        Assert.assertEquals("http%3A%2F%2F127.0.0.1%3A8080", result.get("host"));
        Assert.assertEquals(null, result.get("request-target"));

        result.clear();
        MalToHttpMapper.getInstance().generateHeader(header, result, new URI("malhttp://[::1]:9898/Destination"), false, "Stringstream");
        Assert.assertEquals(null, result.get("x-mal-uri-to"));
        Assert.assertEquals("%2FDestination", result.get("request-target"));
        Assert.assertEquals("malhttp%3A%2F%2F%5B%3A%3A1%5D%3A9898", result.get("host"));
        Assert.assertEquals("application/mal", result.get("content-type"));
        Assert.assertEquals("Stringstream", result.get("x-mal-encoding"));
    }
}
