/*
 * LocalVariableTableAttribTableModel.java
 *
 * Created on February 11, 2002, 9:35 AM
 *
 * Modification Log:
 * 1.00   11th Feb 2002   Tanmay   Original version.
 * 1.10   16th Mar 2002   Tanmay   Facility for adding and deleting entries.
 * 1.20   19th May 2002   Tanmay   Added checks for handling obfuscated classes.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */

package guihelper.attributes;

import classfile.*;
import classfile.attributes.LocalVariableTableAttribute;
import classfile.attributes.LocalVariableTableEntry;

import javax.swing.*;
import javax.swing.table.*;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/** This class manages the table model for the local variable table
 * attribute of code attribute.
 * This is the interconnect between the UI and class file data.
 * <br><br>
 *
 * @author     Tanmay K. Mohapatra
 * @version    1.20, 19th May, 2002
 */

public class LocalVariableTableAttribTableModel extends javax.swing.table.AbstractTableModel {
    
    private final String[] columnNames = {
        "Index in method",
        "Descriptor",
        "Name",
        "Start PC",
        "Length"
    };
    
    private Object[][] data;
    private LocalVariableTableAttribute attribute;
    private ConstantPool constPool;
    private Hashtable hashUTF, hashDesc;
    private String [] strUTF;
    private String [] strDesc;
    private boolean bEditMode;
    
    /** Creates new LocalVariableTableAttribTableModel
     * @param attributeIn The attribute for which to create the table model.
     *
     */
    public LocalVariableTableAttribTableModel(LocalVariableTableAttribute attributeIn, ConstantPool constPoolIn) {
        attribute = attributeIn;
        constPool= constPoolIn;
        createData();
        if(null != constPool) {
            extractConstPoolInfo();
        }
        else {
            hashUTF = null;
            strUTF = null;
        }
    }
    
    private void createData() {
        if(null == attribute) return;
        
        int iIndex, iMaxCount;
        Vector vectEntries = attribute.vectLocalVariableTable;
        iMaxCount = vectEntries.size();
        data = new Object[iMaxCount][columnNames.length];
        for (iIndex=0; iIndex < iMaxCount; iIndex++) {
            setValueAt(vectEntries.elementAt(iIndex), iIndex, 0);
        }
    }
    
    public void setCellEditors(JTable tbl) {
        TableColumn thisCol;
        
        JComboBox descEditor = new JComboBox(strDesc);
        descEditor.setEditable(true);
        thisCol = tbl.getColumnModel().getColumn(1);
        thisCol.setCellEditor(new DefaultCellEditor(descEditor));
        
        JComboBox nameEditor = new JComboBox(strUTF);
        nameEditor.setEditable(true);
        thisCol = tbl.getColumnModel().getColumn(2);
        thisCol.setCellEditor(new DefaultCellEditor(nameEditor));
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
        if( (null != data) && (data.length > 0) ) {
            return data[0][col].getClass();
        }
        return new Object().getClass();
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
        return bEditMode;
    }
    
    /** Sets the editable property of the table.
     * @param bEditModeIn Edit mode, true to enable editing.
     */
    public void setEditable(boolean bEditModeIn) {
        bEditMode = bEditModeIn;
    }
    
    /** Sets the value either for a complete row or for a cell.
     * @param val The value as input.
     * If val is instance of MethodInfo, it is likely to have been called internally.
     * Otherwise it is called when the table cell is edited.
     * @param row Row number
     * @param col Column number
     */
    public void setValueAt(Object val, int row, int col) {
        LocalVariableTableEntry localVariable;
        
        if(val instanceof LocalVariableTableEntry) {
            localVariable = (LocalVariableTableEntry)val;
            data[row][0] = new Integer(localVariable.iIndex);
            data[row][1] = (null != localVariable.cpDescriptor) ? Utils.getReadableDesc(localVariable.cpDescriptor.sUTFStr) : "";
            data[row][2] = (null != localVariable.cpName) ? localVariable.cpName.sUTFStr : "";
            data[row][3] = new Integer(localVariable.iStartPC);
            data[row][4] = new Integer(localVariable.iLength);
        }
        else {
            localVariable = (LocalVariableTableEntry)attribute.vectLocalVariableTable.elementAt(row);
            switch(col) {
                case 0:
                    data[row][0] = val;
                    localVariable.iIndex = ((Integer)val).intValue();
                    break;
                case 1:
                    String sDesc = (String)val;
                    localVariable.cpDescriptor.deleteRef();
                    localVariable.cpDescriptor = searchAddOrModifyLocalVarDescInConstPool(sDesc, localVariable.cpDescriptor);
                    localVariable.cpDescriptor.addRef();
                    data[row][1] = val;
                    break;
                case 2:
                    String sName = (String)val;
                    localVariable.cpName.deleteRef();
                    localVariable.cpName = searchAddOrModifyLocalVarNameInConstPool(sName, localVariable.cpName);
                    localVariable.cpName.addRef();
                    data[row][2] = val;
                    break;
                case 3:
                    data[row][3] = val;
                    localVariable.iStartPC = ((Integer)val).intValue();
                    break;
                case 4:
                    data[row][4] = val;
                    localVariable.iLength = ((Integer)val).intValue();
                    break;
            }
        }
    }
    
    public void addNewEntry() {
        LocalVariableTableEntry lvEntry = new LocalVariableTableEntry();
        lvEntry.iStartPC = lvEntry.iLength = 0;
        lvEntry.constPool = constPool;
        lvEntry.cpName = addNewLocalVarNameInConstPool(null);
        lvEntry.cpDescriptor = addNewLocalVarDescInConstPool(null);
        attribute.addEntry(lvEntry);
    }
    
    private ConstantPoolInfo addNewLocalVarDescInConstPool(String sReadableDesc) {
        String sDesc = null;
        
        if(null != sReadableDesc) {
            sDesc = classfile.Utils.getRawDesc(sReadableDesc);
            if(sDesc.equals("unknown")) return null;
        }
        
        ConstantPoolInfo newPoolInfo = new ConstantPoolInfo();
        newPoolInfo.setConstPool(constPool);
        newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Utf8;
        newPoolInfo.sUTFStr = (null == sDesc) ? "I" : sDesc;
        constPool.addNewPoolInfo(newPoolInfo);
        return newPoolInfo;
    }
    
    private ConstantPoolInfo addNewLocalVarNameInConstPool(String sVarName) {
        ConstantPoolInfo newPoolInfo = new ConstantPoolInfo();
        newPoolInfo.setConstPool(constPool);
        newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Utf8;
        newPoolInfo.sUTFStr = (null == sVarName) ? getUniqueLocalVarName() : sVarName;
        constPool.addNewPoolInfo(newPoolInfo);
        return newPoolInfo;
    }
    
    private String getUniqueLocalVarName() {
        for(int iIndex=0; iIndex < Integer.MAX_VALUE; iIndex++) {
            String sVarName = "NewVar" + iIndex;
            if(null == hashUTF.get(sVarName)) return sVarName;
        }
        return "Phew";
    }
    
    
    /**
     * Either creates a new UTF8 constant pool entry with sReadableDesc or modifies the
     * supplied UTF8 constant pool entry prevPoolInfo depending on whether
     * someone else is using the pool entry.
     * It is important to decrement reference count for the current variable before
     * calling this method, otherwise this method will always add a new entry to
     * the pool!
     */
    private ConstantPoolInfo searchAddOrModifyLocalVarDescInConstPool(String sReadableDesc, ConstantPoolInfo prevPoolInfo) {
        Integer poolIndex = (Integer)hashDesc.get(sReadableDesc);
        
        if(null == poolIndex) {
            if(prevPoolInfo.getRef() > 0) {
                // the current pool entry is being referred to somebody else
                return addNewLocalVarDescInConstPool(sReadableDesc);
            }
            else {
                // no one is referring to this, we can modify it for our purpose
                prevPoolInfo.sUTFStr = classfile.Utils.getRawDesc(sReadableDesc);
                return prevPoolInfo;
            }
        }
        else {
            return constPool.getPoolInfo(poolIndex.intValue());
        }
    }
    
    /**
     * Either creates a new UTF8 constant pool entry with sName or modifies the
     * supplied UTF8 constant pool entry prevPoolInfo depending on whether
     * someone else is using the pool entry.
     * It is important to decrement reference count for the current variable before
     * calling this method, otherwise this method will always add a new entry to
     * the pool!
     */
    private ConstantPoolInfo searchAddOrModifyLocalVarNameInConstPool(String sName, ConstantPoolInfo prevPoolInfo) {
        Integer poolIndex = (Integer)hashUTF.get(sName);
        
        if(null == poolIndex) {
            if(prevPoolInfo.getRef() > 0) {
                // the current pool entry is being referred to somebody else
                return addNewLocalVarNameInConstPool(sName);
            }
            else {
                // no one is referring to this, we can modify it for our purpose
                prevPoolInfo.sUTFStr = sName;
                return prevPoolInfo;
            }
        }
        else {
            // return the existing pool entry
            return constPool.getPoolInfo(poolIndex.intValue());
        }
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
