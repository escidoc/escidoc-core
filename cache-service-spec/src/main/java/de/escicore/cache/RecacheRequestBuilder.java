package de.escicore.cache;

import de.escicore.cache.internal.RecacheRequestBuilderImpl;

/**
 * Builder for {@link RecacheRequest}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public abstract class RecacheRequestBuilder {


    public static RecacheRequestBuilder createRecacheRequest() {
        return new RecacheRequestBuilderImpl();
    }

    public abstract RecacheRequestBuilder withResourceId(final String resourceId);

    public abstract RecacheRequestBuilder withResourceType(final String resourceType);

    public abstract RecacheRequest build();

}
