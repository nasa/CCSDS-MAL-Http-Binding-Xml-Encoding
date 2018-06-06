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
import esa.mo.mal.encoder.gen.GENElementInputStream;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALElementFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.*;

import java.io.InputStream;

/**
 * @author Wai Phyo
 *         Created on 5/3/17.
 *         Decoder Entry Point.
 * NOTE:    This class can implements the MALElementInputStream
 *          But this is extending GENElementInputStream for GENMessageBody to work properly.
 *          This is not a good practice and GENMessageBody should accept all MALElementInputStreams.
 *
 */
public class XmlElementInputStream extends GENElementInputStream {

    private XmlDecoder xmlDecoder;

    /**
     * Constructor with an Encoded Message as a byte stream
     *
     * @param inputStream Encoded Message Stream
     */
    protected XmlElementInputStream(InputStream inputStream) {
        super(null);
        xmlDecoder = new XmlDecoder(inputStream);
    }

    /**
     * Constructor with an Encoded Message as a byte stream
     * It has an offset which means it needs to skip that number of body elements
     *
     * @param inputStream Encoded Message Stream
     * @param offset number of body elements to skip before decoding.
     */
    protected XmlElementInputStream(InputStream inputStream, int offset) {
        super(null);
        xmlDecoder = new XmlDecoder(inputStream);
        init(offset);
    }

    /**
     * private method SOLELY for constructors.
     * To skip the number of elements
     *
     * @param offset number of body elements to skip before decoding.
     */
    private void init(int offset) {
        for (int i = 0; i < offset; i++) {
            xmlDecoder.getNextNode();
        }
    }

    /**
     * The method decodes an Element.
     * decode header or body
     *
     * @param element Element to decode, may be null.
     * @param ctx     MALEncodingContext to be used in order to decode an Element
     * @return The decoded element.
     * @throws IllegalArgumentException If the parameter ‘ctx’ is NULL
     * @throws MALException             If the MALElementInputStream is closed
     */
    @Override
    public Object readElement(Object element, @NotNull MALEncodingContext ctx)
            throws IllegalArgumentException, MALException {
        if (element instanceof Blob && ctx == null) {
            return xmlDecoder.decodeElement((Element) element);
        }
        if (ctx == null) {
            throw new MALException("Invalid operation");
        }
        if (element == ctx.getHeader()) {
            return xmlDecoder.decodeElement((Element) element);
            //return xmlDecoder.fillHeader((MALMessageHeader) element);
        } else {
            return readBodyElement(element, ctx);
        }
    }

    /**
     * reading body element
     * Body can be error message, PubSub or others
     *
     * @param element Element to decode, may be null.
     * @param ctx     MALEncodingContext to be used in order to decode an Element
     * @return The decoded element.
     * @throws IllegalArgumentException If the parameter ‘ctx’ is NULL
     * @throws MALException             If the MALElementInputStream is closed
     */
    private Object readBodyElement(Object element, MALEncodingContext ctx)
            throws IllegalArgumentException, MALException {
        if (ctx.getHeader() == null) {
            throw new MALException("Null MAL Message Header in Encoding Context");
        }
        if (ctx.getHeader().getIsErrorMessage()) {
            return readErrorMsgElement(ctx);
        } else if (InteractionType._PUBSUB_INDEX == ctx.getHeader().getInteractionType().getOrdinal()) {
            return readPubSubElement(ctx);
        } else {
            return readOtherBodyElement(element, ctx);
        }
    }

    /**
     * Decoding error message.
     * error message starts with error number.
     * 1st element is error number in UInteger type
     * If there are others, type id should be in xml doc.
     *
     * @param ctx     MALEncodingContext to be used in order to decode an Element
     * @return The decoded element.
     * @throws IllegalArgumentException If the parameter ‘ctx’ is NULL
     * @throws MALException             If the MALElementInputStream is closed
     */
    private Object readErrorMsgElement(MALEncodingContext ctx)
            throws IllegalArgumentException, MALException {
        if (ctx.getBodyElementIndex() == 0) {
            return xmlDecoder.decodeUInteger();
        } else {
            return decodeSubElement(xmlDecoder.getShortForm(), ctx);
        }
    }

    /**
     * decoding PubSub message
     * Note1: not sure we need this since Transport doesn't support PubSub.
     * Node2: We do since Transport still supports transfer of PubSub messages.
     *
     * @param ctx     MALEncodingContext to be used in order to decode an Element
     * @return The decoded element.
     * @throws IllegalArgumentException If the parameter ‘ctx’ is NULL
     * @throws MALException             If the MALElementInputStream is closed
     */
    private Object readPubSubElement(MALEncodingContext ctx)
            throws IllegalArgumentException, MALException {
        switch (ctx.getHeader().getInteractionStage().getValue()) {
            case MALPubSubOperation._REGISTER_STAGE:
                return xmlDecoder.decodeElement(new Subscription());
            case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
                return xmlDecoder.decodeElement(new EntityKeyList());
            case MALPubSubOperation._DEREGISTER_STAGE:
                return xmlDecoder.decodeElement(new IdentifierList());
            case MALPubSubOperation._PUBLISH_STAGE: {
                int idx = ctx.getBodyElementIndex();
                if (0 == idx) {
                    return xmlDecoder.decodeElement(new UpdateHeaderList());
                } else {
                    return decodeSubElement(getShortForm(ctx), ctx);
                }
            }
            case MALPubSubOperation._NOTIFY_STAGE: {
                int idx = ctx.getBodyElementIndex();
                if (0 == idx) {
                    return xmlDecoder.decodeIdentifier();
                } else if (1 == idx) {
                    return xmlDecoder.decodeElement(new UpdateHeaderList());
                } else {
                    return decodeSubElement(getShortForm(ctx), ctx);
                }
            }
            default:
                return decodeSubElement(xmlDecoder.getShortForm(), ctx);
        }
    }

    /**
     * Helper method finding the short form
     *
     * Steps:
     * 1. try to find it in the short form array from Encoding Context.
     * 2. if not found, it must be hard coded in XML doc. get it from there.
     *
     * @param encodingContext   MALEncodingContext to be used in order to decode an Element
     * @return Short form ID (long data type) of current decoding MAL element
     * @throws MALException     any exception from decoder.
     */
    private long getShortForm(MALEncodingContext encodingContext) throws MALException {
        Object sf = encodingContext.getOperation().getOperationStage(encodingContext.getHeader().getInteractionStage())
                .getElementShortForms()[encodingContext.getBodyElementIndex()];
        if (null == sf) {
            return xmlDecoder.getShortForm();
        } else {
            return (long) sf;
        }
    }

    /**
     * Decoding a body element.
     * 1. find the short form.
     * 2. decode an element by passing shortform as a reference to which data type
     * @param element Element to decode, may be null.
     * @param ctx     MALEncodingContext to be used in order to decode an Element
     * @return The decoded element.
     * @throws IllegalArgumentException If the parameter ‘ctx’ is NULL
     * @throws MALException             If the MALElementInputStream is closed
     */
    private Object readOtherBodyElement(Object element, MALEncodingContext ctx)
            throws IllegalArgumentException, MALException {
        if (null == element) {
            Long shortForm;

            // dirty check to see if we are trying to decode an abstract Attribute (and not a list of them either)
            Object[] finalEleShortForms = ctx.getOperation().getOperationStage(ctx.getHeader().getInteractionStage()).getLastElementShortForms();

            if ((null != finalEleShortForms) && (Attribute._URI_TYPE_SHORT_FORM == finalEleShortForms.length) && ((((Long) finalEleShortForms[0]) & 0x800000L) == 0)) {
                Byte sf = Byte.parseByte(xmlDecoder.getShortForm().toString());
                if (null == sf) {
                    return null;
                }

                shortForm = Attribute.ABSOLUTE_AREA_SERVICE_NUMBER + sf;
            } else {
                shortForm = xmlDecoder.getShortForm();
            }

            return decodeSubElement(shortForm, ctx);
        } else {
            return xmlDecoder.decodeNullableElement((Element) element);
        }
    }

    /**
     * Find a MAL Element to pass to decoder so that decoder can know which MAL element this is.
     *
     * @param shortForm ID of MAL data type which can be looked up to find out which MAL element this is.
     * @param ctx     MALEncodingContext to be used in order to decode an Element
     * @return The decoded element.
     * @throws IllegalArgumentException If the parameter ‘ctx’ is NULL
     * @throws MALException             If the MALElementInputStream is closed
     */
    @Override
    protected Object decodeSubElement(final Long shortForm, final MALEncodingContext ctx) throws MALException {
        if (shortForm == null) {
            return null;
        }
        final MALElementFactory malElementFactory = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(shortForm);
        if (malElementFactory == null) {
            throw new MALException("GEN transport unable to find element factory for short type: " + shortForm);
        }

        return xmlDecoder.decodeElement((Element) malElementFactory.createElement());
    }

    /**
     * Closes the stream.
     *
     * @throws MALException If an internal error occurs
     */
    @Override
    public void close() throws MALException {

    }

    /**
     * Relaying the method from decoder
     *
     * @return serialized byte array of SerializedEncodedMessage object with the remaining encoded data
     * @throws MALException IO
     */
    @Override
    public byte[] getRemainingEncodedData() throws MALException {
        return xmlDecoder.getRemainingEncodedData();
    }
}
