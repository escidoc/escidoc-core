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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import org.escidoc.core.services.fedora.FedoraServiceClient;
import org.escidoc.core.services.fedora.ModifiyDatastreamPathParam;
import org.escidoc.core.services.fedora.ModifyDatastreamQueryParam;
import org.escidoc.core.services.fedora.management.DatastreamProfileTO;
import org.esidoc.core.utils.io.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.item.Component;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.item.ComponentPropertiesUpdateHandler;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.FoXmlProviderConstants;
import de.escidoc.core.om.business.stax.handler.item.OneComponentContentHandler;

/**
 * Contains methods pertaining update of an item. Is extended at least by FedoraItemHandler.
 *
 * @author Michael Schneider
 */
public class ItemHandlerUpdate extends ItemHandlerDelete {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemHandlerUpdate.class);

    @Autowired
    private FedoraServiceClient fedoraServiceClient;

    /**
     * Update the components of an item.
     *
     * @param components          The new set of components.
     * @param mdRecordsAttributes Map with XML attributes of md-record element.
     * @param nsUris              Map with name space URIs
     * @throws ComponentNotFoundException   If a component could not be found in the repository.
     * @throws FileNotFoundException        If binary content can not be retrieved.
     * @throws MissingContentException      If some required content is missing.
     * @throws InvalidContentException      If invalid content is found.
     * @throws MissingElementValueException If an elements value is missing.
     * @throws ReadonlyAttributeViolationException
     *                                      If a read-only attribute is set.
     * @throws ReadonlyElementViolationException
     *                                      If a read-only element is set.
     * @throws SystemException              Thrown in case of internal error.
     */
    protected void setComponents(
        final Map<String, Object> components, final Map<String, Map<String, Map<String, String>>> mdRecordsAttributes,
        final Map<String, String> nsUris) throws ComponentNotFoundException, SystemException, InvalidContentException,
        MissingContentException, FileNotFoundException, ReadonlyElementViolationException,
        ReadonlyAttributeViolationException, MissingElementValueException {

        // FIXME don't set but use getComponents()? (FRS)
        // What do you want to have fixed? I want to have setComponent() but I
        // still missing the hero how implements this! (SWA)
        // super.setComponents(TripleStoreUtility.getInstance().getComponents(getItem().getId()));

        // delete all components which are not in xmlData
        Iterator<String> componentIter = getItem().getComponentIds().iterator();
        final List<String> delete = new ArrayList<String>();
        while (componentIter.hasNext()) {
            final String componentId = componentIter.next();
            if (!components.containsKey(componentId)) {
                delete.add(componentId);
            }
        }
        componentIter = delete.iterator();
        while (componentIter.hasNext()) {
            final String componentId = componentIter.next();
            getItem().deleteComponent(componentId);
        }

        // update
        final Collection<ByteArrayOutputStream> newComponents =
            (Collection<ByteArrayOutputStream>) components.remove("new");

        for (final Entry<String, Object> e : components.entrySet()) {
            final String componentId = e.getKey();
            final Component c = getItem().getComponent(componentId);

            setComponent(c, (Map) e.getValue(), mdRecordsAttributes.get(componentId), nsUris.get(componentId));
        }

        // new
        if (!newComponents.isEmpty()) {
            for (final ByteArrayOutputStream newComponent : newComponents) {
                try {
                    final String componentId = createComponent(newComponent.toString(XmlUtility.CHARACTER_ENCODING));
                    getItem().addComponent(componentId);
                }
                catch (final UnsupportedEncodingException e) {
                    throw new EncodingSystemException(e.getMessage(), e);
                }
            }
            this.getFedoraServiceClient().sync();
            try {
                this.getTripleStoreUtility().reinitialize();
            }
            catch (TripleStoreSystemException e) {
                throw new FedoraSystemException("Error on reinitializing triple store.", e);
            }
        }

    }

    /**
     * Set a component in the item.
     *
     * @param c                           The Component.
     * @param streams                     The components datastreams.
     * @param mdRecordsMetadataAttribures Map with attributes of md-records XML element
     * @param nsUri                       Name space URI
     * @throws InvalidContentException    If some invalid content is found in streams.
     * @throws FileNotFoundException      If binary content can not be retrieved.
     * @throws MissingContentException    If some required content is missing.
     * @throws ComponentNotFoundException Thrown if Component with provided id was not found.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.XmlParserSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    protected void setComponent(
        final Component c, final Map streams, final Map<String, Map<String, String>> mdRecordsMetadataAttribures,
        final String nsUri) throws InvalidContentException, MissingContentException, FileNotFoundException,
        EncodingSystemException, FedoraSystemException, TripleStoreSystemException, XmlParserSystemException,
        WebserverSystemException, IntegritySystemException {

        final Map<String, String> properties;
        try {
            properties =
                c.setProperties(((ByteArrayOutputStream) streams.get("properties"))
                    .toString(XmlUtility.CHARACTER_ENCODING));
        }
        catch (final UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }
        String mimeType = properties.get(TripleStoreUtility.PROP_MIME_TYPE);
        if (mimeType == null || mimeType.length() == 0) {
            mimeType = FoXmlProviderConstants.MIME_TYPE_APPLICATION_OCTET_STREAM;
        }
        String fileName = properties.get(Constants.DC_NS_URI + Elements.ELEMENT_DC_TITLE);
        if (fileName == null || fileName.length() == 0) {
            fileName = "content of component " + c.getId();
        }
        try {
            setComponentContent(c, ((ByteArrayOutputStream) streams.get("content"))
                .toString(XmlUtility.CHARACTER_ENCODING), mimeType, fileName);
        }
        catch (final UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }
        final Map<String, ByteArrayOutputStream> mdRecords =
            streams.get(XmlUtility.NAME_MDRECORDS) == null ? new HashMap<String, ByteArrayOutputStream>() : (Map<String, ByteArrayOutputStream>) streams
                .get(XmlUtility.NAME_MDRECORDS);

        setComponentMetadataRecords(c, mdRecords, mdRecordsMetadataAttribures, nsUri);

    }

    /**
     * Set md-records of Component.
     *
     * @param c                    Component
     * @param mdMap                Map with md-record output streams of Component.
     * @param mdAttributesMap      Map with XML attributes of md-record XML element.
     * @param escidocMdRecordnsUri Name space URI
     * @throws ComponentNotFoundException Thrown if Component with provided objid was not found.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.XmlParserSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    private static void setComponentMetadataRecords(
        final Component c, final Map<String, ByteArrayOutputStream> mdMap,
        final Map<String, Map<String, String>> mdAttributesMap, final String escidocMdRecordnsUri)
        throws EncodingSystemException, IntegritySystemException, FedoraSystemException, WebserverSystemException,
        TripleStoreSystemException, XmlParserSystemException {

        final Map<String, Datastream> dsMap = new HashMap<String, Datastream>();
        for (final Entry<String, ByteArrayOutputStream> stringByteArrayOutputStreamEntry : mdMap.entrySet()) {
            final ByteArrayOutputStream stream = stringByteArrayOutputStreamEntry.getValue();
            final byte[] xmlBytes = stream.toByteArray();
            HashMap<String, String> mdProperties = null;
            if ("escidoc".equals(stringByteArrayOutputStreamEntry.getKey())) {
                mdProperties = new HashMap<String, String>();
                mdProperties.put("nsUri", escidocMdRecordnsUri);
            }
            final Datastream ds =
                new Datastream(stringByteArrayOutputStreamEntry.getKey(), c.getId(), xmlBytes, MimeTypes.TEXT_XML,
                    mdProperties);
            final Map<String, String> mdRecordAttributes =
                mdAttributesMap.get(stringByteArrayOutputStreamEntry.getKey());
            ds.addAlternateId(Datastream.METADATA_ALTERNATE_ID);
            ds.addAlternateId(mdRecordAttributes.get("type"));
            ds.addAlternateId(mdRecordAttributes.get("schema"));
            dsMap.put(stringByteArrayOutputStreamEntry.getKey(), ds);
        }
        c.setMdRecords(dsMap);
    }

    /**
     * Sets the properties datastream of the specified component.
     *
     * @param id  The unique identifier of the component.
     * @param xml The xml representation of the datastream.
     * @return The component properties in a map.
     * @throws InvalidContentException    If xml data contains invalid content.
     * @throws ComponentNotFoundException Thrown if Component with provided objid was not found.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.XmlParserSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    protected Map<String, String> setComponentProperties(final String id, final String xml)
        throws InvalidContentException, ComponentNotFoundException, FedoraSystemException, TripleStoreSystemException,
        XmlParserSystemException, WebserverSystemException, IntegritySystemException {

        final Component component = getComponent(id);
        final StaxParser sp = new StaxParser();
        final ComponentPropertiesUpdateHandler cpuh =
            new ComponentPropertiesUpdateHandler(component, "/properties", sp);
        sp.addHandler(cpuh);
        try {
            sp.parse(xml);
        }
        catch (final InvalidContentException e) {
            throw e;
        }
        catch (final Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        final Map<String, String> properties = cpuh.getProperties();
        properties.put(XmlTemplateProviderConstants.CREATED_BY_ID, UserContext.getId());
        properties.put(XmlTemplateProviderConstants.CREATED_BY_TITLE, UserContext.getRealName());
        try {
            final Datastream newRelsExt =
                new Datastream(Datastream.RELS_EXT_DATASTREAM, id, getComponentRelsExtWithVelocity(id, properties,
                    false).getBytes(XmlUtility.CHARACTER_ENCODING), MimeTypes.TEXT_XML);
            component.setRelsExt(newRelsExt);
            component.persist();
        }
        catch (final UnsupportedEncodingException e) {
            throw new XmlParserSystemException("While building component RELS-EXT.", e);
        }
        return properties;
    }

    /**
     * Sets content type specific properties datastream of the item.
     *
     * @param xml The xml representation of the content type specific properties.
     * @throws FedoraSystemException      If Fedora reports an error.
     * @throws WebserverSystemException   In case of an internal error.
     * @throws TripleStoreSystemException If triple store reports an error.
     * @throws EncodingSystemException    If encoding fails.
     * @throws IntegritySystemException   If the integrity of the repository is violated.
     */
    @Deprecated
    protected void setContentTypeSpecificProperties(final String xml) throws FedoraSystemException,
        EncodingSystemException {
        try {
            final Datastream oldDs = getItem().getCts();
            final Datastream newDs =
                new Datastream(Elements.ELEMENT_CONTENT_MODEL_SPECIFIC, getItem().getId(), xml
                    .getBytes(XmlUtility.CHARACTER_ENCODING), MimeTypes.TEXT_XML);

            if (oldDs == null || !oldDs.equals(newDs)) {
                getItem().setCts(newDs);
            }

        }
        catch (final UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }
    }

    /**
     * /** Set the content of a component.
     *
     * @param component The Component.
     * @param xml       The xml representation of the new content.
     * @param fileName  The file name.
     * @param mimeType  the mime type.
     * @throws MissingContentException    If some required content of xml data is missing.
     * @throws InvalidContentException    If some invalid content is found in xml data.
     * @throws FileNotFoundException      If binary content can not be retrieved.
     * @throws ComponentNotFoundException Thrown if Component with provided objid was not found.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     */
    protected void setComponentContent(
        final Component component, final String xml, final String fileName, final String mimeType)
        throws MissingContentException, InvalidContentException, FileNotFoundException, TripleStoreSystemException,
        FedoraSystemException, WebserverSystemException {

        final StaxParser sp = new StaxParser();

        final OneComponentContentHandler occh = new OneComponentContentHandler(sp, "/content");
        sp.addHandler(occh);
        try {
            sp.parse(xml);
        }
        catch (final MissingContentException e) {
            throw e;
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e);
        }

        final Map<String, String> componentBinary = occh.getComponentBinary();
        // load url or binary to fedora
        if (componentBinary.get("content") == null) {
            // ingest by URL
            String url = componentBinary.get("uploadUrl");
            // process URL
            // - local repository URL ("/ir/item/...")
            // - local staging URL ("/st/...")
            // - local repository URL to this
            // - full qualified URL to this
            url = Utility.processUrl(url, getItem().getId(), component.getId());

            if (url == null) {
                // it's the local url we send
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Do not update content of " + component.getId() + ". URL is null.");
                }
                return;
            }
            else {
                // update content and check by checksum if it is really changed
                // and in case remove content PID if exists
                final String contentChecksum = component.getChecksum();
                final ModifiyDatastreamPathParam path = new ModifiyDatastreamPathParam(component.getId(), "content");
                final ModifyDatastreamQueryParam query = new ModifyDatastreamQueryParam();
                query.setDsLocation(url);
                try {
                    final DatastreamProfileTO dsProfile = this.fedoraServiceClient.modifyDatastream(path, query, null);
                    this.getFedoraServiceClient().sync();
                    try {
                        this.getTripleStoreUtility().reinitialize();
                    }
                    catch (TripleStoreSystemException e) {
                        throw new FedoraSystemException("Error on reinitializing triple store.", e);
                    }
                    if (!contentChecksum.equals(dsProfile.getDsChecksum())) {
                        if (component.hasObjectPid()) {
                            // remove Content PID
                            component.removeObjectPid();
                        }
                    }
                }
                catch (final Exception e) {
                    handleFedoraUploadError(url, e);
                }
            }
        }
        else {
            final Datastream content = component.getContent();
            if (content.getControlGroup().equals(FoXmlProviderConstants.CONTROL_GROUP_E)
                || content.getControlGroup().equals(FoXmlProviderConstants.CONTROL_GROUP_R)) {
                throw new InvalidContentException("A binary content of the component " + component.getId()
                    + " has to be referenced by a URL, " + "because the attribute 'storage' of the section"
                    + " 'content' was set to 'external-url' or " + "'external-managed' while create.");
            }
            final String url = uploadBase64EncodedContent(componentBinary.get("content"), fileName, mimeType);
            final ModifiyDatastreamPathParam path = new ModifiyDatastreamPathParam(component.getId(), "content");
            final ModifyDatastreamQueryParam query = new ModifyDatastreamQueryParam();
            query.setDsLocation(url);
            try {
                this.fedoraServiceClient.modifyDatastream(path, query, null);
                this.getFedoraServiceClient().sync();
                this.getTripleStoreUtility().reinitialize();
            }
            catch (final Exception e) {
                handleFedoraUploadError(url, e);
            }
        }
        component.notifySetContent();
    }
}
