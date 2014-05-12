//NOTE: ins_blank_bef has some crazy print statements

package GUI;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;
import javax.sound.midi.*;
import DataStructure.*;
import GUI.UndoEdits.*;

public class ScorePopup extends JPopupMenu implements ActionListener
{
    private class PopRefsChecker implements ActionListener
    {
        ScorePanel p;
        public PopRefsChecker(ScorePanel p)
        {
            this.p = p;
        }
        public void actionPerformed(ActionEvent e)
        {
            p.popilize();
            System.out.println("YEAY!!!!");
        }
    }
    ScorePanel p;
    ScorePlayer player = null;
    JMenu pasteMenu, pasteMenuRef, compz;
    JMenuItem refLock, rmv_part, ins_chg;
    InstrumentDialog storage;
    IntSetPanel paster, transposer, tempozer;
    BarNumberSetter bns;
    ChordizePanel chorder;
    Vector refBarCheckList;
    final String cut_st = "Cut",
                 copy_st = "Copy",
                 ins_bef = "Insert before",
                 ins_aft = "Insert after",
                 wri_ovr = "Write over",
                 ins_bef_ref = "Insert REF before",
                 ins_aft_ref = "Insert REF after",
                 wri_ovr_ref = "Write REF over",
                 ref_lk = "Remove REF tag",
                 ins_blank_bef = "Insert bar(s) before",
                 ins_blank_aft = "Insert bar(s) after",
                 ins_blank_befall = "Insert bar(s) before 4 all parts",
                 ins_blank_aftall = "Insert bar(s) after 4 all parts",
                 clr_nts = "Clear notes",
                 del_bar = "Remove the bar(s)",
                 del_bar_all = "Remove the bar(s) 4 all parts",
                 add_prt = "Add Part",
                 rmv_prt = "Remove Part",
                 inst_chg = "Change Instrument",
                 time_sig = "Change time signature",
                 temp_chg = "Change tempo",
                 apply_pl = "Apply Current Note Modifier",
                 chordize = "Chordize",
                 transpz = "Transpose",
                 compHalf = "Compress-half",
                 comp2 = "Compress-fill",
                 play_sl = "Play this bar";
    public ScorePopup(ScorePanel p, MidiChannel chan)
    {
        this.p = p;
        JMenuItem cut = new JMenuItem(cut_st);
        cut.addActionListener(this);
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        JMenuItem copy = new JMenuItem(copy_st);
        copy.addActionListener(this);
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        pasteMenu = new JMenu("Paste...");
        JMenuItem paste_insertBefore = new JMenuItem(ins_bef);
        paste_insertBefore.addActionListener(this);
        JMenuItem paste_insertAfter  = new JMenuItem(ins_aft);
        paste_insertAfter.addActionListener(this);
        JMenuItem paste_writeOver    = new JMenuItem(wri_ovr);
        paste_writeOver.addActionListener(this);
        pasteMenu.add(paste_insertBefore);
        pasteMenu.add(paste_insertAfter);
        pasteMenu.add(paste_writeOver);
        pasteMenuRef = new JMenu("Paste by REF...");
        JMenuItem paste_insertBefore_ref = new JMenuItem(ins_bef_ref);
        paste_insertBefore_ref.addActionListener(this);
        JMenuItem paste_insertAfter_ref  = new JMenuItem(ins_aft_ref);
        paste_insertAfter_ref.addActionListener(this);
        JMenuItem paste_writeOver_ref    = new JMenuItem(wri_ovr_ref);
        paste_writeOver_ref.addActionListener(this);
        pasteMenuRef.add(paste_insertBefore_ref);
        pasteMenuRef.add(paste_insertAfter_ref);
        pasteMenuRef.add(paste_writeOver_ref);
        refLock = new JMenuItem(ref_lk);
        refLock.addActionListener(this);
        
        JMenu insMenu = new JMenu("Insert blank bars...");
        JMenuItem ins_before = new JMenuItem(ins_blank_bef);
        ins_before.addActionListener(this);
        JMenuItem ins_after = new JMenuItem(ins_blank_aft);
        ins_after.addActionListener(this);
        JMenuItem ins_before_all = new JMenuItem(ins_blank_befall);
        ins_before_all.addActionListener(this);
        JMenuItem ins_after_all = new JMenuItem(ins_blank_aftall);
        ins_after_all.addActionListener(this);
        insMenu.add(ins_before);
        insMenu.add(ins_after);
        insMenu.add(ins_before_all);
        insMenu.add(ins_after_all);
        JMenu delMenu = new JMenu("Delete...");
        JMenuItem del_clrNotes = new JMenuItem(clr_nts);
        del_clrNotes.addActionListener(this);
        delMenu.add(del_clrNotes);
        JMenuItem del_rmvbar = new JMenuItem(del_bar);
        del_rmvbar.addActionListener(this);
        delMenu.add(del_rmvbar);
        JMenuItem del_rmvbarall = new JMenuItem(del_bar_all);
        del_rmvbarall.addActionListener(this);
        delMenu.add(del_rmvbarall);
        JMenuItem add_part = new JMenuItem(add_prt);
        add_part.addActionListener(this);
        rmv_part = new JMenuItem(rmv_prt);
        rmv_part.addActionListener(this);
        ins_chg = new JMenuItem(inst_chg);
        ins_chg.addActionListener(this);
        JMenuItem barNumChg = new JMenuItem(time_sig);
        barNumChg.addActionListener(this);
        JMenuItem tempoCH = new JMenuItem(temp_chg);
        tempoCH.addActionListener(this);
        JMenuItem applyMod = new JMenuItem(apply_pl);
        applyMod.addActionListener(this);
        JMenuItem chrdzr = new JMenuItem(chordize);
        chrdzr.addActionListener(this);
        JMenuItem trans = new JMenuItem(transpz);
        trans.addActionListener(this);
        compz = new JMenu("Compress notes by 2");
        JMenuItem compz1 = new JMenuItem(compHalf);
        JMenuItem compz2 = new JMenuItem(comp2);
        compz1.addActionListener(this);
        compz2.addActionListener(this);
        compz.add(compz1);
        compz.add(compz2);
        //------------------------------------------------
        JMenuItem playSolo = new JMenuItem(play_sl);
        playSolo.addActionListener(this);
        
        add(cut);
        add(copy);
        add(pasteMenu);
        add(pasteMenuRef);
        add(refLock);
        add(applyMod);
        add(chrdzr);
        add(trans);
        add(compz);
        add(delMenu);
        addSeparator();
        add(insMenu);
        add(add_part);
        add(rmv_part);
        add(ins_chg);
        add(barNumChg);
        add(tempoCH);
        addSeparator();
        add(playSolo);
        storage = new InstrumentDialog(p.instruments, chan);
        paster = new IntSetPanel(1, 200, "How many pastes to make?");
        transposer = new IntSetPanel(-12, 12, "Set the transposition:");
        tempozer = new IntSetPanel(1, 5000, "Set the new Tempo:");
        bns = new BarNumberSetter();
        chorder = new ChordizePanel();
        refBarCheckList = new Vector();
    }
    
    //public JMenuItem createCut()
    //{
        
    //}
    
    public void setPlayer(ScorePlayer player)
    {
        this.player = player;
    }
    
    //all bar #s are same
    private boolean isSimpleSelection()
    {
        if(!p.selectedBars.hasBars) return true;
        Part curPart = p.popPartRef;
        int prevTop = 0, prevBot = 0;
        for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
        {
            Bar curBar = (Bar)(curPart.getBar(Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend) + i));
            Score.TimeSignatureChangeEvent ev = p.score.getTimeSignatureAtBar(Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend) + i);
            if((prevTop != ev.top || prevBot != ev.bottom) && (i != 0)) return false;
            prevTop = ev.top;
            prevBot = ev.bottom;
        }
        return true;
    }
    public void show(Component invoker, int x, int y, boolean pasteActive, boolean refRelease)
    {
        pasteMenu.setEnabled(pasteActive);
        pasteMenuRef.setEnabled(pasteActive);
        refLock.setEnabled(refRelease);
        rmv_part.setEnabled(p.popSectionRef.getSize() > 1);
        ins_chg.setEnabled(p.score.getSectionIndexAtPart(p.popLine) != p.drumChannel);
        compz.setEnabled(isSimpleSelection());
        show(invoker, x, y);
    }
    /** Fixes up all the time signatures in the specified line
     *  The line is ie = the part index
     *  it basically scans the thing and does it all...
     *  And if u dont beleive it... too Bad
     */
    /*public void fixTimeSignaturesAtLine(int changedPart)
    {
        if(changedPart != 0)
        {//i=0 must exist
            Part targetPart = p.score.getPart(changedPart);
            Enumeration en = targetPart.getBars().elements();
            int i = 0;
            for(Enumeration en2 = p.score.getPart(0).getBars().elements(); en2.hasMoreElements();)
            {
                Bar curLookup = (Bar)en2.nextElement();
                Bar target = (Bar)en.nextElement();
                
                if(target.byRef && (curLookup.topNumber != target.topNumber || curLookup.bottomNumber != target.bottomNumber))
                {
                    Bar newOne = target.makeCopy();
                    newOne.setBarNumbers(curLookup.topNumber, curLookup.bottomNumber);
                    targetPart.getBars().set(i, newOne);
                    target.incRefBarCount(-1); //let it know that a ref of itselft was removed; it'll figure it out
                }
                else target.setBarNumbers(curLookup.topNumber, curLookup.bottomNumber);
                i++;
            }
        }
        else
        {
            Part targetPart = p.score.getPart(changedPart);
            Enumeration en = targetPart.getBars().elements();
            int i = 0;
            for(Enumeration en2 = p.score.getLastPart().getBars().elements(); en2.hasMoreElements();)
            {
                Bar curLookup = (Bar)en2.nextElement();
                Bar target = (Bar)en.nextElement();
                if(target.byRef && (curLookup.topNumber != target.topNumber || curLookup.bottomNumber != target.bottomNumber))
                {
                    Bar newOne = target.makeCopy();
                    newOne.setBarNumbers(curLookup.topNumber, curLookup.bottomNumber);
                    targetPart.getBars().set(i, newOne);
                    target.incRefBarCount(-1); //let it know that a ref of itselft was removed; it'll figure it out
                }
                else target.setBarNumbers(curLookup.topNumber, curLookup.bottomNumber);
                i++;
            }
        }
    }*/
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
            else p.copiedBars.addBar(p.popBarRef);
            //2. Replace bars with blank ones
            if(p.selectedBars.hasBars)
            {
                Part curPart = p.score.getPart(p.selectedBars.lineNumber);
                int baseIndex = Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend);
                for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                {
                    Bar removed = curPart.getBar(baseIndex+i);
                    //Score.TimeSignatureChangeEvent ev = p.score.getTimeSignatureAtBar(baseIndex+i);
                    curPart.getBars().set(baseIndex+i, new Bar());
                    removed.incRefBarCount(-1);
                }
            }
            else
            {
                Bar target = p.popBarRef;
                p.popPartRef.getBars().set(p.popBar, new Bar());
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
            else p.copiedBars.addBar(p.popBarRef);
        }
        else if(source.getText() == ins_bef)
        {
            //insert Before
            Part curPart = p.popPartRef;
            paster.refresh();
            paster.setTitle("How many pastes to make?");
            int option = JOptionPane.showConfirmDialog(p, paster, "Insert Before (by-value)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int pastes = paster.getValue();
                for(int j = 0; j < pastes; j++)
                {
                    int i = 0;
                    for(Enumeration en = p.copiedBars.elements(); en.hasMoreElements();)
                    {
                        Bar curBar = (Bar)en.nextElement();
                        curPart.insertBar(p.popBar+i, curBar.makeCopy());
                        p.score.addBarToEndExceptForOneLine(p.popLine);
                        i++;
                    }
                }
                //fixTimeSignaturesAtLine(p.popLine);
            }
        }
        else if(source.getText() == ins_aft)
        {
            //insert Before
            Part curPart = p.popPartRef;
            paster.refresh();
            paster.setTitle("How many pastes to make?");
            int option = JOptionPane.showConfirmDialog(p, paster, "Insert After (by-value)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int pastes = paster.getValue();
                for(int j = 0; j < pastes; j++)
                {
                    int i = 0;
                    for(Enumeration en = p.copiedBars.elements(); en.hasMoreElements();)
                    {
                        Bar curBar = (Bar)en.nextElement();
                        curPart.insertBar(p.popBar+1+i, curBar.makeCopy());
                        p.score.addBarToEndExceptForOneLine(p.popLine);
                        i++;
                    }
                }
                //fixTimeSignaturesAtLine(p.popLine);
            }
        }
        else if(source.getText() == wri_ovr)
        {
            Part curPart = p.popPartRef;
            int i = p.popBar;
            paster.refresh();
            paster.setTitle("How many pastes to make?");
            int option = JOptionPane.showConfirmDialog(p, paster, "Write Over (by-value)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int pastes = paster.getValue();
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
                //fixTimeSignaturesAtLine(p.popLine);
            }
        }
        else if(source.getText() == ins_bef_ref)
        {
            //insert Before
            Part curPart = p.popPartRef;
            paster.refresh();
            paster.setTitle("How many pastes to make?");
            int option = JOptionPane.showConfirmDialog(p, paster, "Insert Before (by-ref)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int pastes = paster.getValue();
                for(int j = 0; j < pastes; j++)
                {
                    int i = 0;
                    for(Enumeration en = p.copiedBars.elements(); en.hasMoreElements();)
                    {
                        Bar curBar = (Bar)en.nextElement();
                        Bar target = (Bar)curPart.getBar(p.popBar+i);
                        if(p.score.getBarMaxLength(p.popBar+i) == p.score.getBarMaxLength(i))
                        {//ok to paste curBar by REF
                            curBar.incRefBarCount(1); //let it know that another ref of itselft was pasted
                            curPart.insertBar(p.popBar+i, curBar);
                        }
                        else
                        {//uhhhhhh... not ok.... do by value
                            curPart.insertBar(p.popBar+1+i, curBar.makeCopy());
                        }
                        p.score.addBarToEndExceptForOneLine(p.popLine);
                        i++;
                    }
                }
                //fixTimeSignaturesAtLine(p.popLine);
            }
        }
        else if(source.getText() == ins_aft_ref)
        {
            //insert Before
            Part curPart = p.popPartRef;
            paster.refresh();
            paster.setTitle("How many pastes to make?");
            int option = JOptionPane.showConfirmDialog(p, paster, "Insert After (by-ref)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int pastes = paster.getValue();
                for(int j = 0; j < pastes; j++)
                {
                    int i = 0;
                    for(Enumeration en = p.copiedBars.elements(); en.hasMoreElements();)
                    {
                        Bar curBar = (Bar)en.nextElement();
                        Bar target = (Bar)curPart.getBar(p.popBar+i+1);
                        if(p.score.getBarMaxLength(p.popBar+i+1) == p.score.getBarMaxLength(i))
                        {//ok to paste curBar by REF
                            curBar.incRefBarCount(1); //let it know that another ref of itselft was pasted
                            curPart.insertBar(p.popBar+1+i, curBar);
                        }
                        else
                        {//uhhhhhh... not ok.... do by value
                            curPart.insertBar(p.popBar+1+i, curBar.makeCopy());
                        }
                        p.score.addBarToEndExceptForOneLine(p.popLine);
                        i++;
                    }
                }
                //fixTimeSignaturesAtLine(p.popLine);
            }
        }
        /*  Representation Inv
         * if(barAt(i).totalLength() == curBar)
         *        bar.set(i, curBar)
         * else   barAt(i).setBar(curBar);
        */
        else if(source.getText() == wri_ovr_ref)
        {
            Part curPart = p.popPartRef;
            Vector bars = curPart.getBars();
            int i = p.popBar;
            paster.refresh();
            paster.setTitle("How many pastes to make?");
            int option = JOptionPane.showConfirmDialog(p, paster, "Write Over (by-ref)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int pastes = paster.getValue();
                for(int j = 0; j < pastes; j++)
                {
                    int k = 0;
                    for(Enumeration en = p.copiedBars.elements(); en.hasMoreElements();)
                    {
                        Bar curBar = (Bar)en.nextElement();
                        Bar target = (Bar)bars.elementAt(i);
                        if(p.score.getBarMaxLength(i) == p.score.getBarMaxLength(k))
                        {
                            bars.set(i, curBar);
                            curBar.incRefBarCount(1); //let it know that another ref of itselft was pasted
                            target.incRefBarCount(-1);
                        }
                        else
                        {
                            curPart.getBars().set(i, curBar.makeCopy());
                        }
                        if(i+1 == p.score.getTotalBars()) p.score.addBarToEnd();
                        i++;
                        k++;
                    }
                }
                //fixTimeSignaturesAtLine(p.popLine);
            }
        }
        else if(source.getText() == ref_lk)
        {// makes bars that are by ref by VALUE
            if(p.selectedBars.hasBars)
            {
                Part curPart = p.score.getPart(p.selectedBars.lineNumber);
                for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                {
                    int index = Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend) + i;
                    Bar target = curPart.getBar(index);
                    if(target.byRef)
                    {
                        curPart.getBars().set(index, target.makeCopy());
                        target.incRefBarCount(-1); //let it know that a ref of itselft was removed; it'll figure it out
                    }
                }
            }
            else
            {
                Bar target = p.popBarRef;
                if(target.byRef)
                {
                    p.popPartRef.getBars().set(p.popBar, target.makeCopy());
                    target.incRefBarCount(-1); //let it know that a ref of itselft was removed; it'll figure it out
                }
            }
        }
        else if(source.getText() == ins_blank_bef)
        {
            System.out.println("Uhhh.... start.....");
            Part curPart = p.popPartRef;
            Vector bars = curPart.getBars();
            int i = p.popBar;
            paster.refresh();
            paster.setTitle("How many bars to insert?");
            System.out.println("Uhhh.... calling 1st call");
            int option = JOptionPane.showConfirmDialog(p, paster, "Insert Before (blank)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int pastes = paster.getValue();
                System.out.println("Uhhh.... 1st call");
                for(int j = 0; j < pastes; j++)
                {
                    curPart.insertBar(p.popBar, new Bar());
                    p.score.addBarToEndExceptForOneLine(p.popLine);
                }
                System.out.println("Uhhh.... where???");
                //fixTimeSignaturesAtLine(p.popLine);
            }
        }
        else if(source.getText() == ins_blank_aft)
        {
            Part curPart = p.popPartRef;
            Vector bars = curPart.getBars();
            int i = p.popBar;
            paster.refresh();
            paster.setTitle("How many bars to insert?");
            int option = JOptionPane.showConfirmDialog(p, paster, "Insert After (blank)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int pastes = paster.getValue();
                for(int j = 0; j < pastes; j++)
                {
                    curPart.insertBar(p.popBar+1, new Bar());
                    p.score.addBarToEndExceptForOneLine(p.popLine);
                }
                //fixTimeSignaturesAtLine(p.popLine);
            }
        }
        else if(source.getText() == ins_blank_befall)
        {
            paster.refresh();
            paster.setTitle("How many bars to insert?");
            int option = JOptionPane.showConfirmDialog(p, paster, "Blanks Before ALL", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int pastes = paster.getValue();
                //Score.TimeSignatureChangeEvent ev = p.score.getTimeSignatureAtBar(p.popBar);
                //int topN = ev.top;
                //int botN = ev.bottom;
                BandSection[] sections = p.score.getSections();
                for(int i = 0; i < sections.length; i++)
                {
                    for(Enumeration en = sections[i].getParts().elements(); en.hasMoreElements();)
                    {
                        Part curPart = (Part)en.nextElement();
                        for(int j = 0; j < pastes; j++)
                        {
                            curPart.insertBar(p.popBar, new Bar());
                        }
                    }
                }
            }
        }
        else if(source.getText() == ins_blank_aftall)
        {
            paster.refresh();
            paster.setTitle("How many bars to insert?");
            int option = JOptionPane.showConfirmDialog(p, paster, "Blanks After ALL", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int pastes = paster.getValue();
                //int topN = p.popBarRef.topNumber;
                //int botN = p.popBarRef.bottomNumber;
                BandSection[] sections = p.score.getSections();
                for(int i = 0; i < sections.length; i++)
                {
                    for(Enumeration en = sections[i].getParts().elements(); en.hasMoreElements();)
                    {
                        Part curPart = (Part)en.nextElement();
                        for(int j = 0; j < pastes; j++)
                        {
                            curPart.insertBar(p.popBar+1, new Bar());
                        }
                    }
                }
            }
        }
        else if(source.getText() == apply_pl)
        {
            if(p.selectedBars.hasBars)
            {
                Part curPart = p.score.getPart(p.selectedBars.lineNumber);
                refBarCheckList.removeAllElements(); //to make sure we dont go over em 2-wice
                for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                {
                    Bar curBar = curPart.getBar(Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend) + i);
                    if(!refBarCheckList.contains(curBar))
                    {
                        for(Enumeration en = curBar.getNotes().elements(); en.hasMoreElements();)
                        {
                            Note curNote = (Note)en.nextElement();
                            p.ntMd.modifyNote(curNote);
                        }
                        if(curBar.byRef) refBarCheckList.addElement(curBar);
                    }
                }
                refBarCheckList.removeAllElements();
            }
            else
            {
                Bar curBar = p.popBarRef;
                for(Enumeration en = curBar.getNotes().elements(); en.hasMoreElements();)
                {
                    Note curNote = (Note)en.nextElement();
                    p.ntMd.modifyNote(curNote);
                }
            }
        }
        else if(source.getText() == clr_nts)
        {
            if(p.selectedBars.hasBars)
            {
                Part curPart = p.score.getPart(p.selectedBars.lineNumber);
                for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                {
                    int index = Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend) + i;
                    Bar target = curPart.getBar(index);
                    if(target.byRef)
                    {
                        curPart.getBars().set(index, new Bar());
                        target.incRefBarCount(-1); //let it know that a ref of itselft was removed; it'll figure it out
                    }
                    else target.clearBar();
                }
            }
            else
            {
                Bar target = p.popBarRef;
                if(target.byRef)
                {
                    p.popPartRef.getBars().set(p.popBar, new Bar());
                    target.incRefBarCount(-1); //let it know that a ref of itselft was removed; it'll figure it out
                }
                else target.clearBar();
            }
        }
        else if(source.getText() == del_bar)
        {
            if(p.selectedBars.hasBars)
            {
                Part curPart = p.score.getPart(p.selectedBars.lineNumber);
                for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)//just count how many times to rmv the bar
                {
                    if(p.selectedBars.extend < 0)
                    {
                        Bar target = curPart.getBar(p.selectedBars.barNumber+p.selectedBars.extend);
                        target.incRefBarCount(-1); //let it know that a ref of itselft was removed; it'll figure it out
                        curPart.getBars().removeElementAt(p.selectedBars.barNumber+p.selectedBars.extend);
                    }
                    else
                    {
                        Bar target = curPart.getBar(p.selectedBars.barNumber);
                        target.incRefBarCount(-1); //let it know that a ref of itselft was removed; it'll figure it out
                        curPart.getBars().removeElementAt(p.selectedBars.barNumber);
                    }
                    curPart.addBarToEnd();
                }
            }
            else
            {
                p.popBarRef.incRefBarCount(-1); //let it know that a ref of itselft was removed; it'll figure it out
                p.popPartRef.removeBar(p.popBar);
                p.popPartRef.addBarToEnd();
            }
            //fixTimeSignaturesAtLine(p.popLine);
        }
        else if(source.getText() == del_bar_all)
        {//:)
            int option = JOptionPane.showConfirmDialog(p, new JLabel("Are you sure you want to delete these bars from the Score?"), "Please confirm:", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(option == JOptionPane.YES_OPTION)
            {
                if(p.selectedBars.hasBars)
                {
                    BandSection[] sec = p.score.getSections();
                    for(int j = 0; j < sec.length; j++)
                    {
                        for(Enumeration en = sec[j].getParts().elements(); en.hasMoreElements();)
                        {
                            Part curPart = (Part)en.nextElement();
                            for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)//just count how many times to rmv the bar
                            {
                                if(p.selectedBars.extend < 0)
                                {
                                    Bar target = curPart.getBar(p.selectedBars.barNumber+p.selectedBars.extend);
                                    target.incRefBarCount(-1); //let it know that a ref of itselft was removed; it'll figure it out
                                    curPart.getBars().removeElementAt(p.selectedBars.barNumber+p.selectedBars.extend);
                                }
                                else
                                {
                                    Bar target = curPart.getBar(p.selectedBars.barNumber);
                                    target.incRefBarCount(-1); //let it know that a ref of itselft was removed; it'll figure it out
                                    curPart.getBars().removeElementAt(p.selectedBars.barNumber);
                                }
                                curPart.addBarToEnd();
                            }
                        }
                    }
                }
                else
                {
                    BandSection[] sec = p.score.getSections();
                    for(int j = 0; j < sec.length; j++)
                    {
                        for(Enumeration en = sec[j].getParts().elements(); en.hasMoreElements();)
                        {
                            Part curPart = (Part)en.nextElement();
                            curPart.getBar(p.popBar).incRefBarCount(-1); //let it know that a ref of itselft was removed; it'll figure it out
                            curPart.getBars().removeElementAt(p.popBar);
                            curPart.addBarToEnd();
                        }
                    }
                }
            }
        }
        else if(source.getText() == add_prt)
        {
            Part newPart = new Part();
            for(Enumeration en = p.popPartRef.getBars().elements(); en.hasMoreElements();)
            {
                Bar curBar = (Bar)en.nextElement();
                newPart.addBar(new Bar());
            }
            p.popSectionRef.addPart(newPart);
            p.addEdit(new PartAddedEdit(p.popSectionRef, newPart));
        }
        else if(source.getText() == rmv_prt)
        {
            int option = JOptionPane.showConfirmDialog(p, new JLabel("Are you sure you want to remove this part?"), "Please confirm:", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(option == JOptionPane.YES_OPTION)
            {
                Part removed = p.popPartRef;
                int posit = p.popSectionRef.getParts().indexOf(removed);
                p.popSectionRef.getParts().removeElement(removed);
                p.addEdit(new PartRemovedEdit(p.popSectionRef, posit, removed));
            }
        }
        else if(source.getText() == inst_chg)
        {
            BandSection.InstrumentChangeEvent ev = p.popSectionRef.getInstrumentChangeEventAtBar(p.popBar);
            int oldInst = ev.instrumentNumber;
            storage.setProgram(oldInst);
            int option = JOptionPane.showConfirmDialog(this, storage, "Select an instrument", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int newInst = storage.getInstrument();
                if(newInst != oldInst)
                {
                    BandSection theSection = p.score.getSectionAtPart(p.popLine);
                    BandSection.InstrumentChangeEvent[] removed = theSection.addInstrumentChangeEvent(newInst, p.popBar);
                    p.addEdit(new InstrumentChangeEdit(theSection, p.popBar, newInst, oldInst, removed));
                }
            }
        }
        else if(source.getText() == time_sig)
        {
            //Change bar numbers
            Score.TimeSignatureChangeEvent ev = p.score.getTimeSignatureAtBar(p.popBar);
            int oldTop = ev.top;
            int oldBot = ev.bottom;
            bns.setBarNumbers(oldTop, oldBot);
            int option = JOptionPane.showConfirmDialog(p, bns, "Set the value", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                BarNumberSetter.TimeSig nums = bns.getBarNumbers();
                if(oldTop != nums.topNumber || oldBot != nums.bottomNumber)
                {
                    Score.TimeSignatureChangeEvent[] olds = p.score.addTimeSignatureChangeEvent(nums.topNumber, nums.bottomNumber, p.popBar+1);
                    Score.TimeSignatureChangeEvent newOne = p.score.getTimeSignatureAtBar(p.curBar);
                    p.addEdit(new TimeSigChangeEdit(p.score, olds, newOne));
                }
            }
            
        }
        else if(source.getText() == temp_chg)
        {
            int oldTempo = p.score.getTempoAtBar(p.popBar);
            tempozer.setValue(oldTempo);
            int option = JOptionPane.showConfirmDialog(p, tempozer, "Tempo Change", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int newTempo = tempozer.getValue();
                if(newTempo != oldTempo)
                {
                    Score.TempoChangeEvent[] removed = p.score.addTempoChangeEvent(newTempo, p.popBar);
                    p.addEdit(new TempoChangeEdit(p.score, p.popBar, newTempo, oldTempo, removed));
                }
            }
        }
        else if(source.getText() == chordize)
        {
            int option = JOptionPane.showConfirmDialog(p, chorder, "Add a Note to Chords", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int val = chorder.getValue();
                boolean above = chorder.isAbove();
                if(p.selectedBars.hasBars)
                {
                    Part curPart = p.score.getPart(p.selectedBars.lineNumber);
                    refBarCheckList.removeAllElements();
                    for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                    {
                        Bar curBar = curPart.getBar(Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend) + i);
                        if(!refBarCheckList.contains(curBar))
                        {
                            for(Enumeration en2 = curBar.getNotes().elements(); en2.hasMoreElements();)
                            {
                                Note curNote = (Note)en2.nextElement();
                                curNote.addNoteToEdge(val, above);
                            }
                            if(curBar.byRef) refBarCheckList.addElement(curBar);
                        }
                    }
                    refBarCheckList.removeAllElements();
                }
                else
                {
                    for(Enumeration en2 = p.popBarRef.getNotes().elements(); en2.hasMoreElements();)
                    {
                        Note curNote = (Note)en2.nextElement();
                        curNote.addNoteToEdge(val, above);
                    }
                }
            }
        }
        else if(source.getText() == transpz)
        {
            int option = JOptionPane.showConfirmDialog(p, transposer, "Transpoze", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(option == JOptionPane.OK_OPTION)
            {
                int delta = transposer.getValue();
                if(p.selectedBars.hasBars)
                {
                    Part curPart = p.score.getPart(p.selectedBars.lineNumber);
                    for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                    {
                        Bar curBar = curPart.getBar(Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend) + i);
                        int newD = curBar.getExtremeTransposition(delta);
                             if(delta > 0 && delta > newD) delta = newD;
                        else if(delta < 0 && delta < newD) delta = newD;
                    }
                    refBarCheckList.removeAllElements();
                    for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                    {
                        Bar curBar = curPart.getBar(Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend) + i);
                        if(!refBarCheckList.contains(curBar))
                        {
                            curBar.transposeBar(delta);
                            if(curBar.byRef) refBarCheckList.addElement(curBar);
                        }
                    }
                    refBarCheckList.removeAllElements();
                }
                else p.popBarRef.transposeBar(p.popBarRef.getExtremeTransposition(delta));
            }
        }
        else if(source.getText() == compHalf)
        {//precondition: all bar #s are SAME
            if(!p.selectedBars.hasBars)
            {
                p.popPartRef.getBars().removeElementAt(p.popBar);
                
                Bar newBar = new Bar();
                p.popPartRef.insertBar(p.popBar, newBar);
                p.popBarRef.incRefBarCount(-1);
                //now do the THING!!!!!
                for(Enumeration en = p.popBarRef.getNotes().elements(); en.hasMoreElements();)
                {
                    Note newNote = ((Note)en.nextElement()).makeCopy();
                    newNote.setLength(newNote.getLength()/2);
                    newBar.addNote(newNote);
                }
            }//ok:>
            else
            {
                refBarCheckList.removeAllElements();
                Part curPart = p.popPartRef;
                //Score.TimeSignatureChangeEvent ev = p.score.getTimeSignatureAtBar(p.selectedBars.barNumber);
                //int top = ev.top;
                //int bot = ev.bottom;
                int baseIndex = Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend);
                for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                {
                    Bar removed = curPart.getBar(baseIndex);
                    refBarCheckList.addElement(removed);
                    curPart.getBars().removeElementAt(baseIndex);
                    removed.incRefBarCount(-1);
                }
                Enumeration en = refBarCheckList.elements();
                int j = 0;
                for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                {
                    Bar newBar = new Bar();
                    if(en.hasMoreElements())
                    {
                        Bar curBar = (Bar)en.nextElement();
                        for(Enumeration en2 = curBar.getNotes().elements(); en2.hasMoreElements();)
                        {
                            Note newNote = ((Note)en2.nextElement()).makeCopy();
                            newNote.setLength(newNote.getLength()/2);
                            newBar.addNote(newNote);
                        }
                        if(!isFull(p.score, curBar, baseIndex+j))
                        {
                            Note littleRest = new Note(0, getRemainder(p.score, curBar, baseIndex+j)/2, 0);
                            newBar.addNote(littleRest);
                        }
                        j++;
                    }
                    if(en.hasMoreElements())
                    {
                        Bar curBar = (Bar)en.nextElement();
                        for(Enumeration en2 = curBar.getNotes().elements(); en2.hasMoreElements();)
                        {
                            Note newNote = ((Note)en2.nextElement()).makeCopy();
                            newNote.setLength(newNote.getLength()/2);
                            newBar.addNote(newNote);
                        }
                        if(!isFull(p.score, curBar, baseIndex+j))
                        {
                            Note littleRest = new Note(0, getRemainder(p.score, curBar, baseIndex+j)/2, 0);
                            newBar.addNote(littleRest);
                        }
                        j++;
                    }
                    curPart.insertBar(baseIndex+i, newBar);
                }
                refBarCheckList.removeAllElements();
            }//ok:>
        }
        else if(source.getText() == comp2)
        {
            if(!p.selectedBars.hasBars)
            {//no selection - do just one bar
                p.popPartRef.getBars().removeElementAt(p.popBar);
                //Bar newBar = new Bar(p.popBarRef.getTopNumber(), p.popBarRef.getBottomNumber());
                Bar newBar = new Bar();
                p.popPartRef.insertBar(p.popBar, newBar);
                p.popBarRef.incRefBarCount(-1);
                //now do the THING!!!!!
                for(Enumeration en = p.popBarRef.getNotes().elements(); en.hasMoreElements();)
                {
                    Note newNote = ((Note)en.nextElement()).makeCopy();
                    newNote.setLength(newNote.getLength()/2);
                    newBar.addNote(newNote);
                }
                if(!isFull(p.score, p.popBarRef, p.popBar))
                {
                    Note littleRest = new Note(0, getRemainder(p.score, p.popBarRef, p.popBar)/2, 0);
                    newBar.addNote(littleRest);
                }
                for(Enumeration en = p.popBarRef.getNotes().elements(); en.hasMoreElements();)
                {
                    Note newNote = ((Note)en.nextElement()).makeCopy();
                    newNote.setLength(newNote.getLength()/2);
                    newBar.addNote(newNote);
                }
            }//ok:>
            else
            {
                refBarCheckList.removeAllElements();
                Part curPart = p.popPartRef;
                //int top = 0;//curPart.getBar(p.selectedBars.barNumber).getTopNumber();
                //int bot = 0;//curPart.getBar(p.selectedBars.barNumber).getBottomNumber();
                int baseIndex = Math.min(p.selectedBars.barNumber, p.selectedBars.barNumber+p.selectedBars.extend);
                for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                {
                    Bar removed = curPart.getBar(baseIndex);
                    refBarCheckList.addElement(removed);
                    curPart.getBars().removeElementAt(baseIndex);
                    removed.incRefBarCount(-1);
                }
                Enumeration en = refBarCheckList.elements();
                int j = 0;
                for(int i = 0; i <= Math.abs(p.selectedBars.extend); i++)
                {
                    //Bar newBar = new Bar(top, bot);
                    Bar newBar = new Bar();
                    if(en.hasMoreElements())
                    {
                        Bar curBar = (Bar)en.nextElement();
                        for(Enumeration en2 = curBar.getNotes().elements(); en2.hasMoreElements();)
                        {
                            Note newNote = ((Note)en2.nextElement()).makeCopy();
                            newNote.setLength(newNote.getLength()/2);
                            newBar.addNote(newNote);
                        }
                        if(!isFull(p.score, curBar, baseIndex+j))
                        {
                            Note littleRest = new Note(0, getRemainder(p.score, curBar, baseIndex+j)/2, 0);
                            newBar.addNote(littleRest);
                        }
                        j++;
                    }
                    if(!en.hasMoreElements())
                    {
                        en = refBarCheckList.elements();
                        j = 0;
                    }
                    if(en.hasMoreElements())
                    {
                        Bar curBar = (Bar)en.nextElement();
                        for(Enumeration en2 = curBar.getNotes().elements(); en2.hasMoreElements();)
                        {
                            Note newNote = ((Note)en2.nextElement()).makeCopy();
                            newNote.setLength(newNote.getLength()/2);
                            newBar.addNote(newNote);
                        }
                        if(!isFull(p.score, curBar, baseIndex+j))
                        {
                            Note littleRest = new Note(0, getRemainder(p.score, curBar, baseIndex+j)/2, 0);
                            newBar.addNote(littleRest);
                        }
                        j++;
                    }
                    if(!en.hasMoreElements())
                    {
                        en = refBarCheckList.elements();
                        j = 0;
                    }
                    curPart.insertBar(baseIndex+i, newBar);
                }
                refBarCheckList.removeAllElements();
            }//ok:>
        }
        //-----------------------
        else if(source.getText() == play_sl)
        {
            if(player != null)
            {
                p.setSound(false);
                player.playSolo(p.score, p.popSectionRef, p.popPartRef, p.curBar, p.score.getSectionIndexAtPart(p.popLine));
            }
            else System.out.println("Uhh... Mikhail? I think you forgot to set the player 4 the ScoreP.pop");
        }
        p.repaint();
    }
    private boolean isFull(Score score, Bar bar, int barPosit)
    {
        return (bar.getCurLength() > score.getBarMaxLength(barPosit));
    }
    private double getRemainder(Score score, Bar bar, int barPosit)
    {
        return score.getBarMaxLength(barPosit) - bar.getCurLength();
    }
}
