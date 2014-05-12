package GUI;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class CustomNoteLengthButton extends JToggleButton
{
    private class MyDialog extends JPanel
    {
        int top = 1,
            bot = 4;
        JTextField tfTop, tfBot;
        JLabel decLabel;
        public MyDialog()
        {
            super(new BorderLayout());
            tfTop = new JTextField(""+top);
            tfTop.addFocusListener(new FocusAdapter()
            {
                public void focusLost(FocusEvent e)
                {
                    scanTextFields();
                }
            });
            tfBot = new JTextField(""+bot);
            tfBot.addFocusListener(new FocusAdapter()
            {
                public void focusLost(FocusEvent e)
                {
                    scanTextFields();
                }
            });
            decLabel = new JLabel("Decimal Value: 0.25");
            JPanel center = new JPanel(new GridLayout(2, 2));
            center.add(new JLabel("Numerator:"));
            center.add(tfTop);
            center.add(new JLabel("Denominator:"));
            center.add(tfBot);
            
            add(center, BorderLayout.CENTER);
            add(decLabel, BorderLayout.SOUTH);
        }
        public void setNumbers(int top, int bot)
        {
            this.top = top;
            this.bot = bot;
            tfTop.setText(""+top);
            tfBot.setText(""+bot);
            scanTextFields();
        }
        private void scanTextFields()
        {
            try
            {
                int topTmp = Integer.parseInt(tfTop.getText());
                top = topTmp;
            }
            catch(Exception e) { tfTop.setText(""+top); }
            try
            {
                int botTmp = Integer.parseInt(tfBot.getText());
                bot = botTmp;
            }
            catch(Exception e) { tfBot.setText(""+bot); }
            double decVal = (double)top/bot;
            decLabel.setText("Decimal Value: "+decVal);
        }
    }
    int top = 1,
        bot = 4;
    MyDialog setter;
    ScorePanel p;
    public CustomNoteLengthButton(ScorePanel p)
    {
        this.p = p;
        setter = new MyDialog();
        addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                if(e.getClickCount() >= 2)
                {
                    respondToDoubleClick();
                }
            }
        });
        setToolTipText("Custom Note Length");
    }
    public void respondToDoubleClick()
    {
        setter.setNumbers(top, bot);
        int option = JOptionPane.showConfirmDialog(this, setter, "Custom Note Length", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(option == JOptionPane.OK_OPTION)
        {
            top = setter.top;
            bot = setter.bot;
            p.setCurNoteLength(getValue());
            repaint();
        }
        else
        {
            setter.setNumbers(top, bot);
        }
    }
    public double getValue()
    {
        return (double)top/bot;
    }
    public void paint(Graphics g1)
    {
        super.paint(g1);
        Graphics2D g = (Graphics2D)g1;
        g.setColor(Color.black);
        g.setFont(new Font(null, Font.BOLD, getHeight()/3));
        g.drawString(""+top, getWidth()/2-10, getHeight()/2-1);
        g.drawString(""+bot, getWidth()/2-10, getHeight()-5);
        g.draw(new Line2D.Double(5, getHeight()/2, getWidth()-15, getHeight()/2));
    }
}
