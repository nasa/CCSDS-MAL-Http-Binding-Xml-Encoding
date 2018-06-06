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
import org.junit.Test;

import java.util.Calendar;

/**
 * @author wphyo
 *         Created on 7/19/17.
 */
public class HeaderMappingHelperTest {
    /**
     * Checking null validation
     *
     * @throws MALException will be thrown
     */
    @Test(expected = MALException.class)
    public void encodeTimeTest1() throws MALException {
        HeaderMappingHelper.encodeTime(null);
    }

    /**
     * Using REGEX to verify the format.
     * @throws MALException will not be thrown
     */
    @Test
    public void encodeTimeTest2() throws MALException {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        String time = HeaderMappingHelper.encodeTime(currentTime);
        long decodedTime = HeaderMappingHelper.decodeTime(time);
        Assert.assertEquals(currentTime, decodedTime);
        Assert.assertTrue(HeaderMappingHelper.encodeTime(Calendar.getInstance().getTimeInMillis())
                .matches("^[0-9]{4}-[0-9]{3}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}$"));//
    }

    /**
     * Test to see method is converting bytes to hex strings as expected.
     */
    @Test
    public void convertByteArrayToHexStringTest1() {
        byte[] input = new byte[3];
        input[0] = (byte)15;
        input[1] = (byte)254;
        input[2] = (byte)63;
        Assert.assertEquals(HeaderMappingHelper.convertByteArrayToHexString(input), "0ffe3f");
    }

    /**
     * Test to confirm method return empty string when array is null or empty
     */
    @Test
    public void convertByteArrayToHexStringTest2() {
        byte[] input = new byte[0];
        Assert.assertEquals("", HeaderMappingHelper.convertByteArrayToHexString(null));
        Assert.assertEquals("", HeaderMappingHelper.convertByteArrayToHexString(input));
    }

    /**
     * Test to throw exception when null.
     * @throws MALException null exception
     */
    @Test(expected = MALException.class)
    public void checkForNullTest1() throws MALException {
        HeaderMappingHelper.checkForNull(null);
    }

    /**
     * Mime Encoding testing.
     * Checking if decoded text of encoded text is the same as original text in different languages
     * @throws MALException exception from encoder and decoder
     */
    @Test
    public void encodingTextTest1() throws MALException {
        String simpleText = "This is a test";
        String chineseText = "这是一个测试。";
        String georgianText = "ეს ტესტია.";
        String khmerText = "នេះគឺជាការធ្វើតេស្តមួយ។";
        String complexText = "Test!@#$%^&*()_+-={}|[]\\:\";\'<>?   ,./ ";
        String burmeseText = "ဒါကစမ်းသပ်မှုဖြစ်ပါတယ်။";
        Assert.assertEquals(simpleText, HeaderMappingHelper.decodeString(HeaderMappingHelper.encodeString(simpleText)));
        Assert.assertEquals(complexText, HeaderMappingHelper.decodeString(HeaderMappingHelper.encodeString(complexText)));
        Assert.assertEquals(chineseText, HeaderMappingHelper.decodeString(HeaderMappingHelper.encodeString(chineseText)));
        Assert.assertEquals(georgianText, HeaderMappingHelper.decodeString(HeaderMappingHelper.encodeString(georgianText)));
        Assert.assertEquals(khmerText, HeaderMappingHelper.decodeString(HeaderMappingHelper.encodeString(khmerText)));
        Assert.assertEquals(burmeseText, HeaderMappingHelper.decodeString(HeaderMappingHelper.encodeString(burmeseText)));
    }
}
