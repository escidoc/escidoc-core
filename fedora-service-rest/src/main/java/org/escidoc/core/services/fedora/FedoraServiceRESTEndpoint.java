package org.escidoc.core.services.fedora;

import org.escidoc.core.services.fedora.access.ObjectDatastreamsTO;
import org.escidoc.core.services.fedora.access.ObjectProfileTO;
import org.esidoc.core.utils.io.Datastream;
import org.esidoc.core.utils.io.MimeTypes;

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
@Path("/fedora/objects")
public interface FedoraServiceRESTEndpoint {

    @POST
    @Path("nextPID")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    PidListTO getNextPID(@PathParam("") NextPIDPathParam path,
                         @QueryParam("") NextPIDQueryParam query);

    @POST
    @Path("{pid}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    void createObject(@PathParam("") CreateObjectPathParam path,
                      @QueryParam("") CreateObjectQueryParam query);

    @GET
    @Path("{pid}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    ObjectProfileTO getObjectProfile(@PathParam("") GetObjectProfilePathParam path,
                                     @QueryParam("") GetObjectProfileQueryParam query);

    @POST
    @Path("/{pid}/datastreams/{dsID}")
    @Produces(MimeTypes.ALL)
    @Consumes(MimeTypes.TEXT_XML)
    void addDatastream(@PathParam("") AddDatastreamPathParam path,
                       @QueryParam("") AddDatastreamQueryParam query,
                       Datastream inputStream);

    @GET
    @Path("/{pid}/datastreams/{dsID}/content")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.ALL)
    Datastream getDatastream(@PathParam("") GetDatastreamPathParam path,
                             @QueryParam("") GetDatastreamQueryParam query);

    @PUT
    @Path("/{pid}/datastreams/{dsID}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.ALL)
    void modifyDatastream(@PathParam("") ModifiyDatastreamPathParam path,
                          @QueryParam("") ModifyDatastreamQueryParam query,
                          Datastream datastream);

    @GET
    @Path("/{pid}/datastreams")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    ObjectDatastreamsTO listDatastreams(@PathParam("") ListDatastreamsPathParam path,
                                        @QueryParam("") ListDatastreamsQueryParam query);

    @PUT
    @Path("{pid}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    void updateObject(@PathParam("") UpdateObjectPathParam path,
                      @QueryParam("") UpdateObjectQueryParam query);

    @DELETE
    @Path("{pid}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    void deleteObject(@PathParam("") DeleteObjectPathParam path,
                      @QueryParam("") DeleteObjectQueryParam query);

    @GET
    @Path("{pid}/objectXML")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    DigitalObjectTO getObjectXML(@PathParam("") GetObjectXMLPathParam path,
                                 @QueryParam("") GetObjectXMLQueryParam query);

    @POST
    @Path("{pid}")
    @Produces(MimeTypes.TEXT_XML)
    @Consumes(MimeTypes.TEXT_XML)
    String ingest(@PathParam("") IngestPathParam path,
                  @QueryParam("") IngestQueryParam query,
                  DigitalObjectTypeTOExtension digitalObjectTO);

}
