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

import nasa.mo.mal.encoder.xml.XmlStreamFactory;
import esa.mo.mal.transport.gen.GENMessageHeader;
import org.ccsds.moims.mo.mal.*;
import org.ccsds.moims.mo.mal.structures.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @author wphyo
 *         Created on 7/19/17.
 */
public class MessageTestBase {
    public static int serviceAreaCounter = 0;

    protected List<Object> elements;
    protected List<Object> shortForms;
    protected UShort serviceArea;

    protected URI uri = new URI("http://test:8080/");
    protected Blob auth = new Blob(new byte[] {1,2, 3});
    protected UShort uShort = new UShort(1);
    protected UOctet uOctet = new UOctet((short) 1);
    protected UInteger uInteger = new UInteger(1L);
    protected Time time = new Time(987654321234567898L);
    protected static Identifier identifier = new Identifier("Test Identifier");
    protected static IdentifierList identifiers = new IdentifierList();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        identifiers.add(identifier);
    }
    
    @Before
    public void setUp() throws Exception {
        elements = new ArrayList<>();
        shortForms = new ArrayList<>();
        serviceArea = new UShort(serviceAreaCounter++);
    }
    
    @After
    public void tearDown() throws Exception {
        shortForms.forEach(e -> MALContextFactory.getElementFactoryRegistry().deregisterElementFactory(e));
    }

    /**
     * Static helper class to generate headers
     *
     * @param isError          flag for error message
     * @param type             Interaction Type like Send, Submit, ...
     * @param stage stage for the interaction type. Should be valid stage. Submit has 1 & 2, ...
     * @param opStage   Operation Stage
     * @return GEN Message Header
     */
    public GENMessageHeader getMessageHeader(boolean isError, InteractionType type, UOctet stage,
                                             UShort opStage, UShort serviceArea) {
        return new GENMessageHeader(
                uri, auth, uri, time, QoSLevel.ASSURED, uInteger, identifiers, identifier, SessionType.LIVE, identifier,
                type, stage, uInteger.getValue(), serviceArea, uShort, opStage, uOctet, isError);
    }

    public void generateMALOperationSteps(UShort serviceArea, MALOperation operation, Identifier identifier)
            throws MALException {
        MALService submitService = new MALService(uShort, identifier);
        MALArea malArea = new MALArea(serviceArea, new Identifier(UUID.randomUUID().toString()), uOctet);
        submitService.addOperation(operation);
        malArea.addService(submitService);
        MALContextFactory.registerArea(malArea);
    }

    protected MALSendOperation generateSendOp() throws MALException {
        MALOperationStage sendStage = new MALOperationStage(new UOctet((short) 1), shortForms.toArray(), shortForms.toArray());
        MALSendOperation op = new MALSendOperation(uShort, identifier, true, new UShort(1), sendStage);
        generateMALOperationSteps(serviceArea, op, identifier);
        return op;
    }

    protected MALInvokeOperation generateInvokeOp() throws MALException {
        MALOperationStage invokeStage = new MALOperationStage(new UOctet((short) 1), shortForms.toArray(), shortForms.toArray());
        MALOperationStage invokeAckStage = new MALOperationStage(new UOctet((short) 2), shortForms.toArray(), shortForms.toArray());
        MALOperationStage invokeResponseStage = new MALOperationStage(new UOctet((short) 3), shortForms.toArray(), shortForms.toArray());
        MALInvokeOperation op = new MALInvokeOperation(uShort, identifier, true, new UShort(1), invokeStage, invokeAckStage, invokeResponseStage);
        generateMALOperationSteps(serviceArea, op, identifier);
        return op;
    }

    protected MALProgressOperation generateProgressOp() throws MALException {
        MALOperationStage progressStage = new MALOperationStage(new UOctet((short) 1), shortForms.toArray(), shortForms.toArray());
        MALOperationStage progressAckStage = new MALOperationStage(new UOctet((short) 2), shortForms.toArray(), shortForms.toArray());
        MALOperationStage progressUpdateStage = new MALOperationStage(new UOctet((short) 3), shortForms.toArray(), shortForms.toArray());
        MALOperationStage progressResponseStage = new MALOperationStage(new UOctet((short) 4), shortForms.toArray(), shortForms.toArray());
        MALProgressOperation op = new MALProgressOperation(uShort, identifier, true, new UShort(1), progressStage, progressAckStage, progressUpdateStage, progressResponseStage);
        generateMALOperationSteps(serviceArea, op, identifier);
        return op;
    }

    protected MALPubSubOperation generatePubSubOp() throws MALException {
        MALPubSubOperation op = new MALPubSubOperation(uShort, identifier, true, new UShort(1), shortForms.toArray(), shortForms.toArray());
        generateMALOperationSteps(serviceArea, op, identifier);
        return op;
    }

    protected MALSubmitOperation generateSubmitOp() throws MALException {
        MALOperationStage submitStage = new MALOperationStage(new UOctet((short) 1), shortForms.toArray(), shortForms.toArray());
        MALSubmitOperation op = new MALSubmitOperation(uShort, identifier, true, new UShort(1), submitStage);
        generateMALOperationSteps(serviceArea, op, identifier);
        return op;
    }

    protected MALRequestOperation generateRequestOp() throws MALException {
        MALOperationStage requestStage = new MALOperationStage(new UOctet((short) 1), shortForms.toArray(), shortForms.toArray());
        MALOperationStage requestResponseStage = new MALOperationStage(new UOctet((short) 2), shortForms.toArray(), shortForms.toArray());
        MALRequestOperation op = new MALRequestOperation(uShort, identifier, true, new UShort(1), requestStage, requestResponseStage);
        generateMALOperationSteps(serviceArea, op, identifier);
        return op;
    }

    protected XmlStreamFactory getStreamFactory() {
        return new XmlStreamFactory();
    }

    protected ByteArrayOutputStream getOutputStream() {
        return new ByteArrayOutputStream();
    }

    protected void showEncodedMessage(ByteArrayOutputStream stream) throws ClassNotFoundException, IOException {
        System.out.println(new String(stream.toByteArray()));

    }

    public static void stringToOutputStream(OutputStream message, String encodedXml) {
        PrintWriter p = new PrintWriter(message);
        p.write(encodedXml);
        p.flush();
        p.close();
    }

    public static int getAvailablePort() {
        int freeport = -1;
        try {
            ServerSocket socket = new ServerSocket(0);
            freeport = socket.getLocalPort();
            socket.close();
        } catch (Exception exp) {

        }
        return freeport;
    }
}
