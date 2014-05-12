package Features;

import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.io.*;

public class PluginLoader
{
    private static class ClassFilter implements FileFilter
    {// filters only those files with a .class extension
        public ClassFilter(){}
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
    /**
     * Returns an array of objects which contains all the extensions of the superClassName
     * class found in the specified folder.
     * @param folderName - A folder to search for extensions in
     * @param superClassName - A full (packagewise) name of the super class
     */
    public static Object[] loadPlugins(String folderName, String superClassName, boolean doPrint)
    {
        Vector v = new Vector();
        File file = new File(System.getProperty("user.dir")+"/"+folderName);
        File[] files = file.listFiles(new ClassFilter());
        String packagePath = folderName.replaceAll("/", ".");
        for(int i = 0; files != null && i < files.length; i++)
        {
            String className = files[i].getName().split(".class")[0];
            try
            {
                Class curClass = Class.forName(packagePath+"."+className);
                Class superClass = curClass.getSuperclass();
                boolean isIt = false; // if curClass is a child of TerrShape
                while(superClass != null)
                {
                    if(superClassName.equals(superClass.getName()))
                    {
                        isIt = true;
                        break;
                    }
                    superClass = superClass.getSuperclass();
                }
                if(isIt && !Modifier.isAbstract(curClass.getModifiers()))
                {
                    //curClass should now be the nonabstract instanciancible class inhereting superClassName
                    //now just test for image existense
                    File imageFile = new File(System.getProperty("user.dir")+"/"+folderName+"/img/", className+".gif");
                    if(!imageFile.exists())
                    {//all good: add Button and add varMods to Hashtable of tp
                        if(doPrint)
                            System.out.println(imageFile+" does not exist");
                    }
                    Object o = curClass.newInstance();
                    v.addElement(o);
                }
            }
            catch(Exception e)
            {
                if(doPrint)
                    System.out.println(className+" cannot be instanciated: "+e);
            }
        }
        return v.toArray();
    }
    /**
     * Returns an array of classes which contains all the extensions of the superClassName
     * class found in the specified folder.
     * @param folderName - A folder to search for extensions in
     * @param superClassName - A full (packagewise) name of the super class
     */
    public static Class[] loadPluginClasses(String folderName, String superClassName, boolean doPrint)
    {
        Vector v = new Vector();
        File file = new File(System.getProperty("user.dir")+"/"+folderName);
        File[] files = file.listFiles(new ClassFilter());
        String packagePath = folderName.replaceAll("/", ".");
        for(int i = 0; files != null && i < files.length; i++)
        {
            String className = files[i].getName().split(".class")[0];
            try
            {
                Class curClass = Class.forName(packagePath+"."+className);
                Class superClass = curClass.getSuperclass();
                boolean isIt = false; // if curClass is a child of TerrShape
                while(superClass != null)
                {
                    if(superClassName.equals(superClass.getName()))
                    {
                        isIt = true;
                        break;
                    }
                    superClass = superClass.getSuperclass();
                }
                if(isIt && !Modifier.isAbstract(curClass.getModifiers()))
                {
                    //curClass should now be the nonabstract instanciancible class inhereting superClassName
                    //now just test for image existense
                    File imageFile = new File(System.getProperty("user.dir")+"/"+folderName+"/img/", className+".gif");
                    if(!imageFile.exists())
                    {//all good: add Button and add varMods to Hashtable of tp
                        if(doPrint)
                            System.out.println(imageFile+" does not exist");
                    }
                    //Object o = curClass.newInstance();
                    v.addElement(curClass);
                }
            }
            catch(Exception e)
            {
                if(doPrint)
                    System.out.println(className+" cannot be instanciated: "+e);
            }
        }
        Class[] clss = new Class[v.size()];
        int i = 0;
        for(Enumeration en = v.elements(); en.hasMoreElements();)
        {
            Class cur = (Class)en.nextElement();
            clss[i] = cur;
            i++;
        }
        return clss;
    }
    /**
     * Returns an array of classnames which correspond to all the extensions of the superClassName
     * class found in the specified folder.
     * @param folderName - A folder to search for extensions in
     * @param superClassName - A full (packagewise) name of the super class
     */
    public static String[] loadPluginNames(String folderName, String superClassName, boolean doPrint)
    {
        Vector v = new Vector();
        File file = new File(System.getProperty("user.dir")+"/"+folderName);
        File[] files = file.listFiles(new ClassFilter());
        String packagePath = folderName.replaceAll("/", ".");
        for(int i = 0; files != null && i < files.length; i++)
        {
            String className = files[i].getName().split(".class")[0];
            try
            {
                Class curClass = Class.forName(packagePath+"."+className);
                Class superClass = curClass.getSuperclass();
                boolean isIt = false; // if curClass is a child of TerrShape
                while(superClass != null)
                {
                    if(superClassName.equals(superClass.getName()))
                    {
                        isIt = true;
                        break;
                    }
                    superClass = superClass.getSuperclass();
                }
                if(isIt && !Modifier.isAbstract(curClass.getModifiers()))
                {
                    //curClass should now be the nonabstract instanciancible class inhereting superClassName
                    v.addElement(className);
                }
            }
            catch(Exception e)
            {
                if(doPrint)
                    System.out.println(className+" cannot be instanciated: "+e);
            }
        }
        String[] strs = new String[v.size()];
        int i = 0;
        for(Enumeration en = v.elements(); en.hasMoreElements();)
        {
            String cur = (String)en.nextElement();
            strs[i] = cur;
            i++;
        }
        return strs;
    }
}
