package NoteModifiers;

import DataStructure.*;
import Features.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import javax.sound.midi.*;

public class CrescFrontEvent implements NoteEvents
{
    public int percent;
    public int firstVolume;
    final double angleSpread = 1.2;
    final double extent = 0.5;
    public CrescFrontEvent(int percent, int firstVolume)
    {
        this.percent = percent;
        this.firstVolume = firstVolume;
    }
    public void drawEvent(Graphics2D g, double noteX, double noteY, double noteLength, double staffWidth, boolean stemUp, Note note)
    {
        g.setColor(Color.black);
        g.draw(new Line2D.Double(noteX+1.5*staffWidth, noteY, noteX+1.5*staffWidth+extent*noteLength, noteY-angleSpread*staffWidth));
        g.draw(new Line2D.Double(noteX+1.5*staffWidth, noteY, noteX+1.5*staffWidth+extent*noteLength, noteY+angleSpread*staffWidth));
        if(firstVolume == 0) g.drawString("n", (int)(noteX+staffWidth), (int)(noteY-staffWidth));
    }
    public void generateEvents(Track track, int channel, long posit, long noteLength, Note note)
    {
        int steps = (int)Math.abs(note.getVolume() - firstVolume);
        for(int i = 0; i < steps; i++)
        {
            long tick = (long)(posit + noteLength*((double)i/Math.abs(steps)*(double)percent/100));
            track.add(MidiScoreUtil.createShortEvent(ShortMessage.CONTROL_CHANGE , channel, 7, (int)(127.0*(note.getVolume()-steps+i)/note.getVolume()), tick));
        }
    }
    public void saveConstructorData(PrintStream stream, int indent)
    {
        MiscHelper.saveInteger(stream, indent, percent);
        MiscHelper.saveInteger(stream, indent, firstVolume);
    }
}
