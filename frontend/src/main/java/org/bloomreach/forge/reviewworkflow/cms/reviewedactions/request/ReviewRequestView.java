package org.bloomreach.forge.reviewworkflow.cms.reviewedactions.request;

import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.bloomreach.forge.reviewworkflow.cms.reviewedactions.request.model.ReviewRequestModel;
import org.bloomreach.forge.reviewworkflow.cms.reviewedactions.request.model.ReviewRequestWrapper;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugins.reviewedactions.RequestsView;
import org.hippoecm.frontend.plugins.reviewedactions.model.Request;
import org.hippoecm.frontend.plugins.standards.datetime.DateTimePrinter;
import org.hippoecm.repository.api.Workflow;
import org.hippoecm.repository.api.WorkflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReviewRequestView extends RequestsView {

    private static final Logger log = LoggerFactory.getLogger(ReviewRequestView.class);

    private final IPluginContext context;

    public ReviewRequestView(final String id, final WorkflowDescriptorModel model, final IPluginContext context) {
        super(id, model, context);
        this.context = context;
    }

    @Override
    protected void onPopulate() {
        List<IModel<Request>> requests = new ArrayList<>();
        try {
            WorkflowDescriptorModel model = getModel();
            Workflow workflow = model.getWorkflow();
            if (workflow != null) {
                Map<String, Serializable> info = workflow.hints();
                if (info.containsKey("requests")) {
                    Map<String, Map<String, ?>> infoRequests = (Map<String, Map<String, ?>>) info.get("requests");
                    for (Map.Entry<String, Map<String, ?>> entry : infoRequests.entrySet()) {
                        requests.add(new ReviewRequestModel(entry.getKey(), entry.getValue()));
                    }
                }
            }
        } catch (RepositoryException | WorkflowException | RemoteException ex) {
            log.error(ex.getMessage());
        }
        removeAll();
        int index = 0;
        for (IModel<Request> requestModel : requests) {
            Item<Request> item = new Item<>(newChildId(), index++, requestModel);
            populateItem(item);
            add(item);
        }
    }

    protected void populateItem(final Item<Request> item) {
        super.populateItem(item);
        item.addOrReplace(new StdWorkflow("info", "info") {

            @Override
            public String getSubMenu() {
                return "info";
            }

            @Override
            protected IModel getTitle() {
                final Request request = item.getModelObject();
                Date schedule = request.getSchedule();
                String state = request.getState();

                if(request instanceof ReviewRequestWrapper) {
                    ReviewRequestWrapper reviewRequestWrapper = (ReviewRequestWrapper)request;
                    return new StringResourceModel("state-"+state, this, Model.of(reviewRequestWrapper));
                }

                final String parameter = schedule != null ?
                        DateTimePrinter.of(schedule).appendDST().print(FormatStyle.FULL) : "??";
                return new StringResourceModel("state-" + state, this, Model.of(parameter));
            }

            @Override
            protected void invoke() {
            }
        });


    }
}
