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

/**
 * @author Wai Phyo
 *         Created on 4/5/17.
 */
public interface HttpHeaderMapper {
    public String mapContentType(boolean isXMLEncoded) throws MALException;
    public String mapContentLength(long byteLength) throws MALException;
    public String mapEncoding(int encodingId) throws MALException;
    public String mapVersionNumber() throws MALException;
}
