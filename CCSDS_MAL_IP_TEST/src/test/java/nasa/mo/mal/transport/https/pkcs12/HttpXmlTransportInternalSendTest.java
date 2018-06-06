/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.https.pkcs12;

import nasa.mo.mal.transport.http.HttpTransportInternalSendTest;
import org.junit.BeforeClass;

/**
 * @author wphyo
 *         Created on 7/24/17.
 */
public class HttpXmlTransportInternalSendTest extends HttpTransportInternalSendTest {
    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        System.setProperty("org.ccsds.moims.mo.mal.encoding.protocol.malhttp", "nasa.mo.mal.encoder.xml.XmlStreamFactory");
        manualOneTimeSetUp(new SecureServerSetup());
    }
}
