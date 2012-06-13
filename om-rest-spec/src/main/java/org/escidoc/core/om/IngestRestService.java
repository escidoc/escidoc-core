/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
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

package org.escidoc.core.om;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import de.escidoc.core.common.exceptions.EscidocException;
import net.sf.oval.constraint.NotNull;
import org.escidoc.core.domain.result.ResultTypeTO;
import org.escidoc.core.utils.io.Stream;

/**
 * 
 * @author MIH
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Path("/ir/ingest")
public interface IngestRestService {

    @PUT
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    JAXBElement<ResultTypeTO> ingest(@NotNull Stream xmlStream) throws EscidocException;
}