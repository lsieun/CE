/*
 * FieldInfo.java
 *
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception,
 *                                needs constant pool during read and write
 * 1.02  12th Jun 1999   Tanmay   Methods to verify and get summary text added.
 * 1.03  05th Jul 1999   Tanmay   setFieldDescriptor method added
 * 1.04  05th Jul 1999   Tanmay   methods to add and delete reference added
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.05  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 */

package classfile;

import classfile.attributes.Attributes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Class to handle a single field.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.05, 28th Sep, 2003
 */


public class FieldInfo {
    public AccessFlags          accessFlags;
    public int                  iNameIndex;
    public int                  iDescriptorIndex;
    public Attributes           attributes;
    public ConstantPoolInfo     cpName;       // points to UTF8
    public ConstantPoolInfo     cpDescriptor; // points to UTF8
    
    void read(DataInputStream dis, ConstantPool constantPool) throws IOException {
        accessFlags      = new AccessFlags();
        accessFlags.read(dis);
        iNameIndex       = dis.readUnsignedShort();
        cpName           = constantPool.getPoolInfo(iNameIndex);
        iDescriptorIndex = dis.readUnsignedShort();
        cpDescriptor     = constantPool.getPoolInfo(iDescriptorIndex);
        attributes = new Attributes();
        attributes.read(dis, constantPool);
        addReference();
    }
    
    void write(DataOutputStream dos, ConstantPool constantPool) throws IOException {
        accessFlags.write(dos);
        iNameIndex = constantPool.getIndexOf(cpName);
        dos.writeShort(iNameIndex);
        iDescriptorIndex = constantPool.getIndexOf(cpDescriptor);
        dos.writeShort(iDescriptorIndex);
        attributes.write(dos, constantPool);
    }
    
    public void removeReferences() {
        cpName.deleteRef();
        cpDescriptor.deleteRef();
    }
    
    public void addReference() {
        cpName.addRef();
        cpDescriptor.addRef();
    }
    
    public String toString() {
        String sRetStr;
        
        sRetStr = "FieldInfo:" +
        accessFlags.toString() +
        " Name: " + cpName +
        " Desc: " + cpDescriptor +
        " Attribs: " + attributes.toString();
        return sRetStr;
    }
    
    public String getFieldName() {
        return cpName.sUTFStr;
    }
    
    public void setFieldName(String sNewName) {
        cpName.sUTFStr = sNewName;
    }
    
    public String getFieldDescriptor() {
        String sRawDesc = cpDescriptor.sUTFStr;
        return Utils.getReadableDesc(sRawDesc);
    }
    
    public void setFieldDescriptor(String sNewDesc) {
        String sRawDesc = Utils.getRawDesc(sNewDesc);
        cpDescriptor.sUTFStr = sRawDesc;
    }
    
    public boolean verify(String sPrepend, Vector vectVerifyErrors) {
        boolean bRet = true;
        
        bRet = (accessFlags.verify(sPrepend, vectVerifyErrors, false) && bRet);
        bRet = (attributes.verify(sPrepend, vectVerifyErrors) && bRet);
        
        if ( (ConstantPoolInfo.CONSTANT_Utf8 != cpName.iTag) ||
        (cpName.sUTFStr.length() == 0) ) {
            vectVerifyErrors.addElement(sPrepend + ": Field name must point to a constant UTF8 and can not be empty.");
            bRet = false;
        }
        if ( (ConstantPoolInfo.CONSTANT_Utf8 != cpDescriptor.iTag) ||
        (cpDescriptor.sUTFStr.length() == 0) ) {
            vectVerifyErrors.addElement(sPrepend + ": Field descriptor must point to a constant UTF8 and can not be empty.");
            bRet = false;
        }
        return bRet;
    }
}
