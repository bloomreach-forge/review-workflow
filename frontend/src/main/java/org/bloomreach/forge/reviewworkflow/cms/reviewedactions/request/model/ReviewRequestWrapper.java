package org.bloomreach.forge.reviewworkflow.cms.reviewedactions.request.model;

import org.bloomreach.forge.reviewworkflow.ReviewWorkflowNodeType;
import org.hippoecm.frontend.plugins.reviewedactions.model.Request;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Collections;

public class ReviewRequestWrapper extends Request {

    private Node requestNode;

    public ReviewRequestWrapper(final Request request, final Node requestNode) throws RepositoryException {
        super(
                request.getId(),
                request.getSchedule(),
                "review-" + requestNode.getProperty(ReviewWorkflowNodeType.REVIEWWORKFLOW_STATE).getString(),
                Collections.singletonMap("infoRequest", request.getInfo())
        );
        this.requestNode = requestNode;
    }

    public String getOwner() throws RepositoryException {
        return requestNode.getProperty(ReviewWorkflowNodeType.REVIEWWORKFLOW_OWNER).getString();
    }

    public String getAssignTo() throws RepositoryException {
        return requestNode.getProperty(ReviewWorkflowNodeType.REVIEWWORKFLOW_ASSIGNTO).getString();
    }

    public String getReviewedBy() throws RepositoryException {
        return requestNode.getProperty(ReviewWorkflowNodeType.REVIEWWORKFLOW_REVIEWEDBY).getString();
    }

    public String getReason() throws RepositoryException {
        return requestNode.getProperty(ReviewWorkflowNodeType.REVIEWWORKFLOW_REASON).getString();
    }

    @Override
    public Boolean getCancel() {
        return false;
    }
}
