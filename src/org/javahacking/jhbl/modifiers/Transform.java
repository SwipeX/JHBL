package org.javahacking.jhbl.modifiers;

import org.objectweb.asm.tree.ClassNode;


/**
 * @author Ganesh Ravendranathan
 * @since 1.7
 */
public abstract class Transform {

    public abstract boolean accept(ClassNode theClass);

    public abstract void process(ClassNode cNode);

    public abstract void runTransform();

}
