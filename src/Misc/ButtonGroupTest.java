package Misc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ButtonGroupTest extends JFrame implements ActionListener
{
    final int max = 10;
    JToggleButton[] buttnz;
    int ind = 0;
    public ButtonGroupTest()
    {
        super("Button selection Test");
        buttnz = new JToggleButton[max];
        ButtonGroup bg = new ButtonGroup();
        JPanel buttnRow = new JPanel(new GridLayout(1, max));
        for(int i = 0; i < max; i++)
        {
            buttnz[i] = new JToggleButton("#"+i);
            bg.add(buttnz[i]);
            buttnRow.add(buttnz[i]);
        }
        buttnz[ind].setSelected(true);
        
        JButton plus = new JButton("+");
        JButton minus = new JButton("-");
        plus.addActionListener(this);
        minus.addActionListener(this);
        JPanel bot = new JPanel(new GridLayout(1, 2));
        bot.add(minus);
        bot.add(plus);
        
        Container pane = getContentPane();
        pane.setLayout(new GridLayout(2, 1));
        pane.add(buttnRow);
        pane.add(bot);
        
        setSize(500, 200);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e)
    {
        JButton source = (JButton)e.getSource();
        if(source.getText() == "+") ind++;
        else                        ind--;
             if(ind >= max) ind = 0;
        else if(ind < 0)    ind = max-1;
        buttnz[ind].setSelected(true);
    }
    public static void main(String[] args)
    {
        new ButtonGroupTest();
    }
}
