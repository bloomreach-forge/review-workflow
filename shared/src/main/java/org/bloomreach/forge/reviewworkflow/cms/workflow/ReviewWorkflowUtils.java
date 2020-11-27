package org.bloomreach.forge.reviewworkflow.cms.workflow;

import org.bloomreach.forge.reviewworkflow.ReviewWorkflowNodeType;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.text.MessageFormat;

public class ReviewWorkflowUtils {

    private static final String XPATH_QUERY_REQUEST_BY_ID = "//element(*,{0})[@{1}=''{2}'']";

    public static Node getRequestNodeFromWorkflowId(final String workflowId, final Session session) throws RepositoryException {
        String xpath = MessageFormat.format(XPATH_QUERY_REQUEST_BY_ID, ReviewWorkflowNodeType.REVIEWWORKFLOW_REQUEST, ReviewWorkflowNodeType.REVIEWWORKFLOW_UUID, workflowId);
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        query.setLimit(1);
        QueryResult result = query.execute();
        NodeIterator nodeIterator = result.getNodes();
        if( nodeIterator.hasNext()) {
            Node requestNode = nodeIterator.nextNode();
            return requestNode;
        } else throw new ItemNotFoundException(MessageFormat.format("No results for xpath query {0} ",xpath));

    }
}
