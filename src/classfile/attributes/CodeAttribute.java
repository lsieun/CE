/*
 * CodeAttribute.java
 *
 * Created on June 2nd, 1999
 *
 * Modification Log:
 * 1.00  02nd Jun 1999   Tanmay   Original Version
 * 1.01  12th Jun 1999   Tanmay   Methods to verify and get text summary added.
 * 1.02  20th Jan 2002   Tanmay   Implemented Attribute interface
 * 1.03  23rd Mar 2002   Tanmay   Moved exception table entries from array to vector
 * 1.04  01st May 2002   Tanmay   Added methods to add and delete exception table entries
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.05  28th Sep 2003   Tanmay   Moved text summary method to visitor.
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
 * Class to handle the code attribute.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.05, 28th Sep, 2003
 */

public class CodeAttribute extends Attribute {
    public static final String      NAME = "Code";
    int                             iAttribLength;
    public int                      iMaxStack, iMaxLocals;
    public Code                     code;
    public Vector                   vectExceptionTableEntries;
    public Attributes               codeAttributes;
    
    public CodeAttribute() {
        sName = Attribute.CODE;
    }
    
    void readAttributeDetails(DataInputStream dis, ConstantPool constPool) throws IOException {
        int iExceptTableLength;
        
        iAttribLength   = dis.readInt();
        iMaxStack       = dis.readUnsignedShort();
        iMaxLocals      = dis.readUnsignedShort();
        code            = new Code();
        code.read(dis, constPool);
        vectExceptionTableEntries = new Vector();
        iExceptTableLength = dis.readUnsignedShort();
        
        for (int iIndex=0; iIndex < iExceptTableLength; iIndex++) {
            ExceptionTableEntry thisEntry = new ExceptionTableEntry();
            thisEntry.read(dis, constPool);
            vectExceptionTableEntries.add(thisEntry);
        }
        codeAttributes = new Attributes();
        codeAttributes.read(dis, constPool);
    }
    
    void writeAttributeDetails(DataOutputStream dos, ConstantPool constPool) throws IOException {
        int iExceptTableLength;
        
        dos.writeInt(iAttribLength); // will change if we allow modification
        dos.writeShort(iMaxStack);
        dos.writeShort(iMaxLocals);
        code.write(dos, constPool);
        iExceptTableLength = vectExceptionTableEntries.size();
        dos.writeShort(iExceptTableLength);
        for (int iIndex=0; iIndex < iExceptTableLength; iIndex++) {
            ExceptionTableEntry thisEntry = (ExceptionTableEntry)vectExceptionTableEntries.elementAt(iIndex);
            thisEntry.write(dos, constPool);
        }
        codeAttributes.write(dos, constPool);
    }
    
    public boolean verify(String sPrepend, Vector vectVerifyErrors) {
        return true; // not implemented yet because code cannot be changed
    }
    
    public String toString() {
        return "Attribute " + sName + ". Length=" + iAttribLength;
    }
    
    public void addNewExceptionTableEntry() {
        ExceptionTableEntry thisEntry = new ExceptionTableEntry();
        vectExceptionTableEntries.add(thisEntry);
    }
    
    public void deleteExceptionTableEntryAt(int iIndex) {
        ExceptionTableEntry thisEntry = (ExceptionTableEntry)vectExceptionTableEntries.elementAt(iIndex);
        if(null == thisEntry) return;
        if(null != thisEntry.cpCatchType) thisEntry.cpCatchType.deleteRef();
        vectExceptionTableEntries.removeElementAt(iIndex);
    }
}
