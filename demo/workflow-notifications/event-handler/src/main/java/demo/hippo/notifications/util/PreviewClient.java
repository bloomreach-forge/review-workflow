package demo.hippo.notifications.util;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/preview/")
public interface PreviewClient {

    @Path("/uuid/{uuid}")
    @GET
    public String getDocumentUrl(@PathParam(value = "uuid") String uuid);
}
