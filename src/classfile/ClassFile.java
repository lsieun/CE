/*
 * ClassFile.java
 *
 * Created on March 12th, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception. Some attributes
 *                                need constant pool for read.
 * 1.02  12th Jun 1999   Tanmay   Methods to verify and get text summary added.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.03  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 *
 */


package classfile;

import classfile.attributes.Attributes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;


/**
 * Reads, writes and manipulates the whole class file. Contains other classes
 * to perform the same job on different sections.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.03, 28th Sep, 2003
 */


public class ClassFile {
    public Version         version         = new Version();
    public ConstantPool    constantPool    = new ConstantPool();
    public AccessFlags     accessFlags     = new AccessFlags();
    public ClassNames      classNames      = new ClassNames();
    public Interfaces      interfaces      = new Interfaces();
    public Fields          fields          = new Fields();
    public Methods         methods         = new Methods();
    public Attributes      attributes      = new Attributes();
    
    public int             iReadWriteStage;
    
    public void createSimplestClass() throws IOException {
        byte [] bClassDump = {
            (byte)0xca, (byte)0xfe, (byte)0xba, (byte)0xbe, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x2d, (byte)0x00, (byte)0x0a, (byte)0x0a, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x07, (byte)0x07,
            (byte)0x00, (byte)0x08, (byte)0x07, (byte)0x00, (byte)0x09, (byte)0x01, (byte)0x00, (byte)0x06, (byte)0x3c, (byte)0x69, (byte)0x6e, (byte)0x69, (byte)0x74, (byte)0x3e, (byte)0x01, (byte)0x00,
            (byte)0x03, (byte)0x28, (byte)0x29, (byte)0x56, (byte)0x01, (byte)0x00, (byte)0x04, (byte)0x43, (byte)0x6f, (byte)0x64, (byte)0x65, (byte)0x0c, (byte)0x00, (byte)0x04, (byte)0x00, (byte)0x05,
            (byte)0x01, (byte)0x00, (byte)0x06, (byte)0x73, (byte)0x69, (byte)0x6d, (byte)0x70, (byte)0x6c, (byte)0x65, (byte)0x01, (byte)0x00, (byte)0x10, (byte)0x6a, (byte)0x61, (byte)0x76, (byte)0x61,
            (byte)0x2f, (byte)0x6c, (byte)0x61, (byte)0x6e, (byte)0x67, (byte)0x2f, (byte)0x4f, (byte)0x62, (byte)0x6a, (byte)0x65, (byte)0x63, (byte)0x74, (byte)0x00, (byte)0x21, (byte)0x00, (byte)0x02,
            (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x04, (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x01,
            (byte)0x00, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x11, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x05, (byte)0x2a, (byte)0xb7,
            (byte)0x00, (byte)0x01, (byte)0xb1, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00
        };
        ByteArrayInputStream bais = new ByteArrayInputStream(bClassDump);
        DataInputStream dis = new DataInputStream(bais);
        read(dis);
    }
    
    /**
     * reads the class file into data structures.
     */
    public void read(DataInputStream dis) throws IOException {
        iReadWriteStage = 0;
        version.read(dis);
        iReadWriteStage = 1;
        
        constantPool.read(dis);
        constantPool.resolveReferences();
        iReadWriteStage = 2;
        
        accessFlags.read(dis);
        iReadWriteStage = 3;
        classNames.read(dis, constantPool);
        iReadWriteStage = 4;
        
        interfaces.read(dis, constantPool);
        iReadWriteStage = 5;
        fields.read(dis, constantPool);
        iReadWriteStage = 6;
        methods.read(dis, constantPool);
        iReadWriteStage = 7;
        
        attributes.read(dis, constantPool);
        iReadWriteStage = 8;
    }
    
    /**
     * writes a class file from data structures.
     */
    public void write(DataOutputStream dos) throws IOException {
        iReadWriteStage = 0;
        version.write(dos);
        iReadWriteStage = 1;
        
        //constantPool.removeUnreferenced();
        constantPool.write(dos);
        iReadWriteStage = 2;
        
        accessFlags.write(dos);
        iReadWriteStage = 3;
        classNames.write(dos, constantPool);
        iReadWriteStage = 4;
        
        interfaces.write(dos, constantPool);
        iReadWriteStage = 5;
        fields.write(dos, constantPool);
        iReadWriteStage = 6;
        methods.write(dos, constantPool);
        iReadWriteStage = 7;
        
        attributes.write(dos, constantPool);
        iReadWriteStage = 8;
    }
    
    public boolean verify(Vector vectVerifyErrors) {
        boolean bRet = true;
        
        bRet = (version.verify(vectVerifyErrors) && bRet);
        bRet = (constantPool.verify(vectVerifyErrors) && bRet);
        bRet = (accessFlags.verify("ClassFile", vectVerifyErrors, true) && bRet);
        bRet = (classNames.verify(vectVerifyErrors) && bRet);
        bRet = (interfaces.verify(vectVerifyErrors) && bRet);
        bRet = (fields.verify(vectVerifyErrors) && bRet);
        bRet = (methods.verify(vectVerifyErrors) && bRet);
        bRet = (attributes.verify("ClassFile", vectVerifyErrors) && bRet);
        return bRet;
    }
    
    /**
     * Give a string representation of the class file with details.
     */
    public String toString() {
        return "ClassEditor: " + classNames.getThisClassName();
    }
}
