package GUI.UndoEdits;

import DataStructure.*;
import javax.swing.undo.*;

public class AddNoteEdit extends UndoableEditBase
{
    Note target;
    int noteNumber;
    public AddNoteEdit(Note target, int noteNumber)
    {
        this.target = target;
        this.noteNumber = noteNumber;
    }
    public void undo()
    {
        target.addNote(noteNumber);
    }
    public void redo()
    {
        target.addNote(noteNumber);
    }
}
