package GUI.UndoEdits;

import DataStructure.*;
import javax.swing.undo.*;

public class NoteChangeEdit extends UndoableEditBase
{
    Bar target;
    Note oldNote, newNote;
    int posit;
    public NoteChangeEdit(Bar target, Note oldNote, Note newNote, int posit)
    {
        this.target = target;
        this.oldNote = oldNote;
        this.newNote = newNote;
        this.posit = posit;
    }
    public void undo()
    {
        target.getNotes().setElementAt(oldNote, posit);
    }
    public void redo()
    {
        target.getNotes().setElementAt(newNote, posit);
    }
}
