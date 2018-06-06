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

import nasa.mo.mal.transport.http.util.Constants;
import nasa.mo.mal.transport.http.util.ResponseCodes;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.ccsds.moims.mo.mal.MALException;
import org.junit.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wphyo
 *         Created on 7/5/17.
 */
public class HttpTransportIpV6Test {
    private HttpTransport transport;
    private Map<String, String> properties;
    private static HttpTransportFactoryImpl factory;
    private String ipV6LocalHost = "[0000:0000:0000:0000:0000:0000:0000:0001]";
    private int availablePort;
    @BeforeClass
    public static void start() {
        System.setProperty("org.ccsds.moims.mo.mal.encoding.protocol.malhttp", "nasa.mo.mal.encoder.xml.XmlStreamFactory");
        factory = new HttpTransportFactoryImpl("malhttp");
    }

    @Before
    public void setUp() throws MALException {
        availablePort = MessageTestBase.getAvailablePort();
        properties = new HashMap<>();
        properties.put(Constants.SERVER_HOST_KEY, ipV6LocalHost);
        properties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(availablePort).toString());
        transport = (HttpTransport) factory.createTransport(null, properties);
    }

    @After
    public void cleanUp() throws MALException {
        transport.close();
    }

    @Test
    public void test01() throws IOException {
        HttpGet get = new HttpGet("http://" + ipV6LocalHost + ":" + availablePort + "/");
        try (CloseableHttpClient client = HttpClientBuilder.create().build();
             CloseableHttpResponse response = client.execute(get);) {
            Assert.assertEquals(ResponseCodes.METHOD_NOT_ALLOWED.getCode(), response.getStatusLine().getStatusCode());
        }
    }
}
