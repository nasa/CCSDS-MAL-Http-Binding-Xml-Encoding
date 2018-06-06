/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.gen;

import nasa.mo.mal.encoder.xml.XmlStreamFactory;
import esa.mo.mal.transport.gen.GENMessage;
import esa.mo.mal.transport.gen.GENMessageHeader;
import esa.mo.mal.transport.gen.body.GENErrorBody;
import esa.mo.mal.transport.gen.body.GENMessageBody;
import esa.mo.mal.transport.gen.body.GENPublishBody;
import nasa.mo.mal.transport.http.MessageTestBase;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.structures.factory.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @author wphyo
 *         Created on 7/19/17.
 */
public class GENMessageBodyTest extends MessageTestBase {

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        elements.clear();
        shortForms.clear();
    }

    @Test
    public void genErrorBodyTest01() throws Exception {
        GENMessageHeader header = getMessageHeader(true, InteractionType.PUBSUB, uOctet, uShort, serviceArea);
        elements.add(MALHelper.DELIVERY_DELAYED_ERROR_NUMBER);
        elements.add(new Union("Error Test 01"));

        shortForms.add(Attribute.USHORT_SHORT_FORM);
        shortForms.add(Attribute.STRING_SHORT_FORM);

        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.UINTEGER_SHORT_FORM, new UIntegerFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.STRING_SHORT_FORM, new StringFactory());

        XmlStreamFactory factory = getStreamFactory();
        GENMessage msg = new GENMessage(true, header, null,
                generateSubmitOp(), factory, elements.toArray());
        Assert.assertTrue(msg.getBody() instanceof GENErrorBody);

        GENMessage received = new GENMessage(false, true, new GENMessageHeader(), null, compareAndShow(msg, factory).toByteArray(), factory);
        Assert.assertTrue(received.getBody() instanceof GENErrorBody);
        for (int i = 0; i < elements.size(); i++) {
            Assert.assertTrue(elements.get(i).equals(received.getBody().getBodyElement(i, null)));
        }

    }

    @Test
    public void genSubmitBodyTest01() throws Exception {
        GENMessageHeader header = getMessageHeader(false, InteractionType.SUBMIT, uOctet, uShort, serviceArea);
        elements.add(new FineTime(987654321123456789L));
        elements.add(SessionType.REPLAY);
        elements.add(new EntityKey(identifier, 1L, 2L, 3L));

        shortForms.add(Attribute.FINETIME_SHORT_FORM);
        shortForms.add(SessionType.SHORT_FORM);
        shortForms.add(EntityKey.SHORT_FORM);

        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Attribute.FINETIME_SHORT_FORM, new FineTimeFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(SessionType.SHORT_FORM, new SessionTypeFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(EntityKey.SHORT_FORM, new EntityKeyFactory());

        XmlStreamFactory factory = getStreamFactory();
        GENMessage msg = new GENMessage(false, header, null, generateSubmitOp(),
                factory, elements.toArray());

        GENMessage received = new GENMessage(false, true, new GENMessageHeader(), null, compareAndShow(msg, factory).toByteArray(), factory);
        for (int i = 0; i < elements.size(); i++) {
            Assert.assertTrue(elements.get(i).equals(received.getBody().getBodyElement(i, null)));
        }
    }


    @Test
    public void genSubmitBodyTest02() throws Exception {
        GENMessageHeader header = getMessageHeader(false, InteractionType.SUBMIT, uOctet, uShort, serviceArea);

        XmlStreamFactory factory = getStreamFactory();
        GENMessage msg = new GENMessage(false, header, null, generateSubmitOp(),
                factory, elements.toArray());

        GENMessage received = new GENMessage(false, true, new GENMessageHeader(), null, compareAndShow(msg, factory).toByteArray(), factory);
        for (int i = 0; i < elements.size(); i++) {
            Assert.assertTrue(elements.get(i).equals(received.getBody().getBodyElement(i, null)));
        }
    }

    @Test
    public void getRequestBodyTest01() throws Exception {
        GENMessageHeader header = getMessageHeader(false, InteractionType.REQUEST, uOctet, uShort, serviceArea);

        UpdateHeader updateHeader = new UpdateHeader();
        updateHeader.setKey(new EntityKey());
        updateHeader.setTimestamp(new Time(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()));
        updateHeader.setSourceURI(new URI("http://127.0.0.1:8081/"));
        updateHeader.setUpdateType(UpdateType.CREATION);

        Identifier simpleIdentifier = new Identifier("Testing-Identifier");
        EntityKeyList entityKeys = new EntityKeyList();
        entityKeys.add(new EntityKey(simpleIdentifier, 1L, 2L, 3L));
        entityKeys.add(new EntityKey(simpleIdentifier, 2L, 7L, 6L));
        entityKeys.add(new EntityKey(simpleIdentifier, 3L, 8L, 5L));
        entityKeys.add(null);
        entityKeys.add(new EntityKey(simpleIdentifier, 4L, 9L, 4L));
        entityKeys.add(new EntityKey(simpleIdentifier, 5L, 8L, 3L));
        entityKeys.add(new EntityKey(simpleIdentifier, 6L, 7L, 2L));
        EntityRequest entityRequest = new EntityRequest(identifiers, true, true, true, true, entityKeys);

        QoSLevelList qoSLevels = new QoSLevelList();
        qoSLevels.add(QoSLevel.ASSURED);
        qoSLevels.add(QoSLevel.QUEUED);
        qoSLevels.add(QoSLevel.TIMELY);

        IdBooleanPair idBooleanPair = new IdBooleanPair(new Identifier("TestString"), true);

        IdBooleanPairList idBooleanPairs = new IdBooleanPairList();
        idBooleanPairs.add(idBooleanPair);
        idBooleanPairs.add(null);
        idBooleanPairs.add(idBooleanPair);

        EntityRequestList entityRequests = new EntityRequestList();
        entityRequests.add(entityRequest);
        entityRequests.add(null);
        entityRequests.add(entityRequest);

        Subscription subscription = new Subscription(new Identifier("TestString"), entityRequests);

        NamedValue namedValue = new NamedValue(new Identifier("TestString"),
                new FineTime(TimeUnit.MILLISECONDS.toNanos(Calendar.getInstance().getTimeInMillis()) + 765432189));

        File file = new File(new Identifier("TestString"), "UTF-8",
                new Time(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()),
                new Time(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()), new ULong(new BigInteger("7654")), null, null);

        SubscriptionList subscriptions = new SubscriptionList();
        subscriptions.add(null);
        subscriptions.add(subscription);
        subscriptions.add(null);
        subscriptions.add(subscription);
        subscriptions.add(null);

        FileList files = new FileList();
        files.add(file);
        files.add(null);
        files.add(file);
        files.add(null);
        files.add(file);

        NamedValueList namedValues = new NamedValueList();
        namedValues.add(namedValue);
        namedValues.add(null);
        namedValues.add(new NamedValue(null, null));

        UpdateHeaderList updateHeaders = new UpdateHeaderList();
        updateHeaders.add(updateHeader);
        updateHeaders.add(null);

        PairList pairs = new PairList();
        pairs.add(null);
        pairs.add(null);
        pairs.add(null);
        pairs.add(new Pair(new Union(23), new Union(Long.valueOf(234))));

        elements.add(new UInteger(123));
        elements.add(new Duration(1234.5678));
        elements.add(new BlobList());
        elements.add(updateHeader);
        elements.add(new Pair());
        elements.add(entityRequest);
        elements.add(new UOctet((short)123));
        elements.add(new UShort(12));
        elements.add(new ULong(new BigInteger("23424")));
        elements.add(simpleIdentifier);
        elements.add(new Blob(new byte[] {87, 21, 23, 45, 111, 123, 22}));
        elements.add(new URI("http://127.0.0.1:8081/"));
        elements.add(new Duration(87654.23456));
        elements.add(new Time(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()));
        elements.add(new FineTime(TimeUnit.MILLISECONDS.toNanos(Calendar.getInstance().getTimeInMillis()) + 765432189));
        elements.add(InteractionType.INVOKE);
        elements.add(QoSLevel.QUEUED);
        elements.add(UpdateType.CREATION);
        elements.add(SessionType.LIVE);
        elements.add(qoSLevels);
        elements.add(idBooleanPair);
        elements.add(idBooleanPairs);
        elements.add(subscription);
        elements.add(namedValue);
        elements.add(file);
        elements.add(subscriptions);
        elements.add(files);
        elements.add(namedValues);
        elements.add(updateHeaders);
        elements.add(pairs);

        shortForms.add(UInteger.UINTEGER_SHORT_FORM);
        shortForms.add(UInteger.DURATION_SHORT_FORM);
        shortForms.add(BlobList.SHORT_FORM);
        shortForms.add(UpdateHeader.SHORT_FORM);
        shortForms.add(Pair.SHORT_FORM);
        shortForms.add(EntityRequest.SHORT_FORM);
        shortForms.add(UOctet.UOCTET_SHORT_FORM);
        shortForms.add(UShort.USHORT_SHORT_FORM);
        shortForms.add(ULong.ULONG_SHORT_FORM);
        shortForms.add(Identifier.IDENTIFIER_SHORT_FORM);
        shortForms.add(Blob.BLOB_SHORT_FORM);
        shortForms.add(URI.URI_SHORT_FORM);
        shortForms.add(Duration.DURATION_SHORT_FORM);
        shortForms.add(Time.TIME_SHORT_FORM);
        shortForms.add(FineTime.FINETIME_SHORT_FORM);
        shortForms.add(InteractionType.SHORT_FORM);
        shortForms.add(QoSLevel.SHORT_FORM);
        shortForms.add(UpdateType.SHORT_FORM);
        shortForms.add(SessionType.SHORT_FORM);
        shortForms.add(QoSLevelList.SHORT_FORM);
        shortForms.add(IdBooleanPair.SHORT_FORM);
        shortForms.add(IdBooleanPairList.SHORT_FORM);
        shortForms.add(Subscription.SHORT_FORM);
        shortForms.add(NamedValue.SHORT_FORM);
        shortForms.add(File.SHORT_FORM);
        shortForms.add(SubscriptionList.SHORT_FORM);
        shortForms.add(FileList.SHORT_FORM);
        shortForms.add(NamedValueList.SHORT_FORM);
        shortForms.add(UpdateHeaderList.SHORT_FORM);
        shortForms.add(PairList.SHORT_FORM);

        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UInteger.UINTEGER_SHORT_FORM, new UIntegerFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UInteger.DURATION_SHORT_FORM, new DurationFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(BlobList.SHORT_FORM, new BlobListFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UpdateHeader.SHORT_FORM, new UpdateHeaderFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Pair.SHORT_FORM, new PairFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(EntityRequest.SHORT_FORM, new EntityRequestFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UOctet.UOCTET_SHORT_FORM, new UOctetFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UShort.USHORT_SHORT_FORM, new UShortFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(ULong.ULONG_SHORT_FORM, new ULongFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Identifier.IDENTIFIER_SHORT_FORM, new IdentifierFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Blob.BLOB_SHORT_FORM, new BlobFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(URI.URI_SHORT_FORM, new URIFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Duration.DURATION_SHORT_FORM, new DurationFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Time.TIME_SHORT_FORM, new TimeFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(FineTime.FINETIME_SHORT_FORM, new FineTimeFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(InteractionType.SHORT_FORM, new InteractionTypeFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(QoSLevel.SHORT_FORM, new QoSLevelFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UpdateType.SHORT_FORM, new UpdateTypeFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(SessionType.SHORT_FORM, new SessionTypeFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(QoSLevelList.SHORT_FORM, new QoSLevelListFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(IdBooleanPair.SHORT_FORM, new IdBooleanPairFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(IdBooleanPairList.SHORT_FORM, new IdBooleanPairListFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Subscription.SHORT_FORM, new SubscriptionFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(NamedValue.SHORT_FORM, new NamedValueFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(File.SHORT_FORM, new FileFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(SubscriptionList.SHORT_FORM, new SubscriptionListFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(FileList.SHORT_FORM, new FileListFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(NamedValueList.SHORT_FORM, new NamedValueListFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UpdateHeaderList.SHORT_FORM, new UpdateHeaderListFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(PairList.SHORT_FORM, new PairListFactory());

        XmlStreamFactory factory = getStreamFactory();
        GENMessage msg = new GENMessage(false, header, null, generateRequestOp(),
                factory, elements.toArray());

        GENMessage received = new GENMessage(false, true, new GENMessageHeader(), null, compareAndShow(msg, factory).toByteArray(), factory);
        for (int i = 0; i < elements.size(); i++) {
            Assert.assertTrue(elements.get(i).equals(received.getBody().getBodyElement(i, null)));
        }
    }

    @Test
    public void genPublishBodyTest01() throws Exception {
        UOctet interactionStage = new UOctet((short) 5);
        GENMessageHeader header = getMessageHeader(false, InteractionType.PUBSUB, interactionStage, uShort, serviceArea);

        elements.add(new UpdateHeaderList());
        elements.add(new UpdateHeaderList());
        elements.add(new Union(true));

        shortForms.add(UpdateHeaderList.SHORT_FORM);
        shortForms.add(null);

        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UpdateHeaderList.SHORT_FORM, new UpdateHeaderListFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Union.BOOLEAN_SHORT_FORM, new BooleanFactory());

        XmlStreamFactory factory = getStreamFactory();
        GENMessage msg = new GENMessage(false, header, null, generatePubSubOp(),
                factory, elements.toArray());

        GENMessage received = new GENMessage(false, true, new GENMessageHeader(), null, compareAndShow(msg, factory).toByteArray(), factory);

        Assert.assertTrue(msg.getBody() instanceof GENPublishBody);
        Assert.assertTrue(received.getBody() instanceof GENPublishBody);

        Assert.assertTrue(elements.get(0).equals(((GENPublishBody) received.getBody()).getUpdateHeaderList()));
        Assert.assertTrue(elements.get(1).equals(((GENPublishBody) received.getBody()).getUpdateList(0, new UpdateHeaderList())));
        Assert.assertTrue(elements.get(2).equals(received.getBody().getBodyElement(2, new Union(false))));
        shortForms.remove(null);
    }

    @Test
    public void genPublishBodyTest02() throws Exception {
        UOctet interactionStage = new UOctet((short) 5);
        GENMessageHeader header = getMessageHeader(false, InteractionType.PUBSUB, interactionStage, uShort, serviceArea);

        elements.add(new UpdateHeaderList());
        elements.add(new UpdateHeaderList());
        elements.add(new Union(true));

        shortForms.add(UpdateHeaderList.SHORT_FORM);
        shortForms.add(Attribute.BOOLEAN_SHORT_FORM);

        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UpdateHeaderList.SHORT_FORM, new UpdateHeaderListFactory());
        MALContextFactory.getElementFactoryRegistry().registerElementFactory(Union.BOOLEAN_SHORT_FORM, new BooleanFactory());

        XmlStreamFactory factory = getStreamFactory();
        GENMessage msg = new GENMessage(false, header, null, generatePubSubOp(),
                factory, elements.toArray());

        GENMessage received = new GENMessage(false, true, new GENMessageHeader(), null, compareAndShow(msg, factory).toByteArray(), factory);

        Assert.assertTrue(msg.getBody() instanceof GENPublishBody);
        Assert.assertTrue(received.getBody() instanceof GENPublishBody);
        Assert.assertTrue(elements.get(0).equals(((GENPublishBody) received.getBody()).getUpdateHeaderList()));
        Assert.assertTrue(elements.get(1).equals(((GENPublishBody) received.getBody()).getUpdateList(0, new UpdateHeaderList())));
        Assert.assertTrue(elements.get(2).equals(received.getBody().getBodyElement(2, new Union(false))));
    }

    @Test
    public void genPublishBodyTest03() throws Exception {
        UOctet interactionStage = new UOctet((short) 2);
        GENMessageHeader header = getMessageHeader(false, InteractionType.PUBSUB, interactionStage, uShort, serviceArea);

        MALContextFactory.getElementFactoryRegistry().registerElementFactory(UpdateHeaderList.SHORT_FORM, new UpdateHeaderListFactory());

        XmlStreamFactory factory = getStreamFactory();
        GENMessage msg = new GENMessage(false, header, null, generatePubSubOp(),
                factory, elements.toArray());

        GENMessage received = new GENMessage(false, true, new GENMessageHeader(), null, compareAndShow(msg, factory).toByteArray(), factory);
        Assert.assertTrue(msg.getBody() instanceof GENMessageBody);
        Assert.assertTrue(received.getBody() instanceof GENMessageBody);
        Assert.assertEquals(0, received.getBody().getElementCount());
    }

    private ByteArrayOutputStream compareAndShow(GENMessage msg, XmlStreamFactory factory) throws Exception {
        ByteArrayOutputStream stream = getOutputStream();
        msg.encodeMessage(factory, factory.createOutputStream(stream), stream, true);
        showEncodedMessage(stream);

        ByteArrayOutputStream stream2 = getOutputStream();
        msg.encodeMessage(factory, factory.createOutputStream(stream2), stream2, true);

        Assert.assertTrue(Arrays.equals(stream.toByteArray(), stream2.toByteArray()));
        return stream;
    }


}
