/*
 * Fields.java
 *
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception,
 *                                needs constant pool during read and write
 * 1.02  12th Jun 1999   Tanmay   Methods to verify and get text summary added.
 * 1.03  05th Jul 1999   Tanmay   Method to delete/add a field introduced
 * 1.04  01st May 2001   Tanmay   Checking for number of fields before returning from getField
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.05  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 */

package classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;


/**
 * Class to handle Fields.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.05, 28th Sep, 2003
 */


public class Fields {
    int                     iFieldsCount;
    Vector                  fieldsVect;
    
    void read(DataInputStream dis, ConstantPool constantPool) throws IOException {
        int iIndex;
        
        iFieldsCount        = dis.readUnsignedShort();
        fieldsVect          = new Vector(iFieldsCount);
        for (iIndex=0; iIndex < iFieldsCount; iIndex++) {
            FieldInfo newInfo = new FieldInfo();
            newInfo.read(dis, constantPool);
            fieldsVect.addElement(newInfo);
        }
    }
    
    void write(DataOutputStream dos, ConstantPool constantPool) throws IOException {
        int iIndex;
        
        iFieldsCount = fieldsVect.size();
        dos.writeShort(iFieldsCount);
        for (iIndex=0; iIndex < iFieldsCount; iIndex++) {
            FieldInfo newInfo = (FieldInfo)fieldsVect.elementAt(iIndex);
            newInfo.write(dos, constantPool);
        }
    }
    
    public boolean verify(Vector vectVerifyErrors) {
        boolean bRet = true;
        for (int iIndex=0; iIndex < iFieldsCount; iIndex++) {
            FieldInfo newInfo = (FieldInfo)fieldsVect.elementAt(iIndex);
            bRet = (newInfo.verify("Field " + (iIndex+1) + "(" + newInfo.getFieldName() + ")",
            vectVerifyErrors) && bRet);
        }
        return bRet;
    }
    
    public String toString() {
        String sRetStr;
        String sNewLine = System.getProperty("line.separator");
        
        iFieldsCount = fieldsVect.size();
        sRetStr = "Fields count: " + iFieldsCount + sNewLine;
        for (int iIndex=0; iIndex < iFieldsCount; sRetStr += (fieldsVect.elementAt(iIndex++).toString()+sNewLine));
        return sRetStr;
    }
    
    public FieldInfo getField(int iIndex) {
        if(0 == iFieldsCount) return null;
        return (FieldInfo)fieldsVect.elementAt(iIndex);
    }
    
    public int getFieldsCount() {
        return iFieldsCount;
    }
    
    public void deleteField(int iIndex) {
        FieldInfo fldInfo = (FieldInfo)fieldsVect.elementAt(iIndex);
        fldInfo.removeReferences();
        fieldsVect.removeElementAt(iIndex);
        iFieldsCount--;
    }
    
    public void addField(FieldInfo newFld) {
        newFld.addReference();
        fieldsVect.addElement(newFld);
        iFieldsCount++;
    }
}
