/*
 * ConstantPoolPropPane.java
 *
 * Created on September 30, 2001, 3:59 PM
 *
 * Modification Log:
 * 1.00   30th Sep 2001   Tanmay   Original version.
 * 1.01   30th Jan 2002   Tanmay   Implemented toggle modify mode.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */

package gui;

import classfile.ClassFile;
import classfile.ConstantPoolInfo;
import guihelper.*;
import javax.swing.*;
import javax.swing.table.TableColumn;

/**
 * Copyright (C) 2002-2003  Tanmay K. Mohapatra
 * <br>
 *
 * @author 	Tanmay K. Mohapatra
 * @version     1.00, 30th January, 2002
 */
public class ConstantPoolPropPane extends javax.swing.JPanel {
    private ClassFile           currClassFile;
    private int                 iFilterTag;
    private ConstantPoolInfo    prevConstPoolEntry;
    private boolean             bEditable;
    static String       sClassDesc       = "The name_index item points to a Utf8 structure representing a valid fully qualified Java class name.";
    static String       sRefDesc         = "The class_index item points to a Class structure representing the class or interface type that contains the declaration of the field or method. The name_and_type_index item points to the name and descriptor of the field or method.";
    static String       sStringDesc      = "The string_index item points to a Utf8 structure";
    static String       sIntFltDesc      = "Four bytes of data holding the value.";
    static String       sLongDoubleDesc  = "Eight bytes of data holding the value.";
    static String       sNameAndTypeDesc = "The name_index points to a Utf8 structure denoting the field or method name. The descriptor_index points points to a Utf8 structure denoting a valid field or method descriptor.";
    static String       sUtfDesc         = "String in Utf format. Utf format stores the length and the data after that.";
    
    /** Creates new form ConstantPoolPropPane */
    public ConstantPoolPropPane() {
        initComponents();
        iFilterTag = ConstPoolTableModel.NO_FILTER;
    }
    
    public void setModifyMode(boolean bEditableIn) {
        bEditable = bEditableIn;
        cmbPoolEntryType.setEnabled(bEditable);
        txtStringIndex.setEnabled(bEditable);
        txtClassIndex.setEnabled(bEditable);
        txtNameAndTypeIndex.setEnabled(bEditable);
        txtNameIndex.setEnabled(bEditable);
        txtDescriptorIndex.setEnabled(bEditable);
        txtValue.setEnabled(bEditable);
        btnAddNew.setEnabled(bEditable);
        btnModify.setEnabled(bEditable);
        btnDelete.setEnabled(bEditable);
    }
    
    void clear() {
        tblConstPool.setModel(new ConstPoolTableModel(null));
        cmbPoolEntryType.setSelectedIndex(0);
        cmbFilter.setSelectedIndex(0);
        txtStringIndex.setText("");
        txtClassIndex.setText("");
        txtNameAndTypeIndex.setText("");
        txtNameIndex.setText("");
        txtDescriptorIndex.setText("");
        txtValue.setText("");
        cmbJumpTo.setEditable(true);
        cmbJumpTo.setModel(new DefaultComboBoxModel());
        txtPoolTypeDesc.setText("");
        txtConstPoolSearch.setText("");
        prevConstPoolEntry = null;
    }
    
    void refresh() {
        clear();
        if(null == currClassFile) return;
        
        TableColumn thisCol;
        ConstPoolTableModel thisModel = new ConstPoolTableModel(currClassFile.constantPool);
        thisModel.setFilter(iFilterTag);
        thisModel.applyFilter();
        tblConstPool.setModel(thisModel);
        
        thisCol = tblConstPool.getColumnModel().getColumn(0);
        thisCol.setPreferredWidth(30);
        thisCol.setMaxWidth(100);
        
        thisCol = tblConstPool.getColumnModel().getColumn(1);
        thisCol.setPreferredWidth(15);
        thisCol.setMaxWidth(60);
        
        thisCol = tblConstPool.getColumnModel().getColumn(2);
        thisCol.setPreferredWidth(400);
        thisCol.setMaxWidth(1000);
        
        tblConstPool.changeSelection(0, 1, false, false);
        
        setConstantPoolTypeSelector(cmbFilter, iFilterTag);
    }
    
    void setClassFile(ClassFile classFile) {
        currClassFile = classFile;
    }
    
    private void setConstantPoolTypeSelector(JComboBox cmbThis, int iTag) {
        switch(iTag) {
            case ConstantPoolInfo.CONSTANT_Class:
                cmbThis.setSelectedIndex(1);
                break;
            case ConstantPoolInfo.CONSTANT_Double:
                cmbThis.setSelectedIndex(2);
                break;
            case ConstantPoolInfo.CONSTANT_Fieldref:
                cmbThis.setSelectedIndex(3);
                break;
            case ConstantPoolInfo.CONSTANT_Float:
                cmbThis.setSelectedIndex(4);
                break;
            case ConstantPoolInfo.CONSTANT_Integer:
                cmbThis.setSelectedIndex(5);
                break;
            case ConstantPoolInfo.CONSTANT_InterfaceMethodref:
                cmbThis.setSelectedIndex(6);
                break;
            case ConstantPoolInfo.CONSTANT_Long:
                cmbThis.setSelectedIndex(7);
                break;
            case ConstantPoolInfo.CONSTANT_Methodref:
                cmbThis.setSelectedIndex(8);
                break;
            case ConstantPoolInfo.CONSTANT_NameAndType:
                cmbThis.setSelectedIndex(9);
                break;
            case ConstantPoolInfo.CONSTANT_String:
                cmbThis.setSelectedIndex(10);
                break;
            case ConstantPoolInfo.CONSTANT_Utf8:
                cmbThis.setSelectedIndex(11);
                break;
            default:
                cmbThis.setSelectedIndex(0);
                break;
        }
    }
    
    private void searchPool() {
        if (null == currClassFile) return;
        String sSrchStr = txtConstPoolSearch.getText();
        sSrchStr = sSrchStr.trim();
        if (sSrchStr.length() <= 0) return;
        int iIndex = tblConstPool.getSelectedRow();
        
        ConstPoolTableModel tblModel = (ConstPoolTableModel)tblConstPool.getModel();
        iIndex = tblModel.nextIndex(iIndex+1, sSrchStr);
        if(iIndex >= 0) {
            tblConstPool.changeSelection(iIndex, 1, false, false);
        }
    }
    
    private void tblConstPoolValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if(evt.getValueIsAdjusting()) return;
        
        DefaultComboBoxModel dcm = new DefaultComboBoxModel();
        txtStringIndex.setText("");
        txtClassIndex.setText("");
        txtNameAndTypeIndex.setText("");
        txtNameIndex.setText("");
        txtDescriptorIndex.setText("");
        txtValue.setText("");
        cmbJumpTo.setEditable(true);
        cmbJumpTo.setModel(dcm);
        
        int iSelectedRow = tblConstPool.getSelectedRow();
        if(0 > iSelectedRow) return;
        // get the actual constant pool entry index
        Integer IntSelRow = ((Integer)tblConstPool.getValueAt(iSelectedRow, 0));
        if(null == IntSelRow) return;
        iSelectedRow = IntSelRow.intValue();
        
        ConstantPoolInfo thisInfo = currClassFile.constantPool.getPoolInfo(iSelectedRow);
        prevConstPoolEntry = thisInfo;
        tblConstPool.setToolTipText("Selected: " + thisInfo.getExtraInfoString());
        
        if(null == thisInfo) return;
        
        setConstantPoolTypeSelector(cmbPoolEntryType, thisInfo.iTag);
        switch(thisInfo.iTag) {
            case ConstantPoolInfo.CONSTANT_Class:
                txtNameIndex.setText(Integer.toString(thisInfo.iNameIndex));
                dcm.addElement(Integer.toString(thisInfo.iNameIndex));
                cmbJumpTo.setModel(dcm);
                break;
            case ConstantPoolInfo.CONSTANT_Double:
                txtValue.setText(Double.toString(thisInfo.dDoubleVal));
                break;
            case ConstantPoolInfo.CONSTANT_Fieldref:
                txtClassIndex.setText(Integer.toString(thisInfo.iClassIndex));
                txtNameAndTypeIndex.setText(Integer.toString(thisInfo.iNameAndTypeIndex));
                dcm.addElement(Integer.toString(thisInfo.iClassIndex));
                dcm.addElement(Integer.toString(thisInfo.iNameAndTypeIndex));
                cmbJumpTo.setModel(dcm);
                break;
            case ConstantPoolInfo.CONSTANT_Float:
                txtValue.setText(Float.toString(thisInfo.fFloatVal));
                break;
            case ConstantPoolInfo.CONSTANT_Integer:
                txtValue.setText(Integer.toString(thisInfo.iIntValue));
                break;
            case ConstantPoolInfo.CONSTANT_InterfaceMethodref:
                txtClassIndex.setText(Integer.toString(thisInfo.iClassIndex));
                txtNameAndTypeIndex.setText(Integer.toString(thisInfo.iNameAndTypeIndex));
                dcm.addElement(Integer.toString(thisInfo.iClassIndex));
                dcm.addElement(Integer.toString(thisInfo.iNameAndTypeIndex));
                cmbJumpTo.setModel(dcm);
                break;
            case ConstantPoolInfo.CONSTANT_Long:
                txtValue.setText(Long.toString(thisInfo.lLongVal));
                break;
            case ConstantPoolInfo.CONSTANT_Methodref:
                txtClassIndex.setText(Integer.toString(thisInfo.iClassIndex));
                txtNameAndTypeIndex.setText(Integer.toString(thisInfo.iNameAndTypeIndex));
                dcm.addElement(Integer.toString(thisInfo.iClassIndex));
                dcm.addElement(Integer.toString(thisInfo.iNameAndTypeIndex));
                cmbJumpTo.setModel(dcm);
                break;
            case ConstantPoolInfo.CONSTANT_NameAndType:
                txtNameIndex.setText(Integer.toString(thisInfo.iNameIndex));
                txtDescriptorIndex.setText(Integer.toString(thisInfo.iDescriptorIndex));
                dcm.addElement(Integer.toString(thisInfo.iNameIndex));
                dcm.addElement(Integer.toString(thisInfo.iDescriptorIndex));
                cmbJumpTo.setModel(dcm);
                break;
            case ConstantPoolInfo.CONSTANT_String:
                txtStringIndex.setText(Integer.toString(thisInfo.iStringIndex));
                dcm.addElement(Integer.toString(thisInfo.iStringIndex));
                cmbJumpTo.setModel(dcm);
                break;
            case ConstantPoolInfo.CONSTANT_Utf8:
                txtValue.setText(thisInfo.sUTFStr);
                break;
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane2 = new javax.swing.JScrollPane();
        tblConstPool = new javax.swing.JTable();
        tblConstPool.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel tblSM = tblConstPool.getSelectionModel();
        tblSM.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                tblConstPoolValueChanged(evt);
            }
        });

        detailsPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cmbPoolEntryType = new javax.swing.JComboBox(new String[] {"", "Class", "Double", "Fieldref", "Float", "Integer", "InterfaceMethodref", "Long", "Methodref", "NameAndType", "String", "Utf8"});
        jLabel7 = new javax.swing.JLabel();
        txtStringIndex = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtClassIndex = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtNameAndTypeIndex = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtNameIndex = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtDescriptorIndex = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtValue = new javax.swing.JTextField();
        searchPanel = new javax.swing.JPanel();
        jTextArea2 = new javax.swing.JTextArea();
        txtConstPoolSearch = new javax.swing.JTextField();
        btnConstPoolSearch = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        cmbFilter = new javax.swing.JComboBox(new String[] {"", "Class", "Double", "Fieldref", "Float", "Integer", "InterfaceMethodref", "Long", "Methodref", "NameAndType", "String", "Utf8"});
        btnApplyFilter = new javax.swing.JButton();
        editPanel = new javax.swing.JPanel();
        btnAddNew = new javax.swing.JButton();
        btnModify = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        navigatePanel = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        cmbJumpTo = new javax.swing.JComboBox();
        btnJumpTo = new javax.swing.JButton();
        descriptionPanel = new javax.swing.JPanel();
        txtPoolTypeDesc = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.TitledBorder("Constant Pool"));
        tblConstPool.setModel(new ConstPoolTableModel(null));
        tblConstPool.setShowHorizontalLines(false);
        jScrollPane2.setViewportView(tblConstPool);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane2, gridBagConstraints);

        detailsPanel.setLayout(new java.awt.GridBagLayout());

        detailsPanel.setBorder(new javax.swing.border.TitledBorder("Details"));
        jLabel5.setText("Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        detailsPanel.add(jLabel5, gridBagConstraints);

        cmbPoolEntryType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbPoolEntryTypeItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        detailsPanel.add(cmbPoolEntryType, gridBagConstraints);

        jLabel7.setText("String");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        detailsPanel.add(jLabel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        detailsPanel.add(txtStringIndex, gridBagConstraints);

        jLabel8.setText("Class");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        detailsPanel.add(jLabel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        detailsPanel.add(txtClassIndex, gridBagConstraints);

        jLabel9.setText("Name and Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        detailsPanel.add(jLabel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        detailsPanel.add(txtNameAndTypeIndex, gridBagConstraints);

        jLabel10.setText("Name Index");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        detailsPanel.add(jLabel10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        detailsPanel.add(txtNameIndex, gridBagConstraints);

        jLabel11.setText("Descriptor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        detailsPanel.add(jLabel11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        detailsPanel.add(txtDescriptorIndex, gridBagConstraints);

        jLabel12.setText("Value");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        detailsPanel.add(jLabel12, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        detailsPanel.add(txtValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(detailsPanel, gridBagConstraints);

        searchPanel.setLayout(new java.awt.GridBagLayout());

        searchPanel.setBorder(new javax.swing.border.TitledBorder("Search"));
        jTextArea2.setWrapStyleWord(true);
        jTextArea2.setEditable(false);
        jTextArea2.setText("Enter search string below and press Find \nto search from the current position");
        jTextArea2.setBackground(new java.awt.Color(204, 204, 255));
        jTextArea2.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        searchPanel.add(jTextArea2, gridBagConstraints);

        txtConstPoolSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtConstPoolSearchActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        searchPanel.add(txtConstPoolSearch, gridBagConstraints);

        btnConstPoolSearch.setText("Find/Find Next");
        btnConstPoolSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConstPoolSearchActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        searchPanel.add(btnConstPoolSearch, gridBagConstraints);

        jLabel1.setText("Filter");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        searchPanel.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 0, 0);
        searchPanel.add(cmbFilter, gridBagConstraints);

        btnApplyFilter.setText("Apply");
        btnApplyFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyFilterActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 0, 0);
        searchPanel.add(btnApplyFilter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(searchPanel, gridBagConstraints);

        editPanel.setBorder(new javax.swing.border.TitledBorder("Edit"));
        btnAddNew.setText("Add New");
        btnAddNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewActionPerformed(evt);
            }
        });

        editPanel.add(btnAddNew);

        btnModify.setText("Modify");
        btnModify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModifyActionPerformed(evt);
            }
        });

        editPanel.add(btnModify);

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        editPanel.add(btnDelete);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(editPanel, gridBagConstraints);

        navigatePanel.setBorder(new javax.swing.border.TitledBorder("Navigate"));
        jLabel13.setText("Go to");
        navigatePanel.add(jLabel13);

        navigatePanel.add(cmbJumpTo);

        btnJumpTo.setText("Go");
        btnJumpTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJumpToActionPerformed(evt);
            }
        });

        navigatePanel.add(btnJumpTo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(navigatePanel, gridBagConstraints);

        descriptionPanel.setLayout(new java.awt.GridLayout(1, 0));

        descriptionPanel.setBorder(new javax.swing.border.TitledBorder("Description"));
        txtPoolTypeDesc.setWrapStyleWord(true);
        txtPoolTypeDesc.setLineWrap(true);
        txtPoolTypeDesc.setEditable(false);
        txtPoolTypeDesc.setRows(10);
        txtPoolTypeDesc.setBackground(new java.awt.Color(204, 204, 255));
        txtPoolTypeDesc.setRequestFocusEnabled(false);
        descriptionPanel.add(txtPoolTypeDesc);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        add(descriptionPanel, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void btnApplyFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyFilterActionPerformed
        int iSelectedIndex = cmbFilter.getSelectedIndex();
        switch(iSelectedIndex) {
            case 1:
                iFilterTag = ConstantPoolInfo.CONSTANT_Class;
                break;
            case 2:
                iFilterTag = ConstantPoolInfo.CONSTANT_Double;
                break;
            case 3:
                iFilterTag = ConstantPoolInfo.CONSTANT_Fieldref;
                break;
            case 4:
                iFilterTag = ConstantPoolInfo.CONSTANT_Float;
                break;
            case 5:
                iFilterTag = ConstantPoolInfo.CONSTANT_Integer;
                break;
            case 6:
                iFilterTag = ConstantPoolInfo.CONSTANT_InterfaceMethodref;
                break;
            case 7:
                iFilterTag = ConstantPoolInfo.CONSTANT_Long;
                break;
            case 8:
                iFilterTag = ConstantPoolInfo.CONSTANT_Methodref;
                break;
            case 9:
                iFilterTag = ConstantPoolInfo.CONSTANT_NameAndType;
                break;
            case 10:
                iFilterTag = ConstantPoolInfo.CONSTANT_String;
                break;
            case 11:
                iFilterTag = ConstantPoolInfo.CONSTANT_Utf8;
                break;
            default:
                iFilterTag = ConstPoolTableModel.NO_FILTER;
                break;
        }
        refresh();
    }//GEN-LAST:event_btnApplyFilterActionPerformed
    
    private void txtConstPoolSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtConstPoolSearchActionPerformed
        searchPool();
    }//GEN-LAST:event_txtConstPoolSearchActionPerformed
    
    private void btnConstPoolSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConstPoolSearchActionPerformed
        searchPool();
    }//GEN-LAST:event_btnConstPoolSearchActionPerformed
    
    private void btnModifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModifyActionPerformed
        if (null != prevConstPoolEntry) {
            setConstantPoolTypeSelector(cmbPoolEntryType, prevConstPoolEntry.iTag);
            switch(prevConstPoolEntry.iTag) {
                case ConstantPoolInfo.CONSTANT_Class:
                    prevConstPoolEntry.setNameIndex(Integer.parseInt(txtNameIndex.getText()));
                    break;
                case ConstantPoolInfo.CONSTANT_Double:
                    prevConstPoolEntry.dDoubleVal = Double.valueOf(txtValue.getText()).doubleValue();
                    break;
                case ConstantPoolInfo.CONSTANT_Float:
                    prevConstPoolEntry.fFloatVal = Float.valueOf(txtValue.getText()).floatValue();
                    break;
                case ConstantPoolInfo.CONSTANT_Integer:
                    prevConstPoolEntry.iIntValue = Integer.parseInt(txtValue.getText());
                    break;
                case ConstantPoolInfo.CONSTANT_InterfaceMethodref:
                case ConstantPoolInfo.CONSTANT_Methodref:
                case ConstantPoolInfo.CONSTANT_Fieldref:
                    prevConstPoolEntry.setClassIndex(Integer.parseInt(txtClassIndex.getText()));
                    prevConstPoolEntry.setNameAndTypeIndex(Integer.parseInt(txtNameAndTypeIndex.getText()));
                    break;
                case ConstantPoolInfo.CONSTANT_Long:
                    prevConstPoolEntry.lLongVal = Long.parseLong(txtValue.getText());
                    break;
                case ConstantPoolInfo.CONSTANT_NameAndType:
                    prevConstPoolEntry.setNameIndex(Integer.parseInt(txtNameIndex.getText()));
                    prevConstPoolEntry.setDescriptorIndex(Integer.parseInt(txtDescriptorIndex.getText()));
                    break;
                case ConstantPoolInfo.CONSTANT_String:
                    prevConstPoolEntry.setStringIndex(Integer.parseInt(txtStringIndex.getText()));
                    break;
                case ConstantPoolInfo.CONSTANT_Utf8:
                    prevConstPoolEntry.sUTFStr = txtValue.getText();
                    break;
            }
        }
        ConstPoolTableModel tblModel = (ConstPoolTableModel)tblConstPool.getModel();
        int iSelectedRow = tblConstPool.getSelectedRow();
        tblModel.setValueAt(prevConstPoolEntry, iSelectedRow, 0);
        tblConstPool.setModel(tblModel);
        tblConstPool.changeSelection(iSelectedRow, 1, false, false);
    }//GEN-LAST:event_btnModifyActionPerformed
    
    private void btnAddNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewActionPerformed
        if (null == currClassFile) return;
        ConstantPoolInfo newPoolInfo = new ConstantPoolInfo();
        newPoolInfo.setConstPool(currClassFile.constantPool);
        
        switch(cmbPoolEntryType.getSelectedIndex()) {
            case 0:
                return;
            case 1:
                newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Class;
                newPoolInfo.setNameIndex(Integer.parseInt(txtNameIndex.getText()));
                break;
            case 2:
                newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Double;
                newPoolInfo.dDoubleVal = Double.valueOf(txtValue.getText()).doubleValue();
                break;
            case 3:
                newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Fieldref;
                newPoolInfo.setClassIndex(Integer.parseInt(txtClassIndex.getText()));
                newPoolInfo.setNameAndTypeIndex(Integer.parseInt(txtNameAndTypeIndex.getText()));
                break;
            case 4:
                newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Float;
                newPoolInfo.fFloatVal = Float.valueOf(txtValue.getText()).floatValue();
                break;
            case 5:
                newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Integer;
                newPoolInfo.iIntValue = Integer.parseInt(txtValue.getText());
                break;
            case 6:
                newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_InterfaceMethodref;
                newPoolInfo.setClassIndex(Integer.parseInt(txtClassIndex.getText()));
                newPoolInfo.setNameAndTypeIndex(Integer.parseInt(txtNameAndTypeIndex.getText()));
                break;
            case 7:
                newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Long;
                newPoolInfo.lLongVal = Long.parseLong(txtValue.getText());
                break;
            case 8:
                newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Methodref;
                newPoolInfo.setClassIndex(Integer.parseInt(txtClassIndex.getText()));
                newPoolInfo.setNameAndTypeIndex(Integer.parseInt(txtNameAndTypeIndex.getText()));
                break;
            case 9:
                newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_NameAndType;
                newPoolInfo.setNameIndex(Integer.parseInt(txtNameIndex.getText()));
                newPoolInfo.setDescriptorIndex(Integer.parseInt(txtDescriptorIndex.getText()));
                break;
            case 10:
                newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_String;
                newPoolInfo.setStringIndex(Integer.parseInt(txtStringIndex.getText()));
                break;
            case 11:
                newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Utf8;
                newPoolInfo.sUTFStr = txtValue.getText();
                break;
            default:
                return;
        }
        
        currClassFile.constantPool.addNewPoolInfo(newPoolInfo);
        refresh();
        tblConstPool.changeSelection(tblConstPool.getModel().getRowCount()-1, 1, false, false);
    }//GEN-LAST:event_btnAddNewActionPerformed
    
    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (null != prevConstPoolEntry) {
            int iNumRef;
            String sMsg;
            int iConfirm;
            
            if (0 == (iNumRef = prevConstPoolEntry.getRef())) {
                sMsg = "There are no resolved references to this entry.\n";
                sMsg += "However it might be getting used in the code.\n";
                sMsg += "Leaving this intact will not affect the functionality of the class.\n";
                sMsg += "\nAre you sure you want to delete this?";
            }
            else {
                sMsg = "There are " + iNumRef + " place(s) where this entry is being used.\n";
                sMsg += "Deleting this entry will result in an error in the class file.\n";
                sMsg += "\nAre you sure you want to delete this?";
            }
            iConfirm = JOptionPane.showConfirmDialog(null, sMsg, "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if(JOptionPane.YES_OPTION == iConfirm) {
                currClassFile.constantPool.removePoolInfo(prevConstPoolEntry);
                refresh();
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed
    
    private void btnJumpToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJumpToActionPerformed
        if (null == currClassFile) return;
        String selObj = (String)(cmbJumpTo.getSelectedItem());
        if(null == selObj) return;
        String sIndex = selObj.trim();
        if (sIndex.length() == 0)   return;
        
        try {
            int iIndex = Integer.parseInt(sIndex);
            iIndex = ((ConstPoolTableModel)tblConstPool.getModel()).getModelIndex(iIndex);
            if (iIndex < 0) return;
            tblConstPool.changeSelection(iIndex, 1, false, false);
        }
        catch(NumberFormatException nfe) {
            // do nothing
        }
    }//GEN-LAST:event_btnJumpToActionPerformed
    
    private void cmbPoolEntryTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbPoolEntryTypeItemStateChanged
        txtStringIndex.setEnabled(false);
        txtClassIndex.setEnabled(false);
        txtNameAndTypeIndex.setEnabled(false);
        txtNameIndex.setEnabled(false);
        txtDescriptorIndex.setEnabled(false);
        txtValue.setEnabled(false);
        
        switch(cmbPoolEntryType.getSelectedIndex()) {
            case 1:
                txtPoolTypeDesc.setText(sClassDesc);
                if(bEditable) txtNameIndex.setEnabled(true);
                break;
            case 2:
                txtPoolTypeDesc.setText(sLongDoubleDesc);
                if(bEditable) txtValue.setEnabled(true);
                break;
            case 3:
                txtPoolTypeDesc.setText(sRefDesc);
                if(bEditable) txtClassIndex.setEnabled(true);
                if(bEditable) txtNameAndTypeIndex.setEnabled(true);
                break;
            case 4:
                txtPoolTypeDesc.setText(sIntFltDesc);
                if(bEditable) txtValue.setEnabled(true);
                break;
            case 5:
                txtPoolTypeDesc.setText(sIntFltDesc);
                if(bEditable) txtValue.setEnabled(true);
                break;
            case 6:
                txtPoolTypeDesc.setText(sRefDesc);
                if(bEditable) txtClassIndex.setEnabled(true);
                if(bEditable) txtNameAndTypeIndex.setEnabled(true);
                break;
            case 7:
                txtPoolTypeDesc.setText(sLongDoubleDesc);
                if(bEditable) txtValue.setEnabled(true);
                break;
            case 8:
                txtPoolTypeDesc.setText(sRefDesc);
                if(bEditable) txtClassIndex.setEnabled(true);
                if(bEditable) txtNameAndTypeIndex.setEnabled(true);
                break;
            case 9:
                txtPoolTypeDesc.setText(sNameAndTypeDesc);
                if(bEditable) txtNameIndex.setEnabled(true);
                if(bEditable) txtDescriptorIndex.setEnabled(true);
                break;
            case 10:
                txtPoolTypeDesc.setText(sStringDesc);
                if(bEditable) txtStringIndex.setEnabled(true);
                break;
            case 11:
                txtPoolTypeDesc.setText(sUtfDesc);
                if(bEditable) txtValue.setEnabled(true);
                break;
        }
    }//GEN-LAST:event_cmbPoolEntryTypeItemStateChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNew;
    private javax.swing.JButton btnApplyFilter;
    private javax.swing.JButton btnConstPoolSearch;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnJumpTo;
    private javax.swing.JButton btnModify;
    private javax.swing.JComboBox cmbFilter;
    private javax.swing.JComboBox cmbJumpTo;
    private javax.swing.JComboBox cmbPoolEntryType;
    private javax.swing.JPanel descriptionPanel;
    private javax.swing.JPanel detailsPanel;
    private javax.swing.JPanel editPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JPanel navigatePanel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTable tblConstPool;
    private javax.swing.JTextField txtClassIndex;
    private javax.swing.JTextField txtConstPoolSearch;
    private javax.swing.JTextField txtDescriptorIndex;
    private javax.swing.JTextField txtNameAndTypeIndex;
    private javax.swing.JTextField txtNameIndex;
    private javax.swing.JTextArea txtPoolTypeDesc;
    private javax.swing.JTextField txtStringIndex;
    private javax.swing.JTextField txtValue;
    // End of variables declaration//GEN-END:variables
    
}
