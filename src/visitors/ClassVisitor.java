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
    void visitClass(ClassFile classFile);
    void visitAccessFlags(AccessFlags flags);
    void visitClassNames(ClassNames names);
    void visitConstantPool(ConstantPool pool);
    void visitConstantPoolInfo(ConstantPoolInfo poolInfo);
    void visitFields(Fields flds);
    void visitFieldInfo(FieldInfo fldInfo);
    void visitInterfaces(Interfaces interfaces);
    void visitMethods(Methods methods);
    void visitMethodInfo(MethodInfo methodInfo);
    void visitVersion(Version ver);
    void visitAttribute(Attribute attr);
    void visitAttributes(Attributes attrs);
    // Attribute visits
    void visitCodeAttribute(CodeAttribute codeattr);
    void visitCode(Code code);
    void visitConstantValueAttribute(ConstantValueAttribute constval);
    void visitDeprecatedAttribute(DeprecatedAttribute depr);
    void visitExceptionsAttribute(ExceptionsAttribute ex);
    void visitExceptionTableEntry(ExceptionTableEntry extab);
    void visitInnerClassesAttribute(InnerClassesAttribute classattr);
    void visitInnerClassInfo(InnerClassInfo innerclass);
    void visitInstruction(Instruction instr);
    void visitInstructions(Instructions instrs);
    void visitLineNumberTableAttribute(LineNumberTableAttribute linenumtab);
    void visitLineNumberTableEntry(LineNumberTableEntry linenumtabentry);
    void visitLocalVariableTableAttribute(LocalVariableTableAttribute lvtab);
    void visitLocalVariableTableEntry(LocalVariableTableEntry lvtabentry);
    void visitSourceFileAttribute(SourceFileAttribute src);
    void visitSyntheticAttribute(SyntheticAttribute synth);
    void visitUnknownAttribute(UnknownAttribute unknown);
}
