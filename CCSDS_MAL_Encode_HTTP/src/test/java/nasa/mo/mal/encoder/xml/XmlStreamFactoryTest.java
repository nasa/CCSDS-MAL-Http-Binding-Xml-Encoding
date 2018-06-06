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
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;

/**
 * @author wphyo
 *         Created on 7/5/17.
 * Test cases for XmlStreamFactory
 * Only Encode method needs to be tested as others are simply creating objects.
 */
public class XmlStreamFactoryTest {
    @Mock
    private MALMessageHeader header;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private XmlStreamFactory factory;
    private MALEncodingContext context;
    @Before
    public void init() {
        factory = new XmlStreamFactory();
    }

    @Test
    public void test01() throws Exception {
        context = new MALEncodingContext(header, null, 0, null, null);
        Blob result = factory.encode(new Object[0], context);
        Assert.assertTrue(result != null);
        Assert.assertTrue(Arrays.equals(result.getValue(), String.valueOf(AbstractEncoderTest.XML_EMPTY_DOCUMENT).getBytes()));
    }


    @Test(expected = MALException.class)
    public void test02() throws MALException {
        factory.encode(new Object[0], null);
    }

    @Test(expected = MALException.class)
    public void test03() throws MALException {
        context = new MALEncodingContext(header, null, 0, null, null);
        factory.encode(null, context);
    }

    @Test
    public void test04() throws MALException, IOException, ClassNotFoundException {
        doReturn(true).when(header).getIsErrorMessage();
        context = new MALEncodingContext(header, null, 0, null, null);
        List<Object> objects = new ArrayList<>();
        objects.add(MALHelper.AUTHENTICATION_FAIL_ERROR_NUMBER);
        Blob result = factory.encode(objects.toArray(), context);
        Assert.assertTrue(result != null);

        Assert.assertTrue(Arrays.equals(result.getValue(),
                String.valueOf(AbstractEncoderTest.XML_BEGINNING +
                        "<UInteger><UInteger>" + MALHelper._AUTHENTICATION_FAIL_ERROR_NUMBER + "</UInteger></UInteger>"
                        + "</malxml:Body>").getBytes()));
    }

    @Test
    public void createInputStreamTest01() throws Exception {
        thrown.expect(NullPointerException.class);
        factory.createInputStream(new ByteArrayInputStream("Invalid String".getBytes()));
    }

    @Test
    public void createInputStreamTest02() throws Exception {
        thrown.expect(NullPointerException.class);
        factory.createInputStream("Invalid String".getBytes(), 2);
    }
}
