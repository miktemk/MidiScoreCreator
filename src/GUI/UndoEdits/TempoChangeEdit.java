package GUI.UndoEdits;

import DataStructure.*;
import javax.swing.undo.*;

public class TempoChangeEdit extends UndoableEditBase
{
    Score target;
    int posit, newTempo, oldTempo;
    Score.TempoChangeEvent[] removed;
    public TempoChangeEdit(Score target, int posit, int newTempo, int oldTempo, Score.TempoChangeEvent[] removed)
    {
        this.target = target;
        this.posit = posit;
        this.newTempo = newTempo;
        this.oldTempo = oldTempo;
        this.removed = removed;
    }
    public void undo()
    {
        target.addTempoChangeEvent(oldTempo, posit);
        for(int i = 0; i < removed.length; i++)
        {
            target.addTempoChangeEvent(removed[i].tempo, removed[i].barNumberOfOccurence);
        }
    }
    public void redo()
    {
        target.addTempoChangeEvent(newTempo, posit);
    }
}
