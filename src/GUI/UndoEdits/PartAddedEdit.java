package GUI.UndoEdits;

import DataStructure.*;
import javax.swing.undo.*;

public class PartAddedEdit extends UndoableEditBase
{
    BandSection target;
    Part p;
    public PartAddedEdit(BandSection target, Part p)
    {
        this.target = target;
        this.p = p;
    }
    public void undo()
    {
        target.getParts().removeElement(p);
    }
    public void redo()
    {
        target.addPart(p);
    }
}
