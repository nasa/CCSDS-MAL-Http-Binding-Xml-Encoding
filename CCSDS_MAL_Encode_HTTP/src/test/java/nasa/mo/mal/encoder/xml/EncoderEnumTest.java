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

import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.maldemo.structures.BasicEnum;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author wphyo
 *         Created on 7/31/17.
 */
public class EncoderEnumTest extends AbstractEncoderTest {
    private XmlDecoder decoder;

    @Test
    public void enumTest01() throws Exception {
        encoder.encodeElement(QoSLevel.ASSURED);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING +
                "<QoSLevel><QoSLevel>ASSURED</QoSLevel></QoSLevel></malxml:Body>", new String(message.toByteArray()));
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(QoSLevel.ASSURED, decoder.decodeElement(QoSLevel.QUEUED));
    }

    @Test
    public void enumTest02() throws Exception {
        encoder.encodeElement(SessionType.LIVE);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING +
                "<SessionType><SessionType>LIVE</SessionType></SessionType></malxml:Body>", new String(message.toByteArray()));
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(SessionType.LIVE, decoder.decodeElement(SessionType.REPLAY));
    }

    @Test
    public void enumTest03() throws Exception {
        encoder.encodeElement(InteractionType.PUBSUB);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING +
                "<InteractionType><InteractionType>PUBSUB</InteractionType></InteractionType></malxml:Body>", new String(message.toByteArray()));
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(InteractionType.PUBSUB, decoder.decodeElement(InteractionType.SEND));
    }

    @Test
    public void enumTest04() throws Exception {
        encoder.encodeElement(UpdateType.CREATION);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING +
                "<UpdateType><UpdateType>CREATION</UpdateType></UpdateType></malxml:Body>", new String(message.toByteArray()));
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(UpdateType.CREATION, decoder.decodeElement(UpdateType.MODIFICATION));
    }

    @Test
    public void enumTest05() throws Exception {
        encoder.encodeElement(BasicEnum.FOURTH);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING +
                "<BasicEnum><BasicEnum>FOURTH</BasicEnum></BasicEnum></malxml:Body>", new String(message.toByteArray()));
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(BasicEnum.FOURTH, decoder.decodeElement(BasicEnum.FIRST));
    }


    @Test
    public void enumTest06() throws Exception {
        message = new ByteArrayOutputStream();
        stringToOutputStream(message, XML_BEGINNING +
                "<BasicEnum><BasicEnum>FIFTH</BasicEnum></BasicEnum></malxml:Body>");
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(null, decoder.decodeElement(BasicEnum.FIRST));
    }
}
