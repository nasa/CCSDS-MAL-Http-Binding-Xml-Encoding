/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport;

import nasa.mo.mal.transport.http.HttpEndpoint;
import nasa.mo.mal.transport.http.HttpTransport;
import nasa.mo.mal.transport.http.HttpTransportFactoryImpl;
import nasa.mo.mal.transport.http.MessageTestBase;
import nasa.mo.mal.transport.http.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wphyo
 *         Created on 7/26/17.
 */
public class PlainServerSetup extends ServerSetup {
    private HttpTransport serverTransport;
    private HttpEndpoint serverEndpoint;
    private Map<String, String> serverProperties;
    private String targetURI;

    public PlainServerSetup() throws Exception {
        int freePort = MessageTestBase.getAvailablePort();
        targetURI = "malhttp://127.0.0.1:" + freePort + "/Service1";
        serverProperties = new HashMap<>();
        serverProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        serverProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(freePort).toString());
        serverProperties.put(Constants.HTTP_DESTINATION_ENDPOINT_KEY, targetURI);
        serverTransport = (HttpTransport) new HttpTransportFactoryImpl("malhttp").createTransport(null, serverProperties);
        serverEndpoint = (HttpEndpoint) serverTransport.createEndpoint("Service1", serverProperties);
    }

    public HttpTransport getServerTransport() {
        return serverTransport;
    }

    public HttpEndpoint getServerEndpoint() {
        return serverEndpoint;
    }

    public Map<String, String> getServerProperties() {
        return serverProperties;
    }

    public String getTargetURI() {
        return targetURI;
    }


}
