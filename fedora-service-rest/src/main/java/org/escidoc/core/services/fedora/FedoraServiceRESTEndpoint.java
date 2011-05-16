package org.escidoc.core.services.fedora;

import org.escidoc.core.services.fedora.access.ObjectDatastreamsTO;
import org.escidoc.core.services.fedora.access.ObjectProfileTO;
import org.esidoc.core.utils.io.Datastream;
import org.esidoc.core.utils.io.MimeTypes;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Service to access the Fedora repository.
 *
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
@Path("/objects")
public interface FedoraServiceRESTEndpoint {

    @POST
    @Path("nextPID")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    PidListTO getNextPID(@NotNull @PathParam("") NextPIDPathParam path,
                         @NotNull @QueryParam("") NextPIDQueryParam query);

    @POST
    @Path("{pid}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    void createObject(@NotNull @PathParam("") CreateObjectPathParam path,
                      @NotNull @QueryParam("") CreateObjectQueryParam query);

    @GET
    @Path("{pid}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    ObjectProfileTO getObjectProfile(@NotNull @PathParam("") GetObjectProfilePathParam path,
                                     @NotNull @QueryParam("") GetObjectProfileQueryParam query);

    @POST
    @Path("/{pid}/datastreams/{dsID}")
    @Produces(MimeTypes.ALL)
    @Consumes(MimeTypes.TEXT_XML)
    void addDatastream(@NotNull @PathParam("") AddDatastreamPathParam path,
                       @NotNull @QueryParam("") AddDatastreamQueryParam query,
                       @NotNull Datastream inputStream);

    @GET
    @Path("/{pid}/datastreams/{dsID}/content")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.ALL)
    Datastream getDatastream(@NotNull @PathParam("") GetDatastreamPathParam path,
                             @NotNull @QueryParam("") GetDatastreamQueryParam query);

    @PUT
    @Path("/{pid}/datastreams/{dsID}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.ALL)
    void modifyDatastream(@NotNull @PathParam("") ModifiyDatastreamPathParam path,
                          @NotNull @QueryParam("") ModifyDatastreamQueryParam query,
                          @NotNull Datastream datastream);

    @GET
    @Path("/{pid}/datastreams")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    ObjectDatastreamsTO listDatastreams(@NotNull @PathParam("") ListDatastreamsPathParam path,
                                        @NotNull @QueryParam("") ListDatastreamsQueryParam query);

    @PUT
    @Path("{pid}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    void updateObject(@NotNull @PathParam("") UpdateObjectPathParam path,
                      @NotNull @QueryParam("") UpdateObjectQueryParam query);

    @DELETE
    @Path("{pid}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    void deleteObject(@NotNull @PathParam("") DeleteObjectPathParam path,
                      @NotNull @QueryParam("") DeleteObjectQueryParam query);

    @GET
    @Path("{pid}/objectXML")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    DigitalObjectTO getObjectXML(@NotNull @PathParam("") GetObjectXMLPathParam path,
                                 @NotNull @QueryParam("") GetObjectXMLQueryParam query);

    @POST
    @Path("{pid}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    String ingest(@NotNull @PathParam("") IngestPathParam path,
                  @NotNull @QueryParam("") IngestQueryParam query,
                  @NotNull DigitalObjectTypeTOExtension digitalObjectTO);

}
