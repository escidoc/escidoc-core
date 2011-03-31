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
package de.escidoc.core.oum.business.interfaces;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.business.filter.SRURequestParameters;
import de.escidoc.core.common.business.interfaces.IngestableResource;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHasChildrenException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHierarchyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * The interface for access to the business OrganizationalUnitHandler.
 *
 * @author Michael Schneider
 */
public interface OrganizationalUnitHandlerInterface extends IngestableResource {

    /**
     * Create a new organizational unit.
     *
     * @param xml The xml representation of the organizational unit.
     * @return The xml representation of the created organizational unit.
     * @throws InvalidStatusException         If a parent is not in state 'created' or 'opened'.
     * @throws MissingAttributeValueException If a mandatory attribute is not set in xml data.
     * @throws MissingElementValueException   If a mandatory element is not found in xml data.
     * @throws MissingMdRecordException       If the required md-record is missing
     * @throws OrganizationalUnitNotFoundException
     *                                        If any of the included references to a parent is not valid.
     * @throws SystemException                If an internal error occurred.
     * @throws XmlCorruptedException          Thrown if the schema validation of the provided data failed.
     * @throws XmlSchemaValidationException   Thrown if the schema validation of the provided data failed.
     */
    String create(String xml) throws MissingAttributeValueException, MissingElementValueException,
        MissingMdRecordException, OrganizationalUnitNotFoundException, SystemException, InvalidStatusException,
        XmlCorruptedException, XmlSchemaValidationException;

    /**
     * Delete an organizational unit. By now the state of the organizational unit is not checked because there are only
     * organizational units in state 'created', but the organizational unit must not have any children.
     *
     * @param id The id of the organizational unit.
     * @throws OrganizationalUnitNotFoundException
     *                                If an organizational unit with the provided id does not exist.
     * @throws InvalidStatusException If the organizational has an invalid status.
     * @throws OrganizationalUnitHasChildrenException
     *                                If the organizational unit has children.
     * @throws SystemException        If an internal error occurred.
     */
    void delete(String id) throws OrganizationalUnitNotFoundException, InvalidStatusException,
        OrganizationalUnitHasChildrenException, SystemException;

    /**
     * Retrieve an organizational unit.
     *
     * @param id The id of the organizational unit.
     * @return The xml representation of the organizational unit.
     * @throws OrganizationalUnitNotFoundException
     *                         If an organizational unit with the provided id does not exist.
     * @throws SystemException If an internal error occurred.
     */
    String retrieve(String id) throws OrganizationalUnitNotFoundException, SystemException;

    /**
     * Update an organizational unit.
     *
     * @param id  The id of the organizational unit.
     * @param xml The xml representation of the organizational unit.
     * @return The xml representation of the updated organizational unit.
     * @throws InvalidStatusException       If a parent is not in state 'created' or 'opened'.
     * @throws InvalidXmlException          If the schema validation fails.
     * @throws MissingElementValueException Thrown if required element value is missing.
     * @throws OptimisticLockingException   If the organizational unit was changed in the meantime.
     * @throws OrganizationalUnitHierarchyViolationException
     *                                      If any of the specified parents is also a child of the updated
     *                                      organizational unit.
     * @throws OrganizationalUnitNotFoundException
     *                                      If an organizational unit with the provided id does not exist.
     * @throws SystemException              If an internal error occurred.
     */
    String update(final String id, final String xml) throws InvalidStatusException, InvalidXmlException,
        MissingElementValueException, OptimisticLockingException, OrganizationalUnitHierarchyViolationException,
        OrganizationalUnitNotFoundException, SystemException;

    /**
     * Update the md-records of an organizational unit.
     *
     * @param id  The id of the organizational unit.
     * @param xml The xml representation of the md-records.
     * @return The xml representation of the updated organizational unit.
     * @throws InvalidXmlException          If the schema validation fails.
     * @throws InvalidStatusException       Thrown if organizational unit has invalid status to add meta data.
     * @throws MissingElementValueException Thrown if required element value is missing.
     * @throws OptimisticLockingException   If the organizational unit was changed in the meantime.
     * @throws OrganizationalUnitNotFoundException
     *                                      If an organizational unit with the provided id does not exist.
     * @throws SystemException              If an internal error occurred.
     */
    String updateMdRecords(final String id, final String xml) throws InvalidXmlException, InvalidStatusException,
        MissingElementValueException, OptimisticLockingException, OrganizationalUnitNotFoundException, SystemException;

    /**
     * Update the parents of an organizational unit.
     *
     * @param id  The id of the organizational unit.
     * @param xml The xml representation of the parents.
     * @return The xml representation of the updated organizational unit.
     * @throws InvalidStatusException       If a parent is not in state 'created' or 'opened'.
     * @throws InvalidXmlException          If the schema validation fails.
     * @throws MissingElementValueException Thrown if required element value is missing.
     * @throws OptimisticLockingException   If the organizational unit was changed in the meantime.
     * @throws OrganizationalUnitHierarchyViolationException
     *                                      If any of the specified parents is also a child of the updated
     *                                      organizational unit.
     * @throws OrganizationalUnitNotFoundException
     *                                      If an organizational unit with the provided id does not exist.
     * @throws SystemException              If an internal error occurred.
     */
    String updateParents(final String id, final String xml) throws InvalidStatusException, InvalidXmlException,
        MissingElementValueException, OptimisticLockingException, OrganizationalUnitHierarchyViolationException,
        OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve the properties subresource of an organizational unit.
     *
     * @param id The id of the organizational unit.
     * @return The xml representation of the properties sub resource.
     * @throws OrganizationalUnitNotFoundException
     *                         If an organizational unit with the provided id does not exist.
     * @throws SystemException If an internal error occurred.
     */
    String retrieveProperties(final String id) throws OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve a virtual resource by name.
     *
     * @param id           organizational unit id
     * @param resourceName name of the virtual resource
     * @return virtual resource as XML representation
     * @throws OperationNotFoundException thrown if there is no method configured for the given resource name
     * @throws OrganizationalUnitNotFoundException
     *                                    thrown if no organizational unit with that id exists
     * @throws SystemException            If an internal error occurred.
     */
    EscidocBinaryContent retrieveResource(final String id, final String resourceName)
        throws OperationNotFoundException, OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve the list of virtual Resources of the organizational unit.
     *
     * @param id The id of the organizational unit.
     * @return The xml representation of the resources sub resource.
     * @throws OrganizationalUnitNotFoundException
     *                         If an organizational unit with the provided id does not exist.
     * @throws SystemException If an internal error occurred.
     */
    String retrieveResources(final String id) throws OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve the md-records subresource of the organizational unit.
     *
     * @param id The id of the organizational unit.
     * @return The xml representation of the md-records sub resource.
     * @throws OrganizationalUnitNotFoundException
     *                         If an organizational unit with the provided id does not exist.
     * @throws SystemException If an internal error occurred.
     */
    String retrieveMdRecords(final String id) throws OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve a single md-record subresource of the organizational unit.<br/>
     *
     * @param id   The identifier of the Organizational Unit.
     * @param name The name of the md-record.
     * @return The xml representation of the md-record sub resource.
     * @throws MdRecordNotFoundException If the md-record does not exist.
     * @throws OrganizationalUnitNotFoundException
     *                                   If an organizational unit with the provided id does not exist.
     * @throws SystemException           If an internal error occurred.
     */
    String retrieveMdRecord(final String id, final String name) throws MdRecordNotFoundException,
        OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve the parents subresource of the organizational unit.
     *
     * @param id The id of the organizational unit.
     * @return The xml representation of the parents sub resource.
     * @throws OrganizationalUnitNotFoundException
     *                         If an organizational unit with the provided id does not exist.
     * @throws SystemException If an internal error occurred.
     */
    String retrieveParents(final String id) throws OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve the organizational units to that this organizational unit is subordinated.
     *
     * @param id The id of the organizational unit.
     * @return The XML representation of a list of references to the parent organizational units.
     * @throws OrganizationalUnitNotFoundException
     *                         If an organizational unit with the provided id does not exist.
     * @throws SystemException If an internal error occurred.
     */
    String retrieveParentObjects(final String id) throws OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve the organizational units that are subordinated to this organizational unit.
     *
     * @param id The id of the organizational unit.
     * @return The XML representation of a list of references to the child organizational units.
     * @throws OrganizationalUnitNotFoundException
     *                         If an organizational unit with the provided id does not exist.
     * @throws SystemException If an internal error occurred.
     */
    String retrieveChildObjects(final String id) throws OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve the list of paths of an organizational unit.
     *
     * @param id The id of the organizational unit.
     * @return The list of paths.
     * @throws OrganizationalUnitNotFoundException
     *                         If an organizational unit with the provided id does not exist.
     * @throws SystemException If an internal error occurred.
     */
    String retrievePathList(final String id) throws OrganizationalUnitNotFoundException, SystemException;

    /**
     * Filtered retrieval of organizational unit objects.
     *
     * @param parameters parameters from the SRU request
     * @return A list of organizational unit objects.
     * @throws SystemException If an internal error occurred.
     */
    String retrieveOrganizationalUnits(final SRURequestParameters parameters) throws SystemException;

    /**
     * Set status of Organizational Unit to opened.
     *
     * @param id        Objid of Organizational Unit.
     * @param taskParam TaskParam.
     * @return Organizational Unit in new representation.
     * @throws InvalidStatusException     If a parent is not in state 'created'.
     * @throws InvalidXmlException        Thrown if TaskParam is invalid.
     * @throws OptimisticLockingException Thrown if Organizational Unit was altered through third during update.
     * @throws OrganizationalUnitNotFoundException
     *                                    Thrown if Organizational Unit not exist.
     * @throws SystemException            Thrown if internal failure occurs.
     */
    String open(final String id, final String taskParam) throws InvalidStatusException, InvalidXmlException,
        OptimisticLockingException, OrganizationalUnitNotFoundException, SystemException;

    /**
     * Set status of Organizational Unit to closed.
     *
     * @param id        Objid of Organizational Unit.
     * @param taskParam TaskParam.
     * @return Organizational Unit in new representation.
     * @throws InvalidStatusException     If a parent is not in state 'closed'.
     * @throws InvalidXmlException        Thrown if TaskParam is invalid.
     * @throws OptimisticLockingException Thrown if Organizational Unit was altered through third during update.
     * @throws OrganizationalUnitNotFoundException
     *                                    Thrown if Organizational Unit not exist.
     * @throws SystemException            Thrown if internal failure occurs.
     */
    String close(final String id, final String taskParam) throws InvalidStatusException, InvalidXmlException,
        OptimisticLockingException, OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve list of successors of the selected Organizational Unit.
     *
     * @param objid Objid of Organizational Unit.
     * @return XML representation of Organizational Unit successors.
     * @throws OrganizationalUnitNotFoundException
     *                         Thrown if Organizational Unit not exist.
     * @throws SystemException Thrown if internal failure occurs.
     */
    String retrieveSuccessors(final String objid) throws OrganizationalUnitNotFoundException, SystemException;

}
