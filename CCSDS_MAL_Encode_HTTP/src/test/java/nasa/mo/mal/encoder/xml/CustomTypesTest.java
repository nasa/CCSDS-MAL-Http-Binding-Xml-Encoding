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

import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.maldemo.structures.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wphyo
 *         Created on 6/28/17.
 */
public class CustomTypesTest extends AbstractEncoderTest {

    @Test
    public void basicEnumTest() throws Exception {
        BasicEnum original = BasicEnum.FOURTH;
        encoder.encodeElement(original);
        encoder.close();
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<BasicEnum><BasicEnum>FOURTH</BasicEnum></BasicEnum></malxml:Body>", new String(message.toByteArray()));
        BasicEnum decoded = (BasicEnum) decoder.decodeElement(BasicEnum.FIRST);
        Assert.assertTrue(original.equals(decoded));
    }

    /**
     * Testing if the exception is thrown if invalid types of enum is decoded.
     * For this case, BasicEnum has First, ..., Fourth.
     * Attempting to decode Fourths should return an null
     *
     * @throws Exception Runtime exception for unknown ordinal
     */
    @Test
    public void invalidBasicEnumTest() throws Exception {
        BasicEnum original = BasicEnum.FOURTH;
        encoder.encodeElement(original);
        encoder.close();
        message = new ByteArrayOutputStream();
        String encodedXml = XML_BEGINNING + "<BasicEnum><BasicEnum>FOURTHS</BasicEnum></BasicEnum></malxml:Body>\n";

        stringToOutputStream(message, encodedXml);
        XmlDecoder decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        BasicEnum decoded = (BasicEnum) decoder.decodeElement(BasicEnum.FIRST);
        Assert.assertTrue(decoded == null);
    }

    @Test
    public void basicEnumListTest() throws Exception {
        List<BasicEnumList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new BasicEnumList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(BasicEnum.FIRST);
        lists.get(4).add(BasicEnum.SECOND);
        lists.get(4).add(BasicEnum.THIRD);
        lists.get(4).add(BasicEnum.FOURTH);
        lists.get(4).add(BasicEnum.FIRST);
        lists.get(5).add(null);
        lists.get(5).add(BasicEnum.SECOND);
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(BasicEnum.THIRD);
        lists.get(5).add(BasicEnum.FOURTH);
        lists.get(5).add(BasicEnum.FIRST);
        lists.get(5).add(null);
        helpTester(lists, new BasicEnumList());
    }

    @Test
    public void basicCompositeTest() throws Exception {
        List<BasicComposite> lists = new ArrayList<>();
        lists.add(new BasicComposite());
        lists.add(new BasicComposite(null, null, null));
        lists.add(new BasicComposite(Short.MIN_VALUE, null, null));
        lists.add(new BasicComposite(null, "Test String", null));
        lists.add(new BasicComposite(null, null, true));
        lists.add(new BasicComposite(Short.MAX_VALUE, "Test String2", false));
        lists.add(new BasicComposite((short) 0, "Test String3", null));
        lists.add(new BasicComposite(null, "Test String4", true));
        lists.add(new BasicComposite(Short.valueOf("2345"), null, true));
        helpTester(lists, new BasicComposite());
    }

    @Test
    public void basicCompositeListTest() throws Exception {
        List<BasicCompositeList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new BasicCompositeList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new BasicComposite());
        lists.get(4).add(new BasicComposite(null, null, null));
        lists.get(4).add(new BasicComposite(Short.MIN_VALUE, null, null));
        lists.get(4).add(new BasicComposite(null, "Test String", null));
        lists.get(4).add(new BasicComposite(null, null, true));
        lists.get(5).add(null);
        lists.get(5).add(new BasicComposite(Short.MAX_VALUE, "Test String2", false));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new BasicComposite((short) 0, "Test String3", null));
        lists.get(5).add(new BasicComposite(null, "Test String4", true));
        lists.get(5).add(new BasicComposite((short) 0, null, false));
        lists.get(5).add(null);
        helpTester(lists, new BasicCompositeList());
    }

    @Test
    public void basicUpdateTest() throws Exception {
        List<BasicUpdate> lists = new ArrayList<>();
        lists.add(new BasicUpdate());
        lists.add(new BasicUpdate(null));
        lists.add(new BasicUpdate((short) 0));
        lists.add(new BasicUpdate(Short.MAX_VALUE));
        lists.add(new BasicUpdate(Short.MIN_VALUE));
        helpTester(lists, new BasicUpdate());
    }

    @Test
    public void basicUpdateListTest() throws Exception {
        List<BasicUpdateList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new BasicUpdateList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new BasicUpdate());
        lists.get(4).add(new BasicUpdate(null));
        lists.get(4).add(new BasicUpdate(Short.MIN_VALUE));
        lists.get(4).add(new BasicUpdate(Short.valueOf("6543", 7)));
        lists.get(4).add(new BasicUpdate(Short.valueOf((short) 2345)));
        lists.get(5).add(null);
        lists.get(5).add(new BasicUpdate(Short.MAX_VALUE));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new BasicUpdate((short) 0l));
        lists.get(5).add(new BasicUpdate(null));
        lists.get(5).add(new BasicUpdate((short) 0));
        lists.get(5).add(null);
        helpTester(lists, new BasicUpdateList());
    }

    @Test
    public void complexCompositeTest() throws Exception {
        List<ComplexComposite> lists = new ArrayList<>();
        lists.add(new ComplexComposite());
        lists.add(new ComplexComposite(null, null, null, null, null, null, null, null, null));
        lists.add(new ComplexComposite(new URI("http://www.google.com"), null, null, null, null, null, null, null, null));
        lists.add(new ComplexComposite(null, true, null, null, null, null, null, null, null));
        lists.add(new ComplexComposite(null, null, 23.45f, null, null, null, null, null, null));
        lists.add(new ComplexComposite(null, null, null, new BasicComposite(), null, null, null, null, null));
        lists.add(new ComplexComposite(null, null, null, null, BasicEnum.THIRD, null, null, null, null));
        lists.add(new ComplexComposite(null, null, null, null, null, QoSLevel.ASSURED, null, null, null));
        lists.add(new ComplexComposite(null, null, null, null, null, null, new IntegerList(), null, null));
        lists.add(new ComplexComposite(null, null, null, null, null, null, null, new BasicEnumList(), null));
        lists.add(new ComplexComposite(null, null, null, null, null, null, null, null, new EntityKey()));
        lists.add(new ComplexComposite(new URI("http://www.google.com"), true, Float.MIN_NORMAL,
                new BasicComposite(Short.MAX_VALUE, "test1", false),
                BasicEnum.SECOND, QoSLevel.BESTEFFORT, getIntegerList(), getBasicEnumList(), new EntityKey(null, 4L, 5L, 1L)));
        helpTester(lists, new ComplexComposite());
    }

    @Test
    public void baseCompositeTest() throws Exception {
        List<BaseComposite> lists = new ArrayList<>();
        lists.add(new ComplexComposite());
        lists.add(new ComplexComposite(null, null, null, null, null, null, null, null, null));
        lists.add(new ComplexComposite(new URI("http://www.google.com"), null, null, null, null, null, null, null, null));
        lists.add(new ComplexComposite(null, true, null, null, null, null, null, null, null));
        lists.add(new ComplexComposite(null, null, 23.45f, null, null, null, null, null, null));
        lists.add(new ComplexComposite(null, null, null, new BasicComposite(), null, null, null, null, null));
        lists.add(new ComplexComposite(null, null, null, null, BasicEnum.THIRD, null, null, null, null));
        lists.add(new ComplexComposite(null, null, null, null, null, QoSLevel.ASSURED, null, null, null));
        lists.add(new ComplexComposite(null, null, null, null, null, null, new IntegerList(), null, null));
        lists.add(new ComplexComposite(null, null, null, null, null, null, null, new BasicEnumList(), null));
        lists.add(new ComplexComposite(null, null, null, null, null, null, null, null, new EntityKey()));
        lists.add(new ComplexComposite(new URI("http://www.google.com"), true, Float.MIN_NORMAL,
                new BasicComposite(Short.MAX_VALUE, "test1", false),
                BasicEnum.SECOND, QoSLevel.BESTEFFORT, getIntegerList(), getBasicEnumList(), new EntityKey(null, 4L, 5L, 1L)));
        helpTester(lists, new ComplexComposite());
    }

    @Test
    public void complexCompositeListTest() throws Exception {
        List<ComplexCompositeList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new ComplexCompositeList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new ComplexComposite());
        lists.get(4).add(new ComplexComposite(null, null, null, null, null, null, null, null, null));
        lists.get(4).add(new ComplexComposite(new URI("http://www.google.com"), null, null, null, null, null, null, null, null));
        lists.get(4).add(new ComplexComposite(null, true, null, null, null, null, null, null, null));
        lists.get(4).add(new ComplexComposite(null, null, 23.45f, null, null, null, null, null, null));
        lists.get(5).add(null);
        lists.get(5).add(new ComplexComposite(null, null, null, new BasicComposite(), null, null, null, null, null));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new ComplexComposite(null, null, null, null, BasicEnum.THIRD, null, null, null, null));
        lists.get(5).add(new ComplexComposite(null, null, null, null, null, QoSLevel.ASSURED, null, null, null));
        lists.get(5).add(new ComplexComposite(null, null, null, null, null, null, new IntegerList(), null, null));
        lists.get(5).add(new ComplexComposite(null, null, null, null, null, null, null, null, new EntityKey()));
        lists.get(5).add(new ComplexComposite(new URI("http://www.google.com"), true, Float.MIN_NORMAL,
                new BasicComposite(Short.MAX_VALUE, "test1", false),
                BasicEnum.SECOND, QoSLevel.BESTEFFORT, getIntegerList(), getBasicEnumList(), new EntityKey(null, 4L, 5L, 1L)));
        lists.get(5).add(null);
        helpTester(lists, new ComplexCompositeList());
    }

    @Test
    public void baseCompositeListTest() throws Exception {
        List<BaseCompositeList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new ComplexCompositeList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new ComplexComposite());
        lists.get(4).add(new ComplexComposite(null, null, null, null, null, null, null, null, null));
        lists.get(4).add(new ComplexComposite(new URI("http://www.google.com"), null, null, null, null, null, null, null, null));
        lists.get(4).add(new ComplexComposite(null, true, null, null, null, null, null, null, null));
        lists.get(4).add(new ComplexComposite(null, null, 23.45f, null, null, null, null, null, null));
        lists.get(5).add(null);
        lists.get(5).add(new ComplexComposite(null, null, null, new BasicComposite(), null, null, null, null, null));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new ComplexComposite(null, null, null, null, BasicEnum.THIRD, null, null, null, null));
        lists.get(5).add(new ComplexComposite(null, null, null, null, null, QoSLevel.ASSURED, null, null, null));
        lists.get(5).add(new ComplexComposite(null, null, null, null, null, null, new IntegerList(), null, null));
        lists.get(5).add(new ComplexComposite(null, null, null, null, null, null, null, null, new EntityKey()));
        lists.get(5).add(new ComplexComposite(new URI("http://www.google.com"), true, Float.MIN_NORMAL,
                new BasicComposite(Short.MAX_VALUE, "test1", false),
                BasicEnum.SECOND, QoSLevel.BESTEFFORT, getIntegerList(), getBasicEnumList(), new EntityKey(null, 4L, 5L, 1L)));
        lists.get(5).add(null);
        helpTester(lists, new ComplexCompositeList());
    }

    private IntegerList getIntegerList() {
        IntegerList integers = new IntegerList();
        integers.add(Integer.MAX_VALUE);
        integers.add(Integer.MIN_VALUE);
        integers.add(0);
        integers.add(null);
        return integers;
    }

    private BasicEnumList getBasicEnumList() {
        BasicEnumList basicEnums = new BasicEnumList();
        basicEnums.add(BasicEnum.FIRST);
        basicEnums.add(BasicEnum.SECOND);
        basicEnums.add(BasicEnum.THIRD);
        basicEnums.add(BasicEnum.FOURTH);
        basicEnums.add(null);
        return basicEnums;
    }
}
