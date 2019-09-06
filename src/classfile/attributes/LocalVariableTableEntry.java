/*
 * LocalVariableTableEntry.java
 *
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception
 * 1.02  12th Jun 1999   Tanmay   Method to get text summary added.
 * 1.03  16th Mar 2002   Tanmay   Made constant pool public so that it can
 *                                   be reassigned from outside.
 * 1.04  19th May 2002   Tanmay   Added protection against obfuscation.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.05  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 *
 */

package classfile.attributes;

import classfile.Utils;
import classfile.ConstantPoolInfo;
import classfile.ConstantPool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Class to handle a single entry in the local variable table attribute.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.05, 28th Sep, 2003
 */

public class LocalVariableTableEntry {
    public int                  iStartPC, iLength, iIndex;
    public ConstantPoolInfo     cpName, cpDescriptor;
    public ConstantPool         constPool;
    
    void read(DataInputStream dis, ConstantPool constPoolIn) throws IOException {
        int iNameIndex, iDescriptorIndex;
        
        constPool = constPoolIn;
        iStartPC = dis.readUnsignedShort();
        iLength = dis.readUnsignedShort();
        iNameIndex = dis.readUnsignedShort();
        iDescriptorIndex = dis.readUnsignedShort();
        iIndex = dis.readUnsignedShort();
        cpName = (iNameIndex > 0) ? constPool.getPoolInfo(iNameIndex) : null;
        cpDescriptor = (iDescriptorIndex > 0) ? constPool.getPoolInfo(iDescriptorIndex) : null;
    }
    
    void write(DataOutputStream dos, ConstantPool constPoolIn) throws IOException {
        int iNameIndex, iDescriptorIndex;
        
        constPool = constPoolIn;
        dos.writeShort(iStartPC);
        dos.writeShort(iLength);
        iNameIndex = (null != cpName) ? constPool.getIndexOf(cpName) : 0;
        dos.writeShort(iNameIndex);
        iDescriptorIndex = (null != cpDescriptor) ? constPool.getIndexOf(cpDescriptor) : 0;
        dos.writeShort(iDescriptorIndex);
        dos.writeShort(iIndex);
    }
    
    public void addRef() {
        if(null != cpName) cpName.addRef();
        if(null != cpDescriptor) cpDescriptor.addRef();
    }
    
    public void deleteRef() {
        if(null != cpName) cpName.deleteRef();
        if(null != cpDescriptor) cpDescriptor.deleteRef();
    }
    
    public String toString() {
        int iNameIndex, iDescriptorIndex;
        
        iNameIndex = (null != cpName) ? constPool.getIndexOf(cpName) : 0;
        iDescriptorIndex = (null != cpDescriptor) ? constPool.getIndexOf(cpDescriptor) : 0;
        
        return  "start_pc=" + Integer.toString(iStartPC) +
        " length=" + Integer.toString(iLength) +
        " name_index=" + Integer.toString(iNameIndex) +
        " desc_index=" + Integer.toString(iDescriptorIndex) +
        " index=" + Integer.toString(iIndex);
    }
}
