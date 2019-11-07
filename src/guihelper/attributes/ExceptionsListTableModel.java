/*
 * ExceptionsListTableModel.java
 *
 * Created on May 05, 2002, 4:22 PM
 *
 * Modification Log:
 * 1.00   05th May 2002   Tanmay   Original version.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */


package guihelper.attributes;

import classfile.*;
import classfile.attributes.ExceptionsAttribute;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.Hashtable;
import java.util.Enumeration;

/** This class manages the table model for the method exceptions list.
 * This is the interconnect between the UI and class file data.
 * <br><br>
 *
 * @author     Tanmay K. Mohapatra
 * @version    1.00, 05th May, 2002
 */

public class ExceptionsListTableModel extends AbstractTableModel {

    private final String[] columnNames = {
        "Index",
        "Exception",
    };

    private Object[][] data;
    private ExceptionsAttribute exceptAttrib;
    private ConstantPool constPool;
    private Hashtable hashDesc;
    private String [] strDesc;
    private boolean bEditMode;
    
    /** Creates new CodeExceptionsListTableModel */
    public ExceptionsListTableModel(ConstantPool constPoolIn, ExceptionsAttribute exceptAttribIn) {
        if(null != constPoolIn) {
            constPool= constPoolIn;
            exceptAttrib = exceptAttribIn;
            extractConstPoolInfo();
        }
        else {
            constPool = null;
            exceptAttrib = null;
            hashDesc = null;
            strDesc = null;
        }

        createData();
    }

    
    /** Sets appropriate editors for the columns.
     * @param tbl The table for which to set column editors.
     */
    public void setCellEditors(JTable tbl) {
        TableColumn thisCol;

        JComboBox descEditor = new JComboBox(strDesc);
        descEditor.setEditable(true);
        thisCol = tbl.getColumnModel().getColumn(1);
        thisCol.setCellEditor(new DefaultCellEditor(descEditor));
    }
    
    private void createData() {
        if(null == exceptAttrib) return;

        int iIndex, iMaxCount;
        iMaxCount = exceptAttrib.vectExceptionTypes.size();
        data = new Object[iMaxCount][columnNames.length];
        for (iIndex=0; iIndex < iMaxCount; iIndex++) {
            ConstantPoolInfo exceptEntry = (ConstantPoolInfo)exceptAttrib.vectExceptionTypes.elementAt(iIndex);
            setValueAt(exceptEntry, iIndex, 0);
        }
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
    
    /** Returns the column heading string.
     * @param col Column number
     * @return Column heading string
     */
    public String getColumnName(int col) {
        return columnNames[col];
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
    
    private String GetExceptionDisplayStr(ConstantPoolInfo exceptEntry)
    {
        return Utils.convertClassStrToStr(exceptEntry.refUTF8.sUTFStr);
    }
    
    /** Sets the value either for a complete row or for a cell.
     * @param val The value as input.
     * If val is instance of FieldInfo, it is likely to have been called internally.
     * Otherwise it is called when the table cell is edited.
     * @param row Row number
     * @param col Column number
     */
    public void setValueAt(Object val, int row, int col) {
        if(val instanceof ConstantPoolInfo) {
            ConstantPoolInfo exceptEntry = (ConstantPoolInfo)val;
            String sExceptionClass = GetExceptionDisplayStr(exceptEntry);
            data[row][0] = new Integer(row+1);
            data[row][1] = sExceptionClass;
        }
        else {
            ConstantPoolInfo exceptEntry = (ConstantPoolInfo)exceptAttrib.vectExceptionTypes.elementAt(row);
            Integer intVal;
            switch(col) {
                case 1:
                    String sReadableDesc = (String)val;
                    setCatchTypeUTF(exceptEntry, sReadableDesc);
                    data[row][1] = GetExceptionDisplayStr(exceptEntry);
                    break;
                default:
                    break; //ignore;
            }
        }
    }

    
    /** Adds a new exception in the exception list.
     */
    public void addNewExceptionEntry() {
        ConstantPoolInfo exceptEntry = new ConstantPoolInfo();
        exceptEntry.setConstPool(constPool);
        exceptEntry.iTag = ConstantPoolInfo.CONSTANT_Class;
        
        ConstantPoolInfo exceptUTFEntry = addNewExceptionClassUTFInConstPool(null);
        exceptEntry.iNameIndex = constPool.getIndexOf(exceptUTFEntry);
        exceptEntry.refUTF8 = exceptUTFEntry;
        
        constPool.addNewPoolInfo(exceptEntry);
        exceptAttrib.vectExceptionTypes.add(exceptEntry);
        (exceptAttrib.iNumExceptions)++;
    }


    private void setCatchTypeUTF(ConstantPoolInfo thisEntry, String sReadableDesc)
    {
        thisEntry.refUTF8.deleteRef();
        ConstantPoolInfo exceptUTFEntry = searchAddOrModifyExceptionClassUTFInConstPool(sReadableDesc, thisEntry.refUTF8);
        thisEntry.iNameIndex = constPool.getIndexOf(exceptUTFEntry);
        thisEntry.refUTF8 = exceptUTFEntry;
        thisEntry.refUTF8.addRef();
    }

    
    /**
     * Either creates a new UTF8 constant pool entry with sReadableDesc or modifies the
     * supplied UTF8 constant pool entry prevPoolInfo depending on whether
     * someone else is using the pool entry.
     * It is important to decrement reference count for the current string before
     * calling this method, otherwise this method will always add a new entry to
     * the pool!
     */
    private ConstantPoolInfo searchAddOrModifyExceptionClassUTFInConstPool(String sReadableDesc, ConstantPoolInfo prevPoolInfo) {
        Integer poolIndex = (Integer)hashDesc.get(sReadableDesc);

        if(null == poolIndex) {
            if((null == prevPoolInfo) || (prevPoolInfo.getRef() > 0)) {
                // the current pool entry is being referred to somebody else 
                // or there is no prev pool entry
                return addNewExceptionClassUTFInConstPool(sReadableDesc);
            }
            else {
                // no one is referring to this, we can modify it for our purpose
                prevPoolInfo.sUTFStr = Utils.convertStrToClassStr(sReadableDesc);
                return prevPoolInfo;
            }
        }
        else {
            return constPool.getPoolInfo(poolIndex.intValue());
        }
    }
    
    private ConstantPoolInfo addNewExceptionClassUTFInConstPool(String sReadableDesc) {
        String sDesc = null;

        if(null != sReadableDesc) {
            sDesc = classfile.Utils.getRawDesc(sReadableDesc);
            if(sDesc.equals("unknown")) return null;
        }

        ConstantPoolInfo newPoolInfo = new ConstantPoolInfo();
        newPoolInfo.setConstPool(constPool);
        newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Utf8;
        newPoolInfo.sUTFStr = (null == sDesc) ? "java/lang/Exception" : sDesc;
        constPool.addNewPoolInfo(newPoolInfo);
        return newPoolInfo;
    }

    private void extractConstPoolInfo() {
        int iMaxPoolLen = constPool.getPoolInfoCount();
        int iIndex;
        hashDesc = new Hashtable();

        for (iIndex=0; iIndex < iMaxPoolLen; iIndex++) {
            ConstantPoolInfo thisInfo = (ConstantPoolInfo)constPool.getPoolInfo(iIndex+1);
            if(ConstantPoolInfo.CONSTANT_Utf8 == thisInfo.iTag) {
                if(classfile.Utils.isJavaClassString(thisInfo.sUTFStr))
                {
                    String sDesc = Utils.convertClassStrToStr(thisInfo.sUTFStr);
                    hashDesc.put(sDesc, new Integer(iIndex+1));
                }
            }
        }

        Enumeration allKeys;
        strDesc = new String[hashDesc.size()];
        allKeys=hashDesc.keys();
        for (iIndex=0; allKeys.hasMoreElements(); iIndex++) {
            strDesc[iIndex] = (String)allKeys.nextElement();
        }
    }
}
