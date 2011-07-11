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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.openid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.openid.OpenIDAttribute;

import de.escidoc.core.common.util.service.EscidocUserDetails;

/**
 * @author Michael Hoppe
 *
 */
public class EscidocOpenidUserDetails extends EscidocUserDetails {
    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 1L;

    private final Map<String, List<String>> stringAttributes = new HashMap<String, List<String>>();

    /**
     * Adds the openId-Attributes to the HashMap of stringAttributes.
     *
     * @param attributes list of OpenIDAttribute-Objects
     */
    public void addAttributes(final List<OpenIDAttribute> attributes) {
        if (attributes != null) {
            for (OpenIDAttribute attribute : attributes) {
                stringAttributes.put(attribute.getName(), attribute.getValues());
            }
        }
    }

    /**
     * @return the attributes
     */
    public Map<String, List<String>> getStringAttributes() {
        return this.stringAttributes;
    }

}
