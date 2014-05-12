package DataStructure;

import java.util.*;
import java.lang.*;
import java.io.*;
import Features.*;

public class Score
{
    public class TempoChangeEvent
    {
        public int tempo, barNumberOfOccurence;
        public TempoChangeEvent(int tempo, int barNumberOfOccurence)
        {
            this.tempo = tempo;
            this.barNumberOfOccurence = barNumberOfOccurence;
        }
        public void setTempo(int tempo) { this.tempo = tempo; }
    }
    public class TimeSignatureChangeEvent
    {
        public int top, bottom, barNumber;
        public TimeSignatureChangeEvent(int top, int bottom, int barNumber)
        {
            this.top = top;
            this.bottom = bottom;
            this.barNumber = barNumber;
        }
        public void setBarNumbers(int top, int bottom)
        {
            this.top = top;
            this.bottom = bottom;
        }
    }
    public static final int SAVE_INDENT = 2;
    BandSection[] sections;
    Vector tempoChanges = new Vector();
    Vector timeSigChanges = new Vector();
    Vector old = new Vector();
    private Vector refBars_onSave = new Vector();
    /**
     * Used for blank new Score creation
     */
    public Score(int maxSections, int numBars)
    {
        sections = new BandSection[maxSections];
        for(int i = 0; i < sections.length; i++)
        {
            int id = (((int)(Math.random()*8))+8*i)%128;
            sections[i] = new BandSection(1, numBars, id);
            //System.out.println(""+id);
        }
        tempoChanges.addElement(new TempoChangeEvent(120, 0));
        timeSigChanges.addElement(new TimeSignatureChangeEvent(4, 4, 0));
    }
    /**
     * Used for loading the Score from file
     */
    public Score(BandSection[] sections)
    {
        this.sections = sections;
        tempoChanges.addElement(new TempoChangeEvent(120, 0));
    }
    /**
     * Used for loading the Score from file
     */
    public Score(BandSection[] sections, Vector tempoChanges, Vector timeSigChanges)
    {
        this.sections = sections;
        this.tempoChanges = tempoChanges;
        this.timeSigChanges = timeSigChanges;
        if(tempoChanges.size() < 1)
        {//this segment should NEVER EXECUTE
            tempoChanges.addElement(new TempoChangeEvent(120, 0));
            System.out.println("HEY!!!, Mikhail, your program is skrewed up....");
            System.out.println("It should NEVER have printed this!!!");
        }
    }
    public BandSection[] getSections()         { return sections; }
    public BandSection   getSection(int index) { return sections[index]; }
    public int getTotalParts()
    {
        int totalParts = 0;
        for(int i = 0; i < sections.length; i++)
        {
            totalParts += sections[i].getSize();
        }
        return totalParts;
    }
    public int getTotalBars()
    {
        if(sections != null)
        {
            return sections[0].getTotalBars();
        }
        else return 0;
    }
    public Part getPart(int index)
    {//index is 0-based!!!!!!!!!
        int i = 0, partCount = 0;
        for(i = 0; i < sections.length; i++)
        {
            partCount += sections[i].getSize();
            if(index+1 <= partCount) break;
        }
        //System.out.println("part Count  "+partCount+"   index = "+index+"  sections[i].getSize() = "+sections[i].getSize());
        return (Part)(sections[i].getParts().elementAt(index - partCount + sections[i].getSize()));
    }
    public Part getLastPart()
    {
        BandSection lastSection = sections[sections.length - 1];
        return (Part)lastSection.getParts().lastElement();
    }
    public void addBarToEnd()
    {
        for(int i = 0; i < sections.length; i++)
        {
            sections[i].addBarToEnd();
        }
    }
    public void addBarToEndExceptForOneLine(int lineNumber)
    // line number is 0 based;
    {
        int curLine = 0;
        for(int i = 0; i < sections.length; i++)
        {
            for(Enumeration e = sections[i].parts.elements(); e.hasMoreElements();)
            {
                Part curPart = (Part)e.nextElement();
                if(curLine != lineNumber) curPart.addBarToEnd();
                curLine++;
            }
        }
    }
    public void addBarToPart(int index)
    {
        int i = 0, partCount = 0;
        for(i = 0; i < sections.length; i++)
        {
            partCount += sections[i].getSize();
            if(index+1 <= partCount) break;
        }
        try
        {
            Part p = (Part)(sections[i].getParts().elementAt(index - partCount + sections[i].getSize()));
            p.addBarToEnd();
        }
        catch(Exception e){}
    }
    /**
     *Inserts blank bars into entire score before the bar at a specified index
     */
    public void insertBars(int index, int numBars)
    {
        for(int i = 0; i < sections.length; i++)
        {
            for(Enumeration e = sections[i].parts.elements(); e.hasMoreElements();)
            {
                Part curPart = (Part)e.nextElement();
                Vector curBarsVector = curPart.getBars();
                Bar indexBar = null;
                if(index >= curBarsVector.size()) indexBar = (Bar)curBarsVector.elementAt(index);
                else                              indexBar = (Bar)curBarsVector.lastElement();
                for(int j = 0; j < numBars; j++)
                {
                    curBarsVector.insertElementAt(new Bar(), index);
                }
            }
        }
    }
    /*public int setBarNumbers(int top, int bottom, int atBar)
    {
        int extent = 0;
        for(int i = 0; i < sections.length; i++)
        {
            for(Enumeration e = sections[i].parts.elements(); e.hasMoreElements();)
            {
                int index = 0;
                int prevTop = 0, prevBot = 0;
                Part curPart = (Part)e.nextElement();
                extent = 0;
                for(Enumeration e2 = curPart.bars.elements(); e2.hasMoreElements();)
                {
                    Bar curBar = (Bar)e2.nextElement();
                    index++;
                    if(index == atBar)
                    {
                        prevTop = curBar.topNumber;
                        prevBot = curBar.bottomNumber;
                    }
                    if((prevTop == curBar.topNumber)&&(prevBot == curBar.bottomNumber))
                    {
                        curBar.setBarNumbers(top, bottom);
                        extent++;
                    }
                    else if((index > atBar)) break;
                }
            }
        }
        return extent;
    }
    public void setBarNumbersExtent(int top, int bottom, int atBar, int extent)
    {
        for(int i = 0; i < sections.length; i++)
        {
            for(Enumeration e = sections[i].parts.elements(); e.hasMoreElements();)
            {
                int index = 0;
                boolean starta = false;
                Part curPart = (Part)e.nextElement();
                for(Enumeration e2 = curPart.bars.elements(); e2.hasMoreElements();)
                {
                    Bar curBar = (Bar)e2.nextElement();
                    index++;
                    if(index == atBar) starta = true;
                    if(starta) curBar.setBarNumbers(top, bottom);
                    if(index >= atBar+extent-1) break;
                }
            }
        }
    }*/
    
    public TempoChangeEvent[] addTempoChangeEvent(int tempo, int bar)
    {
        TempoChangeEvent ev = null;
        boolean clear = true;
        int index = 0;
        for(Enumeration e = tempoChanges.elements(); e.hasMoreElements();)
        {
            ev = (TempoChangeEvent)e.nextElement();
            if(ev.barNumberOfOccurence == bar)
            {
                clear = false;
                break;
            }
            else if(ev.barNumberOfOccurence > bar) break;
            index++;
        }
        if(!clear) ev.setTempo(tempo);
        else
        {
            tempoChanges.add(index, new TempoChangeEvent(tempo, bar));
        }
        old.removeAllElements();
        ev = null;
        int prevTempo = -1;
        for(Enumeration e = tempoChanges.elements(); e.hasMoreElements();)
        {
            ev = (TempoChangeEvent)e.nextElement();
            if(prevTempo == ev.tempo)
            {
                prevTempo = ev.tempo;
                tempoChanges.removeElement(ev);
                old.addElement(ev);
            }
            else prevTempo = ev.tempo;
        }
        TempoChangeEvent[] arr = new TempoChangeEvent[old.size()];
        int i = 0;
        for(Enumeration e = old.elements(); e.hasMoreElements();)
        {
            arr[i] = (TempoChangeEvent)e.nextElement();
            i++;
        }
        return arr;
    }
    /**Computes the tempo at the specified bar*/
    public int getTempoAtBar(int barNumber)
    {// bar number is 0-based ---->>>> starting from the very beginning!!!!
        TempoChangeEvent ev = (TempoChangeEvent)tempoChanges.firstElement();
        int barDifference = barNumber;
        for(Enumeration e = tempoChanges.elements(); e.hasMoreElements();)
        {
            TempoChangeEvent cur = (TempoChangeEvent)e.nextElement();
            if((barDifference > barNumber - cur.barNumberOfOccurence)&&(barNumber >= cur.barNumberOfOccurence))
            {
                barDifference = barNumber - cur.barNumberOfOccurence;
                ev = cur;
            }
        }
        return ev.tempo;
    }
    public int getNextTempoAtBar(int barNumber)
    {// bar number is 0-based ---->>>> starting from the very beginning!!!!
        TempoChangeEvent ev = (TempoChangeEvent)tempoChanges.firstElement();
        int barDifference = barNumber;
        for(Enumeration e = tempoChanges.elements(); e.hasMoreElements();)
        {
            TempoChangeEvent cur = (TempoChangeEvent)e.nextElement();
            if((barDifference > cur.barNumberOfOccurence - barNumber)&&(barNumber <= cur.barNumberOfOccurence))
            {
                barDifference = cur.barNumberOfOccurence - barNumber;
                ev = cur;
            }
        }
        return ev.tempo;
    }
    public Vector getTempoChangeEvents() { return tempoChanges; }
    
    public Vector getTimeSignatureChangeEvents() { return timeSigChanges; }
    /**Returns an array of TimeSignatureChangeEvent objects that it has deleted in the process*/
    public TimeSignatureChangeEvent[] addTimeSignatureChangeEvent(int top, int bottom, int bar)
    {
        TimeSignatureChangeEvent ev = null;
        boolean clear = true;
        int index = 0;
        for(Enumeration e = timeSigChanges.elements(); e.hasMoreElements();)
        {
            ev = (TimeSignatureChangeEvent)e.nextElement();
            if(ev.barNumber == bar)
            {
                clear = false;
                break;
            }
            else if(ev.barNumber > bar) break;
            index++;
        }
        if(!clear) ev.setBarNumbers(top, bottom);
        else
        {
            timeSigChanges.add(index, new TimeSignatureChangeEvent(top, bottom, bar));
        }
        old.removeAllElements();
        ev = null;
        int prevTop = -1;
        int prevBottom = -1;
        for(Enumeration e = timeSigChanges.elements(); e.hasMoreElements();)
        {
            ev = (TimeSignatureChangeEvent)e.nextElement();
            if(prevTop != ev.top || prevBottom != ev.bottom)
            {
                prevTop = ev.top;
                prevBottom = ev.bottom;
                tempoChanges.removeElement(ev);
                old.addElement(ev);
            }
            else
            {
                prevTop = ev.top;
                prevBottom = ev.bottom;
            }
        }
        TimeSignatureChangeEvent[] arr = new TimeSignatureChangeEvent[old.size()];
        int i = 0;
        for(Enumeration e = old.elements(); e.hasMoreElements();)
        {
            arr[i] = (TimeSignatureChangeEvent)e.nextElement();
            i++;
        }
        return arr;
    }
    /**Computes the time signature at the specified bar*/
    public TimeSignatureChangeEvent getTimeSignatureAtBar(int barNumber)
    {// bar number is 0-based ---->>>> starting from the very beginning!!!!
        TimeSignatureChangeEvent ev = (TimeSignatureChangeEvent)timeSigChanges.firstElement();
        int barDifference = barNumber;
        for(Enumeration e = timeSigChanges.elements(); e.hasMoreElements();)
        {
            TimeSignatureChangeEvent cur = (TimeSignatureChangeEvent)e.nextElement();
            if((barDifference > barNumber - cur.barNumber)&&(barNumber >= cur.barNumber))
            {
                barDifference = barNumber - cur.barNumber;
                ev = cur;
            }
        }
        return ev;
    }
    public double getBarMaxLength(int barNumber)
    {// bar number is 0-based ---->>>> starting from the very beginning!!!!
        TimeSignatureChangeEvent ev = (TimeSignatureChangeEvent)timeSigChanges.firstElement();
        int barDifference = barNumber;
        for(Enumeration e = timeSigChanges.elements(); e.hasMoreElements();)
        {
            TimeSignatureChangeEvent cur = (TimeSignatureChangeEvent)e.nextElement();
            if((barDifference > barNumber - cur.barNumber)&&(barNumber >= cur.barNumber))
            {
                barDifference = barNumber - cur.barNumber;
                ev = cur;
            }
        }
        return (double)ev.top/ev.bottom;
    }
//---------------extra worthless methods---------------------------------
    public BandSection getSectionAtPart(int index)
    {//index is 0-based!!!!!!!!!
        int i = 0, partCount = 0;
        for(i = 0; i < sections.length; i++)
        {
            partCount += sections[i].getSize();
            if(index+1 <= partCount) break;
        }
        return (BandSection)(sections[i]);
    }
    public int getSectionIndexAtPart(int index)
    {//index is 0-based!!!!!!!!!
        int i = 0, partCount = 0;
        for(i = 0; i < sections.length; i++)
        {
            partCount += sections[i].getSize();
            if(index+1 <= partCount) break;
        }
        return i;
    }//*/
//--------NOW this one is A REAL SWEETIE!!!!!!!!!!!!!---------------
    public void save(PrintStream stream)
    {
        stream.println("<?xml version=\"1.0\"?>");
        stream.println("<score>");
        stream.println(MiscHelper.printBar(' ', SAVE_INDENT)+"<tempo>");
        for(Enumeration e = tempoChanges.elements(); e.hasMoreElements();)
        {
            TempoChangeEvent cur = (TempoChangeEvent)e.nextElement();
            stream.println(MiscHelper.printBar(' ', SAVE_INDENT*2)+"<tempoEvent>");
            stream.println(MiscHelper.printBar(' ', SAVE_INDENT*3)+"<tempoValue>"+cur.tempo+"</tempoValue>");
            stream.println(MiscHelper.printBar(' ', SAVE_INDENT*3)+"<barNumber>"+cur.barNumberOfOccurence+"</barNumber>");
            stream.println(MiscHelper.printBar(' ', SAVE_INDENT*2)+"</tempoEvent>");
        }
        stream.println(MiscHelper.printBar(' ', SAVE_INDENT)+"</tempo>");
        stream.println(MiscHelper.printBar(' ', SAVE_INDENT)+"<timeSignatures>");
        for(Enumeration e = timeSigChanges.elements(); e.hasMoreElements();)
        {
            TimeSignatureChangeEvent cur = (TimeSignatureChangeEvent)e.nextElement();
            stream.println(MiscHelper.printBar(' ', SAVE_INDENT*2)+"<timeSigEvent>");
            stream.println(MiscHelper.printBar(' ', SAVE_INDENT*3)+"<top>"+cur.top+"</top>");
            stream.println(MiscHelper.printBar(' ', SAVE_INDENT*3)+"<bottom>"+cur.bottom+"</bottom>");
            stream.println(MiscHelper.printBar(' ', SAVE_INDENT*3)+"<barNumber>"+cur.barNumber+"</barNumber>");
            stream.println(MiscHelper.printBar(' ', SAVE_INDENT*2)+"</timeSigEvent>");
        }
        stream.println(MiscHelper.printBar(' ', SAVE_INDENT)+"</timeSignatures>");
        stream.println(MiscHelper.printBar(' ', SAVE_INDENT)+"<refBars>");
        populateRefVector();
        for(Enumeration en = refBars_onSave.elements(); en.hasMoreElements();)
        {
            Bar curBar = (Bar)en.nextElement();
            curBar.save(stream, 2*SAVE_INDENT, refBars_onSave.indexOf(curBar));
        }
        stream.println(MiscHelper.printBar(' ', SAVE_INDENT)+"</refBars>");
        stream.println(MiscHelper.printBar(' ', SAVE_INDENT)+"<sections>");
        for(int i = 0; i < sections.length; i++)
        {
            sections[i].save(stream, 2*SAVE_INDENT, refBars_onSave);
        }
        stream.println(MiscHelper.printBar(' ', SAVE_INDENT)+"</sections>");
        stream.println("</score>");
    }
//--------------Other helper method(s)--------------------------
    private void populateRefVector()
    {
        refBars_onSave.removeAllElements();
        for(int i = 0; i < sections.length; i++)
        {
            for(Enumeration e = sections[i].getParts().elements(); e.hasMoreElements();)
            {
                Part curPart = (Part)e.nextElement();
                for(Enumeration en = curPart.getBars().elements(); en.hasMoreElements();)
                {
                    Bar curBar = (Bar)en.nextElement();
                    if(curBar.byRef && !refBars_onSave.contains(curBar))
                        refBars_onSave.addElement(curBar);
                }
            }
        }
    }
}
