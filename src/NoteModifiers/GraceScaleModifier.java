package NoteModifiers;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import java.io.*;
import javax.sound.midi.*;
import DataStructure.*;
import Features.*;
import GUI.*;

public class GraceScaleModifier extends NoteModifier implements ActionListener,
                                                                FocusListener,
                                                                ItemListener
{
    private class PlayButton extends JButton
    {
        Border myBorder;
        public PlayButton(String name)
        {
            super(name);
            myBorder = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), BorderFactory.createLineBorder(Color.blue)), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.cyan), BorderFactory.createLineBorder(Color.white)));
            addMouseListener(new MouseAdapter()
            {
                public void mouseEntered(MouseEvent e)
                {
                    setBorder(myBorder);
                }
                public void mouseExited(MouseEvent e)
                {
                    setBorder(null);
                }
            });
        }
    }
    /** filters only those files with a .class extension */
    private class MyFilter implements FileFilter
    {
        public MyFilter(){}
        public boolean accept(File pathname)
        {
            if(pathname.isDirectory()) return false;
            else
            {
                if(pathname.getName().indexOf(".scl") != -1) return true;
                else                                         return false;
            }
        }
    }
    private class ScaleFilter extends javax.swing.filechooser.FileFilter
    {
        public ScaleFilter(){}
        public boolean accept(File pathname)
        {
            if(pathname.isDirectory()) return true;
            else
            {
                if(pathname.getName().indexOf(".scl") != -1) return true;
                else                                         return false;
            }
        }
        public String getDescription()
        { return "Scale Files (*.scl)"; }
    }
    private class ScaleItem
    {
        String name, strName;
        int[] scale;
        public ScaleItem(String name, String strName, int[] scale)
        {
            this.name = name;
            this.strName = strName;
            this.scale = scale;
        }
        public String toString()
        {
            return strName;
        }
    }
    JPanel mainPanel;
    JTextField sclNotesTF, iteratTF, semitonesTF, octavesTF;
    JComboBox scalesCB;
    Vector scales;
    int sclNotes, basicN, index;
    int iterations, semitones, octaves;
    final static int[] zeroScale = {0, 0};
    JFileChooser fileChooser;
    SAXParser parser = null;
    ScorePanel scoreP = null;
    MidiChannel chan = null;
    public GraceScaleModifier()
    {
        scales = new Vector();
        scalesCB = new JComboBox(scales);
        loadScales();
        if(scales.size() > 0)
        {
            scalesCB.setSelectedIndex(0);
            index = 0;
        }
        int[] curScl = getCurScale();
        if(curScl != null) sclNotes = curScl.length;
        else               sclNotes = 1;
        basicN = sclNotes;
        JButton loadB = new JButton("Load...");
        JButton createB = new JButton("CREATE");
        loadB.addActionListener(this);
        createB.addActionListener(this);
        JPanel scaleBot = new JPanel(new GridLayout(1, 2));
        scaleBot.add(loadB);
        scaleBot.add(createB);
        JPanel scaleP = new JPanel(new GridLayout(2, 1));
        scaleP.add(scalesCB);
        scaleP.add(scaleBot);
        scaleP.setBorder(new TitledBorder(null, "Scale", TitledBorder.LEFT, TitledBorder.TOP));
        
        PlayButton playB = new PlayButton("Play Scale");
        playB.addActionListener(this);
        sclNotesTF = new JTextField(""+sclNotes);
        iteratTF = new JTextField();
        semitonesTF = new JTextField();
        octavesTF = new JTextField();
        updateTFs();
        sclNotesTF.addFocusListener(this);
        iteratTF.addFocusListener(this);
        semitonesTF.addFocusListener(this);
        octavesTF.addFocusListener(this);
        JPanel bottom = new JPanel(new GridLayout(4, 2));
        bottom.add(new JLabel("Scale Notes:"));
        bottom.add(sclNotesTF);
        bottom.add(new JLabel("Scale Iterations:"));
        bottom.add(iteratTF);
        bottom.add(new JLabel("Semitones:"));
        bottom.add(semitonesTF);
        bottom.add(new JLabel("Octaves:"));
        bottom.add(octavesTF);
        JPanel center = new JPanel(new BorderLayout());
        center.add(playB, BorderLayout.NORTH);
        center.add(bottom, BorderLayout.CENTER);
        scalesCB.addItemListener(this);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scaleP, BorderLayout.NORTH);
        mainPanel.add(center, BorderLayout.CENTER);
        
        fileChooser = new JFileChooser(new File(System.getProperty("user.dir")+"/Features/Scales"));
        javax.swing.filechooser.FileFilter filter = new ScaleFilter();
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
    }
    public void addScale(String name, int[] scale)
    {
        boolean clear = true;
        int sameNames = 1;
        for(Enumeration e = scales.elements(); e.hasMoreElements();)
        {
            ScaleItem cur = (ScaleItem)e.nextElement();
            if(cur.name.equals(name))
            {
                sameNames++;
                if(cur.scale.length == scale.length)
                {
                    int[] curScale = cur.scale;
                    boolean same = true;
                    for(int i = 0; i < curScale.length; i++)
                    {
                        if(curScale[i] != scale[i])
                        {
                            same = false;
                            break;
                        }
                    }
                    if(same) clear = false;
                }
            }
            if(!clear) break;
        }
        String strName = name;
        if(sameNames > 1) strName = (name+" ("+sameNames+")");
        if(clear) scales.addElement(new ScaleItem(name, strName, scale));
        scalesCB.updateUI();
    }
    public void updateTFs()
    {
        int[] scale = getCurScale();
        if(scale != null)
        {
            sclNotesTF.setText(""+sclNotes);
            iterations = (int)((sclNotes)/(scale.length-1));
            iteratTF.setText(""+iterations);
            if(sclNotes > 0) semitones = scale[scale.length-1]*(int)((sclNotes-1)/(scale.length-1)) + scale[(sclNotes-1) % (scale.length-1)];
            else             semitones = scale[scale.length-1]*((int)((sclNotes+1)/(scale.length-1))-1) + scale[scale.length-1+(sclNotes+1) % (scale.length-1)];
            semitonesTF.setText(""+semitones);
            octaves = (int)(semitones/12);
            octavesTF.setText(""+octaves);
        }
        else
        {
            sclNotesTF.setText("");
            iteratTF.setText("");
            semitonesTF.setText("");
            octavesTF.setText("");
        }
    }
    public int[] getCurScale()
    {
        ScaleItem si = (ScaleItem)scalesCB.getSelectedItem();
        if(si == null) return null;
        return si.scale;
    }
    public String getCurName()
    {
        ScaleItem si = (ScaleItem)scalesCB.getSelectedItem();
        if(si == null) return null;
        return si.strName;
    }
    public void calculateSemitones(int sems)
    {
        int[] scale = getCurScale();
        int offSet = 0;
        if(sems > 0)
        {
            int curDiff = sems;
            for(int i = 1; i < 5000; i++)
            {
                if(i % (scale.length-1) == 0) offSet += scale[scale.length-1];
                int curSem = offSet + scale[i%(scale.length-1)];
                if(curSem == sems)
                {
                    sclNotes = i;
                    break;
                }
                else if(Math.abs(curSem - sems) < curDiff)
                {
                    curDiff = Math.abs(curSem - sems);
                    sclNotes = i;
                }
            }
            sclNotes++;
        }
        else // ie - if(sems < 0)
        {
            int curDiff = -sems;
            for(int i = 1; i < 5000; i++)
            {
                if(i % (scale.length-1) == 0) offSet -= scale[scale.length-1];
                int curSem = offSet - scale[scale.length-1] + scale[scale.length-1-i%(scale.length-1)];
                if(curSem == sems)
                {
                    sclNotes = -i;
                    break;
                }
                else if(Math.abs(curSem - sems) < curDiff)
                {
                    curDiff = Math.abs(curSem - sems);
                    sclNotes = -i;
                }
            }
            sclNotes--;
        }
    }
    public void actionPerformed(ActionEvent e)
    {
        JButton b = (JButton)e.getSource();
        if(b.getText() == "Play Scale")
        {
            if(scoreP != null) scoreP.playScale(getCurScale(), 120.0f);
        }
        else if(b.getText() == "Load...")
        {
            int option = fileChooser.showOpenDialog(mainPanel);
            if(option == JFileChooser.APPROVE_OPTION)
            {
                try
                {
                    ScaleParser handler = new ScaleParser(true);
                    parser.parse(fileChooser.getSelectedFile(), handler);
                    addScale(handler.getName(), handler.getScale());
                }
                catch(Exception ex)
                {
                    System.out.println("Caught: "+ex.getMessage());
                }
            }
        }
        else if(b.getText() == "CREATE")
        {
            scoreP.parent.featuresMenu.openFeature("Features.ScaleMainFrame", mainPanel);
        }
    }
    public void focusGained(FocusEvent e) {}
    public void focusLost(FocusEvent e)
    {
        JTextField source = (JTextField)e.getSource();
        if(source == sclNotesTF)
        {
            //update semies + octaves
            try
            {
                int tmp = Integer.parseInt(sclNotesTF.getText());
                if(tmp == 0) throw new Exception();
                sclNotes = tmp;
                updateTFs();
            }
            catch(Exception ex)
            {
                sclNotesTF.setText(""+sclNotes);
            }
        }
        else if(source == iteratTF)
        {
            try
            {
                int tmp = Integer.parseInt(iteratTF.getText());
                if((tmp == 0) || (tmp == iterations)) throw new Exception();
                sclNotes = tmp*(getCurScale().length-1) + 1;
                updateTFs();
            }
            catch(Exception ex)
            {
                updateTFs();
            }
        }
        else if(source == semitonesTF)
        {
            try
            {
                int sems = Integer.parseInt(semitonesTF.getText());
                if((sems == 0) || (sems == semitones)) throw new Exception();
                calculateSemitones(sems);
                updateTFs();
            }
            catch(Exception ex)
            {
                updateTFs();
            }
        }
        else if(source == octavesTF)
        {
            try
            {
                int ocs = Integer.parseInt(octavesTF.getText());
                if((ocs == 0) || (ocs == octaves)) throw new Exception();
                calculateSemitones(ocs*12);
                updateTFs();
            }
            catch(Exception ex)
            {
                updateTFs();
            }
        }
    }
    public void itemStateChanged(ItemEvent e)
    {
        if(e.getStateChange() == ItemEvent.SELECTED)
            updateTFs();
    }
    public Component getPanel()
    {
        return mainPanel;
    }
    public void resetPanel()
    {
        sclNotes = basicN;
        scalesCB.setSelectedIndex(index);
        updateTFs();
    }
    public void update()
    {
        index = scalesCB.getSelectedIndex();
        basicN = sclNotes;
        updateTFs();
    }
    public void paintButton(Graphics2D g, JToggleButton b)
    {
        g.setColor(Color.black);
        g.draw(new Ellipse2D.Double(3, b.getHeight()*(5.0/9), b.getWidth()/3-3, b.getHeight()*(2.0/9)));
        g.draw(new Line2D.Double(b.getWidth()/3, b.getHeight()*2.0/3, b.getWidth()/3, b.getHeight()/5));
        for(int i = 0; i < 5; i++)
        {
            g.draw(new Line2D.Double(2+b.getWidth()*(1.0/3+i*0.1), b.getHeight()*2.0/3-i*b.getHeight()/10, 2+b.getWidth()*(1.0/3+(i+1)*0.1), b.getHeight()*2.0/3-i*b.getHeight()/10));
        }
        for(int i = 0; i < 4; i++)
        {
            g.draw(new Line2D.Double(2+b.getWidth()*(1.0/3+(i+1)*0.1), b.getHeight()*2.0/3-i*b.getHeight()/10, 2+b.getWidth()*(1.0/3+(i+1)*0.1), b.getHeight()*2.0/3-(i+1)*b.getHeight()/10));
        }
    }
    public void modifyNote(Note note)
    {
        int[] scl = getCurScale();
        String name = getCurName();
        if(scl != null)
        {
            if(scl.length > 1) note.addModifier(new GraceScaleEvents(name, scl, sclNotes));
            else               note.addModifier(new GraceScaleEvents(name, zeroScale, sclNotes));
        }
        else JOptionPane.showMessageDialog(scoreP, new JLabel("No Scale!"), "Error", JOptionPane.ERROR_MESSAGE);
    }
    public int getHighOrder() { return 5; }
    public int getOrder() { return 1; }
    public void setScorePanel(ScorePanel p)
    {
        scoreP = p;
        chan = p.getPlayChannel();
    }
    public void openingDialog()
    {
        loadScales();
    }
    //-------------------------
    private void loadScales()
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try
        {
            parser = factory.newSAXParser();
        }
        catch(Exception e)
        {
            System.out.println("no!!!!!! exception: "+e);
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        File file = new File(System.getProperty("user.dir")+"/Features/Scales");
        File[] files = file.listFiles(new MyFilter());
        for(int i = 0; i < files.length; i++)
        {
            try
            {
                ScaleParser handler = new ScaleParser(true);
                parser.parse(files[i], handler);
                addScale(handler.getName(), handler.getScale());
            }
            catch(Exception e)
            {
                System.out.println("Caught: "+e.getMessage());
            }
        }
    }
}
