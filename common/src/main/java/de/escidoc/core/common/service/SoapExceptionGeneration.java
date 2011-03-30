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

/**
 * 
 */
package de.escidoc.core.common.service;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.ApplicationException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidAggregationTypeException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentModelException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidItemStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidPidException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidRelationPropertiesException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidScopeException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSqlException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreOutputFormatException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.ReferenceCycleException;
import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.exceptions.application.invalid.ValidationException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingLicenceException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.missing.MissingParameterException;
import de.escidoc.core.common.exceptions.application.missing.MissingUserListException;
import de.escidoc.core.common.exceptions.application.notfound.ActionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.AdminDescriptorNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.AggregationDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.AggregationTypeNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.IndexNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.IngestionDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.IngestionSourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.IngestionTaskNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemReferenceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.PidNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationTypeNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RevisionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.SearchNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StructuralMapEntryNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.TargetBasketNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.TaskListNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.TaskNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.TransitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.VersionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.XmlSchemaNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.AdminDescriptorViolationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyDeletedException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyPublishedException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyWithdrawnException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.NotPublishedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHierarchyViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException;
import de.escidoc.core.common.exceptions.application.violated.RelationRuleViolationException;
import de.escidoc.core.common.exceptions.application.violated.ResourceInUseException;
import de.escidoc.core.common.exceptions.application.violated.RoleInUseViolationException;
import de.escidoc.core.common.exceptions.application.violated.RuleViolationException;
import de.escidoc.core.common.exceptions.application.violated.ScopeContextViolationException;
import de.escidoc.core.common.exceptions.application.violated.TimeFrameViolationException;
import de.escidoc.core.common.exceptions.application.violated.UserGroupHierarchyViolationException;
import de.escidoc.core.common.exceptions.system.ApplicationServerSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.FileSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.service.interfaces.SoapExceptionGenerationInterface;

/**
 * @author Michael Hoppe
 */
public class SoapExceptionGeneration
    implements SoapExceptionGenerationInterface {



    /**
     * See Interface for functional description.
     * 
     * @throws EscidocException
     * @throws AggregationDefinitionNotFoundException
     * @throws ApplicationException
     * @throws ValidationException
     * @throws ResourceNotFoundException
     * @throws MissingAttributeValueException
     * @throws MissingElementValueException
     * @throws MissingParameterException
     * @throws RuleViolationException
     * @throws InvalidContextException
     * @throws InvalidContextStatusException
     * @throws InvalidAggregationTypeException
     * @throws InvalidContentException
     * @throws InvalidItemStatusException
     * @throws ReadonlyVersionException
     * @throws InvalidContentModelException
     * @throws InvalidPidException
     * @throws InvalidRelationPropertiesException
     * @throws InvalidScopeException
     * @throws InvalidSearchQueryException
     * @throws InvalidSqlException
     * @throws InvalidStatusException
     * @throws InvalidXmlException
     * @throws ReferenceCycleException
     * @throws XmlCorruptedException
     * @throws XmlSchemaValidationException
     * @throws MissingLicenceException
     * @throws MissingMethodParameterException
     * @throws MissingContentException
     * @throws MissingUserListException
     * @throws MissingMdRecordException
     * @throws ActionNotFoundException
     * @throws ContextNotFoundException
     * @throws AdminDescriptorNotFoundException
     * @throws AggregationTypeNotFoundException
     * @throws ComponentNotFoundException
     * @throws ContainerNotFoundException
     * @throws ItemNotFoundException
     * @throws ItemReferenceNotFoundException
     * @throws ContentModelNotFoundException
     * @throws FileNotFoundException
     * @throws IndexNotFoundException
     * @throws IngestionDefinitionNotFoundException
     * @throws IngestionSourceNotFoundException
     * @throws IngestionTaskNotFoundException
     * @throws MdRecordNotFoundException
     * @throws OptimisticLockingException
     * @throws OrganizationalUnitHierarchyViolationException
     * @throws PidNotFoundException
     * @throws RelationNotFoundException
     * @throws RelationTypeNotFoundException
     * @throws ReportDefinitionNotFoundException
     * @throws RevisionNotFoundException
     * @throws RoleNotFoundException
     * @throws ScopeContextViolationException
     * @throws ScopeNotFoundException
     * @throws SearchNotFoundException
     * @throws ContentRelationNotFoundException
     * @throws ContentRelationNotFoundException
     * @throws ReferencedResourceNotFoundException
     * @throws RelationPredicateNotFoundException
     * @throws StructuralMapEntryNotFoundException
     * @throws TargetBasketNotFoundException
     * @throws TaskListNotFoundException
     * @throws TaskNotFoundException
     * @throws TransitionNotFoundException
     * @throws UserAccountNotFoundException
     * @throws UserGroupNotFoundException
     * @throws UserNotFoundException
     * @throws VersionNotFoundException
     * @throws XmlSchemaNotFoundException
     * @throws AdminDescriptorViolationException
     * @throws AlreadyDeletedException
     * @throws AlreadyExistsException
     * @throws AlreadyPublishedException
     * @throws AlreadyWithdrawnException
     * @throws LockingException
     * @throws NotPublishedException
     * @throws ReadonlyViolationException
     * @throws ReadonlyElementViolationException
     * @throws ReadonlyAttributeViolationException
     * @throws RelationRuleViolationException
     * @throws RoleInUseViolationException
     * @throws TimeFrameViolationException
     * @throws InvalidTripleStoreQueryException
     * @throws InvalidTripleStoreOutputFormatException
     * @throws SystemException
     * @throws ApplicationServerSystemException
     * @throws FedoraSystemException
     * @throws FileSystemException
     * @throws IntegritySystemException
     * @throws SqlDatabaseSystemException
     * @throws TripleStoreSystemException
     * @throws WebserverSystemException
     * @throws XmlParserSystemException
     * @throws UserGroupHierarchyViolationException
     */
    @Override
    public void generateExceptions() throws EscidocException,
        AggregationDefinitionNotFoundException, ApplicationException,
        ValidationException, ResourceNotFoundException,
        MissingAttributeValueException, MissingElementValueException,
        MissingParameterException, RuleViolationException,
        InvalidContextException, InvalidContextStatusException,
        InvalidAggregationTypeException, InvalidContentException,
        InvalidItemStatusException, ReadonlyVersionException,
        InvalidContentModelException, InvalidPidException,
        InvalidRelationPropertiesException, InvalidSearchQueryException,
        InvalidScopeException, InvalidSqlException, InvalidStatusException,
        InvalidXmlException, ReferenceCycleException, XmlCorruptedException,
        XmlSchemaValidationException, MissingLicenceException,
        MissingMethodParameterException, MissingContentException,
        MissingUserListException, MissingMdRecordException,
        ActionNotFoundException, ContextNotFoundException,
        AdminDescriptorNotFoundException, AggregationTypeNotFoundException,
        ComponentNotFoundException, ContainerNotFoundException,
        ItemNotFoundException, ItemReferenceNotFoundException,
        ContentModelNotFoundException, FileNotFoundException,
        IndexNotFoundException, IngestionDefinitionNotFoundException,
        IngestionSourceNotFoundException, IngestionTaskNotFoundException,
        MdRecordNotFoundException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, PidNotFoundException,
        RelationNotFoundException, RelationTypeNotFoundException,
        ReportDefinitionNotFoundException, RevisionNotFoundException,
        RoleNotFoundException, ScopeContextViolationException,
        ScopeNotFoundException, SearchNotFoundException,
        ContentRelationNotFoundException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException,
        StructuralMapEntryNotFoundException, TargetBasketNotFoundException,
        TaskListNotFoundException, TaskNotFoundException,
        TransitionNotFoundException, UserAccountNotFoundException,
        UserGroupNotFoundException, UserNotFoundException,
        VersionNotFoundException, XmlSchemaNotFoundException,
        AdminDescriptorViolationException, AlreadyDeletedException,
        AlreadyExistsException, AlreadyPublishedException,
        AlreadyWithdrawnException, LockingException, NotPublishedException,
        ReadonlyViolationException, ReadonlyElementViolationException,
        ReadonlyAttributeViolationException, RelationRuleViolationException,
        RoleInUseViolationException, TimeFrameViolationException,
        ContentRelationNotFoundException, InvalidTripleStoreQueryException,
        InvalidTripleStoreOutputFormatException, SystemException,
        ApplicationServerSystemException, FedoraSystemException,
        FileSystemException, IntegritySystemException,
        SqlDatabaseSystemException, TripleStoreSystemException,
        WebserverSystemException, XmlParserSystemException, TmeException,
        UserGroupHierarchyViolationException, ResourceInUseException {
    }


}
