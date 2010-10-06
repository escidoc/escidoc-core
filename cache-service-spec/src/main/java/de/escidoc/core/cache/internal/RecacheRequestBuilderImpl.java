package de.escidoc.core.cache.internal;

import de.escidoc.core.cache.RecacheRequest;
import de.escidoc.core.cache.RecacheRequestBuilder;

/**
 * Default implementation of {@link RecacheRequestBuilder}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class RecacheRequestBuilderImpl extends RecacheRequestBuilder {

    private RecacheRequestImpl recacheRequest = new RecacheRequestImpl();

    /**
     * {@inheritDoc}
     */
    @Override
    public RecacheRequestBuilder withResourceId(final String resourceId) {
        this.recacheRequest.setResourceId(resourceId);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecacheRequestBuilder withResourceType(final String resourceType) {
        this.recacheRequest.setResourceType(resourceType);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecacheRequest build() {
        final RecacheRequest returnValue = this.recacheRequest;
        this.recacheRequest = new RecacheRequestImpl();
        return returnValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "RecacheRequestBuilderImpl{" +
                "recacheRequest=" + recacheRequest +
                '}';
    }
}
