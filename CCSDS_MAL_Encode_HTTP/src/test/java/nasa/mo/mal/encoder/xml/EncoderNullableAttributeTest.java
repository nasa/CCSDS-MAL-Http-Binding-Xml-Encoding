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

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Union;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * @author wphyo
 *         Created on 6/21/17.
 */
public class EncoderNullableAttributeTest extends AbstractEncoderTest {

    @Test
    public void uriTest() throws MALException {
        encoder.encodeNullableURI(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableURI() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<URI xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void fineTimeTest() throws MALException {
        encoder.encodeNullableFineTime(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableFineTime() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<FineTime xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void timeTest() throws MALException {
        encoder.encodeNullableTime(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableTime() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<Time xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void durationTest() throws MALException {
        encoder.encodeNullableDuration(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableDuration() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<Duration xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void identifierTest() throws MALException {
        encoder.encodeNullableIdentifier(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableIdentifier() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<Identifier xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void blobTest() throws MALException {
        encoder.encodeNullableBlob(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableBlob() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<Blob xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void uOctetTest() throws MALException {
        encoder.encodeNullableUOctet(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableUOctet() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<UOctet xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void uShortTest() throws MALException {
        encoder.encodeNullableUShort(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableUShort() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<UShort xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void uIntegerTest() throws MALException {
        encoder.encodeNullableUInteger(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableUInteger() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void uLongTest() throws MALException {
        encoder.encodeNullableULong(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableULong() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<ULong xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void octetTest() throws MALException {
        encoder.encodeNullableOctet(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableOctet() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<Octet xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void shortTest() throws MALException {
        encoder.encodeNullableShort(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableShort() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<Short xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void integerTest() throws MALException {
        encoder.encodeNullableInteger(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableInteger() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<Integer xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void longTest() throws MALException {
        encoder.encodeNullableLong(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableLong() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<Long xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void stringTest() throws MALException {
        encoder.encodeNullableString(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableString() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<String xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void doubleTest() throws MALException {
        encoder.encodeNullableDouble(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableDouble() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<Double xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void floatTest() throws MALException {
        encoder.encodeNullableFloat(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableFloat() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<Float xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void booleanTest() throws MALException {
        encoder.encodeNullableBoolean(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableBoolean() == null);
        Assert.assertEquals(XML_BEGINNING +
                "<Boolean xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void attributeTest01() throws MALException {
        encoder.encodeNullableAttribute(null);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING +
                "<Attribute xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableAttribute() == null);
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void elementTest() throws MALException {
        encoder.encodeNullableElement(null);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Element xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableElement(new Blob()) == null);
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }

    @Test
    public void elementNotNullTest() throws MALException {
        Union original = new Union("This is a test");
        encoder.encodeNullableElement(original);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.decodeNullableElement(new Union("")).equals(original));
        Assert.assertEquals(XML_BEGINNING +
                "<Union><String>This is a test</String></Union></malxml:Body>", new String(message.toByteArray()));
        Assert.assertEquals(XML_EMPTY_DOCUMENT, new String(decoder.getRemainingEncodedData()));
    }
}
