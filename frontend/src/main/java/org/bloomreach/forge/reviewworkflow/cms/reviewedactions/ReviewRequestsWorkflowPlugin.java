package org.bloomreach.forge.reviewworkflow.cms.reviewedactions;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.bloomreach.forge.reviewworkflow.cms.reviewedactions.request.model.ReviewRequestModel;
import org.bloomreach.forge.reviewworkflow.cms.workflow.ReviewWorkflow;
import org.bloomreach.forge.reviewworkflow.repository.documentworkflow.ReviewWorkflowNodeType;
import org.hippoecm.addon.workflow.ConfirmDialog;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.TextDialog;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.reviewedactions.AbstractDocumentWorkflowPlugin;
import org.hippoecm.frontend.plugins.reviewedactions.model.Request;
import org.hippoecm.frontend.plugins.standards.icon.HippoIcon;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.frontend.skin.Icon;
import org.hippoecm.frontend.util.DocumentUtils;
import org.hippoecm.repository.api.Workflow;
import org.hippoecm.repository.util.WorkflowUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Workflow menu plugin for the Review Workflow
 */
public class ReviewRequestsWorkflowPlugin extends AbstractDocumentWorkflowPlugin {

    private static final Logger log = LoggerFactory.getLogger(ReviewRequestsWorkflowPlugin.class);

    private boolean isRequestRejected(final ReviewRequestModel reviewRequestModel) {
        final Request request = reviewRequestModel.getObject();
        final String state = request.getState();
        return state.equals("review-rejected");
    }

    private final String internalAssignListLocation;
    private final String onlineRequestListLocation;

    public ReviewRequestsWorkflowPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);

        final boolean requestReviewEnabled = config.getAsBoolean("requestReview.enabled", true);
        final boolean onlineRequestReviewEnabled = config.getAsBoolean("onlineRequestReview.enabled", false);
        final boolean acceptReviewEnabled = config.getAsBoolean("acceptReview.enabled", true);
        final boolean cancelReviewEnabled = config.getAsBoolean("cancelReview.enabled", true);
        final boolean rejectReviewEnabled = config.getAsBoolean("rejectReview.enabled", true);
        final boolean dropReviewEnabled = config.getAsBoolean("dropReview.enabled", true);

        this.internalAssignListLocation = config.getString("internal.assign.list.path");
        this.onlineRequestListLocation = config.getString("online.request.list.path");

        final Map<String, Serializable> info = getHints();

        final List<IModel<Request>> requests = new ArrayList<>();
        WorkflowDescriptorModel model = getModel();
        Workflow workflow = model.getWorkflow();
        Request rejectedRequestTemp = null;
        if (workflow != null && info != null) {
            if (info.containsKey("requests")) {
                Map<String, Map<String, ?>> infoRequests = (Map<String, Map<String, ?>>) info.get("requests");
                for (Map.Entry<String, Map<String, ?>> entry : infoRequests.entrySet()) {
                    final ReviewRequestModel reviewRequestModel = new ReviewRequestModel(entry.getKey(), entry.getValue());
                    if (isRequestRejected(reviewRequestModel)) {
                        rejectedRequestTemp = reviewRequestModel.getObject();
                    }
                    requests.add(reviewRequestModel);
                }
            }
        }
        final Request rejectedRequest = rejectedRequestTemp;


        final StdWorkflow requestReview;
        add(requestReview = new StdWorkflow("requestReview", new StringResourceModel("request-review", this, null), context, getModel()) {

            public String user = ""; //this variable should not be final as it is set later by the dialog

            @Override
            public String getSubMenu() {
                return new StringResourceModel("review", ReviewRequestsWorkflowPlugin.this).getString();
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.CHECK_CIRCLE);
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                try {
                    Node handle = ((WorkflowDescriptorModel) getDefaultModel()).getNode();
                    Node unpublished = getVariant(handle, WorkflowUtils.Variant.UNPUBLISHED);
                    final IModel<String> titleModel = new StringResourceModel("assign-to-title", ReviewRequestsWorkflowPlugin.this, getDocumentName());
                    return new InternalAssignDialog(this, new JcrNodeModel(unpublished),
                            PropertyModel.of(this, "user"), titleModel, getEditorManager(), internalAssignListLocation);
                } catch (RepositoryException e) {
                    log.error(e.getMessage());
                }
                return null;
            }

            @Override
            protected String execute(Workflow wf) throws Exception {
                ReviewWorkflow workflow = (ReviewWorkflow) wf;
                workflow.requestReview(user);
                return null;
            }
        });

        final StdWorkflow requestReviewOnline;
        add(requestReviewOnline = new StdWorkflow("onlineRequestReview", new StringResourceModel("request-review-online", this, null), context, getModel()) {

            public String user = ""; //this variable should not be final as it is set later by the dialog

            @Override
            public String getSubMenu() {
                return new StringResourceModel("review", ReviewRequestsWorkflowPlugin.this, null).getString();
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                try {
                    Node handle = ((WorkflowDescriptorModel) getDefaultModel()).getNode();
                    Node unpublished = getVariant(handle, WorkflowUtils.Variant.UNPUBLISHED);
                    final IModel<String> titleModel = new StringResourceModel("email-to", ReviewRequestsWorkflowPlugin.this, getDocumentName());
                    return new OnlineRequestDialog(this, new JcrNodeModel(unpublished),
                            PropertyModel.of(this, "user"), titleModel, getEditorManager(), onlineRequestListLocation);
                } catch (RepositoryException e) {
                    log.error(e.getMessage());
                }
                return null;
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.CHECK_CIRCLE);
            }

            @Override
            protected String execute(Workflow wf) throws Exception {
                ReviewWorkflow workflow = (ReviewWorkflow) wf;
                workflow.requestReviewOnline(user, UUID.randomUUID().toString());
                return null;
            }
        });

        final StdWorkflow acceptReview;
        add(acceptReview = new StdWorkflow("acceptReview", new StringResourceModel("accept-review", this, null), context, getModel()) {

            @Override
            public String getSubMenu() {
                return new StringResourceModel("review", ReviewRequestsWorkflowPlugin.this, null).getString();
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.CHECK_CIRCLE);
            }

            @Override
            protected String execute(Workflow wf) throws Exception {
                ReviewWorkflow workflow = (ReviewWorkflow) wf;
                workflow.acceptReview();
                return null;
            }
        });

        final StdWorkflow cancelReview;
        add(cancelReview = new StdWorkflow("cancelReview", new StringResourceModel("cancel-review", this, null), context, getModel()) {

            @Override
            public String getSubMenu() {
                return new StringResourceModel("review", ReviewRequestsWorkflowPlugin.this, null).getString();
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.TIMES);
            }

            @Override
            protected String execute(Workflow wf) throws Exception {
                ReviewWorkflow workflow = (ReviewWorkflow) wf;
                workflow.cancelReview();
                return null;
            }
        });

        final StdWorkflow rejectReview;
        add(rejectReview = new StdWorkflow("rejectReview", new StringResourceModel("reject-review", this, null), context, getModel()) {

            public String reason;

            @Override
            public String getSubMenu() {
                return new StringResourceModel("review", ReviewRequestsWorkflowPlugin.this, null).getString();
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.MINUS_CIRCLE);
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                final StringResourceModel title = new StringResourceModel("rejected-review-title", ReviewRequestsWorkflowPlugin.this, null);
                final StringResourceModel text = new StringResourceModel("rejected-review-text", ReviewRequestsWorkflowPlugin.this, null);
                final StdWorkflow stdWorkflow = this;
                return new TextDialog(
                        title,
                        text,
                        new PropertyModel<>(this, "reason")) {
                    @Override
                    public void invokeWorkflow() throws Exception {
                        stdWorkflow.invokeWorkflow();
                    }
                };
            }

            @Override
            protected String execute(Workflow wf) throws Exception {
                ReviewWorkflow workflow = (ReviewWorkflow) wf;
                workflow.rejectReview(reason);
                return null;
            }
        });

        final StdWorkflow dropReview;
        add(dropReview = new StdWorkflow("dropReview", new StringResourceModel("show-reject", this, null), context, getModel()) {

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                IModel<String> reason = null;
                try {
                    String id = rejectedRequest.getId();
                    Node node = UserSession.get().getJcrSession().getNodeByIdentifier(id);
                    if (node != null && node.hasProperty(ReviewWorkflowNodeType.REVIEWWORKFLOW_REASON)) {
                        reason = Model.of(node.getProperty(ReviewWorkflowNodeType.REVIEWWORKFLOW_REASON).getString());
                    }
                } catch (RepositoryException ex) {
                    log.warn(ex.getMessage(), ex);
                }
                if (reason == null) {
                    reason = new StringResourceModel("rejected-request-unavailable", ReviewRequestsWorkflowPlugin.this, null);
                }
                final StdWorkflow cancelAction = this;
                return new ConfirmDialog(
                        new StringResourceModel("rejected-request-title", ReviewRequestsWorkflowPlugin.this, null),
                        new StringResourceModel("rejected-request-text", ReviewRequestsWorkflowPlugin.this, null),
                        reason,
                        new StringResourceModel("rejected-request-question", ReviewRequestsWorkflowPlugin.this, null)) {

                    @Override
                    public void invokeWorkflow() throws Exception {
                        cancelAction.invokeWorkflow();
                    }
                };
            }

            @Override
            protected String execute(Workflow wf) throws Exception {
                ReviewWorkflow workflow = (ReviewWorkflow) wf;
                workflow.dropReview();
                return null;
            }

            @Override
            public String getSubMenu() {
                return new StringResourceModel("review", ReviewRequestsWorkflowPlugin.this, null).getString();
            }

            @Override
            protected Component getIcon(final String id) {
                return HippoIcon.fromSprite(id, Icon.TIMES);
            }
        });

        if (requestReviewEnabled && info != null && isActionAllowed(info, "requestReview")) {
            requestReview.setVisible(true);
        } else {
            requestReview.setVisible(false);
        }
        if (onlineRequestReviewEnabled && info != null && isActionAllowed(info, "onlineRequestReview")) {
            requestReviewOnline.setVisible(true);
        } else {
            requestReviewOnline.setVisible(false);
        }
        if (acceptReviewEnabled && info != null && isActionAllowed(info, "acceptReview")) {
            acceptReview.setVisible(true);
        } else {
            acceptReview.setVisible(false);
        }
        if (cancelReviewEnabled && info != null && isActionAllowed(info, "cancelReview")) {
            cancelReview.setVisible(true);
        } else {
            cancelReview.setVisible(false);
        }
        if (rejectReviewEnabled && info != null && isActionAllowed(info, "rejectReview")) {
            rejectReview.setVisible(true);
        } else {
            rejectReview.setVisible(false);
        }
        if (dropReviewEnabled && info != null && isActionAllowed(info, "dropReview")) {
            dropReview.setVisible(true);
        } else {
            dropReview.setVisible(false);
        }
    }

    protected IModel<String> getDocumentName() {
        try {
            final IModel<String> result = DocumentUtils.getDocumentNameModel(((WorkflowDescriptorModel) getDefaultModel()).getNode());
            if (result != null) {
                return result;
            }
        } catch (RepositoryException ignored) {
        }
        return new StringResourceModel("unknown", this, null);
    }
}
