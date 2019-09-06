/*
 * Methods.java
 *
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception,
 *                                needs constant pool during read and write
 * 1.02  12th Jun 1999   Tanmay   Methods to verify and get text summary added.
 * 1.03  14th Aug 1999   Tanmay   Added functionality to delete method.
 * 1.04  14th Aug 1999   Tanmay   Added functionality to add a new method.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.05  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 *
 */

package classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Class to handle methods information.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.05, 28th September, 2003
 */

public class Methods {
    int                     iMethodsCount;
    Vector                  methodsVect;
    
    void read(DataInputStream dis, ConstantPool constantPool) throws IOException {
        iMethodsCount       = dis.readUnsignedShort();
        methodsVect         = new Vector(iMethodsCount);
        for (int iIndex=0; iIndex < iMethodsCount; iIndex++) {
            MethodInfo newInfo = new MethodInfo();
            newInfo.read(dis, constantPool);
            methodsVect.addElement(newInfo);
        }
    }
    
    void write(DataOutputStream dos, ConstantPool constantPool) throws IOException {
        iMethodsCount = methodsVect.size();
        dos.writeShort(iMethodsCount);
        for (int iIndex=0; iIndex < iMethodsCount; iIndex++) {
            MethodInfo newInfo = (MethodInfo)methodsVect.elementAt(iIndex);
            newInfo.write(dos, constantPool);
        }
    }
    
    public boolean verify(Vector vectVerifyErrors) {
        boolean bRet = true;
        for (int iIndex=0; iIndex < iMethodsCount; iIndex++) {
            MethodInfo newInfo = (MethodInfo)methodsVect.elementAt(iIndex);
            bRet = (newInfo.verify("Method " + (iIndex+1) + "(" + newInfo.cpName.sUTFStr + ")" ,
            vectVerifyErrors) && bRet);
        }
        return bRet;
    }
    
    public String toString() {
        String sRetStr;
        String sNewLine = System.getProperty("line.separator");
        
        iMethodsCount = methodsVect.size();
        sRetStr = "Methods count: " + iMethodsCount + sNewLine;
        for (int iIndex=0; iIndex < iMethodsCount; sRetStr += (methodsVect.elementAt(iIndex++).toString()+sNewLine));
        return sRetStr;
    }
    
    public int getMethodsCount() {
        return (iMethodsCount = methodsVect.size());
    }
    
    public MethodInfo getMethod(int iIndex) {
        return (MethodInfo)methodsVect.elementAt(iIndex);
    }
    
    public void deleteMethod(int iIndex) {
        MethodInfo delMethod = (MethodInfo)methodsVect.elementAt(iIndex);
        delMethod.removeReferences();
        methodsVect.removeElementAt(iIndex);
        iMethodsCount--;
    }
    
    public void addMethod(MethodInfo newMethod) {
        newMethod.addReferences();
        methodsVect.addElement(newMethod);
        iMethodsCount++;
    }
}
