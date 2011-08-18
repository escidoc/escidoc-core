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
     * Setting the stagingFileDao.
     *
     * @param userAccountDao The {@link UserAccountDaoInterface} to set.
     */
    public final void setUserAccountDao(final UserAccountDaoInterface userAccountDao) {

        this.userAccountDao = userAccountDao;
    }
}
