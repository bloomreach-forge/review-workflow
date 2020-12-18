package org.bloomreach.forge.reviewworkflow.cms.reviewedactions;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hippoecm.addon.workflow.IWorkflowInvoker;
import org.onehippo.forge.selection.frontend.model.ListItem;
import org.onehippo.forge.selection.frontend.model.ValueList;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

public class OnlineRequestDialog extends AbstractAssignDialog {

    public OnlineRequestDialog(final IWorkflowInvoker invoker, final IModel<Node> nodeModel,
                               final IModel<String> assignToModel, final IModel<String> titleModel,
                               final String listLocation) {
        super(invoker, nodeModel, assignToModel, titleModel, listLocation);
    }

    @Override
    protected IModel<? extends List<String>> getDropDownModel(final IModel<Node> nodeModel, final ValueList valueList) throws RepositoryException {
        List<String> list = new ArrayList<>();
        for (ListItem next : valueList) {
            if (!next.getKey().equals(nodeModel.getObject().getSession().getUserID())) {
                list.add(next.getKey());
            }
        }
        return Model.ofList(list);
    }
}
