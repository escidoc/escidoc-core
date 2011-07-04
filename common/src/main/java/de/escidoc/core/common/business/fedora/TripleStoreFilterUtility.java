/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.business.fedora;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;

import java.util.List;
import java.util.Map;

public interface TripleStoreFilterUtility {

    String MEMBER_RELATION_PREDICATE = "<http://www.nsdl.org/ontologies/relationships/hasMember>";

    String PARENT_RELATION_PREDICATE = "<http://www.nsdl.org/ontologies/relationships/hasParent>";

    String ITEM_PREDICATE_PREFIX = '<' + Constants.ITEM_PROPERTIES_NAMESPACE_URI + '/';

    String CONTAINER_PREDICATE_PREFIX = "<http://www.escidoc.de/schemas/container/0.1/";

    String ITEMS_CONTEXT_PREDICATE = ITEM_PREDICATE_PREFIX + "context>";

    String CONTAINERS_CONTEXT_PREDICATE = CONTAINER_PREDICATE_PREFIX + "context>";

    /**
     * <param> <filter name="items"> <id>escidoc:23232</id> <id>escidoc:12121</id> </filter> <filter
     * name="created-by">escidoc:14141"</filter> <filter name="related">true</filter> <filter
     * name="public-status">submitted</filter> </param>
     * <p/>
     * TODO more than one id-filter (name="items" and name="containers")
     *
     * @param objectType
     * @param filterMap
     * @param additionalQueryPart
     * @param whereClause
     * @throws SystemException Thrown in case of an internal error that prevents the filtering using user id and role.
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @return
     */
    List<String> evaluate(
        final String objectType, final Map<String, Object> filterMap, final String additionalQueryPart,
        final String whereClause) throws SystemException, MissingMethodParameterException;

    /**
     * @param id
     * @param whereClause
     * @return
     * @throws TripleStoreSystemException
     */
    List<String> getMemberList(final String id, final String whereClause) throws TripleStoreSystemException;

    List<String> getContainerMemberList(
        final String containerId, final Map<String, Object> filter, final String whereClause) throws SystemException,
        MissingMethodParameterException;

    List<String> getContextMemberList(
        final String contextId, final Map<String, Object> filterMap, final String whereClause) throws SystemException,
        MissingMethodParameterException;

    String getObjectRefs(final String objectType, final Map<String, Object> filterMap, final String whereClause)
        throws SystemException, MissingMethodParameterException;

    /**
     * Reload possibly needed values. Generalization of setUpTableManager which is called after sync of
     * MPT-TripleStore.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    void reinitialize() throws TripleStoreSystemException;

}
