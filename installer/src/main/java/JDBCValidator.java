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
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.DataValidator;
import com.izforge.izpack.installer.DataValidator.Status;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Check if a database exists and return an error if it does.
 *
 * @author SCHE
 */
public class JDBCValidator implements DataValidator {

private final StringBuilder errorMessage = new StringBuilder();

    /*
     * (non-Javadoc)
     * @see
     * com.izforge.izpack.installer.DataValidator#getDefaultAnswer
     * ()
     */
    @Override
    public boolean getDefaultAnswer() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.izforge.izpack.installer.DataValidator#getErrorMessageId
     * ()
     */
    @Override
    public String getErrorMessageId() {
        return errorMessage.toString();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.izforge.izpack.installer.DataValidator#getWarningMessageId
     * ()
     */
    @Override
    public String getWarningMessageId() {
        return "";
    }

    /*
     * (non-Javadoc)
     * @see
     * com.izforge.izpack.installer.DataValidator#validateData
     * (com.izforge.izpack.installer.AutomatedInstallData)
     */
    @Override
     public Status validateData(AutomatedInstallData data) {
         Status status = Status.ERROR;
         boolean skipValidation = Boolean.valueOf(data.getVariable("SYSTEM_skip_validation"));

         if (skipValidation) {
             status = Status.OK;
         }
         else {
             String userName = data.getVariable("DatabaseUsername");
             String password = data.getVariable("DatabasePassword");
             String dbName = data.getVariable("DatasourceEscidoc");
             String url = data.getVariable("DatabaseURL") + dbName;

             buildErrorMessage(dbName);
             try {
                 Connection conn = DriverManager.getConnection(url, userName, password);

                 conn.close();
             }
             catch (SQLException e) {
                 // FIXME: Is there a better check for the existance of a database?
                 System.err.println(e.getMessage());
                 status = Status.OK;
             }
         }
         return status;
     }

    private void buildErrorMessage(String message) {
        clearErrorMessage();
        errorMessage.append("The Database \"");
        errorMessage.append(message);
        errorMessage.append("\" already exists.");
    }

    private void clearErrorMessage() {
        if (!errorMessage.toString().isEmpty()) {
            errorMessage.delete(0, errorMessage.length());
        }
    }
}
