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

import net.time4j.Moment;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.scale.TimeScale;
import net.time4j.tz.ZonalOffset;
import org.ccsds.moims.mo.mal.MALException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Wai Phyo
 *         Created on 4/3/17.
 *
 * Static functions consumed by Http encoding and decoding classes.
 */
public class EncodingHelper extends HeaderMappingHelper {
    private static final String DATE_TIME_SEPARATOR = "T";
    private static final String YEAR_DATE_SEPARATOR = "-";
    private static final String TIME_SEPARATOR = ":";
    private static final String ENC_MILLISECOND_SEPARATOR = ".";
    private static final int BASE_16 = 16;
    private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    private static ChronoFormatter<Moment> fineTimeFormatterForXml = ChronoFormatter.ofMomentPattern(
            "uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSS", PatternType.CLDR, Locale.ROOT, ZonalOffset.UTC);
    private static ChronoFormatter<Moment> timeFormatterForXml = ChronoFormatter.ofMomentPattern(
            "uuuu-MM-dd'T'HH:mm:ss.SSS", PatternType.CLDR, Locale.ROOT, ZonalOffset.UTC);
    private static final Pattern XSD_DURATION_PATTERN = Pattern.compile("([-]?)P([0-9]+Y)?([0-9]+M)?([0-9]+D)?(T.+)?");
    private static final Pattern XSD_DURATION_TIME_PATTERN =
            Pattern.compile("T([0-9]+H)?([0-9]+M)?([0-9]+(\\.[0-9]+)?S)?");



    /**
     * Encoded Time in Long format to xsd:dateTime format.
     * Followed one of the example formats from this link
     * http://books.xmlschemata.org/relaxng/ch19-77049.html
     * Format: 2001-10-26T21:32:52.12679
     * Replaced the last part with millisecond.
     * @param time Date Time in Millisecond in Long form (accurate up to millisecond).
     * @return yyyy-MM-ddTHH:mm:ss.ddd
     */
    public static String encodeTimeToXML(Long time) {
        long timeInSec = TimeUnit.MILLISECONDS.toSeconds(time);
        long nanoSecond = TimeUnit.MILLISECONDS.toNanos(time - TimeUnit.SECONDS.toMillis(timeInSec));
        return timeFormatterForXml.format(Moment.of(timeInSec, (int) nanoSecond, TimeScale.POSIX));
    }

    /**
     * Decode Time in xsd:dateTime format to long format.
     * Followed one of the example formats from this link
     * http://books.xmlschemata.org/relaxng/ch19-77049.html
     * Format: 2001-10-26T21:32:52.12679
     * Replaced the last part with millisecond.
     * @param dateTime DateTime in String form of yyyy-MM-ddTHH:mm:ss.ddd
     * @return Millisecond in Long format
     */
    public static long decodeTimeFromXML(String dateTime) throws MALException {
        long result = 0;
        try {
            Moment parsedMoment = timeFormatterForXml.parse(dateTime);
            result += TimeUnit.SECONDS.toMillis(parsedMoment.getPosixTime());
            result += TimeUnit.NANOSECONDS.toMillis(parsedMoment.getNanosecond());
        } catch (java.text.ParseException exp) {
            throw new MALException(exp.getLocalizedMessage(), exp);
        }
        return result;
    }

    /**
     * Based on Blue Book, Duration is in seconds. and fraction of seconds is represented by decimal values.
     *
     * Steps.
     * 1. Start the time with beginning of POSIX time. (1970-01-01T00:00:00.000...)
     * 2. Add the seconds from duration.
     * 3. Subtract from beginning of POSIX time for year, month, date, ...
     * 4. if each subtraction is bigger than 0, add that date or time part to duration.
     * 5. prefix "-" if double is negative value.
     *
     * @param duration second with fraction of second in decimal.
     * @return xsd: duration https://www.w3schools.com/xml/schema_dtypes_date.asp
     */
    public static String encodeDuration(Double duration) throws MALException {
        if (duration == null || duration.isNaN() || duration.isInfinite()) {
            throw new MALException("Invalid Duration");
        }
        StringBuilder result = ObjectFactory.createStringBuilder();
        if (duration < 0) {
            result.append("-");
        }
        result.append("P");

        Calendar minCalendar = Calendar.getInstance(UTC_TIME_ZONE);
        Calendar durationCalendar = Calendar.getInstance(UTC_TIME_ZONE);

        minCalendar.setTimeInMillis(0);
        durationCalendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(Math.abs(duration.longValue())));

        int year = durationCalendar.get(Calendar.YEAR) - minCalendar.get(Calendar.YEAR);
        int month = durationCalendar.get(Calendar.MONTH) - minCalendar.get(Calendar.MONTH);
        int day = durationCalendar.get(Calendar.DAY_OF_MONTH) - minCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = durationCalendar.get(Calendar.HOUR_OF_DAY) - minCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = durationCalendar.get(Calendar.MINUTE) - minCalendar.get(Calendar.MINUTE);
        int second = durationCalendar.get(Calendar.SECOND) - minCalendar.get(Calendar.SECOND);
        BigDecimal fractionOfSecond = BigDecimal.valueOf(duration).remainder(BigDecimal.ONE).abs();
        boolean hasFraction = BigDecimal.ZERO.compareTo(fractionOfSecond) != 0;
        if (year > 0) {
            result.append(year).append("Y");
        }
        if (month > 0) {
            result.append(month).append("M");
        }
        if (day > 0) {
            result.append(day).append("D");
        }


        if (hour == 0 && minute == 0 && second == 0 && !hasFraction) {
            return result.toString();
        } else {
            result.append("T");
        }

        if (hour > 0) {
            result.append(hour).append("H");
        }

        if (minute > 0) {
            result.append(minute).append("M");
        }

        if (second > 0 || hasFraction) {
            if (hasFraction) {
                result.append(second + fractionOfSecond.doubleValue()).append("S");
            } else {
                result.append(second).append("S");
            }
        }

        return result.toString();
    }

    /**
     * Decoding XSD Duration format back to seconds,
     *
     * 1. Check Date part of duration is correct.
     * 1.1 If time part (T) exists in duration, there must be something following it.
     * 2. Check Time part.
     * 3. Create a calendar and set to minimum POSIX time.
     * 4. start filling in values if they are not null.
     * 5. get seconds.
     *
     * @param xsdDuration String in XSD formation
     * @return seconds in double
     * @throws MALException invalid types or number format exceptions
     */
    public static double decodeDuration(String xsdDuration) throws MALException {
        Matcher dateMatcher = XSD_DURATION_PATTERN.matcher(xsdDuration);
        double duration = 0.0;
        if (dateMatcher.matches()) {
            Calendar durationCalendar = Calendar.getInstance(UTC_TIME_ZONE);
            durationCalendar.setTimeInMillis(0);
            String negative = dateMatcher.group(1);
            String year = dateMatcher.group(2);
            String month = dateMatcher.group(3);
            String day = dateMatcher.group(4);
            String time = dateMatcher.group(5);


            if (time != null) {
                Matcher timeMatcher = XSD_DURATION_TIME_PATTERN.matcher(time);
                if (timeMatcher.matches()) {
                    String hour = timeMatcher.group(1);
                    String minute = timeMatcher.group(2);
                    String second = timeMatcher.group(3);
                    if (hour != null) {
                        durationCalendar.add(Calendar.HOUR_OF_DAY, parseDurationSegment(hour));
                    }
                    if (minute != null) {
                        durationCalendar.add(Calendar.MINUTE, parseDurationSegment(minute));
                    }
                    if (second != null) {
                        try {
                            duration += Double.parseDouble(second.substring(0, second.length() - 1));
                        } catch (NumberFormatException exp) {
                            throw new MALException(exp.getLocalizedMessage(), exp);
                        }
                    }
                } else {
                    throw new MALException("Invalid Time Expression in XSD Duration.");
                }
            }


            if (year != null) {
                durationCalendar.add(Calendar.YEAR, parseDurationSegment(year));
            }

            if (month != null) {
                durationCalendar.add(Calendar.MONTH, parseDurationSegment(month));
            }

            if (day != null) {
                durationCalendar.add(Calendar.DAY_OF_MONTH, parseDurationSegment(day));
            }
            duration += TimeUnit.MILLISECONDS.toSeconds(durationCalendar.getTimeInMillis());
            if (negative != null && negative.equals("-")) {
                duration *= -1;
            }
            return duration;
        } else {
            throw new MALException("Invalid Date Expression in XSD Duration.");
        }
    }

    /**
     * Converting to integer
     * @param input String starts with 1 or more numbers, ending with 1 character.
     * @return integer
     * @throws MALException Number Format Exception
     */
    private static int parseDurationSegment(String input) throws MALException {
        try {
            return Integer.parseInt(input.substring(0, input.length() - 1));
        } catch (NumberFormatException exp) {
            throw new MALException(exp.getLocalizedMessage(), exp);
        }
    }

    /**
     * TODO implementation
     * @param fineTime
     * @return
     */
    public static String encodeFineTimeToXML(Long fineTime) throws MALException {
        checkForNull(fineTime);
        long timeInSecond = TimeUnit.NANOSECONDS.toSeconds(fineTime);
        long nanoSecond = fineTime - TimeUnit.SECONDS.toNanos(timeInSecond);
        return fineTimeFormatterForXml.format(Moment.of(timeInSecond, (int) nanoSecond, TimeScale.POSIX));
    }


    /**
     * TODO implementation
     * @param fineTime
     * @return
     */
    public static long decodeFineTimeFromXML(String fineTime) throws MALException {
        long result = 0;
        try {
            Moment parsedMoment = fineTimeFormatterForXml.parse(fineTime);
            result += parsedMoment.getNanosecond();
            result += TimeUnit.SECONDS.toNanos(parsedMoment.getPosixTime());
            return result;
        } catch (ParseException exp) {
            throw new MALException(exp.getLocalizedMessage(), exp);
        }
    }
}
