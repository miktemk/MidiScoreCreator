package NoteModifiers;

import java.awt.*;
import javax.swing.*;
import GUI.*;
import DataStructure.*;

public class VolumeModifier extends NoteModifier
{
    IntSetPanel volSetter;
    int volume = 90;
    public VolumeModifier()
    {
        volSetter = new IntSetPanel(0, 127, "Note's new volume:");
        volSetter.setValue(volume);
    }
    public void paintButton(Graphics2D g, JToggleButton b)
    {
        //draw an mf
        g.setColor(Color.black);
        g.drawString("mf", b.getWidth()/3, b.getHeight()*2/3);
    }
    public void modifyNote(Note note)
    {
        note.setVolume(volSetter.getValue());
    }
    public void update()     { volume = volSetter.getValue(); }
    public void resetPanel() { volSetter.setValue(volume); }
    public Component getPanel() { return volSetter; }
    public int getHighOrder() { return 3; }
    public int getOrder() { return 5; }
}
