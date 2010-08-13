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
package de.escidoc.core.ant;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.tools.ant.Task;

public class StatisticTask extends Task {
    private String driver = null;

    private String url = null;

    private String user = null;

    private String password = null;

    private String output = null;

    private String sqlfile = null;

    public void execute() {
        getData();
    }

    private void getData() {
        // Initialize output-file
        OutputStreamWriter ostr = null;
        try {
            ostr =
                new OutputStreamWriter(new FileOutputStream(getOutput()
                    + getDateString(), false), "UTF-8");
        }
        catch (Exception e1) {
            System.out.println(e1);
        }

        BufferedReader in = null;
        StringBuffer sql = new StringBuffer("");
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // get sql from file
            in =
                new BufferedReader(new InputStreamReader(new FileInputStream(
                    getSqlfile()), "UTF-8"));
            String str = new String("");
            while ((str = in.readLine()) != null) {
                sql.append(str).append(" ");
            }
            System.out.println("executing sql: " + sql.toString());

            // execute sql and write result in file
            con = openConnection(con);
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql.toString());
            HashMap<Integer, Integer> columnSizes =
                new HashMap<Integer, Integer>();
            Collection<Collection<String>> rsImitation =
                new ArrayList<Collection<String>>();

            while (rs.next()) {
                Collection<String> rowImitation = new ArrayList<String>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    rowImitation.add(rs.getObject(i).toString());

                    if (columnSizes.get(new Integer(i)) == null) {
                        columnSizes.put(new Integer(i), rs.getMetaData()
                            .getColumnName(i).length());
                    }
                    if (columnSizes.get(new Integer(i)).intValue() < rs
                        .getObject(i).toString().length()) {
                        columnSizes.put(new Integer(i), rs.getObject(i)
                            .toString().length());
                    }
                }
                rsImitation.add(rowImitation);
            }

            int rows = 0;
            for (Iterator<Collection<String>> iter = rsImitation.iterator(); iter.hasNext();) {
                Collection<String> rowImitation = iter.next();
                if (rows == 0) {
                    for (int i = 1; i <= rowImitation.size(); i++) {
                        ostr.write(writeColumn(rs.getMetaData()
                            .getColumnName(i), columnSizes.get(i)));
                    }
                    ostr.write("\n");
                    ostr.flush();
                }

                int i = 1;
                for (Iterator<String> iterator = rowImitation.iterator(); iterator
                    .hasNext();) {
                    String columnValue = iterator.next();
                    ostr.write(writeColumn(columnValue, columnSizes.get(i)));
                    i++;
                }
                ostr.write("\n");
                ostr.flush();
                rows++;
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e1) {
            try {
                ostr.write(e1.toString());
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                }
            }
            if (ostr != null) {
                try {
                    ostr.close();
                }
                catch (IOException e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException e) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                }
            }
        }

    }

    private Connection openConnection(Connection conn) throws Exception {
        if ((conn == null) || (conn.isClosed())) {
            Class.forName(getDriver());
            StringBuffer connect_string = new StringBuffer(getUrl());
            connect_string.append("?user=").append(getUser()).append(
                "&password=").append(getPassword());
            conn = DriverManager.getConnection(connect_string.toString());
        }
        return conn;
    }

    private String writeColumn(String text, Integer size) {
        StringBuffer filledText = new StringBuffer(text);
        while (filledText.length() < size + 2) {
            filledText.append(" ");
        }
        return filledText.toString();
    }

    private String getDateString() {
        Calendar cal = Calendar.getInstance();
        StringBuffer dateString = new StringBuffer("");
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        if (year < 10) {
            dateString.append("0");
        }
        dateString.append(year).append("-");
        if (month < 10) {
            dateString.append("0");
        }
        dateString.append(month).append("-");
        if (day < 10) {
            dateString.append("0");
        }
        dateString.append(day).append("-");
        if (hour < 10) {
            dateString.append("0");
        }
        dateString.append(hour).append(".");
        if (minute < 10) {
            dateString.append("0");
        }
        dateString.append(minute);
        return dateString.toString();
    }

    /**
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driver
     *            the driver to set
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * @return the output
     */
    public String getOutput() {
        return output;
    }

    /**
     * @param output
     *            the output to set
     */
    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the sqlfile
     */
    public String getSqlfile() {
        return sqlfile;
    }

    /**
     * @param sqlfile
     *            the sqlfile to set
     */
    public void setSqlfile(String sqlfile) {
        this.sqlfile = sqlfile;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user
     *            the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

}
