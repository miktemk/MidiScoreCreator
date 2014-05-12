package NoteModifiers;

import DataStructure.*;
import Features.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;
import javax.sound.midi.*;

public class GraceScaleEvents implements NoteEvents
{
    public String name;
    public int[] scale;  //normilized scale
    public int sclNotes;
    public GraceScaleEvents(String name, int[] scale, int sclNotes)
    {
        this.name = name;
        this.scale = scale;
        this.sclNotes = sclNotes;
    }
    public void drawEvent(Graphics2D g, double noteX, double noteY, double noteLength, double staffWidth, boolean stemUp, Note note)
    {
        //1. find semitones
        //2. draw stair like
        int semitones = 0;
        if(sclNotes > 0) semitones = scale[scale.length-1]*(int)((sclNotes-1)/(scale.length-1)) + scale[(sclNotes-1) % (scale.length-1)];
        else             semitones = scale[scale.length-1]*((int)((sclNotes+1)/(scale.length-1))-1) + scale[scale.length-1+(sclNotes+1) % (scale.length-1)];
        if(Math.abs(semitones) < 5) semitones = ((semitones > 0)?1:-1)*5;
        int widths = 7*semitones/12;
        g.setColor(Color.black);
        g.setFont(new Font(null, Font.PLAIN, 9));
        if(widths > 0)
        {
            for(int i = 0; i <= widths; i++)
            {
                g.draw(new Line2D.Double(noteX+i*noteLength/(widths+1), noteY-i*staffWidth, noteX+(i+1)*noteLength/(widths+1), noteY-i*staffWidth));
                if(i < widths) g.draw(new Line2D.Double(noteX+(i+1)*noteLength/(widths+1), noteY-i*staffWidth, noteX+(i+1)*noteLength/(widths+1), noteY-(i+1)*staffWidth));
            }
            g.drawString(name, (int)(noteX + 2*staffWidth), (int)(noteY+6));
        }
        else
        {
            for(int i = 0; i <= -widths; i++)
            {
                g.draw(new Line2D.Double(noteX+i*noteLength/(-widths+1), noteY+i*staffWidth, noteX+(i+1)*noteLength/(-widths+1), noteY+i*staffWidth));
                if(i < -widths) g.draw(new Line2D.Double(noteX+(i+1)*noteLength/(-widths+1), noteY+i*staffWidth, noteX+(i+1)*noteLength/(-widths+1), noteY+(i+1)*staffWidth));
            }
            g.drawString(name, (int)(noteX + 2*staffWidth), (int)(noteY-2));
        }
        g.setFont(new Font(null, Font.PLAIN, 12));
    }
    public void generateEvents(Track track, int channel, long posit, long noteLength, Note note)
    {
        int offSet = 0;
        if(sclNotes > 0)
        {
            for(int i = 1; i <= sclNotes; i++)
            {
                long curTick = i*noteLength/sclNotes;
                int prevOff = offSet;
                if(i % (scale.length-1) == 0) offSet += scale[scale.length-1];
                for(Enumeration en = note.getNotes().elements(); en.hasMoreElements();)
                {
                    Note.MiniNote cur = (Note.MiniNote)en.nextElement();
                    if(i == sclNotes) track.add(MidiScoreUtil.createShortEvent_Safe(ShortMessage.NOTE_OFF, channel, cur.getValue()+prevOff+scale[(i-1)%(scale.length-1)], 0, posit+curTick-1));
                    else
                    {
                        track.add(MidiScoreUtil.createShortEvent_Safe(ShortMessage.NOTE_OFF, channel, cur.getValue()+prevOff+scale[(i-1)%(scale.length-1)], 0, posit+curTick-1));
                        track.add(MidiScoreUtil.createShortEvent_Safe(ShortMessage.NOTE_ON,  channel, cur.getValue()+offSet+scale[i%(scale.length-1)], note.getVolume(), posit+curTick+1));
                    }
                }
            }
        }
        else //ie - if(sclNotes < 0)
        {
            for(int i = 1; i <= -sclNotes; i++)
            {
                long curTick = -i*noteLength/sclNotes;
                int prevOff = offSet;
                if(i % (scale.length-1) == 0) offSet -= scale[scale.length-1];
                for(Enumeration en = note.getNotes().elements(); en.hasMoreElements();)
                {
                    Note.MiniNote cur = (Note.MiniNote)en.nextElement();
                    if(i == -sclNotes) track.add(MidiScoreUtil.createShortEvent_Safe(ShortMessage.NOTE_OFF, channel, cur.getValue()+prevOff-scale[scale.length-1]+scale[scale.length-1-(i-1)%(scale.length-1)], 0, posit+curTick-1));
                    else
                    {
                        track.add(MidiScoreUtil.createShortEvent_Safe(ShortMessage.NOTE_OFF, channel, cur.getValue()+prevOff-scale[scale.length-1]+scale[scale.length-1-(i-1)%(scale.length-1)], 0, posit+curTick-1));
                        track.add(MidiScoreUtil.createShortEvent_Safe(ShortMessage.NOTE_ON,  channel, cur.getValue()+offSet -scale[scale.length-1]+scale[scale.length-1-i%(scale.length-1)], note.getVolume(), posit+curTick+1));
                    }
                }
            }
        }
    }
    public void saveConstructorData(PrintStream stream, int indent)
    {
        MiscHelper.saveString(stream, indent, name);
        MiscHelper.saveIntArray(stream, indent, scale);
        MiscHelper.saveInteger(stream, indent, sclNotes);
    }
}
