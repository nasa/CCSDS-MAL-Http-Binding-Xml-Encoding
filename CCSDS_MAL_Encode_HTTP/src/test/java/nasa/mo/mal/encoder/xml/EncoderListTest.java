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

import org.ccsds.moims.mo.mal.structures.*;
import org.junit.Test;

import java.math.BigInteger;
import java.util.*;

/**
 * @author wphyo
 *         Created on 6/20/17.
 * Testing encoding & decoding of lists from default structures
 * Testing
 * 1. empty list
 * 2. 1 null element list
 * 3. 1 valid element list
 * 4. more than 1 null element list
 * 5. more than 1 valid element list
 * 6. mixture of null and valid element list
 */
public class EncoderListTest extends AbstractEncoderTest {
    /**
     * QoSLevel List test
     */
    @Test
    public void qoSLevelListTest() throws Exception {
        List<QoSLevelList> lists = new ArrayList<>();
        lists.add(new QoSLevelList());
        lists.add(new QoSLevelList());
        lists.add(new QoSLevelList());
        lists.add(new QoSLevelList());
        lists.add(new QoSLevelList());
        lists.add(new QoSLevelList());
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(QoSLevel.ASSURED);
        lists.get(4).add(QoSLevel.ASSURED);
        lists.get(4).add(QoSLevel.BESTEFFORT);
        lists.get(4).add(QoSLevel.QUEUED);
        lists.get(4).add(QoSLevel.TIMELY);
        lists.get(5).add(null);
        lists.get(5).add(QoSLevel.ASSURED);
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(QoSLevel.TIMELY);
        lists.get(5).add(QoSLevel.QUEUED);
        lists.get(5).add(null);
        helpTester(lists, new QoSLevelList());
    }

    /**
     * UpdateType List test
     */
    @Test
    public void updateTypeListTest() throws Exception {
        List<UpdateTypeList> lists = new ArrayList<>();
        lists.add(new UpdateTypeList());
        lists.add(new UpdateTypeList());
        lists.add(new UpdateTypeList());
        lists.add(new UpdateTypeList());
        lists.add(new UpdateTypeList());
        lists.add(new UpdateTypeList());
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(UpdateType.CREATION);
        lists.get(4).add(UpdateType.DELETION);
        lists.get(4).add(UpdateType.MODIFICATION);
        lists.get(4).add(UpdateType.UPDATE);
        lists.get(4).add(UpdateType.CREATION);
        lists.get(5).add(null);
        lists.get(5).add(UpdateType.DELETION);
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(UpdateType.MODIFICATION);
        lists.get(5).add(UpdateType.UPDATE);
        lists.get(5).add(null);
        helpTester(lists, new UpdateTypeList());
    }

    /**
     * InteractionType List test
     */
    @Test
    public void interactionTypeListTest() throws Exception {
        List<InteractionTypeList> lists = new ArrayList<>();
        lists.add(new InteractionTypeList());
        lists.add(new InteractionTypeList());
        lists.add(new InteractionTypeList());
        lists.add(new InteractionTypeList());
        lists.add(new InteractionTypeList());
        lists.add(new InteractionTypeList());
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(InteractionType.SEND);
        lists.get(4).add(InteractionType.SUBMIT);
        lists.get(4).add(InteractionType.PROGRESS);
        lists.get(4).add(InteractionType.INVOKE);
        lists.get(4).add(InteractionType.PUBSUB);
        lists.get(5).add(null);
        lists.get(5).add(InteractionType.REQUEST);
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(InteractionType.PUBSUB);
        lists.get(5).add(InteractionType.INVOKE);
        lists.get(5).add(null);
        helpTester(lists, new InteractionTypeList());
    }

    /**
     * SessionType List test
     */
    @Test
    public void sessionTypeListTest() throws Exception {
        List<SessionTypeList> lists = new ArrayList<>();
        lists.add(new SessionTypeList());
        lists.add(new SessionTypeList());
        lists.add(new SessionTypeList());
        lists.add(new SessionTypeList());
        lists.add(new SessionTypeList());
        lists.add(new SessionTypeList());
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(SessionType.LIVE);
        lists.get(4).add(SessionType.REPLAY);
        lists.get(4).add(SessionType.SIMULATION);
        lists.get(4).add(SessionType.LIVE);
        lists.get(4).add(SessionType.REPLAY);
        lists.get(5).add(null);
        lists.get(5).add(SessionType.SIMULATION);
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(SessionType.LIVE);
        lists.get(5).add(SessionType.REPLAY);
        lists.get(5).add(null);
        helpTester(lists, new SessionTypeList());
    }

    /**
     * FineTime List Test
     */
    @Test
    public void fineTimeListTest() throws Exception {
        List<FineTimeList> lists = new ArrayList<>();
        lists.add(new FineTimeList());
        lists.add(new FineTimeList());
        lists.add(new FineTimeList());
        lists.add(new FineTimeList());
        lists.add(new FineTimeList());
        lists.add(new FineTimeList());
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new FineTime(0));
        lists.get(4).add(new FineTime(2736455647382918272L));
        lists.get(4).add(new FineTime(Calendar.getInstance().getTimeInMillis() * 1000000));
        lists.get(4).add(new FineTime(Calendar.getInstance().getTimeInMillis() * 1000000 + 123));
        lists.get(4).add(new FineTime(Calendar.getInstance().getTimeInMillis() * 1000000 + 987655));
        lists.get(5).add(null);
        lists.get(5).add(new FineTime(Calendar.getInstance().getTimeInMillis() * 1000000 + 23));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new FineTime(0));
        lists.get(5).add(new FineTime(Calendar.getInstance().getTimeInMillis() * 1000000 + 987654));
        lists.get(5).add(null);
        helpTester(lists, new FineTimeList());
    }

    /**
     * Time List Test
     */
    @Test
    public void timeListTest() throws Exception {
        List<TimeList> lists = new ArrayList<>();
        lists.add(new TimeList());
        lists.add(new TimeList());
        lists.add(new TimeList());
        lists.add(new TimeList());
        lists.add(new TimeList());
        lists.add(new TimeList());
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new Time(0));
        lists.get(4).add(new Time(2736455647382918272L));
        lists.get(4).add(new Time(Calendar.getInstance().getTimeInMillis()));
        lists.get(4).add(new Time(Calendar.getInstance().getTimeInMillis() + 123));
        lists.get(4).add(new Time(Calendar.getInstance().getTimeInMillis() + 987655));
        lists.get(5).add(null);
        lists.get(5).add(new Time(Calendar.getInstance().getTimeInMillis() + 23));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new Time(0));
        lists.get(5).add(new Time(Calendar.getInstance().getTimeInMillis() + 987654));
        lists.get(5).add(null);
        helpTester(lists, new TimeList());
    }

    /**
     * Duration List Test
     */
    @Test
    public void durationListTest() throws Exception {
        List<DurationList> lists = new ArrayList<>();
        lists.add(new DurationList());
        lists.add(new DurationList());
        lists.add(new DurationList());
        lists.add(new DurationList());
        lists.add(new DurationList());
        lists.add(new DurationList());
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new Duration(0.87654));
        lists.get(4).add(new Duration(-652.34567));
        lists.get(4).add(new Duration(98765.323456));
        lists.get(4).add(new Duration(-123.45678987654345678765432345));
        lists.get(4).add(new Duration(76.4323456789876543234));
        lists.get(5).add(null);
        lists.get(5).add(new Duration(-1234.787654324567876));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new Duration(0));
        lists.get(5).add(new Duration(-0));
        lists.get(5).add(new Duration(-98765432.2345654));
        lists.get(5).add(null);
        helpTester(lists, new DurationList());
    }

    /**
     * URI List Test
     */
    @Test
    public void uriListTest() throws Exception {
        List<URIList> lists = new ArrayList<>();
        lists.add(new URIList());
        lists.add(new URIList());
        lists.add(new URIList());
        lists.add(new URIList());
        lists.add(new URIList());
        lists.add(new URIList());
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new URI(""));
        lists.get(4).add(new URI("localhost"));
        lists.get(4).add(new URI(""));
        lists.get(4).add(new URI(""));
        lists.get(4).add(new URI("http://0.0.0.0:1111"));
        lists.get(4).add(new URI("malhttp://127.0.0.1:7654"));
        lists.get(4).add(new URI("malhttp://127.0.0.1:7654/Service1.aspx"));
        lists.get(5).add(null);
        lists.get(5).add(new URI("malhttp://127.0.0.1:7654/Service1.aspx?id=8765"));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new URI("malhttp://127.0.0.1:7654/Service1.aspx?name=test&server=this"));
        lists.get(5).add(new URI("malhttp://[abcd:6543:0:0000:234f:2f2a:0:1001]:7654/Service1.aspx"));
        lists.get(5).add(new URI("malhttp://[abcd:6543:0:0000:234f:2f2a:0:1001]:7654/Service1.aspx?name=test&server=this"));
        lists.get(5).add(null);
        helpTester(lists, new URIList());
    }

    /**
     * Blob List Test
     */
    @Test
    public void blobListTest() throws Exception {
        List<BlobList> lists = new ArrayList<>();
        lists.add(new BlobList());
        lists.add(new BlobList());
        lists.add(new BlobList());
        lists.add(new BlobList());
        lists.add(new BlobList());
        lists.add(new BlobList());
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new Blob(new byte[1]));
        lists.get(4).add(new Blob(new byte[] {12, 12, 23, 45}));
        lists.get(4).add(new Blob(new byte[] {1, 11, 111}));
        lists.get(4).add(new Blob(new byte[] {127, -128, 0}));
        lists.get(4).add(new Blob(new byte[] {65, 43, 21, 98, 76, 54, 32, 12, 34, 56, 78}));
        lists.get(5).add(null);
        lists.get(5).add(new Blob(new byte[] {87, 65}));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new Blob(new byte[] {127, -128, 0}));
        lists.get(5).add(new Blob(new byte[] {1, 11, 111}));
        lists.get(5).add(new Blob(new byte[] {1}));
        lists.get(5).add(null);
        helpTester(lists, new BlobList());
    }

    /**
     * Identifier List Test
     */
    @Test
    public void identifierListTest() throws Exception {
        List<IdentifierList> lists = new ArrayList<>();
        lists.add(new IdentifierList());
        lists.add(new IdentifierList());
        lists.add(new IdentifierList());
        lists.add(new IdentifierList());
        lists.add(new IdentifierList());
        lists.add(new IdentifierList());
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new Identifier("TestString"));
        lists.get(4).add(new Identifier("TestString"));
        lists.get(4).add(new Identifier("TestString"));
        lists.get(4).add(new Identifier("TestString"));
        lists.get(4).add(new Identifier("TestString"));
        lists.get(5).add(null);
        lists.get(5).add(new Identifier("TestString"));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new Identifier("TestString"));
        lists.get(5).add(new Identifier("TestString"));
        lists.get(5).add(new Identifier("TestString"));
        lists.get(5).add(null);
        helpTester(lists, new IdentifierList());
    }

    /**
     * Unsigned Octet List Test
     */
    @Test
    public void uOctetListTest() throws Exception {
        List<UOctetList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new UOctetList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new UOctet());
        lists.get(4).add(new UOctet(Short.valueOf("6534")));
        lists.get(4).add(new UOctet(Short.valueOf("345", 8)));
        lists.get(4).add(new UOctet(Short.MAX_VALUE));
        lists.get(4).add(new UOctet((short) 0));
        lists.get(5).add(null);
        lists.get(5).add(new UOctet());
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new UOctet((short) 543));
        lists.get(5).add(new UOctet((short) 222));
        lists.get(5).add(new UOctet());
        lists.get(5).add(null);
        helpTester(lists, new UOctetList());
    }

    /**
     * Unsigned Short List Test
     */
    @Test
    public void uShortListTest() throws Exception {
        List<UShortList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new UShortList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new UShort());
        lists.get(4).add(new UShort(Integer.valueOf("6534")));
        lists.get(4).add(new UShort(Integer.valueOf("345", 16)));
        lists.get(4).add(new UShort(Integer.MAX_VALUE));
        lists.get(4).add(new UShort(0));
        lists.get(5).add(null);
        lists.get(5).add(new UShort());
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new UShort(543));
        lists.get(5).add(new UShort(Integer.MAX_VALUE));
        lists.get(5).add(new UShort());
        lists.get(5).add(null);
        helpTester(lists, new UShortList());
    }

    /**
     * Unsigned Integer List Test
     */
    @Test
    public void uIntegerListTest() throws Exception {
        List<UIntegerList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new UIntegerList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new UInteger());
        lists.get(4).add(new UInteger(Long.valueOf("6534")));
        lists.get(4).add(new UInteger(Long.valueOf("876543234", 16)));
        lists.get(4).add(new UInteger(Long.MAX_VALUE));
        lists.get(4).add(new UInteger(0));
        lists.get(5).add(null);
        lists.get(5).add(new UInteger());
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new UInteger(543L));
        lists.get(5).add(new UInteger(Long.MAX_VALUE));
        lists.get(5).add(new UInteger());
        lists.get(5).add(null);
        helpTester(lists, new UIntegerList());
    }

    /**
     * Unsigned Long List Test
     */
    @Test
    public void uLongListTest() throws Exception {
        List<ULongList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new ULongList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new ULong());
        lists.get(4).add(new ULong(new BigInteger("8765434567876543456787654345678765434567897654345678765434567", 16)));
        lists.get(4).add(new ULong(new BigInteger(54, new Random())));
        lists.get(4).add(new ULong(BigInteger.valueOf(8765434564565443257L)));
        lists.get(4).add(new ULong(BigInteger.TEN));
        lists.get(5).add(null);
        lists.get(5).add(new ULong());
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new ULong(BigInteger.ZERO));
        lists.get(5).add(new ULong(BigInteger.ONE));
        lists.get(5).add(new ULong());
        lists.get(5).add(null);
        helpTester(lists, new ULongList());
    }

    /**
     * Unsigned Boolean List Test
     */
    @Test
    public void booleanListTest() throws Exception {
        List<BooleanList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new BooleanList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(new Boolean("true"));
        lists.get(4).add(new Boolean("false"));
        lists.get(4).add(Boolean.valueOf(true));
        lists.get(4).add(Boolean.valueOf(false));
        lists.get(4).add(true);
        lists.get(5).add(null);
        lists.get(5).add(false);
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(Boolean.FALSE);
        lists.get(5).add(Boolean.TRUE);
        lists.get(5).add(Boolean.valueOf("true"));
        lists.get(5).add(null);
        helpTester(lists, new BooleanList());
    }

    /**
     * Unsigned String List Test
     */
    @Test
    public void stringListTest() throws Exception {
        List<StringList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new StringList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add("");
        lists.get(4).add("Test");
        lists.get(4).add("This is a test");
        lists.get(4).add("");
        lists.get(4).add("这是一个测试");
        lists.get(5).add(null);
        lists.get(5).add("ეს ტესტია.");
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add("នេះគឺជាការធ្វើតេស្តមួយ។");
        lists.get(5).add("Test!@#$%^&*()_+-={}|[]\\:\";\'<>?   ,./");
        lists.get(5).add("ဒါကစမ်းသပ်မှုဖြစ်ပါတယ်။");
        lists.get(5).add(null);
        helpTester(lists, new StringList());
    }

    /**
     * Unsigned Octet List Test
     */
    @Test
    public void octetListTest() throws Exception {
        List<OctetList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new OctetList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(Byte.MIN_VALUE);
        lists.get(4).add(Byte.MAX_VALUE);
        lists.get(4).add(Byte.parseByte("34"));
        lists.get(4).add((byte) 127);
        lists.get(4).add((byte) -128);
        lists.get(5).add(null);
        lists.get(5).add((byte) 0);
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(Byte.decode("76"));
        lists.get(5).add(Byte.valueOf("125"));
        lists.get(5).add(new Byte("-127"));
        lists.get(5).add(null);
        helpTester(lists, new OctetList());
    }


    /**
     * Unsigned Short List Test
     */
    @Test
    public void shortListTest() throws Exception {
        List<ShortList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new ShortList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(Short.MIN_VALUE);
        lists.get(4).add(Short.MAX_VALUE);
        lists.get(4).add(Short.valueOf("34", 5));
        lists.get(4).add((short) 127);
        lists.get(4).add((short) -128);
        lists.get(5).add(null);
        lists.get(5).add((short) 0);
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(Short.decode("76"));
        lists.get(5).add(Short.valueOf("125"));
        lists.get(5).add(new Short("-127"));
        lists.get(5).add(null);
        helpTester(lists, new ShortList());
    }

    /**
     * Unsigned Integer List Test
     */
    @Test
    public void integerListTest() throws Exception {
        List<IntegerList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new IntegerList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(Integer.MIN_VALUE);
        lists.get(4).add(Integer.MAX_VALUE);
        lists.get(4).add(Integer.valueOf("34", 5));
        lists.get(4).add(127);
        lists.get(4).add(-128);
        lists.get(5).add(null);
        lists.get(5).add(0);
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(Integer.decode("76"));
        lists.get(5).add(Integer.valueOf("125"));
        lists.get(5).add(new Integer("-127"));
        lists.get(5).add(null);
        helpTester(lists, new IntegerList());
    }

    /**
     * Unsigned Long List Test
     */
    @Test
    public void longListTest() throws Exception {
        List<LongList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new LongList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(Long.MIN_VALUE);
        lists.get(4).add(Long.MAX_VALUE);
        lists.get(4).add(Long.valueOf("34", 5));
        lists.get(4).add((long) 127);
        lists.get(4).add((long) -128);
        lists.get(5).add(null);
        lists.get(5).add((long) 0);
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(Long.decode("76"));
        lists.get(5).add(Long.valueOf("125"));
        lists.get(5).add(new Long("-127"));
        lists.get(5).add(null);
        helpTester(lists, new LongList());
    }

    /**
     * Unsigned Float List Test
     */
    @Test
    public void floatListTest() throws Exception {
        List<FloatList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new FloatList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(Float.MIN_VALUE);
        lists.get(4).add(Float.MAX_VALUE);
        lists.get(4).add(Float.valueOf("2342342.23423"));
        lists.get(4).add(Float.NaN);
        lists.get(4).add(Float.POSITIVE_INFINITY);
        lists.get(5).add(null);
        lists.get(5).add(Float.NEGATIVE_INFINITY);
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(Float.MIN_NORMAL);
        lists.get(5).add(Float.valueOf("125"));
        lists.get(5).add(new Float("-127"));
        lists.get(5).add(null);
        helpTester(lists, new FloatList());
    }


    /**
     * Unsigned Double List Test
     */
    @Test
    public void doubleListTest() throws Exception {
        List<DoubleList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new DoubleList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(3).add(Double.MIN_VALUE);
        lists.get(4).add(Double.MAX_VALUE);
        lists.get(4).add(Double.valueOf("2342342.23423"));
        lists.get(4).add(Double.NaN);
        lists.get(4).add(Double.POSITIVE_INFINITY);
        lists.get(5).add(null);
        lists.get(5).add(Double.NEGATIVE_INFINITY);
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(Double.MIN_NORMAL);
        lists.get(5).add(Double.valueOf("125"));
        lists.get(5).add(new Double("-127"));
        lists.get(5).add(null);
        helpTester(lists, new DoubleList());
    }
}
