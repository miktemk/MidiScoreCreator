package Misc;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.lang.*;
import java.awt.event.*;
import javax.swing.event.*;

import GUI.CurNoteSetter;
import GUI.ModifierCarrier;
import GUI.ScorePanel;
import GUI.ScorePlayer;

public class TestFrame extends JFrame
{
	public TestFrame()
	{
		super("cur test");
        
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
        
        /*ScorePanel a = new ScorePanel(this);
        JPanel bottom  = new JPanel();
        bottom.setLayout(new BorderLayout());
        ScorePlayer p = new ScorePlayer(a);
        a.pop.setPlayer(p);
        CurNoteSetter c = new CurNoteSetter(a);
        
        ModifierCarrier mdc = new ModifierCarrier(a);
        
        JPanel bottomBot = new JPanel(new GridLayout(1, 2));
        bottomBot.add(p);
        bottomBot.add(a.noteDispl);
        
        bottom.add(c, BorderLayout.CENTER);
        bottom.add(bottomBot, BorderLayout.SOUTH);
        getContentPane().add(a, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);
        getContentPane().add(mdc, BorderLayout.WEST);
//----------------------------------------------------------------
		pack();*/
		setSize(900, 700);
		setVisible(true);
	}
	public static void main(String[] args)
	{ TestFrame f = new TestFrame(); }
}
