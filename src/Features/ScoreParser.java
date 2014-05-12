package Features;

import DataStructure.*;
import NoteModifiers.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;

public class ScoreParser extends HandlerBase
{
    class NumberEvent
    {
        int val1, val2, bar;
        public NumberEvent(int val1, int val2, int bar)
        {
            this.val1 = val1;
            this.val2 = val2;
            this.bar = bar;
        }
        public void setValue1(int val1) { this.val1 = val1; }
        public void setValue2(int val2) { this.val2 = val2; }
        public void setValues(int val1, int val2)
        {
            this.val1 = val1;
            this.val2 = val2;
        }
    }
    Vector hierarchy;
    StringBuffer biff = null;
    
    Vector tempoEvents;
    Vector timeSigEvents;
    NumberEvent curEvent;
    Map refBars;
    Integer curkey;
    Vector sections;
    BandSection curSection;
    Part curPart;
    Bar curBar;
    Note curNote;
    Vector params;
    Vector curArray;
    public ScoreParser()
    {
        hierarchy = new Vector();
        
        tempoEvents = new Vector();
        timeSigEvents = new Vector();
        refBars = new HashMap();
        sections = new Vector();
        params = new Vector();
        curArray = new Vector();
    }
    public void startElement(String name, AttributeList attrs) throws SAXException
    {
        hierarchy.addElement(name);
        biff = new StringBuffer();
             if("tempoEvent".equals(name) || "instrumentEvent".equals(name) || "timeSigEvent".equals(name)) curEvent = new NumberEvent(0, 0, 0);
        else if("section".equals(name))
        {
            curSection = new BandSection();
            curSection.getParts().removeAllElements();
        }
        else if("part".equals(name)) curPart = new Part();
        else if("bar".equals(name))
        {
            for(int i = 0; i < attrs.getLength(); i++)
            {
                if("refIndex".equals(attrs.getName(i))) curkey = new Integer(attrs.getValue(i));
            }
            curBar = new Bar();
        }
        else if("note".equals(name))
        {
            double length = 0.25;
            int volume = 90;
            byte slurm = 0;
            for(int i = 0; i < attrs.getLength(); i++)
            {
                     if("length".equals(attrs.getName(i))) length = Double.parseDouble(attrs.getValue(i));
                else if("volume".equals(attrs.getName(i))) volume = Integer.parseInt(attrs.getValue(i));
                else if( "slurm".equals(attrs.getName(i))) slurm  = Byte.parseByte(attrs.getValue(i));
            }
            curNote = new Note(length, volume, slurm);
        }
        else if(hierarchy.size() > 2 && "modifiers".equals(hierarchy.elementAt(hierarchy.size()-2))) params.removeAllElements();
        else if("IntArray".equals(name)) curArray.removeAllElements();
    }
    public void endElement(String name) throws SAXException
    {
        if(biff != null) processChars(new String(biff));
        biff = null;
        hierarchy.removeElementAt(hierarchy.size()-1);
        
        if("tempoEvent".equals(name))
            tempoEvents.addElement(curEvent);
        else if("timeSigEvent".equals(name))
            timeSigEvents.addElement(curEvent);
        else if("instrumentEvent".equals(name))
            curSection.addInstrumentChangeEvent(curEvent.val1, curEvent.bar);
        else if("IntArray".equals(name))
        {
            int[] curArr = new int[curArray.size()];
            int i = 0;
            for(Enumeration en = curArray.elements(); en.hasMoreElements();)
            {
                Integer curInt = (Integer)en.nextElement();
                curArr[i] = curInt.intValue();
                i++;
            }
            params.addElement(curArr);
        }
        else if("note".equals(name)) curBar.addNote(curNote);
        else if("bar".equals(name))
        {
                 if("bars".equals(hierarchy.lastElement()))    curPart.addBar(curBar);
            else if("refBars".equals(hierarchy.lastElement()))
            {
                curBar.byRef = true;
                curBar.overrideRefCount(0);
                refBars.put(curkey, curBar);
            }
        }
        //refBar objects are dealt in Chars
        else if("part".equals(name))    curSection.addPart(curPart);
        else if("section".equals(name)) sections.addElement(curSection);
        
        else if(hierarchy.size() > 0 && "modifiers".equals(hierarchy.lastElement()))
        {
            try
            {
                Class curClass = Class.forName(name);
                Constructor constructor = curClass.getConstructors()[0];
                curNote.addModifier((NoteEvents)constructor.newInstance(params.toArray()));
            }
            catch(Exception ex) {}
        }
    }
    public void characters(char[] buf, int offset, int len) throws SAXException
    {
        if(biff != null) biff.append(buf, offset, len);
    }
    public void processChars(String chars) throws SAXException
    {
        if("tempoValue".equals(hierarchy.lastElement()) || "instrumentNumber".equals(hierarchy.lastElement()))
            curEvent.val1 = Integer.parseInt(chars);
        else if("top".equals(hierarchy.lastElement()))
            curEvent.val1 = Integer.parseInt(chars);
        else if("bottom".equals(hierarchy.lastElement()))
            curEvent.val2 = Integer.parseInt(chars);
        else if("barNumber".equals(hierarchy.lastElement()))
            curEvent.bar = Integer.parseInt(chars);
        else if("refBar".equals(hierarchy.lastElement()))
        {
            Bar newBar = (Bar)refBars.get(new Integer(chars));
            newBar.incRefBarCount(1);
            curPart.addBar(newBar);
        }
        else if("chordNote".equals(hierarchy.lastElement()))
            curNote.addNote(Integer.parseInt(chars));
        else if("Boolean".equals(hierarchy.lastElement()) && "modifiers".equals(hierarchy.elementAt(hierarchy.size()-3)))
            params.addElement(new Boolean(chars));
        else if("Integer".equals(hierarchy.lastElement()) && "modifiers".equals(hierarchy.elementAt(hierarchy.size()-3)))
            params.addElement(new Integer(chars));
        else if("Double".equals(hierarchy.lastElement()) && "modifiers".equals(hierarchy.elementAt(hierarchy.size()-3)))
            params.addElement(new Double(chars));
        else if("String".equals(hierarchy.lastElement()) && "modifiers".equals(hierarchy.elementAt(hierarchy.size()-3)))
            params.addElement(new String(chars));
        else if("Integer".equals(hierarchy.lastElement()) && "IntArray".equals(hierarchy.elementAt(hierarchy.size()-2)) && "modifiers".equals(hierarchy.elementAt(hierarchy.size()-4)))
            curArray.addElement(new Integer(chars));
    }
    
    public void tossEx(String message) throws SAXException
    {
        throw new SAXException(message);
    }
    //------------------------------------
    public Score getScore()
    {
        BandSection[] sect = new BandSection[sections.size()];
        int i = 0;
        for(Enumeration e = sections.elements(); e.hasMoreElements();)
        {
            sect[i] = (BandSection)e.nextElement();
            i++;
        }
        Score score = new Score(sect);
        for(Enumeration e = tempoEvents.elements(); e.hasMoreElements();)
        {
            NumberEvent cur = (NumberEvent)e.nextElement();
            score.addTempoChangeEvent(cur.val1, cur.bar);
        }
        for(Enumeration e = timeSigEvents.elements(); e.hasMoreElements();)
        {
            NumberEvent cur = (NumberEvent)e.nextElement();
            score.addTimeSignatureChangeEvent(cur.val1, cur.val2, cur.bar);
        }
        
        return score;
    }
}
