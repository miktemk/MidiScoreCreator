package GUI;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.lang.*;

public class PianoKeyB extends JPanel
{
    int curNote = 60;
    public PianoKeyB()
    {
        //setPreferredSize(new Dimension(400, 200));
    }
    public void setNote(int newNote)
    {
        curNote = newNote;
        repaint();
    }
    public void paint(Graphics g1)
    {
        Graphics2D g = (Graphics2D)g1;
        g.setColor(Color.white);
        g.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        int nt = curNote % 12;
        boolean[] isWhite = {true, false, true, false, true, true, false, true, false, true, false, true};
        int[] notePosit = {0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6};
        int[] blackPosit = {0, 1, 3, 4, 5};
        if(isWhite[nt])
        {
            g.setColor(Color.cyan);
            g.fill(new Rectangle2D.Double(notePosit[nt]*getWidth()/7+1, 0, getWidth()/7, getHeight()));
        }
        g.setColor(Color.black);
        for(int i = 0; i < 8; i++)
        {
            g.draw(new Line2D.Double(i*getWidth()/7, 0, i*getWidth()/7, getHeight()));
        }
        g.draw(new Line2D.Double(0, getHeight(), getWidth(), getHeight()));
        g.setColor(Color.black);
        for(int i = 0; i < blackPosit.length; i++)
        {
            g.fill(new Rectangle2D.Double((blackPosit[i]+2.0/3)*getWidth()/7, 0, 2.0/3*getWidth()/7, getHeight()*3.0/5));
        }
        if(!isWhite[nt])
        {
            g.setColor(Color.cyan);
            g.fill(new Rectangle2D.Double((notePosit[nt]+2.0/3)*getWidth()/7+1, 0, 2.0/3*getWidth()/7-2, getHeight()*3.0/5 - 1));
        }
    }
}
