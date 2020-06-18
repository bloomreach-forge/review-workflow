package org.bloomreach.forge.reviewworkflow.repository.documentworkflow;

import org.onehippo.repository.documentworkflow.DocumentHandle;
import org.onehippo.repository.documentworkflow.DocumentHandleFactory;

import javax.jcr.Node;

public class ReviewWorkflowDocumentHandleFactory implements DocumentHandleFactory {
    @Override
    public DocumentHandle createDocumentHandle(final Node node) {
        return new ReviewWorkflowDocumentHandle(node);
    }
}
