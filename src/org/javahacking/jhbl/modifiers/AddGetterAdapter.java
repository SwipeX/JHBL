package org.javahacking.jhbl.modifiers;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Adds a getter method to a {@link ClassNode}.
 * This adapter uses the Tree API, and it should be avoided if you're trying to make more efficient transformations.
 *
 * This is primarily used for providing a means of retrieving data within a class.

 * @see ClassVisitor
 * @author trDna
 */
public class AddGetterAdapter extends ClassVisitor implements Opcodes {

    /**
     * The field name.
     */
    private String fieldName = null;

    /**
     * The getter method name.
     */
    private String getterName = null;

    /**
     * The getter method descriptor (i.e. what it returns with its parameters in descriptor form).
     */
    private String fieldDescriptor = null;

    /**
     * A getter method's generics (it shouldn't be needed in most cases).
     */
    private String signature = null;

    /**
     * The return instruction opcode.
     */
    private int retInsn;

    /**
     * This is a toggle to ensure that the wanted field exists.
     */
    private boolean isFieldPresent = false;

    /**
     * This is a toggle to ensure to add a getter if there isn't a getter with the same method signature.
     */
    private boolean isMethodPresent = false;

    /**
     * This is a toggle for the GETSTATIC instruction.
     */
    private boolean isStatic = false;

    /**
     * The getter descriptor.
     */
    private String getterDesc;

    /**
     * For use within the adapter.
     */
    private ClassVisitor next;

    /**
     * Constructs an {@link AddGetterAdapter}.
     * This should be called when you're creating a getter method that doesn't require a descriptor that involves any class found outside of your project's scope.
     *
     *
     * @param cv The ClassVisitor (usually a mixture of adapters, or a {@link ClassNode}).
     * @param fieldName The wanted field's name.
     * @param fieldDescriptor The wanted field's descriptor.
     * @param getterName The getter method name. By default, the descriptor of the getter method will be the same as the field's descriptor.
     * @param retInsn The return instruction opcode.
     *
     */
    public AddGetterAdapter(final ClassVisitor cv, final String fieldName, final String fieldDescriptor, final String getterName, final int retInsn){
        //Pass a ClassNode to the ClassVisitor
        super(ASM4, new ClassNode());
        next = cv;

        //Initialize variables
        this.fieldName = fieldName;
        this.getterName = getterName;
        this.retInsn = retInsn;
        this.fieldDescriptor = fieldDescriptor;
        this.getterDesc = fieldDescriptor;
    }

    /**
     * Constructs an {@link AddGetterAdapter}.
     * This should be called when you're creating a getter method that requires a descriptor that involves any class found outside of your project's scope.
     *
     * @param cv The ClassVisitor (usually a mixture of adapters, or a {@link ClassNode}).
     * @param fieldName The wanted field's name.
     * @param fieldDescriptor The wanted field's descriptor.
     * @param getterName The getter method name.
     * @param getterDesc The getter descriptor.
     * @param retInsn The return instruction opcode.
     *
     */
    public AddGetterAdapter(final ClassVisitor cv, final String fieldName, final String fieldDescriptor, final String getterName, final String getterDesc, final int retInsn){
        //Pass a ClassNode to the ClassVisitor
        super(ASM4, new ClassNode());
        next = cv;

        //Initialize variables
        this.fieldName = fieldName;
        this.getterName = getterName;
        this.retInsn = retInsn;
        this.fieldDescriptor = fieldDescriptor;
        this.getterDesc = getterDesc;
    }


    @Override
    public void visitEnd(){
        //Call the newly transformed ClassNode from the ClassVisitor.
        ClassNode cn = (ClassNode) cv;

        //Loop through the fields to find if the field exists
        for(FieldNode f : cn.fields){

            //Check to see if the FieldNode's name has the same name and descriptor as the FieldNode that we're searching for.
            if(fieldName.equals(f.name) && fieldDescriptor.equals(f.desc)){

                //Toggle the isFieldPresent switch to add in the getter method for this field.
                isFieldPresent = true;

                //Mark down any generics of the FieldNode (highly unlikely to have any, so it will likely be null).
                signature = f.signature;

                //Check to see if the field is 'static'.
                if((f.access & ACC_STATIC) != 0){
                    //Toggle the isStatic boolean so that we are aware that the field is static. They must be handled differently in bytecode.
                    isStatic = true;
                }
                break;
            }
        }

        //Loop through the MethodNode to see if a getter method with the same name and descriptor exists.
        for(MethodNode mv: cn.methods){
            //Check to see if the MethodNode's name has the same name and descriptor as the MethodNode that we're searching for.
            if(getterName.equals(cn.name) && fieldDescriptor.equals(mv.desc)){
                //Toggle the isMethodPresent boolean so that we know that the getter method already exists.
                isMethodPresent = true;
                break;
            }
        }

        //Make sure that we add the getter method only if the wanted field is present and the getter method isn't present.
        if(isFieldPresent && !isMethodPresent){

            //Create the header of the getter method. It should be 'public' if you wish to access the getter method from any other class that has an instance of it.
            MethodNode mn = new MethodNode(ACC_PUBLIC, getterName, "()" + getterDesc, signature, null);

            //Add the ALOAD_0 instruction, which allows for any object.
            mn.instructions.add(new VarInsnNode(ALOAD, 0));

            //Add the GETSTATIC instruction if the field is 'static' or the GETFIELD instruction if it is an instance field.
            mn.instructions.add(new FieldInsnNode(isStatic ? GETSTATIC : GETFIELD, cn.name, fieldName, fieldDescriptor));

            //Add the appropriate return instruction.
            //TODO: Automatic calculation of the return instruction opcode.
            mn.instructions.add(new InsnNode(retInsn));

            //This must be called at all times, even with the COMPUTE_MAXS flag of a ClassWriter.
            //If COMPUTE_MAXS is called, it will still need to see this method call, however, the arguments will be ignored.
            mn.visitMaxs(3, 3);

            //This must always be called after any visitor is done 'visiting' instructions.
            mn.visitEnd();

            //Add the getter method to the list of methods of the ClassNode.
            cn.methods.add(mn);

            //Print out the changes.
            System.out.println("          [+M] " + fieldDescriptor + " " + getterName + "() identified as " +  cn.name + "." + fieldName);
        }

        try{
            //Produce the changes.
            cn.accept(next);
        }catch (Exception ez)  {
            ez.printStackTrace();
        }

    }

}
