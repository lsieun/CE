/*
 * LocalVariableTableAttribute.java
 *
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  02nd Jun 1999   Tanmay   Original Version
 * 1.01  12th Jun 1999   Tanmay   Methods to verify and get text summary added.
 * 1.02  20th Jan 2002   Tanmay   Implemented Attribute interface
 * 1.03  11th Feb 2002   Tanmay   Used vector for storing local variables instead
 *                                  of difficult to manipulate arrays.
 * 1.04  16th Mar 2002   Tanmay   New methods for adding and deleting entries.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.05  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 *
 */

package classfile.attributes;

import classfile.Utils;
import classfile.ConstantPool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;


/**
 * Class to handle the local variable table attribute.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.05, 28th Sep, 2003
 */

public class LocalVariableTableAttribute extends Attribute {
    public Vector                   vectLocalVariableTable;
    private ConstantPool            constPool;
    
    public LocalVariableTableAttribute() {
        sName = Attribute.LOCAL_VARIABLE_TABLE;
    }
    
    void readAttributeDetails(DataInputStream dis, ConstantPool constPoolIn) throws IOException {
        constPool = constPoolIn;
        iAttribLength = dis.readInt();
        int iLocalVariableTableLength;
        vectLocalVariableTable = new Vector();
        if ((iLocalVariableTableLength = dis.readUnsignedShort()) > 0) {
            for (int iIndex=0; iIndex < iLocalVariableTableLength; iIndex++) {
                LocalVariableTableEntry thisEntry = new LocalVariableTableEntry();
                thisEntry.read(dis, constPool);
                thisEntry.addRef();
                vectLocalVariableTable.add(thisEntry);
            }
        }
    }
    
    void writeAttributeDetails(DataOutputStream dos, ConstantPool constPool) throws IOException {
        int iLocalVariableTableLength = vectLocalVariableTable.size();
        
        dos.writeInt(iAttribLength);
        dos.writeShort(iLocalVariableTableLength);
        if (iLocalVariableTableLength > 0) {
            for (int iIndex=0; iIndex < iLocalVariableTableLength; iIndex++) {
                LocalVariableTableEntry thisEntry = (LocalVariableTableEntry) vectLocalVariableTable.elementAt(iIndex);
                thisEntry.write(dos, constPool);
            }
        }
    }
    
    public void addEntry(LocalVariableTableEntry thisEntry) {
        thisEntry.constPool = constPool;
        thisEntry.iIndex = vectLocalVariableTable.size();
        vectLocalVariableTable.add(thisEntry);
        thisEntry.addRef();
    }
    
    public void deleteEntryAt(int iRow) {
        LocalVariableTableEntry thisEntry = (LocalVariableTableEntry) vectLocalVariableTable.elementAt(iRow);
        thisEntry.deleteRef();
        vectLocalVariableTable.removeElementAt(iRow);
    }
    
    public String toString() {
        int iLocalVariableTableLength = vectLocalVariableTable.size();
        
        return "Attribute " + sName + ". Length=" + iAttribLength +
        ". TableLength=" + iLocalVariableTableLength;
    }
    
    public boolean verify(String sPrepend, Vector vectVerifyErrors) {
        return true; // not implemented because local variable table cannot be changed now
    }
}
