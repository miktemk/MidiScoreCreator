package NoteModifiers;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import DataStructure.*;

public class BendScaleModifier extends NoteModifier implements FocusListener
{
    JPanel mainPanel;
    JTextField semitonesTF, octavesTF;
    int semitones = 12;
    int octaves = 1;
    public BendScaleModifier()
    {
        semitonesTF = new JTextField(""+semitones);
        octavesTF = new JTextField(""+octaves);
        semitonesTF.addFocusListener(this);
        //octavesTF.addFocusListener(this);
        octavesTF.setEditable(false);
        mainPanel = new JPanel(new GridLayout(2, 2));
        mainPanel.add(new JLabel("Semitones:"));
        mainPanel.add(semitonesTF);
        mainPanel.add(new JLabel("Octaves:"));
        mainPanel.add(octavesTF);
    }
    public void focusGained(FocusEvent e) {}
    public void focusLost(FocusEvent e)
    {
        JTextField source = (JTextField)e.getSource();
        if(source == semitonesTF)
        {
            try
            {
                int val = Integer.parseInt(semitonesTF.getText());
                if(val == 0) val = 1;
                semitonesTF.setText(""+val);
                octavesTF.setText(""+(int)(val/12));
            }
            catch(Exception ex)
            {
                semitonesTF.setText(""+semitones);
            }
        }
        else
        {
            try
            {
                int val = Integer.parseInt(octavesTF.getText());
                if(val == 0) val = 1;
                octavesTF.setText(""+val);
                semitonesTF.setText(""+(val*12));
            }
            catch(Exception ex)
            {
                octavesTF.setText(""+octaves);
            }
        }
    }
    public Component getPanel()
    {
        return mainPanel;
    }
    public void resetPanel()
    {
        semitonesTF.setText(""+semitones);
        octavesTF.setText(""+octaves);
    }
    public void update()
    {
        try
        {
            semitones = Integer.parseInt(semitonesTF.getText());
            octaves   = Integer.parseInt(octavesTF.getText());
        }
        catch(Exception ex){}
    }
    public void paintButton(Graphics2D g, JToggleButton b)
    {
        g.setColor(Color.black);
        g.draw(new Ellipse2D.Double(3, b.getHeight()*(5.0/9), b.getWidth()/3-3, b.getHeight()*(2.0/9)));
        g.draw(new Line2D.Double(b.getWidth()/3, b.getHeight()*2.0/3, b.getWidth()/3, b.getHeight()/5));
        g.draw(new Arc2D.Double(b.getWidth()/3+2-b.getWidth()/2, b.getHeight()/5-b.getHeight()/2, b.getWidth(), b.getHeight(), 270, 90, Arc2D.OPEN));
    }
    public void modifyNote(Note note)
    {
        note.addModifier(new BendScaleEvents(semitones));
    }
    public int getHighOrder() { return 5; }
    public int getOrder() { return 2; }
}
