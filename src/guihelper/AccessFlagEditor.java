/*
 * AccessFlagEditor.java
 *
 * Created on January 14, 2002, 10:42 AM
 *
 * Modification Log:
 * 1.00   14th Jan 2002   Tanmay   Original version.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */

package guihelper;


import classfile.AccessFlags;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** This class provides the cell editor for access flag cells.
 * <br><br>
 *
 *
 * @author     Tanmay K. Mohapatra
 * @version    1.00, 14th Jan, 2002
 */

public class AccessFlagEditor extends DefaultCellEditor {
    AccessFlags currFlags;
    Component editorComponent;

    /** Creates new AccessFlagRenderer */
    public AccessFlagEditor(JButton btnEditor) {
        super(new JCheckBox());
        editorComponent = btnEditor;
        setClickCountToStart(1);
        btnEditor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    protected void fireEditingStopped(){
        super.fireEditingStopped();
    }
    
    public Object getCellEditorValue() {
        return currFlags;
    }

    public java.awt.Component getTableCellEditorComponent(JTable jTable, java.lang.Object obj, boolean param, int param4, int param5) {
        currFlags = (AccessFlags)obj;
        ((JButton)editorComponent).setText(currFlags.toString());
        return editorComponent;
    }
    
}

