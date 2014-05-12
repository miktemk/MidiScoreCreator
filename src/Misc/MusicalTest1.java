package Misc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.midi.*;
import java.awt.geom.*;

public class MusicalTest1 extends JFrame
{
    private class StaffTest extends JPanel implements MouseMotionListener
    {
        JLabel dataLabel;
        final int MinNote = 20,
                  MaxNote = 100;
        int curNote = 0;
        public StaffTest(JLabel dataLabel)
        {
            this.dataLabel = dataLabel;
            addMouseMotionListener(this);
            System.out.println(""+posit(MinNote)+"  "+posit(MaxNote));
            System.out.println(""+(posit(MaxNote)-posit(MinNote)+1));
            addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    double staffWidth = (getHeight()/(posit(MaxNote) - posit(MinNote) + 1));
                    System.out.println(""+curNote);
                    System.out.println("x: "+e.getX()+"  y: "+e.getY());
                }
            });
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
        public void drawNote(int noteNumber, Graphics2D g)
        {
            boolean[] isAccidentaled = {false, true, false, true, false, false, true, false, true, false, true, false};
            double staffWidth = (getHeight()/((posit(MaxNote) - posit(MinNote)) + 1));
            double LineY = getHeight() - (posit(noteNumber)-posit(MinNote) + 0.5)*staffWidth;
            //extra linez
            g.setColor(Color.black);
            if(noteNumber < 71)
            {
                int totalLinez = (posit(71) - posit(noteNumber))/2;
                for(int i = 0; i < totalLinez; i++)
                {
                    g.draw(new Line2D.Double(20, NoteY(67)+i*2*staffWidth, 20+staffWidth*3, NoteY(67)+i*2*staffWidth));
                }
                g.drawString(""+totalLinez,50,(int)LineY+5);
            }
            else
            {
                int totalLinez = (posit(noteNumber) - posit(71))/2;
                for(int i = 0; i < totalLinez; i++)
                {
                    g.draw(new Line2D.Double(20, NoteY(74)-i*2*staffWidth, 20+staffWidth*3, NoteY(74)-i*2*staffWidth));
                }
                g.drawString(""+totalLinez,50,(int)LineY+5);
            }
            g.setColor(Color.blue);
            // body
            g.draw(new Ellipse2D.Double(20, LineY-staffWidth, staffWidth*3, staffWidth*2));
            //stem
            if(noteNumber < 71) g.draw(new Line2D.Double(20+staffWidth*3, LineY, 20+staffWidth*3, LineY - 6*staffWidth));
            else                g.draw(new Line2D.Double(20, LineY, 20, LineY + 6*staffWidth));
            
            if(isAccidentaled[noteNumber % 12])// accidental
            {
                g.drawString("#",10,(int)LineY+5);
            }
        }
        public void drawTrebleCleff(Graphics2D g)
        {
            final double x = 100;
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
            g.setColor(Color.blue);
            drawNote(curNote, g);
            drawTrebleCleff(g);
        }//*/
        public void mouseMoved(MouseEvent e)
        {
            curNote = getNoteNumber(getHeight() - e.getY());
            repaint();
            dataLabel.setText("Note #: "+curNote);
        }
        public void mouseDragged(MouseEvent e)
        {
            
        }
    }
    JPanel p;
    public MusicalTest1()
    {
  //-----------------------------------------------------
        final int res = 1000;
        Synthesizer synth;
        Sequencer seq;
        try
        {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            seq = MidiSystem.getSequencer();
            seq.open();
        }
        catch (Exception e)
        {
            return;
        }
        System.out.println("Max Polyphony: "+synth.getMaxPolyphony());
        Sequence tape;
        try{tape = new Sequence(Sequence.PPQ, res);}
        catch (Exception e)
        {
            return;
        }
        Track track1 = tape.createTrack();
        ShortMessage mes1 = new ShortMessage();
        try{ mes1.setMessage(ShortMessage.PROGRAM_CHANGE, 1, 97, 0); }
        catch (Exception e)
        {
            return;
        }
        //*/
        track1.add(new MidiEvent(mes1, 1));
        /*for(int i = 0; i < 45; i++)
        {
            ShortMessage mes2 = new ShortMessage();
            try{ mes2.setMessage(ShortMessage.NOTE_ON, 1, 50+i, 40); }
            catch (Exception e)
            {
                return;
            }
            //track1.add(new MidiEvent(mes1, 1));
            track1.add(new MidiEvent(mes2, 300*i));
            ShortMessage mes = new ShortMessage();
            try{ mes.setMessage(ShortMessage.NOTE_OFF, 1, 50+i, 0); }
            catch (Exception e)
            {
                return;
            }
            //track1.add(new MidiEvent(mes1, 1));
            track1.add(new MidiEvent(mes, 300*(i+1)-1));
        }//*/
        
            ShortMessage mes3 = new ShortMessage();
            try{ mes3.setMessage(ShortMessage.NOTE_OFF, 1, 57, 0); }
            catch (Exception e)
            {
                System.out.println("NOTE OFF 57 failed!");
                return;
            }
            track1.add(new MidiEvent(mes3, 3000));
            ShortMessage mes4 = new ShortMessage();
            try{ mes4.setMessage(ShortMessage.NOTE_OFF, 1, 50, 0); }
            catch (Exception e)
            {
                System.out.println("NOTE OFF 50 failed!");
                return;
            }
            track1.add(new MidiEvent(mes4, 3000));
            
            
            ShortMessage mes2 = new ShortMessage();
            try{ mes2.setMessage(ShortMessage.NOTE_ON, 1, 50, 100); }
            catch (Exception e)
            {
                System.out.println("#50 failed!");
                return;
            }
            track1.add(new MidiEvent(mes2, 2));
            ShortMessage mes = new ShortMessage();
            try{ mes.setMessage(ShortMessage.NOTE_ON, 1, 56, 100); }
            catch (Exception e)
            {
                System.out.println("#57 failed!");
                return;
            }
            track1.add(new MidiEvent(mes, 1));
            
            
        Track track2 = tape.createTrack();
        ShortMessage mesg1 = new ShortMessage();
        try{ mesg1.setMessage(ShortMessage.PROGRAM_CHANGE, 2, 99, 0); }
        catch (Exception e)
        {
            return;
        }
        //*/
        track2.add(new MidiEvent(mesg1, 1));
        /*for(int i = 0; i < 45; i++)
        {
            ShortMessage mes2 = new ShortMessage();
            try{ mes2.setMessage(ShortMessage.NOTE_ON, 2, 96-i, 40); }
            catch (Exception e)
            {
                return;
            }
            //track1.add(new MidiEvent(mes1, 1));
            track2.add(new MidiEvent(mes2, 300*i));
            ShortMessage mes = new ShortMessage();
            try{ mes.setMessage(ShortMessage.NOTE_OFF, 2, 96-i, 0); }
            catch (Exception e)
            {
                return;
            }
            //track1.add(new MidiEvent(mes1, 1));
            track2.add(new MidiEvent(mes, 300*(i+1)-1));
        }//*/
        Sequence tape2;
        try{tape2 = new Sequence(Sequence.PPQ, res);}
        catch (Exception e)
        {
            return;
        }
        Track t1 = tape2.createTrack();
       /* for(int i = 0; i < 128; i++)
        {
            mes = new ShortMessage();
            try{ mes.setMessage(ShortMessage.NOTE_ON, 1, 80, 60); }
            catch (Exception e)
            {
                return;
            }
            t1.add(new MidiEvent(mes, 1+50*i));
            mes = new ShortMessage();
            try{ mes.setMessage(ShortMessage.PITCH_BEND, 1, 0, i); }
            catch (Exception e)
            {
                System.out.println("Message: "+e.getMessage());
                return;
            }
            t1.add(new MidiEvent(mes, 1+50*i));
        }//*/
            mes = new ShortMessage();
            try{ mes.setMessage(ShortMessage.NOTE_ON, 1, 80, 60); }
            catch (Exception e)
            {
                return;
            }
            t1.add(new MidiEvent(mes, 1));
            
            mes = new ShortMessage();
            try{ mes.setMessage(ShortMessage.PITCH_BEND, 1, 0, 63); }
            catch (Exception e)
            {
                return;
            }
            t1.add(new MidiEvent(mes, 999));//*/
            
            mes = new ShortMessage();
            try{ mes.setMessage(ShortMessage.NOTE_ON, 1, 80, 60); }
            catch (Exception e)
            {
                return;
            }
            t1.add(new MidiEvent(mes, 1000));
           /* mes = new ShortMessage();
            try{ mes.setMessage(ShortMessage.NOTE_OFF, 1, 80, 0); }
            catch (Exception e)
            {
                return;
            }
            t1.add(new MidiEvent(mes, 999));
            mes = new ShortMessage();
            try{ mes.setMessage(ShortMessage.PITCH_BEND, 1, 0, 64); }
            catch (Exception e)
            {
                return;
            }
            t1.add(new MidiEvent(mes, 1000));
            mes = new ShortMessage();
            try{ mes.setMessage(ShortMessage.NOTE_ON, 1, 81, 60); }
            catch (Exception e)
            {
                return;
            }
            t1.add(new MidiEvent(mes, 1000));
            //*/
            mes = new ShortMessage();
            try{ mes.setMessage(ShortMessage.NOTE_OFF, 1, 81, 0); }
            catch (Exception e)
            {
                return;
            }
            t1.add(new MidiEvent(mes, 1999));//*/
        try{ seq.setSequence(tape2); }
        catch (Exception e)
        {
            System.out.println("Insertion of tape failed!");
            return;
        }
        seq.start();
  //--------------------------------------------------------
        p = new JPanel();
        p.addMouseListener(new MouseAdapter()
        {
            public void mouseEntered(MouseEvent e)
            {
              //  p.setBackground(Color.red);
            }
            public void mouseExited(MouseEvent e)
            {
               // p.setBackground(Color.blue);
            }
        });
        JLabel label = new JLabel("Begins NOW!!!");
        StaffTest st = new StaffTest(label);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(st, BorderLayout.CENTER);
        getContentPane().add(label, BorderLayout.SOUTH);
        pack();
        setSize(200, 200);
        setVisible(true);
    }
    public void openMidiResources(Synthesizer synth, Sequencer seq)
    {
        
    }
    public static void main(String args[])
    { MusicalTest1 m = new MusicalTest1(); }
}
