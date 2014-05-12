package GUI.UndoEdits;

import javax.swing.undo.*;

public abstract class UndoableEditBase implements UndoableEdit
{
    public boolean addEdit(UndoableEdit anEdit) { return false; }
    public boolean canRedo() { return true; }
    public boolean canUndo() { return true; }
    public void die() {}
    public String getPresentationName()     { return ""; }
    public String getRedoPresentationName() { return ""; }
    public String getUndoPresentationName() { return ""; }
    public boolean isSignificant() { return true; }
    public boolean replaceEdit(UndoableEdit anEdit) { return false; }
}
