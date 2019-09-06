/*
 * UnknownAttribute.java
 *
 * Created on June 2nd, 1999
 *
 * Modification Log:
 * 1.00  02nd Jun 1999   Tanmay   Original Version
 * 1.01  12th Jun 1999   Tanmay   Method to verify added
 * 1.02  20th Jan 2002   Tanmay   Implemented Attribute interface
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.03  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 */

package classfile.attributes;

import classfile.ConstantPoolInfo;
import classfile.ConstantPool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;


/**
 * Class to handle unknown attributes.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.03, 28th Sep, 2003
 */

public class UnknownAttribute extends Attribute {
    public byte[]               abUnknAttr;
    
    public UnknownAttribute() {
        sName = Attribute.UNKNOWN;
    }
    
    void readAttributeDetails(DataInputStream dis, ConstantPool constPool) throws IOException {
        iAttribLength      = dis.readInt();
        abUnknAttr = new byte[iAttribLength];
        dis.read(abUnknAttr);
    }
    
    void writeAttributeDetails(DataOutputStream dos, ConstantPool constPool) throws IOException {
        dos.writeInt(iAttribLength);
        dos.write(abUnknAttr);
    }
    
    public String toString() {
        return "Unrecognized Attribute. Length=" + iAttribLength;
    }
    
    public boolean verify(String sPrepend, Vector vectVerifyErrors) {
        return true; // not implemented because it cannot be changed now
    }
}
