/*
 * AttributeDialog.java
 *
 * Created on March 12, 1999, 10:10 AM
 *
 * Modification Log:
 * 1.00   12th Mar 1999   Tanmay   Original version.
 * 2.00   16th Dec 2001   Tanmay   Moved over to Java Swing.
 * 3.00   27th Jan 2002   Tanmay   Split into multiple classes and tree based UI added
 * 3.01   02nd Feb 2002   Tanmay   Implemented InnerClasses, Synthetic and Deprecated attribute display.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 *
 */


package gui.attributes;

import classfile.*;
import classfile.attributes.*;
import guihelper.AttributeTreeNode;
import javax.swing.tree.*;
import javax.swing.*;

/**
 * The Attributes dialog. USed for all attribute displays.
 *
 * @author 	Tanmay K. Mohapatra
 * @version     3.01, 02nd February, 2002
 */
public class AttributesDialog extends javax.swing.JDialog {
    
    private Attributes attributeList;
    private ConstantPool constPool;
    private boolean bModifyMode;
    private DefaultMutableTreeNode rootTreeNode;
    
    private SourceFileAttribPane srcFileAttribPane;
    private ConstantValueAttribPane constValAttribPane;
    private CodeAttribPane codeAttribPane;
    private ExceptionsAttribPane exceptionsAttribPane;
    private InnerClassesAttribPane innerClassesAttribPane;
    private LineNumberAttribPane lineNumberAttribPane;
    private LocalVariableAttribPane localVariableTableAttribPane;
    private UnknownAttribPane unknownAttribPane;
    
    /** Creates new form AttributesDialog */
    public AttributesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        rootTreeNode = new DefaultMutableTreeNode("Attributes");
        initComponents();
    }
    
    public void setInput(Attributes attributeList, ConstantPool constPool, boolean bModifyMode) {
        this.attributeList = attributeList;
        this.constPool = constPool;
        this.bModifyMode = bModifyMode;
        populateTree();
        treeAttributes.setSelectionRow(0);
    }
    
    private void populateTree() {
        if ( (null == attributeList) || (null == constPool) ) {
            return;
        }
        clearTree();
        AttributeTreeNode.makeTree(attributeList, rootTreeNode);
        treeAttributes.setModel(new DefaultTreeModel(rootTreeNode));
    }
    
    private void clearTree() {
        treeAttributes.setModel(new DefaultTreeModel(rootTreeNode));
    }
    
    private JPanel createAndGetPanelToAdd(Attribute thisAttrib) {
        JPanel panelToAdd = null;
        
        if (thisAttrib instanceof SourceFileAttribute) {
            if(null == srcFileAttribPane) srcFileAttribPane = new SourceFileAttribPane(bModifyMode);
            panelToAdd = srcFileAttribPane;
        }
        else if (thisAttrib instanceof ConstantValueAttribute) {
            if(null == constValAttribPane) constValAttribPane = new ConstantValueAttribPane(bModifyMode);
            panelToAdd = constValAttribPane;
        }
        else if (thisAttrib instanceof ExceptionsAttribute) {
            if(null == exceptionsAttribPane) exceptionsAttribPane = new ExceptionsAttribPane(bModifyMode);
            panelToAdd = exceptionsAttribPane;
        }
        else if (thisAttrib instanceof CodeAttribute) {
            if(null == codeAttribPane) codeAttribPane = new CodeAttribPane(bModifyMode);
            panelToAdd = codeAttribPane;
        }
        else if (thisAttrib instanceof LineNumberTableAttribute) {
            if(null == lineNumberAttribPane) lineNumberAttribPane = new LineNumberAttribPane(bModifyMode);
            panelToAdd = lineNumberAttribPane;
        }
        else if (thisAttrib instanceof LocalVariableTableAttribute) {
            if(null == localVariableTableAttribPane) localVariableTableAttribPane = new LocalVariableAttribPane(bModifyMode);
            panelToAdd = localVariableTableAttribPane;
        }
        else if (thisAttrib instanceof InnerClassesAttribute) {
            if(null == innerClassesAttribPane) innerClassesAttribPane = new InnerClassesAttribPane(bModifyMode);
            panelToAdd = innerClassesAttribPane;
        }
        else if (thisAttrib instanceof UnknownAttribute) {
            if(null == unknownAttribPane) unknownAttribPane = new UnknownAttribPane(bModifyMode);
            panelToAdd = unknownAttribPane;
        }
        
        return panelToAdd;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jSplitPane1 = new javax.swing.JSplitPane();
        attribDisplayPane = new javax.swing.JPanel();
        treeAttributes = new javax.swing.JTree();
        
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        
        jSplitPane1.setDividerLocation(150);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(462, 402));
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setAutoscrolls(true);
        attribDisplayPane.setLayout(new java.awt.GridLayout(1, 0));
        
        attribDisplayPane.setPreferredSize(new java.awt.Dimension(600, 400));
        attribDisplayPane.setMinimumSize(new java.awt.Dimension(300, 400));
        jSplitPane1.setRightComponent(attribDisplayPane);
        
        treeAttributes.setShowsRootHandles(true);
        treeAttributes.setPreferredSize(new java.awt.Dimension(100, 400));
        treeAttributes.setRootVisible(false);
        treeAttributes.setMinimumSize(new java.awt.Dimension(100, 400));
        treeAttributes.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeAttributesValueChanged(evt);
            }
        });
        
        jSplitPane1.setLeftComponent(treeAttributes);
        
        getContentPane().add(jSplitPane1);
        
        pack();
    }//GEN-END:initComponents
    
    private void treeAttributesValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeAttributesValueChanged
        AttributeTreeNode node = (AttributeTreeNode)treeAttributes.getLastSelectedPathComponent();
        
        if (node == null) return;
        
        Attribute thisAttrib = node.attr;
        
        attribDisplayPane.removeAll();
        JPanel panelToAdd = createAndGetPanelToAdd(thisAttrib);
        
        if(null != panelToAdd) {
            jSplitPane1.setRightComponent(panelToAdd);
            ((AttribDisplay)panelToAdd).setInput(thisAttrib, constPool);
        }
        else {
            jSplitPane1.setRightComponent(attribDisplayPane);
        }
    }//GEN-LAST:event_treeAttributesValueChanged
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel attribDisplayPane;
    private javax.swing.JTree treeAttributes;
    // End of variables declaration//GEN-END:variables
    
}