package org.escidoc.core.utils.xml;

import org.escidoc.core.utils.io.Stream;
import org.w3c.dom.*;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public final class StreamElement implements Element, StreamSupport {
    
    private final Stream stream;
    
    public StreamElement(final Stream stream) {
        this.stream = stream;
    }

    @Override public Stream getStream() {
        return this.stream;
    }
    
    @Override public String getTagName() {
        throw new UnsupportedOperationException();
    }

    @Override public String getAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    @Override public void setAttribute(String name, String value) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public void removeAttribute(String name) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public Attr getAttributeNode(String name) {
        throw new UnsupportedOperationException();
    }

    @Override public Attr setAttributeNode(Attr newAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public NodeList getElementsByTagName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public boolean hasAttribute(String name) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public TypeInfo getSchemaTypeInfo() {
        throw new UnsupportedOperationException();
    }

    @Override public void setIdAttribute(String name, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public String getNodeName() {
        throw new UnsupportedOperationException();
    }

    @Override public String getNodeValue() throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public void setNodeValue(String nodeValue) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public short getNodeType() {
        throw new UnsupportedOperationException();
    }

    @Override public Node getParentNode() {
        throw new UnsupportedOperationException();
    }

    @Override public NodeList getChildNodes() {
        throw new UnsupportedOperationException();
    }

    @Override public Node getFirstChild() {
        throw new UnsupportedOperationException();
    }

    @Override public Node getLastChild() {
        throw new UnsupportedOperationException();
    }

    @Override public Node getPreviousSibling() {
        throw new UnsupportedOperationException();
    }

    @Override public Node getNextSibling() {
        throw new UnsupportedOperationException();
    }

    @Override public NamedNodeMap getAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override public Document getOwnerDocument() {
        throw new UnsupportedOperationException();
    }

    @Override public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public Node removeChild(Node oldChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public Node appendChild(Node newChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public boolean hasChildNodes() {
        throw new UnsupportedOperationException();
    }

    @Override public Node cloneNode(boolean deep) {
        throw new UnsupportedOperationException();
    }

    @Override public void normalize() {
        throw new UnsupportedOperationException();
    }

    @Override public boolean isSupported(String feature, String version) {
        throw new UnsupportedOperationException();
    }

    @Override public String getNamespaceURI() {
        throw new UnsupportedOperationException();
    }

    @Override public String getPrefix() {
        throw new UnsupportedOperationException();
    }

    @Override public void setPrefix(String prefix) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public String getLocalName() {
        throw new UnsupportedOperationException();
    }

    @Override public boolean hasAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override public String getBaseURI() {
        throw new UnsupportedOperationException();
    }

    @Override public short compareDocumentPosition(Node other) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public String getTextContent() throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public void setTextContent(String textContent) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override public boolean isSameNode(Node other) {
        throw new UnsupportedOperationException();
    }

    @Override public String lookupPrefix(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean isDefaultNamespace(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    @Override public String lookupNamespaceURI(String prefix) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean isEqualNode(Node arg) {
        throw new UnsupportedOperationException();
    }

    @Override public Object getFeature(String feature, String version) {
        throw new UnsupportedOperationException();
    }

    @Override public Object setUserData(String key, Object data, UserDataHandler handler) {
        throw new UnsupportedOperationException();
    }

    @Override public Object getUserData(String key) {
        throw new UnsupportedOperationException();
    }
}
