package de.escidoc.core.purge.internal;

import de.escidoc.core.purge.PurgeRequest;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.UUID;

/**
 * Default implementation of {@link PurgeRequest}.
 */
@XmlRootElement(name = "PurgeRequest", namespace = "http://www.escidoc.de/schemas/purge-service/1.0/")
@XmlType(name = "PurgeRequest", namespace = "http://www.escidoc.de/schemas/purge-service/1.0/")
public class PurgeRequestImpl implements PurgeRequest {

    private final String requestId = UUID.randomUUID().toString();

    private final Date creationTimestamp = new Date();

    private String resourceId;

    @Override
    public String getRequestId() {
        return this.requestId;
    }

    @Override
    public Date getCreationTimestamp() {
        return new Date(this.creationTimestamp.getTime());
    }

    @Override
    public String getResourceId() {
        return this.resourceId;
    }

    /**
     * Sets the resource identifier.
     *
     * @param resourceId the resource identifier
     */
    public void setResourceId(final String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String toString() {
        return "PurgeRequestImpl{" + "requestId='" + this.requestId + '\'' + ", creationTimestamp="
            + this.creationTimestamp + ", resourceId='" + this.resourceId + '\'' + '}';
    }
}
