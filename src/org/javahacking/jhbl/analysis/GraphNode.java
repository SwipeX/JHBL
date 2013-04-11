package org.javahacking.jhbl.analysis;

import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

/**
 * A representation of a node of a graph.
 *
 * @author trDna
 */
public class GraphNode<V extends Value> extends Frame<V> {

    /**
     * Represents a Node for use of a Graph.
     *
     * @param nLocals
     * @param nStack
     */
    public GraphNode(int nLocals, int nStack){
        super(nLocals, nStack);
    }

    /**
     * Represents a Node for use of a Graph.
     *
     * @param src A Frame.
     */
    public GraphNode(Frame<? extends V> src){
        super(src);
    }

}

