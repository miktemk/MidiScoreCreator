package Features;

import org.xml.sax.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

public class ScaleParser extends HandlerBase
{
    Vector values;
    String scaleName = "";
    Vector hierarchy;
    StringBuffer biff = null;
    boolean normilize;
    public ScaleParser(boolean normilize)
    {
        this.normilize = normilize;
        values = new Vector();
        hierarchy = new Vector();
    }
    public void startElement(String name, AttributeList attrs) throws SAXException
    {
        hierarchy.addElement(name);
        biff = new StringBuffer();
    }
    public void endElement(String name) throws SAXException
    {
        if(biff != null) processChars(new String(biff));
        biff = null;
        hierarchy.removeElementAt(hierarchy.size()-1);
    }
    public void characters(char[] buf, int offset, int len) throws SAXException
    {
        if(biff != null) biff.append(buf, offset, len);
    }
    public void processChars(String chars)
    {
        if(hierarchy.size() == 2)
        {
            String tag = (String)hierarchy.lastElement();
            if(tag.equals("name"))
            {
                scaleName = chars;
            }
            else if(tag.equals("note"))
            {
                try
                {
                    int val = Integer.parseInt(chars);
                    values.addElement(new Integer(val));
                }
                catch(Exception e) {}
            }
        }
    }
    //-------------------
    public String getName() { return scaleName; }
    public int[]  getScale()
    {
        int[] arr = new int[values.size()];
        int i = 0;
        int base = 0;
        for(Enumeration e = values.elements(); e.hasMoreElements();)
        {
            Integer cur = (Integer)e.nextElement();
            if(normilize && i == 0) base = cur.intValue();
            arr[i] = cur.intValue()-base;
            i++;
        }
        return arr;
    }
}
