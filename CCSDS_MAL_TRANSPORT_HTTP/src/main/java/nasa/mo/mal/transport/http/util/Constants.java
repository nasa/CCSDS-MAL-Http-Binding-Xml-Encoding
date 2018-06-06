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

import org.apache.http.HttpVersion;

/**
 * @author Wai Phyo
 *         Created on 3/31/17.
 */
public class Constants {
    public static final HttpVersion HTTP_VERSION = HttpVersion.HTTP_1_1;
    public static final String DEFAULT_RESPONSE_BODY = "Message received. This is for information only.";
    public static final String DEFAULT_ERROR_RESPONSE_BODY = "Message rejected. This is for information only.";
    /**
     * if server port is empty in configuration, use this.
     * Got this from ESA's TCPIP package
     */
    public static final int DEFAULT_SERVER_PORT = 61616;

    /**
     * socket backlog.
     * ESA's TCPIP is using default which is 0.
     */
    public static final int DEFAULT_SOCKET_BACKLOG = 0;

    /**
     * thread pool number for server.
     */
    public static final int DEFAULT_THREAD_POOL = 10;

    /**
     * Only valid method. to be used to check when receiving requests.
     */
    public static final String VALID_REQUEST_METHOD = "POST";

    /**
     * Property Keys
     */
    public static final String SERVER_HOST_KEY = "org.ccsds.moims.mo.mal.transport.http.host";
    public static final String SERVER_PORT_KEY = "org.ccsds.moims.mo.mal.transport.http.port";
    public static final String SOCKET_BACKLOG_KEY = "org.ccsds.moims.mo.mal.transport.http.backlog";
    public static final String THREAD_POOL_KEY = "org.ccsds.moims.mo.mal.transport.http.threadpool";
    public static final String HTTP_DESTINATION_ENDPOINT_KEY = "org.ccsds.moims.mo.mal.transport.http.destination.endpoint";

    /**
     * Lowest error number in Http
     * any higher number is definitely an error
     */
    public static final int HTTP_LOWEST_ERROR_CODE = 400;

    public static final String IS_SSL_KEY = "org.ccsds.moims.mo.mal.transport.http.isssl";
    public static final String KEY_STORE_FILE_KEY = "org.ccsds.moims.mo.mal.transport.http.ssl.keystore";
    public static final String KEY_STORE_PWD_FILE_KEY = "org.ccsds.moims.mo.mal.transport.http.ssl.keypass";
    public static final String TRUST_STORE_FILE_KEY = "org.ccsds.moims.mo.mal.transport.http.ssl.truststore";
    public static final String TRUST_STORE_PWD_FILE_KEY = "org.ccsds.moims.mo.mal.transport.http.ssl.trustpass";
    public static final String KEY_STORE_TYPE_KEY = "org.ccsds.moims.mo.mal.transport.http.ssl.keystoretype";
    public static final String SSL_PROTOCOL = "TLS";
    public static final String KEY_STORE_TYPE = "JKS";

    public static final String SECURE_HTTP = "https";
    public static final String PLAIN_HTTP = "http";
}
