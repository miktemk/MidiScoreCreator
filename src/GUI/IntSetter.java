package GUI;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;

public class IntSetter extends JDialog
{
    private class FieldListener implements ActionListener
    {
        IntSetter is;
        public FieldListener(IntSetter is) { this.is = is; }
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
        IntSetter is;
        public ButtonListener(IntSetter is) { this.is = is; }
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
    private class OKlistener implements ActionListener
    {
        IntSetter is;
        public OKlistener(IntSetter is) { this.is = is; }
        public void actionPerformed(ActionEvent e)
        {
            is.hide();
        }
    }
    JTextField txt;
    JLabel tit;
    int min, max, curValue;
    public IntSetter(Frame owner, String title, int min, int max)
    {
        super(owner, "Set Value", true);
        this.min = min;
        this.max = max;
        curValue = min;
        Container pane = this.getContentPane();
        pane.setLayout(new BorderLayout());
        tit = new JLabel(title);
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
        JButton okB = new JButton("OK");
        okB.addActionListener(new OKlistener(this));
        
        pane.add(tit, BorderLayout.NORTH);
        pane.add(txt, BorderLayout.CENTER);
        pane.add(rp, BorderLayout.EAST);
        pane.add(lp, BorderLayout.WEST);
        pane.add(okB, BorderLayout.SOUTH);
        setSize(300, 120);
        //show();
    }
    public String getTitle()
    { return tit.getText(); }
    public void setTitle(String title)
    { tit.setText(title); }
    public int getIntNow(boolean refresh)
    {
        if(refresh) curValue = min;
        txt.setText(""+curValue);
        show();
        
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
}
