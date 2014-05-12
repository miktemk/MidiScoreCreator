package GUI;
import java.awt.*;
import javax.swing.*;
import javax.swing.undo.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import javax.sound.midi.*;
import Features.*;


public class FeaturesMenu extends JMenu
{
    private class FeatureObject
    {
        public Class c;
        //public JFrame f = null;
        public FeaturesPanel fp = null;
        public FeatureObject(Class c)
        {
            this.c = c;
        }
    }
    private class FeaturesActionListener implements ActionListener
    {
        FeaturesMenu menu;
        String name;
        public FeaturesActionListener(String name, FeaturesMenu menu)
        {
            this.name = name;
            this.menu = menu;
        }
        public void actionPerformed(ActionEvent arg0)
        {
            menu.openFeature(name, smf);
        }
    }
    
    HashMap<String, FeatureObject> map;
    ScoreMainFrame smf;
    public FeaturesMenu(ScoreMainFrame smf)
    {
        super("Features");
        this.smf = smf;
        map = new HashMap<String, FeatureObject>();
        
        Class[] classes = PluginLoader.loadPluginClasses("Features", "Features.FeaturesPanel", false);
        for(int i = 0; i < classes.length; i++)
        {
            String[] sss = classes[i].getName().split("\\.");
            JMenuItem item = new JMenuItem((sss.length == 0)? classes[i].getName() : sss[sss.length-1]);
            item.addActionListener(new FeaturesActionListener(classes[i].getName(), this));
            add(item);
            map.put(classes[i].getName(), new FeatureObject(classes[i]));
        }
    }
    public void openFeature(String name, Component parent)
    {
        FeatureObject o = map.get(name);
        try
        {
            if(o.fp == null)
            {
                o.fp = (FeaturesPanel)o.c.newInstance();
                o.fp.setScoreMainFrame(smf);
            }
            JOptionPane.showMessageDialog(parent, o.fp, o.c.getName(), JOptionPane.PLAIN_MESSAGE);
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(null, new JLabel("Error: "+ex.getMessage()), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
