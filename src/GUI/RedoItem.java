package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

public class RedoItem extends JMenuItem implements ActionListener
{
    WindowsMenu menu;
    ScorePanel p;
    public RedoItem(WindowsMenu menu, ScorePanel p)
    {
        super("Redo");
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
        addActionListener(this);
        this.menu = menu;
        this.p = p;
    }
    public void actionPerformed(ActionEvent e)
    {
        UndoManager undo = menu.getCurScoreObject().manager;
        try
        {
            undo.redo();
            menu.setCurSaveFlag(false);
            p.repaint();
        }
        catch (CannotRedoException ex)
        {
            //System.out.println("Unable to undo: " + ex);
            //ex.printStackTrace();
        }
        
    }
}
