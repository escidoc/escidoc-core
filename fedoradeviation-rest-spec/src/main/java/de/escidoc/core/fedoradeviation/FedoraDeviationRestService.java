/**
 * 
 */
package de.escidoc.core.fedoradeviation;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.escidoc.core.utils.io.EscidocBinaryContent;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author Michael Hoppe
 * 
 */

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface FedoraDeviationRestService {

    @GET
    @Path("/objects/{id}/export")
    String export(@PathParam("id") String id) throws SystemException;


    @GET
    @Path("/objects/{id}/datastreams/{ds-id}/content")
    EscidocBinaryContent getDatastreamDissemination(@PathParam("id") String id, @PathParam("ds-id") String dsId)
        throws SystemException;

    @GET
    @Path("/describe")
    String getFedoraDescription(@QueryParam("xml") String xml) throws Exception;

}
