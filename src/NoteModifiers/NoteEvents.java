package NoteModifiers;

import DataStructure.*;
import java.awt.*;
import java.io.*;
import javax.sound.midi.*;

public interface NoteEvents
{
    public abstract void drawEvent(Graphics2D g, double noteX, double noteY, double noteLength, double staffWidth, boolean stemUp, Note note);
    public abstract void generateEvents(Track track, int channel, long posit, long noteLength, Note note);
    public abstract void saveConstructorData(PrintStream stream, int indent);
}
