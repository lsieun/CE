/*
 * Utils.java
 * 
 * Created on June 2nd, 1999
 *
 * Modification Log:
 * 1.00  02nd Jun 1999   Tanmay   Original Version
 * 1.01  05th Jul 1999   Tanmay   Added methods to get back raw descriptor from 
 *                                readable descriptor.
 * 1.02  14th Aug 1999   Tanmay   Added method to interpret method descriptor
 * 1.03  14th Aug 1999   Tanmay   Added method to reverse interpret method descriptor
 * 1.04  05th Jan 2002   Tanmay   Added method to check for Java identifiers
 * 1.05  25th Mar 2002   Tanmay   Added method to check for Java class names
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 */

package classfile;

import java.util.Stack;

/**
 * Common methods and values used frequently and throughout. 
 *<br><br>
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.05, 25th March, 2002
 */

public final class Utils
{
    public static String sNewLine = System.getProperty("line.separator");
    public static String sUnderLine = "----------------------------------------------------------------------";
        
    public static String convertStrToClassStr(String sInStr)
    {
        return sInStr.replace('.', '/');
    }
    
    public static String convertClassStrToStr(String sInStr)
    {
        return sInStr.replace('/', '.');
    }
    
    public static String getRawDesc(String sReadableDesc)
    {
        if (sReadableDesc.startsWith("byte"))           return "B";
        else if (sReadableDesc.startsWith("char"))      return "C";
        else if (sReadableDesc.startsWith("double"))    return "D";
        else if (sReadableDesc.startsWith("float"))     return "F";
        else if (sReadableDesc.startsWith("int"))       return "I";
        else if (sReadableDesc.startsWith("long"))      return "L";
        else if (sReadableDesc.startsWith("short"))     return "Z";
        else if (sReadableDesc.startsWith("boolean"))   return "Z";
        else if (sReadableDesc.startsWith("void"))      return "V";
        else if (sReadableDesc.startsWith("[]"))        return "[" + getRawDesc(sReadableDesc.substring(2));
        else                                            return "L" + convertStrToClassStr(sReadableDesc) + ";";
    }

    public static String getReadableDesc(String sRawDesc)
    {
        if(sRawDesc.length() > 0)
        {
            switch(sRawDesc.charAt(0))
            {
                case 'B':
                    if(sRawDesc.length() == 1) return "byte";
                case 'C':
                    if(sRawDesc.length() == 1) return "char";
                case 'D':
                    if(sRawDesc.length() == 1) return "double";
                case 'F':
                    if(sRawDesc.length() == 1) return "float";
                case 'I':
                    if(sRawDesc.length() == 1) return "int";
                case 'J':
                    if(sRawDesc.length() == 1) return "long";
                case 'S':
                    if(sRawDesc.length() == 1) return "short";
                case 'Z':
                    if(sRawDesc.length() == 1) return "boolean";
                case 'V':
                    if(sRawDesc.length() == 1) return "void";
                case 'L':
                    {
                        int iClassEnd = sRawDesc.indexOf(';');
                        if(sRawDesc.length() > (iClassEnd+1)) return "unknown";
                        return convertClassStrToStr(sRawDesc.substring(1, iClassEnd));
                    }
                case '[':
                    {
                        String sArrayType = getReadableDesc(sRawDesc.substring(1));
                        if(sArrayType.equals("unknown")) return "unknown";
                        return "[]" + sArrayType;
                    }
            }
        }
        return "unknown";
    }

    public static String getRawMethodDesc(String []aDescs)
    {
        String sRawRetType = getRawDesc(aDescs[0]);
        String sParamDesc = "(";
        for(int iParamIndex=1; iParamIndex < aDescs.length; iParamIndex++)
        {
            sParamDesc += getRawDesc(aDescs[iParamIndex]);
        }
        sParamDesc += ")";
        
        return sParamDesc + sRawRetType;
    }
    
    public static String [] getReadableMethodDesc(String  sRawDesc)
    {
        int     iStart, iEnd;
        Stack   descStack = new Stack();
        String  aDescs[];
        String  sParamsDesc;

        iStart = 1;
        iEnd = sRawDesc.indexOf(')');
        if( (sRawDesc.length() == 0) || (-1 == iEnd) || (sRawDesc.charAt(0) != '(') ||
            (sRawDesc.length() <= iEnd+1) )
        {
            return null; // not a method desc
        }
        
        if (iStart == iEnd)
        {
            sParamsDesc = null; // no params
        }
        else
        {
            sParamsDesc = sRawDesc.substring(iStart, iEnd);
        }

        String  sRetDesc = Utils.getReadableDesc(sRawDesc.substring(iEnd+1));
        descStack.push(sRetDesc);   // push the return parameter type

        if (null != sParamsDesc)
        {
            for(iStart=0; iStart < (iEnd-1); )
            {
                descStack.push(Utils.getReadableDesc(sParamsDesc.substring(iStart)));
                switch(sParamsDesc.charAt(iStart))
                {
                    case 'B':
                    case 'C':
                    case 'D':
                    case 'F':
                    case 'I':
                    case 'J':
                    case 'S':
                    case 'Z':
                    case 'V':
                        iStart++;
                        break;
                    case 'L':
                        iStart = sParamsDesc.indexOf(';', iStart)+1;
                        break;
                    case '[':
                        while('[' == sParamsDesc.charAt(++iStart)); // skip all [s
                        if('L' == sParamsDesc.charAt(iStart))
                        {
                            iStart = sParamsDesc.indexOf(';', iStart)+1;
                        }
                        else
                        {
                            iStart++;
                        }
                        break;
                }
            }
        }

        int iStackSize = descStack.size();
        aDescs = new String[iStackSize];
        while(iStackSize > 0)
        {
            aDescs[--iStackSize] = (String) descStack.pop();
        }
        
        return aDescs;
    }

    public static boolean isJavaIdentifier(String sStr) {
        int iLen = sStr.length();
        
        if(0 == iLen) return false;
        if(!Character.isJavaIdentifierStart(sStr.charAt(0))) return false;
        
        for(int iIndex=1; iIndex < iLen; iIndex++) {
            if(!Character.isJavaIdentifierPart(sStr.charAt(iIndex))) return false;
        }
        return true;
    }


    public static boolean isJavaClassString(String sStr) {
        int iLen = sStr.length();
        
        if(0 == iLen) return false;
        if(!Character.isJavaIdentifierStart(sStr.charAt(0))) return false;
        
        for(int iIndex=1; iIndex < iLen; iIndex++) {
            if(!Character.isJavaIdentifierPart(sStr.charAt(iIndex))) {
                if(!(sStr.charAt(iIndex) == '/')) return false;
            }
        }
        return true;
    }    
}
