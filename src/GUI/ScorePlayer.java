package GUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import javax.sound.midi.*;
import DataStructure.*;
import NoteModifiers.*;
import Features.*;

public class ScorePlayer extends JPanel implements ActionListener
{
    private class PositSlider extends JPanel
    {
        Sequencer s;
        double posit = 0;
        long baseTime = 0;
        boolean isPlaying = false;
        public PositSlider(Sequencer s)
        {
            this.s = s;
            setPreferredSize(new Dimension(200, 20));
            addMouseListener(new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                    press(e);
                }
            });
        }
        public void press(MouseEvent e)
        {
            double pos = (double)e.getX()/getWidth();
            Sequence sq = s.getSequence();
            if(sq != null && isPlaying)
            {
                long totalL = sq.getTickLength();
                s.setTickPosition((long)(pos*totalL));
                baseTime += (long)((System.currentTimeMillis() - baseTime)*(1-pos/posit));
                setPosition(pos);
            }
        }
        public void setBaseTimeNow()
        {
            baseTime = System.currentTimeMillis();
        }
        public void setIsPlaying(boolean aflag)
        {
            isPlaying = aflag;
            if(!isPlaying)
            {
                setBaseTimeNow();
                setPosition(0);
            }
        }
        public void setPosition(double pos)
        {
            if(posit != pos)
            {
                this.posit = pos;
                repaint();
            }
            if(posit == 0)
            {
                setBaseTimeNow();
            }
        }
        public void paint(Graphics g1)
        {
            Graphics2D g = (Graphics2D)g1;
            g.setColor(Color.white);
            g.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
            g.setColor(Color.cyan);
            g.fill(new Rectangle2D.Double(0, 0, getWidth()*posit, getHeight()));
            g.setColor(Color.black);
            long secs = (System.currentTimeMillis() - baseTime)/1000;
            int seconds = (int)(secs % 60);
            int minutes = (int)(secs / 60);
            String time;
            if(seconds < 10) time = minutes+":0"+seconds;
            else             time = minutes+":"+seconds;
            g.drawString(time, getWidth()/2-20, getHeight()-5);
        }
    }
    private class Checker extends Thread
    {
        Sequencer seq;
        ScorePanel scoreP;
        PositSlider slid;
        long length = 1;
        public Checker(Sequencer seq, ScorePanel scoreP, PositSlider slid)
        {
            this.seq = seq;
            this.scoreP = scoreP;
            this.slid = slid;
        }
        public void run()
        {
            while(true)
            {
                boolean halted = !seq.isRunning();
                scoreP.setSound(halted);
                slid.setIsPlaying(!halted);
                if(!halted) slid.setPosition((double)seq.getTickPosition()/length);
                try{ sleep(20); } catch(InterruptedException e) {}
            }
        }
        public void setLength(long length)
        {
            this.length = length;
        }
    }
    private class MainPlayButton extends JButton
    {
        Border myBorder;
        public MainPlayButton(String name)
        {
            super(name);
            myBorder = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), BorderFactory.createLineBorder(Color.blue)), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.cyan), BorderFactory.createLineBorder(Color.white)));
            addMouseListener(new MouseAdapter()
            {
                public void mouseEntered(MouseEvent e)
                {
                    setBorder(myBorder);
                }
                public void mouseExited(MouseEvent e)
                {
                    setBorder(null);
                }
            });
        }
    }
    private class MIDIFilter extends javax.swing.filechooser.FileFilter
    {
        public MIDIFilter(){}
        public boolean accept(File pathname)
        {
            if(pathname.isDirectory()) return true;
            else
            {
                if(pathname.getName().indexOf(".mid") != -1) return true;
                else                                         return false;
            }
        }
        public String getDescription()
        { return "MIDI Files (*.mid)"; }
    }
    Sequencer seq;
    Sequence tape = null;
    ScorePanel scoreP;
    Checker checker;
    JFileChooser fileChooser;
    javax.swing.filechooser.FileFilter filter;
    public void openMidiResources()
    {
        try
        {
            seq = MidiSystem.getSequencer();
            seq.open();
        }
        catch (Exception e)
        {
            return;
        }
        try{tape = new Sequence(Sequence.PPQ, MidiScoreUtil.resolution);}
        catch (Exception e)
        {
            return;
        }
    }
    public ScorePlayer(ScorePanel scoreP)
    {
        super(new BorderLayout());
        this.scoreP = scoreP;
        openMidiResources();
        MainPlayButton playCurButton  = new MainPlayButton("Play from CURRENT page");
        JButton playAllButton  = new JButton("Play All");
        JButton stopButton  = new JButton("STOP");
        JButton export = new JButton("Export MIDI file");
        playCurButton.setPreferredSize(new Dimension(200, 45));
        export.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.cyan), BorderFactory.createLineBorder(Color.green)));
        PositSlider slid = new PositSlider(seq);
        
        playCurButton.addActionListener(this);
        playAllButton.addActionListener(this);
        stopButton.addActionListener(this);
        export.addActionListener(this);
        
        setLayout(new BorderLayout());
        JPanel middle = new JPanel(new GridLayout(1, 2));
        middle.add(playAllButton);
        middle.add(stopButton);
        JPanel top = new JPanel(new BorderLayout());
        top.add(playCurButton, BorderLayout.CENTER);
        top.add(slid, BorderLayout.NORTH);
        add(top, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);
        add(export, BorderLayout.SOUTH);
        
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.black), BorderFactory.createLineBorder(Color.black)));
        
        scoreP.setPlayer(this);
        checker = new Checker(seq, scoreP, slid);
        checker.start();
        
        fileChooser = new JFileChooser();
        filter = new MIDIFilter();
    }
    public void actionPerformed(ActionEvent e)
    {
        JButton source = (JButton)e.getSource();
        if(source.getText() == "Play All")
        {
            scoreP.setSound(false);
            play(scoreP.score, 0);
        }
        else if(source.getText() == "Play from CURRENT page")
        {
            scoreP.setSound(false);
            play(scoreP.score, scoreP.barPosit);
        }
        else if(source.getText() == "STOP")
        {
            scoreP.setSound(true);
            stop();
        }
        else if(source.getText() == "Export MIDI file")
        {
            saveMIDI();
        }
    }
    public void play(Score score, int barPosit)
    {
        stop();
        seq.setTempoInBPM(120.0f);
        tape = MidiScoreUtil.encodeSequence(score, barPosit, seq, scoreP.instruments);
        checker.setLength(tape.getTickLength());
        try{ seq.setSequence(tape); }
        catch (Exception ex)
        {
            System.out.println("Insertion of tape failed!");
            return;
        }
        seq.start();
    }
    public void playSolo(Score score, BandSection section, Part part, int barPosit, int channel)
    {
        stop();
        seq.setTempoInBPM(120.0f);
        tape = MidiScoreUtil.encodeSoloSequence(score, section, part, barPosit, channel, seq, scoreP.instruments);
        checker.setLength(tape.getTickLength());
        try{ seq.setSequence(tape); }
        catch (Exception ex)
        {
            System.out.println("Insertion of tape failed!");
            return;
        }
        seq.start();
    }
    public void playScale(int[] scale, float tempo)
    {
        stop();
        seq.setTempoInBPM(120f);
        tape = MidiScoreUtil.encodeScale(scale, seq, tempo);
        checker.setLength(tape.getTickLength());
        try{ seq.setSequence(tape); }
        catch (Exception ex)
        {
            System.out.println("Insertion of tape failed!");
            return;
        }
        seq.start();
    }
    public void stop()
    {
        seq.stop();
        seq.setMicrosecondPosition((long)0);
    }
    public void saveMIDI()
    {
        fileChooser.setFileFilter(filter);
        int option = getSaveFileOption();
        if(option == JFileChooser.APPROVE_OPTION)
        {
            File target = fileChooser.getSelectedFile();
            if(target.getName().indexOf(".mid") == -1) target = new File(target.getPath()+".mid");
            Sequence sequence = MidiScoreUtil.encodeSequence(scoreP.score, 0, seq, scoreP.instruments);
            try
            {
                MidiSystem.write(sequence, 1, target);
            }
            catch(Exception ex)
            {
                JOptionPane.showMessageDialog(this, new JLabel("Error: "+ex.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    /** This checks for fileExists/not
      * @return an JFileChooser option*/
    public int getSaveFileOption()
    {
        int option = fileChooser.showSaveDialog(scoreP);
        while(option == JFileChooser.APPROVE_OPTION)
        {
            File target = fileChooser.getSelectedFile();
            if(!target.exists()) return option;
            int subOpt = JOptionPane.showConfirmDialog(fileChooser, new JLabel(target.getName()+" exists. Overwrite?"), "File Exists", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if(subOpt == JOptionPane.CANCEL_OPTION)   return JFileChooser.CANCEL_OPTION;
            else if(subOpt == JOptionPane.YES_OPTION) return JFileChooser.APPROVE_OPTION;
            option = fileChooser.showSaveDialog(scoreP);
        }
        return option;
    }
    //------------------------THIS IS NOT WORTHY OF USAGE---------------------------
    /*
    public Sequence encodeSequence2()
    {
        Sequence sequence;
        try{sequence = new Sequence(Sequence.PPQ, resolution);}
        catch (Exception e)
        {
            System.out.println("can not init sequence");
            return null;
        }
        BandSection[] sections = score.getSections();
        for(int i = 0; i < sections.length; i++)
        {
            //add Instrument Change Events;
            Track programTrack = sequence.createTrack();
            int partNumber = 0;
            for(Enumeration e = sections[i].getParts().elements(); e.hasMoreElements();)
            {
                Part curPart = (Part)e.nextElement();
                Track curTrack = sequence.createTrack();
                double barPosit = 0; // the position in the part in term of bar lengths, eg 6/8 + 4/4 etc
                int barNumber = 0;
                long totalOverTick = 0;
                Note prevNote = null;
                for(Enumeration e2 = curPart.getBars().elements(); e2.hasMoreElements();)
                {
                    Bar curBar = (Bar)e2.nextElement();
                    double inBarPosit = 0; // position within bar, eg 2/4 in a 4/4 bar
                    for(Enumeration e3 = curBar.getNotes().elements(); e3.hasMoreElements();)
                    {
                        //adding actual Notes;
                        Note curNote = (Note)e3.nextElement();
                        long thisTick = (long)(4*resolution*(inBarPosit+barPosit));
                        long overTick = (long)(4*resolution*(inBarPosit+barPosit+curNote.getLength()));
                        if((prevNote != null)&&(!prevNote.slured))
                        {
                            for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                            {
                                Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  i, curMiniNote.getValue(), curNote.getVolume(), thisTick));
                                curTrack.add(createShortEvent(ShortMessage.NOTE_OFF, i, curMiniNote.getValue(), 0, overTick));
                            }
                        }
                        else(()())
                        if(prevNote == null)
                        {
                            for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                            {
                                Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  i, curMiniNote.getValue(), curNote.getVolume(), thisTick));
                                totalOverTick = overTick;
                            }
                        }
                        else
                        {
                            if(!prevNote.slured)
                            {
                                for(Enumeration e4 = prevNote.getNotes().elements(); e4.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  i, curMiniNote.getValue(), 0, thisTick));
                                }
                                for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  i, curMiniNote.getValue(), curNote.getVolume(), thisTick));
                                    totalOverTick = overTick;
                                }
                            }
                            else
                            {
                                for(Enumeration e4 = prevNote.getNotes().elements(); e4.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                    System.out.println(""+curMiniNote.getValue()+"   "+curNote.getNotes().size());
                                    boolean stopNote = true;
                                    for(Enumeration e5 = curNote.getNotes().elements(); e5.hasMoreElements();)
                                    {
                                        Note.MiniNote curMiniNote2 = (Note.MiniNote)e5.nextElement();
                                        if(curMiniNote.getValue() == curMiniNote2.getValue())
                                        {
                                            stopNote = false;
                                            break;
                                        }
                                    }
                                    if(stopNote) curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  i, curMiniNote.getValue(), 0, thisTick));
                                    else totalOverTick = overTick;
                                }
                                for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                    boolean startNote = true;
                                    for(Enumeration e5 = prevNote.getNotes().elements(); e5.hasMoreElements();)
                                    {
                                        Note.MiniNote curMiniNote2 = (Note.MiniNote)e5.nextElement();
                                        if(curMiniNote.getValue() == curMiniNote2.getValue())
                                        {
                                            startNote = false;
                                            break;
                                        }
                                    }
                                    if(startNote)
                                    {
                                        curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  i, curMiniNote.getValue(), curNote.getVolume(), thisTick));
                                        totalOverTick = overTick;
                                    }
                                }
                            }
                        }
                        inBarPosit += curNote.getLength();
                        prevNote = curNote;
                    }
                    //-----adding Program changes-----------------
                    if(partNumber == 0)
                    {
                        for(Enumeration e5 = sections[i].getInstrumentChangeEvents().elements(); e5.hasMoreElements();)
                        {
                            BandSection.InstrumentChangeEvent curEvent = (BandSection.InstrumentChangeEvent)e5.nextElement();
                            if(curEvent.barNumberOfOccurence == barNumber)
                            {
                                programTrack.add(createShortEvent(ShortMessage.PROGRAM_CHANGE, i, curEvent.instrumentNumber, 0, (long)(4*resolution*(barPosit))));
                               // System.out.println(""+curEvent.barNumberOfOccurence);
                            }
                        }
                    }
                    barPosit += curBar.getMaxLength();
                    barNumber++;
                }
                if(prevNote != null)
                {
                    for(Enumeration e4 = prevNote.getNotes().elements(); e4.hasMoreElements();)
                    {
                        Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                        curTrack.add(createShortEvent(ShortMessage.NOTE_OFF, i, curMiniNote.getValue(), 0, totalOverTick));
                    }
                }
                partNumber++;
            }
            //add Tracks.
        }
        return sequence;
    }//*/
 //----------------------------------------------------------------------------------------
    
    public MidiEvent createShortEvent(int command, int channel, int data1, int data2, long tick)
    {
        ShortMessage mes = new ShortMessage();
        try{ mes.setMessage(command, channel, data1, data2); }
        catch (Exception e)
        {
            System.out.println("can not set Message: "+e.getMessage());
            return null;
        }
        return (new MidiEvent(mes, tick));
    }
}
