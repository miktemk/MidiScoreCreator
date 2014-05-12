package NoteModifiers;

import DataStructure.*;
import Features.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import javax.sound.midi.*;

public class BendEnd_Rough implements NoteEvents
{
    public static final int UP = 1,
                            DOWN = 2;
    public int percent;
    public int direction;
    public boolean chokeChannels;
    public BendEnd_Rough(int percent, int direction, boolean chokeChannels)
    {
        this.percent = percent;
        this.direction = direction;
        this.chokeChannels = chokeChannels;
    }
    public void drawEvent(Graphics2D g, double noteX, double noteY, double noteLength, double staffWidth, boolean stemUp, Note note)
    {
        g.setColor(Color.red);
        if(direction == DOWN) g.draw(new Arc2D.Double(noteX-0.75*noteLength, noteY,              1.5*noteLength, staffWidth*3, 0,   90, Arc2D.OPEN));
        else                  g.draw(new Arc2D.Double(noteX-0.75*noteLength, noteY-staffWidth*3, 1.5*noteLength, staffWidth*3, 270, 90, Arc2D.OPEN));
    }
    public void generateEvents(Track track, int channel, long posit, long noteLength, Note note)
    {
        if(direction == DOWN)
        {
            for(int i = 0; i < 64; i++)
            {
                track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND, channel, 0, (int)(64-i), (long)(posit + noteLength*(1+(double)percent/100.0*((double)i/64 - 1))) ));
            }
        }
        else
        {
            for(int i = 0; i < 64; i++)
            {
                track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND, channel, 0, (int)(64+i), (long)(posit+noteLength*(1+(double)percent/100.0*((double)i/64 - 1)))));
            }
        }
        if(chokeChannels) track.add(MidiScoreUtil.createShortEvent(ShortMessage.CONTROL_CHANGE , channel, 120, 0, posit+noteLength));
        track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND,      channel, 0, 63,  posit+noteLength+1));
    }
    public void saveConstructorData(PrintStream stream, int indent)
    {
        MiscHelper.saveInteger(stream, indent, percent);
        MiscHelper.saveInteger(stream, indent, direction);
        MiscHelper.saveBoolean(stream, indent, chokeChannels);
    }
}
