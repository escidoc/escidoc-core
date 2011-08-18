/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
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

package de.escidoc.core.aa.business;

import de.escidoc.core.aa.business.interfaces.UserLoginService;
import de.escidoc.core.aa.business.persistence.UserAccount;
import de.escidoc.core.aa.business.persistence.UserAccountDaoInterface;
import de.escidoc.core.aa.business.persistence.UserLoginData;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Service("UserLoginService")
public class UserLoginServiceImpl implements UserLoginService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Cache userLoginDataCache;

    @Autowired
    @Qualifier("persistence.UserAccountDao")
    private UserAccountDaoInterface userAccountDao;

    @Autowired
    @Qualifier("security.SecurityHelper")
    private SecurityHelper securityHelper;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void init() {
        final CacheManager cacheManager = (CacheManager) this.applicationContext.getBean("cacheManager");
        this.userLoginDataCache = cacheManager.getCache("UserLoginDataCache");
    }

    @Override
    public void loginUser(final UserAccount userAccount, final String handle) {
        securityHelper.clearUserDetails(handle);
        final UserLoginData userLoginData = new UserLoginData();
        userLoginData.setId(UUID.randomUUID().toString());
        userLoginData.setUserAccount(userAccount);
        userLoginData.setHandle(handle);
        Element cacheElement = new Element(handle, userLoginData);
        userLoginDataCache.put(cacheElement);
    }

    @Override
    public void logoutUser(final String handle) {
        securityHelper.clearUserDetails(handle);
        userLoginDataCache.remove(handle);
    }

    @Override
    public UserAccount getUserAccountByHandle(final String handle) throws SqlDatabaseSystemException {
        Element cacheElement = this.userLoginDataCache.get(handle);
        UserLoginData userLoginData = null;
        if (cacheElement != null) {
            userLoginData = (UserLoginData) cacheElement.getValue();
        }
        else {
            userLoginData = this.userAccountDao.retrieveUserLoginDataByHandle(handle);
            if (userLoginData != null) {
                final Element newCacheElement = new Element(handle, userLoginData);
                userLoginDataCache.put(newCacheElement);
            }
        }
        if (userLoginData != null) {
            return userLoginData.getUserAccount();
        }
        return null;
    }

}
