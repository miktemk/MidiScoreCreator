package GUI.UndoEdits;

import DataStructure.*;
import javax.swing.undo.*;

public class RemoveNoteEdit extends UndoableEditBase
{
    Score score;
    Bar target;
    int barPosit;
    Note note;
    int noteNumber;
    int posit;
    public RemoveNoteEdit(Score score, Bar target, int barPosit, Note note, int noteNumber, int posit)
    {
        this.score = score;
        this.target = target;
        this.barPosit = barPosit;
        this.note = note;
        this.noteNumber = noteNumber;
        this.posit = posit;
    }
    public void undo()
    {
        if(note.getNotes().size() == 0)
            note.addNote(noteNumber);
        target.addNote(note, posit, score.getBarMaxLength(barPosit));
    }
    public void redo()
    {
        target.removeNote(posit);
    }
}
