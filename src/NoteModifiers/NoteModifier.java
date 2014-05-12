package NoteModifiers;

import java.awt.*;
import javax.swing.*;
import DataStructure.*;
import GUI.*;

public abstract class NoteModifier
{
    public NoteModifier() {}
    public abstract Component getPanel();
    public abstract void resetPanel();
    public abstract void update();
    public abstract void paintButton(Graphics2D g, JToggleButton b);
    public abstract void modifyNote(Note note);
    public abstract int getHighOrder();
    public abstract int getOrder();
    public void setScorePanel(ScorePanel p) {}
    public void openingDialog() {}
}
