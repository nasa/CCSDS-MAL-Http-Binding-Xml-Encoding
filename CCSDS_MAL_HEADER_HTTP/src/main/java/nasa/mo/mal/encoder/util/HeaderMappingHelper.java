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

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import net.time4j.Moment;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.scale.TimeScale;
import net.time4j.tz.ZonalOffset;
import org.ccsds.moims.mo.mal.MALException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author wphyo
 *         Created on 7/19/17.
 */
public class HeaderMappingHelper {

    private static final ChronoFormatter<Moment> HEADER_TIME_FORMATTER = ChronoFormatter.ofMomentPattern(
            "uuuu-DDD'T'HH:mm:ss.SSS", PatternType.CLDR, Locale.ROOT, ZonalOffset.UTC);

    private static final String URI_ENCODE_ERR = "Bad URI Encoding";
    private static final String BYTE_TO_HEX_STRING_FORMATTER = "%02x";
    public static final String EMPTY_STRING = "";
    public static final String DOT_SEPARATOR = ".";
    public static final String DEC_MILLISECOND_SEPARATOR = "\\.";

    /**
     * @param value generic object
     * @throws MALException if object is null
     */
    public static void checkForNull(Object value) throws MALException {
        if (value == null) {
            throw new MALException("Null value supplied in a non-nullable field");
        }
    }

    /**
     * Encode timeInMilliSeconds in Long format to formatted String based on BlueBook [CCSDS 301.0-B-4]
     * Section 3.5.1.2 ASCII TIME CODE B, Year/Day of Year Calendar Variation:
     *
     * Format = yyyy-DDDTHH:mm:ss.SSS
     * yyyy = year in 4 digit
     * DDD = day of the year in 3 digit.
     * HH = hour (24 hour format) in 2 digit
     * mm = minute in 2 digit
     * ss = second in 2 digit
     * SSS = millisecond in 3 digits
     *
     * Ending format with 'Z' is ignored based on based on encoding timestamp instruction in [CCSDS 524.3-W-1]
     *
     *
     * NOTE: in BlueBook, 'd' stands for fraction of a second which can have 1 to n characters.
     * Since the incoming timeInMilliSeconds is in milliseconds, d is translated as milliseconds.
     *
     * Since Java.Time does not support leap second, Time4J is used.
     * Time4J moment can create leap seconds.
     * Formatting is done via ChronoFormatter.
     * https://stackoverflow.com/questions/44205662/formatting-time4j-moment/44208333#44208333
     *
     * @param timeInMilliSeconds Input timeInMilliSeconds in millisecond
     * @return Encoded date timeInMilliSeconds string
     * @throws MALException Null exception
     */
    public static String encodeTime(Long timeInMilliSeconds) throws MALException {
        checkForNull(timeInMilliSeconds);

        long timeInSec = TimeUnit.MILLISECONDS.toSeconds(timeInMilliSeconds);
        long millisecond = timeInMilliSeconds - TimeUnit.SECONDS.toMillis(timeInSec);
        Moment ls = Moment.of(timeInSec, (int)TimeUnit.MILLISECONDS.toNanos(millisecond), TimeScale.POSIX);
        return HEADER_TIME_FORMATTER.format(ls);
    }

    /**
     * Decoding back to milliseonds in long form from Date Time format.
     *
     * Moment is used to account for leap seconds.
     * Moment.POSIX will return POSIX time in second.
     * Moment.Nanosecond will return the nanosecond.
     * Combine them to get to the original value.
     *
     * @param dateTime DateTime in yyyy-DDDTHH:mm:ss.SSS form
     * @return Time in Millisecond in long data type
     * @throws MALException invalid formats
     */
    public static long decodeTime(String dateTime) throws MALException {
        long result = 0;
        try {
            Moment parsedMoment = HEADER_TIME_FORMATTER.parse(dateTime);
            result += TimeUnit.SECONDS.toMillis(parsedMoment.getPosixTime());
            result += TimeUnit.NANOSECONDS.toMillis(parsedMoment.getNanosecond());
        } catch (java.text.ParseException exp) {
            throw new MALException(exp.getLocalizedMessage(), exp);
        }
        return result;
    }

    /**
     * Fixed encoding URI to UTF-8.
     *
     * @param normalURI normal URI from MAL Body
     * @return UTF-8 Encoded URI
     * @throws MALException IO Exception
     */
    public static String encodeURI(String normalURI) throws MALException {
        try {
            return URLEncoder.encode(normalURI, StandardCharsets.UTF_8.toString());
        } catch (IOException ex) {
            throw new MALException(URI_ENCODE_ERR, ex);
        }
    }

    /**
     * Decoding UTF-8 encoded URI
     * @param encodedURI UTF-8 encoded URI
     * @return decoded URI
     * @throws MALException IO Exception
     */
    public static String decodeURI(String encodedURI) throws MALException {
        try {
            return URLDecoder.decode(encodedURI, StandardCharsets.UTF_8.toString());
        } catch (IOException ex) {
            throw new MALException(URI_ENCODE_ERR, ex);
        }
    }

    /**
     * MIME decoding technique based on RFC 2047
     * @param encodedString Encoded String sent over HTTP
     * @return decoded original String
     * @throws MALException null Exception
     */
    public static String decodeString(String encodedString) throws MALException {
        checkForNull(encodedString);
        try {
            return MimeUtility.decodeText(encodedString);
        } catch (UnsupportedEncodingException exp) {
            throw new MALException("Error while Decoding String based on RFC2047", exp);
        }
    }

    /**
     * MIME encoding technique based on RFC 2047
     * //http://docs.spring.io/spring/docs/4.3.x/javadoc-api/org/springframework/web/util/UriUtils.html#UriUtils()
     * //http://isu.ifmo.ru/docs/doc102/appdev.102/b14224/oracle/i18n/net/MimeUtility.html
     * @param decodedString decoded normal String
     * @return encoded String to be sent over HTTP
     * @throws MALException null exception
     */
    public static String encodeString(String decodedString) throws MALException {
        checkForNull(decodedString);
        try {
            return MimeUtility.encodeText(decodedString);
        } catch (UnsupportedEncodingException exp) {
            throw new MALException("Error while Encoding String based on RFC2047", exp);
        }
    }

    /**
     * Converting Byte Array to String in hexadecimals.
     *
     * 1. Null and Empty Array validation to return empty string.
     * 2. Convert each value to hex string.
     * 3. return entire string
     *
     * @param byteArray Array of bytes
     * @return String with HEX character for each byte value.
     */
    public static String convertByteArrayToHexString(final byte[] byteArray) {
        if (byteArray == null || byteArray.length == 0) {
            return "";
        }
        StringBuilder result = ObjectFactory.createStringBuilder();
        for (byte each : byteArray) {
            result.append(String.format(BYTE_TO_HEX_STRING_FORMATTER, each));
        }
        return result.toString();
    }

    /**
     * Convert from String to Byte Array
     *
     * if the string is empty -> nothing to convert, return null
     * found algorithm from here
     * https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
     *
     * @param hexString String with Hexadecimal Values
     * @return byte array
     */
    public static byte[] convertHexStringToByteArray(String hexString) {
        if (hexString.length() == 0) {
            return new byte[0];
        } else {
            int len = hexString.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                        + Character.digit(hexString.charAt(i+1), 16));
            }
            return data;
        }
    }
}
