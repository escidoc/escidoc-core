package org.escidoc.core.services.fedora;

import org.escidoc.core.services.fedora.access.ObjectDatastreamsTO;
import org.escidoc.core.services.fedora.access.ObjectProfileTO;
import org.escidoc.core.services.fedora.management.DatastreamProfileTO;
import org.esidoc.core.utils.VoidObject;
import org.esidoc.core.utils.io.Datastream;

import javax.validation.constraints.NotNull;
import java.io.InputStream;
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

    void createObject(@NotNull CreateObjectPathParam path,
                      @NotNull CreateObjectQueryParam query);

    Future<VoidObject> createObjectAsync(@NotNull CreateObjectPathParam path,
                                         @NotNull CreateObjectQueryParam query);

    ObjectProfileTO getObjectProfile(@NotNull String pid);

    Future<ObjectProfileTO> getObjectProfileAsync(@NotNull String pid);

    DatastreamProfileTO addDatastream(@NotNull AddDatastreamPathParam path,
                       @NotNull AddDatastreamQueryParam query,
                       Datastream outputStream);

    Future<DatastreamProfileTO> addDatastreamAsync(@NotNull AddDatastreamPathParam path,
                                          @NotNull AddDatastreamQueryParam query,
                                          Datastream outputStream);

    Datastream getDatastream(@NotNull GetDatastreamPathParam path,
                             @NotNull GetDatastreamQueryParam query);

    Future<Datastream> getDatastreamAsync(@NotNull GetDatastreamPathParam path,
                                          @NotNull GetDatastreamQueryParam query);

    DatastreamProfileTO modifyDatastream(@NotNull ModifiyDatastreamPathParam path,
                          @NotNull ModifyDatastreamQueryParam query,
                          Datastream outputStream);

    Future<DatastreamProfileTO> modifyDatastreamAsync(@NotNull ModifiyDatastreamPathParam path,
                                 @NotNull ModifyDatastreamQueryParam query,
                                 Datastream outputStream);

    ObjectDatastreamsTO listDatastreams(@NotNull ListDatastreamsPathParam path,
                                        @NotNull ListDatastreamsQueryParam query);

    Future<ObjectDatastreamsTO> listDatastreamsAsync(@NotNull ListDatastreamsPathParam path,
                                                     @NotNull ListDatastreamsQueryParam query);

    void updateObject(@NotNull UpdateObjectPathParam path,
                      @NotNull UpdateObjectQueryParam query);

    Future<VoidObject> updateObjectAsync(@NotNull UpdateObjectPathParam path,
                                         @NotNull UpdateObjectQueryParam query);

    void deleteObject(@NotNull String pid);

    Future<VoidObject> deleteObjectAsync(@NotNull String pid);

    String ingest(@NotNull IngestPathParam path,
                  @NotNull IngestQueryParam query,
                  @NotNull DigitalObjectTypeTOExtension digitalObjectTO);

    Future<String> ingestAsync(@NotNull IngestPathParam path,
                               @NotNull IngestQueryParam query,
                               @NotNull DigitalObjectTypeTOExtension digitalObjectTO);

    DigitalObjectTO getObjectXML(@NotNull GetObjectXMLPathParam path,
                                 @NotNull GetObjectXMLQueryParam query);

    Future<DigitalObjectTO> getObjectXMLAsync(@NotNull GetObjectXMLPathParam path,
                                              @NotNull GetObjectXMLQueryParam query);

    InputStream getObjectXMLAsStream(@NotNull GetObjectXMLPathParam path,
                                     @NotNull GetObjectXMLQueryParam query);

    Future<InputStream> getObjectXMLAsStreamAsync(@NotNull GetObjectXMLPathParam path,
                                          @NotNull GetObjectXMLQueryParam query);

}
