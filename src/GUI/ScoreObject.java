package GUI;

import javax.swing.undo.*;
import java.io.*;
import DataStructure.*;

public class ScoreObject
{
    Score score;
    int barPosit = 0, linePosit = 0;
    boolean saved = true;
    File file;
    UndoManager manager;
    public ScoreObject(Score score, File file)
    {
        this.score = score;
        this.file = file;
        manager = new UndoManager();
    }
}
