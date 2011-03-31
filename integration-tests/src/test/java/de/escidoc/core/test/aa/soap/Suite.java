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
package de.escidoc.core.test.aa.soap;

import org.junit.runner.RunWith;

/**
 * The AA test suite (SOAP).
 *
 * @author Torsten Tetteroo
 */
@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses( { AdministratorSoapTest.class, DefaultPoliciesSoapTest.class,
    DepositorSoapTest.class, ModeratorSoapTest.class, StatisticEditorSoapTest.class, StatisticReaderSoapTest.class,
    CollaboratorSoapTest.class, CollaboratorModifierSoapTest.class, CollaboratorModifierAddRemoveMembersSoapTest.class,
    CollaboratorModifierAddRemoveAnyMembersSoapTest.class, CollaboratorModifierUpdateDirectMembersSoapTest.class,
    CollaboratorModifierUpdateAnyMembersSoapTest.class, ContentRelationManagerSoapTest.class,
    ContentRelationModifierSoapTest.class, UserGroupAdminSoapTest.class, UserGroupInspectorSoapTest.class,
    UserAccountAdminSoapTest.class, UserAccountInspectorSoapTest.class, OrgUnitAdminSoapTest.class,
    ContextAdminSoapTest.class, ContextModifierSoapTest.class,

    PdpSoapTest.class, RoleSoapTest.class,

    UserAccountGrantSoapTest.class, UserAccountSoapTest.class, UserAttributeSoapTest.class, UserGroupSoapTest.class,
    UserGroupGrantSoapTest.class, UserManagementWrapperSoapTest.class, UserPreferenceSoapTest.class,

    GrantFilterSoapTest.class })
public class Suite {

}
