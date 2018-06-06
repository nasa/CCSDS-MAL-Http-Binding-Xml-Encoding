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

import com.sun.net.httpserver.HttpHandler;

/**
 * @author wphyo
 *         Created on 8/8/17.
 * Builder for Http Mini Server
 */
public class HttpMiniServerBuilder {
    private String serverHost;
    private int serverPort;
    private String keyStoreFile;
    private String keyStorePassword;
    private String trustStoreFile;
    private String trustStorePassword;
    private int socketBacklog;
    private int threadPoolCount;
    private HttpHandler mainHandler;
    private String keyStoreType;
    private boolean isSecureServer;
    /**
     * Static method to create a new builder
     * @return HttpMiniServerBuilder
     */
    public static HttpMiniServerBuilder create() {
        return new HttpMiniServerBuilder();
    }

    private HttpMiniServerBuilder() {

    }

    public HttpMiniServerBuilder serverHost(final String serverHost) {
        this.serverHost = serverHost;
        return this;
    }

    public HttpMiniServerBuilder serverPort(final int serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    public HttpMiniServerBuilder keyStoreFile(final String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
        return this;
    }

    public HttpMiniServerBuilder keyStorePassword(final String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
        return this;
    }

    public HttpMiniServerBuilder trustStoreFile(final String trustStoreFile) {
        this.trustStoreFile = trustStoreFile;
        return this;
    }

    public HttpMiniServerBuilder trustStorePassword(final String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
        return this;
    }

    public HttpMiniServerBuilder socketBacklog(final int socketBacklog) {
        this.socketBacklog = socketBacklog;
        return this;
    }

    public HttpMiniServerBuilder threadPoolCount(final int threadPoolCount) {
        this.threadPoolCount = threadPoolCount;
        return this;
    }

    public HttpMiniServerBuilder mainHandler(final HttpHandler mainHandler) {
        this.mainHandler = mainHandler;
        return this;
    }

    public HttpMiniServerBuilder keyStoreType(final String keyStoreType) {
        this.keyStoreType = keyStoreType;
        return this;
    }

    public HttpMiniServerBuilder isSecureServer(final boolean isSecureServer) {
        this.isSecureServer = isSecureServer;
        return this;
    }

    /**
     * build the object it is supposed to build
     * @return HttpMiniServer
     */
    public HttpMiniServer build() {
        return new HttpMiniServer(serverHost, serverPort, keyStoreFile, keyStorePassword, trustStoreFile,
                trustStorePassword, keyStoreType, socketBacklog, threadPoolCount, isSecureServer, mainHandler);
    }
}
