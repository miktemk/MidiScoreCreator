package Misc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ButtonTest2 extends JFrame implements ActionListener
{
    private class MyL implements ActionListener
    {
        JToggleButton t;
        public MyL(JToggleButton t)
        {
            this.t = t;
        }
        public void actionPerformed(ActionEvent e)
        {
            t.setSelected(true);
        }
    }
    JButton trig;
    JToggleButton b;
    public ButtonTest2()
    {
        super("2 Buttons");
        trig = new JButton("Trigger");
        b = new JToggleButton("Toggle");
        trig.addActionListener(new MyL(b));
        b.addActionListener(this);
        
        Container pane = getContentPane();
        pane.setLayout(new GridLayout(2, 1));
        pane.add(b);
        pane.add(trig);
        
        setSize(300, 300);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e)
    {
        System.out.println("Weeeeeeeeeeeeeee");
    }
    public static void main(String[] args)
    {
        new ButtonTest2();
    }
}
