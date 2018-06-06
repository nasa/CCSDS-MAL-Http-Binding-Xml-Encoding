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
import org.junit.Assert;
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author wphyo
 *         Created on 6/28/17.
 */
public abstract class AbstractEncoderTest {
    public static final String XML_BEGINNING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><malxml:Body xmlns:malxml=\"http://www.ccsds.org/schema/malxml/MAL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
    public static final String XML_EMPTY_DOCUMENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<malxml:Body xmlns:malxml=\"http://www.ccsds.org/schema/malxml/MAL\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>";
    protected ByteArrayOutputStream message;
    protected XmlEncoder encoder;
    protected URI uri = new URI("http://test:8080/");
    protected Blob auth = new Blob(new byte[] {1,2, 3});
    protected UShort uShort = new UShort(1);
    protected UOctet uOctet = new UOctet((short) 1);
    protected UInteger uInteger = new UInteger(1L);
    protected Time time = new Time(0L);
    protected Identifier identifier = new Identifier("Test Identifier");
    protected IdentifierList identifiers = new IdentifierList();

    public static void stringToOutputStream(OutputStream message, String encodedXml) {
        PrintWriter p = new PrintWriter(message);
        p.write(encodedXml);
        p.flush();
        p.close();
    }

    /**
     * initialize message and encoder for every test.
     */
    @Before
    public void setUp() {
        message = new ByteArrayOutputStream();
        encoder = new XmlEncoder(message);
    }

    /**
     * 1. loop and encode everything
     * 2. close encoder
     * 3. setUp decoder with the message written by encoder
     * 4. display message for visual xml
     * 5. decode & check if it receives the same element.
     *
     * Using reflection to create new instance from sample element as decoder will fill elements to that element
     *
     * @param lists List holding element lists
     * @param sample sample element list for decoder
     * @param <T> Element type
     */
    protected  <T> void helpTester(List<T> lists, Element sample) throws Exception {
        XmlDecoder decoder;
        for (T each : lists) {
            encoder.encodeElement((Element)each);
        }
        encoder.close();
        decoder = new XmlDecoder(new ByteArrayInputStream(message.toByteArray()));
        for (T each : lists) {
            T decoded = (T) decoder.decodeElement(sample.getClass().getDeclaredConstructor().newInstance());
            Assert.assertTrue(each.equals(decoded));
        }
    }
}
