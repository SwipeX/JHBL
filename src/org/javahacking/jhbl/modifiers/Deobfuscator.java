package org.javahacking.jhbl.modifiers;

import org.javahacking.jhbl.analysis.Optimizer;
import org.javahacking.jhbl.data.ClassPool;
import org.javahacking.jhbl.io.JarConstruct;

import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * What do you think this does?
 *
 * TODO:
 * - The Core of the Deobfuscator
 *   [x] The
 * - The field and class remapper
 *
 * @author trDna
 */
public class Deobfuscator {

    /**
     * The {@link ClassPool} to work with.
     */
    private ClassPool cp;

    /**
     * Contains a {@link ClassPool} with all of the newly deobfucated {@link ClassNode}s.
     */
    private ClassPool injCp;

    /**
     * {@link ClassTransformer}s. It may have a use later.
     */
    private LinkedList<ClassTransformer> transforms = new LinkedList<>();

    /**
     * Constructs a {@link Deobfuscator}. It does what it's supposed to do.
     *
     * @param jc The {@link JarConstruct} to work with.
     */
    public Deobfuscator(JarConstruct jc){
        this.cp = jc.getClassPool();
    }

    /**
     * Constructs a {@link Deobfuscator}. It does what it's supposed to do.
     *
     * @param cp The {@link ClassPool} to work with.
     */
    public Deobfuscator(ClassPool cp){
        this.cp = cp;
    }

    /**
     * Runs all forms of deobfuscation that this class provides.
     */
    public void runAll(){
        runRenamer();

        //Dump stuff
        HashMap<String, ClassNode> injClasses = new HashMap<>();

        for(ClassNode cn : cp.getClasses()){
            for(MethodNode mn : cn.methods){
                new Optimizer(mn);
            }
            injClasses.put(cn.name, cn);
        }

        this.injCp = new ClassPool(injClasses);
    }

    /**
     * The transformed {@link ClassPool}.
     *
     * @return The transformed {@link ClassPool}.
     */
    public ClassPool getClassPool(){
        return injCp;
    }

    /**
     * Used to give fields and classes better names.
     */
    public void runRenamer(){
        Refactorer.renameFields(cp);
    }

}
