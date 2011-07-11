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
package de.escidoc.core.om.business.interfaces;

/**
 * Interface of an Admin-Descrictor Handler of the business layer.
 *
 * @author Torsten Tetteroo
 */
public interface AdminDescriptorHandlerInterface {

    /**
     * Creates a resource with the provided data.
     *
     * @param xmlData The data of the resource.
     * @return Returns the XML representation of the created resource, now containing the id by which the resource can
     *         be identified in the system.
     * @throws Exception
     */
    String create(String xmlData) throws Exception;

    /**
     * Deletes the specified resource.
     *
     * @param id The id of the resource.
     * @throws Exception
     */
    void delete(String id) throws Exception;

    /**
     * Retrieves the specified resource.
     *
     * @param id The id of the resource.
     * @return Returns the XML representation of the resource.
     * @throws Exception
     */
    String retrieve(String id) throws Exception;

    /**
     * Updates the specified resource with the provided data.
     *
     * @param id      The id of the resource.
     * @param xmlData The new data of the resource.
     * @return Returns the XML representation of the updated resource.
     * @throws Exception
     */
    String update(String id, String xmlData) throws Exception;

}
