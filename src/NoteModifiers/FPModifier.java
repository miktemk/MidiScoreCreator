package NoteModifiers;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import GUI.*;
import DataStructure.*;

public class FPModifier extends NoteModifier
{
    IntSetPanel volSetter, percentSetter;
    JPanel setter;
    int percent = 50;
    int minVol = 10;
    public FPModifier()
    {
        volSetter = new IntSetPanel(0, 127, "Minimal volume in FP:");
        volSetter.setValue(minVol);
        percentSetter = new IntSetPanel(0, 100, "Decresc percentage:");
        percentSetter.setValue(percent);
        setter = new JPanel(new GridLayout(2, 1));
        setter.add(volSetter);
        setter.add(percentSetter);
    }
    public void paintButton(Graphics2D g, JToggleButton b)
    {
        g.setColor(Color.black);
        g.draw(new Ellipse2D.Double(b.getWidth()/3, b.getHeight()*(5.0/9), b.getWidth()/3, b.getHeight()*(2.0/9)));
        g.draw(new Line2D.Double(b.getWidth()*2.0/3, b.getHeight()*2.0/3, b.getWidth()*2.0/3, b.getHeight()/5));
        g.drawString("fp", (int)(b.getWidth()/3), (int)(b.getHeight()*(4.5/9)));
    }
    public void modifyNote(Note note)
    {
        if(note.getVolume() > minVol) note.addModifier(new FPEvent(percent, minVol));
    }
    public void update()
    {
        minVol = volSetter.getValue();
        percent = percentSetter.getValue();
    }
    public void resetPanel()
    {
        volSetter.setValue(minVol);
        percentSetter.setValue(percent);
    }
    public Component getPanel()
    {
        return setter;
    }
    public int getHighOrder() { return 3; }
    public int getOrder() { return 8; }
}
