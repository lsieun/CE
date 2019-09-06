/*
 * AccessFlags.java
 *
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 * 1.01  18th Mar 1999   Tanmay   Throws IOexception instead of Exception
 * 1.02  12th Jun 1999   Tanmay   Methods to verify and get text summary added
 * 1.03  05th Jan 2002   Tanmay   Split toString method to two functions.
 * 1.04  17th Jan 2002   Tanmay   New method to give valid flags. Added ACC_STRICT.
 *                                Added mechanism to distinguish between super and synchronized.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 1.05  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 */

package classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;


/**
 * Class to handle access flags.
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.05, 28th Sep, 2003
 */

public class AccessFlags {
    int     iAccessFlags;
    boolean bSuperFlagSet;
    boolean bSynchronizedFlagSet;
    
    public static final int    FIELD_VALID_FLAGS   = 0;
    public static final int    METHOD_VALID_FLAGS   = 2;
    public static final int    CLASS_VALID_FLAGS   = 4;
    
    static final int    ACC_PUBLIC          = 0x0001;
    static final int    ACC_PRIVATE         = 0x0002;
    static final int    ACC_PROTECTED       = 0x0004;
    static final int    ACC_STATIC          = 0x0008;
    static final int    ACC_FINAL           = 0x0010;
    static final int    ACC_SUPER           = 0x0020;
    static final int    ACC_VOLATILE        = 0x0040;
    static final int    ACC_TRANSIENT       = 0x0080;
    static final int    ACC_INTERFACE       = 0x0200;
    static final int    ACC_ABSTRACT        = 0x0400;
    static final int    ACC_NATIVE          = 0x0100;
    static final int    ACC_SYNCHRONIZED    = 0x0020;
    static final int    ACC_STRICT          = 0x0800;
    
    public void read(DataInputStream dis) throws IOException {
        iAccessFlags = dis.readUnsignedShort();
        
        if(ACC_SYNCHRONIZED == (ACC_SYNCHRONIZED & iAccessFlags)) {
            setSynchronized(true);
        }
        
        if(ACC_SUPER == (ACC_SUPER & iAccessFlags)) {
            setSuper(true);
        }
    }
    
    public void write(DataOutputStream dos) throws IOException {
        dos.writeShort(iAccessFlags);
    }
    
    public boolean verify(String sParent, Vector vectVerifyErrors, boolean bClassAttrib) {
        boolean bRet = true;
        int iNumFlags;
        iNumFlags=0;
        if(isPublic()) iNumFlags++;
        if(isPrivate()) iNumFlags++;
        if(isProtected()) iNumFlags++;
        if (1 < iNumFlags) {
            vectVerifyErrors.addElement(sParent + ": Only one of private, public and protected can be set.");
            bRet = false;
        }
        if (isInterface()) {
            if (!isAbstract()) {
                vectVerifyErrors.addElement(sParent + ": Interfaces must be abstract.");
                bRet = false;
            }
            if (isFinal()) {
                vectVerifyErrors.addElement(sParent + ": Interfaces can not be final.");
                bRet = false;
            }
        }
        if (isFinal() && isVolatile()) {
            vectVerifyErrors.addElement(sParent + ": Final and Volatile flags can not be set together.");
            bRet = false;
        }
        if (isAbstract()) {
            if(isFinal()) {
                vectVerifyErrors.addElement(sParent + ": Abstract and final flags can not be set together.");
                bRet = false;
            }
            if(isNative()) {
                vectVerifyErrors.addElement(sParent + ": Abstract and native flags can not be set together.");
                bRet = false;
            }
            if (!bClassAttrib) {
                if(isSynchronized()) {
                    vectVerifyErrors.addElement(sParent + ": Abstract and synchronized flags can not be set together.");
                    bRet = false;
                }
            }
        }
        return bRet;
    }
    
    public String getAccessString() {
        String sAFlags = "";
        if (isPublic())         sAFlags += " public";
        if (isPrivate())        sAFlags += " private";
        if (isProtected())      sAFlags += " protected";
        return sAFlags.trim();
    }
    
    public String getModifierString() {
        String sAFlags = "";
        if (isStatic())         sAFlags += " static";
        if (isFinal())          sAFlags += " final";
        if (isSuper())          sAFlags += " super";
        if (isVolatile())       sAFlags += " volatile";
        if (isTransient())      sAFlags += " transient";
        if (isInterface())      sAFlags += " interface";
        if (isAbstract())       sAFlags += " abstract";
        if (isNative())         sAFlags += " native";
        if (isSynchronized())   sAFlags += " synchronized";
        if (isStrict())         sAFlags += " strict";
        return sAFlags.trim();
    }
    
    public String toString() {
        return (getAccessString() + " " + getModifierString()).trim();
    }
    
    public boolean isPublic() {
        return (ACC_PUBLIC    == (ACC_PUBLIC    & iAccessFlags));
    }
    public boolean isPrivate() {
        return (ACC_PRIVATE   == (ACC_PRIVATE   & iAccessFlags));
    }
    public boolean isProtected() {
        return (ACC_PROTECTED == (ACC_PROTECTED & iAccessFlags));
    }
    public boolean isStatic() {
        return (ACC_STATIC    == (ACC_STATIC    & iAccessFlags));
    }
    public boolean isFinal() {
        return (ACC_FINAL     == (ACC_FINAL     & iAccessFlags));
    }
    public boolean isSuper() {
        if(bSuperFlagSet) {
            return (ACC_SUPER     == (ACC_SUPER     & iAccessFlags));
        }
        return false;
    }
    public boolean isVolatile() {
        return (ACC_VOLATILE  == (ACC_VOLATILE  & iAccessFlags));
    }
    public boolean isTransient() {
        return (ACC_TRANSIENT == (ACC_TRANSIENT & iAccessFlags));
    }
    public boolean isInterface() {
        return (ACC_INTERFACE == (ACC_INTERFACE & iAccessFlags));
    }
    public boolean isAbstract() {
        return (ACC_ABSTRACT == (ACC_ABSTRACT & iAccessFlags));
    }
    public boolean isNative() {
        return (ACC_NATIVE == (ACC_NATIVE & iAccessFlags));
    }
    public boolean isSynchronized() {
        if(bSynchronizedFlagSet) {
            return (ACC_SYNCHRONIZED == (ACC_SYNCHRONIZED & iAccessFlags));
        }
        return false;
    }
    public boolean isStrict() {
        return (ACC_STRICT == (ACC_STRICT & iAccessFlags));
    }
    
    
    public void setPublic(boolean bToSet) {
        if (bToSet) {
            iAccessFlags |= ACC_PUBLIC;
        }
        else {
            if (isPublic()) iAccessFlags &= (iAccessFlags^ACC_PUBLIC);
        }
    }
    public void setPrivate(boolean bToSet) {
        if (bToSet) {
            iAccessFlags |= ACC_PRIVATE;
        }
        else {
            if(isPrivate()) iAccessFlags &= (iAccessFlags^ACC_PRIVATE);
        }
    }
    public void setProtected(boolean bToSet) {
        if (bToSet) {
            iAccessFlags |= ACC_PROTECTED;
        }
        else {
            if(isProtected()) iAccessFlags &= (iAccessFlags^ACC_PROTECTED);
        }
    }
    public void setStatic(boolean bToSet) {
        if (bToSet) {
            iAccessFlags |= ACC_STATIC;
        }
        else {
            if(isStatic()) iAccessFlags &= (iAccessFlags^ACC_STATIC);
        }
    }
    public void setFinal(boolean bToSet) {
        if (bToSet) {
            iAccessFlags |= ACC_FINAL;
        }
        else {
            if(isFinal()) iAccessFlags &= (iAccessFlags^ACC_FINAL);
        }
    }
    public void setSuper(boolean bToSet) {
        if (bToSet) {
            bSuperFlagSet = true;
            iAccessFlags |= ACC_SUPER;
        }
        else {
            if(isSuper()) {
                if(!isSynchronized()) {
                    iAccessFlags &= (iAccessFlags^ACC_SUPER);
                }
                bSuperFlagSet = false;
            }
        }
    }
    public void setVolatile(boolean bToSet) {
        if (bToSet) {
            iAccessFlags |= ACC_VOLATILE;
        }
        else {
            if(isVolatile()) iAccessFlags &= (iAccessFlags^ACC_VOLATILE);
        }
    }
    public void setTransient(boolean bToSet) {
        if (bToSet) {
            iAccessFlags |= ACC_TRANSIENT;
        }
        else {
            if(isTransient()) iAccessFlags &= (iAccessFlags^ACC_TRANSIENT);
        }
    }
    public void setInterface(boolean bToSet) {
        if (bToSet) {
            iAccessFlags |= ACC_INTERFACE;
        }
        else {
            if(isInterface()) iAccessFlags &= (iAccessFlags^ACC_INTERFACE);
        }
    }
    public void setAbstract(boolean bToSet) {
        if (bToSet) {
            iAccessFlags |= ACC_ABSTRACT;
        }
        else {
            if(isAbstract()) iAccessFlags &= (iAccessFlags^ACC_ABSTRACT);
        }
    }
    public void setNative(boolean bToSet) {
        if (bToSet) {
            iAccessFlags |= ACC_NATIVE;
        }
        else {
            if(isNative()) iAccessFlags &= (iAccessFlags^ACC_NATIVE);
        }
    }
    
    public void setSynchronized(boolean bToSet) {
        if (bToSet) {
            bSynchronizedFlagSet = true;
            iAccessFlags |= ACC_SYNCHRONIZED;
        }
        else {
            if(isSynchronized()) {
                if(!isSuper()) {
                    iAccessFlags &= (iAccessFlags^ACC_SYNCHRONIZED);
                }
                bSynchronizedFlagSet = false;
            }
        }
    }
    
    public void setStrict(boolean bToSet) {
        if (bToSet) {
            iAccessFlags |= ACC_STRICT;
        }
        else {
            if(isStrict()) iAccessFlags &= (iAccessFlags^ACC_STRICT);
        }
    }
    
    /** Flags valied for:
     * Class file: public, final, super, interface, abstract
     * Fields: public, private, protected, static, final, volatile, transient
     * Methods: public, private, protected, static, final, synchronized, native, abstract, strict
     */
    public static AccessFlags getValidFlags(int iWhat) {
        AccessFlags flg = new AccessFlags();
        
        flg.setPublic(true);
        
        if( (iWhat == FIELD_VALID_FLAGS) || (iWhat == METHOD_VALID_FLAGS) ) {
            flg.setPrivate(true);
            flg.setProtected(true);
            flg.setStatic(true);
            flg.setFinal(true);
        }
        
        if(iWhat == FIELD_VALID_FLAGS) {
            flg.setVolatile(true);
            flg.setTransient(true);
        }
        else if(iWhat == METHOD_VALID_FLAGS) {
            flg.setSynchronized(true);
            flg.setNative(true);
            flg.setAbstract(true);
            flg.setStrict(true);
        }
        else if(iWhat == CLASS_VALID_FLAGS) {
            flg.setFinal(true);
            flg.setSuper(true);
            flg.setInterface(true);
            flg.setAbstract(true);
        }
        
        return flg;
    }
}
