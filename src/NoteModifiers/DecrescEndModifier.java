package NoteModifiers;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import GUI.*;
import DataStructure.*;

public class DecrescEndModifier extends NoteModifier
{
    IntSetPanel volSetter, percentSetter;
    JCheckBox chokeBox;
    JPanel setter;
    int percent = 50;
    int finalVol = 0;
    boolean choke = false;
    public DecrescEndModifier()
    {
        volSetter = new IntSetPanel(0, 127, "Final volume of note:");
        volSetter.setValue(finalVol);
        percentSetter = new IntSetPanel(0, 100, "% of note to decresc:");
        percentSetter.setValue(percent);
        chokeBox = new JCheckBox("Choke Channels", choke);
        setter = new JPanel(new GridLayout(3, 1));
        setter.add(volSetter);
        setter.add(percentSetter);
        setter.add(chokeBox);
    }
    public void paintButton(Graphics2D g, JToggleButton b)
    {
        g.setColor(Color.black);
        g.draw(new Ellipse2D.Double(b.getWidth()/3, b.getHeight()*(5.0/9), b.getWidth()/3, b.getHeight()*(2.0/9)));
        g.draw(new Line2D.Double(b.getWidth()*2.0/3, b.getHeight()*2.0/3, b.getWidth()*2.0/3, b.getHeight()/5));
        g.draw(new Line2D.Double(b.getWidth()*2.0/3+1, b.getHeight()*(2.0/3-1.0/9), b.getWidth(), b.getHeight()*2.0/3));
        g.draw(new Line2D.Double(b.getWidth()*2.0/3+1, b.getHeight()*(2.0/3+1.0/9), b.getWidth(), b.getHeight()*2.0/3));
    }
    public void modifyNote(Note note)
    {
        if(note.getVolume() > finalVol) note.addModifier(new DecrescEndEvent(percent, finalVol, choke));
    }
    public void update()
    {
        finalVol = volSetter.getValue();
        percent = percentSetter.getValue();
        choke = chokeBox.isSelected();
    }
    public void resetPanel()
    {
        volSetter.setValue(finalVol);
        percentSetter.setValue(percent);
        chokeBox.setSelected(choke);
    }
    public Component getPanel()
    {
        return setter;
    }
    public int getHighOrder() { return 3; }
    public int getOrder() { return 6; }
}
