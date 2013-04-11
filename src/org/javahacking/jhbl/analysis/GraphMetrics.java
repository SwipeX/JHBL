package org.javahacking.jhbl.analysis;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

/**
 * A set of utilities for an {@link org.objectweb.asm.tree.analysis.Analyzer}.
 *
 * @author trDna
 */
public class GraphMetrics {

    /**
     * Calculates the cyclomatic complexity of the control flow graph representation of a {@link MethodNode}.
     * This will give a good idea of how complex a method is.
     *
     * M = E - N + 2
     *
     * Let M be the complexity, E be the number of edges of a control flow graph and N be the number of nodes of the control flow graph.
     * M, E and N are integers.
     *
     *
     * @param owner The owner of the {@link MethodNode}.
     * @param mn The {@link MethodNode}.
     *
     * @return The recommended number of test cases to test a {@link MethodNode} correctly.
     */
    public int getCyclomaticComplexity(String owner, MethodNode mn) throws AnalyzerException{
        ControlFlowAnalyzer<BasicValue> a = new ControlFlowAnalyzer<>(mn, new BasicInterpreter());

        a.analyze(owner, mn);

        Frame<BasicValue>[] frames = a.getFrames();
        int edges = 0;
        int nodes = 0;

        for(int i = 0; i < frames.length; ++i){
            if(frames[i] != null){
                edges += ((GraphNode<BasicValue>) frames[i]).successors.size();
                nodes += 1;
            }
        }

        return edges - nodes + 2;
    }

}

