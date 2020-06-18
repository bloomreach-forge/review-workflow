package org.bloomreach.forge.reviewworkflow.repository.scxml;

import org.bloomreach.forge.reviewworkflow.repository.documentworkflow.ReviewRequest;
import org.hippoecm.repository.api.WorkflowException;
import org.onehippo.repository.documentworkflow.DocumentHandle;
import org.onehippo.repository.documentworkflow.DocumentVariant;
import org.onehippo.repository.documentworkflow.task.AbstractDocumentTask;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.rmi.RemoteException;

public class OnlineRequestReviewTask extends AbstractDocumentTask {

    private static final long serialVersionUID = 1L;

    private DocumentVariant contextVariant;
    private String email;
    private String uuid;


    public DocumentVariant getContextVariant() {
        return contextVariant;
    }

    public void setContextVariant(DocumentVariant contextVariant) {
        this.contextVariant = contextVariant;
    }


    @Override
    public Object doExecute() throws WorkflowException, RepositoryException, RemoteException {

        DocumentHandle dm = getDocumentHandle();

        if (!dm.isRequestPending()) {

            final Node node = contextVariant.getNode(getWorkflowContext().getInternalWorkflowSession());
            new ReviewRequest(node,
                    contextVariant, getWorkflowContext().getUserIdentity(), getEmail(), getUuid());
            final Session internalWorkflowSession = getWorkflowContext().getInternalWorkflowSession();
            getWorkflowContext().getInternalWorkflowSession().getWorkspace().getVersionManager().checkin(contextVariant.getNode(internalWorkflowSession).getPath());
            getWorkflowContext().getInternalWorkflowSession().save();
        } else {
            throw new WorkflowException("publication request already pending");
        }
        return null;
    }


    public void setEmail(final String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
