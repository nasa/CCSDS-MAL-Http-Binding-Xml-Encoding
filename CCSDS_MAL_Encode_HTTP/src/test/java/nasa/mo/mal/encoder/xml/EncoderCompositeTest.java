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

import nasa.mo.mal.encoder.util.builders.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;

/**
 * @author wphyo
 *         Created on 6/20/17.
 */
public class EncoderCompositeTest extends AbstractEncoderTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Test
    public void subscriptionTest1() throws MALException {
        Subscription original = SubscriptionBuilder.create().build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void subscriptionTest2() throws MALException {
        Subscription original = SubscriptionBuilder.create()
                .subscriptionId(new Identifier()).entities(new EntityRequestList()).build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void subscriptionTest3() throws MALException {
        Subscription original = SubscriptionBuilder.create()
                .subscriptionId(new Identifier("Test")).entities(new EntityRequestList()).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<Subscription><Identifier><Identifier>Test</Identifier></Identifier><EntityRequestList/></Subscription></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Subscription decoded = (Subscription) decoder.decodeElement(new Subscription());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void NamedPairTest1() throws MALException {
        NamedValue original = new NamedValue(new Identifier("Test"), new Union("This is a test"));
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<NamedValue><Identifier><Identifier>Test</Identifier></Identifier><String><String>This is a test</String></String></NamedValue></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        NamedValue decoded = (NamedValue) decoder.decodeElement(new NamedValue());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void NamedPairTest2() throws MALException {
        NamedValue original = new NamedValue();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<NamedValue><Identifier xsi:nil=\"true\"/><Attribute xsi:nil=\"true\"/></NamedValue></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        NamedValue decoded = (NamedValue) decoder.decodeElement(new NamedValue());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void NamedPairTest3() throws MALException {
        NamedValue original = new NamedValue(null, new Union(true));
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<NamedValue><Identifier xsi:nil=\"true\"/><Boolean><Boolean>true</Boolean></Boolean></NamedValue></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        NamedValue decoded = (NamedValue) decoder.decodeElement(new NamedValue());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void NamedPairTest4() throws MALException {
        NamedValue original = new NamedValue(null, new Union((short) 3));
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<NamedValue><Identifier xsi:nil=\"true\"/><Short><Short>3</Short></Short></NamedValue></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        NamedValue decoded = (NamedValue) decoder.decodeElement(new NamedValue());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void NamedPairTest5() throws MALException {
        NamedValue original = new NamedValue(null, null);
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<NamedValue><Identifier xsi:nil=\"true\"/><Attribute xsi:nil=\"true\"/></NamedValue></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        NamedValue decoded = (NamedValue) decoder.decodeElement(new NamedValue());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void NamedPairTest6() throws MALException {
        NamedValue original = new NamedValue(new Identifier(), null);
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void PairTest1() throws MALException {
        Pair original = new Pair();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<Pair><Attribute xsi:nil=\"true\"/><Attribute xsi:nil=\"true\"/></Pair></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Pair decoded = (Pair) decoder.decodeElement(new Pair());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void PairTest2() throws MALException {
        Pair original = new Pair(null, null);
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<Pair><Attribute xsi:nil=\"true\"/><Attribute xsi:nil=\"true\"/></Pair></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Pair decoded = (Pair) decoder.decodeElement(new Pair());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void PairTest3() throws MALException {
        Pair original = new Pair(new Time(23243552424243L), new Duration(76543.234567876543));
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<Pair><Time><Time>2706-07-24T14:20:24.243</Time></Time><Duration><Duration>PT21H15M43.23456787654S</Duration></Duration></Pair></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Pair decoded = (Pair) decoder.decodeElement(new Pair());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void PairTest4() throws MALException {
        Pair original = new Pair(new URI(), null);
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void PairTest5() throws MALException {
        Pair original = new Pair(null, new URI("http://www.google.com"));
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<Pair><Attribute xsi:nil=\"true\"/><URI><URI>http%3A%2F%2Fwww.google.com</URI></URI></Pair></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Pair decoded = (Pair) decoder.decodeElement(new Pair());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void IdBooleanPairTest1() throws MALException {
        IdBooleanPair original = new IdBooleanPair();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<IdBooleanPair><Identifier xsi:nil=\"true\"/><Boolean xsi:nil=\"true\"/></IdBooleanPair></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        IdBooleanPair decoded = (IdBooleanPair) decoder.decodeElement(new IdBooleanPair());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void IdBooleanPairTest2() throws MALException {
        IdBooleanPair original = new IdBooleanPair(null, null);
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<IdBooleanPair><Identifier xsi:nil=\"true\"/><Boolean xsi:nil=\"true\"/></IdBooleanPair></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        IdBooleanPair decoded = (IdBooleanPair) decoder.decodeElement(new IdBooleanPair());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void IdBooleanPairTest3() throws MALException {
        IdBooleanPair original = new IdBooleanPair(new Identifier("test"), null);
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<IdBooleanPair><Identifier><Identifier>test</Identifier></Identifier><Boolean xsi:nil=\"true\"/></IdBooleanPair></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        IdBooleanPair decoded = (IdBooleanPair) decoder.decodeElement(new IdBooleanPair());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void IdBooleanPairTest4() throws MALException {
        IdBooleanPair original = new IdBooleanPair(null, true);
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<IdBooleanPair><Identifier xsi:nil=\"true\"/><Boolean><Boolean>true</Boolean></Boolean></IdBooleanPair></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        IdBooleanPair decoded = (IdBooleanPair) decoder.decodeElement(new IdBooleanPair());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void IdBooleanPairTest5() throws MALException {
        IdBooleanPair original = new IdBooleanPair(new Identifier("test"), true);
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<IdBooleanPair><Identifier><Identifier>test</Identifier></Identifier><Boolean><Boolean>true</Boolean></Boolean></IdBooleanPair></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        IdBooleanPair decoded = (IdBooleanPair) decoder.decodeElement(new IdBooleanPair());
        Assert.assertTrue(original.equals(decoded));
    }

    /**
     * TODO expected behavior for identifier
     * @throws MALException null
     */
    @Test
    public void IdBooleanPairTest6() throws MALException {
        IdBooleanPair original = new IdBooleanPair(new Identifier(), true);
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void FileTest1() throws MALException {
        File original = FileBuilder.create().build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void FileTest2() throws MALException {
        File original = FileBuilder.create().name(new Identifier("Test")).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<File><Identifier><Identifier>Test</Identifier></Identifier><String xsi:nil=\"true\"/><Time xsi:nil=\"true\"/><Time xsi:nil=\"true\"/><ULong xsi:nil=\"true\"/><Blob xsi:nil=\"true\"/><Element xsi:nil=\"true\"/></File></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        File decoded = (File) decoder.decodeElement(new File());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void FileTest3() throws MALException {
        File original = FileBuilder.create().name(new Identifier()).build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void FileTest4() throws MALException {
        File original = FileBuilder.create().name(new Identifier("")).mimeType("").build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<File><Identifier><Identifier/></Identifier><String><String/></String><Time xsi:nil=\"true\"/><Time xsi:nil=\"true\"/><ULong xsi:nil=\"true\"/><Blob xsi:nil=\"true\"/><Element xsi:nil=\"true\"/></File></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        File decoded = (File) decoder.decodeElement(new File());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void FileTest5() throws MALException {
        File original = FileBuilder.create().name(new Identifier("")).mimeType("")
                .creationDate(new Time()).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<File><Identifier><Identifier/></Identifier><String><String/></String><Time><Time>1970-01-01T00:00:00.000</Time></Time><Time xsi:nil=\"true\"/><ULong xsi:nil=\"true\"/><Blob xsi:nil=\"true\"/><Element xsi:nil=\"true\"/></File></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        File decoded = (File) decoder.decodeElement(new File());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void FileTest6() throws MALException {
        File original = FileBuilder.create().name(new Identifier("")).mimeType("")
                .creationDate(new Time()).modificationDate(new Time()).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<File><Identifier><Identifier/></Identifier><String><String/></String><Time><Time>1970-01-01T00:00:00.000</Time></Time><Time><Time>1970-01-01T00:00:00.000</Time></Time><ULong xsi:nil=\"true\"/><Blob xsi:nil=\"true\"/><Element xsi:nil=\"true\"/></File></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        File decoded = (File) decoder.decodeElement(new File());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void FileTest7() throws MALException {
        File original = FileBuilder.create().name(new Identifier("")).mimeType("")
                .creationDate(new Time()).modificationDate(new Time()).size(new ULong()).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<File><Identifier><Identifier/></Identifier><String><String/></String><Time><Time>1970-01-01T00:00:00.000</Time></Time><Time><Time>1970-01-01T00:00:00.000</Time></Time><ULong><ULong>0</ULong></ULong><Blob xsi:nil=\"true\"/><Element xsi:nil=\"true\"/></File></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        File decoded = (File) decoder.decodeElement(new File());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void FileTest8() throws MALException {
        File original = FileBuilder.create().name(new Identifier("")).mimeType("")
                .creationDate(new Time()).modificationDate(new Time()).size(new ULong()).content(new Blob()).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<File><Identifier><Identifier/></Identifier><String><String/></String><Time><Time>1970-01-01T00:00:00.000</Time></Time><Time><Time>1970-01-01T00:00:00.000</Time></Time><ULong><ULong>0</ULong></ULong><Blob><Blob/></Blob><Element xsi:nil=\"true\"/></File></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        File decoded = (File) decoder.decodeElement(new File());
        Assert.assertTrue(original.getName().equals(decoded.getName()));
        Assert.assertTrue(original.getMimeType().equals(decoded.getMimeType()));
        Assert.assertTrue(original.getCreationDate().equals(decoded.getCreationDate()));
        Assert.assertTrue(original.getModificationDate().equals(decoded.getModificationDate()));
        Assert.assertTrue(original.getSize().equals(decoded.getSize()));
        Assert.assertTrue(original.getMetaData() == null && decoded.getMetaData() == null);
    }

    @Test
    public void FileTest9() throws MALException {
        File original = FileBuilder.create().name(new Identifier("")).mimeType("")
                .creationDate(new Time()).modificationDate(new Time()).size(new ULong())
                .content(new Blob(new byte[] {127})).metaData(new NamedValueList()).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<File><Identifier><Identifier/></Identifier><String><String/></String><Time><Time>1970-01-01T00:00:00.000</Time></Time><Time><Time>1970-01-01T00:00:00.000</Time></Time><ULong><ULong>0</ULong></ULong><Blob><Blob>7f</Blob></Blob><NamedValueList/></File></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        File decoded = (File) decoder.decodeElement(new File());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void EntityKeyTest1() throws MALException {
        EntityKey original = EntityKeyBuilder.create().build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<EntityKey><Identifier xsi:nil=\"true\"/><Long xsi:nil=\"true\"/><Long xsi:nil=\"true\"/><Long xsi:nil=\"true\"/></EntityKey></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        EntityKey decoded = (EntityKey) decoder.decodeElement(new EntityKey());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void EntityKeyTest3() throws MALException {
        EntityKey original = EntityKeyBuilder.create().firstSubKey(new Identifier()).build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void EntityKeyTest4() throws MALException {
        EntityKey original = EntityKeyBuilder.create().firstSubKey(new Identifier("Test")).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<EntityKey><Identifier><Identifier>Test</Identifier></Identifier><Long xsi:nil=\"true\"/><Long xsi:nil=\"true\"/><Long xsi:nil=\"true\"/></EntityKey></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        EntityKey decoded = (EntityKey) decoder.decodeElement(new EntityKey());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void EntityKeyTest5() throws MALException {
        EntityKey original = EntityKeyBuilder.create().firstSubKey(new Identifier("Test"))
                .secondSubKey(23L).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<EntityKey><Identifier><Identifier>Test</Identifier></Identifier><Long><Long>23</Long></Long><Long xsi:nil=\"true\"/><Long xsi:nil=\"true\"/></EntityKey></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        EntityKey decoded = (EntityKey) decoder.decodeElement(new EntityKey());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void EntityKeyTest6() throws MALException {
        EntityKey original = EntityKeyBuilder.create().firstSubKey(new Identifier("Test")).secondSubKey(23L)
                .thirdSubKey(45L).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<EntityKey><Identifier><Identifier>Test</Identifier></Identifier><Long><Long>23</Long></Long><Long><Long>45</Long></Long><Long xsi:nil=\"true\"/></EntityKey></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        EntityKey decoded = (EntityKey) decoder.decodeElement(new EntityKey());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void EntityKeyTest7() throws MALException {
        EntityKey original = EntityKeyBuilder.create().firstSubKey(new Identifier("Test")).secondSubKey(23L)
                .thirdSubKey(45L).fourthSubKey(Long.MAX_VALUE).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<EntityKey><Identifier><Identifier>Test</Identifier></Identifier><Long><Long>23</Long></Long><Long><Long>45</Long></Long><Long><Long>9223372036854775807</Long></Long></EntityKey></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        EntityKey decoded = (EntityKey) decoder.decodeElement(new EntityKey());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void UpdateHeaderTest1() throws MALException {
        UpdateHeader original = UpdateHeaderBuilder.create().build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void UpdateHeaderTest3() throws MALException {
        UpdateHeader original = UpdateHeaderBuilder.create().timestamp(new Time()).build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);

    }

    @Test
    public void UpdateHeaderTest4() throws MALException {
        UpdateHeader original = UpdateHeaderBuilder.create().timestamp(new Time()).sourceURI(new URI("")).build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void UpdateHeaderTest5() throws MALException {
        UpdateHeader original = UpdateHeaderBuilder.create().timestamp(new Time()).sourceURI(new URI(""))
                .updateType(UpdateType.UPDATE).build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void UpdateHeaderTest6() throws MALException {
        UpdateHeader original = UpdateHeaderBuilder.create().timestamp(new Time()).sourceURI(new URI(""))
                .updateType(UpdateType.UPDATE).key(EntityKeyBuilder.create().build()).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<UpdateHeader><Time><Time>1970-01-01T00:00:00.000</Time></Time><URI><URI/></URI><UpdateType><UpdateType>UPDATE</UpdateType></UpdateType><EntityKey><Identifier xsi:nil=\"true\"/><Long xsi:nil=\"true\"/><Long xsi:nil=\"true\"/><Long xsi:nil=\"true\"/></EntityKey></UpdateHeader></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        UpdateHeader decoded = (UpdateHeader) decoder.decodeElement(new UpdateHeader());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void EntityRequestTest1() throws MALException {
        EntityRequest original = new EntityRequest();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void EntityRequestTest2() throws MALException {
        EntityRequest original = EntityRequestBuilder.create().build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void EntityRequestTest3() throws MALException {
        EntityRequest original = EntityRequestBuilder.create().allAreas(true).build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void EntityRequestTest4() throws MALException {
        EntityRequest original = EntityRequestBuilder.create().allAreas(true).allServices(true).build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void EntityRequestTest5() throws MALException {
        EntityRequest original = EntityRequestBuilder.create().allAreas(true).allServices(true)
                .allOperations(true).build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void EntityRequestTest6() throws MALException {
        EntityRequest original = EntityRequestBuilder.create().allAreas(true).allServices(true)
                .allOperations(true).onlyOnChange(true).build();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void EntityRequestTest7() throws MALException {
        EntityRequest original = EntityRequestBuilder.create().allAreas(true).allServices(true)
                .allOperations(true).onlyOnChange(true).entityKeys(new EntityKeyList()).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<EntityRequest><Element xsi:nil=\"true\"/><Boolean><Boolean>true</Boolean></Boolean><Boolean><Boolean>true</Boolean></Boolean><Boolean><Boolean>true</Boolean></Boolean><Boolean><Boolean>true</Boolean></Boolean><EntityKeyList/></EntityRequest></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        EntityRequest decoded = (EntityRequest) decoder.decodeElement(new EntityRequest());
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void EntityRequestTest8() throws MALException {
        EntityRequest original = EntityRequestBuilder.create().allAreas(true).allServices(true).subDomain(new IdentifierList())
                .allOperations(true).onlyOnChange(true).entityKeys(new EntityKeyList()).build();
        encoder.encodeElement(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING + "<EntityRequest><IdentifierList/><Boolean><Boolean>true</Boolean></Boolean><Boolean><Boolean>true</Boolean></Boolean><Boolean><Boolean>true</Boolean></Boolean><Boolean><Boolean>true</Boolean></Boolean><EntityKeyList/></EntityRequest></malxml:Body>", new String(message.toByteArray()));
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        EntityRequest decoded = (EntityRequest) decoder.decodeElement(new EntityRequest());
        Assert.assertTrue(original.equals(decoded));
    }
}
