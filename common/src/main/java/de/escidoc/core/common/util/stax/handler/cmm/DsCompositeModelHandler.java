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

package de.escidoc.core.common.util.stax.handler.cmm;

import de.escidoc.core.common.business.fedora.resources.cmm.DsTypeModel;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.naming.directory.NoSuchAttributeException;
import java.util.ArrayList;
import java.util.List;

public class DsCompositeModelHandler extends DefaultHandler {

    private static final String DS_COMPOSITE_MODEL_PATH = "/dsCompositeModel";

    private static final String DS_TYPE_MODEL_PATH = DS_COMPOSITE_MODEL_PATH + "/dsTypeModel";

    private static final String ATTRIBUTE_ID = "ID";

    private static final String DS_TYPE_MODEL_EXTENSIONS_PATH = DS_TYPE_MODEL_PATH + "/extensions";

    private static final String ATTRIBUTE_NAME = "name";

    private static final String EXTENSION_NAME_SCHEMA = "SCHEMA";

    private final List<DsTypeModel> dsTypeModels;

    private DsTypeModel dtm;

    private final StaxParser parser;

    /**
     *
     * @param parser
     */
    public DsCompositeModelHandler(final StaxParser parser) {
        this.parser = parser;
        this.dsTypeModels = new ArrayList<DsTypeModel>();
    }

    public List<DsTypeModel> getDsTypeModels() {
        return this.dsTypeModels;
    }

    @Override
    public StartElement startElement(final StartElement element) throws IntegritySystemException {

        if (parser.getCurPath().equals(DS_TYPE_MODEL_PATH)) {
            this.dtm = new DsTypeModel();
            try {
                dtm.setName(element.getAttributeValue(null, ATTRIBUTE_ID));
            }
            catch (final NoSuchAttributeException e) {
                throw new IntegritySystemException("Stream type model must have an attribute " + ATTRIBUTE_ID + '.', e);
            }
        }
        else if (parser.getCurPath().equals(DS_TYPE_MODEL_EXTENSIONS_PATH)) {
            try {
                if (element.getAttributeValue(null, ATTRIBUTE_NAME).equalsIgnoreCase(EXTENSION_NAME_SCHEMA)) {
                    this.dtm.setHasSchema(true);
                }
            }
            catch (final NoSuchAttributeException e) {
                this.dtm.setHasSchema(false);
            }
        }

        return element;
    }

    @Override
    public EndElement endElement(final EndElement element) {

        if (parser.getCurPath().equals(DS_TYPE_MODEL_PATH)) {
            this.dsTypeModels.add(this.dtm);
            this.dtm = null;
        }

        return element;
    }

}
