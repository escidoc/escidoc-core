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
package de.escidoc.core.oum.business.handler;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.resources.Predecessor;
import de.escidoc.core.common.business.fedora.resources.PredecessorForm;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;

import javax.naming.directory.NoSuchAttributeException;
import java.util.ArrayList;
import java.util.List;

/**
 * StAX handler for predecessor relation of Organizational Unit.
 *
 * @author Steffen Wagner
 */
public class OrganizationalUnitPredecessorsHandler extends HandlerBase {

    private final List<Predecessor> predecessors = new ArrayList<Predecessor>();

    private static final String XPATH_PREDECESSOR =
        '/' + XmlUtility.NAME_ORGANIZATIONAL_UNIT + '/' + XmlUtility.NAME_PREDECESSORS + '/'
            + XmlUtility.NAME_PREDECESSOR;

    /**
     * @param parser The StAX parser.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public OrganizationalUnitPredecessorsHandler(final StaxParser parser) {

        super(parser);
    }

    /**
     * See Interface for functional description.
     *
     * @param element StAX Parser StartElement
     * @return StAX Parser StartElement
     * @throws MissingAttributeValueException Thrown if OU predecessor reference is not set (neither over href nor
     *                                        objid)
     * @throws OrganizationalUnitNotFoundException
     *                                        Thrown if the referenced Organizational Units does not exist.
     * @throws InvalidContentException        Thrown if value of Attribute is invalid (e.g. type).
     */
    @Override
    public StartElement startElement(final StartElement element) throws MissingAttributeValueException,
        OrganizationalUnitNotFoundException, InvalidContentException, TripleStoreSystemException,
        IntegritySystemException, WebserverSystemException {

        final String curPath = getParser().getCurPath();

        if (curPath.equals(XPATH_PREDECESSOR)) {

            String objid;
            try {
                objid =
                    XmlUtility.getIdFromURI(element
                        .getAttribute(Constants.XLINK_URI, Elements.ATTRIBUTE_XLINK_HREF).getValue());
            }
            catch (final NoSuchAttributeException e) {
                try {
                    objid = element.getAttribute(null, Elements.ATTRIBUTE_XLINK_OBJID).getValue();
                }
                catch (final NoSuchAttributeException e1) {
                    throw new MissingAttributeValueException("Predecessor attribute '" + Elements.ATTRIBUTE_XLINK_HREF
                        + "' or '" + Elements.ATTRIBUTE_XLINK_OBJID + "' has to be set! ", e1);
                }
            }

            final String type;
            try {
                type =
                    XmlUtility.getIdFromURI(element.getAttribute(null, Elements.PREDECESSOR_ATTRIBUTE_FORM).getValue());
            }
            catch (final NoSuchAttributeException e) {
                throw new MissingAttributeValueException("Predecessor attribute '"
                    + Elements.PREDECESSOR_ATTRIBUTE_FORM + "' has to be set! ", e);
            }

            this.getUtility().checkIsOrganizationalUnit(objid);

            final PredecessorForm predecessorType = getPredecessorForm(type);

            this.predecessors.add(new Predecessor(objid, predecessorType));
        }
        return element;
    }

    /**
     * Get list of predecessors of OU.
     *
     * @return list of predecessors
     */
    public List<Predecessor> getPredecessors() {
        return this.predecessors;
    }

    /**
     * Get PredecessorType from String.
     *
     * @param predecessorForm PredecessorType
     * @return PredecessorForm
     * @throws InvalidContentException Thrown if PredecessorType is not supported.
     */
    private static PredecessorForm getPredecessorForm(final String predecessorForm) throws InvalidContentException {

        if (predecessorForm.equals(PredecessorForm.SPLITTING.getLabel())) {
            return PredecessorForm.SPLITTING;
        }
        else if (predecessorForm.equals(PredecessorForm.FUSION.getLabel())) {
            return PredecessorForm.FUSION;
        }
        else if (predecessorForm.equals(PredecessorForm.SPIN_OFF.getLabel())) {
            return PredecessorForm.SPIN_OFF;
        }
        else if (predecessorForm.equals(PredecessorForm.AFFILIATION.getLabel())) {
            return PredecessorForm.AFFILIATION;
        }
        else if (predecessorForm.equals(PredecessorForm.REPLACEMENT.getLabel())) {
            return PredecessorForm.REPLACEMENT;
        }
        throw new InvalidContentException("Unsupported type '" + predecessorForm + "' for predecessors.");
    }

}
