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

package de.escidoc.core.common.business.interfaces;

import de.escidoc.core.common.exceptions.EscidocException;

/**
 * This interface marks all extending interfaces as "ingestable", this is a means of integrating all handlers so that
 * they all comply to this contract.
 *
 * @author Kai Strnad
 */
public interface IngestableResource {

    /**
     * Ingest a resource consisting of an xml string.
     *
     * @param xmlData XML representation of the resource.
     * @return Returns the identifier given to this particular resource.
     * @throws EscidocException Any exception within the Escidoc realm. This is necessary due to the different handlers
     *                          all throwing different exceptions for the same operation.
     */
    String ingest(String xmlData) throws EscidocException;

}
