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

import java.text.MessageFormat;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.bloomreach.forge.reviewworkflow.ReviewWorkflowNodeType;

public class ReviewWorkflowUtils {

    private static final String XPATH_QUERY_REQUEST_BY_ID = "//element(*,{0})[@{1}=''{2}'']";

    @SuppressWarnings("deprecation")
    public static Node getRequestNodeFromWorkflowId(final String workflowId, final Session session) throws RepositoryException {
        String xpath = MessageFormat.format(XPATH_QUERY_REQUEST_BY_ID, ReviewWorkflowNodeType.REVIEWWORKFLOW_REQUEST, ReviewWorkflowNodeType.REVIEWWORKFLOW_UUID, workflowId);
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        query.setLimit(1);
        QueryResult result = query.execute();
        NodeIterator nodeIterator = result.getNodes();
        if( nodeIterator.hasNext()) {
            return nodeIterator.nextNode();
        } else throw new ItemNotFoundException(MessageFormat.format("No results for xpath query {0} ",xpath));

    }
}
