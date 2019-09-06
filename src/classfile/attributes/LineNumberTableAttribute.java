/*
 * LineNumberTableAttribute.java
 *
 * Created on June 2nd, 1999
 *
 * Modification Log:
 * 1.00  02nd Jun 1999   Tanmay  Original Version
 * 1.01  12th Jun 1999   Tanmay  Methods to verify and get text summary added.
 * 1.02  20th Jan 2002   Tanmay  Implemented Attribute interface
 * 1.03  05th Feb 2002   Tanmay  Used vector for storing line numbers instead
 *                                  of difficult to manipulate arrays. New
 *                                   methods to add and delete table entries.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.04  28th Sep 2003   Tanmay   Moved text summary method to visitor.
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
 * Class to handle the line number table attribute.
 * This attribute is an attribute of code attribute.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.04, 28th Sep, 2003
 */

public class LineNumberTableAttribute extends Attribute {
    public Vector                   vectEntries;
    
    public LineNumberTableAttribute() {
        sName = Attribute.LINE_NUMBER_TABLE;
    }
    
    void readAttributeDetails(DataInputStream dis, ConstantPool constPool) throws IOException {
        iAttribLength      = dis.readInt();
        vectEntries = new Vector();
        int iLineNumberTableLength;
        if ((iLineNumberTableLength = dis.readUnsignedShort()) > 0) {
            for (int iIndex=0; iIndex < iLineNumberTableLength; iIndex++) {
                LineNumberTableEntry entry = new LineNumberTableEntry();
                entry.read(dis);
                vectEntries.addElement(entry);
            }
        }
    }
    
    void writeAttributeDetails(DataOutputStream dos, ConstantPool constPool) throws IOException {
        dos.writeInt(iAttribLength);
        int iSize = vectEntries.size();
        dos.writeShort(iSize);
        if (iSize > 0) {
            for (int iIndex=0; iIndex < iSize; iIndex++) {
                ((LineNumberTableEntry)vectEntries.elementAt(iIndex)).write(dos);
            }
        }
    }
    
    public void addNewEntry() {
        vectEntries.addElement(new LineNumberTableEntry());
    }
    
    public void deleteEntryAt(int iIndexToDelete) {
        vectEntries.removeElementAt(iIndexToDelete);
    }
    
    public String toString() {
        return "Attribute " + sName + ". Length=" + iAttribLength +
        ". TableLength=" + vectEntries.size();
    }
    
    public boolean verify(String sPrepend, Vector vectVerifyErrors) {
        return true; // not implemented because line number table cannot be changed now
    }
}
