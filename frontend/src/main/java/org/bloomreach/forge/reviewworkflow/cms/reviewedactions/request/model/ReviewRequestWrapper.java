package org.bloomreach.forge.reviewworkflow.cms.reviewedactions.request.model;

import org.hippoecm.frontend.plugins.reviewedactions.model.Request;

import java.util.Collections;

public class ReviewRequestWrapper extends Request {


    public ReviewRequestWrapper(final Request request, final String state) {
        super(request.getId(), request.getSchedule(), state, Collections.singletonMap("infoRequest", request.getInfo()));
    }

    @Override
    public Boolean getCancel() {
        return false;
    }
}
