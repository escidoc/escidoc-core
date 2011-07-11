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
package de.escidoc.core.test.common.util;

import etm.core.aggregation.ExecutionAggregate;
import etm.core.renderer.MeasurementRenderer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Render for SQL databases.
 *
 * @author Steffen Wagner
 */
public class SQLRenderer implements MeasurementRenderer {

    private String dbClassName = null;

    private String dbURL = null;

    private String dbUserName = null;

    private String dbPassword = null;

    private Connection conn = null;

    private Environment testEnvironment = null;

    /**
     * SQL Renderer.
     *
     * @param dbClassName      Name of DB driver class.
     * @param dbURL            URL of DB.
     * @param dbUserName       DB user name.
     * @param dbPassword       DB password.
     * @param environmentInfos Information about Test Environment.
     * @throws ClassNotFoundException Thrown if driver class could not be found.
     * @throws SQLException           Thrown if Connection to DB failed.
     */
    public SQLRenderer(final String dbClassName, final String dbURL, final String dbUserName, final String dbPassword,
        final Environment environmentInfos) throws ClassNotFoundException, SQLException {

        this.dbClassName = dbClassName;
        this.dbURL = dbURL;
        this.dbUserName = dbUserName;
        this.dbPassword = dbPassword;
        this.testEnvironment = environmentInfos;

        initDBConnection();
    }

    /**
     * The render method.
     *
     * @param points The measurement points from the measurement framework.
     */
    public void render(final Map points) {

        Map map = new TreeMap(points);
        Iterator<ExecutionAggregate> iterator = map.values().iterator();

        while (iterator.hasNext()) {
            ExecutionAggregate point = iterator.next();
            // write values to performance DB
            Statement statement;
            String sqlStatement = null;
            try {
                sqlStatement =
                    "INSERT INTO measurements " + "(hostname, java_version, cpus, mem, fw_series, fw_build, "
                        + "runs, methodname, m_parameter, min, max, average, " + "total_time) " + "VALUES('"
                        + this.testEnvironment.getHostname()
                        + "', '"
                        + this.testEnvironment.getJavaVersion()
                        + "', "
                        + this.testEnvironment.getNoOfCpus()
                        + ", "
                        + this.testEnvironment.getMemory()
                        + ", '"
                        + this.testEnvironment.getFwSeries()
                        + "', '"
                        + this.testEnvironment.getFwBuild()
                        + "', "
                        + point.getMeasurements()
                        + ", '"
                        + point.getName()
                        + "', '"
                        + this.testEnvironment.getMethodParameter()
                        + "', '"
                        + point.getMin()
                        + "', '"
                        + point.getMax()
                        + "', '" + point.getAverage() + "', '" + point.getTotal() + "');";

                statement = conn.createStatement();
                int result = statement.executeUpdate(sqlStatement);
                statement.close();
            }
            catch (final SQLException e) {
            }

        }
    }

    /**
     * Init DB connection.
     *
     * @throws ClassNotFoundException Thrown if database class could not be found.
     * @throws SQLException           Thrown if DB Connection init faild.
     */
    private void initDBConnection() throws ClassNotFoundException, SQLException {

        Class.forName(this.dbClassName);
        this.conn = DriverManager.getConnection(this.dbURL, this.dbUserName, this.dbPassword);

    }

}
