/*
 * Attributes.java
 *
 * Created on March 12th, 1999
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception.
 * 1.02  12th Jun 1999   Tanmay   Methods to verify and get text summary added.
 * 1.03  14th Aug 1999   Tanmay   Empty vector created on instantiation to prevent
 *                                null pointer exception.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.04  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 */

package classfile.attributes;

import classfile.ConstantPool;
import classfile.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Class to handle attributes.
 * <br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.04, 28th Sep, 2003
 */

public class Attributes {
    public int         iAttributesCount = 0;
    public Vector      attribVect = new Vector();
    
    public void read(DataInputStream dis, ConstantPool constPool) throws IOException {
        iAttributesCount    = dis.readUnsignedShort();
        attribVect          = new Vector(iAttributesCount);
        for (int iIndex=0; iIndex < iAttributesCount; iIndex++) {
            Attribute newAttrib = Attribute.readAndCreate(dis, constPool);
            attribVect.addElement(newAttrib);
        }
    }
    
    public void write(DataOutputStream dos, ConstantPool constPool) throws IOException {
        iAttributesCount = attribVect.size();
        dos.writeShort(iAttributesCount);
        for (int iIndex=0; iIndex < iAttributesCount; iIndex++) {
            Attribute newAttrib = (Attribute)attribVect.elementAt(iIndex);
            newAttrib.write(dos, constPool);
        }
    }
    
    public String toString() {
        String sRetStr;
        
        iAttributesCount = attribVect.size();
        sRetStr = "Attributes count: " + iAttributesCount + Utils.sNewLine;
        for (int iIndex=0; iIndex < iAttributesCount; sRetStr += (attribVect.elementAt(iIndex++).toString()+Utils.sNewLine));
        
        return sRetStr;
    }
    
    public boolean verify(String sPrepend, Vector vectVerifyErrors) {
        boolean bRet = true;
        
        for (int iIndex=0; iIndex < iAttributesCount; iIndex++) {
            Attribute thisAttrib = (Attribute)attribVect.elementAt(iIndex);
            bRet = (thisAttrib.verify(sPrepend, vectVerifyErrors) && bRet);
        }
        return bRet;
    }
    
    public int getAttribCount() {
        iAttributesCount = attribVect.size();
        return iAttributesCount;
    }
    
    public Attribute getAttribute(int iIndex) {
        return (Attribute) attribVect.elementAt(iIndex);
    }
}
