package GUI.UndoEdits;

import DataStructure.*;
import javax.swing.undo.*;
import java.util.*;

public class TimeSigChangeEdit extends UndoableEditBase
{
    Score target;
    Score.TimeSignatureChangeEvent[] olds;
    Score.TimeSignatureChangeEvent newOne;
    //int posit, newTop, newBot, oldTop, oldBot, extent;
    //public TimeSigChangeEdit(Score target, int posit, int newTop, int newBot, int oldTop, int oldBot, int extent)
    public TimeSigChangeEdit(Score target, Score.TimeSignatureChangeEvent[] olds, Score.TimeSignatureChangeEvent newOne)
    {
        this.target = target;
        this.olds = olds;
        this.newOne = newOne;
        /*this.posit = posit;
        this.newTop = newTop;
        this.newBot = newBot;
        this.oldTop = oldTop;
        this.oldBot = oldBot;
        this.extent = extent;*/
        
    }
    public void undo()
    {
        //target.setBarNumbersExtent(oldTop, oldBot, posit, extent);
        Vector evs = target.getTimeSignatureChangeEvents();
        evs.remove(newOne);
        for(int i = 0; i < olds.length; i++)
        {
            evs.addElement(olds[i]);
        }
    }
    public void redo()
    {
        Vector evs = target.getTimeSignatureChangeEvents();
        evs.addElement(newOne);
        for(int i = 0; i < olds.length; i++)
        {
            evs.remove(olds[i]);
        }
        //target.setBarNumbers(newTop, newBot, posit);
    }
}
