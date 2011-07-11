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

package de.escidoc.core.common.business.fedora;

/**
 * Constants.
 *
 * @author Michael Schneider
 */
public final class Constants {

    public static final String CONTENT_STREAMS_URL_PART = "/content-streams";

    public static final String CONTENT_STREAM_URL_PART = CONTENT_STREAMS_URL_PART + "/content-stream";

    public static final String CONTENT_STREAM_CONTENT_URL_EXTENSION = "/content";

    public static final String RELATIONS_URL_PART = "/relations";

    public static final String RELATION_URL_PART = "/relation";

    public static final String COMPONENTS_URL_PART = "/components";

    public static final String COMPONENT_URL_PART = "/components/component/";

    public static final String COMPONENT_CONTENT_URL_PART = "/content";

    public static final String PROPERTIES_URL_PART = "/properties";

    public static final String ADMIN_DESCRIPTOR_ALT_ID = "admin-descriptor";

    public static final String STORAGE_INTERNAL_MANAGED = "internal-managed";

    public static final String STORAGE_EXTERNAL_URL = "external-url";

    public static final String STORAGE_EXTERNAL_MANAGED = "external-managed";

    /**
     * Private constructor to avoid instanziation.
     */
    private Constants() {
    }
}
