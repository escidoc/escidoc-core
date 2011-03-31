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

package de.escidoc.core.aa.service.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Interface of a handler managing eSciDoc actions.
 *
 * @author Torsten Tetteroo
 */
public interface ActionHandlerInterface {

    /**
     * Defines which actions shall not be secured by the role based authorization for a specified context.
     *
     * @param contextId The Context ID within the actions shall be unsecured, i.e. shall be available to the public.
     * @param actions   The list of actions that shall be unsecured within the context.
     * @return Returns the list of unsecured actions.
     * @throws ContextNotFoundException     Thrown if a context with the provided id does not exist.
     * @throws XmlCorruptedException        Thrown if the provided list of actions is not valid Xml.
     * @throws XmlSchemaValidationException Thrown if the provided list of actions is not valid Xml.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal error.
     */
    String createUnsecuredActions(String contextId, String actions) throws ContextNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, AuthenticationException, AuthorizationException,
        SystemException;

    /**
     * Retrieves the list of actions that are not secured by the role based authorization for the specified context.
     *
     * @param contextId The Context ID for that the list of unsecured actions shall be returned.
     * @return Returns the list of unsecured actions.
     * @throws ContextNotFoundException Thrown if a context with the provided id does not exist.
     * @throws AuthenticationException  Thrown if the authentication fails due to an invalid provided
     *                                  eSciDocUserHandle.
     * @throws AuthorizationException   Thrown if the authorization fails.
     * @throws SystemException          Thrown in case of an internal error.
     */
    String retrieveUnsecuredActions(String contextId) throws ContextNotFoundException, AuthenticationException,
        AuthorizationException, SystemException;

    /**
     * Deletes the list of actions that shall not be secured by the role based authorization. <br> After calling this
     * method, all methods will be secured within the context..
     *
     * @param contextId The Context ID for that all actions shall be secured by role based authorization.
     * @throws ContextNotFoundException Thrown if a context with the provided id does not exist.
     * @throws AuthenticationException  Thrown if the authentication fails due to an invalid provided
     *                                  eSciDocUserHandle.
     * @throws AuthorizationException   Thrown if the authorization fails.
     * @throws SystemException          Thrown in case of an internal error.
     */
    void deleteUnsecuredActions(String contextId) throws ContextNotFoundException, AuthenticationException,
        AuthorizationException, SystemException;

}
