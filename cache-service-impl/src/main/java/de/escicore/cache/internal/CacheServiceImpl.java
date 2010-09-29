package de.escicore.cache.internal;

import de.escicore.cache.RecacheRequest;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.interfaces.ResourceCacheInterface;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.UserContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link de.escicore.cache.CacheService}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class CacheServiceImpl implements InitializingBean {


    private static final Log LOG = LogFactory.getLog(CacheServiceImpl.class);

    /**
     * Pattern to identify body of resource representation.
     */
    private static final Pattern PATTERN_BODY =
            Pattern.compile("(<[^?].*)", Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * Method name to get a single resource from eSciDoc.
     */
    private static final String RETRIEVE_METHOD_NAME = "retrieve";

    // TODO: Cache ist nicht clusterfähig! Cache gegen clusterfähige Implemententierung ersetzen.
    private Map<ResourceType, ResourceCacheInterface> cacheMap = new HashMap<ResourceType, ResourceCacheInterface>();

    public void recache(final RecacheRequest recacheRequest) {
        // TODO: Refactor this old code.
        try {
            try {
                final boolean isInternalUser = UserContext.isInternalUser();
                if (!isInternalUser) {
                    UserContext.setUserContext("");
                    UserContext.runAsInternalUser();
                }
            } catch (final Exception e) {
                UserContext.setUserContext("");
                UserContext.runAsInternalUser();
            }
            String xmlDataRest = retrieveResource(recacheRequest.getResourceId(), recacheRequest.getResourceType(), true);
            String xmlDataSoap = retrieveResource(recacheRequest.getResourceId(), recacheRequest.getResourceType(), false);
            storeResource(recacheRequest.getResourceType(), recacheRequest.getResourceId(), xmlDataRest, xmlDataSoap);
        }
        catch (Exception e) {
            LOG.error("could not dequeue message", e);
        } finally {
            // TODO: RecacheStatus ist ein Singleton. Dies funktioniert nicht in einer Cluster-Umgebung. Muss überarbeitet werden.
            /*if (recacheRequest.getResourceType() != null) {
                RecacheStatus.getInstance().dec(recacheRequest.getResourceType());
            }*/
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO: Dependency Auflösung mit Spring wird hier umgangen. Spring kann somit rekursive Abhängigkeiten nicht auflösen. BeanLocator muss entfernt werden!
        try {
            cacheMap.put(ResourceType.CONTAINER, BeanLocator.locateContainerCache());
            cacheMap.put(ResourceType.CONTENT_MODEL, BeanLocator.locateContentModelCache());
            cacheMap.put(ResourceType.CONTENT_RELATION, BeanLocator.locateContentRelationCache());
            cacheMap.put(ResourceType.CONTEXT, BeanLocator.locateContextCache());
            cacheMap.put(ResourceType.ITEM, BeanLocator.locateItemCache());
            cacheMap.put(ResourceType.OU, BeanLocator.locateOrganizationalUnitCache());
        } catch (WebserverSystemException e) {
            LOG.error("could not localize bean", e);
        }
    }

    /**
     * Retrieve a single resource.
     *
     * @param id           resource id
     * @param resourceType         resource type
     * @param isRestAccess true if the REST form should be requested
     * @return XML representation of this resource
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     *          Thrown if eSciDoc failed to retrieve the resource.
     */
    private String retrieveResource(
            final String id, final String resourceType, final boolean isRestAccess)
            throws SystemException {
        String result = null;
        final boolean oldValue = UserContext.isRestAccess();
        UserContext.setRestAccess(isRestAccess);
        try {
            final Object handler = getHandler(ResourceType.valueOf(resourceType));
            final Method retrieveMethod = handler.getClass().getMethod(RETRIEVE_METHOD_NAME, String.class);
            result = (String) retrieveMethod.invoke(handler, id);
            Matcher m = PATTERN_BODY.matcher(result);
            if (m.find()) {
                result = m.group(1);
            }
        } catch (final Exception e) {
            LOG.error("could not retrieve resource", e);
            throw new SystemException(e);
        }
        UserContext.setRestAccess(oldValue);
        return result;
    }

    /**
     * Store a resource in the database cache.
     *
     * @param resourceType        resource type
     * @param id          resource id
     * @param xmlDataRest complete item as XML (REST form)
     * @param xmlDataSoap complete item as XML (SOAP form)
     * @throws SystemException Thrown if eSciDoc failed to receive a resource.
     */
    private void storeResource(
            final String resourceType, final String id, final String xmlDataRest,
            final String xmlDataSoap) throws SystemException {
        this.cacheMap.get(ResourceType.valueOf(resourceType)).add(id, xmlDataRest, xmlDataSoap);
    }

    /**
     * Return the corresponding resource handler for the given type.
     *
     * @param type resource type
     * @return resource handler for the given type
     * @throws WebserverSystemException Thrown if a resource handler could not be localized.
     */
    private Object getHandler(final ResourceType type)
            throws WebserverSystemException {
        Object result = null;
        if (type == ResourceType.CONTAINER) {
            result = BeanLocator.locateContainerHandler();
        } else if (type == ResourceType.CONTENT_MODEL) {
            result = BeanLocator.locateContentModelHandler();
        } else if (type == ResourceType.CONTENT_RELATION) {
            result = BeanLocator.locateContentRelationHandler();
        } else if (type == ResourceType.CONTEXT) {
            result = BeanLocator.locateContextHandler();
        } else if (type == ResourceType.ITEM) {
            result = BeanLocator.locateItemHandler();
        } else if (type == ResourceType.OU) {
            result = BeanLocator.locateOrganizationalUnitHandler();
        }
        return result;
    }

}
