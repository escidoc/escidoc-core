package de.escidoc.core.index.internal;

import de.escidoc.core.index.IndexRequest;
import de.escidoc.core.index.IndexRequestBuilder;

/**
 * Default implementation of {@link IndexRequestBuilder}.
 */
public class IndexRequestBuilderImpl extends IndexRequestBuilder {

    private IndexRequestImpl indexRequest = new IndexRequestImpl();

    @Override
    public IndexRequestBuilder withAction(final String action) {
        this.indexRequest.setAction(action);
        return this;
    }

    @Override
    public IndexRequestBuilder withIndexName(final String indexName) {
        this.indexRequest.setIndexName(indexName);
        return this;
    }

    @Override
    public IndexRequestBuilder withResource(final String resource) {
        this.indexRequest.setResource(resource);
        return this;
    }

    @Override
    public IndexRequestBuilder withObjectType(final String objectType) {
        this.indexRequest.setObjectType(objectType);
        return this;
    }

    @Override
    public IndexRequestBuilder withData(final String data) {
        this.indexRequest.setData(data);
        return this;
    }

    @Override
    public IndexRequestBuilder withIsReindexerCaller(final boolean isReindexerCaller) {
        this.indexRequest.setIsReindexerCaller(isReindexerCaller);
        return this;
    }

    @Override
    public IndexRequest build() {
        final IndexRequest returnValue = this.indexRequest;
        this.indexRequest = new IndexRequestImpl();
        return returnValue;
    }

    @Override
    public String toString() {
        return "IndexRequestBuilderImpl{" + "indexRequest=" + this.indexRequest + '}';
    }
}
