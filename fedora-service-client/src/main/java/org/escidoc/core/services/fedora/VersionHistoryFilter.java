package org.escidoc.core.services.fedora;

import org.escidoc.core.util.xml.internal.EsciDocUnmarshallerListener;

import java.io.InputStream;

public class VersionHistoryFilter extends EsciDocUnmarshallerListener {

    public VersionHistoryFilter(final InputStream inputStream) {
        super(inputStream);
    }

    public void beforeUnmarshal(Object target, Object parent) {
        super.beforeUnmarshal(target, parent);
        if (target instanceof DigitalObjectTO) {
            final DigitalObjectTO digitalObjectTO = (DigitalObjectTO) target;
            digitalObjectTO.addDatastreamTypeTOListener(new DatastreamTypeTOListener() {
                public DatastreamTypeTO process(DatastreamTypeTO datastreamTypeTO) {
                    if (!"version-history".equals(datastreamTypeTO.getID())) {
                        return null;
                    }
                    return datastreamTypeTO;
                }
            });
        } else if (target instanceof DatastreamTypeTOExtension && parent instanceof DigitalObjectTO) {
            final DatastreamTypeTOExtension datastreamTypeTOExtension = (DatastreamTypeTOExtension) target;
            datastreamTypeTOExtension.addDatastreamVersionTypeTOListener(new DatastreamVersionTypeTOListener() {

                private DatastreamTypeTO datastreamTypeTO = datastreamTypeTOExtension;

                public DatastreamVersionTypeTO process(DatastreamVersionTypeTO datastreamVersionTypeTO) {
                    if("version-history".equals(datastreamTypeTO.getID())) {
                        return datastreamVersionTypeTO;
                    }
                    return null;
                }
            });
        }
    }
}


