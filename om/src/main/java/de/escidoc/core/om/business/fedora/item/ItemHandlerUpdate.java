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
package de.escidoc.core.om.business.fedora.item;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.item.Component;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.item.ComponentPropertiesUpdateHandler;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.FoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.om.business.stax.handler.item.OneComponentContentHandler;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * Contains methods pertaining update of an item. Is extended at least by
 * FedoraItemHandler.
 * 
 * @author MSC
 * 
 */
public class ItemHandlerUpdate extends ItemHandlerDelete {

    private static AppLogger log = new AppLogger(
        ItemHandlerUpdate.class.getName());

    /**
     * Update the components of an item.
     * 
     * @param components
     *            The new set of components.
     * @param mdRecordsAttributes
     *            Map with XML attributes of md-record element.
     * @param nsUris
     *            Map with name space URIs
     * 
     * @throws ComponentNotFoundException
     *             If a component could not be found in the repository.
     * @throws InvalidStatusException
     *             If the operation pertaining a component is not valid because
     *             of its status.
     * @throws LockingException
     *             If the item is locked and the current user is not the one who
     *             locked it.
     * @throws FileNotFoundException
     *             If binary content can not be retrieved.
     * @throws MissingContentException
     *             If some required content is missing.
     * @throws InvalidContentException
     *             If invalid content is found.
     * @throws MissingElementValueException
     *             If an elements value is missing.
     * @throws ReadonlyAttributeViolationException
     *             If a read-only attribute is set.
     * @throws ReadonlyElementViolationException
     *             If a read-only element is set.
     * @throws XmlSchemaValidationException
     *             If xml schema validation fails.
     * @throws XmlCorruptedException
     *             If xml data is corrupt.
     * @throws SystemException
     *             Thrown in case of internal error.
     */
    protected void setComponents(
        final Map<String, Object> components,
        final HashMap<String, HashMap<String, HashMap<String, String>>> mdRecordsAttributes,
        final Map<String, String> nsUris) throws ComponentNotFoundException,
        LockingException, InvalidStatusException, SystemException,
        InvalidContentException, MissingContentException,
        FileNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, ReadonlyElementViolationException,
        ReadonlyAttributeViolationException, MissingElementValueException {

        // FIXME don't set but use getComponents()? (FRS)
        // What do you want to have fixed? I want to have setComponent() but I
        // still missing the hero how implements this! (SWA)
        // super.setComponents(TripleStoreUtility.getInstance().getComponents(getItem().getId()));

        // delete all components which are not in xmlData
        Iterator<String> componentIter = getItem().getComponentIds().iterator();
        Vector<String> delete = new Vector<String>();
        while (componentIter.hasNext()) {
            String componentId = componentIter.next();
            if (!components.containsKey(componentId)) {
                delete.add(componentId);
            }
        }
        componentIter = delete.iterator();
        while (componentIter.hasNext()) {
            String componentId = componentIter.next();
            getItem().deleteComponent(componentId);
        }

        // update
        Collection<ByteArrayOutputStream> newComponents =
            (Collection<ByteArrayOutputStream>) components.remove("new");
        componentIter = components.keySet().iterator();
        while (componentIter.hasNext()) {
            String componentId = componentIter.next();
            Component c = getItem().getComponent(componentId);

            setComponent(c, (HashMap) components.get(componentId),
                mdRecordsAttributes.get(componentId), nsUris.get(componentId));
        }

        // new
        if (newComponents.size() > 0) {
            Iterator<ByteArrayOutputStream> newCompIt =
                newComponents.iterator();
            while (newCompIt.hasNext()) {
                try {
                    String componentId =
                        createComponent((newCompIt.next())
                            .toString(XmlUtility.CHARACTER_ENCODING));
                    // addComponent((newCompIt.next())
                    // .toString(XmlUtility.CHARACTER_ENCODING));
                    getItem().addComponent(componentId);
                }
                catch (UnsupportedEncodingException e) {
                    throw new EncodingSystemException(e.getMessage(), e);
                }
            }
            getFedoraUtility().sync();
        }

    }

    /**
     * Set a component in the item.
     * 
     * @param c
     *            The Component.
     * @param streams
     *            The components datastreams.
     * @param mdRecordsMetadataAttribures
     *            Map with attributes of md-records XML element
     * @param nsUri
     *            Name space URI
     * @throws SystemException
     *             In case of an internal error.
     * @throws InvalidContentException
     *             If some invalid content is found in streams.
     * @throws FileNotFoundException
     *             If binary content can not be retrieved.
     * @throws MissingContentException
     *             If some required content is missing.
     * @throws ComponentNotFoundException
     *             Thrown if Component with provided id was not found.
     * 
     */
    protected void setComponent(
        final Component c,
        final Map streams,
        final HashMap<String, HashMap<String, String>> mdRecordsMetadataAttribures,
        final String nsUri) throws InvalidContentException, SystemException,
        MissingContentException, FileNotFoundException,
        ComponentNotFoundException {

        Map<String, String> properties = null;
        try {
            properties =
                c.setProperties(
                    ((ByteArrayOutputStream) streams.get("properties"))
                        .toString(XmlUtility.CHARACTER_ENCODING), getItem()
                        .getId());
        }
        catch (UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }
        String mimeType = properties.get(TripleStoreUtility.PROP_MIME_TYPE);
        if ((mimeType == null) || mimeType.length() == 0) {
            mimeType = FoXmlProvider.MIME_TYPE_APPLICATION_OCTET_STREAM;
        }
        String fileName =
            properties.get(de.escidoc.core.common.business.Constants.DC_NS_URI
                + Elements.ELEMENT_DC_TITLE);
        if ((fileName == null) || fileName.length() == 0) {
            fileName = "content of component " + c.getId();
        }
        try {
            setComponentContent(c,
                ((ByteArrayOutputStream) streams.get("content"))
                    .toString(XmlUtility.CHARACTER_ENCODING), mimeType,
                fileName);
        }
        catch (UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }
        HashMap<String, ByteArrayOutputStream> mdRecords = null;
        if (streams.get("md-records") == null) {
            mdRecords = new HashMap<String, ByteArrayOutputStream>();
        }
        else {
            mdRecords =
                (HashMap<String, ByteArrayOutputStream>) streams
                    .get("md-records");
        }

        setComponentMetadataRecords(c, mdRecords, mdRecordsMetadataAttribures,
            nsUri);

    }

    /**
     * Set md-records of Component.
     * 
     * @param c
     *            Component
     * @param mdMap
     *            Map with md-record output streams of Component.
     * @param mdAttributesMap
     *            Map with XML attributes of md-record XML element.
     * @param escidocMdRecordnsUri
     *            Name space URI
     * @throws SystemException
     *             Thrown in case of internal error.
     * @throws ComponentNotFoundException
     *             Thrown if Component with provided objid was not found.
     */
    private void setComponentMetadataRecords(
        final Component c, final HashMap<String, ByteArrayOutputStream> mdMap,
        final HashMap<String, HashMap<String, String>> mdAttributesMap,
        final String escidocMdRecordnsUri) throws SystemException,
        ComponentNotFoundException {

        HashMap<String, Datastream> dsMap = new HashMap<String, Datastream>();

        Iterator<String> mdIt = mdMap.keySet().iterator();
        while (mdIt.hasNext()) {
            String name = mdIt.next();
            ByteArrayOutputStream stream = mdMap.get(name);
            byte[] xmlBytes = stream.toByteArray();
            HashMap<String, String> mdProperties = null;
            if (name.equals("escidoc")) {
                mdProperties = new HashMap<String, String>();
                mdProperties.put("nsUri", escidocMdRecordnsUri);

            }
            Datastream ds =
                new Datastream(name, c.getId(), xmlBytes, "text/xml",
                    mdProperties);
            HashMap<String, String> mdRecordAttributes =
                mdAttributesMap.get(name);
            ds.addAlternateId(Datastream.METADATA_ALTERNATE_ID);
            ds.addAlternateId((String) mdRecordAttributes.get("type"));
            ds.addAlternateId((String) mdRecordAttributes.get("schema"));
            dsMap.put(name, ds);
        }
        c.setMdRecords(dsMap);
    }

    /**
     * Sets the properties datastream of the specified component.
     * 
     * @param id
     *            The unique identifier of the component.
     * @param xml
     *            The xml representation of the datastream.
     * 
     * @return The component properties in a map.
     * 
     * @throws InvalidContentException
     *             If xml data contains invalid content.
     * @throws SystemException
     *             Thrown in case of internal error.
     * @throws ComponentNotFoundException
     *             Thrown if Component with provided objid was not found.
     */
    protected Map<String, String> setComponentProperties(
        final String id, final String xml) throws InvalidContentException,
        ComponentNotFoundException, SystemException {

        Component component = getComponent(id);
        StaxParser sp = new StaxParser();
        ComponentPropertiesUpdateHandler cpuh =
            new ComponentPropertiesUpdateHandler(component, "/properties", sp);
        sp.addHandler(cpuh);
        try {
            sp.parse(xml);
        }
        catch (InvalidContentException e) {
            throw e;
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        Map<String, String> properties = cpuh.getProperties();
        properties.put(XmlTemplateProvider.CREATED_BY_ID, UserContext.getId());
        properties.put(XmlTemplateProvider.CREATED_BY_TITLE,
            UserContext.getRealName());
        try {
            Datastream newRelsExt =
                new Datastream(Datastream.RELS_EXT_DATASTREAM, id,
                    getComponentRelsExtWithVelocity(id, properties, false)
                        .getBytes(XmlUtility.CHARACTER_ENCODING),
                    FoXmlProvider.MIME_TYPE_TEXT_XML);
            component.setRelsExt(newRelsExt);
            component.persist();
        }
        catch (UnsupportedEncodingException e) {
            throw new XmlParserSystemException(
                "While building component RELS-EXT.", e);
        }
        return properties;
    }

    /**
     * Sets content type specific properties datastream of the item.
     * 
     * @param xml
     *            The xml representation of the content type specific
     *            properties.
     * @throws FedoraSystemException
     *             If Fedora reports an error.
     * @throws LockingException
     *             If the item is locked and the current user is not the one who
     *             locked it.
     * @throws WebserverSystemException
     *             In case of an internal error.
     * @throws TripleStoreSystemException
     *             If triple store reports an error.
     * @throws EncodingSystemException
     *             If encoding fails.
     * @throws IntegritySystemException
     *             If the integrity of the repository is violated.
     */
    protected void setContentTypeSpecificProperties(final String xml)
        throws FedoraSystemException, LockingException,
        WebserverSystemException, TripleStoreSystemException,
        EncodingSystemException, IntegritySystemException {
        try {
            Datastream oldDs = getItem().getCts();
            Datastream newDs =
                new Datastream(Elements.ELEMENT_CONTENT_MODEL_SPECIFIC,
                    getItem().getId(),
                    xml.getBytes(XmlUtility.CHARACTER_ENCODING), "text/xml");

            if (oldDs == null || !oldDs.equals(newDs)) {
                getItem().setCts(newDs);
            }

        }
        catch (UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }
    }

    /**
     * /** Set the content of a component.
     * 
     * @param component
     *            The Component.
     * @param xml
     *            The xml representation of the new content.
     * @param fileName
     *            The file name.
     * @param mimeType
     *            the mime type.
     * @throws MissingContentException
     *             If some required content of xml data is missing.
     * @throws InvalidContentException
     *             If some invalid content is found in xml data.
     * @throws FileNotFoundException
     *             If binary content can not be retrieved.
     * @throws SystemException
     *             Thrown in case of internal error.
     * @throws ComponentNotFoundException
     *             Thrown if Component with provided objid was not found.
     */
    protected void setComponentContent(
        final Component component, final String xml, final String fileName,
        final String mimeType) throws MissingContentException,
        InvalidContentException, FileNotFoundException,
        ComponentNotFoundException, SystemException {

        StaxParser sp = new StaxParser();

        OneComponentContentHandler occh =
            new OneComponentContentHandler(sp, "/content");
        sp.addHandler(occh);
        try {
            sp.parse(xml);
        }
        catch (MissingContentException e) {
            throw e;
        }
        catch (Exception e) {
            throw new WebserverSystemException(e);
        }

        HashMap<String, String> componentBinary = occh.getComponentBinary();
        // load url or binary to fedora
        if (componentBinary.get("content") == null) {
            // ingest by URL
            String url = (String) componentBinary.get("uploadUrl");
            // process URL
            // - local repository URL ("/ir/item/...")
            // - local staging URL ("/st/...")
            // - local repository URL to this
            // - full qualified URL to this
            url = Utility.processUrl(url, getItem().getId(), component.getId());

            if (url == null) {
                // it's the local url we send
                if (log.isDebugEnabled()) {
                    log.debug("Do not update content of " + component.getId()
                        + ". URL[" + url + "]");
                }
                return;
            }
            else {
                // update content and check by checksum if it is really changed
                // and in case remove content PID if exists
                String contentChecksum = component.getChecksum();

                try {
                    getFedoraUtility().modifyDatastream(component.getId(),
                        "content", null, null, null, url, true);
                }
                catch (FedoraSystemException e) {
                    handleFedoraUploadError(url, e);
                }

                // component object is not in sync with Fedora after modifying
                // datastream. So get new checksum from Fedora directly.
                String newContentChecksum =
                    getFedoraUtility().getDatastreamInformation(
                        component.getId(), "content", null).getChecksum();

                if (!contentChecksum.equals(newContentChecksum)) {
                    // remove Content PID
                    component.removeObjectPid();
                }
            }
        }
        else {
            Datastream content = component.getContent();
            if (content.getControlGroup().equals(FoXmlProvider.CONTROL_GROUP_E)
                || content.getControlGroup().equals(
                    FoXmlProvider.CONTROL_GROUP_R)) {
                String message =
                    "A binary content of the component " + component.getId()
                        + " has to be referenced by a URL, "
                        + "because the attribute 'storage' of the section"
                        + " 'content' was set to 'external-url' or "
                        + "'external-managed' while create.";
                log.error(message);
                throw new InvalidContentException(message);
            }
            String url =
                uploadBase64EncodedContent(
                    (String) componentBinary.get("content"), fileName, mimeType);
            try {
                getFedoraUtility().modifyDatastream(component.getId(),
                    "content", null, null, null, url, true);
            }
            catch (FedoraSystemException e) {
                handleFedoraUploadError(url, e);
            }
        }
        component.notifySetContent();
    }
}
