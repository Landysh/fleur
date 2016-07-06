package io.landysh.inflor.java.knime.nodes.readFCSFool;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "ReadFCSSet" Node.
 * 
 *
 * @author Landysh Co.
 */
public class ReadFCSSetNodeFactory 
        extends NodeFactory<ReadFCSSetNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadFCSSetNodeModel createNodeModel() {
        return new ReadFCSSetNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<ReadFCSSetNodeModel> createNodeView(final int viewIndex,
            final ReadFCSSetNodeModel nodeModel) {
        return new ReadFCSSetNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new ReadFCSSetNodeDialog();
    }

}
