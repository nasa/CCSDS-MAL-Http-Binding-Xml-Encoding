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

import com.sun.net.httpserver.*;
import nasa.mo.mal.transport.http.util.Constants;
import org.apache.http.ssl.SSLContextBuilder;
import org.ccsds.moims.mo.mal.MALException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author wphyo
 *         Created on 7/26/17.
 * Small Http Server using com.sun.net.httpserver
 */
public class HttpMiniServer {
    public static final int MIN_VALID_THREAD_COUNT = 1;
    private String serverHost;
    private int serverPort;
    private String keyStoreFile;
    private String keyStorePassword;
    private String trustStoreFile;
    private String trustStorePassword;
    private int socketBacklog;
    private HttpServer server;
    private int threadPoolCount;
    private HttpHandler mainHandler;
    private String keyStoreType;
    private boolean isSecureServer;
    private static final Map<String, SSLContext> SSL_CONTEXT_MAP = new HashMap<>();

    /**
     * Constructor
     *
     * @param serverHost Host name of server
     * @param serverPort port of server
     * @param keyStoreFile Java Key Store file if secure http
     * @param keyStorePassword Java Key Store password if secure http
     * @param socketBacklog maximum allowed socket backlog
     * @param threadPoolCount thread pool count
     * @param mainHandler Http Handler
     */
    HttpMiniServer(String serverHost, int serverPort, String keyStoreFile, String keyStorePassword,
                   String trustStoreFile, String trustStorePassword, String keyStoreType,
                   int socketBacklog, int threadPoolCount, boolean isSecureServer, HttpHandler mainHandler) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.keyStoreFile = keyStoreFile;
        this.keyStorePassword = keyStorePassword;
        this.trustStoreFile = trustStoreFile;
        this.trustStorePassword = trustStorePassword;
        this.socketBacklog = socketBacklog;
        this.threadPoolCount = threadPoolCount;
        this.mainHandler = mainHandler;
        this.keyStoreType = keyStoreType;
        this.isSecureServer = isSecureServer;
    }

    /**
     * Creating a http server with given host, port, handler, and threads.
     * setting up plain or secure server based on flags.
     * @throws MALException any IO Exception
     */
    void create() throws MALException {
        if (threadPoolCount == 0) {
            threadPoolCount = MIN_VALID_THREAD_COUNT;
        }
        if (threadPoolCount < MIN_VALID_THREAD_COUNT) {
            throw new MALException("Invalid number of threads");
        }
        if (mainHandler == null || serverHost == null || serverPort == 0 || serverHost.isEmpty()) {
            throw new MALException("Null Host, Port, or Handler.");
        }
        try {
            InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(serverHost), serverPort);
            if (!isSecureServer) {
                server = HttpServer.create(address, socketBacklog);
            } else {
                if (isPlainServer()) {
                    throw new MALException("Null values in necessary keystore and password files");
                }
                HttpsConfigurator configurator = new ClientAuthenticatingConfigurator(
                        getSSLContext(keyStoreFile, keyStorePassword, trustStoreFile, trustStorePassword, keyStoreType));
                server = HttpsServer.create(address, socketBacklog);
                ((HttpsServer) server).setHttpsConfigurator(configurator);
            }
            server.createContext("/", mainHandler);
            Executors.newFixedThreadPool(threadPoolCount);
            server.setExecutor(Executors.newCachedThreadPool());
        } catch (IOException exp) {
            StringBuilder errorBuilder = new StringBuilder("Error creating Server: \n");
            errorBuilder.append("host: ").append(serverHost).append("\n");
            errorBuilder.append("port: ").append(serverPort).append("\n");
            errorBuilder.append("exception: ").append(exp.getMessage()).append("\n");
            throw new MALException(errorBuilder.toString(), exp);
        }
    }

    /**
     * Helper method for creating a server.
     * Check if this server is a plain http or SSL by checking necessary parameters for SSL is null
     *
     * @return flag
     */
    private boolean isPlainServer() {
        return keyStoreFile == null || keyStoreFile.isEmpty() ||
                trustStoreFile == null || trustStoreFile.isEmpty() ||
                keyStoreType == null || keyStoreType.isEmpty();
    }

    /**
     * Reading contents of the file.
     * if file is null, return null
     * @param fileName Path of the file including the name
     * @return Content of the file
     * @throws MALException any IO exception
     */
    public synchronized static String getPasswordFromFile(String fileName) throws MALException {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        StringBuilder storePass = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                storePass.append(sCurrentLine);
            }
        } catch (IOException exp) {
            throw new MALException("Error read from file.", exp);
        }
        return storePass.toString();
    }

    /**
     * Starting Server if it is not null.
     */
    void start() {
        if (server != null) {
            server.start();
        }
    }

    /**
     * Stopping Server if it is not null.
     */
    void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    /**
     * getting or creating SSL Context
     * if SSl Context is created & stored, return it from the map.
     *
     * if Key Store file is empty, create a default SSL Context
     *
     * Create SSL Context
     * NOTE: this method assumes that Key Store file has keys for both Key Manager & Trust Manager
     *
     * @param keyStoreFile Java Key Store File
     * @param keyStorePwd Password for Java Key Store File
     * @return SSL Context
     * @throws MALException any exception
     */
    public synchronized static SSLContext getSSLContext(String keyStoreFile, String keyStorePwd, String trustStoreFile,
                                                        String trustStorePwd, String keyStoreType) throws MALException {
        String key = "";
        if (!isDefaultSSLContext(keyStoreFile, trustStoreFile, keyStoreType)) {
            key = keyStoreFile + trustStoreFile + keyStoreType;
        }
        if (SSL_CONTEXT_MAP.containsKey(key)) {
            return SSL_CONTEXT_MAP.get(key);
        }
        SSLContext sslContext;
        try {
            if (isDefaultSSLContext(keyStoreFile, trustStoreFile, keyStoreType)) {
                sslContext = SSLContextBuilder.create().build();
            } else {
                char[] keyStorePwdChar = keyStorePwd.toCharArray();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(new FileInputStream(keyStoreFile), keyStorePwdChar);
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(keyStore, keyStorePwdChar);

                KeyStore trustStore = KeyStore.getInstance(keyStoreType);
                trustStore.load(new FileInputStream(trustStoreFile), trustStorePwd.toCharArray());
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(trustStore);

                // setup the HTTPS context and parameters
                sslContext = SSLContext.getInstance(Constants.SSL_PROTOCOL);
                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            }
        } catch (Exception exp) {
            throw new MALException("Error while creating new SSL Context.", exp);
        }
        SSL_CONTEXT_MAP.put(key, sslContext);
        return sslContext;
    }

    /**
     * Check default SSL content should be used.
     * @param keyStoreFile SSL Key Store File Absolute Path
     * @param trustStoreFile SSL Trust Store File Absolute Path
     * @param keyStoreType SSL Key Store Type eg. PKCS12 / JKS
     * @return boolean
     */
    private static boolean isDefaultSSLContext(String keyStoreFile, String trustStoreFile, String keyStoreType) {
        return keyStoreFile == null || keyStoreFile.isEmpty() ||
                trustStoreFile == null || trustStoreFile.isEmpty() ||
                keyStoreType == null || keyStoreType.isEmpty();
    }

    /**
     * Start to create the builder
     * @return
     */
    public static HttpMiniServerBuilder custom() {
        return HttpMiniServerBuilder.create();
    }

    /**
     * Custom Https Configurator to enable Client Authentication
     */
    private class ClientAuthenticatingConfigurator extends HttpsConfigurator {
        /**
         * Constructor
         * @param sslContext SSL Context from server
         */
        ClientAuthenticatingConfigurator(SSLContext sslContext) {
            super(sslContext);  }

        /**
         * Configuring SSL on each request
         * Steps:
         * 1.   Get SSL Context from constructor
         * 2.   enable Client Authentication in that Context
         * 3.   enable client Authentication in Https Parameters
         * 4.   copy other parameters from context.
         *
         * @param params HttpsParameters from the request
         */
        @Override
        public  void configure(HttpsParameters params) {
            SSLContext sslContext = getSSLContext();
            SSLParameters sslParams = sslContext.getDefaultSSLParameters();
            sslParams.setNeedClientAuth(true);
            params.setNeedClientAuth(true);
            params.setSSLParameters(sslParams);
        }
    }
}
