/*
 * AttribDisplay.java
 *
 * Created on January 27, 2002, 5:20 PM
 *
 * Modification Log:
 * 1.00   27th Jan 2002   Tanmay   Original version.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */


package gui.attributes;

import classfile.*;
import classfile.attributes.Attribute;


/**
 * Copyright (C) 2002-2003  Tanmay K. Mohapatra
 * <br>
 *
 * @author 	Tanmay K. Mohapatra
 * @version     1.00, 27th January, 2002
 */
public interface AttribDisplay {

    public void setInput(Attribute attribute, ConstantPool constPool);
}

