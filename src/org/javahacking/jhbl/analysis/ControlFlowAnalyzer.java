package org.javahacking.jhbl.analysis;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

/**
 * Unfinished.
 * @author trDna
 */
public class ControlFlowAnalyzer<V extends Value> extends Analyzer {

    private MethodNode mn;

    /**
     * Constructs a new {@link org.objectweb.asm.tree.analysis.Analyzer}.
     *
     * @param interpreter the interpreter to be used to symbolically interpret the
     *                    bytecode instructions.
     */
    public ControlFlowAnalyzer(MethodNode mn, Interpreter interpreter) {
        super(interpreter);
        this.mn = mn;
    }

    protected Frame<BasicValue> newFrame(int nLocals, int nStack) {
        return new GraphNode<>(nLocals, nStack);
    }
    protected Frame<V> newFrame(Frame src) {
        return new GraphNode<V>(src);
    }
    protected void newControlFlowEdge(int insn, int succ) {
        AbstractInsnNode from = mn.instructions.get(insn);
        AbstractInsnNode to = mn.instructions.get(succ);


        ClassWriter c;

        GraphNode<BasicValue> s = (GraphNode<BasicValue>) getFrames()[insn];
        s.successors.add((GraphNode<BasicValue>) getFrames()[succ]);
    }
}
