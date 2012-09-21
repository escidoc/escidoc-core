package de.escidoc.core.common.util.stax.handler;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.namespace.QName;
import java.util.*;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Configurable
public class ParentsHandler extends DefaultHandler {

    private final Map<String, String> parents = new HashMap<String, String>();

    private final List<String> validResourceTypes;

    private final StaxParser parser;

    private final QName expectedRootElement;

    private boolean expectedRootElementParsed = false;

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    /**
     *
     * @param parser The parser.
     * @param validResourceTypes The valid types of the parents. For example: Constants.CONTAINER_OBJECT_TYPE.
     *                           Set to <tt>null</tt> to allow any type.
     * @see Constants
     */
    public ParentsHandler(final StaxParser parser, final QName expectedRootElement,
        final List<String> validResourceTypes) {
        this.parser = parser;
        this.expectedRootElement = expectedRootElement;
        this.validResourceTypes = validResourceTypes;
    }

    /**
     *
     * @param parser The parser.
     * @param validResourceType The valid types of the parents. For example: Constants.CONTAINER_OBJECT_TYPE.
     *                          Set to <tt>null</tt> to allow any type.
     * @see Constants
     */
    public ParentsHandler(final StaxParser parser, final QName expectedRootElement, final String validResourceType) {
        this.parser = parser;
        this.expectedRootElement = expectedRootElement;

        if (validResourceType != null) {
            final List<String> list = new ArrayList<String>();
            list.add(validResourceType);
            this.validResourceTypes = list;
        }
        else {
            this.validResourceTypes = null;
        }
    }

    /**
     *
     * @param parser The parser.
     * @param expectedRootElement The expected root element
     */
    public ParentsHandler(final StaxParser parser, final QName expectedRootElement) {
        this.parser = parser;
        this.expectedRootElement = expectedRootElement;
        this.validResourceTypes = null;
    }

    /**
     *
     * @param parser The parser.
     */
    public ParentsHandler(final StaxParser parser) {
        this(parser, null);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public StartElement startElement(final StartElement element) throws MissingAttributeValueException,
        IntegritySystemException, ResourceNotFoundException, TripleStoreSystemException, XmlCorruptedException {

        if (expectedRootElement != null && !expectedRootElementParsed) {
            if (element.getNamespace().equals(expectedRootElement.getNamespaceURI())
                && element.getLocalName().equals(expectedRootElement.getLocalPart())) {
                expectedRootElementParsed = true;
            }
            else {
                throw new XmlCorruptedException("Invalid root element {" + element.getNamespace() + "}"
                    + element.getLocalName() + ". Expected {" + this.expectedRootElement.getNamespaceURI() + "}"
                    + this.expectedRootElement.getLocalPart() + ".");
            }
        }
        if (element.getLocalName().equals(XmlUtility.NAME_PARENT)
            && element.getNamespace().equals(Constants.STRUCTURAL_RELATIONS_NS_URI)) {

            Map.Entry<String, String> entry = checkParentRef(element);
            parents.put(entry.getKey(), entry.getValue());
        }
        return element;
    }

    /**
     * @return A list of all parent IDs.
     */
    public List<String> getParentsAsList() {
        return new ArrayList<String>(this.parents.keySet());
    }

    /**
     * @return A map containing the parent IDs as the keys and the resource types as the values.
     */
    public Map<String, String> getParents() {
        return this.parents;
    }

    /**
     * Check the given element if it contains a valid reference to another resource.
     *
     * @param element The element.
     * @return The id of the referenced resource.
     * @throws MissingAttributeValueException If the href is not found.
     * @throws ResourceNotFoundException If the id does not point to an organizational unit.
     * @throws TripleStoreSystemException
     * @throws IntegritySystemException
     */
    protected Map.Entry<String, String> checkParentRef(final StartElement element)
        throws MissingAttributeValueException, TripleStoreSystemException, IntegritySystemException,
        ResourceNotFoundException {

        String id;
        try {
            id = XmlUtility.getIdFromURI(element.getAttribute(Constants.XLINK_URI, "href").getValue());
        }
        catch (final NoSuchAttributeException e) {
            try {
                id = element.getAttribute(null, "objid").getValue();
            }
            catch (final NoSuchAttributeException e1) {
                throw new MissingAttributeValueException("Parent attribute 'href' or 'objid' has to be set! ", e1);
            }
        }
        final String idWithoutVersionNumber = XmlUtility.getObjidWithoutVersion(id);

        if (this.tripleStoreUtility.exists(idWithoutVersionNumber)) {
            String objType = this.tripleStoreUtility.getObjectType(idWithoutVersionNumber);

            if (this.validResourceTypes != null && !this.validResourceTypes.isEmpty()) {
                if (this.validResourceTypes.contains(objType)) {
                    return new AbstractMap.SimpleEntry<String, String>(idWithoutVersionNumber, objType);
                }
                else {
                    throw new ResourceNotFoundException("Reference to parent of type '" + objType + "' is not valid!");
                }
            }
            else {
                return new AbstractMap.SimpleEntry<String, String>(idWithoutVersionNumber, objType);
            }
        }
        else {
            throw new ResourceNotFoundException("Object with id " + idWithoutVersionNumber + " does not exist!");
        }
    }
}