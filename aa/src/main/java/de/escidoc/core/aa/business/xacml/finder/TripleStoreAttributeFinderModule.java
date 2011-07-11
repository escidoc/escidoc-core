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
package de.escidoc.core.aa.business.xacml.finder;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import de.escidoc.core.aa.business.authorisation.Constants;
import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.aa.business.xacml.util.MapResult;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.string.StringUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Attribute finder that uses access to an attribute cache to retrieve attribute values of a resource instead of
 * retrieving them from the resource itself.<br> This handler retrieves values of attributes that are stored in a triple
 * store. Commonly, these are attributes of non-versionized object or that are common for all versions of an object,
 * e.g. object status, created-by or latest-version-status.<br> This handler retrieves attributes of the current
 * version, too, if the current version is the latest version.
 * <p/>
 * Supported Attributes:<br> -info:escidoc/names:aa:1.0:resource:component:id<br> the id of the component, single value
 * attribute -info:escidoc/names:aa:1.0:resource:component-id<br> the id of the component, single value attribute
 * -info:escidoc/names:aa:1.0:resource:component:item<br> the id of the item of the component, single value attribute
 * -info:escidoc/names:aa:1.0:resource:component:content-category<br> the content-category of the component, single
 * value attribute -info:escidoc/names:aa:1.0:resource:component:created-by<br> the id of the user who created the
 * component, single value attribute -info:escidoc/names:aa:1.0:resource:component:valid-status<br> the valid-status of
 * the component, single value attribute -info:escidoc/names:aa:1.0:resource:component:visibility<br> the visibility of
 * the component, single value attribute -info:escidoc/names:aa:1.0:resource:container:id<br> the id of the container,
 * single value attribute -info:escidoc/names:aa:1.0:resource:container-id<br> the id of the container, single value
 * attribute -info:escidoc/names:aa:1.0:resource:container:title<br> the title of the container, single value attribute
 * -info:escidoc/names:aa:1.0:resource:container:container<br> the (parent)container of the container, multi value
 * attribute -info:escidoc/names:aa:1.0:resource:container:hierarchical-containers<br> the (parent)containers
 * (hierarchical) of the container, multi value attribute -info:escidoc/names:aa:1.0:resource:container:content-model<br>
 * the id of the content-model of the container, single value attribute -info:escidoc/names:aa:1.0:resource:container:context<br>
 * the id of the context of the container, single value attribute -info:escidoc/names:aa:1.0:resource:container:created-by<br>
 * the id of the user who created the container, single value attribute -info:escidoc/names:aa:1.0:resource:container:latest-release-number<br>
 * the number of the latest release of the container, single value attribute -info:escidoc/names:aa:1.0:resource:container:latest-release-pid<br>
 * the pid of the latest release of the container, single value attribute -info:escidoc/names:aa:1.0:resource:container:latest-version-modified-by<br>
 * the id of the user who created the latest-version of the container, single value attribute
 * -info:escidoc/names:aa:1.0:resource:container:latest-version-number<br> the number of the latest-version of the
 * container, single value attribute -info:escidoc/names:aa:1.0:resource:container:latest-version-status<br> the status
 * of the latest-version of the container, single value attribute -info:escidoc/names:aa:1.0:resource:container:public-status<br>
 * the public-status of the container, single value attribute -info:escidoc/names:aa:1.0:resource:container:version-modified-by<br>
 * the id of the user who created the current version of the container, single value attribute
 * -info:escidoc/names:aa:1.0:resource:container:version-status<br> the status of the current version of the container,
 * single value attribute -info:escidoc/names:aa:1.0:resource:content-model:id<br> the id of the content-model, single
 * value attribute -info:escidoc/names:aa:1.0:resource:content-model-id<br> the id of the content-model, single value
 * attribute -info:escidoc/names:aa:1.0:resource:content-model:title<br> the title of the content-model
 * -info:escidoc/names:aa:1.0:resource:content-relation:id<br> the id of the content-relation, single value attribute
 * -info:escidoc/names:aa:1.0:resource:content-relation-id<br> the id of the content-relation, single value attribute
 * -info:escidoc/names:aa:1.0:resource:content-relation:created-by<br> the id of the user who created the
 * content-relation, single value attribute -info:escidoc/names:aa:1.0:resource:content-relation:version-status<br> the
 * status of the current version of the content-relation, single value attribute -info:escidoc/names:aa:1.0:resource:context:id<br>
 * the id of the context, single value attribute -info:escidoc/names:aa:1.0:resource:context-id<br> the id of the
 * context, single value attribute -info:escidoc/names:aa:1.0:resource:context:title<br> the title of the context,
 * single value attribute -info:escidoc/names:aa:1.0:resource:context:created-by<br> the id of the user who created the
 * context, single value attribute -info:escidoc/names:aa:1.0:resource:context:organizational-unit<br> the
 * organizational-unit of the context, single value attribute -info:escidoc/names:aa:1.0:resource:context:public-status<br>
 * the public-status of the context, single value attribute -info:escidoc/names:aa:1.0:resource:item:id<br> the id of
 * the item, single value attribute -info:escidoc/names:aa:1.0:resource:item-id<br> the id of the item, single value
 * attribute -info:escidoc/names:aa:1.0:resource:item:title<br> the title of the item, single value attribute
 * -info:escidoc/names:aa:1.0:resource:item:component<br> the id of the component of the item, multi value attribute
 * -info:escidoc/names:aa:1.0:resource:item:container<br> the id of the container of the item, multi value attribute
 * -info:escidoc/names:aa:1.0:resource:item:hierarchical-containers<br> the ids of the containers of the item
 * (hierarchical), multi value attribute -info:escidoc/names:aa:1.0:resource:item:content-model<br> the id of the
 * content-model of the item, single value attribute -info:escidoc/names:aa:1.0:resource:item:context<br> the id of the
 * context of the item, single value attribute -info:escidoc/names:aa:1.0:resource:item:created-by<br> the id of the
 * user who created the item, single value attribute -info:escidoc/names:aa:1.0:resource:item:latest-release-number<br>
 * the latest-release-number of the item, single value attribute -info:escidoc/names:aa:1.0:resource:item:latest-release-pid<br>
 * the latest-release-pid of the item, single value attribute -info:escidoc/names:aa:1.0:resource:item:latest-version-modified-by<br>
 * the id of the user who created the latest-version of the item, single value attribute
 * -info:escidoc/names:aa:1.0:resource:item:latest-version-number<br> the latest-version-number of the item, single
 * value attribute -info:escidoc/names:aa:1.0:resource:item:latest-version-status<br> the latest-version-status of the
 * item, single value attribute -info:escidoc/names:aa:1.0:resource:item:public-status<br> the public-status of the
 * item, single value attribute -info:escidoc/names:aa:1.0:resource:item:version-modified-by<br> the id of the user who
 * created the current version of the item, single value attribute -info:escidoc/names:aa:1.0:resource:item:version-status<br>
 * the version-status of the item, single value attribute -info:escidoc/names:aa:1.0:resource:organizational-unit:id<br>
 * the id of the organizational-unit, single value attribute -info:escidoc/names:aa:1.0:resource:organizational-unit-id<br>
 * the id of the organizational-unit, single value attribute -info:escidoc/names:aa:1.0:resource:organizational-unit:title<br>
 * the title of the organizational-unit, single value attribute -info:escidoc/names:aa:1.0:resource:organizational-unit:created-by<br>
 * the id of the user who created the organizational-unit, single value attribute -info:escidoc/names:aa:1.0:resource:organizational-unit:latest-version-number<br>
 * the latest-version-number of the organizational-unit, single value attribute -info:escidoc/names:aa:1.0:resource:organizational-unit:public-status<br>
 * the public-status of the organizational-unit, single value attribute -info:escidoc/names:aa:1.0:resource:organizational-unit:parent<br>
 * the ids of the parents of the organizational-unit, multi value attribute -info:escidoc/names:aa:1.0:resource:organizational-unit:hierarchical-parents<br>
 * the ids of the parents of the organizational-unit (hierarchical), multi value attribute
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.TripleStoreAttributeFinderModule")
public class TripleStoreAttributeFinderModule extends AbstractAttributeFinderModule {

    private static final Pattern SPLIT_PATTERN = Pattern.compile(":");

    /**
     * Pattern to detect item attributes that are version dependent: component, modified-by, and version-status.
     */
    private static final Pattern PATTERN_ITEM_DEPENDENT_ATTRS =
        Pattern.compile(AttributeIds.URN_ITEM_COMPONENT_ATTR + ".*|" + AttributeIds.URN_ITEM_MODIFIED_BY_ATTR + ".*|"
            + AttributeIds.URN_ITEM_VERSION_STATUS_ATTR + ".*");

    /**
     * Pattern to detect container attributes that are version dependent: member, modified-by, and version-status.
     */
    private static final Pattern PATTERN_CONTAINER_DEPENDENT_ATTRS =
        Pattern.compile(AttributeIds.URN_CONTAINER_MEMBER_ATTR + ".*|"
            + AttributeIds.URN_CONTAINER_VERSION_MODIFIED_BY_ATTR + ".*|"
            + AttributeIds.URN_CONTAINER_VERSION_STATUS_ATTR + ".*");

    /**
     * The mapping from the attribute ids to the ids in the triple store.
     */
    private Map<String, MapResult> mapping;

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tsu;

    private final MapResult publicStatusMapResult = new MapResult(TripleStoreUtility.PROP_PUBLIC_STATUS, false);

    private final MapResult createdByMapResult = new MapResult(TripleStoreUtility.PROP_CREATED_BY_ID, false);

    private final MapResult latestVersionNumberMapResult =
        new MapResult(TripleStoreUtility.PROP_LATEST_VERSION_NUMBER, false);

    private final MapResult contentModelMapResult = new MapResult(TripleStoreUtility.PROP_CONTENT_MODEL_ID, false);

    private final MapResult contextMapResult = new MapResult(TripleStoreUtility.PROP_CONTEXT_ID, false);

    private final MapResult memberMapResult = new MapResult(TripleStoreUtility.PROP_MEMBER, true);

    private final MapResult hierarchicalMemberMapResult =
        new MapResult(TripleStoreUtility.PROP_MEMBER, true, true, false);

    private final MapResult parentMapResult = new MapResult(TripleStoreUtility.PROP_PARENT, false);

    private final MapResult hierarchicalParentMapResult =
        new MapResult(TripleStoreUtility.PROP_PARENT, false, true, true);

    private final MapResult componentMapResult = new MapResult(TripleStoreUtility.PROP_COMPONENT, false);

    private final MapResult latestReleaseNumberMapResult =
        new MapResult(TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER, false);

    private final MapResult latestReleasePidMapResult =
        new MapResult(TripleStoreUtility.PROP_LATEST_RELEASE_PID, false);

    private final MapResult latestVersionUserMapResult =
        new MapResult(TripleStoreUtility.PROP_LATEST_VERSION_USER_ID, false);

    private final MapResult latestVersionStatusMapResult =
        new MapResult(TripleStoreUtility.PROP_LATEST_VERSION_STATUS, false);

    private final MapResult organizationalUnitMapResult =
        new MapResult(de.escidoc.core.common.business.Constants.STRUCTURAL_RELATIONS_NS_URI + "organizational-unit",
            false);

    /**
     * The constructor.
     */
    public TripleStoreAttributeFinderModule() {

        initMapping();
    }

    /**
     * See Interface for functional description.<br>
     * <p/>
     * This handler is responsible for all eSciDoc attributes that can be fetched from the triple store, i.e. it is
     * responsible for all attributes of objects stored in fedora if <ul> <li>the object is not under version
     * control,</li> <li>the latest version is addressed, or</li> <li>if the attribute is not version specific, i.e. it
     * has the same value for all versions of the object.</li> </ul> Therefore, the handler is responsible if the
     * resource id does not contain a version number (resourceVersionNumber == null), as in this case always the latest
     * version shall be addressed. It is responsible, too, if the version number specified in the resource id matches
     * the latest-version number. And it is responsible to fetch all non-version specific attributes even if the
     * specified version number does not match the latest-version-number.
     */
    @Override
    protected boolean assertAttribute(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber, final int designatorType) throws EscidocException {

        // make sure it is an eSciDoc resource attribute and an id of the
        // resource for that the attribute shall be found
        // is specified, as this handler is not able to resolve values of
        // not existing resources.
        if (!super.assertAttribute(attributeIdValue, ctx, resourceId, resourceObjid, resourceVersionNumber,
            designatorType)
            || FinderModuleHelper.isNewResourceId(resourceId)) {

            return false;
        }

        // The handler is responsible if the resource id does not
        // contain a version number (resourceVersionNumber == null), as in this
        // case always the latest version shall be addressed.
        if (resourceVersionNumber != null) {
            // It is responsible, too, if the version number specified in the
            // resource id matches the latest-version number.
            // And it is responsible to fetch all non-version specific
            // attributes even if the specified version number does not match
            // the latest-version-number.
            // In the following in case of version dependent attributes the
            // version number is compared with the latest version number

            // item components, modified-by, and version-status are version
            // specific.
            if (PATTERN_ITEM_DEPENDENT_ATTRS.matcher(attributeIdValue).find()) {
                final String latestVersionNumber =
                    fetchSingleResourceAttribute(ctx, resourceObjid, AttributeIds.URN_ITEM_LATEST_VERSION_NUMBER_ATTR);
                if (!latestVersionNumber.equals(resourceVersionNumber)) {
                    return false;
                }
            }
            // container members, modified-by, and version-status are version
            // specific.
            else if (PATTERN_CONTAINER_DEPENDENT_ATTRS.matcher(attributeIdValue).find()) {

                final String latestVersionNumber =
                    fetchSingleResourceAttribute(ctx, resourceObjid,
                        AttributeIds.URN_CONTAINER_LATEST_VERSION_NUMBER_ATTR);
                if (!latestVersionNumber.equals(resourceVersionNumber)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * See Interface for functional description.
     */
    @Override
    protected Object[] resolveLocalPart(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber) throws SystemException, ResourceNotFoundException {

        List<String> cachedAttribute = new ArrayList<String>();
        final MapResult mapresult = mapIt(attributeIdValue);

        if (mapresult == null) {
            return null;
        }

        EvaluationResult result;
        if (!mapresult.getresolvableAttributeId().equals(attributeIdValue)) {
            try {
                result =
                    ctx.getResourceAttribute(Constants.URI_XMLSCHEMA_STRING, new URI(mapresult
                        .getresolvableAttributeId()), null);
            }
            catch (final URISyntaxException e) {
                result = CustomEvaluationResultBuilder.createSyntaxErrorResult(e);
            }
            return new Object[] { result, mapresult.getresolvableAttributeId() };
        }

        if (mapresult.isHierarchical()) {
            if (mapresult.isIncludeHierarchyBase()) {
                cachedAttribute.add(resourceObjid);
            }
            final Collection<String> attributesList = new ArrayList<String>();
            attributesList.add(resourceObjid);
            cachedAttribute = getHierarchicalCachedAttributes(attributesList, cachedAttribute, mapresult);
        }
        else {
            cachedAttribute =
                FinderModuleHelper.retrieveFromTripleStore(mapresult.isInverse(), mapresult
                    .getResolveCurrentWhereClause(resourceObjid, this.tsu), resourceObjid, mapresult.getCacheId(),
                    this.tsu);
        }

        final List<StringAttribute> stringAttributes = new ArrayList<StringAttribute>();
        if (cachedAttribute.isEmpty()) {
            // if Attribute was not found it is not there
            // to avoid repeatedly resolving, put marker in EvaluationCtx
            stringAttributes.add(new StringAttribute("nonresolvable"));
        }
        else {
            for (final String stringAttribute : cachedAttribute) {
                stringAttributes.add(new StringAttribute(stringAttribute));
            }
        }

        result = new EvaluationResult(new BagAttribute(Constants.URI_XMLSCHEMA_STRING, stringAttributes));

        return new Object[] { result, mapresult.getresolvableAttributeId() };
    }

    /**
     * resolves Attributes hierarchically from TripleStore.
     *
     * @param attributesList      list of Attributes to resolve
     * @param totalAttributesList list of total resolved Attributes
     * @param mapresult           MapResult
     * @return List
     * @throws ResourceNotFoundException e
     * @throws SystemException           e
     */
    protected List<String> getHierarchicalCachedAttributes(
        final Collection<String> attributesList, final List<String> totalAttributesList, final MapResult mapresult)
        throws ResourceNotFoundException, SystemException {
        List<String> hierarchicalAttributesList = totalAttributesList;
        if (attributesList != null && !attributesList.isEmpty()) {
            for (final String attribute : attributesList) {
                final List<String> theseAttributes =
                    FinderModuleHelper
                        .retrieveFromTripleStore(mapresult.isInverse(), mapresult.getResolveCurrentWhereClause(
                            attribute, this.tsu), attribute, mapresult.getCacheId(), this.tsu);
                if (theseAttributes != null && !theseAttributes.isEmpty()) {
                    hierarchicalAttributesList.addAll(theseAttributes);
                    hierarchicalAttributesList =
                        getHierarchicalCachedAttributes(theseAttributes, hierarchicalAttributesList, mapresult);
                }
            }
        }
        return hierarchicalAttributesList;
    }

    /**
     * Initializes the mapping from the attribute id to the id used inside the triple store (e.g. MPT).
     */
    private void initMapping() {

        this.mapping = new HashMap<String, MapResult>();

        // *************************
        // common attribute id
        // *************************
        // FIXME: remove unused, check path expressions

        // object id must be fetched inverse, because other wise all dc
        // identifiers would be returned, not only the id.

        // String cacheId =
        // de.escidoc.core.common.business.Constants.DC_IDENTIFIER_URI;

        // dc:identifier is replaced by a FedoraCreatedDate because a DC data
        // stream
        // of an Escidoc resource does not always contain dc:identifier entry
        // due to using of custom XSLTs for DC-Mapping
        String cacheId = TripleStoreUtility.FEDORA_CREATION_DATE_PREDICATE;

        mapping.put("component-id", new MapResult(cacheId, true));
        mapping.put("component:id", new MapResult(cacheId, true));

        mapping.put("container-id", new MapResult(cacheId, true));
        mapping.put("container:id", new MapResult(cacheId, true));

        mapping.put("content-model-id", new MapResult(cacheId, true));
        mapping.put("content-model:id", new MapResult(cacheId, true));

        mapping.put("context-id", new MapResult(cacheId, true));
        mapping.put("context:id", new MapResult(cacheId, true));

        mapping.put("item-id", new MapResult(cacheId, true));
        mapping.put("item:id", new MapResult(cacheId, true));

        mapping.put("content-relation-id", new MapResult(cacheId, true));
        mapping.put("content-relation:id", new MapResult(cacheId, true));

        mapping.put("organizational-unit-id", new MapResult(cacheId, true));
        mapping.put("organizational-unit:id", new MapResult(cacheId, true));

        // *************************
        // common attribute title
        // *************************
        cacheId = "http://www.nsdl.org/ontologies/relationships/title";
        mapping.put("container:title", new MapResult(cacheId, false));
        mapping.put("content-model:title", new MapResult(cacheId, false));
        mapping.put("context:title", new MapResult(cacheId, false));
        mapping.put("item:title", new MapResult(cacheId, false));
        mapping.put("organizational-unit:title", new MapResult(cacheId, false));

        // *************************
        // component attributes
        // *************************

        initMappingComponent();

        // *************************
        // container attributes
        // *************************

        initMappingContainer();

        // *************************
        // content-model
        // *************************

        // created-by
        // mapping.put("content-model:created-by", new MapResult(
        // TripleStoreUtility.PROP_CREATED_BY_ID, false,
        // ContentTypeNotFoundException.class));

        // *************************
        // context attributes
        // *************************

        initMappingContext();

        // *************************
        // item attributes
        // *************************

        initMappingItem();

        // *************************
        // content-relation attributes
        // *************************

        initMappingContentRelation();

        // *******************************
        // organizational-unit attributes
        // *******************************

        initMappingOrganizationalUnit();

    }

    /**
     * Initializes the mapping from the attribute id to the id used inside the triple store (e.g. kowari) for attributes
     * related to organizational units.
     */
    private void initMappingOrganizationalUnit() {

        mapping.put("organizational-unit:created-by", this.createdByMapResult);

        mapping.put("organizational-unit:latest-version-number", this.latestVersionNumberMapResult);

        mapping.put("organizational-unit:public-status", this.publicStatusMapResult);

        mapping.put("organizational-unit:parent", this.parentMapResult);

        mapping.put("organizational-unit:hierarchical-parents", this.hierarchicalParentMapResult);

    }

    /**
     * Initializes the mapping from the attribute id to the id used inside the triple store (e.g. mulgara) for
     * attributes related to items.
     */
    private void initMappingItem() {
        mapping.put("item:component", this.componentMapResult);

        // container (of latest version, only? or version independent?)
        mapping.put("item:container", this.memberMapResult);

        mapping.put("item:hierarchical-containers", this.hierarchicalMemberMapResult);

        mapping.put("item:content-model", this.contentModelMapResult);

        mapping.put("item:context", this.contextMapResult);

        // created-by
        mapping.put("item:created-by", this.createdByMapResult);

        mapping.put("item:latest-release-number", this.latestReleaseNumberMapResult);

        mapping.put("item:latest-release-pid", this.latestReleasePidMapResult);

        mapping.put("item:latest-version-modified-by", this.latestVersionUserMapResult);

        // latest version number
        mapping.put("item:latest-version-number", this.latestVersionNumberMapResult);

        mapping.put("item:latest-version-status", this.latestVersionStatusMapResult);

        // public-status
        mapping.put("item:public-status", this.publicStatusMapResult);

        // modified-by (of latest version)
        mapping.put("item:version-modified-by", mapping.get("item:latest-version-modified-by"));

        // version status (of latest version)
        mapping.put("item:version-status", mapping.get("item:latest-version-status"));
    }

    /**
     * Initializes the mapping from the attribute id to the id used inside the triple store (e.g. mulgara) for
     * attributes related to items.
     */
    private void initMappingContentRelation() {
        // created-by
        mapping.put("content-relation:created-by", this.createdByMapResult);
        // public-status
        mapping.put("content-relation:public-status", this.publicStatusMapResult);
    }

    /**
     * Initializes the mapping from the attribute id to the id used inside the triple store (e.g. kowari) for attributes
     * related to contexts.
     */
    private void initMappingContext() {
        // created-by
        mapping.put("context:created-by", this.createdByMapResult);

        mapping.put("context:organizational-unit", this.organizationalUnitMapResult);

        // public-status
        mapping.put("context:public-status", this.publicStatusMapResult);
    }

    /**
     * Initializes the mapping from the attribute id to the id used inside the triple store (e.g. kowari) for attributes
     * related to container.
     */
    private void initMappingContainer() {
        // containers (of latest version, only? or version independent?)
        mapping.put("container:container", this.memberMapResult);

        mapping.put("container:hierarchical-containers", this.hierarchicalMemberMapResult);

        // content type
        mapping.put("container:content-model", this.contentModelMapResult);

        // context
        mapping.put("container:context", this.contextMapResult);

        // created-by
        mapping.put("container:created-by", this.createdByMapResult);

        // latest release number
        mapping.put("container:latest-release-number", this.latestReleaseNumberMapResult);

        // latest release pid
        mapping.put("container:latest-release-pid", this.latestReleasePidMapResult);

        // latest version modified by
        mapping.put("container:latest-version-modified-by", this.latestVersionUserMapResult);

        // latest version number
        mapping.put("container:latest-version-number", this.latestVersionNumberMapResult);

        // latest version status
        mapping.put("container:latest-version-status", this.latestVersionStatusMapResult);

        // public-status
        mapping.put("container:public-status", this.publicStatusMapResult);

        // modified-by (of latest version)
        mapping.put("container:version-modified-by", mapping.get("container:latest-version-modified-by"));

        // version status (of latest version)
        mapping.put("container:version-status", mapping.get("container:latest-version-status"));
    }

    /**
     * Initializes the mapping from the attribute id to the id used inside the triple store (e.g. kowari) for attributes
     * related to components.
     */
    private void initMappingComponent() {

        // item
        mapping.put("component:item", new MapResult(TripleStoreUtility.PROP_COMPONENT, true));

        // content category
        mapping.put("component:content-category", new MapResult(TripleStoreUtility.PROP_CONTENT_CATEGORY, false));

        // created-by
        mapping.put("component:created-by", this.createdByMapResult);

        // status
        mapping.put("component:valid-status", new MapResult(TripleStoreUtility.PROP_VALID_STATUS, false));

        // visibility
        mapping.put("component:visibility", new MapResult(TripleStoreUtility.PROP_VISIBILITY, false));
    }

    /**
     * Maps the specified attribute id specified to an id used inside the cache (e.g. kowari).<br> The longest match is
     * searched in the mappings, i.e. if there are mapping defined for &quot;a:b&quot; and for &quot;a:b:c&quot;, the
     * mapping for &quot;a:b:c&quot; is chosen.
     *
     * @param attributeIdValue The value of the attribute id to map.
     * @return Returns a {@code MapResult} object containing the mapped cache id for the longest found match<br> If
     *         no match can be found, {@code null} is returned.
     */
    public MapResult mapIt(final String attributeIdValue) {

        if (attributeIdValue == null) {
            return null;
        }

        final String[] elements =
            SPLIT_PATTERN.split(attributeIdValue.substring(AttributeIds.RESOURCE_ATTR_PREFIX_LENGTH));

        String currentPath = "";
        String longestPath = null;
        MapResult longestMatch = null;
        String contentModelTitle = null;
        String contentTypePredicateId = null;
        int indexLongestMatch = -1;
        for (int i = 0; i < elements.length; i++) {
            String element = elements[i];
            final int dotIndex = element.indexOf('.');
            if (dotIndex != -1) {
                contentModelTitle = element.substring(dotIndex + 1);
                element = element.substring(0, dotIndex);
                elements[i] = element;
            }
            currentPath = i == 0 ? element : StringUtility.concatenateWithColon(currentPath, element).toString();

            if (mapping.get(currentPath) != null) {
                longestMatch =
                    new MapResult(mapping.get(currentPath).getCacheId(), mapping.get(currentPath).isInverse(), mapping
                        .get(currentPath).isHierarchical(), mapping.get(currentPath).isIncludeHierarchyBase());
                longestPath = currentPath;
                indexLongestMatch = i;
            }
            if (contentModelTitle != null) {
                contentTypePredicateId = TripleStoreUtility.PROP_CONTENT_MODEL_ID;
                break;
            }
        }
        if (indexLongestMatch == -1) {
            return null;
        }

        String tail = null;
        if (indexLongestMatch < elements.length - 1) {
            final StringBuilder tailBuf = new StringBuilder(AttributeIds.RESOURCE_ATTR_PREFIX);
            tail = AttributeIds.RESOURCE_ATTR_PREFIX;
            for (int i = indexLongestMatch; i < elements.length; i++) {
                tailBuf.append(elements[i]);
                tailBuf.append(':');
            }
            tail = tailBuf.substring(0, tailBuf.length() - 1);
        }

        longestMatch.setResolvableAttributeId(AttributeIds.RESOURCE_ATTR_PREFIX + longestPath);
        longestMatch.setNextAttributeId(tail);
        longestMatch.setContentTypePredicateId(contentTypePredicateId);
        longestMatch.setContentTypeTitle(contentModelTitle);

        return longestMatch;
    }
}
