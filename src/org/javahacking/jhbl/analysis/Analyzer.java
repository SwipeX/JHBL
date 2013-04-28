package org.javahacking.jhbl.analysis;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

/**
 * TODO: A lot of things
 * @author trDna
 */
public class Analyzer implements Opcodes {

    private static final String[] OPCODES;

    private static final String LINE_SEPARATOR;

    static {
        String s = "NOP,ACONST_NULL,ICONST_M1,ICONST_0,ICONST_1,ICONST_2,"
                + "ICONST_3,ICONST_4,ICONST_5,LCONST_0,LCONST_1,FCONST_0,"
                + "FCONST_1,FCONST_2,DCONST_0,DCONST_1,BIPUSH,SIPUSH,LDC,,,"
                + "ILOAD,LLOAD,FLOAD,DLOAD,ALOAD,,,,,,,,,,,,,,,,,,,,,IALOAD,"
                + "LALOAD,FALOAD,DALOAD,AALOAD,BALOAD,CALOAD,SALOAD,ISTORE,"
                + "LSTORE,FSTORE,DSTORE,ASTORE,,,,,,,,,,,,,,,,,,,,,IASTORE,"
                + "LASTORE,FASTORE,DASTORE,AASTORE,BASTORE,CASTORE,SASTORE,POP,"
                + "POP2,DUP,DUP_X1,DUP_X2,DUP2,DUP2_X1,DUP2_X2,SWAP,IADD,LADD,"
                + "FADD,DADD,ISUB,LSUB,FSUB,DSUB,IMUL,LMUL,FMUL,DMUL,IDIV,LDIV,"
                + "FDIV,DDIV,IREM,LREM,FREM,DREM,INEG,LNEG,FNEG,DNEG,ISHL,LSHL,"
                + "ISHR,LSHR,IUSHR,LUSHR,IAND,LAND,IOR,LOR,IXOR,LXOR,IINC,I2L,"
                + "I2F,I2D,L2I,L2F,L2D,F2I,F2L,F2D,D2I,D2L,D2F,I2B,I2C,I2S,LCMP,"
                + "FCMPL,FCMPG,DCMPL,DCMPG,IFEQ,IFNE,IFLT,IFGE,IFGT,IFLE,"
                + "IF_ICMPEQ,IF_ICMPNE,IF_ICMPLT,IF_ICMPGE,IF_ICMPGT,IF_ICMPLE,"
                + "IF_ACMPEQ,IF_ACMPNE,GOTO,JSR,RET,TABLESWITCH,LOOKUPSWITCH,"
                + "IRETURN,LRETURN,FRETURN,DRETURN,ARETURN,RETURN,GETSTATIC,"
                + "PUTSTATIC,GETFIELD,PUTFIELD,INVOKEVIRTUAL,INVOKESPECIAL,"
                + "INVOKESTATIC,INVOKEINTERFACE,INVOKEDYNAMIC,NEW,NEWARRAY,"
                + "ANEWARRAY,ARRAYLENGTH,ATHROW,CHECKCAST,INSTANCEOF,"
                + "MONITORENTER,MONITOREXIT,,MULTIANEWARRAY,IFNULL,IFNONNULL,";

        OPCODES = new String[200];
        int i = 0;
        int j = 0;
        int l;
        while ((l = s.indexOf(',', j)) > 0) {
            OPCODES[i++] = j + 1 == l ? null : s.substring(j, l);
            j = l + 1;
        }

        LINE_SEPARATOR = System.lineSeparator();
    }


    public static void printOpcodes(String owner, MethodNode mv) {
        ListIterator<AbstractInsnNode> iterator = mv.instructions.iterator();

        int opcode;
        String output = "";

        output += "---------------- Class = " + owner + " Method = " + mv.name + mv.desc + "--------------------------" + LINE_SEPARATOR;

        while (iterator.hasNext()){
            AbstractInsnNode ain = iterator.next();

            opcode = ain.getOpcode();

            if(opcode >= 0 && opcode < OPCODES.length)   {

                if(ain instanceof LabelNode){
                    LabelNode ln = (LabelNode) ain;
                    output += "Label (+" +  ln.getLabel().getOffset() + ")";

                }else if (ain instanceof JumpInsnNode){
                    JumpInsnNode jin = (JumpInsnNode)ain;
                    output += (OPCODES[opcode]) + "(+" + mv.instructions.indexOf(jin.label) + ")" + LINE_SEPARATOR;

                 //Bugged? ALOAD_17.....
                }else if (ain instanceof VarInsnNode){
                    VarInsnNode vin = (VarInsnNode)ain;
                    output += (OPCODES[opcode]) + "_" + vin.var + LINE_SEPARATOR;

                }else if (ain instanceof FieldInsnNode){
                    FieldInsnNode fin = (FieldInsnNode)ain;
                    output += (OPCODES[opcode]) + "[" + fin.owner + "." + fin.name + " of type " + fin.desc +  "]" + LINE_SEPARATOR;

                }else{
                    output += (OPCODES[opcode]) + LINE_SEPARATOR;
                }
            }

        }

        System.out.println(output);
    }

}
