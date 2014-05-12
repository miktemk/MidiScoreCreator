package DataStructure;

import java.util.*;
import java.awt.*;
import java.lang.*;
import java.io.*;
import Features.*;

public class Bar implements Cloneable
{
    Vector notes = new Vector();
    //public int topNumber = 4, bottomNumber = 4;
    public boolean byRef = false;
    int refs = 1;
    public Bar(){}
    /*public Bar(int topNumber, int bottomNumber)
    {
        this.topNumber = topNumber;
        this.bottomNumber = bottomNumber;
    }*/
    /**@param maxLength is the length of this bar according to score and its events*/
    public void addNote(Note note, int index, double maxLength)
    {//inserts a brand new note
        try
        {
            if(getCurLength() + note.getLength() <= maxLength) notes.add(index, note);
        }
        catch(Exception e){}
    }
    public void addNote(Note note)
    {//inserts a brand new note
        notes.addElement(note);
    }
    public void addNoteToAChord(int note, int index)
    {//adds a note to an existing chords
        try
        {
            Note cur = (Note)notes.elementAt(index);
            cur.addNote(note);
        }
        catch(Exception e){}
    }
    public void removeNote(int index)
    {//index is 0-based
        try
        {
            notes.remove(index);
        }
        catch(Exception e){}
    }
    public void clearBar()
    {//removes all notes
        notes.removeAllElements();
    }
    public Vector getNotes()     { return notes; }
    /*public int getTopNumber()    { return topNumber; }
    public int getBottomNumber() { return bottomNumber; }
    public double getMaxLength() { return (double)topNumber/bottomNumber; }*/
    public double getCurLength()
    {
        double l = 0;
        for(Enumeration e = notes.elements(); e.hasMoreElements();)
        {
            Note cur = (Note)e.nextElement();
            l += cur.getLength();
        }
        return l;
    }
    /*public double getRemainder()
    {
        return getMaxLength()-getCurLength();
    }
    public void setBarNumbers(int topNumber, int bottomNumber)
    {
        this.topNumber = topNumber;
        this.bottomNumber = bottomNumber;
        trim();
    }//*/
    /** gets rid of all notes at the end which don't fit into the bar's time dig */
    /*public void trim()
    {
        while(getCurLength() > getMaxLength())
        {
            notes.removeElementAt(notes.size()-1);
        }
    }*/
    /*public boolean isFull()
    {
        return (getCurLength() == getMaxLength());
    }*/
    public Note getNoteAt(int i)
    {
        try
        {
            return (Note)(notes.elementAt(i));
        }
        catch(Exception e) { return null; }
    }
    //DANGER: only use in extreme cases!!!!!!
    public void overrideRefCount(int newCount)
    {
        refs = newCount;
    }
    public void incRefBarCount(int incr)
    {//this means that another bar Byref was added/removed to/from all of 'em :)
        //if(!byRef) refs = 1; //it will now be BY REF!!!
        byRef = true;
        refs += incr; //and theres 2 (3, 4.. n) of em now, if incr > 0;
        if(refs <= 1) //if incr < 0, well...
        {//there is the only 1 (or less) THIS ref bars left
            if(refs < 0) refs = 0;
            byRef = false;
        }
    }
    public Bar makeCopy()
    {
        Bar newBar = new Bar();
        for(Enumeration e = notes.elements(); e.hasMoreElements();)
        {
            Note curNote = (Note)e.nextElement();
            newBar.addNote(curNote.makeCopy());
        }
        return newBar;
    }
    public void resetBar(Bar b)
    {
        this.clearBar();
        for(Enumeration e = b.getNotes().elements(); e.hasMoreElements();)
        {
            Note curNote = (Note)e.nextElement();
            this.addNote(curNote.makeCopy());
        }
    }
    public void resetBarRef(Bar b)
    {
        this.clearBar();
        for(Enumeration e = b.getNotes().elements(); e.hasMoreElements();)
        {
            Note curNote = (Note)e.nextElement();
            this.addNote(curNote);
        }
    }
    public void transposeBar(int delta)
    {
        for(Enumeration e = notes.elements(); e.hasMoreElements();)
        {
            Note curNote = (Note)e.nextElement();
            for(Enumeration e2 = curNote.getNotes().elements(); e2.hasMoreElements();)
            {
                Note.MiniNote curMiniNote = (Note.MiniNote)e2.nextElement();
                curMiniNote.setTranspose(delta);
            }
        }
    }
    public int getExtremeTransposition(int delta)
    {
        for(Enumeration e = notes.elements(); e.hasMoreElements();)
        {
            Note curNote = (Note)e.nextElement();
            int newD = curNote.getExtremeTransposition(delta);
                 if(delta > 0 && delta > newD) delta = newD;
            else if(delta < 0 && delta < newD) delta = newD;
        }
        return delta;
    }
    
    public void save(PrintStream stream, int indent, int refIndex)
    {
        if(byRef) stream.println(MiscHelper.printBar(' ', indent)+"<bar refIndex=\""+refIndex+"\">");
        else      stream.println(MiscHelper.printBar(' ', indent)+"<bar>");
        stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT)+"<notes>");
        for(Enumeration e = notes.elements(); e.hasMoreElements();)
        {
            Note curNote = (Note)e.nextElement();
            curNote.save(stream, indent+Score.SAVE_INDENT*2);
        }
        stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT)+"</notes>");
        stream.println(MiscHelper.printBar(' ', indent)+"</bar>");
    }
//-----------Graphical features--------------- THIS IS USELESS JUNK :(
    /*public void drawYourself(Graphics2D g, int lineNumber, int barNumber)
    {
        
    }
        public void drawNote(int noteNumber, int barNumber, double posit, int lineNumber, Graphics2D g)
        {//ok   line Number is 1 based, barNumber is 1-based
            double barWidth = (getWidth()-leftSpace-scrollWidth)/numOfScreenBars;
            double noteX = leftSpace + (barNumber-barPosit-1)*barWidth + barWidth*posit;
            double barHeight = (getHeight()-scrollWidth)/numOfScreenParts;
            boolean[] isAccidentaled = {false, true, false, true, false, false, true, false, true, false, true, false};
            double staffWidth = (barHeight/((posit(MaxNote) - posit(MinNote)) + 1));
            double LineY = (lineNumber-linePosit)*barHeight - (posit(noteNumber)-posit(MinNote) + 0.5)*staffWidth;
            //extra linez
            g.setColor(Color.black);
            if(noteNumber < 71)
            {
                int totalLinez = (posit(71) - posit(noteNumber))/2;
                for(int i = 0; i < totalLinez; i++)
                {
                    g.draw(new Line2D.Double(noteX, NoteY(67, lineNumber-linePosit)+i*2*staffWidth, noteX+staffWidth*3, NoteY(67, lineNumber-linePosit)+i*2*staffWidth));
                }
                //g.drawString(""+totalLinez,50,(int)LineY+5);
            }
            else
            {
                int totalLinez = (posit(noteNumber) - posit(71))/2;
                for(int i = 0; i < totalLinez; i++)
                {
                    g.draw(new Line2D.Double(noteX, NoteY(74, lineNumber-linePosit)-i*2*staffWidth, noteX+staffWidth*3, NoteY(74, lineNumber-linePosit)-i*2*staffWidth));
                }
                //g.drawString(""+totalLinez,50,(int)LineY+5);
            }
            g.setColor(Color.blue);
            // body
            g.draw(new Ellipse2D.Double(noteX, LineY-staffWidth, staffWidth*3, staffWidth*2));
            //stem
            if(noteNumber < 71) g.draw(new Line2D.Double(noteX+staffWidth*3, LineY, noteX+staffWidth*3, LineY - 6*staffWidth));
            else                g.draw(new Line2D.Double(noteX, LineY, noteX, LineY + 6*staffWidth));
            
            if(isAccidentaled[noteNumber % 12])// accidental
            {
                g.drawString("#",(int)noteX-9,(int)LineY+5);
            }
            g.setColor(Color.black);
        }//*/
}
