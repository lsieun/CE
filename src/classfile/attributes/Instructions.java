/*
 * Instructions.java
 * 
 * Created on March 12, 1999
 *
 * Modification Log:
 * 1.00  12th Mar 1999   Tanmay   Original version.
 *-----------------------------------------------------------------------------------------
 *       10th Sep 2003   Tanmay   Moved to SourceForge (http://classeditor.sourceforge.net)
 *-----------------------------------------------------------------------------------------
 *
 */

package classfile.attributes;


/**
 * Codes and string representations of JVM instructions.
 *
 * @author 	    Tanmay K. Mohapatra
 * @version     1.00, 12th March, 1999
 */

public final class Instructions
{
    public static String sInstrCodes[] = new String[1024];
    
    static
    {
        sInstrCodes[0x32] = "aaload";
        sInstrCodes[0x53] = "aastore";
        sInstrCodes[0x1] = "aconst_null";
        sInstrCodes[0x19] = "aload";
        sInstrCodes[0x2a] = "aload_0";
        sInstrCodes[0x2b] = "aload_1";
        sInstrCodes[0x2c] = "aload_2";
        sInstrCodes[0x2d] = "aload_3";
        sInstrCodes[0xbd] = "anewarray";
        sInstrCodes[0xb0] = "areturn";
        sInstrCodes[0xbe] = "arraylength";
        sInstrCodes[0x3a] = "astore";
        sInstrCodes[0x4b] = "astore_0";
        sInstrCodes[0x4c] = "astore_1";
        sInstrCodes[0x4d] = "astore_2";
        sInstrCodes[0x4e] = "astore_3";
        sInstrCodes[0xbf] = "athrow";    
        sInstrCodes[0x33] = "baload";
        sInstrCodes[0x54] = "bastore";
        sInstrCodes[0x10] = "bipush";
        sInstrCodes[0x34] = "caload";
        sInstrCodes[0x55] = "castore";
        sInstrCodes[0xc0] = "checkcast";
        sInstrCodes[0x90] = "d2f";
        sInstrCodes[0x8e] = "d2i";
        sInstrCodes[0x8f] = "d2l";
        sInstrCodes[0x63] = "dadd";
        sInstrCodes[0x31] = "daload";
        sInstrCodes[0x52] = "dastore";
        sInstrCodes[0x98] = "dcmpg";
        sInstrCodes[0x97] = "dcmpl";
        sInstrCodes[0xe] = "dconst_0";
        sInstrCodes[0xf] = "dconst_1";
        sInstrCodes[0x6f] = "ddiv";
        sInstrCodes[0x18] = "dload";
        sInstrCodes[0x26] = "dload_0";
        sInstrCodes[0x27] = "dload_1";
        sInstrCodes[0x28] = "dload_2";
        sInstrCodes[0x29] = "dload_3";
        sInstrCodes[0x6b] = "dmul";
        sInstrCodes[0x77] = "dneg";
        sInstrCodes[0x73] = "drem";
        sInstrCodes[0xaf] = "dreturn";
        sInstrCodes[0x39] = "dstore";
        sInstrCodes[0x47] = "dstore_0";
        sInstrCodes[0x48] = "dstore_1";
        sInstrCodes[0x49] = "dstore_2";
        sInstrCodes[0x4a] = "dstore_3";
        sInstrCodes[0x67] = "dsub";
        sInstrCodes[0x59] = "dup";
        sInstrCodes[0x5a] = "dup_x1";
        sInstrCodes[0x5b] = "dup_x2";
        sInstrCodes[0x5c] = "dup2";
        sInstrCodes[0x5d] = "dup2_x1";
        sInstrCodes[0x5e] = "dup2_x2";
        sInstrCodes[0x8d] = "f2d";
        sInstrCodes[0x8b] = "f2i";    
        sInstrCodes[0x8c] = "f2l";
        sInstrCodes[0x62] = "fadd";
        sInstrCodes[0x30] = "faload";
        sInstrCodes[0x51] = "fastore";
        sInstrCodes[0x96] = "fcmpg";
        sInstrCodes[0x95] = "fcmpl";
        sInstrCodes[0xb] = "fconst_0";
        sInstrCodes[0xc] = "fconst_1";
        sInstrCodes[0xd] = "fconst_2";
        sInstrCodes[0x6e] = "fdiv";    
        sInstrCodes[0x17] = "fload";
        sInstrCodes[0x22] = "fload_0";
        sInstrCodes[0x23] = "fload_1";
        sInstrCodes[0x24] = "fload_2";
        sInstrCodes[0x25] = "fload_3";
        sInstrCodes[0x6a] = "fmul";
        sInstrCodes[0x76] = "fneg";
        sInstrCodes[0x72] = "frem";
        sInstrCodes[0xae] = "freturn";
        sInstrCodes[0x38] = "fstore";
        sInstrCodes[0x43] = "fstore_0";
        sInstrCodes[0x44] = "fstore_1";
        sInstrCodes[0x45] = "fstore_2";
        sInstrCodes[0x46] = "fstore_3";
        sInstrCodes[0x66] = "fsub";    
        sInstrCodes[0xb4] = "getfield";
        sInstrCodes[0xb2] = "getstatic";
        sInstrCodes[0xa7] = "goto";
        sInstrCodes[0xc8] = "goto_w";
        sInstrCodes[0x91] = "i2b";
        sInstrCodes[0x92] = "i2c";
        sInstrCodes[0x87] = "i2d";
        sInstrCodes[0x86] = "i2f";
        sInstrCodes[0x85] = "i2l";
        sInstrCodes[0x93] = "i2s";
        sInstrCodes[0x60] = "iadd";
        sInstrCodes[0x2e] = "iaload";
        sInstrCodes[0x7e] = "iand";
        sInstrCodes[0x4f] = "iastore";
        sInstrCodes[0x2] = "iconst_ml";
        sInstrCodes[0x3] = "iconst_0";
        sInstrCodes[0x4] = "iconst_1";
        sInstrCodes[0x5] = "iconst_2";
        sInstrCodes[0x6] = "iconst_3";
        sInstrCodes[0x7] = "iconst_4";
        sInstrCodes[0x8] = "iconst_5";
        sInstrCodes[0x6c] = "idiv";
        sInstrCodes[0xa5] = "if_acmpeq";
        sInstrCodes[0xa6] = "if_acmpne";
        sInstrCodes[0x9f] = "if_icmpeq";
        sInstrCodes[0xa0] = "if_icmpne";
        sInstrCodes[0xa1] = "if_icmplt";
        sInstrCodes[0xa2] = "if_icmpge";
        sInstrCodes[0xa3] = "if_icmpgt";
        sInstrCodes[0xa4] = "if_icmple";
        sInstrCodes[0x99] = "ifeq";
        sInstrCodes[0x9a] = "ifne";
        sInstrCodes[0x9b] = "iflt";
        sInstrCodes[0x9c] = "ifge";
        sInstrCodes[0x9d] = "ifgt";
        sInstrCodes[0x9e] = "ifle";
        sInstrCodes[0xc7] = "ifnonnull";
        sInstrCodes[0xc6] = "ifnull";
        sInstrCodes[0x84] = "iinc";
        sInstrCodes[0x15] = "iload";
        sInstrCodes[0x1a] = "iload_0";
        sInstrCodes[0x1b] = "iload_1";
        sInstrCodes[0x1c] = "iload_2";
        sInstrCodes[0x1d] = "iload_3";
        sInstrCodes[0x68] = "imul";
        sInstrCodes[0x74] = "ineg";
        sInstrCodes[0xc1] = "instanceof";
        sInstrCodes[0xb9] = "invokeinterface";
        sInstrCodes[0xb7] = "invokespecial";
        sInstrCodes[0xb8] = "invokestatic";
        sInstrCodes[0xb6] = "invokevirtual";
        sInstrCodes[0x80] = "ior";
        sInstrCodes[0x70] = "irem";
        sInstrCodes[0xac] = "ireturn";
        sInstrCodes[0x78] = "ishl";
        sInstrCodes[0x7a] = "ishr";
        sInstrCodes[0x36] = "istore";
        sInstrCodes[0x3b] = "istore_0";
        sInstrCodes[0x3c] = "istore_1";
        sInstrCodes[0x3d] = "istore_2";
        sInstrCodes[0x3e] = "istore_3";
        sInstrCodes[0x64] = "isub";
        sInstrCodes[0x7c] = "iushr";
        sInstrCodes[0x82] = "ixor";    
        sInstrCodes[0xa8] = "jshr";
        sInstrCodes[0xc9] = "jshr_w";
        sInstrCodes[0x8a] = "l2d";
        sInstrCodes[0x89] = "l2f";
        sInstrCodes[0x88] = "l2i";
        sInstrCodes[0x61] = "ladd";
        sInstrCodes[0x2f] = "laload";
        sInstrCodes[0x7f] = "land";
        sInstrCodes[0x50] = "lastore";
        sInstrCodes[0x94] = "lcmp";
        sInstrCodes[0x9] = "lconst_0";
        sInstrCodes[0xa] = "lconst_1";
        sInstrCodes[0x12] = "ldc";
        sInstrCodes[0x13] = "ldc_w";
        sInstrCodes[0x14] = "ldc2_w";
        sInstrCodes[0x6d] = "ldiv";
        sInstrCodes[0x16] = "lload";
        sInstrCodes[0x1e] = "lload_0";
        sInstrCodes[0x1f] = "lload_1";
        sInstrCodes[0x20] = "lload_2";
        sInstrCodes[0x21] = "lload_3";
        sInstrCodes[0x69] = "lmul";
        sInstrCodes[0x75] = "lneg";

        sInstrCodes[0xab] = "lookupswitch";
        sInstrCodes[0x81] = "lor";
        sInstrCodes[0x71] = "lrem";
        sInstrCodes[0xad] = "lreturn";
        sInstrCodes[0x79] = "lshl";
        sInstrCodes[0x7b] = "lshr";
        sInstrCodes[0x37] = "lstore";
        sInstrCodes[0x3f] = "lstore_0";
        sInstrCodes[0x40] = "lstore_1";
        sInstrCodes[0x41] = "lstore_2";
        sInstrCodes[0x42] = "lstore_3";
        sInstrCodes[0x65] = "lsub";
        sInstrCodes[0x7d] = "lushr";
        sInstrCodes[0x83] = "lxor";
        sInstrCodes[0xc2] = "monitorenter";
        sInstrCodes[0xc3] = "monitorexit";
        sInstrCodes[0xc5] = "multianewarray";
        sInstrCodes[0xbb] = "new";
        sInstrCodes[0xbc] = "newarray";
        sInstrCodes[0x0] = "nop";
        sInstrCodes[0x57] = "pop";
        sInstrCodes[0x58] = "pop2";
        sInstrCodes[0xb5] = "putfield";
        sInstrCodes[0xb3] = "putstatic";
        sInstrCodes[0xa9] = "ret";
        sInstrCodes[0xb1] = "return";    
        sInstrCodes[0x35] = "saload";
        sInstrCodes[0x56] = "sastore";
        sInstrCodes[0x11] = "sipush";
        sInstrCodes[0x5f] = "swap";
        sInstrCodes[0xaa] = "tableswitch";
        sInstrCodes[0xc4] = "wide";
    }
}
