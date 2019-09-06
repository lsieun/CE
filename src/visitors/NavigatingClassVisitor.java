/*
 * NavigatingClassVisitor.java
 *
 * Created on September 23, 2003, 10:47 PM
 *
 * Modification Log:
 * 1.00   28th Sep 2003   Tanmay   Original version.
 */

package visitors;


import classfile.*;
import classfile.attributes.*;

/**
 * Visitor pattern with one type of navigation in-built. Other visitor patterns
 * wishing to follow this navigation need to inherit from this and call the
 * super methods at appropriate places. See TextSummaryVisitor for an example.
 *
 * @author 	Tanmay K. Mohapatra
 * @version     1.00, 28th Sep, 2003
 */
abstract class NavigatingClassVisitor implements ClassVisitor {
    
    public void visitAccessFlags(AccessFlags flags) {
        return; // no sub levels
    }
    
    public void visitAttribute(Attribute attr) {
        if(attr instanceof CodeAttribute) {
            visitCodeAttribute((CodeAttribute)attr);
        }
        else if(attr instanceof ConstantValueAttribute) {
            visitConstantValueAttribute((ConstantValueAttribute)attr);
        }
        else if(attr instanceof DeprecatedAttribute) {
            visitDeprecatedAttribute((DeprecatedAttribute)attr);
        }
        else if(attr instanceof ExceptionsAttribute) {
            visitExceptionsAttribute((ExceptionsAttribute)attr);
        }
        else if(attr instanceof InnerClassesAttribute) {
            visitInnerClassesAttribute((InnerClassesAttribute)attr);
        }
        else if(attr instanceof LineNumberTableAttribute) {
            visitLineNumberTableAttribute((LineNumberTableAttribute)attr);
        }
        else if(attr instanceof LocalVariableTableAttribute) {
            visitLocalVariableTableAttribute((LocalVariableTableAttribute)attr);
        }
        else if(attr instanceof SourceFileAttribute) {
            visitSourceFileAttribute((SourceFileAttribute)attr);
        }
        else if(attr instanceof SyntheticAttribute) {
            visitSyntheticAttribute((SyntheticAttribute)attr);
        }
        else if(attr instanceof UnknownAttribute) {
            visitUnknownAttribute((UnknownAttribute)attr);
        }
    }
    
    public void visitAttributes(Attributes attrs) {
        for (int iIndex=0; iIndex < attrs.iAttributesCount; iIndex++) {
            visitAttribute((Attribute)attrs.attribVect.elementAt(iIndex));
        }
    }
    
    public void visitClass(ClassFile classFile) {
        visitVersion(classFile.version);
        visitAccessFlags(classFile.accessFlags);
        visitConstantPool(classFile.constantPool);
        visitClassNames(classFile.classNames);
        visitAttributes(classFile.attributes);
        visitInterfaces(classFile.interfaces);
        visitFields(classFile.fields);
        visitMethods(classFile.methods);
    }
    
    public void visitClassNames(ClassNames names) {
        return; // no sub levels
    }
    
    public void visitCode(Code code) {
        return; // no sub levels
    }
    
    public void visitCodeAttribute(CodeAttribute codeattr) {
        int iExceptTableLength = codeattr.vectExceptionTableEntries.size();
        if (0 < iExceptTableLength) {
            for (int iIndex=0; iIndex < iExceptTableLength; iIndex++) {
                visitExceptionTableEntry((ExceptionTableEntry)codeattr.vectExceptionTableEntries.elementAt(iIndex));
            }
        }
        visitCode(codeattr.code);
        visitAttributes(codeattr.codeAttributes);
    }
    
    public void visitConstantPool(ConstantPool pool) {
        int iNumPoolInfos = pool.getPoolInfoCount();
        for (int iIndex=0; iIndex < iNumPoolInfos; iIndex++) {
            ConstantPoolInfo newInfo = pool.getPoolInfo(iIndex+1);
            visitConstantPoolInfo(newInfo);
            if (newInfo.isDoubleSizeConst()) {
                iIndex++;
            }
        }
    }
    
    public void visitConstantPoolInfo(ConstantPoolInfo poolInfo) {
        return; // no sub levels
    }
    
    public void visitConstantValueAttribute(ConstantValueAttribute constval) {
        return; // no sub levels
    }
    
    public void visitDeprecatedAttribute(DeprecatedAttribute depr) {
        return; // no sub levels
    }
    
    public void visitExceptionTableEntry(ExceptionTableEntry extab) {
        return; // no sub levels
    }
    
    public void visitExceptionsAttribute(ExceptionsAttribute ex) {
        return; // no sub levels
    }
    
    public void visitFieldInfo(FieldInfo fldInfo) {
        return; // no sub levels
    }
    
    public void visitFields(Fields flds) {
        int iFieldsCount = flds.getFieldsCount();
        for (int iIndex=0; iIndex < iFieldsCount; iIndex++) {
            visitFieldInfo(flds.getField(iIndex));
        }
    }
    
    public void visitInnerClassInfo(InnerClassInfo innerclass) {
        visitAccessFlags(innerclass.accFlags);
    }
    
    public void visitInnerClassesAttribute(InnerClassesAttribute classattr) {
        for (int iIndex=0; iIndex < classattr.getNumClasses(); iIndex++) {
            visitInnerClassInfo(classattr.getInnerClassInfo(iIndex));
        }
    }
    
    public void visitInstruction(Instruction instr) {
    }
    
    public void visitInstructions(Instructions instrs) {
    }
    
    public void visitInterfaces(Interfaces interfaces) {
        return; // no sub levels
    }
    
    public void visitLineNumberTableAttribute(LineNumberTableAttribute linenumtab) {
        int iSize = linenumtab.vectEntries.size();
        if (iSize > 0) {
            for (int iIndex=0; iIndex < iSize; iIndex++) {
                visitLineNumberTableEntry((LineNumberTableEntry)linenumtab.vectEntries.elementAt(iIndex));
            }
        }
    }
    
    public void visitLineNumberTableEntry(LineNumberTableEntry linenumtabentry) {
        return; // no sub levels
    }
    
    public void visitLocalVariableTableAttribute(LocalVariableTableAttribute lvtab) {
        int iLocalVariableTableLength = lvtab.vectLocalVariableTable.size();
        if (iLocalVariableTableLength > 0) {
            for (int iIndex=0; iIndex < iLocalVariableTableLength; iIndex++) {
                visitLocalVariableTableEntry((LocalVariableTableEntry)lvtab.vectLocalVariableTable.elementAt(iIndex));
            }
        }
    }
    
    public void visitLocalVariableTableEntry(LocalVariableTableEntry lvtabentry) {
        return; // no sub levels
    }
    
    public void visitMethodInfo(MethodInfo methodInfo) {
        visitAttributes(methodInfo.attributes);
    }
    
    public void visitMethods(Methods methods) {
        int iMethodsCount = methods.getMethodsCount();
        for (int iIndex=0; iIndex < iMethodsCount; iIndex++) {
            visitMethodInfo(methods.getMethod(iIndex));
        }
    }
    
    public void visitSourceFileAttribute(SourceFileAttribute src) {
        return; // no sub levels
    }
    
    public void visitSyntheticAttribute(SyntheticAttribute synth) {
        return; // no sub levels
    }
    
    public void visitUnknownAttribute(UnknownAttribute unknown) {
        return; // no sub levels
    }
    
    public void visitVersion(Version ver) {
        return; // no sub levels
    }
}
