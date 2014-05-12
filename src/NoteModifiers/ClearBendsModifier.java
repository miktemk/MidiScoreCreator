package NoteModifiers;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import DataStructure.*;

public class ClearBendsModifier extends NoteModifier
{
    public void paintButton(Graphics2D g, JToggleButton b)
    {
        //draw an mf
        g.setColor(Color.red);
        g.draw(new Arc2D.Double(b.getWidth()/6, b.getHeight()/3, b.getWidth(), b.getHeight()/3, 180, 90, Arc2D.OPEN));
        g.setColor(Color.black);
        g.drawString("fp", b.getWidth()/3, b.getHeight()/2);
        g.setColor(Color.blue);
        g.setStroke(new BasicStroke(2.0f));
        g.draw(new Ellipse2D.Double(2, b.getHeight()/4, b.getWidth()-4, b.getHeight()/2));
        g.draw(new Line2D.Double((b.getWidth()-4)/4,  b.getHeight()*5/8+2, (b.getWidth()-4)*3/4+4,  b.getHeight()*3/8-2));
    }
    public void modifyNote(Note note)
    {
        //note.setPitchBend(Note.NONE, 0);
        //note.setVolumeBend(Note.NONE, 0);
        note.mods.removeAllElements();
    }
    public void update()     {}
    public void resetPanel() {}
    public Component getPanel() { return null; }
    public int getHighOrder() { return 100; }
    public int getOrder() { return 0; }
}
