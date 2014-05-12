package GUI;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class NoteLengthSetter extends JToolBar implements ActionListener
{
    private class ValueButton extends JToggleButton
    {
        double value;
        public ValueButton(double value)
        {
            this.value = value;
            ImageIcon icon = new ImageIcon("GUI/NoteImg/note"+((int)(96*value))+".gif");
            if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
            {
                setIcon(icon);
            }
            else
            {
                setText("1/"+(1/value));
            }
            setRolloverEnabled(true);
        }
        public double getValue() { return value; }
    }
    public static final double[] lengths = {1.0, 0.5, 0.25, 0.125, 0.0625, 0.03125, 0.75, 0.375, 0.1875, 1.0/3, 1.0/6, 1.0/12, 1.0/24};
    ScorePanel p;
    public NoteLengthSetter(ScorePanel p)
    {
        setLayout(new GridLayout(1, lengths.length+1));
        setRollover(true);
        this.p = p;
        ButtonGroup bg = new ButtonGroup();
        for(int i = 0; i < lengths.length; i++)
        {
            ValueButton but = new ValueButton(lengths[i]);
            but.addActionListener(this);
            add(but);
            bg.add(but);
            if(lengths[i] == 0.25) but.setSelected(true);
            else                   but.setSelected(false);
        }
        CustomNoteLengthButton custom = new CustomNoteLengthButton(p);
        custom.addActionListener(this);
        add(custom);
        bg.add(custom);
        setPreferredSize(new Dimension(500, 45));
    }
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            ValueButton source = (ValueButton)e.getSource();
            p.setCurNoteLength(source.getValue());
        }
        catch(Exception ex)
        {
            CustomNoteLengthButton source = (CustomNoteLengthButton)e.getSource();
            p.setCurNoteLength(source.getValue());
        }
        //lengthLabel.setText(""+source.getText());
    }
}
