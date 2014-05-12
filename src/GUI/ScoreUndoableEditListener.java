package GUI;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

public class ScoreUndoableEditListener implements UndoableEditListener
{
    UndoManager undo;
    public ScoreUndoableEditListener(UndoManager undo)
    {
        this.undo = undo;
    }
    public void undoableEditHappened(UndoableEditEvent e)
    {
        //Remember the edit and update the menus
        undo.addEdit(e.getEdit());
        //undoAction.updateUndoState();
        //redoAction.updateRedoState();
    }
}
