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
package de.escidoc.core.om.business.stax.handler.container;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

@Configurable
public class BuildRelsExtMemberEntriesFromTaskParamHandlerNew extends DefaultHandler {

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    private String methodName;

    private final List<String> memberIds;

    private final List<String> memberIdsToRemove;

    private final String parentId;

    public BuildRelsExtMemberEntriesFromTaskParamHandlerNew(final String parentId, final String methodName) {
        this.parentId = parentId;
        this.memberIds = new ArrayList<String>();
        this.memberIdsToRemove = new ArrayList<String>();
        this.methodName = methodName;
    }

    public BuildRelsExtMemberEntriesFromTaskParamHandlerNew(final String parentId) {
        this.parentId = parentId;
        this.memberIds = new ArrayList<String>();
        this.memberIdsToRemove = new ArrayList<String>();
    }

    @Override
    public String characters(final String objid, final StartElement element) throws InvalidContentException,
        TripleStoreSystemException, WebserverSystemException {
        final String localName = element.getLocalName();

        if ("id".equals(localName)) {
            if ("add".equals(this.methodName)) {
                if (!this.tripleStoreUtility.exists(objid)) {

                    throw new InvalidContentException("Object with id " + objid
                        + " does not exist and can not be added to members of " + this.parentId + '.');
                }
            }
            if (this.tripleStoreUtility.isMemberOf(this.parentId, objid)) {
                memberIdsToRemove.add(objid);
            }
            else {
                memberIds.add(objid);
            }
        }
        return objid;
    }

    public List<String> getMemberIds() {
        return this.memberIds;
    }

    public List<String> getMemberIdsToRemove() {
        return this.memberIdsToRemove;
    }

}
