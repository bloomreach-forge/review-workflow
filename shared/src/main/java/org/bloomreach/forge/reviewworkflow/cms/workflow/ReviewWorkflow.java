package org.bloomreach.forge.reviewworkflow.cms.workflow;

import org.hippoecm.repository.api.WorkflowException;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;

import java.util.Date;

public interface ReviewWorkflow extends DocumentWorkflow {

    /**
     * Request document review for the reviewer role before publication.
     *
     * @throws WorkflowException
     */
    void requestReview(String assignTo) throws WorkflowException;

    /**
     * Request document review for online.
     *
     * @throws WorkflowException
     */
    void requestReviewOnline(String email, String uuid) throws WorkflowException;

    /**
     * Request a scheduled publication review for the reviewer role.
     *
     * @param publicationDate
     * @throws WorkflowException
     */
    @Deprecated
    void requestPublicationReview(final Date publicationDate) throws WorkflowException;

    /**
     * Accept a review request.
     *
     * @throws WorkflowException
     */
    void acceptReview() throws WorkflowException;

    /**
     * Reject a review request plus give reason to author why.
     *
     * @param reason
     * @throws WorkflowException
     */
    void rejectReview(String reason) throws WorkflowException;

    /**
     * Cancel the reivew request the user just made
     *
     * @throws WorkflowException
     */
    void cancelReview() throws WorkflowException;

    /**
     * Reject a review request
     *
     * @throws WorkflowException
     */
    void dropReview() throws WorkflowException;

    boolean isEligibleForReview(String id) throws WorkflowException;

}
