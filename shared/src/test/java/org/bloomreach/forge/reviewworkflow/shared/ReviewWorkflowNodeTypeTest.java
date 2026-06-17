package org.bloomreach.forge.reviewworkflow.shared;

import org.bloomreach.forge.reviewworkflow.ReviewWorkflowNodeType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReviewWorkflowNodeTypeTest {

    @Test
    void constants_areNonNullAndNonEmpty() {
        assertNotNull(ReviewWorkflowNodeType.REVIEWWORKFLOW_REQUEST);
        assertFalse(ReviewWorkflowNodeType.REVIEWWORKFLOW_REQUEST.isEmpty());
        assertNotNull(ReviewWorkflowNodeType.REVIEWWORKFLOW_OWNER);
        assertNotNull(ReviewWorkflowNodeType.REVIEWWORKFLOW_ASSIGNTO);
        assertNotNull(ReviewWorkflowNodeType.REVIEWWORKFLOW_STATE);
        assertNotNull(ReviewWorkflowNodeType.REVIEWWORKFLOW_CREATIONDATE);
        assertNotNull(ReviewWorkflowNodeType.REVIEWWORKFLOW_UUID);
    }

    @Test
    void constants_haveExpectedNamespacePrefix() {
        assertTrue(ReviewWorkflowNodeType.REVIEWWORKFLOW_REQUEST.startsWith("reviewworkflow:"));
        assertTrue(ReviewWorkflowNodeType.REVIEWWORKFLOW_OWNER.startsWith("reviewworkflow:"));
    }
}
