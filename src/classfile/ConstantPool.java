/*
 * ConstantPool.java
 *
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception
 * 1.02  29th May 1999   Tanmay   Vector instead of array,
 *                                Methods to get/set constant pool info,
 *                                Implemented reference counting for pool infos.
 * 1.03  12th Jun 1999   Tanmay   Methods to verify and get text summary added.
 * 1.04  03rd Jul 1999   Tanmay   addNewPoolInfo made public.
 * 1.05  03rd Jul 1999   Tanmay   New method removePoolInfo to delete from constant pool.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.06  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 *
 */

package classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Class to handle the constant pool.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.06, 28th Sep, 2003
 */


public class ConstantPool {
    int                 iConstantPoolCount; // as per class file entry
    int                 iNumPoolInfos;      // number of actual pool infos
    Vector              vectConstPool;      // collection of pool infos
    
    void read(DataInputStream dis) throws IOException {
        int iIndex;
        
        iConstantPoolCount  = dis.readUnsignedShort();
        // const pool index 0 is not present in the class file and is for internal use of JVMs
        iNumPoolInfos       = iConstantPoolCount - 1;
        vectConstPool       = new Vector(iNumPoolInfos);
        for (iIndex=0; iIndex < iNumPoolInfos; iIndex++) {
            ConstantPoolInfo newInfo = new ConstantPoolInfo();
            newInfo.read(dis);
            vectConstPool.addElement(newInfo);
            if (newInfo.isDoubleSizeConst()) {
                vectConstPool.addElement(newInfo); // check how indexOf works
                iIndex++;
            }
        }
    }
    
    void write(DataOutputStream dos) throws IOException {
        int iIndex;
        
        iConstantPoolCount = (iNumPoolInfos = vectConstPool.size()) + 1;
        dos.writeShort(iConstantPoolCount);
        for (iIndex=0; iIndex < iNumPoolInfos; iIndex++) {
            ConstantPoolInfo newInfo = (ConstantPoolInfo) vectConstPool.elementAt(iIndex);
            newInfo.write(dos, this);
            if (newInfo.isDoubleSizeConst()) {
                iIndex++;
            }
        }
    }
    
    void resolveReferences() {
        int iIndex;
        iConstantPoolCount = (iNumPoolInfos = vectConstPool.size()) + 1;
        for (iIndex=0; iIndex < iNumPoolInfos; iIndex++) {
            ConstantPoolInfo newInfo = (ConstantPoolInfo)vectConstPool.elementAt(iIndex);
            newInfo.resolveReferences(this);
            if (newInfo.isDoubleSizeConst()) {
                iIndex++;
            }
        }
    }
    
    void removeUnreferenced() {
        Vector newVect = new Vector(iConstantPoolCount/2);
        int iIndex;
        iConstantPoolCount = (iNumPoolInfos = vectConstPool.size()) + 1;
        for (iIndex=0; iIndex < iNumPoolInfos; iIndex++) {
            ConstantPoolInfo newInfo = (ConstantPoolInfo)vectConstPool.elementAt(iIndex);
            if (newInfo.getRef() > 0) {
                newVect.addElement(newInfo);
            }
            if (newInfo.isDoubleSizeConst()) {
                if (newInfo.getRef() > 0) {
                    newVect.addElement(newInfo);
                }
                iIndex++;
            }
        }
        vectConstPool = newVect;
    }
    
    public void addNewPoolInfo(ConstantPoolInfo cpInfo) {
        vectConstPool.addElement(cpInfo);
        cpInfo.resolveReferences(this);
        iConstantPoolCount = (iNumPoolInfos = vectConstPool.size()) + 1;
    }
    
    public void removePoolInfo(ConstantPoolInfo cpInfo) {
        // delete ref
        while(cpInfo.getRef() > 0) {
            cpInfo.deleteRef();
        }
        vectConstPool.removeElement(cpInfo);
        iConstantPoolCount = (iNumPoolInfos = vectConstPool.size()) + 1;
    }
    
    public String toString() {
        String sRetStr;
        
        iConstantPoolCount = (iNumPoolInfos = vectConstPool.size()) + 1;
        sRetStr = "Constant pool. Count: " + iConstantPoolCount;
        return sRetStr;
    }
    
    // The index is the value as referred to in the class file. The first valid index is 1.
    // The fact that we have our own vector and we may rearrange it is unknown to others.
    public ConstantPoolInfo getPoolInfo(int iIndex) {
        return (ConstantPoolInfo)vectConstPool.elementAt(iIndex-1);
    }
    
    public int getIndexOf(ConstantPoolInfo cpInfo) {
        return vectConstPool.indexOf(cpInfo)+1;
    }
    
    public int getPoolInfoCount() {
        iConstantPoolCount = (iNumPoolInfos = vectConstPool.size()) + 1;
        return iNumPoolInfos;
    }
    
    public boolean verify(Vector vectVerifyErrors) {
        boolean bRet = true;
        if (0 == getPoolInfoCount()) {
            vectVerifyErrors.addElement("Constant pool count must be greater than 0.");
            bRet = false;
        }
        for (int iIndex=0; iIndex < iNumPoolInfos; iIndex++) {
            ConstantPoolInfo newInfo = (ConstantPoolInfo)vectConstPool.elementAt(iIndex);
            bRet = (newInfo.verify("ConstPool " + (iIndex+1), vectVerifyErrors) && bRet);
        }
        return bRet;
    }
}
