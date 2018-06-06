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

/**
 * @author Wai Phyo
 *         Created on 4/3/17.
 *
 * Header Key Names grouped in enumeration.
 * The list can be added or edited if the standards are updated.
 */
public enum HttpHeaderKeys {
    HOST("host", true),
    CONTENT_LENGTH("content-length", true),
    CONTENT_TYPE("content-type", true),
    URI_TO("x-mal-uri-to", false),
    URI_FROM("x-mal-uri-from", true),
    ENCODING("x-mal-encoding", false),
    VERSION("x-mal-version-number", true),
    AUTH_ID("x-mal-authentication-id", true),
    TIMESTAMP("x-mal-timestamp", true),
    QOS_LEVEL("x-mal-qoslevel", true),
    PRIORITY("x-mal-priority", true),
    DOMAIN("x-mal-domain", true),
    NETWORK_ZONE("x-mal-network-zone", true),
    SESSION("x-mal-session", true),
    SESSION_NAME("x-mal-session-name", true),
    INTERACTION_TYPE("x-mal-interaction-type", true),
    INTERACTION_STAGE("x-mal-interaction-stage", true),
    TRANSACTION_ID("x-mal-transaction-id", true),
    SERVICE_AREA("x-mal-service-area", true),
    SERVICE("x-mal-service", true),
    OPERATION("x-mal-operation", true),
    AREA_VERSION("x-mal-area-version", true),
    IS_ERROR_MSG("x-mal-is-error-message", true),
    REQUEST_TARGET("request-target", true);

    private String keyName;
    private boolean isMandatory;

    HttpHeaderKeys(String keyName, boolean isMandatory) {
        this.keyName = keyName;
        this.isMandatory = isMandatory;
    }

    public String toString() {
        return keyName;
    }

    public boolean isMandatory() {
        return isMandatory;
    }
}
