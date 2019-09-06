/*
 * Interfaces.java
 *
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.01  18th Mar 1999   Tanmay   throws IOexception instead of Exception
 * 1.02  12th Jun 1999   Tanmay   Methods to verify and get text summary added.
 * 1.03  03rd Jul 1999   Tanmay   Method to add interface introduced
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.04  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 */


package classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Class to handle interfaces.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.04, 28th Sep, 2003
 */


public class Interfaces {
    int                     iInterfacesCount;
    Vector                  vectInterfaces; // contains CONSTANT_Class
    
    void read(DataInputStream dis, ConstantPool constPool) throws IOException {
        int iIndex;
        iInterfacesCount    = dis.readUnsignedShort();
        vectInterfaces      = new Vector(iInterfacesCount);
        for (iIndex=0; iIndex < iInterfacesCount; iIndex++) {
            int iInterfaceIndex = dis.readUnsignedShort();
            ConstantPoolInfo cpInfo = constPool.getPoolInfo(iInterfaceIndex);
            vectInterfaces.addElement(cpInfo);
            cpInfo.addRef();
        }
    }
    
    void write(DataOutputStream dos, ConstantPool constPool) throws IOException {
        int iIndex;
        iInterfacesCount = vectInterfaces.size();
        dos.writeShort(iInterfacesCount);
        for (iIndex=0; iIndex < iInterfacesCount; iIndex++) {
            int iInterfaceIndex = constPool.getIndexOf((ConstantPoolInfo)vectInterfaces.elementAt(iIndex));
            dos.writeShort(iInterfaceIndex);
        }
    }
    
    public boolean verify(Vector vectVerifyErrors) {
        boolean bRet = true;
        for (int iIndex=0; iIndex < iInterfacesCount; iIndex++) {
            ConstantPoolInfo cpClass = (ConstantPoolInfo)vectInterfaces.elementAt(iIndex);
            if (ConstantPoolInfo.CONSTANT_Class != cpClass.iTag) {
                vectVerifyErrors.addElement("Interface index " + (iIndex+1) + ": Interfaces must point to constant pool of type Class.");
                bRet = false;
            }
        }
        return bRet;
    }
    
    public String toString() {
        String sRet;
        String sNewLine = System.getProperty("line.separator");
        
        iInterfacesCount = vectInterfaces.size();
        sRet = "Interfaces count: " + iInterfacesCount + sNewLine;
        for (int iIndex=0; iIndex < iInterfacesCount; iIndex++) {
            sRet += ("Interface " + (iIndex+1) + ": const_pool_entry=" + vectInterfaces.elementAt(iIndex) + sNewLine);
        }
        
        return sRet;
    }
    
    public String getInterfaceName(int iIndex) {
        ConstantPoolInfo cpClass = (ConstantPoolInfo)vectInterfaces.elementAt(iIndex);
        return Utils.convertClassStrToStr(cpClass.refUTF8.sUTFStr);
    }
    
    public void setInterfaceName(int iIndex, String sNewName) {
        ConstantPoolInfo cpClass = (ConstantPoolInfo)vectInterfaces.elementAt(iIndex);
        cpClass.refUTF8.sUTFStr = Utils.convertStrToClassStr(sNewName);
    }
    
    public void removeInterface(int iIndex) {
        ConstantPoolInfo cpClass = (ConstantPoolInfo)vectInterfaces.elementAt(iIndex);
        cpClass.deleteRef();
        vectInterfaces.removeElementAt(iIndex);
        iInterfacesCount--;
    }
    
    public void addInterface(ConstantPoolInfo cpClass) {
        if (ConstantPoolInfo.CONSTANT_Class == cpClass.iTag) {
            iInterfacesCount++;
            vectInterfaces.addElement(cpClass);
            cpClass.addRef();
        }
    }
    
    public int getInterfacesCount() {
        return iInterfacesCount;
    }
}
