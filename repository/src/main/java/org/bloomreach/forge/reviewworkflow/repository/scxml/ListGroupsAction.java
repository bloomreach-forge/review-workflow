package org.bloomreach.forge.reviewworkflow.repository.scxml;

import org.apache.commons.scxml2.SCXMLExpressionException;
import org.apache.commons.scxml2.model.ModelException;
import org.onehippo.repository.documentworkflow.action.AbstractDocumentTaskAction;

public class ListGroupsAction extends AbstractDocumentTaskAction<ListGroupsTask> {

    private static final long serialVersionUID = 1L;

    @Override
    protected ListGroupsTask createWorkflowTask() {
        return new ListGroupsTask();
    }

    @Override
    protected void initTask(ListGroupsTask task) throws ModelException, SCXMLExpressionException {
        super.initTask(task);
    }
}
