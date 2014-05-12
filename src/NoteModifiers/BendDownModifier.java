package NoteModifiers;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import GUI.*;
import DataStructure.*;

public class BendDownModifier extends NoteModifier
{
    IntSetPanel bendSetter;
    JCheckBox chokeBox;
    JPanel setter;
    int bend = 50;
    boolean choke = false;
    public BendDownModifier()
    {
        bendSetter = new IntSetPanel(0, 100, "% of note to bend:");
        bendSetter.setValue(bend);
        chokeBox = new JCheckBox("Choke Channels", choke);
        setter = new JPanel(new GridLayout(2, 1));
        setter.add(bendSetter);
        setter.add(chokeBox);
    }
    public void paintButton(Graphics2D g, JToggleButton b)
    {
        g.setColor(Color.black);
        g.draw(new Ellipse2D.Double(b.getWidth()/3, b.getHeight()*(5.0/9), b.getWidth()/3, b.getHeight()*(2.0/9)));
        g.draw(new Line2D.Double(b.getWidth()*2.0/3, b.getHeight()*2.0/3, b.getWidth()*2.0/3, b.getHeight()/5));
        g.setColor(Color.red);
        g.draw(new Arc2D.Double(0, b.getHeight()*2.0/3, b.getWidth(), b.getHeight()/3, 0, 90, Arc2D.OPEN));
    }
    public void modifyNote(Note note)
    {
        note.addModifier(new BendEnd_Rough(bend, BendEnd_Rough.DOWN, choke));
    }
    public void update()
    {
        bend = bendSetter.getValue();
        choke = chokeBox.isSelected();
    }
    public void resetPanel()
    {
        bendSetter.setValue(bend);
        chokeBox.setSelected(choke);
    }
    public Component getPanel() { return setter; }
    public int getHighOrder() { return 4; }
    public int getOrder() { return 10; }
}
