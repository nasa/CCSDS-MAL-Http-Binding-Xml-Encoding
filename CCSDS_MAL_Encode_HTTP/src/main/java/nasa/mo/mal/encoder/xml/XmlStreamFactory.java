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
import org.ccsds.moims.mo.mal.encoding.MALElementInputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Blob;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Wai Phyo
 *         Created on 5/3/17.
 * Entry point to encoder & decoder.
 * Following BinaryStreamFactory from MO_TRANS package.
 */
public class XmlStreamFactory extends MALElementStreamFactory {
    /**
     * The method enables the specific implementation class to initialize the encoding module.
     * No initialization required.
     *
     * @param protocol   Name of the protocol passed through the instantiation method
     * @param properties Properties passed through the instantiation method, may be null
     * @throws IllegalArgumentException If the protocol string is null.
     * @throws MALException             If an internal error occurs
     */
    @Override
    protected void init(String protocol, Map properties) throws IllegalArgumentException, MALException {

    }

    /**
     * Creates a MALElementInputStream using a java.io.InputStream as the data source.
     *
     * @param is The data source.
     * @return The new MALElementInputStream.
     * @throws IllegalArgumentException if the data source is null.
     * @throws MALException             If a MALElementInputStream cannot be created
     */
    @Override
    public MALElementInputStream createInputStream(InputStream is) throws IllegalArgumentException, MALException {
        return new XmlElementInputStream(is);
    }

    /**
     * Creates a MALElementInputStream using a byte array as the data source.
     * TODO This might not work for XML.
     * @param bytes  Bytes to be decoded
     * @param offset Index of the first byte to decode
     * @return The new MALElementInputStream.
     * @throws IllegalArgumentException if the data source is null.
     * @throws MALException             If a MALElementInputStream cannot be created
     */
    @Override
    public MALElementInputStream createInputStream(byte[] bytes, int offset)
            throws IllegalArgumentException, MALException {
        return new XmlElementInputStream(new ByteArrayInputStream(bytes), offset);
    }

    /**
     * Creates a MALElementOutputStream using a java.io.OutputStream as the data sink.
     *
     * @param os The data sink.
     * @return The new MALElementOutputStream.
     * @throws IllegalArgumentException if the data sink is null.
     * @throws MALException             If a MALElementOutputStream cannot be created
     */
    @Override
    public MALElementOutputStream createOutputStream(OutputStream os) throws IllegalArgumentException, MALException {
        return new XmlElementOutputStream(os);
    }

    /**
     * The method encodes an element array and returns the encoding result as a byte array.
     *
     * NOTE: Existing methods for Binary and String is FLUSHING the stream after the loop.
     * Since flushing is not supported here, it will close and write everything to stream.
     *
     * TODO There might be issues if there are encode method is called repeatedly.
     *
     * @param elements Elements to encode
     * @param ctx      MALEncodingContext to be used in order to encode the elements
     * @return The encoded elements as a byte array.
     * @throws IllegalArgumentException if the arguments are null.
     * @throws MALException             If an encoding error occurs
     */
    @Override
    public Blob encode(Object[] elements, MALEncodingContext ctx) throws IllegalArgumentException, MALException {
        if (elements == null) {
            throw new MALException("Null Element Array.");
        }
        if (ctx == null) {
            throw new MALException("Null MAL Encoding Context.");
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        final MALElementOutputStream os = createOutputStream(baos);
        for (int i = 0; i < elements.length; i++) {
            os.writeElement(elements[i], ctx);
        }
        os.close();

        return new Blob(baos.toByteArray());
    }
}
