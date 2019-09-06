/*
 * AttributeTreeNode.java
 *
 * Created on January 27, 2002, 3:32 PM
 *
 * Modification Log:
 * 1.00  27th Jan 2002   Tanmay   Original Version
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */

package guihelper;

import classfile.*;
import classfile.attributes.Attribute;
import classfile.attributes.Attributes;
import classfile.attributes.CodeAttribute;
import java.awt.Component;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 *
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.00, 27th Jan, 2002
 */

public class AttributeTreeNode extends DefaultMutableTreeNode {

    /** Creates new AttributeTreeNode */
    public Attribute attr;

    public AttributeTreeNode(Attribute attrIn)
    {
        attr = attrIn;
        this.setUserObject(attr.sName);
    }
    
    public static JFrame getFrameFrom(Component compIn) {
        int iIndex = 0;
        Component comp = compIn;
        
        for(iIndex=0; (iIndex < 1000) && !(comp instanceof JFrame); iIndex++) {
            comp = comp.getParent();
        }
        
        if(comp instanceof JFrame) return (JFrame)comp;
        return null;
    }
        
    
    private static void addTreeNode(Attribute attr, DefaultMutableTreeNode rootNode) {
        AttributeTreeNode newNode = new AttributeTreeNode(attr);
        if(attr instanceof CodeAttribute) {
            Attributes subAttrs = ((CodeAttribute)attr).codeAttributes;
            makeTree(subAttrs, newNode);
        }
        rootNode.add(newNode);
    }
    
    public static void makeTree(Attributes attributeList, DefaultMutableTreeNode rootNode) {
        for (int iIndex=attributeList.getAttribCount(); iIndex > 0; iIndex--) {
            Attribute thisAttrib = attributeList.getAttribute(iIndex-1);
            addTreeNode(thisAttrib, rootNode);
        }
    }
}
