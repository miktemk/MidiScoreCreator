package Features;

import DataStructure.*;
import java.lang.*;
import java.io.*;

public class MiscHelper
{
    public static String printBar(char c, int l)
    {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < l; i++)
        {
            sb.append(c);
        }
        return sb.toString();
    }
    public static void saveBoolean(PrintStream stream, int indent, boolean value)
    {// will write "true" or "false" in lowercase
        stream.println(MiscHelper.printBar(' ', indent)+"<Boolean>"+value+"</Boolean>");
    }
    public static void saveInteger(PrintStream stream, int indent, int value)
    {
        stream.println(MiscHelper.printBar(' ', indent)+"<Integer>"+value+"</Integer>");
    }
    public static void saveDouble(PrintStream stream, int indent, double value)
    {
        stream.println(MiscHelper.printBar(' ', indent)+"<Double>"+value+"</Double>");
    }
    public static void saveString(PrintStream stream, int indent, String value)
    {
        stream.println(MiscHelper.printBar(' ', indent)+"<String>"+value+"</String>");
    }
    public static void saveIntArray(PrintStream stream, int indent, int[] value)
    {
        stream.println(MiscHelper.printBar(' ', indent)+"<IntArray>");
        for(int i = 0; i < value.length; i++)
        {
            stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT)+"<Integer>"+value[i]+"</Integer>");
        }
        stream.println(MiscHelper.printBar(' ', indent)+"</IntArray>");
    }
}
