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
package de.escidoc.core.om.business.fedora.context;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.TmeException;
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
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.ContextNameNotUniqueException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.PidAlreadyAssignedException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.MultipleExtractor;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import de.escidoc.core.om.business.renderer.VelocityXmlContextFoXmlRenderer;
import de.escidoc.core.om.business.renderer.interfaces.ContextFoXmlRendererInterface;
import de.escidoc.core.om.business.stax.handler.context.ContextPropertiesHandler;
import org.escidoc.core.services.fedora.IngestPathParam;
import org.escidoc.core.services.fedora.IngestQueryParam;

/**
 * Handler to create Context objects.
 *
 * @author Steffen Wagner
 */
public class ContextHandlerCreate extends ContextHandlerRetrieve {

    private static final String XPATH_PROPERTIES = "/context/properties";

    private static final String XPATH_RESOURCES = "/context/resources";

    private static final String XPATH_ADMIN_DESCRIPTORS = "/context/admin-descriptors/admin-descriptor";

    private ContextFoXmlRendererInterface foxmlRenderer;

    /**
     * Create new Context.
     *
     * @param xmlData New Context data in XML representation.
     * @return id of created context
     * @throws ContextNameNotUniqueException  Thrown if Context name is not unique.
     * @throws ContentModelNotFoundException  Thrown if content type could not be found.
     * @throws ReadonlyElementViolationException
     *                                        Thrown if read-only elements are set.
     * @throws MissingAttributeValueException Thrown if attributes are missing.
     * @throws MissingElementValueException   Thrown if elements are missing.
     * @throws ReadonlyAttributeViolationException
     *                                        Thrown if read-only attributes are set.
     * @throws InvalidContentException        Thrown if content is invalid.
     * @throws OrganizationalUnitNotFoundException
     *                                        Thrown if related organizational unit(s) could not be found.
     * @throws SystemException                Thrown if anything else fails.
     * @throws InvalidStatusException         Thrown if an organizational unit is in an invalid status.
     */
    public String createContext(final String xmlData) throws ContextNameNotUniqueException,
        ContentModelNotFoundException, ReadonlyElementViolationException, MissingAttributeValueException,
        MissingElementValueException, ReadonlyAttributeViolationException, InvalidContentException,
        OrganizationalUnitNotFoundException, SystemException, InvalidStatusException {

        final String contextId = getIdProvider().getNextPid();
        final String createComment = "Object " + contextId + " created.";

        final StaxParser sp = new StaxParser();

        final ContextPropertiesHandler propertiesHandler = new ContextPropertiesHandler(sp);
        sp.addHandler(propertiesHandler);

        final HashMap<String, String> extractPathes = new HashMap<String, String>();
        extractPathes.put(XPATH_PROPERTIES, null);
        extractPathes.put(XPATH_RESOURCES, null);
        extractPathes.put(XPATH_ADMIN_DESCRIPTORS, Elements.ATTRIBUTE_NAME);

        final MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
        sp.addHandler(me);

        try {
            sp.parse(xmlData);
        }
        catch (final XMLStreamException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final LockingException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final OptimisticLockingException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final AlreadyExistsException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final ReferencedResourceNotFoundException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final RelationPredicateNotFoundException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final ContentRelationNotFoundException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final ContextNotFoundException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final MissingContentException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final PidAlreadyAssignedException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final MissingMdRecordException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final InvalidXmlException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        catch (final TmeException e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }

        // get properties hashmap
        final Map<String, Object> properties = propertiesHandler.getPropertiesMap();

        // check that at least one OU is given
        if (propertiesHandler.getOrganizationalUnits().isEmpty()) {
            throw new InvalidContentException("No 'organizational-unit' element is given.");
        }

        // set status created
        properties.put(Elements.ELEMENT_PUBLIC_STATUS, Constants.STATUS_CONTEXT_CREATED);
        properties.put(Elements.ELEMENT_PUBLIC_STATUS_COMMENT, createComment);
        properties.put(Elements.ELEMENT_CREATED_BY, getUtility().getCurrentUserId());
        properties.put(Elements.ELEMENT_CREATED_BY_TITLE, getUtility().getCurrentUserRealName());
        properties.put(Elements.ELEMENT_MODIFIED_BY, getUtility().getCurrentUserId());
        properties.put(Elements.ELEMENT_MODIFIED_BY_TITLE, getUtility().getCurrentUserRealName());

        // name must be unique
        final String name = (String) properties.remove(Elements.ELEMENT_NAME);
        if (getTripleStoreUtility().getContextForName(name) != null) {
            throw new ContextNameNotUniqueException();
        }
        // set title in triple store (title == name)
        // TODO this is a fix to set a title in all objects.
        // in later version could the dc:title be used for object title
        // properties.put(TripleStoreUtility.PROP_TITLE, properties
        // .get(TripleStoreUtility.PROP_NAME));
        final Map<String, String> dcProperties = new HashMap<String, String>();
        final String description = (String) properties.remove(Elements.ELEMENT_DESCRIPTION);
        if (description != null && description.length() > 0) {
            dcProperties.put(Elements.ELEMENT_DESCRIPTION, description);
        }
        dcProperties.put(Elements.ELEMENT_DC_TITLE, name);

        final Map<String, String> propertiesAsReferences = new HashMap<String, String>();
        propertiesAsReferences.put(Elements.ELEMENT_MODIFIED_BY, (String) properties
            .remove(Elements.ELEMENT_MODIFIED_BY));
        propertiesAsReferences
            .put(Elements.ELEMENT_CREATED_BY, (String) properties.remove(Elements.ELEMENT_CREATED_BY));

        // get modified data streams
        final Map<String, Object> streams = me.getOutputStreams();
        streams.remove("properties");
        streams.remove("resources");

        final String contextFoxml =
            buildContextFoxml(contextId, properties, dcProperties, propertiesAsReferences, streams);
        final IngestPathParam path = new IngestPathParam();
        final IngestQueryParam query = new IngestQueryParam();
        this.getFedoraServiceClient().ingest(path, query, contextFoxml);
        this.getFedoraServiceClient().sync();

        return contextId;
    }

    /**
     * The method builds foxml for fedora object, which will represent a Context.
     *
     * @param id                     Id of that context object.
     * @param properties             Rels-ext of that context object.
     * @param dcProperties           DC
     * @param propertiesAsReferences All properties with reference name space.
     * @param dataStreams            Data streams of that context object
     * @return foxml String
     * @throws SystemException Thrown if the FOXML rendering failed.
     */
    protected String buildContextFoxml(
        final String id, final Map<String, Object> properties, final Map<String, String> dcProperties,
        final Map<String, String> propertiesAsReferences, final Map<String, Object> dataStreams) throws SystemException {
        final Map<String, Object> values = new HashMap<String, Object>();

        values.put("id", id);
        values.put("contextTitle", dcProperties.get(Elements.ELEMENT_DC_TITLE));

        final Collection<Map<String, String>> adminDescriptors = new ArrayList<Map<String, String>>();

        for (final Entry<String, Object> stringObjectEntry : dataStreams.entrySet()) {
            final Map<String, String> adminDescriptor = new HashMap<String, String>();
            adminDescriptor.put("name", stringObjectEntry.getKey());
            adminDescriptor.put("id", stringObjectEntry.getKey());
            try {
                adminDescriptor.put("ds", ((ByteArrayOutputStream) stringObjectEntry.getValue())
                    .toString(XmlUtility.CHARACTER_ENCODING));
            }
            catch (final UnsupportedEncodingException e) {
                throw new SystemException(e);
            }
            adminDescriptors.add(adminDescriptor);
        }

        if (!adminDescriptors.isEmpty()) {
            values.put("adminDescriptors", adminDescriptors);
        }

        values.put("organizational-units", properties.remove("organizational-units"));

        values.put("properties", properties);
        values.put("dcProperties", dcProperties);
        values.put("propertiesAsReferences", propertiesAsReferences);

        values.put(XmlTemplateProviderConstants.FRAMEWORK_BUILD_NUMBER, Utility.getBuildNumber());
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProviderConstants.VAR_STRUCT_RELATIONS_NAMESPACE, Constants.STRUCTURAL_RELATIONS_NS_URI);
        values.put("resourcesOntologiesNamespace", Constants.RESOURCES_NS_URI);
        values.put("contentRelationsNamespacePrefix", Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT);
        values.put("latestVersionUserTitle", Utility.getCurrentUser()[1]);

        return getFoxmlRenderer().render(values);
    }

    /**
     * @return The foxml renderer.
     */
    public ContextFoXmlRendererInterface getFoxmlRenderer() {

        if (this.foxmlRenderer == null) {
            this.foxmlRenderer = new VelocityXmlContextFoXmlRenderer();
        }
        return this.foxmlRenderer;
    }

}
