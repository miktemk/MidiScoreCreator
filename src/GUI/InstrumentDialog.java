package GUI;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.lang.*;
import javax.sound.midi.*;

public class InstrumentDialog extends JPanel
{
    private class OtherListener implements ActionListener
    {
        int index = 0;
        InstrumentsTable it;
        public OtherListener(InstrumentsTable it)
        {
            this.it = it;
            
        }
        public void actionPerformed(ActionEvent e)
        {
            it.table.clearSelection();
            JComboBox cb = (JComboBox)e.getSource();
            index = cb.getSelectedIndex();
            //System.out.println("index = "+index);
            curInstrument = index+128;
            otherLabel.setEnabled(true);
        }
    }
    
    private class SamplePlayer extends JPanel
    {
        MidiChannel chan;
        final int sect = 30;
        boolean rectOn = false;
        int sectID = -1;
        public SamplePlayer(MidiChannel chan)
        {
            this.chan = chan;
            addMouseListener(new MouseAdapter()
            {
                public void mouseEntered(MouseEvent e)
                {
                    rectOn = true;
                }
                public void mouseExited(MouseEvent e)
                {
                    rectOn = false;
                    sectID = -1;
                    off();
                    repaint();
                }
            });
            addMouseMotionListener(new MouseMotionAdapter()
            {
                public void mouseMoved(MouseEvent e)
                {
                    rectOn = true;
                    int tmp = sect*e.getX()/getWidth();
                    if(tmp != sectID)
                    {
                        sectID = tmp;
                        playSample(sectID+50);
                        repaint();
                    }
                }
            });
            setPreferredSize(new Dimension(200, 40));
        }
        private void playSample(int note)
        {
            Patch p = inst[curInstrument].getPatch();
            chan.programChange(p.getBank(), p.getProgram());
            chan.allNotesOff();
            chan.noteOn(note, 100);
        }
        private void off()
        {
            chan.allNotesOff();
        }
        public void paint(Graphics g1)
        {
            Graphics2D g = (Graphics2D)g1;
            g.setColor(Color.white);
            g.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
            if(rectOn)
            {
                g.setColor(Color.cyan);
                g.fill(new Rectangle2D.Double(getWidth()*sectID/sect+1, 1, getWidth()/sect, getHeight()-2));
            }
            g.setColor(Color.black);
            g.draw(new Rectangle2D.Double(0, 0, getWidth()-1, getHeight()-1));
            for(int i = 1; i < sect; i++)
            {
                g.draw(new Line2D.Double(getWidth()*i/sect, 0, getWidth()*i/sect, getHeight()));
            }
        }
    }
    private class InstrumentItem
    {
        Instrument i;
        public InstrumentItem(Instrument i)
        {
            this.i = i;
        }
        public String toString()
        {
            return i.getName();
        }
    }
    int curInstrument = 0;
    MidiChannel chan;
    Instrument[] inst;
    InstrumentsTable it;
    IntSetPanel bankSetter;
    JLabel otherLabel = null;
    JComboBox otherBox;
    public InstrumentDialog(Instrument[] inst, MidiChannel ch)
    {
        this.chan = ch;
        this.inst = inst;
        it = new InstrumentsTable(inst, this);
        /*JLabel tester = new JLabel("--->>> Hear it!");
        tester.addMouseListener(new MouseAdapter()
        {
            public void mouseEntered(MouseEvent e)
            {
                chan.programChange(curInstrument);
                chan.noteOn(60, 100);
            }
            public void mouseExited(MouseEvent e)
            {
                chan.allNotesOff();
            }
        });*/
        
        Vector namez = new Vector();
        for(int i = 128; i < inst.length; i++)
        {
            namez.addElement(new InstrumentItem(inst[i]));
        }
        /*for(int i = 0; i < inst.length; i++)
        {
            Patch p = inst[i].getPatch();
            if(p.getBank() == 1) System.out.println(inst[i]);
        }//*/
        
        otherLabel = new JLabel("Other: ");
        otherBox = new JComboBox(namez);
        otherBox.addActionListener(new OtherListener(it));
        JPanel otherP = new JPanel(new FlowLayout());
        otherP.add(otherLabel);
        otherP.add(otherBox);
        /*bankSetter = new IntSetPanel(0, 16383, "Bank number:");
        JPanel bankP = new JPanel(new FlowLayout());
        bankP.add(bankSetter);*/
        SamplePlayer sp = new SamplePlayer(chan);
        JPanel bot = new JPanel(new GridLayout(2, 1));
        bot.add(otherP);
        bot.add(sp);
        
        setLayout(new BorderLayout());
        add(it, BorderLayout.CENTER);
        add(bot, BorderLayout.SOUTH);
    }
    public void setProgram(int instrumentNumber)
    {
        otherBox.setSelectedIndex(Math.max(-1, instrumentNumber-128));
        otherLabel.setEnabled(instrumentNumber > 127);
        it.setProgram(instrumentNumber);
    }
    public int getInstrument()
    {
        return curInstrument;
    }
    /*public int resetInstrument(int instr)
    {
        curInstrument = instr;
        //show();
        if(curInstrument < 0) curInstrument = instr;
        return curInstrument;
    }//*/
}
