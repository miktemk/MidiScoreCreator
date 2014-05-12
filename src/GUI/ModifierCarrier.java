package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import NoteModifiers.*;

public class ModifierCarrier extends JPanel//JToolBar
{
    /** filters only those files with a .class extension */
    private class MyFilter implements FileFilter
    {
        public MyFilter(){}
        public boolean accept(File pathname)
        {
            if(pathname.isDirectory()) return false;
            else
            {
                if((pathname.getName().indexOf(".class") != -1)&&
                   (pathname.getName().indexOf("$") == -1)) return true;
                else                                        return false;
            }
        }
    }
    /** Temporarily carries a NoteModifier object and a Name of that object */
    private class ModItem
    {
        NoteModifier mod;
        String modName;
        public ModItem(NoteModifier mod, String modName)
        {
            this.mod = mod;
            this.modName = modName;
        }
    }
    /**
     * Compares 2 objects, which have to be NoteModifier objects by means
     * of comparing getHighOrder() and getOrder() priority functions
     * Used for sorting the buttons in the Mod ToolBar
     */
    private class myComparator implements Comparator
    {
        public int compare(Object o1, Object o2)
        {
            ModItem n1 = (ModItem)o1;
            ModItem n2 = (ModItem)o2;
                 if(n1.mod.getHighOrder() <  n2.mod.getHighOrder()) return -1;
            else if(n1.mod.getHighOrder() == n2.mod.getHighOrder())
            {
                     if(n1.mod.getOrder() <  n2.mod.getOrder()) return -1;
                else if(n1.mod.getOrder() == n2.mod.getOrder()) return 0;
                else                                            return 1;
            }
            else return 1;
        }
        public boolean equals(Object obj)
        {
            return false;
        }
    }
    /**
     * Constructs the thing
     * Searches the subfolder "NoteModifiers" 4 all the modifiers that it loads
     */
    public ModifierCarrier(ScorePanel p)
    {
        ButtonGroup bg = new ButtonGroup();
        Vector modsVector = new Vector();
        File file = new File(System.getProperty("user.dir")+"/NoteModifiers");
        File[] files = file.listFiles(new MyFilter());
        for(int i = 0; i < files.length; i++)
        {
            String className = files[i].getName().split(".class")[0];
            try
            {
                Class curClass = Class.forName("NoteModifiers."+className);
                Class superClass = curClass.getSuperclass();
                boolean isIt = false; // if curClass is a child of TerrShape
                while(superClass != null)
                {
                    if("NoteModifiers.NoteModifier".equals(superClass.getName()))
                    {
                        isIt = true;
                        break;
                    }
                    superClass = superClass.getSuperclass();
                }
                if(isIt && !Modifier.isAbstract(curClass.getModifiers()))
                {
                    try
                    {
                        NoteModifier modifier = (NoteModifier)curClass.newInstance();
                        modifier.setScorePanel(p);
                        modsVector.addElement(new ModItem(modifier, className));
                    }
                    catch(Exception ex)
                    {
                        System.out.println(className);
                        System.out.println(ex);
                        ex.printStackTrace();
                    }
                }
            }
            catch(Exception e)
            {
                System.out.println(className+" cannot be instanciated: "+e);
            }
        }
        Object[] modsArr = modsVector.toArray();
        Arrays.sort(modsArr, new myComparator());
        int prevHOrd = -1;
        ButtonRow curRow = null;
        for(int i = 0; i < modsArr.length; i++)
        {
            ModItem mi = (ModItem)modsArr[i];
            NoteModifier curMod = mi.mod;
            if(prevHOrd != curMod.getHighOrder() || curRow == null)
            {
                prevHOrd = curMod.getHighOrder();
                curRow = new ButtonRow(p, curMod, mi.modName);
                curRow.setPreferredSize(new Dimension(40, 40));
                bg.add(curRow);
                add(curRow);
                if(curMod instanceof NormalModifier) curRow.setSelected(true);
            }
            else
            {
                curRow.addModifier(curMod, mi.modName);
            }
        }
        setPreferredSize(new Dimension(40, 40));
    }
}
