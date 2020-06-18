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
import java.util.Date;

public class RequestReviewTask extends AbstractDocumentTask {

    private static final long serialVersionUID = 1L;

    private DocumentVariant contextVariant;
    private Date targetDate;
    private String assignTo;


    public DocumentVariant getContextVariant() {
        return contextVariant;
    }

    public void setContextVariant(DocumentVariant contextVariant) {
        this.contextVariant = contextVariant;
    }

    @SuppressWarnings("unused")
    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    @Override
    public Object doExecute() throws WorkflowException, RepositoryException, RemoteException {

        DocumentHandle dm = getDocumentHandle();

        if (!dm.isRequestPending()) {

            if (targetDate == null) {
                final Node node = contextVariant.getNode(getWorkflowContext().getInternalWorkflowSession());

                new ReviewRequest(node,
                        contextVariant, getWorkflowContext().getUserIdentity(), assignTo);
            }
            final Session internalWorkflowSession = getWorkflowContext().getInternalWorkflowSession();
            getWorkflowContext().getInternalWorkflowSession().getWorkspace().getVersionManager().checkin(contextVariant.getNode(internalWorkflowSession).getPath());
            getWorkflowContext().getInternalWorkflowSession().save();
        } else {
            throw new WorkflowException("publication request already pending");
        }
        return null;
    }

    public void setAssignTo(final String assignTo) {
        this.assignTo = assignTo;
    }

    public String getAssignTo() {
        return assignTo;
    }
}
