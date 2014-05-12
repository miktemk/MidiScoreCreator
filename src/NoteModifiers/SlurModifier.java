package NoteModifiers;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import DataStructure.*;

public class SlurModifier extends NoteModifier
{
    public void paintButton(Graphics2D g, JToggleButton b)
    {
        //draw a Note
        g.setColor(Color.black);
        g.draw(new Ellipse2D.Double(0, b.getHeight()*(5.0/9), b.getWidth()/3, b.getHeight()*(2.0/9)));
        g.draw(new Line2D.Double(b.getWidth()*1.0/3, b.getHeight()*2.0/3, b.getWidth()*1.0/3, b.getHeight()/5));
        g.draw(new Ellipse2D.Double(b.getWidth()*2.0/3-3, b.getHeight()*(5.0/9), b.getWidth()/3, b.getHeight()*(2.0/9)));
        g.draw(new Line2D.Double(b.getWidth()-4, b.getHeight()*2.0/3, b.getWidth()-4, b.getHeight()/5));
        g.draw(new Arc2D.Double(0, b.getHeight()*2.0/3, b.getWidth()-4, b.getHeight()/3-4, 180, 180, Arc2D.OPEN));
    }
    public void modifyNote(Note note)
    {
        if(!note.slured()) note.setSlurm(Note.SLURED);
        else               note.setSlurm(Note.NONE);
    }
    public void update()     {}
    public void resetPanel() {}
    public Component getPanel() { return null; }
    public int getHighOrder() { return 2; }
    public int getOrder() { return 2; }
}
