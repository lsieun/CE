/*
 * ClassEditor.java
 *
 * Created on March 12, 1999, 9:36 AM
 *
 * Modification Log:
 * 1.00   12th Mar 1999   Tanmay   Original version.
 * 1.01   09th Jun 1999   Tanmay   Related classes functionality added.
 * 1.02   11th Jun 1999   Tanmay   Summary function added.
 * 1.03   12th Jun 1999   Tanmay   Verify function added.
 * 1.04   12th Jun 1999   Tanmay   Buffered io streams used. 
 *                                 Silly of me not to have used them before.
 * 1.05   03rd Jul 1999   Tanmay   Updated the costantant pool tab. They type 
 *                                 selector now protects/unprotects the value fields.
 * 1.06   03rd Jul 1999   Tanmay   Addition to constant pool supported.
 *                                 Pool does not update on change of selected index
 *                                 of the list.
 * 1.07   03rd Jul 1999   Tanmay   Deletion from constant pool supported.
 * 1.08   03rd Jul 1999   Tanmay   Fixed null pointer exception in interface modify button
 * 1.09   03rd Jul 1999   Tanmay   Add/remove interface supported.
 * 1.10   03rd Jul 1999   Tanmay   Search implemented in Constant Pool Tab
 * 1.11   05th Jul 1999   Tanmay   Search implemented in Fields Tab
 * 1.12   05th Jul 1999   Tanmay   Modify implemented in Fields Tab
 * 1.13   05th Jul 1999   Tanmay   Add/Delete implemented in Fields Tab
 * 1.14   17th Jul 1999   Tanmay   ConstPool tab made similar to fields tab.
 * 1.15   14th Aug 1999   Tanmay   Methods screen changed to add search facility.
 * 1.16   14th Aug 1999   Tanmay   Methods name and return type editing facility enhanced.
 * 1.17   14th Aug 1999   Tanmay   Method parameter editing facility added.
 * 1.18   14th Aug 1999   Tanmay   Functionality to delete a method added.
 * 1.19   14th Aug 1999   Tanmay   Functionality to add a new method introduced.
 * 1.20   14th Aug 1999   Tanmay   Fixed bug in add field where empty attributes was 
 *                                 giving null pointer exception.
 * 1.21   14th Aug 1999   Tanmay   Attributes dialogs show even if attribute count is 0.
 *                                 This is to enable addition of new attributes.
 * 1.22   29th Apr 2001   Tanmay   Added feature to keep multiple files open simultaneously.
 *                                 And navigate between open files through a treeview.
 * 1.23   01st May 2001   Tanmay   Fixed a NullPointerException while showing field attribs 
 *                                 of a class without any fields.
 * 1.24   01st May 2001   Tanmay   When creating a new class file, create a minimal valid class
 * 2.00   19th Sep 2001   Tanmay   Moved over to Java Swing.
 * 2.01   30th Jan 2002   Tanmay   Added modify mode toggle button.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 * 2.02  28th Sep 2003   Tanmay   Moved text summary method to visitor.
 * 2.03  18th Mar 2004   Tanmay   Added XML export option and enabled/disabled menu based on context.
 *                                Made class file chooser common so that it opens in previous folder.
 *                                Added history of opened files for ease of use.
 * 2.04  21st Mar 2004   Tanmay   Added config file and modified XMLWriter to use local schema document.
 *
 */


package gui;

import classfile.*;
import guihelper.*;
import visitors.*;
import java.io.*;
import java.util.*;
import javax.swing.tree.*;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import java.awt.Dimension;

/**
 * As the name suggests, this is a Java class file reader. Similar to the javap 
 * utility in JDK. The advantage of this over javap is the GUI interface and
 * facility for editing the class file. That means you can open a class file, 
 * edit strings, attributes and in theory anything as long as you know what you 
 * are doing. Apart from that, this is a great tool to understand the class file 
 * structure. Unlike javap, ClassEditor does not attempt to reconstruct the source. 
 * It shows the class file in exactly the same format as it is in, but in a more 
 * understandable way.
 * <br><br>
 * Usage: java -jar ce.jar [path to classfile].<br>
 *<br><br>
 *
 * @author 	Tanmay K. Mohapatra
 * @version     2.04, 21st Mar, 2004
 */

public class ClassEditor extends javax.swing.JFrame {
    
    private GeneralPropPane TabPaneGeneralProp;
    private ConstantPoolPropPane TabPaneConstPool;
    private FieldsPropPane TabPaneFields;
    private MethodsPropPane TabPaneMethods;
    private DefaultMutableTreeNode rootTreeNode;
    private ClassFile classFile;
    private ClassFileStatus classStatus;
    private boolean bEditable;
    private JFileChooser classFileChooser;
    
    // variables used for maintaining file history
    private static int NUM_FILES_IN_HISTORY = 5;
    private ArrayList asHistoryFileNames = new ArrayList();
    private ArrayList aHistoryFiles = new ArrayList();
    
    // configuration file
    private java.util.Properties Config;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        ClassEditor classEdit = new ClassEditor();
        classEdit.show();
        if(args.length > 0) {
            for(int iIndex=0; iIndex<args.length; iIndex++) {
                try {
                    classEdit.chkNLoadClass(args[iIndex]);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            // disable menu and buttons
            classEdit.setMenuAndButtonStatus();
        }
    }
    
    /** Creates new form gui */
    public ClassEditor() {
        int iIndex;
        
        File fileUserDir = null;
        File fHist = null;
        File fConf = null;
        
        classFileChooser = new javax.swing.JFileChooser();
        classFileChooser.addChoosableFileFilter(new JavaFileFilter("class", "Class Files"));
        rootTreeNode = new DefaultMutableTreeNode("Class Files");
        initComponents();
        TabPaneGeneralProp = new gui.GeneralPropPane();
        jTabbedPane1.addTab("General", new javax.swing.ImageIcon(getClass().getResource("/res/general.gif")), TabPaneGeneralProp);
        TabPaneConstPool = new gui.ConstantPoolPropPane();
        jTabbedPane1.addTab("Constant Pool", new javax.swing.ImageIcon(getClass().getResource("/res/constpool.gif")), TabPaneConstPool);
        TabPaneFields = new gui.FieldsPropPane();
        jTabbedPane1.addTab("Fields", new javax.swing.ImageIcon(getClass().getResource("/res/field.gif")), TabPaneFields);
        TabPaneMethods = new gui.MethodsPropPane();
        jTabbedPane1.addTab("Methods", new javax.swing.ImageIcon(getClass().getResource("/res/method.gif")), TabPaneMethods);
        
        // check if config directory is present, create otherwise
        try {
            fileUserDir = new File(System.getProperty("user.home") + File.separatorChar + ".ce");
            fileUserDir.mkdir();
        } catch (Exception userDirCreateExcept) {
            System.err.println("Error creating user preferences directory - " + fileUserDir.getAbsolutePath());
            System.exit(0);
        }


        // open the configuration file
        try {
            Config = new java.util.Properties();
            fConf = new File(fileUserDir.getAbsolutePath() + File.separatorChar + ".ce_config");
            if(!fConf.exists()) {
                BufferedOutputStream configStream;
                fConf.createNewFile();
                Config.put("LocalSchemaLocation", System.getProperty("user.dir")+File.separatorChar+"CEJavaClass.xsd");
                configStream = new BufferedOutputStream(new FileOutputStream(fConf));
                Config.store(configStream, "Default properties for classeditor");
                configStream.close();
            }
            else {
                BufferedInputStream configStream;
                configStream = new BufferedInputStream(new FileInputStream(fConf));
                Config.load(configStream);
                configStream.close();
            }
        } catch (Exception historyFileCreateExcept) {
            System.err.println("Error reading configuration file - " + fConf.getAbsolutePath());
            System.exit(0);
        }
        
        // open the fileHistory
        try {
            fHist = new File(fileUserDir.getAbsolutePath() + File.separatorChar + ".ce_hostory");
            if(!fHist.exists()) fHist.createNewFile();
            else {
                LineNumberReader readerHist = new LineNumberReader(new FileReader(fHist));
                String sLine;
                while(null != (sLine = readerHist.readLine())) addToFileHistory(sLine, true);
                readerHist.close();
            }
        } catch (Exception historyFileCreateExcept) {
            System.err.println("Error creating file history - " + fHist.getAbsolutePath());
            System.exit(0);
        }
        
        setSize(800, 600);
        setModifyFlag();
    }
    
    private void clearTab(int iWhich) {
        TabPaneGeneralProp.setClassFile(classFile);
        TabPaneConstPool.setClassFile(classFile);
        TabPaneFields.setClassFile(classFile);
        TabPaneMethods.setClassFile(classFile);
        switch(iWhich) {
            case 0:
                TabPaneGeneralProp.clear();
                break;
            case 1:
                TabPaneConstPool.clear();
                break;
            case 2:
                TabPaneFields.clear();
                break;
            case 3:
                TabPaneMethods.clear();
                break;
            default:
                // raise exception??
        }
    }
    
    private void updateTab(int iWhich) {
        TabPaneGeneralProp.setClassFile(classFile);
        TabPaneConstPool.setClassFile(classFile);
        TabPaneFields.setClassFile(classFile);
        TabPaneMethods.setClassFile(classFile);
        switch(iWhich) {
            case 0:
                TabPaneGeneralProp.refresh();
                break;
            case 1:
                TabPaneConstPool.refresh();
                break;
            case 2:
                TabPaneFields.refresh();
                break;
            case 3:
                TabPaneMethods.refresh();
                break;
            default:
                // raise exception??
        }
    }
    
    private void clearClassData() {
        classFile = null;
        clearTab(0);
        clearTab(1);
        clearTab(2);
        clearTab(3);
    }
    
    private void switchClass(ClassFileStatus newClassStatus) {
        if(classStatus == newClassStatus) return;
        if(null != classStatus) {
            clearClassData();
            classStatus = null;
        }
        classStatus = newClassStatus;
        classFile = classStatus.classFile;
        jTabbedPane1.setSelectedIndex(0);
        updateTab(0);
        jTree1.setSelectionRow(rootTreeNode.getIndex(newClassStatus));
        setTitle("ClassEditor - " + newClassStatus.getTreeDisplayString());
    }
    
    private void unloadClass() {
        setTitle("ClassEditor");
        if(null != classStatus) {
            clearClassData();
            rootTreeNode.remove(classStatus);
            jTree1.setModel(new DefaultTreeModel(rootTreeNode));
            classStatus = null;
        }
        
        // check if any other file is open, switch to that
        if(0 != rootTreeNode.getChildCount()) {
            ClassFileStatus newClassStatus = (ClassFileStatus) rootTreeNode.getChildAt(0);
            switchClass(newClassStatus);
            return;
        }
        
        // else disable irrelevant menu and buttons
        setMenuAndButtonStatus();
    }
    
    private void setMenuAndButtonStatus() {
        boolean bEnabled = (null != classStatus);
        
        btnSaveClass.setEnabled(bEnabled);
        btnCloseClass.setEnabled(bEnabled);
        btnValidate.setEnabled(bEnabled);
        btnRelatedClass.setEnabled(bEnabled);
        btnShowSummary.setEnabled(bEnabled);
        MenuItemClose.setEnabled(bEnabled);
        MenuItemSave.setEnabled(bEnabled);
        MenuItemSaveAs.setEnabled(bEnabled);
        MenuItemExport.setEnabled(bEnabled);
        MenuItemExport.setEnabled(bEnabled);
        MenuViewSummary.setEnabled(bEnabled);
        MenuValidateChanges.setEnabled(bEnabled);
        MenuItemRelatedClasses.setEnabled(bEnabled);
    }
    
    private void writeClass(String sFileName) {
        DataOutputStream    dos;
        try {
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(sFileName))));
        }
        catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return;
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        
        // write the class to a new file
        try {
            classFile.write(dos);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            dos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    void chkNLoadClass(String sFileName) throws FileNotFoundException, IOException {
        int iIndex;
        ClassFileStatus newClassStatus = null;
        ClassFileStatus sTempStatus = new ClassFileStatus(sFileName, null);
        
        // check if it is already loaded
        for(iIndex=rootTreeNode.getChildCount(); iIndex > 0; iIndex--) {
            ClassFileStatus thisStatus = (ClassFileStatus)rootTreeNode.getChildAt(iIndex-1);
            if( sTempStatus.sClassName.equals(thisStatus.sClassName) && sTempStatus.sPath.equals(thisStatus.sPath) ) {
                // found
                newClassStatus = thisStatus;
                // check if it is the current class, if not set.
                classFile = newClassStatus.classFile;
                break;
            }
        }
        
        if(null == newClassStatus) {
            // read class
            statusBar1.setText("Reading Class " + sFileName);
            try {
                readClass(sFileName);
                statusBar1.setText("Read class: " + sFileName);
            }
            catch(FileNotFoundException fnfe) {
                statusBar1.setText("File not found: " + sFileName);
                return;
            }
            catch(IOException ioe) {
                statusBar1.setText("Error reading file: " + sFileName);
                return;
            }
            
            newClassStatus = new ClassFileStatus(sFileName, classFile);
            rootTreeNode.add(newClassStatus);
            jTree1.setModel(new DefaultTreeModel(rootTreeNode));
            statusBar1.setText("Loaded class: " + sFileName);
        }
        
        switchClass(newClassStatus);
        setMenuAndButtonStatus(); // enable menu and buttons
        addToFileHistory(sFileName, false); // add to file history
    }

    private void addToFileHistory (String sFileName, boolean bAddToEnd) {
        int iIndex;
        
        for(iIndex=0; iIndex < asHistoryFileNames.size(); iIndex++) {            
            if(sFileName.equals(asHistoryFileNames.get(iIndex))) {
                MenuFile.remove((JMenuItem)(aHistoryFiles.get(iIndex)));
                aHistoryFiles.remove(iIndex);
                asHistoryFileNames.remove(iIndex);
                break;
            }
        }
        
        if(aHistoryFiles.size() >= NUM_FILES_IN_HISTORY) {
            int iToRem = bAddToEnd ? 0 : (NUM_FILES_IN_HISTORY-1);
            MenuFile.remove((JMenuItem)(aHistoryFiles.get(iToRem)));
            asHistoryFileNames.remove(iToRem);
            aHistoryFiles.remove(iToRem);
        }

        JMenuItem newMenuItem = new javax.swing.JMenuItem();
        newMenuItem.setText(sFileName);
        newMenuItem.setActionCommand(sFileName);
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemFileHistoryActionPerformed(evt);
            }
        });
        
        if(bAddToEnd) {
            asHistoryFileNames.add(sFileName);
            aHistoryFiles.add(newMenuItem);
            MenuFile.add(newMenuItem);
        }
        else {
            asHistoryFileNames.add(0, sFileName);
            aHistoryFiles.add(0, newMenuItem);
            MenuFile.add(newMenuItem, 10);
        }
    }
    
    private void readClass(String sFileName) throws FileNotFoundException, IOException {
        DataInputStream     dis;
        classFile = new ClassFile();
        dis = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(sFileName))));
        // read the class
        classFile.read(dis);
        dis.close();
    }
    
    /** Open new file */
    private void createNewClass() {
        String sFileName;
        int returnVal = classFileChooser.showSaveDialog(this);
        
        if(returnVal != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        sFileName = classFileChooser.getSelectedFile().getAbsolutePath();
        ClassFile newClassFile = new ClassFile();
        
        try {
            newClassFile.createSimplestClass();
            // extract the class name from file name
            File fileTemp = new File(sFileName);
            String sName = fileTemp.getName();
            sName = sName.substring(0, sName.indexOf('.'));
            newClassFile.classNames.setThisClassName(sName);
        }
        catch(IOException ioe) {
            statusBar1.setText("Internal error while creating class");
            return;
        }
        ClassFileStatus	newClassStatus = new ClassFileStatus(sFileName, newClassFile);
        rootTreeNode.add(newClassStatus);
        jTree1.setModel(new DefaultTreeModel(rootTreeNode));
        statusBar1.setText("Loaded class: " + sFileName);
        switchClass(newClassStatus);
    }
    
    private void openNewFile() {
        String sNewFile;
        int returnVal = classFileChooser.showOpenDialog(this);
        
        if(returnVal != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        sNewFile = classFileChooser.getSelectedFile().getAbsolutePath();
        
        statusBar1.setText("Loading Class " + sNewFile);
        try {
            chkNLoadClass(sNewFile);
        }
        catch(FileNotFoundException fnfe) {
            statusBar1.setText("File not found: " + sNewFile);
        }
        catch(IOException ioe) {
            statusBar1.setText("Error reading file: " + sNewFile);
        }
    }
    
    private void closeClass() {
        if(null != classStatus) {
            statusBar1.setText("Unloading Class " + classStatus.sFileName);
            unloadClass();
        }
        statusBar1.setText("Ready");
    }
    
    private void saveClass() {
        if (null != classStatus) {
            statusBar1.setText("Saving to class " + classStatus.sFileName);
            writeClass(classStatus.sFileName);
            statusBar1.setText("Wrote class " + classStatus.sFileName);
        }
    }
    
    private void showValidation(boolean bValid, Vector vErrVect) {
        if (!bValid) {
            StringBuffer summaryBuffer = new StringBuffer(256);
            for (int iIndex=0; iIndex < vErrVect.size(); iIndex++) {
                summaryBuffer.append((String)vErrVect.elementAt(iIndex)).append(Utils.sNewLine);
            }
            SummaryDialog summDial = new SummaryDialog(this, true);
            summDial.setClassFileSummary(summaryBuffer.toString());
            summDial.show();
        }
        else {
            JOptionPane.showMessageDialog(this, "No errors detected.", "Validation Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showCompleteSummary() {
        if (null != classFile) {
            TextSummaryVisitor txtVisitor = new TextSummaryVisitor();
            txtVisitor.visitClass(classFile);
            SummaryDialog summDial = new SummaryDialog(this, true);
            summDial.setClassFileSummary(txtVisitor.getSummary().toString());
            summDial.show();
        }
    }
    
    private void showMethodsSummary(boolean bShowCode) {
        if (null != classFile) {
            TextSummaryVisitor txtVisitor = new TextSummaryVisitor(!bShowCode);
            txtVisitor.visitMethods(classFile.methods);
            SummaryDialog summDial = new SummaryDialog(this, true);
            summDial.setClassFileSummary(txtVisitor.getSummary().toString());
            summDial.show();
        }
    }
    
    private void showRelatedClasses() {
        if (null != classFile) {
            RelatedClasses relClasses = new RelatedClasses(this, true);
            relClasses.showRelatedClasses(this, classFile.constantPool);
            relClasses.show();
        }
    }
    
    private void setModifyFlag() {
        TabPaneGeneralProp.setModifyMode(bEditable);
        TabPaneConstPool.setModifyMode(bEditable);
        TabPaneFields.setModifyMode(bEditable);
        TabPaneMethods.setModifyMode(bEditable);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jToolBar1 = new javax.swing.JToolBar();
        btnOpenClass = new javax.swing.JButton();
        btnNewFile = new javax.swing.JButton();
        btnSaveClass = new javax.swing.JButton();
        btnCloseClass = new javax.swing.JButton();
        btnValidate = new javax.swing.JButton();
        btnRelatedClass = new javax.swing.JButton();
        btnShowSummary = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        btnModifyMode = new javax.swing.JToggleButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jTree1 = new javax.swing.JTree(rootTreeNode);
        ((DefaultTreeCellRenderer)jTree1.getCellRenderer()).setLeafIcon(new javax.swing.ImageIcon(getClass().getResource("/res/class.gif")));
        jTabbedPane1 = new javax.swing.JTabbedPane();
        statusBar1 = new javax.swing.JLabel();
        jMenuBar2 = new javax.swing.JMenuBar();
        MenuFile = new javax.swing.JMenu();
        MenuItemNew = new javax.swing.JMenuItem();
        MenuItemOpen = new javax.swing.JMenuItem();
        MenuItemClose = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        MenuItemSave = new javax.swing.JMenuItem();
        MenuItemSaveAs = new javax.swing.JMenuItem();
        MenuItemExport = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        MenuItemExit = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        MenuView = new javax.swing.JMenu();
        MenuViewSummary = new javax.swing.JMenu();
        MenuItemCompleteSummary = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        MenuItemGeneral = new javax.swing.JMenuItem();
        MenuItemConstantPool = new javax.swing.JMenuItem();
        MenuItemFields = new javax.swing.JMenuItem();
        MenuItemMethods = new javax.swing.JMenuItem();
        MenuItemMethodNoCode = new javax.swing.JMenuItem();
        MenuValidateChanges = new javax.swing.JMenu();
        MenuItemValidateComplete = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        MenuItemValidateGeneral = new javax.swing.JMenuItem();
        MenuItemValidateConstPool = new javax.swing.JMenuItem();
        MenuItemValidateFields = new javax.swing.JMenuItem();
        MenuItemValidateMethods = new javax.swing.JMenuItem();
        MenuItemRelatedClasses = new javax.swing.JMenuItem();
        MenuHelp = new javax.swing.JMenu();
        MenuItemAbout = new javax.swing.JMenuItem();

        setTitle("ClassEditor");
        setName("MainFrame");
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/res/classeditor.gif")).getImage());
        setForeground(java.awt.Color.white);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jToolBar1.setFloatable(false);
        btnOpenClass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/open1.gif")));
        btnOpenClass.setText("Open");
        btnOpenClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toolBarOpenAction(evt);
            }
        });

        jToolBar1.add(btnOpenClass);

        btnNewFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/newfile1.gif")));
        btnNewFile.setText("New");
        btnNewFile.setName("");
        btnNewFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toolBarNewFileAction(evt);
            }
        });

        jToolBar1.add(btnNewFile);

        btnSaveClass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/save1.gif")));
        btnSaveClass.setText("Save");
        btnSaveClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toolBarSaveAction(evt);
            }
        });

        jToolBar1.add(btnSaveClass);

        btnCloseClass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/close1.gif")));
        btnCloseClass.setText("Close");
        btnCloseClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toolBarCloseAction(evt);
            }
        });

        jToolBar1.add(btnCloseClass);

        btnValidate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/verify3.gif")));
        btnValidate.setText("Validate");
        btnValidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnValidateActionPerformed(evt);
            }
        });

        jToolBar1.add(btnValidate);

        btnRelatedClass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/related1.gif")));
        btnRelatedClass.setText("Related");
        btnRelatedClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRelatedClassActionPerformed(evt);
            }
        });

        jToolBar1.add(btnRelatedClass);

        btnShowSummary.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/report1.gif")));
        btnShowSummary.setText("Summary");
        btnShowSummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowSummaryActionPerformed(evt);
            }
        });

        jToolBar1.add(btnShowSummary);

        jToolBar1.add(jSeparator5);

        btnModifyMode.setBackground(new java.awt.Color(153, 255, 153));
        btnModifyMode.setText("Modify Mode (Off)");
        btnModifyMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModifyModeActionPerformed(evt);
            }
        });

        jToolBar1.add(btnModifyMode);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

        jSplitPane1.setDividerLocation(100);
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setAutoscrolls(true);
        jTree1.setRootVisible(false);
        jTree1.setMinimumSize(new java.awt.Dimension(50, 400));
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });

        jSplitPane1.setLeftComponent(jTree1);

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(400, 400));
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jSplitPane1.setRightComponent(jTabbedPane1);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        statusBar1.setText(" ");
        statusBar1.setBorder(new javax.swing.border.EtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        getContentPane().add(statusBar1, java.awt.BorderLayout.SOUTH);

        MenuFile.setMnemonic('F');
        MenuFile.setText("File");
        MenuItemNew.setMnemonic('N');
        MenuItemNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        MenuItemNew.setText("New");
        MenuItemNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemNewActionPerformed(evt);
            }
        });

        MenuFile.add(MenuItemNew);

        MenuItemOpen.setMnemonic('O');
        MenuItemOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        MenuItemOpen.setText("Open");
        MenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemOpenActionPerformed(evt);
            }
        });

        MenuFile.add(MenuItemOpen);

        MenuItemClose.setText("Close");
        MenuItemClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemCloseActionPerformed(evt);
            }
        });

        MenuFile.add(MenuItemClose);

        MenuFile.add(jSeparator1);

        MenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        MenuItemSave.setMnemonic('S');
        MenuItemSave.setText("Save");
        MenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemSaveActionPerformed(evt);
            }
        });

        MenuFile.add(MenuItemSave);

        MenuItemSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        MenuItemSaveAs.setMnemonic('A');
        MenuItemSaveAs.setText("Save As");
        MenuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemSaveAsActionPerformed(evt);
            }
        });

        MenuFile.add(MenuItemSaveAs);

        MenuItemExport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        MenuItemExport.setMnemonic('X');
        MenuItemExport.setText("Export to XML");
        MenuItemExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemExportActionPerformed(evt);
            }
        });

        MenuFile.add(MenuItemExport);

        MenuFile.add(jSeparator2);

        MenuItemExit.setMnemonic('x');
        MenuItemExit.setText("Exit");
        MenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemExitActionPerformed(evt);
            }
        });

        MenuFile.add(MenuItemExit);

        MenuFile.add(jSeparator6);

        jMenuBar2.add(MenuFile);

        MenuView.setMnemonic('V');
        MenuView.setText("View");
        MenuViewSummary.setText("Summary");
        MenuItemCompleteSummary.setText("Complete");
        MenuItemCompleteSummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemCompleteSummaryActionPerformed(evt);
            }
        });

        MenuViewSummary.add(MenuItemCompleteSummary);

        MenuViewSummary.add(jSeparator3);

        MenuItemGeneral.setText("General");
        MenuItemGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemGeneralActionPerformed(evt);
            }
        });

        MenuViewSummary.add(MenuItemGeneral);

        MenuItemConstantPool.setText("Constant Pool");
        MenuItemConstantPool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemConstantPoolActionPerformed(evt);
            }
        });

        MenuViewSummary.add(MenuItemConstantPool);

        MenuItemFields.setText("Fields");
        MenuItemFields.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemFieldsActionPerformed(evt);
            }
        });

        MenuViewSummary.add(MenuItemFields);

        MenuItemMethods.setText("Methods");
        MenuItemMethods.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemMethodsActionPerformed(evt);
            }
        });

        MenuViewSummary.add(MenuItemMethods);

        MenuItemMethodNoCode.setText("Methods without code");
        MenuItemMethodNoCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemMethodNoCodeActionPerformed(evt);
            }
        });

        MenuViewSummary.add(MenuItemMethodNoCode);

        MenuView.add(MenuViewSummary);

        MenuValidateChanges.setText("Validate Changes");
        MenuItemValidateComplete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        MenuItemValidateComplete.setText("Complete");
        MenuItemValidateComplete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemValidateCompleteActionPerformed(evt);
            }
        });

        MenuValidateChanges.add(MenuItemValidateComplete);

        MenuValidateChanges.add(jSeparator4);

        MenuItemValidateGeneral.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        MenuItemValidateGeneral.setText("General");
        MenuItemValidateGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemValidateGeneralActionPerformed(evt);
            }
        });

        MenuValidateChanges.add(MenuItemValidateGeneral);

        MenuItemValidateConstPool.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        MenuItemValidateConstPool.setText("Constant Pool");
        MenuItemValidateConstPool.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemValidateConstPoolActionPerformed(evt);
            }
        });

        MenuValidateChanges.add(MenuItemValidateConstPool);

        MenuItemValidateFields.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        MenuItemValidateFields.setText("Fields");
        MenuItemValidateFields.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemValidateFieldsActionPerformed(evt);
            }
        });

        MenuValidateChanges.add(MenuItemValidateFields);

        MenuItemValidateMethods.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        MenuItemValidateMethods.setText("Methods");
        MenuItemValidateMethods.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemValidateMethodsActionPerformed(evt);
            }
        });

        MenuValidateChanges.add(MenuItemValidateMethods);

        MenuView.add(MenuValidateChanges);

        MenuItemRelatedClasses.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        MenuItemRelatedClasses.setText("Related Classes");
        MenuItemRelatedClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemRelatedClassesActionPerformed(evt);
            }
        });

        MenuView.add(MenuItemRelatedClasses);

        jMenuBar2.add(MenuView);

        MenuHelp.setMnemonic('H');
        MenuHelp.setText("Help");
        MenuItemAbout.setText("About...");
        MenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemAboutActionPerformed(evt);
            }
        });

        MenuHelp.add(MenuItemAbout);

        jMenuBar2.add(MenuHelp);

        setJMenuBar(jMenuBar2);

        pack();
    }//GEN-END:initComponents

    private void MenuItemExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemExportActionPerformed
        if (null != classStatus) {
            String sFileName;                
            javax.swing.JFileChooser newFileChooser = new javax.swing.JFileChooser();
            newFileChooser.addChoosableFileFilter(new JavaFileFilter("xml", "XML Files"));
            int returnVal = newFileChooser.showSaveDialog(this);
            
            if(returnVal != javax.swing.JFileChooser.APPROVE_OPTION) {
                return;
            }
            
            sFileName = newFileChooser.getSelectedFile().getAbsolutePath();
            statusBar1.setText("Saving to XML file " + sFileName);
            
            try
            {
                FileOutputStream fos = new FileOutputStream(sFileName, false);
                XMLOutputVisitor xmlVisitor = new XMLOutputVisitor();
                if(null != Config.get("LocalSchemaLocation")) xmlVisitor.setLocalSchema((String)Config.get("LocalSchemaLocation"));
                xmlVisitor.visitClass(classFile);
                xmlVisitor.getAsString(fos);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            statusBar1.setText("Wrote XML file " + sFileName);
        }
    }//GEN-LAST:event_MenuItemExportActionPerformed

    private void btnModifyModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModifyModeActionPerformed
        bEditable = btnModifyMode.isSelected();
        btnModifyMode.setText("Modify Mode (" + (bEditable ? "On" : "Off") + ")");
        btnModifyMode.setBackground(bEditable ? java.awt.Color.pink : (new java.awt.Color(153, 255, 153)));
        setModifyFlag();
    }//GEN-LAST:event_btnModifyModeActionPerformed
    
    private void btnRelatedClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRelatedClassActionPerformed
        showRelatedClasses();
    }//GEN-LAST:event_btnRelatedClassActionPerformed
    
    private void MenuItemRelatedClassesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemRelatedClassesActionPerformed
        showRelatedClasses();
    }//GEN-LAST:event_MenuItemRelatedClassesActionPerformed
    
    private void MenuItemMethodNoCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemMethodNoCodeActionPerformed
        showMethodsSummary(false);
    }//GEN-LAST:event_MenuItemMethodNoCodeActionPerformed
    
    private void MenuItemMethodsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemMethodsActionPerformed
        showMethodsSummary(true);
    }//GEN-LAST:event_MenuItemMethodsActionPerformed
    
    private void MenuItemFieldsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemFieldsActionPerformed
        if (null != classFile) {
            TextSummaryVisitor txtVisitor = new TextSummaryVisitor();
            txtVisitor.visitFields(classFile.fields);
            SummaryDialog summDial = new SummaryDialog(this, true);
            summDial.setClassFileSummary(txtVisitor.getSummary().toString());
            summDial.show();
        }
    }//GEN-LAST:event_MenuItemFieldsActionPerformed
    
    private void MenuItemConstantPoolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemConstantPoolActionPerformed
        if (null != classFile) {
            TextSummaryVisitor txtVisitor = new TextSummaryVisitor();
            txtVisitor.visitConstantPool(classFile.constantPool);
            SummaryDialog summDial = new SummaryDialog(this, true);
            summDial.setClassFileSummary(txtVisitor.getSummary().toString());
            summDial.show();
        }
    }//GEN-LAST:event_MenuItemConstantPoolActionPerformed
    
    private void btnShowSummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowSummaryActionPerformed
        showCompleteSummary();
    }//GEN-LAST:event_btnShowSummaryActionPerformed
    
    private void MenuItemValidateMethodsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemValidateMethodsActionPerformed
        if (null != classFile) {
            Vector vErrVect = new Vector();
            boolean bValid = classFile.methods.verify(vErrVect);
            showValidation(bValid, vErrVect);
        }
    }//GEN-LAST:event_MenuItemValidateMethodsActionPerformed
    
    private void MenuItemValidateFieldsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemValidateFieldsActionPerformed
        if (null != classFile) {
            Vector vErrVect = new Vector();
            boolean bValid = classFile.fields.verify(vErrVect);
            showValidation(bValid, vErrVect);
        }
    }//GEN-LAST:event_MenuItemValidateFieldsActionPerformed
    
    private void MenuItemValidateConstPoolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemValidateConstPoolActionPerformed
        if (null != classFile) {
            Vector vErrVect = new Vector();
            boolean bValid = classFile.constantPool.verify(vErrVect);
            showValidation(bValid, vErrVect);
        }
    }//GEN-LAST:event_MenuItemValidateConstPoolActionPerformed
    
    private void MenuItemValidateGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemValidateGeneralActionPerformed
        if (null != classFile) {
            Vector vErrVect = new Vector();
            boolean bValid = classFile.version.verify(vErrVect);
            bValid = (bValid && classFile.accessFlags.verify("ClassFile", vErrVect, true));
            bValid = (bValid && classFile.classNames.verify(vErrVect));
            bValid = (bValid && classFile.interfaces.verify(vErrVect));
            bValid = (bValid && classFile.attributes.verify("ClassFile", vErrVect));
            showValidation(bValid, vErrVect);
            gui.attributes.AttributesDialog adTest = new gui.attributes.AttributesDialog(this, false);
            adTest.setInput(classFile.attributes, classFile.constantPool, true);
            adTest.show();
        }
    }//GEN-LAST:event_MenuItemValidateGeneralActionPerformed

    private void MenuItemFileHistoryActionPerformed(java.awt.event.ActionEvent evt) {
        String sFile = evt.getActionCommand();
        try {
            chkNLoadClass(sFile);
        } 
        catch(FileNotFoundException fnfe) {
            statusBar1.setText("File not found: " + sFile);
        }
        catch(IOException ioe) {
            statusBar1.setText("Error reading file: " + sFile);
        }
    }
    
    private void MenuItemValidateCompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemValidateCompleteActionPerformed
        btnValidateActionPerformed(evt);
    }//GEN-LAST:event_MenuItemValidateCompleteActionPerformed
    
    private void MenuItemGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemGeneralActionPerformed
        if (null != classFile) {
            TextSummaryVisitor txtVisitor = new TextSummaryVisitor();
            txtVisitor.visitVersion(classFile.version);
            txtVisitor.visitAccessFlags(classFile.accessFlags);
            txtVisitor.visitClassNames(classFile.classNames);
            txtVisitor.visitAttributes(classFile.attributes);
            txtVisitor.visitInterfaces(classFile.interfaces);
            
            SummaryDialog summDial = new SummaryDialog(this, true);
            summDial.setClassFileSummary(txtVisitor.getSummary().toString());
            summDial.show();
        }
    }//GEN-LAST:event_MenuItemGeneralActionPerformed
    
    private void MenuItemCompleteSummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemCompleteSummaryActionPerformed
        showCompleteSummary();
    }//GEN-LAST:event_MenuItemCompleteSummaryActionPerformed
    
    private void btnValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidateActionPerformed
        if (null != classFile) {
            Vector vErrVect = new Vector();
            boolean bValid = classFile.verify(vErrVect);
            showValidation(bValid, vErrVect);
        }
    }//GEN-LAST:event_btnValidateActionPerformed
    
    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if(null == classFile) return;
        updateTab(jTabbedPane1.getSelectedIndex());
    }//GEN-LAST:event_jTabbedPane1StateChanged
    
    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)jTree1.getLastSelectedPathComponent();
        
        if (node == null) return;
        
        ClassFileStatus newClassStatus = (ClassFileStatus)node;
        switchClass(newClassStatus);
    }//GEN-LAST:event_jTree1ValueChanged
    
    private void MenuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemSaveAsActionPerformed
        if (null != classStatus) {
            String sFileName;
            int returnVal = classFileChooser.showSaveDialog(this);
            
            if(returnVal != javax.swing.JFileChooser.APPROVE_OPTION) {
                return;
            }
            
            sFileName = classFileChooser.getSelectedFile().getAbsolutePath();
            statusBar1.setText("Saving to class " + sFileName);
            writeClass(sFileName);
            statusBar1.setText("Wrote class " + sFileName);
        }
    }//GEN-LAST:event_MenuItemSaveAsActionPerformed
    
    private void MenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemSaveActionPerformed
        saveClass();
    }//GEN-LAST:event_MenuItemSaveActionPerformed
    
    private void toolBarSaveAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolBarSaveAction
        saveClass();
    }//GEN-LAST:event_toolBarSaveAction
    
    private void MenuItemCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemCloseActionPerformed
        closeClass();
    }//GEN-LAST:event_MenuItemCloseActionPerformed
    
    private void MenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemOpenActionPerformed
        openNewFile();
    }//GEN-LAST:event_MenuItemOpenActionPerformed
    
    private void toolBarOpenAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolBarOpenAction
        openNewFile();
    }//GEN-LAST:event_toolBarOpenAction
    
    private void toolBarCloseAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolBarCloseAction
        closeClass();
    }//GEN-LAST:event_toolBarCloseAction
    
    private void toolBarNewFileAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolBarNewFileAction
        createNewClass();
    }//GEN-LAST:event_toolBarNewFileAction
    
    private void MenuItemNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemNewActionPerformed
        createNewClass();
    }//GEN-LAST:event_MenuItemNewActionPerformed
    
    private void MenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemExitActionPerformed
        saveHistAndExit();
    }//GEN-LAST:event_MenuItemExitActionPerformed
    
    private void MenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemAboutActionPerformed
        AboutDialog dialog = new AboutDialog(this, false);
        dialog.show();
    }//GEN-LAST:event_MenuItemAboutActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        saveHistAndExit();
    }//GEN-LAST:event_exitForm
    
    private void saveHistAndExit() {
        try {
            File fHist = new File(System.getProperty("user.home") + File.separatorChar + ".ce" + File.separatorChar + ".ce_hostory");
            FileWriter fw = new FileWriter(fHist);
            String sSep = System.getProperty("line.separator");

            for(int iIndex=0; iIndex < asHistoryFileNames.size(); iIndex++) {
                fw.write(asHistoryFileNames.get(iIndex) + sSep);
            }
            fw.close();
        }
        catch (Exception e) {
            System.err.println("Could not save file history - " + e.toString());
        }
        System.exit(0);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu MenuFile;
    private javax.swing.JMenu MenuHelp;
    private javax.swing.JMenuItem MenuItemAbout;
    private javax.swing.JMenuItem MenuItemClose;
    private javax.swing.JMenuItem MenuItemCompleteSummary;
    private javax.swing.JMenuItem MenuItemConstantPool;
    private javax.swing.JMenuItem MenuItemExit;
    private javax.swing.JMenuItem MenuItemExport;
    private javax.swing.JMenuItem MenuItemFields;
    private javax.swing.JMenuItem MenuItemGeneral;
    private javax.swing.JMenuItem MenuItemMethodNoCode;
    private javax.swing.JMenuItem MenuItemMethods;
    private javax.swing.JMenuItem MenuItemNew;
    private javax.swing.JMenuItem MenuItemOpen;
    private javax.swing.JMenuItem MenuItemRelatedClasses;
    private javax.swing.JMenuItem MenuItemSave;
    private javax.swing.JMenuItem MenuItemSaveAs;
    private javax.swing.JMenuItem MenuItemValidateComplete;
    private javax.swing.JMenuItem MenuItemValidateConstPool;
    private javax.swing.JMenuItem MenuItemValidateFields;
    private javax.swing.JMenuItem MenuItemValidateGeneral;
    private javax.swing.JMenuItem MenuItemValidateMethods;
    private javax.swing.JMenu MenuValidateChanges;
    private javax.swing.JMenu MenuView;
    private javax.swing.JMenu MenuViewSummary;
    private javax.swing.JButton btnCloseClass;
    private javax.swing.JToggleButton btnModifyMode;
    private javax.swing.JButton btnNewFile;
    private javax.swing.JButton btnOpenClass;
    private javax.swing.JButton btnRelatedClass;
    private javax.swing.JButton btnSaveClass;
    private javax.swing.JButton btnShowSummary;
    private javax.swing.JButton btnValidate;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel statusBar1;
    // End of variables declaration//GEN-END:variables
    
}
