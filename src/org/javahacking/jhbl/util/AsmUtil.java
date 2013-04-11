package org.javahacking.jhbl.util;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;

import java.util.Arrays;
import java.util.LinkedList;

public class AsmUtil implements Opcodes {


    public static ClassNode get(final ClassNode[] classes, final String name) {
        for (final ClassNode node : classes) {
            if (node.name.equals(name)) {
                return node;
            }
        }
        return null;
    }

    public static LinkedList<ClassNode> getDirectDescendants(final ClassNode[] classes, final ClassNode baseClass) {
        final LinkedList<ClassNode> directDescendants = new LinkedList<>();
        for (final ClassNode node : classes) {
            if (node.superName.equals(baseClass.name)) {
                directDescendants.add(node);
            }
        }
        return directDescendants;
    }

    public static LinkedList<ClassNode> getDescendants(final ClassNode[] classes, final ClassNode baseClass) {
        final LinkedList<ClassNode> descendants = new LinkedList<>(),
                uncheckedDescendants = new LinkedList<>();
        uncheckedDescendants.add(baseClass);
        while (!uncheckedDescendants.isEmpty()) {
            final LinkedList<ClassNode> direct = getDirectDescendants(classes, uncheckedDescendants.pop());
            descendants.addAll(direct);
            uncheckedDescendants.addAll(direct);
        }
        return descendants;
    }

    public static boolean isDescendantOf(final ClassNode[] classes, ClassNode current, final ClassNode base) {
        loop:
        while (current != null) {
            if (current.equals(base)) {
                return true;
            } else if (current.superName.equals("java/lang/Object")) {
                break;
            }
            for (final ClassNode node : classes) {
                if (node.name.equals(current.superName)) {
                    current = node;
                    continue loop;
                }
            }
            break;
        }
        return false;
    }

    public static boolean isDescendantOf(final ClassNode[] classes, final String current, final String base) {
        return isDescendantOf(classes, get(classes, current), get(classes, base));
    }

    /**
     * @param nodeA a node
     * @param nodeB a node
     * @return nodeA or nodeB, depending on which comes first.  Returns nodeA if the instructions are in different methods
     */
    public static AbstractInsnNode whichIsFirst(final AbstractInsnNode nodeA, AbstractInsnNode nodeB) {
        while ((nodeB = nodeB.getNext()) != null) {
            if (nodeB.equals(nodeA)) {
                return nodeB;
            }
        }
        return nodeA;
    }

    public static AbstractInsnNode getTarget(final JumpInsnNode jin) {
        AbstractInsnNode target = jin.label;
        while (target != null && target.getOpcode() < 0) {
            target = target.getNext();
        }
        return target;
    }

    public static boolean isConstant(final AbstractInsnNode instruction) {
        return instruction instanceof LdcInsnNode
                || instruction instanceof IntInsnNode
                || instruction.getOpcode() == ACONST_NULL
                || instruction.getOpcode() - ICONST_M1 <= 6 && instruction.getOpcode() - ICONST_M1 >= 0
                || instruction.getOpcode() == LCONST_0 || instruction.getOpcode() == LCONST_1
                || instruction.getOpcode() - FCONST_0 >= 0 && instruction.getOpcode() - FCONST_0 <= 2
                || instruction.getOpcode() == DCONST_0 || instruction.getOpcode() == DCONST_1;
    }


    public static AbstractInsnNode generateNumericalInstruction(final Number number) {
        if (number instanceof Integer) {
            final int value = (Integer) number;
            if (value >= -128 && value <= 127) {
                return new IntInsnNode(BIPUSH, value);
            } else if (value >= -32768 && value <= 32767) {
                return new IntInsnNode(SIPUSH, value);
            }
        }
        return new LdcInsnNode(number);
    }

    /**
     * Checks whether an instruction add exactly one element to the stack
     *
     * @param input an abstract instruction node to check
     * @return whether or not the input adds exactly one element to the stack
     */
    public static boolean isSingleInstructionStackInsertion(final AbstractInsnNode input) {
        if (input.getOpcode() == INVOKESTATIC) {
            final String desc = ((MethodInsnNode) input).desc;
            return desc.startsWith("()") && !desc.endsWith(")V");
        }
        return input.getOpcode() >= ACONST_NULL && input.getOpcode() <= ALOAD || input.getOpcode() == GETSTATIC;
    }

    /**
     * @param input instruction to check
     * @return whether or not the input replaces the element on the top of the stack
     */
    public static boolean isStackReplacement(final AbstractInsnNode input) {
        if (input.getOpcode() == INVOKEVIRTUAL || input.getOpcode() == INVOKEINTERFACE) {
            final String desc = ((MethodInsnNode) input).desc;
            return desc.startsWith("()") && !desc.endsWith(")V");
        }
        return input.getOpcode() == GETFIELD;
    }

    public static boolean isBytecodeValid(final ClassNode... classes) {
        final Analyzer analyzer = new Analyzer(new BasicInterpreter());
        for (final ClassNode node : classes) {
            for (final MethodNode method : node.getMethods()) {
                try {
                    analyzer.analyze(node.name, method);
                } catch (final AnalyzerException ae) {
                    System.out.println(node.name + "." + method.name);
                    return false;
                }
            }
        }
        return true;
    }

    public static FieldNode getField(final ClassNode[] classes, final String owner, final String name, final String desc) {
        for (final ClassNode node : classes) {
            if (node.name.equals(owner)) {
                for (final FieldNode field : node.getFields()) {
                    if (field.name.equals(name) && field.desc.equals(desc)) {
                        return field;
                    }
                }
            }
        }
        return null;
    }

    public static FieldNode[] getAccessibleFields(final ClassNode[] classes, final ClassNode clazz) {
        final LinkedList<FieldNode> fields = new LinkedList<>();
        for (final ClassNode node : getDescendants(classes, clazz)) {
            for (final FieldNode field : node.getFields()) {
                fields.add(field);
            }
        }
        return fields.toArray(new FieldNode[fields.size()]);
    }

    public static int getHashcode(final ClassNode classnode) {
        final ClassWriter writer = new ClassWriter(0);
        classnode.accept(writer);
        return Arrays.hashCode(writer.toByteArray());
    }

    public static boolean isFieldInherited(final ClassNode base, final String fieldName, final String fieldDescriptor) {
        if (base == null) {
            return false;
        }
        for (final FieldNode field : base.getFields()) {
            if (field.name.equals(fieldName) && field.desc.equals(fieldDescriptor)) {
                return false;
            }
        }
        return true;
    }

    public static AbstractInsnNode getNext(AbstractInsnNode base, final int opcode) {
        while ((base = base.getNext()) != null && base.getOpcode() != opcode) ;
        return base;
    }

    public static AbstractInsnNode getNext(AbstractInsnNode base, final Class<? extends AbstractInsnNode> nodeType) {
        while ((base = base.getNext()) != null && !nodeType.isAssignableFrom(base.getClass())) ;
        return base;
    }


    public static AbstractInsnNode getPrevious(AbstractInsnNode base, final int opcode) {
        while ((base = base.getPrevious()) != null && base.getOpcode() != opcode) ;
        return base;
    }

    public static AbstractInsnNode getPrevious(AbstractInsnNode base, final Class<? extends AbstractInsnNode> nodeType) {
        while ((base = base.getPrevious()) != null && !nodeType.isAssignableFrom(base.getClass())) ;
        return base;
    }


    public static AbstractInsnNode getRealPrevious(AbstractInsnNode ain) {
        while ((ain = ain.getPrevious()) != null) {
            if (ain.getOpcode() >= 0)
                break;
        }
        return ain;
    }


}
