/*
 * GeneralPropPane.java
 *
 * Created on September 30, 2001, 3:34 PM
 *
 * Modification Log:
 * 1.00   30th Sep 2001   Tanmay   Original version.
 * 1.01   30th Jan 2002   Tanmay   Implemented toggle modify mode.
 * 1.02   02nd Feb 2002   Tanmay   Moved over to new attributes dialog.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */

package gui;

import classfile.ClassFile;
import classfile.ConstantPoolInfo;
import gui.attributes.AttributesDialog;
import guihelper.AttributeTreeNode;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * Copyright (C) 2002-2003  Tanmay K. Mohapatra
 * <br>
 *
 * @author 	Tanmay K. Mohapatra
 * @version     1.02, 02nd February, 2002
 */
public class GeneralPropPane extends JPanel {
    
    private ClassFile currClassFile;
    private DefaultMutableTreeNode interfacesRootNode;
    private boolean bEditable;
    
    /** Creates new form GeneralPropPane */
    public GeneralPropPane() {
        initComponents();
        treeInterfaces.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ((DefaultTreeCellRenderer)treeInterfaces.getCellRenderer()).setLeafIcon(new ImageIcon(getClass().getResource("/res/interface.gif")));
    }
    
    public void setModifyMode(boolean bEditableIn) {
        bEditable = bEditableIn;
        
        txtClassName.setEnabled(bEditable);
        txtSuperClassName.setEnabled(bEditable);
        txtMagicNumber.setEnabled(bEditable);
        txtMajorVersion.setEnabled(bEditable);
        txtMinorVersion.setEnabled(bEditable);
        
        chkFinal.setEnabled(bEditable);
        chkPublic.setEnabled(bEditable);
        chkSuper.setEnabled(bEditable);
        chkInterface.setEnabled(bEditable);
        chkAbstract.setEnabled(bEditable);
        
        txtNewInterfaceName.setEnabled(bEditable);
        txtConstPoolIndex.setEnabled(bEditable);
        
        btnDiscardChanges.setEnabled(bEditable);
        btnAcceptChanges.setEnabled(bEditable);
        btnDeleteInterface.setEnabled(bEditable);
        btnModifyInterface.setEnabled(bEditable);
        btnAddNewInterface.setEnabled(bEditable);
    }
    
    private void modifyInterfaceName(int iIndex) {
        currClassFile.interfaces.setInterfaceName(iIndex, txtNewInterfaceName.getText());
        refreshInterfaceList();
    }
    
    private void deleteInterface(int iIndex) {
        currClassFile.interfaces.removeInterface(iIndex);
        refreshInterfaceList();
    }
    
    /**
     * Clears the UI components in preparation for display of a new class
     */
    void clear() {
        txtClassName.setText("");
        txtSuperClassName.setText("");
        txtMagicNumber.setText("");
        txtMajorVersion.setText("");
        txtMinorVersion.setText("");
        
        chkFinal.setSelected(false);
        chkPublic.setSelected(false);
        chkSuper.setSelected(false);
        chkInterface.setSelected(false);
        chkAbstract.setSelected(false);
        
        interfacesRootNode.removeAllChildren();
        treeInterfaces.setModel(new DefaultTreeModel(interfacesRootNode));
        txtNewInterfaceName.setText("");
        txtConstPoolIndex.setText("");
    }
    
    void setClassFile(ClassFile classFile) {
        currClassFile = classFile;
    }
    
    void refresh() {
        if(null == currClassFile) return;
        
        txtClassName.setText(currClassFile.classNames.getThisClassName());
        txtSuperClassName.setText(currClassFile.classNames.getSuperClassName());
        txtMagicNumber.setText(currClassFile.version.getMagicNumberString());
        txtMajorVersion.setText(currClassFile.version.getMajorVersionString());
        txtMinorVersion.setText(currClassFile.version.getMinorVersionString());
        
        chkFinal.setSelected(currClassFile.accessFlags.isFinal());
        chkPublic.setSelected(currClassFile.accessFlags.isPublic());
        chkSuper.setSelected(currClassFile.accessFlags.isSuper());
        chkInterface.setSelected(currClassFile.accessFlags.isInterface());
        chkAbstract.setSelected(currClassFile.accessFlags.isAbstract());
        refreshInterfaceList();
    }
    
    private void refreshInterfaceList() {
        interfacesRootNode.removeAllChildren();
        int iNumInterfaces = currClassFile.interfaces.getInterfacesCount();
        for(int iIndex=0; iIndex < iNumInterfaces; iIndex++) {
            String sName = currClassFile.interfaces.getInterfaceName(iIndex);
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(sName);
            treeNode.setUserObject(sName);
            interfacesRootNode.add(treeNode);
        }
        treeInterfaces.setModel(new DefaultTreeModel(interfacesRootNode));
    }
    
    private void saveChanges() {
        if(null == currClassFile) return;
        
        currClassFile.version.setMagicNumberString(txtMagicNumber.getText());
        currClassFile.version.setMajorVersionString(txtMajorVersion.getText());
        currClassFile.version.setMinorVersionString(txtMinorVersion.getText());
        currClassFile.classNames.setThisClassName(txtClassName.getText());
        currClassFile.classNames.setSuperClassName(txtSuperClassName.getText());
        
        currClassFile.accessFlags.setFinal(chkFinal.isSelected());
        currClassFile.accessFlags.setPublic(chkPublic.isSelected());
        currClassFile.accessFlags.setSuper(chkSuper.isSelected());
        currClassFile.accessFlags.setInterface(chkInterface.isSelected());
        currClassFile.accessFlags.setAbstract(chkAbstract.isSelected());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new JPanel();
        jPanel2 = new JPanel();
        lblClassName = new JLabel();
        txtClassName = new JTextField();
        lblSuperClassName = new JLabel();
        txtSuperClassName = new JTextField();
        jPanel3 = new JPanel();
        lblMagicNumber = new JLabel();
        txtMagicNumber = new JTextField();
        lblMajorVersion = new JLabel();
        txtMajorVersion = new JTextField();
        lblMinorVersion = new JLabel();
        txtMinorVersion = new JTextField();
        jPanel4 = new JPanel();
        chkFinal = new JCheckBox();
        chkPublic = new JCheckBox();
        chkInterface = new JCheckBox();
        chkSuper = new JCheckBox();
        chkAbstract = new JCheckBox();
        jPanel5 = new JPanel();
        btnShowEditAttribs = new JButton();
        jPanel6 = new JPanel();
        btnDiscardChanges = new JButton();
        btnAcceptChanges = new JButton();
        jPanel7 = new JPanel();
        jScrollPane1 = new JScrollPane();
        treeInterfaces = new JTree(interfacesRootNode = new DefaultMutableTreeNode(""));
        btnDeleteInterface = new JButton();
        lblModifiedName = new JLabel();
        txtNewInterfaceName = new JTextField();
        btnModifyInterface = new JButton();
        lblConstPoolIndex = new JLabel();
        txtConstPoolIndex = new JTextField();
        btnAddNewInterface = new JButton();
        txtHelpText = new JTextArea();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(new TitledBorder(null, "Class Properties", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBorder(new TitledBorder("Names"));
        lblClassName.setText("Class Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(lblClassName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel2.add(txtClassName, gridBagConstraints);

        lblSuperClassName.setText("Super Class");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(lblSuperClassName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel2.add(txtSuperClassName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBorder(new TitledBorder("Version"));
        lblMagicNumber.setText("Magic Number");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(lblMagicNumber, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel3.add(txtMagicNumber, gridBagConstraints);

        lblMajorVersion.setText("Major Version");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(lblMajorVersion, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel3.add(txtMajorVersion, gridBagConstraints);

        lblMinorVersion.setText("Minor Version");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(lblMinorVersion, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel3.add(txtMinorVersion, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel3, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridLayout(2, 3));

        jPanel4.setBorder(new TitledBorder("Access"));
        chkFinal.setText("final");
        jPanel4.add(chkFinal);

        chkPublic.setText("public");
        jPanel4.add(chkPublic);

        chkInterface.setText("interface");
        jPanel4.add(chkInterface);

        chkSuper.setText("super");
        jPanel4.add(chkSuper);

        chkAbstract.setText("abstract");
        jPanel4.add(chkAbstract);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel4, gridBagConstraints);

        jPanel5.setBorder(new TitledBorder("Attributes"));
        btnShowEditAttribs.setText("Show/Edit Attributes");
        btnShowEditAttribs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowEditAttribsActionPerformed(evt);
            }
        });

        jPanel5.add(btnShowEditAttribs);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel5, gridBagConstraints);

        jPanel6.setBorder(new TitledBorder("Property Changes"));
        btnDiscardChanges.setText("Discard");
        btnDiscardChanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiscardChangesActionPerformed(evt);
            }
        });

        jPanel6.add(btnDiscardChanges);

        btnAcceptChanges.setText("Accept");
        btnAcceptChanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcceptChangesActionPerformed(evt);
            }
        });

        jPanel6.add(btnAcceptChanges);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        jPanel7.setLayout(new java.awt.GridBagLayout());

        jPanel7.setBorder(new TitledBorder("Interfaces"));
        treeInterfaces.setToolTipText("");
        treeInterfaces.setEditable(true);
        treeInterfaces.setRootVisible(false);
        treeInterfaces.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent evt) {
                treeInterfacesValueChanged(evt);
            }
        });

        jScrollPane1.setViewportView(treeInterfaces);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel7.add(jScrollPane1, gridBagConstraints);

        btnDeleteInterface.setIcon(new ImageIcon(getClass().getResource("/res/close1.gif")));
        btnDeleteInterface.setText("Delete");
        btnDeleteInterface.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteInterfaceActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel7.add(btnDeleteInterface, gridBagConstraints);

        lblModifiedName.setText("Modified Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel7.add(lblModifiedName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(txtNewInterfaceName, gridBagConstraints);

        btnModifyInterface.setText("Modify");
        btnModifyInterface.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModifyInterfaceActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel7.add(btnModifyInterface, gridBagConstraints);

        lblConstPoolIndex.setText("Constant Pool Index");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        jPanel7.add(lblConstPoolIndex, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(txtConstPoolIndex, gridBagConstraints);

        btnAddNewInterface.setText("Add New");
        btnAddNewInterface.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewInterfaceActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel7.add(btnAddNewInterface, gridBagConstraints);

        txtHelpText.setWrapStyleWord(true);
        txtHelpText.setLineWrap(true);
        txtHelpText.setEditable(false);
        txtHelpText.setText("To remove an interface, select interface name from list and push the \"Delete\" button.\nTo modify an interface name, select interface name from list, modify it in the text field below and push the \"Modify\" button.\nTo add a new interface, enter a constant pool index of type ClassInfo in the text field and push the \"Add New\" botton.");
        txtHelpText.setBackground(new java.awt.Color(204, 204, 255));
        txtHelpText.setMargin(new java.awt.Insets(4, 4, 0, 0));
        txtHelpText.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.25;
        jPanel7.add(txtHelpText, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel7, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void btnShowEditAttribsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowEditAttribsActionPerformed
        if ((null == currClassFile) || (null == currClassFile.attributes)) {
            return;
        }
        
        AttributesDialog attribsDial = new AttributesDialog(AttributeTreeNode.getFrameFrom(this), true);
        attribsDial.setTitle("Class Attributes");
        attribsDial.setInput(currClassFile.attributes, currClassFile.constantPool, bEditable);
        attribsDial.show();
    }//GEN-LAST:event_btnShowEditAttribsActionPerformed
    
    private void btnDeleteInterfaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteInterfaceActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeInterfaces.getLastSelectedPathComponent();
        if (node == null) return;
        DefaultTreeModel treeModel = (DefaultTreeModel)treeInterfaces.getModel();
        int iSelected = treeModel.getIndexOfChild(treeModel.getRoot(), node);
        if ( (null != currClassFile) && (iSelected >= 0) ) {
            deleteInterface(iSelected);
        }
    }//GEN-LAST:event_btnDeleteInterfaceActionPerformed
    
    private void btnAddNewInterfaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewInterfaceActionPerformed
        if (null == currClassFile) return;
        int iIndex;
        String sIndex = txtConstPoolIndex.getText();
        sIndex = sIndex.trim();
        if (sIndex.length() <= 0) return;
        try {
            iIndex = Integer.parseInt(sIndex);
        }
        catch(NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null,
            "Constant pool index must be an integer greater than 0.",
            "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (iIndex <= 0) return;
        ConstantPoolInfo cpClass = currClassFile.constantPool.getPoolInfo(iIndex);
        if(ConstantPoolInfo.CONSTANT_Class != cpClass.iTag) {
            JOptionPane.showMessageDialog(null,
            "Constant pool entry " + iIndex + " is not of type Class.",
            "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        currClassFile.interfaces.addInterface(cpClass);
        refreshInterfaceList();
    }//GEN-LAST:event_btnAddNewInterfaceActionPerformed
    
    private void btnModifyInterfaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModifyInterfaceActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeInterfaces.getLastSelectedPathComponent();
        if (node == null) return;
        DefaultTreeModel treeModel = (DefaultTreeModel)treeInterfaces.getModel();
        int iSelected = treeModel.getIndexOfChild(treeModel.getRoot(), node);
        if ( (null != currClassFile) && (iSelected >= 0) ) {
            modifyInterfaceName(iSelected);
        }
    }//GEN-LAST:event_btnModifyInterfaceActionPerformed
    
    private void treeInterfacesValueChanged(TreeSelectionEvent evt) {//GEN-FIRST:event_treeInterfacesValueChanged
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeInterfaces.getLastSelectedPathComponent();
        if (node == null) return;
        txtNewInterfaceName.setText((String)node.getUserObject());
    }//GEN-LAST:event_treeInterfacesValueChanged
    
    private void btnAcceptChangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcceptChangesActionPerformed
        saveChanges();
    }//GEN-LAST:event_btnAcceptChangesActionPerformed
    
    private void btnDiscardChangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiscardChangesActionPerformed
        refresh();
    }//GEN-LAST:event_btnDiscardChangesActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAcceptChanges;
    private JButton btnAddNewInterface;
    private JButton btnDeleteInterface;
    private JButton btnDiscardChanges;
    private JButton btnModifyInterface;
    private JButton btnShowEditAttribs;
    private JCheckBox chkAbstract;
    private JCheckBox chkFinal;
    private JCheckBox chkInterface;
    private JCheckBox chkPublic;
    private JCheckBox chkSuper;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JPanel jPanel6;
    private JPanel jPanel7;
    private JScrollPane jScrollPane1;
    private JLabel lblClassName;
    private JLabel lblConstPoolIndex;
    private JLabel lblMagicNumber;
    private JLabel lblMajorVersion;
    private JLabel lblMinorVersion;
    private JLabel lblModifiedName;
    private JLabel lblSuperClassName;
    private JTree treeInterfaces;
    private JTextField txtClassName;
    private JTextField txtConstPoolIndex;
    private JTextArea txtHelpText;
    private JTextField txtMagicNumber;
    private JTextField txtMajorVersion;
    private JTextField txtMinorVersion;
    private JTextField txtNewInterfaceName;
    private JTextField txtSuperClassName;
    // End of variables declaration//GEN-END:variables
    
}
