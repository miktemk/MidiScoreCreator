package Features;

import java.awt.*;
import javax.swing.*;
import GUI.*;

public abstract class FeaturesPanel extends JPanel
{
    public FeaturesPanel(LayoutManager lm) { super(lm); }
    public abstract void setScoreMainFrame(ScoreMainFrame smf);
}
