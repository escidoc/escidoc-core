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
package de.escidoc.core.sm.business.vo.database.record;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.sm.business.Constants;
import de.escidoc.core.sm.business.vo.database.DatabaseConventionChecker;

/**
 * Holds all values needed to represent one Field out of one database-record.
 *
 * @author Michael Hoppe
 */
public class DatabaseRecordFieldVo {

    private String fieldName;

    private String fieldValue;

    // also can be numeric, date
    private String fieldType = Constants.DATABASE_FIELD_TYPE_TEXT;

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * @param fieldName the fieldName to set
     * @throws SqlDatabaseSystemException databaseException
     */
    public void setFieldName(final String fieldName) throws SqlDatabaseSystemException {
        DatabaseConventionChecker.checkName(fieldName);
        this.fieldName = fieldName;
    }

    /**
     * Can be text, date or numeric.
     *
     * @return the fieldType
     */
    public String getFieldType() {
        return this.fieldType;
    }

    /**
     * Can be text, date or numeric.
     *
     * @param fieldType the fieldType to set
     * @throws SqlDatabaseSystemException e
     */
    public void setFieldType(final String fieldType) throws SqlDatabaseSystemException {
        if (fieldType == null || !fieldType.equals(Constants.DATABASE_FIELD_TYPE_TEXT)
            && !fieldType.equals(Constants.DATABASE_FIELD_TYPE_NUMERIC)
            && !fieldType.equals(Constants.DATABASE_FIELD_TYPE_DATE)) {
            throw new SqlDatabaseSystemException("wrong fieldType given");
        }
        this.fieldType = fieldType;
    }

    /**
     * @return the fieldValue
     */
    public String getFieldValue() {
        return this.fieldValue;
    }

    /**
     * @param fieldValue the fieldValue to set
     */
    public void setFieldValue(final String fieldValue) {
        this.fieldValue = fieldValue;
    }

}
