/*
 * LineNumberAttribPane.java
 *
 * Created on January 27, 2002, 4:37 PM
 *
 * Modification Log:
 * 1.00   27th Jan 2002   Tanmay   Original version.
 * 1.01   05th Feb 2002   Tanmay   Moved over to table view instead of list.
 *                                 Added edit capability.
 * 1.02   11th May 2002   Tanmay   Made table single selection model
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */

package gui.attributes;

import classfile.*;
import classfile.attributes.LineNumberTableAttribute;
import classfile.attributes.Attribute;
import guihelper.attributes.LineNumberTableAttribTableModel;

import javax.swing.table.TableColumn;
import javax.swing.ListSelectionModel;

/**
 * Copyright (C) 2002-2003  Tanmay K. Mohapatra
 * <br>
 *
 * @author 	Tanmay K. Mohapatra
 * @version     1.02, 11th May, 2002
 */
public class LineNumberAttribPane extends javax.swing.JPanel implements AttribDisplay {

    private LineNumberTableAttribute attribute;
    private ConstantPool constPool;
    private boolean bModifyMode;
    
    /** Creates new form LineNumberAttribPane */
    public LineNumberAttribPane(boolean bModifyMode) {
        this.bModifyMode = bModifyMode;
        
        initComponents();
        btnAdd.setEnabled(bModifyMode);
        btnDelete.setEnabled(bModifyMode);
    }

    public void setInput(Attribute attribute, ConstantPool constPool) {
        this.attribute = (LineNumberTableAttribute)attribute;
        this.constPool = constPool;
        setTableModel();
    }

    private void setTableModel() {
        LineNumberTableAttribTableModel tblModel = new LineNumberTableAttribTableModel(attribute);
        tblModel.setEditable(bModifyMode);
        tblLineNumberEntries.setModel(tblModel);

        TableColumn thisCol;
        thisCol = tblLineNumberEntries.getColumnModel().getColumn(0);
        thisCol.setPreferredWidth(30);
        thisCol.setMaxWidth(80);

        thisCol = tblLineNumberEntries.getColumnModel().getColumn(1);
        thisCol.setPreferredWidth(200);
        thisCol.setMaxWidth(400);

        thisCol = tblLineNumberEntries.getColumnModel().getColumn(2);
        thisCol.setPreferredWidth(200);
        thisCol.setMaxWidth(400);
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLineNumberEntries = new javax.swing.JTable();
        tblLineNumberEntries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jPanel1 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        tblLineNumberEntries.setModel(new LineNumberTableAttribTableModel(null));
        jScrollPane2.setViewportView(tblLineNumberEntries);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(10, 10, 10, 10);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(jScrollPane2, gridBagConstraints1);
        
        jPanel1.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints2;
        
        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(btnAdd, gridBagConstraints2);
        
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(btnDelete, gridBagConstraints2);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints1);
        
    }//GEN-END:initComponents

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int iRow = tblLineNumberEntries.getSelectedRow();
        if(0 > iRow) return;
        
        attribute.deleteEntryAt(iRow);
        setTableModel();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        attribute.addNewEntry();
        setTableModel();
    }//GEN-LAST:event_btnAddActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblLineNumberEntries;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    // End of variables declaration//GEN-END:variables

}