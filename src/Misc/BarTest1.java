package Misc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.lang.*;
import java.util.*;

public class BarTest1 extends JFrame
{
    private class BarTest extends JPanel implements MouseMotionListener
    {
        private class TestNumber
        {
            double value;
            public TestNumber()
            {
                value = Math.random()*0.1;
            }
            public TestNumber(double value)
            {
                this.value = value;
            }
            public double getValue()
            {
                return value;
            }
        }
        final double k = 1;
        int curSection;
        JLabel dataLabel;
        Vector numbers;
        TestNumber ins = new TestNumber(0.1);
        public BarTest(JLabel dataLabel)
        {
            this.dataLabel = dataLabel;
            addMouseMotionListener(this);
            addMouseListener(new MouseAdapter()
            {
                public void mouseExited(MouseEvent e)
                {
                    curSection = 0;
                    repaint();
                }
            });
            numbers = new Vector();
            for(int i = 0; i < 15; i++)
            {
                numbers.addElement(new TestNumber());
            }
        }
        public void paint(Graphics g1)
        {
            Graphics2D g = (Graphics2D)g1;
            g.setColor(Color.white);
            g.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
            g.setColor(Color.black);
            double curX = 0;
            int curID = 0;
            for(Enumeration en = numbers.elements(); en.hasMoreElements();)
            {
                TestNumber cur = (TestNumber)(en.nextElement());
                curID++;
                if(curID == curSection)
                {
                    curX += ins.getValue();
                    g.setColor(Color.red);
                    g.draw(new Line2D.Double(curX*getWidth(), 0, curX*getWidth(), getHeight()));
                    g.setColor(Color.black);
                }
                curX += cur.getValue();
                g.draw(new Line2D.Double(curX*getWidth(), 0, curX*getWidth(), getHeight()));
            }
        }//*/
        public void mouseMoved(MouseEvent e)
        {
            double x = k*e.getX()/getWidth(),
                   curX = 0.0;
            int posit = 1;//<<<<<<<<<----- use 0 for vertor insertion!!!!!!!!!!!!
            TestNumber selectedOne = null;
            for(Enumeration en = numbers.elements(); en.hasMoreElements();)
            {
                TestNumber cur = (TestNumber)(en.nextElement());
                curX += cur.getValue();
                if(x > curX)
                {
                    posit++;
                    //selectedOne = cur;
                }
                else
                {
                    selectedOne = cur;
                    break;
                }//*/
            }
            if(e.isShiftDown()) repaint();
            curSection = posit;
            dataLabel.setText("Section #: "+curSection);
            if(selectedOne != null) dataLabel.setText("Section #: "+curSection+"  Value = "+selectedOne.getValue());
            else                    dataLabel.setText("Section #: "+curSection+"  no Value ");
        }
        public void mouseDragged(MouseEvent e)
        {
            
        }
    }
    JPanel p;
    public BarTest1()
    {
  //-----------------------------------------------------
        JLabel label = new JLabel("Begins NOW!!!");
        BarTest bt = new BarTest(label);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(bt, BorderLayout.CENTER);
        getContentPane().add(label, BorderLayout.SOUTH);
        pack();
        setSize(200, 200);
        setVisible(true);
    }
    public static void main(String args[])
    { BarTest1 m = new BarTest1(); }
}
