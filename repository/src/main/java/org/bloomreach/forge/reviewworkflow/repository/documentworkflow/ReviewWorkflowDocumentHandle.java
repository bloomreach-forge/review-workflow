package org.bloomreach.forge.reviewworkflow.repository.documentworkflow;

import org.bloomreach.forge.reviewworkflow.ReviewWorkflowNodeType;
import org.hippoecm.repository.HippoStdPubWfNodeType;
import org.hippoecm.repository.util.NodeIterable;
import org.onehippo.repository.documentworkflow.DocumentHandle;
import org.onehippo.repository.documentworkflow.Request;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.HashSet;
import java.util.Set;

public class ReviewWorkflowDocumentHandle extends DocumentHandle {

    private Set<String> groups = new HashSet<>(); //will hold current user's groups

    public ReviewWorkflowDocumentHandle(final Node handle) {
        super(handle);
    }

    @Override
    protected void initializeRequestStatus() throws RepositoryException {
        super.initializeRequestStatus();
        for (Node requestNode : new NodeIterable(getHandle().getNodes(HippoStdPubWfNodeType.HIPPO_REQUEST))) {
            Request request = createRequest(requestNode);
            if (request instanceof ReviewRequest && ReviewWorkflowNodeType.REVIEWWORKFLOW_REQUEST.equals(request.getRequestType())) {
                ReviewRequest reviewRequest = (ReviewRequest) request;
                if (reviewRequest.getIsStateRequest()) {
                    getRequests().put(request.getIdentity(), request);
                    setRequestPending(true);
                }
                if (reviewRequest.getIsStateRejected()) {
                    getRequests().put(request.getIdentity(), request);
                    setRequestPending(true);
                }
            }
        }
    }

    @Override
    protected Request createRequest(final Node node) throws RepositoryException {
        if (node.isNodeType(ReviewWorkflowNodeType.REVIEWWORKFLOW_REQUEST)) {
            return new ReviewRequest(node);
        }
        return super.createRequest(node);
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
    }
}
