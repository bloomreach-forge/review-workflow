package org.bloomreach.forge.reviewworkflow.repository.scxml;

import org.apache.commons.scxml2.SCXMLExpressionException;
import org.apache.commons.scxml2.model.ModelException;
import org.onehippo.repository.documentworkflow.action.AbstractDocumentTaskAction;

public class IsEligibleForReviewAction extends AbstractDocumentTaskAction<IsEeligibleForReviewTask> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    public void setRequestExpr(String requestExpr) {
        setParameter("requestExpr", requestExpr);
    }

    public String getRequestExpr() {
        return getParameter("requestExpr");
    }

    @SuppressWarnings("unused")
    public void setId(String id) {
        setParameter("id", id);
    }

    public String getId() {
        return getParameter("id");
    }

    @Override
    protected IsEeligibleForReviewTask createWorkflowTask() {
        return new IsEeligibleForReviewTask();
    }

    @Override
    protected void initTask(IsEeligibleForReviewTask task) throws ModelException, SCXMLExpressionException {
        super.initTask(task);
        task.setRequest(eval(getRequestExpr()));
        task.setId(eval(getId()));
    }
}
