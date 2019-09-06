/*
 * InstructionsTableModel.java
 *
 * Created on May 11, 2002, 12:16 PM
 *
 * Modification Log:
 * 1.00   11th May 2002   Tanmay   Original version.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */

package guihelper.attributes;

import classfile.*;
import classfile.attributes.Code;
import classfile.attributes.Instruction;
import classfile.attributes.ExceptionTableEntry;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.Hashtable;
import java.util.Enumeration;

/** This class manages the table model for the code instructions list.
 * This is the interconnect between the UI and class file data.
 * <br><br>
 *
 * @author     Tanmay K. Mohapatra
 * @version    1.00, 11th May, 2002
 */

public class InstructionsTableModel extends javax.swing.table.AbstractTableModel {
    private final String[] columnNames = {
        "Index",
        "Byte Index",
        "Instruction",
    };

    private Object[][] data;
    private Code code;
    private ConstantPool constPool;
    private boolean bEditMode;
    
    /** Creates new InstructionsTableModel */
    public InstructionsTableModel(ConstantPool constPoolIn, Code code) {
        if(null != constPoolIn) {
            constPool= constPoolIn;
            this.code = code;
        }
        else {
            constPool = null;
            this.code = null;
        }

        createData();
    }
    /** Sets appropriate editors for the columns.
     * @param tbl The table for which to set column editors.
     */
    public void setCellEditors(JTable tbl) {
        /*
        TableColumn thisCol;

        JComboBox descEditor = new JComboBox(strDesc);
        descEditor.setEditable(true);
        thisCol = tbl.getColumnModel().getColumn(1);
        thisCol.setCellEditor(new DefaultCellEditor(descEditor));
         */
    }
    
    private void createData() {
        if(null == code) return;

        int iIndex, iMaxCount;
        iMaxCount = code.vectCode.size();
        data = new Object[iMaxCount][columnNames.length];
        for (iIndex=0; iIndex < iMaxCount; iIndex++) {
            Instruction thisInstr = (Instruction)code.vectCode.elementAt(iIndex);
            setValueAt(thisInstr, iIndex, 0);
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
        if((0 == col) || (1 == col)) return false;
        return bEditMode;
    }
    
    /** Sets the editable property of the table.
     * @param bEditModeIn Edit mode, true to enable editing.
     */
    public void setEditable(boolean bEditModeIn) {
        bEditMode = bEditModeIn;
    }

    private int getByteIndex(int row) {
        if(row == 0) return 0;
        Instruction thisInstr = (Instruction)code.vectCode.elementAt(row-1);
        return ((Integer)data[row-1][1]).intValue() + thisInstr.iDataLength + 1;
    }
    
    /** Sets the value either for a complete row or for a cell.
     * @param val The value as input.
     * If val is instance of FieldInfo, it is likely to have been called internally.
     * Otherwise it is called when the table cell is edited.
     * @param row Row number
     * @param col Column number
     */
    public void setValueAt(Object val, int row, int col) {
        if(val instanceof Instruction) {
            Instruction instr = (Instruction)val;
            data[row][0] = new Integer(row);
            data[row][1] = new Integer(getByteIndex(row));
            data[row][2] = val.toString();
        }
        else {
            Instruction instr = (Instruction)code.vectCode.elementAt(row);
            Integer intVal;
            switch(col) {
                case 1:
                    break;
            }
        }
    }
}
