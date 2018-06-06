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

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.junit.Assert;
import org.junit.Before;
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
 *         Created on 7/11/17.
 */
public class HttpToMalMapperTest {
    @Mock
    private MALMessageHeader originalHeader;
    @Mock
    private MALMessageHeader resultHeader;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    private URI uriTo = new URI("http://test:8081/TetService");

    /**
     * Creating a sample MAL Message Header for testing
     * @throws MALException null
     */
    @Before
    public void setUp() throws MALException {
        URI uriFrom = new URI("http://test:8080/");
        Blob auth = new Blob(new byte[] {1,2, 3});
        UShort uShort = new UShort(1);
        UOctet uOctet = new UOctet((short) 1);
        UInteger uInteger = new UInteger(1L);
        Time time = new Time(0L);
        Identifier identifier = new Identifier("Test Identifier");
        IdentifierList identifiers = new IdentifierList();
        identifiers.add(identifier);
        doReturn(uriFrom).when(originalHeader).getURIFrom();
        doReturn(auth).when(originalHeader).getAuthenticationId();
        doReturn(uriTo).when(originalHeader).getURITo();
        doReturn(time).when(originalHeader).getTimestamp();
        doReturn(QoSLevel.ASSURED).when(originalHeader).getQoSlevel();
        doReturn(uInteger).when(originalHeader).getPriority();
        doReturn(identifiers).when(originalHeader).getDomain();
        doReturn(identifier).when(originalHeader).getNetworkZone();
        doReturn(SessionType.LIVE).when(originalHeader).getSession();
        doReturn(identifier).when(originalHeader).getSessionName();
        doReturn(InteractionType.SEND).when(originalHeader).getInteractionType();
        doReturn(uOctet).when(originalHeader).getInteractionStage();
        doReturn(uInteger.getValue()).when(originalHeader).getTransactionId();
        doReturn(uShort).when(originalHeader).getServiceArea();
        doReturn(uShort).when(originalHeader).getService();
        doReturn(uShort).when(originalHeader).getOperation();
        doReturn(uOctet).when(originalHeader).getAreaVersion();
        doReturn(false).when(originalHeader).getIsErrorMessage();
    }

    /**
     * when encoded without an extra URI to compare to.
     * All Host, request-target, URI-TO are missing in the result HashMap
     * When decoded, URI-TO is null
     * @throws MALException null
     */
    @Test
    public void test01() throws MALException {
        Map<String, String> headerMap = new HashMap<>();
        MalToHttpMapper.getInstance().generateHeader(originalHeader, headerMap);
        Assert.assertFalse(headerMap.containsKey(HttpHeaderKeys.URI_TO.toString()));
        HttpToMalMapper.getInstance().fillMalMessageHeader(headerMap, resultHeader);
        verify(resultHeader, times(0)).setURITo(any(URI.class));
    }

    /**
     * when encoded with an extra URI which has the same value,
     * URI-TO is missing.
     * When decoded, URI-TO is original value
     * @throws MALException null
     */
    @Test
    public void test02() throws MALException {
        Map<String, String> headerMap = new HashMap<>();
        MalToHttpMapper.getInstance().generateHeader(originalHeader, headerMap, uriTo, true, null);
        Assert.assertTrue(headerMap.containsKey(HttpHeaderKeys.HOST.toString()));
        Assert.assertTrue(headerMap.containsKey(HttpHeaderKeys.REQUEST_TARGET.toString()));
        Assert.assertFalse(headerMap.containsKey(HttpHeaderKeys.URI_TO.toString()));
        HttpToMalMapper.getInstance().fillMalMessageHeader(headerMap, resultHeader);
        verify(resultHeader, times(1)).setURITo(uriTo);
    }

    /**
     * when encoded with an extra UIR which has different value,
     * URI-TO is present.
     * When decoded, URI-TO is original value
     * @throws MALException null
     */
    @Test
    public void test03() throws MALException {
        Map<String, String> headerMap = new HashMap<>();
        MalToHttpMapper.getInstance().generateHeader(originalHeader, headerMap, new URI("http://test:8081/"), true, null);
        Assert.assertTrue(headerMap.containsKey(HttpHeaderKeys.HOST.toString()));
        Assert.assertFalse(headerMap.containsKey(HttpHeaderKeys.REQUEST_TARGET.toString()));
        Assert.assertTrue(headerMap.containsKey(HttpHeaderKeys.URI_TO.toString()));
        HttpToMalMapper.getInstance().fillMalMessageHeader(headerMap, resultHeader);
        verify(resultHeader, times(1)).setURITo(uriTo);
    }
}
