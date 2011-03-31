package de.escidoc.core.purge.internal;

import de.escidoc.core.purge.PurgeRequest;
import de.escidoc.core.purge.PurgeRequestBuilder;

/**
 * Default implementation for {@link PurgeRequestBuilder}.
 */
public class PurgeRequestBuilderImpl extends PurgeRequestBuilder {

    private PurgeRequestImpl purgeRequest = new PurgeRequestImpl();

    @Override
    public PurgeRequestBuilder withResourceId(final String resourceId) {
        this.purgeRequest.setResourceId(resourceId);
        return this;
    }

    @Override
    public PurgeRequest build() {
        final PurgeRequest returnValue = this.purgeRequest;
        this.purgeRequest = new PurgeRequestImpl();
        return returnValue;
    }

    @Override
    public String toString() {
        return "PurgeRequestBuilderImpl{" + "purgeRequest=" + this.purgeRequest + '}';
    }
}
