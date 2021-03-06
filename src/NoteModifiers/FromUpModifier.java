package NoteModifiers;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import GUI.*;
import DataStructure.*;

public class FromUpModifier extends NoteModifier
{
    IntSetPanel bendSetter;
    int bend = 50;
    public FromUpModifier()
    {
        bendSetter = new IntSetPanel(0, 100, "% of note to bend:");
        bendSetter.setValue(bend);
    }
    public void paintButton(Graphics2D g, JToggleButton b)
    {
        g.setColor(Color.black);
        g.draw(new Ellipse2D.Double(b.getWidth()/3, b.getHeight()*(5.0/9), b.getWidth()/3, b.getHeight()*(2.0/9)));
        g.draw(new Line2D.Double(b.getWidth()*2.0/3, b.getHeight()*2.0/3, b.getWidth()*2.0/3, b.getHeight()/5));
        g.setColor(Color.red);
        g.draw(new Arc2D.Double(0, b.getHeight()/3, b.getWidth(), b.getHeight()/3, 180, 90, Arc2D.OPEN));
    }
    public void modifyNote(Note note)
    {
        note.addModifier(new BendFront_Rough(bend, BendFront_Rough.UP));
    }
    public void update()     { bend = bendSetter.getValue(); }
    public void resetPanel() { bendSetter.setValue(bend); }
    public Component getPanel() { return bendSetter; }
    public int getHighOrder() { return 4; }
    public int getOrder() { return 15; }
}
