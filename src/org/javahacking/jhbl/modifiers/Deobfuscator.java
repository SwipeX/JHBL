package org.javahacking.jhbl.modifiers;

import org.javahacking.jhbl.analysis.Analyzer;
import org.javahacking.jhbl.data.ClassPool;
import org.javahacking.jhbl.io.JarReference;

import org.objectweb.asm.tree.*;

import java.util.HashMap;

/**
 * What do you think this does?
 *
 * TODO:
 * - The Core of the Deobfuscator
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
     * Constructs a {@link Deobfuscator}. It does what it's supposed to do.
     *
     * @param jc The {@link org.javahacking.jhbl.io.JarReference} to work with.
     */
    public Deobfuscator(JarReference jc){
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

        HashMap<String, ClassNode> injClasses = new HashMap<>();

        for(ClassNode cn : cp.getClasses()){
             //TODO: Analysis
                Analyzer.printOpcodes(cn.name,  cn.methods.get(1));

            injClasses.put(cn.name, cn);
            break;
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
        Remapper.renameFields(cp);
    }

}
