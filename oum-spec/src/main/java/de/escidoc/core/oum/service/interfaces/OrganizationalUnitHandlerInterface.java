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

package de.escidoc.core.oum.service.interfaces;

import de.escidoc.core.common.annotation.Validate;
import de.escidoc.core.common.business.interfaces.IngestableResource;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHasChildrenException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHierarchyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.Map;

import org.esidoc.core.utils.io.EscidocBinaryContent;

/**
 * The interface for access to an OrganizationalUnitHandler Service.
 *
 * @author Michael Schneider
 */
public interface OrganizationalUnitHandlerInterface extends IngestableResource {

    /**
     * Create an Organizational Unit.<br /> <br /> See chapter 4 for detailed information about input and output data
     * elements.<br /> Every Organizational Unit needs to have at least one (mandatory) metadata record describing the
     * details of this Organizational Unit. It is highly recommended to use the eSciDoc Organizational Unit metadata
     * profile (Namespace: http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit) for this metadata record.
     * The schema location is http://metadata.mpdl.mpg.de/escidoc /metadata/schemas/0.1/escidoc_organizationalunit_profile.xsd.
     * You may decide to use a differing schema, but you should be aware that might inhibit the successful deployment of
     * other eSciDoc solutions (e.g., PubMan). The same applies if you include additional metadata records in the
     * Organizational Unit representation.<br />
     * <p/>
     * <br /> <b>Tasks:</b><br /> <ul> <li>The XML data is validated against the XML schema of an Organizational
     * Unit.</li> <li>The following consistency checks are made: <ul> <li>The name is unique in relation to the name of
     * every other sibling Organizational Unit.</li> <li>The links to the parents are referencing Organizational
     * Units.</li> <li>All parents are in state "created" or "opened".</li> </ul> </li> <li>An Organizational Unit is
     * created from the provided data including a generated internal id.</li> <li>The status of the new Organizational
     * Unit is set to "created".</li> <li>Creator and creation date are added to the new Organizational Unit.</li>
     * <li>The new Organizational Unit is stored.</li> <li>The XML representation for the stored Organizational Unit is
     * created. </li> <li>The XML data is returned.</li> </ul>
     *
     * @param xml The XML representation of the Organizational Unit to be created corresponding to XML schema
     *            "organizational-unit.xsd".
     * @return The XML representation of the created Organizational Unit corresponding to XML schema
     *         "organizational-unit.xsd", including the generated id, the creator and creation date.
     * @throws InvalidStatusException         Thrown if a parent is not in state "created" or "opened".
     * @throws MissingAttributeValueException Thrown if a mandatory attribute is not set in XML data.
     * @throws MissingElementValueException   Thrown if a mandatory element is not found in XML data.
     * @throws MissingMethodParameterException
     *                                        Thrown if the XML data is not provided.
     * @throws MissingMdRecordException       If the required md-record is missing
     * @throws OrganizationalUnitNotFoundException
     *                                        Thrown if any of the included references to a parent is not valid.
     * @throws SystemException                Thrown in case of an internal error.
     * @throws AuthenticationException        Thrown if the authentication failed due to an invalid provided eSciDoc
     *                                        user handle.
     * @throws AuthorizationException         Thrown if the authorization failed.
     * @throws XmlCorruptedException          Thrown if the schema validation of the provided data failed.
     * @throws XmlSchemaValidationException   Thrown if the schema validation of the provided data failed.
     */
    @Validate(param = 0, resolver = "getOrganizationalUnitSchemaLocation", root = "organizational-unit")
    String create(String xml) throws AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingAttributeValueException, MissingElementValueException, MissingMethodParameterException,
        MissingMdRecordException, OrganizationalUnitNotFoundException, SystemException, XmlCorruptedException,
        XmlSchemaValidationException;

    /**
     * Delete an Organizational Unit. <br /> <br /> <b>Prerequisites:</b> <br /> <br /> The Organizational Unit must
     * exist and must be in state "created".<br /> <br /> The Organizational Unit must not have any children.<br /> <br
     * /> <b>Tasks:</b> <ul> <li>The Organizational Unit is removed.</li> </ul>
     *
     * @param id The identifier of the Organizational Unit.
     * @throws MissingMethodParameterException
     *                                 Thrown if the id is not provided.
     * @throws OrganizationalUnitNotFoundException
     *                                 Thrown if an Organizational Unit with the provided id does not exist.
     * @throws InvalidStatusException  Thrown if the Organizational Unit has an invalid status.
     * @throws OrganizationalUnitHasChildrenException
     *                                 Thrown if the Organizational Unit has children.
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    void delete(String id) throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, InvalidStatusException, OrganizationalUnitHasChildrenException,
        SystemException;

    /**
     * Retrieve the XML representation of an Organizational Unit.<br /> <br /> <b>Prerequisites:</b><br /> <br /> The
     * Organizational Unit must exist.<br /> <br /> <b>Tasks:</b> <ul> <li>The XML data for that Organizational Unit is
     * created.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param id The identifier of the Organizational Unit.
     * @return The XML representation of the Organizational Unit corresponding to XML schema "organizational-unit.xsd".
     * @throws MissingMethodParameterException
     *                                 Thrown if the XML data is not provided.
     * @throws OrganizationalUnitNotFoundException
     *                                 Thrown if an Organizational Unit with the provided id does not exist.
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    String retrieve(String id) throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException;

    /**
     * Update an Organizational Unit.<br /> Every Organizational Unit needs to have at least one (mandatory) metadata
     * record describing the details of this Organizational Unit. It is highly recommended to use the eSciDoc
     * Organizational Unit metadata profile (Namespace: http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit)
     * for this metadata record. The schema location is http://metadata.mpdl.mpg.de/escidoc
     * /metadata/schemas/0.1/escidoc_organizationalunit_profile.xsd. You may decide to use a differing schema, but you
     * should be aware that might inhibit the successful deployment of other eSciDoc solutions (e.g., PubMan). The same
     * applies if you include additional metadata records in the Organizational Unit representation.<br /> <br />
     * <b>Prerequisites:</b><br /> <br /> The Organizational Unit must exist.<br /> <br /> See chapter 4 for detailed
     * information about input and output data elements.<br /> <br /> <b>Tasks:</b> <ul> <li>The XML data is validated
     * against the XML schema of an Organizational Unit.</li> <li>The optimistic locking criteria is checked. <br /> The
     * LastModificationTimestamp provided must match the LastModificationTimestamp currently saved in the system.</li>
     * <li>The following consistency checks are made: <ul> <li>The name is unique in relation to the name of every other
     * sibling Organizational Unit.</li> <li>The links to the parents are referencing Organizational Units.</li> <li>The
     * new Organizational Unit hierarchy has no cycles, i.e. none of its parents is also one of its children.</li>
     * <li>The former list of parents may only be changed if the updated Organizational Unit is in status "created"</li>
     * <li>All parents are in status "created" or "opened".</li> </ul> </li> <li>The Organizational Unit is updated from
     * the provided data and stored. </li> <li>The XML data for the updated Organizational Unit is created.</li> <li>The
     * XML data is returned.</li> </ul>
     *
     * @param id  The identifier of the Organizational Unit.
     * @param xml The XML representation of the Organizational Unit to be updated corresponding to XML schema
     *            "organizational-unit.xsd".
     * @return The XML representation of the updated Organizational Unit corresponding to XML schema
     *         "organizational-unit.xsd", including the generated id, the creator and creation date.
     * @throws InvalidStatusException       Thrown if a parent is not in state "created" or "opened".
     * @throws InvalidXmlException          Thrown if the schema validation fails.
     * @throws MissingMethodParameterException
     *                                      Thrown if the XML data is not provided.
     * @throws OptimisticLockingException   Thrown if the Organizational Unit was changed in the meantime.
     * @throws OrganizationalUnitHierarchyViolationException
     *                                      Thrown if a parent is already a child of the Organizational Unit.
     * @throws OrganizationalUnitNotFoundException
     *                                      Thrown if an Organizational Unit with the provided id does not exist.
     * @throws MissingElementValueException Thrown if a mandatory element is not found in XML data.
     * @throws SystemException              Thrown in case of an internal error.
     * @throws AuthenticationException      Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                      handle.
     * @throws AuthorizationException       Thrown if the authorization failed.
     */
    @Validate(param = 1, resolver = "getOrganizationalUnitSchemaLocation", root = "organizational-unit")
    String update(String id, String xml) throws AuthenticationException, AuthorizationException,
        InvalidStatusException, InvalidXmlException, MissingMethodParameterException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException;

    /**
     * Update the md-records of an Organizational Unit.
     *
     * @param id  The identifier of the Organizational Unit.
     * @param xml The XML representation of the md-records.
     * @return The XML representation of the updated md-records.
     * @throws InvalidXmlException          Thrown if the schema validation fails.
     * @throws InvalidStatusException       Thrown if the Organizational Unit has an invalid status to add meta data.
     * @throws MissingMethodParameterException
     *                                      Thrown if the XML data is not provided.
     * @throws OptimisticLockingException   Thrown if the Organizational Unit was changed in the meantime.
     * @throws OrganizationalUnitNotFoundException
     *                                      Thrown if an Organizational Unit with the provided id does not exist.
     * @throws MissingElementValueException Thrown if a mandatory element is not found in XML data.
     * @throws SystemException              Thrown in case of an internal error.
     * @throws AuthenticationException      Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                      handle.
     * @throws AuthorizationException       Thrown if the authorization failed.
     */
    @Validate(param = 1, resolver = "getOrganizationalUnitSchemaLocation", root = "md-records")
    String updateMdRecords(String id, String xml) throws AuthenticationException, AuthorizationException,
        InvalidXmlException, InvalidStatusException, MissingMethodParameterException, OptimisticLockingException,
        OrganizationalUnitNotFoundException, MissingElementValueException, SystemException;

    /**
     * Update the parents of an Organizational Unit.<br />
     * <p/>
     * <br /> The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br />
     * <p/>
     * <br /> The Organizational Unit must exist.<br />
     * <p/>
     * <br /> The public-status is "opened".<br />
     * <p/>
     * <br /> <b>Tasks:</b> <ul> <li>Optimistic Locking criteria is checked.</li> <li>The XML data is returned.</li>
     * </ul>
     *
     * @param id  The identifier of the Organizational Unit.
     * @param xml The XML representation of the parents.
     * @return The XML representation of the updated parents.
     * @throws InvalidStatusException       Thrown if a parent is not in state "created" or "opened".
     * @throws InvalidXmlException          Thrown if the schema validation fails.
     * @throws MissingMethodParameterException
     *                                      Thrown if the XML data is not provided.
     * @throws OptimisticLockingException   Thrown if the Organizational Unit was changed in the meantime.
     * @throws OrganizationalUnitHierarchyViolationException
     *                                      Thrown if a parent is already a child of the Organizational Unit.
     * @throws OrganizationalUnitNotFoundException
     *                                      Thrown if an Organizational Unit with the provided id does not exist.
     * @throws MissingElementValueException Thrown if a mandatory element is not found in XML data.
     * @throws SystemException              Thrown in case of an internal error.
     * @throws AuthenticationException      Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                      handle.
     * @throws AuthorizationException       Thrown if the authorization failed.
     */
    @Validate(param = 1, resolver = "getOrganizationalUnitSchemaLocation", root = "parents")
    String updateParents(String id, String xml) throws AuthenticationException, AuthorizationException,
        InvalidStatusException, InvalidXmlException, MissingMethodParameterException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException;

    /**
     * Retrieve the sub resource "properties" of an Organizational Unit.<br />
     * <p/>
     * <br /> <b>Prerequisites:</b><br />
     * <p/>
     * <br /> The Organizational Unit must exist.<br />
     * <p/>
     * <br /> <b>Tasks:</b> <ul> <li>The XML data is returned.</li> </ul>
     *
     * @param id The identifier of the Organizational Unit.
     * @return The XML representation of the properties of that Organizational Unit corresponding to XML schema
     *         "organizational-unit.xsd".
     * @throws MissingMethodParameterException
     *                                 Thrown if the XML data is not provided.
     * @throws OrganizationalUnitNotFoundException
     *                                 Thrown if an Organizational Unit with the provided id does not exist.
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    String retrieveProperties(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve the content of the specified virtual resource of an Organizational Unit.
     *
     * @param id           The identifier of the Organizational Unit.
     * @param resourceName The name of the resource.
     * @return The content of the resource.
     * @throws OrganizationalUnitNotFoundException
     *                                    Thrown if an Organizational Unit with the specified id cannot be found.
     * @throws MissingMethodParameterException
     *                                    Thrown if no data is provided.
     * @throws OperationNotFoundException Thrown if there is no method configured for the given resource name.
     * @throws SystemException            Thrown in case of an internal error.
     * @throws AuthenticationException    Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                    handle.
     * @throws AuthorizationException     Thrown if the authorization failed.
     */
    EscidocBinaryContent retrieveResource(final String id, final String resourceName)
        throws OrganizationalUnitNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OperationNotFoundException, SystemException;

    /**
     * Retrieve the list of virtual resources of an Organizational Unit.<br /> <br /> This methods returns a list of
     * additional resources which aren't stored but created on request by the eSciDoc Infrastructure.<br /> <br />
     * <b>Prerequisites:</b><br /> <br /> The Organizational Unit must exist.<br /> <br /> <b>Tasks:</b> <ul>
     * <li>Determine which resources are available.</li> <li>Create the list of resources.</li> <li>The XML data is
     * returned.</li> </ul>
     *
     * @param id The identifier of the Organizational Unit.
     * @return The XML representation of the resources of that Organizational Unit.
     * @throws MissingMethodParameterException
     *                                 Thrown if the XML data is not provided.
     * @throws OrganizationalUnitNotFoundException
     *                                 Thrown if an Organizational Unit with the provided id does not exist.
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    String retrieveResources(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    /**
     * <br /> Retrieve the "md-records" sub resource of an Organizational Unit.<br />
     *
     * @param id The identifier of the Organizational Unit.
     * @return The XML representation of the md-records of that Organizational Unit corresponding to XML schema
     *         "organizational-unit.xsd".
     * @throws MissingMethodParameterException
     *                                 Thrown if the XML data is not provided.
     * @throws OrganizationalUnitNotFoundException
     *                                 Thrown if an Organizational Unit with the provided id does not exist.
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    String retrieveMdRecords(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve a single md-record sub resource of an Organizational Unit.<br />
     *
     * @param id   The identifier of the Organizational Unit.
     * @param name The name of the md-record.
     * @return The XML representation of the md-records of that Organizational Unit corresponding to XML schema
     *         "organizational-unit.xsd".
     * @throws MdRecordNotFoundException Thrown if the md-record does not exist.
     * @throws MissingMethodParameterException
     *                                   Thrown if any of the parameters is not provided.
     * @throws OrganizationalUnitNotFoundException
     *                                   Thrown if an Organizational Unit with the provided id does not exist.
     * @throws SystemException           Thrown in case of an internal error.
     * @throws AuthenticationException   Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                   handle.
     * @throws AuthorizationException    Thrown if the authorization failed.
     */
    String retrieveMdRecord(final String id, final String name) throws AuthenticationException, AuthorizationException,
        MdRecordNotFoundException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        SystemException;

    /**
     * Retrieve a list with references to all Organizational Units to that this Organizational Unit is subordinated.<br
     * /> <br /> <b>Prerequisites:</b><br /> <br /> The Organizational Unit must exist.<br /> <br /> <b>Tasks:</b> <ul>
     * <li>The XML representation of a list of references to the parent Organizational Units is created.</li> <li>The
     * XML data is returned.</li> </ul>
     *
     * @param id The identifier of the Organizational Unit.
     * @return The XML representation of the parents of that Organizational Unit corresponding to XML schema
     *         "organizational-unit.xsd" (only part "parents").
     * @throws MissingMethodParameterException
     *                                 Thrown if the XML data is not provided.
     * @throws OrganizationalUnitNotFoundException
     *                                 Thrown if an Organizational Unit with the provided id does not exist.
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    String retrieveParents(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve all Organizational Unit objects to that this Organizational Unit is subordinated.<br /> <br />
     * <b>Prerequisites:</b><br /> <br /> The Organizational Unit must exist.<br /> <br /> <b>Tasks:</b> <ul> <li>All
     * Organizational Units to that this Organizational Unit is subordinated are retrieved.</li> <li>The XML
     * representation of a list of complete Organizational Units is created.</li> <li>The XML data is returned.</li>
     * </ul>
     *
     * @param id The identifier of the Organizational Unit.
     * @return The XML representation of the list of parent Organizational Units corresponding to XML schema
     *         "organizational-unit-list.xsd".
     * @throws MissingMethodParameterException
     *                                 If the XML data is not provided.
     * @throws OrganizationalUnitNotFoundException
     *                                 If an Organizational Unit with the provided id does not exist.
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    String retrieveParentObjects(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve a list with references to all successors of this Organizational Unit (see schema for data structure).
     * <br /> <br /> <b>Prerequisites:</b><br /> <br /> The Organizational Unit must exist.<br /> <br /> <b>Tasks:</b>
     * <ul> <li>The XML representation of a list of references to the successors of this Organizational Unit is
     * compiled.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param id The identifier of the Organizational Unit.
     * @return The XML representation of successors of the Organizational Unit corresponding the
     *         organizational-unit-successors schema.
     * @throws MissingMethodParameterException
     *                                 Thrown if the XML data is not provided.
     * @throws OrganizationalUnitNotFoundException
     *                                 Thrown if an Organizational Unit with the provided id does not exist.
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    String retrieveSuccessors(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve the Organizational Units that are subordinated to this Organizational Unit.<br /> <br />
     * <b>Prerequisites:</b><br /> <br /> The Organizational Unit must exist.<br /> <br /> <b>Tasks:</b> <ul> <li>The
     * Organizational Units that are subordinated to this Organizational Unit are retrieved.</li> <li>The XML
     * representation of a list of child Organizational Units is created. This list contains references to the
     * objects.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param id The identifier of the Organizational Unit.
     * @return The XML representation of the list of child Organizational Units corresponding to XML schema
     *         "organizational-unit.xsd".
     * @throws MissingMethodParameterException
     *                                 Thrown if the XML data is not provided.
     * @throws OrganizationalUnitNotFoundException
     *                                 Thrown if an Organizational Unit with the provided id does not exist.
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    String retrieveChildObjects(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve the path list of an Organizational Unit. This is a list of all paths from a given Organizational Unit to
     * all its top level Organizational Units. Each path contains references to all Organizational Units of that
     * path.<br />
     * <p/>
     * <br /> <b>Prerequisites:</b><br />
     * <p/>
     * <br /> The Organizational Unit must exist.<br />
     * <p/>
     * <br /> <b>Tasks:</b> <ul> <li>The XML data is returned.</li> </ul> <br /> <b>A simple example:</b><br /> <br />
     * Following Organizational Unit structure is given:<br />
     * <p/>
     * <pre>
     * OrgA
     * </pre>
     * <p/>
     * <pre>
     *     |_ OrgC
     * </pre>
     * <p/>
     * <pre>
     * OrgB
     * </pre>
     * <p/>
     * <pre>
     *     |_ OrgD
     * </pre>
     * <p/>
     * <pre>
     *           |_ OrgC
     * </pre>
     * <p/>
     * The service will return the following result in an appropriate XML structure when called with the id of OrgC:
     * <p/>
     * <pre>
     * -Path:
     * </pre>
     * <p/>
     * <pre>
     * -OrgA
     * </pre>
     * <p/>
     * <pre>
     * -OrgC
     * </pre>
     * <p/>
     * <pre>
     * -Path:
     * </pre>
     * <p/>
     * <pre>
     * -OrgB
     * </pre>
     * <p/>
     * <pre>
     * -OrgD
     * </pre>
     * <p/>
     * <pre>
     * -OrgC
     * </pre>
     *
     * @param id The identifier of the Organizational Unit.
     * @return The XML representation of the path list of that Organizational Unit corresponding to XML schema
     *         "organizational-unit-path-list.xsd"
     * @throws MissingMethodParameterException
     *                                 Thrown if the XML data is not provided.
     * @throws OrganizationalUnitNotFoundException
     *                                 Thrown if an Organizational Unit with the provided id does not exist.
     * @throws SystemException         Thrown in case of an internal error.
     * @throws AuthenticationException Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                 handle.
     * @throws AuthorizationException  Thrown if the authorization failed.
     */
    String retrievePathList(final String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    /**
     * Change the state of an Organizational Unit to "opened". <br /> <br /> <b>Prerequisites:</b><br /> <br /> The
     * Organizational Unit must exist and must be in state "created". <br /> <br /> Any parent of the Organizational
     * Unit must be in state "opened". <br /> <br /> <b>Tasks:</b> <ul> <li>The optimistic locking criteria is
     * checked.</li> <li>The state of the Organizational Unit is set to "opened".</li> </ul> <br /> <br /> <div
     * id="example_open_task_param" title="Task parameter for open">
     * <p/>
     * <pre>
     * &lt;param last-modification-date=&quot;1967-08-13T12:00:00.000+01:00&quot;/&gt;
     * </pre>
     * <p/>
     * </div>
     *
     * @param id        The identifier of the Organizational Unit.
     * @param taskParam The time stamp of the last modification of the Organizational Unit (see example above).
     * @return last-modification-date within XML (result.xsd)
     * @throws InvalidStatusException     Thrown if the Organizational Unit is not in public-status "created".
     * @throws InvalidXmlException        Thrown if the schema validation fails.
     * @throws MissingMethodParameterException
     *                                    Thrown if the XML data is not provided.
     * @throws OptimisticLockingException Thrown if the Organizational Unit was changed in the meantime.
     * @throws OrganizationalUnitNotFoundException
     *                                    Thrown if an Organizational Unit with the provided id does not exist.
     * @throws SystemException            Thrown in case of an internal error.
     * @throws AuthenticationException    Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                    handle.
     * @throws AuthorizationException     Thrown if the authorization failed.
     */
    String open(String id, String taskParam) throws AuthenticationException, AuthorizationException,
        InvalidStatusException, InvalidXmlException, MissingMethodParameterException, OptimisticLockingException,
        OrganizationalUnitNotFoundException, SystemException;

    /**
     * Change the state of an Organizational Unit to "closed".<br /> <br /> <b>Prerequisites:</b><br /> <br /> The
     * Organizational Unit must exist and must be in state "opened".<br /> <br /> The Organizational Unit must not have
     * any children in state "created" or "opened".<br /> <br /> <b>Tasks:</b> <ul> <li>The optimistic locking criteria
     * is checked.</li> <li>The state of the Organizational Unit is set to "closed".</li> </ul> <br /> <br /> <div
     * id="example_close_task_param" title="Task parameter for close">
     * <p/>
     * <pre>
     * &lt;param last-modification-date=&quot;1967-08-13T12:00:00.000+01:00&quot;/&gt;
     * </pre>
     * <p/>
     * </div>
     *
     * @param id        The identifier of the Organizational Unit.
     * @param taskParam The time stamp of the last modification of the Organizational Unit (see example above).
     * @return last-modification-date within XML (result.xsd)
     * @throws InvalidStatusException     Thrown if the Organizational Unit is not in public-status "created".
     * @throws InvalidXmlException        Thrown if the schema validation fails.
     * @throws MissingMethodParameterException
     *                                    Thrown if the XML data is not provided.
     * @throws OptimisticLockingException Thrown if the Organizational Unit was changed in the meantime.
     * @throws OrganizationalUnitNotFoundException
     *                                    Thrown if an Organizational Unit with the provided id does not exist.
     * @throws SystemException            Thrown in case of an internal error.
     * @throws AuthenticationException    Thrown if the authentication failed due to an invalid provided eSciDoc user
     *                                    handle.
     * @throws AuthorizationException     Thrown if the authorization failed.
     */
    String close(String id, String taskParam) throws AuthenticationException, AuthorizationException,
        InvalidStatusException, InvalidXmlException, MissingMethodParameterException, OptimisticLockingException,
        OrganizationalUnitNotFoundException, SystemException;

    /**
     * Retrieve a list of complete Organizational Units applying filters. <br /> <br /> <b>Tasks:</b> <ul> <li>Check
     * whether all filter names are valid.</li> <li>All Organizational Units matching the given filter criteria are
     * retrieved.</li> <li>The XML representation of the list of Organizational Units corresponding to SRW schema is
     * returned as output.</li> </ul> See chapter "Filters" for detailed information about filter definitions.
     * <p/>
     * Special filters for this method are: <ul> <li><br /> top-level-organizational-units<br />
     * <p/>
     * If this filter is defined only Organizational Unit objects that have no associated parent are returned.</li>
     * </ul>
     *
     * @param filter The filter criteria to select the Organizational Units as a map of key - value pairs
     * @return The XML representation of the created list of Organizational Units corresponding to the SRW schema.
     * @throws InvalidSearchQueryException Thrown if the given search query could not be translated into a SQL query.
     * @throws InvalidXmlException         Thrown if the schema validation fails.
     * @throws MissingMethodParameterException
     *                                     Thrown if the XML data is not provided.
     * @throws SystemException             Thrown in case of an internal error.
     */
    String retrieveOrganizationalUnits(final Map<String, String[]> filter) throws InvalidSearchQueryException,
        InvalidXmlException, MissingMethodParameterException, SystemException;
}
