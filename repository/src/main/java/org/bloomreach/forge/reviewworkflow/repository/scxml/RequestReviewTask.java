package org.bloomreach.forge.reviewworkflow.repository.scxml;

import org.bloomreach.forge.reviewworkflow.repository.documentworkflow.ReviewRequest;
import org.onehippo.repository.documentworkflow.DocumentVariant;
import org.onehippo.repository.documentworkflow.task.AbstractDocumentTask;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class RequestReviewTask extends AbstractDocumentTask {

    private static final long serialVersionUID = 1L;

    private String assignTo;
    private DocumentVariant contextVariant;

    public void setAssignTo(final String assignTo) {
        this.assignTo = assignTo;
    }

    public void setContextVariant(DocumentVariant contextVariant) {
        this.contextVariant = contextVariant;
    }

    @Override
    public Object doExecute() throws RepositoryException {

        final Node node = contextVariant.getNode(getWorkflowContext().getInternalWorkflowSession());
        ReviewRequest reviewRequest = new ReviewRequest(node, contextVariant, getWorkflowContext().getUserIdentity(), assignTo);
        final Session internalWorkflowSession = getWorkflowContext().getInternalWorkflowSession();
        internalWorkflowSession.getWorkspace().getVersionManager().checkin(contextVariant.getNode(internalWorkflowSession).getPath());
        internalWorkflowSession.save();
        return reviewRequest;
    }
}
