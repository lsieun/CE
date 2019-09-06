/*
 * ExceptionTableEntry.java
 *
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception.
 * 1.02  12th Jun 1999   Tanmay   Method to get text summary added.
 * 1.03  25th Mar 2002   Tanmay   Method to set catch type class added.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.04  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 *
 */

package classfile.attributes;

import classfile.Utils;
import classfile.ConstantPoolInfo;
import classfile.ConstantPool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Class to handle a single entry in the exception table.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.04, 28th Sep, 2003
 */


public class ExceptionTableEntry {
    public int                  iStartPC, iEndPC, iHandlerPC;
    public int                  iCatchType;  // if non zero, points to const pool entry of type Class
    public ConstantPoolInfo     cpCatchType; // contains const pool entry of type Class if iCatchType is non zero.
    // if cpCatchType is null, this is for all exceptions (used to implement finally)
    
    void read(DataInputStream dis, ConstantPool constPool) throws IOException {
        iStartPC = dis.readUnsignedShort();
        iEndPC = dis.readUnsignedShort();
        iHandlerPC = dis.readUnsignedShort();
        iCatchType = dis.readUnsignedShort();
        if (0 != iCatchType) {
            cpCatchType = constPool.getPoolInfo(iCatchType);
            cpCatchType.addRef();
        }
    }
    
    void write(DataOutputStream dos, ConstantPool constPool) throws IOException {
        dos.writeShort(iStartPC);
        dos.writeShort(iEndPC);
        dos.writeShort(iHandlerPC);
        if (null != cpCatchType) {
            iCatchType = constPool.getIndexOf(cpCatchType);
        }
        dos.writeShort(iCatchType);
    }
    
    
    public void setCatchTypeClass(ConstantPoolInfo cpCatchTypeIn, ConstantPool constPool) {
        if(null != cpCatchType) {
            cpCatchType.deleteRef();
            cpCatchType = null;
            iCatchType = 0;
        }
        if(null != cpCatchTypeIn) {
            cpCatchTypeIn.addRef();
            cpCatchType = cpCatchTypeIn;
            iCatchType = constPool.getIndexOf(cpCatchType);
        }
    }
    
    public String toString() {
        return "type=" + ((null==cpCatchType) ? "all" : Utils.convertClassStrToStr(cpCatchType.refUTF8.sUTFStr)) +
        " start=" + iStartPC +
        " end=" + iEndPC +
        " handler=" + iHandlerPC;
    }
}
