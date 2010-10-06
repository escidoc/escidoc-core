package de.escidoc.core.cache.internal;

import de.escidoc.core.cache.RecacheRequest;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.UUID;

/**
 * Default implementation of {@link RecacheRequest}.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@XmlRootElement(name="RecacheRequest", namespace = "http://www.escidoc.de/schemas/cache-service/1.0/")
@XmlType(name="RecacheRequest", namespace = "http://www.escidoc.de/schemas/cache-service/1.0/")
public class RecacheRequestImpl implements RecacheRequest {

    private final String requestId = UUID.randomUUID().toString();
    private final Date creationTimestamp = new Date();
    private String resourceId;
    private String resourceType;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestId() {
        return this.requestId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getCreationTimestamp() {
        return new Date(this.creationTimestamp.getTime());
    }

    /**
     * {@inheritDoc}
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Sets the resource identiefier.
     *
     * @param resourceId the resource identifier
     */
    public void setResourceId(final String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * {@inheritDoc}
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Sets the resource type.
     *
     * @param resourceType the resource type
     */
    public void setResourceType(final String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "RecacheRequestImpl{" +
                "requestId='" + requestId + '\'' +
                ", creationTimestamp=" + creationTimestamp +
                ", resourceId='" + resourceId + '\'' +
                ", resourceType='" + resourceType + '\'' +
                '}';
    }
}

