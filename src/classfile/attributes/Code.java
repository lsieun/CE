/*
 * Code.java
 *
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay  Original version.
 * 1.01  12th Jun 1999   Tanmay  Methods to verify and get text summary added.
 * 1.02  11th May 2002   TAnmay  Made members public for use in view
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.03  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 */

package classfile.attributes;

import classfile.Utils;
import classfile.ConstantPoolInfo;
import classfile.ConstantPool;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Class to go through the code array and return sCodeIndex + a string representation of
 * the code one instruction per call of method getNextInstruction.
 * <p>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.03, 28th Sep, 2003
 */

public class Code {
    public int             iCodeLength;
    public int             iCurrIndex;
    public int             iCodeIndex;
    public Vector          vectCode;
    
    void read(DataInputStream dis, ConstantPool constPool) throws IOException {
        iCodeLength = dis.readInt();
        
        vectCode = new Vector();
        
        for (int iIndex=0; iIndex < iCodeLength; ) {
            Instruction thisInstr = new Instruction();
            thisInstr.readInstruction(dis, constPool, iIndex);
            vectCode.addElement(thisInstr);
            iIndex += (thisInstr.iDataLength + 1);
        }
        iCurrIndex = 0;
        iCodeIndex = 0;
    }
    
    void write(DataOutputStream dos, ConstantPool constPool) throws IOException {
        dos.writeInt(iCodeLength);
        
        for (int iIndex=0; iIndex < vectCode.size(); iIndex++) {
            Instruction thisInstr = (Instruction) vectCode.elementAt(iIndex);
            thisInstr.writeInstruction(dos, constPool);
        }
    }
    
    public String getNextInstruction() {
        if (iCurrIndex >= vectCode.size()) {
            iCurrIndex = 0; // reset
            iCodeIndex = 0;
            return null;
        }
        
        Instruction thisInstr = (Instruction) vectCode.elementAt(iCurrIndex);
        iCurrIndex++;
        iCodeIndex += (thisInstr.iDataLength+1);
        return Integer.toString(iCodeIndex-thisInstr.iDataLength-1) + " " + thisInstr.toString();
    }
    
    public boolean verify(String sPrepend, Vector vectVerifyErrors) {
        return true; // not implemented yet because code cannot be changed
    }
    
    public int getCodeLength() {
        return iCodeLength;
    }
}
