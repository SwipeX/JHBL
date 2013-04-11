package org.javahacking.jhbl.data;

import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;

/**
 * Acts as a tool to access {@link org.objectweb.asm.tree.ClassNode}s.
 *
 * @author trDna
 */
public class ClassPool {

    private HashMap<String, ClassNode> loadedClasses;

    public ClassPool(HashMap<String, ClassNode> loadedClasses){
        this.loadedClasses = loadedClasses;
    }

}
