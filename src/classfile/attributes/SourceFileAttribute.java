/*
 * SourceFileAttribute.java
 *
 * Created on June 2nd, 1999
 *
 * Modification Log:
 * 1.00  02nd Jun 1999   Tanmay   Original Version
 * 1.01  12th Jun 1999   Tanmay   Methods to verify and get text summary added.
 * 1.02  20th Jan 2002   Tanmay   Implemented Attribute interface
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.03  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 */

package classfile.attributes;

import classfile.Utils;
import classfile.ConstantPoolInfo;
import classfile.ConstantPool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;


/**
 * Class to handle the source file attribute.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.03, 28th Sep, 2003
 */

public class SourceFileAttribute extends Attribute {
    int                         iSourceFileIndex; // points to a UTF8
    public ConstantPoolInfo     cpSourceFile;     // is a UTF8
    
    public SourceFileAttribute() {
        sName = Attribute.SOURCE_FILE;
    }
    
    void readAttributeDetails(DataInputStream dis, ConstantPool constPool) throws IOException {
        iAttribLength      = dis.readInt();
        iSourceFileIndex   = dis.readUnsignedShort();
        cpSourceFile = constPool.getPoolInfo(iSourceFileIndex);
        cpSourceFile.addRef();
    }
    
    void writeAttributeDetails(DataOutputStream dos, ConstantPool constPool) throws IOException {
        dos.writeInt(iAttribLength);
        iSourceFileIndex = constPool.getIndexOf(cpSourceFile);
        dos.writeShort(iSourceFileIndex);
    }
    
    public String toString() {
        return "Attribute " + sName + ". Source=" + cpSourceFile.sUTFStr;
    }
    
    public boolean verify(String sPrepend, Vector vectVerifyErrors) {
        if ( (null == cpSourceFile) || (ConstantPoolInfo.CONSTANT_Utf8 != cpSourceFile.iTag) ) {
            vectVerifyErrors.addElement(sPrepend + ": Should point to a UTF8 constant pool.");
            return false;
        }
        return true;
    }
}
