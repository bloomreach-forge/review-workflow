package org.bloomreach.forge.reviewworkflow.repository.documentworkflow;

import org.bloomreach.forge.reviewworkflow.cms.workflow.ReviewWorkflow;
import org.hippoecm.repository.api.DocumentWorkflowAction;
import org.hippoecm.repository.api.WorkflowException;
import org.onehippo.repository.documentworkflow.DocumentWorkflowImpl;

import java.rmi.RemoteException;
import java.util.Date;

public class DocumentReviewWorkflowImpl extends DocumentWorkflowImpl implements ReviewWorkflow {

    public DocumentReviewWorkflowImpl() throws RemoteException {
    }

    @Override
    public void requestReview(String assignTo) throws WorkflowException {
        final DocumentWorkflowAction dfa = new DocumentWorkflowAction("requestReview");
        dfa.addEventPayload("assignTo", assignTo);
        triggerAction(dfa);
    }

    @Override
    public void requestReviewOnline(String email, String uuid) throws WorkflowException {
        final DocumentWorkflowAction dfa = new DocumentWorkflowAction("onlineRequestReview");
        dfa.addEventPayload("email", email);
        dfa.addEventPayload("uuid", uuid);
        triggerAction(dfa);
    }

    @Deprecated
    @Override
    public void requestPublicationReview(final Date publicationDate) throws WorkflowException {
        final DocumentWorkflowAction dfa = new DocumentWorkflowAction("requestReview");
        dfa.addEventPayload("targetDate", publicationDate);
        triggerAction(dfa);
    }

    @Override
    public void acceptReview() throws WorkflowException {
        final DocumentWorkflowAction dfa = new DocumentWorkflowAction("acceptReview");
        triggerAction(dfa);
    }

    @Override
    public void rejectReview(String reason) throws WorkflowException {
        final DocumentWorkflowAction dfa = new DocumentWorkflowAction("rejectReview");
        dfa.addEventPayload("reason", reason);
        triggerAction(dfa);
    }

    @Override
    public void cancelReview() throws WorkflowException {
        final DocumentWorkflowAction dfa = new DocumentWorkflowAction("cancelReview");
        triggerAction(dfa);
    }

    @Override
    public void dropReview() throws WorkflowException {
        final DocumentWorkflowAction dfa = new DocumentWorkflowAction("dropReview");
        triggerAction(dfa);
    }

    @Override
    public boolean isEligibleForReview(final String id) throws WorkflowException {
        final DocumentWorkflowAction dfa = new DocumentWorkflowAction("isEligibleForReview");
        dfa.addEventPayload("id", id);
        final Object o = triggerAction(dfa);
        if (o != null) {
            return (Boolean) o;
        }
        return false;
    }

}

