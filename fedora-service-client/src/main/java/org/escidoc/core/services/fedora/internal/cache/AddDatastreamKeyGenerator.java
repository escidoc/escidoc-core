package org.escidoc.core.services.fedora.internal.cache;

import com.googlecode.ehcache.annotations.key.CacheKeyGenerator;
import org.aopalliance.intercept.MethodInvocation;
import org.escidoc.core.services.fedora.AddDatastreamPathParam;

/**
 * {@link CacheKeyGenerator} for addDatastream-Operation in {@link org.escidoc.core.services.fedora.FedoraServiceClient}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public final class AddDatastreamKeyGenerator implements CacheKeyGenerator<String> {

    @Override
    public String generateKey(final MethodInvocation methodInvocation) {
        return this.generateKey(methodInvocation.getArguments());
    }

    @Override
    public String generateKey(final Object... objects) {
        if(objects.length > 0) {
            if(objects[0] instanceof AddDatastreamPathParam) {
                final AddDatastreamPathParam addDatastreamPathParam = (AddDatastreamPathParam) objects[0];
                return addDatastreamPathParam.getPid();
            }
        }
        return null;
    }
}