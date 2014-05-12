package GUI;

import javax.swing.*;

public class AboutDialog extends JPanel
{
    JFrame parent;
    public AboutDialog(JFrame parent)
    {
        this.parent = parent;
        add(new JLabel("<html><center><font size=5 align=CENTER>MIDI Score Creator v2.1 - <font color=blue>July 28, 2006 Edition</font><br>Creator created by: Mikhail Temkine<br>Go AHEAD! Compose!</font></center></html>"));
    }
    public void popUp()
    {
        JOptionPane.showMessageDialog(null, this, "About MIDI Score Creator", JOptionPane.PLAIN_MESSAGE);
    }
    //public static void main(String[] args)
    //{ new AboutDialog(null).popUp(); }
}
