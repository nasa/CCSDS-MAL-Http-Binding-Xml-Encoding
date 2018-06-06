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

import nasa.mo.mal.encoder.util.builders.EntityKeyBuilder;
import nasa.mo.mal.encoder.util.builders.EntityRequestBuilder;
import nasa.mo.mal.encoder.util.builders.FileBuilder;
import nasa.mo.mal.encoder.util.builders.SubscriptionBuilder;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.*;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wphyo
 *         Created on 6/28/17.
 */
public class EncoderCompositeListTest extends AbstractEncoderTest {

    @Test
    public void pairListTest1() throws Exception {
        List<PairList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new PairList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(new Pair(null, null));

        lists.get(4).add(new Pair());
        lists.get(4).add(new Pair(null, null));
        lists.get(4).add(new Pair(new Union(true), null));
        lists.get(4).add(new Pair(null, new Union(65.3212345f)));
        lists.get(5).add(null);
        lists.get(5).add(new Pair(new Duration(7654.23456789765), new URI("http://www.google.com")));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new Pair(null, new Identifier("Test String")));
        lists.get(5).add(new Pair(new Union(65L), new FineTime(8764567654323456762L)));
        lists.get(5).add(null);
        helpTester(lists, new PairList());
    }

    @Test
    public void idBooleanPairListTest1() throws Exception {
        List<IdBooleanPairList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new IdBooleanPairList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(new IdBooleanPair(null, null));

        lists.get(4).add(new IdBooleanPair());
        lists.get(4).add(new IdBooleanPair(null, null));
        lists.get(4).add(new IdBooleanPair(new Identifier("Test String"), null));
        lists.get(4).add(new IdBooleanPair(null, true));
        lists.get(5).add(null);
        lists.get(5).add(new IdBooleanPair(new Identifier("Test String 1"), false));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new IdBooleanPair(null, true));
        lists.get(5).add(new IdBooleanPair(new Identifier("Test String 2"), false));
        lists.get(5).add(null);
        helpTester(lists, new IdBooleanPairList());
    }

    @Test
    public void namedValueListTest1() throws Exception {
        List<NamedValueList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new NamedValueList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(new NamedValue(null, null));

        lists.get(4).add(new NamedValue());
        lists.get(4).add(new NamedValue(null, null));
        lists.get(4).add(new NamedValue(new Identifier("Test String"), null));
        lists.get(4).add(new NamedValue(null, new Union("Test String")));
        lists.get(5).add(null);
        lists.get(5).add(new NamedValue(new Identifier("Test String 1"), new Time(7654567876544654563L)));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new NamedValue(null, new UInteger(8765434567654345652L)));
        lists.get(5).add(new NamedValue(new Identifier("Test String 2"), new Blob(String.valueOf("Test String").getBytes())));
        lists.get(5).add(null);
        helpTester(lists, new NamedValueList());
    }


    @Test(expected = MALException.class)
    public void subscriptionListTest1() throws Exception {
        List<SubscriptionList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new SubscriptionList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(SubscriptionBuilder.create().build());

        lists.get(4).add(SubscriptionBuilder.create().build());
        lists.get(4).add(SubscriptionBuilder.create().build());
        lists.get(4).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String")).build());
        lists.get(4).add(SubscriptionBuilder.create().entities(new EntityRequestList()).build());
        lists.get(5).add(null);
        lists.get(5).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String 1")).entities(new EntityRequestList()).build());
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(SubscriptionBuilder.create().entities(new EntityRequestList()).build());
        lists.get(5).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String 2")).entities(new EntityRequestList()).build());
        lists.get(5).add(null);
        for (SubscriptionList each : lists) {
            encoder.encodeNullableElement(each);
        }
    }


    @Test(expected = MALException.class)
    public void subscriptionListTest2() throws Exception {
        List<SubscriptionList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new SubscriptionList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String1")).build());

        lists.get(4).add(SubscriptionBuilder.create().build());
        lists.get(4).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String2")).build());
        lists.get(4).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String3")).build());
        lists.get(4).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String4")).entities(new EntityRequestList()).build());
        lists.get(5).add(null);
        lists.get(5).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String5")).entities(new EntityRequestList()).build());
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String6")).entities(new EntityRequestList()).build());
        lists.get(5).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String7")).entities(new EntityRequestList()).build());
        lists.get(5).add(null);
        for (SubscriptionList each : lists) {
            encoder.encodeNullableElement(each);
        }
    }


    @Test(expected = MALException.class)
    public void subscriptionListTest3() throws Exception {
        List<SubscriptionList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new SubscriptionList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String1")).build());

        lists.get(4).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String1")).build());
        lists.get(4).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String2")).build());
        lists.get(4).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String3")).build());
        lists.get(4).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String4")).entities(new EntityRequestList()).build());
        lists.get(5).add(null);
        lists.get(5).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String5")).entities(new EntityRequestList()).build());
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String6")).entities(new EntityRequestList()).build());
        lists.get(5).add(SubscriptionBuilder.create().subscriptionId(new Identifier("Test String7")).entities(new EntityRequestList()).build());
        lists.get(5).add(null);
        for (SubscriptionList each : lists) {
            encoder.encodeNullableElement(each);
        }
    }

    @Test
    public void subscriptionListTest4() throws Exception {
        List<SubscriptionList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new SubscriptionList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(new Subscription(new Identifier("Test String1"), new EntityRequestList()));

        lists.get(4).add(new Subscription(new Identifier("Test String1"), new EntityRequestList()));
        lists.get(4).add(new Subscription(new Identifier("Test String2"), new EntityRequestList()));
        lists.get(4).add(new Subscription(new Identifier("Test String3"), new EntityRequestList()));
        lists.get(4).add(new Subscription(new Identifier("Test String4"), new EntityRequestList()));
        lists.get(5).add(null);
        lists.get(5).add(new Subscription(new Identifier("Test String5"), new EntityRequestList()));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new Subscription(new Identifier("Test String6"), new EntityRequestList()));
        lists.get(5).add(new Subscription(new Identifier("Test String7"), new EntityRequestList()));
        lists.get(5).add(null);
        helpTester(lists, new SubscriptionList());
    }


    @Test
    public void subscriptionListTest5() throws Exception {


        EntityRequestList entityRequests = new EntityRequestList();
        entityRequests.add(null);
        entityRequests.add(new EntityRequest(null, true, false, true, false, new EntityKeyList()));
        entityRequests.add(new EntityRequest(null, true, false, true, false, getNotNullEntityKeyList()));
        entityRequests.add(new EntityRequest(new IdentifierList(), true, false, true, false, getNotNullEntityKeyList()));
        entityRequests.add(new EntityRequest(getNotNullIdentifierList(), true, false, true, false, getNotNullEntityKeyList()));

        List<SubscriptionList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new SubscriptionList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(new Subscription(new Identifier("Test String1"), entityRequests));

        lists.get(4).add(new Subscription(new Identifier("Test String1"), entityRequests));
        lists.get(4).add(new Subscription(new Identifier("Test String2"), entityRequests));
        lists.get(4).add(new Subscription(new Identifier("Test String3"), entityRequests));
        lists.get(4).add(new Subscription(new Identifier("Test String4"), entityRequests));
        lists.get(5).add(null);
        lists.get(5).add(new Subscription(new Identifier("Test String5"), entityRequests));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new Subscription(new Identifier("Test String6"), entityRequests));
        lists.get(5).add(new Subscription(new Identifier("Test String7"), entityRequests));
        lists.get(5).add(null);
        helpTester(lists, new SubscriptionList());
    }

    @Test
    public void entityKeyListTest1() throws Exception {
        List<EntityKeyList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new EntityKeyList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(new EntityKey(new Identifier("Test1"), 1L, 2L, 3L));

        lists.get(4).add(new EntityKey(null, 1L, 2L, 3L));
        lists.get(4).add(new EntityKey(new Identifier("Test1"), null, 2L, 3L));
        lists.get(4).add(new EntityKey(new Identifier("Test1"), 1L, null, 3L));
        lists.get(4).add(new EntityKey(new Identifier("Test1"), 1L, 2L, null));
        lists.get(5).add(null);
        lists.get(5).add(new EntityKey(new Identifier("Test1"), 1L, null, null));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new EntityKey(new Identifier("Test1"), null, null, null));
        lists.get(5).add(new EntityKey(null, null, null, null));
        lists.get(5).add(new EntityKey());
        lists.get(5).add(null);
        helpTester(lists, new EntityKeyList());
    }


    @Test(expected = MALException.class)
    public void entityKeyListTest2() throws Exception {
        List<EntityKeyList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new EntityKeyList());
        }
        lists.get(1).add(new EntityKey(new Identifier(null), null, null, null));
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(new EntityKey(new Identifier("Test1"), 1L, 2L, 3L));

        lists.get(4).add(new EntityKey(null, 1L, 2L, 3L));
        lists.get(4).add(new EntityKey(new Identifier("Test1"), null, 2L, 3L));
        lists.get(4).add(new EntityKey(new Identifier("Test1"), 1L, null, 3L));
        lists.get(4).add(new EntityKey(new Identifier("Test1"), 1L, 2L, null));
        lists.get(5).add(null);
        lists.get(5).add(new EntityKey(new Identifier("Test1"), 1L, null, null));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new EntityKey(new Identifier("Test1"), null, null, null));
        lists.get(5).add(new EntityKey(null, null, null, null));
        lists.get(5).add(null);
        for (EntityKeyList each : lists) {
            encoder.encodeNullableElement(each);
        }
    }

    @Test
    public void updateHeaderListTest1() throws Exception {
        List<UpdateHeaderList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new UpdateHeaderList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(new UpdateHeader(new Time(0L), new URI("http://www.google.com"), UpdateType.UPDATE, new EntityKey()));

        lists.get(4).add(new UpdateHeader(new Time(1L), new URI("http://www.google.com"), UpdateType.CREATION, new EntityKey()));
        lists.get(4).add(new UpdateHeader(new Time(2L), new URI("http://www.google.com"), UpdateType.DELETION, new EntityKey()));
        lists.get(4).add(new UpdateHeader(new Time(3L), new URI("http://www.google.com"), UpdateType.UPDATE, new EntityKey()));
        lists.get(4).add(new UpdateHeader(new Time(4L), new URI("http://www.google.com"), UpdateType.CREATION, new EntityKey()));
        lists.get(5).add(null);
        lists.get(5).add(new UpdateHeader(new Time(5L), new URI("http://www.google.com"), UpdateType.DELETION, new EntityKey()));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new UpdateHeader(new Time(6L), new URI("http://www.google.com"), UpdateType.UPDATE, new EntityKey()));
        lists.get(5).add(new UpdateHeader(new Time(7L), new URI("http://www.google.com"), UpdateType.CREATION, new EntityKey()));
        lists.get(5).add(null);
        helpTester(lists, new UpdateHeaderList());
    }


    @Test(expected = MALException.class)
    public void updateHeaderListTest2() throws Exception {
        List<UpdateHeaderList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new UpdateHeaderList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(new UpdateHeader());

        lists.get(4).add(new UpdateHeader(new Time(1L), new URI("http://www.google.com"), UpdateType.CREATION, new EntityKey()));
        lists.get(4).add(new UpdateHeader(new Time(2L), new URI("http://www.google.com"), UpdateType.DELETION, new EntityKey()));
        lists.get(4).add(new UpdateHeader(new Time(3L), new URI("http://www.google.com"), UpdateType.UPDATE, new EntityKey()));
        lists.get(4).add(new UpdateHeader(new Time(4L), new URI("http://www.google.com"), UpdateType.CREATION, new EntityKey()));
        lists.get(5).add(null);
        lists.get(5).add(new UpdateHeader(new Time(5L), new URI("http://www.google.com"), UpdateType.DELETION, new EntityKey()));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new UpdateHeader(new Time(6L), new URI("http://www.google.com"), UpdateType.UPDATE, new EntityKey()));
        lists.get(5).add(new UpdateHeader(new Time(7L), new URI("http://www.google.com"), UpdateType.CREATION, new EntityKey()));
        lists.get(5).add(null);
        for (UpdateHeaderList each : lists) {
            encoder.encodeNullableElement(each);
        }
    }

    @Test(expected = MALException.class)
    public void entityRequestListTest1() throws Exception {
        List<EntityRequestList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new EntityRequestList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(EntityRequestBuilder.create().build());

        lists.get(4).add(EntityRequestBuilder.create().build());
        lists.get(4).add(EntityRequestBuilder.create().build());
        lists.get(4).add(EntityRequestBuilder.create().build());
        lists.get(4).add(EntityRequestBuilder.create().build());
        lists.get(5).add(null);
        lists.get(5).add(EntityRequestBuilder.create().build());
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(EntityRequestBuilder.create().build());
        lists.get(5).add(EntityRequestBuilder.create().build());
        lists.get(5).add(null);
        for (EntityRequestList each : lists) {
            encoder.encodeNullableElement(each);
        }
    }


    @Test
    public void entityRequestListTest2() throws Exception {
        List<EntityRequestList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new EntityRequestList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(new EntityRequest(null, true, false, true, false, new EntityKeyList()));

        lists.get(4).add(new EntityRequest(getNotNullIdentifierList(), true, false, true, false, getNotNullEntityKeyList()));
        lists.get(4).add(new EntityRequest(new IdentifierList(), true, false, true, false, new EntityKeyList()));
        lists.get(4).add(new EntityRequest(null, true, false, true, false, getNotNullEntityKeyList()));
        lists.get(4).add(new EntityRequest(getNotNullIdentifierList(), true, false, true, false, new EntityKeyList()));
        lists.get(5).add(null);
        lists.get(5).add(new EntityRequest(new IdentifierList(), true, false, true, false, getNotNullEntityKeyList()));
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(new EntityRequest(null, true, false, true, false, new EntityKeyList()));
        lists.get(5).add(new EntityRequest(getNotNullIdentifierList(), true, false, true, false, getNotNullEntityKeyList()));
        lists.get(5).add(null);
        helpTester(lists, new EntityRequestList());
    }

    @Test(expected = MALException.class)
    public void fileListTest1() throws Exception {
        List<FileList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new FileList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(FileBuilder.create().build());

        lists.get(4).add(FileBuilder.create().build());
        lists.get(4).add(FileBuilder.create().build());
        lists.get(4).add(FileBuilder.create().build());
        lists.get(4).add(FileBuilder.create().build());
        lists.get(5).add(null);
        lists.get(5).add(FileBuilder.create().build());
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(FileBuilder.create().build());
        lists.get(5).add(FileBuilder.create().build());
        lists.get(5).add(null);
        for (FileList each : lists) {
            encoder.encodeNullableElement(each);
        }
    }

    @Test
    public void fileListTest2() throws Exception {
        NamedValueList namedValues = new NamedValueList();
        namedValues.add(null);
        namedValues.add(null);
        namedValues.add(null);
        namedValues.add(null);
        namedValues.add(null);

        namedValues.add(new NamedValue(null, null));

        namedValues.add(new NamedValue());
        namedValues.add(new NamedValue(null, null));
        namedValues.add(new NamedValue(new Identifier("Test String"), null));
        namedValues.add(new NamedValue(null, new Union("Test String")));
        namedValues.add(null);
        namedValues.add(new NamedValue(new Identifier("Test String 1"), new Time(7654567876544654563L)));
        namedValues.add(null);
        namedValues.add(null);
        namedValues.add(new NamedValue(null, new UInteger(8765434567654345652L)));
        namedValues.add(new NamedValue(new Identifier("Test String 2"), new Blob(String.valueOf("Test String").getBytes())));
        namedValues.add(null);
        List<FileList> lists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            lists.add(new FileList());
        }
        lists.get(1).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);
        lists.get(2).add(null);

        lists.get(3).add(FileBuilder.create().name(new Identifier("File 1")).build());

        lists.get(4).add(FileBuilder.create().name(new Identifier("File 2")).mimeType("Mime Type 1").build());
        lists.get(4).add(FileBuilder.create().name(new Identifier("File 3")).creationDate(new Time(7654345676546485368L)).build());
        lists.get(4).add(FileBuilder.create().name(new Identifier("File 4")).modificationDate(new Time(0L)).build());
        lists.get(4).add(FileBuilder.create().name(new Identifier("File 5")).size(new ULong(new BigInteger("3940875348956734897562398456782934562398456772834"))).build());
        lists.get(5).add(null);
        lists.get(5).add(FileBuilder.create().name(new Identifier("File 6")).content(new Blob(String.valueOf("It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).").getBytes())).build());
        lists.get(5).add(null);
        lists.get(5).add(null);
        lists.get(5).add(FileBuilder.create().name(new Identifier("File 7")).metaData(new NamedValueList()).build());
        lists.get(5).add(FileBuilder.create().name(new Identifier("File 8")).metaData(namedValues).build());
        lists.get(5).add(null);
        helpTester(lists, new FileList());
    }

    private IdentifierList getNotNullIdentifierList() {
        IdentifierList identifiers = new IdentifierList();
        identifiers.add(null);
        identifiers.add(new Identifier("Test ID 5"));
        return identifiers;
    }

    private EntityKeyList getNotNullEntityKeyList() {
        EntityKeyList entityKeys = new EntityKeyList();
        entityKeys.add(null);
        entityKeys.add(EntityKeyBuilder.create().build());
        entityKeys.add(EntityKeyBuilder.create().build());
        entityKeys.add(EntityKeyBuilder.create().secondSubKey(1L).thirdSubKey(2L).fourthSubKey(3L).build());
        entityKeys.add(EntityKeyBuilder.create().firstSubKey(new Identifier("Test ID 1")).thirdSubKey(2L).fourthSubKey(3L).build());
        entityKeys.add(EntityKeyBuilder.create().firstSubKey(new Identifier("Test ID 2")).secondSubKey(1L).fourthSubKey(3L).build());
        entityKeys.add(EntityKeyBuilder.create().firstSubKey(new Identifier("Test ID 3")).secondSubKey(1L).thirdSubKey(2L).build());
        entityKeys.add(EntityKeyBuilder.create().firstSubKey(new Identifier("Test ID 4")).secondSubKey(1L).thirdSubKey(2L).fourthSubKey(3L).build());
        return entityKeys;
    }
}
