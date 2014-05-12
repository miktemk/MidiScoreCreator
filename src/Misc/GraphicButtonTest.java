package Misc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.lang.*;
import java.util.*;
import java.awt.image.*;

public class GraphicButtonTest extends JFrame
{
    private class ButtonTest extends JButton// implements MouseMotionListener
    {
        public ButtonTest()
        {
           // BufferedImage img = new BufferedImage();
            //img.getGraphics() = g;
        }
        public void paint(Graphics g1)
        {
            Graphics2D g = (Graphics2D)g1;
            super.paint(g1);
            g.setColor(Color.white);
            //g.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
            g.setColor(Color.black);
            g.drawLine(0, 0, 100, 100);
        }//*/
    }
    public GraphicButtonTest()
    {
  //-----------------------------------------------------
        ButtonTest bt = new ButtonTest();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(bt, BorderLayout.CENTER);
        pack();
        setSize(200, 200);
        setVisible(true);
    }
    public static void main(String args[])
    { GraphicButtonTest m = new GraphicButtonTest(); }
}
