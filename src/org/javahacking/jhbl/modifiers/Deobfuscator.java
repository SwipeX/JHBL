package org.javahacking.jhbl.modifiers;

import org.javahacking.jhbl.data.ClassPool;
import org.javahacking.jhbl.io.JarReference;

import org.objectweb.asm.Opcodes;

import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.Frame;

import java.util.ListIterator;

/**
 * What do you think this does?
 *
 * TODO:
 * - The Core of the Deobfuscator
 * - The field and class remapper
 *
 * @author trDna
 */
public abstract class Deobfuscator implements Opcodes {

    /**
     * The {@link ClassPool} to work with.
     */
    protected ClassPool cp;

    /**
     * Contains a {@link ClassPool} with all of the newly deobfucated {@link ClassNode}s.
     */
    private ClassPool injCp;

    /**
     * Used to hold opcodes as a {@link String} array.
     */
    private static final String[] OPCODES;

    /**
     * Used for printing out the opcodes.
     */
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
     * Replaces a jump to a GOTO label instruction with a jump to a label.
     * It also replaces a GOTO to a RETURN instruction with a simple RETURN instruction (because it's redundant).
     * More to come.
     *
     * @param mn The {@link MethodNode} to be optimized.
     * @see org.objectweb.asm.Opcodes
     */
    public void optimizeJumps(MethodNode mn){
        ListIterator<AbstractInsnNode> iterator = mn.instructions.iterator();

        while (iterator.hasNext()){
            AbstractInsnNode ain = iterator.next();

            if(ain instanceof JumpInsnNode){
                LabelNode lbl = ((JumpInsnNode) ain).label;
                AbstractInsnNode tgt;

                while (true){
                    tgt = lbl;
                    while (tgt != null && tgt.getOpcode() < 0){
                        tgt = tgt.getNext();
                    }
                    if(tgt != null && tgt.getOpcode() == GOTO){
                        System.err.println("Optimizing jump insns..");
                        lbl = ((JumpInsnNode)tgt).label;
                    }else{
                        break;
                    }
                }

                ((JumpInsnNode) ain).label = lbl;

                if(ain.getOpcode() == GOTO && tgt != null){
                    int opcode = tgt.getOpcode();

                    if((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW){
                        System.out.println("Optimizing jump instructions..");
                        mn.instructions.set(ain, tgt.clone(null));
                    }

                }

            }

        }
    }


    /**
     * Removes any unnecessary code.
     *
     * @param cn The {@link ClassNode} to work with.
     */
    public void removeDeadCode(ClassNode cn){
        org.objectweb.asm.tree.analysis.Analyzer asmAnalyzer = new org.objectweb.asm.tree.analysis.Analyzer(new BasicInterpreter());

        for(MethodNode mn : cn.methods){
            try{
                asmAnalyzer.analyze(cn.name, mn);

                Frame[] analyzerFrames = asmAnalyzer.getFrames();
                AbstractInsnNode[] ains = mn.instructions.toArray();

                for(int i = 0; i < analyzerFrames.length; i++) {
                    if(analyzerFrames[i] == null && !(ains[i] instanceof LabelNode)) {
                        mn.instructions.remove(ains[i]);
                        System.out.println("Removing dead code..");
                    }
                }

            }catch (AnalyzerException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints the opcodes passed in.
     *
     * @param owner The class name.
     * @param mv The {@link MethodNode} to be analyzed.
     */
    public void printOpcodes(String owner, MethodNode mv) {
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

                }else if (ain instanceof IntInsnNode){
                    IntInsnNode in = (IntInsnNode)ain;
                    output += (OPCODES[opcode]) + "[" + in.operand  + "]" + LINE_SEPARATOR;

                }else if (ain instanceof IincInsnNode){
                    IincInsnNode in = (IincInsnNode)ain;
                    output += (OPCODES[opcode]) + "[Increment of " + in.incr + " at Var " + in.var  + "]" + LINE_SEPARATOR;

                }else if (ain instanceof LdcInsnNode){
                    LdcInsnNode ldc = (LdcInsnNode)ain;
                    output += (OPCODES[opcode]) + "[" + ldc.cst + "]" + LINE_SEPARATOR;

                }else if (ain instanceof MethodInsnNode){
                    MethodInsnNode min = (MethodInsnNode)ain;
                    output += (OPCODES[opcode]) + "[" + min.owner + "." + min.name + min.desc + "]" + LINE_SEPARATOR;

                }else if (ain instanceof InvokeDynamicInsnNode){
                    InvokeDynamicInsnNode id = (InvokeDynamicInsnNode)ain;
                    output += (OPCODES[opcode]) + "[" + id.name + " <" + id.desc + ">" + "<" + id.bsm.toString() + ">" +  "]" + LINE_SEPARATOR;

                }else if (ain instanceof TableSwitchInsnNode){
                    TableSwitchInsnNode ts = (TableSwitchInsnNode)ain;
                    output += "[" + OPCODES[opcode];

                    for(LabelNode ln : ts.labels){
                        output += "(HBOffset: + " + mv.instructions.indexOf(ln) + ")";
                    }

                    output += "(DFLT-HBOffset: " + mv.instructions.indexOf(ts.dflt) + ")";
                    output += "(Max: " + ts.max + ", Min: " + ts.min + ")";

                    output += "]" + LINE_SEPARATOR;

                }else if (ain instanceof LookupSwitchInsnNode){
                    LookupSwitchInsnNode ls = (LookupSwitchInsnNode)ain;
                    output += "[" + OPCODES[opcode];

                    for(int key : ls.keys){
                        output += "(Key: " + key + ")";
                    }

                    for(LabelNode ln : ls.labels){
                        output += "(HBOffset: + " + mv.instructions.indexOf(ln) + ")";
                    }

                    output += "(DFLT-HBOffset: " + mv.instructions.indexOf(ls.dflt) + ")";

                    output +=  "]" + LINE_SEPARATOR;

                }else{
                    output += (OPCODES[opcode]) + LINE_SEPARATOR;
                }
            }

        }

        System.out.println(output);
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
