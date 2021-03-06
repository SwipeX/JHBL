package org.javahacking.jhbl.io;

import org.javahacking.jhbl.data.ClassPool;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * Obvious stuff.
 *
 * @author Swipe
 * @author trDna
 */
public class JarWriter {

    public JarWriter(String location, ClassPool cp) {
        try {
            File file = new File(location);
            FileOutputStream stream = new FileOutputStream(file);
            JarOutputStream out = new JarOutputStream(stream);
            for (ClassNode classNode : cp.getClasses()) {
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
