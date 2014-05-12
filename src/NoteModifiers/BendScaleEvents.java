package NoteModifiers;

import DataStructure.*;
import Features.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;
import javax.sound.midi.*;

public class BendScaleEvents implements NoteEvents
{
    public int semitones;
    //precondition: 0 <= Extrema(note.miniNote.value)+semitones < 128
    public BendScaleEvents(int semitones)
    {
        this.semitones = semitones;
    }
    public void drawEvent(Graphics2D g, double noteX, double noteY, double noteLength, double staffWidth, boolean stemUp, Note note)
    {
        g.setColor(Color.black);
        if(semitones > 0)
        {
            double h = 14*staffWidth*semitones/12;
            g.draw(new Arc2D.Double(noteX-noteLength, noteY-h, noteLength*2, h, 270, 90, Arc2D.OPEN));
        }
        else
        {
            double h = 14*staffWidth*Math.abs(semitones)/12;
            g.draw(new Arc2D.Double(noteX, noteY-h/2, noteLength*2, h, 180, 90, Arc2D.OPEN));
        }
    }
    public void generateEvents(Track track, int channel, long posit, long noteLength, Note note)
    {
        int sems = semitones;
        int min = 128, max = -1;
        for(Enumeration en = note.getNotes().elements(); en.hasMoreElements();)
        {
            Note.MiniNote curMiniNote = (Note.MiniNote)en.nextElement();
            if(min > curMiniNote.getValue()) min = curMiniNote.getValue();
            if(max < curMiniNote.getValue()) max = curMiniNote.getValue();
        }
             if(max+sems > 127) sems = 125-max;
        else if(min+sems < 0)   sems = 2-min;
        if(sems > 0)
        {
            int curSem = 0;
            int lastSwitch = 0;
            while(curSem < sems)
            {
                if(curSem % 4 == 0)
                {
                    for(int i = 0; i < 32; i++)//bend front of THIS note up
                    {
                        track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND, channel, 0, (int)(64+i), (long)(posit+(double)i*noteLength/(sems*32)+noteLength*curSem/sems)));
                    }
                    curSem++;
                }
                else if(curSem % 4 == 1)
                {
                    for(int i = 0; i < 32; i++)//bend front of THIS note up
                    {
                        track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND, channel, 0, (int)(96+i), (long)(posit+(double)i*noteLength/(sems*32)+noteLength*curSem/sems)));
                    }
                    curSem++;
                }
                else if(curSem % 4 == 2)
                {//uh-oh... time to switch Notes...
                    for(Enumeration en = note.getNotes().elements(); en.hasMoreElements();)
                    {
                        Note.MiniNote curMiniNote = (Note.MiniNote)en.nextElement();
                        track.add(MidiScoreUtil.createShortEvent(ShortMessage.NOTE_OFF,  channel, curMiniNote.getValue()+curSem-2, 0, (long)(posit+curSem*noteLength/sems)));
                        track.add(MidiScoreUtil.createShortEvent(ShortMessage.NOTE_ON,   channel, curMiniNote.getValue()+curSem+2, note.getVolume(), (long)(posit+curSem*noteLength/sems)+1));
                    }//*/
                    lastSwitch = curSem+2;
                    for(int i = 0; i < 32; i++)//bend front of THIS note up
                    {
                        track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND, channel, 0, (int)(32-i), (long)(posit+(double)(32-i)*noteLength/(sems*32)+noteLength*curSem/sems)));
                    }
                    curSem++;
                }
                else//ie-- if(curSem % 4 == 3)
                {
                    for(int i = 0; i < 32; i++)//bend front of THIS note up
                    {
                        track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND, channel, 0, (int)(64-i), (long)(posit+(double)(32-i)*noteLength/(sems*32)+noteLength*curSem/sems)));
                    }
                    curSem++;
                }
            }
            for(Enumeration en = note.getNotes().elements(); en.hasMoreElements();)
            {
                Note.MiniNote curMiniNote = (Note.MiniNote)en.nextElement();
                track.add(MidiScoreUtil.createShortEvent(ShortMessage.NOTE_OFF,  channel, curMiniNote.getValue()+lastSwitch, 0, (long)(posit+noteLength)));
            }//*/
        }
        else //ie-- if(Math.abs(sems) < 0)
        {
            int curSem = 0;
            int lastSwitch = 0;
            while(curSem < Math.abs(Math.abs(sems)))
            {
                if(curSem % 4 == 0)
                {
                    for(int i = 0; i < 32; i++)//bend front of THIS note up
                    {
                        track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND, channel, 0, (int)(64-i), (long)(posit+(double)i*noteLength/(Math.abs(sems)*32)+noteLength*curSem/Math.abs(sems))));
                    }
                    curSem++;
                }
                else if(curSem % 4 == 1)
                {
                    for(int i = 0; i < 32; i++)//bend front of THIS note up
                    {
                        track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND, channel, 0, (int)(32-i), (long)(posit+(double)i*noteLength/(Math.abs(sems)*32)+noteLength*curSem/Math.abs(sems))));
                    }
                    curSem++;
                }
                else if(curSem % 4 == 2)
                {//uh-oh... time to switch Notes...
                    for(Enumeration en = note.getNotes().elements(); en.hasMoreElements();)
                    {
                        Note.MiniNote curMiniNote = (Note.MiniNote)en.nextElement();
                        track.add(MidiScoreUtil.createShortEvent(ShortMessage.NOTE_OFF,  channel, curMiniNote.getValue()-curSem+2, 0, (long)(posit+curSem*noteLength/Math.abs(sems))));
                        track.add(MidiScoreUtil.createShortEvent(ShortMessage.NOTE_ON,   channel, curMiniNote.getValue()-curSem-2, note.getVolume(), (long)(posit+curSem*noteLength/Math.abs(sems))+1));
                    }//*/
                    lastSwitch = -curSem-2;
                    for(int i = 0; i < 32; i++)//bend front of THIS note up
                    {
                        track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND, channel, 0, (int)(96+i), (long)(posit+(double)(32-i)*noteLength/(Math.abs(sems)*32)+noteLength*curSem/Math.abs(sems))));
                    }
                    curSem++;
                }
                else//ie-- if(curSem % 4 == 3)
                {
                    for(int i = 0; i < 32; i++)//bend front of THIS note up
                    {
                        track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND, channel, 0, (int)(64+i), (long)(posit+(double)(32-i)*noteLength/(Math.abs(sems)*32)+noteLength*curSem/Math.abs(sems))));
                    }
                    curSem++;
                }
            }
            for(Enumeration en = note.getNotes().elements(); en.hasMoreElements();)
            {
                Note.MiniNote curMiniNote = (Note.MiniNote)en.nextElement();
                track.add(MidiScoreUtil.createShortEvent(ShortMessage.NOTE_OFF,  channel, curMiniNote.getValue()+lastSwitch, 0, (long)(posit+noteLength)));
            }//*/
        }
        /*if(note.held())
        {
            for(Enumeration en = note.getNotes().elements(); en.hasMoreElements();)
            {
                Note.MiniNote curMiniNote = (Note.MiniNote)en.nextElement();
                track.add(MidiScoreUtil.createShortEvent(ShortMessage.NOTE_ON,  channel, curMiniNote.getValue()+sems, note.getVolume(), (long)(posit+noteLength)));
            }
        }//*/
        track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND,  channel, 0, 63, (long)(posit+noteLength)));
    }
    public void saveConstructorData(PrintStream stream, int indent)
    {
        MiscHelper.saveInteger(stream, indent, semitones);
    }
}
