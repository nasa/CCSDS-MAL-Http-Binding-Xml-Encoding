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

import java.util.Map;

/**
 * @author wphyo
 *         Created on 7/26/17.
 */
public abstract class ServerSetup {
    public abstract HttpTransport getServerTransport();

    public abstract HttpEndpoint getServerEndpoint();

    public abstract Map<String, String> getServerProperties();

    public abstract String getTargetURI();
}
