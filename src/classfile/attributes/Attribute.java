/*
 * Attribute.java
 *
 * Created on March 12th, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception,
 *                                needs constant pool during read and write,
 *                                attribute details are resolved during read.
 * 1.02  12th Jun 1999   Tanmay   Methods to verify and get text summary added.
 * 1.03  30th Jun 1999   Tanmay   Check for modified attribute name added. This
 *                                fixes the null pointer exceptions.
 * 2.00  20th Jan 2002   Tanmay   Revamped and moved over to abstract class.
 * 2.01  02nd Feb 2002   Tanmay   Added InnerCLasses, Synthetic and Deprecated attributes
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 2.02  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 *
 */

package classfile.attributes;

import classfile.ConstantPoolInfo;
import classfile.ConstantPool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Attribute interface.
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     2.02, 28th Sep, 2003
 */


public abstract class Attribute {
    static final String SOURCE_FILE = "SourceFile";
    static final String LOCAL_VARIABLE_TABLE = "LocalVariableTable";
    static final String LINE_NUMBER_TABLE = "LineNumberTable";
    static final String EXCEPTIONS = "Exceptions";
    static final String CONSTANT_VALUE = "ConstantValue";
    static final String CODE = "Code";
    static final String INNER_CLASSES = "InnerClasses";
    static final String SYNTHETIC = "Synthetic";
    static final String DEPRECATED = "Deprecated";
    static final String UNKNOWN = "Unknown";
    
    public int                  iAttribNameIndex;
    public ConstantPoolInfo     cpAttribName; // sUTFStr field gives the name
    
    public String               sName;
    int                         iAttribLength;
    
    static Attribute readAndCreate(DataInputStream dis, ConstantPool constPool) throws IOException {
        int iAttribNameIndex = dis.readUnsignedShort();
        ConstantPoolInfo cpAttribName = constPool.getPoolInfo(iAttribNameIndex);
        cpAttribName.addRef();
        Attribute attrib = Attribute.createAttrib(cpAttribName.sUTFStr);
        attrib.cpAttribName = cpAttribName;
        attrib.iAttribNameIndex = iAttribNameIndex;
        attrib.readAttributeDetails(dis, constPool);
        return attrib;
    }
    
    void write(DataOutputStream dos, ConstantPool constPool) throws IOException {
        iAttribNameIndex = constPool.getIndexOf(cpAttribName);
        dos.writeShort(iAttribNameIndex);
        writeAttributeDetails(dos, constPool);
    }
    
    abstract void readAttributeDetails(DataInputStream dis, ConstantPool constPool) throws IOException;
    abstract void writeAttributeDetails(DataOutputStream dos, ConstantPool constPool) throws IOException;
    abstract public String toString();
    abstract public boolean verify(String sPrepend, Vector vectVerifyErrors);
    
    static Attribute createAttrib(String sType) {
        if (Attribute.SOURCE_FILE.equals(sType)) {
            return new SourceFileAttribute();
        }
        else if (Attribute.CONSTANT_VALUE.equals(sType)) {
            return new ConstantValueAttribute();
        }
        else if (Attribute.EXCEPTIONS.equals(sType)) {
            return new ExceptionsAttribute();
        }
        else if (Attribute.CODE.equals(sType)) {
            return new CodeAttribute();
        }
        else if (Attribute.LINE_NUMBER_TABLE.equals(sType)) {
            return new LineNumberTableAttribute();
        }
        else if (Attribute.LOCAL_VARIABLE_TABLE.equals(sType)) {
            return new LocalVariableTableAttribute();
        }
        else if (Attribute.INNER_CLASSES.equals(sType)) {
            return new InnerClassesAttribute();
        }
        else if (Attribute.SYNTHETIC.equals(sType)) {
            return new SyntheticAttribute();
        }
        else if (Attribute.DEPRECATED.equals(sType)) {
            return new DeprecatedAttribute();
        }
        
        return new UnknownAttribute();
    }
}

