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

package de.escidoc.core.common.util.security.persistence;

/**
 * Class holding information of an invocation mapping.
 *
 * @author Torsten Tetteroo
 */
public class InvocationMapping extends InvocationMappingBase {

    public static final int SIMPLE_ATTRIBUTE_MAPPING = 0;

    public static final int COMPLEX_ATTRIBUTE_MAPPING = 1;

    /**
     * Mandatory attribute from xml data. If this attribute is not found, an exception should be thrown.
     */
    public static final int XML_ATTRIBUTE_MAPPING = 2;

    public static final int VALUE_MAPPING = 3;

    /**
     * Optional attribute from xml data.
     */
    public static final int OPTIONAL_XML_ATTRIBUTE_MAPPING = 12;

    public static final String INDEXED_PATTERN = "\\[i\\]";

    public static final String SUBRESOURCE_PATTERN = ".*-id";

    private static final long serialVersionUID = 4912823084983730315L;

    /**
     * Creates a invocation mapping for single parameter .
     * @param id
     * @param path
     * @param position
     * @param attributeType
     * @param mappingType
     * @param value
     */
    public InvocationMapping(final String id, final String path, final int position, final String attributeType,
        final int mappingType, final String value) {

        super(id, path, position, attributeType, mappingType, false, value, null);

    }

    /**
     * Creates an invocation mapping.
     * @param id
     * @param path
     * @param position
     * @param attributeType
     * @param mappingType
     * @param multipleValue
     * @param value
     */
    public InvocationMapping(final String id, final String path, final int position, final String attributeType,
        final int mappingType, final boolean multipleValue, final String value) {

        super(id, path, position, attributeType, mappingType, multipleValue, value, null);

    }

    /**
     * The default constructor.
     */
    public InvocationMapping() {
    }
}
