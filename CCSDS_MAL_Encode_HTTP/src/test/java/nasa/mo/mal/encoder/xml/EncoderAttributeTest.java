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
import org.ccsds.moims.mo.mal.structures.*;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author wphyo
 *         Created on 6/19/17.
 */
public class EncoderAttributeTest extends AbstractEncoderTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private XmlDecoder decoder;
    @Test
    public void encoderTest1() throws Exception {
        Double original = 23425.56;
        Double original1 = Double.NaN;
        Double original2 = Double.POSITIVE_INFINITY;
        Double original3 = Double.NEGATIVE_INFINITY;
        Double original4 = 0.0;
        Double original5 = -423425.235236;
        encoder.encodeElement(new Union(original));
        encoder.encodeElement(new Union(original1));
        encoder.encodeElement(new Union(original2));
        encoder.encodeElement(new Union(original3));
        encoder.encodeElement(new Union(original4));
        encoder.encodeElement(new Union(original5));
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Union><Double>23425.56</Double></Union><Union><Double>NaN</Double></Union><Union><Double>Infinity</Double></Union><Union><Double>-Infinity</Double></Union><Union><Double>0.0</Double></Union><Union><Double>-423425.235236</Double></Union></malxml:Body>", new String(message.toByteArray()));
        Double decoded = decoder.decodeDouble();
        Double decoded1 = ((Union) decoder.decodeAttribute()).getDoubleValue();
        Double decoded2 = decoder.decodeDouble();
        Double decoded3 = ((Union) decoder.decodeAttribute()).getDoubleValue();
        Double decoded4 = decoder.decodeDouble();
        Double decoded5 = ((Union) decoder.decodeAttribute()).getDoubleValue();
        Assert.assertEquals(original, decoded, Double.MIN_VALUE);
        Assert.assertEquals(original1, decoded1, Double.MIN_VALUE);
        Assert.assertEquals(original2, decoded2, Double.MIN_VALUE);
        Assert.assertEquals(original3, decoded3, Double.MIN_VALUE);
        Assert.assertEquals(original4, decoded4, Double.MIN_VALUE);
        Assert.assertEquals(original5, decoded5, Double.MIN_VALUE);
        Assert.assertEquals(original, decoded, Double.MIN_VALUE);
        }

    @Test
    public void encoderTest2() throws Exception {
        Long original = Long.MIN_VALUE;
        Long original1 = Long.MAX_VALUE;
        Long original2 = 0L;
        Long original3 = 98765432123456L;
        Long original4 = -23434L;
        encoder.encodeElement(new Union(original));
        encoder.encodeElement(new Union(original1));
        encoder.encodeElement(new Union(original2));
        encoder.encodeElement(new Union(original3));
        encoder.encodeElement(new Union(original4));
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Union><Long>-9223372036854775808</Long></Union><Union><Long>9223372036854775807</Long></Union><Union><Long>0</Long></Union><Union><Long>98765432123456</Long></Union><Union><Long>-23434</Long></Union></malxml:Body>", new String(message.toByteArray()));
        Long decoded = decoder.decodeLong();
        Long decoded1 = decoder.decodeLong();
        Long decoded2 = decoder.decodeLong();
        Long decoded3 = decoder.decodeLong();
        Long decoded4 = decoder.decodeLong();
        Assert.assertEquals(original, decoded);
        Assert.assertEquals(original1, decoded1);
        Assert.assertEquals(original2, decoded2);
        Assert.assertEquals(original3, decoded3);
        Assert.assertEquals(original4, decoded4);
        }

    @Test
    public void encoderTest3() throws Exception {
        Short original = Short.MIN_VALUE;
        Short original1 = Short.MAX_VALUE;
        Short original2 = 0;
        Short original3 = 2342;
        Short original4 = -23434;
        encoder.encodeElement(new Union(original));
        encoder.encodeElement(new Union(original1));
        encoder.encodeElement(new Union(original2));
        encoder.encodeElement(new Union(original3));
        encoder.encodeElement(new Union(original4));
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Union><Short>-32768</Short></Union><Union><Short>32767</Short></Union><Union><Short>0</Short></Union><Union><Short>2342</Short></Union><Union><Short>-23434</Short></Union></malxml:Body>", new String(message.toByteArray()));
        Short decoded = decoder.decodeShort();
        Short decoded1 = decoder.decodeShort();
        Short decoded2 = decoder.decodeShort();
        Short decoded3 = decoder.decodeShort();
        Short decoded4 = decoder.decodeShort();
        Assert.assertEquals(original, decoded);
        Assert.assertEquals(original1, decoded1);
        Assert.assertEquals(original2, decoded2);
        Assert.assertEquals(original3, decoded3);
        Assert.assertEquals(original4, decoded4);
        }

    @Test
    public void encoderTest4() throws Exception {
        Byte original = Byte.MIN_VALUE;
        Byte original1 = Byte.MAX_VALUE;
        Byte original2 = 0;
        Byte original3 = 123;
        Byte original4 = -34;
        encoder.encodeElement(new Union(original));
        encoder.encodeElement(new Union(original1));
        encoder.encodeElement(new Union(original2));
        encoder.encodeElement(new Union(original3));
        encoder.encodeElement(new Union(original4));
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Union><Octet>-128</Octet></Union><Union><Octet>127</Octet></Union><Union><Octet>0</Octet></Union><Union><Octet>123</Octet></Union><Union><Octet>-34</Octet></Union></malxml:Body>", new String(message.toByteArray()));
        Byte decoded = decoder.decodeOctet();
        Byte decoded1 = ((Union) decoder.decodeAttribute()).getOctetValue();
        Byte decoded2 = decoder.decodeOctet();
        Byte decoded3 = ((Union) decoder.decodeAttribute()).getOctetValue();
        Byte decoded4 = decoder.decodeOctet();
        Assert.assertEquals(original, decoded);
        Assert.assertEquals(original1, decoded1);
        Assert.assertEquals(original2, decoded2);
        Assert.assertEquals(original3, decoded3);
        Assert.assertEquals(original4, decoded4);
        }

    @Test
    public void encoderTest5() throws Exception {
        UOctet original = new UOctet((short) 0);
        UOctet original1 = new UOctet(Short.MAX_VALUE);
        UOctet original2 = new UOctet((short) 23);
        encoder.encodeElement(original);
        encoder.encodeElement(original1);
        encoder.encodeElement(original2);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<UOctet><UOctet>0</UOctet></UOctet><UOctet><UOctet>32767</UOctet></UOctet><UOctet><UOctet>23</UOctet></UOctet></malxml:Body>", new String(message.toByteArray()));
        UOctet decoded = decoder.decodeUOctet();
        UOctet decoded1 = decoder.decodeUOctet();
        UOctet decoded2 = (UOctet) decoder.decodeAttribute();
        Assert.assertEquals(original, decoded);
        Assert.assertEquals(original1, decoded1);
        Assert.assertEquals(original2, decoded2);
        }

    @Test        
    public void encoderTest6() throws MALException {
        Double original = null;
        thrown.expect(MALException.class);
        encoder.encodeElement(new Union(original));
    }

    @Test
    public void encoderTest7() throws Exception {
        String original = "This is a test String. 这是一个测试。ეს ტესტია. នេះគឺជាការធ្វើតេស្តមួយ។ Test!@#$%^&*()_+-={}|[]\\:\";\'<>?   ,./  ဒါကစမ်းသပ်မှုဖြစ်ပါတယ်။";
        encoder.encodeElement(new Union(original));
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Union><String>This is a test String. 这是一个测试。ეს ტესტია. នេះគឺជាការធ្វើតេស្តមួយ។ Test!@#$%^&amp;*()_+-={}|[]\\:\";'&lt;&gt;?   ,./  ဒါကစမ်းသပ်မှုဖြစ်ပါတယ်။</String></Union></malxml:Body>", new String(message.toByteArray()));
        String decoded = decoder.decodeString();
        Assert.assertEquals(original, decoded);
        }

    @Test        
    public void encoderTest8() throws MALException {
        String original = null;
        thrown.expect(MALException.class);
        encoder.encodeElement(new Union(original));
    }

    @Test
    public void encoderTest9() throws Exception {
        URI original = new URI("https://www.google.com/");
        encoder.encodeElement(original);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<URI><URI>https%3A%2F%2Fwww.google.com%2F</URI></URI></malxml:Body>", new String(message.toByteArray()));
        URI decoded = decoder.decodeURI();
        Assert.assertTrue(original.equals(decoded));
        }

    @Test
    public void encoderTest9_1() throws Exception {
        URI original = new URI("");
        encoder.encodeElement(original);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<URI><URI/></URI></malxml:Body>", new String(message.toByteArray()));
        URI decoded = decoder.decodeURI();
        Assert.assertTrue(original.equals(decoded));
        }

    @Test        
    public void encoderTest9_2() throws Exception {
        URI original = new URI();
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test        
    public void encoderTest10() throws MALException {
        URI original = new URI(null);
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void encoderTest11() throws Exception {
        Duration original = new Duration(987.654321);
        encoder.encodeElement(original);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Duration><Duration>PT16M27.654321S</Duration></Duration></malxml:Body>", new String(message.toByteArray()));
        Duration decoded = decoder.decodeDuration();
        Assert.assertTrue(original.equals(decoded));
        }

    @Test
    public void encoderTest12() throws Exception {
        Duration original = new Duration(-987.654321);
        encoder.encodeElement(original);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Duration><Duration>-PT16M27.654321S</Duration></Duration></malxml:Body>", new String(message.toByteArray()));
        Duration decoded = decoder.decodeDuration();
        Assert.assertTrue(original.equals(decoded));
        }

    @Test
    public void encoderTest13() throws Exception {
        Duration original = new Duration(0.0);
        encoder.encodeElement(original);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Duration><Duration>P</Duration></Duration></malxml:Body>", new String(message.toByteArray()));
        Duration decoded = decoder.decodeDuration();
        Assert.assertTrue(original.equals(decoded));
        }

    @Test        
    public void encoderTest14() throws MALException {
        Duration original = null;
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void encoderTest15() throws Exception {
        Boolean original = true;
        encoder.encodeElement(new Union(original));
        Boolean original2 = false;
        encoder.encodeElement(new Union(original2));
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Union><Boolean>true</Boolean></Union><Union><Boolean>false</Boolean></Union></malxml:Body>", new String(message.toByteArray()));
        Boolean decoded = decoder.decodeBoolean();
        Boolean decoded2 = decoder.decodeBoolean();
        Assert.assertEquals(original, decoded);
        Assert.assertEquals(original2, decoded2);
        }

    @Test
    public void encoderTest16() throws Exception {
        Blob original = new Blob(new byte[] {43, 12, 122, 127, 54, 0, -5});
        encoder.encodeElement(original);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Blob><Blob>2b0c7a7f3600fb</Blob></Blob></malxml:Body>", new String(message.toByteArray()));
        Blob decoded = decoder.decodeBlob();
        Assert.assertTrue(original.equals(decoded));
        }

    @Test
    public void encoderTest17() throws MALException {
        Blob original = new Blob(new byte[0]);
        encoder.encodeElement(original);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Blob><Blob/></Blob></malxml:Body>", new String(message.toByteArray()));
        Blob decoded = decoder.decodeBlob();
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void encoderTest18() throws Exception {
        Identifier original = new Identifier("This is a test String. 这是一个测试。ეს ტესტია. នេះគឺជាការធ្វើតេស្តមួយ។ Test!@#$%^&*()_+-={}|[]\\:\";\'<>?   ,./  ဒါကစမ်းသပ်မှုဖြစ်ပါတယ်။");
        encoder.encodeElement(original);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Identifier><Identifier>=?UTF-8?Q?This_is_a_test_?= =?UTF-8?Q?String._?= =?UTF-8?Q?=E8=BF=99=E6=98=AF=E4=B8=80=E4=B8=AA?= =?UTF-8?Q?=E6=B5=8B=E8=AF=95=E3=80=82=E1=83=94?= =?UTF-8?Q?=E1=83=A1_=E1=83=A2=E1=83=94?= =?UTF-8?Q?=E1=83=A1=E1=83=A2=E1=83=98=E1=83=90?= =?UTF-8?Q?._=E1=9E=93=E1=9F=81=E1=9F=87=E1=9E=82=E1=9E=BA=E1=9E=87?= =?UTF-8?Q?=E1=9E=B6=E1=9E=80=E1=9E=B6=E1=9E=9A?= =?UTF-8?Q?=E1=9E=92=E1=9F=92=E1=9E=9C=E1=9E=BE?= =?UTF-8?Q?=E1=9E=8F=E1=9F=81=E1=9E=9F=E1=9F=92?= =?UTF-8?Q?=E1=9E=8F=E1=9E=98=E1=9E=BD=E1=9E=99?= =?UTF-8?Q?=E1=9F=94_Test!@#$%^&amp;*()=5F+-=3D{}|[]\\:\";'&lt;&gt;?= =?UTF-8?Q?=3F___,./_?= =?UTF-8?Q?_=E1=80=92=E1=80=AB=E1=80=80?= =?UTF-8?Q?=E1=80=85=E1=80=99=E1=80=BA=E1=80=B8?= =?UTF-8?Q?=E1=80=9E=E1=80=95=E1=80=BA=E1=80=99?= =?UTF-8?Q?=E1=80=BE=E1=80=AF=E1=80=96=E1=80=BC?= =?UTF-8?Q?=E1=80=85=E1=80=BA=E1=80=95=E1=80=AB?= =?UTF-8?Q?=E1=80=90=E1=80=9A=E1=80=BA=E1=81=8B?=</Identifier></Identifier></malxml:Body>", new String(message.toByteArray()));
        Identifier decoded = decoder.decodeIdentifier();
        Assert.assertTrue(original.equals(decoded));
        }

    @Test
    public void encoderTest19() throws MALException {
        Identifier original = new Identifier("");
        encoder.encodeElement(original);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Identifier><Identifier/></Identifier></malxml:Body>", new String(message.toByteArray()));
        Identifier decoded = decoder.decodeIdentifier();
        Assert.assertTrue(original.equals(decoded));
    }

    @Test        
    public void encoderTest19_1() throws MALException {
        Identifier original = new Identifier(null);
        thrown.expect(MALException.class);
        encoder.encodeElement(original);
    }

    @Test
    public void encoderTest20() throws Exception {
        FineTime original = new FineTime((Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() * 1000000) + 685937);
        encoder.encodeElement(original);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        FineTime decoded = decoder.decodeFineTime();
        Assert.assertTrue(original.equals(decoded));
        }

    @Test
    public void encoderTest21() throws Exception {
        FineTime original = new FineTime(0);
        encoder.encodeElement(original);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<FineTime><FineTime>1970-01-01T00:00:00.000000000</FineTime></FineTime></malxml:Body>", new String(message.toByteArray()));
        FineTime decoded = decoder.decodeFineTime();
        Assert.assertTrue(original.equals(decoded));
    }

    @Test
    public void encoderTest22() throws Exception {
        Time original = new Time(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
        encoder.encodeElement(original);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Time decoded = decoder.decodeTime();
        Assert.assertTrue(original.equals(decoded));
        }

    @Test
    public void encoderTest23() throws Exception {
        Time original = new Time(0);
        encoder.encodeElement(original);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Time><Time>1970-01-01T00:00:00.000</Time></Time></malxml:Body>", new String(message.toByteArray()));
        Time decoded = decoder.decodeTime();
        Assert.assertTrue(original.equals(decoded));
        }

    @Test
    public void encoderTest24() throws Exception {
        Integer original = 34;
        Integer original1 = -394;
        Integer original2 = Integer.MAX_VALUE;
        Integer original3 = Integer.MIN_VALUE;
        Integer original4 = 0;
        encoder.encodeElement(new Union(original));
        encoder.encodeElement(new Union(original1));
        encoder.encodeElement(new Union(original2));
        encoder.encodeElement(new Union(original3));
        encoder.encodeElement(new Union(original4));
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<Union><Integer>34</Integer></Union><Union><Integer>-394</Integer></Union><Union><Integer>2147483647</Integer></Union><Union><Integer>-2147483648</Integer></Union><Union><Integer>0</Integer></Union></malxml:Body>", new String(message.toByteArray()));
        Integer decoded = decoder.decodeInteger();
        Integer decoded1 = ((Union) decoder.decodeAttribute()).getIntegerValue();
        Integer decoded2 = decoder.decodeInteger();
        Integer decoded3 = ((Union) decoder.decodeAttribute()).getIntegerValue();
        Integer decoded4 = decoder.decodeInteger();
        Assert.assertEquals(original, decoded);
        Assert.assertEquals(original4, decoded4);
        Assert.assertEquals(original1, decoded1);
        Assert.assertEquals(original2, decoded2);
        Assert.assertEquals(original3, decoded3);
        }

    @Test        
    public void encoderTest25() throws MALException {
        Integer original = null;
        thrown.expect(MALException.class);
        encoder.encodeElement(new Union(original));
    }

    @Test
    public void encoderTest26() throws Exception {
        UShort original = new UShort(0);
        UShort original1 = new UShort(Integer.MAX_VALUE);
        UShort original2 = new UShort((short) 23);
        encoder.encodeElement(original);
        encoder.encodeElement(original1);
        encoder.encodeElement(original2);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<UShort><UShort>0</UShort></UShort><UShort><UShort>2147483647</UShort></UShort><UShort><UShort>23</UShort></UShort></malxml:Body>", new String(message.toByteArray()));
        UShort decoded = decoder.decodeUShort();
        UShort decoded1 = decoder.decodeUShort();
        UShort decoded2 = (UShort) decoder.decodeAttribute();
        Assert.assertEquals(original, decoded);
        Assert.assertEquals(original1, decoded1);
        Assert.assertEquals(original2, decoded2);
        }

    @Test
    public void encoderTest27() throws Exception {
        UInteger original = new UInteger( 0);
        UInteger original1 = new UInteger(Long.MAX_VALUE);
        UInteger original2 = new UInteger(23);
        encoder.encodeElement(original);
        encoder.encodeElement(original1);
        encoder.encodeElement(original2);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<UInteger><UInteger>0</UInteger></UInteger><UInteger><UInteger>9223372036854775807</UInteger></UInteger><UInteger><UInteger>23</UInteger></UInteger></malxml:Body>", new String(message.toByteArray()));
        UInteger decoded = decoder.decodeUInteger();
        UInteger decoded1 = decoder.decodeUInteger();
        UInteger decoded2 = (UInteger) decoder.decodeAttribute();
        Assert.assertEquals(original, decoded);
        Assert.assertEquals(original1, decoded1);
        Assert.assertEquals(original2, decoded2);
        }

    @Test
    public void encoderTest28() throws Exception {
        ULong original = new ULong(new BigInteger("0"));
        ULong original1 = new ULong(new BigInteger("1234567898765432123456789876543212345678987654321"));
        encoder.encodeElement(original);
        encoder.encodeElement(original1);
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Assert.assertEquals(XML_BEGINNING +
                "<ULong><ULong>0</ULong></ULong><ULong><ULong>1234567898765432123456789876543212345678987654321</ULong></ULong></malxml:Body>", new String(message.toByteArray()));
        ULong decoded = decoder.decodeULong();
        ULong decoded1 = (ULong) decoder.decodeAttribute();
        Assert.assertEquals(original, decoded);
        Assert.assertEquals(original1, decoded1);
        }

    @Test
    public void encoderTest29() throws Exception {
        Blob original = null;
        encoder.encodeNullableElement(original);
        encoder.encodeNullableAttribute(original);
        encoder.close();
        Assert.assertEquals(XML_BEGINNING +
                "<Element xsi:nil=\"true\"/><Attribute xsi:nil=\"true\"/></malxml:Body>", new String(message.toByteArray()));
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        Blob decoded = decoder.decodeNullableBlob();
        Blob decoded1 = (Blob) decoder.decodeNullableAttribute();
        Assert.assertTrue(decoded == null);
        Assert.assertTrue(decoded1 == null);
        }
}
