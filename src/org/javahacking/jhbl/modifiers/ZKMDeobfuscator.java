package org.javahacking.jhbl.modifiers;

import org.javahacking.jhbl.data.ClassPool;
import org.javahacking.jhbl.io.JarReference;

import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author trDna
 */
public class ZKMDeobfuscator extends Deobfuscator{

    /**
     * The opcodes to use.
     */
    private ArrayList<Integer> opkeys = new ArrayList<>();

    /**
     * Constructs a {@link Deobfuscator} that can deobfuscate methods obfuscated by ZKM.
     *
     * @param jr The {@link JarReference} to work with.
     */
    public ZKMDeobfuscator(JarReference jr){
        super(jr);
    }

    /**
     * Constructs a {@link Deobfuscator} that can deobfuscate methods obfuscated by ZKM.
     *
     * @param cp The {@link ClassPool} to work with.
     */
    public ZKMDeobfuscator(ClassPool cp){
        super(cp);
    }


}
