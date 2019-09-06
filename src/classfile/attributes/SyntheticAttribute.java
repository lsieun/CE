/*
 * SyntheticAttribute.java
 *
 * Created on February 2, 2002, 4:25 PM
 *
 * Modification Log:
 * 1.00  02nd Feb 2002   Tanmay   Original version.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.01  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 */

package classfile.attributes;

import classfile.ConstantPoolInfo;
import classfile.ConstantPool;
import classfile.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.util.Vector;


/**
 * Class to handle synthetic attributes.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.01, 28th Sep, 2003
 */
public class SyntheticAttribute extends Attribute {
    
    /** Creates new SyntheticAttribute */
    public SyntheticAttribute() {
        sName = Attribute.SYNTHETIC;
    }
    
    void readAttributeDetails(DataInputStream dis, ConstantPool constPool) throws IOException {
        iAttribLength = dis.readInt();
    }
    
    void writeAttributeDetails(DataOutputStream dos, ConstantPool constPool) throws IOException {
        dos.writeInt(iAttribLength);
    }
    
    public boolean verify(String sPrepend, Vector vectVerifyErrors) {
        boolean bRet = true;
        if (iAttribLength != 0) {
            vectVerifyErrors.addElement(": Synthetic attribute length must be zero.");
            bRet = false;
        }
        return bRet;
    }
    
    public String toString() {
        return "Synthetic";
    }
}
