package org.javahacking.jhbl.io;

import org.javahacking.jhbl.StoragePool;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 1/28/13
 * Time: 4:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class JarWriter {

    public JarWriter(String location) {
        try {
            File file = new File(location);
            FileOutputStream stream = new FileOutputStream(file);
            JarOutputStream out = new JarOutputStream(stream);
            for (ClassNode classNode : StoragePool.getClasses().values()) {
                JarEntry je = new JarEntry(classNode.name.replace('.', '/') + ".class");
                out.putNextEntry(je);
                out.write(classNode.getBytes(false));
            }
            out.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
