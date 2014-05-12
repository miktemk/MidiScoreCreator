package Misc;

import org.xml.sax.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

public class ScaleLoadTest
{
    static class MyParser extends HandlerBase
    {
        Vector values;
        String scaleName = "";
        Vector hierarchy;
        StringBuffer biff = null;
        public MyParser()
        {
            values = new Vector();
            hierarchy = new Vector();
        }
        public void startDocument() throws SAXException
        { //System.out.println("start");
        }
        public void endDocument() throws SAXException
        { //System.out.println("start");
        }
        public void startElement(String name, AttributeList attrs) throws SAXException
        {
            //System.out.println("Start Element: "+name);
            hierarchy.addElement(name);
            //if(biff != null) processChars(new String(biff))
            biff = new StringBuffer();
        }
        public void endElement(String name) throws SAXException
        {
            //System.out.println("End Element: "+name);
            if(biff != null) processChars(new String(biff));
            biff = null;
            hierarchy.removeElementAt(hierarchy.size()-1);
        }
        public void characters(char[] buf, int offset, int len) throws SAXException
        {
            //System.out.println("chars: "+new String(buf, offset, len));
            if(biff != null) biff.append(buf, offset, len);
        }
        public void processChars(String chars)
        {
            if(hierarchy.size() == 2)
            {
                String tag = (String)hierarchy.lastElement();
                if(tag == "name")
                {
                    scaleName = chars;
                }
                else if(tag == "note")
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
            for(Enumeration e = values.elements(); e.hasMoreElements();)
            {
                arr[i] = ((Integer)e.nextElement()).intValue();
                i++;
            }
            return arr;
        }
    }
    public static void main(String[] args)
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        MyParser handler = new MyParser();
        try
        {
            SAXParser parser = factory.newSAXParser();
            parser.parse(new File("NoteModifiers/Scales/chromatic.scl"), handler);
        }
        catch(Exception e)
        {
            System.out.println("no!!!!!! exception: "+e);
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        System.out.println(handler.getName());
        int[] scale = handler.getScale();
        for(int i = 0; i < scale.length; i++)
        {
            System.out.println(scale[i]);
        }
    }
}
