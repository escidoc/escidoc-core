package org.escidoc.core.services.fedora.internal.cache;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import org.aopalliance.intercept.MethodInvocation;
import org.escidoc.core.services.fedora.GetDatastreamPathParam;

/**
 * {@link CacheKeyGenerator} for getDatastream-Operation in
 * {@link org.escidoc.core.services.fedora.FedoraServiceClient}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class GetDatastreamKeyGenerator implements CacheKeyGenerator<DatastreamCacheKey> {

    public DatastreamCacheKey generateKey(final MethodInvocation methodInvocation) {
        return this.generateKey(methodInvocation.getArguments());
    }

    public DatastreamCacheKey generateKey(final Object... objects) {
        if (objects.length > 0) {
            if (objects[0] instanceof GetDatastreamPathParam) {
                final GetDatastreamPathParam getDatastreamPathParam = (GetDatastreamPathParam) objects[0];
                return new DatastreamCacheKey(getDatastreamPathParam.getPid(), getDatastreamPathParam.getDsID());
            }
        }
        return null;
    }
}
