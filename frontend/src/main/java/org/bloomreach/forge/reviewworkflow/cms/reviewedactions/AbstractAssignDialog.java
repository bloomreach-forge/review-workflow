package org.bloomreach.forge.reviewworkflow.cms.reviewedactions;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.hippoecm.addon.workflow.IWorkflowInvoker;
import org.hippoecm.addon.workflow.WorkflowDialog;
import org.hippoecm.frontend.dialog.DialogConstants;
import org.hippoecm.frontend.service.IEditorManager;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.repository.api.HippoNodeType;
import org.hippoecm.repository.translation.HippoTranslationNodeType;
import org.onehippo.forge.selection.frontend.Namespace;
import org.onehippo.forge.selection.frontend.model.ListItem;
import org.onehippo.forge.selection.frontend.model.ValueList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.query.Query;
import java.util.List;
import java.util.Locale;

public abstract class AbstractAssignDialog extends WorkflowDialog<Node> {

    private static final Logger LOG = LoggerFactory.getLogger(InternalAssignDialog.class);

    public AbstractAssignDialog(final IWorkflowInvoker invoker, final IModel<Node> nodeModel,
                                final IModel<String> assignToModel, final IModel<String> titleModel,
                                final IEditorManager editorMgr, final String valueListLocation) {
        super(invoker, nodeModel, titleModel);
        final ValueList valueList = getValueList(valueListLocation);

        if (valueList != null) {
            DropDownChoice dropDownChoice = null;
            try {
                dropDownChoice = new DropDownChoice<>(
                        "dropdown", assignToModel, getDropDownModel(nodeModel, valueList), new ChoiceRenderer<String>() {

                    @Override
                    public Object getDisplayValue(final String object) {
                        if (valueList.contains(object)) {
                            return valueList.getLabel(object);
                        }
                        return object;
                    }

                    @Override
                    public String getIdValue(final String object, final int index) {
                        if (valueList.contains(object)) {
                            return valueList.getKey(object);
                        }
                        return object;
                    }
                });
                add(dropDownChoice);
            } catch (Exception e) {
                LOG.warn("unable to populate dropdown", e);
            }
        } else {
            LOG.warn("not a valid valuelist location: {}", valueListLocation);
        }


        setCssClass("hippo-workflow-dialog");
        setFocusOnCancel();
        setSize(DialogConstants.SMALL_AUTO);
    }

    protected abstract IModel<? extends List<? extends String>> getDropDownModel(final IModel<Node> nodeModel, final ValueList valueList) throws RepositoryException;


    public ValueList getValueList(String name) {
        return getValueList(name, null/*locale*/);
    }

    /**
     * {@inheritDoc}
     */
    public ValueList getValueList(String name, Locale locale) {

        final ValueList valuelist = new ValueList();

        if ((name == null) || (name.equals(""))) {
            LOG.debug("No node name (uuid or path) configured, returning empty value list");
        } else {
            Node sourceNode = getSourceNode(name);
            if (sourceNode != null) {

                if (locale != null) {
                    sourceNode = getSourceNodeVariant(sourceNode, locale);
                }

                try {
                    LOG.debug("Start filling map with values from node " + sourceNode.getPath());

                    NodeIterator iterator = sourceNode.getNodes(Namespace.Type.VALUE_LIST_ITEM);
                    LOG.debug("Items in the list: {}", iterator.getSize());
                    while (iterator.hasNext()) {
                        Node n = iterator.nextNode();
                        String key = n.getProperty(Namespace.Property.KEY).getValue().getString();
                        String label = n.getProperty(Namespace.Property.LABEL).getValue().getString();

                        valuelist.add(new ListItem(key, label));

                        LOG.debug("Adding key: {} with value: {} ", key, label);
                    }
                } catch (RepositoryException e) {
                    LOG.error("RepositoryException occurred while trying to fill the list of values: {}", e.getMessage());
                }
            }
        }

        return valuelist;
    }

    /**
     * Gets the configured JCR node that holds the values for the select.
     *
     * @param nodeSource path to the source node
     * @return {@link Node}
     */
    protected Node getSourceNode(String nodeSource) {
        Node valueNode;
        try {
            LOG.debug("Trying to get node from: {}", nodeSource);

            final Session session = obtainSession();
            if (nodeSource.startsWith("/")) {
                final String relativePath = nodeSource.substring(1);
                if (!session.getRootNode().hasNode(relativePath)) {
                    LOG.warn("Value list node not found at absolute path {}", nodeSource);
                    return null;
                }
                valueNode = session.getRootNode().getNode(relativePath);
            } else {
                // assume nodeSource is a uuid
                valueNode = session.getNodeByIdentifier(nodeSource);
            }

            LOG.debug("Nodetype of valueNode: {}", valueNode.getPrimaryNodeType().getName());
            if (valueNode.getPrimaryNodeType().getName().equals(HippoNodeType.NT_HANDLE)) {
                valueNode = valueNode.getNode(valueNode.getName());
                LOG.debug("Nodetype of valueNode below configured handle: {}", valueNode.getPrimaryNodeType().getName());
            }
            LOG.debug("Found node with name: {}", valueNode.getName());

            return valueNode;

        } catch (IllegalArgumentException e) {
            LOG.warn("IllegalArgumentException: provided source " + nodeSource + " is probably not a valid node UUID", e);
        } catch (ItemNotFoundException e) {
            LOG.warn("ItemNotFoundException occurred while trying to get value list node by source " + nodeSource, e);
        } catch (RepositoryException e) {
            LOG.error("RepositoryException occurred while trying to get value list node by source " + nodeSource, e);
        }
        return null;
    }

    /**
     * Gets the JCR {@link Session} from the Wicket {@link org.apache.wicket.Session}
     *
     * @return {@link Session}
     */
    protected Session obtainSession() {
        UserSession userSession = (UserSession) org.apache.wicket.Session.get();
        return userSession.getJcrSession();
    }

    /**
     * Get a possible variant of a source node specifified by locale.
     */
    protected Node getSourceNodeVariant(final Node sourceNode, final Locale locale) {
        Node variant = sourceNode;

        // Check if the document exists in the preferred locale
        try {
            if (sourceNode.hasProperty(HippoTranslationNodeType.ID)) {
                String id = sourceNode.getProperty(HippoTranslationNodeType.ID).getString();
                String xpath = "//element(*," + HippoTranslationNodeType.NT_TRANSLATED + ")["
                        + HippoTranslationNodeType.ID + " = '" + id + "' and "
                        + HippoTranslationNodeType.LOCALE + " = '" + locale.toString() + "']";
                Query query = sourceNode.getSession().getWorkspace().getQueryManager().createQuery(xpath, "xpath");
                final NodeIterator nodeIterator = query.execute().getNodes();
                if (nodeIterator.hasNext()) {
                    variant = nodeIterator.nextNode();
                    LOG.debug("Using '{}' valuelist translation: '{}'", locale, variant.getPath());
                } else {
                    LOG.debug("'{}' valuelist translation of '{}' does not exist", locale, variant.getPath());
                }
            }
        } catch (RepositoryException e) {
            LOG.warn("RepositoryException getting source node variant", e);
        }

        return variant;
    }


    private static final CssResourceReference SELECT2_CSS = new CssResourceReference(AbstractAssignDialog.class, "select2.min.css");
    private static final JavaScriptResourceReference SELECT2_JS = new JavaScriptResourceReference(AbstractAssignDialog.class, "select2.min.js");


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(SELECT2_CSS));
        response.render(JavaScriptHeaderItem.forReference(SELECT2_JS));
        response.render(OnDomReadyHeaderItem.forScript("$(document).ready(function() {\n" +
                "  $(\".js-example-basic-single\").select2();\n" +
                "});"));
    }
}
