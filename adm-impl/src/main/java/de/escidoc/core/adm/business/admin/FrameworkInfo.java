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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.IOUtils;
import de.escidoc.core.common.util.Version;
import de.escidoc.core.common.util.db.DatabaseType;
import de.escidoc.core.common.util.db.Fingerprint;

/**
 * Get some interesting information about the eSciDoc framework.
 *
 * @author André Schenk
 */
public class FrameworkInfo extends JdbcDaoSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkInfo.class);

    /**
     * Version of the eSciDocCore database which is currently needed to run the framework.
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
    private static final String QUERY_LATEST_VERSION =
        "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + "=(SELECT MAX(" + COLUMN_DATE + ") FROM "
            + TABLE_NAME + ')';

    /**
     * path to XML file with the finger print of the "escidoc-core" database.
     */
    private static final String FINGERPRINT_PATH = "/META-INF/db/fingerprints/";

    /**
     * Check if the currently installed eSciDocCore database matches the needed database version.
     *
     * @throws SystemException Thrown if the eSciDocCore database has the wrong version
     */
    public void checkDbVersion() throws SystemException {
        final Version currentDbVersion = getVersion();

        if (!DB_VERSION.equals(currentDbVersion)) {
            throw new SystemException("database version differs (needed: " + DB_VERSION + ", installed: "
                + currentDbVersion + ')');
        }
    }

    /**
     * Get the current database version from the database.
     *
     * @return current database version
     */
    public Version getVersion() {
        Version result;

        try {
            result = (Version) getJdbcTemplate().query(QUERY_LATEST_VERSION, new ResultSetExtractor<Object>() {
                @Override
                public Object extractData(final ResultSet rs) throws SQLException {
                    Version result = null;

                    if (rs.next()) {
                        result =
                            new Version(rs.getInt(COLUMN_MAJOR_NUMBER), rs.getInt(COLUMN_MINOR_NUMBER), rs
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
        catch (final DataAccessException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.debug("Error on getting database version.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on getting database version.", e);
            }
            // version table doesn't exist
            result = new Version(1, 0, 0);
        }
        return result;
    }

    /**
     * Compare the current database structure with the structure stored in an XML file.
     *
     * @return true if both structures are equal
     * @throws IOException  Thrown if the XML file could not be read
     * @throws SQLException Thrown if the structure of the database could not be determined
     */
    public boolean isConsistent() throws IOException, SQLException {
        boolean result = false;
        Connection connection = null;
        try {
            connection = getConnection();
            final Fingerprint currentFingerprint = new Fingerprint(connection);
            final Fingerprint storedFingerprint =
                Fingerprint.readObject(getClass().getResourceAsStream(
                    FINGERPRINT_PATH + DatabaseType.valueOf(connection).getProductName() + '/' + DB_VERSION.toString()
                        + ".xml"));
            result = storedFingerprint.equals(currentFingerprint);
        }
        finally {
            IOUtils.closeConnection(connection);
        }
        return result;
    }
}
