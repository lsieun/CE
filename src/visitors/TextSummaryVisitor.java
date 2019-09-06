/*
 * TextSummaryVisitor.java
 *
 * Created on September 16, 2003, 10:48 PM
 *
 * Modification Log:
 * 1.00   28th Sep 2003   Tanmay   Original version.
 */

package visitors;

import classfile.*;
import classfile.attributes.*;

/**
 * A visitor class that generates a text summary for a class file.
 *
 * @author 	Tanmay K. Mohapatra
 * @version     1.00, 28th Sep, 2003
 */
public class TextSummaryVisitor extends NavigatingClassVisitor {
    
    StringBuffer summary = new java.lang.StringBuffer(4096);
    String sPrepend = "";
    int iConstantPoolIndex;
    int iFieldIndex;
    int iMethodIndex;
    boolean bNoCode;
    
    /** Creates a new instance of TextSummaryVisitor */
    public TextSummaryVisitor() {
        // default constructor
    }
    
    public TextSummaryVisitor(boolean bNoCodeIn) {
        bNoCode = bNoCodeIn;
    }
    
    public StringBuffer getSummary() {
        return summary;
    }
    
    public void visitAccessFlags(AccessFlags flags) {
        summary.append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Access flags").append(Utils.sNewLine).append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append(flags.toString()).append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitAccessFlags(flags);
        sPrepend = sPrependBak;
    }
    
    public void visitAttributes(Attributes attrs) {
        summary.append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Attributes");
        summary.append(sPrepend).append(Utils.sNewLine).append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Attributes count: ").append(attrs.iAttributesCount).append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitAttributes(attrs);
        sPrepend = sPrependBak;
    }
    
    public void visitClass(ClassFile classFile) {
        summary.append(sPrepend).append("Summary for class ").append(classFile.classNames.getThisClassName()).append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitClass(classFile);
        sPrepend = sPrependBak;
    }
    
    public void visitClassNames(ClassNames names) {
        summary.append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Class Names").append(Utils.sNewLine).append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("This class: ").append(names.getThisClassName()).append(Utils.sNewLine);
        summary.append(sPrepend).append("Super class: ").append(names.getSuperClassName()).append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitClassNames(names);
        sPrepend = sPrependBak;
    }
    
    public void visitCode(Code code) {
        if(bNoCode) return;
        
        int iCurrIndex, iCodeIndex;
        iCurrIndex=iCodeIndex=0;
        String sOneInstr;
        summary.append(sPrepend).append("------Begin Bytecode-------").append(Utils.sNewLine);
        while(null != (sOneInstr = code.getNextInstruction())) {
            summary.append(sPrepend).append(sOneInstr).append(Utils.sNewLine);
        }
        summary.append(sPrepend).append("------End Bytecode-------").append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitCode(code);
        sPrepend = sPrependBak;
    }
    
    public void visitCodeAttribute(CodeAttribute codeattr) {
        summary.append(sPrepend).append(codeattr.toString()).append(Utils.sNewLine);
        summary.append(sPrepend).append("Max stack: ").append(codeattr.iMaxStack);
        summary.append(sPrepend).append("Max locals: ").append(codeattr.iMaxLocals).append(Utils.sNewLine);
        if (0 < codeattr.vectExceptionTableEntries.size()) {
            summary.append(sPrepend).append("Exceptions Table").append(Utils.sNewLine);
            summary.append(sPrepend).append("\tCatch type : Start PC : End PC : Handler PC").append(Utils.sNewLine);
        }
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitCodeAttribute(codeattr);
        sPrepend = sPrependBak;
    }
    
    public void visitConstantPool(ConstantPool pool) {
        int iNumPoolInfos = pool.getPoolInfoCount();
        summary.append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Constant Pool").append(Utils.sNewLine).append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Constant pool count: ").append(iNumPoolInfos+1).append(Utils.sNewLine);
        
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        iConstantPoolIndex = 0;
        super.visitConstantPool(pool);
        sPrepend = sPrependBak;
    }
    
    public void visitConstantPoolInfo(ConstantPoolInfo poolInfo) {
        summary.append(sPrepend).append(iConstantPoolIndex+1).append(": ");
        summary.append(poolInfo.toString()).append(Utils.sNewLine);
        iConstantPoolIndex++;
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitConstantPoolInfo(poolInfo);
        sPrepend = sPrependBak;
    }
    
    public void visitConstantValueAttribute(ConstantValueAttribute constval) {
        summary.append(sPrepend).append("Attribute ").append(constval.sName).append(". Type=").append(constval.sConstType).append(". Value=").append(constval.sConstValue).append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitConstantValueAttribute(constval);
        sPrepend = sPrependBak;
    }
    
    public void visitDeprecatedAttribute(DeprecatedAttribute depr) {
        summary.append(sPrepend).append("Attribute ").append("Deprecated");
        summary.append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitDeprecatedAttribute(depr);
        sPrepend = sPrependBak;
    }
    
    public void visitExceptionTableEntry(ExceptionTableEntry extab) {
        summary.append(sPrepend).append(((null==extab.cpCatchType) ? "all" : Utils.convertClassStrToStr(extab.cpCatchType.refUTF8.sUTFStr))).append(" : ");
        summary.append(Integer.toString(extab.iStartPC)).append(" : ");
        summary.append(Integer.toString(extab.iEndPC)).append(" : ");
        summary.append(Integer.toString(extab.iHandlerPC)).append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitExceptionTableEntry(extab);
        sPrepend = sPrependBak;
    }
    
    public void visitExceptionsAttribute(ExceptionsAttribute ex) {
        summary.append(sPrepend).append("Attribute ").append(ex.sName).append(". Number=").append(ex.iNumExceptions);
        for (int iIndex=0; iIndex < ex.iNumExceptions; iIndex++) {
            ConstantPoolInfo thisExcept = (ConstantPoolInfo) ex.vectExceptionTypes.elementAt(iIndex);
            summary.append(", ").append(Utils.convertClassStrToStr(thisExcept.refUTF8.sUTFStr));
        }
        summary.append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitExceptionsAttribute(ex);
        sPrepend = sPrependBak;
    }
    
    public void visitFieldInfo(FieldInfo fldInfo) {
        iFieldIndex++;
        summary.append(sPrepend).append(iFieldIndex).append(": ");
        summary.append(fldInfo.accessFlags.toString()).append(" ");
        summary.append(fldInfo.getFieldDescriptor()).append(" ").append(fldInfo.getFieldName()).append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitFieldInfo(fldInfo);
        sPrepend = sPrependBak;
    }
    
    public void visitFields(Fields flds) {
        summary.append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Fields").append(Utils.sNewLine).append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        int iFieldsCount = flds.getFieldsCount();
        summary.append(sPrepend).append("Number of fields: ").append(iFieldsCount).append(Utils.sNewLine);
        iFieldIndex = 0;
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitFields(flds);
        sPrepend = sPrependBak;
    }
    
    public void visitInnerClassInfo(InnerClassInfo innerclass) {
        if (null != innerclass.cpInnerClass) {
            summary.append(Utils.sNewLine);
            summary.append(sPrepend).append("InnerClass: ").append(Utils.convertClassStrToStr(innerclass.cpInnerClass.refUTF8.sUTFStr));
        }
        if (null != innerclass.cpOuterClass) {
            summary.append(Utils.sNewLine);
            summary.append(sPrepend).append("OuterClass: ").append(Utils.convertClassStrToStr(innerclass.cpOuterClass.refUTF8.sUTFStr));
        }
        if (null != innerclass.cpInnerName) {
            summary.append(Utils.sNewLine);
            summary.append(sPrepend).append("Name: ").append(innerclass.cpInnerName.sUTFStr);
        }
        summary.append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitInnerClassInfo(innerclass);
        sPrepend = sPrependBak;
    }
    
    public void visitInnerClassesAttribute(InnerClassesAttribute classattr) {
        summary.append(sPrepend).append("Attribute ").append("InnerClasses ").append(". Number=").append(classattr.getNumClasses());
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitInnerClassesAttribute(classattr);
        sPrepend = sPrependBak;
        summary.append(Utils.sNewLine);
    }
    
    public void visitInstruction(Instruction instr) {
    }
    
    public void visitInstructions(Instructions instrs) {
    }
    
    public void visitInterfaces(Interfaces interfaces) {
        int iInterfacesCount = interfaces.getInterfacesCount();
        summary.append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Interfaces").append(Utils.sNewLine).append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Interfaces count: ").append(iInterfacesCount).append(Utils.sNewLine);
        for (int iIndex=0; iIndex < iInterfacesCount; iIndex++) {
            summary.append(sPrepend).append(iIndex+1).append(": ").append(interfaces.getInterfaceName(iIndex)).append(Utils.sNewLine);
        }
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitInterfaces(interfaces);
        sPrepend = sPrependBak;
    }
    
    public void visitLineNumberTableAttribute(LineNumberTableAttribute linenumtab) {
        int iSize = linenumtab.vectEntries.size();
        summary.append(sPrepend).append("Attribute ").append(linenumtab.sName);
        summary.append(". TableLength=").append(iSize).append(Utils.sNewLine);
        summary.append(sPrepend).append("\tLine : Start Program Counter").append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitLineNumberTableAttribute(linenumtab);
        sPrepend = sPrependBak;
    }
    
    public void visitLineNumberTableEntry(LineNumberTableEntry linenumtabentry) {
        summary.append(sPrepend).append(Integer.toString(linenumtabentry.iLineNum)).append(" : ").append(Integer.toString(linenumtabentry.iStartPC)).append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitLineNumberTableEntry(linenumtabentry);
        sPrepend = sPrependBak;
    }
    
    public void visitLocalVariableTableAttribute(LocalVariableTableAttribute lvtab) {
        summary.append(sPrepend).append("Attribute ").append(lvtab.sName);
        summary.append(". TableLength=").append(lvtab.vectLocalVariableTable.size()).append(Utils.sNewLine);
        summary.append(sPrepend).append("\tPosition : Start PC : Length : Descriptor : Name").append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitLocalVariableTableAttribute(lvtab);
        sPrepend = sPrependBak;
    }
    
    public void visitLocalVariableTableEntry(LocalVariableTableEntry lvtabentry) {
        summary.append(sPrepend);
        summary.append(Integer.toString(lvtabentry.iIndex)).append(" : ");
        summary.append(Integer.toString(lvtabentry.iStartPC)).append(" : ");
        summary.append(Integer.toString(lvtabentry.iLength)).append(" : ");
        if(null != lvtabentry.cpDescriptor) {
            summary.append(Utils.getReadableDesc(lvtabentry.cpDescriptor.sUTFStr)).append(" : ");
        }
        else {
            summary.append("null (possibly obfuscated) : ");
        }
        if(null != lvtabentry.cpName) {
            summary.append(lvtabentry.cpName.sUTFStr);
        }
        else {
            summary.append("null (possibly obfuscated)");
        }
        summary.append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitLocalVariableTableEntry(lvtabentry);
        sPrepend = sPrependBak;
    }
    
    public void visitMethodInfo(MethodInfo methodInfo) {
        iMethodIndex++;
        summary.append(sPrepend).append(iMethodIndex).append(": ");
        int iIndex;
        
        summary.append(methodInfo.accessFlags).append(" ");
        String sDesc[] = Utils.getReadableMethodDesc(methodInfo.cpDescriptor.sUTFStr);
        if (sDesc.length > 0) {
            summary.append(sDesc[0]).append(" ");
        }
        summary.append(methodInfo.cpName.sUTFStr).append("(");
        for (int iIndex2=1; iIndex2 < sDesc.length; iIndex2++) {
            summary.append(sDesc[iIndex2]);
            if(iIndex2+1 < sDesc.length) {
                summary.append(", ");
            }
        }
        summary.append(")");
        
        summary.append(Utils.sNewLine).append(sPrepend).append("{").append(Utils.sNewLine);
        
        // append attributes
        summary.append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Attributes");
        summary.append(sPrepend).append(Utils.sNewLine).append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Attributes count: ").append(methodInfo.attributes.iAttributesCount).append(Utils.sNewLine);
        
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitMethodInfo(methodInfo);
        sPrepend = sPrependBak;
        
        summary.append(sPrepend).append("}").append(Utils.sNewLine);
        summary.append(Utils.sNewLine);
    }
    
    public void visitMethods(Methods methods) {
        summary.append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Methods").append(Utils.sNewLine).append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        int iMethodsCount = methods.getMethodsCount();
        summary.append(sPrepend).append("Methods count: ").append(iMethodsCount).append(Utils.sNewLine);
        iMethodIndex = 0;
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitMethods(methods);
        sPrepend = sPrependBak;
    }
    
    public void visitSourceFileAttribute(SourceFileAttribute src) {
        summary.append(sPrepend).append("Attribute ").append(src.sName).append(". Source=").append(src.cpSourceFile.sUTFStr).append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitSourceFileAttribute(src);
        sPrepend = sPrependBak;
    }
    
    public void visitSyntheticAttribute(SyntheticAttribute synth) {
        summary.append(sPrepend).append("Attribute ").append("Synthetic");
        summary.append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitSyntheticAttribute(synth);
        sPrepend = sPrependBak;
    }
    
    public void visitUnknownAttribute(UnknownAttribute unknown) {
        summary.append(sPrepend).append("Attribute ").append(unknown.sName);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitUnknownAttribute(unknown);
        sPrepend = sPrependBak;
    }
    
    public void visitVersion(Version ver) {
        summary.append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Java Version Information").append(Utils.sNewLine).append(sPrepend).append(Utils.sUnderLine).append(Utils.sNewLine);
        summary.append(sPrepend).append("Magic number: ").append(ver.getMagicNumberString()).append(Utils.sNewLine);
        summary.append(sPrepend).append("Minor version: ").append(ver.getMinorVersionString()).append(Utils.sNewLine);
        summary.append(sPrepend).append("Major version: ").append(ver.getMajorVersionString()).append(Utils.sNewLine);
        String sPrependBak = sPrepend;
        sPrepend += "\t";
        super.visitVersion(ver);
        sPrepend = sPrependBak;
    }
}
