package NoteModifiers;

import DataStructure.*;
import Features.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import javax.sound.midi.*;

public class DecrescEndEvent implements NoteEvents
{
    public int percent;
    public int finalVolume;
    public boolean chokeChannels;
    final double angleSpread = 1.2;
    final double extent = 0.5;
    public DecrescEndEvent(int percent, int finalVolume, boolean chokeChannels)
    {
        this.percent = percent;
        this.finalVolume = finalVolume;
        this.chokeChannels = chokeChannels;
    }
    public void drawEvent(Graphics2D g, double noteX, double noteY, double noteLength, double staffWidth, boolean stemUp, Note note)
    {
        g.setColor(Color.black);
        if(note.getVolume() > finalVolume)
        {// decresc sign (>)
            g.draw(new Line2D.Double(noteX+1.5*staffWidth, noteY-angleSpread*staffWidth, noteX+1.5*staffWidth+extent*noteLength, noteY));
            g.draw(new Line2D.Double(noteX+1.5*staffWidth, noteY+angleSpread*staffWidth, noteX+1.5*staffWidth+extent*noteLength, noteY));
        }
        else
        {// cresc sign (<)
            g.draw(new Line2D.Double(noteX+1.5*staffWidth, noteY, noteX+1.5*staffWidth+extent*noteLength, noteY-angleSpread*staffWidth));
            g.draw(new Line2D.Double(noteX+1.5*staffWidth, noteY, noteX+1.5*staffWidth+extent*noteLength, noteY+angleSpread*staffWidth));
        }
        if(finalVolume == 0) g.drawString("n", (int)(noteX+extent*noteLength), (int)(noteY-staffWidth));
    }
    public void generateEvents(Track track, int channel, long posit, long noteLength, Note note)
    {
        int steps = (int)Math.abs(finalVolume-note.getVolume());
        for(int i = 0; i < steps; i++)
        {
            long tick = (long)(posit + noteLength*((1-(double)percent/100) + (double)i/Math.abs(steps)*(double)percent/100));
            track.add(MidiScoreUtil.createShortEvent(ShortMessage.CONTROL_CHANGE , channel, 7, (int)(127.0*(note.getVolume()-i-1)/note.getVolume()), tick));
        }
        if(chokeChannels) track.add(MidiScoreUtil.createShortEvent(ShortMessage.CONTROL_CHANGE , channel, 120, 0, posit+noteLength));
        track.add(MidiScoreUtil.createShortEvent(ShortMessage.CONTROL_CHANGE , channel, 7, 127, posit+noteLength+1));
    }
    public void saveConstructorData(PrintStream stream, int indent)
    {
        MiscHelper.saveInteger(stream, indent, percent);
        MiscHelper.saveInteger(stream, indent, finalVolume);
        MiscHelper.saveBoolean(stream, indent, chokeChannels);
    }
}
