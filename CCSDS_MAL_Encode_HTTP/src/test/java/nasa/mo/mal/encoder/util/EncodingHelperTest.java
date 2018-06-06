/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.encoder.util;

import org.ccsds.moims.mo.mal.MALException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @author Wai Phyo
 *         Created on 4/5/17.
 * Unit Test Cases for helper methods
 */
public class EncodingHelperTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Checking if Encoding Duration has the expected value
     * @throws MALException exception from encoder and decoder
     */
    @Test
    public void encodingDurationTest1() throws MALException {
        Assert.assertEquals("PT1M", EncodingHelper.encodeDuration(60.00));
        Assert.assertEquals("P", EncodingHelper.encodeDuration(00.00));
        Assert.assertEquals("PT1H1M", EncodingHelper.encodeDuration(3660.00));
        Assert.assertEquals("PT1H1M36S", EncodingHelper.encodeDuration(3696.00));
        Assert.assertEquals("P1DT1H1M35S", EncodingHelper.encodeDuration(90095.00));
        Assert.assertEquals("-P1DT1H1M35S", EncodingHelper.encodeDuration(-90095.00));
        Assert.assertEquals("-P10DT1H1M35S", EncodingHelper.encodeDuration(-867695.00));
        Assert.assertEquals("P30DT1H1M35S", EncodingHelper.encodeDuration(2595695.00));
        Assert.assertEquals("P1M1DT1H1M35S", EncodingHelper.encodeDuration(2768495.00));
        Assert.assertEquals("P1YT1H1M35S", EncodingHelper.encodeDuration(31539695.00));
        Assert.assertEquals("P1Y1DT1H1M35S", EncodingHelper.encodeDuration(31626095.00));
        Assert.assertEquals("P1D", EncodingHelper.encodeDuration(86400.00));
        Assert.assertEquals("P1DT0.12S", EncodingHelper.encodeDuration(86400.12));
    }

    /**
     * Checking if decoded text of encoded text is the same as original value
     * @throws MALException exception from encoder and decoder
     */
    @Test
    public void encodingDurationTest5() throws MALException {
        double value1 = 1;
        double value2 = 176543.345;
        double value3 = 100.9876543;
        double value4 = 86399.9876543;
        double value5 = 0;
        double value6 = 99999999.99;
        double value7 = -99999999.99;
        Assert.assertEquals(value1, EncodingHelper.decodeDuration(EncodingHelper.encodeDuration(value1)), 0.001);
        Assert.assertEquals(value2, EncodingHelper.decodeDuration(EncodingHelper.encodeDuration(value2)), 0.001);
        Assert.assertEquals(value3, EncodingHelper.decodeDuration(EncodingHelper.encodeDuration(value3)), 0.00000001);
        Assert.assertEquals(value4, EncodingHelper.decodeDuration(EncodingHelper.encodeDuration(value4)), 0.00000001);
        Assert.assertEquals(value5, EncodingHelper.decodeDuration(EncodingHelper.encodeDuration(value5)), 0.00000001);
        Assert.assertEquals(value6, EncodingHelper.decodeDuration(EncodingHelper.encodeDuration(value6)), 0.00000001);
        Assert.assertEquals(value7, EncodingHelper.decodeDuration(EncodingHelper.encodeDuration(value7)), 0.00000001);
    }

    /**
     * Not a number exception test
     * @throws MALException exception from encoder and decoder
     */
    @Test(expected = MALException.class)
    public void encodingDurationTest2() throws MALException {
        EncodingHelper.encodeDuration(Double.NaN);
    }

    /**
     * Infinity exception test
     * @throws MALException exception from encoder and decoder
     */
    @Test(expected = MALException.class)
    public void encodingDurationTest3() throws MALException {
        EncodingHelper.encodeDuration(Double.NEGATIVE_INFINITY);
    }

    /**
     * Infinity exception test
     * @throws MALException exception from encoder and decoder
     */
    @Test(expected = MALException.class)
    public void encodingDurationTest4() throws MALException {
        EncodingHelper.encodeDuration(Double.POSITIVE_INFINITY);
    }

    /**
     * Test FineTime encoder to encode it to xsd:datetime
     * @throws MALException no exception
     */
    @Test
    public void encodingFineTimeTest() throws MALException {
        long second = TimeUnit.MILLISECONDS
                .toSeconds(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
        long nanoSecond = TimeUnit.SECONDS.toNanos(second) + 123456789;
        Assert.assertTrue(EncodingHelper.encodeFineTimeToXML(nanoSecond)
                .matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.123456789$"));
        nanoSecond = TimeUnit.SECONDS.toNanos(second) + 123;
        Assert.assertTrue(EncodingHelper.encodeFineTimeToXML(nanoSecond)
                .matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.000000123$"));
        nanoSecond = TimeUnit.SECONDS.toNanos(second) + 0;
        Assert.assertTrue(EncodingHelper.encodeFineTimeToXML(nanoSecond)
                .matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.000000000$"));

        nanoSecond = 987654321;
        Assert.assertTrue(EncodingHelper.encodeFineTimeToXML(nanoSecond)
                .equals("1970-01-01T00:00:00.987654321"));
    }

    /**
     * Test decoded finetime from encoded finetime is equal to original value.
     * @throws MALException no exception
     */
    @Test
    public void encodingDecodingFineTimeTest1() throws MALException {
        long second = TimeUnit.MILLISECONDS
                .toSeconds(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
        long nanoSecond = TimeUnit.SECONDS.toNanos(second) + 123456789;
        Assert.assertEquals(EncodingHelper.decodeFineTimeFromXML(EncodingHelper.encodeFineTimeToXML(nanoSecond)),
                nanoSecond);

        nanoSecond = TimeUnit.SECONDS.toNanos(second) + 123;
        Assert.assertEquals(EncodingHelper.decodeFineTimeFromXML(EncodingHelper.encodeFineTimeToXML(nanoSecond)),
                nanoSecond);

        nanoSecond = TimeUnit.SECONDS.toNanos(second);
        Assert.assertEquals(EncodingHelper.decodeFineTimeFromXML(EncodingHelper.encodeFineTimeToXML(nanoSecond)),
                nanoSecond);

        nanoSecond = 987654321;
        Assert.assertEquals(EncodingHelper.decodeFineTimeFromXML(EncodingHelper.encodeFineTimeToXML(nanoSecond)),
                nanoSecond);
    }

    /**
     * testing if exception is thrown for invalid string
     * @throws Exception MAL Exception
     */
    @Test(expected = MALException.class)
    public void decodingFineTimeTest2() throws Exception {
        EncodingHelper.decodeFineTimeFromXML("This is invalid string");
    }

    /**
     * testing if exception is thrown for invalid string
     * @throws Exception MAL Exception
     */
    @Test(expected = MALException.class)
    public void decodingFineTimeTest3() throws Exception {
        EncodingHelper.decodeFineTimeFromXML("1970-1-1T12:12:12.0");
    }

    @Test
    public void decodeTimeFromXMLTest01() throws Exception {
        Assert.assertEquals(0, EncodingHelper.decodeTimeFromXML("1970-01-01T00:00:00.000"));
    }

    @Test(expected = MALException.class)
    public void decodeTimeFromXMLTest02() throws Exception {
        EncodingHelper.decodeTimeFromXML("1970-01-01L00:00:00.000");
    }

    @Test(expected = MALException.class)
    public void encodeDurationTest01() throws Exception {
        EncodingHelper.encodeDuration(Double.NaN);
    }

    @Test(expected = MALException.class)
    public void encodeDurationTest02() throws Exception {
        EncodingHelper.encodeDuration(Double.POSITIVE_INFINITY);
    }

    @Test(expected = MALException.class)
    public void encodeDurationTest03() throws Exception {
        EncodingHelper.encodeDuration(Double.NEGATIVE_INFINITY);
    }

    @Test(expected = MALException.class)
    public void encodeDurationTest04() throws Exception {
        EncodingHelper.encodeDuration(null);
    }

    @Test
    public void decodeFineTimeFromXMLTest01() throws Exception {
        Assert.assertEquals(0, EncodingHelper.decodeFineTimeFromXML("1970-01-01T00:00:00.000000000"));
        Assert.assertEquals(123456789, EncodingHelper.decodeFineTimeFromXML("1970-01-01T00:00:00.123456789"));
        Assert.assertEquals(1123456789, EncodingHelper.decodeFineTimeFromXML("1970-01-01T00:00:01.123456789"));
    }

    @Test(expected = MALException.class)
    public void decodeFineTimeFromXMLTest02() throws Exception {
        EncodingHelper.decodeFineTimeFromXML("1970-01-01T00:00:00:000000000");
    }

    @Test
    public void encodeFineTimeToXMLTest01() throws Exception {
        Assert.assertEquals("1970-01-01T00:00:00.000000001", EncodingHelper.encodeFineTimeToXML(1L));
        Assert.assertEquals("1970-01-01T00:00:00.123456789", EncodingHelper.encodeFineTimeToXML(123456789L));
        Assert.assertEquals("1970-01-01T00:00:01.123456789", EncodingHelper.encodeFineTimeToXML(1123456789L));
    }

    @Test(expected = MALException.class)
    public void decodeDurationTest01() throws Exception {
        EncodingHelper.decodeDuration("P11T1S");
    }

    @Test(expected = MALException.class)
    public void decodeDurationTest02() throws Exception {
        EncodingHelper.decodeDuration("P11DT1MS");
    }

    @Test
    public void parseDurationSegmentTest01() throws Exception {
        Method parseDurationSegmentMethod = EncodingHelper.class.getDeclaredMethod("parseDurationSegment", String.class);
        parseDurationSegmentMethod.setAccessible(true);
        Assert.assertEquals(34, (int) parseDurationSegmentMethod.invoke(null, "34T"));
        Assert.assertEquals(987, (int) parseDurationSegmentMethod.invoke(null, "987R"));
        thrown.expect(InvocationTargetException.class);
        parseDurationSegmentMethod.invoke(null, "987RR");
    }
}
