package org.escidoc.core.services.fedora;

import java.util.List;

public class DatastreamTypeTOExtension extends DatastreamTypeTO {

    private static final long serialVersionUID = - 6022506140785157451L;

    public void addDatastreamVersionTypeTOListener(final DatastreamVersionTypeTOListener listener) {
        final DatastreamVersionTypeListTO datastreamVersionTypeListTO =
                (DatastreamVersionTypeListTO) this.getDatastreamVersion();
        datastreamVersionTypeListTO.addDatastreamVersionTypeTOListener(listener);
    }

    public void removeDatastreamVersionTypeTOListener(final DatastreamVersionTypeTOListener listener) {
        final DatastreamVersionTypeListTO datastreamVersionTypeListTO =
                (DatastreamVersionTypeListTO) this.getDatastreamVersion();
        datastreamVersionTypeListTO.removeDatastreamVersionTypeTOListener(listener);
    }

    public DatastreamTypeTOExtension() {
        this.datastreamVersion = new DatastreamVersionTypeListTO();
    }

    @Override
    public List<DatastreamVersionTypeTO> getDatastreamVersion() {
        if(this.datastreamVersion == null) {
            this.datastreamVersion = new DatastreamVersionTypeListTO();
        }
        return this.datastreamVersion;
    }

}
