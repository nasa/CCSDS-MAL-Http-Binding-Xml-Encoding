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

import org.ccsds.moims.mo.mal.MALElementFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.structures.factory.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author wphyo
 *         Created on 6/29/17.
 */
public class MixingElementsTest extends AbstractEncoderTest {

    /**
     * A mixture of MAL Elements & Element Lists to verify if they can encode correctly
     *
     * @throws MALException any exception from encoder
     */
    @Test
    public void testMixedElements() throws MALException {

        InteractionTypeList interactionTypes = new InteractionTypeList();
        interactionTypes.add(InteractionType.INVOKE);
        interactionTypes.add(null);
        interactionTypes.add(InteractionType.PUBSUB);
        interactionTypes.add(null);
        interactionTypes.add(InteractionType.PROGRESS);
        interactionTypes.add(null);
        interactionTypes.add(InteractionType.SUBMIT);
        interactionTypes.add(null);
        interactionTypes.add(InteractionType.SEND);
        interactionTypes.add(null);
        interactionTypes.add(InteractionType.REQUEST);
        interactionTypes.add(null);

        DoubleList doubles = new DoubleList();
        doubles.add(Double.MIN_VALUE);
        doubles.add(Double.MAX_VALUE);
        doubles.add(Double.MIN_NORMAL);
        doubles.add(.0);
        doubles.add(null);

        UpdateHeaderList updateHeaders = new UpdateHeaderList();
        updateHeaders.add(new UpdateHeader(new Time(0L), new URI(""), UpdateType.CREATION, new EntityKey()));
        updateHeaders.add(null);

        List<Element> original = new ArrayList<>();
        original.add(new Identifier("Tämä on testi."));
        original.add(UpdateType.CREATION);
        original.add(new IdBooleanPair(new Identifier("Això és un test."), null));
        original.add(new FloatList());
        original.add(new QoSLevelList());
        original.add(new FileList());
        original.add(new URI("https://drive.google.com/drive/my-drive"));
        original.add(new EntityKey(null, 1L, null, 2L));
        original.add(interactionTypes);
        original.add(SessionType.REPLAY);
        original.add(doubles);
        original.add(new EntityRequest(new IdentifierList(), false, true, false, true, new EntityKeyList()));
        original.add(new Time(Calendar.getInstance().getTimeInMillis()));
        original.add(updateHeaders);

        List<MALElementFactory> samples = new ArrayList<>();
        samples.add(new IdentifierFactory());
        samples.add(new UpdateTypeFactory());
        samples.add(new IdBooleanPairFactory());
        samples.add(new FloatListFactory());
        samples.add(new QoSLevelListFactory());
        samples.add(new FileListFactory());
        samples.add(new URIFactory());
        samples.add(new EntityKeyFactory());
        samples.add(new InteractionTypeListFactory());
        samples.add(new SessionTypeFactory());
        samples.add(new DoubleListFactory());
        samples.add(new EntityRequestFactory());
        samples.add(new TimeFactory());
        samples.add(new UpdateHeaderListFactory());

        original.forEach(this::safeEncoder);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));

        for (int i = 0; i < original.size(); i++) {
            Element decoded = decoder.decodeElement((Element) samples.get(i).createElement());
            Assert.assertTrue(decoded.equals(original.get(i)));
        }
    }

    /**
     * Testing malformed XML with lots of spaces, tabs, and nextlines to see if it can be decoded correctly.
     *
     * @throws MALException required for XML-Decoder
     */
    @Test
    public void spaceTest01() throws MALException {
        String sampleXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n\n<Body xmlns:malxml=\"http://www.ccsds.org/schema/malxml/MAL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> <Union malxml:type=\"281474993487887\">\n" +
                " \n" +
                "        <String>Hello\n</String></Union>\n" +
                "\n\t   \t<Union> \t\t<Boolean>True\n</Boolean>\t\n\n</Union>\t  \t  \n " +
                "\n\t   \t  <Time>\n \t\t<Time>    1970-01-01T00:00:00.999\n</Time>\t\n\n</Time>\t     \t  \n " +
                "\n\t   \t<QoSLevel> \t\t<QoSLevel>\n\tBESTEFFORT\n   </QoSLevel>\t\n\n</QoSLevel>\t  \t  \n " +
                "\t  \t<Pair>" +
                "\n\t   \t<Union> \t\t<Double>\n\t123\n   </Double>\t\n\n</Union>\t  \t  \n " +
                "<Attribute   \t\t  \n xsi:nil=\"  \t true \n   \"/>" +
                "</Pair>    " +
                " \n\n<Union>       <String>  \t  \n  </String></Union>\n" +
                "</Body>";
        System.out.println(sampleXML);
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(sampleXML.getBytes()));
        Assert.assertTrue((decoder.decodeAttribute()).equals(new Union("Hello")));
        Assert.assertTrue((decoder.decodeAttribute()).equals(new Union(true)));
        Assert.assertTrue(decoder.decodeElement(new Time(0)).equals(new Time(999)));
        Assert.assertTrue(decoder.decodeElement(QoSLevel.ASSURED).equals(QoSLevel.BESTEFFORT));
        Assert.assertTrue(decoder.decodeElement(new Pair()).equals(new Pair(new Union(123.0), null)));
        Assert.assertTrue((decoder.decodeAttribute()).equals(new Union("")));
    }

    /**
     * Testing if decoder can retrieve maltype and encoded value
     * Edit: Added a encoded XML with extra spaces to verify if it can be decoded
     *
     * @throws MALException for XML encoder and decoder
     */
    @Test
    public void shortFormTest() throws MALException {
        Union original = new Union(true);
        encoder.encodeElement(original, 123L);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING +
                "<Union malxml:type=\"123\"><Boolean>true</Boolean></Union></malxml:Body>", new String(message.toByteArray()));

        String replacementXML = XML_BEGINNING + "<Union      malxml:type\t\t=\"\n\n123  \"><Boolean>true</Boolean></Union></malxml:Body>";
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertTrue(decoder.getShortForm().equals(123L));
        Assert.assertEquals(original.getBooleanValue(), decoder.decodeBoolean());

        decoder = new XmlDecoder(new ByteArrayInputStream(replacementXML.getBytes()));
        Assert.assertTrue(decoder.getShortForm().equals(123L));
        Assert.assertEquals(original.getBooleanValue(), decoder.decodeBoolean());
    }

    public void test() throws MALException {
        XmlElementOutputStream outputStream = new XmlElementOutputStream(message);
        outputStream.writeElement(new Object[1], null);
    }

    /**
     * Helper class to switch MALException to RuntimeException for lambda
     *
     * @param element MAL element to be encoded
     */
    private void safeEncoder(Element element) {
        try {
            encoder.encodeElement(element);
        } catch (MALException exp) {
            throw new RuntimeException(exp);
        }
    }
}
