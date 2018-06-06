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

import nasa.mo.mal.encoder.Constants;
import nasa.mo.mal.encoder.util.BiConsumerWithMALException;
import nasa.mo.mal.encoder.util.EncodingHelper;
import nasa.mo.mal.encoder.util.RunnableWithMALException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALListEncoder;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.structures.Element;
import org.w3c.dom.*;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Wai Phyo
 *         Created on 5/3/17.
 */
public class XmlEncoder implements MALListEncoder {
    private Document document;
    private OutputStream outputStream;
    /**
     * This stack holds encoding Xml element.
     * This is required since complex elements like list and composites have several children to be encoded.
     * example: a ID list with 4 children
     * In the beginning stack has only body
     * Body |
     * all 4 children should be encoded inside ID list.
     * Body | ID-List |
     * when encoding each child,
     * Body | ID-List | ID |
     * after each child is encoded, it is popped.
     * When entire list is encoded, ID=List is popped
     * Body |
     * Now it is ready for next element.
     */
    private Stack<XmlElementContainer> parentStack;
    private static final Logger LOGGER = Logger.getLogger(XmlEncoder.class.toString());
    private XmlDocGenerator xmlDocGenerator;

    /**
     * Constructor with Output Stream
     * This method will convert encoded messages to byte level.
     * This method is necessary because Stream Factory (inherited from MAL Stream Factory) needs to call a method with
     * output stream
     *
     * @param outputStream Stream encoded message is written to.
     */
    public XmlEncoder(OutputStream outputStream) {
        this.outputStream = outputStream;
        init();
    }

    /**
     * Initialization method.
     * To be used by constructors.
     * 1. Create a new XML Document to store the body
     * 2. get document from it for easier access to write.
     * 3. a stack to store current parent.
     * 4. create root / Body element, store to document, push to stack as it is current parent.
     * 5. encoding list flag is used in close() method when encoding lists.
     * 6. Set Content Type to XML as using this class means encoding is done in XML.
     * TODO since Content-Type header values is set in Encoder, other encoders should follow section 3.6.5
     */
    private void init() {
        xmlDocGenerator = new XmlDocGenerator();
        document = xmlDocGenerator.getDocument();
        parentStack = new Stack<>();
        org.w3c.dom.Element root = xmlDocGenerator.createRootElement();
        document.appendChild(root);
        parentStack.push(new XmlElementContainer(root, false));
    }

    /**
     * 2 place can call this.
     * 1. list encoder
     * 1.1 if it is list encoder, remove the parent from stack
     * 2. Stream factory
     * 2.1 if it is done, convert XML to byte and copy to serializable object
     * 2.2 add necessary things for header. TODO add content type, content length, encoding key to header
     *
     * NOTE: in order for GENMessageBody to copy the encoded body properly, some validations are added.
     * The original workflow is to write encoded body parts to the stream.
     * Since this encoder write everything to the stream just before it is closed,
     * if there is something written to the stream, this will append it.
     * To avoid this problem, first it checks if the stream is empty.
     * At the moment, this is only checking it for bytearray stream.
     * write everything to output stream
     * If there is exception, write to log.
     */
    @Override
    public void close() {
        if (parentStack.peek().isList) {
            parentStack.pop();
        } else {
            try {
                if (outputStream == null) {
                    return;
                }
                if (outputStream instanceof ByteArrayOutputStream &&
                        ((ByteArrayOutputStream) outputStream).toByteArray().length > 0) {
                    outputStream.close();
                    return;
                }
                TransformerFactory.newInstance().newTransformer()
                        .transform(new DOMSource(document), new StreamResult(outputStream));
                outputStream.close();
            } catch (IOException | TransformerException ioException) {
                LOGGER.log(Level.SEVERE, "IOException while writing XML Doc to Output Stream {0}.", ioException);
            }
        }
    }

    /**
     * Encodes a non-null Boolean.
     *
     * @param att The Boolean to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeBoolean(Boolean att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.BOOLEAN, att.toString());
    }

    /**
     * Encodes a Boolean that may be null
     *
     * @param att The Boolean to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableBoolean(Boolean att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.BOOLEAN, () -> encodeBoolean(att));
    }

    /**
     * Encodes a non-null Float.
     *
     * @param att The Float to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeFloat(Float att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.FLOAT, att.toString());
    }

    /**
     * Encodes a Float that may be null
     *
     * @param att The Float to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableFloat(Float att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.FLOAT, () -> encodeFloat(att));
    }

    /**
     * Encodes a non-null Double.
     *
     * @param att The Double to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeDouble(Double att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.DOUBLE, att.toString());
    }

    /**
     * Encodes a Double that may be null
     *
     * @param att The Double to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableDouble(Double att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.DOUBLE, () -> encodeDouble(att));
    }

    /**
     * Encodes a non-null Octet.
     *
     * @param att The Octet to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeOctet(Byte att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.OCTET, att.toString());
    }

    /**
     * Encodes an Octet that may be null
     *
     * @param att The Octet to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableOctet(Byte att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.OCTET, () -> encodeOctet(att));
    }

    /**
     * Encodes a non-null UOctet.
     *
     * @param att The UOctet to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeUOctet(UOctet att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.U_OCTET, att.toString());
    }

    /**
     * Encodes a UOctet that may be null
     *
     * @param att The UOctet to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableUOctet(UOctet att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.U_OCTET, () -> encodeUOctet(att));
    }

    /**
     * Encodes a non-null Short.
     *
     * @param att The Short to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeShort(Short att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.SHORT, att.toString());

    }

    /**
     * Encodes a Short that may be null
     *
     * @param att The Short to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableShort(Short att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.SHORT, () -> encodeShort(att));
    }

    /**
     * Encodes a non-null UShort.
     *
     * @param att The UShort to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeUShort(UShort att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.U_SHORT, att.toString());
    }

    /**
     * Encodes a UShort that may be null
     *
     * @param att The UShort to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableUShort(UShort att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.U_SHORT, () -> encodeUShort(att));
    }

    /**
     * Encodes a non-null Integer.
     *
     * @param att The Integer to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeInteger(Integer att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.INTEGER, att.toString());
    }

    /**
     * Encodes an Integer that may be null
     *
     * @param att The Integer to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableInteger(Integer att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.INTEGER, () -> encodeInteger(att));
    }

    /**
     * Encodes a non-null UInteger.
     *
     * @param att The UInteger to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeUInteger(UInteger att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.U_INTEGER, att.toString());
    }

    /**
     * Encodes a UInteger that may be null
     *
     * @param att The UInteger to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableUInteger(UInteger att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.U_INTEGER, () -> encodeUInteger(att));
    }

    /**
     * Encodes a non-null Long.
     *
     * @param att The Long to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeLong(Long att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.LONG, att.toString());
    }

    /**
     * Encodes a Long that may be null
     *
     * @param att The Long to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableLong(Long att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.LONG, () -> encodeLong(att));
    }

    /**
     * Encodes a non-null ULong.
     *
     * @param att The ULong to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeULong(ULong att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.U_LONG, att.toString());
    }

    /**
     * Encodes a ULong that may be null
     *
     * @param att The ULong to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableULong(ULong att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.U_LONG, () -> encodeULong(att));
    }

    /**
     * Encodes a non-null String.
     *
     * @param att The String to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeString(String att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.STRING, att);
    }

    /**
     * Encodes a String that may be null
     *
     * @param att The String to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableString(String att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.STRING, () -> encodeString(att));
    }

    /**
     * Encodes a non-null Blob.
     *
     * @param att The Blob to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeBlob(Blob att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        //EncodingHelper.checkForNull(att.getValue());
        if (att.getValue() == null || att.getValue().length == 0) {
            internalEncodeAttributes(Constants.BLOB, "");
        } else {
            internalEncodeAttributes(Constants.BLOB, EncodingHelper.convertByteArrayToHexString(att.getValue()));
        }
    }

    /**
     * Encodes a Blob that may be null
     *
     * @param att The Blob to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableBlob(Blob att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.BLOB, () -> encodeBlob(att));
    }

    /**
     * Encodes a non-null Duration.
     *
     * @param att The Duration to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeDuration(Duration att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.DURATION, EncodingHelper.encodeDuration(att.getValue()));
    }

    /**
     * Encodes a Duration that may be null
     *
     * @param att The Duration to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableDuration(Duration att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.DURATION, () -> encodeDuration(att));
    }

    /**
     * Encodes a non-null FineTime.
     *
     * @param att The FineTime to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeFineTime(FineTime att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.FINE_TIME, EncodingHelper.encodeFineTimeToXML(att.getValue()));
    }

    /**
     * Encodes a FineTime that may be null
     *
     * @param att The FineTime to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableFineTime(FineTime att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.FINE_TIME, () -> encodeFineTime(att));
    }

    /**
     * Encodes a non-null Identifier.
     *
     * @param att The Identifier to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeIdentifier(Identifier att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        EncodingHelper.checkForNull(att.getValue());
        internalEncodeAttributes(Constants.IDENTIFIER, EncodingHelper.encodeString(att.getValue()));
    }

    /**
     * Encodes an Identifier that may be null
     *
     * @param att The Identifier to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableIdentifier(Identifier att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.IDENTIFIER, () -> encodeIdentifier(att));
    }

    /**
     * Encodes a non-null Time.
     *
     * @param att The Time to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeTime(Time att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        internalEncodeAttributes(Constants.TIME, EncodingHelper.encodeTimeToXML(att.getValue()));
    }

    /**
     * Encodes a Time that may be null
     *
     * @param att The Time to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableTime(Time att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.TIME, () -> encodeTime(att));
    }

    /**
     * Encodes a non-null URI.
     *
     * @param att The URI to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeURI(URI att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        EncodingHelper.checkForNull(att.getValue());
        internalEncodeAttributes(Constants.URI, EncodingHelper.encodeURI(att.getValue()));
    }

    /**
     * Encodes a URI that may be null
     *
     * @param att The URI to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableURI(URI att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.URI, () -> encodeURI(att));
    }

    /**
     * encoding a MAL element with the short form.
     * Call respective method with the correct method to create the parent xml
     *
     * @param element The Element to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeElement(Element element) throws IllegalArgumentException, MALException {
        internalEncodeElement(element, this::createParentXmlElement);
    }

    /**
     * encoding a MAL element with the short form.
     * Call respective method with the correct method to create the parent xml
     *
     * @param element The Element to encode.
     * @param type short form id of the element
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    public void encodeElement(Element element, final long type) throws IllegalArgumentException, MALException {
        internalEncodeElement(element, (name) -> createParentXmlElement(name, type));
    }

    /**
     * validations:   check if element is null
     * 1.   list
     * 1.1. enumeration
     *      call encoding list with method implementation of encoding each enumeration element in the list
     *      flag for composite = false
     * 1.2. composite
     *      call encoding list with method implementation which calls each element's encode method.
     *      flag for composite = true
     * 1.3. attribute
     *      call encoding list with method implementation which calls each element's encode method.
     *      flag for composite = false
     * 1.4. invalid type
     *      throw exception
     *
     * 2.   not list
     * 2.1. enumeration
     *      create xml parent element, encode child element, remove current xml parent as it is done.
     *      flag for composite = false
     * 2.2. composite
     *      create xml parent element, call child element's encode method, remove current xml parent as it is done.
     *      flag for composite = true
     * 2.3. attribute
     *      create xml parent element, call child element's encode method, remove current xml parent as it is done.
     *      flag for composite = false
     * 2.4. invalid type
     *      throw exception
     *
     * @param element MAL element to be encoded
     * @param createParentXmlElement method parameter how to create parent xml element for current MAL element
     * @throws MALException null or invalid type exceptions
     */
    private void internalEncodeElement(Element element, Function<String, org.w3c.dom.Element> createParentXmlElement)
            throws MALException {
        EncodingHelper.checkForNull(element);
        if (element instanceof ElementList) {
            if (element instanceof EnumerationList) {
                encodeElementList((EnumerationList) element,
                        (name) -> addParentUnderChild(createParentXmlElement.apply(name), false, true),
                        (name, each) -> parentStack.peek().currentElement
                                .appendChild(xmlDocGenerator.createSimpleElementWithValue(name, each.toString())),
                        false);
            } else if (element instanceof CompositeList) {
                encodeElementList((List) element,
                        (name) -> addParentUnderChild(createParentXmlElement.apply(name), false, true),
                        (name, each) -> ((Element) each).encode(this),
                        true);
            } else if (element instanceof AttributeList) {
                encodeElementList((List) element,
                        (name) -> addParentUnderChild(createParentXmlElement.apply(name), false, true),
                        (name, each) -> {
                            if (each instanceof Element) {
                                ((Element) each).encode(this);
                            } else {
                                if (each instanceof String) {
                                    encodeString((String) each);
                                } else if (each instanceof Boolean) {
                                    encodeBoolean((boolean) each);
                                } else if (each instanceof Byte) {
                                    encodeOctet((byte) each);
                                } else if (each instanceof Short) {
                                    encodeShort((short) each);
                                } else if (each instanceof Integer) {
                                    encodeInteger((int) each);
                                } else if (each instanceof Long) {
                                    encodeLong((long) each);
                                } else if (each instanceof Double) {
                                    encodeDouble((double) each);
                                } else if (each instanceof Float) {
                                    encodeFloat((float) each);
                                } else {
                                    throw new MALException("Unknown primitive type attribute in AttributeList");
                                }
                            }
                        },
                        false);
            } else {
                throw new MALException("Unknown element list type");
            }
        } else {
            if (element instanceof Enumeration) {
                String name = element.getClass().getSimpleName();
                addParentUnderChild(createParentXmlElement.apply(name), false, false);
                parentStack.peek().currentElement.appendChild(xmlDocGenerator.createSimpleElementWithValue(name,
                        element.toString()));
            } else if (element instanceof Composite) {
                addParentUnderChild(createParentXmlElement.apply(element.getClass().getSimpleName()), true, false);
                element.encode(this);
            } else if (element instanceof Attribute) {
                addParentUnderChild(createParentXmlElement.apply(element.getClass().getSimpleName()), false, false);
                element.encode(this);
            } else {
                throw new MALException("Unknown element type");
            }
            parentStack.pop();

        }
    }

    /**
     * Encodes an Element that may be null
     *
     * @param element The Element to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableElement(Element element) throws MALException {
        internalEncodeNullableAttribute(element, Constants.ELEMENT, () -> encodeElement(element));
    }

    /**
     * Encodes a non-null Attribute.
     * Since it is an Attribute, it is drilled down to the exact attribute type.
     * Then the actual encoding method is called.
     *
     * @param att The Attribute to encode.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during encoding.
     */
    @Override
    public void encodeAttribute(Attribute att) throws IllegalArgumentException, MALException {
        EncodingHelper.checkForNull(att);
        att.encode(this);
    }

    /**
     * Temporary method to add attributes to XML doc.
     * There is a tweak in encoding attributes which are part of a composite.
     *
     * In composites' encode method, they simply encode the attributes.
     *
     * This class' logic is to encode that under current parent element.
     * Hence, they are directly encoded under composite.
     *
     * Standard White Book format is to an extra parent element.
     * This is done by checking if the current encoding element is composite.
     * If so, add an extra layer, and remove it in the end.
     *
     * If not, just encode it under current parent element.
     *
     * @param name Name of the core element
     * @param value Value of the core element
     */
    private void internalEncodeAttributes(final String name, final String value) {
        if (parentStack.peek().isComposite) {
            addParentUnderChild(document.createElement(name), false, false);
            parentStack.peek().currentElement.appendChild(xmlDocGenerator.createSimpleElementWithValue(name, value));
            parentStack.pop();

        } else {
            parentStack.peek().currentElement.appendChild(xmlDocGenerator.createSimpleElementWithValue(name, value));

        }
    }

    /**
     * Method for encoding Nullable Attributes.
     * 1.   if the object (attribute) is null, add a null element
     * 2.   if not, execute the respective encoding method passed from the parameter
     *
     * @param att encoding attribute
     * @param name name of the xml element for the encoding attribute
     * @param attributeEncoder encoding method
     * @throws MALException null or invalid format exceptions
     */
    private void internalEncodeNullableAttribute(Object att,
                                                 String name,
                                                 RunnableWithMALException attributeEncoder)
            throws MALException {
        if (att == null) {
            parentStack.peek().currentElement.appendChild(xmlDocGenerator.createEmptyElementWithNullAttr(name));
        } else {
            attributeEncoder.run();
        }
    }

    /**
     * Encodes an Attribute that may be null
     *
     * @param att The Attribute to encode.
     * @throws MALException If an error detected during encoding.
     */
    @Override
    public void encodeNullableAttribute(Attribute att) throws MALException {
        internalEncodeNullableAttribute(att, Constants.ATTRIBUTE, () -> encodeAttribute(att));
    }

    /**
     * Creates a list encoder for encoding a list element.
     * NOTE: Not using this for XML as this will not work for List of Composites.
     *
     * @param list The list to encode, java.lang.IllegalArgumentException exception thrown if null.
     * @return The new list encoder.
     * @throws IllegalArgumentException If the list argument is null.
     * @throws MALException             If an error detected during list encoder creation.
     */
    @Override
    public MALListEncoder createListEncoder(List list) throws IllegalArgumentException, MALException {
        throw new MALException("Not supported for " + this.getClass().getName());
    }

    /**
     * Encoding MAL element list to the XML document
     *
     * 1.   get the element list name
     * 2.   add the xml parent element for this list. done by executing lambda's method
     *      Based on White Book, it should end with suffix "List"
     * 2.1. remove the list suffix
     *      Since the child elements are of the same name without the suffix "List"
     * 3.   for empty list, add a null child element
     * 4.   for non empty list
     * 4.1. if each element is null, add a null child element
     * 4.2. if each element is not null, place a parent xml element
     *      and let the method parameter take care of how to encode it.
     * 5.   call close() which will revert the conditions and flags to say list is encoded.
     * TODO commented adding null element if the list is empty. Need to verify
     * @param list MAL element list
     * @param createListParentXmlElement method parameter of how xml parent element is created.
     * @param createEachXmlElement method parameter of how each element in the list is encoded.
     * @throws MALException null or other possible exceptions
     */
    private void encodeElementList(List list,
                                   Consumer<String> createListParentXmlElement,
                                   BiConsumerWithMALException<String, Object> createEachXmlElement,
                                   boolean isComposite) throws MALException {
        String elementName = list.getClass().getSimpleName();
        createListParentXmlElement.accept(elementName);
        elementName = elementName.replace(Constants.LIST_SUFFIX, EncodingHelper.EMPTY_STRING);
//        if (list.isEmpty()) {
//            parentStack.peek().currentElement
//                    .appendChild(xmlDocGenerator.createEmptyElementWithNullAttr(elementName));
//        }
        for (Object each : list) {
            if (each == null) {
                parentStack.peek().currentElement
                        .appendChild(xmlDocGenerator.createEmptyElementWithNullAttr(elementName));
            } else {
                addParentUnderChild(createParentXmlElement(elementName), isComposite, false);
                createEachXmlElement.accept(elementName, each);
                parentStack.pop();
            }
        }
        close();
    }

    /**
     * Adding Xml element from parameter to the main stack.
     *
     * @param currentXmlElement current Xml element
     * @param isComposite flag if this element will hold encoded MAL composite element
     */
    private void addParentUnderChild(org.w3c.dom.Element currentXmlElement, boolean isComposite, boolean isList) {
        parentStack.peek().currentElement.appendChild(currentXmlElement);
        parentStack.push(new XmlElementContainer(currentXmlElement, isComposite, isList));
    }

    /**
     * Simple method to create an element from the main Xml document
     * 1 liner method used by lambda methods
     *
     * @param name name of the new XML element
     * @return Xml element
     */
    private org.w3c.dom.Element createParentXmlElement(final String name) {
        return document.createElement(name);
    }

    /**
     * Creating an XML element and add a malxml:type attribute  from the main Xml document
     *
     * @param name name of the new XML element
     * @param typeAttribute value of malxml:type
     * @return Xml element
     */
    private org.w3c.dom.Element createParentXmlElement(final String name, final long typeAttribute) {
        org.w3c.dom.Element parent = document.createElement(name);
        parent.setAttributeNode(xmlDocGenerator.getNewTypeAttribute(typeAttribute));
        return parent;
    }

    /**
     * Container class with XML element and a flag.
     */
    private final class XmlElementContainer {
        /**
         * Xml element
         */
        org.w3c.dom.Element currentElement;
        /**
         * flag if the MAL element of the Xml element is a composite
         */
        boolean isComposite;
        /**
         * flag if the MAL element of the Xml element is a list
         */
        boolean isList;

        /**
         * Simple Constructor
         * @param currentElement created XML element
         * @param isComposite flag if the MAL element of the Xml element is a composite
         */
        XmlElementContainer(org.w3c.dom.Element currentElement, boolean isComposite) {
            this.currentElement = currentElement;
            this.isComposite = isComposite;
            isList = false;
        }

        /**
         * Constructor with List flag
         * @param currentElement created XML element
         * @param isComposite flag if the MAL element of the Xml element is a composite
         * @param isList flag if MAL element is a list
         */
        XmlElementContainer(org.w3c.dom.Element currentElement, boolean isComposite, boolean isList) {
            this.currentElement = currentElement;
            this.isComposite = isComposite;
            this.isList = isList;
        }
    }
}

