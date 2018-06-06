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

import nasa.mo.mal.transport.ServerSetup;
import esa.mo.mal.transport.gen.GENMessage;
import nasa.mo.mal.transport.http.util.HttpTransportHelper;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.junit.*;
import org.mockito.Mock;

import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * @author wphyo
 *         Created on 7/19/17.
 */
public abstract class HttpTransportTestBase extends MessageTestBase {
    protected static HttpTransport serverTransport;
    protected static HttpEndpoint serverEndpoint;
    protected static Map<String, String> serverProperties;
    protected HttpTransportFactoryImpl factory;
    protected static String targetURI;

    /**
     * Random number generator to create Transaction IDs
     */
    protected static Random random = new Random();
    protected HttpTransport clientTransport;
    protected HttpEndpoint clientEndpoint;
    /**
     * A Mock MAL Message Listener for Server Endpoint
     */
    @Mock
    protected MALMessageListener serverListener;
    /**
     * A Mock MAL Message Listener for Client Endpoint
     */
    @Mock
    protected MALMessageListener clientListener;
    /**
     * Saving current Transaction ID for matchings.
     */
    protected Long currentTransactionId;
    protected Map<String, String> clientProperties;

    @BeforeClass
    public static void oneTimeSetUpHttpTransportTestBase() throws Exception {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        HttpTransport.LOGGER.addHandler(consoleHandler);
        HttpTransport.LOGGER.setLevel(Level.FINE);
    }

    /**
     * Setting System properties for all test cases
     * <p>
     * 1.   Create Console Handler to capture Fine Level logs
     * 2.   Set it to HttpTransport
     * 3.   Set HttpTransport to capture Fine Level logs
     * 4.   Set system properties
     * 5.   Set up Server
     * 6.   Set up Server endpoint.
     *
     * @throws MALException when creating HttpTransport
     */
    public static void manualOneTimeSetUp(ServerSetup setup) throws Exception {
        targetURI = setup.getTargetURI();
        serverProperties = setup.getServerProperties();
        serverTransport = setup.getServerTransport();
        serverEndpoint = setup.getServerEndpoint();
    }

    /**
     * Everything is done. Shutdown server.
     *
     * @throws MALException from HttpTransport
     */
    @AfterClass
    public static void oneTimeTearDown1() throws MALException {
        serverTransport.close();
    }

    /**
     * Setting up before each test
     * <p>
     * Settings:
     * 1.   Each test needs a random transaction ID
     * 2.   Each test needs a server which can receive messages.
     * 3.   Server needs an endpoint for a specific service.
     * 4.   Endpoint needs a MAL Message Listener to send messages to MAL Layer
     * It is a mock object.
     * Mock object needs to mock a receiving message method: onMessage
     * 5.   There will be a client endpoint which requires similar mock object which accepts onMessage method
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        factory = new HttpTransportFactoryImpl("malhttp");
        currentTransactionId = random.nextLong();
        doAnswer(invocationOnMock -> {
            System.out.println("client MAL Message Listener onMessage");
            Object[] arguments = invocationOnMock.getArguments();
            if (arguments != null && arguments.length == 2 && arguments[0] != null && arguments[1] != null) {

                HttpEndpoint paramEndpoint = (HttpEndpoint) arguments[0];
                GENMessage incomingMessage = (GENMessage) arguments[1];
                Assert.assertEquals(currentTransactionId, incomingMessage.getHeader().getTransactionId());
                Assert.assertEquals(paramEndpoint, clientEndpoint);
                printIncomingMessage(incomingMessage);
                System.out.println("Response Message is submitted to MAL. ");
            } else {
                System.out.println("Invalid parameters on MALMessageListener onMessage()");
                Assert.assertTrue(false);
            }
            return null;
        }).when(clientListener).onMessage(any(), any());
    }

    /**
     * Releasing resources after each test case
     * Need to shutdown both client transports.
     */
    @After
    public void tearDown() {
        try {
            clientTransport.close();
        } catch (MALException exp) {
            System.out.println("tearDown " + exp);
        }
    }

    protected void printIncomingMessage(MALMessage incomingMessage) throws MALException {
        System.out.println("income Message's Header: " + incomingMessage.getHeader());
        System.out.println("incoming Message's Body Count: " + incomingMessage.getBody().getElementCount());
        for (int i = 0; i < incomingMessage.getBody().getElementCount(); i++) {
            System.out.println("incoming Message's Body Element @ index:" + i + " = " + incomingMessage.getBody().getBodyElement(i, new Object()));
        }
    }

    protected void internalTest(Map<String, String> properties, String endPointName, InteractionType type, UOctet stage)
            throws MALException, MALTransmitErrorException {
        clientTransport = (HttpTransport) factory.createTransport(null, properties);
        clientEndpoint = (HttpEndpoint) clientTransport.createEndpoint(endPointName, properties);
        clientEndpoint.setMessageListener(clientListener);
        GENMessage message = (GENMessage) clientEndpoint.createMessage(new Blob(new byte[]{1, 2, 3}),
                new URI(targetURI),
                new Time(Calendar.getInstance().getTimeInMillis()),
                QoSLevel.ASSURED,
                new UInteger(1L),
                new IdentifierList(),
                new Identifier("Test Zone"),
                SessionType.LIVE,
                new Identifier("Test Session Name"),
                type,
                stage,
                currentTransactionId,
                serviceArea,
                new UShort(1),
                new UShort(1),
                new UOctet((short) 1),
                false,
                properties,
                elements.toArray());
        clientEndpoint.sendMessage(message);
    }

    protected void sendOnlyServerAnalyzer(GENMessage incomingMessage) {
        if (HttpTransportHelper.isDefaultReply(incomingMessage.getHeader().getInteractionType(),
                incomingMessage.getHeader().getInteractionStage())) {
            System.out.println("No Reply is necessary. ");
            Assert.assertTrue(true);
        } else {
            System.out.println("Reply is required. Replying now.");
            Assert.assertTrue(false);
        }
    }
}
