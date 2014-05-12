package Misc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.midi.*;
import java.awt.geom.*;
import Features.*;

public class ScaleTest extends JFrame
{
    public ScaleTest()
    {
        super("SCALE TEST!!!!!!!");
        getContentPane().add(new ScaleMainFrame());
        
        setSize(500, 300);
        setVisible(true);
    }
    public static void main(String[] args)
    {
        new ScaleTest();
    }
}
