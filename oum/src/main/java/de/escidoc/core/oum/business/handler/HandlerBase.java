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
package de.escidoc.core.oum.business.handler;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import de.escidoc.core.common.util.xml.stax.interfaces.DefaultHandlerStackInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Base class for stax handlers.
 *
 * @author Michael Schneider
 */
@Configurable
public class HandlerBase extends DefaultHandler {

    private StaxParser parser;

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    @Autowired
    @Qualifier("business.Utility")
    private Utility utility;

    /**
     * @param parser The stax parser.
     */
    public HandlerBase(final StaxParser parser) {

        this.parser = parser;
    }

    /**
     * @return the parser
     */
    public DefaultHandlerStackInterface getParser() {

        return this.parser;
    }

    /**
     * @param parser the parser to set
     */
    public void setParser(final StaxParser parser) {

        this.parser = parser;
    }

    /**
     * Gets the {@link TripleStoreUtility}.
     *
     * @return TripleStoreUtility Returns the {@link TripleStoreUtility} object.
     */
    protected TripleStoreUtility getTripleStoreUtility() {

        return this.tripleStoreUtility;
    }

    /**
     * @return An instance of the Utility.
     */
    public Utility getUtility() {

        return this.utility;
    }
}
