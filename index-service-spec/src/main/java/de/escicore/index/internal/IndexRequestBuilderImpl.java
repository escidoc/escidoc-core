package de.escicore.index.internal;

import de.escicore.index.IndexRequest;
import de.escicore.index.IndexRequestBuilder;


/**
 * Default implementation of {@link IndexRequestBuilder}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class IndexRequestBuilderImpl extends IndexRequestBuilder {

    private IndexRequestImpl indexRequest = new IndexRequestImpl();

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexRequestBuilder withAction(final String action) {
        this.indexRequest.setAction(action);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexRequestBuilder withIndexName(final String indexName) {
        this.indexRequest.setIndexName(indexName);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexRequestBuilder withResource(final String resource) {
        this.indexRequest.setResource(resource);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexRequestBuilder withObjectType(final String objectType) {
        this.indexRequest.setObjectType(objectType);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexRequestBuilder withData(final String data) {
        this.indexRequest.setData(data);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexRequest build() {
        final IndexRequest returnValue = this.indexRequest;
        this.indexRequest = new IndexRequestImpl();
        return returnValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "IndexRequestBuilderImpl{" +
                "indexRequest=" + indexRequest +
                '}';
    }
}
