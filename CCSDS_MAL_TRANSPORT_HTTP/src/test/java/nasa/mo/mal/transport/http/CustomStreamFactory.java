/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.http;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.encoding.MALElementInputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author wphyo
 *         Created on 7/28/17.
 */
public class CustomStreamFactory extends MALElementStreamFactory {
    public CustomStreamFactory() {
        super();
    }
    @Override
    protected void init(String protocol, Map properties) throws IllegalArgumentException, MALException {

    }

    @Override
    public MALElementInputStream createInputStream(InputStream is) throws IllegalArgumentException, MALException {
        return null;
    }

    @Override
    public MALElementInputStream createInputStream(byte[] bytes, int offset) throws IllegalArgumentException, MALException {
        return null;
    }

    @Override
    public MALElementOutputStream createOutputStream(OutputStream os) throws IllegalArgumentException, MALException {

        return new MALElementOutputStream() {
            @Override
            public void writeElement(Object element, MALEncodingContext ctx) throws IllegalArgumentException, MALException {

            }

            @Override
            public void flush() throws MALException {

            }

            @Override
            public void close() throws MALException {

            }
        };
    }

    @Override
    public Blob encode(Object[] elements, MALEncodingContext ctx) throws IllegalArgumentException, MALException {
        return null;
    }
}
