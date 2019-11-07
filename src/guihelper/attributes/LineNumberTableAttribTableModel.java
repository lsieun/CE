/*
 * LineNumberTableAttribTableModel.java
 *
 * Created on February 5, 2002, 2:42 AM
 *
 * Modification Log:
 * 1.00   5th Feb 2002   Tanmay   Original version.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */

package guihelper.attributes;

import classfile.attributes.LineNumberTableAttribute;
import classfile.attributes.LineNumberTableEntry;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/** This class manages the table model for the line number table
 * attribute of code attribute.
 * This is the interconnect between the UI and class file data.
 * <br><br>
 *
 * @author     Tanmay K. Mohapatra
 * @version    1.00, 5th Feb, 2002
 */

public class LineNumberTableAttribTableModel extends AbstractTableModel {

    private final String[] columnNames = {
        "",
        "Start PC",
        "Line Number"
    };
    
    private Object[][] data;
    private LineNumberTableAttribute attribute;
    private boolean bEditMode;
    
    /** Creates new LineNumberTableAttribTableModel
     * @param attributeIn The attribute for which to create the table model.
     *
     */
    public LineNumberTableAttribTableModel(LineNumberTableAttribute attributeIn) {
        attribute = attributeIn;        
        createData();
    }

    private void createData() {
        if(null == attribute) return;
        
        int iIndex, iMaxCount;
        Vector vectEntries = attribute.vectEntries;
        iMaxCount = vectEntries.size();
        data = new Object[iMaxCount][columnNames.length];
        for (iIndex=0; iIndex < iMaxCount; iIndex++) {
            setValueAt(vectEntries.elementAt(iIndex), iIndex, 0);
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
        if(0 == col) return false;
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
        LineNumberTableEntry lineNumber;
        if(val instanceof LineNumberTableEntry) {
            lineNumber = (LineNumberTableEntry)val;

            data[row][0] = new Integer(row+1);
            data[row][1] = new Integer(lineNumber.iStartPC);
            data[row][2] = new Integer(lineNumber.iLineNum);
        }
        else {
            lineNumber = (LineNumberTableEntry)attribute.vectEntries.elementAt(row);
            switch(col) {
                case 1:
                    data[row][1] = val;
                    lineNumber.iStartPC = ((Integer)val).intValue();
                    break;
                case 2:
                    data[row][2] = val;
                    lineNumber.iLineNum = ((Integer)val).intValue();
                    break;
            }
        }
    }
}
