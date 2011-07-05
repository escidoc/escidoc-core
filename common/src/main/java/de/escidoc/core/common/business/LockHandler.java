/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.business;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import net.sf.oval.guard.Guarded;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * @author Frank Schwichtenberg
 */
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true, assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public class LockHandler extends JdbcDaoSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(LockHandler.class);

    private static final boolean LOCKED_VALUE = true;

    private SessionFactory sessionFactory = null;

    private String lockedValue = null;

    /**
     * Initialize and get the database dependent locked value.
     */
    public void init() {
        lockedValue = ((SessionFactoryImplementor) sessionFactory).getDialect().toBooleanValueString(LOCKED_VALUE);
    }

    /**
     * Wrapper of setDataSource to enable bean stuff generation for this handler.
     *
     * @param myDataSource The {@link DataSource} to wrap.
     */
    public void setMyDataSource(final DataSource myDataSource) {
        setDataSource(myDataSource);
    }

    /**
     * Wrapper of setSessionFactory to enable bean stuff generation for this handler.
     *
     * @param sessionFactory The sessionFactory to set.
     */
    public final void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Lock an object and forbid further modifications.
     *
     * @param objid     The id of the object to lock.
     * @param lockOwner The id of the lock owner.
     * @throws SqlDatabaseSystemException Thrown if an error occurs accessing the database.
     */
    public void lock(final String objid, final String[] lockOwner) throws SqlDatabaseSystemException {

        try {
            getJdbcTemplate().execute(
                "INSERT INTO om.lockstatus (objid, owner, ownertitle, locked) " + "VALUES ('" + objid + "','"
                    + lockOwner[0] + "','" + lockOwner[1] + "', " + lockedValue + ')');
        }
        catch (final DataAccessException e) {
            throw new SqlDatabaseSystemException(e);
        }
    }

    /**
     * Unlock an object and permit further modifications.
     *
     * @param objid The id of the object to unlock.
     * @throws SqlDatabaseSystemException Thrown if an error occurs accessing the database.
     */
    public void unlock(final String objid) throws SqlDatabaseSystemException {

        try {
            getJdbcTemplate().update("DELETE FROM om.lockstatus WHERE objid = ?", objid);
        }
        catch (final DataAccessException e) {
            throw new SqlDatabaseSystemException(e);
        }
    }

    /**
     * Get the lock owner of a locked object.
     *
     * @param objid The id of the object to unlock.
     * @return The lock owner.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public String getLockOwner(@NotNull
    final String objid) throws WebserverSystemException {
        // TODO: use other query method to avoid exception in case of unlocked.
        try {
            return getJdbcTemplate().queryForObject("SELECT owner FROM om.lockstatus WHERE objid = ?",
                new Object[] { objid }, String.class);
        }
        catch (final IncorrectResultSizeDataAccessException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on quering for lock owner.", e);
            }
            return null;
        }
        catch (final DataAccessException e) {
            throw new WebserverSystemException("Error on quering for lock owner.", e);
        }
    }

    /**
     * Gets the title for the lock owner of the specified object.
     *
     * @param objid The internal id of the object.
     * @return Returns the title of the lock owner.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public String getLockOwnerTitle(final String objid) throws WebserverSystemException {
        // TODO: use other query method to avoid exception in case of unlocked.
        try {
            return getJdbcTemplate().queryForObject("SELECT ownertitle FROM om.lockstatus WHERE objid = ?",
                new Object[] { objid }, String.class);
        }
        catch (final IncorrectResultSizeDataAccessException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on quering for lock owner title.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on quering for lock owner title.", e);
            }
            return null;
        }
        catch (final DataAccessException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * Get the lock owner of a locked object.
     *
     * @param objid The id of the object to unlock.
     * @return The lock owner.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public String getLockDate(final String objid) throws WebserverSystemException {
        final String result;
        // TODO: use other query method to avoid exception in case of unlocked.
        try {
            final Timestamp ts =
                getJdbcTemplate().queryForObject("SELECT lock_timestamp FROM om.lockstatus WHERE objid = ?",
                    new Object[] { objid }, Timestamp.class);
            final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cal.setTimeInMillis(ts.getTime());
            final XMLGregorianCalendar xmlcal =
                DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) cal);
            result = xmlcal.toString();
        }
        catch (final IncorrectResultSizeDataAccessException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on quering for lock date.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on quering for lock date.", e);
            }
            return null;
        }
        catch (final DataAccessException e) {
            throw new WebserverSystemException(e);
        }
        catch (final DatatypeConfigurationException e) {
            throw new WebserverSystemException(e);
        }

        return result;
    }

    /**
     * Get the lock status of an object.
     *
     * @param objid The id of the object.
     * @return The lock status. Returns true if object is locked, false otherwise.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public boolean isLocked(final String objid) throws WebserverSystemException {
        // TODO: use other query method to avoid exception in case of unlocked.
        try {
            return getJdbcTemplate().queryForObject("SELECT locked FROM om.lockstatus WHERE objid = ?",
                new Object[] { objid }, Boolean.class);
        }
        catch (final IncorrectResultSizeDataAccessException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on quering for lock.", e);
            }
            return false;
        }
        catch (final DataAccessException e) {
            throw new WebserverSystemException("Could not find lock status for object '" + objid + '!', e);
        }
    }
}
