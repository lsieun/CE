/*
 * FieldTableModel.java
 *
 * Created on December 30, 2001, 10:28 AM
 *
 * Modification Log:
 * 1.00   30th Dec 2001   Tanmay   Original version.
 * 1.01   15th Jan 2002   Tanmay   Implemented access flag editor dialog
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */

package guihelper;

import classfile.*;
import classfile.attributes.Attributes;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.Hashtable;
import java.util.Enumeration;

/** This class manages the table model for the fields list.
 * This is the interconnect between the UI and class file data.
 * <br><br>
 *
 * @author     Tanmay K. Mohapatra
 * @version    1.01, 15th Jan, 2002
 */
public class FieldTableModel extends AbstractTableModel {
    
    private final String[] columnNames = {
        "Index",
        "Access & Modifier Flags",
        "Descriptor",
        "Name"
    };
    
    private Object[][] data;
    private ClassFile currClassFile;
    private ConstantPool constPool;
    private Hashtable hashUTF, hashDesc;
    private String [] strUTF;
    private String [] strDesc;
    private boolean bEditMode;
    
    /** Creates new FieldTableModel
     * @param classFileIn The class file for which to create the table model.
     *
     */
    public FieldTableModel(ClassFile classFileIn) {
        currClassFile = classFileIn;
        if(null != currClassFile) {
            constPool= currClassFile.constantPool;
            extractConstPoolInfo();
        }
        else {
            constPool = null;
            hashUTF = null;
            strUTF = null;
        }
        
        createData();
    }
    
    private JFrame getFrameFrom(JTable tbl) {
        int iIndex = 0;
        Component comp = tbl;
        
        for(iIndex=0; (iIndex < 1000) && !(comp instanceof JFrame); iIndex++) {
            comp = comp.getParent();
        }
        
        if(comp instanceof JFrame) return (JFrame)comp;
        return null;
    }
    
    /** Sets appropriate editors for the columns.
     * @param tbl The table for which to set column editors.
     */
    public void setCellEditors(JTable tbl) {
        TableColumn thisCol;
        
        final JButton btnEditor = new JButton();
        final AccessFlagEditor flgEditor = new AccessFlagEditor(btnEditor);
        final AccessFlagEditorDialog flgEditorDlg = new AccessFlagEditorDialog(getFrameFrom(tbl), true);
        flgEditorDlg.setValidAccessFlags(AccessFlags.getValidFlags(AccessFlags.FIELD_VALID_FLAGS));
        
        thisCol = tbl.getColumnModel().getColumn(1);
        thisCol.setCellEditor(flgEditor);
        
        btnEditor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                flgEditorDlg.setAccessFlags(flgEditor.currFlags);
                flgEditorDlg.setLocationRelativeTo((Component)btnEditor);
                flgEditorDlg.show();
            }
        });
        
        JComboBox descEditor = new JComboBox(strDesc);
        descEditor.setEditable(true);
        thisCol = tbl.getColumnModel().getColumn(2);
        thisCol.setCellEditor(new DefaultCellEditor(descEditor));
        
        JComboBox nameEditor = new JComboBox(strUTF);
        nameEditor.setEditable(true);
        thisCol = tbl.getColumnModel().getColumn(3);
        thisCol.setCellEditor(new DefaultCellEditor(nameEditor));        
    }
    
    private void createData() {
        if(null == currClassFile) return;
        
        int iIndex, iMaxCount;
        iMaxCount = currClassFile.fields.getFieldsCount();
        data = new Object[iMaxCount][columnNames.length];
        for (iIndex=0; iIndex < iMaxCount; iIndex++) {
            FieldInfo thisInfo = currClassFile.fields.getField(iIndex);
            setValueAt(thisInfo, iIndex, 0);
        }
    }
    
    /** Returns the column heading string.
     * @param col Column number
     * @return Column heading string
     */
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    /** Returns the java class corresponding to the column.
     * @param col Column number
     * @return Instance of Java Class class for the data represented by this column.
     */
    public Class getColumnClass(int col) {
        return data[0][col].getClass();
    }
    
    /** Returns the value for the appropriate cell.
     * @param row Row number
     * @param col Column number
     * @return Cell value
     */
    public java.lang.Object getValueAt(int row, int col) {
        if( (null != data) && (data.length > 0) ) {
            return data[row][col];
        }
        return null;
    }
    
    /** Returns the number of rows in the table.
     * @return number of rows in the table
     */
    public int getRowCount() {
        if(null == data) return 0;
        return data.length;
    }
    
    /** Returns the number of columns in the table.
     * @return number of columns in the table
     */
    public int getColumnCount() {
        return columnNames.length;
    }
    
    /** Whether the cell is editable.
     * Column zero is not editable, it being the index.
     * Other columns are editable if editable property has been set to true.
     * @param row Row number
     * @param col Column number
     * @return true if cell is editable, false otherwise
     * @see setEditable
     */
    public boolean isCellEditable(int row, int col) {
        if(0 == col) return false;
        return bEditMode;
    }
    
    /** Sets the editable property of the table.
     * @param bEditModeIn Edit mode, true to enable editing.
     */
    public void setEditable(boolean bEditModeIn) {
        bEditMode = bEditModeIn;
    }
    
    /** Searches the field name and descripor columns of the
     * field list for the specified string.
     * @param iStartIndex The index from where to start search
     * @param sSrchStr The string to search for
     * @return The index where the searched item was found.
     * -1 if string was not found.
     */
    public int nextIndex(int iStartIndex, String sSrchStr) {
        if(null == data) return -1;
        for (; iStartIndex < data.length; iStartIndex++) {
            if(((String)(data[iStartIndex][2])).indexOf(sSrchStr) >= 0) break;
            if(((String)(data[iStartIndex][3])).indexOf(sSrchStr) >= 0) break;
        }
        return (iStartIndex < data.length) ? iStartIndex : -1;
    }
    
    /** Sets the value either for a complete row or for a cell.
     * @param val The value as input.
     * If val is instance of FieldInfo, it is likely to have been called internally.
     * Otherwise it is called when the table cell is edited.
     * @param row Row number
     * @param col Column number
     */
    public void setValueAt(Object val, int row, int col) {
        if(val instanceof FieldInfo) {
            FieldInfo fldInfo = (FieldInfo)val;
            String sDescriptor = fldInfo.getFieldDescriptor();
            String sName = fldInfo.cpName.sUTFStr;
            
            data[row][0] = new Integer(row+1);
            data[row][1] = fldInfo.accessFlags;
            data[row][2] = sDescriptor;
            data[row][3] = sName;
        }
        else {
            FieldInfo thisFld = currClassFile.fields.getField(row);
            switch(col) {
                case 1:
                    thisFld.accessFlags = (AccessFlags)val;
                    data[row][1] = thisFld.accessFlags;
                    break;
                case 2:
                    String sDesc = (String)val;
                    thisFld.cpDescriptor.deleteRef();
                    thisFld.cpDescriptor = searchAddOrModifyFieldDescInConstPool(sDesc, thisFld.cpDescriptor);
                    thisFld.cpDescriptor.addRef();
                    thisFld.setFieldDescriptor(sDesc);
                    data[row][2] = sDesc;
                    break;
                case 3:
                    String sName = (String)val;
                    thisFld.cpName.deleteRef();
                    thisFld.cpName = searchAddOrModifyFieldNameInConstPool(sName, thisFld.cpName);
                    thisFld.cpName.addRef();
                    thisFld.setFieldName(sName);
                    data[row][3] = sName;
                    break;
                default:
                    break; //ignore;
            }
        }
    }
    
    /** Adds a new field in the field list.
     */
    public void addNewField() {
        FieldInfo thisFld = new FieldInfo();
        AccessFlags thisAccFlg = new AccessFlags();
        thisAccFlg.setPublic(false);
        thisAccFlg.setFinal(false);
        thisAccFlg.setPrivate(false);
        thisAccFlg.setProtected(false);
        thisAccFlg.setStatic(false);
        thisAccFlg.setTransient(false);
        thisAccFlg.setVolatile(false);
        thisFld.accessFlags = thisAccFlg;
        thisFld.attributes = new Attributes();
        
        if(hashDesc.size() > 0) {
            String sDesc = (String)hashDesc.keys().nextElement();
            int iDescIndex = ((Integer)hashDesc.get(sDesc)).intValue();
            thisFld.cpDescriptor = currClassFile.constantPool.getPoolInfo(iDescIndex);
            thisFld.setFieldDescriptor(sDesc);
        }
        else {
            thisFld.cpDescriptor = addNewFieldDescInConstPool(null);
            thisFld.setFieldDescriptor(classfile.Utils.getReadableDesc(thisFld.cpDescriptor.sUTFStr));
        }
        
        thisFld.cpName = addNewFieldNameInConstPool(null);
        thisFld.setFieldName(thisFld.cpName.sUTFStr);
        
        currClassFile.fields.addField(thisFld);
    }

    private String getUniqueFieldName() {
        for(int iIndex=0; iIndex < Integer.MAX_VALUE; iIndex++) {
            String sFldName = "NewField" + iIndex;
            if(null == hashUTF.get(sFldName)) return sFldName;
        }
        return "Phew";
    }
    
    /**
     * Either creates a new UTF8 constant pool entry with sReadableDesc or modifies the
     * supplied UTF8 constant pool entry prevPoolInfo depending on whether
     * someone else is using the pool entry.
     * It is important to decrement reference count for the current field before
     * calling this method, otherwise this method will always add a new entry to
     * the pool!
     */
    private ConstantPoolInfo searchAddOrModifyFieldDescInConstPool(String sReadableDesc, ConstantPoolInfo prevPoolInfo) {
        Integer poolIndex = (Integer)hashDesc.get(sReadableDesc);
        
        if(null == poolIndex) {
            if(prevPoolInfo.getRef() > 0) {
                // the current pool entry is being referred to somebody else
                return addNewFieldDescInConstPool(sReadableDesc);
            }
            else {
                // no one is referring to this, we can modify it for our purpose
                prevPoolInfo.sUTFStr = classfile.Utils.getRawDesc(sReadableDesc);
                return prevPoolInfo;
            }
        }
        else {
            return currClassFile.constantPool.getPoolInfo(poolIndex.intValue());
        }
    }
    
    /**
     * Either creates a new UTF8 constant pool entry with sName or modifies the
     * supplied UTF8 constant pool entry prevPoolInfo depending on whether
     * someone else is using the pool entry.
     * It is important to decrement reference count for the current field before
     * calling this method, otherwise this method will always add a new entry to
     * the pool!
     */
    private ConstantPoolInfo searchAddOrModifyFieldNameInConstPool(String sName, ConstantPoolInfo prevPoolInfo) {
        Integer poolIndex = (Integer)hashUTF.get(sName);
        
        if(null == poolIndex) {
            if(prevPoolInfo.getRef() > 0) {
                // the current pool entry is being referred to somebody else
                return addNewFieldNameInConstPool(sName);
            }
            else {
                // no one is referring to this, we can modify it for our purpose
                prevPoolInfo.sUTFStr = sName;
                return prevPoolInfo;
            }
        }
        else {
            // return the existing pool entry
            return currClassFile.constantPool.getPoolInfo(poolIndex.intValue());
        }
    }
    
    
    private ConstantPoolInfo addNewFieldDescInConstPool(String sReadableDesc) {
        String sDesc = null;
        
        if(null != sReadableDesc) {
            sDesc = classfile.Utils.getRawDesc(sReadableDesc);
            if(sDesc.equals("unknown")) return null;
        }
        
        ConstantPoolInfo newPoolInfo = new ConstantPoolInfo();
        newPoolInfo.setConstPool(currClassFile.constantPool);
        newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Utf8;
        newPoolInfo.sUTFStr = (null == sDesc) ? "I" : sDesc;
        currClassFile.constantPool.addNewPoolInfo(newPoolInfo);
        return newPoolInfo;
    }
    
    private ConstantPoolInfo addNewFieldNameInConstPool(String sFldName) {
        ConstantPoolInfo newPoolInfo = new ConstantPoolInfo();
        newPoolInfo.setConstPool(currClassFile.constantPool);
        newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Utf8;
        newPoolInfo.sUTFStr = (null == sFldName) ? getUniqueFieldName() : sFldName;
        currClassFile.constantPool.addNewPoolInfo(newPoolInfo);
        return newPoolInfo;
    }
    
    private void extractConstPoolInfo() {
        int iMaxPoolLen = constPool.getPoolInfoCount();
        int iIndex;
        hashUTF = new Hashtable();
        hashDesc = new Hashtable();
        
        for (iIndex=0; iIndex < iMaxPoolLen; iIndex++) {
            ConstantPoolInfo thisInfo = (ConstantPoolInfo)constPool.getPoolInfo(iIndex+1);
            if(ConstantPoolInfo.CONSTANT_Utf8 == thisInfo.iTag) {
                if(classfile.Utils.isJavaIdentifier(thisInfo.sUTFStr)) {
                    hashUTF.put(thisInfo.sUTFStr, new Integer(iIndex+1));
                }
                String sDesc = classfile.Utils.getReadableDesc(thisInfo.sUTFStr);
                if(!"unknown".equals(sDesc)) {
                    hashDesc.put(sDesc, new Integer(iIndex+1));
                }
            }
        }
        
        Enumeration allKeys;
        strUTF = new String[hashUTF.size()];
        allKeys=hashUTF.keys();
        for (iIndex=0; allKeys.hasMoreElements(); iIndex++) {
            strUTF[iIndex] = (String)allKeys.nextElement();
        }
        
        strDesc = new String[hashDesc.size()];
        allKeys=hashDesc.keys();
        for (iIndex=0; allKeys.hasMoreElements(); iIndex++) {
            strDesc[iIndex] = (String)allKeys.nextElement();
        }
    }
}
