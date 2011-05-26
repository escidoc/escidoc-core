package org.escidoc.core.services.fedora;

import net.sf.oval.guard.Guarded;
import org.escidoc.core.util.xml.internal.EsciDocUnmarshallerListener;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.io.InputStream;

import static org.esidoc.core.utils.Preconditions.checkNotNull;

@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true,
        assertParametersNotNull = false, checkInvariants=true, inspectInterfaces = true)
public class VersionFilter extends EsciDocUnmarshallerListener {

    private DateTime versionDate;

    public VersionFilter(@NotNull final InputStream inputStream, @NotNull final DateTime versionDate) {
        super(inputStream);
        this.versionDate = versionDate;
    }

    public void beforeUnmarshal(Object target, Object parent) {
        super.beforeUnmarshal(target, parent);
        if (target instanceof DatastreamTypeTOExtension && parent instanceof DigitalObjectTO) {
            final DatastreamTypeTOExtension datastreamTypeTOExtension = (DatastreamTypeTOExtension) target;
            datastreamTypeTOExtension.addDatastreamVersionTypeTOListener(new DatastreamVersionTypeTOListener() {

                private DatastreamTypeTO datastreamTypeTO = datastreamTypeTOExtension;

                public DatastreamVersionTypeTO process(DatastreamVersionTypeTO datastreamVersionTypeTO) {
                    if (versionDate.equals(datastreamVersionTypeTO.getCREATED())) {
                        if (! datastreamTypeTO.getDatastreamVersion().isEmpty()) {
                            datastreamTypeTO.getDatastreamVersion().remove(0);
                        }
                        return datastreamVersionTypeTO;
                    } else if (versionDate.isAfter(datastreamVersionTypeTO.getCREATED())) {
                        if (datastreamTypeTO.getDatastreamVersion().isEmpty()) {
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
