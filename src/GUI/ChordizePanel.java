package GUI;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;

public class ChordizePanel extends JPanel implements FocusListener, ActionListener
{
    int val = 7;
    boolean above = true;
    JTextField tf;
    JRadioButton aboveB, belowB;
    public ChordizePanel()
    {
        super(new GridLayout(3, 1));
        
        tf = new JTextField(""+val);
        tf.addFocusListener(this);
        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("# of Semitones"), BorderLayout.WEST);
        top.add(tf, BorderLayout.CENTER);
        
        aboveB = new JRadioButton("Above top note");
        belowB = new JRadioButton("Below bottom note");
        aboveB.addActionListener(this);
        belowB.addActionListener(this);
        ButtonGroup bg = new ButtonGroup();
        bg.add(aboveB);
        bg.add(belowB);
        aboveB.setSelected(true);
        
        add(top);
        add(aboveB);
        add(belowB);
    }
    public void actionPerformed(ActionEvent e)
    {
        JRadioButton b = (JRadioButton)e.getSource();
        above = (b == aboveB);
    }
    public void focusGained(FocusEvent e) {}
    public void focusLost(FocusEvent e)
    {
        try
        { val = Integer.parseInt(tf.getText()); }
        catch(Exception ex) { tf.setText(""+val); }
    }
    public int getValue()
    {
        try
        { val = Integer.parseInt(tf.getText()); }
        catch(Exception ex) {}
        return val;
    }
    public boolean isAbove() { return above; }
}
