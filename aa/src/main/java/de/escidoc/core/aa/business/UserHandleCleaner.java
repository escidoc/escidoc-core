package de.escidoc.core.aa.business;

import de.escidoc.core.aa.business.persistence.UserAccountDaoInterface;
import de.escidoc.core.aa.business.persistence.UserLoginData;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;

/**
 * Cleans up the login data by removing expired eSciDoc user handles.
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.UserHandleCleaner")
public class UserHandleCleaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserHandleCleaner.class);

    /**
     * Offset added to user handle expire time stamp before removing them to avoid removing of currently used handles.
     */
    private static final long EXPIRY_OFFSET = 500000L;

    /**
     * The login data DAO.
     */
    @Autowired
    @Qualifier("persistence.UserAccountDao")
    private UserAccountDaoInterface userAccountDao;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected UserHandleCleaner() {
    }

    /**
     * Cleans up the login data, i.e. removes each eSciDoc user handle that has been expired a while ago.
     */
    @Scheduled(fixedRate = 3600000)
    // TODO: made configurable
    public void cleanUp() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Cleaning up the staging file area");
        }
        final Iterator<UserLoginData> expiredLoginDatas;
        try {
            expiredLoginDatas =
                userAccountDao.retrieveExpiredUserLoginData(System.currentTimeMillis() - EXPIRY_OFFSET).iterator();
        }
        catch (final SqlDatabaseSystemException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on retriving expired user login data.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on retriving expired user login data.", e);
            }
            return;
        }

        while (expiredLoginDatas.hasNext()) {
            final UserLoginData loginData = expiredLoginDatas.next();
            try {
                userAccountDao.delete(loginData);
            }
            catch (final SqlDatabaseSystemException e) {
                final String message =
                    StringUtility.format("Removing login data failed", loginData.getHandle(), e.getClass().getName());
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn(message);
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(message, e);
                }
            }
        }
    }

    /**
     * Setting the stagingFileDao.
     *
     * @param userAccountDao The {@link UserAccountDaoInterface} to set.
     */
    public final void setUserAccountDao(final UserAccountDaoInterface userAccountDao) {

        this.userAccountDao = userAccountDao;
    }
}
