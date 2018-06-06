/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.gen;

import nasa.mo.mal.encoder.xml.XmlStreamFactory;
import esa.mo.mal.transport.gen.GENMessageHeader;
import esa.mo.mal.transport.gen.body.GENMessageBody;
import esa.mo.mal.transport.gen.body.GENPublishBody;
import nasa.mo.mal.transport.http.MessageTestBase;
import org.ccsds.moims.mo.mal.*;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.structures.factory.SessionTypeFactory;
import org.ccsds.moims.mo.mal.structures.factory.TimeFactory;
import org.ccsds.moims.mo.mal.structures.factory.UpdateHeaderListFactory;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author wphyo
 *         Created on 6/30/17.
 *         Test cases for MAL Message Body
 */
public class GENMessageBodyByXmlEncoderTest extends MessageTestBase {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private GENMessageBody body;
    private XmlStreamFactory factory;
    private MALEncodingContext context;
    private ByteArrayOutputStream encodedMessage;
    private MALMessageHeader header;
    private MALOperation operation;
    private UShort operationNumber;
    private Identifier testSend;
    private List<Object> shortForms;
    private UShort serviceArea;
    private List<Object> objects;
    private byte[] defaultEncodedMessageBody;
    private String encodedXml;
    @Before
    public void init() {
        header = new GENMessageHeader(new URI(""),
                new Blob(new byte[]{2}),
                new URI(""),
                new Time(0L),
                QoSLevel.ASSURED,
                new UInteger(1L),
                new IdentifierList(),
                new Identifier(""),
                SessionType.LIVE,
                new Identifier(""),
                InteractionType.SEND,
                new UOctet((short) 1),
                1L,
                new UShort(1),
                new UShort(1),
                new UShort(1),
                new UOctet((short) 1), false);
        factory = new XmlStreamFactory();
        encodedMessage = new ByteArrayOutputStream();
        operationNumber = new UShort(1);
        testSend = new Identifier("testSend");
        shortForms = new ArrayList<>();
        serviceArea = new UShort(serviceAreaCounter++);
        operation = null;
        objects = new ArrayList<>();
        encodedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><malxml:Body xmlns:malxml=\"http://www.ccsds.org/schema/malxml/MAL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Time><Time>1970-01-01T00:00:00.000</Time></Time></malxml:Body>";
        defaultEncodedMessageBody = String.valueOf(encodedXml).getBytes();
    }

    private ByteArrayInputStream getStreamFromEncodedMessage(byte[] encodedBytes) throws Exception {
        return new ByteArrayInputStream(encodedBytes);
    }

    /**
     * Getting MAL-Encoded-Body for a decoded body.
     * expecting error
     *
     * @throws MALException any error
     */
    @Test
    public void getEncodedBodyTest01() throws MALException {
        body = new GENMessageBody(context, factory, new Object[0]);
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Not supported yet.");
        body.getEncodedBody();
    }

    /**
     * Getting MAL-Encoded-Body for a encoded body
     * Checking if getting the same as initial body since they are the same
     *
     * @throws MALException any error
     */
    @Test
    public void getEncodedBodyTest02() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MessageTestBase.stringToOutputStream(outputStream, encodedXml);
        ByteArrayInputStream inputStream = getStreamFromEncodedMessage(defaultEncodedMessageBody);
        body = new GENMessageBody(context, false, factory, inputStream, factory.createInputStream(inputStream));
        MALEncodedBody retrievedEncodedBody = body.getEncodedBody();
        Assert.assertTrue(retrievedEncodedBody != null);
        Assert.assertEquals(encodedXml, new String(retrievedEncodedBody.getEncodedBody().getValue()));
    }

    /**
     * Getting MAL-Encoded-Body for a special encoded body (publish body)
     * Checking if getting the same as initial body since they are the same
     *
     * @throws MALException any error
     */
    @Test
    public void getEncodedBodyTest03() throws Exception {
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 5));
        shortForms.add(UpdateHeaderList.SHORT_FORM);
        operation = new MALPubSubOperation(operationNumber, testSend, true, new UShort(1), shortForms.toArray(), shortForms.toArray());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UpdateHeaderList.SHORT_FORM, new UpdateHeaderListFactory());
        String encodedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><malxml:Body xmlns:malxml=\"http://www.ccsds.org/schema/malxml/MAL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><UpdateHeaderList/><UpdateHeaderList/></malxml:Body>";
        MessageTestBase.stringToOutputStream(encodedMessage, encodedXml);

        MALService submitService = new MALService(new UShort(1), testSend);

        MALArea malArea = new MALArea(serviceArea, new Identifier(UUID.randomUUID().toString()), new UOctet((short) 1));

        submitService.addOperation(operation);
        malArea.addService(submitService);
        MALContextFactory.registerArea(malArea);

        context = new MALEncodingContext(header, operation, 0, null, null);
        ByteArrayInputStream inputStream = getStreamFromEncodedMessage(encodedMessage.toByteArray());
        body = new GENPublishBody(context, false, factory, inputStream, factory.createInputStream(inputStream));
        MALEncodedBody retrievedEncodedBody = body.getEncodedBody();
        Assert.assertTrue(retrievedEncodedBody != null);
        Assert.assertEquals(encodedXml, new String(retrievedEncodedBody.getEncodedBody().getValue()));
    }

    /**
     * Encoding the encoded message body.
     * It will just copy encoded body into a new body.
     *
     * @throws MALException any error
     */
    @Test
    public void encodeMessageBodyTest01() throws Exception {
        ByteArrayInputStream inputStream = getStreamFromEncodedMessage(defaultEncodedMessageBody);
        body = new GENMessageBody(context, false, factory, inputStream, factory.createInputStream(inputStream));
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        body.encodeMessageBody(factory, factory.createOutputStream(result), result, header.getInteractionStage(), context);
        Assert.assertEquals(encodedXml, new String(result.toByteArray()));
    }

    /**
     * Getting MAL-Encoded-Body for a special encoded body (publish body)
     * Although it is decoded, it should check MAL-Encoded-Body is not null, and return that.
     * NOTE: This test-case is no longer valid for its original purpose
     *
     * @throws MALException any error
     */
    @Test
    public void encodeMessageBodyTest02() throws Exception {
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 5));
        shortForms.add(UpdateHeaderList.SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UpdateHeaderList.SHORT_FORM, new UpdateHeaderListFactory());
        generatePubSubOp();
        String encodedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><malxml:Body xmlns:malxml=\"http://www.ccsds.org/schema/malxml/MAL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><UpdateHeaderList/><UpdateHeaderList/></malxml:Body>";
        context = new MALEncodingContext(header, operation, 0, null, null);
        ByteArrayInputStream inputStream = getStreamFromEncodedMessage(encodedXml.getBytes());
        body = new GENPublishBody(context, false, factory, inputStream, factory.createInputStream(inputStream));
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        body.encodeMessageBody(factory, factory.createOutputStream(result), result, header.getInteractionStage(), context);
        Assert.assertEquals(encodedXml, new String(result.toByteArray()));
    }

    /**
     * Encoding the encoded message body.
     * It will just copy encoded body into a new body.
     *
     * @throws MALException any error
     */
    @Test
    public void encodeMessageBodyTest03() throws MALException {
        MALEncodedBody encodedBody = new MALEncodedBody(new Blob(defaultEncodedMessageBody));
        objects.add(encodedBody);
        body = new GENMessageBody(context, factory, objects.toArray());
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        body.encodeMessageBody(factory, factory.createOutputStream(result), result, header.getInteractionStage(), context);
        Assert.assertTrue(Arrays.equals(defaultEncodedMessageBody, result.toByteArray()));
    }

    /**
     * Encoding decoded message body
     * Encoding a single element
     *
     * @throws MALException any error
     */
    @Test
    public void encodeMessageBodyTest04() throws Exception {
        header.setInteractionType(InteractionType.SEND);
        header.setInteractionStage(new UOctet((short) 1));
        objects.add(new Time(0L));
        shortForms.add(Attribute.TIME_SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.TIME_SHORT_FORM, new TimeFactory());
        operation = new MALSendOperation(operationNumber, testSend, true, new UShort(1), new MALOperationStage(new UOctet((short) 1), shortForms.toArray(), shortForms.toArray()));
        context = new MALEncodingContext(header, operation, 0, null, null);
        body = new GENMessageBody(context, factory, objects.toArray());
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        body.encodeMessageBody(factory, factory.createOutputStream(result), result, header.getInteractionStage(), context);
        Assert.assertEquals(encodedXml, new String(result.toByteArray()));
    }

    /**
     * Encoding decoded message body
     * Encoding 2 elements
     *
     * @throws MALException any error
     */
    @Test
    public void internalEncodeMessageBody01() throws Exception {
        header.setInteractionType(InteractionType.SEND);
        header.setInteractionStage(new UOctet((short) 1));
        objects.add(new FineTime(0L));
        objects.add(SessionType.LIVE);
        shortForms.add(Attribute.TIME_SHORT_FORM);
        shortForms.add(SessionType.SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.TIME_SHORT_FORM, new TimeFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(SessionType.SHORT_FORM, new SessionTypeFactory());
        operation = new MALSendOperation(operationNumber, testSend, true, new UShort(1), new MALOperationStage(new UOctet((short) 1), shortForms.toArray(), shortForms.toArray()));
        context = new MALEncodingContext(header, operation, 0, null, null);
        body = new GENMessageBody(context, factory, objects.toArray());
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        body.encodeMessageBody(factory, factory.createOutputStream(result), result, header.getInteractionStage(), context);
        Assert.assertTrue(new String(result.toByteArray()).endsWith("<FineTime><FineTime>1970-01-01T00:00:00.000000000</FineTime></FineTime><SessionType><SessionType>LIVE</SessionType></SessionType></malxml:Body>"));
    }

    /**
     * Encoding decoded message body
     * Testing exception for null Element OutputStream
     *
     * @throws Exception any error
     */
    @Test
    public void internalEncodeMessageBody03() throws Exception {
        header.setInteractionType(InteractionType.SEND);
        header.setInteractionStage(new UOctet((short) 1));
        objects.add(new FineTime(0L));
        objects.add(SessionType.LIVE);
        shortForms.add(Attribute.TIME_SHORT_FORM);
        shortForms.add(SessionType.SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.TIME_SHORT_FORM, new TimeFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(SessionType.SHORT_FORM, new SessionTypeFactory());
        operation = new MALSendOperation(operationNumber, testSend, true, new UShort(1), new MALOperationStage(new UOctet((short) 1), shortForms.toArray(), shortForms.toArray()));
        context = new MALEncodingContext(header, operation, 0, null, null);
        body = new GENMessageBody(context, factory, objects.toArray());
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        thrown.expect(NullPointerException.class);
        body.encodeMessageBody(factory, null, result, header.getInteractionStage(), context);
    }

    /**
     * Encoding decoded message body
     * Testing exception for null Context
     *
     * @throws MALException any error
     */
    @Test
    public void internalEncodeMessageBody04() throws MALException {
        header.setInteractionType(InteractionType.SEND);
        header.setInteractionStage(new UOctet((short) 1));
        objects.add(new FineTime(0L));
        objects.add(SessionType.LIVE);
        shortForms.add(Attribute.TIME_SHORT_FORM);
        shortForms.add(SessionType.SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.TIME_SHORT_FORM, new TimeFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(SessionType.SHORT_FORM, new SessionTypeFactory());
        operation = new MALSendOperation(operationNumber, testSend, true, new UShort(1), new MALOperationStage(new UOctet((short) 1), shortForms.toArray(), shortForms.toArray()));
        context = new MALEncodingContext(header, operation, 0, null, null);
        body = new GENMessageBody(context, factory, objects.toArray());
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        thrown.expect(MALException.class);
        thrown.expectMessage("Null MALEncodingContext");
        body.encodeMessageBody(factory, factory.createOutputStream(result), result, header.getInteractionStage(), null);
    }

    /**
     * Encoding decoded message body
     * Testing exception for null Stage
     *
     * @throws MALException any error
     */
    @Test
    public void internalEncodeMessageBody05() throws MALException {
        header.setInteractionType(InteractionType.REQUEST);
        header.setInteractionStage(new UOctet((short) 1));
        objects.add(new FineTime(0L));
        objects.add(SessionType.LIVE);
        shortForms.add(Attribute.TIME_SHORT_FORM);
        shortForms.add(SessionType.SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.TIME_SHORT_FORM, new TimeFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(SessionType.SHORT_FORM, new SessionTypeFactory());

        MALOperationStage invokeStage = new MALOperationStage(new UOctet((short) 1), shortForms.toArray(), shortForms.toArray());
        MALOperationStage invokeAckStage = new MALOperationStage(new UOctet((short) 2), shortForms.toArray(), shortForms.toArray());
        MALOperationStage invokeResponseStage = new MALOperationStage(new UOctet((short) 3), shortForms.toArray(), shortForms.toArray());
        MALInvokeOperation op = new MALInvokeOperation(uShort, identifier, true, new UShort(1), invokeStage, invokeAckStage, invokeResponseStage);

        context = new MALEncodingContext(header, op, 0, null, null);
        body = new GENMessageBody(context, factory, objects.toArray());
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Supplied stage number must not be NULL");
        body.encodeMessageBody(factory, factory.createOutputStream(result), result, null, context);
    }

    /**
     * Decoding encoded message body
     * Encoding 1 element
     *
     * @throws MALException any error
     */
    @Test
    public void internalEncodeMessageBody06() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MessageTestBase.stringToOutputStream(outputStream, encodedXml);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        header.setInteractionType(InteractionType.SEND);
        header.setInteractionStage(new UOctet((short) 1));
        objects.add(new Time(0L));
        objects.add(SessionType.LIVE);
        shortForms.add(Attribute.TIME_SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.TIME_SHORT_FORM, new TimeFactory());
        operation = new MALSendOperation(operationNumber, testSend, true, new UShort(1), new MALOperationStage(new UOctet((short) 1), shortForms.toArray(), shortForms.toArray()));
        context = new MALEncodingContext(header, operation, 0, null, null);
        body = new GENMessageBody(context, false, factory, inputStream, factory.createInputStream(inputStream));
        Assert.assertTrue(body.getBodyElement(0, new Time()).equals(objects.get(0)));
    }

    /**
     * Decoding encoded message body
     * Encoding 2 elements
     *
     * @throws MALException any error
     */
    @Test
    public void internalEncodeMessageBody07() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MessageTestBase.stringToOutputStream(outputStream, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><malxml:Body xmlns:malxml=\"http://www.ccsds.org/schema/malxml/MAL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Time><Time>1970-01-01T00:00:00.000</Time></Time><SessionType><SessionType>LIVE</SessionType></SessionType></malxml:Body>");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        header.setInteractionType(InteractionType.SEND);
        header.setInteractionStage(new UOctet((short) 1));
        objects.add(new Time(0L));
        objects.add(SessionType.LIVE);
        shortForms.add(Attribute.TIME_SHORT_FORM);
        shortForms.add(SessionType.SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.TIME_SHORT_FORM, new TimeFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(SessionType.SHORT_FORM, new SessionTypeFactory());
        operation = new MALSendOperation(operationNumber, testSend, true, new UShort(1), new MALOperationStage(new UOctet((short) 1), shortForms.toArray(), shortForms.toArray()));
        context = new MALEncodingContext(header, operation, 0, null, null);
        body = new GENMessageBody(context, false, factory, inputStream, factory.createInputStream(inputStream));
        Assert.assertTrue(body.getBodyElement(0, new Time()).equals(objects.get(0)));
        Assert.assertTrue(body.getBodyElement(1, SessionType.REPLAY).equals(objects.get(1)));
    }

    /**
     * Decoding encoded message body
     * Expecting error because of null context
     *
     * @throws MALException any error
     */
    @Test
    public void internalEncodeMessageBody08() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MessageTestBase.stringToOutputStream(outputStream, encodedXml);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        header.setInteractionType(InteractionType.SEND);
        header.setInteractionStage(new UOctet((short) 1));
        objects.add(new Time(0L));
        objects.add(SessionType.LIVE);
        shortForms.add(Attribute.TIME_SHORT_FORM);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.TIME_SHORT_FORM, new TimeFactory());
        operation = new MALSendOperation(operationNumber, testSend, true, new UShort(1), new MALOperationStage(new UOctet((short) 1), shortForms.toArray(), shortForms.toArray()));
        context = new MALEncodingContext(header, operation, 0, null, null);
        body = new GENMessageBody(null, false, factory, inputStream, factory.createInputStream(inputStream));
        thrown.expect(NullPointerException.class);
        body.getBodyElement(0, new Time());
    }
}
