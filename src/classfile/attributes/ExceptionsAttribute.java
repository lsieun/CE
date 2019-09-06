/*
 * ExceptionsAttribute.java
 *
 * Created on June 2nd, 1999
 *
 * Modification Log:
 * 1.00  02nd Jun 1999   Tanmay   Original Version
 * 1.01  12th Jun 1999   Tanmay   Methods to verify and get summary text added.
 * 1.02  20th Jan 2002   Tanmay   Implemented Attribute interface
 * 1.03  05th May 2002   Tanmay   Made vector of exceptions and number of exceptions public
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.04  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 *
 */

package classfile.attributes;

import classfile.Utils;
import classfile.ConstantPool;
import classfile.ConstantPoolInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;


/**
 * Class to handle the exceptions attribute.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.04, 28th Sep, 2003
 */

public class ExceptionsAttribute extends Attribute {
    public int      iNumExceptions;
    public Vector   vectExceptionTypes; // each is a ConstantPoolInfo that points to a Class
    
    public ExceptionsAttribute() {
        sName = Attribute.EXCEPTIONS;
    }
    
    void readAttributeDetails(DataInputStream dis, ConstantPool constPool) throws IOException {
        iAttribLength      = dis.readInt();
        if ((iNumExceptions = dis.readUnsignedShort()) > 0) {
            vectExceptionTypes      = new Vector(iNumExceptions);
        }
        for (int iIndex=0; iIndex < iNumExceptions; iIndex++) {
            int iExceptClassInfoIndex = dis.readUnsignedShort();
            ConstantPoolInfo thisExcept = constPool.getPoolInfo(iExceptClassInfoIndex);
            thisExcept.addRef();
            vectExceptionTypes.addElement(thisExcept);
        }
    }
    
    void writeAttributeDetails(DataOutputStream dos, ConstantPool constPool) throws IOException {
        dos.writeInt(iAttribLength); // will change if we allow modification
        iNumExceptions = vectExceptionTypes.size();
        dos.writeShort(iNumExceptions);
        for (int iIndex=0; iIndex < iNumExceptions; iIndex++) {
            ConstantPoolInfo thisExcept = (ConstantPoolInfo) vectExceptionTypes.elementAt(iIndex);
            int iExceptClassInfoIndex = constPool.getIndexOf(thisExcept);
            dos.writeShort(iExceptClassInfoIndex);
        }
    }
    
    public void deleteExceptionAt(int iIndex) {
        if (iIndex >= iNumExceptions) return;
        ConstantPoolInfo thisExcept = (ConstantPoolInfo) vectExceptionTypes.elementAt(iIndex);
        thisExcept.deleteRef();
        iNumExceptions--;
        vectExceptionTypes.removeElementAt(iIndex);
    }
    
    public int getExceptionCount() {
        return iNumExceptions;
    }
    
    public String [] getExceptionNames() {
        String [] retArr = null;
        if (iNumExceptions > 0) {
            retArr = new String[iNumExceptions];
            for (int iIndex=0; iIndex < iNumExceptions; iIndex++) {
                ConstantPoolInfo thisExcept = (ConstantPoolInfo) vectExceptionTypes.elementAt(iIndex);
                retArr[iIndex] = thisExcept.refUTF8.sUTFStr;
            }
        }
        return retArr;
    }
    
    public boolean verify(String sPrepend, Vector vectVerifyErrors) {
        boolean bRet = true;
        if (iNumExceptions > 0) {
            for (int iIndex=0; iIndex < iNumExceptions; iIndex++) {
                ConstantPoolInfo thisExcept = (ConstantPoolInfo) vectExceptionTypes.elementAt(iIndex);
                if (ConstantPoolInfo.CONSTANT_Class != thisExcept.iTag) {
                    vectVerifyErrors.addElement(": Exception " + (iIndex+1) + " must point to a constant pool of type Class.");
                    bRet = false;
                }
            }
        }
        return bRet;
    }
    
    public String toString() {
        String sRetStr = "Attribute " + sName + ". Number=" + iNumExceptions;
        return sRetStr;
    }
}
