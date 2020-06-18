package org.bloomreach.forge.reviewworkflow.repository.documentworkflow;

import org.bloomreach.forge.reviewworkflow.cms.workflow.ReviewWorkflow;
import org.hippoecm.repository.api.WorkflowException;
import org.onehippo.repository.documentworkflow.DocumentHandle;
import org.onehippo.repository.documentworkflow.DocumentWorkflowImpl;
import org.onehippo.repository.scxml.SCXMLWorkflowContext;
import org.onehippo.repository.scxml.SCXMLWorkflowExecutor;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;

import static java.util.Collections.singletonMap;

public class DocumentReviewWorkflowImpl extends DocumentWorkflowImpl implements ReviewWorkflow {

    public DocumentReviewWorkflowImpl() throws RemoteException {
    }

    @Override
    public void requestReview(String assignTo) throws WorkflowException {
        getWorkflowExecutor().start();
        getWorkflowExecutor().triggerAction("requestReview", singletonMap("assignTo", assignTo));
    }

    @Override
    public void requestReviewOnline(String email, String uuid) throws WorkflowException {
        getWorkflowExecutor().start();
        final HashMap<String, String> payload = new HashMap<String, String>() {{
            put("email", email);
            put("uuid", uuid);
        }};
        getWorkflowExecutor().triggerAction("onlineRequestReview", payload);
    }

    @Deprecated
    @Override
    public void requestPublicationReview(final Date publicationDate) throws WorkflowException {
        getWorkflowExecutor().start();
        getWorkflowExecutor().triggerAction("requestReview", singletonMap("targetDate", publicationDate));
    }

    @Override
    public void acceptReview() throws WorkflowException {
        SCXMLWorkflowExecutor<SCXMLWorkflowContext, DocumentHandle> executor = getWorkflowExecutor();
        executor.start();
        executor.triggerAction("acceptReview");
    }

    @Override
    public void rejectReview(String reason) throws WorkflowException {
        SCXMLWorkflowExecutor<SCXMLWorkflowContext, DocumentHandle> executor = getWorkflowExecutor();
        executor.start();
        executor.triggerAction("rejectReview", singletonMap("reason", reason));
    }

    @Override
    public void cancelReview() throws WorkflowException {
        SCXMLWorkflowExecutor<SCXMLWorkflowContext, DocumentHandle> executor = getWorkflowExecutor();
        executor.start();
        executor.triggerAction("cancelReview");
    }

    @Override
    public void dropReview() throws WorkflowException {
        SCXMLWorkflowExecutor<SCXMLWorkflowContext, DocumentHandle> executor = getWorkflowExecutor();
        executor.start();
        executor.triggerAction("dropReview");
    }

    @Override
    public boolean isEligibleForReview(final String id) throws WorkflowException {
        SCXMLWorkflowExecutor<SCXMLWorkflowContext, DocumentHandle> executor = getWorkflowExecutor();
        executor.start();
        final Object o = executor.triggerAction("isEligibleForReview", singletonMap("id", id));
        if (o != null) {
            return (Boolean) o;
        }
        return false;
    }

}

