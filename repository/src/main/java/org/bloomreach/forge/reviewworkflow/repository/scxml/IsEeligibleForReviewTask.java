package org.bloomreach.forge.reviewworkflow.repository.scxml;

import org.apache.commons.lang.StringUtils;
import org.bloomreach.forge.reviewworkflow.repository.documentworkflow.ReviewWorkflowNodeType;
import org.hippoecm.repository.util.JcrUtils;
import org.onehippo.repository.documentworkflow.Request;
import org.onehippo.repository.documentworkflow.task.AbstractDocumentTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;

public class IsEeligibleForReviewTask extends AbstractDocumentTask {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(IsEeligibleForReviewTask.class);

    private Request request;
    private String id;

    public Request getRequest() {
        return request;
    }

    public String getId() {
        return id;
    }

    public void setRequest(final Request request) {
        this.request = request;
    }

    @Override
    public Object doExecute() {
        try {
            final Session session = getWorkflowContext().getInternalWorkflowSession();
            Node requestNode = request.getCheckedOutNode(session);
            JcrUtils.ensureIsCheckedOut(requestNode.getParent());
            if (requestNode.isNodeType(ReviewWorkflowNodeType.REVIEWWORKFLOW_REQUEST) && StringUtils.isNotEmpty(id)) {
                requestNode.setProperty(ReviewWorkflowNodeType.REVIEWWORKFLOW_CHECKED, true);
                session.save();
                return requestNode.getProperty(ReviewWorkflowNodeType.REVIEWWORKFLOW_UUID).getString().equals(getId());
            }
        } catch (Exception e) {
            log.error("error while trying to receive if document is eligable for review {}", e.getMessage(), e);
        }
        return false;
    }

    public void setId(String id) {
        this.id = id;
    }
}
