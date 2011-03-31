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

package de.escidoc.core.common.business;

/**
 * Constants for properties names within resource properties map (from GenericResource, GenericVersionableResource).
 * This names should lead to a consistent name in the properties value map independent if the TripleStore is requested
 * or the WOV/RELS-EXT is parsed. The Velocity handler should also use these keys to avoid an additional mapping.
 * <p/>
 * The properties name conform the defined eSciDoc ontology.
 * <p/>
 * FIXME I started a central mapping instance. Please check if some keys are useless or have ambiguouse names
 *
 * @author Steffen Wagner
 */
public final class PropertyMapKeys {

    /**
     * Private constructor to avoid instantiation.
     */
    private PropertyMapKeys() {
    }

    /*
     * FIXME may be it would be better to call some values OBJECT_XY like
     * OBJECT_COMMENT or OBJECT_STATUS instead of VERSION_STATUS which leads the
     * VERSION_VERSION_STATUS
     */
    // public static final String CURRENT_VERSION_ = "CURRENT_VERSION_";
    // public static final String LATEST_VERSION_ = "LATEST_VERSION_";
    /*
     * Resource Object Properties
     */
    public static final String LAST_MODIFICATION_DATE = "LAST_MODIFICATION_DATE";

    public static final String CREATED_BY_TITLE = "CREATED_BY_TITLE";

    public static final String CREATED_BY_ID = "CREATED_BY_ID";

    public static final String OBJECT_PID = "OBJECT_PID";

    public static final String PUBLIC_STATUS = "PUBLIC_STATUS";

    public static final String PUBLIC_STATUS_COMMENT = "PUBLIC_STATUS_COMMENT";

    // TODO check if this is not a version or latest version parameter
    public static final String CONTEXT_TYPE = "CONTEXT_TYPE";

    /*
     * Latest Version Keys (also use for unversioned resources (there we have
     * ever the latest version ;-))
     */

    public static final String LATEST_VERSION_TITLE = "LATEST_VERSION_TITLE";

    public static final String LATEST_VERSION_DESCRIPTION = "LATEST_VERSION_DESCRIPTION";

    // /properties/public-status
    public static final String LATEST_VERSION_PUBLIC_STATUS = "LATEST_VERSION_PUBLIC_STATUS";

    // /properties/version/public-status
    public static final String LATEST_VERSION_VERSION_STATUS = "LATEST_VERSION_VERSION_STATUS";

    public static final String LATEST_VERSION_CONTENT_MODEL_TITLE = "LATEST_VERSION_CONTENT_MODEL_TITLE";

    public static final String LATEST_VERSION_CONTENT_MODEL_ID = "LATEST_VERSION_CONTENT_MODEL_ID";

    public static final String LATEST_VERSION_CONTEXT_TITLE = "LATEST_VERSION_CONTEXT_TITLE";

    public static final String LATEST_VERSION_CONTEXT_ID = "LATEST_VERSION_CONTEXT_ID";

    public static final String LATEST_VERSION_DATE = "LATEST_VERSION_DATE";

    public static final String LATEST_VERSION_NUMBER = "LATEST_VERSION_NUMBER";

    // /properties/comment
    public static final String LATEST_VERSION_COMMENT = "LATEST_VERSION_COMMENT";

    // /properties/version/comment
    public static final String LATEST_VERSION_VERSION_COMMENT = "LATEST_VERSION_VERSION_COMMENT";

    public static final String LATEST_VERSION_VALID_STATUS = "LATEST_VERSION_VALID_STATUS";

    public static final String LATEST_VERSION_MODIFIED_BY_ID = "LATEST_VERSION_MODIFIED_BY_ID";

    public static final String LATEST_VERSION_MODIFIED_BY_TITLE = "LATEST_VERSION_MODIFIED_BY_TITLE";

    public static final String ORIGIN = "ORIGIN";

    public static final String ORIGIN_VERSION = "ORIGIN_VERSION";

    public static final String LATEST_VERSION_MODIFIED_BY_HREF = "LATEST_VERSION_MODIFIED_BY_HREF";

    public static final String LATEST_VERSION_CONTENT_CATEGORY = "LATEST_VERSION_CONTENT_CATEGORY";

    public static final String LATEST_VERSION_PID = "LATEST_VERSION_PID";

    /*
     * Current Version Keys (use this if a version, which is not the latest, is
     * used)
     */

    public static final String CURRENT_VERSION_TITLE = "CURRENT_VERSION_TITLE";

    public static final String CURRENT_VERSION_DESCRIPTION = "CURRENT_VERSION_DESCRIPTION";

    public static final String CURRENT_VERSION_PUBLIC_STATUS = "CURRENT_VERSION_PUBLIC_STATUS";

    public static final String CURRENT_VERSION_CONTENT_MODEL_TITLE = "CURRENT_VERSION_CONTENT_MODEL_TITLE";

    public static final String CURRENT_VERSION_CONTENT_MODEL_ID = "CURRENT_VERSION_CONTENT_MODEL_ID";

    public static final String CURRENT_VERSION_CONTEXT_TITLE = "CURRENT_VERSION_CONTEXT_TITLE";

    public static final String CURRENT_VERSION_CONTEXT_ID = "CURRENT_VERSION_CONTEXT_ID";

    public static final String CURRENT_VERSION_VERSION_COMMENT = "CURRENT_VERSION_VERSION_COMMENT";

    public static final String CURRENT_VERSION_VERSION_DATE = "CURRENT_VERSION_VERSION_DATE";

    public static final String CURRENT_VERSION_VERSION_NUMBER = "CURRENT_VERSION_VERSION_NUMBER";

    public static final String CURRENT_VERSION_STATUS = "CURRENT_VERSION_STATUS";

    public static final String CURRENT_VERSION_VALID_STATUS = "CURRENT_VERSION_VALID_STATUS";

    public static final String CURRENT_VERSION_MODIFIED_BY_ID = "CURRENT_VERSION_MODIFIED_BY_ID";

    public static final String CURRENT_VERSION_MODIFIED_BY_TITLE = "CURRENT_VERSION_MODIFIED_BY_TITLE";

    /**
     * @deprecated ??? not used? should href be assambled in renderer or mapping?
     */
    @Deprecated
    public static final String CURRENT_VERSION_MODIFIED_BY_HREF = "CURRENT_VERSION_MODIFIED_BY_HREF";

    public static final String CURRENT_VERSION_PID = "CURRENT_VERSION_PID";

    /*
     * Latest Release Keys
     */
    public static final String LATEST_RELEASE_VERSION_NUMBER = "LATEST_RELEASE_VERSION_NUMBER";

    public static final String LATEST_RELEASE_VERSION_DATE = "LATEST_RELEASE_VERSION_DATE";

    public static final String LATEST_RELEASE_PID = "LATEST_RELEASE_PID";

}
