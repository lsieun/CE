/*
 * ClassNames.java
 *
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception
 * 1.02  12th Jun 1999   Tanmay   Methods to verify and get summary text added.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.03  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 *
 */

package classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;


/**
 * Class to handle class names.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.03, 28th Sep, 2003
 */

public class ClassNames {
    int                     iThisClass;
    int                     iSuperClass;
    public ConstantPoolInfo cpThisClass;  //CONSTANT_Class
    public ConstantPoolInfo cpSuperClass; //CONSTANT_Class
    
    void read(DataInputStream dis, ConstantPool constPool) throws IOException {
        iThisClass   = dis.readUnsignedShort();
        iSuperClass  = dis.readUnsignedShort();
        cpThisClass = constPool.getPoolInfo(iThisClass);
        cpThisClass.addRef();
        cpSuperClass = constPool.getPoolInfo(iSuperClass);
        cpSuperClass.addRef();
    }
    
    void write(DataOutputStream dos, ConstantPool constPool) throws IOException {
        iThisClass = constPool.getIndexOf(cpThisClass);
        iSuperClass = constPool.getIndexOf(cpSuperClass);
        dos.writeShort(iThisClass);
        dos.writeShort(iSuperClass);
    }
    
    public boolean verify(Vector vectVerifyErrors) {
        boolean bRet = true;
        if ( (ConstantPoolInfo.CONSTANT_Class != cpThisClass.iTag) ||
        (ConstantPoolInfo.CONSTANT_Class != cpSuperClass.iTag) ) {
            vectVerifyErrors.addElement("ClassName and SuperClassName indexes should point to constant pool of type Class.");
            bRet = false;
        }
        if ( (0 == cpThisClass.refUTF8.sUTFStr.length()) || (0 == cpSuperClass.refUTF8.sUTFStr.length()) ) {
            vectVerifyErrors.addElement("Class and SuperClass names can not be empty.");
            bRet = false;
        }
        return bRet;
    }
    
    public String toString() {
        String sRetStr;
        sRetStr = "This class: " + cpThisClass + ", ";
        sRetStr += ("Super class: " + cpSuperClass);
        return sRetStr;
    }
    
    public String getThisClassName() {
        return Utils.convertClassStrToStr(cpThisClass.refUTF8.sUTFStr);
    }
    
    public String getSuperClassName() {
        return Utils.convertClassStrToStr(cpSuperClass.refUTF8.sUTFStr);
    }
    
    public void setThisClassName(String sNewStr) {
        cpThisClass.refUTF8.sUTFStr = Utils.convertStrToClassStr(sNewStr);
    }
    
    public void setSuperClassName(String sNewStr) {
        cpSuperClass.refUTF8.sUTFStr = Utils.convertStrToClassStr(sNewStr);
    }
}
