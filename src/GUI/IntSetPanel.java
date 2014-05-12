package GUI;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;

public class IntSetPanel extends JPanel
{
    private class FieldListener implements ActionListener
    {
        IntSetPanel is;
        public FieldListener(IntSetPanel is) { this.is = is; }
        public void actionPerformed(ActionEvent e)
        {
            JTextField source = (JTextField)e.getSource();
            try { is.curValue = Integer.parseInt(source.getText()); }
            catch(Exception e1)
            {
                try { is.curValue = (int)Math.round(Double.parseDouble(source.getText())); }
                catch(Exception e2)
                { is.curValue = is.min; }
            }
            
            if(is.curValue < is.min)      is.curValue = is.min;
            else if(is.curValue > is.max) is.curValue = is.max;
            source.setText(""+is.curValue);
        }
    }
    private class ButtonListener implements ActionListener
    {
        IntSetPanel is;
        public ButtonListener(IntSetPanel is) { this.is = is; }
        public void actionPerformed(ActionEvent e)
        {
            JButton source = (JButton)e.getSource();
            if(source.getText() == "<<")      is.curValue--;
            else if(source.getText() == ">>") is.curValue++;
            else if(source.getText() == "|<") is.curValue -= 10;
            else if(source.getText() == ">|") is.curValue += 10;
            
            if(is.curValue < is.min)      is.curValue = is.min;
            else if(is.curValue > is.max) is.curValue = is.max;
            is.txt.setText(""+is.curValue);
        }
    }
    JTextField txt;
    JLabel titleLabel;
    int min, max, curValue;
    public IntSetPanel(int min, int max, String label)
    {
        super(new BorderLayout());
        this.min = min;
        this.max = max;
        curValue = min;
        JButton l = new JButton("<<");
        JButton r = new JButton(">>");
        JButton ll = new JButton("|<");
        JButton rr = new JButton(">|");
        l.addActionListener(new ButtonListener(this));
        r.addActionListener(new ButtonListener(this));
        ll.addActionListener(new ButtonListener(this));
        rr.addActionListener(new ButtonListener(this));
        if(max-min < 10)
        {
            ll.setEnabled(false);
            rr.setEnabled(false);
        }
        JPanel lp = new JPanel(new GridLayout(1, 2));
        JPanel rp = new JPanel(new GridLayout(1, 2));
        lp.add(ll);
        lp.add(l);
        rp.add(r);
        rp.add(rr);
        txt = new JTextField(""+curValue);
        txt.addActionListener(new FieldListener(this));
        
        titleLabel = new JLabel(label);
        add(titleLabel, BorderLayout.NORTH);
        add(txt, BorderLayout.CENTER);
        add(rp, BorderLayout.EAST);
        add(lp, BorderLayout.WEST);
        setPreferredSize(new Dimension(250, 40));
    }
    public int getValue()
    {
        try { curValue = Integer.parseInt(txt.getText()); }
        catch(Exception e1)
        {
            try { curValue = (int)Math.round(Double.parseDouble(txt.getText())); }
            catch(Exception e2)
            { curValue = min; }
        }
        
        if(curValue < min)      curValue = min;
        else if(curValue > max) curValue = max;
        
        return curValue;
    }
    public void setValue(int value)
    {
        curValue = value;
        if(curValue < min)      curValue = min;
        else if(curValue > max) curValue = max;
        txt.setText(""+value);
    }
    public void refresh()
    {
        curValue = min;
        txt.setText(""+curValue);
    }
    public void setTitle(String title)
    {
        titleLabel.setText(title);
    }
}
