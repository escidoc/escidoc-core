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
 * Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.adm.business.admin;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.Version;
import de.escidoc.core.common.util.db.Fingerprint;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Get some interesting information about the eSciDoc framework.
 * 
 * @author Andr&eacute; Schenk
 * 
 * @spring.bean id="admin.FrameworkInfo"
 * 
 */
public class FrameworkInfo extends JdbcDaoSupport {
    /**
     * Version of the eSciDocCore database which is currently needed to run the
     * framework.
     */
    private static final Version DB_VERSION = new Version(1, 3, 0);

    /**
     * Database table name for the version information.
     */
    private static final String TABLE_NAME = "adm.version";

    /**
     * Database column name for the major number.
     */
    private static final String COLUMN_MAJOR_NUMBER = "major_number";

    /**
     * Database column name for the minor number.
     */
    private static final String COLUMN_MINOR_NUMBER = "minor_number";

    /**
     * Database column name for the revision number.
     */
    private static final String COLUMN_REVISION_NUMBER = "revision_number";

    /**
     * Database column name for the version date.
     */
    private static final String COLUMN_DATE = "date";

    /**
     * Database query to get the latest version.
     */
    private static final String QUERY_LATEST_VERSION = "SELECT * FROM "
        + TABLE_NAME + " WHERE " + COLUMN_DATE + "=(SELECT MAX(" + COLUMN_DATE
        + ") FROM " + TABLE_NAME + ")";

    /**
     * XML file with the finger print of the "escidoc-core" database.
     */
    private static final String FINGERPRINT_FILE = "/META-INF/db/fingerprints/"
        + DB_VERSION.toString() + ".xml";

    /**
     * Check if the currently installed eSciDocCore database matches the needed
     * database version.
     * 
     * @throws SystemException
     *             Thrown if the eSciDocCore database has the wrong version
     */
    public void checkDbVersion() throws SystemException {
        Version currentDbVersion = getVersion();

        if (!DB_VERSION.equals(currentDbVersion)) {
            throw new SystemException("database version differs (needed: "
                + DB_VERSION + ", installed: " + currentDbVersion + ")");
        }
    }

    /**
     * Get the current database version from the database.
     * 
     * @return current database version
     */
    public Version getVersion() {
        Version result = null;

        try {
            result =
                (Version) getJdbcTemplate().query(QUERY_LATEST_VERSION,
                    new ResultSetExtractor<Object>() {
                        public Object extractData(final ResultSet rs)
                            throws SQLException {
                            Version result = null;

                            if (rs.next()) {
                                result =
                                    new Version(rs.getInt(COLUMN_MAJOR_NUMBER),
                                        rs.getInt(COLUMN_MINOR_NUMBER), rs
                                            .getInt(COLUMN_REVISION_NUMBER));
                            }
                            return result;
                        }
                    });
            if (result == null) {
                // version table is empty
                result = new Version(1, 0, 0);
            }
        }
        catch (DataAccessException e) {
            // version table doesn't exist
            result = new Version(1, 0, 0);
        }
        return result;
    }

    /**
     * Compare the current database structure with the structure stored in an
     * XML file.
     * 
     * @return true if both structures are equal
     * @throws IOException
     *             Thrown if the XML file could not be read
     * @throws SQLException
     *             Thrown if the structure of the database could not be
     *             determined
     */
    public boolean isConsistent() throws IOException, SQLException {
        boolean result = false;
        Connection conn = null;

        try {
            conn = getConnection();

            Fingerprint currentFingerprint = new Fingerprint(conn);
            Fingerprint storedFingerprint =
                Fingerprint.readObject(getClass().getResourceAsStream(
                    FINGERPRINT_FILE));

            result = storedFingerprint.compareTo(currentFingerprint) == 0;
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
        return result;
    }
}
