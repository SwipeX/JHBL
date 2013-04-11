package org.javahacking.jhbl.data;

import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;

/**
 * Acts as a tool to access {@link org.objectweb.asm.tree.ClassNode}s.
 *
 * @author trDna
 */
public class ClassPool{

    /**
     * The loaded classes.
     */
    private HashMap<String, ClassNode> loadedClasses;

    /**
     * Constructs a {@link ClassPool} object containing {@link ClassNode}s.
     *
     * @param loadedClasses The loaded classes stored in a {@link HashMap}.
     */
    public ClassPool(HashMap<String, ClassNode> loadedClasses){
        this.loadedClasses = loadedClasses;
    }

    /**
     * Retrieves a {@link ClassNode} with a given name.
     *
     * @param cName The class name.
     * @return The corresponding {@link ClassNode}.
     */
    public ClassNode getClassNodeAt(String cName){
        return loadedClasses.get(cName);
    }

    /**
     * Removes a {@link ClassNode} with a given name.
     *
     * @param cName The class name.
     */
    public void removeClassNodeAt(String cName){
        loadedClasses.remove(cName);
    }

    /**
     * Turns the {@link HashMap} into a {@link ClassNode} array.
     *
     * @return The {@link ClassNode}s stored.
     */
    public ClassNode[] toArray(){
        return loadedClasses.values().toArray(new ClassNode[loadedClasses.size()]);
    }

}
