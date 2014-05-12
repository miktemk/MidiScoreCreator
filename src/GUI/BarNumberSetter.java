package GUI;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;

public class BarNumberSetter extends JPanel implements FocusListener
{
    class TimeSig
    {
        int topNumber, bottomNumber;
        public TimeSig(int topNumber, int bottomNumber)
        {
            this.topNumber = topNumber;
            this.bottomNumber = bottomNumber;
        }
    }
    private class TopFieldListener implements ActionListener
    {
        BarNumberSetter bns;
        public TopFieldListener(BarNumberSetter bns) { this.bns = bns; }
        public void actionPerformed(ActionEvent e)
        {
            JTextField source = (JTextField)e.getSource();
            int tmp = bns.curTop;
            try { tmp = Integer.parseInt(source.getText()); }
            catch(Exception e1)
            {
                try { tmp = (int)Math.round(Double.parseDouble(source.getText())); }
                catch(Exception e2) {}
            }
            if(tmp < 1) tmp = 1;
            bns.curTop = tmp;
            source.setText(""+tmp);
        }
    }
    private class BotFieldListener implements ActionListener
    {
        BarNumberSetter bns;
        public BotFieldListener(BarNumberSetter bns) { this.bns = bns; }
        public void actionPerformed(ActionEvent e)
        {
            JTextField source = (JTextField)e.getSource();
            int tmp = bns.curBottom;
            try { tmp = Integer.parseInt(source.getText()); }
            catch(Exception e1)
            {
                try { tmp = (int)Math.round(Double.parseDouble(source.getText())); }
                catch(Exception e2) {}
            }
            if(tmp < 1) tmp = 1;
            bns.curBottom = tmp;
            source.setText(""+tmp);
        }
    }
    private class TopButtonListener implements ActionListener
    {
        BarNumberSetter bns;
        public TopButtonListener(BarNumberSetter bns) { this.bns = bns; }
        public void actionPerformed(ActionEvent e)
        {
            JButton source = (JButton)e.getSource();
            if(source.getText() == "<")      bns.curTop--;
            else if(source.getText() == ">") bns.curTop++;
            
            if(bns.curTop < 1)      bns.curTop = 1;
            bns.topTxt.setText(""+bns.curTop);
        }
    }
    private class BotButtonListener implements ActionListener
    {
        BarNumberSetter bns;
        public BotButtonListener(BarNumberSetter bns) { this.bns = bns; }
        public void actionPerformed(ActionEvent e)
        {
            JButton source = (JButton)e.getSource();
            if(source.getText() == "<")      bns.curBottom--;
            else if(source.getText() == ">") bns.curBottom++;
            
            if(bns.curBottom < 1)      bns.curBottom = 1;
            bns.botTxt.setText(""+bns.curBottom);
        }
    }
    JTextField topTxt, botTxt;
    int curTop, curBottom;
    public BarNumberSetter()
    {
        super(new BorderLayout());
        curTop = 4;
        curBottom = 4;
        
        JButton topl = new JButton("<");
        JButton topr = new JButton(">");
        topl.addActionListener(new TopButtonListener(this));
        topr.addActionListener(new TopButtonListener(this));
        topTxt = new JTextField(""+curTop);
        topTxt.addActionListener(new TopFieldListener(this));
        topTxt.addFocusListener(this);
        JPanel topP = new JPanel();
        topP.setLayout(new BorderLayout());
        topP.add(topl, BorderLayout.WEST);
        topP.add(topr, BorderLayout.EAST);
        topP.add(topTxt, BorderLayout.CENTER);
        
        JButton botl = new JButton("<");
        JButton botr = new JButton(">");
        botl.addActionListener(new BotButtonListener(this));
        botr.addActionListener(new BotButtonListener(this));
        botTxt = new JTextField(""+curBottom);
        botTxt.addActionListener(new BotFieldListener(this));
        botTxt.addFocusListener(this);
        JPanel botP = new JPanel();
        botP.setLayout(new BorderLayout());
        botP.add(botl, BorderLayout.WEST);
        botP.add(botr, BorderLayout.EAST);
        botP.add(botTxt, BorderLayout.CENTER);
        
        JPanel holder = new JPanel();
        holder.setLayout(new GridLayout(2, 2));
        holder.add(new JLabel("   Top Number: "));
        holder.add(topP);
        holder.add(new JLabel("Bottom Number: "));
        holder.add(botP);
        
        add(new JLabel("Set the bar numbers:"), BorderLayout.NORTH);
        add(holder, BorderLayout.CENTER);
    }
    public void focusGained(FocusEvent e) {}
    public void focusLost(FocusEvent e)
    {
        JTextField source = (JTextField)e.getSource();
        if(source == topTxt)
        {
            try
            {
                int tmp = Integer.parseInt(topTxt.getText());
                if(tmp == curTop) throw new Exception();
                curTop = tmp;
                if(curTop < 1) curTop = 1;
                topTxt.setText(""+curTop);
            }
            catch(Exception ex) { topTxt.setText(""+curTop); }
        }
        else if(source == botTxt)
        {
            try
            {
                int tmp = Integer.parseInt(botTxt.getText());
                if(tmp == curBottom) throw new Exception();
                curBottom = tmp;
                if(curBottom < 1) curBottom = 1;
                botTxt.setText(""+curBottom);
            }
            catch(Exception ex) { botTxt.setText(""+curBottom); }
        }
    }
    public BarNumberSetter.TimeSig getBarNumbers()
    {
        return new TimeSig(curTop, curBottom);
    }
    public void setBarNumbers(int top, int bot)
    {
        curTop = top;
        curBottom = bot;
        if(curTop < 1)    curTop = 1;
        if(curBottom < 1) curBottom = 1;
        topTxt.setText(""+curTop);
        botTxt.setText(""+curBottom);
    }
}
