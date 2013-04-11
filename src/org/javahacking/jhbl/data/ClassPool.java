package org.javahacking.jhbl.data;

import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;
import java.util.HashMap;

/**
 * Acts as a tool to access {@link org.objectweb.asm.tree.ClassNode}s.
 *
 * @author trDna
 * @author Swipe
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
    public ClassNode getClassNode(String cName){
        return loadedClasses.get(cName);
    }

    /**
     * Removes a {@link ClassNode} with a given name.
     *
     * @param cName The class name.
     */
    public void removeClassNode(String cName){
        loadedClasses.remove(cName);
    }

    /**
     * Retrieves the loaded {@link ClassNode} as a {@link Collection}.
     *
     * @return The {@link ClassNode}s.
     */
    public Collection<ClassNode> getClasses(){
        return loadedClasses.values();
    }

    /**
     * Turns the {@link HashMap} into a {@link ClassNode} array.
     *
     * @return The {@link ClassNode}s stored.
     */
    public ClassNode[] toArray(){
        return loadedClasses.values().toArray(new ClassNode[loadedClasses.size()]);
    }


    @Override
    public String toString(){
        String tmp = "[";
        ClassNode[] cns = toArray();

        for (int i = 0; i < getClasses().size(); ++i){

            tmp += cns[i].name;

            if(i == getClasses().size() - 1){
                tmp += "]";

            }else{
                tmp += ", ";
            }


        }
        return tmp;
    }

}
