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

import de.escidoc.core.common.exceptions.EscidocException;

/**
 * This interface unifies all handlers in order to create a single ingest interface that can handle any given resource.
 * Each handler which needs to be part of this ingest has to implement this interface via an extension of its
 * implemented interface.
 *
 * @author Kai Strnad
 */
public interface IngestFacade {

    /**
     * Ingest the given resource without prior knowledge of its type. As any implementing interface can throw different
     * checked exceptions the assumptions regarding exceptions have to be fairly generic.
     *
     * @param xmlData The resource which is to ingest as XML.
     * @return Returns the object id.
     * @throws EscidocException Thrown if ingest failed.
     */
    String ingest(final String xmlData) throws EscidocException;

}
