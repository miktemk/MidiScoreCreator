package GUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.undo.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import javax.sound.midi.*;
import DataStructure.*;
import Features.*;

public class WindowsMenu extends JMenu implements ActionListener
{
    private class WindowChangeListener implements ActionListener
    {
        ScorePanel p;
        Map scores;
        ScoreObject curScore = null;
        WindowsMenu menu;
        public WindowChangeListener(ScorePanel p, Map scores, WindowsMenu menu)
        {
            this.p = p;
            this.scores = scores;
            this.menu = menu;
        }
        public void setCurScore(ScoreObject obj)
        {
            if(curScore != null)
            {
                curScore.barPosit = p.barPosit;
                curScore.linePosit = p.linePosit;
            }
            p.setScore(obj.score);
            p.scrollHere(obj.barPosit, obj.linePosit);
            curScore = obj;
        }
        public void actionPerformed(ActionEvent e)
        {
            JMenuItem source = (JMenuItem)e.getSource();
            ScoreObject obj = (ScoreObject)scores.get(source.getText());
            setCurScore(obj);
            menu.frame.setTitle("MIDI Score Creator - "+source.getText());
            menu.curItem = (JRadioButtonMenuItem)source;
            menu.curObj = obj;
        }
    }
    ScorePanel p;
    ScoreMainFrame frame;
    Map scores;
    ButtonGroup bg;
    JRadioButtonMenuItem curItem;
    ScoreObject curObj = null;
    WindowChangeListener changeL;
    public WindowsMenu(ScorePanel p, ScoreMainFrame frame)
    {
        super("Window");
        this.p = p;
        this.frame = frame;
        scores = new HashMap();
        changeL = new WindowChangeListener(p, scores, this);
        bg = new ButtonGroup();
        
        JMenuItem newWindow = new JMenuItem("New Window");
        newWindow.addActionListener(this);
        JMenuItem closeWindow = new JMenuItem("Close Window");
        closeWindow.addActionListener(this);
        JMenuItem closeAll = new JMenuItem("Close All");
        closeAll.addActionListener(this);
        add(newWindow);
        add(closeWindow);
        add(closeAll);
        addSeparator();
        
        closeCurrent();
    }
    public ScoreObject getCurScoreObject()
    { return curObj; }
    public void setCurSaveFlag(boolean aflag)
    {//this should be used by the UNDO manager..  i think
        curObj.saved = aflag;
        if(aflag) frame.setTitle("MIDI Score Creator - "+curItem.getText());
        else      frame.setTitle("MIDI Score Creator - "+curItem.getText()+"*");
    }
    public void saveUpdate(File file)
    {
        if(file != null)
        {
            curObj.file = file;
            scores.remove(curItem.getText());
            String scoreName = file.getName();
            int maxID = getMaxID(scoreName);
            if(maxID != 0)
                scoreName = scoreName+" "+(maxID+1);
            curItem.setText(scoreName);
            scores.put(curItem.getText(), curObj);
        }
    }
    public void addEdit(UndoableEdit edit)
    {
        curObj.manager.addEdit(edit);
        setCurSaveFlag(false);
    }
    /**
     * Adds a score to this menu and sets it in the ScorePanel
     */
    public void addWindow(Score score, String scoreName, File file)
    {
        int maxID = getMaxID(scoreName);
        if(maxID != 0)
            scoreName = scoreName+" "+(maxID+1);
        if(file != null)
            for(Iterator iii = scores.entrySet().iterator(); iii.hasNext();)
            {
                Map.Entry curEntry = (Map.Entry)iii.next();
                ScoreObject cur = (ScoreObject)curEntry.getValue();
                if(file.equals(cur.file))
                {
                    JOptionPane.showMessageDialog(frame, new JLabel("The file "+file.getName()+ " is already opened!"), "Error", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
        curItem = new JRadioButtonMenuItem(scoreName);
        curItem.addActionListener(changeL);
        add(curItem);
        bg.add(curItem);
        curItem.setSelected(true);
        frame.setTitle("MIDI Score Creator - "+scoreName);
        ScoreObject obj = new ScoreObject(score, file);
        scores.put(scoreName, obj);
        //if(changeL.curScore == null) changeL.setCurScore(obj);
        changeL.setCurScore(obj);
        p.setScore(obj.score);
        p.scrollHere(obj.barPosit, obj.linePosit);
        curObj = obj;
    }
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() instanceof JMenuItem)
        {
            JMenuItem source = (JMenuItem)e.getSource();
            if(source.getText() == "New Window")        newWindow();
            else if(source.getText() == "Close Window") closeCurrent();
            else if(source.getText() == "Close All")    closeAll();
        }
        else
        {
            
        }
    }
    public void newWindow()
    {
        int maxID = getMaxID("Untitled");
        addWindow(p.createNewScore(), "Untitled "+(maxID+1), null);
    }
    private int getMaxID(String head)
    {
        int maxID = 0;
        for(int i = 4; i < getItemCount(); i++)
        {
            if(getItem(i).getText().startsWith(head))
            {
                String curTitle = getItem(i).getText();
                if(curTitle.equals(head))
                    maxID = 1;
                else
                {
                    String num = curTitle.substring(head.length()+1, getItem(i).getText().length());
                    int curID = Integer.parseInt(num);
                    if(curID > maxID) maxID = curID;
                }
            }
        }
        return maxID;
    }
    /**
     * close the current window, by first requesting to save...
     * @return false if save was CANELLED... true otherwise
     */
    public boolean closeCurrent()
    {
        //0. SAVE???
        //1. removes curItem from the list
        //2. removes JMenuItem from the list
        //3. Finds a new value 4 the curItem
        //4. Sets the new curItem in ScorePanel
        if(curObj != null && !curObj.saved)
        {
            int option = JOptionPane.showConfirmDialog(frame, new JLabel("Save changes to "+curItem.getText()+"?"), "Save File?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if(option == JOptionPane.CANCEL_OPTION)
                return false;
            if(option == JOptionPane.YES_OPTION)
            {
                File prevFFF = curObj.file;
                File fff = frame.saveCurScore(curObj.file);
                if(fff == null)
                    return false;
                else if(prevFFF == null)
                {
                    //:| apparently this is done by frame.saveCurScore calling saveUpdate 
                }    
            }
        }
        if(curItem != null)
        {
            scores.remove(curItem.getText());
            remove(curItem);
            bg.remove(curItem);
        }
        if(getItemCount() <= 4)
        {
            //create new Score - it will be the only one here!!!
            addWindow(p.createNewScore(), "Untitled 1", null);
        }
        else
        {
            curItem = (JRadioButtonMenuItem)getItem(4);
            curItem.setSelected(true);
            ScoreObject obj = (ScoreObject)scores.get(curItem.getText());
            frame.setTitle("MIDI Score Creator - "+curItem.getText());
            p.setScore(obj.score);
            p.scrollHere(obj.barPosit, obj.linePosit);
            changeL.setCurScore(obj);
            curObj = obj;
        }
        return true;
    }
    /**
     * close all the windows, by first requesting to save...
     * @return false if save was CANELLED for any window... true otherwise
     */
    public boolean closeAll()
    {
        while(getItemCount() > 5)
        {
            if(!closeCurrent()) return false;
        }
        if(!closeCurrent()) return false;
        return true;
    }
}
