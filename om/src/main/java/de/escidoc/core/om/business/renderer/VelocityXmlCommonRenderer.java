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
package de.escidoc.core.om.business.renderer;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.GenericVersionableResourcePid;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.date.Iso8601Util;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VelocityXmlCommonRenderer {

    /**
     * Adds content relations values to the provided map.
     *
     * @param relations Vector with relation maps
     * @param href      href of resource
     * @param values    The map to add values to.
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws de.escidoc.core.common.exceptions.system.XmlParserSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    public void addRelationsValues(
        final List<Map<String, String>> relations, final String href, final Map<String, Object> values)
        throws FedoraSystemException, IntegritySystemException, XmlParserSystemException, WebserverSystemException,
        TripleStoreSystemException {

        values.put("contentRelationsHref", href + de.escidoc.core.common.business.fedora.Constants.RELATIONS_URL_PART);

        List<Map<String, String>> entries = null;
        if (relations != null && !relations.isEmpty()) {
            final Iterator<Map<String, String>> relIter = relations.iterator();
            entries = new ArrayList<Map<String, String>>(relations.size());
            while (relIter.hasNext()) {
                final Map<String, String> entry = new HashMap<String, String>(3);
                final Map<String, String> relation = relIter.next();
                final String targetId = relation.get("target");
                final String predicate = relation.get("predicate");
                entry.put("targetId", targetId);
                entry.put("predicate", predicate);
                final String objectType = TripleStoreUtility.getInstance().getObjectType(targetId);
                if (objectType.endsWith("Item")) {
                    entry.put("targetHref", XmlUtility.BASE_OM + "item/" + targetId);
                }
                else {
                    entry.put("targetHref", XmlUtility.BASE_OM + "container/" + targetId);
                }
                final String targetTitle = TripleStoreUtility.getInstance().getTitle(targetId);
                entry.put("targetTitle", targetTitle);
                entries.add(entry);
            }
        }
        if (entries != null && !entries.isEmpty()) {
            values.put("contentRelations", entries);
        }
    }

    protected void addXlinkValues(final Map values) throws WebserverSystemException {

        values.put(XmlTemplateProvider.VAR_ESCIDOC_BASE_URL, XmlUtility.getEscidocBaseUrl());
        values.put(XmlTemplateProvider.VAR_XLINK_NAMESPACE_PREFIX, Constants.XLINK_NS_PREFIX);
        values.put(XmlTemplateProvider.VAR_XLINK_NAMESPACE, Constants.XLINK_NS_URI);
    }

    protected void addRelationsNamespaceValues(final Map values) throws WebserverSystemException {
        values.put("contentRelationsNamespacePrefix", Constants.CONTENT_RELATIONS_NAMESPACE_PREFIX);
        values.put("contentRelationsNamespace", Constants.CONTENT_RELATIONS_NAMESPACE_URI);

    }

    protected void addStructuralRelationsValues(final Map values) throws WebserverSystemException {
        values.put(XmlTemplateProvider.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_SREL_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);
    }

    protected void addParentsNamespaceValues(final Map values) throws WebserverSystemException {
        values.put("parentsNamespacePrefix", Constants.PARENTS_NAMESPACE_PREFIX);
        values.put("parentsNamespace", Constants.PARENTS_NAMESPACE_URI);

    }

    /**
     * Adds the common values to the provided map.
     *
     * @param resource The resource for that data shall be created.
     * @param values   The map to add values to.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    protected void addCommonValues(final GenericVersionableResourcePid resource, final Map values)
        throws WebserverSystemException {

        final String lmd;
        try {
            lmd = resource.getLastModificationDate();
        }
        catch (final FedoraSystemException e1) {
            throw new WebserverSystemException(e1);
        }

        try {

            final String n = Iso8601Util.getIso8601(Iso8601Util.parseIso8601(lmd));
            values.put(XmlTemplateProvider.VAR_LAST_MODIFICATION_DATE, n);
        }
        catch (final ParseException e) {
            throw new WebserverSystemException("Unable to parse last-modification-date '" + lmd + "' of resource '"
                + resource.getId() + "'!", e);
        }
        addXlinkValues(values);

    }
}
