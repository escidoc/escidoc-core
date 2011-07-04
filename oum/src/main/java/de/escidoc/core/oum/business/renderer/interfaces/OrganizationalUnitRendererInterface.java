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
package de.escidoc.core.oum.business.renderer.interfaces;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.oum.business.fedora.resources.OrganizationalUnit;

import java.util.List;

/**
 * Interface of an organizational unit renderer.
 *
 * @author Michael Schneider
 */
public interface OrganizationalUnitRendererInterface {

    /**
     * Gets the representation of an organizational unit.
     *
     * @param organizationalUnit The organizational unit to render.
     * @return Returns the XML representation of the organizational unit.
     * @throws SystemException Thrown in case of an internal error.
     */
    String render(final OrganizationalUnit organizationalUnit) throws SystemException;

    /**
     * Gets the representation of the sub resource {@code properties} of an organizational unit.
     *
     * @param organizationalUnit The organizational unit to render.
     * @return Returns the XML representation of the sub resource {@code properties} of an organizational unit.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String renderProperties(final OrganizationalUnit organizationalUnit) throws WebserverSystemException;

    /**
     * Gets the representation of the sub resource {@code resources} of an organizational unit.
     *
     * @param organizationalUnit The organizational unit to render.
     * @return Returns the XML representation of the sub resource {@code resources} of an organizational unit.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String renderResources(final OrganizationalUnit organizationalUnit) throws WebserverSystemException;

    /**
     * Gets the representation of the sub resource {@code md-records} of an organizational unit.
     *
     * @param organizationalUnit The organizational unit to render.
     * @return Returns the XML representation of the sub resource {@code md-records} of an organizational unit.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String renderMdRecords(final OrganizationalUnit organizationalUnit) throws WebserverSystemException;

    /**
     * Gets the representation of a single sub resource {@code md-record} of an organizational unit.
     *
     * @param organizationalUnit The organizational unit to render.
     * @param name               The name of the md-record.
     * @return Returns the XML representation of the sub resource {@code md-records} of an organizational unit.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String renderMdRecord(final OrganizationalUnit organizationalUnit, final String name)
        throws WebserverSystemException;

    /**
     * Gets the representation of the sub resource {@code parents} of an organizational unit.
     *
     * @param organizationalUnit The organizational unit to render.
     * @return Returns the XML representation of the sub resource {@code parents} of an organizational unit.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderParents(final OrganizationalUnit organizationalUnit) throws SystemException;

    /**
     * Gets the representation of the virtual sub resource {@code children} of an organizational unit.
     *
     * @param organizationalUnit The organizational unit to render.
     * @param children           The children of the organizational unit.
     * @return Returns the XML representation of the virtual sub resource {@code children} of the organizational
     *         unit.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderChildObjects(final OrganizationalUnit organizationalUnit, final List<String> children)
        throws SystemException;

    /**
     * Gets the representation of the virtual sub resource {@code parents} of an organizational unit.
     *
     * @param organizationalUnit The organizational unit to render.
     * @param parents            The list of parent objects of the organizational unit.
     * @return Returns the XML representation of the virtual sub resource {@code parents} of an organizational
     *         unit.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderParentObjects(final OrganizationalUnit organizationalUnit, final List<String> parents)
        throws SystemException;

    /**
     * Gets the representation of the virtual sub resource {@code path-list} of an organizational unit.
     *
     * @param organizationalUnit The organizational unit to render.
     * @param pathes             The path-list of the organizational unit.
     * @return Returns the XML representation of the virtual sub resource {@code path-list} of an organizational
     *         unit.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderPathList(final OrganizationalUnit organizationalUnit, final List<List<String>> pathes)
        throws SystemException;

    /**
     * Gets the successor representation of the {@code organizational unit} .
     *
     * @param organizationalUnit The Organizational Unit.
     * @return Returns the XML representation of the virtual sub resource {@code successors} of an organizational
     *         unit.
     * @throws SystemException Thrown in case of an internal error.
     */
    String renderSuccessors(final OrganizationalUnit organizationalUnit) throws SystemException;
}
