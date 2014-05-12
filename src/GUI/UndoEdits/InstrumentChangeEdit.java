package GUI.UndoEdits;

import DataStructure.*;
import javax.swing.undo.*;

public class InstrumentChangeEdit extends UndoableEditBase
{
    BandSection target;
    int posit, newTempo, oldTempo;
    BandSection.InstrumentChangeEvent[] removed;
    public InstrumentChangeEdit(BandSection target, int posit, int newTempo, int oldTempo, BandSection.InstrumentChangeEvent[] removed)
    {
        this.target = target;
        this.posit = posit;
        this.newTempo = newTempo;
        this.oldTempo = oldTempo;
        this.removed = removed;
    }
    public void undo()
    {
        target.addInstrumentChangeEvent(oldTempo, posit);
        for(int i = 0; i < removed.length; i++)
        {
            target.addInstrumentChangeEvent(removed[i].instrumentNumber, removed[i].barNumberOfOccurence);
        }
    }
    public void redo()
    {
        target.addInstrumentChangeEvent(newTempo, posit);
    }
}
