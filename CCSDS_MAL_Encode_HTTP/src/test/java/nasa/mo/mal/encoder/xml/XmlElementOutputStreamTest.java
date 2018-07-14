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
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.maldemo.structures.TestMessageHeader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wphyo
 *         Created on 6/29/17.
 * Test cases for XmlElementOutputStream
 */
public class XmlElementOutputStreamTest extends AbstractEncoderTest {
    private TestMessageHeader header;
    private XmlElementOutputStream outputStream;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        outputStream = new XmlElementOutputStream(message);
        header = new TestMessageHeader();
    }

    /**
     * Testing null MAL Encoding Context
     * @throws MALException null exception
     */
    @Test(expected = MALException.class)
    public void writeElementTest1() throws MALException {
        outputStream.writeElement(new Object(), null);
    }

    /**
     * Testing null Header in Encoding context
     * @throws MALException null exception
     */
    @Test(expected = MALException.class)
    public void writeElementTest2() throws MALException {
        MALMessageHeader header = null;
        Object[] shortForms = new Object[1];
        shortForms[0] = Attribute.TIME_SHORT_FORM;
        MALOperationStage stage = new MALOperationStage(new UOctet((short) 1), shortForms, shortForms);
        MALOperation operation = new MALSendOperation(new UShort(1),
                new Identifier("Test"),
                true,
                new UShort(1),
                stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        outputStream.writeElement(new Time(0L), context);
    }





    /**
     * Correctly encoded time element
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest3() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.SEND);
        Object[] shortForms = new Object[1];
        shortForms[0] = Attribute.TIME_SHORT_FORM;
        MALOperationStage stage = new MALOperationStage(new UOctet((short) 1), shortForms, shortForms);
        MALOperation operation = new MALSendOperation(new UShort(1),
                new Identifier("Test"),
                true,
                new UShort(1),
                stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        outputStream.writeElement(new Time(0L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<Time><Time>1970-01-01T00:00:00.000</Time></Time></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded time element which type is unknown
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest4() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.SEND);
        Object[] shortForms = new Object[1];
        MALOperationStage stage = new MALOperationStage(new UOctet((short) 1), shortForms, shortForms);
        MALOperation operation = new MALSendOperation(new UShort(1),
                new Identifier("Test"),
                true,
                new UShort(1),
                stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        outputStream.writeElement(new Time(0L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<Time malxml:type=\"281474993487888\"><Time>1970-01-01T00:00:00.000</Time></Time></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded null element
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest5() throws MALException {
        Object[] shortForms = new Object[1];
        MALOperationStage stage = new MALOperationStage(new UOctet((short) 1), shortForms, shortForms);
        MALOperation operation = new MALSendOperation(new UShort(1),
                new Identifier("Test"),
                true,
                new UShort(1),
                stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        outputStream.writeElement(null, context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<Element xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
    }


    /**
     * Exception while trying to encode not MAL element
     * @throws MALException any exception
     */
    @Test(expected = MALException.class)
    public void writeElementTest6() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.SEND);
        Object[] shortForms = new Object[1];
        shortForms[0] = Attribute.TIME_SHORT_FORM;
        MALOperationStage stage = new MALOperationStage(new UOctet((short) 1), shortForms, shortForms);
        MALOperation operation = new MALSendOperation(new UShort(1),
                new Identifier("Test"),
                true,
                new UShort(1),
                stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        outputStream.writeElement(true, context);
        outputStream.close();
    }

    /**
     * Correctly encoded 1st element of error message
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest7() throws MALException {
        header.setIsErrorMessage(true);
        Object[] shortForms = new Object[1];
        MALOperationStage stage = new MALOperationStage(new UOctet((short) 1), shortForms, shortForms);
        MALOperation operation = new MALSendOperation(new UShort(1),
                new Identifier("Test"),
                true,
                new UShort(1),
                stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        outputStream.writeElement(new UInteger(4294967295L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger><UInteger>4294967295</UInteger></UInteger></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded 2nd element of error message with type
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest8() throws MALException {
        header.setIsErrorMessage(true);
        Object[] shortForms = new Object[1];
        MALOperationStage stage = new MALOperationStage(new UOctet((short) 1), shortForms, shortForms);
        MALOperation operation = new MALSendOperation(new UShort(1),
                new Identifier("Test"),
                true,
                new UShort(1),
                stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);
        outputStream.writeElement(new Duration(65.0), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<Duration malxml:type=\"" + Attribute.DURATION_SHORT_FORM
                + "\"><Duration>PT1M5S</Duration></Duration></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded header keys
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest9() throws MALException {
        header.setURIFrom(uri);
        header.setAuthenticationId(auth);
        header.setTimestamp(time);
        header.setQoSlevel(QoSLevel.ASSURED);
        header.setPriority(uInteger);
        header.setDomain(identifiers);
        header.setNetworkZone(identifier);
        header.setSession(SessionType.LIVE);
        header.setSessionName(identifier);
        header.setInteractionType(InteractionType.SEND);
        header.setInteractionStage(uOctet);
        header.setTransactionId(uInteger.getValue());
        header.setServiceArea(uShort);
        header.setService(uShort);
        header.setOperation(uShort);
        header.setAreaVersion(uOctet);
        header.setIsErrorMessage(true);
        Object[] shortForms = new Object[1];
        MALOperationStage stage = new MALOperationStage(new UOctet((short) 1), shortForms, shortForms);
        MALOperation operation = new MALSendOperation(new UShort(1),
                new Identifier("Test"),
                true,
                new UShort(1),
                stage);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);
        outputStream.writeElement(header, context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING + "<TestMessageHeader><URI><URI>http%3A%2F%2Ftest%3A8080%2F</URI></URI><Blob><Blob>010203</Blob></Blob><URI xsi:nil=\"true\"/><Time><Time>1970-01-01T00:00:00.000</Time></Time><QoSLevel><QoSLevel>ASSURED</QoSLevel></QoSLevel><UInteger><UInteger>1</UInteger></UInteger><IdentifierList/><Identifier><Identifier>Test Identifier</Identifier></Identifier><SessionType><SessionType>LIVE</SessionType></SessionType><Identifier><Identifier>Test Identifier</Identifier></Identifier><InteractionType><InteractionType>SEND</InteractionType></InteractionType><UOctet><UOctet>1</UOctet></UOctet><Long><Long>1</Long></Long><UShort><UShort>1</UShort></UShort><UShort><UShort>1</UShort></UShort><UShort><UShort>1</UShort></UShort><UOctet><UOctet>1</UOctet></UOctet><Boolean><Boolean>true</Boolean></Boolean></TestMessageHeader></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded element for PubSub Register Stage
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest10() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 1));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        outputStream.writeElement(new UInteger(4294967295L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger><UInteger>4294967295</UInteger></UInteger></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded element for PubSub Publish Register Stage
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest11() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 3));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        outputStream.writeElement(new UInteger(4294967295L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger><UInteger>4294967295</UInteger></UInteger></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded element for PubSub Deregister Stage
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest12() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 7));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        outputStream.writeElement(new UInteger(4294967295L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger><UInteger>4294967295</UInteger></UInteger></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded element for PubSub Not Register, Deregister, Notify, Publish, Publish Register Stage
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest13() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 2));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);
        outputStream.writeElement(new UInteger(4294967295L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger malxml:type=\"" + Attribute.UINTEGER_SHORT_FORM +
                "\"><UInteger>4294967295</UInteger></UInteger></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded 1st element w/o type for PubSub Publish Stage
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest14() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 5));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        outputStream.writeElement(new UInteger(4294967295L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger><UInteger>4294967295</UInteger></UInteger></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded 2nd element w/ type for PubSub Publish Stage
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest16() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 5));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);
        outputStream.writeElement(new UInteger(4294967295L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger malxml:type=\"" + Attribute.UINTEGER_SHORT_FORM +
                "\"><UInteger>4294967295</UInteger></UInteger></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded 2nd element w/o type for PubSub Publish Stage
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest17() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 5));
        Object[] shortForms = new Object[1];
        shortForms[0] = Attribute.UINTEGER_SHORT_FORM;
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);
        outputStream.writeElement(new UInteger(4294967295L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger><UInteger>4294967295</UInteger></UInteger></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded 1st element w/o type for PubSub Publish Stage
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest18() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 6));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 0,
                null, null);
        outputStream.writeElement(new UInteger(4294967295L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger><UInteger>4294967295</UInteger></UInteger></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded 2nd element w/o type for PubSub Publish Stage
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest18_1() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 6));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 1,
                null, null);
        outputStream.writeElement(new UInteger(4294967295L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger><UInteger>4294967295</UInteger></UInteger></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded 3rd element w/ type for PubSub Publish Stage
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest19() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 6));
        Object[] shortForms = new Object[1];
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 2,
                null, null);
        outputStream.writeElement(new UInteger(4294967295L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger malxml:type=\"" + Attribute.UINTEGER_SHORT_FORM +
                "\"><UInteger>4294967295</UInteger></UInteger></malxml:Body>", new String(message.toByteArray()));
    }

    /**
     * Correctly encoded 3rd element w/o type for PubSub Publish Stage
     * @throws MALException any exception
     */
    @Test
    public void writeElementTest20() throws MALException {
        header.setIsErrorMessage(false);
        header.setInteractionType(InteractionType.PUBSUB);
        header.setInteractionStage(new UOctet((short) 6));
        Object[] shortForms = new Object[1];
        shortForms[0] = Attribute.UINTEGER_SHORT_FORM;
        MALOperation operation = new MALPubSubOperation(new UShort(1), new Identifier("pubsub"),
                true, new UShort(1), shortForms, shortForms);
        MALEncodingContext context = new MALEncodingContext(header, operation, 2,
                null, null);
        outputStream.writeElement(new UInteger(4294967295L), context);
        outputStream.close();
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger><UInteger>4294967295</UInteger></UInteger></malxml:Body>", new String(message.toByteArray()));
    }
}
