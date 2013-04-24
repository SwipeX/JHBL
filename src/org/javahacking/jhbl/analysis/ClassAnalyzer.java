package org.javahacking.jhbl.analysis;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author trDna
 */
public class ClassAnalyzer extends ClassVisitor implements Opcodes {

    private ClassVisitor next;

    public ClassAnalyzer(final ClassVisitor cv){
        //Pass a MethodNode to the ClassVisitor
        super(ASM4, new ClassNode());
        this.next = this.cv;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions){
        //Build the MethodVisitor
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        if(mv != null){
            //Run the Optimizer on the method. It will return a newly transformed MethodVisitor
            mv = new Optimizer(mv);
        }

        return mv;
    }
}
