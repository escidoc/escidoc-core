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
package de.escidoc.core.common.business;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @spring.bean id="business.LockHandler"
 * @author FRS
 * 
 * @common
 * 
 */
public class LockHandler extends JdbcDaoSupport {

    private static final boolean LOCKED_VALUE = true;

    /**
     * Wrapper of setDataSource to enable bean stuff generation for this
     * handler.
     * 
     * @param myDataSource
     *            The {@link DataSource} to wrap.
     * 
     * @spring.property ref="escidoc-core.DataSource"
     * @param driverManagerDataSource
     * @common
     */
    public void setMyDataSource(final DataSource myDataSource) {
        super.setDataSource(myDataSource);
    }

    /**
     * Lock an object and forbid further modifications.
     * 
     * @param objid
     *            The id of the object to lock.
     * @param lockOwner
     *            The id of the lock owner.
     * @throws SqlDatabaseSystemException
     *             Thrown if an error occurs accessing the database.
     * @common
     */
    public void lock(final String objid, final String[] lockOwner)
        throws SqlDatabaseSystemException {

        try {
            getJdbcTemplate().execute(
                "INSERT INTO om.lockstatus (objid, owner, ownertitle, locked) "
                    + "VALUES ('" + objid + "','" + lockOwner[0] + "','"
                    + lockOwner[1] + "', " + LOCKED_VALUE + ")");
        }
        catch (DataAccessException e) {
            throw new SqlDatabaseSystemException(e);
            // TODO throw this Exception
            // throw new SqlDatabaseSystemException("Could not lock object '"
            // + objid + "' for user '" + lockOwner + "'!", e);
        }
    }

    /**
     * Unlock an object and permit further modifications.
     * 
     * @param objid
     *            The id of the object to unlock.
     * @throws SqlDatabaseSystemException
     *             Thrown if an error occurs accessing the database.
     * @common
     */
    public void unlock(final String objid) throws SqlDatabaseSystemException {

        try {
            getJdbcTemplate().update(
                "DELETE FROM om.lockstatus WHERE objid = ?",
                new Object[] { objid });
        }
        catch (DataAccessException e) {
            throw new SqlDatabaseSystemException(e);
            // TODO throw this Exception
            // throw new SqlDatabaseSystemException("Could not unlock object '"
            // + objid + "!", e);
        }
    }

    /**
     * Get the lock owner of a locked object.
     * 
     * @param objid
     *            The id of the object to unlock.
     * @return The lock owner.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @common
     */
    public String getLockOwner(final String objid)
        throws WebserverSystemException {

        // TODO: use other query method to avoid exception in case of unlocked.
        try {
            String result =
                    getJdbcTemplate().queryForObject(
                        "SELECT owner FROM om.lockstatus WHERE objid = ?",
                        new Object[] { objid }, String.class);
            return result;
        }
        catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
        catch (DataAccessException e) {
            throw new WebserverSystemException(e);
            // TODO throw this Exception
            // throw new SqlDatabaseSystemException(
            // "Could not find lock owner for object '" + objid + "!", e);
        }
    }

    /**
     * Gets the title for the lock owner of the specified object.
     * 
     * @param objid
     *            The internal id of the object.
     * @return Returns the title of the lock owner.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @common
     */
    public String getLockOwnerTitle(final String objid)
        throws WebserverSystemException {

        // TODO: use other query method to avoid exception in case of unlocked.
        try {
            String result =
                    getJdbcTemplate().queryForObject(
                        "SELECT ownertitle FROM om.lockstatus WHERE objid = ?",
                        new Object[] { objid }, String.class);
            return result;
        }
        catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
        catch (DataAccessException e) {
            throw new WebserverSystemException(e);
            // TODO throw this Exception
            // throw new SqlDatabaseSystemException(
            // "Could not find lock owner for object '" + objid + "!", e);
        }
    }

    /**
     * Get the lock owner of a locked object.
     * 
     * @param objid
     *            The id of the object to unlock.
     * @return The lock owner.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @common
     */
    public String getLockDate(final String objid)
        throws WebserverSystemException {
        String result;

        // TODO: use other query method to avoid exception in case of unlocked.
        try {
            Timestamp ts =
                    getJdbcTemplate().queryForObject(
                        "SELECT lock_timestamp FROM om.lockstatus WHERE objid = ?",
                        new Object[] { objid }, Timestamp.class);
            Calendar cal =
                GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
            cal.setTimeInMillis(ts.getTime());
            XMLGregorianCalendar xmlcal =
                DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    (GregorianCalendar) cal);
            result = xmlcal.toString();
        }
        catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
        catch (DataAccessException e) {
            throw new WebserverSystemException(e);
        }
        catch (DatatypeConfigurationException e) {
            throw new WebserverSystemException(e);
        }

        return result;
    }

    /**
     * Get the lock status of an object.
     * 
     * @param objid
     *            The id of the object.
     * @return The lock status. Returns true if object is locked, false
     *         otherwise.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public boolean isLocked(final String objid) throws WebserverSystemException {

        // TODO: use other query method to avoid exception in case of unlocked.
        try {
            Boolean result =
                    getJdbcTemplate().queryForObject(
                        "SELECT locked FROM om.lockstatus WHERE objid = ?",
                        new Object[] { objid }, Boolean.class);
            return result;
        }
        catch (IncorrectResultSizeDataAccessException e) {
            return false;
        }
        catch (DataAccessException e) {
            throw new WebserverSystemException(
                "Could not find lock status for object '" + objid + "!", e);
        }
    }

    /**
     * Returns a LockHandler instance.
     * 
     * @return The LockHandler instance.
     */
    public static LockHandler getInstance() {

        BeanFactoryLocator beanFactoryLocator =
            SingletonBeanFactoryLocator.getInstance();
        BeanFactory factory =
            beanFactoryLocator
                .useBeanFactory("Om.spring.ejb.context").getFactory();
        return (LockHandler) factory.getBean("business.LockHandler");
    }
}
