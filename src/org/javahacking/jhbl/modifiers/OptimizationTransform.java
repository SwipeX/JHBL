package org.javahacking.jhbl.modifiers;

import org.objectweb.asm.tree.ClassNode;

/**
 * @author trDna
 */
public class OptimizationTransform extends ClassTransformer {

    @Override
    public boolean accept(ClassNode theClass) {
        return true;
    }

    @Override
    public void runTransform() {
        optimize();
    }

}
