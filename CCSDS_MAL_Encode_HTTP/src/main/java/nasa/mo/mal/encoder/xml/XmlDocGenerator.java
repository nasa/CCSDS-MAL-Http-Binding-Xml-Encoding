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
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Wai Phyo
 *         Created on 5/3/17.
 * singleton class generating XML Document and elements
 * TODO verify if it can work for multi-thread programming.
 */
class XmlDocGenerator {
    /**
     * Since this class cannot throw exceptions, all excpetions are written to LOG
     * TODO is it sufficient?
     */
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(XmlDocGenerator.class.toString());
    /**
     * Basic namespaces and links from whitebook
     * TODO will there be different namespaces for different message types?
     */
    private static final String XMLNS_XSI = "xmlns:xsi";
    private Document document;
    private static final String XML_VERSION = "1.0";
    private static final String MAL_XML = "xmlns:malxml";
    private static final String NULLABLE_ATTRIBUTE_VALUE = "true";
    private static final String MAL_XML_NAMESPACE_URL = "http://www.ccsds.org/schema/malxml/MAL";
    private static final String XML_XSI_NAMESPACE_URL = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XML_NAMESPACE = "http://www.w3.org/2000/xmlns/";

    /**
     * Constructor.
     * Create an empty XML document by WhiteBook Specification
     */
    XmlDocGenerator() {
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            document.setXmlVersion(XML_VERSION);
            document.setXmlStandalone(true);
        } catch (ParserConfigurationException exp) {
            LOGGER.log(Level.SEVERE,
                    "ParserConfigurationException while creating XML Document for XmlDocGenerator {0}", exp);
        }
    }

    /**
     * get method for document
     */
    Document getDocument() {
        return document;
    }

    /**
     * used by several encoder when adding a null attribute
     * @return new Null Attribute with preset name & value
     */
    Attr getNewNullAttribute() {
        Attr nullAttr = document.createAttribute(Constants.NULLABLE_ATTRIBUTE_NAME);
        nullAttr.setValue(NULLABLE_ATTRIBUTE_VALUE);
        return nullAttr;
    }

    /**
     * used by several encoder when adding a null attribute
     * @return new MALXML type Attribute with preset name & value
     */
    Attr getNewTypeAttribute(long type) {
        Attr typeAttribute = document.createAttribute(Constants.MALXML_TYPE_ATTRIBUTE_NAME);
        typeAttribute.setValue(Long.valueOf(type).toString());
        return typeAttribute;
    }

    /**
     * generate new Simple DOM element
     * @param name name of element
     * @param value value of element
     * @return new DOM element
     */
    Element createSimpleElementWithValue(String name, String value) {
        Element element = document.createElement(name);
        element.appendChild(document.createTextNode(value));
        return element;
    }

    /**
     * @param document Encoded XML Document
     * @return XML in byte array
     */
    public byte[] toByteArray(final Document document) {
        if (document == null) {
            LOGGER.log(Level.WARNING, "Writing null Xml Doc to Byte Array");
            return null;
        }
        try {
            return xmlDocToByteArray(document);
        } catch (TransformerException transformerException) {
            LOGGER.log(Level.SEVERE, "TransformerException while converting XML Document to byte array {0}",
                    transformerException);
            return null;
        }
    }

    /**
     * @param document Encoded XML Document
     * @return XML in String form.
     */
    String toString(final Document document) {
        if (document == null) {
            LOGGER.log(Level.WARNING, "Writing null Xml Doc to String");
            return null;
        }
        try {
            StringWriter writer = new StringWriter();
            TransformerFactory.newInstance().newTransformer()
                    .transform(new DOMSource(document), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException transformerException) {
            LOGGER.log(Level.SEVERE, "TransformerException while converting XML Document to String {0}",
                    transformerException);
            return null;
        }
    }

    /**
     * Generate Root Element which has 2 name spaces.
     * TODO check namespaces are valid and correct.
     * TODO different namespaces for different bodies?
     * @return Root Element
     */
    Element createRootElement() {
        org.w3c.dom.Element rootElement = document.createElement(Constants.ROOT_ELEMENT);
        rootElement.setAttributeNS(XML_NAMESPACE, XMLNS_XSI, XML_XSI_NAMESPACE_URL);
        rootElement.setAttributeNS(XML_NAMESPACE, MAL_XML, MAL_XML_NAMESPACE_URL);
        return rootElement;
    }

    /**
     * Create new Element with the given name.
     * Add null attribute and set to true.
     * @param name Element or Node Name
     * @return new Empty Element with null attribute.
     */
    Element createEmptyElementWithNullAttr(String name) {
        Element element = document.createElement(name);
        element.setAttributeNode(getNewNullAttribute());
        return element;
    }

    /**
     * Helper method to convert document to byte array
     * @param document
     * @return
     * @throws TransformerException
     */
    static byte[] xmlDocToByteArray(final Document document) throws TransformerException {
        if (document == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TransformerFactory.newInstance().newTransformer()
                .transform(new DOMSource(document), new StreamResult(outputStream));
        return outputStream.toByteArray();
    }
}
