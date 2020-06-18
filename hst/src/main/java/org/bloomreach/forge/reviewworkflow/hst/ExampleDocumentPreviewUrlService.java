package org.bloomreach.forge.reviewworkflow.hst;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.jaxrs.services.AbstractResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Path("/preview/")
public class ExampleDocumentPreviewUrlService extends AbstractResource {

    private static Logger log = LoggerFactory.getLogger(ExampleDocumentPreviewUrlService.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/uuid/{uuid}")
    public String getDocumentResource(@Context HttpServletRequest servletRequest,
                                      @Context HttpServletResponse servletResponse,
                                      @Context UriInfo uriInfo,
                                      @PathParam("uuid") String uuid) {
        final HstRequestContext requestContext = getRequestContext(servletRequest);
        final HstLinkCreator hstLinkCreator = requestContext.getHstLinkCreator();
        final Mount mount = requestContext.getMount("reviewworkflow", "preview");

        Node nodeByIdentifier = null;
        try {
            nodeByIdentifier = requestContext.getSession().getNodeByIdentifier(uuid);
        } catch (RepositoryException e) {
            log.error("error while trying to retrieve node identifier {}", e.getMessage(), e);
        }

        final HstLink hstLink = hstLinkCreator.create(nodeByIdentifier, mount);
        return hstLink.toUrlForm(requestContext, true);
    }

}
