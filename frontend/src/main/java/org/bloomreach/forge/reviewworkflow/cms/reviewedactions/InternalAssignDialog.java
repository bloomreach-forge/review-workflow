package org.bloomreach.forge.reviewworkflow.cms.reviewedactions;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hippoecm.addon.workflow.IWorkflowInvoker;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.hst.site.HstServices;
import org.hippoecm.repository.util.JcrUtils;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.forge.selection.frontend.model.ListItem;
import org.onehippo.forge.selection.frontend.model.ValueList;
import org.onehippo.repository.security.Group;
import org.onehippo.repository.security.SecurityService;

import javax.jcr.Node;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class InternalAssignDialog extends AbstractAssignDialog {

    private static final int GROUPS_LIMIT = 20;

    public InternalAssignDialog(final IWorkflowInvoker invoker, final IModel<Node> nodeModel,
                                final IModel<String> assignToModel, final IModel<String> titleModel,
                                final String listLocation) {
        super(invoker, nodeModel, assignToModel, titleModel, listLocation);
    }

    @Override
    protected IModel<? extends List<String>> getDropDownModel(final IModel<Node> nodeModel, final ValueList valueList) {
        Set<String> groupsSet = new TreeSet<>();
        for (ListItem item : valueList) {
            groupsSet.add(item.getKey());
        }
        final AssignableGroupsProvider assignableGroupsProvider = HstServices.getComponentManager().getComponent(AssignableGroupsProvider.class.getName());
        if (assignableGroupsProvider != null) {
            final String currentUserId = UserSession.get().getJcrSession().getUserID();
            final Set<String> assignableGroups = assignableGroupsProvider.provideGroups(currentUserId, JcrUtils.getNodePathQuietly(nodeModel.getObject()));
            if (assignableGroups != null) {
                groupsSet.addAll(assignableGroups);
            }
        } else {
            final SecurityService securityService = HippoServiceRegistry.getService(SecurityService.class);
            final Iterable<Group> allGroups = securityService.getGroups(0, GROUPS_LIMIT);
            allGroups.forEach(group -> groupsSet.add(group.getId()));
        }
        return Model.ofList(new LinkedList<>(groupsSet));
    }
}
