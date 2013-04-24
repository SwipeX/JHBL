package org.javahacking.jhbl.modifiers;

import org.javahacking.jhbl.analysis.ClassAnalyzer;
import org.javahacking.jhbl.analysis.adapters.AddInterfaceAdapter;
import org.javahacking.jhbl.analysis.adapters.ChangeSuperclassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;

import java.util.HashMap;


/**
 * @since 1.7
 * @author trDna
 */

public abstract class ClassTransformer extends Transform implements Opcodes {

    private ClassReader cr;
    private ClassWriter cw;

    private ClassVisitor currentAdapter;

    private byte[] classBytes;

    protected static HashMap<String, String> classNames = new HashMap<>();


    /**
     * Starts up the transform.
     *
     * @param cn - The class to use.
     */
    public void process(final ClassNode cn){

        try{
            //Set up the ClassWriter.
            cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            //Delegate cn method calls and data to 'cw'.
            cn.accept(cw);

            //Set up the ClassReader
            cr = new ClassReader(cw.toByteArray());

            cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            ClassVisitor cv = new CheckClassAdapter(cw);

            //Set the current adapter to be the ClassWriter.
            currentAdapter = cv;

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    /**
     * End-User could use this to manipulate files.
     */
    public abstract void runTransform();

    /**
     * Adds a getter to return a wanted value.
     *
     * @param targetVar - The target field name (to be changed so that it auto detects the field!).
     * @param descriptor - The descriptor of the target field.
     * @param getterName - The name of the getter method.
     * @param retInsn - The return instruction (which returns the value of what you want).
     *
     * @since 1.7
     */
    public void addGetterMethod(final String targetVar, final String descriptor, final String getterName, final int retInsn) {
        AddGetterAdapter am = new AddGetterAdapter(currentAdapter, targetVar, descriptor, getterName, retInsn);
        currentAdapter = am;
    }

    public void addCustomGetterMethod(final String fieldName, final String fieldDescriptor, final String getterName, final String getterDesc, final int retInsn) {
        AddGetterAdapter am = new AddGetterAdapter(currentAdapter, fieldName, fieldDescriptor, getterName, getterDesc, retInsn);
        currentAdapter = am;
    }

    public void optimize(){
        ClassAnalyzer ca = new ClassAnalyzer(currentAdapter);
        currentAdapter = ca;
    }
    /**
     * Adds an interface to a given class.
     *
     * @param interfacesToAdd - The interfaces to add to the given class.
     */
    public void addInterface(final String... interfacesToAdd){
        AddInterfaceAdapter ai = new AddInterfaceAdapter(currentAdapter, interfacesToAdd);
        currentAdapter = ai;
    }

    public void changeSuper(final String superClass){
        ChangeSuperclassAdapter sc = new ChangeSuperclassAdapter(currentAdapter, superClass);
        currentAdapter = sc;
    }


    public void applyChanges(){
        //Apply transformations from the top of the hierarchy backwards.
        cr.accept(currentAdapter, 0);

        //Have the new class bytes ready to go.
        classBytes = cw.toByteArray();

    }

    public byte[] getClassBytes(){
        return classBytes;
    }


    public ClassNode getResultingClassNode(){
        if(classBytes != null){
            ClassReader cr = new ClassReader(classBytes);
            ClassNode cn = new ClassNode(ASM4);
            cr.accept(cn, 0);
            return cn;
        } else{
            throw new NullPointerException("ClassNode bytes is null!");
        }
    }


}
