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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.shibboleth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.io.Serializable;

/**
 * Object that holds Shibboleth-Userdata.
 *
 * @author Michael Hoppe
 */
public class ShibbolethUser implements Serializable {

    private final Map<String, List<String>> stringAttributes = new HashMap<String, List<String>>();

    public static final Pattern DISPOSABLE_HEADER_PATTERN =
        Pattern.compile("host|user-agent|accept|accept-.*|Keep-Alive"
            + "|connection|referer|cookie|Shib-.*|REMOTE_USER");

    private String loginName;

    private String name;

    /**
     * Adds an attribute to the HashMap of stringAttributes.
     *
     * @param attName name of attribute
     * @param value   value of attribute
     */
    public void addStringAttribute(final String attName, final String value) {
        if (stringAttributes.get(attName) == null) {
            stringAttributes.put(attName, new ArrayList<String>());
        }
        stringAttributes.get(attName).add(value);
    }

    /**
     * @return the attributes
     */
    public Map<String, List<String>> getStringAttributes() {
        return this.stringAttributes;
    }

    /**
     * Returns the value of the attribute with the given name.
     *
     * @param attName name of the attribute.
     * @return one attribute with given name
     */
    public List<String> getStringAttribute(final String attName) {
        return stringAttributes.get(attName);
    }

    /**
     * @return the loginName
     */
    public String getLoginName() {
        return this.loginName;
    }

    /**
     * @param loginName the loginName to set
     */
    public void setLoginName(final String loginName) {
        this.loginName = loginName;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

}
