package org.javahacking.jhbl.analysis;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ListIterator;

/**
 * TODO: A lot of things
 * @author trDna
 */
public class Analyzer implements Opcodes {


    public static void analyze(MethodNode mv) {
        ListIterator<AbstractInsnNode> iterator = mv.instructions.iterator();

        int opcode;

        while (iterator.hasNext()){
            AbstractInsnNode ain = iterator.next();

            opcode = ain.getOpcode();

            if (ain.getOpcode() == NOP) {
                mv.instructions.remove(ain);
            }

            switch (opcode){
                case NOP :
                    mv.instructions.remove(ain);
                    break;
                case GOTO:
                    System.out.println("GOTO found in method: " + mv.name);
                    break;
            }

        }
    }

}
