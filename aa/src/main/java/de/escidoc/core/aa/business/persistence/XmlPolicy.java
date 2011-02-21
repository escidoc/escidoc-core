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
package de.escidoc.core.aa.business.persistence;

/**
 * The persistence domain object for the XACML policies.
 * 
 * @author Roland Werner (Accenture)
 * @hibernate.mapping schema = "aa"
 * @hibernate.class table = "xml_policies"
 */
public class XmlPolicy {
    private int id;

    private String userId;

    private String action;

    private String xml;

    /**
     * @return Returns the id.
     * @hibernate.id generator-class = "increment"
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * @return Returns the action.
     * @hibernate.property column = "action"
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action
     *            The action to set.
     */
    public void setAction(final String action) {
        this.action = action;
    }

    /**
     * @return Returns the userId.
     * @hibernate.property column = "user_id"
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(final String userId) {
        this.userId = userId;
    }

    /**
     * @return Returns the xml.
     * @hibernate.property column = "xml" type = "text"
     */
    public String getXml() {
        return xml;
    }

    /**
     * @param xml
     *            The xml to set.
     */
    public void setXml(final String xml) {
        this.xml = xml;
    }
}
