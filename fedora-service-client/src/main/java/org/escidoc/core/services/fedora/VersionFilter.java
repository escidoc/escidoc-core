package org.escidoc.core.services.fedora;

import org.escidoc.core.util.xml.internal.EsciDocUnmarshallerListener;
import org.joda.time.DateTime;

import java.io.InputStream;

import static org.esidoc.core.utils.Preconditions.checkNotNull;

public class VersionFilter extends EsciDocUnmarshallerListener {

    private DateTime versionDate;

    public VersionFilter(final InputStream inputStream, final DateTime versionDate) {
        super(inputStream);
        this.versionDate = checkNotNull(versionDate, "Version date can not be null.");
    }

    public void beforeUnmarshal(Object target, Object parent) {
        super.beforeUnmarshal(target, parent);
        if (target instanceof DatastreamTypeTOExtension && parent instanceof DigitalObjectTO) {
            final DatastreamTypeTOExtension datastreamTypeTOExtension = (DatastreamTypeTOExtension) target;
            datastreamTypeTOExtension.addDatastreamVersionTypeTOListener(new DatastreamVersionTypeTOListener() {

                private DatastreamTypeTO datastreamTypeTO = datastreamTypeTOExtension;

                public DatastreamVersionTypeTO process(DatastreamVersionTypeTO datastreamVersionTypeTO) {
                    if (versionDate.equals(datastreamVersionTypeTO.getCREATED())) {
                        if (datastreamTypeTO.getDatastreamVersion().size() > 0) {
                            datastreamTypeTO.getDatastreamVersion().remove(0);
                        }
                        return datastreamVersionTypeTO;
                    } else if (versionDate.isAfter(datastreamVersionTypeTO.getCREATED())) {
                        if (datastreamTypeTO.getDatastreamVersion().size() == 0) {
                            return datastreamVersionTypeTO;
                        } else {
                            final DatastreamVersionTypeTO lastDatastreamVersionTypeTO = datastreamTypeTO.getDatastreamVersion().get(0);
                            if(datastreamVersionTypeTO.getCREATED().isAfter(lastDatastreamVersionTypeTO.getCREATED())) {
                                datastreamTypeTO.getDatastreamVersion().remove(0);
                                return datastreamVersionTypeTO;
                            }
                        }
                    }
                    return null;
                }
            });
        }
    }
}
