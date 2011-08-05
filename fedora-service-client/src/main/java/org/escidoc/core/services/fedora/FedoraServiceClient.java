package org.escidoc.core.services.fedora;

import java.util.Collection;
import java.util.concurrent.Future;

import javax.validation.constraints.NotNull;

import net.sf.oval.constraint.NotEmpty;

import org.escidoc.core.services.fedora.access.ObjectDatastreamsTO;
import org.escidoc.core.services.fedora.access.ObjectProfileTO;
import org.escidoc.core.services.fedora.management.DatastreamHistoryTO;
import org.escidoc.core.services.fedora.management.DatastreamProfileTO;
import org.escidoc.core.services.fedora.management.DatastreamProfilesTO;
import org.esidoc.core.utils.VoidObject;
import org.esidoc.core.utils.io.MimeStream;
import org.esidoc.core.utils.io.Stream;
import org.joda.time.DateTime;

import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * REST Client for Fedora repository.
 * 
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public interface FedoraServiceClient {

    PidListTO getNextPID(@NotNull @NotEmpty String namespace);

    PidListTO getNextPID(@NotNull @NotEmpty String namespace, int numPIDs);

    Future<PidListTO> getNextPIDAsync(@NotNull @NotEmpty String namespace);

    Future<PidListTO> getNextPIDAsync(@NotNull @NotEmpty String namespace, int numPIDs);

    ObjectProfileTO getObjectProfile(@NotNull @NotEmpty String pid);

    Future<ObjectProfileTO> getObjectProfileAsync(@NotNull @NotEmpty String pid);

    DigitalObjectTO export(@NotNull @NotEmpty final String pid, final String format, final String context, final String encoding);

    Future<DigitalObjectTO> exportAsync(@NotNull @NotEmpty final String pid, final String format, final String context, final String encoding);

    Stream exportAsStream(@NotNull @NotEmpty final String pid, final String format, final String context, final String encoding);

    Future<Stream> exportAsStreamAsync(@NotNull @NotEmpty final String pid, final String format, final String context, final String encoding);

    DigitalObjectTO getObjectXML(@NotNull @NotEmpty final String pid);

    Future<DigitalObjectTO> getObjectXMLAsync(@NotNull @NotEmpty final String pid);

    Stream getObjectXMLAsStream(@NotNull @NotEmpty final String pid);

    Future<Stream> getObjectXMLAsStreamAsync(@NotNull @NotEmpty final String pid);

    void updateObject(@NotNull UpdateObjectPathParam path, @NotNull UpdateObjectQueryParam query);

    Future<VoidObject> updateObjectAsync(@NotNull UpdateObjectPathParam path, @NotNull UpdateObjectQueryParam query);

    void deleteObject(@NotNull String pid);

    Future<VoidObject> deleteObjectAsync(@NotNull String pid);

    /**
     * 
     * @param path
     * @param query
     * @param digitalObjectTO
     * @return The ID of the persisted resource.
     */
    String ingest(
        @NotNull IngestPathParam path, @NotNull IngestQueryParam query, @NotNull DigitalObjectTO digitalObjectTO);

    /**
     * FIXME Fix {@link FedoraServiceClient#ingest(IngestPathParam, IngestQueryParam, DigitalObjectTO)} and remove this
     * method.
     * 
     * @param path
     * @param query
     * @param foxml
     * @return The ID of the persisted resource.
     */
    String ingest(@NotNull IngestPathParam path, @NotNull IngestQueryParam query, @NotNull String foxml);

    Future<String> ingestAsync(
        @NotNull IngestPathParam path, @NotNull IngestQueryParam query, @NotNull DigitalObjectTO digitalObjectTO);

    DatastreamProfileTO addDatastream(
        @NotNull AddDatastreamPathParam path, @NotNull AddDatastreamQueryParam query, Stream outputStream);

    Future<DatastreamProfileTO> addDatastreamAsync(
        @NotNull AddDatastreamPathParam path, @NotNull AddDatastreamQueryParam query, Stream outputStream);

    MimeStream getDatastream(@NotNull final String pid, @NotNull final String dsID, final DateTime timestamp);

    Future<MimeStream> getDatastreamAsync(@NotNull final String pid, @NotNull final String dsID, final DateTime timestamp);

    DatastreamProfileTO getDatastreamProfile(
        @NotNull GetDatastreamProfilePathParam path, @NotNull GetDatastreamProfileQueryParam query);

    Future<DatastreamProfileTO> getDatastreamProfileAsync(
        @NotNull GetDatastreamProfilePathParam path, @NotNull GetDatastreamProfileQueryParam query);

    DatastreamProfileTO modifyDatastream(
        @NotNull ModifiyDatastreamPathParam path, @NotNull ModifyDatastreamQueryParam query, Stream outputStream);

    Future<DatastreamProfileTO> modifyDatastreamAsync(
        @NotNull ModifiyDatastreamPathParam path, @NotNull ModifyDatastreamQueryParam query, Stream outputStream);

    void deleteDatastream(@NotNull DeleteDatastreamPathParam path, @NotNull DeleteDatastreamQueryParam query);

    Future<VoidObject> deleteDatastreamAsync(
        @NotNull DeleteDatastreamPathParam path, @NotNull DeleteDatastreamQueryParam query);

    DatastreamProfileTO setDatastreamState(
        @NotNull String pid, @NotNull @NotEmpty String dsID, @NotNull @NotEmpty DatastreamState state);

    Future<DatastreamProfileTO> setDatastreamStateAsync(
        @NotNull String pid, @NotNull @NotEmpty String dsID, @NotNull @NotEmpty DatastreamState state);

    ObjectDatastreamsTO listDatastreams(@NotNull String pid, DateTime timestamp);

    Future<ObjectDatastreamsTO> listDatastreamsAsync(@NotNull String pid, DateTime timestamp);

    DatastreamHistoryTO getDatastreamHistory(
        @NotNull GetDatastreamHistoryPathParam path, @NotNull GetDatastreamHistoryQueryParam query);

    Future<DatastreamHistoryTO> getDatastreamHistoryAsync(
        @NotNull GetDatastreamHistoryPathParam path, @NotNull GetDatastreamHistoryQueryParam query);

    DatastreamProfilesTO getDatastreamProfiles(@NotNull String pid, DateTime timestamp);

    Future<DatastreamProfilesTO> getDatastreamProfilesAsync(@NotNull String pid, DateTime timestamp);

    DatastreamProfilesTO getDatastreamProfilesByAltId(@NotNull String pid, @NotNull String altId, DateTime timestamp);

    Future<DatastreamProfilesTO> getDatastreamProfilesByAltIdAsync(
        @NotNull String pid, @NotNull String altId, DateTime timestamp);

    Collection<String> queryResourceIdsByType(@NotNull String resourceType);

    Future<Collection<String>> queryResourceIdsByTypeAsync(@NotNull String resourceType);

    Stream getBinaryContent(@NotNull String pid, @NotNull String dsId, DateTime versionDate);

    Future<Stream> getBinaryContentAsync(@NotNull String pid, @NotNull String dsId, DateTime versionDate);

    MimeStream getMimeTypedBinaryContent(@NotNull String pid, @NotNull String dsId, DateTime versionDate)
        throws ResourceNotFoundException, SystemException;

    Future<MimeStream> getMimeTypedBinaryContentAsync(@NotNull String pid, @NotNull String dsId, DateTime versionDate)
        throws ResourceNotFoundException, SystemException;

    Stream getDissemination(@NotNull String pid, @NotNull String contentModelPid, @NotNull String methodName);

    Future<Stream> getDisseminationAsync(
        @NotNull String pid, @NotNull String contentModelPid, @NotNull String methodName);

    Stream risearch(@NotNull RisearchPathParam path, @NotNull RisearchQueryParam query);

    Future<Stream> risearchAsync(@NotNull RisearchPathParam path, @NotNull RisearchQueryParam query);

    void sync();
}
