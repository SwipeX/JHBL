package org.javahacking.jhbl;

import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 4/10/13
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class StoragePool {
    public static HashMap<String, ClassNode> classes = new HashMap<String, ClassNode>();

    public static HashMap<String, ClassNode> getClasses() {
        return classes;
    }

    public static void setClasses(HashMap<String, ClassNode> classe) {
        classes = classe;
    }
}
