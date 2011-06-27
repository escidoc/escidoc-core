package org.escidoc.core.services.fedora;


import java.util.List;

/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class DigitalObjectTypeTOExtension extends DigitalObjectTypeTO {

    public void addDatastreamTypeTOListener(DatastreamTypeTOListener listener) {
        final DatastreamTypeListTO datastreamTypeListTO = (DatastreamTypeListTO) this.getDatastream();
        datastreamTypeListTO.addDatastreamTypeTOListener(listener);
    }

    public void removeDatastreamTypeTOListener(DatastreamTypeTOListener listener) {
        final DatastreamTypeListTO datastreamTypeListTO = (DatastreamTypeListTO) this.getDatastream();
        datastreamTypeListTO.removeDatastreamTypeTOListener(listener);
    }

    public DigitalObjectTypeTOExtension() {
        this.datastream = new DatastreamTypeListTO();
    }

    @Override
    public List<DatastreamTypeTO> getDatastream() {
        if(this.datastream == null) {
            this.datastream = new DatastreamTypeListTO();
        }
        return this.datastream;
    }

}
