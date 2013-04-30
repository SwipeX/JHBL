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

    public void deobfuscateAll(ClassNode cn){
        deobStrings(cn.methods.get(cn.methods.size() - 1));
        System.out.println("EncKey Grabber for class: " + cn.name);

    }

    private String decryptString(String str, List<Integer> keys){
        String tmp = "";
        for(int i = 0; i < str.toCharArray().length; ++i){
            tmp += (char)(str.charAt(i) ^ keys.get(i % keys.size()));
        }
        return tmp;
    }

    private void deobStrings(MethodNode mn){
        AbstractInsnNode ain;
        Iterator<AbstractInsnNode> iterator =  mn.instructions.iterator();

        while (iterator.hasNext()){
            ain = iterator.next();

            if(ain instanceof LdcInsnNode){
                LdcInsnNode ldc = (LdcInsnNode) ain;
                if(!(ldc.cst instanceof String)){
                    continue;
                }

                AbstractInsnNode ai = iterator.next();
                AbstractInsnNode aii = iterator.next();

                if(ai instanceof MethodInsnNode && ai.getOpcode() == INVOKESTATIC && aii instanceof MethodInsnNode && aii.getOpcode() == INVOKESTATIC){
                    mn.instructions.insertBefore(ldc, new LdcInsnNode(decryptString((String) ldc.cst, getKeys())));
                    mn.instructions.remove(ldc);
                    mn.instructions.remove(ai);
                    mn.instructions.remove(aii);
                }
            }

        }
    }

    private ArrayList<Integer> getKeys(){
        ArrayList<Integer> opkeys = new ArrayList<>();
        AbstractInsnNode ain;

        for(ClassNode cn : cp.getClasses()){
            for(MethodNode mn : cn.methods){
                Iterator<AbstractInsnNode> iterator = mn.instructions.iterator();

                while(iterator.hasNext()){
                    ain = iterator.next();

                    if((ain instanceof InsnNode)){
                        if(ain.getOpcode() == IXOR){
                            return opkeys;
                        }

                        opkeys.add(ain.getOpcode() - 3);
                    }else if (ain instanceof IntInsnNode){

                        IntInsnNode in = (IntInsnNode) ain;
                        if(in.getOpcode() == SIPUSH || in.getOpcode() == BIPUSH){
                            opkeys.add(in.operand);
                        }
                    }

                }

            }
        }
        return opkeys;
    }

}
