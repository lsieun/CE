/*
 * Version.java
 *
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception
 * 1.02  29th May 1999   Tanmay   Added methods to get/set and verify the values
 * 1.03  12th Jun 1999   Tanmay   Method to get text summary added.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.04  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 * 1.05  21st Mar 2004   Tanmay   New method getMagicNumberInteger added for XML output
 *
 */


package classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Class to handle version information.
 * <br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.05, 21st Mar, 2004
 */

public final class Version {
    int             iMagicNumber;
    int             iMinorVersion;
    int             iMajorVersion;
    
    void read(DataInputStream dis) throws IOException {
        iMagicNumber        = dis.readInt();
        iMinorVersion       = dis.readUnsignedShort();
        iMajorVersion       = dis.readUnsignedShort();
    }
    
    void write(DataOutputStream dos) throws IOException {
        dos.writeInt(iMagicNumber);
        dos.writeShort(iMinorVersion);
        dos.writeShort(iMajorVersion);
    }
    
    public boolean verify(Vector vectVerifyErrors) {
        boolean bRet = true;
        
        if (0xcafebabe != iMagicNumber) {
            vectVerifyErrors.addElement("Magic number should be CAFEBABE.");
            bRet = false;
        }
        if (45 != iMajorVersion) {
            vectVerifyErrors.addElement("Major version should be 45.");
            bRet = false;
        }
        if (3 != iMinorVersion) {
            vectVerifyErrors.addElement("Minor version should be 3.");
            bRet = false;
        }
        return bRet;
    }
    
    public String toString() {
        String sRetStr;
        
        sRetStr = ("Magic number: " + getMagicNumberString() + ", ");
        sRetStr += ("Minor version: " + iMinorVersion + ", ");
        sRetStr += ("Major version: " + iMajorVersion + ", ");
        
        return sRetStr;
    }
    
    public int getMagicNumberInteger() {
        return iMagicNumber;
    }
    
    public String getMagicNumberString() {
        return (Integer.toHexString(iMagicNumber));
    }
    
    public void setMagicNumberString(String sMagicNumber) {
        iMagicNumber = Long.valueOf(sMagicNumber, 16).intValue();
    }
    
    public String getMajorVersionString() {
        return Integer.toString(iMajorVersion);
    }
    
    public String getMinorVersionString() {
        return Integer.toString(iMinorVersion);
    }
    
    public void setMajorVersionString(String sVer) {
        iMajorVersion = Integer.parseInt(sVer);
    }
    
    public void setMinorVersionString(String sVer) {
        iMinorVersion = Integer.parseInt(sVer);
    }
}
