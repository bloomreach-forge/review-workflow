package org.bloomreach.forge.reviewworkflow.repository.documentworkflow;

import org.bloomreach.forge.reviewworkflow.ReviewWorkflowNodeType;
import org.hippoecm.repository.HippoStdPubWfNodeType;
import org.hippoecm.repository.quartz.HippoSchedJcrConstants;
import org.hippoecm.repository.util.NodeIterable;
import org.onehippo.repository.documentworkflow.*;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReviewWorkflowDocumentHandle extends DocumentHandle {

    private final Map<String, Request> requests = new HashMap<>();
    private Boolean requestPending = false;
    private Set<String> groups = new HashSet<>(); //will hold current user's groups

    public ReviewWorkflowDocumentHandle(final Node handle) {
        super(handle);
    }

    public static Request createCustomRequest(Node requestNode) throws RepositoryException {
        if (requestNode.isNodeType(ReviewWorkflowNodeType.REVIEWWORKFLOW_REQUEST)) {
            return new ReviewRequest(requestNode);
        }
        Request request = createWorkflowRequest(requestNode);
        if (request == null) {
            request = createScheduledRequest(requestNode);
        }
        return request;
    }

    public static WorkflowRequest createWorkflowRequest(Node requestNode) throws RepositoryException {
        if (requestNode.isNodeType(HippoStdPubWfNodeType.NT_HIPPOSTDPUBWF_REQUEST)) {
            return new WorkflowRequest(requestNode);
        }
        return null;
    }

    public static ScheduledRequest createScheduledRequest(Node requestNode) throws RepositoryException {
        if (requestNode.isNodeType(HippoSchedJcrConstants.HIPPOSCHED_WORKFLOW_JOB)) {
            return new ScheduledRequest(requestNode);
        }
        return null;
    }

    protected DocumentVariant createDocumentVariant(Node node) throws RepositoryException {
        return new DocumentVariant(node);
    }

    protected Request createRequest(Node node) throws RepositoryException {
        return createCustomRequest(node);
    }

    @Override
    protected void initializeRequestStatus() throws RepositoryException {
        for (Node requestNode : new NodeIterable(getHandle().getNodes(HippoStdPubWfNodeType.HIPPO_REQUEST))) {
            Request request = createRequest(requestNode);
            if (request != null) {
                if (request.isWorkflowRequest()) {
                    requests.put(request.getIdentity(), request);
                    if (!HippoStdPubWfNodeType.REJECTED.equals(((WorkflowRequest) request).getType())) {
                        requestPending = true;
                    }
                } else if (request.isScheduledRequest()) {
                    requests.put(request.getIdentity(), request);
                    requestPending = true;
                } else if (request.getRequestType().equals(ReviewWorkflowNodeType.REVIEWWORKFLOW_REQUEST) && request instanceof ReviewRequest) {
                    ReviewRequest reviewRequest = (ReviewRequest) request;
                    if (reviewRequest.getIsStateRequest()) {
                        requests.put(request.getIdentity(), request);
                        requestPending = true;
                    }
                    if (reviewRequest.getIsStateRejected()) {
                        requests.put(request.getIdentity(), request);
                        requestPending = true;
                    }
                }
            }
        }
    }

    public Map<String, Request> getRequests() {
        return requests;
    }

    public boolean isRequestPending() {
        return requestPending;
    }

    public void setGroups(final Set<String> groups) {
        this.groups = groups;
    }

    @SuppressWarnings("unused")
    public Set<String> getGroups() {
        return groups;
    }

    @Override
    public void reset() {
        super.reset();
        requests.clear();
    }
}
