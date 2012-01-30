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
package de.escidoc.core.test.common.client.servlet.oum;

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;

import java.util.Map;

/**
 * Offers access methods to the escidoc REST and interface of the organizational unit resource.
 *
 * @author Michael Schneider
 */
public class OrganizationalUnitClient extends ClientBase {

    /**
     * Create an organizational unit.
     *
     * @param ouXml The xml representation of an organizational unit
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object create(final Object ouXml) throws Exception {

        return callEsciDoc("OrganizationalUnit.create", METHOD_CREATE, Constants.HTTP_METHOD_PUT,
            Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] {}, changeToString(ouXml));
    }

    /**
     * Delete an organizational unit.
     *
     * @param id The id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object delete(final String id) throws Exception {

        return callEsciDoc("OrganizationalUnit.delete", METHOD_DELETE, Constants.HTTP_METHOD_DELETE,
            Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id });
    }

    /**
     * Update an organizational unit.
     *
     * @param id    the organizational unit id.
     * @param ouXml The xml representation of the organizational unit
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object update(final String id, final Object ouXml) throws Exception {

        return callEsciDoc("OrganizationalUnit.update", METHOD_UPDATE, Constants.HTTP_METHOD_PUT,
            Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id }, changeToString(ouXml));
    }

    /**
     * Update the sub resource organization-details of an organizational unit.
     *
     * @param id    the organizational unit id.
     * @param ouXml The xml representation of the sub resource organization-details of the organizational unit
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object updateMdRecords(final String id, final Object ouXml) throws Exception {

        return callEsciDoc("OrganizationalUnit.updateMdRecords", METHOD_UPDATE_MD_RECORDS, Constants.HTTP_METHOD_PUT,
            Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id, "/" + Constants.SUB_MD_RECORDS },
            changeToString(ouXml));
    }

    /**
     * Update the sub resource parents of an organizational unit.
     *
     * @param id    the organizational unit id.
     * @param ouXml The xml representation of the sub resource parent-ous of the organizational unit
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object updateParents(final String id, final Object ouXml) throws Exception {

        return callEsciDoc("OrganizationalUnit.updateParents", METHOD_UPDATE_PARENTS, Constants.HTTP_METHOD_PUT,
            Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id, "/" + Constants.SUB_PARENTS },
            changeToString(ouXml));
    }

    /**
     * Retrieve the xml representation of an organizational unit.
     *
     * @param id The id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieve(final String id) throws Exception {

        return callEsciDoc("OrganizationalUnit.retrieve", METHOD_RETRIEVE, Constants.HTTP_METHOD_GET,
            Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id });
    }

    /**
     * Retrieve the xml representation of a list of resources.
     *
     * @param id The id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveResources(final String id) throws Exception {

        return callEsciDoc("OrganizationalUnit.retrieveResources", METHOD_RETRIEVE_RESOURCES,
            Constants.HTTP_METHOD_GET, Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id,
                "/" + Constants.SUB_RESOURCES });
    }

    /**
     * Retrieve the xml representation of the properties.
     *
     * @param id The id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveProperties(final String id) throws Exception {

        return callEsciDoc("OrganizationalUnit.retrieveProperties", METHOD_RETRIEVE_PROPERTIES,
            Constants.HTTP_METHOD_GET, Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id,
                "/" + Constants.SUB_PROPERTIES });
    }

    /**
     * Retrieve the xml representation of the organizational-details.
     *
     * @param id The id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveMdRecords(final String id) throws Exception {

        return callEsciDoc("OrganizationalUnit.retrieveMdRecords", METHOD_RETRIEVE_MD_RECORDS,
            Constants.HTTP_METHOD_GET, Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id,
                "/" + Constants.SUB_MD_RECORDS });
    }

    /**
     * Retrieve the XML representation of a md-record of a organizational unit.
     * 
     * @param id
     *            The id.
     * @param name
     *            The name of the md-record
     * @return The HttpMethod after the service call .
     * @throws Exception
     *             If the service call fails.
     */
    public Object retrieveMdRecord(final String id, final String name) throws Exception {

        return callEsciDoc("OrganizationalUnit.retrieveMdRecord", METHOD_RETRIEVE_MD_RECORD, Constants.HTTP_METHOD_GET,
            Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id, "/" + Constants.SUB_MD_RECORD, name });
    }

    /**
     * Retrieve the xml representation of the parent-ous.
     *
     * @param id The id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveParents(final String id) throws Exception {

        return callEsciDoc("OrganizationalUnit.retrieveParentOus", METHOD_RETRIEVE_PARENTS, Constants.HTTP_METHOD_GET,
            Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id, "/" + Constants.SUB_PARENTS });
    }

    /**
     * Retrieve the child ous of an organizational unit.
     *
     * @param id The id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveChildObjects(final String id) throws Exception {

        return callEsciDoc("OrganizationalUnit.retrieveChildObjects", METHOD_RETRIEVE_CHILD_OBJECTS,
            Constants.HTTP_METHOD_GET, Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id,
                Constants.SUB_CHILD_OBJECTS });
    }

    /**
     * Retrieve the parent ous of an organizational unit.
     *
     * @param id The id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveParentObjects(final String id) throws Exception {

        return callEsciDoc("OrganizationalUnit.retrieveParentObjectss", METHOD_RETRIEVE_PARENT_OBJECTS,
            Constants.HTTP_METHOD_GET, Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id,
                Constants.SUB_PARENT_OBJECTS });
    }

    /**
     * Retrieve a filtered list of all organizational units.
     *
     * @param filter The id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveOrganizationalUnits(final Map<String, String[]> filter) throws Exception {

        return callEsciDoc("OrganizationalUnit.retrieveOrganizationalUnits", METHOD_RETRIEVE_ORGANIZATIONAL_UNITS,
            Constants.HTTP_METHOD_GET, Constants.ORGANIZATIONAL_UNITS_BASE_URI, new String[] {}, filter);
    }

    /**
     * Close an organizational unit.
     *
     * @param id        The id of the organizational unit to close.
     * @param taskParam The task parameter containing the last modification date of the organizational unit and an
     *                  comment.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object close(final String id, final String taskParam) throws Exception {

        return callEsciDoc("OrganizationalUnit.cloae", METHOD_CLOSE, Constants.HTTP_METHOD_POST,
            Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id, Constants.SUB_CLOSE }, taskParam);
    }

    /**
     * Open an organizational unit.
     *
     * @param id        The id of the organizational unit to open.
     * @param taskParam The task parameter containing the last modification date of the organizational unit and an
     *                  comment.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object open(final String id, final String taskParam) throws Exception {

        return callEsciDoc("OrganizationalUnit.open", METHOD_OPEN, Constants.HTTP_METHOD_POST,
            Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id, Constants.SUB_OPEN }, taskParam);
    }

    /**
     * Retrieve a pathlist of references from a specified organizational units to the toplevel ous.
     *
     * @param id The Organizational Unit id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrievePathList(final String id) throws Exception {

        return callEsciDoc("OrganizationalUnit.retrievePathList", METHOD_RETRIEVE_ORGANIZATIONAL_UNIT_PATH_LIST,
            Constants.HTTP_METHOD_GET, Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { id,
                Constants.SUB_PATH_LIST });
    }

    /**
     * Retrieve a list of successors from a specified organizational unit.
     *
     * @param objid The Organizational Unit id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveSuccessors(final String objid) throws Exception {

        return callEsciDoc("OrganizationalUnit.retrieveSuccessors", METHOD_RETRIEVE_ORGANIZATIONAL_UNIT_SUCCESSORS,
            Constants.HTTP_METHOD_GET, Constants.ORGANIZATIONAL_UNIT_BASE_URI, new String[] { objid,
                Constants.SUB_SUCCESSORS });
    }

}
