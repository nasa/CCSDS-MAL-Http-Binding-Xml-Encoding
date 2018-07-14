/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.encoder.xml;

import org.ccsds.moims.mo.mal.*;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.structures.factory.UIntegerFactory;
import org.ccsds.moims.mo.maldemo.structures.TestMessageHeader;
import org.junit.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * @author wphyo
 *         Created on 6/29/17.
 */
public class XmlElementInputStreamTest extends AbstractEncoderTest {
    private TestMessageHeader header;
    private XmlElementInputStream inputStream;

    @Before
    public void setUp() {
        header = new TestMessageHeader();
    }
    @After
    public void tearDown() {
        MALContextFactory.getElementFactoryRegistry().deregisterElementFactory(Attribute.UINTEGER_SHORT_FORM);
    }

    /**
     * Testing null exception for null context
     * @throws Exception Null exception
     */
    @Test(expected = MALException.class)
    public void readElementTest01() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger malxml:type=\"281474993487884\">" +
                "<UInteger>23432343234</UInteger></UInteger></malxml:Body>");
        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));
        inputStream.readElement(new Object(), null);
    }

    /**
     * Testing to retrieve wrapped body of BLOB
     * @throws Exception Null exception
     */
    @Test
    public void readElementTest02() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<Blob>" +
                "<Blob>010203</Blob></Blob></malxml:Body>");
        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));
        Object decoded = inputStream.readElement(new Blob(), null);
        Assert.assertTrue(decoded instanceof Blob);
        Assert.assertTrue(decoded.equals(new Blob(new byte[] {1, 2, 3})));
    }

    /**
     * Testing header
     * TODO URI-TO is manually added to decoded as the workflow is still not decided.
     * NOTE: GENMessageHeader doesn't override equals() .
     * So they are converted to string and compared.
     *
     * @throws Exception null exception
     */
    @Test
    public void readElementTest03() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<TestMessageHeader><URI><URI>http%3A%2F%2Ftest%3A8080%2F</URI></URI><Blob><Blob>010203</Blob></Blob><URI xsi:nil=\"true\"/><Time><Time>1970-01-01T00:00:00.000</Time></Time><QoSLevel><QoSLevel>ASSURED</QoSLevel></QoSLevel><UInteger><UInteger>1</UInteger></UInteger><IdentifierList/><Identifier><Identifier>Test Identifier</Identifier></Identifier><SessionType><SessionType>LIVE</SessionType></SessionType><Identifier><Identifier>Test Identifier</Identifier></Identifier><InteractionType><InteractionType>SEND</InteractionType></InteractionType><UOctet><UOctet>1</UOctet></UOctet><Long><Long>1</Long></Long><UShort><UShort>1</UShort></UShort><UShort><UShort>1</UShort></UShort><UShort><UShort>1</UShort></UShort><UOctet><UOctet>1</UOctet></UOctet><Boolean><Boolean>true</Boolean></Boolean></TestMessageHeader><Blob>" +
                "<Blob>010203</Blob></Blob></malxml:Body>");
        header.setIsErrorMessage(true);
        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));


        Object[] shortForms = new Object[1];
        MALOperationStage stage = new MALOperationStage(uOctet, shortForms, shortForms);
        MALOperation operation = new MALSendOperation(uShort, identifier, true, uShort, stage);

        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);

        inputStream.readElement(header, context);
        Assert.assertEquals(true, header.getIsErrorMessage());
        Assert.assertEquals(uOctet, header.getAreaVersion());
        Assert.assertEquals(uShort, header.getOperation());
        Assert.assertEquals(uShort, header.getService());
        Assert.assertEquals(uShort, header.getServiceArea());
        Assert.assertEquals(Long.valueOf(uInteger.getValue()), header.getTransactionId());
        Assert.assertEquals(uOctet, header.getInteractionStage());
        Assert.assertEquals(InteractionType.SEND, header.getInteractionType());
        Assert.assertEquals(identifier, header.getSessionName());
        Assert.assertEquals(SessionType.LIVE, header.getSession());
        Assert.assertEquals(identifier, header.getNetworkZone());
        Assert.assertEquals(identifiers, header.getDomain());
        Assert.assertEquals(uInteger, header.getPriority());
        Assert.assertEquals(QoSLevel.ASSURED, header.getQoSlevel());
        Assert.assertEquals(time, header.getTimestamp());
        Assert.assertEquals(auth, header.getAuthenticationId());
        Assert.assertEquals(uri, header.getURIFrom());
    }

    /**
     * Testing 1st element of error body
     * @throws Exception any exception
     */
    @Test
    public void readElementTest04() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger>" +
                "<UInteger>4294967295</UInteger></UInteger></malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));
        header.setIsErrorMessage(true);

        Object[] shortForms = new Object[1];
        MALOperationStage stage = new MALOperationStage(uOctet, shortForms, shortForms);
        MALOperation operation = new MALSendOperation(uShort, identifier, true, uShort, stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        Assert.assertTrue(((UInteger) inputStream.readElement(new UInteger(), context)).getValue() == 4294967295L);
    }

    /**
     * Testing 2nd element with type of error body
     * @throws Exception any exception
     */
    @Test
    public void readElementTest05() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger malxml:type=\"281474993487884\">" +
                "<UInteger>4294967295</UInteger></UInteger></malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));
        header.setIsErrorMessage(true);
        Object[] shortForms = new Object[1];
        MALOperationStage stage = new MALOperationStage(uOctet, shortForms, shortForms);
        MALOperation operation = new MALSendOperation(uShort, identifier, true, uShort, stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);

        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(((UInteger) inputStream.readElement(new UInteger(), context)).getValue() == 4294967295L);
    }

    /**
     * Testing null header exception
     * @throws Exception any exception
     */
    @Test(expected = MALException.class)
    public void readElementTest06() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger>" +
                "<UInteger>23432343234</UInteger></UInteger></malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));


        Object[] shortForms = new Object[1];
        MALOperationStage stage = new MALOperationStage(new UOctet((short) 1), shortForms, shortForms);
        MALOperation operation = new MALSendOperation(new UShort(1),
                new Identifier("Test"),
                true,
                new UShort(1),
                stage);
        MALEncodingContext context = new MALEncodingContext(null, operation, 0,
                null, null);
        inputStream.readElement(new UInteger(), context);
    }

    /**
     * Testing normal message body with known element type
     * @throws Exception any exception
     */
    @Test
    public void readElementTest07() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger>" +
                "<UInteger>4294967295</UInteger></UInteger></malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.SEND);

        Object[] shortForms = new Object[1];
        MALOperationStage stage = new MALOperationStage(uOctet, shortForms, shortForms);
        MALOperation operation = new MALSendOperation(uShort, identifier, true, uShort, stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);
        Assert.assertTrue(((UInteger) inputStream.readElement(new UInteger(), context)).getValue() == 4294967295L);
    }

    /**
     * Testing normal message body with unknown element type. By getting type from XML
     * @throws Exception any exception
     */
    @Test
    public void readElementTest08() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger malxml:type=\"281474993487884\">" +
                "<UInteger>4294967295</UInteger></UInteger></malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.SEND);
        Object[] shortForms = new Object[1];
        MALOperationStage stage = new MALOperationStage(uOctet, shortForms, shortForms);
        MALOperation operation = new MALSendOperation(uShort, identifier, true, uShort, stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(((UInteger) inputStream.readElement(null, context)).getValue() == 4294967295L);
    }

    /**
     * Testing getting remaining data (xml file) in bytes
     * @throws Exception any exception
     */
    @Test
    public void readElementTest09() throws Exception, IOException, ClassNotFoundException {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger malxml:type=\"281474993487884\">" +
                "<UInteger>4294967295</UInteger></UInteger><Element xsi:nil=\"true\"/>" +
                "</malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.SEND);
        Object[] shortForms = new Object[1];
        shortForms[0] = Attribute.UINTEGER_SHORT_FORM;
        MALOperationStage stage = new MALOperationStage(uOctet, shortForms, shortForms);
        MALOperation operation = new MALSendOperation(uShort, identifier, true, uShort, stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);
        Assert.assertEquals(XML_BEGINNING + "<UInteger malxml:type=\"281474993487884\">" +
                "<UInteger>4294967295</UInteger></UInteger><Element xsi:nil=\"true\"/></malxml:Body>", new String(inputStream.getRemainingEncodedData()));

        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(((UInteger) inputStream.readElement(null, context)).getValue() == 4294967295L);

        Assert.assertEquals(XML_BEGINNING + "<Element xsi:nil=\"true\"/></malxml:Body>", new String(inputStream.getRemainingEncodedData()));
    }

    /**
     * Testing getting remaining data (xml file) in bytes
     * Original encoded body has 2 elements,
     * After decoding both elements, remaining body returns an empty body.
     *
     * @throws Exception any exception
     */
    @Test
    public void readElementTest09_1() throws Exception, IOException, ClassNotFoundException {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger malxml:type=\"281474993487884\">" +
                "<UInteger>4294967295</UInteger></UInteger><UInteger malxml:type=\"281474993487884\">" +
                "<UInteger>4294967295</UInteger></UInteger>" +
                "</malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.SEND);
        Object[] shortForms = new Object[1];
        shortForms[0] = Attribute.UINTEGER_SHORT_FORM;
        MALOperationStage stage = new MALOperationStage(uOctet, shortForms, shortForms);
        MALOperation operation = new MALSendOperation(uShort, identifier, true, uShort, stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);

        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(((UInteger) inputStream.readElement(null, context)).getValue() == 4294967295L);
        Assert.assertTrue(((UInteger) inputStream.readElement(null, context)).getValue() == 4294967295L);

        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(inputStream.getRemainingEncodedData()));
    }

    /**
     * Testing getting remaining data (xml file) in bytes
     * Original encoded body has 3 elements,
     * After decoding both elements, remaining body returns an empty body.
     *
     * @throws Exception any exception
     */
    @Test
    public void readElementTest09_2() throws Exception, IOException, ClassNotFoundException {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger malxml:type=\"281474993487884\">" +
                "<UInteger>4294967295</UInteger></UInteger><Element xsi:nil=\"true\"/><UInteger malxml:type=\"281474993487884\">" +
                "<UInteger>4294967295</UInteger></UInteger>" +
                "</malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.SEND);
        Object[] shortForms = new Object[1];
        shortForms[0] = Attribute.UINTEGER_SHORT_FORM;
        MALOperationStage stage = new MALOperationStage(uOctet, shortForms, shortForms);
        MALOperation operation = new MALSendOperation(uShort, identifier, true, uShort, stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);

        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(((UInteger) inputStream.readElement(null, context)).getValue() == 4294967295L);
        Assert.assertTrue(inputStream.readElement(QoSLevel.ASSURED, context) == null);
        Assert.assertTrue(((UInteger) inputStream.readElement(null, context)).getValue() == 4294967295L);
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(inputStream.getRemainingEncodedData()));
    }

    /**
     * Testing getting remaining data (xml file) in bytes
     * Original encoded body has 2 elements,
     * After decoding both elements, remaining body returns an empty body.
     *
     * @throws Exception any exception
     */
    @Test
    public void readElementTest09_3() throws Exception, IOException, ClassNotFoundException {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger malxml:type=\"281474993487884\">" +
                "<UInteger>4294967295</UInteger></UInteger><Element xsi:nil=\"true\"/>" +
                "</malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.SEND);
        Object[] shortForms = new Object[1];
        shortForms[0] = Attribute.UINTEGER_SHORT_FORM;
        MALOperationStage stage = new MALOperationStage(uOctet, shortForms, shortForms);
        MALOperation operation = new MALSendOperation(uShort, identifier, true, uShort, stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);

        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(((UInteger) inputStream.readElement(null, context)).getValue() == 4294967295L);
        Assert.assertTrue(inputStream.readElement(QoSLevel.ASSURED, context) == null);
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(inputStream.getRemainingEncodedData()));
    }

    /**
     * Testing getting remaining data (xml file) in bytes
     * Original encoded body has 1 elements,
     * After decoding both elements, remaining body returns an empty body.
     *
     * @throws Exception any exception
     */
    @Test
    public void readElementTest09_4() throws Exception, IOException, ClassNotFoundException {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<Element xsi:nil=\"true\"/>" +
                "</malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.SEND);
        Object[] shortForms = new Object[1];
        shortForms[0] = Attribute.UINTEGER_SHORT_FORM;
        MALOperationStage stage = new MALOperationStage(uOctet, shortForms, shortForms);
        MALOperation operation = new MALSendOperation(uShort, identifier, true, uShort, stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);

        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(inputStream.readElement(QoSLevel.ASSURED, context) == null);
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(inputStream.getRemainingEncodedData()));
    }

    /**
     * Testing PubSub Publish Stage: element 1, UpdateHeaderList
     * @throws Exception any exception
     */
    @Test
    public void readElementTest10() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UpdateHeaderList />" +
                "</malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 5));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(inputStream.readElement(null, context) instanceof UpdateHeaderList);
    }

    /**
     * Testing PubSub Publish Stage: element 2 and higher
     * @throws Exception any exception
     */
    @Test
    public void readElementTest11() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger malxml:type=\"281474993487884\">" +
                "<UInteger>4294967295</UInteger></UInteger></malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 5));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(((UInteger) inputStream.readElement(null, context)).getValue() == 4294967295L);
    }

    /**
     * Testing PubSub Publish Stage: element 1, Identifier
     * @throws Exception any exception
     */
    @Test
    public void readElementTest12() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<Identifier><Identifier>Testing</Identifier></Identifier>" +
                "</malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 6));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(((Identifier) inputStream.readElement(null, context)).getValue().equals("Testing"));
    }

    /**
     * Testing PubSub Notify Stage: element 2, UpdateHeaderList
     * @throws Exception any exception
     */
    @Test
    public void readElementTest13() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UpdateHeaderList />" +
                "</malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 6));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(inputStream.readElement(null, context) instanceof UpdateHeaderList);
    }

    /**
     * Testing PubSub Notify Stage: element 3 and higher
     * @throws Exception any exception
     */
    @Test
    public void readElementTest14() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger malxml:type=\"281474993487884\">" +
                "<UInteger>4294967295</UInteger></UInteger></malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 6));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 2,
                null, null);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(((UInteger) inputStream.readElement(null, context)).getValue() == 4294967295L);
    }

    /**
     * Testing PubSub other stages: required malxml:type
     * @throws Exception any exception
     */
    @Test
    public void readElementTest15() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger malxml:type=\"281474993487884\">" +
                "<UInteger>4294967295</UInteger></UInteger></malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 2));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 2,
                null, null);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(((UInteger) inputStream.readElement(null, context)).getValue() == 4294967295L);
    }

    /**
     * Testing PubSub other stages: w/o malxml:type, returning null
     * @throws Exception any exception
     */
    @Test
    public void readElementTest16() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<UInteger>" +
                "<UInteger>23432343234</UInteger></UInteger></malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 2));

        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 2,
                null, null);
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        Assert.assertTrue(inputStream.readElement(null, context) == null);
    }

    /**
     * Testing PubSub register stage: subscription ONLY.
     * Body Index is not USED
     * @throws Exception any exception
     */
    @Test
    public void readElementTest17() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<Subscription><Identifier><Identifier>" +
                "Test</Identifier></Identifier><EntityRequestList/></Subscription></malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 1));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 20000,
                null, null);
        Assert.assertTrue(((Subscription) inputStream.readElement(null, context)).toString().equals("(subscriptionId=Test, entities=[])"));
    }

    /**
     * Testing PubSub Publish Register stage: subscription ONLY.
     * Body Index is not USED
     * @throws Exception any exception
     */
    @Test
    public void readElementTest18() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<EntityKeyList /></malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 3));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 276543,
                null, null);
        Assert.assertTrue(inputStream.readElement(null, context) instanceof EntityKeyList);
    }

    /**
     * Testing PubSub Deregister stage: subscription ONLY.
     * Body Index is not USED
     * @throws Exception any exception
     */
    @Test
    public void readElementTest19() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING + "<IdentifierList /></malxml:Body>");

        inputStream = new XmlElementInputStream(new ByteArrayInputStream(message.toByteArray()));

        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 7));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 232,
                null, null);
        Assert.assertTrue(inputStream.readElement(null, context) instanceof IdentifierList);
    }
}
