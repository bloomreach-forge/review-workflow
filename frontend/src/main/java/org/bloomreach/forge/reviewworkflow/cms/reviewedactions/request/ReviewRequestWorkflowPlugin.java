package org.bloomreach.forge.reviewworkflow.cms.reviewedactions.request;

import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.reviewedactions.RequestsWorkflowPlugin;

public class ReviewRequestWorkflowPlugin extends RequestsWorkflowPlugin {

    public ReviewRequestWorkflowPlugin(final IPluginContext context, final IPluginConfig config) {
        super(context, config);
        addOrReplace(new ReviewRequestView("requests", getModel(), context));
    }
}
