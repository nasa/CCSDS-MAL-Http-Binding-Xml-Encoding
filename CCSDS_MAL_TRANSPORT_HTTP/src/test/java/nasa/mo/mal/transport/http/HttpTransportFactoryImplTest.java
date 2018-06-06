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

import org.ccsds.moims.mo.mal.MALException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;

/**
 * @author wphyo
 *         Created on 7/28/17.
 */
public class HttpTransportFactoryImplTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private HttpTransportFactoryImpl factory;

    @Before
    public void setUp() {
        factory = new HttpTransportFactoryImpl("http");
    }

    /**
     * Exception when Encoding Factory is not registered.
     * Checking error.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void createTransportTest01() throws Exception {
        thrown.expect(MALException.class);
        thrown.expectMessage("Unknown encoding factory for protocol: http");
        factory.createTransport(null, null);
    }

    /**
     * Success.
     * Creating client transport
     *
     * When creating twice, returning same transport.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void createTransportTest02() throws Exception {
        System.setProperty("org.ccsds.moims.mo.mal.encoding.protocol.http", "nasa.mo.mal.transport.http.CustomStreamFactory");
        HttpTransport transport = (HttpTransport) factory.createTransport(null, null);
        Assert.assertTrue(transport.getStreamFactory() instanceof CustomStreamFactory);

        HttpTransport transport2 = (HttpTransport) factory.createTransport(null, new HashMap());

        Assert.assertTrue( transport == transport2);
        System.getProperties().remove("org.ccsds.moims.mo.mal.encoding.protocol.http");
    }
}
