package org.escidoc.core.services.fedora;

import org.escidoc.core.services.fedora.access.ObjectDatastreamsTO;
import org.escidoc.core.services.fedora.access.ObjectProfileTO;
import org.esidoc.core.utils.VoidObject;
import org.esidoc.core.utils.io.Datastream;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Future;

/**
 * REST Client for Fedora repository.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public interface FedoraServiceClient {

    PidListTO getNextPID(@NotNull String namespace);

    PidListTO getNextPID(@NotNull String namespace, int numPIDs);

    Future<PidListTO> getNextPIDAsync(@NotNull String namespace);

    Future<PidListTO> getNextPIDAsync(@NotNull String namespace, int numPIDs);

    // TODO: Refactor interface. Use real parameters and not REST interface (Path and Query objects)

    void createObject(CreateObjectPathParam path, CreateObjectQueryParam query);

    Future<VoidObject> createObjectAsync(CreateObjectPathParam path, CreateObjectQueryParam query);

    ObjectProfileTO getObjectProfile(GetObjectProfilePathParam path, GetObjectProfileQueryParam query);

    Future<ObjectProfileTO> getObjectProfileAsync(GetObjectProfilePathParam path, GetObjectProfileQueryParam query);

    void addDatastream(AddDatastreamPathParam path, AddDatastreamQueryParam query, Datastream outputStream);

    Future<VoidObject> addDatastreamAsync(AddDatastreamPathParam path, AddDatastreamQueryParam query, Datastream outputStream);

    Datastream getDatastream(GetDatastreamPathParam path, GetDatastreamQueryParam query);

    Future<Datastream> getDatastreamAsync(GetDatastreamPathParam path, GetDatastreamQueryParam query);

    void modifyDatastream(ModifiyDatastreamPathParam path,
                          ModifyDatastreamQueryParam query,
                          Datastream outputStream);

    Future<VoidObject> modifyDatastreamAsync(ModifiyDatastreamPathParam path,
                                 ModifyDatastreamQueryParam query,
                                 Datastream outputStream);

    ObjectDatastreamsTO listDatastreams(ListDatastreamsPathParam path,
                                        ListDatastreamsQueryParam query);

    Future<ObjectDatastreamsTO> listDatastreamsAsync(ListDatastreamsPathParam path,
                                                     ListDatastreamsQueryParam query);

    void updateObject(UpdateObjectPathParam path, UpdateObjectQueryParam query);

    Future<VoidObject> updateObjectAsync(UpdateObjectPathParam path, UpdateObjectQueryParam query);

    void deleteObject(DeleteObjectPathParam path, DeleteObjectQueryParam query);

    Future<VoidObject> deleteObjectAsync(DeleteObjectPathParam path, DeleteObjectQueryParam query);

    String ingest(IngestPathParam path, IngestQueryParam query, DigitalObjectTypeTOExtension digitalObjectTO);

    Future<String> ingestAsync(IngestPathParam path, IngestQueryParam query, DigitalObjectTypeTOExtension digitalObjectTO);

    DigitalObjectTO getObjectXML(GetObjectXMLPathParam path, GetObjectXMLQueryParam query);

    Future<DigitalObjectTO> getObjectXMLAsync(GetObjectXMLPathParam path, GetObjectXMLQueryParam query);

}
