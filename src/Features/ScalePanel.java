package Features;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.midi.*;

import java.awt.geom.*;
import java.util.*;
import GUI.*;

public class ScalePanel extends JPanel implements MouseListener, MouseMotionListener
{
    class Notik
    {
        int n;
        public Notik(int n) { this.n = n; }
    }
    final int MinNote = 40,
              MaxNote = 100;
    final double rightSpc = 100;
    int curNote = 0,
        prevNote = -1;
    int curIndex = 0;
    Vector notes;
    ScaleMainFrame smf;
    PianoKeyB kb;
    public MidiChannel[] channels = null;
    public Instrument piano = null;
    public ScalePanel(ScaleMainFrame smf, PianoKeyB kb)
    {
        this.smf = smf;
        this.kb = kb;
        notes = new Vector();
        addMouseMotionListener(this);
        addMouseListener(this);
    }
    public int posit(int noteNumber)
    {
        int[] notePosit = {0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6};
        return ((int)(noteNumber/12))*7 + notePosit[noteNumber % 12];
    }
    public int pos(int y)//Note Position given the Y
    {
        return (int)(y/(getHeight()/(posit(MaxNote) - posit(MinNote) + 1))) + posit(MinNote);
    }
    public double NoteY(int noteNumber)
    {
        double staffWidth = (getHeight()/((posit(MaxNote) - posit(MinNote)) + 1));
        return getHeight() - (posit(noteNumber)-posit(MinNote) + 0.5)*staffWidth;
    }
    public int getNoteNumber(int y)
    {
        //Here y is measured from the BOTTOM
        int[] notes = {0, 2, 4, 5, 7, 9, 11};
        boolean[] noteHasBlackSharp = {true, true, false, true, true, true, false};
        //int y = getHeight() - e.getY();
        int NoteNumber = notes[pos(y) % 7] + 12*((int)(pos(y)/7));
        double staffWidth = (getHeight()/(posit(MaxNote) - posit(MinNote) + 1));
        if((y % staffWidth > staffWidth*2/3)&&(noteHasBlackSharp[pos(y) % 7] ))
        { NoteNumber++; }
        return NoteNumber;
    }
    public void lineThroughNoteNumber(int noteNumber, Graphics2D g)
    {
        double staffWidth = (getHeight()/((posit(MaxNote) - posit(MinNote)) + 1));
        double LineY = getHeight() - (posit(noteNumber)-posit(MinNote) + 0.5)*staffWidth;
        g.draw(new Line2D.Double(0, LineY, getWidth(), LineY));
    }
    public void drawCurNote(int noteNumber, int noteIndex, Graphics2D g)
    {
        boolean[] isAccidentaled = {false, true, false, true, false, false, true, false, true, false, true, false};
        double staffWidth = (getHeight()/((posit(MaxNote) - posit(MinNote)) + 1));
        double LineY = getHeight() - (posit(noteNumber)-posit(MinNote) + 0.5)*staffWidth;
        double l = getWidth()-30-3*staffWidth;
        double noteX = 30+3*staffWidth + (noteIndex+0.5)*(l-rightSpc)/(notes.size()+1);
        //extra linez
        g.setColor(Color.black);
        if(noteNumber < 71)
        {
            int totalLinez = (posit(71) - posit(noteNumber))/2;
            for(int i = 0; i < totalLinez; i++)
            {
                g.draw(new Line2D.Double(noteX, NoteY(67)+i*2*staffWidth, noteX+staffWidth*3, NoteY(67)+i*2*staffWidth));
            }
            //g.drawString(""+totalLinez,50,(int)LineY+5);
        }
        else
        {
            int totalLinez = (posit(noteNumber) - posit(71))/2;
            for(int i = 0; i < totalLinez; i++)
            {
                g.draw(new Line2D.Double(noteX, NoteY(74)-i*2*staffWidth, noteX+staffWidth*3, NoteY(74)-i*2*staffWidth));
            }
            //g.drawString(""+totalLinez,50,(int)LineY+5);
        }
        g.setColor(Color.magenta);
        // body
        g.draw(new Ellipse2D.Double(noteX, LineY-staffWidth, staffWidth*3, staffWidth*2));
        //stem
        if(noteNumber < 71) g.draw(new Line2D.Double(noteX+staffWidth*3, LineY, noteX+staffWidth*3, LineY - 6*staffWidth));
        else                g.draw(new Line2D.Double(noteX, LineY, noteX, LineY + 6*staffWidth));
        
        if(isAccidentaled[noteNumber % 12])// accidental
        {
            g.drawString("#", (int)noteX-10, (int)LineY+5);
        }
    }
    public void drawNote(int noteNumber, int noteIndex, Graphics2D g)
    {
        boolean[] isAccidentaled = {false, true, false, true, false, false, true, false, true, false, true, false};
        double staffWidth = (getHeight()/((posit(MaxNote) - posit(MinNote)) + 1));
        double LineY = getHeight() - (posit(noteNumber)-posit(MinNote) + 0.5)*staffWidth;
        double l = getWidth()-30-3*staffWidth;
        double noteX = 30+3*staffWidth + (noteIndex+0.5)*(l-rightSpc)/(notes.size()+1);
        //extra linez
        g.setColor(Color.black);
        if(noteNumber < 71)
        {
            int totalLinez = (posit(71) - posit(noteNumber))/2;
            for(int i = 0; i < totalLinez; i++)
            {
                g.draw(new Line2D.Double(noteX, NoteY(67)+i*2*staffWidth, noteX+staffWidth*3, NoteY(67)+i*2*staffWidth));
            }
        }
        else
        {
            int totalLinez = (posit(noteNumber) - posit(71))/2;
            for(int i = 0; i < totalLinez; i++)
            {
                g.draw(new Line2D.Double(noteX, NoteY(74)-i*2*staffWidth, noteX+staffWidth*3, NoteY(74)-i*2*staffWidth));
            }
        }
        g.setColor(Color.blue);
        // body
        g.fill(new Ellipse2D.Double(noteX, LineY-staffWidth, staffWidth*3, staffWidth*2));
        //stem
        if(noteNumber < 71) g.draw(new Line2D.Double(noteX+staffWidth*3, LineY, noteX+staffWidth*3, LineY - 6*staffWidth));
        else                g.draw(new Line2D.Double(noteX, LineY, noteX, LineY + 6*staffWidth));
        
        if(isAccidentaled[noteNumber % 12])// accidental
        {
            g.drawString("#", (int)noteX-10, (int)LineY+5);
        }
    }
    public void drawTrebleCleff(Graphics2D g)
    {
        final double x = 15;
        double staffWidth = (getHeight()/((posit(MaxNote) - posit(MinNote)) + 1));
        double trebY = getHeight() - (posit(67)-posit(MinNote) + 0.5)*staffWidth;
        Arc2D.Double arc1 = new Arc2D.Double(x, trebY-2*staffWidth, 2*staffWidth, 4*staffWidth, 0, 180, Arc2D.OPEN);
        Arc2D.Double arc2 = new Arc2D.Double(x-staffWidth, trebY-2*staffWidth, 3*staffWidth, 4*staffWidth, 180, 180, Arc2D.OPEN);
        CubicCurve2D.Double curve = new CubicCurve2D.Double(x-staffWidth, trebY, x-staffWidth, trebY-staffWidth, x+staffWidth*2.5, trebY-staffWidth*6, x+staffWidth, trebY-staffWidth*9);
        Line2D.Double line = new Line2D.Double(x+staffWidth, trebY-staffWidth*9, x+staffWidth, trebY+staffWidth*3.5);
        Arc2D.Double arc3 = new Arc2D.Double(x, trebY+3*staffWidth, staffWidth, staffWidth, 180, 180, Arc2D.OPEN);
        
        g.setColor(Color.black);
        g.draw(arc1);
        g.draw(arc2);
        g.draw(curve);
        g.draw(line);
        g.draw(arc3);
    }
    public void paint(Graphics g1)
    {
        Graphics2D g = (Graphics2D)g1;
        double staffWidth = getHeight()/(posit(MaxNote) - posit(MinNote) + 1);
        g.setColor(Color.white);
        g.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        g.setColor(Color.black);
        lineThroughNoteNumber(64, g);
        lineThroughNoteNumber(67, g);
        lineThroughNoteNumber(71, g);
        lineThroughNoteNumber(74, g);
        lineThroughNoteNumber(77, g);
        drawTrebleCleff(g);
        int i = 0;
        for(Enumeration e = notes.elements(); e.hasMoreElements();)
        {
            drawNote(((Notik)e.nextElement()).n, i, g);
            i++;
        }
        drawCurNote(curNote, curIndex, g);
    }//*/
    public void mouseMoved(MouseEvent e)
    {
        curNote = getNoteNumber(getHeight() - e.getY());
        if(curNote != prevNote)
        {
            if(channels != null)
            {
                channels[0].allNotesOff();
                Patch p = piano.getPatch();
                channels[0].programChange(p.getBank(), p.getProgram());
                channels[0].programChange(0, 0);
                channels[0].noteOn(curNote, 100);
            }
            if(kb != null)
                kb.setNote(curNote);
            prevNote = curNote;
        }
        double staffWidth = getHeight()/(posit(MaxNote) - posit(MinNote) + 1);
        double l = getWidth()-30-3*staffWidth;
        curIndex = (int)((e.getX() - 30-3*staffWidth)/((l-rightSpc)/(notes.size()+1)));
        if(curIndex > notes.size()) curIndex = notes.size();
        else if(curIndex < 0)       curIndex = 0;
        repaint();
    }
    public void mouseDragged(MouseEvent e) {}
    public void mouseClicked(MouseEvent e)
    {
        if(e.isShiftDown()) notes.insertElementAt(new Notik(curNote), curIndex);
        else
        {
            if(curIndex == notes.size()) notes.addElement(new Notik(curNote));
            else
            {
                Notik cur = (Notik)notes.elementAt(curIndex);
                if(cur.n == curNote) notes.removeElementAt(curIndex);
                else                 cur.n = curNote;
            }
        }
        smf.saved = false;
        repaint();
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e)
    {
        if(channels != null)
            channels[0].allNotesOff();
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e){}
    public void addNote(int note)
    {
        notes.addElement(new Notik(note));
    }
    
    //-----------------
    public int[] getScale()
    {
        int[] iii = new int[notes.size()];
        for(int i = 0; i < iii.length; i++)
        {
            iii[i] = ((Notik)notes.elementAt(i)).n-60;
        }
        return iii;
    }
}
