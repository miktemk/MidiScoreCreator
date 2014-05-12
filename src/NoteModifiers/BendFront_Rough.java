package NoteModifiers;

import DataStructure.*;
import Features.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import javax.sound.midi.*;

public class BendFront_Rough implements NoteEvents
{
    public static final int UP = 1,
                            DOWN = 2;
    public int percent;
    public int direction;
    public BendFront_Rough(int percent, int direction)
    {
        this.percent = percent;
        this.direction = direction;
    }
    public void drawEvent(Graphics2D g, double noteX, double noteY, double noteLength, double staffWidth, boolean stemUp, Note note)
    {
        g.setColor(Color.red);
        if(direction == DOWN) g.draw(new Arc2D.Double(noteX+staffWidth*3-noteLength, noteY,              2*noteLength - staffWidth*6, staffWidth*3, 90,  90, Arc2D.OPEN));
        else                  g.draw(new Arc2D.Double(noteX+staffWidth*3-noteLength, noteY-staffWidth*3, 2*noteLength - staffWidth*6, staffWidth*3, 180, 90, Arc2D.OPEN));
    }
    public void generateEvents(Track track, int channel, long posit, long noteLength, Note note)
    {
        if(direction == DOWN)
        {
            for(int i = 0; i < 64; i++)
            {
                long tick = (long)(posit + noteLength*((double)i*percent/6400));
                track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND, channel, 0, i, tick));
            }
        }
        else
        {
            for(int i = 0; i < 64; i++)
            {
                long tick = (long)(posit + noteLength*((double)i*percent/6400));
                track.add(MidiScoreUtil.createShortEvent(ShortMessage.PITCH_BEND, channel, 0, 127-i, tick));
            }
        }
    }
    public void saveConstructorData(PrintStream stream, int indent)
    {
        MiscHelper.saveInteger(stream, indent, percent);
        MiscHelper.saveInteger(stream, indent, direction);
    }
}
