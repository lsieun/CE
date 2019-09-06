/*
 * ConstantValueAttribute.java
 *
 * Created on June 2nd, 1999
 *
 * Modification Log:
 * 1.00  02nd Jun 1999   Tanmay   Original Version
 * 1.01  12th Jun 1999   Tanmay   Methods to verify and get summary text added.
 * 1.02  20th Jan 2002   Tanmay   Implemented Attribute interface
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.03  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 *
 */

package classfile.attributes;

import classfile.Utils;
import classfile.ConstantPoolInfo;
import classfile.ConstantPool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;


/**
 * Class to handle the constant value attribute.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.03, 28th Sep, 2003
 */

public class ConstantValueAttribute extends Attribute {
    int                         iConstValueIndex; // points to a String, Integer, Float, Long or Double
    public ConstantPoolInfo     cpConstant;       // is a String, Integer, Float, Long or Double
    public String               sConstType;
    public String               sConstValue;
    ConstantPool                constPool;
    
    public ConstantValueAttribute() {
        sName = Attribute.CONSTANT_VALUE;
    }
    
    void readAttributeDetails(DataInputStream dis, ConstantPool constPoolIn) throws IOException {
        constPool = constPoolIn;
        iAttribLength    = dis.readInt();
        iConstValueIndex = dis.readUnsignedShort();
        cpConstant       = constPool.getPoolInfo(iConstValueIndex);
        cpConstant.addRef();
        
        switch(cpConstant.iTag) {
            case ConstantPoolInfo.CONSTANT_String:
                sConstValue = cpConstant.refUTF8.sUTFStr;
                sConstType = "java.lang.String";
                break;
            case ConstantPoolInfo.CONSTANT_Integer:
                sConstValue = Integer.toString(cpConstant.iIntValue);
                sConstType = "int, short, char, byte, boolean";
                break;
            case ConstantPoolInfo.CONSTANT_Float:
                sConstValue = Float.toString(cpConstant.fFloatVal);
                sConstType = "float";
                break;
            case ConstantPoolInfo.CONSTANT_Long:
                sConstValue = Long.toString(cpConstant.lLongVal);
                sConstType = "long";
                break;
            case ConstantPoolInfo.CONSTANT_Double:
                sConstValue = Double.toString(cpConstant.dDoubleVal);
                sConstType = "double";
                break;
            default:
                sConstType = sConstValue = "Unknown type: " + cpConstant.iTag;
                break;
        }
    }
    
    public boolean verify(String sPrepend, Vector vectVerifyErrors) {
        switch(cpConstant.iTag) {
            case ConstantPoolInfo.CONSTANT_String:
            case ConstantPoolInfo.CONSTANT_Integer:
            case ConstantPoolInfo.CONSTANT_Float:
            case ConstantPoolInfo.CONSTANT_Long:
            case ConstantPoolInfo.CONSTANT_Double:
                return true;
            default:
                vectVerifyErrors.addElement(": Must point to a String, Integer, Float, Long or Double pool type.");
                return false;
        }
    }
    
    void writeAttributeDetails(DataOutputStream dos, ConstantPool constPoolIn) throws IOException {
        constPool = constPoolIn;
        dos.writeInt(iAttribLength);
        iConstValueIndex = constPool.getIndexOf(cpConstant);
        dos.writeShort(iConstValueIndex);
    }
    
    public String toString() {
        return "Attribute " + sName + ". Type=" + sConstType + ". Value=" + sConstValue;
    }
    
    public void setConstantValue(String sVal) {
        switch(cpConstant.iTag) {
            case ConstantPoolInfo.CONSTANT_String:
                cpConstant.refUTF8.sUTFStr = sVal;
                break;
            case ConstantPoolInfo.CONSTANT_Integer:
                cpConstant.iIntValue = Integer.parseInt(sVal);
                break;
            case ConstantPoolInfo.CONSTANT_Float:
                cpConstant.fFloatVal = Float.valueOf(sVal).floatValue();
                break;
            case ConstantPoolInfo.CONSTANT_Long:
                cpConstant.lLongVal = Long.parseLong(sVal);
                break;
            case ConstantPoolInfo.CONSTANT_Double:
                cpConstant.dDoubleVal = Double.valueOf(sVal).doubleValue();
                break;
        }
        sConstValue = sVal;
    }
}
