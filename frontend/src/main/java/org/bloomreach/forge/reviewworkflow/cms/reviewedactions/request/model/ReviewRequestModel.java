package org.bloomreach.forge.reviewworkflow.cms.reviewedactions.request.model;

import org.bloomreach.forge.reviewworkflow.ReviewWorkflowNodeType;
import org.hippoecm.frontend.plugins.reviewedactions.model.Request;
import org.hippoecm.frontend.plugins.reviewedactions.model.RequestModel;
import org.hippoecm.frontend.session.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Map;

public class ReviewRequestModel extends RequestModel {

    private static final Logger log = LoggerFactory.getLogger(ReviewRequestModel.class);

    public ReviewRequestModel(final String id, final Map<String, ?> info) {
        super(id, info);
    }

    @Override
    protected Request load() {
        Request request = super.load();
        try {
            Session session = UserSession.get().getJcrSession();
            Node node = session.getNodeByIdentifier(request.getId());
            if (node.isNodeType(ReviewWorkflowNodeType.REVIEWWORKFLOW_REQUEST)) {
                request = new ReviewRequestWrapper(request, node);
            }
        } catch (RepositoryException e) {
            log.error("Error loading the request in the model",e);
        }
        return request;
    }
}
