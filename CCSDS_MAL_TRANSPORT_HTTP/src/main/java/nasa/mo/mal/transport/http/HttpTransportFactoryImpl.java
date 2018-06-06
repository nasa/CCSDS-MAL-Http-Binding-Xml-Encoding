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

import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.transport.MALTransport;
import org.ccsds.moims.mo.mal.transport.MALTransportFactory;

import java.util.Map;

/**
 * @author Wai Phyo
 *         Created on 5/15/17.
 * Http Transport Factory class
 */
public class HttpTransportFactoryImpl extends MALTransportFactory {
    private static final Object MUTEX = new Object();
    private HttpTransport transport = null;

    public HttpTransportFactoryImpl(final String protocol) {
        super(protocol);
    }

    /**
     * The method to instantiate a MALTransport.
     * This method imitates TCP/IP's Factory method.
     *
     * @param malContext The MAL context that is creating the transport, may be null.
     * @param properties Configuration properties
     * @return The transport instance.
     * @throws MALException If no MALTransport can be returned
     */
    @Override
    public MALTransport createTransport(MALContext malContext, Map properties) throws MALException {
        synchronized (MUTEX) {
            if (null == transport) {
                transport = new HttpTransport(getProtocol(), '/', false, false,
                        this, properties);
                transport.init();
            }
            return transport;
        }
    }
}
