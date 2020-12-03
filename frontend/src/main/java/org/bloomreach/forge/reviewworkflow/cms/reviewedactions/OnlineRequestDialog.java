package org.bloomreach.forge.reviewworkflow.cms.reviewedactions;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hippoecm.addon.workflow.IWorkflowInvoker;
import org.hippoecm.frontend.service.IEditorManager;
import org.onehippo.forge.selection.frontend.model.ListItem;
import org.onehippo.forge.selection.frontend.model.ValueList;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class OnlineRequestDialog extends AbstractAssignDialog {

    public OnlineRequestDialog(final IWorkflowInvoker invoker, final IModel<Node> nodeModel,
                               final IModel<String> assignToModel, final IModel<String> titleModel,
                               final IEditorManager editorMgr, final String listLocation) {
        super(invoker, nodeModel, assignToModel, titleModel, editorMgr, listLocation);
    }

    @Override
    protected IModel<? extends List<? extends String>> getDropDownModel(final IModel<Node> nodeModel, final ValueList valueList) throws RepositoryException {
        ArrayList<String> list = new ArrayList<>();
        for (ListItem next : valueList) {
            if (!next.getKey().equals(nodeModel.getObject().getSession().getUserID())) {
                list.add(next.getKey());
            }
        }
        return Model.ofList(list);
    }
}
