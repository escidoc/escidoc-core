package de.escidoc.core.aa.business;

import de.escidoc.core.aa.business.persistence.UserAccountDaoInterface;
import de.escidoc.core.aa.business.persistence.UserLoginData;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.string.StringUtility;

import java.util.Iterator;

/**
 * Cleans up the login data by removing expired eSciDoc user handles.
 * 
 * @author TTE
 * @spring.bean id="eSciDoc.core.aa.UserHandleCleaner"
 * @aa
 */
public class UserHandleCleaner {
    private static final AppLogger LOG =
        new AppLogger(UserHandleCleaner.class.getName());

    /**
     * Offset added to user handle expire time stamp before removing them to
     * avoid removing of currently used handles.
     */
    private static final long EXPIRY_OFFSET = 500000;

    /**
     * The login data DAO.
     */
    private UserAccountDaoInterface userAccountDao;

    /**
     * Cleans up the login data, i.e. removes each eSciDoc user handle that has
     * been expired a while ago.
     * 
     * @st
     */
    public void cleanUp() {

        LOG.debug("Cleaning up the staging file area");

        Iterator<UserLoginData> expiredLoginDatas;
        try {
            expiredLoginDatas =
                userAccountDao.retrieveExpiredUserLoginData(
                    System.currentTimeMillis() - EXPIRY_OFFSET).iterator();
        }
        catch (SqlDatabaseSystemException e) {
            LOG.error(e);
            return;
        }

        while (expiredLoginDatas.hasNext()) {
            final UserLoginData loginData = expiredLoginDatas.next();
            try {
                userAccountDao.delete(loginData);
            }
            catch (SqlDatabaseSystemException e) {
                LOG.error(StringUtility.format(
                    "Removing login data failed", loginData.getHandle(),
                    e.getClass().getName()).toString(), e);
            }
        }
    }

    /**
     * Setting the stagingFileDao.
     * 
     * @param userAccountDao
     *            The {@link UserAccountDaoInterface} to set.
     * @spring.property ref="persistence.UserAccountDao"
     * @st
     */
    public final void setUserAccountDao(
        final UserAccountDaoInterface userAccountDao) {

        this.userAccountDao = userAccountDao;
    }
}
