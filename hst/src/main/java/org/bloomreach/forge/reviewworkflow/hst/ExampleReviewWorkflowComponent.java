package org.bloomreach.forge.reviewworkflow.hst;

import org.apache.commons.lang.StringEscapeUtils;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.site.HstServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;

public class ExampleReviewWorkflowComponent extends BaseHstComponent {

    private static final Logger log = LoggerFactory.getLogger(ExampleReviewWorkflowComponent.class);

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        request.setAttribute("isPreview", request.getRequestContext().isPreview());
        final String workflowId = getPublicRequestParameter(request, "workflowId");
        final HippoBean contentBean = request.getRequestContext().getContentBean();
        ReviewWorkflowUtils reviewWorkflowUtils = HstServices.getComponentManager().getComponent(ReviewWorkflowUtils.class.getName(), "org.bloomreach.forge.reviewworkflow");
        boolean eligibleForReview = false;
        try {
            eligibleForReview = reviewWorkflowUtils.isEligibleForReview(contentBean, workflowId);
        } catch (RepositoryException ex) {
            log.error(ex.getMessage());
        }
        request.setAttribute("eligibleForReview", eligibleForReview);
    }

    @Override
    public void doAction(final HstRequest request, final HstResponse response) throws HstComponentException {
        super.doAction(request, response);

        try {
            final String workflow = getEscapedParameter(request, "workflow");
            final String reason = getEscapedParameter(request, "reason");
            final HippoBean contentBean = request.getRequestContext().getContentBean();
            final ReviewWorkflowUtils reviewWorkflowUtils = HstServices.getComponentManager().getComponent(ReviewWorkflowUtils.class.getName(), "org.bloomreach.forge.reviewworkflow");
            reviewWorkflowUtils.execute(contentBean, "accept".equals(workflow) ? ReviewWorkflowUtils.Type.ACCEPT : ReviewWorkflowUtils.Type.REJECT, reason);
        } catch (RepositoryException ex) {
            log.error(ex.getMessage());
        }
    }

    public static String getEscapedParameter(final HstRequest request, final String parameterName) {
        final String value = request.getParameter(parameterName);
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        return StringEscapeUtils.escapeHtml(value);
    }

}
