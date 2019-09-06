/*
 * ClassVisitor.java
 *
 * Created on September 16, 2003, 10:46 PM
 *
 * Modification Log:
 * 1.00   28th Sep 2003   Tanmay   Original version.
 */

package visitors;

import classfile.*;
import classfile.attributes.*;
/**
 * Visitor pattern for visiting the class file structure.
 *
 * @author 	Tanmay K. Mohapatra
 * @version     1.00, 28th Sep, 2003
 */
public interface ClassVisitor {
    public void visitClass(ClassFile classFile);
    public void visitAccessFlags(AccessFlags flags);
    public void visitClassNames(ClassNames names);
    public void visitConstantPool(ConstantPool pool);
    public void visitConstantPoolInfo(ConstantPoolInfo poolInfo);
    public void visitFields(Fields flds);
    public void visitFieldInfo(FieldInfo fldInfo);
    public void visitInterfaces(Interfaces interfaces);
    public void visitMethods(Methods methods);
    public void visitMethodInfo(MethodInfo methodInfo);
    public void visitVersion(Version ver);
    public void visitAttribute(Attribute attr);
    public void visitAttributes(Attributes attrs);
    // Attribute visits    
    public void visitCodeAttribute(CodeAttribute codeattr);
    public void visitCode(Code code);
    public void visitConstantValueAttribute(ConstantValueAttribute constval);
    public void visitDeprecatedAttribute(DeprecatedAttribute depr);
    public void visitExceptionsAttribute(ExceptionsAttribute ex);
    public void visitExceptionTableEntry(ExceptionTableEntry extab);
    public void visitInnerClassesAttribute(InnerClassesAttribute classattr);
    public void visitInnerClassInfo(InnerClassInfo innerclass);
    public void visitInstruction(Instruction instr);
    public void visitInstructions(Instructions instrs);    
    public void visitLineNumberTableAttribute(LineNumberTableAttribute linenumtab);
    public void visitLineNumberTableEntry(LineNumberTableEntry linenumtabentry);
    public void visitLocalVariableTableAttribute(LocalVariableTableAttribute lvtab);
    public void visitLocalVariableTableEntry(LocalVariableTableEntry lvtabentry);
    public void visitSourceFileAttribute(SourceFileAttribute src);
    public void visitSyntheticAttribute(SyntheticAttribute synth);
    public void visitUnknownAttribute(UnknownAttribute unknown);
}
