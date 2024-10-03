/*
 * Copyright 2024 Bloomreach (https://www.bloomreach.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bloomreach.forge.reviewworkflow.cms.workflow;

import java.util.Date;

import org.hippoecm.repository.api.WorkflowException;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;

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
    void acceptReview(String id) throws WorkflowException;

    /**
     * Reject a review request plus give reason to author why.
     *
     * @param reason
     * @throws WorkflowException
     */
    void rejectReview(String id, String reason) throws WorkflowException;

    /**
     * Cancel the reivew request the user just made
     *
     * @throws WorkflowException
     */
    void cancelReview(String id) throws WorkflowException;

    /**
     * Reject a review request
     *
     * @throws WorkflowException
     */
    void dropReview(String id) throws WorkflowException;

    boolean isEligibleForReview(String id) throws WorkflowException;

}
