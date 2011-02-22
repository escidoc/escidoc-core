/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.business.fedora;

import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.GenericResource;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.PidAlreadyAssignedException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.MultipleExtractor;
import de.escidoc.core.common.util.xml.XmlUtility;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author FRS
 * 
 */
public abstract class AbstractResourceHandler extends HandlerBase {

    private GenericResource theResource;

    protected void setResource(final String id) throws ResourceNotFoundException, SystemException {
        theResource = new GenericResource(id);
    }

    public String retrieve(final String id) throws ResourceNotFoundException,
        SystemException {
        setResource(id);

        String xml = "";
        try {
            xml += getDatastream();
        }
        catch (StreamNotFoundException e) {
            throw new ResourceNotFoundException(e);
        }

        return xml;
    }

    protected String getDatastream() throws StreamNotFoundException,
        FedoraSystemException, EncodingSystemException, WebserverSystemException {
        Datastream datastream = theResource.getDatastream();
        String xml;
        try {
            xml =
                new String(datastream.getStream(),
                    XmlUtility.CHARACTER_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new EncodingSystemException("AbstractResourceHandler: ", e);
        }
        return xml;
    }

    public String update(final String id, final String xmlData)
        throws LockingException, ResourceNotFoundException,
        OptimisticLockingException, SystemException {
        setResource(id);

        if (theResource.isLocked()) {
            throw new LockingException("Resource + " + theResource.getId()
                + " is locked.");
        }

        String updatedXmlData = null;

        try {

            StaxParser sp = new StaxParser();

            // handler to check last modified date
            // DO NOT CHECK LAST MODIFIED DATE UNLESS ADMIN-DESCRIPTOR IS A
            // STANDALONE RESOURCE
            // OptimisticLockingHandler olh = new OptimisticLockingHandler(
            // theResource.getId(), theResource.getLastModifiedDate(), sp);
            // sp.addHandler(olh);

            // handler to check and modify the xml
            sp.addHandler(getResourceHandler(sp));

            // handler to extract datastreams from xml
            HashMap<String, String> extractPathes =
                new HashMap<String, String>();
            extractPathes.put(getRootElementPath(), null);
            MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
            sp.addHandler(me);

            sp.parse(xmlData);

            Map<String, Object> streams = me.getOutputStreams();

            setDatastream(streams
                .get(getRootElement()).toString());

            updatedXmlData = retrieve(theResource.getId());

        }
        catch (LockingException le) {
            throw le;
        }
        catch (XMLStreamException xse) {
            throw new SystemException(xse);
        }
        catch (ReadonlyAttributeViolationException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (TripleStoreSystemException e) {
            throw e;
        }
        catch (OptimisticLockingException e) {
            throw e;
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        return updatedXmlData;
    }

    private void setDatastream(final String xmlData) throws LockingException,
        StreamNotFoundException, SystemException {
        try {
            Datastream oldDs = theResource.getDatastream();
            byte[] xmlBytes;
            xmlBytes = xmlData.getBytes(XmlUtility.CHARACTER_ENCODING);
            Datastream newDs =
                new Datastream("datastream", theResource.getId(), xmlBytes,
                    "text/xml");

            if (!oldDs.equals(newDs)) {
                // DO some checks etc.
                theResource.setDatastream(newDs);
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }
    }

    public String create(String xmlData) throws XmlSchemaValidationException,
        InvalidXmlException, SystemException {

        String createdXml;

        try {
            StaxParser sp = new StaxParser();

            final String id = getIdProvider().getNextPid();
            // handler to set id
            xmlData = xmlData.replaceAll("objid=\"\"", "objid=\"" + id + '\"');
            xmlData =
                xmlData.replaceAll("xlink:href=\"\"", "xlink:href=\""
                    + getFirstPathPart() + getRootElement() + '/' + id + '\"');
            // handler to extract properties ?

            // handler to extract datastreams from xml
            HashMap<String, String> extractPathes =
                new HashMap<String, String>();
            extractPathes.put(getRootElementPath(), null);
            MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
            sp.addHandler(me);

            try {
                sp.parse(xmlData);
            }
            catch (ContentModelNotFoundException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (ContextNotFoundException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (MissingContentException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }

            catch (LockingException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (ReadonlyElementViolationException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (MissingAttributeValueException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (MissingElementValueException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (ReadonlyAttributeViolationException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (TripleStoreSystemException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (InvalidContentException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (InvalidStatusException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (ContentRelationNotFoundException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (ReferencedResourceNotFoundException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (RelationPredicateNotFoundException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (OptimisticLockingException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (AlreadyExistsException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (OrganizationalUnitNotFoundException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (PidAlreadyAssignedException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            catch (MissingMdRecordException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }

            catch (TmeException e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            Map streams = me.getOutputStreams();

            String label = getRootElement();

            // create FOXML for ingest
            String foXml =
                "<foxml:digitalObject VERSION=\"1.1\" PID=\""
                    + id
                    + "\" "
                    + "fedoraxsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd\" "
                    + "xmlns:audit=\"info:fedora/fedora-system:def/audit#\" xmlns:fedoraxsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\"> "
                    + "<foxml:objectProperties>"
                    + "<foxml:property NAME=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\" VALUE=\"FedoraObject\"/>"
                    + "<foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"Active\"/>"
                    + "<foxml:property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\""
                    + label + "\"/>" + "</foxml:objectProperties>";

            foXml +=
                "<foxml:datastream CONTROL_GROUP=\"X\" ID=\"datastream\" STATE=\"A\" VERSIONABLE=\"true\">"
                    + "<foxml:datastreamVersion ALT_IDS=\"\" ID=\"datastream.0\" LABEL=\"datastream\" MIMETYPE=\"text/xml\">"
                    + "<foxml:xmlContent>";

            ByteArrayOutputStream datastreamXml =
                (ByteArrayOutputStream) streams.get(getRootElement());
            foXml += datastreamXml.toString();

            foXml +=
                "</foxml:xmlContent></foxml:datastreamVersion></foxml:datastream>";

            foXml += "</foxml:digitalObject>";

            getFedoraUtility().storeObjectInFedora(foXml, true);

            try {
                createdXml = retrieve(id);
            }
            catch (ResourceNotFoundException e) {
                throw new IntegritySystemException(e);
            }
        }
        catch (XMLStreamException xse) {
            throw new SystemException(xse);
        }

        return createdXml;
    }

    public void delete(final String id) throws LockingException,
        ResourceNotFoundException, SystemException {
        setResource(id);

        if (theResource.isLocked()) {
            throw new LockingException("Resource + " + theResource.getId()
                + " is locked.");
        }

        getFedoraUtility().deleteObject(theResource.getId(), true);

    }

    // TODO should be defined in an utility
    protected void validate(final String xmlData)
        throws XmlSchemaValidationException, WebserverSystemException {

        try {
            XmlUtility.validate(xmlData, getXMLSchemaUrl(xmlData));
        }
        // FIXME: throw XmlCorruptedException?
        catch (XmlCorruptedException e) {
            throw new XmlSchemaValidationException(e.getMessage(), e);
        }
    }

    /**
     * @return "/resource-name"
     */
    protected abstract String getRootElementPath();

    protected abstract String getFirstPathPart();

    protected abstract String getRootElement();

    /**
     * @return A List of DefaultHandler to check/modify this resource.
     */
    protected abstract List getResourceHandler(StaxParser parser);

    /**
     * @return "http://localhost:8080/xsd/XXX.xsd"
     * @throws WebserverSystemException
     *             Thrown if schema location cannot be determined.
     */
    protected abstract String getXMLSchemaUrl() throws WebserverSystemException;

    /**
     * Determine the schema location from dataset.
     * 
     * @return "http://localhost:8080/xsd/[rest|soap]?/[version]/XXX.xsd"
     * @throws WebserverSystemException
     *             Thrown if schema location cannot be determined.
     */
    protected String getXMLSchemaUrl(final String xmlData)
        throws WebserverSystemException {
        return getXMLSchemaUrl();
    }

}
