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

import classfile.ClassFile;
import classfile.Utils;
import gui.attributes.AttributesDialog;
import guihelper.ClassFileStatus;
import guihelper.JavaFileFilter;
import visitors.TextSummaryVisitor;
import visitors.XMLOutputVisitor;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

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
 * <br><br>
 *
 * @author Tanmay K. Mohapatra
 * @version 2.04, 21st Mar, 2004
 */

public class ClassEditor extends JFrame {
    // 顶部，菜单栏
    private JMenuBar topMenuBar = new JMenuBar();
    private JMenu menuFile = new JMenu("File");
    private JMenu menuView = new JMenu("View");
    private JMenu menuHelp = new JMenu("Help");

    // 顶部，菜单栏--> File
    private JMenuItem menuFile_New = new JMenuItem("New");
    private JMenuItem menuFile_Open = new JMenuItem("Open");
    private JMenuItem menuFile_Close = new JMenuItem("Close");
    private JMenuItem menuFile_Save = new JMenuItem("Save");
    private JMenuItem menuFile_SaveAs = new JMenuItem("Save As");
    private JMenuItem menuFile_Export = new JMenuItem("Export to XML");
    private JMenuItem menuFile_Exit = new JMenuItem("Exit");

    // 顶部，菜单栏--> View
    private JMenu menuView_Summary = new JMenu("Summary");
    private JMenu menuView_ValidateChanges = new JMenu("Validate Changes");
    private JMenuItem menuView_RelatedClasses = new JMenuItem("Related Classes");

    // 顶部，菜单栏--> Help
    private JMenuItem menuHelp_About = new JMenuItem("About...");

    // 上侧，工具条
    private JToolBar toolBar = new JToolBar();
    private JButton btnOpenClass = new JButton();
    private JButton btnNewFile = new JButton();
    private JButton btnSaveClass = new JButton();
    private JButton btnCloseClass = new JButton();
    private JButton btnValidate = new JButton();
    private JButton btnRelatedClass = new JButton();
    private JButton btnShowSummary = new JButton();
    private JToggleButton btnModifyMode = new JToggleButton();

    // 布局
    private JSplitPane splitPane = new JSplitPane();

    // 左侧，树
    private DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode("Class Files");
    private JTree tree = new JTree(rootTreeNode);

    // 右侧，选项卡
    private JTabbedPane tabbedPane = new JTabbedPane();
    private GeneralPropPane tabPaneGeneralProp = new GeneralPropPane();
    private ConstantPoolPropPane tabPaneConstPool = new ConstantPoolPropPane();
    private FieldsPropPane tabPaneFields = new FieldsPropPane();
    private MethodsPropPane tabPaneMethods = new MethodsPropPane();

    // 底部状态栏
    private JLabel statusBarLabel = new JLabel();

    // 其他
    private JFileChooser classFileChooser = new JFileChooser();
    private ClassFileStatus classStatus;

    // 数据部分
    private ClassFile classFile;
    private boolean bEditable;
    private Properties Config; // configuration file


    // variables used for maintaining file history
    private static int NUM_FILES_IN_HISTORY = 5;
    private List<String> history_file_list = new ArrayList<>();
    private List<JMenuItem> history_file_menu_item_list = new ArrayList<>();




    /**
     * Creates new form gui
     */
    public ClassEditor() {
        initComponents();
        initConfig();
    }

    private void initConfig() {

        File fileUserDir = null;
        File fHist = null;
        File fConf = null;

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
            if (!fConf.exists()) {
                BufferedOutputStream configStream;
                fConf.createNewFile();
                Config.put("LocalSchemaLocation", System.getProperty("user.dir") + File.separatorChar + "CEJavaClass.xsd");
                configStream = new BufferedOutputStream(new FileOutputStream(fConf));
                Config.store(configStream, "Default properties for classeditor");
                configStream.close();
            } else {
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
            fHist = new File(fileUserDir.getAbsolutePath() + File.separatorChar + ".ce_history");
            if (!fHist.exists()) fHist.createNewFile();
            else {
                LineNumberReader readerHist = new LineNumberReader(new FileReader(fHist));
                String sLine;
                while (null != (sLine = readerHist.readLine())) addToFileHistory(sLine, true);
                readerHist.close();
            }
        } catch (Exception historyFileCreateExcept) {
            System.err.println("Error creating file history - " + fHist.getAbsolutePath());
            System.exit(0);
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {

        setTitle("ClassEditor");
        setName("MainFrame");
        setSize(800, 600);
        setIconImage(new ImageIcon(getClass().getResource("/res/classeditor.gif")).getImage());
        setForeground(Color.white);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                exitForm(evt);
            }
        });

        classFileChooser.addChoosableFileFilter(new JavaFileFilter("class", "Class Files"));

        // init menu bar
        init_menu_bar();

        // init toolbar
        init_tool_bar();

        // init layout
        init_layout();
        init_left();
        init_right();

        // init status bar
        init_status_bar();

        pack();
    }

    private void init_menu_bar() {
        setJMenuBar(topMenuBar);

        init_menu_file();
        init_menu_view();
        init_menu_help();
    }

    private void init_menu_file() {
        menuFile.setMnemonic('F');
        topMenuBar.add(menuFile);


        menuFile_New.setMnemonic('N');
        menuFile_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        menuFile_New.addActionListener(evt -> menuItemNew_ActionPerformed(evt));
        menuFile.add(menuFile_New);


        menuFile_Open.setMnemonic('O');
        menuFile_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        menuFile_Open.addActionListener(evt -> menuItemOpen_ActionPerformed(evt));
        menuFile.add(menuFile_Open);


        menuFile_Close.addActionListener(evt -> menuItemClose_ActionPerformed(evt));
        menuFile.add(menuFile_Close);


        menuFile.add(new JSeparator());


        menuFile_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        menuFile_Save.setMnemonic('S');
        menuFile_Save.addActionListener(evt -> menuItemSave_ActionPerformed(evt));
        menuFile.add(menuFile_Save);


        menuFile_SaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        menuFile_SaveAs.setMnemonic('A');
        menuFile_SaveAs.addActionListener(evt -> menuItemSaveAs_ActionPerformed(evt));
        menuFile.add(menuFile_SaveAs);


        menuFile_Export.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        menuFile_Export.setMnemonic('X');
        menuFile_Export.addActionListener(evt -> menuItemExport_ActionPerformed(evt));
        menuFile.add(menuFile_Export);


        menuFile.add(new JSeparator());


        menuFile_Exit.setMnemonic('x');
        menuFile_Exit.addActionListener((evt) -> menuItemExit_ActionPerformed(evt));
        menuFile.add(menuFile_Exit);

        menuFile.add(new JSeparator());
    }

    // region menu file item action
    private void menuItemNew_ActionPerformed(ActionEvent evt) {
        createNewClass();
    }

    private void menuItemOpen_ActionPerformed(ActionEvent evt) {
        openNewFile();
    }

    private void menuItemClose_ActionPerformed(ActionEvent evt) {
        closeClass();
    }

    private void menuItemSave_ActionPerformed(ActionEvent evt) {
        saveClass();
    }

    private void menuItemSaveAs_ActionPerformed(ActionEvent evt) {
        if (null != classStatus) {
            String sFileName;
            int returnVal = classFileChooser.showSaveDialog(this);

            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }

            sFileName = classFileChooser.getSelectedFile().getAbsolutePath();
            statusBarLabel.setText("Saving to class " + sFileName);
            writeClass(sFileName);
            statusBarLabel.setText("Wrote class " + sFileName);
        }
    }

    private void menuItemExport_ActionPerformed(ActionEvent evt) {
        if (null != classStatus) {
            String sFileName;
            JFileChooser newFileChooser = new JFileChooser();
            newFileChooser.addChoosableFileFilter(new JavaFileFilter("xml", "XML Files"));
            int returnVal = newFileChooser.showSaveDialog(this);

            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }

            sFileName = newFileChooser.getSelectedFile().getAbsolutePath();
            statusBarLabel.setText("Saving to XML file " + sFileName);

            try {
                FileOutputStream fos = new FileOutputStream(sFileName, false);
                XMLOutputVisitor xmlVisitor = new XMLOutputVisitor();
                if (null != Config.get("LocalSchemaLocation"))
                    xmlVisitor.setLocalSchema((String) Config.get("LocalSchemaLocation"));
                xmlVisitor.visitClass(classFile);
                xmlVisitor.getAsString(fos);
            } catch (Exception e) {
                e.printStackTrace();
            }

            statusBarLabel.setText("Wrote XML file " + sFileName);
        }
    }

    private void menuItemExit_ActionPerformed(ActionEvent evt) {
        saveHistAndExit();
    }
    // endregion

    private void init_menu_view() {
        menuView.setMnemonic('V');
        topMenuBar.add(menuView);

        menuView.add(menuView_Summary);
        menuView.add(menuView_ValidateChanges);
        menuView.add(menuView_RelatedClasses);

        // region Menu --> View --> Summary
        JMenuItem summary_01_complete = new JMenuItem("Complete");
        summary_01_complete.addActionListener(evt -> menuItemCompleteSummaryActionPerformed(evt));
        menuView_Summary.add(summary_01_complete);


        menuView_Summary.add(new JSeparator());


        JMenuItem summary_02_general = new JMenuItem("General");
        summary_02_general.addActionListener(evt -> menuItemGeneralActionPerformed(evt));
        menuView_Summary.add(summary_02_general);


        JMenuItem summary_03_cp = new JMenuItem("Constant Pool");
        summary_03_cp.addActionListener(evt -> menuItemConstantPoolActionPerformed(evt));
        menuView_Summary.add(summary_03_cp);


        JMenuItem summary_04_fields = new JMenuItem("Fields");
        summary_04_fields.addActionListener(evt -> menuItemFieldsActionPerformed(evt));
        menuView_Summary.add(summary_04_fields);


        JMenuItem summary_05_methods = new JMenuItem("Methods");
        summary_05_methods.addActionListener(evt -> menuItemMethodsActionPerformed(evt));
        menuView_Summary.add(summary_05_methods);


        JMenuItem summary_06_methods_without_code = new JMenuItem("Methods without code");
        summary_06_methods_without_code.addActionListener(evt -> menuItemMethodNoCodeActionPerformed(evt));
        menuView_Summary.add(summary_06_methods_without_code);
        // endregion


        // region Menu --> View --> Validate Changes
        JMenuItem validate_01_complete = new JMenuItem("Complete");
        validate_01_complete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        validate_01_complete.addActionListener(evt -> menuItemValidateCompleteActionPerformed(evt));
        menuView_ValidateChanges.add(validate_01_complete);


        menuView_ValidateChanges.add(new JSeparator());


        JMenuItem validate_02_general = new JMenuItem("General");
        validate_02_general.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        validate_02_general.addActionListener(evt -> menuItemValidateGeneralActionPerformed(evt));
        menuView_ValidateChanges.add(validate_02_general);


        JMenuItem validate_03_cp = new JMenuItem("Constant Pool");
        validate_03_cp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        validate_03_cp.addActionListener(evt -> menuItemValidateConstPoolActionPerformed(evt));
        menuView_ValidateChanges.add(validate_03_cp);


        JMenuItem validate_04_fields = new JMenuItem("Fields");
        validate_04_fields.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        validate_04_fields.addActionListener(evt -> menuItemValidateFieldsActionPerformed(evt));
        menuView_ValidateChanges.add(validate_04_fields);


        JMenuItem validate_05_methods = new JMenuItem("Methods");
        validate_05_methods.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        validate_05_methods.addActionListener(evt -> menuItemValidateMethodsActionPerformed(evt));
        menuView_ValidateChanges.add(validate_05_methods);
        // endregion


        // region Menu --> View --> Related Classes
        menuView_RelatedClasses.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        menuView_RelatedClasses.addActionListener(evt -> menuItemRelatedClassesActionPerformed(evt));
        // endregion
    }

    // region menu view action
    private void menuItemCompleteSummaryActionPerformed(ActionEvent evt) {
        showCompleteSummary();
    }

    private void menuItemGeneralActionPerformed(ActionEvent evt) {
        if (null != classFile) {
            TextSummaryVisitor txtVisitor = new TextSummaryVisitor();
            txtVisitor.visitVersion(classFile.version);
            txtVisitor.visitAccessFlags(classFile.accessFlags);
            txtVisitor.visitClassNames(classFile.classNames);
            txtVisitor.visitAttributes(classFile.attributes);
            txtVisitor.visitInterfaces(classFile.interfaces);

            SummaryDialog summDial = new SummaryDialog(this, true);
            summDial.setClassFileSummary(txtVisitor.getSummary().toString());
            summDial.setVisible(true);
        }
    }

    private void menuItemConstantPoolActionPerformed(ActionEvent evt) {
        if (null != classFile) {
            TextSummaryVisitor txtVisitor = new TextSummaryVisitor();
            txtVisitor.visitConstantPool(classFile.constantPool);
            SummaryDialog summDial = new SummaryDialog(this, true);
            summDial.setClassFileSummary(txtVisitor.getSummary().toString());
            summDial.setVisible(true);
        }
    }

    private void menuItemFieldsActionPerformed(ActionEvent evt) {
        if (null != classFile) {
            TextSummaryVisitor txtVisitor = new TextSummaryVisitor();
            txtVisitor.visitFields(classFile.fields);
            SummaryDialog summDial = new SummaryDialog(this, true);
            summDial.setClassFileSummary(txtVisitor.getSummary().toString());
            summDial.setVisible(true);
        }
    }

    private void menuItemMethodsActionPerformed(ActionEvent evt) {
        showMethodsSummary(true);
    }

    private void menuItemMethodNoCodeActionPerformed(ActionEvent evt) {
        showMethodsSummary(false);
    }

    private void menuItemValidateCompleteActionPerformed(ActionEvent evt) {
        btnValidate_ActionPerformed(evt);
    }

    private void menuItemValidateGeneralActionPerformed(ActionEvent evt) {
        if (null != classFile) {
            Vector vErrVect = new Vector();
            boolean bValid = classFile.version.verify(vErrVect);
            bValid = (bValid && classFile.accessFlags.verify("ClassFile", vErrVect, true));
            bValid = (bValid && classFile.classNames.verify(vErrVect));
            bValid = (bValid && classFile.interfaces.verify(vErrVect));
            bValid = (bValid && classFile.attributes.verify("ClassFile", vErrVect));
            showValidation(bValid, vErrVect);
            AttributesDialog adTest = new AttributesDialog(this, false);
            adTest.setInput(classFile.attributes, classFile.constantPool, true);
            adTest.setVisible(true);
        }
    }

    private void menuItemValidateConstPoolActionPerformed(ActionEvent evt) {
        if (null != classFile) {
            Vector vErrVect = new Vector();
            boolean bValid = classFile.constantPool.verify(vErrVect);
            showValidation(bValid, vErrVect);
        }
    }

    private void menuItemValidateFieldsActionPerformed(ActionEvent evt) {
        if (null != classFile) {
            Vector vErrVect = new Vector();
            boolean bValid = classFile.fields.verify(vErrVect);
            showValidation(bValid, vErrVect);
        }
    }

    private void menuItemValidateMethodsActionPerformed(ActionEvent evt) {
        if (null != classFile) {
            Vector vErrVect = new Vector();
            boolean bValid = classFile.methods.verify(vErrVect);
            showValidation(bValid, vErrVect);
        }
    }

    private void menuItemRelatedClassesActionPerformed(ActionEvent evt) {
        showRelatedClasses();
    }
    // endregion

    private void init_menu_help() {
        menuHelp.setMnemonic('H');
        topMenuBar.add(menuHelp);

        menuHelp_About.addActionListener(evt -> MenuItemAboutActionPerformed(evt));
        menuHelp.add(menuHelp_About);
    }

    // region menu help action
    private void MenuItemAboutActionPerformed(ActionEvent evt) {
        AboutDialog dialog = new AboutDialog(this, false);
        dialog.setVisible(true);
    }
    // endregion

    private void init_tool_bar() {
        toolBar.setFloatable(false);
        getContentPane().add(toolBar, BorderLayout.NORTH);


        btnOpenClass.setIcon(new ImageIcon(getClass().getResource("/res/open1.gif")));
        btnOpenClass.setText("Open");
        btnOpenClass.addActionListener(evt -> btnOpenClass_ActionPerformed(evt));
        toolBar.add(btnOpenClass);


        btnNewFile.setIcon(new ImageIcon(getClass().getResource("/res/newfile1.gif")));
        btnNewFile.setText("New");
        btnNewFile.setName("");
        btnNewFile.addActionListener(evt -> btnNewFile_ActionPerformed(evt));
        toolBar.add(btnNewFile);


        btnSaveClass.setIcon(new ImageIcon(getClass().getResource("/res/save1.gif")));
        btnSaveClass.setText("Save");
        btnSaveClass.addActionListener(evt -> btnSaveClass_ActionPerformed(evt));
        toolBar.add(btnSaveClass);


        btnCloseClass.setIcon(new ImageIcon(getClass().getResource("/res/close1.gif")));
        btnCloseClass.setText("Close");
        btnCloseClass.addActionListener(evt -> btnCloseClass_ActionPerformed(evt));
        toolBar.add(btnCloseClass);


        btnValidate.setIcon(new ImageIcon(getClass().getResource("/res/verify3.gif")));
        btnValidate.setText("Validate");
        btnValidate.addActionListener(evt -> btnValidate_ActionPerformed(evt));
        toolBar.add(btnValidate);


        btnRelatedClass.setIcon(new ImageIcon(getClass().getResource("/res/related1.gif")));
        btnRelatedClass.setText("Related");
        btnRelatedClass.addActionListener(evt -> btnRelatedClass_ActionPerformed(evt));
        toolBar.add(btnRelatedClass);


        btnShowSummary.setIcon(new ImageIcon(getClass().getResource("/res/report1.gif")));
        btnShowSummary.setText("Summary");
        btnShowSummary.addActionListener(evt -> btnShowSummary_ActionPerformed(evt));
        toolBar.add(btnShowSummary);


        toolBar.add(new JSeparator());


        btnModifyMode.setBackground(new Color(153, 255, 153));
        btnModifyMode.setText("Modify Mode (Off)");
        btnModifyMode.addActionListener(evt -> btnModifyMode_ActionPerformed(evt));
        toolBar.add(btnModifyMode);

        setModifyFlag();
    }

    // region tool bar action
    private void btnOpenClass_ActionPerformed(ActionEvent evt) {
        openNewFile();
    }

    private void btnNewFile_ActionPerformed(ActionEvent evt) {
        createNewClass();
    }

    private void btnSaveClass_ActionPerformed(ActionEvent evt) {
        saveClass();
    }

    private void btnCloseClass_ActionPerformed(ActionEvent evt) {
        closeClass();
    }

    private void btnValidate_ActionPerformed(ActionEvent evt) {
        if (null != classFile) {
            Vector vErrVect = new Vector();
            boolean bValid = classFile.verify(vErrVect);
            showValidation(bValid, vErrVect);
        }
    }

    private void btnRelatedClass_ActionPerformed(ActionEvent evt) {
        showRelatedClasses();
    }

    private void btnShowSummary_ActionPerformed(ActionEvent evt) {
        showCompleteSummary();
    }

    private void btnModifyMode_ActionPerformed(ActionEvent evt) {
        bEditable = btnModifyMode.isSelected();
        btnModifyMode.setText("Modify Mode (" + (bEditable ? "On" : "Off") + ")");
        btnModifyMode.setBackground(bEditable ? Color.pink : (new Color(153, 255, 153)));
        setModifyFlag();
    }
    // endregion

    private void init_layout() {
        splitPane.setDividerLocation(100);
        splitPane.setOneTouchExpandable(true);
        splitPane.setAutoscrolls(true);

        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    private void init_left() {
        ((DefaultTreeCellRenderer) tree.getCellRenderer()).setLeafIcon(new ImageIcon(getClass().getResource("/res/class.gif")));
        tree.setRootVisible(false);
        tree.setMinimumSize(new Dimension(50, 400));
        tree.addTreeSelectionListener(evt -> jTree1ValueChanged(evt));
        splitPane.setLeftComponent(tree);
    }

    private void init_right() {
        tabbedPane.setMinimumSize(new Dimension(400, 400));
        tabbedPane.addChangeListener(evt -> jTabbedPane1StateChanged(evt));

        splitPane.setRightComponent(tabbedPane);

        tabbedPane.addTab("General", new ImageIcon(getClass().getResource("/res/general.gif")), tabPaneGeneralProp);
        tabbedPane.addTab("Constant Pool", new ImageIcon(getClass().getResource("/res/constpool.gif")), tabPaneConstPool);
        tabbedPane.addTab("Fields", new ImageIcon(getClass().getResource("/res/field.gif")), tabPaneFields);
        tabbedPane.addTab("Methods", new ImageIcon(getClass().getResource("/res/method.gif")), tabPaneMethods);
    }

    private void init_status_bar() {
        statusBarLabel.setText(" ");
        statusBarLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        getContentPane().add(statusBarLabel, BorderLayout.SOUTH);
    }


    private void MenuItemFileHistoryActionPerformed(ActionEvent evt) {
        String sFile = evt.getActionCommand();
        try {
            chkNLoadClass(sFile);
        } catch (FileNotFoundException fnfe) {
            statusBarLabel.setText("File not found: " + sFile);
        } catch (IOException ioe) {
            statusBarLabel.setText("Error reading file: " + sFile);
        }
    }


    private void jTabbedPane1StateChanged(ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if (null == classFile) return;
        updateTab(tabbedPane.getSelectedIndex());
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jTree1ValueChanged(TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (node == null) return;

        ClassFileStatus newClassStatus = (ClassFileStatus) node;
        switchClass(newClassStatus);
    }//GEN-LAST:event_jTree1ValueChanged


    /**
     * Exit the Application
     */
    private void exitForm(WindowEvent evt) {//GEN-FIRST:event_exitForm
        saveHistAndExit();
    }//GEN-LAST:event_exitForm

    private void saveHistAndExit() {
        try {
            File fHist = new File(System.getProperty("user.home") + File.separatorChar + ".ce" + File.separatorChar + ".ce_history");
            FileWriter fw = new FileWriter(fHist);
            String sSep = System.getProperty("line.separator");

            for (int iIndex = 0; iIndex < history_file_list.size(); iIndex++) {
                fw.write(history_file_list.get(iIndex) + sSep);
            }
            fw.close();
        } catch (Exception e) {
            System.err.println("Could not save file history - " + e.toString());
        }
        System.exit(0);
    }


    private void clearTab(int iWhich) {
        tabPaneGeneralProp.setClassFile(classFile);
        tabPaneConstPool.setClassFile(classFile);
        tabPaneFields.setClassFile(classFile);
        tabPaneMethods.setClassFile(classFile);
        switch (iWhich) {
            case 0:
                tabPaneGeneralProp.clear();
                break;
            case 1:
                tabPaneConstPool.clear();
                break;
            case 2:
                tabPaneFields.clear();
                break;
            case 3:
                tabPaneMethods.clear();
                break;
            default:
                // raise exception??
        }
    }

    private void updateTab(int iWhich) {
        tabPaneGeneralProp.setClassFile(classFile);
        tabPaneConstPool.setClassFile(classFile);
        tabPaneFields.setClassFile(classFile);
        tabPaneMethods.setClassFile(classFile);
        switch (iWhich) {
            case 0:
                tabPaneGeneralProp.refresh();
                break;
            case 1:
                tabPaneConstPool.refresh();
                break;
            case 2:
                tabPaneFields.refresh();
                break;
            case 3:
                tabPaneMethods.refresh();
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
        if (classStatus == newClassStatus) return;
        if (null != classStatus) {
            clearClassData();
            classStatus = null;
        }
        classStatus = newClassStatus;
        classFile = classStatus.classFile;
        tabbedPane.setSelectedIndex(0);
        updateTab(0);
        tree.setSelectionRow(rootTreeNode.getIndex(newClassStatus));
        setTitle("ClassEditor - " + newClassStatus.getTreeDisplayString());
    }

    private void unloadClass() {
        setTitle("ClassEditor");
        if (null != classStatus) {
            clearClassData();
            rootTreeNode.remove(classStatus);
            tree.setModel(new DefaultTreeModel(rootTreeNode));
            classStatus = null;
        }

        // check if any other file is open, switch to that
        if (0 != rootTreeNode.getChildCount()) {
            ClassFileStatus newClassStatus = (ClassFileStatus) rootTreeNode.getChildAt(0);
            switchClass(newClassStatus);
            return;
        }

        // else disable irrelevant menu and buttons
        setMenuAndButtonStatus();
    }

    public void setMenuAndButtonStatus() {
        boolean bEnabled = (null != classStatus);

        btnSaveClass.setEnabled(bEnabled);
        btnCloseClass.setEnabled(bEnabled);
        btnValidate.setEnabled(bEnabled);
        btnRelatedClass.setEnabled(bEnabled);
        btnShowSummary.setEnabled(bEnabled);
        menuFile_Close.setEnabled(bEnabled);
        menuFile_Save.setEnabled(bEnabled);
        menuFile_SaveAs.setEnabled(bEnabled);
        menuFile_Export.setEnabled(bEnabled);
        menuFile_Export.setEnabled(bEnabled);
        menuView_Summary.setEnabled(bEnabled);
        menuView_ValidateChanges.setEnabled(bEnabled);
        menuView_RelatedClasses.setEnabled(bEnabled);
    }

    private void writeClass(String sFileName) {
        DataOutputStream dos;
        try {
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(sFileName))));
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }

        // write the class to a new file
        try {
            classFile.write(dos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void chkNLoadClass(String filename) throws FileNotFoundException, IOException {
        int iIndex;
        ClassFileStatus newClassStatus = null;
        ClassFileStatus sTempStatus = new ClassFileStatus(filename, null);

        // check if it is already loaded
        for (iIndex = rootTreeNode.getChildCount(); iIndex > 0; iIndex--) {
            ClassFileStatus thisStatus = (ClassFileStatus) rootTreeNode.getChildAt(iIndex - 1);
            if (sTempStatus.sClassName.equals(thisStatus.sClassName) && sTempStatus.sPath.equals(thisStatus.sPath)) {
                // found
                newClassStatus = thisStatus;
                // check if it is the current class, if not set.
                classFile = newClassStatus.classFile;
                break;
            }
        }

        if (null == newClassStatus) {
            // read class
            statusBarLabel.setText("Reading Class " + filename);
            try {
                readClass(filename);
                statusBarLabel.setText("Read class: " + filename);
            } catch (FileNotFoundException fnfe) {
                statusBarLabel.setText("File not found: " + filename);
                return;
            } catch (IOException ioe) {
                statusBarLabel.setText("Error reading file: " + filename);
                return;
            }

            newClassStatus = new ClassFileStatus(filename, classFile);
            rootTreeNode.add(newClassStatus);
            tree.setModel(new DefaultTreeModel(rootTreeNode));
            statusBarLabel.setText("Loaded class: " + filename);
        }

        switchClass(newClassStatus);
        setMenuAndButtonStatus(); // enable menu and buttons
        addToFileHistory(filename, false); // add to file history
    }

    private void addToFileHistory(String sFileName, boolean bAddToEnd) {
        for (int i = 0; i < history_file_list.size(); i++) {
            if (sFileName.equals(history_file_list.get(i))) {
                menuFile.remove((JMenuItem) (history_file_menu_item_list.get(i)));
                history_file_menu_item_list.remove(i);
                history_file_list.remove(i);
                break;
            }
        }

        if (history_file_menu_item_list.size() >= NUM_FILES_IN_HISTORY) {
            int iToRem = bAddToEnd ? 0 : (NUM_FILES_IN_HISTORY - 1);
            menuFile.remove((JMenuItem) (history_file_menu_item_list.get(iToRem)));
            history_file_list.remove(iToRem);
            history_file_menu_item_list.remove(iToRem);
        }

        JMenuItem newMenuItem = new JMenuItem();
        newMenuItem.setText(sFileName);
        newMenuItem.setActionCommand(sFileName);
        newMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                MenuItemFileHistoryActionPerformed(evt);
            }
        });

        if (bAddToEnd) {
            history_file_list.add(sFileName);
            history_file_menu_item_list.add(newMenuItem);
            menuFile.add(newMenuItem);
        } else {
            history_file_list.add(0, sFileName);
            history_file_menu_item_list.add(0, newMenuItem);
            menuFile.add(newMenuItem, 10);
        }
    }

    private void readClass(String sFileName) throws FileNotFoundException, IOException {
        DataInputStream dis;
        classFile = new ClassFile();
        dis = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(sFileName))));
        // read the class
        classFile.read(dis);
        dis.close();
    }

    /**
     * Open new file
     */
    private void createNewClass() {
        String sFileName;
        int returnVal = classFileChooser.showSaveDialog(this);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
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
        } catch (IOException ioe) {
            statusBarLabel.setText("Internal error while creating class");
            return;
        }
        ClassFileStatus newClassStatus = new ClassFileStatus(sFileName, newClassFile);
        rootTreeNode.add(newClassStatus);
        tree.setModel(new DefaultTreeModel(rootTreeNode));
        statusBarLabel.setText("Loaded class: " + sFileName);
        switchClass(newClassStatus);
    }

    private void openNewFile() {
        String sNewFile;
        int returnVal = classFileChooser.showOpenDialog(this);

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        sNewFile = classFileChooser.getSelectedFile().getAbsolutePath();

        statusBarLabel.setText("Loading Class " + sNewFile);
        try {
            chkNLoadClass(sNewFile);
        } catch (FileNotFoundException fnfe) {
            statusBarLabel.setText("File not found: " + sNewFile);
        } catch (IOException ioe) {
            statusBarLabel.setText("Error reading file: " + sNewFile);
        }
    }

    private void closeClass() {
        if (null != classStatus) {
            statusBarLabel.setText("Unloading Class " + classStatus.sFileName);
            unloadClass();
        }
        statusBarLabel.setText("Ready");
    }

    private void saveClass() {
        if (null != classStatus) {
            statusBarLabel.setText("Saving to class " + classStatus.sFileName);
            writeClass(classStatus.sFileName);
            statusBarLabel.setText("Wrote class " + classStatus.sFileName);
        }
    }

    private void showValidation(boolean bValid, Vector vErrVect) {
        if (!bValid) {
            StringBuffer summaryBuffer = new StringBuffer(256);
            for (int iIndex = 0; iIndex < vErrVect.size(); iIndex++) {
                summaryBuffer.append((String) vErrVect.elementAt(iIndex)).append(Utils.sNewLine);
            }
            SummaryDialog summDial = new SummaryDialog(this, true);
            summDial.setClassFileSummary(summaryBuffer.toString());
            summDial.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "No errors detected.", "Validation Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showCompleteSummary() {
        if (null != classFile) {
            TextSummaryVisitor txtVisitor = new TextSummaryVisitor();
            txtVisitor.visitClass(classFile);
            SummaryDialog summDial = new SummaryDialog(this, true);
            summDial.setClassFileSummary(txtVisitor.getSummary().toString());
            summDial.setVisible(true);
        }
    }

    private void showMethodsSummary(boolean bShowCode) {
        if (null != classFile) {
            TextSummaryVisitor txtVisitor = new TextSummaryVisitor(!bShowCode);
            txtVisitor.visitMethods(classFile.methods);
            SummaryDialog summDial = new SummaryDialog(this, true);
            summDial.setClassFileSummary(txtVisitor.getSummary().toString());
            summDial.setVisible(true);
        }
    }

    private void showRelatedClasses() {
        if (null != classFile) {
            RelatedClasses relClasses = new RelatedClasses(this, true);
            relClasses.showRelatedClasses(this, classFile.constantPool);
            relClasses.setVisible(true);
        }
    }

    private void setModifyFlag() {
        tabPaneGeneralProp.setModifyMode(bEditable);
        tabPaneConstPool.setModifyMode(bEditable);
        tabPaneFields.setModifyMode(bEditable);
        tabPaneMethods.setModifyMode(bEditable);
    }

}
