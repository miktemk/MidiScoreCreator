package GUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.undo.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import javax.sound.midi.*;
import DataStructure.*;
import NoteModifiers.*;
import GUI.UndoEdits.*;

public class ScorePanel extends JPanel implements MouseListener,
                                                  MouseMotionListener,
                                                  MouseWheelListener
{
    private class Ticker extends Thread
    {
        public static final int NONE = 0,
                                LEFT = 1,
                                RIGHT = 2;
        int dragDirection = NONE;
        int countDown = 0;
        ScorePanel p;
        public Ticker(ScorePanel p)
        {
            this.p = p;
            
        }
        public void run()
        {
            while(true)
            {
                if(dragDirection == RIGHT)
                {
                    p.scrollHere(p.barPosit+1, p.linePosit);
                    p.selectedBars.upDate(numOfScreenBars-1+barPosit);
                    p.repaint();
                }
                else if(dragDirection == LEFT)
                {
                    p.scrollHere(p.barPosit-1, p.linePosit);
                    p.selectedBars.upDate(barPosit);
                    p.repaint();
                }
                if(countDown > 0) countDown--;
                else
                {
                    switch(p.scrollButton)
                    {
                        case ScorePanel.LEFT:  p.scrollHere(p.barPosit-1, p.linePosit); break;
                        case ScorePanel.RIGHT: p.scrollHere(p.barPosit+1, p.linePosit); break;
                        case ScorePanel.UP:    p.scrollHere(p.barPosit, p.linePosit-1); break;
                        case ScorePanel.DOWN:  p.scrollHere(p.barPosit, p.linePosit+1); break;
                    }
                }
                try{ sleep(150); } catch(InterruptedException e) {}
            }
        }
        public void startCountDown()
        {
            countDown = 2;
        }
    }
    class BarGroup extends Vector
    {
        public BarGroup(){ super(); }
        public void addBar(Bar bar)
        {
            //Im wondering: Why do we need to have all bars unique???
            /*boolean clear = true;
            for(Enumeration e = elements(); e.hasMoreElements();)
            {
                if(bar == e.nextElement())
                {
                    clear = false;
                    break;
                }
            }
            if(clear)
            {
                addElement(bar);
                System.out.println("new selected bar added");
            }*/
            addElement(bar);
        }
    }
    class HighlightedBars
    {
        boolean hasBars = false;
        int barNumber = 0, lineNumber = 0, extend = 0;
        public HighlightedBars(){}
        public void reset() { hasBars = false; }
        public void setFirstBar(int barNumber, int lineNumber)
        {
            hasBars = true;
            this.barNumber = barNumber;
            this.lineNumber = lineNumber;
        }
        public void upDate(int newBar)
        {
            extend = newBar - barNumber;
        }
    }
    Score score;
    public Synthesizer synth;
    //Sequencer seq;
    public MidiChannel[] channels;
    public Instrument[] instruments;
    int drumChannel = 9;
    //------------
      public ScoreMainFrame parent = null;
      final int VERTICAL_SCROLL = 1,
                HORIZONTAL_SCROLL = 2,
                NO_SCROLL = 3;
      static final int NONE = 0,
                       UP = 1,
                       DOWN = 2,
                       LEFT = 3,
                       RIGHT = 4;
      final int leftSpace = 50,
                scrollWidth = 20,
                scrollTabLength = 30,
                extraBarLineSpace = 10;
      final Color highlight = new Color(100, 0, 100, 80);
      final Color refNoteColor = new Color(0, 150, 180);
      final Color barBoxColor = new Color(200, 0, 255);
      GeneralPath arrows;
      
      int numOfScreenBars = 4,
          numOfScreenParts = 3;
      int MaxNote = 96,
          MinNote = 48;
          
      int scrollCondition = NO_SCROLL;
      int scrollButton = NONE;
      boolean dragFirst = true;
      boolean ignoreNextClick = false;
      boolean soundOn = true;
      int curLine = 1;
      int curBar = 1;
      int curNote = 60, prevNote = 60;
      double inBarPosit = 0;
      int    inBarIndex = 0;
      int linePosit = 0;
      int barPosit = 0;
      Bar curBarRef = null;
      Part curPartRef = null;
      BandSection curSectionRef = null;
      
      ScorePopup pop;
      int popBar = 0,
          popLine = 0;
      Bar popBarRef = null;
      Part popPartRef = null;
      BandSection popSectionRef = null;
      double curNoteLength = 0.25;
      int curNoteVolume = 100;
      byte curNoteSlurm = Note.NONE;
      HighlightedBars selectedBars = new HighlightedBars();
      BarGroup copiedBars = new BarGroup();
      
      NoteModifier ntMd = new NormalModifier();
      
      PianoKeyB noteDispl = new PianoKeyB();
      ScorePlayer player = null;
      
      Ticker ticker;
      
      WindowsMenu wmenu = null;
    public void openMidiResources()
    {
        try{ synth = MidiSystem.getSynthesizer(); synth.open(); }
        catch(Exception e)
        {
            System.out.println("a piece of some junky high tech eqipment is not available!");
            return;
        }
        if(synth == null)
        {
            System.out.println("getSynthesizer() FAILED!!!!!!");
            return;
        }
        channels = synth.getChannels();
        if(channels.length == 0)
        {
            System.out.println("ZERO CHANNELS!!!!!!!!");
            return;
        }
        try
        {
            Soundbank s = synth.getDefaultSoundbank();
            //Soundbank s = MidiSystem.getSoundbank(new File("Soundbanks/soundbank-min.gm"));
            if(!synth.isSoundbankSupported(s)) 
            { System.out.println("soundbank is not supported"); }
            synth.loadAllInstruments(s);
            instruments = synth.getLoadedInstruments();
        }
        catch(Exception e)
        {
        	System.out.println("loadAllInstruments() FAILED!!!!!! " + e.getMessage());
        	e.printStackTrace();
        }
        
    }
    public ScorePanel(ScoreMainFrame parent)
    {
        openMidiResources();
        createNewScore();
        this.parent = parent;
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        pop = new ScorePopup(this, channels[0]);
        
        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                if((e.getX() >= leftSpace)&&(e.getX() < getWidth()-scrollWidth)&&(e.getY() < getHeight()-scrollWidth)&&(e.getButton() == MouseEvent.BUTTON3))
                {//Right click in the MAIN AREA
                    popBar = curBar;
                    popLine = curLine;
                    popBarRef = curBarRef;
                    popPartRef = curPartRef;
                    popSectionRef = curSectionRef;
                    boolean pasteActive = (copiedBars.size() > 0);
                    boolean refLKActive = false;
                    if(selectedBars.hasBars)
                    {
                        for(int i = 0; i <= Math.abs(selectedBars.extend); i++)
                        {
                            int index = Math.min(selectedBars.barNumber, selectedBars.barNumber+selectedBars.extend) + i;
                            Bar target = popPartRef.getBar(index);
                            if(target.byRef)
                            {
                                refLKActive = true;
                                break;
                            }
                        }
                    }
                    else refLKActive = popBarRef.byRef;
                    pop.show(e.getComponent(), e.getX(), e.getY(), pasteActive, refLKActive);
                }
            }
        });
        addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent e)
            {
                createArrows();
            }
        });
        createArrows();
        ticker = new Ticker(this);
        ticker.start();
    }
    /**Writes references to current popup stuff*/
    public void popilize()
    {
        popBar = curBar;
        popLine = curLine;
        popBarRef = curBarRef;
        popPartRef = curPartRef;
        popSectionRef = curSectionRef;
    }
    /**Sets the WindowsMenu object for undo commands, etc*/
    public void setMenu(WindowsMenu wmenu)
    {
        this.wmenu = wmenu;
    }
    public void addEdit(UndoableEdit edit)
    {
        if(wmenu != null) wmenu.addEdit(edit);
    }
    /**Returns the old score or null if the newScore=null*/
    public Score setScore(Score newScore)
    {
        if(newScore == null) return null;
        Score prev = score;
        score = newScore;
        scrollHere(barPosit, linePosit);
        return prev;
    }
    /**Returns the new score*/
    public Score createNewScore()
    {
        score = new Score(channels.length, 20);
        repaint();
        return score;
    }
    /**Saves the current score*/
    public void save(PrintStream stream)
    {
        if(score != null) score.save(stream);
    }
    //-------------------------------------------
    public void scrollHere(int newBar, int newLine)
    {
        //we do not want to go beyond score.getTotalParts() - numOfScreenParts
        linePosit = newLine;
        if(linePosit > score.getTotalParts() - numOfScreenParts) linePosit = score.getTotalParts() - numOfScreenParts;
        else if(linePosit < 0)                                   linePosit = 0;
        //we do not want to go beyond score.getTotalBars() - numOfScreenBars
        barPosit = newBar;
        if(barPosit > score.getTotalBars() - numOfScreenBars) barPosit = score.getTotalBars() - numOfScreenBars;
        else if(barPosit < 0)                                 barPosit = 0;
        repaint();
    }
    public void setNewView(int numOfScreenBars, int numOfScreenParts)
    {
        this.numOfScreenBars = numOfScreenBars;
        this.numOfScreenParts = numOfScreenParts;
        //FINI LATAR!!!!!!! :(((
        
    }
    //--------------------------------------------
    public int posit(int noteNumber)
    {//ok
        int[] notePosit = {0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6};
        return ((int)(noteNumber/12))*7 + notePosit[noteNumber % 12];
    }
    public int pos(int y)//Note Position given the Y
    {//ok
        double barHeight = (getHeight()-scrollWidth)/numOfScreenParts;
        return (int)(y/(barHeight/(posit(MaxNote) - posit(MinNote) + 1))) + posit(MinNote);
    }
    public double NoteY(int noteNumber, int lineNumber)//line number is 1 based
    {//ok
        double barHeight = (getHeight()-scrollWidth)/numOfScreenParts;
        double staffWidth = (barHeight/((posit(MaxNote) - posit(MinNote)) + 1));
        return lineNumber*barHeight - (posit(noteNumber)-posit(MinNote) + 0.5)*staffWidth;
    }
    public int getNoteNumber(int y)
    {//ok
        //Here y is measured from the BOTTOM
        double barHeight = (getHeight()-scrollWidth)/numOfScreenParts;
        int[] notes = {0, 2, 4, 5, 7, 9, 11};
        boolean[] noteHasBlackSharp = {true, true, false, true, true, true, false};
        //int y = getHeight() - e.getY();
        int NoteNumber = notes[pos(y) % 7] + 12*((int)(pos(y)/7));
        double staffWidth = (barHeight/(posit(MaxNote) - posit(MinNote) + 1));
        if((y % staffWidth > staffWidth*2/3)&&(noteHasBlackSharp[pos(y) % 7] ))
        { NoteNumber++; }
        return NoteNumber;
    }
    public boolean isPwrOfTwo(double num)
    {
        double pwr = Math.log(num)/Math.log(2);
        return (pwr == Math.round(pwr));
    }
    public boolean dotted(double length)
    {
        return isPwrOfTwo(length/3);
    }
    public boolean third(double length)
    {
        return isPwrOfTwo(length*3);
    }
    public int countTails(double length)
    {
        int tails = -(int)Math.floor(Math.log(length)/Math.log(2));
        if(third(length)) return tails - 3;
        else              return tails - 2;
    }
    //-----------------------------------------------------
    public void lineThroughNoteNumber(int noteNumber, int lineNumber, Graphics2D g)
    {
        double barHeight = (getHeight()-scrollWidth)/numOfScreenParts;
        double staffWidth = (barHeight/((posit(MaxNote) - posit(MinNote)) + 1));
        double LineY = (lineNumber+1)*barHeight - (posit(noteNumber)-posit(MinNote) + 0.5)*staffWidth;
        g.draw(new Line2D.Double(0, LineY, getWidth()-scrollWidth, LineY));
    }
    public void drawBarLines(int lineNumber, Graphics2D g)
    {
        double barHeight = (getHeight()-scrollWidth)/numOfScreenParts;
        double barWidth = (getWidth()-leftSpace-scrollWidth)/numOfScreenBars;
        double staffWidth = (barHeight/((posit(MaxNote) - posit(MinNote)) + 1));
        double lineYbot = (lineNumber+1)*barHeight - (posit(64)-posit(MinNote) + 0.5)*staffWidth;
        double lineYtop = (lineNumber+1)*barHeight - (posit(77)-posit(MinNote) + 0.5)*staffWidth;
        for(int i = 0; i < numOfScreenBars; i++)
        {
            g.draw(new Line2D.Double(leftSpace+barWidth*(i+1)-extraBarLineSpace, lineYbot, leftSpace+barWidth*(i+1)-extraBarLineSpace, lineYtop));
        }
    }
    public void drawNote(int noteNumber, int barNumber, double posit, int lineNumber, Graphics2D g, Note note, Bar bar)
    {//ok   line Number is 1 based, barNumber is 1-based; posit is out of 1 (x/1 <= 0)
        double maxLength = score.getBarMaxLength(barNumber-1);
        double barWidth = (getWidth()-leftSpace-scrollWidth)/numOfScreenBars;
//DANGER LEVEL
        double bar4NoteW = barWidth - 3*extraBarLineSpace;
//DANGER LEVEL
        double noteX = leftSpace + (barNumber-barPosit-1)*barWidth + bar4NoteW*posit+extraBarLineSpace;
//old line->double noteX = leftSpace + (barNumber-barPosit-1)*barWidth + barWidth*posit;//bar.getMaxLength();
        double barHeight = (getHeight()-scrollWidth)/numOfScreenParts;
        boolean[] isAccidentaled = {false, true, false, true, false, false, true, false, true, false, true, false};
        double staffWidth = (barHeight/((posit(MaxNote) - posit(MinNote)) + 1));
     if(note.getVolume() > 0)
     {
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
        if(bar.byRef) g.setColor(refNoteColor);
        else          g.setColor(Color.blue); //NOTE COLOR!!!
        // body
        if((note.getLength() >= 1.0/3)&&(note.getLength() != 1.5/4)) g.draw(new Ellipse2D.Double(noteX, LineY-staffWidth, staffWidth*3, staffWidth*2));
        else                                                         g.fill(new Ellipse2D.Double(noteX, LineY-staffWidth, staffWidth*3, staffWidth*2));
        //dot
        if(dotted(note.getLength())) g.fill(new Ellipse2D.Double(noteX+staffWidth*3.25, LineY-staffWidth/2, staffWidth, staffWidth));
        //stem + slur linez + 3rd label + tails
        if(noteNumber < 71)
        {//this is tail up
            if(note.getLength() < 1.0) g.draw(new Line2D.Double(noteX+staffWidth*3, LineY, noteX+staffWidth*3, LineY - 6*staffWidth));
            if(note.slured())
                g.draw(new Arc2D.Double(noteX, LineY+staffWidth, note.getLength()*bar4NoteW/maxLength+(((Note)bar.getNotes().lastElement() == note)?3*extraBarLineSpace:0), staffWidth*2.7, 180, 180, Arc2D.OPEN));
            else if(note.held())
            {
                g.draw(new Line2D.Double(noteX+3*staffWidth, LineY-2, noteX+3*staffWidth+note.getLength()*bar4NoteW/maxLength, LineY-2));
                g.draw(new Line2D.Double(noteX+3*staffWidth, LineY+2, noteX+3*staffWidth+note.getLength()*bar4NoteW/maxLength, LineY+2));
            }
            else if(note.stacatto())
                g.fill(new Ellipse2D.Double(noteX+staffWidth*1.1, LineY + staffWidth*1.1, staffWidth*0.8, staffWidth*0.8));
            if(third(note.getLength())) g.drawString("3", (int)(noteX+staffWidth), (int)(LineY+staffWidth*(2.5 + (note.stacatto()?1:0))));
            int tails = countTails(note.getLength());
            if(tails > 0)
            {
                for(int i = 0; i < tails; i++)
                {
                    g.draw(new Line2D.Double(noteX+staffWidth*3, LineY - 6*staffWidth + i*1.5*staffWidth, noteX+staffWidth*3+note.getLength()*bar4NoteW/maxLength, LineY - 6*staffWidth + (i+1)*1.5*staffWidth));
                }
            }
        }
        else
        {//this is tail down
            if(note.getLength() < 1.0) g.draw(new Line2D.Double(noteX, LineY, noteX, LineY + 6*staffWidth));
            if(note.slured())
                g.draw(new Arc2D.Double(noteX, LineY-3*staffWidth, note.getLength()*bar4NoteW/maxLength+((bar.getNotes().lastElement() == note)?3*extraBarLineSpace:0), staffWidth*2.7, 0, 180, Arc2D.OPEN));
            else if(note.held())
            {
                g.draw(new Line2D.Double(noteX+3*staffWidth, LineY-2, noteX+3*staffWidth+note.getLength()*bar4NoteW/maxLength, LineY-2));
                g.draw(new Line2D.Double(noteX+3*staffWidth, LineY+2, noteX+3*staffWidth+note.getLength()*bar4NoteW/maxLength, LineY+2));
            }
            else if(note.stacatto())
                g.fill(new Ellipse2D.Double(noteX+staffWidth*1.1, LineY - staffWidth*1.9, staffWidth*0.8, staffWidth*0.8));
            if(third(note.getLength())) g.drawString("3", (int)(noteX+staffWidth), (int)(LineY-staffWidth*(1.5 + (note.stacatto()?1:0))));
            int tails = countTails(note.getLength());
            if(tails > 0)
            {
                for(int i = 0; i < tails; i++)
                {
                    g.draw(new Line2D.Double(noteX, LineY + 6*staffWidth - i*1.5*staffWidth, noteX+note.getLength()*barWidth/maxLength, LineY + 6*staffWidth - (i+1)*1.5*staffWidth));
                }
            }
        }
        
        if(isAccidentaled[noteNumber % 12])// accidental
        {
            g.drawString("#",(int)noteX-9,(int)LineY+5);
        }
        for(Enumeration enMod = note.mods.elements(); enMod.hasMoreElements();)
        {
            NoteEvents ne = (NoteEvents)enMod.nextElement();
            ne.drawEvent(g, noteX+1.5*staffWidth, LineY, note.getLength()*bar4NoteW/maxLength, staffWidth, noteNumber < 71, note);
        }
        g.setColor(Color.black);
     }
     else
     {
        //This note is a rest
        double LineY = (lineNumber-linePosit)*barHeight - (posit(72)-posit(MinNote) + 0.5)*staffWidth;
        final int restWidth = 5;
        g.setColor(Color.black);
        if(note.getLength() == 0.5)
        {
            g.fill(new Rectangle2D.Double(noteX, LineY, restWidth, staffWidth));
        }
        else
        {
            g.drawString("R", (int)noteX, (int)LineY);
        }
     }
     g.setColor(Color.black);
    }
    public void drawCurNote(int noteNumber, int barNumber, double posit, int lineNumber, Graphics2D g, Bar bar)
    {//ok   line Number is 1 based, barNumber is 1-based; posit is out of 1 (x/1 <= 0)
        //System.out.println("posit = "+posit);
        double barWidth = (getWidth()-leftSpace-scrollWidth)/numOfScreenBars;
//DANGER LEVEL
        double bar4NoteW = barWidth - 3*extraBarLineSpace;
//DANGER LEVEL
        double noteX = leftSpace + (barNumber-barPosit-1)*barWidth + bar4NoteW*posit+extraBarLineSpace;
//old line->double noteX = leftSpace + (barNumber-barPosit-1)*barWidth + barWidth*posit;//maxLength;
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
        // the box around the bar:
        g.setColor(barBoxColor);
        g.draw(new Rectangle2D.Double(leftSpace + (barNumber-barPosit-1)*barWidth - extraBarLineSpace,   (lineNumber - linePosit)*barHeight - (posit(77)-posit(MinNote) + 0.5)*staffWidth,   barWidth, staffWidth*8));
        g.draw(new Rectangle2D.Double(leftSpace + (barNumber-barPosit-1)*barWidth - extraBarLineSpace+1, (lineNumber - linePosit)*barHeight - (posit(77)-posit(MinNote) + 0.5)*staffWidth+1, barWidth-2, staffWidth*8-2));
        // The actual curNote
        g.setColor(new Color(255, 0, 255, 200));
        // body
        g.draw(new Ellipse2D.Double(noteX, LineY-staffWidth, staffWidth*3, staffWidth*2));
        g.draw(new Ellipse2D.Double(noteX+1, LineY-staffWidth+1, staffWidth*3-2, staffWidth*2-2));
        //stem
        if(noteNumber < 71)
        {
            g.draw(new Line2D.Double(noteX+staffWidth*3, LineY, noteX+staffWidth*3, LineY - 6*staffWidth));
            g.draw(new Line2D.Double(noteX+staffWidth*3-1, LineY, noteX+staffWidth*3-1, LineY - 6*staffWidth));
        }
        else
        {
            g.draw(new Line2D.Double(noteX, LineY, noteX, LineY + 6*staffWidth));
            g.draw(new Line2D.Double(noteX+1, LineY, noteX+1, LineY + 6*staffWidth));
        }

        if(isAccidentaled[noteNumber % 12])// accidental
        {
            g.drawString("#",(int)noteX-9,(int)LineY+5);
        }
        g.setColor(Color.black);
    }
    public void drawTrebleCleff(int lineNumber, Graphics2D g)
    {
        final double x = 20;
        double barHeight = (getHeight()-scrollWidth)/numOfScreenParts;
        double staffWidth = (barHeight/((posit(MaxNote) - posit(MinNote)) + 1));
        double trebY = (lineNumber+1)*barHeight - (posit(67)-posit(MinNote) + 0.5)*staffWidth;
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
    public void drawBarNotes(Graphics2D g, int lineNumber, int barNumber, Bar bar)
    {//Bar number and line  Number are 0 based
        double barRatio = score.getBarMaxLength(barNumber);
        double curPosit = 0.0;
        for(Enumeration e = bar.getNotes().elements(); e.hasMoreElements();)
        {
            Note cur = (Note)(e.nextElement());
            for(Enumeration e2 = cur.getNotes().elements(); e2.hasMoreElements();)
            {
                Note.MiniNote cur2 = (Note.MiniNote)(e2.nextElement());
                drawNote(cur2.getValue(), barNumber+1+barPosit, curPosit/barRatio, lineNumber+1+linePosit, g, cur, bar);
            }
            curPosit += cur.getLength();
        }
    }
    public void drawKeySignature(Graphics2D g, int lineNumber, int barNumber, Score.TimeSignatureChangeEvent event)
    {//line # and bar # are 0 based
        final int xDispl = 10;
        double barWidth = (getWidth()-leftSpace-scrollWidth)/numOfScreenBars;
        double barHeight = (getHeight()-scrollWidth)/numOfScreenParts;
        double staffWidth = (barHeight/((posit(MaxNote) - posit(MinNote)) + 1));
        double LineY = (lineNumber+1)*barHeight - (posit(71)-posit(MinNote) + 0.5)*staffWidth;
        double noteX = leftSpace + (barNumber)*barWidth;
        g.setFont(new Font(null, Font.PLAIN, (int)(staffWidth*5)+1));
        g.setColor(Color.black);
           g.drawString(""+event.top, (int)noteX-xDispl, (int)LineY);
        LineY = (lineNumber+1)*barHeight - (posit(64)-posit(MinNote) + 0.5)*staffWidth;
           g.drawString(""+event.bottom, (int)noteX-xDispl, (int)LineY);
        g.setFont(new Font(null, Font.PLAIN, 12));
    }
    //-------------------------------------------
    public void createArrows()
    {
        arrows = new GeneralPath();
        arrows.append(new Line2D.Double(getWidth()-scrollWidth/2, getHeight()-scrollWidth-2, getWidth()-scrollWidth+2, getHeight()-2*scrollWidth+5), true);
        arrows.append(new Line2D.Double(getWidth()-scrollWidth+2, getHeight()-2*scrollWidth+5, getWidth()-2, getHeight()-2*scrollWidth+5), true);
        arrows.append(new Line2D.Double(getWidth()-2, getHeight()-2*scrollWidth+5, getWidth()-scrollWidth/2, getHeight()-scrollWidth-2), true);
        
        arrows.append(new Line2D.Double(getWidth()-scrollWidth-2, getHeight()-scrollWidth/2, getWidth()-2*scrollWidth+5, getHeight()-scrollWidth+2), false);
        arrows.append(new Line2D.Double(getWidth()-2*scrollWidth+5, getHeight()-scrollWidth+2, getWidth()-2*scrollWidth+5, getHeight()-2), true);
        arrows.append(new Line2D.Double(getWidth()-2*scrollWidth+5, getHeight()-2, getWidth()-scrollWidth-2, getHeight()-scrollWidth/2), true);
        
        arrows.append(new Line2D.Double(getWidth()-scrollWidth/2, 2, getWidth()-scrollWidth+2, scrollWidth-5), false);
        arrows.append(new Line2D.Double(getWidth()-scrollWidth+2, scrollWidth-5, getWidth()-2, scrollWidth-5), true);
        arrows.append(new Line2D.Double(getWidth()-2, scrollWidth-5, getWidth()-scrollWidth/2, 2), true);
        
        arrows.append(new Line2D.Double(leftSpace+2, getHeight()-scrollWidth/2, leftSpace+scrollWidth-5, getHeight()-scrollWidth+2), false);
        arrows.append(new Line2D.Double(leftSpace+scrollWidth-5, getHeight()-scrollWidth+2, leftSpace+scrollWidth-5, getHeight()-2), true);
        arrows.append(new Line2D.Double(leftSpace+scrollWidth-5, getHeight()-2, leftSpace+2, getHeight()-scrollWidth/2), true);
    }
    public void drawScrollBars(Graphics2D g)
    {
        g.setColor(Color.cyan);
        switch(scrollButton)
        {
            case LEFT:  g.fill(new Rectangle2D.Double(leftSpace, getHeight()-scrollWidth, scrollWidth, scrollWidth));                break;
            case RIGHT: g.fill(new Rectangle2D.Double(getWidth()-2*scrollWidth, getHeight()-scrollWidth, scrollWidth, scrollWidth)); break;
            case UP:    g.fill(new Rectangle2D.Double(getWidth()-scrollWidth, 0, scrollWidth, scrollWidth));                         break;
            case DOWN:  g.fill(new Rectangle2D.Double(getWidth()-scrollWidth, getHeight()-2*scrollWidth, scrollWidth, scrollWidth)); break;
        }
        g.setColor(Color.black);
        g.draw(new Line2D.Double(getWidth()-scrollWidth, 0, getWidth()-scrollWidth, getHeight()));
        g.draw(new Line2D.Double(leftSpace, getHeight()-scrollWidth, getWidth(), getHeight()-scrollWidth));
        g.draw(new Line2D.Double(leftSpace, getHeight()-scrollWidth, leftSpace, getHeight()));
        g.draw(new Line2D.Double(leftSpace+scrollWidth, getHeight()-scrollWidth, leftSpace+scrollWidth, getHeight()));
        g.draw(new Line2D.Double(getWidth()-scrollWidth, scrollWidth, getWidth(), scrollWidth));
        g.draw(new Line2D.Double(getWidth()-2*scrollWidth, getHeight()-scrollWidth, getWidth()-2*scrollWidth, getHeight()));
        g.draw(new Line2D.Double(getWidth()-scrollWidth, getHeight()-2*scrollWidth, getWidth(), getHeight()-2*scrollWidth));
        g.fill(arrows);
        //g.drawString("^", getWidth()-scrollWidth+2, scrollWidth-2);
        //g.drawString("v", getWidth()-scrollWidth+10, getHeight()-scrollWidth+15);
        
        //    SCROLL TABS   1 - vertical    2 - horizontal
        g.setColor(Color.blue);
        int totalParts = score.getTotalParts();
        int tabY = scrollWidth+1;
        if(totalParts != 0) tabY += (getHeight() - 3*scrollWidth - scrollTabLength)*linePosit/(totalParts-numOfScreenParts);
        g.fill(new Rectangle2D.Double(getWidth()-scrollWidth+1, tabY, scrollWidth-1, scrollTabLength-1));
        int totalBars = score.getTotalBars();
        int tabX = leftSpace+scrollWidth+1;
        if(totalBars != 0) tabX += (getWidth() - 3*scrollWidth - scrollTabLength-leftSpace)*barPosit/(totalBars-numOfScreenBars);
        g.fill(new Rectangle2D.Double(tabX, getHeight()-scrollWidth+1, scrollTabLength, scrollWidth-1));
    }
    //-------------------------------------------
    public void paint(Graphics g1)
    {
        Graphics2D g = (Graphics2D)g1;
        g.setColor(Color.white);
        g.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        g.setColor(Color.black);
        double barWidth = (getWidth()-leftSpace-scrollWidth)/numOfScreenBars;
        double barHeight = (getHeight()-scrollWidth)/numOfScreenParts;
        for(int i = 0; i < numOfScreenParts; i++)
        {
            lineThroughNoteNumber(64, i, g);
            lineThroughNoteNumber(67, i, g);
            lineThroughNoteNumber(71, i, g);
            lineThroughNoteNumber(74, i, g);
            lineThroughNoteNumber(77, i, g);
            
            Part curPart = score.getPart(i + linePosit);
            int curSectionIndex = score.getSectionIndexAtPart(i + linePosit);
            BandSection curSection = score.getSection(curSectionIndex);
            
            drawBarLines(i, g);
            drawTrebleCleff(i, g);
            int prevTopNumber = 0,  prevBottomNumber = 0;
            int prevInstNumber = -1;
            for(int i2 = 0; i2 < numOfScreenBars; i2++)
            {
                Bar cur = (Bar)(curPart.getBar(i2 + barPosit));
                Score.TimeSignatureChangeEvent event = score.getTimeSignatureAtBar(i2 + barPosit);
                //---------------time signature--(#s)-----
                if((event.top != prevTopNumber)||(event.bottom != prevBottomNumber))
                {
                    drawKeySignature(g, i, i2, event);
                    prevTopNumber = event.top;
                    prevBottomNumber = event.bottom;
                }
                //---------------instrument namez---------
                if(curSectionIndex == drumChannel)
                {
                    g.drawString("Drums", (int)(leftSpace/2), (int)((i+0.25)*barHeight));
                }
                else
                {
                    int curInstrument = curSection.getInstrumentNumberAtBar(i2+barPosit);
                    if(prevInstNumber != curInstrument)
                    {
                        g.drawString(""+instruments[curInstrument].getName(), (int)(leftSpace/2 + (i2)*barWidth), (int)((i+0.25)*barHeight));
                        prevInstNumber = curInstrument;
                    }
                }
                //---------------bar notes----------------
                if((cur != null))//&&(cur != curBarRef))
                    drawBarNotes(g, i, i2, cur);
            }
        }
        int prevTempo = -1;
        for(int i = 0; i < numOfScreenBars; i++)
        {
            int curTempo = score.getTempoAtBar(i + barPosit);
            if(curTempo != prevTempo)
            {
                g.drawString("Q="+curTempo, (int)(leftSpace+barWidth*i), 10);
                prevTempo = curTempo;
            }
            //----------Bar numbers-------------------
            g.drawString(""+(i + barPosit+1), (int)(leftSpace/2 + (i)*barWidth+extraBarLineSpace*1.5), (int)NoteY(81, 1));
        }
        if(selectedBars.hasBars)
        {
            double staffWidth = (barHeight/((posit(MaxNote) - posit(MinNote)) + 1));
            
            double selX = leftSpace + barWidth*Math.min(selectedBars.barNumber - barPosit, selectedBars.barNumber+selectedBars.extend - barPosit);
            double selY = (selectedBars.lineNumber+1-linePosit)*barHeight - (posit(77)-posit(MinNote) + 0.5)*staffWidth;
            g.setColor(highlight);
            g.fill(new Rectangle2D.Double(selX-extraBarLineSpace, selY, (Math.abs(selectedBars.extend)+1)*barWidth, 8*staffWidth));
        }
        drawCurNote(curNote, curBar+1, inBarPosit, curLine+1, g, curBarRef);
        drawScrollBars(g);
    }
    
    //-------------------------------------------
    public void mouseClicked(MouseEvent e)
    {
        if((e.getX() < leftSpace)&&(e.getY() < getHeight()-scrollWidth))
        {
            //in the cleff area
            curLine = linePosit+(int)(e.getY()*numOfScreenParts/(getHeight()-scrollWidth));
            
            curPartRef = score.getPart(curLine);
            int sectionIndex = score.getSectionIndexAtPart(curLine);
            curSectionRef = score.getSection(sectionIndex);
            /*if(e.getClickCount() == 3)
            {
                curSectionRef.addPart(new Part(curSectionRef.getTotalBars()));
                repaint();
            }//*/
        }
        else if((e.getX() < leftSpace)&&(e.getY() >= getHeight()-scrollWidth))
        {
            //in the bottom left little Corner
            
        }
        else if((e.getX() >= leftSpace)&&(e.getX() < getWidth()-scrollWidth)&&(e.getY() < getHeight()-scrollWidth))
        {
            //Main area
            //line Number and Bar number are 1-based
            int lineNumber = linePosit+(int)(e.getY()*numOfScreenParts/(getHeight()-scrollWidth))+1;
            int barNumber  = barPosit +(int)((e.getX()-leftSpace)*numOfScreenBars/(getWidth()-leftSpace-scrollWidth))+1;
            int barX = (e.getX()-leftSpace)%((getWidth()-leftSpace-scrollWidth)/numOfScreenBars);
            int barY = e.getY()%((getHeight()-scrollWidth)/numOfScreenParts);
            double maxLength = score.getBarMaxLength(curBar);
            if(curBarRef != null)
            {
              if(e.getButton() == MouseEvent.BUTTON1 && !ignoreNextClick)
              {
                double barWidth = (getWidth()-leftSpace-scrollWidth)/numOfScreenBars;
                double x = (barX-extraBarLineSpace)/(barWidth - 3*extraBarLineSpace),
                       curX = 0.0;
                if(x > 1.0) x = 0.9999;
                int posit = 0;//<<<<<<<<<----- use 0 for vertor insertion!!!!!!!!!!!!
                Note selectedOne = null;
                for(Enumeration en = curBarRef.getNotes().elements(); en.hasMoreElements();)
                {
                    Note cur = (Note)(en.nextElement());
                    curX += cur.getLength()/maxLength;
                    if(x > curX)
                    {
                        posit++;
                        //selectedOne = cur;
                    }
                    else
                    {
                        selectedOne = cur;
                        break;
                    }//*/
                }
                if(e.isShiftDown())
                {
                    Note thisNote = new Note(curNote, curNoteLength, curNoteVolume, curNoteSlurm);
                    curBarRef.addNote(thisNote, posit, maxLength);
                    addEdit(new InsertNoteEdit(score, curBarRef, curBar, thisNote, posit));
                }
                //-----Here we gOOOOOOOOOOO!!!!!!!!
                else
                {
                    if(selectedOne != null)
                    {
                        if(selectedOne.getVolume() != 0)
                        {
                            if(!(ntMd instanceof NoteModifiers.NormalModifier))
                            {//modifiers added...
                                Note newNote = selectedOne.makeCopy_RefNotes();
                                ntMd.modifyNote(newNote);
                                if(!newNote.equals(selectedOne))
                                {
                                    curBarRef.getNotes().setElementAt(newNote, posit);
                                    addEdit(new NoteChangeEdit(curBarRef, selectedOne, newNote, posit));
                                }
                            }
                            else
                            {
                                Note thisNote = curBarRef.getNoteAt(posit);
                                curBarRef.addNoteToAChord(curNote, posit);
                                if(curBarRef.getNoteAt(posit).getNotes().size() == 0)
                                {//add REMOVE_NOTE_EDIT
                                    curBarRef.removeNote(posit);
                                    addEdit(new RemoveNoteEdit(score, curBarRef, curBar, thisNote, curNote, posit));
                                }
                                else
                                {//add ADD_NOTE_EDIT
                                    addEdit(new AddNoteEdit(curBarRef.getNoteAt(posit), curNote));
                                }
                            }
                        }
                        else
                        {//ITS A REST!!!!
                            Note thisNote = curBarRef.getNoteAt(posit);
                            curBarRef.removeNote(posit);
                            addEdit(new RemoveNoteEdit(score, curBarRef, curBar, thisNote, 0, posit));
                        }
                    }
                    else
                    {
                        Note thisNote = new Note(curNote, curNoteLength, curNoteVolume, curNoteSlurm);
                        curBarRef.addNote(thisNote, posit, maxLength);
                        addEdit(new InsertNoteEdit(score, curBarRef, curBar, thisNote, posit));
                    }
                }
                if(barNumber == score.getTotalBars())
                {
                    score.addBarToEnd();
                }
              }
              ignoreNextClick = false;
              /*else if((e.getButton() == MouseEvent.BUTTON3)&&(e.getClickCount() == 2))
              {
                  //left double click
                  //open bar attribute setter to change the insrument, time signature, etc
                  //but for now just change the instrument
                  score.getSectionAtPart(curLine).addInstrumentChangeEvent((int)(Math.random()*128), curBar);
              }//*/
            }
            else { System.out.println("Uhhh.. critical error: thats not supposed to happen: ScorePanel line 726"); }
            repaint();
            //System.out.println("Bar#: "+barNumber+"  line#: "+lineNumber+"  barX = "+barX+"  barY = "+barY+"  CurNote = "+curNote);
        }
        mouseMoved(e);
    }
    public void mouseEntered(MouseEvent e)  {}
    public void mouseExited(MouseEvent e)
    {
        channels[0].allNotesOff();
    }
    public void mousePressed(MouseEvent e)
    {
        dragFirst = true;
        if((e.getX() > getWidth()-scrollWidth)&&(e.getY() > scrollWidth)&&(e.getY() < getHeight()-scrollWidth*2))
        {
            scrollCondition = VERTICAL_SCROLL;
            int y = e.getY()-scrollWidth+scrollTabLength/2;
            int l = getHeight()-3*scrollWidth-scrollTabLength;
            scrollHere(barPosit, (int)((score.getTotalParts()-numOfScreenParts)*y/l));
        }
        else if((e.getY() > getHeight()-scrollWidth)&&(e.getX() > leftSpace+scrollWidth)&&(e.getX() < getWidth()-scrollWidth*2))
        {
            scrollCondition = HORIZONTAL_SCROLL;
            int x = e.getX()-leftSpace-scrollWidth+scrollTabLength/2;
            int l = getWidth()-3*scrollWidth-scrollTabLength/2-leftSpace;
            scrollHere((int)((score.getTotalBars()-numOfScreenBars)*x/l), linePosit);
        }
        else if((e.getX() >= leftSpace)&&(e.getX() < leftSpace+scrollWidth)&&(e.getY() >= getHeight()-scrollWidth))
        {
            //left Button
            scrollHere(barPosit-1, linePosit);
            scrollButton = LEFT;
            ticker.startCountDown();
        }
        else if((e.getX() >= getWidth()-scrollWidth*2)&&(e.getX() < getWidth()-scrollWidth)&&(e.getY() >= getHeight()-scrollWidth))
        {
            //right Button
            scrollHere(barPosit+1, linePosit);
            scrollButton = RIGHT;
            ticker.startCountDown();
        }
        else if((e.getX() >= getWidth()-scrollWidth)&&(e.getY() < scrollWidth))
        {
            //up Button
            scrollHere(barPosit, linePosit-1);
            scrollButton = UP;
            ticker.startCountDown();
        }
        else if((e.getX() >= getWidth()-scrollWidth)&&(e.getY() >= getHeight()-scrollWidth*2)&&(e.getY() < getHeight()-scrollWidth))
        {
            //down Button
            scrollHere(barPosit, linePosit+1);
            scrollButton = DOWN;
            ticker.startCountDown();
        }
        else
        {
            // main area
            curLine = linePosit+(int)(e.getY()*numOfScreenParts/(getHeight()-scrollWidth));
            curBar  = barPosit +(int)((e.getX()-leftSpace)*numOfScreenBars/(getWidth()-leftSpace-scrollWidth));
            if(e.getButton() == MouseEvent.BUTTON1)
            {
                if(selectedBars.hasBars)
                {
                    ignoreNextClick = true;
                    selectedBars.reset();
                }
            }
        }
    }
    public void mouseReleased(MouseEvent e)
    {
        scrollCondition = NO_SCROLL;
        if(scrollButton != NONE)
        {
            scrollButton = NONE;
            repaint();
        }
        ticker.dragDirection = Ticker.NONE;
    }
    public void mouseDragged(MouseEvent e)
    {
        //scrolls
        if(scrollCondition == VERTICAL_SCROLL)
        {
            int y = e.getY()-scrollWidth+scrollTabLength/2;
            int l = getHeight()-3*scrollWidth-scrollTabLength;
            scrollHere(barPosit, (int)((score.getTotalParts()-numOfScreenParts)*y/l));
        }
        else if(scrollCondition == HORIZONTAL_SCROLL)
        {
            int x = e.getX()-leftSpace-scrollWidth+scrollTabLength/2;
            int l = getWidth()-3*scrollWidth-scrollTabLength/2-leftSpace;
            scrollHere((int)((score.getTotalBars()-numOfScreenBars)*x/l), linePosit);
        }
        else if(scrollButton == NONE)
        {
            if((e.getX() >= leftSpace)&&(e.getX() < getWidth()-scrollWidth)&&(e.getY() < getHeight()-scrollWidth))
            {
                // main area
                ticker.dragDirection = Ticker.NONE;
                if(dragFirst)
                {
                    selectedBars.setFirstBar(curBar, curLine);
                    dragFirst = false;
                }
                curLine = linePosit+(int)(e.getY()*numOfScreenParts/(getHeight()-scrollWidth));
                curBar  = barPosit +(int)((e.getX()-leftSpace)*numOfScreenBars/(getWidth()-leftSpace-scrollWidth));
                curPartRef = score.getPart(curLine);
                curBarRef = curPartRef.getBar(curBar);
                int sectionIndex = score.getSectionIndexAtPart(curLine);
                curSectionRef = score.getSection(sectionIndex);
                selectedBars.upDate(curBar);
                repaint();
            }
            else if((e.getX() > getWidth()-scrollWidth)&&(selectedBars.hasBars))
            {
                ticker.dragDirection = Ticker.RIGHT;
                //scrollHere(barPosit+1, linePosit);
                //selectedBars.upDate(numOfScreenBars-1+barPosit);
            }
            else if((e.getX() < leftSpace)&&(selectedBars.hasBars))
            {
                ticker.dragDirection = Ticker.LEFT;
                //scrollHere(barPosit-1, linePosit);
                //selectedBars.upDate(barPosit);
            }
        }
    }
    public void mouseMoved(MouseEvent e)
    {
        if((e.getX() < leftSpace)&&(e.getY() < getHeight()-scrollWidth))
        {
            //in the cleff area
            int lineNumber = (int)(e.getY()*numOfScreenParts/(getHeight()-scrollWidth))+1;
            
        }
        else if((e.getX() < leftSpace)&&(e.getY() >= getHeight()-scrollWidth))
        {
            //in the bottom left little Corner
            
        }
        else if((e.getX() >= leftSpace)&&(e.getX() < getWidth()-scrollWidth)&&(e.getY() < getHeight()-scrollWidth))
        {
            //Main area
            curLine = linePosit+(int)(e.getY()*numOfScreenParts/(getHeight()-scrollWidth));
            curBar  = barPosit +(int)((e.getX()-leftSpace)*numOfScreenBars/(getWidth()-leftSpace-scrollWidth));
            
            curPartRef = score.getPart(curLine);
            curBarRef = curPartRef.getBar(curBar);
            int sectionIndex = score.getSectionIndexAtPart(curLine);
            curSectionRef = score.getSection(sectionIndex);
            int barX = (e.getX()-leftSpace)%((getWidth()-leftSpace-scrollWidth)/numOfScreenBars);
            int barY = (e.getY()-2)%((getHeight()-scrollWidth)/numOfScreenParts);
            curNote = getNoteNumber((getHeight()-scrollWidth)/numOfScreenParts - barY+1);
            double maxLength = score.getBarMaxLength(curBar);
            if(curBarRef != null)
            {
                double barWidth = (getWidth()-leftSpace-scrollWidth)/numOfScreenBars;
                double x = (barX-extraBarLineSpace)/(barWidth - 3*extraBarLineSpace);
                if(x > 1.0) x = 0.9999;
                double curX = 0.0;
                int posit = 0;//<<<<<<<<<----- use 0 for vertor insertion!!!!!!!!!!!!
                Note selectedOne = null;
                for(Enumeration en = curBarRef.getNotes().elements(); en.hasMoreElements();)
                {
                    Note cur = (Note)(en.nextElement());
                    curX += cur.getLength()/maxLength;
                    if(x > curX)
                    {
                        posit++;
                        //selectedOne = cur;
                    }
                    else
                    {
                        selectedOne = cur;
                        break;
                    }//*/
                }
                inBarIndex = posit;
                if(selectedOne != null) inBarPosit = curX - selectedOne.getLength()/maxLength;
                else                    inBarPosit = curX;
            }
            if(prevNote != curNote && soundOn)//activate synthesizer
            {
                int chanNumber = 0;
                if(sectionIndex == drumChannel)
                {
                    chanNumber = drumChannel;
                }
                channels[chanNumber].allNotesOff();
                Patch p = instruments[curSectionRef.getInstrumentNumberAtBar(curBar)].getPatch();
                channels[chanNumber].programChange(p.getBank(), p.getProgram());
                channels[chanNumber].noteOn(curNote, 100);
                noteDispl.setNote(curNote);
            }
            prevNote = curNote;
            repaint();
        }
        
    }
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        scrollHere(barPosit, linePosit + e.getWheelRotation());
    }
    public MidiChannel getPlayChannel() { return channels[0]; }
    public void setPlayer(ScorePlayer player)
    {
        this.player = player;
    }
    public void setSound(boolean aflag)
    {
        soundOn = aflag;
        if(!soundOn)
        {
            channels[0].allNotesOff();
            channels[drumChannel].allNotesOff();
        }
    }
    public void setModifier(NoteModifier mdf) { ntMd = mdf; }
    public void setCurNoteLength(double l) { curNoteLength = l; }
    public void setCurNoteVolume(int v)    { curNoteVolume = v; }
    public void setSlurm(byte slurm)       { curNoteSlurm = slurm; }
    //--------------------------------------------------------------
    public void playScale(int[] scale, float tempo)
    {
        if(player != null) player.playScale(scale, tempo);
    }
}
