/*
 * LineNumberTableEntry.java
 *
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception
 * 1.02  12th Jun 1999   Tanmay   Method to get text summary added.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.03  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 *
 */

package classfile.attributes;

import classfile.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Class to handle a single entry in the line number table.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.03, 28th Sep, 2003
 */


public class LineNumberTableEntry {
    public int     iStartPC, iLineNum;
    
    void read(DataInputStream dis) throws IOException {
        iStartPC = dis.readUnsignedShort();
        iLineNum = dis.readUnsignedShort();
    }
    
    void write(DataOutputStream dos) throws IOException {
        dos.writeShort(iStartPC);
        dos.writeShort(iLineNum);
    }
    
    public String toString() {
        return "start_pc=" + iStartPC + " line_number=" + iLineNum;
    }
}
