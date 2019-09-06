/*
 * ConstantPoolInfo.java
 * 
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception.
 * 1.02  12th Jun 1999   Tanmay   Methods to verify and get text summary added.
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
 * Class to handle a single entry in the constant pool. 
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.03, 28th Sep, 2003
 */


public class ConstantPoolInfo
{
    public static final int  CONSTANT_Class                = 7;
    public static final int  CONSTANT_Fieldref             = 9;
    public static final int  CONSTANT_Methodref            = 10;
    public static final int  CONSTANT_InterfaceMethodref   = 11;
    public static final int  CONSTANT_String               = 8;
    public static final int  CONSTANT_Integer              = 3;
    public static final int  CONSTANT_Float                = 4;
    public static final int  CONSTANT_Long                 = 5;
    public static final int  CONSTANT_Double               = 6;
    public static final int  CONSTANT_NameAndType          = 12;
    public static final int  CONSTANT_Utf8                 = 1;

    /**
     * Tag denotes the type of pool entry. It will be one of CONSTANT_<...> types
     */
    public int              iTag;
    public int              iNameIndex;
    public int              iClassIndex;
    public int              iNameAndTypeIndex;
    public int              iStringIndex;
    public int              iIntValue;
    public float            fFloatVal;
    public long             lLongVal;
    public int              iDescriptorIndex;
    public double           dDoubleVal;
    public String           sUTFStr;
    
    int                     iNumRefs;
    ConstantPool            constPool;
    public ConstantPoolInfo        refUTF8, refExtraUTF8, refClass, refNameAndType;

    public boolean verify(String sPrepend, Vector vectVerifyErrors)
    {
        boolean bRet = true;
        
        switch (iTag)
        {
            case CONSTANT_Class:
                if ( (null == refUTF8) || (CONSTANT_Utf8 != refUTF8.iTag) )
                {
                    vectVerifyErrors.addElement(sPrepend + ": Constant class should point to a UTF8 pool item.");
                    bRet = false;
                }
                break;
            case CONSTANT_String:
                if ( (null == refUTF8) || (CONSTANT_Utf8 != refUTF8.iTag) )
                {
                    vectVerifyErrors.addElement(sPrepend + ": Constant string should point to a UTF8 pool item.");
                    bRet = false;
                }
                break;
            case CONSTANT_Fieldref:
            case CONSTANT_Methodref:
            case CONSTANT_InterfaceMethodref:
                if ( (null == refClass) || (CONSTANT_Class != refClass.iTag) )
                {
                    vectVerifyErrors.addElement(sPrepend + ": Class index of constant field/method/interfacemethod ref should point to a Class pool item.");
                    bRet = false;
                }
                if ( (null == refNameAndType) || (CONSTANT_NameAndType != refNameAndType.iTag) )
                {
                    vectVerifyErrors.addElement(sPrepend + ": NameAndType index of constant field/method/interfacemethod ref should point to a NameAndType pool item.");
                    bRet = false;
                }
                break;
            case CONSTANT_NameAndType:
                if ( (null == refUTF8) || (CONSTANT_Utf8 != refUTF8.iTag) )
                {
                    vectVerifyErrors.addElement(sPrepend + ": Name index of NameAndType ref should point to a UTF8 pool item.");
                    bRet = false;
                }
                if ( (null == refExtraUTF8) || (CONSTANT_Utf8 != refExtraUTF8.iTag) )
                {
                    vectVerifyErrors.addElement(sPrepend + ": Descriptor index of NameAndType ref should point to a UTF8 pool item.");
                    bRet = false;
                }
                break;
            case CONSTANT_Integer:
            case CONSTANT_Float:
            case CONSTANT_Long:
            case CONSTANT_Double:
            case CONSTANT_Utf8:
                // nothing to check
                break;
            default:
                vectVerifyErrors.addElement(sPrepend + ": Constant pool type not recognized.");
                bRet = false;
                break;
        }
        return bRet;
    }
    
    void read(DataInputStream dis) throws IOException
    {
        iTag = dis.readByte();

        switch (iTag)
        {
            case CONSTANT_Class:
                iNameIndex = dis.readUnsignedShort(); // points to a UTF8 
                break;
            case CONSTANT_String:
                iStringIndex = dis.readUnsignedShort(); // points to a UTF8 
                break;
            case CONSTANT_Fieldref:
            case CONSTANT_Methodref:
            case CONSTANT_InterfaceMethodref:
                iClassIndex = dis.readUnsignedShort(); // points to a Class
                iNameAndTypeIndex = dis.readUnsignedShort(); // points to a NameAndType
                break;
            case CONSTANT_Integer:
                iIntValue = dis.readInt();
                break;
            case CONSTANT_Float:
                fFloatVal = dis.readFloat();
                break;
            case CONSTANT_NameAndType:
                iNameIndex = dis.readUnsignedShort(); // points to UTF8
                iDescriptorIndex = dis.readUnsignedShort(); // points to UTF8
                break;
            case CONSTANT_Long:
                lLongVal = dis.readLong();
                break;
            case CONSTANT_Double:
                dDoubleVal = dis.readDouble();
                break;
            case CONSTANT_Utf8:
                sUTFStr = dis.readUTF();
                break;
            default:
                System.out.println("Unknown constant pool type: " + iTag);
                break;
        }
    }

    void write(DataOutputStream dos, ConstantPool constPoolIn) throws IOException
    {
        if (null != constPoolIn)
        {
            constPool = constPoolIn;
        }
        dos.writeByte(iTag);

        switch (iTag)
        {
            case CONSTANT_Class:
                iNameIndex = constPool.getIndexOf(refUTF8);
                dos.writeShort(iNameIndex);
                break;
            case CONSTANT_String:
                iStringIndex = constPool.getIndexOf(refUTF8);
                dos.writeShort(iStringIndex);
                break;
            case CONSTANT_Fieldref:
            case CONSTANT_Methodref:
            case CONSTANT_InterfaceMethodref:
                iClassIndex = constPool.getIndexOf(refClass);
                iNameAndTypeIndex = constPool.getIndexOf(refNameAndType);
                dos.writeShort(iClassIndex);
                dos.writeShort(iNameAndTypeIndex);
                break;
            case CONSTANT_Integer:
                dos.writeInt(iIntValue);
                break;
            case CONSTANT_Float:
                dos.writeFloat(fFloatVal);
                break;
            case CONSTANT_NameAndType:
                iNameIndex = constPool.getIndexOf(refUTF8);
                iDescriptorIndex = constPool.getIndexOf(refExtraUTF8);
                dos.writeShort(iNameIndex);
                dos.writeShort(iDescriptorIndex);
                break;
            case CONSTANT_Long:
                dos.writeLong(lLongVal);
                break;
            case CONSTANT_Double:
                dos.writeDouble(dDoubleVal);
                break;
            case CONSTANT_Utf8:
                dos.writeUTF(sUTFStr);
                break;
            default:
                System.out.println("Unknown constant pool type: " + iTag);
                break;
        }
    }

    public void setNameIndex(int iIndex)
    {
        iNameIndex = iIndex;
        if(null != refUTF8) refUTF8.deleteRef();
        refUTF8 = constPool.getPoolInfo(iNameIndex);
        refUTF8.addRef();
    }
    
    public void setConstPool(ConstantPool constPoolIn)
    {
        constPool = constPoolIn;
    }
    
    public void setStringIndex(int iIndex)
    {
        iStringIndex = iIndex;
        if(null != refUTF8) refUTF8.deleteRef();
        refUTF8 = constPool.getPoolInfo(iStringIndex);
        refUTF8.addRef();
    }

    public void setDescriptorIndex(int iIndex)
    {
        iDescriptorIndex = iIndex;
        if(null != refExtraUTF8) refExtraUTF8.deleteRef();
        refExtraUTF8 = constPool.getPoolInfo(iDescriptorIndex);
        refExtraUTF8.addRef();
    }

    public void setClassIndex(int iIndex)
    {
        iClassIndex = iIndex;
        if(null != refClass) refClass.deleteRef();
        refClass = constPool.getPoolInfo(iClassIndex);
        refClass.addRef();
    }

    public void setNameAndTypeIndex(int iIndex)
    {
        iNameAndTypeIndex = iIndex;
        if(null != refNameAndType) refNameAndType.deleteRef();
        refNameAndType = constPool.getPoolInfo(iNameAndTypeIndex);
        refNameAndType.addRef();
    }
    
    void resolveReferences(ConstantPool constPool)
    {
        this.constPool = constPool;
        switch (iTag)
        {
            case CONSTANT_Class:
                refUTF8 = constPool.getPoolInfo(iNameIndex);
                refUTF8.addRef();
                break;
            case CONSTANT_String:
                refUTF8 = constPool.getPoolInfo(iStringIndex);
                refUTF8.addRef();
                break;
            case CONSTANT_Fieldref:
            case CONSTANT_Methodref:
            case CONSTANT_InterfaceMethodref:
                refClass = constPool.getPoolInfo(iClassIndex);
                refClass.addRef();
                refNameAndType = constPool.getPoolInfo(iNameAndTypeIndex);
                refNameAndType.addRef();
                break;
            case CONSTANT_NameAndType:
                refUTF8 = constPool.getPoolInfo(iNameIndex);
                refUTF8.addRef();
                refExtraUTF8 = constPool.getPoolInfo(iDescriptorIndex);
                refExtraUTF8.addRef();
                break;
        }
    }

    public String tag2Column()
    {
        String sDesc = "Unknown";
        
        switch(iTag)
        {
            case ConstantPoolInfo.CONSTANT_Class:
                sDesc = "CLASS";
                break;
            case ConstantPoolInfo.CONSTANT_Fieldref:
                sDesc = "FIELDREF";
                break;
            case ConstantPoolInfo.CONSTANT_Methodref:
                sDesc = "METHODREF";
                break;
            case ConstantPoolInfo.CONSTANT_InterfaceMethodref:
                sDesc = "INTERFACEMETHODREF";
                break;
            case ConstantPoolInfo.CONSTANT_String:
                sDesc = "STRING";
                break;
            case ConstantPoolInfo.CONSTANT_Integer:
                sDesc = "INTEGER";
                break;
            case ConstantPoolInfo.CONSTANT_Float:
                sDesc = "FLOAT";
                break;
            case ConstantPoolInfo.CONSTANT_Long:
                sDesc = "LONG";
                break;
            case ConstantPoolInfo.CONSTANT_Double:
                sDesc = "DOUBLE";
                break;
            case ConstantPoolInfo.CONSTANT_NameAndType:
                sDesc = "NAMEANDTYPE";
                break;
            case ConstantPoolInfo.CONSTANT_Utf8:
                sDesc = "UTF8";
                break;
        }
        return sDesc;
    }
    
    public String toString()
    {
        return tag2Column() + ": " + getExtraInfoString();
    }
    
    public String getExtraInfoString()
    {
        String sDesc = "";
        switch(iTag)
        {
            case CONSTANT_Class:
                sDesc = "name=" + refUTF8.sUTFStr;
                break;
            case CONSTANT_Fieldref:
            case CONSTANT_Methodref:
            case CONSTANT_InterfaceMethodref:
                sDesc = "class=" + refClass.refUTF8.sUTFStr + 
                        ", name=" + refNameAndType.refUTF8.sUTFStr + 
                        ", type=" + refNameAndType.refExtraUTF8.sUTFStr;
                break;
            case CONSTANT_String:
                sDesc = "string=" + refUTF8.sUTFStr;
                break;
            case CONSTANT_Integer:
                sDesc = "int_value=" + iIntValue;
                break;
            case CONSTANT_Float:
                sDesc = "float_value=" + fFloatVal;
                break;
            case CONSTANT_Long:
                sDesc = "long_value=" + lLongVal;
                break;
            case CONSTANT_Double:
                sDesc = "double_value=" + dDoubleVal;
                break;
            case CONSTANT_NameAndType:
                sDesc = "name=" + refUTF8.sUTFStr + ", descriptor=" + refExtraUTF8.sUTFStr;
                break;
            case CONSTANT_Utf8:
                sDesc = "string=" + sUTFStr;
                break;
        }

        return sDesc;
    }

    public boolean isDoubleSizeConst()
    {
        return ((ConstantPoolInfo.CONSTANT_Long   == iTag) ||
                (ConstantPoolInfo.CONSTANT_Double == iTag));
    }
    
    public void addRef()
    {
        iNumRefs++;
    }
    
    public void deleteRef()
    {
        iNumRefs--;
        if (0 == iNumRefs)
        {
            // cascade delete refs
            switch (iTag)
            {
                case CONSTANT_Class:
                    refUTF8.deleteRef();
                    break;
                case CONSTANT_String:
                    refUTF8.deleteRef();
                    break;
                case CONSTANT_Fieldref:
                case CONSTANT_Methodref:
                case CONSTANT_InterfaceMethodref:
                    refClass.deleteRef();
                    refNameAndType.deleteRef();
                    break;
                case CONSTANT_NameAndType:
                    refUTF8.deleteRef();
                    refExtraUTF8.deleteRef();
                    break;
            }
        }
    }
    
    public int getRef()
    {
        return iNumRefs;
    }
}
