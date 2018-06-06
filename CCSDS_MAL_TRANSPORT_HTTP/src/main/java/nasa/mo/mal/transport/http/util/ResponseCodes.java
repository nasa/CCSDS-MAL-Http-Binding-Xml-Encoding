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

/**
 * @author Wai Phyo
 *         Created on 5/1/17.
 *
 * Http Response Status codes for possible Interactions.
 * Based on WhiteBook.
 */
public enum ResponseCodes {
    OK(200),
    ACCEPTED(202),
    OTHER(204),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    REQUEST_TIMEOUT(408),
    GONE(410),
    TOO_MANY_REQUEST(429),
    INTERNAL_SERVER_ERROR(500),
    NOT_IMPLEMENTED(501),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503),
    GATEWAY_TIMEOUT(504),
    NETWORK_AUTHENTICATION_REQUIRED(511)
    ;

    private int code;
    ResponseCodes(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }
    public String toString() {
        return Integer.valueOf(code).toString();
    }
}
