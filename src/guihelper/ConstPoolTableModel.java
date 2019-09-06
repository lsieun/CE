/*
 * ConstPooTableModel.java
 *
 * Created on December 23, 2001, 11:20 AM
 *
 * Modification Log:
 * 1.00   23rd Dec 2001   Tanmay   Original version.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */

package guihelper;

import classfile.*;
import javax.swing.ImageIcon;

/**
 * The table model for Constant Pool GUI. THis class is the link between the
 * class data structures and the Swing GUI.
 *
 *
 * @author 	Tanmay K. Mohapatra
 * @version     1.00, 23rd December, 2001
 */
public class ConstPoolTableModel extends javax.swing.table.AbstractTableModel {
    
    private final String[] columnNames = {
        "Index",
        "Type",
        "Entry"
    };
    
    public static int NO_FILTER = -1;
    private int iFilterTag = NO_FILTER;
    private Object[][] data;
    private Object[][] filteredData;
    private ConstantPool constPool;
    
    private static ImageIcon imgClass;
    private static ImageIcon imgField;
    private static ImageIcon imgMethod;
    private static ImageIcon imgIface;
    private static ImageIcon imgString;
    private static ImageIcon imgInt;
    private static ImageIcon imgFloat;
    private static ImageIcon imgLong;
    private static ImageIcon imgDouble;
    private static ImageIcon imgNameAndType;
    private static ImageIcon imgUTF;
    private static ImageIcon imgUnknown;
    
    /** Creates new ConstPooTableModel */
    public ConstPoolTableModel(ConstantPool constPoolIn) {
        constPool = constPoolIn;
        imgClass = new javax.swing.ImageIcon(getClass().getResource("/res/class.gif"));
        imgField = new javax.swing.ImageIcon(getClass().getResource("/res/field.gif"));
        imgMethod = new javax.swing.ImageIcon(getClass().getResource("/res/method.gif"));
        imgIface = new javax.swing.ImageIcon(getClass().getResource("/res/interface.gif"));
        imgString = new javax.swing.ImageIcon(getClass().getResource("/res/string.gif"));
        imgInt = new javax.swing.ImageIcon(getClass().getResource("/res/integer.gif"));
        imgFloat = new javax.swing.ImageIcon(getClass().getResource("/res/float.gif"));
        imgLong = new javax.swing.ImageIcon(getClass().getResource("/res/long.gif"));
        imgDouble = new javax.swing.ImageIcon(getClass().getResource("/res/double.gif"));
        imgNameAndType = new javax.swing.ImageIcon(getClass().getResource("/res/nameandtype.gif"));
        imgUTF = new javax.swing.ImageIcon(getClass().getResource("/res/utf.gif"));
        imgUnknown = new javax.swing.ImageIcon(getClass().getResource("/res/report1.gif"));
        createData();
    }
    
    
    private void createData() {
        if(null == constPool) return;
        
        int iIndex, iMaxCount;
        iMaxCount = constPool.getPoolInfoCount();
        data = new Object[iMaxCount][columnNames.length];
        for (iIndex=0; iIndex < iMaxCount; iIndex++) {
            ConstantPoolInfo thisInfo = (ConstantPoolInfo)constPool.getPoolInfo(iIndex+1);
            setValueAt(data, thisInfo, iIndex, 0);
        }
        applyFilter();
    }
    
    public void setFilter(int iFilterTagIn) {
        iFilterTag = iFilterTagIn;
    }
    
    public void applyFilter() {
        int iFilteredDataCount = data.length;
        int iIndex, iFilteredIndex;
        
        if(NO_FILTER != iFilterTag) {
            // count the filtered data count
            iFilteredDataCount = 0;
            for (iIndex=0; iIndex < data.length; iIndex++) {
                ConstantPoolInfo thisInfo = (ConstantPoolInfo)constPool.getPoolInfo(iIndex+1);
                if(thisInfo.iTag == iFilterTag) iFilteredDataCount++;
            }
        }
        
        filteredData = null;
        if(iFilteredDataCount > 0) {
            filteredData = new Object[iFilteredDataCount][columnNames.length];
            for (iIndex=iFilteredIndex=0; iIndex < data.length; iIndex++) {
                ConstantPoolInfo thisInfo = (ConstantPoolInfo)constPool.getPoolInfo(iIndex+1);
                if( (thisInfo.iTag == iFilterTag) || (NO_FILTER == iFilterTag) ) {
                    setValueAt(filteredData, thisInfo, iFilteredIndex, 0);
                    iFilteredIndex++;
                }
            }
        }
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public Class getColumnClass(int col) {
        return data[0][col].getClass();
    }
    
    public java.lang.Object getValueAt(int row, int col) {
        if( (null != filteredData) && (filteredData.length > 0) ) {
            return filteredData[row][col];
        }
        return null;
    }
    
    public int getRowCount() {
        if(null == filteredData) return 0;
        return filteredData.length;
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public boolean isCellEditable(int row, int col) {
        return false;
    }
    
    public int nextIndex(int iStartIndex, String sSrchStr) {
        if(null == filteredData) return -1;
        for (; iStartIndex < filteredData.length; iStartIndex++) {
            if(((String)(filteredData[iStartIndex][2])).indexOf(sSrchStr) >= 0) break;
        }
        return (iStartIndex < filteredData.length) ? iStartIndex : -1;
    }
    
    public int getModelIndex(int iConstPoolIndex) {
        if ( (iConstPoolIndex <= 0) || (iConstPoolIndex > data.length) || (null == filteredData) ) return -1;
        for(int iIndex=0; iIndex < filteredData.length; iIndex++) {
            if(iConstPoolIndex == ((Integer)getValueAt(iIndex, 0)).intValue()) {
                return iIndex;
            }
        }
        return -1;
    }
    
    /**
     * Updates both data and filteredData arrays.
     * This methods is NOT to be used from within the class.
     */
    public void setValueAt(Object val, int row, int col) {
        setValueAt(filteredData, val, row, col);
        ConstantPoolInfo thisInfo = (ConstantPoolInfo)val;
        setValueAt(data, val, constPool.getIndexOf(thisInfo)-1, col);
    }
    
    private void setValueAt(Object [][]dataIn, Object val, int row, int col) {
        ConstantPoolInfo thisInfo = (ConstantPoolInfo)val;
        
        dataIn[row][0] = new Integer(constPool.getIndexOf(thisInfo));
        dataIn[row][1] = tag2Image(thisInfo.iTag);
        dataIn[row][2] = thisInfo.getExtraInfoString();
    }
    
    private ImageIcon tag2Image(int iTag) {
        ImageIcon img = imgUnknown;
        
        switch(iTag) {
            case ConstantPoolInfo.CONSTANT_Class:
                img = imgClass;
                break;
            case ConstantPoolInfo.CONSTANT_Fieldref:
                img = imgField;
                break;
            case ConstantPoolInfo.CONSTANT_Methodref:
                img = imgMethod;
                break;
            case ConstantPoolInfo.CONSTANT_InterfaceMethodref:
                img = imgIface;
                break;
            case ConstantPoolInfo.CONSTANT_String:
                img = imgString;
                break;
            case ConstantPoolInfo.CONSTANT_Integer:
                img = imgInt;
                break;
            case ConstantPoolInfo.CONSTANT_Float:
                img = imgFloat;
                break;
            case ConstantPoolInfo.CONSTANT_Long:
                img = imgLong;
                break;
            case ConstantPoolInfo.CONSTANT_Double:
                img = imgDouble;
                break;
            case ConstantPoolInfo.CONSTANT_NameAndType:
                img = imgNameAndType;
                break;
            case ConstantPoolInfo.CONSTANT_Utf8:
                img = imgUTF;
                break;
        }
        return img;
    }
}
