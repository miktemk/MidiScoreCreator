package GUI.UndoEdits;

import DataStructure.*;
import javax.swing.undo.*;

public class InsertNoteEdit extends UndoableEditBase
{
    Score score;
    Bar target;
    int barPosit;
    Note note;
    int posit;
    public InsertNoteEdit(Score score, Bar target, int barPosit, Note note, int posit)
    {
        this.score = score;
        this.target = target;
        this.barPosit = barPosit;
        this.note = note;
        this.posit = posit;
    }
    public void undo()
    {
        target.removeNote(posit);
    }
    public void redo()
    {
        target.addNote(note, posit, score.getBarMaxLength(barPosit));
    }
}
