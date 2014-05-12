package NoteModifiers;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import GUI.*;
import DataStructure.*;

public class CrescFrontModifier extends NoteModifier
{
    IntSetPanel volSetter, percentSetter;
    JPanel setter;
    int percent = 50;
    int firstVol = 0;
    public CrescFrontModifier()
    {
        volSetter = new IntSetPanel(0, 127, "First volume of note:");
        volSetter.setValue(firstVol);
        percentSetter = new IntSetPanel(0, 100, "% of note to cresc:");
        percentSetter.setValue(percent);
        setter = new JPanel(new GridLayout(2, 1));
        setter.add(volSetter);
        setter.add(percentSetter);
        setter.setPreferredSize(new Dimension(230, 70));
    }
    public void paintButton(Graphics2D g, JToggleButton b)
    {
        g.setColor(Color.black);
        g.draw(new Ellipse2D.Double(b.getWidth()/3, b.getHeight()*(5.0/9), b.getWidth()/3, b.getHeight()*(2.0/9)));
        g.draw(new Line2D.Double(b.getWidth()*2.0/3, b.getHeight()*2.0/3, b.getWidth()*2.0/3, b.getHeight()/5));
        g.draw(new Line2D.Double(b.getWidth()/3-1, b.getHeight()*(2.0/3-1.0/9), 0, b.getHeight()*2.0/3));
        g.draw(new Line2D.Double(b.getWidth()/3-1, b.getHeight()*(2.0/3+1.0/9), 0, b.getHeight()*2.0/3));
    }
    public void modifyNote(Note note)
    {
        if(note.getVolume() > firstVol) note.addModifier(new CrescFrontEvent(percent, firstVol));
    }
    public void update()
    {
        firstVol = volSetter.getValue();
        percent = percentSetter.getValue();
    }
    public void resetPanel()
    {
        volSetter.setValue(firstVol);
        percentSetter.setValue(percent);
    }
    public Component getPanel()
    {
        return setter;
    }
    public int getHighOrder() { return 3; }
    public int getOrder() { return 7; }
}
