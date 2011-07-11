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
 * Copyright 2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */

package de.escidoc.core.sm.business.stax.handler;

import org.joda.time.DateTime;

/**
 * Holds parameter data.
 *
 * @author Michael Hoppe
 */
public class ParameterVo {

    private String name;

    private String stringValue;

    private Double decimalValue;

    private DateTime dateValue;

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

    /**
     * @return the stringValue
     */
    public String getStringValue() {
        return this.stringValue;
    }

    /**
     * @param stringValue the stringValue to set
     */
    public void setStringValue(final String stringValue) {
        this.stringValue = stringValue;
    }

    /**
     * @return the decimalValue
     */
    public Double getDecimalValue() {
        return this.decimalValue;
    }

    /**
     * @param decimalValue the decimalValue to set
     */
    public void setDecimalValue(final Double decimalValue) {
        this.decimalValue = decimalValue;
    }

    /**
     * @return the dateValue
     */
    public DateTime getDateValue() {
        return this.dateValue;
    }

    /**
     * @param dateValue the dateValue to set
     */
    public void setDateValue(final DateTime dateValue) {
        this.dateValue = dateValue;
    }

}
