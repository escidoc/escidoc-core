package de.escidoc.core.purge.internal;

import de.escidoc.core.adm.business.admin.PurgeStatus;
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.purge.PurgeRequest;
import de.escidoc.core.purge.PurgeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Default implementation of {@link PurgeService}.
 */
public class PurgeServiceImpl implements InitializingBean {

    private static final Log LOGGER = LogFactory.getLog(PurgeServiceImpl.class);

    private FedoraUtility fedoraUtility;

    private TripleStoreUtility tripleStoreUtility;

    public void purge(final PurgeRequest purgeRequest) {
        // TODO: Refector this old code.
        try {
            try {
                final boolean isInternalUser = UserContext.isInternalUser();

                if (!isInternalUser) {
                    UserContext.setUserContext("");
                    UserContext.runAsInternalUser();
                }
            } catch (final Exception e) {
                if(LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Error on setting user context.");
                }
                if(LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Error on setting user context.", e);
                }
                UserContext.setUserContext("");
                UserContext.runAsInternalUser();
            }
            for (final String componentId : this.tripleStoreUtility
                .getComponents(purgeRequest.getResourceId())) {
                this.fedoraUtility.deleteObject(componentId, false);
            }
            this.fedoraUtility
                .deleteObject(purgeRequest.getResourceId(), false);
            // synchronize triple store
            this.fedoraUtility.sync();

        }
        catch (final Exception e) {
            LOGGER.error("could not dequeue message", e);
        }
        finally {
            PurgeStatus.getInstance().dec();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            // TODO: Dependency Auflösung mit Spring wird hier umgangen. Spring
            // kann somit rekursive Abhängigkeiten nicht auflösen. BeanLocator
            // muss entfernt werden!
            fedoraUtility =
                (FedoraUtility) BeanLocator.getBean(
                    BeanLocator.COMMON_FACTORY_ID,
                    "escidoc.core.business.FedoraUtility");
            tripleStoreUtility = BeanLocator.locateTripleStoreUtility();
        }
        catch (final WebserverSystemException e) {
            LOGGER.error("could not localize bean", e);
        }
    }
}
