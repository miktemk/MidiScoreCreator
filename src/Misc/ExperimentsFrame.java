package Misc;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import GUI.*;
import NoteModifiers.*;

public class ExperimentsFrame extends JFrame
{
    private class MyDialog extends JDialog
    {
        public MyDialog(Frame owner, String title, boolean modal)
        {
            super(owner, title, modal);
            Container c = getContentPane();
            c.setLayout(new BorderLayout());
            c.add(new JButton("hello 1"), BorderLayout.NORTH);
            c.add(new JButton("hello 1"), BorderLayout.CENTER);
            c.add(new JButton("hello 1"), BorderLayout.SOUTH);
            
            setSize(200, 200);
            setVisible(true);
            //show();
        }
        
    }
    private class TestPanel extends JPanel
    {
        private class myPopup extends JPopupMenu
        {
            private class Listener1 implements ActionListener
            {
                int num;
                public Listener1(int num) { this.num = num; }
                public void actionPerformed(ActionEvent e)
                {
                    System.out.println("Action fired from menu item #"+num);
                }
            }
            public myPopup(int numItems)
            {
                for(int i = 0; i < numItems; i++)
                {
                    addItem(new JMenuItem("item #"+(i+1)), new Listener1(i+1));
                }
            }
            public void addItem(JMenuItem i, ActionListener l)
            {
                i.addActionListener(l);
                add(i);
            }
        }
        myPopup pop;
        MyDialog d;
        public TestPanel(JFrame fr)
        {
            d = new MyDialog(fr, "HJHJ", true);
            pop = new myPopup(6);
            addMouseListener(new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                    if(e.getButton() == MouseEvent.BUTTON3)
                    {
                        pop.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        }
        
    }
    private class SpecialListener implements ActionListener
    {
        public SpecialListener(){}
        public void actionPerformed(ActionEvent e)
        {
            JButton source = (JButton)e.getSource();
            source.setSelected(!source.isSelected());
        }
    }
    public ExperimentsFrame()
    {
        super("SWING TESTS");
        //TestPanel p = new TestPanel(this);
        /*ButtonGroup bg = new ButtonGroup();
        JPanel p = new JPanel(new GridLayout(6, 1));
        for(int i = 0; i < 6; i++)
        {
            JToggleButton r = new JToggleButton("RAD "+(i+1));
            //r.addActionListener(new SpecialListener());
            bg.add(r);
            p.add(r);
        }//*/
        ButtonRow p = new ButtonRow(null, new NormalModifier(), "norm");
        //p.addModifier(new BendDownModifier(), "bendDWN");
        //p.addModifier(new BendUpModifier(), "bendUP");
        
        JPanel center = new JPanel(new GridLayout(1, 2));
        center.add(p);
        center.add(new JButton("A"));
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(center, BorderLayout.CENTER);
        pack();
        setSize(100, 100);
        setVisible(true);
    }
    public static void main(String[] args)
	{ ExperimentsFrame f = new ExperimentsFrame(); }
}
