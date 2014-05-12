package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import DataStructure.*;

public class EditListener implements ActionListener
{
    final String cut_st = "Cut",
                 copy_st = "Copy",
                 wri_ovr = "Write over",
                 wri_ovr_ref = "Write REF over";
    ScorePanel p;
    ScorePopup pop;
    public EditListener(ScorePanel p, ScorePopup pop)
    {
        this.p = p;
        this.pop = pop;
    }
    public JMenuItem createCut()
    {
        JMenuItem cut = new JMenuItem(cut_st);
        cut.addActionListener(this);
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        return cut;
    }
    public JMenuItem createCopy()
    {
        JMenuItem copy = new JMenuItem(copy_st);
        copy.addActionListener(this);
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        return copy;
    }
    public JMenuItem createPaste()
    {
        JMenuItem paste_writeOver = new JMenuItem(wri_ovr);
        paste_writeOver.addActionListener(this);
        return paste_writeOver;
    }
    public JMenuItem createPasteRef()
    {
        JMenuItem paste_writeOver_ref = new JMenuItem(wri_ovr_ref);
        paste_writeOver_ref.addActionListener(this);
        paste_writeOver_ref.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        return paste_writeOver_ref;
    }
    public void actionPerformed(ActionEvent e)
    {
        JMenuItem source = (JMenuItem)e.getSource();
        if(source.getText() == cut_st)
        {
            //1. Copy
            p.copiedBars.removeAllElements();
            if(p.selectedBars.hasBars)
            {
                Part curPart = p.score.getPart(p.selectedBars.lineNumber);
                for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                {
                    p.copiedBars.addBar((Bar)(curPart.getBar(Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend) + i)));
                }
            }
            else if(p.curBarRef != null) p.copiedBars.addBar(p.curBarRef);
            //2. Replace bars with blank ones
            if(p.selectedBars.hasBars)
            {
                Part curPart = p.score.getPart(p.selectedBars.lineNumber);
                int baseIndex = Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend);
                for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                {
                    Bar removed = curPart.getBar(baseIndex+i);
                    curPart.getBars().set(baseIndex+i, new Bar());
                    removed.incRefBarCount(-1);
                }
            }
            else if(p.curBarRef != null)
            {
                Bar target = p.curBarRef;
                p.curPartRef.getBars().set(p.curBar, new Bar());
                target.incRefBarCount(-1); //let it know that a ref of itselft was removed; it'll figure it out
            }
        }
        else if(source.getText() == copy_st)
        {
            p.copiedBars.removeAllElements();
            if(p.selectedBars.hasBars)
            {
                Part curPart = p.score.getPart(p.selectedBars.lineNumber);
                for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                {
                    p.copiedBars.addBar((Bar)(curPart.getBar(Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend) + i)));
                }
            }
            else if(p.curBarRef != null) p.copiedBars.addBar(p.curBarRef);
        }
        else if(source.getText() == wri_ovr)
        {
            if(p.curPartRef != null)
            {
                Part curPart = p.curPartRef;
                int i = p.curBar;
                pop.paster.refresh();
                pop.paster.setTitle("How many pastes to make?");
                int option = JOptionPane.showConfirmDialog(p, pop.paster, "Write Over (by-value)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if(option == JOptionPane.OK_OPTION)
                {
                    int pastes = pop.paster.getValue();
                    for(int j = 0; j < pastes; j++)
                    {
                        for(Enumeration en = p.copiedBars.elements(); en.hasMoreElements();)
                        {
                            Bar curBar = (Bar)en.nextElement();
                            Bar removed = (Bar)curPart.getBars().elementAt(i);
                            removed.incRefBarCount(-1);
                            curPart.getBars().set(i, curBar.makeCopy());
                            if(i+1 == p.score.getTotalBars()) p.score.addBarToEnd();
                            i++;
                        }
                    }
                    //pop.fixTimeSignaturesAtLine(p.curLine);
                }
            }
        }
        else if(source.getText() == wri_ovr_ref)
        {
            if(p.curPartRef != null)
            {
                Part curPart = p.curPartRef;
                Vector bars = curPart.getBars();
                int i = p.curBar;
                pop.paster.refresh();
                pop.paster.setTitle("How many pastes to make?");
                int option = JOptionPane.showConfirmDialog(p, pop.paster, "Write Over (by-ref)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if(option == JOptionPane.OK_OPTION)
                {
                    int pastes = pop.paster.getValue();
                    for(int j = 0; j < pastes; j++)
                    {
                        for(Enumeration en = p.copiedBars.elements(); en.hasMoreElements();)
                        {
                            Bar curBar = (Bar)en.nextElement();
                            Bar target = (Bar)bars.elementAt(i);
                            /*if(target.getMaxLength() == curBar.getMaxLength())
                            {
                                bars.set(i, curBar);
                                curBar.incRefBarCount(1); //let it know that another ref of itselft was pasted
                                target.incRefBarCount(-1);
                            }
                            else
                            {
                                curPart.getBars().set(i, curBar.makeCopy());
                            }*/
                            bars.set(i, curBar);
                            curBar.incRefBarCount(1); //let it know that another ref of itselft was pasted
                            target.incRefBarCount(-1);
                            if(i+1 == p.score.getTotalBars()) p.score.addBarToEnd();
                            i++;
                        }
                    }
                    //pop.fixTimeSignaturesAtLine(p.curLine);
                }
            }
        }
    }
}
