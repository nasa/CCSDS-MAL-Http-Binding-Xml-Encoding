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

import com.sun.istack.internal.NotNull;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UOctet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Wai Phyo
 *         Created on 5/5/17.
 */
public class XmlElementOutputStream implements MALElementOutputStream {
    /**
     * Stream field where encoded message will be written.
     */
    protected final OutputStream dos;

    protected XmlEncoder encoder = null;

    /**
     * Logger to log info
     */
    public static final Logger LOGGER = Logger.getLogger(XmlElementOutputStream.class.getName());
    /**
     * Constructor.
     *
     * @param os Output stream to write to.
     */
    protected XmlElementOutputStream(final OutputStream os) {
        this.dos = os;
        this.encoder = new XmlEncoder(dos);
    }

    /**
     * The method encodes an Element.
     * 1. Initialize Encoder if necessary.
     * 2. Element can be HEADER, or BODY.
     *
     * NOTE: added validation for Encoding Context.
     *
     * @param element Element to encode, may be null.
     * @param ctx     MALEncodingContext to be used in order to encode an Element
     * @throws IllegalArgumentException If the parameter ‘ctx’ is NULL
     * @throws MALException             If the MALElementOutputStream is closed
     */
    @Override
    public void writeElement(Object element, @NotNull MALEncodingContext ctx)
            throws IllegalArgumentException, MALException {
        if (ctx == null) {
            throw new MALException("Null MALEncodingContext");
        }
        if (encoder == null) {
            this.encoder = new XmlEncoder(dos);
        }
        if (element == ctx.getHeader()) {
            encoder.encodeElement((Element) element);
            //encoder.encodeHeader((MALMessageHeader) element);
        } else {
            encodeBody(element, ctx);
        }
    }

    /**
     * Flushes the stream.
     * NOTE: Not Supported for XML implementation
     * NOTE-2: removed exception & added log. since calling this doesn't need to crash the system.
     *          Also to be compatible with GEN-Messages
     * @throws MALException If the MALElementOutputStream is closed
     */
    @Override
    public void flush() throws MALException {
        LOGGER.log(Level.FINE, "Unsupported method is called", this.getClass().getName());
    }

    /**
     * Close the encoder ( which has logic to write to stream)
     * Closes the stream.
     *
     * @throws MALException If an internal error occurs
     */
    @Override
    public void close() throws MALException {
        try {
            if (encoder != null) {
                encoder.close();
            }
            if (dos != null) {
                dos.close();
            }
        } catch (IOException ex) {
            throw new MALException(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * Body encoder.
     * Body can be NULL, ERROR Message, PUB-SUB, or other
     *
     * @param element MAL Body element
     * @param ctx The encoding context to use
     * @throws MALException null or invalid type exceptions
     */
    private void encodeBody(Object element, MALEncodingContext ctx) throws MALException {
        if (element == null) {
            encoder.encodeNullableElement(null);
        } else {
            if (ctx.getHeader() == null) {
                throw new MALException("null MAL Message Header");
            }
            if (ctx.getHeader().getIsErrorMessage()) {
                encodeErrorMessage(element, ctx);
            } else if (ctx.getHeader().getInteractionType().getOrdinal() == InteractionType._PUBSUB_INDEX) {
                encodePubSubMessage(element, ctx);
            } else {
                encodeGeneralMessage(element, ctx);
            }
        }
    }

    /**
     * Encoding Other type of messages
     * Following same logic of GENElementOutputStream
     * TODO understand logic & MALEncodingContext
     * @param element MAL Body Element
     * @param ctx The encoding context to use
     * @throws MALException null or invalid type exception
     */
    private void encodeGeneralMessage(Object element, MALEncodingContext ctx) throws MALException {
        if (element instanceof Element) {
            // encode the short form if it is not fixed in the operation
            final Element e = (Element) element;

            UOctet stage = ctx.getHeader().getInteractionStage();
            Object sf = ctx.getOperation().getOperationStage(stage).getElementShortForms()[ctx.getBodyElementIndex()];

            encodeSubElement(e, sf, ctx);
        } else {
            throw new MALException("Encoding element is not instance of MAL Element .");
        }
    }

    /**
     * Encoding Pub-Sub Message.
     * Following same logic of GENElementOutputStream
     * TODO understand logic & MALEncodingContext
     * @param element MAL Body Element
     * @param ctx The encoding context to use
     * @throws MALException null or invalid type exception
     */
    private void encodePubSubMessage(Object element, MALEncodingContext ctx) throws MALException {
        switch (ctx.getHeader().getInteractionStage().getValue()) {
            case MALPubSubOperation._REGISTER_STAGE:
            case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
            case MALPubSubOperation._DEREGISTER_STAGE:
                encoder.encodeElement((Element) element);
                return;
            case MALPubSubOperation._PUBLISH_STAGE:
                if (pubSubValidation(0, ctx)) {
                    encodeSubElement((Element) element, null, null);
                } else {
                    encoder.encodeElement((Element) element);
                }
                return;
            case MALPubSubOperation._NOTIFY_STAGE:
                if (pubSubValidation(1, ctx)) {
                    encodeSubElement((Element) element, null, null);
                } else {
                    encoder.encodeElement((Element) element);
                }
                return;
            default:
                encodeSubElement((Element) element, null, null);
        }
    }

    /**
     * Validation for valid Pub-Sub element
     *
     * @param index index of Body Element
     * @param ctx The encoding context to use
     * @return flag indicating validity
     */
    private boolean pubSubValidation(final int index, MALEncodingContext ctx) {
        return (ctx.getBodyElementIndex() > index) &&
                (ctx.getOperation().getOperationStage(ctx.getHeader().getInteractionStage())
                        .getElementShortForms()[ctx.getBodyElementIndex()] == null);
    }

    /**
     * Encoding Error Message.
     * Error Message starts with an error number which is U_Integer.
     * There may or may not be other elements depends on error type.
     *
     * @param element MAL Body Element
     * @param ctx The encoding context to use
     * @throws MALException null or invalid type exception
     */
    private void encodeErrorMessage(Object element, MALEncodingContext ctx) throws MALException {
        if (ctx.getBodyElementIndex() == 0) {
            encoder.encodeElement((Element) element);
        } else {
            encodeSubElement((Element) element, null, null);
        }
    }

    /**
     * Encoding single Body Element
     *
     * Following same logic from GENElementOutputStream
     * TODO understand logic & MALEncodingContext
     * @param element MAL Body Element
     * @param shortForm Interaction Stage in U_Octet
     * @param encodingContext The encoding context to use
     * @throws MALException null or invalid type exception
     */
    private void encodeSubElement(final Element element,
                                  final Object shortForm,
                                  final MALEncodingContext encodingContext) throws MALException {
        if (shortForm == null) {
            // dirty check to see if we are trying to decode an abstract Attribute (and not a list of them either)
            Object[] finalEleShortForms = null;
            if (null != encodingContext) {
                finalEleShortForms = encodingContext.getOperation().getOperationStage(encodingContext.getHeader().getInteractionStage())
                        .getLastElementShortForms();
            }
            long shortFormType;
            if ((null != finalEleShortForms) && (finalEleShortForms.length == Attribute._URI_TYPE_SHORT_FORM) &&
                    ((((Long) finalEleShortForms[0]) & 0x800000L) == 0)) {
                shortFormType = (long) element.getTypeShortForm().byteValue();
            } else {
                shortFormType = element.getShortForm();
            }

            encoder.encodeElement(element, shortFormType);
        } else {
            encoder.encodeNullableElement(element);
        }
    }
}
