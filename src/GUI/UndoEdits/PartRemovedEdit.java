package GUI.UndoEdits;

import DataStructure.*;
import javax.swing.undo.*;

public class PartRemovedEdit extends UndoableEditBase
{
    BandSection target;
    int posit;
    Part p;
    public PartRemovedEdit(BandSection target, int posit, Part p)
    {
        this.target = target;
        this.posit = posit;
        this.p = p;
    }
    public void undo()
    {
        target.getParts().add(posit, p);
    }
    public void redo()
    {
        target.getParts().removeElement(p);
    }
}
