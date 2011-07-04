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
package de.escidoc.core.om.business.security;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.Container;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.RelsExtRefListExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to filter elements out where the user permissions are restricted.
 *
 * @author Steffen Wagner
 */
@Configurable
public class UserFilter {

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    /**
     * Get the list of the member (structural relation) of the Container.
     *
     * @param container
     * @return List of Container member (if {@code filter != null} filtered)
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    public List<String> getMemberRefList(final Container container) throws MissingMethodParameterException,
        SystemException {

        final List<String> memberRefs;

        if (container.getVersionNumber() == null) {
            memberRefs = this.tripleStoreUtility.getContainerMemberList(container.getId(), null, null);
        }
        else {
            // A work around until Fedora makes restrictions on the FOXML-size:
            // RELS-EXT is now unversioned and therefore a Stream
            // Escidoc_RELS_EXT
            // with a managed content must be parsed to fetch values for old
            // Container
            // versions
            final List<String> predicates = new ArrayList<String>();
            predicates.add(Constants.STRUCTURAL_RELATIONS_NS_URI + "member");
            final StaxParser sp = new StaxParser();
            final RelsExtRefListExtractor rerle = new RelsExtRefListExtractor(predicates);
            sp.addHandler(rerle);
            try {
                sp.parse(container.getEscidocRelsExt().getStream());
            }
            catch (final Exception e) {
                throw new XmlParserSystemException("Unexpected exception.", e);
            }
            memberRefs = rerle.getEntries().get(Constants.STRUCTURAL_RELATIONS_NS_URI + "member");
        }
        return memberRefs;
    }
}
