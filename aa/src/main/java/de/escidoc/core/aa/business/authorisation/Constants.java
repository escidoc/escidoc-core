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
package de.escidoc.core.aa.business.authorisation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import de.escidoc.core.common.business.aa.authorisation.AttributeIds;

/**
 * Some constants used in the AA component.
 *
 * @author Torsten Tetteroo
 */
public final class Constants {

    /**
     * Pattern used to parse a resource id and identify the objid and optional version number.
     */
    public static final Pattern PATTERN_PARSE_RESOURCE_ID = Pattern.compile("([^:]*:[^:]*)(:([^:]*)){0,1}");

    public static final String XMLSCHEMA_STRING = "http://www.w3.org/2001/XMLSchema#string";

    public static final URI URI_OBJECT_TYPE;

    public static final URI URI_RESOURCE_ID;

    public static final URI URI_SUBRESOURCE_ID;

    public static final URI URI_SUBJECT_ID;

    public static final URI URI_XMLSCHEMA_STRING;

    static {
        URI_OBJECT_TYPE = getURI(AttributeIds.URN_OBJECT_TYPE);
        URI_RESOURCE_ID = getURI(AttributeIds.URN_RESOURCE_ID);
        URI_SUBRESOURCE_ID = getURI(AttributeIds.URN_SUBRESOURCE_ATTR);
        URI_SUBJECT_ID = getURI(AttributeIds.URN_SUBJECT_ID);
        URI_XMLSCHEMA_STRING = getURI(XMLSCHEMA_STRING);
    }

    /**
     * Values used by the UserGroupHandler.
     */
    public static final String USER_GROUP_INTERNAL_MEMBER_TYPE = "internal";

    public static final String USER_GROUP_ORG_UNIT_MEMBER = "organizational-unit";

    public static final String USER_GROUP_GROUP_MEMBER = "user-group";

    public static final String USER_GROUP_USER_MEMBER = "user-account";

    /**
     * Values used by the User Detail Objects (LDAP, Shibboleth).
     */
    public static final String GROUP_ATTRIBUTE_NAME = "groupRole";

    public static final String MULTI_VALUE_DELIMITER = "|";

    /**
     * Gets the URI from the given String. If the provided URI string causes an
     * error, {@code null} is returned.
     *
     * @param uriString The string defining the URI.
     * @return Returns the URI for the provided string or {@code null}.
     */
    private static URI getURI(final String uriString) {

        URI uri;
        try {
            uri = new URI(uriString);
        }
        catch (final URISyntaxException e) {
            uri = null;
        }
        return uri;
    }

    /**
     * Constructor.
     */
    private Constants() {
    }

}
