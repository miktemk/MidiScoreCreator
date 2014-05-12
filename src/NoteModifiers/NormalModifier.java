package NoteModifiers;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import DataStructure.*;

public class NormalModifier extends NoteModifier
{
    public void paintButton(Graphics2D g, JToggleButton b)
    {
        //draw a Note
        g.setColor(Color.black);
        g.draw(new Ellipse2D.Double(b.getWidth()/3, b.getHeight()*(5.0/9), b.getWidth()/3, b.getHeight()*(2.0/9)));
        g.draw(new Line2D.Double(b.getWidth()*2.0/3, b.getHeight()*2.0/3, b.getWidth()*2.0/3, b.getHeight()/5));
    }
    public void modifyNote(Note note) {}
    public void update()     {}
    public void resetPanel() {}
    public Component getPanel() { return null; }
    public int getHighOrder() { return 1; }
    public int getOrder() { return 1; }
}
