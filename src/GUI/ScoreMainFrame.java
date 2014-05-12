package GUI;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;
import java.io.*;

import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.sound.midi.*;
import DataStructure.*;
import Features.*;

public class ScoreMainFrame extends JFrame implements ActionListener
{
    private class ScoreFilter extends javax.swing.filechooser.FileFilter
    {
        public ScoreFilter(){}
        public boolean accept(File pathname)
        {
            if(pathname.isDirectory()) return true;
            else
            {
                if(pathname.getName().indexOf(".score") != -1) return true;
                else                                           return false;
            }
        }
        public String getDescription()
        { return "Score Files (*.score)"; }
    }
    public ScorePanel scoreP;
    public ScorePlayer p;
    public WindowsMenu wmenu;
    public FeaturesMenu featuresMenu;
    public AboutDialog abouts;
    JFileChooser fileChooser;
    javax.swing.filechooser.FileFilter filter;
    private boolean dontClose = false;
    public ScoreMainFrame()
    {
        super("MIDI Score Creator");
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                exitProgram();
            }
        });
        addComponentListener(new ComponentAdapter()
        {
            public void componentHidden(ComponentEvent e)
            {
                if(dontClose)
                {
                    setVisible(true);
                    dontClose = false;
                }
            }
        });
        
        scoreP = new ScorePanel(this);
        JPanel bottom  = new JPanel();
        bottom.setLayout(new BorderLayout());
        p = new ScorePlayer(scoreP);
        scoreP.pop.setPlayer(p);
        CurNoteSetter c = new CurNoteSetter(scoreP);
        
        ModifierCarrier mdc = new ModifierCarrier(scoreP);
        
        JPanel bottomBot = new JPanel(new GridLayout(1, 2));
        bottomBot.add(p);
        bottomBot.add(scoreP.noteDispl);
        
        bottom.add(c, BorderLayout.CENTER);
        bottom.add(bottomBot, BorderLayout.SOUTH);
        getContentPane().add(scoreP, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);
        getContentPane().add(mdc, BorderLayout.WEST);
		setJMenuBar(createMenuBar(scoreP.pop));
        
        pack();
		setSize(900, 700);
        //setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
        
        fileChooser = new JFileChooser();
        filter = new ScoreFilter();
        abouts = new AboutDialog(this);
        
    }
    public JMenuBar createMenuBar(ScorePopup pop)
    {
        JMenuBar menuBar = new JMenuBar();
        JMenuItem newie = new JMenuItem("New");
        newie.addActionListener(this);
        newie.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        JMenuItem openie = new JMenuItem("Open...");
        openie.addActionListener(this);
        openie.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        JMenuItem closie = new JMenuItem("Close");
        closie.addActionListener(this);
        JMenuItem closiall = new JMenuItem("Close All");
        closiall.addActionListener(this);
        JMenuItem savie = new JMenuItem("Save");
        savie.addActionListener(this);
        savie.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        JMenuItem saveAs = new JMenuItem("Save As...");
        saveAs.addActionListener(this);
        JMenuItem export = new JMenuItem("Export MIDI");
        export.addActionListener(this);
        JMenuItem exiter = new JMenuItem("Exit");
        exiter.addActionListener(this);
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(newie);
        fileMenu.add(openie);
        fileMenu.add(closie);
        fileMenu.add(closiall);
        fileMenu.addSeparator();
        fileMenu.add(savie);
        fileMenu.add(saveAs);
        fileMenu.addSeparator();
        fileMenu.add(export);
        fileMenu.addSeparator();
        fileMenu.add(exiter);
        
        wmenu = new WindowsMenu(scoreP, this);
        scoreP.setMenu(wmenu);
        
        JMenu editMenu = new JMenu("Edit");
        //add undo-redo
        UndoItem undoItem = new UndoItem(wmenu, scoreP);
        RedoItem redoItem = new RedoItem(wmenu, scoreP);
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        EditListener edl = new EditListener(scoreP, pop);
        editMenu.add(edl.createCut());
        editMenu.add(edl.createCopy());
        editMenu.add(edl.createPaste());
        editMenu.add(edl.createPasteRef());
        
        featuresMenu = new FeaturesMenu(this);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem help1 = new JMenuItem("View Manual");
        help1.addActionListener(this);
        helpMenu.add(help1);
        JMenuItem help2 = new JMenuItem("About MIDI Score...");
        help2.addActionListener(this);
        helpMenu.add(help2);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(wmenu);
        menuBar.add(featuresMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }
    public void actionPerformed(ActionEvent e)
    {
        JMenuItem source = (JMenuItem)e.getSource();
        if(source.getText() == "New")
        {
            wmenu.newWindow();
        }
        else if(source.getText() == "Open...")
        {
            fileChooser.setFileFilter(filter);
            int option = fileChooser.showOpenDialog(this);
            if(option == JFileChooser.APPROVE_OPTION)
            {
                File loadedFile = fileChooser.getSelectedFile();
                if(loadedFile.getName().indexOf(".score") == -1)
                {
                    JOptionPane.showMessageDialog(this, new JLabel("Error: this is not a *.score file"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                SAXParserFactory factory = SAXParserFactory.newInstance();
                ScoreParser handler = new ScoreParser();
                try
                {
                    SAXParser parser = factory.newSAXParser();
                    parser.parse(loadedFile, handler);
                }
                catch(Exception ex)
                {
                    System.out.println("no!!!!!! exception: "+e);
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, new JLabel("Error: "+ex.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
                }
                //-----now :P:P---------------
                Score loadedScore = handler.getScore();
                wmenu.addWindow(loadedScore, loadedFile.getName(), loadedFile);
            }
        }
        else if(source.getText() == "Close")
        {
            wmenu.closeCurrent();
        }
        else if(source.getText() == "Close All")
        {
            wmenu.closeAll();
        }
        else if(source.getText() == "Save")
        {
            File filee = saveCurScore(wmenu.getCurScoreObject().file);
            wmenu.setCurSaveFlag(true);
        }
        else if(source.getText() == "Save As...")
        {
            fileChooser.setFileFilter(filter);
            int option = getSaveFileOption();
            if(option == JFileChooser.APPROVE_OPTION)
            {
                File target = fileChooser.getSelectedFile();
                if(target.getName().indexOf(".score") == -1) target = new File(target.getPath()+".score");
                try
                {
                    PrintStream stream = new PrintStream(new FileOutputStream(target), false);
                    scoreP.save(stream);
                    stream.close();
                }
                catch(Exception ex)
                {
                    JOptionPane.showMessageDialog(this, new JLabel("Error: "+ex.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
                }
                wmenu.saveUpdate(target);
                wmenu.setCurSaveFlag(true);
            }
        }
        else if(source.getText() == "Export MIDI")
        {
            p.saveMIDI();
        }
        else if(source.getText() == "View Manual")
        {
            //call the default browser with the page "./help/index.html"
            try
            {
                String url = "file:///"+System.getProperty("user.dir")+"/help/index.html";
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            }
            catch (IOException e1)
            {
                JOptionPane.showMessageDialog(this, new JLabel("<html>Error: Cannot launch default browser<br>You will need to access the manual... manually :)</html>"), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(source.getText() == "About MIDI Score...")
        {
            abouts.popUp();
        }
        else if(source.getText() == "Exit")
        {
            exitProgram();
        }
    }
    /** This checks for fileExists/not
      * @return an JFileChooser option*/
    public int getSaveFileOption()
    {
        int option = fileChooser.showSaveDialog(this);
        while(option == JFileChooser.APPROVE_OPTION)
        {
            File target = fileChooser.getSelectedFile();
            if(!target.exists()) return option;
            int subOpt = JOptionPane.showConfirmDialog(fileChooser, new JLabel(target.getName()+" exists. Overwrite?"), "File Exists", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if(subOpt == JOptionPane.CANCEL_OPTION)   return JFileChooser.CANCEL_OPTION;
            else if(subOpt == JOptionPane.YES_OPTION) return JFileChooser.APPROVE_OPTION;
            option = fileChooser.showSaveDialog(this);
        }
        return option;
    }
    /**Saves the current score in the ScorePanel
      * taking care of all the options
      * @return the file it saved to; null if SAVE was aborted
      */
    public File saveCurScore(File file)
    {
        if(file != null)
        {
            try
            {
                PrintStream stream = new PrintStream(new FileOutputStream(file), false);
                scoreP.save(stream);
                stream.close();
            }
            catch(Exception ex)
            {
                JOptionPane.showMessageDialog(this, new JLabel("Error: "+ex.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return file;
        }
        else
        {
            fileChooser.setFileFilter(filter);
            int option = getSaveFileOption();
            if(option == JFileChooser.APPROVE_OPTION)
            {
                File target = fileChooser.getSelectedFile();
                if(target.getName().indexOf(".score") == -1) target = new File(target.getPath()+".score");
                try
                {
                    PrintStream stream = new PrintStream(new FileOutputStream(target), false);
                    scoreP.save(stream);
                    stream.close();
                }
                catch(Exception ex)
                {
                    JOptionPane.showMessageDialog(this, new JLabel("Error: "+ex.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
                }
                wmenu.saveUpdate(target);
                return target;
            }
            
            return null;
        }
    }
    /**Also checks for unsaved files!!!!!*/
    public void exitProgram()
    {
        if(wmenu.closeAll()) System.exit(0);
        else                 dontClose = true;
    }
    public static void main(String[] args)
    { new ScoreMainFrame(); }
}
