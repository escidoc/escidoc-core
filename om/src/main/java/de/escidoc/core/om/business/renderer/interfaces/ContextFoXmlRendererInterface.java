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
package de.escidoc.core.om.business.renderer.interfaces;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.Map;

/**
 * Interface of a context foxml renderer.
 *
 * @author André Schenk
 */
public interface ContextFoXmlRendererInterface {

    /**
     * Gets the foxml representation of a context.
     *
     * @param values The values of the context.
     * @return Returns the foxml representation of the context.
     * @throws SystemException Thrown in case of an internal error.
     */
    String render(final Map<String, Object> values) throws SystemException;

    /**
     * Gets the foxml representation of the {@code RELS-EXT} datastream of a context.
     *
     * @param values The values of the context to render.
     * @return Returns the foxml representation of the {@code RELS-EXT} datastream of a context.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String renderRelsExt(final Map<String, Object> values) throws WebserverSystemException;

    /**
     * Gets the foxml representation of the {@code DC} datastream of a context.
     *
     * @param values The values of the context to render.
     * @return Returns the foxml representation of the {@code DC} datastream of a context.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    String renderDc(final Map<String, Object> values) throws WebserverSystemException;
}
