package org.bloomreach.forge.reviewworkflow.repository.scxml;

import org.apache.commons.scxml2.SCXMLExpressionException;
import org.apache.commons.scxml2.model.ModelException;
import org.onehippo.repository.documentworkflow.action.AbstractDocumentTaskAction;

public class RequestReviewAction extends AbstractDocumentTaskAction<RequestReviewTask> {

    private static final long serialVersionUID = 1L;


    public String getContextVariantExpr() {
        return getParameter("contextVariantExpr");
    }

    @SuppressWarnings("unused")
    public void setContextVariantExpr(String contextVariantExpr) {
        setParameter("contextVariantExpr", contextVariantExpr);
    }

    public String getAssignTo() {
        return getParameter("assignTo");
    }

    @SuppressWarnings("unused")
    public void setAssignTo(String assignTo) {
        setParameter("assignTo", assignTo);
    }

    @Override
    protected RequestReviewTask createWorkflowTask() {
        return new RequestReviewTask();
    }

    @Override
    protected void initTask(RequestReviewTask task) throws ModelException, SCXMLExpressionException {
        super.initTask(task);
        task.setContextVariant(eval(getContextVariantExpr()));
        task.setAssignTo(eval(getAssignTo()));
    }
}

