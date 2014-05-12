package DataStructure;

import java.util.*;
import java.io.*;
import Features.*;

public class BandSection
{
    public class InstrumentChangeEvent
    {
        public int instrumentNumber = 0, barNumberOfOccurence = 0;
        public InstrumentChangeEvent(int instrumentNumber, int barNumberOfOccurence)
        {
            this.instrumentNumber = instrumentNumber;
            this.barNumberOfOccurence = barNumberOfOccurence;
        }
        public void setInstrumentNumber(int instrumentNumber)
        {
            this.instrumentNumber = instrumentNumber;
        }
    }
    Vector parts = new Vector();
    Vector instrumentChanges = new Vector();
    Vector oldInstrumentChanges = new Vector();
    public BandSection()
    {
        instrumentChanges.addElement(new InstrumentChangeEvent(0, 0));
        parts.addElement(new Part());
    }
    public BandSection(int numParts)
    {
        instrumentChanges.addElement(new InstrumentChangeEvent(0, 0));
        for(int i = 0; i < numParts; i++)
        {
            parts.addElement(new Part());
        }
    }
    public BandSection(int numParts, int numBars)
    {
        instrumentChanges.addElement(new InstrumentChangeEvent(0, 0));
        for(int i = 0; i < numParts; i++)
        {
            parts.addElement(new Part(numBars));
        }
    }
    public BandSection(int numParts, int numBars, int instrumentNumber)
    {
        instrumentChanges.addElement(new InstrumentChangeEvent(instrumentNumber, 0));
        for(int i = 0; i < numParts; i++)
        {
            parts.addElement(new Part(numBars));
        }
    }
    public void addPart(Part part)
    {
        parts.addElement(part);
    }
    public void addBarToEnd()
    {
        for(Enumeration e = parts.elements(); e.hasMoreElements();)
        {
            Part cur = (Part)e.nextElement();
            cur.addBarToEnd();
        }
    }
    public Vector getParts() { return parts; }
    public int getSize()     { return parts.size(); }
    public int getTotalBars()
    {
        if(!parts.isEmpty())
        {
            Part first = (Part)(parts.firstElement());
            return first.getTotalBars();
        }
        else return 0;
    }
    public InstrumentChangeEvent[] addInstrumentChangeEvent(int inst, int bar)
    {
        InstrumentChangeEvent ev = null;
        boolean clear = true;
        int index = 0;
        for(Enumeration e = instrumentChanges.elements(); e.hasMoreElements();)
        {
            ev = (InstrumentChangeEvent)e.nextElement();
            if(ev.barNumberOfOccurence == bar)
            {
                clear = false;
                break;
            }
            else if(ev.barNumberOfOccurence > bar) break;
            index++;
        }
        if(!clear) ev.setInstrumentNumber(inst);
        else
        {
            instrumentChanges.add(index, new InstrumentChangeEvent(inst, bar));
        }
        oldInstrumentChanges.removeAllElements();
        ev = null;
        int prevInst = -1;
        for(Enumeration e = instrumentChanges.elements(); e.hasMoreElements();)
        {
            ev = (InstrumentChangeEvent)e.nextElement();
            if(prevInst == ev.instrumentNumber)
            {
                prevInst = ev.instrumentNumber;
                instrumentChanges.removeElement(ev);
                oldInstrumentChanges.addElement(ev);
            }
            else prevInst = ev.instrumentNumber;
        }
        InstrumentChangeEvent[] arr = new InstrumentChangeEvent[oldInstrumentChanges.size()];
        int i = 0;
        for(Enumeration e = oldInstrumentChanges.elements(); e.hasMoreElements();)
        {
            arr[i] = (InstrumentChangeEvent)e.nextElement();
            i++;
        }
        return arr;
    }
    public int getInstrumentNumberAtBar(int barNumber)
    {// bar number is 0-based ---->>>> starting from the very beginning!!!!
        InstrumentChangeEvent ev = (InstrumentChangeEvent)instrumentChanges.firstElement();
        int barDifference = barNumber;
        for(Enumeration e = instrumentChanges.elements(); e.hasMoreElements();)
        {
            InstrumentChangeEvent cur = (InstrumentChangeEvent)e.nextElement();
            if((barDifference > barNumber - cur.barNumberOfOccurence)&&(barNumber >= cur.barNumberOfOccurence))
            {
                barDifference = barNumber - cur.barNumberOfOccurence;
                ev = cur;
            }
        }
        return ev.instrumentNumber;
    }
    public InstrumentChangeEvent getInstrumentChangeEventAtBar(int barNumber)
    {// bar number is 0-based ---->>>> starting from the very beginning!!!!
        InstrumentChangeEvent ev = (InstrumentChangeEvent)instrumentChanges.firstElement();
        int barDifference = barNumber;
        for(Enumeration e = instrumentChanges.elements(); e.hasMoreElements();)
        {
            InstrumentChangeEvent cur = (InstrumentChangeEvent)e.nextElement();
            if((barDifference > barNumber - cur.barNumberOfOccurence)&&(barNumber >= cur.barNumberOfOccurence))
            {
                barDifference = barNumber - cur.barNumberOfOccurence;
                ev = cur;
            }
        }
        return ev;
    }
    public Vector getInstrumentChangeEvents() { return instrumentChanges; }
    
    public void save(PrintStream stream, int indent, Vector refBars)
    {
        stream.println(MiscHelper.printBar(' ', indent)+"<section>");
        stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT)+"<instrumentChanges>");
        for(Enumeration e = instrumentChanges.elements(); e.hasMoreElements();)
        {
            InstrumentChangeEvent cur = (InstrumentChangeEvent)e.nextElement();
            stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT*2)+"<instrumentEvent>");
            stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT*3)+"<instrumentNumber>"+cur.instrumentNumber+"</instrumentNumber>");
            stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT*3)+"<barNumber>"+cur.barNumberOfOccurence+"</barNumber>");
            stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT*2)+"</instrumentEvent>");
        }
        stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT)+"</instrumentChanges>");
        stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT)+"<parts>");
        for(Enumeration e = parts.elements(); e.hasMoreElements();)
        {
            Part cur = (Part)e.nextElement();
            cur.save(stream, indent+Score.SAVE_INDENT*2, refBars);
        }
        stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT)+"</parts>");
        stream.println(MiscHelper.printBar(' ', indent)+"</section>");
    }
}
