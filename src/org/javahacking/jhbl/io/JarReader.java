package org.javahacking.jhbl.io;

import org.javahacking.jhbl.StoragePool;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 1/28/13
 * Time: 3:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class JarReader {

    public JarReader(String location) {
        HashMap<String, ClassNode> classes = new HashMap<String, ClassNode>();
        try {
            JarFile jarfile = new JarFile(location);
            Enumeration<?> en = jarfile.entries();
            while (en.hasMoreElements()) {
                JarEntry entry = (JarEntry) en.nextElement();
                if (entry.getName().endsWith(".class") || entry.isDirectory() && !entry.getName().contains("META")) {
                    ClassReader reader = new ClassReader(jarfile.getInputStream(entry));
                    ClassNode cn = new ClassNode();
                    reader.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    classes.put(cn.name, cn);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        StoragePool.setClasses(classes);
    }
}
