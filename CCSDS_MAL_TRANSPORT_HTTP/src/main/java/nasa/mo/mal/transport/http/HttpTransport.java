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
import nasa.mo.mal.encoder.Header.HttpHeaderKeys;
import nasa.mo.mal.encoder.Header.MalToHttpMapper;
import nasa.mo.mal.encoder.util.HeaderMappingHelper;
import nasa.mo.mal.encoder.util.ObjectFactory;
import esa.mo.mal.transport.gen.GENEndpoint;
import esa.mo.mal.transport.gen.GENMessage;
import esa.mo.mal.transport.gen.GENMessageHeader;
import esa.mo.mal.transport.gen.GENTransport;
import esa.mo.mal.transport.gen.sending.GENMessageSender;
import esa.mo.mal.transport.gen.sending.GENOutgoingMessageHolder;
import nasa.mo.mal.transport.http.receivers.HttpIncomingMessageHolder;
import nasa.mo.mal.transport.http.receivers.IncomingMessageProcessor;
import nasa.mo.mal.transport.http.receivers.MainHandler;
import nasa.mo.mal.transport.http.senders.NewMessageSender;
import nasa.mo.mal.transport.http.senders.ResponseMessageSender;
import nasa.mo.mal.transport.http.util.Constants;
import nasa.mo.mal.transport.http.util.HttpTransportHelper;
import nasa.mo.mal.transport.http.util.MALTransmitErrorBuilder;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.*;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author wphyo
 *         Created on 7/19/17.
 */
public class HttpTransport extends GENTransport<byte[], byte[]> {
    public static final Logger LOGGER = Logger.getLogger(HttpTransport.class.getName());
    private static final char PORT_DELIMITER = ':';
    /**
     * A map to keep the Http messages that need a reply from MAL to respond.
     * Key: Transaction ID
     * Value: Incomplete Http Exchange Object.
     */
    private final Map<Long, HttpExchange> responseMessageMap = new HashMap<>();
    private String serverHost;
    private int serverPort;
    private int socketBacklog;
    private int newFixedThreadPool;
    private boolean isClientOnly;
    private URI httpDestinationEndpoint;
    private boolean isSecureServer;
    /**
     * Http Server which will be created in init() and close in close()
     */
    private HttpMiniServer server;
    /**
     * A pool of threads to process messages and send up to MAL
     */
    private ExecutorService incomingMessageProcessors;
    /**
     * A pool of threads to respond Http messages with a reply from MAL
     */
    private ExecutorService responseMessageProcessors;
    /**
     * A pool of threads to create new Http messages and get a reply.
     */
    private ExecutorService newMessageProcessors;
    /**
     * A pool of threads Log the errors.
     */
    private ExecutorService loggingResultProcessor;

    private boolean isXmlEncodedFactory;
    private String encoderFactoryName;

    private String keyStoreFile;
    private String keyStorePassword;
    private String trustStoreFile;
    private String trustStorePassword;
    private String keyStoreType;

    /**
     * Constructor.
     *
     * @param protocol        The protocol string.
     * @param serviceDelim    The delimiter to use for separating the URL
     * @param supportsRouting True if routing is supported by the naming convention
     * @param wrapBodyParts   True is body parts should be wrapped in BLOBs
     * @param factory         The factory that created us.
     * @param properties      The QoS properties.
     * @throws MALException On error.
     */
    public HttpTransport(String protocol, char serviceDelim, boolean supportsRouting,
                         boolean wrapBodyParts, MALTransportFactory factory, Map properties)
            throws MALException {
        super(protocol, serviceDelim, supportsRouting, wrapBodyParts, factory, properties);
        captureInfo(properties);
    }

    /**
     * Constructor.
     *
     * @param protocol        The protocol string.
     * @param protocolDelim   The delimiter to use for separating the protocol part in the URL
     * @param serviceDelim    The delimiter to use for separating the URL
     * @param routingDelim    The delimiter to use for separating the URL for routing
     * @param supportsRouting True if routing is supported by the naming convention
     * @param wrapBodyParts   True is body parts should be wrapped in BLOBs
     * @param factory         The factory that created us.
     * @param properties      The QoS properties.
     * @throws MALException On error.
     */
    public HttpTransport(String protocol, String protocolDelim, char serviceDelim, char routingDelim,
                         boolean supportsRouting, boolean wrapBodyParts, MALTransportFactory factory, Map properties)
            throws MALException {
        super(protocol, protocolDelim, serviceDelim, routingDelim, supportsRouting, wrapBodyParts, factory, properties);
        captureInfo(properties);
    }

    /**
     * Helper method SOLELY for constructors.
     * capture required information to start a server from the map.
     * 1.   Server address
     * 2.   Server port
     * 3.   Socket Backlog
     * 4.   Thread Pool Number
     *
     * @param properties property map having all the info
     * @throws MALException No exception from this.
     */
    private void captureInfo(Map properties) throws MALException {
        keyStoreType = Constants.KEY_STORE_TYPE;
        if (properties != null) {
            if (properties.containsKey(Constants.SERVER_HOST_KEY)) {
                serverHost = (String) properties.get(Constants.SERVER_HOST_KEY);
            }
            if (properties.containsKey(Constants.SERVER_PORT_KEY)) {
                serverPort = Integer.parseInt((String) properties.get(Constants.SERVER_PORT_KEY));
            }
            if (properties.containsKey(Constants.SOCKET_BACKLOG_KEY)) {
                socketBacklog = Integer.parseInt((String) properties.get(Constants.SOCKET_BACKLOG_KEY));
            }
            if (properties.containsKey(Constants.THREAD_POOL_KEY)) {
                newFixedThreadPool = Integer.parseInt((String) properties.get(Constants.THREAD_POOL_KEY));
            }
            if (properties.containsKey(Constants.HTTP_DESTINATION_ENDPOINT_KEY)) {
                httpDestinationEndpoint = new URI((String) properties.get(Constants.HTTP_DESTINATION_ENDPOINT_KEY));
            }
            if (properties.containsKey(Constants.KEY_STORE_FILE_KEY)) {
                keyStoreFile = (String) properties.get(Constants.KEY_STORE_FILE_KEY);
            }
            if (properties.containsKey(Constants.KEY_STORE_PWD_FILE_KEY)) {
                keyStorePassword = HttpMiniServer.getPasswordFromFile((String) properties.get(Constants.KEY_STORE_PWD_FILE_KEY));
            }

            if (properties.containsKey(Constants.TRUST_STORE_FILE_KEY)) {
                trustStoreFile = (String) properties.get(Constants.TRUST_STORE_FILE_KEY);
            }
            if (properties.containsKey(Constants.TRUST_STORE_PWD_FILE_KEY)) {
                trustStorePassword = HttpMiniServer.getPasswordFromFile((String) properties.get(Constants.TRUST_STORE_PWD_FILE_KEY));
            }
            // if properties have is_ssl and it is true
            isSecureServer = properties.containsKey(Constants.IS_SSL_KEY) &&
                    ((String) properties.get(Constants.IS_SSL_KEY)).toLowerCase().equals("true");

            if (properties.containsKey(Constants.KEY_STORE_TYPE_KEY)) {
                keyStoreType = (String) properties.get(Constants.KEY_STORE_TYPE_KEY);
            }
        } else {
            LOGGER.log(Level.INFO, "This is purely client.");
            httpDestinationEndpoint = new URI("");
            isSecureServer = false;
        }
        isClientOnly = false;
        isXmlEncodedFactory = getStreamFactory().getClass().getName().toLowerCase().contains("xml");
        if (!isXmlEncodedFactory) {
            encoderFactoryName = getStreamFactory().getClass().getSimpleName();
        }
    }

    /**
     * Initialises this transport.
     * <p>
     * 1.   if it is not a server, don't do anything.
     * 2.   if it is server, then fill missing info (if any)
     * 3.   start a new server.
     *
     * @throws MALException On error
     */
    @Override
    public void init() throws MALException {
        super.init();
        if (newFixedThreadPool == 0) {
            newFixedThreadPool = Constants.DEFAULT_THREAD_POOL;
        }
        newMessageProcessors = Executors.newFixedThreadPool(newFixedThreadPool);
        loggingResultProcessor = Executors.newSingleThreadExecutor();
        if (serverHost == null || serverHost.trim().isEmpty()) {
            LOGGER.log(Level.INFO, "This is client. Server is not created");
            isClientOnly = true;
            return;
        }
        if (serverPort == 0) {
            serverPort = Constants.DEFAULT_SERVER_PORT;
        }
        if (socketBacklog == 0) {
            socketBacklog = Constants.DEFAULT_SOCKET_BACKLOG;
        }
        incomingMessageProcessors = Executors.newFixedThreadPool(newFixedThreadPool);
        responseMessageProcessors = Executors.newFixedThreadPool(newFixedThreadPool);

        HttpMiniServerBuilder builder = HttpMiniServer.custom()
                .serverHost(serverHost).serverPort(serverPort).mainHandler(new MainHandler(this))
                .socketBacklog(socketBacklog).threadPoolCount(newFixedThreadPool).isSecureServer(isSecureServer);
        if (isSecureServer) {
            builder.keyStoreFile(keyStoreFile).keyStorePassword(keyStorePassword)
                    .trustStoreFile(trustStoreFile).trustStorePassword(trustStorePassword)
                    .keyStoreType(keyStoreType);
        }
        server = builder.build();
        server.create();
        server.start();
    }

    /**
     * Adding a new processor thread to Executor Service
     * TODO capture future object
     *
     * @param processor Incoming message processor
     */
    public void addNewIncomingMessageToProcess(Callable<Boolean> processor) throws MALTransmitErrorException {
        checkExecutorService(incomingMessageProcessors, null, "incomingMessageProcessors");
        loggingResultProcessor.submit(new ResultLogger(incomingMessageProcessors.submit(processor),
                IncomingMessageProcessor.class.getName()));
    }

    /**
     * Storing an Http Exchange object which requires response to the map
     * TODO Check transaction ID is unique enough
     *
     * @param header   MAL Message Header to get the transaction ID
     * @param exchange Http Exchange Object
     */
    public void storeProcessingMessage(MALMessageHeader header, HttpExchange exchange) {
        LOGGER.log(Level.FINE, "New Message is stored in a map.", header.getTransactionId());
        synchronized (responseMessageMap) {
            responseMessageMap.put(header.getTransactionId(), exchange);
        }
    }

    /**
     * Retrieving responding Http Exchange Object
     * TODO depends on storeProcessingMessage(), key might need to be updated.
     *
     * @param header MAL Message Header to get the transaction ID
     * @return Http Exchange Object
     */
    public HttpExchange getResponseMessage(MALMessageHeader header) {
        LOGGER.log(Level.FINE, "Message is retrieved from the map.", header.getTransactionId());
        synchronized (responseMessageMap) {
            return responseMessageMap.remove(header.getTransactionId());
        }
    }

    /**
     * Sending an Http Message.
     * This method is expected to be used by Endpoint when sending a new MAL Message.
     * <p>
     * Steps:
     * 1.   Validations
     * 2.   Internal / external send
     * 3.   Internal: create thread to process incoming message
     * 4.   External: encode & check response / new message
     * 5.   External-Response: create thread to send response
     * 6.   External-New: create thread to send new message
     *
     * @param multiSendHandle NOT Used
     * @param lastForHandle NOT Used
     * @param message GEN Message
     * @throws MALTransmitErrorException any exception from validations, encoding, or sending message
     */
    @Override
    public void sendMessage(Object multiSendHandle, boolean lastForHandle, GENMessage message)
            throws MALTransmitErrorException {
        if (message == null) {
            throw MALTransmitErrorBuilder.create().setExtraInfo("Null Http Message").build();
        }
        if (!isHeaderNotNull(message.getHeader())) {
            throw MALTransmitErrorBuilder.create()
                    .setHeader(message.getHeader()).setProperties(qosProperties)
                    .setExtraInfo("MAL header has null values\n" + (message.getHeader() != null ?
                            message.getHeader().toString() : "null header")).build();
        }
        if (!hasValidTargetURI(message.getHeader().getURIFrom()) ||
                !hasValidTargetURI(message.getHeader().getURITo())) {
            throw MALTransmitErrorBuilder.create()
                    .setHeader(message.getHeader()).setProperties(qosProperties)
                    .setExtraInfo("URI format is not valid").build();
        }
        if (isLocalSend(message.getHeader().getURITo().getValue())) {
            LOGGER.log(Level.FINE, "Internal Sending");
            try {
                checkExecutorService(incomingMessageProcessors, message, "incomingMessageProcessors");
                loggingResultProcessor.submit(new ResultLogger(incomingMessageProcessors.submit(
                        new IncomingMessageProcessor(new HttpIncomingMessageHolder(null, message,
                                true), this)), IncomingMessageProcessor.class.getName()));
            } catch (IOException | RejectedExecutionException exp) {
                throw MALTransmitErrorBuilder.create().setHeader(message.getHeader()).setProperties(qosProperties)
                        .setExtraInfo(exp).build();
            }
        } else { // external send
            byte[] encodedMessage = internalEncodeMessage(message);
            Map<String, String> headerMap = new HashMap<>();
            try {
                MalToHttpMapper.getInstance().generateHeader(message.getHeader(), headerMap,
                        httpDestinationEndpoint == null || httpDestinationEndpoint.getValue().equals("") ?
                                message.getHeader().getURITo() : httpDestinationEndpoint,
                        isXmlEncodedFactory, encoderFactoryName);
                if (!headerMap.containsKey(HttpHeaderKeys.REQUEST_TARGET.toString())) {
                    headerMap.put(HttpHeaderKeys.REQUEST_TARGET.toString(),
                            HeaderMappingHelper.encodeURI("/"
                                    + getRoutingPart(httpDestinationEndpoint.getValue())));
                }
            } catch (MALException exp) {
                throw MALTransmitErrorBuilder.create().setHeader(message.getHeader()).setProperties(qosProperties)
                        .setExtraInfo(exp).setErrorNumber(MALHelper.BAD_ENCODING_ERROR_NUMBER).build();
            }
            if (HttpTransportHelper.isResponseReply(message.getHeader().getInteractionType(),
                    message.getHeader().getInteractionStage())) {
                if (isClientOnly) {
                    throw MALTransmitErrorBuilder.create().setHeader(message.getHeader()).setProperties(qosProperties)
                            .setExtraInfo("This is client only transport. Responding message is not valid.")
                            .build();
                }
                checkExecutorService(responseMessageProcessors, message, "responseMessageProcessors");
                loggingResultProcessor.submit(new ResultLogger(responseMessageProcessors.submit(
                        new ResponseMessageSender(this, message, encodedMessage, headerMap)),
                        ResponseMessageSender.class.getName()));
            } else {
                checkExecutorService(newMessageProcessors, message, "newMessageProcessors");
                loggingResultProcessor.submit(new ResultLogger(newMessageProcessors.submit(
                        new NewMessageSender(this, message, encodedMessage, headerMap)),
                        NewMessageSender.class.getName()));
            }
        }
    }

    /**
     * Creating GEN Message.
     *
     * @param packet encoded Message
     * @return GEN Message
     * @throws MALException exceptions from HttpMessage
     */
    @Override
    public GENMessage createMessage(byte[] packet) throws MALException {
        return new GENMessage(wrapBodyParts, true, new GENMessageHeader(), qosProperties, packet, getStreamFactory());
    }

    /**
     * Creating GEN Message
     * without reading the head as the head is provided.
     *
     * @param packet encoded Message
     * @param header MAL Message Header
     * @return GEN Message
     * @throws MALException exceptions from HttpMessage
     */
    public GENMessage createMessage(byte[] packet, GENMessageHeader header) throws MALException {
        return new GENMessage(wrapBodyParts, false, header, qosProperties, packet, getStreamFactory());
    }

    /**
     * Not SUPPORTED here
     * @param destinationRootURI NOT Used
     * @param destinationURI NOT Used
     * @param multiSendHandle NOT Used
     * @param lastForHandle NOT Used
     * @param targetURI NOT Used
     * @param msg NOT Used
     * @return NOT Used
     * @throws Exception NOT Used
     */
    @Deprecated
    @Override
    protected GENOutgoingMessageHolder<byte[]> internalEncodeMessage(String destinationRootURI,
                                                                     String destinationURI,
                                                                     Object multiSendHandle,
                                                                     boolean lastForHandle,
                                                                     String targetURI,
                                                                     GENMessage msg) throws Exception {
        throw new UnsupportedOperationException("Not supported for Http Transport");
    }

    /**
     * Creates the part of the URL specific to this transport instance.
     * Steps:
     * 1.   if client
     * get IP of the server (check v4 vs v6)
     * get default server port
     * 2.   if server
     * get assigned IP & port
     *
     * @return The transport specific address part.
     * @throws MALException On error
     */
    @Override
    protected String createTransportAddress() throws MALException {
        if (serverHost == null) {
            StringBuilder ipAddress = ObjectFactory.createStringBuilder();
            try {
                InetAddress addr = Inet4Address.getLocalHost();
                if (addr instanceof Inet6Address) {
                    ipAddress.append('[');
                    ipAddress.append(addr.getHostAddress());
                    ipAddress.append(']');
                } else {
                    ipAddress.append(addr.getHostAddress());
                }
            } catch (UnknownHostException exp) {
                throw new MALException("Could not determine local host address", exp);
            }
            return ipAddress.toString() + PORT_DELIMITER + Constants.DEFAULT_SERVER_PORT;
        } else {
            return serverHost + PORT_DELIMITER + serverPort;
        }
    }

    /**
     * Method to be implemented by the transport in order to return a message sender capable
     * if sending messages to a target root URI.
     * TODO implementation
     *
     * @param msg           the message to be send
     * @param remoteRootURI the remote root URI.
     * @return returns a message sender capable of sending messages to the target URI
     * @throws MALException              in case of error trying to create the communication channel
     * @throws MALTransmitErrorException in case of error connecting to the target URI
     */
    @Override
    protected GENMessageSender createMessageSender(GENMessage msg, String remoteRootURI)
            throws MALException, MALTransmitErrorException {
        throw new UnsupportedOperationException("Http Transport is not using GENMessageSender");
    }

    /**
     * The method allows the creation of a transport level broker. The method returns NULL if no broker can be created by
     * this MALTransport.
     * Broker is NOT SUPPORTED in Http Implementation
     *
     * @param localName            Name of the private MALEndpoint to be created and used by the broker, may be null.
     * @param authenticationId     Authentication identifier that should be used by the broker, may be null.
     * @param expectedQos          QoS levels the broker assumes it can rely on
     * @param priorityLevelNumber  Number of priorities the broker uses
     * @param defaultQosProperties Default QoS properties used by the broker to send messages, may be null.
     * @return The new broker binding.
     * @throws IllegalArgumentException If the parameters ‘expectedQoS’ or ‘priorityLevelNumber’ are NULL
     * @throws MALException             If the MALTransport is closed
     */
    @Override
    public MALBrokerBinding createBroker(String localName, Blob authenticationId, QoSLevel[] expectedQos,
                                         UInteger priorityLevelNumber, Map defaultQosProperties)
            throws IllegalArgumentException, MALException {
        LOGGER.log(Level.SEVERE, "attempting to create broker in Http Transport. UNSUPPORTED");
        return null;
    }

    /**
     * The method allows the creation of a transport level broker.
     * The method returns NULL if no broker can be created by this MALTransport.
     * Broker is NOT SUPPORTED in Http Implementation
     *
     * @param endpoint             Shared MALEndpoint to be used by the broker
     * @param authenticationId     Authentication identifier that should be used by the broker, may be null.
     * @param expectedQos          QoS levels the broker assumes it can rely on
     * @param priorityLevelNumber  Number of priorities the broker uses
     * @param defaultQosProperties Default QoS properties used by the broker to send messages, may be null.
     * @return The new broker binding.
     * @throws IllegalArgumentException If the parameters ‘endpoint’ or ‘expectedQoS’ or ‘priorityLevelNumber’
     *                                  are NULL
     * @throws MALException             If the MALTransport is closed
     */
    @Override
    public MALBrokerBinding createBroker(MALEndpoint endpoint, Blob authenticationId, QoSLevel[] expectedQos,
                                         UInteger priorityLevelNumber, Map defaultQosProperties)
            throws IllegalArgumentException, MALException {
        LOGGER.log(Level.SEVERE, "attempting to create broker in Http Transport. UNSUPPORTED");
        return null;
    }

    /**
     * The method indicates whether a QoS level is supported or not.
     * TODO implementation
     * WhiteBook states "Support for the Quality of Service (QoS) levels
     * defined by MAL shall depend on the capabilities of the underlying layer used to convey the HTTP messages"
     * Need confirmation.
     *
     * @param qos QoSLevel which support is to be tested
     * @return TRUE if the specified QoSLevel is supported by the MALTransport otherwise FALSE
     */
    @Override
    public boolean isSupportedQoSLevel(QoSLevel qos) {
        return true;
    }

    /**
     * The method indicates whether an IP is supported or not.
     * Based on WhiteBook, PUB-SUB is not supported. Others are supported.
     *
     * @param type The InteractionType which support is to be tested.
     * @return TRUE if the specified InteractionType is supported by the MALTransport otherwise FALSE
     */
    @Override
    public boolean isSupportedInteractionType(InteractionType type) {
        return type.getOrdinal() != InteractionType._PUBSUB_INDEX;
    }

    @Override
    protected GENEndpoint internalCreateEndpoint(String localName, String routingName, Map qosProperties)
            throws MALException {
        return new HttpEndpoint(this, localName, routingName, uriBase + routingName, wrapBodyParts);
    }

    /**
     * Releasing all resources including the server
     * <p>
     * Steps:
     * 1.   Calling GenTransport's close method to release their resources
     * 2.   clearing result blocking queue
     * 3.   shutting down 3 executing services. (if server all 3, if client: only NewMessage Service)
     * 4.   shutdown Http Server (only if it is server).
     *
     * @throws MALException no exception yet.
     */
    @Override
    public void close() throws MALException {
        super.close();
        if (newMessageProcessors != null) {
            newMessageProcessors.shutdown();
        }
        if (loggingResultProcessor != null) {
            loggingResultProcessor.shutdown();
        }
        if (!isClientOnly) {
            if (incomingMessageProcessors != null) {
                incomingMessageProcessors.shutdown();
            }
            if (responseMessageProcessors != null) {
                responseMessageProcessors.shutdown();
            }
            if (server != null) {
                server.stop();
            }
        }
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public String getKeyStoreFile() {
        return keyStoreFile;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getTrustStoreFile() {
        return trustStoreFile;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public boolean isSecureServer() {
        return isSecureServer;
    }

    /**
     * Helper method to check if the sending message is sent internally
     *
     * @param targetURI Destination URI
     * @return flag if this message is sent internally.
     */
    private boolean isLocalSend(String targetURI) {
        String targetURIBase = getRootURI(targetURI);
        return inProcessSupport &&
                (uriBase.startsWith(targetURIBase) || targetURIBase.startsWith(uriBase)) &&
                endpointRoutingMap.containsKey(getRoutingPart(targetURI));
    }

    /**
     * Helper method to verify all fields in the MAL header is not null
     *
     * @param header MAL Header
     * @return flag if all the fields are not null
     */
    private boolean isHeaderNotNull(MALMessageHeader header) {
        if (header == null ||
                header.getURIFrom() == null ||
                header.getAuthenticationId() == null ||
                header.getURITo() == null ||
                header.getTimestamp() == null ||
                header.getQoSlevel() == null ||
                header.getPriority() == null ||
                header.getDomain() == null ||
                header.getNetworkZone() == null ||
                header.getSession() == null ||
                header.getSessionName() == null ||
                header.getInteractionType() == null ||
                header.getTransactionId() == null ||
                header.getServiceArea() == null ||
                header.getService() == null ||
                header.getOperation() == null ||
                header.getAreaVersion() == null ||
                header.getIsErrorMessage() == null) {
            return false;
        }
        return !(!header.getInteractionType().equals(InteractionType.SEND) &&
                header.getInteractionStage() == null);
    }

    /**
     * Validation method SOLELY for createHttpMessage
     * checking
     * 1.   if parameter is not null
     * 2.   field in parameter is not null
     * 3.   field doesn't have empty string
     * 4.   field's URI string is valid
     *
     * @param targetURI destination URI String
     * @return flag if it is valid
     */
    private boolean hasValidTargetURI(URI targetURI) {
        return targetURI != null &&
                targetURI.getValue() != null &&
                !targetURI.getValue().equals("") &&
                HttpTransportHelper.isValidURI(targetURI.getValue());
    }

    /**
     * Helper method SOLELY for sendHttpMessage method.
     * Use Http Message form parameter to create a serializable encoded message.
     *
     * @param malMessage Http Message with MAL Header, and specific Body type.
     * @return encoded byte message
     * @throws MALTransmitErrorException any error during encoding process.
     */
    private byte[] internalEncodeMessage(GENMessage malMessage) throws MALTransmitErrorException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            malMessage.encodeMessage(getStreamFactory(),
                    (getStreamFactory()).createOutputStream(outputStream),
                    outputStream,
                    false);
            return outputStream.toByteArray();
        } catch (MALException exp) {
            throw MALTransmitErrorBuilder.create().setHeader(malMessage.getHeader()).setProperties(qosProperties)
                    .setExtraInfo(exp).setErrorNumber(MALHelper.BAD_ENCODING_ERROR_NUMBER)
                    .build();
        } catch (Exception exp) {
            throw MALTransmitErrorBuilder.create().setHeader(malMessage.getHeader()).setProperties(qosProperties)
                    .setExtraInfo(exp).setErrorNumber(MALHelper.INTERNAL_ERROR_NUMBER)
                    .build();
        }
    }

    /**
     * Validating if Executor Service is alive.
     * Helper Method MAINLY for sendHttpMessage method
     * If it is not alive, throwing an error
     *
     * @param service Executor Service
     * @param msg Http Message to get header when throwing an exception
     * @param name Name of Executor Service
     * @throws MALTransmitErrorException if Executor Service is shutdown or terminated
     */
    private void checkExecutorService(ExecutorService service, MALMessage msg, String name)
            throws MALTransmitErrorException {
        if (service.isShutdown() || service.isTerminated()) {
            MALTransmitErrorBuilder builder = MALTransmitErrorBuilder.create().setProperties(qosProperties)
                    .setExtraInfo("Executor Service: " + name + " is closed.");
            if (msg  != null && msg.getHeader() != null) {
                builder.setHeader(msg.getHeader());
            }
            throw builder.build();
        }
    }

    /**
     * Private Class which will log if there is any error.
     */
    private class ResultLogger implements Runnable {
        Future<Boolean> result;
        String className;

        ResultLogger(Future<Boolean> result, String className) {
            this.result = result;
            this.className = className;
        }

        /**
         * Main method
         * Steps:
         * 1.   try to get the result.
         * 2.   if the result is false, something went wrong. log it.
         * 3.   if there is any exception, log it with Severe warning.
         */
        @Override
        public void run() {
            try {
                if (!result.get()) {
                    HttpTransport.LOGGER.log(Level.INFO, "Executor Service failed @ " + className);
                }
            } catch (Exception exp) {
                HttpTransport.LOGGER.log(Level.SEVERE, "Executor Service throws an error", exp);
            }
        }
    }
}
