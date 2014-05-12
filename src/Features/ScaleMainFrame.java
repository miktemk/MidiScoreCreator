package Features;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.sound.midi.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;

import org.xml.sax.*;
import javax.xml.parsers.*;
import GUI.*;

public class ScaleMainFrame extends FeaturesPanel implements ActionListener
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
    private class SaveAbortedException extends Exception {}
    
    JTextField nameTF;
    ScalePanel sp;
    ScorePanel scoreP;
    IntSetPanel tempoP;
    JButton saveB;
    JFileChooser fileChooser;
    javax.swing.filechooser.FileFilter filter;
    boolean saved = true;
    boolean newF = true;
    File loadedFile = null;
    public ScaleMainFrame()
    {
        super(new BorderLayout());
        
        nameTF = new JTextField();
        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Scale Name:"), BorderLayout.WEST);
        top.add(nameTF, BorderLayout.CENTER);
        
        PlayButton pb = new PlayButton("PLAY");
        tempoP = new IntSetPanel(1, 10000, "Tempo");
        tempoP.setValue(120);
        JPanel botL = new JPanel(new BorderLayout());
        botL.add(pb, BorderLayout.EAST);
        botL.add(tempoP, BorderLayout.CENTER);
        PianoKeyB kb = new PianoKeyB();
        kb.setPreferredSize(new Dimension(150, 40));
        //JPanel botTop = new JPanel(new GridLayout(1, 2));
        JPanel botTop = new JPanel(new FlowLayout());
        botTop.add(botL);
        botTop.add(kb);
        
        JButton newB  = new JButton("New");
        saveB = new JButton("Save");
        JButton loadB = new JButton("Load");
        newB.addActionListener(this);
        saveB.addActionListener(this);
        loadB.addActionListener(this);
        pb.addActionListener(this);
        JPanel botBot = new JPanel(new GridLayout(1, 3));
        botBot.add(newB);
        botBot.add(saveB);
        botBot.add(loadB);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(botTop, BorderLayout.CENTER);
        bottom.add(botBot, BorderLayout.SOUTH);
        
        sp = new ScalePanel(this, kb);
        
        add(top, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(500, 300));
        
        fileChooser = new JFileChooser(new File("Features/Scales"));
        filter = new ScaleFilter();
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
    }
    public void actionPerformed(ActionEvent e)
    {
        JButton b = (JButton)e.getSource();
        if(b.getText() == "PLAY")
        {
            if(scoreP != null) scoreP.playScale(sp.getScale(), tempoP.getValue());
        }
        else if(b.getText() == "New")
        {
            try { saveUnsaved(); }
            catch(SaveAbortedException ex) { return; }
            
            sp.notes.removeAllElements();
            sp.repaint();
            nameTF.setText("");
            loadedFile = null;
        }
        else if(b.getText() == "Save")
        {
            if(nameTF.getText().equals(""))
            {
                JOptionPane.showMessageDialog(this, new JLabel("Please type in the Scale Name"), "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            fileChooser.setFileFilter(filter);
            int option = fileChooser.showSaveDialog(this);
            if(option == JFileChooser.APPROVE_OPTION)
            {
                File target = fileChooser.getSelectedFile();
                if(target.getName().indexOf(".scl") == -1)
                    target = new File(target.getPath()+".scl");
                try
                {
                    PrintStream stream = new PrintStream(new FileOutputStream(target), true);
                    save(stream);
                    stream.close();
                }
                catch(Exception ex)
                {
                    JOptionPane.showMessageDialog(this, new JLabel("Error: "+ex.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            }
        }
        else if(b.getText() == "Load")
        {
            try { saveUnsaved(); }
            catch(SaveAbortedException ex) { return; }
            
            fileChooser.setFileFilter(filter);
            int option = fileChooser.showOpenDialog(this);
            if(option == JFileChooser.APPROVE_OPTION)
            {
                loadedFile = fileChooser.getSelectedFile();
                SAXParserFactory factory = SAXParserFactory.newInstance();
                ScaleParser handler = new ScaleParser(false);
                try
                {
                    SAXParser parser = factory.newSAXParser();
                    parser.parse(loadedFile, handler);
                    //System.out.println("success");
                }
                catch(Exception ex)
                {
                    //System.out.println("no!!!!!! exception: "+e);
                    //System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
                //-----now :P:P---------------
                System.out.println("name: "+handler.getName());
                nameTF.setText(handler.getName());
                sp.notes.removeAllElements();
                int[] scale = handler.getScale();
                for(int i = 0; i < scale.length; i++)
                {
                    sp.addNote(scale[i]);
                }
                sp.repaint();
            }
            else loadedFile = null;
        }
    }
    public void saveUnsaved() throws SaveAbortedException
    {
        if(!saved)
        {
            int option = 0;
            if(loadedFile == null) option = JOptionPane.showConfirmDialog(this, new JLabel("Save changes to UNTITLED?"), "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            else                   option = JOptionPane.showConfirmDialog(this, new JLabel("Save changes to "+loadedFile.getName()+"?"), "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if(option == JOptionPane.YES_OPTION)
            {
                if(loadedFile == null)//SAVE NEW FILE
                {
                    if(nameTF.getText().equals(""))
                    {
                        JOptionPane.showMessageDialog(this, new JLabel("Please type in the Scale Name"), "Warning", JOptionPane.WARNING_MESSAGE);
                        throw new SaveAbortedException();
                    }
                    fileChooser.setFileFilter(filter);
                    int option2 = fileChooser.showSaveDialog(this);
                    if(option2 == JFileChooser.APPROVE_OPTION)
                    {
                        File target = fileChooser.getSelectedFile();
                        try
                        {
                            PrintStream stream = new PrintStream(new FileOutputStream(target), true);
                            save(stream);
                            stream.close();
                        }
                        catch(Exception ex)
                        {
                            JOptionPane.showMessageDialog(this, new JLabel("Error: "+ex.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else throw new SaveAbortedException();;
                }
                else //save LOADED FILE
                {
                    try
                    {
                        PrintStream stream = new PrintStream(new FileOutputStream(loadedFile), true);
                        save(stream);
                        stream.close();
                    }
                    catch(Exception ex)
                    {
                        JOptionPane.showMessageDialog(this, new JLabel("Error: "+ex.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            else if(option == JOptionPane.CANCEL_OPTION) throw new SaveAbortedException();;
        }
    }
    public void save(PrintStream stream)
    {
        saved = true;
        stream.println("<?xml version=\"1.0\"?>");
        stream.println("<scale>");
        stream.println("  <name>"+nameTF.getText()+"</name>");
        for(Enumeration en = sp.notes.elements(); en.hasMoreElements();)
        {
            ScalePanel.Notik cur = (ScalePanel.Notik)en.nextElement();
            stream.println("  <note>"+cur.n+"</note>");
        }
        stream.println("</scale>");
    }
    public void setScoreMainFrame(ScoreMainFrame smf)
    {
        sp.channels = smf.scoreP.channels;
        sp.piano = smf.scoreP.instruments[0];
        this.scoreP = smf.scoreP;
    }
}
