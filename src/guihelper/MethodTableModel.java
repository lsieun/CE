/*
 * MethodTableModel.java
 *
 * Created on January 5, 2002, 2:42 AM
 *
 * Modification Log:
 * 1.00   5th Jan 2002   Tanmay   Original version.
 * 1.01   15th Jan 2002   Tanmay   Implemented access flag editor dialog
 * 1.02   14th Mar 2002   Tanmay   Corrected ArrayBoundsException in edit mode
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
import java.util.Stack;
import java.util.StringTokenizer;

/** This class manages the table model for the methods list.
 * This is the interconnect between the UI and class file data.
 * <br><br>
 *
 *
 * @author     Tanmay K. Mohapatra
 * @version    1.02, 14th Mar, 2002
 */

public class MethodTableModel extends javax.swing.table.AbstractTableModel {

    private final String[] columnNames = {
        "Index",
        "Access & Modifier Flags",
        "Name",
        "Descriptor"
    };
    
    private Object[][] data;
    private ClassFile currClassFile;
    private ConstantPool constPool;
    private Hashtable hashUTF, hashDesc;
    private String [] strUTF;
    private String [] strDesc;
    private boolean bEditMode;
    
    /** Creates new MethodTableModel
     * @param classFileIn The class file for which to create the table model.
     *
     */
    public MethodTableModel(ClassFile classFileIn) {
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
        flgEditorDlg.setValidAccessFlags(AccessFlags.getValidFlags(AccessFlags.METHOD_VALID_FLAGS));
        
        thisCol = tbl.getColumnModel().getColumn(1);
        thisCol.setCellEditor(flgEditor);
        
        btnEditor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                flgEditorDlg.setAccessFlags(flgEditor.currFlags);
                flgEditorDlg.setLocationRelativeTo((Component)btnEditor);
                flgEditorDlg.show();
            }
        });
        
        JComboBox nameEditor = new JComboBox(strUTF);
        nameEditor.setEditable(true);
        thisCol = tbl.getColumnModel().getColumn(2);
        thisCol.setCellEditor(new DefaultCellEditor(nameEditor));
        
        JComboBox descEditor = new JComboBox(strDesc);
        descEditor.setEditable(true);
        thisCol = tbl.getColumnModel().getColumn(3);
        thisCol.setCellEditor(new DefaultCellEditor(descEditor));
    }
    
    private void createData() {
        if(null == currClassFile) return;
        
        int iIndex, iMaxCount;
        iMaxCount = currClassFile.methods.getMethodsCount();
        data = new Object[iMaxCount][columnNames.length];
        for (iIndex=0; iIndex < iMaxCount; iIndex++) {
            MethodInfo thisInfo = currClassFile.methods.getMethod(iIndex);
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
     * method list for the specified string.
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
     * If val is instance of MethodInfo, it is likely to have been called internally.
     * Otherwise it is called when the table cell is edited.
     * @param row Row number
     * @param col Column number
     */
    public void setValueAt(Object val, int row, int col) {
        if(val instanceof MethodInfo) {
            MethodInfo methodInfo = (MethodInfo)val;
            
            String [] aParams = methodInfo.getMethodDesc();
            String sMethodDesc = aParams[0] + " (";
            for(int iParamIndex=1; iParamIndex < aParams.length; iParamIndex++) {
                sMethodDesc += aParams[iParamIndex];
                if ((iParamIndex+1) < aParams.length) sMethodDesc += ",";
            }
            sMethodDesc += ")";
            
            data[row][0] = new Integer(row+1);
            data[row][1] = methodInfo.accessFlags;
            data[row][2] = methodInfo.getMethodName();
            data[row][3] = sMethodDesc;
        }
        else {
            MethodInfo thisMethod = currClassFile.methods.getMethod(row);
            AccessFlags prevAccFlg = thisMethod.accessFlags;
            switch(col) {
                case 1:
                    thisMethod.accessFlags = (AccessFlags)val;
                    data[row][1] = thisMethod.accessFlags;
                    break;
                case 2:
                    String sName = (String)val;
                    thisMethod.cpName.deleteRef();
                    thisMethod.cpName = searchAddOrModifyMethodNameInConstPool(sName, thisMethod.cpName);
                    thisMethod.cpName.addRef();
                    data[row][2] = sName;
                    break;
                case 3:
                    String sDesc = (String)val;
                    thisMethod.cpDescriptor.deleteRef();
                    thisMethod.cpDescriptor = searchAddOrModifyMethodDescInConstPool(sDesc, thisMethod.cpDescriptor);
                    thisMethod.cpDescriptor.addRef();
                    data[row][3] = sDesc;
                    break;
            }
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
                String []sDesc = classfile.Utils.getReadableMethodDesc(thisInfo.sUTFStr);
                if(null == sDesc) continue;
                // check if any item was not resolved
                boolean bDescOK = true;
                for(int iDescIndex=0; iDescIndex < sDesc.length; iDescIndex++)
                {
                    if("unknown".equals(sDesc[iDescIndex]))
                    {
                        bDescOK = false;
                        break;
                    }
                }
                if(!bDescOK) continue;
                
                // prepare the final desc
                String sMethodDesc = sDesc[0] + " (";
                for(int iParamIndex=1; iParamIndex < sDesc.length; iParamIndex++) {
                    sMethodDesc += sDesc[iParamIndex];
                    if ((iParamIndex+1) < sDesc.length) sMethodDesc += ",";
                }
                sMethodDesc += ")";
                
                hashDesc.put(sMethodDesc, new Integer(iIndex+1));
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

    /**
     * Either creates a new UTF8 constant pool entry with sName or modifies the
     * supplied UTF8 constant pool entry prevPoolInfo depending on whether
     * someone else is using the pool entry.
     * It is important to decrement reference count for the current method before
     * calling this method, otherwise this method will always add a new entry to
     * the pool!
     */
    private ConstantPoolInfo searchAddOrModifyMethodNameInConstPool(String sName, ConstantPoolInfo prevPoolInfo) {
        Integer poolIndex = (Integer)hashUTF.get(sName);
        
        if(null == poolIndex) {
            if(prevPoolInfo.getRef() > 0) {
                // the current pool entry is being referred to somebody else
                return addNewMethodNameInConstPool(sName);
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
        
    private ConstantPoolInfo addNewMethodNameInConstPool(String sMethodName) {
        ConstantPoolInfo newPoolInfo = new ConstantPoolInfo();
        newPoolInfo.setConstPool(currClassFile.constantPool);
        newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Utf8;
        newPoolInfo.sUTFStr = (null == sMethodName) ? getUniqueMethodName() : sMethodName;
        currClassFile.constantPool.addNewPoolInfo(newPoolInfo);
        return newPoolInfo;
    }

    private String getUniqueMethodName() {
        for(int iIndex=0; iIndex < Integer.MAX_VALUE; iIndex++) {
            String sMethodName = "NewMethod" + iIndex;
            if(null == hashUTF.get(sMethodName)) return sMethodName;
        }
        return "Phew";
    }    

    /**
     * Either creates a new UTF8 constant pool entry with sReadableDesc or modifies the
     * supplied UTF8 constant pool entry prevPoolInfo depending on whether
     * someone else is using the pool entry.
     * It is important to decrement reference count for the current method before
     * calling this method, otherwise this method will always add a new entry to
     * the pool!
     */
    private ConstantPoolInfo searchAddOrModifyMethodDescInConstPool(String sReadableDesc, ConstantPoolInfo prevPoolInfo) {
        Integer poolIndex = (Integer)hashDesc.get(sReadableDesc);
        
        if(null == poolIndex) {
            if(prevPoolInfo.getRef() > 0) {
                // the current pool entry is being referred to somebody else
                return addNewMethodDescInConstPool(sReadableDesc);
            }
            else {
                // no one is referring to this, we can modify it for our purpose
                prevPoolInfo.sUTFStr = classfile.Utils.getRawMethodDesc(getArrayFromMethodDescString(sReadableDesc));
                return prevPoolInfo;
            }
        }
        else {
            return currClassFile.constantPool.getPoolInfo(poolIndex.intValue());
        }
    }
    
    private String [] getArrayFromMethodDescString(String sReadableDesc)
    {
        Stack ParamStack = new Stack();
        StringTokenizer stTok = new StringTokenizer(sReadableDesc, " (,)", false);
        while(stTok.hasMoreTokens())
        {
            ParamStack.push(stTok.nextToken());
        }
        int iNumParams = ParamStack.size();
        if(iNumParams == 0) return null;
        String []asDesc = new String[iNumParams];
        for(int iIndex=0; iIndex<iNumParams; iIndex++)
        {
            asDesc[iNumParams-iIndex-1] = (String)ParamStack.pop();
        }
        
        return asDesc;
    }
    
    private ConstantPoolInfo addNewMethodDescInConstPool(String sReadableDesc) {
        String sDesc = null;
        
        if(null != sReadableDesc) {
            sDesc = classfile.Utils.getRawMethodDesc(getArrayFromMethodDescString(sReadableDesc));
            if(sDesc.equals("unknown")) return null;
        }
        
        ConstantPoolInfo newPoolInfo = new ConstantPoolInfo();
        newPoolInfo.setConstPool(currClassFile.constantPool);
        newPoolInfo.iTag = ConstantPoolInfo.CONSTANT_Utf8;
        newPoolInfo.sUTFStr = (null == sDesc) ? "()V" : sDesc;
        currClassFile.constantPool.addNewPoolInfo(newPoolInfo);
        return newPoolInfo;
    }

    /** Adds a new field in the field list.
     */
    public void addNewMethod() {
        MethodInfo thisMethod = new MethodInfo();
        AccessFlags thisAccFlg = new AccessFlags();
        thisAccFlg.setPublic(false);
        thisAccFlg.setProtected(false);
        thisAccFlg.setStatic(false);
        thisAccFlg.setFinal(false);
        thisAccFlg.setSynchronized(false);
        thisAccFlg.setNative(false);
        thisAccFlg.setAbstract(false);
        thisAccFlg.setPrivate(false);
        thisMethod.accessFlags = thisAccFlg;
        thisMethod.attributes = new Attributes();
        
        thisMethod.cpName = addNewMethodNameInConstPool(null);

        if(hashDesc.size() > 0) {
            String sDesc = (String)hashDesc.keys().nextElement();
            int iDescIndex = ((Integer)hashDesc.get(sDesc)).intValue();
            thisMethod.cpDescriptor = currClassFile.constantPool.getPoolInfo(iDescIndex);
        }
        else {
            thisMethod.cpDescriptor = addNewMethodDescInConstPool(null);
        }
                
        currClassFile.methods.addMethod(thisMethod);        
    }    
}
