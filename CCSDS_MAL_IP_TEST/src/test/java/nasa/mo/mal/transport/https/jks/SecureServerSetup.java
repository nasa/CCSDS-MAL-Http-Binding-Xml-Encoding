/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.https.jks;

import nasa.mo.mal.transport.http.HttpEndpoint;
import nasa.mo.mal.transport.http.HttpTransport;
import nasa.mo.mal.transport.http.HttpTransportFactoryImpl;
import nasa.mo.mal.transport.http.util.Constants;
import nasa.mo.mal.transport.https.BasicSecureServerSetup;

/**
 * @author wphyo
 *         Created on 7/26/17.
 */
public class SecureServerSetup extends BasicSecureServerSetup {
    public SecureServerSetup() throws Exception {
        super();
        serverProperties.put(Constants.KEY_STORE_FILE_KEY, "./server.jks");
        serverProperties.put(Constants.TRUST_STORE_FILE_KEY, "./server-trust.jks");
        serverProperties.put(Constants.KEY_STORE_TYPE, "JKS");
        serverTransport = (HttpTransport) new HttpTransportFactoryImpl("malhttp").createTransport(null, serverProperties);
        serverEndpoint = (HttpEndpoint) serverTransport.createEndpoint("Service1", serverProperties);
    }
}
