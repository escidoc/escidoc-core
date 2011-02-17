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
package de.escidoc.core.common.util.db;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the table structure and the index list of a database.
 * It can be used to compare the current structure with a structure stored in a
 * file to see if the database is in the expected state.
 * 
 * @author SCHE
 */
public class Fingerprint implements Comparable<Object> {
    private static final Map<String, String> IGNORED_SCHEMAS =
        new HashMap<String, String>() {
            private static final long serialVersionUID = 6182156177577971112L;

            {
                put("information_schema", "");
                put("pg_catalog", "");
                put("pg_toast_temp_1", "");
                put("public", "");
            }
        };

    private Schema[] schemas = null;

    /**
     * Constructor for bean deserialization.
     */
    public Fingerprint() {
    }

    /**
     * Create a new finger print from the given database connection.
     * 
     * @param conn
     *            database connection
     * 
     * @throws IOException
     *             Thrown if the XML file could not be written.
     * @throws SQLException
     *             Thrown if an SQL statement failed to be executed.
     */
    public Fingerprint(final Connection conn) throws IOException, SQLException {
        ArrayList<Schema> schemas = new ArrayList<Schema>();

        for (String schemaName : getSchemaNames(conn)) {
            ArrayList<Table> tables = new ArrayList<Table>();

            for (String tableName : getTableNames(conn, schemaName)) {
                tables.add(new Table(tableName, getColumns(conn, schemaName,
                    tableName), getIndexInfo(conn, schemaName, tableName),
                    getPrimaryKeys(conn, schemaName, tableName),
                    getImportedKeys(conn, schemaName, tableName)));
            }
            schemas.add(new Schema(schemaName, tables.toArray(new Table[tables.size()])));
        }
        setSchemas(schemas.toArray(new Schema[schemas.size()]));

        // store current finger print for debugging
        writeObject(new FileOutputStream(System.getProperty("java.io.tmpdir")
            + "/fingerprint.xml"));
    }

    /**
     * Compares to finger prints.
     * 
     * @param o
     *            the Object to be compared.
     * 
     * @return a negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the specified object.
     */
    public int compareTo(final Object o) {
        try {
            // Java version string may differ but is not important for equality.
            String JAVA_VERSION_PATTERN = "^<java version=\\S+";
            ByteArrayOutputStream b1 = new ByteArrayOutputStream();
            ByteArrayOutputStream b2 = new ByteArrayOutputStream();

            writeObject(b1);
            ((Fingerprint) o).writeObject(b2);
            return b1
                .toString().replaceAll(JAVA_VERSION_PATTERN, "").compareTo(
                    b2.toString().replaceAll(JAVA_VERSION_PATTERN, ""));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a list of all table columns for the given combination of schema name
     * and table name.
     * 
     * @param conn
     *            database connection
     * @param schema
     *            schema name
     * @param table
     *            table name
     * 
     * @return list of all table columns
     * @throws SQLException
     *             Thrown if an SQL statement failed to be executed.
     */
    private String[] getColumns(
        final Connection conn, final String schema, final String table)
        throws SQLException {
        ArrayList<String> result = new ArrayList<String>();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs =
            metaData.getColumns(conn.getCatalog(), schema, table, null);

        while (rs.next()) {
            StringBuilder column = new StringBuilder();

            for (int index = 4; index <= 22; index++) {
                // ignore column position
                if (index != 17) {
                    if (column.length() > 0) {
                        column.append('/');
                    }
                    column.append(rs.getString(index));
                }
            }
            result.add(column.toString());
        }
        rs.close();
        return result.toArray(new String[result.size()]);
    }

    /**
     * Get a list of all foreign keys that are defined for the table.
     * 
     * @param conn
     *            database connection
     * @param schema
     *            schema name
     * @param table
     *            table name
     * 
     * @return list of all foreign keys
     * @throws SQLException
     *             Thrown if an SQL statement failed to be executed.
     */
    private String[] getImportedKeys(
        final Connection conn, final String schema, final String table)
        throws SQLException {
        ArrayList<String> result = new ArrayList<String>();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs =
            metaData.getImportedKeys(conn.getCatalog(), schema, table);

        while (rs.next()) {
            StringBuilder indexInfo = new StringBuilder();

            for (int index = 4; index <= 14; index++) {
                if (indexInfo.length() > 0) {
                    indexInfo.append('/');
                }
                indexInfo.append(rs.getString(index));
            }
            result.add(indexInfo.toString());
        }
        rs.close();
        return result.toArray(new String[result.size()]);
    }

    /**
     * Get a list of all table indexes for the given combination of schema name
     * and table name.
     * 
     * @param conn
     *            database connection
     * @param schema
     *            schema name
     * @param table
     *            table name
     * 
     * @return list of all table indexes
     * @throws SQLException
     *             Thrown if an SQL statement failed to be executed.
     */
    private String[] getIndexInfo(
        final Connection conn, final String schema, final String table)
        throws SQLException {
        ArrayList<String> result = new ArrayList<String>();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs =
            metaData
                .getIndexInfo(conn.getCatalog(), schema, table, false, true);

        while (rs.next()) {
            StringBuilder indexInfo = new StringBuilder();

            for (int index = 4; index <= 10; index++) {
                if (indexInfo.length() > 0) {
                    indexInfo.append('/');
                }
                indexInfo.append(rs.getString(index));
            }
            result.add(indexInfo.toString());
        }
        rs.close();
        return result.toArray(new String[result.size()]);
    }

    /**
     * Get a list of all primary keys that are defined for the table.
     * 
     * @param conn
     *            database connection
     * @param schema
     *            schema name
     * @param table
     *            table name
     * 
     * @return list of all primary keys
     * @throws SQLException
     *             Thrown if an SQL statement failed to be executed.
     */
    private String[] getPrimaryKeys(
        final Connection conn, final String schema, final String table)
        throws SQLException {
        ArrayList<String> result = new ArrayList<String>();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs =
            metaData.getPrimaryKeys(conn.getCatalog(), schema, table);

        while (rs.next()) {
            StringBuilder indexInfo = new StringBuilder();

            for (int index = 4; index <= 6; index++) {
                if (indexInfo.length() > 0) {
                    indexInfo.append('/');
                }
                indexInfo.append(rs.getString(index));
            }
            result.add(indexInfo.toString());
        }
        rs.close();
        return result.toArray(new String[result.size()]);
    }

    /**
     * Get a list of all schemas for the given connection.
     * 
     * @param conn
     *            database connection
     * 
     * @return list of all schemas
     * @throws SQLException
     *             Thrown if an SQL statement failed to be executed.
     */
    private String[] getSchemaNames(final Connection conn) throws SQLException {
        ArrayList<String> result = new ArrayList<String>();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs = metaData.getSchemas();

        while (rs.next()) {
            final String schema = rs.getString(1);

            if (!IGNORED_SCHEMAS.containsKey(schema)) {
                result.add(schema);
            }
        }
        rs.close();
        return result.toArray(new String[result.size()]);
    }

    /**
     * Get all schemas.
     * 
     * @return schema list
     */
    public Schema[] getSchemas() {
        return schemas;
    }

    /**
     * Get a list of all tables for the given schema.
     * 
     * @param conn
     *            database connection
     * @param schema
     *            schema name
     * 
     * @return list of all tables
     * @throws SQLException
     *             Thrown if an SQL statement failed to be executed.
     */
    private String[] getTableNames(final Connection conn, final String schema)
        throws SQLException {
        ArrayList<String> result = new ArrayList<String>();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs =
            metaData.getTables(conn.getCatalog(), schema, null,
                new String[] { "TABLE" });

        while (rs.next()) {
            final String name = rs.getString(3);

            // ignore dynamically created tables for statistics manager
            if (!name.startsWith("_")) {
                result.add(name);
            }
        }
        rs.close();
        return result.toArray(new String[result.size()]);
    }

    /**
     * Reads the next object from the underlying input stream.
     * 
     * @param filename
     *            source from which to read the object.
     * 
     * @return the next object read
     * @throws FileNotFoundException
     *             Thrown if the given file could not be found.
     */
    public static Fingerprint readObject(final String filename)
        throws FileNotFoundException {
        return readObject(new FileInputStream(filename));
    }

    /**
     * Reads the next object from the underlying input stream.
     * 
     * @param input
     *            source from which to read the object.
     * 
     * @return the next object read
     */
    public static Fingerprint readObject(final InputStream input) {
        Fingerprint result;
        XMLDecoder d = new XMLDecoder(new BufferedInputStream(input));

        result = (Fingerprint) d.readObject();
        d.close();
        return result;
    }

    /**
     * Set the schemas.
     * 
     * @param schemas
     *            schema list
     */
    void setSchemas(final Schema[] schemas) {
        this.schemas = schemas;
    }

    /**
     * Write an XML representation of the specified object to the output.
     * 
     * @param o
     *            The object to be written to the stream.
     * 
     * @throws IOException
     *             Thrown if the object could not be written.
     */
    void writeObject(final OutputStream o) throws IOException {
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(o));

        e.writeObject(this);
        e.close();
    }
}
