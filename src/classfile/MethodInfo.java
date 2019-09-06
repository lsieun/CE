/*
 * MethodInfo.java
 * 
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception,
 *                                needs constant pool during read and write
 * 1.02  12th Jun 1999   Tanmay   Methods to verify and get text summary added.
 * 1.03  14th Aug 1999   Tanmay   New methods to add and remove references.
 * 1.04  14th Aug 1999   Tanmay   New methods to get and set method name and return type.
 * 1.05  14th Aug 1999   Tanmay   Method to interpret method descriptor moved to Utils.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.06  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 */

package classfile;

import classfile.attributes.*;
import classfile.attributes.Attribute;
import classfile.attributes.ExceptionsAttribute;
import classfile.attributes.CodeAttribute;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Stack;
import java.util.Vector;

/**
 * Class to handle information of an individual method. 
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.06, 28th Sep, 2003
 */

public class MethodInfo
{
    public AccessFlags          accessFlags;
    int                         iNameIndex;
    int                         iDescriptorIndex;
    public Attributes           attributes;
    public ConstantPoolInfo     cpName;       // points to UTF8 
    public ConstantPoolInfo     cpDescriptor; // points to UTF8
    ConstantPool                constPool;

    void read(DataInputStream dis, ConstantPool constantPool) throws IOException
    {
        constPool = constantPool;
        accessFlags = new AccessFlags();
        accessFlags.read(dis);
        iNameIndex       = dis.readUnsignedShort();
        cpName           = constantPool.getPoolInfo(iNameIndex);
        iDescriptorIndex = dis.readUnsignedShort();
        cpDescriptor     = constantPool.getPoolInfo(iDescriptorIndex);
        attributes = new Attributes();
        attributes.read(dis, constantPool);
        addReferences();
    }

    void write(DataOutputStream dos, ConstantPool constantPool) throws IOException
    {
        accessFlags.write(dos);
        iNameIndex = constantPool.getIndexOf(cpName);
        dos.writeShort(iNameIndex);
        iDescriptorIndex = constantPool.getIndexOf(cpDescriptor);
        dos.writeShort(iDescriptorIndex);
        attributes.write(dos, constantPool);
    }

    public void addReferences()
    {
        cpName.addRef();
        cpDescriptor.addRef();
    }
    
    public void removeReferences()
    {
        cpName.deleteRef();
        cpDescriptor.deleteRef();
    }

    public String getMethodName()
    {
        return cpName.sUTFStr;
    }
    
    public void setMethodName(String sNewName)
    {
        cpName.sUTFStr = sNewName;
    }
    
    public String [] getMethodDesc()
    {
        return Utils.getReadableMethodDesc(cpDescriptor.sUTFStr);
    }
    
    public void setMethodDesc(String []aParams)
    {
        String sRawDesc = Utils.getRawMethodDesc(aParams);
        cpDescriptor.sUTFStr = sRawDesc;
    }
    
    public String toString()
    {
        String sRetStr;

        sRetStr = "MethodInfo:" + 
                    accessFlags.toString() + 
                    " Name: " + cpName.sUTFStr +
                    " Desc: " + cpDescriptor.sUTFStr +
                    " Attribs: " + attributes.toString();
        return sRetStr;
    }
    
    public boolean verify(String sPrepend, Vector vectVerifyErrors)
    {
        boolean bRet = true;
        
        bRet = (accessFlags.verify(sPrepend, vectVerifyErrors, false) && bRet);
        bRet = (attributes.verify(sPrepend, vectVerifyErrors) && bRet);
        if ( (ConstantPoolInfo.CONSTANT_Utf8 != cpName.iTag) ||
             (cpName.sUTFStr.length() == 0) )
        {
            vectVerifyErrors.addElement(sPrepend + ": Method name must point to a constant UTF8 and can not be empty.");
            bRet = false;
        }
        if ( (ConstantPoolInfo.CONSTANT_Utf8 != cpDescriptor.iTag) ||
             (cpDescriptor.sUTFStr.length() == 0) )
        {
            vectVerifyErrors.addElement(sPrepend + ": Method descriptor must point to a constant UTF8 and can not be empty.");
            bRet = false;
        }
        return bRet;
    }
}

