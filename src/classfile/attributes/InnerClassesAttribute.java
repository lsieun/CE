/*
 * InnerClasses.java
 *
 * Created on February 2, 2002, 2:01 PM
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
 * Class to handle inner class attributes.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.01, 28th Sep, 2003
 */
public class InnerClassesAttribute extends Attribute {
    
    int         iNumberOfClasses;
    Vector      vectInnerClassInfo;
    
    /** Creates new InnerClasses */
    public InnerClassesAttribute() {
        sName = Attribute.INNER_CLASSES;
    }
    
    void readAttributeDetails(DataInputStream dis, ConstantPool constPool) throws IOException {
        iAttribLength    = dis.readInt();
        vectInnerClassInfo = null;
        if((iNumberOfClasses = dis.readUnsignedShort()) > 0) {
            vectInnerClassInfo = new Vector();
        }
        
        for(int iIndex=0; iIndex < iNumberOfClasses; iIndex++) {
            InnerClassInfo innerClass = new InnerClassInfo();
            innerClass.read(dis, constPool);
            vectInnerClassInfo.add(innerClass);
        }
    }
    
    void writeAttributeDetails(DataOutputStream dos, ConstantPool constPool) throws IOException {
        dos.writeInt(iAttribLength);
        dos.writeInt(iNumberOfClasses);
        
        for(int iIndex=0; iIndex < iNumberOfClasses; iIndex++) {
            InnerClassInfo innerClass = (InnerClassInfo)vectInnerClassInfo.elementAt(iIndex);
            innerClass.write(dos, constPool);
        }
    }
    
    public int getNumClasses() {
        return iNumberOfClasses;
    }
    
    public InnerClassInfo getInnerClassInfo(int iIndex) {
        return (InnerClassInfo)vectInnerClassInfo.elementAt(iIndex);
    }
    
    public boolean verify(String sPrepend, Vector vectVerifyErrors) {
        boolean bRet = true;
        if (iNumberOfClasses > 0) {
            for (int iIndex=0; iIndex < iNumberOfClasses; iIndex++) {
                InnerClassInfo innerClass = (InnerClassInfo)vectInnerClassInfo.elementAt(iIndex);
                if(false == innerClass.verify(sPrepend, vectVerifyErrors)) bRet = false;
            }
        }
        return bRet;
    }
    
    public String toString() {
        String sRetStr = "Attribute " + sName + ". Number=" + iNumberOfClasses;
        return sRetStr;
    }
}
