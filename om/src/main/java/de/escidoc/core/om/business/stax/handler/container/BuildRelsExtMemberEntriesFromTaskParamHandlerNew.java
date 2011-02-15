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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class BuildRelsExtMemberEntriesFromTaskParamHandlerNew extends DefaultHandler {

    private String methodName;

    private List<String> memberIds = null;

    private List<String> memberIdsToRemove = null;

    private String parentId;

    public BuildRelsExtMemberEntriesFromTaskParamHandlerNew(String parentId, String methodName) {
        this.parentId = parentId;
        this.memberIds = new ArrayList<String>();
        this.memberIdsToRemove = new ArrayList<String>();
        this.methodName = methodName;
    }

    public BuildRelsExtMemberEntriesFromTaskParamHandlerNew(String parentId) {
        this.parentId = parentId;
        this.memberIds = new ArrayList<String>();
        this.memberIdsToRemove = new ArrayList<String>();
    }

    public String characters(String data, StartElement element)
        throws InvalidContentException, TripleStoreSystemException,
        WebserverSystemException {
        String localName = element.getLocalName();

        if (localName.equals("id")) {
            String objid = data;

            if (!TripleStoreUtility.getInstance().exists(objid)) {
                if (this.methodName.equals("add")) {
                throw new InvalidContentException("Object with id " + objid
                    + " does not exist and can not be added to members of "
                    + parentId + ".");
                } else if (this.methodName.equals("remove")) {
                    throw new InvalidContentException("Object with id " + objid
                        + " does not exist and can not be removed from members of "
                        + parentId + ".");
                    }
            }
            if (!TripleStoreUtility.getInstance().isMemberOf(parentId, objid)) {
                memberIds.add(objid);
            }
            else {
                memberIdsToRemove.add(objid);
            }
        }

        return data;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public List<String> getMemberIdsToRemove() {
        return memberIdsToRemove;
    }

}
