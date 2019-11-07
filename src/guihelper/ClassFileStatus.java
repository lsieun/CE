/*
 * ClassFileStatus.java
 *
 * Created on April 29, 2001
 *
 * Modification Log:
 * 1.00   29th Apr 2001   Tanmay   Original version.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */

package guihelper;

import classfile.ClassFile;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

/**
 * Class to maintain status of an open file. The GUI class can keep an
 * array of this class to maintain multiple open files. Provides a level
 * of abstraction from file paths, keeps track of backups and
 * modification status.
 * <br><br>
 *
 * @author Tanmay K. Mohapatra
 * @version 1.00, 29th April, 2001
 */

public class ClassFileStatus extends DefaultMutableTreeNode {
    public ClassFile classFile;
    boolean bDirty; // modified, save required
    public boolean bBackupCreated;
    public String sClassName;
    public String sPath;
    public String sFileName;

    public ClassFileStatus(String sFileNameIn, ClassFile classFileIn) {
        sFileName = sFileNameIn;
        File fTemp = new java.io.File(sFileName);
        sClassName = fTemp.getName();
        sPath = fTemp.getAbsolutePath();
        classFile = classFileIn;
        this.setUserObject(getTreeDisplayString());
    }

    public String getTreeDisplayString() {
        String sStr = sClassName;
        if (bDirty) sStr += "*";
        sStr += (" (" + sPath + ")");
        return sStr;
    }

    public void setDirtyFlag(boolean bDirtyIn) {
        if (bDirty != bDirtyIn) {
            bDirty = bDirtyIn;
            setUserObject(getTreeDisplayString());
        }
    }
}
