package org.javahacking.jhbl.modifiers;

import org.javahacking.jhbl.data.ClassPool;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

/**
 * What do you think this does?
 *
 * @author trDna
 */
public class Refactorer {

    protected static void renameFields(ClassPool cp){
        int fieldNum = 0;
        int arrayNum = 0;

        for(ClassNode cn : cp.getClasses()){

            //Rename field declarations
            for(FieldNode fn : cn.fields){

                String realFName = fn.name;
                String deobName = "";

                //Prepend the base of the FieldName
                if(!fn.desc.contains(";")){
                    if (fn.desc.contains("I"))
                        deobName += "anInt";
                    else if (fn.desc.contains("J"))
                        deobName += "aLong";
                    else if (fn.desc.contains("S"))
                        deobName += "aShort";
                    else if (fn.desc.contains("C"))
                        deobName += "aChar";
                    else if (fn.desc.contains("Z"))
                        deobName += "aBoolean";
                    else if (fn.desc.contains("B"))
                        deobName += "aByte";
                    else if (fn.desc.contains("D"))
                        deobName += "aDouble";
                } else {
                    deobName += ((fn.desc.startsWith("a") || fn.desc.startsWith("e") || fn.desc.startsWith("i") || fn.desc.startsWith("o") || fn.desc.startsWith("u")) ? "an" : "a");
                    deobName += fn.desc.replace("L", "").replace(";", "").replace("[", "");

                }

                //Check if it's an array
                if(fn.desc.contains("[")){
                    deobName += "Array" + ++arrayNum;
                }else{
                    deobName += ++fieldNum;
                }

                //Rename instance or class fields within methods
                for(MethodNode mn : cn.methods){
                    ListIterator<AbstractInsnNode> listIterator = mn.instructions.iterator();
                    while(listIterator.hasNext()){
                        AbstractInsnNode ain = listIterator.next();
                        if(ain instanceof FieldInsnNode){
                            FieldInsnNode fin = (FieldInsnNode) ain;
                            if(fin.name.equals(realFName)){
                                fin.name = deobName;
                                System.out.println(realFName + " renamed to " + fin.name);
                            }
                        }
                    }
                }

                fn.name = deobName;

            }
            fieldNum = 0;
            arrayNum = 0;
        }
    }

}
