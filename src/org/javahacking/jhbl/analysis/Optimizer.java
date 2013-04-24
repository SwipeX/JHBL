package org.javahacking.jhbl.analysis;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * TODO: A lot of things
 * @author trDna
 */
public class Optimizer extends MethodVisitor implements Opcodes {

    static int intIdx;

    public Optimizer(MethodVisitor mv) {
        super(ASM4, mv);
    }

    @Override
    public void visitInsn(int opcode) {
        //Remove useless NOPs
        if (opcode != NOP) {
            mv.visitInsn(opcode);


            switch (opcode){
                //Check GOTOs
                case GOTO:
                    System.out.println("GOTO found in method");
                    break;
            }

        }


    }


}
