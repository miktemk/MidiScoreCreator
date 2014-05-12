package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

public class UndoItem extends JMenuItem implements ActionListener
{
    WindowsMenu menu;
    ScorePanel p;
    public UndoItem(WindowsMenu menu, ScorePanel p)
    {
        super("Undo");
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        addActionListener(this);
        this.menu = menu;
        this.p = p;
    }
    public void actionPerformed(ActionEvent e)
    {
        UndoManager undo = menu.getCurScoreObject().manager;
        try
        {
            undo.undo();
            menu.setCurSaveFlag(false);
            p.repaint();
        }
        catch (CannotUndoException ex)
        {
            //System.out.println("Unable to undo: " + ex);
            //ex.printStackTrace();
        }
        
    }
}
