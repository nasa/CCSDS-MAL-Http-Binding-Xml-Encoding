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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Wai Phyo
 *         Created on 5/12/17.
 *
 * This is a replica of NodeListIterator from apache commons.
 * This is created since the original class does not have peek method.
 * Extending it is not possible since the index field is private and not accessible.
 */
public class XmlNodeListIterator implements Iterator<Node> {
    private final NodeList nodeList;
    private int index = 0;

    /**
     * Copied constructor of org.apache.commons.collections4.iterators.NodeListIterator
     * @param baseNode DOM Node from XML document
     */
    public XmlNodeListIterator(Node baseNode) {
        if(baseNode == null) {
            throw new NullPointerException("Node must not be null.");
        } else {
            this.nodeList = baseNode.getChildNodes();
        }
    }

    /**
     * Copied constructor of org.apache.commons.collections4.iterators.NodeListIterator
     * @param nodeList DOM Node List from XML document
     */
    public XmlNodeListIterator(NodeList nodeList) {
        if(nodeList == null) {
            throw new NullPointerException("NodeList must not be null.");
        } else {
            this.nodeList = nodeList;
        }
    }

    /**
     * Creating a new instance of XmlNodeListIterator
     *
     * @param node DOM Node from XML Document
     * @return XmlNodeListIterator
     */
    public static XmlNodeListIterator createXmlNodeListIterator(Node node) {
        return new XmlNodeListIterator(node);
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * copied from org.apache.commons.collections4.iterators.NodeListIterator
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        return this.nodeList != null && this.index < this.nodeList.getLength();
    }

    /**
     * Returns the next element in the iteration.
     * copied from org.apache.commons.collections4.iterators.NodeListIterator
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public Node next() {
        if(this.nodeList != null && this.index < this.nodeList.getLength()) {
            return this.nodeList.item(this.index++);
        } else {
            throw new NoSuchElementException("underlying nodeList has no more elements");
        }
    }

    /**
     * Extra function. This is the reason this iterator is recreated.
     * Returning the next element without incrementing the index.
     * This API is required when user only wants to check some attribute, but not ready to process yet.
     *
     * @return Node at current Index.
     */
    public Node peek() {
        if(this.nodeList != null && this.index < this.nodeList.getLength()) {
            return this.nodeList.item(this.index);
        } else {
            throw new NoSuchElementException("underlying nodeList has no more elements");
        }
    }

    /**
     * Removing current Child node from the parent node.
     * index needs to be updated.
     *
     * Example: a body has 3 elements
     * <Body> <elem1 /> <elem2 /> <elem3 /></Body>
     *
     * Current Iterator is the Body.
     * valid indexes are 0, 1, 2
     *
     * if it is 0, iterator hasn't started looping. do nothing.
     * if it is 4 and above (not possible), invalid index & do nothing.
     *
     * if it is 1 or 2 or 3, valid index.
     *
     * example: index is 1
     * It means the caller has elem1. and wants to remove elem1 from XML document.
     *
     * 1. So retrieve the same node by reducing the index to 0.
     * 2. get the parent of elem1 which is Body
     * 3. remove the child named elem1.
     *
     * But the index remained at 0 since elem1 is removed, elem2's index is 0.
     * Hence, no need to readjust the index.
     */
    public void removeCurrentChildNode() {
        if (index > 0 && index <= this.nodeList.getLength()) {
            Node removingNode = nodeList.item(--index);
            removingNode.getParentNode().removeChild(removingNode);
        }
    }
}
