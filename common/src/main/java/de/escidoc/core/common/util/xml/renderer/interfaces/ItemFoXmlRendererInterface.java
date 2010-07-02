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
package de.escidoc.core.common.util.xml.renderer.interfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * Interface of an item foxml renderer.
 * 
 * @author ROF
 * @om
 */
public interface ItemFoXmlRendererInterface {

    /**
     * Gets the foxml representation of an item.
     * 
     * @param values
     *            The values of the item.
     * @return Returns the foxml representation of the item.
     * @throws SystemException
     *             Thrown in case of an internal error.
     * @om
     */
    String renderItem(
        final Map<String, Object> values, final String itemId,
        final String lastModificationDate, final String[] components,
        final HashMap<String, String> properties,
        final Vector<Map<String, String>> contentRelations,
        final HashMap<String, String> propertiesAsReferences,
        final HashMap<String, String> propertiesVersion) throws SystemException;

    /**
     * Gets the foxml representation of a component.
     * 
     * @param values
     *            The values of the component.
     * @return Returns the foxml representation of the component.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @om
     */
    String renderComponent(final Map<String, Object> values)
        throws WebserverSystemException;

    /**
     * Render RELS-EXT of an Item.
     * 
     * @param properties
     * @param title
     * @param members
     * @param adminDescriptorId
     * @param itemId
     * @param lastModificationDate
     * @param contentRelations
     * @param comment
     * @return RELS-EXT XML representation of Item
     * 
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    String renderItemRelsExt(
        final String id, final String lastModificationDate,
        final String[] components, final HashMap<String, String> properties,
        final Vector<Map<String, String>> contentRelations,
        final HashMap<String, String> propertiesAsReferences,
        final HashMap<String, String> propertiesVersion)
        throws WebserverSystemException;

    /**
     * Render RELS-EXT of a Component.
     * 
     * @param id
     *            Objid of Component.
     * @param properties
     *            Component properties
     * @param inCreate
     *            Set true if Component is to create, false if it's an update.
     * @return RELS-EXT XML representation of Component
     * 
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    String renderComponentRelsExt(
        final String id, final Map<String, String> properties,
        final boolean inCreate) throws WebserverSystemException;

    String renderDefaultDc(final String componentId)
        throws WebserverSystemException;

    String renderWov(
        final String id, final String title, final String versionNo,
        final String lastModificationDate, final String versionStatus,
        final String comment) throws WebserverSystemException;
}
