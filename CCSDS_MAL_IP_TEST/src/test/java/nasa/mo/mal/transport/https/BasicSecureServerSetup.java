/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.https;

import nasa.mo.mal.transport.ServerSetup;
import nasa.mo.mal.transport.http.HttpEndpoint;
import nasa.mo.mal.transport.http.HttpTransport;
import nasa.mo.mal.transport.http.MessageTestBase;
import nasa.mo.mal.transport.http.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wphyo
 *         Created on 7/26/17.
 */
public class BasicSecureServerSetup extends ServerSetup {
    protected HttpTransport serverTransport;
    protected HttpEndpoint serverEndpoint;
    protected Map<String, String> serverProperties;
    protected String targetURI;
    public static Map<String, String> clientPropertiesWithoutServer = new HashMap<>();
    public static Map<String, String> clientProperties = new HashMap<>();

    static {
        clientPropertiesWithoutServer.put(Constants.KEY_STORE_PWD_FILE_KEY, "./store.pass");
        clientPropertiesWithoutServer.put(Constants.KEY_STORE_FILE_KEY, "./client1.jks");
        clientPropertiesWithoutServer.put(Constants.TRUST_STORE_PWD_FILE_KEY, "./store.pass");
        clientPropertiesWithoutServer.put(Constants.TRUST_STORE_FILE_KEY, "./client1-trust.jks");
        clientPropertiesWithoutServer.put(Constants.KEY_STORE_TYPE, "JKS");
    }

    public BasicSecureServerSetup() throws Exception {
        int freePort = MessageTestBase.getAvailablePort();

        targetURI = "malhttp://127.0.0.1:" + freePort + "/Service1";

        serverProperties = new HashMap<>();
        serverProperties.put(Constants.SERVER_HOST_KEY, "127.0.0.1");
        serverProperties.put(Constants.SERVER_PORT_KEY, Integer.valueOf(freePort).toString());
        serverProperties.put(Constants.KEY_STORE_PWD_FILE_KEY, "./store.pass");
        serverProperties.put(Constants.TRUST_STORE_PWD_FILE_KEY, "./store.pass");
        serverProperties.put(Constants.HTTP_DESTINATION_ENDPOINT_KEY, targetURI);
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
