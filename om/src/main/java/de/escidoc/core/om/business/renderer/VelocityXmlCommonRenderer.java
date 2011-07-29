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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.GenericVersionableResourcePid;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.joda.time.DateTime;

@Service
public class VelocityXmlCommonRenderer {

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected VelocityXmlCommonRenderer() {
    }

    /**
     * Adds content relations values to the provided map.
     *
     * @param relations Vector with relation maps
     * @param href      href of resource
     * @param values    The map to add values to.
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    public void addRelationsValues(
        final List<Map<String, String>> relations, final String href, final Map<String, Object> values)
        throws TripleStoreSystemException {

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
                final String objectType = this.tripleStoreUtility.getObjectType(targetId);
                if (objectType.endsWith("Item")) {
                    entry.put("targetHref", XmlUtility.BASE_OM + "item/" + targetId);
                }
                else {
                    entry.put("targetHref", XmlUtility.BASE_OM + "container/" + targetId);
                }
                final String targetTitle = this.tripleStoreUtility.getTitle(targetId);
                entry.put("targetTitle", targetTitle);
                entries.add(entry);
            }
        }
        if (entries != null && !entries.isEmpty()) {
            values.put("contentRelations", entries);
        }
    }

    protected static void addXlinkValues(final Map values) {

        values.put(XmlTemplateProviderConstants.VAR_ESCIDOC_BASE_URL, XmlUtility.getEscidocBaseUrl());
        values.put(XmlTemplateProviderConstants.VAR_XLINK_NAMESPACE_PREFIX, Constants.XLINK_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.VAR_XLINK_NAMESPACE, Constants.XLINK_NS_URI);
    }

    protected static void addRelationsNamespaceValues(final Map values) {
        values.put("contentRelationsNamespacePrefix", Constants.CONTENT_RELATIONS_NAMESPACE_PREFIX);
        values.put("contentRelationsNamespace", Constants.CONTENT_RELATIONS_NAMESPACE_URI);

    }

    protected static void addStructuralRelationsValues(final Map values) {
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);
    }

    protected static void addParentsNamespaceValues(final Map values) {
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

        final DateTime lmd;
        try {
            lmd = resource.getLastModificationDate();
        }
        catch (final FedoraSystemException e1) {
            throw new WebserverSystemException(e1);
        }

        values.put(XmlTemplateProviderConstants.VAR_LAST_MODIFICATION_DATE, lmd.toString());

        addXlinkValues(values);
    }
}
