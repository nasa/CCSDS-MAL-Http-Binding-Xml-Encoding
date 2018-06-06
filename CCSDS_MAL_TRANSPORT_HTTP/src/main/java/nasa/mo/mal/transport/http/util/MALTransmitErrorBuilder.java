/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.http.util;

import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;

import java.util.Map;

/**
 * @author wphyo
 *         Created on 7/12/17.
 * Builder class to create MAL-Transmit-Error-Exception
 * Default Error is Internal-Error.
 */
public class MALTransmitErrorBuilder {
    private MALMessageHeader header;
    private Map qosProperties;
    private UInteger errorNumber;
    private Object extraInfo;
    private MALStandardError error;

    private MALTransmitErrorBuilder() {
        header = null;
        qosProperties = null;
        errorNumber = MALHelper.INTERNAL_ERROR_NUMBER;
        extraInfo = null;
        error = null;
    }

    public MALTransmitErrorBuilder setHeader(MALMessageHeader header) {
        this.header = header;
        return this;
    }

    public MALTransmitErrorBuilder setProperties(Map qosProperties) {
        this.qosProperties = qosProperties;
        return this;
    }

    public MALTransmitErrorBuilder setErrorNumber(UInteger errorNumber) {
        this.errorNumber = errorNumber;
        return this;
    }

    public MALTransmitErrorBuilder setExtraInfo(Object extraInfo) {
        this.extraInfo = extraInfo;
        return this;
    }

    public MALTransmitErrorBuilder setError(MALStandardError error) {
        this.error = error;
        return this;
    }

    public MALTransmitErrorException build() {
        if (error == null) {
            error = new MALStandardError(errorNumber, extraInfo);
        }
        return new MALTransmitErrorException(header, error, qosProperties);
    }

    public static MALTransmitErrorBuilder create() {
        return new MALTransmitErrorBuilder();
    }
}
