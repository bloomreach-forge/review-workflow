package org.bloomreach.forge.reviewworkflow.repository.scxml;

import org.apache.commons.lang.StringUtils;
import org.bloomreach.forge.reviewworkflow.ReviewWorkflowNodeType;
import org.hippoecm.repository.util.JcrUtils;
import org.onehippo.repository.documentworkflow.Request;
import org.onehippo.repository.documentworkflow.task.AbstractDocumentTask;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class AcceptReviewTask extends AbstractDocumentTask {

    private static final long serialVersionUID = 1L;

    private Request request;
    private String reviewer;

    public Request getRequest() {
        return request;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setRequest(final Request request) {
        this.request = request;
    }

    @Override
    public Object doExecute() throws RepositoryException {
        final Session session = getWorkflowContext().getInternalWorkflowSession();
        Node requestNode = request.getCheckedOutNode(session);
        JcrUtils.ensureIsCheckedOut(requestNode.getParent());
        if (requestNode.isNodeType(ReviewWorkflowNodeType.REVIEWWORKFLOW_REQUEST)) {
            requestNode.setProperty(ReviewWorkflowNodeType.REVIEWWORKFLOW_STATE, "accepted");
            requestNode.setProperty(ReviewWorkflowNodeType.REVIEWWORKFLOW_ASSIGNTO, requestNode.getProperty(ReviewWorkflowNodeType.REVIEWWORKFLOW_OWNER).getString());
        }
        if (StringUtils.isNotEmpty(reviewer)) {
            requestNode.setProperty(ReviewWorkflowNodeType.REVIEWWORKFLOW_REVIEWEDBY, reviewer);
        }
        session.save();
        return null;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }
}
