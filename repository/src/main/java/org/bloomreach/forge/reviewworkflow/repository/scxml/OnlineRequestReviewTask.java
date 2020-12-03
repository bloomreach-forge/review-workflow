package org.bloomreach.forge.reviewworkflow.repository.scxml;

import org.bloomreach.forge.reviewworkflow.repository.documentworkflow.ReviewRequest;
import org.onehippo.repository.documentworkflow.DocumentVariant;
import org.onehippo.repository.documentworkflow.task.AbstractDocumentTask;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class OnlineRequestReviewTask extends AbstractDocumentTask {

    private static final long serialVersionUID = 1L;

    private DocumentVariant contextVariant;
    private String email;
    private String uuid;

    public void setContextVariant(DocumentVariant contextVariant) {
        this.contextVariant = contextVariant;
    }
    public void setEmail(final String email) {
        this.email = email;
    }
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    @Override
    public Object doExecute() throws RepositoryException {

        final Node node = contextVariant.getNode(getWorkflowContext().getInternalWorkflowSession());
        ReviewRequest reviewRequest = new ReviewRequest(node, contextVariant, getWorkflowContext().getUserIdentity(), email, uuid);
        final Session internalWorkflowSession = getWorkflowContext().getInternalWorkflowSession();
        getWorkflowContext().getInternalWorkflowSession().getWorkspace().getVersionManager().checkin(contextVariant.getNode(internalWorkflowSession).getPath());
        internalWorkflowSession.save();

        return reviewRequest;
    }

}
