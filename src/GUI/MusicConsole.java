package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class MusicConsole extends JFrame
{
    private class ClearListener implements ActionListener
    {
        JTextArea textArea;
        public ClearListener(JTextArea textArea)
        {
            this.textArea = textArea;
        }
        public void actionPerformed(ActionEvent e)
        {
            textArea.setText("");
        }
    }
    public JTextArea textArea;
    public MusicConsole()
    {
        super("Console for PROBLEM Messages");
        textArea = new JTextArea(80, 10);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        JButton b = new JButton("Clear List");
        b.addActionListener(new ClearListener(textArea));
        
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        pane.add(new JLabel("The following problems occured: "), BorderLayout.NORTH);
        pane.add(textArea, BorderLayout.CENTER);
        pane.add(b, BorderLayout.SOUTH);
        
        setSize(400, 300);
    }
    public void append(String msg)
    {
        textArea.append(msg+"\n");
        setVisible(true);
    }
}
