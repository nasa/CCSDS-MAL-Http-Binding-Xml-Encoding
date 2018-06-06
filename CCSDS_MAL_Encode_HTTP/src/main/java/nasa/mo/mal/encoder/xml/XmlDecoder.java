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
import nasa.mo.mal.encoder.util.EncodingHelper;
import nasa.mo.mal.encoder.util.FunctionWithMALException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALListDecoder;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.structures.Element;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Wai Phyo
 *         Created on 5/3/17.
 */
public class XmlDecoder implements MALListDecoder {
    /**
     * XML Document
     */
    private Document document;
    /**
     * Logger to store fatal errors.
     */
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(XmlDecoder.class.toString());
    /**
     * A stack holding the parent element of the actual element which needs to be decoded.
     */
    private Stack<XmlNodeListIterator> currentNodeStack;

    private InputStream inputStream;
    /**
     * Constructor with input stream.
     * Convert to Encoded Message container.
     * @param inputStream Stream holding an object which has header and body
     */
    public XmlDecoder(InputStream inputStream) {
        this.inputStream = inputStream;
        init();
    }

    /**
     * Initialization method.
     * To be used by constructors.
     * 1. Get XML document from byte array
     * 2. log error for any exception TODO is it enough?
     * 3. normalize the document based on
     *    http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
     * 4. validate XML document has root element named "Body"
     * 5. put root element to stack.
     */
    private void init() {
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(inputStream);
            currentNodeStack = new Stack<>();
        } catch (ParserConfigurationException | SAXException | IOException exp) {
            LOGGER.log(Level.SEVERE,
                    "Some exception while convert XML Document from input stream {0}", exp);
        }
        document.setXmlStandalone(true);
        document.getDocumentElement().normalize();
        if (!document.getDocumentElement().getNodeName().equals(Constants.ROOT_ELEMENT)) {
            LOGGER.log(Level.SEVERE,
                    "Invalid root element name for converted XML document at XmlDecoder.");
        }
        try {
            emptyNodeRemoval(document);
        } catch (XPathExpressionException exp) {
            LOGGER.log(Level.SEVERE,
                    "Error while removing white spaces from XML document.", exp.getCause());
            throw new RuntimeException("Error while removing white spaces from XML document.", exp.getCause());
        }
        currentNodeStack = new Stack<>();
        currentNodeStack.push(XmlNodeListIterator.createXmlNodeListIterator(document
                .getElementsByTagName(document.getDocumentElement().getNodeName()).item(0)));
    }

    /**
     * Logic to remove extra spaces, tabs, and line-breaks.
     * 1. Find all extra text elements.
     * 2. if it has no parent or parent is ROOT, remove
     * 2.1. if the parent has more than 1 elements, remove
     *
     * @param document XML Document
     * @throws XPathExpressionException rquired for XPaths
     */
    private static void emptyNodeRemoval(Document document) throws XPathExpressionException {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        // XPath to find empty text nodes.
        XPathExpression xpathExp = xpathFactory.newXPath().compile(
                "//text()[normalize-space(.) = '']");
        NodeList emptyTextNodes = (NodeList)
                xpathExp.evaluate(document, XPathConstants.NODESET);

        // Remove each empty text node from document.
        for (int i = 0; i < emptyTextNodes.getLength(); i++) {
            Node emptyTextNode = emptyTextNodes.item(i);
            Node parentNode = emptyTextNode.getParentNode();

            if (!(!Objects.isNull(parentNode) && !parentNode.getNodeName().equals(Constants.ROOT_ELEMENT) &&
                    parentNode.getChildNodes().getLength() == 1))  {
                emptyTextNode.getParentNode().removeChild(emptyTextNode);
            }
        }
    }

    /**
     * call next from iterator with some validations.
     * @return current node or null
     */
    Node getNextNode() {
        if (!currentNodeStack.empty() && currentNodeStack.peek().hasNext()) {
            return currentNodeStack.peek().next();
        } else {
            return null;
        }
    }

    /**
     * Check if the node has a null attribute and if it is true.
     * @param node DOM node
     * @return flag if it is null
     */
    private boolean isNullAttributeTrue(Node node) {
        return node.hasAttributes() &&
                ((org.w3c.dom.Element) node).getAttribute(Constants.NULLABLE_ATTRIBUTE_NAME).trim()
                        .equals(Boolean.TRUE.toString());
    }

    /**
     * Getting actual parent Node of the element which is decoded
     * example:
     * <Body>
     *     <Attribute>
     *         <Identifier>TestString</Identifier>
     *     </Attribute>
     * </Body>
     *
     * Currently, stack.peek() will return Attribute.
     * So take that attribute, and put it on stack.
     * So stack has Body | Attribute.
     * Then return child of Attribute which is Identifier
     *
     * @return Element Node
     * @throws MALException null exception
     */
    private Node prepForDecodingElements() throws MALException {
        currentNodeStack.push(XmlNodeListIterator.createXmlNodeListIterator(getNextNode()));
        if (currentNodeStack.peek() != null && currentNodeStack.peek().hasNext()) {
            return currentNodeStack.peek().next();
        } else {
            return null;
        }
    }

    /**
     * Main method to decode all numbers
     * Boolean, Byte, Short, Int, Long, BigInteger
     * 1. Get actual element.
     * 2. validate it is not null.
     * 3. try to convert with the provided method.
     * 4. pop the used parent from stack.
     *
     * @param objectConverter Interface which is implemented by individual decoding methods
     * @param <T> decoded type. Mainly Attribute types
     * @return decoded object
     * @throws MALException null or malformed exception
     */
    private <T> T decodeNotNullableNumber(FunctionWithMALException<String, T> objectConverter) throws MALException {
        Node elementNode = prepForDecodingElements();
        EncodingHelper.checkForNull(isNullAttributeTrue(elementNode) ? null : elementNode);
        return decodeNumber(objectConverter, elementNode);
    }

    /**
     * Similar to decodeNotNullableNumber()
     * Difference is
     * if it is null, instead of throwing exception, return null.
     *
     * @param objectConverter Interface which is implemented by individual decoding methods
     * @param <T> decoded type. Mainly Attribute types
     * @return decoded object
     * @throws MALException null or malformed exception
     */
    private<T> T decodeNullableNumber(FunctionWithMALException<String, T> objectConverter) throws MALException {
        Node elementNode = prepForDecodingElements();
        if (elementNode == null || isNullAttributeTrue(elementNode)) {
            popFromNodeStack();
            return null;
        }
        return decodeNumber(objectConverter, elementNode);
    }

    /**
     * Converting the Xml number node to actual number.
     *
     * @param objectConverter Interface which is implemented by individual decoding methods
     * @param elementNode Xml Element Node which has the number
     * @param <T> decoded type. Mainly Attribute types
     * @return decoded object
     * @throws MALException null or malformed exception*/
    private <T> T decodeNumber(FunctionWithMALException<String, T> objectConverter, Node elementNode) throws MALException {
        try {
            T result = objectConverter.apply(elementNode.getFirstChild().getNodeValue().trim());
            popFromNodeStack();
            return result;
        } catch (NumberFormatException exp) {
            throw new MALException(exp.getLocalizedMessage(), exp);
        }
    }

    /**
     * Similar to decodeNotNullableNumber()
     * This method is responsible for all other types.
     * Difference is There is no try-catch for NumberFormat since they are not number conversions.
     * Edit: This method should be able to decode both Attributes and Enumerations since they are so similar.
     * Difference is how they are encoded in Composites.
     * Hence, the way to retrieve element node is different based on the type.
     *
     * @param elementNode DOM element Node
     * @param objectConverter Interface which is implemented by individual decoding methods
     * @param <T> decoded type. Mainly Attribute types
     * @return decoded object
     * @throws MALException null or malformed exception
     */
    private <T> T decodeObject(Node elementNode, FunctionWithMALException<String, T> objectConverter)
            throws MALException {
        EncodingHelper.checkForNull(isNullAttributeTrue(elementNode) ? null : elementNode);
        T result = objectConverter.apply(elementNode.getFirstChild() == null ? "" : elementNode.getFirstChild().getNodeValue().trim());
        popFromNodeStack();
        return result;
    }

    /**
     * Similar to decodeObject() and decodeNullableNumber()
     * allowing nulls to return null
     * checking child null element for empty strings
     *
     * @param objectConverter Interface which is implemented by individual decoding methods
     * @param <T> decoded type. Mainly Attribute types
     * @return decoded object
     * @throws MALException null or malformed exception
     */
    private <T> T decodeNullableObject(FunctionWithMALException<String, T> objectConverter) throws MALException {
        Node elementNode = prepForDecodingElements();
        if (elementNode == null || isNullAttributeTrue(elementNode)) {
            popFromNodeStack();
            return null;
        } else {
            T result = objectConverter.apply(elementNode.getFirstChild() == null ? "" : elementNode.getFirstChild().getNodeValue().trim());
            popFromNodeStack();
            return result;
        }
    }

    /**
     * Decodes a Boolean.
     *
     * @return The decoded Boolean.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Boolean decodeBoolean() throws MALException {
        return decodeNotNullableNumber(Boolean::valueOf);
    }

    /**
     * Decodes a Boolean that may be null.
     *
     * @return The decoded Boolean or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Boolean decodeNullableBoolean() throws MALException {
        return decodeNullableNumber(Boolean::valueOf);
    }

    /**
     * Decodes a Float.
     *
     * @return The decoded Float.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Float decodeFloat() throws MALException {
        return decodeNotNullableNumber(Float::valueOf);
    }

    /**
     * Decodes a Float that may be null.
     *
     * @return The decoded Float or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Float decodeNullableFloat() throws MALException {
        return decodeNullableNumber(Float::valueOf);
    }

    /**
     * Decodes a Double.
     *
     * @return The decoded Double.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Double decodeDouble() throws MALException {
        return decodeNotNullableNumber(Double::valueOf);
    }

    /**
     * Decodes a Double that may be null.
     *
     * @return The decoded Double or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Double decodeNullableDouble() throws MALException {
        return decodeNullableNumber(Double::valueOf);
    }

    /**
     * Decodes an Octet.
     *
     * @return The decoded Octet.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Byte decodeOctet() throws MALException {
        return decodeNotNullableNumber(Byte::valueOf);

    }

    /**
     * Decodes an Octet that may be null.
     *
     * @return The decoded Octet or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Byte decodeNullableOctet() throws MALException {
        return decodeNullableNumber(Byte::valueOf);
    }

    /**
     * Decodes a UOctet.
     *
     * @return The decoded UOctet.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public UOctet decodeUOctet() throws MALException {
        return new UOctet(decodeNotNullableNumber(Short::valueOf));

    }

    /**
     * Decodes a UOctet that may be null.
     *
     * @return The decoded UOctet or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public UOctet decodeNullableUOctet() throws MALException {
        Short result = decodeNullableNumber(Short::valueOf);
        return result == null ? null : new UOctet(result);
    }

    /**
     * Decodes a Short.
     *
     * @return The decoded Short.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Short decodeShort() throws MALException {
        return decodeNotNullableNumber(Short::valueOf);
    }

    /**
     * Decodes a Short that may be null.
     *
     * @return The decoded Short or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Short decodeNullableShort() throws MALException {
        return decodeNullableNumber(Short::valueOf);
    }

    /**
     * Decodes a UShort.
     *
     * @return The decoded UShort.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public UShort decodeUShort() throws MALException {
        return new UShort(decodeNotNullableNumber(Integer::valueOf));
    }

    /**
     * Decodes a UShort that may be null.
     *
     * @return The decoded UShort or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public UShort decodeNullableUShort() throws MALException {
        Integer result = decodeNullableNumber(Integer::valueOf);
        return result == null ? null : new UShort(result);
    }

    /**
     * Decodes an Integer.
     *
     * @return The decoded Integer.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Integer decodeInteger() throws MALException {
        return decodeNotNullableNumber(Integer::valueOf);
    }

    /**
     * Decodes an Integer that may be null.
     *
     * @return The decoded Integer or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Integer decodeNullableInteger() throws MALException {
        return decodeNullableNumber(Integer::valueOf);
    }

    /**
     * Decodes a UInteger.
     *
     * @return The decoded UInteger.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public UInteger decodeUInteger() throws MALException {
        return new UInteger(decodeNotNullableNumber(Long::valueOf));
    }

    /**
     * Decodes a UInteger that may be null.
     *
     * @return The decoded UInteger or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public UInteger decodeNullableUInteger() throws MALException {
        Long result = decodeNullableNumber(Long::valueOf);
        return result == null ? null : new UInteger(result);
    }

    /**
     * Decodes a Long.
     *
     * @return The decoded Long.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Long decodeLong() throws MALException {
        return decodeNotNullableNumber(Long::valueOf);
    }

    /**
     * Decodes a Long that may be null.
     *
     * @return The decoded Long or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Long decodeNullableLong() throws MALException {
        return decodeNullableNumber(Long::valueOf);
    }

    /**
     * Decodes a ULong.
     *
     * @return The decoded ULong.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public ULong decodeULong() throws MALException {
        return new ULong(decodeNotNullableNumber(BigInteger::new));
    }

    /**
     * Decodes a ULong that may be null.
     *
     * @return The decoded ULong or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public ULong decodeNullableULong() throws MALException {
        BigInteger result = decodeNullableNumber(BigInteger::new);
        return result == null ? null : new ULong(result);
    }

    /**
     * Decodes a String.
     *
     * @return The decoded String.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public String decodeString() throws MALException {
        return decodeObject(prepForDecodingElements(), EncodingHelper::decodeString);
    }

    /**
     * Decodes a String that may be null.
     *
     * @return The decoded String or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public String decodeNullableString() throws MALException {
        return decodeNullableObject(EncodingHelper::decodeString);
    }

    /**
     * Decodes a Blob.
     *
     * @return The decoded Blob.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Blob decodeBlob() throws MALException {
        byte[] value = decodeObject(prepForDecodingElements(), EncodingHelper::convertHexStringToByteArray);
        if (value == null) {
            return new Blob();
        } else {
            return new Blob(value);
        }
    }

    /**
     * Decodes a Blob that may be null.
     *
     * @return The decoded Blob or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Blob decodeNullableBlob() throws MALException {
        byte[] result = decodeNullableObject(EncodingHelper::convertHexStringToByteArray);
        return result == null ? null : new Blob(result);
    }

    /**
     * Decodes a Duration.
     *
     * @return The decoded Duration.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Duration decodeDuration() throws MALException {
        return new Duration(decodeObject(prepForDecodingElements(), EncodingHelper::decodeDuration));
    }

    /**
     * Decodes a Duration that may be null.
     *
     * @return The decoded Duration or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Duration decodeNullableDuration() throws MALException {
        Double result = decodeNullableObject(EncodingHelper::decodeDuration);
        return result == null ? null : new Duration(result);
    }

    /**
     * Decodes a FineTime.
     *
     * @return The decoded FineTime.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public FineTime decodeFineTime() throws MALException {
        return new FineTime(decodeObject(prepForDecodingElements(), EncodingHelper::decodeFineTimeFromXML));
    }

    /**
     * Decodes a FineTime that may be null.
     *
     * @return The decoded FineTime or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public FineTime decodeNullableFineTime() throws MALException {
        Long result = decodeNullableObject(EncodingHelper::decodeFineTimeFromXML);
        return result == null ? null : new FineTime(result);
    }

    /**
     * Decodes an Identifier.
     *
     * @return The decoded Identifier.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Identifier decodeIdentifier() throws MALException {
        String value = decodeObject(prepForDecodingElements(), EncodingHelper::decodeString);
        if (value == null) {
            return new Identifier();
        } else {
            return new Identifier(value);
        }
    }

    /**
     * Decodes an Identifier that may be null.
     *
     * @return The decoded Identifier or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Identifier decodeNullableIdentifier() throws MALException {
        String result = decodeNullableObject(EncodingHelper::decodeString);
        return result == null ? null : new Identifier(result);
    }

    /**
     * Decodes a Time.
     *
     * @return The decoded Time.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Time decodeTime() throws MALException {
        return new Time(decodeObject(prepForDecodingElements(), EncodingHelper::decodeTimeFromXML));
    }

    /**
     * Decodes a Time that may be null.
     *
     * @return The decoded Time or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Time decodeNullableTime() throws MALException {
        Long result = decodeNullableObject(EncodingHelper::decodeTimeFromXML);
        return result == null ? null : new Time(result);
    }

    /**
     * Decodes a URI.
     *
     * @return The decoded URI.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public URI decodeURI() throws MALException {
        return new URI(decodeObject(prepForDecodingElements(), EncodingHelper::decodeURI));
    }

    /**
     * Decodes a URI that may be null.
     *
     * @return The decoded URI or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public URI decodeNullableURI() throws MALException {
        String result = decodeNullableObject(EncodingHelper::decodeURI);
        return result == null ? null : new URI(result);
    }

    /**
     * Decodes an Element.
     * This method is called by composites decoding other composites or enumerations.
     * Also called by Element Input Stream with the desired decoded type as a parameter
     *
     * 1. Split into list or single element.
     * For single element,
     * 1. Attributes: It is implemented. It can take care of itself.
     * 2. Enumeration: Encoded using String value. Needs separate method.
     * 3. Composite: Need separate method to prep.
     *
     * For List, the elements' original decode methods can take care of themselves.
     *
     * @param element An instance of the element to decode.
     * @return The decoded Element.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during decoding.
     */
    @Override
    public Element decodeElement(Element element) throws IllegalArgumentException, MALException {
        if (element instanceof ElementList) {
            return element.decode(this);
        } else {
            if (element instanceof Attribute) {
                return element.decode(this);
            } else if (element instanceof Enumeration) {
                return decodeEnumeration(element);
            } else {
                return decodeComposite(element);
            }
        }
    }

    /**
     * Decoding single Enumeration objects.
     * We cannot use object.decode() since it expects numerical value.
     * And we encoded it using string based on WhiteBook.
     * @param element An instance of the element to decode.
     * @return The decoded Element.
     * @throws MALException If an error detected during decoding.
     */
    private Element decodeEnumeration(Element element) throws MALException {
        if (element instanceof QoSLevel) {
            return decodeObject(prepForDecodingElements(), QoSLevel::fromString);
        } else if (element instanceof UpdateType) {
            return decodeObject(prepForDecodingElements(), UpdateType::fromString);
        } else if (element instanceof SessionType) {
            return decodeObject(prepForDecodingElements(), SessionType::fromString);
        } else if (element instanceof InteractionType) {
            return decodeObject(prepForDecodingElements(), InteractionType::fromString);
        } else if (element instanceof Enumeration) {
            return decodeUnknownEnumeration(element);
        } else {
            throw new MALException("Unknown Enumeration Type");
        }
    }

    /**
     * Helper method to decode new Enumeration classes.
     * !!!NOTE!!! This is not the best practice using Reflections will affect the performance by magnitudes.
     * This method should be replaced once an easier method is agreed.
     *
     * Steps:
     * 1. get element node form XML (same as other methods)
     * 2. check for null (same as others)
     * 3. get the value from XML
     * 4. get constructor of enum by using the sample object from parameter using reflections.
     * 5. since constructor is private (as stated in Enumeration interface), make it public.
     * 6. Enumerations start form ordinal 0 to n.
     * 6.1. loop to find out the value of each ordinal.
     * 6.2. compare value to XML value. if same, found a match.
     * 6.2. if loop hits invalid ordinal, return null & log it.
     *
     * NOTE2: There are faster approaches using Reflection such as getting the field (array with all ordinals).
     *          But only constructor is a guarantee method which will be implemented.
     *
     * @param element sample Enumeration object
     * @return Enumeration object with the correct value
     * @throws MALException all possible Reflection exceptions
     */
    private Element decodeUnknownEnumeration(Element element) throws MALException {
        Node elementNode = prepForDecodingElements();
        EncodingHelper.checkForNull(isNullAttributeTrue(elementNode) ? null : elementNode);
        String value = elementNode.getFirstChild().getNodeValue().trim();
        try {
            Constructor constructor = element.getClass().getDeclaredConstructor(int.class);
            constructor.setAccessible(true);
            int startPosition = 0;
            while (true) {
                try {
                    Enumeration enumeration = (Enumeration) constructor.newInstance(startPosition++);
                    if (enumeration.toString().equals(value)) {
                        popFromNodeStack();
                        return enumeration;
                    }
                } catch (RuntimeException exp) {
                    LOGGER.log(Level.SEVERE, "Attempting to decode invalid Enumeration", exp);
                    popFromNodeStack();
                    return null;
                }
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exp) {
            throw new MALException("Unknown Enumeration Class", exp);
        }
    }

    /**
     * 1. toggle flag. for decoding attributes under composite.
     * 2. grab the parent and put it on top of stack.
     * 3. call element.encode().
     * 4. remove the used parent element.
     * 5. toggle back the flag.
     *
     * @param element An instance of the element to decode.
     * @return The decoded Element.
     * @throws MALException If an error detected during decoding.
     */
    private Element decodeComposite(Element element) throws MALException {
        currentNodeStack.push(XmlNodeListIterator.createXmlNodeListIterator(getNextNode()));
        Element result = element.decode(this);
        popFromNodeStack();
        return result;
    }

    /**
     * Decodes an Element that may be null.
     *
     * @param element An instance of the element to decode.
     * @return The decoded Element or null.
     * @throws IllegalArgumentException If the argument is null.
     * @throws MALException             If an error detected during decoding.
     */
    @Override
    public Element decodeNullableElement(Element element) throws IllegalArgumentException, MALException {
        if (!currentNodeStack.peek().hasNext() || isNullAttributeTrue(currentNodeStack.peek().peek())) {
            currentNodeStack.peek().next();
            currentNodeStack.peek().removeCurrentChildNode();
            return null;
        } else {
            return decodeElement(element);
        }
    }

    /**
     * Decodes an Attribute.
     * GEN Decoder and other decoders decode the attribute based on the id that they encoded beforehand.
     * Based on WhiteBook, type id is not stored.
     * Hence, it uses the string to compare which attribute it is.
     * @return The decoded Attribute.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Attribute decodeAttribute() throws MALException {
        Node parentElement = currentNodeStack.peek().peek();
        EncodingHelper.checkForNull(parentElement);
        if (isNullAttributeTrue(parentElement)) {
            EncodingHelper.checkForNull(null);
        }
        String attributeName = parentElement.getFirstChild().getNodeName();
        switch (attributeName) {
            case Constants.BOOLEAN:
                return new Union(decodeBoolean());
            case Constants.OCTET:
                return new Union(decodeOctet());
            case Constants.SHORT:
                return new Union(decodeShort());
            case Constants.INTEGER:
                return new Union(decodeInteger());
            case Constants.LONG:
                return new Union(decodeLong());
            case Constants.FLOAT:
                return new Union(decodeFloat());
            case Constants.DOUBLE:
                return new Union(decodeDouble());
            case Constants.STRING:
                return new Union(decodeString());
            case Constants.U_OCTET:
                return decodeUOctet();
            case Constants.U_SHORT:
                return decodeUShort();
            case Constants.U_INTEGER:
                return decodeUInteger();
            case Constants.U_LONG:
                return decodeULong();
            case Constants.URI:
                return decodeURI();
            case Constants.IDENTIFIER:
                return decodeIdentifier();
            case Constants.TIME:
                return decodeTime();
            case Constants.FINE_TIME:
                return decodeFineTime();
            case Constants.BLOB:
                return decodeBlob();
            case Constants.DURATION:
                return decodeDuration();
            default:
                throw new MALException("Unknown Attribute Type");
        }
    }

    /**
     * Decodes an Attribute that may be null.
     *
     * @return The decoded Attribute or null.
     * @throws MALException If an error detected during decoding.
     */
    @Override
    public Attribute decodeNullableAttribute() throws MALException {
        if (!currentNodeStack.peek().hasNext() || isNullAttributeTrue(currentNodeStack.peek().peek())) {
            currentNodeStack.peek().next();
            currentNodeStack.peek().removeCurrentChildNode();
            return null;
        } else {
            return decodeAttribute();
        }
    }

    /**
     * Creates a list decoder for decoding a list element.
     *
     * @param list The list to decode, java.lang.IllegalArgumentException exception thrown if null.
     * @return The new list decoder.
     * @throws IllegalArgumentException If the list argument is null.
     * @throws MALException             If an error detected during list decoder creation.
     */
    @Override
    public MALListDecoder createListDecoder(List list) throws IllegalArgumentException, MALException {
        currentNodeStack.push(XmlNodeListIterator.createXmlNodeListIterator(getNextNode()));
        return this;
    }

    /**
     * Determines if there are any more elements in the lists to decode.
     *
     * @return True if more elements in list, else false.
     */
    @Override
    public boolean hasNext() {
        if (!currentNodeStack.peek().hasNext()) {
            popFromNodeStack();
            return false;
        }
        return true;
    }

    /**
     * Returns the total number of elements in the list.
     * NOT supported for now
     * @return the total number of elements in the list if known otherwise -1.
     */
    @Override
    public int size() {
        return -1;
    }

    /**
     * try to get the malxml:type of the next xml element in xml document.
     * If something goes wrong, return null
     * else return long
     *
     * @return Long or null
     */
    public Long getShortForm() throws MALException{
        if (!currentNodeStack.peek().hasNext()) {
            return null;
        }
        org.w3c.dom.Element currentXmlElement = (org.w3c.dom.Element) currentNodeStack.peek().peek();
        if (!currentXmlElement.hasAttribute(Constants.MALXML_TYPE_ATTRIBUTE_NAME)) {
            return null;
        }
        try {
            return Long.valueOf(currentXmlElement.getAttribute(Constants.MALXML_TYPE_ATTRIBUTE_NAME).trim());
        } catch (NumberFormatException exp) {
            throw new MALException(exp.getLocalizedMessage(), exp);
        }
    }

    /**
     * Creating a byte array with the remaining XML data.
     * @return serialized byte array of SerializedEncodedMessage object with the remaining data
     * @throws MALException IO
     */
    public byte[] getRemainingEncodedData() throws MALException {
        try {
            return XmlDocGenerator.xmlDocToByteArray(document);
        } catch (TransformerException exp) {
            throw new MALException("Error while converting the remaining xml document to byte array", exp);
        }
    }

    /**
     * removing decoded element node from the XML document.
     * This is required in case the decoder stops halfway and retrieve the rest of the encoded element.
     * Example: a body has 3 elements
     * <Body> <elem1 /> <elem2 /> <elem3 /></Body>
     *
     * assuming we are decoding elem1, stack has Body | elem1
     *
     * By calling this method, the assumption is that it has successfully decoded.
     * So <elem1 /> needs to be removed from the XML document.
     *
     * So pop the elem1 from the stack. Now stack has Body.
     * Body.removeCurrentChildNode() will remove <elem1 />.
     */
    private void popFromNodeStack() {
        currentNodeStack.pop();
        if (!currentNodeStack.isEmpty()) {
            currentNodeStack.peek().removeCurrentChildNode();
        }
    }
}