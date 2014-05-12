package NoteModifiers;

import DataStructure.*;
import Features.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import javax.sound.midi.*;

public class FPEvent implements NoteEvents
{
    public int skewage;
    public int minVolume;
    final double angleSpread = 1.2;
    final double extent = 0.5;
    public FPEvent(int skewage, int minVolume)
    {
        this.skewage = skewage;
        this.minVolume = minVolume;
    }
    public void drawEvent(Graphics2D g, double noteX, double noteY, double noteLength, double staffWidth, boolean stemUp, Note note)
    {
        g.setColor(Color.black);
        if(stemUp) g.drawString("fp", (int)noteX, (int)(noteY + 3*staffWidth));
        else       g.drawString("fp", (int)noteX, (int)(noteY - staffWidth));
    }
    public void generateEvents(Track track, int channel, long posit, long noteLength, Note note)
    {
        int steps = (int)Math.abs(note.getVolume() - minVolume);
        for(int i = 0; i < steps; i++)
        {
            long tick = (long)(posit + noteLength*((double)i/Math.abs(steps)*(double)skewage/100));
            track.add(MidiScoreUtil.createShortEvent(ShortMessage.CONTROL_CHANGE , channel, 7, (int)(127.0*(note.getVolume()-i)/note.getVolume()), tick));
            long tick2 = (long)(posit + noteLength*((double)skewage/100 + (double)i/Math.abs(steps)*(1-(double)skewage/100)));
            track.add(MidiScoreUtil.createShortEvent(ShortMessage.CONTROL_CHANGE , channel, 7, (int)(127.0*(note.getVolume()-steps+i+1)/note.getVolume()), tick2));
        }
    }
    public void saveConstructorData(PrintStream stream, int indent)
    {
        MiscHelper.saveInteger(stream, indent, skewage);
        MiscHelper.saveInteger(stream, indent, minVolume);
    }
}
