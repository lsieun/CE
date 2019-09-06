/*
 * InnerClassInfo.java
 *
 * Created on February 2, 2002, 2:12 PM
 *
 * Modification Log:
 * 1.00  02nd Feb 2002   Tanmay   Original version.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.01  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 */

package classfile.attributes;

import classfile.AccessFlags;
import classfile.ConstantPoolInfo;
import classfile.ConstantPool;
import classfile.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Class to store inner class attributes details.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.01, 28th Sep, 2003
 */
public class InnerClassInfo {
    int                 iInnerClassInfoIndex, iOuterClassInfoIndex, iInnerNameIndex;
    public AccessFlags         accFlags;
    public ConstantPoolInfo    cpInnerClass, cpOuterClass;
    public ConstantPoolInfo    cpInnerName;
    
    public InnerClassInfo() {
        accFlags = new AccessFlags();
    }
    
    void read(DataInputStream dis, ConstantPool constPool) throws IOException {
        iInnerClassInfoIndex = dis.readUnsignedShort();
        iOuterClassInfoIndex = dis.readUnsignedShort();
        iInnerNameIndex = dis.readUnsignedShort();
        accFlags.read(dis);
        
        cpInnerClass = cpOuterClass = cpInnerName = null;
        if(0 != iInnerClassInfoIndex) cpInnerClass = constPool.getPoolInfo(iInnerClassInfoIndex);
        if(0 != iOuterClassInfoIndex) cpOuterClass = constPool.getPoolInfo(iOuterClassInfoIndex);
        if(0 != iInnerNameIndex) cpInnerName = constPool.getPoolInfo(iInnerNameIndex);
    }
    
    void write(DataOutputStream dos, ConstantPool constPool) throws IOException {
        iInnerClassInfoIndex = iOuterClassInfoIndex = iInnerNameIndex = 0;
        
        if(null != cpInnerClass) iInnerClassInfoIndex = constPool.getIndexOf(cpInnerClass);
        if(null != cpOuterClass) iOuterClassInfoIndex = constPool.getIndexOf(cpOuterClass);
        if(null != cpInnerName) iInnerNameIndex = constPool.getIndexOf(cpInnerName);
        
        dos.writeShort(iInnerClassInfoIndex);
        dos.writeShort(iOuterClassInfoIndex);
        dos.writeShort(iInnerNameIndex);
        accFlags.write(dos);
    }
    
    boolean verify(String sPrepend, Vector vectVerifyErrors) {
        boolean bRet = true;
        
        if ((null != cpInnerClass) && (ConstantPoolInfo.CONSTANT_Class != cpInnerClass.iTag) ) {
            vectVerifyErrors.addElement(": InnerClassInfoIndex must point to a constant pool of type Class.");
            bRet = false;
        }
        
        if ((null != cpOuterClass) && (ConstantPoolInfo.CONSTANT_Class != cpOuterClass.iTag) ) {
            vectVerifyErrors.addElement(": OuterClassInfoIndex must point to a constant pool of type Class.");
            bRet = false;
        }
        
        if ((null != cpInnerName) && (ConstantPoolInfo.CONSTANT_Utf8 != cpInnerName.iTag) ) {
            vectVerifyErrors.addElement(": InnerNameIndex must point to a constant pool of type UTF8.");
            bRet = false;
        }
        return bRet;
    }
    
    public String toString() {
        String sRet="";
        
        if (null != cpInnerClass) {
            sRet += ("inner_class=" + Utils.convertClassStrToStr(cpInnerClass.refUTF8.sUTFStr) + ",");
        }
        if (null != cpOuterClass) {
            sRet += ("outer_class=" + Utils.convertClassStrToStr(cpOuterClass.refUTF8.sUTFStr) + ",");
        }
        if (null != cpInnerName) {
            sRet += ("name=" + cpInnerName.sUTFStr + ",");
        }
        sRet += accFlags.toString();
        return sRet;
    }
}
