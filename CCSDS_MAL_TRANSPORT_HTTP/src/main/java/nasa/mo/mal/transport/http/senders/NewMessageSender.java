/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.http.senders;

import nasa.mo.mal.encoder.Header.HttpHeaderKeys;
import nasa.mo.mal.encoder.Header.HttpToMalMapper;
import nasa.mo.mal.encoder.util.HeaderMappingHelper;
import esa.mo.mal.transport.gen.GENMessageHeader;
import nasa.mo.mal.transport.http.HttpEndpoint;
import nasa.mo.mal.transport.http.HttpMiniServer;
import nasa.mo.mal.transport.http.HttpTransport;
import nasa.mo.mal.transport.http.util.Constants;
import nasa.mo.mal.transport.http.util.HttpTransportHelper;
import nasa.mo.mal.transport.http.util.MALTransmitErrorBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Level;

/**
 * @author wphyo
 *         Created on 6/27/17.
 * TODO remove System.out.print()s after debugging is done
 */
public class NewMessageSender implements Callable<Boolean> {

    private static final int MAX_CONNECTION = 100;
    private HttpTransport transport;

    private int connectionTimeoutInMilliSecond = 0;
    private int connectionRequestTimeoutInMilliSecond = 0;
    private int socketTimeoutInMilliSecond = 0;

    private MALMessage message;
    private byte[] encodedMessage;
    private Map<String, String> headers;
    private HttpEndpoint endpoint;

    private PoolingHttpClientConnectionManager connectionManager;
    private SSLConnectionSocketFactory sslConnectionSocketFactory;

    public NewMessageSender(HttpTransport transport,
                            int connectionTimeoutInMilliSecond,
                            int connectionRequestTimeoutInMilliSecond,
                            int socketTimeoutInMilliSecond,
                            MALMessage message, byte[] encodedMessage, Map<String, String> headers) {
        this.transport = transport;
        this.connectionTimeoutInMilliSecond = connectionTimeoutInMilliSecond;
        this.connectionRequestTimeoutInMilliSecond = connectionRequestTimeoutInMilliSecond;
        this.socketTimeoutInMilliSecond = socketTimeoutInMilliSecond;
        this.message = message;
        this.encodedMessage = encodedMessage;
        this.headers = headers;
    }

    public NewMessageSender(HttpTransport transport, MALMessage message,
                            byte[] encodedMessage, Map<String, String> headers) {
        this.transport = transport;
        this.message = message;
        this.encodedMessage = encodedMessage;
        this.headers = headers;
    }

    /**
     * Creating a new Http Message and send it to the target
     *
     * Steps:
     * 1.   TODO figure out Host, URI-To, Request-Target
     * 2.   TODO create request-URI
     * 3.   create Http Post with encoded message. NOTE: based on Red Book, POST is the only valid method.
     * 4.   call method to execute the post
     * @return flag if workflow is executed correctly
     * @throws Exception any exception
     */
    @Override
    public Boolean call() throws Exception {
        try {
            if (headers == null || encodedMessage == null ||
                    !headers.containsKey(HttpHeaderKeys.HOST.toString()) ||
                    !headers.containsKey(HttpHeaderKeys.REQUEST_TARGET.toString())) {
                throw MALTransmitErrorBuilder.create().setHeader(message.getHeader())
                        .setExtraInfo("Null objects in not-nullable fields in Encoded Message").build();
            }
            headers.put(HttpHeaderKeys.HOST.toString(), headers.get(HttpHeaderKeys.HOST.toString()).replaceAll(
                    "^malhttp", transport.isSecureServer() ? Constants.SECURE_HTTP : Constants.PLAIN_HTTP));
            URI uri = new URI(HeaderMappingHelper.decodeURI(headers.get(HttpHeaderKeys.HOST.toString())));
            HttpTransport.LOGGER.log(Level.FINE, "Target URL = " + uri.toString());
            HttpHost target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());

            HttpPost post = new HttpPost(HeaderMappingHelper.decodeURI(headers.get(HttpHeaderKeys.REQUEST_TARGET.toString())));
            post.setProtocolVersion(Constants.HTTP_VERSION);
            headers.forEach(post::setHeader);
            post.setEntity(new ByteArrayEntity(encodedMessage));
            HttpTransport.LOGGER.log(Level.FINE, "Created Http Post Message for message:\n " + new String(encodedMessage));

            sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    HttpMiniServer.getSSLContext(transport.getKeyStoreFile(),
                            transport.getKeyStorePassword(),
                            transport.getTrustStoreFile(),
                            transport.getTrustStorePassword(),
                            transport.getKeyStoreType()),
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(Constants.PLAIN_HTTP, PlainConnectionSocketFactory.INSTANCE)
                    .register(Constants.SECURE_HTTP, sslConnectionSocketFactory)
                    .build();

            connectionManager = new PoolingHttpClientConnectionManager(registry);
            connectionManager.setMaxTotal(MAX_CONNECTION);
            return executePost(target, post);
        } catch (IOException exp) {
            throw MALTransmitErrorBuilder.create().setHeader(message.getHeader()).setExtraInfo(exp).build();
        }
    }

    /**
     * Executing a Http-Post with the client
     * NOTE: Closeable Client and Response is needed for multi-thread application
     *
     * Steps:
     * 1.   Create Http Client
     * 2.   Create Http Response with the client & post object
     * 3.   call method to handle response
     *
     * NOTE: commented code might be needed if stale connection issue rises.
     * https://stackoverflow.com/questions/10558791/apache-httpclient-interim-error-nohttpresponseexception
     * https://stackoverflow.com/questions/10570672/get-nohttpresponseexception-for-load-testing/10680629#10680629
     *
     * @param post Http-Post Object created with encoded MAL message.
     * @return flag if workflow is executed correctly
     * @throws Exception any exception
     */
    private boolean executePost(final HttpHost target, final HttpPost post) throws Exception {
        try (CloseableHttpClient client = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(connectionTimeoutInMilliSecond)
                        .setConnectionRequestTimeout(connectionRequestTimeoutInMilliSecond)
                        .setSocketTimeout(socketTimeoutInMilliSecond)
                        .build())
//                .setRetryHandler((exp, excCount, context) -> {
//                    if (excCount > 3) {
//                        HttpTransport.LOGGER.log(Level.WARNING, "Maximum tries reached for client http pool");
//                        return false;
//                    }
//                    if (exp instanceof NoHttpResponseException) {
//                        HttpTransport.LOGGER.log(Level.WARNING, "No response from server on " + exp + " call");
//                        return true;
//                    }
//                    return false;
//                })
                .build();
             CloseableHttpResponse response = client.execute(target, post)) {
            HttpTransport.LOGGER.log(Level.FINE, "sent encoded message.");
            boolean result = processResponse(response);
            EntityUtils.consumeQuietly(response.getEntity());
            return result;
        } catch (NoHttpResponseException exp) {
            throw MALTransmitErrorBuilder.create().setHeader(message.getHeader())
                    .setErrorNumber(MALHelper.DELIVERY_TIMEDOUT_ERROR_NUMBER).setExtraInfo(exp).build();
        } catch (IOException exp) {
            throw MALTransmitErrorBuilder.create().setHeader(message.getHeader())
                    .setErrorNumber(MALHelper.UNKNOWN_ERROR_NUMBER).setExtraInfo(exp).build();
        }
    }

    /**
     * Dealing with response after sending message
     * Steps:
     * 1.   validations
     *      a.  for stages that doesn't need a response, log it and end it.
     *      b.  for null endpoint, throw error
     *      c.  for error responses, TODO discuss workflow
     * 2.   if return code is error, TODO how to deal with it
     * 3.   create Http Message and pass it to endpoint so that it can pass to MAL Listener
     * @param response Http Response Object
     * @throws MALTransmitErrorException any exception
     */
    private boolean processResponse(HttpResponse response) throws MALTransmitErrorException {
        HttpTransport.LOGGER.log(Level.FINE, "Received response for original request:\n " + message.getHeader());
        if (HttpTransportHelper.isDefaultReply(message.getHeader().getInteractionType(),
                message.getHeader().getInteractionStage())) {
            HttpTransport.LOGGER.log(Level.FINE, "MAL doesn't need to know the reply");
            return true;
        }
        endpoint = (HttpEndpoint) transport.getEndpoint(message.getHeader().getURIFrom());
        if (endpoint == null) {
            throw MALTransmitErrorBuilder.create().setHeader(message.getHeader())
                    .setExtraInfo("Null Http Endpoint").build();
        }
        if (response.getStatusLine().getStatusCode() >= Constants.HTTP_LOWEST_ERROR_CODE && isPureHttpError(response)) {
            HttpTransport.LOGGER.log(Level.FINE, "This is pure Http Error. NO MAL Body attached");
            UInteger malErrorCode = HttpTransportHelper.getMALErrorFromHttp(response.getStatusLine().getStatusCode());
            try {
                endpoint.receiveMessage(endpoint.createMessage(
                        message.getHeader().getAuthenticationId(),
                        message.getHeader().getURIFrom(),
                        message.getHeader().getTimestamp(),
                        message.getHeader().getQoSlevel(),
                        message.getHeader().getPriority(),
                        message.getHeader().getDomain(),
                        message.getHeader().getNetworkZone(),
                        message.getHeader().getSession(),
                        message.getHeader().getSessionName(),
                        message.getHeader().getInteractionType(),
                        new UOctet((short) (message.getHeader().getInteractionStage().getValue() + 1)),
                        message.getHeader().getTransactionId(),
                        message.getHeader().getServiceArea(),
                        message.getHeader().getService(),
                        message.getHeader().getOperation(),
                        message.getHeader().getAreaVersion(),
                        true,
                        null,
                        malErrorCode,
                        new Union("Created from Http Status Code")));
                return true;
            } catch (MALException exp) {
                throw MALTransmitErrorBuilder.create().setErrorNumber(malErrorCode)
                        .setHeader(message.getHeader()).setExtraInfo(exp).build();
            }
        } else {
            try {
                byte[] responseMessage = IOUtils.toByteArray(response.getEntity().getContent());
                Map<String, String> headerMap = new HashMap<>();
                Arrays.stream(response.getAllHeaders())
                        .forEach(each -> headerMap.put(each.getName().toLowerCase(), each.getValue()));
                headerMap.put(HttpHeaderKeys.HOST.toString(), headerMap.get(HttpHeaderKeys.HOST.toString()).replaceAll(
                        "^" + (transport.isSecureServer() ? Constants.SECURE_HTTP : Constants.PLAIN_HTTP), "malhttp"));
                HttpTransport.LOGGER.log(Level.FINE, "Received Encoded Message:\n" + new String(responseMessage));
                GENMessageHeader header = new GENMessageHeader();
                HttpToMalMapper.getInstance().fillMalMessageHeader(headerMap, header);
                endpoint.receiveMessage(transport.createMessage(responseMessage, header));
                return true;
            } catch (IOException | MALException | NullPointerException exp) {
                throw MALTransmitErrorBuilder.create().setHeader(message.getHeader()).setExtraInfo(exp).build();
            }
        }
    }

    /**
     * Helper class to check when Http Response status code is an error,
     * does it have MAL error body by checking all mandatory header keys.
     *
     * @param response Http Response Object
     * @return flag if the header has all necessary keys
     */
    private boolean isPureHttpError(HttpResponse response) {
        if (response == null || response.getAllHeaders() == null) {
            return false;
        }
        Set<String> headerKeys = new HashSet<>();
        Arrays.stream(response.getAllHeaders()).forEach(e -> headerKeys.add(e.getName().toLowerCase()));
        return !Arrays.stream(HttpHeaderKeys.values()).filter(HttpHeaderKeys::isMandatory)
                .allMatch(e -> headerKeys.contains(e.toString()));
    }
}
