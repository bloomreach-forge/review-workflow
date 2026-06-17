/*
 * Copyright 2025 Bloomreach (https://www.bloomreach.com)
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
package org.bloomreach.forge.reviewworkflow.shared;

import org.bloomreach.forge.reviewworkflow.cms.workflow.ReviewWorkflowUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewWorkflowUtilsTest {

    @Mock private Session session;
    @Mock private Workspace workspace;
    @Mock private QueryManager queryManager;
    @Mock private Query query;
    @Mock private QueryResult queryResult;
    @Mock private NodeIterator nodeIterator;
    @Mock private Node node;

    private void stubQueryChain() throws RepositoryException {
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenReturn(queryManager);
        when(queryManager.createQuery(anyString(), eq(Query.XPATH))).thenReturn(query);
        when(query.execute()).thenReturn(queryResult);
        when(queryResult.getNodes()).thenReturn(nodeIterator);
    }

    @Test
    void getRequestNodeFromWorkflowId_whenNodeFound_returnsNode() throws RepositoryException {
        stubQueryChain();
        when(nodeIterator.hasNext()).thenReturn(true);
        when(nodeIterator.nextNode()).thenReturn(node);

        Node result = ReviewWorkflowUtils.getRequestNodeFromWorkflowId("workflow-123", session);

        assertSame(node, result);
        verify(query).setLimit(1);
    }

    @Test
    void getRequestNodeFromWorkflowId_whenNoResults_throwsItemNotFoundException() throws RepositoryException {
        stubQueryChain();
        when(nodeIterator.hasNext()).thenReturn(false);

        assertThrows(ItemNotFoundException.class,
                () -> ReviewWorkflowUtils.getRequestNodeFromWorkflowId("missing-id", session));
    }

    @Test
    void getRequestNodeFromWorkflowId_xpathContainsWorkflowId() throws RepositoryException {
        stubQueryChain();
        when(nodeIterator.hasNext()).thenReturn(false);

        try {
            ReviewWorkflowUtils.getRequestNodeFromWorkflowId("specific-uuid", session);
        } catch (ItemNotFoundException ignored) {}

        // Verify the query was built with the supplied workflowId embedded
        verify(queryManager).createQuery(
                argThat(xpath -> xpath.contains("specific-uuid")),
                eq(Query.XPATH));
    }

    @Test
    void getRequestNodeFromWorkflowId_xpathQueriesCorrectNodeType() throws RepositoryException {
        stubQueryChain();
        when(nodeIterator.hasNext()).thenReturn(false);

        try {
            ReviewWorkflowUtils.getRequestNodeFromWorkflowId("any-id", session);
        } catch (ItemNotFoundException ignored) {}

        verify(queryManager).createQuery(
                argThat(xpath -> xpath.contains("reviewworkflow:request")),
                eq(Query.XPATH));
    }

    @Test
    void getRequestNodeFromWorkflowId_xpathFiltersOnUuidProperty() throws RepositoryException {
        stubQueryChain();
        when(nodeIterator.hasNext()).thenReturn(false);

        try {
            ReviewWorkflowUtils.getRequestNodeFromWorkflowId("any-id", session);
        } catch (ItemNotFoundException ignored) {}

        verify(queryManager).createQuery(
                argThat(xpath -> xpath.contains("reviewworkflow:uuid")),
                eq(Query.XPATH));
    }

    @Test
    void getRequestNodeFromWorkflowId_whenRepositoryExceptionThrown_propagates() throws RepositoryException {
        // getWorkspace() does not declare RepositoryException; throw from getQueryManager() instead
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenThrow(new RepositoryException("connection lost"));

        assertThrows(RepositoryException.class,
                () -> ReviewWorkflowUtils.getRequestNodeFromWorkflowId("id", session));
    }
}
