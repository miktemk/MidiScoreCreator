package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import NoteModifiers.*;

public class ButtonRow extends JToggleButton implements ActionListener
{
    private class MyButton extends JToggleButton
    {
        NoteModifier ntmdf;
        String modName;
        public MyButton(NoteModifier ntmdf, String modName)
        {
            this.ntmdf = ntmdf;
            this.modName = modName;
            addMouseListener(new MouseAdapter()
            {
                public void mouseEntered(MouseEvent e)
                { setBorderPainted(true); }
                public void mouseExited(MouseEvent e)
                { setBorderPainted(false); }
            });
            setToolTipText(modName);
            setBorderPainted(false);
            setPreferredSize(new Dimension(40, 40));
        }
        public void paint(Graphics g)
        {
            super.paint(g);
            ntmdf.paintButton((Graphics2D)g, this);
        }
        public NoteModifier getModifier() { return ntmdf; }
        public String getName() { return modName; }
    }
    ScorePanel p;
    NoteModifier curMod;
    String modName;
    JPopupMenu row;
    ButtonGroup bg;
    public ButtonRow(ScorePanel p, NoteModifier first, String firstName)
    {
        this.p = p;
        curMod = first;
        modName = firstName;
        bg = new ButtonGroup();
        row = new JPopupMenu();
        row.setLayout(new FlowLayout());
        
        addModifier(curMod, modName);
        addActionListener(this);
        addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                if(e.getButton() == MouseEvent.BUTTON1  &&  e.getClickCount() >= 2)
                {
                    respondToDoubleClick();
                }
            }
            public void mousePressed(MouseEvent e)
            {
                if(e.getButton() == MouseEvent.BUTTON3)
                {
                    setSelected(true);
                    if(bg.getButtonCount() > 1) showRow();
                }
            }
            public void mouseEntered(MouseEvent e)
            { setBorderPainted(true); }
            public void mouseExited(MouseEvent e)
            { setBorderPainted(false); }
        });
        setBorderPainted(false);
        setToolTipText(modName);
        //setPreferredSize(new Dimension(40, 40));
    }
    public void paint(Graphics g)
    {
        super.paint(g);
        curMod.paintButton((Graphics2D)g, this);
    }
    public void addModifier(NoteModifier modifier, String name)
    {
        MyButton mb = new MyButton(modifier, name);
        mb.addActionListener(this);
        bg.add(mb);
        row.add(mb);
        if(bg.getButtonCount() == 1) mb.setSelected(true);
        row.pack();
    }
    private void respondToDoubleClick()
    {
        Component panel = curMod.getPanel();
        if(panel != null)
        {
            curMod.openingDialog();
            int option = JOptionPane.showConfirmDialog(this, panel, modName+" settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION) curMod.update();
            else                                curMod.resetPanel();
        }
    }
    public void showRow()
    {
        row.show(this, getWidth(), 0);
    }
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if(source instanceof ButtonRow)
        {
            p.setModifier(curMod);
        }
        else
        {
            MyButton b = (MyButton)e.getSource();
            curMod = b.getModifier();
            modName = b.getName();
            row.setVisible(false);
            p.setModifier(curMod);
            setToolTipText(modName);
            repaint(); //:)
        }
    }
}
