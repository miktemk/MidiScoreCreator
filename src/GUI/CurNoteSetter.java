package GUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.lang.*;
import java.util.*;
import DataStructure.*;

public class CurNoteSetter extends JPanel implements ChangeListener
{
    private class SlurmListener implements ActionListener
    {
        JToggleButton other, other2;
        ScorePanel p;
        byte slurm;
        public SlurmListener(JToggleButton other, JToggleButton other2, ScorePanel p, byte slurm)
        {
            this.other = other;
            this.other2 = other2;
            this.p = p;
            this.slurm = slurm;
        }
        public void actionPerformed(ActionEvent e)
        {
            JToggleButton source = (JToggleButton)e.getSource();
            if(source.isSelected())
            {
                other.setSelected(false);
                other2.setSelected(false);
                p.setSlurm(slurm);
            }
            else p.setSlurm(Note.NONE);
        }
    }
    private class RestButtonListener implements ActionListener
    {
        JSlider slider;
        ScorePanel p;
        JLabel label;
        public RestButtonListener(JSlider slider, ScorePanel p, JLabel label)
        {
            this.slider = slider;
            this.p = p;
            this.label = label;
        }
        public void actionPerformed(ActionEvent e)
        {
            JToggleButton source = (JToggleButton)e.getSource();
            if(source.isSelected())
            {
                slider.setEnabled(false);
                label.setText("Rest");
                p.setCurNoteVolume(0);
            }
            else
            {
                slider.setEnabled(true);
                if(slider.getValue() > 0) label.setText("Volume: "+slider.getValue());
                p.setCurNoteVolume(slider.getValue());
            }
        }
    }
    ScorePanel p;
    public static final double[] lengths = {1.0, 0.5, 1.0/3, 0.25, 0.125, 0.0625, 1.0/6, 1.0/12, 0.75, 0.375, 0.1875};
    
    JLabel lengthLabel, volumeLabel;
    public CurNoteSetter(ScorePanel p)
    {
        this.p = p;
        NoteLengthSetter top = new NoteLengthSetter(p);
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());
        
        volumeLabel = new JLabel("Volume: "+p.curNoteVolume);
        
        JSlider volumeSlider = new JSlider(0, 127, p.curNoteVolume);
        volumeSlider.addChangeListener(this);
        JToggleButton restButton = new JToggleButton("REST");
        restButton.addActionListener(new RestButtonListener(volumeSlider, p, volumeLabel));
        JPanel volumePanel = new JPanel(new BorderLayout());
        volumePanel.add(volumeSlider, BorderLayout.CENTER);
        volumePanel.add(restButton, BorderLayout.WEST);
        
        //JCheckBox slurBox = new JCheckBox("Slured", p.curNoteIsSlurred);
        //slurBox.addItemListener(this);
        JToggleButton slurTB = new JToggleButton("Tied");
        JToggleButton heldTB = new JToggleButton("Held");
        JToggleButton stacTB = new JToggleButton("Stacatto");
        slurTB.addActionListener(new SlurmListener(heldTB, stacTB, p, Note.SLURED));
        heldTB.addActionListener(new SlurmListener(stacTB, slurTB, p, Note.HELD));
        stacTB.addActionListener(new SlurmListener(slurTB, heldTB, p, Note.STACATTO));
        JPanel slurPanel = new JPanel(new GridLayout(1, 3));
        slurPanel.add(slurTB);
        slurPanel.add(heldTB);
        slurPanel.add(stacTB);
        slurPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        
        center.add(volumeLabel, BorderLayout.EAST);
        center.add(volumePanel, BorderLayout.CENTER);
        center.add(slurPanel, BorderLayout.WEST);
        
        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }
    public void stateChanged(ChangeEvent e)
    {
        JSlider source = (JSlider)e.getSource();
        p.setCurNoteVolume(source.getValue());
        if(source.getValue() == 0)  volumeLabel.setText("Rest");
        else                        volumeLabel.setText("Volume: "+source.getValue());
    }
}
