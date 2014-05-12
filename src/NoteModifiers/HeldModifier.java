package NoteModifiers;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import DataStructure.*;

public class HeldModifier extends NoteModifier
{
    public void paintButton(Graphics2D g, JToggleButton b)
    {
        //draw a Note
        g.setColor(Color.black);
        g.draw(new Ellipse2D.Double(b.getWidth()/3, b.getHeight()*(5.0/9), b.getWidth()/3, b.getHeight()*(2.0/9)));
        g.draw(new Line2D.Double(b.getWidth()*2.0/3, b.getHeight()*2.0/3, b.getWidth()*2.0/3, b.getHeight()/5));
        g.draw(new Line2D.Double(b.getWidth()*2/3, b.getHeight()*2.0/3-2, b.getWidth(), b.getHeight()*2.0/3-2));
        g.draw(new Line2D.Double(b.getWidth()*2/3, b.getHeight()*2.0/3+2, b.getWidth(), b.getHeight()*2.0/3+2));
    }
    public void modifyNote(Note note)
    {
        if(!note.held()) note.setSlurm(Note.HELD);
        else             note.setSlurm(Note.NONE);
    }
    public void update()     {}
    public void resetPanel() {}
    public Component getPanel() { return null; }
    public int getHighOrder() { return 2; }
    public int getOrder() { return 3; }
}
