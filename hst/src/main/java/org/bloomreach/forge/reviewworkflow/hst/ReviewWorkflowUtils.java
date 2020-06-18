package org.bloomreach.forge.reviewworkflow.hst;

import org.bloomreach.forge.reviewworkflow.cms.workflow.ReviewWorkflow;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.content.beans.manager.workflow.WorkflowPersistenceManagerImpl;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocument;
import org.hippoecm.hst.site.HstServices;
import org.hippoecm.repository.api.Workflow;
import org.hippoecm.repository.api.WorkflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;

public class ReviewWorkflowUtils {

    private static final Logger log = LoggerFactory.getLogger(ReviewWorkflowUtils.class);

    private Credentials reviewWorkflowCredentials;

    public enum Type {
        ACCEPT, REJECT
    }

    public Session getReviewWorkflowSession() throws RepositoryException {
        final Repository repository = HstServices.getComponentManager().getComponent(Repository.class.getName());
        return repository.login(reviewWorkflowCredentials);
    }

    public boolean isEligibleForReview(final HippoBean documentBean, final String id) throws RepositoryException {
        final Session persistableSession = getReviewWorkflowSession();
        try {
            boolean hintsContainAcceptOrReject = false;
            final WorkflowPersistenceManagerImpl wpm = new WorkflowPersistenceManagerImpl(persistableSession, RequestContextProvider.get().getContentBeansTool().getObjectConverter(), null);
            final Object object = wpm.getObject(documentBean.getPath());
            final Workflow workflow = wpm.getWorkflow("default", ((HippoDocument) object).getNode().getParent());
            final Map<String, Serializable> hints = workflow.hints();

            if (hints.containsKey("acceptReview") && hints.containsKey("rejectReview")) {
                boolean acceptReview = (boolean) hints.get("acceptReview");
                boolean rejectReview = (boolean) hints.get("rejectReview");
                if (acceptReview && rejectReview) {
                    hintsContainAcceptOrReject = true;
                }
            }
            final ReviewWorkflow reviewWorkflow = (ReviewWorkflow) workflow;
            final boolean eligibleForReview = reviewWorkflow.isEligibleForReview(id);

            if (eligibleForReview && hintsContainAcceptOrReject) {
                return true;
            }
        } catch (WorkflowException | ObjectBeanManagerException | RemoteException e) {
            log.error(e.getMessage());
        } finally {
            persistableSession.logout();
        }
        return false;
    }

    public void execute(final HippoBean bean, Type type, String reason) throws RepositoryException {
        final Session persistableSession = getReviewWorkflowSession();

        try {
            final WorkflowPersistenceManagerImpl wpm = new WorkflowPersistenceManagerImpl(persistableSession, RequestContextProvider.get().getContentBeansTool().getObjectConverter(), null);
            final Object object = wpm.getObject(bean.getPath());
            final ReviewWorkflow reviewWorkflow = (ReviewWorkflow) wpm.getWorkflow("default", ((HippoDocument) object).getNode().getParent());
            if (type.equals(Type.ACCEPT)) {
                reviewWorkflow.acceptReview();
            } else if (type.equals(Type.REJECT)) {
                reviewWorkflow.rejectReview(reason);
            }
        } catch (WorkflowException | ObjectBeanManagerException e) {
            log.error(e.getMessage());
        } finally {
            persistableSession.logout();
        }
    }

    public void setReviewWorkflowCredentials(final Credentials reviewWorkflowCredentials) {
        this.reviewWorkflowCredentials = reviewWorkflowCredentials;
    }
}
