package org.bloomreach.forge.reviewworkflow.repository.scxml;

import org.bloomreach.forge.reviewworkflow.repository.documentworkflow.ReviewWorkflowDocumentHandle;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.repository.documentworkflow.DocumentHandle;
import org.onehippo.repository.documentworkflow.task.AbstractDocumentTask;
import org.onehippo.repository.security.SecurityService;
import org.onehippo.repository.security.User;

import javax.jcr.RepositoryException;
import java.util.Set;

/**
 * Scxml task which fetches the currently logged in user's groups and set it on the workflowData
 */
public class ListGroupsTask extends AbstractDocumentTask {

    @Override
    protected Object doExecute() throws RepositoryException {
        final DocumentHandle documentHandle = getDocumentHandle();
        if (documentHandle instanceof ReviewWorkflowDocumentHandle) {
            final SecurityService securityService = HippoServiceRegistry.getService(SecurityService.class);
            final String userId = getWorkflowContext().getUserIdentity();
            if (!"system".equals(userId)){
                final User repoUser = securityService.getUser(userId);
                final Set<String> groups = repoUser.getMemberships();
                ((ReviewWorkflowDocumentHandle) documentHandle).setGroups(groups);
            }
        }
        return null;
    }

}
