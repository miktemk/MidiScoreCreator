package DataStructure;

import java.util.*;
import java.io.*;
import Features.*;

public class Part
{
    Vector bars = new Vector();
    public Part(){}
    public Part(int numBars)
    {
        for(int i = 0; i < numBars; i++)
        {
            bars.addElement(new Bar());
        }
    }
    /*public Part(int numBars, int topNumber, int bottomNumber)
    {
        for(int i = 0; i < numBars; i++)
        {
            bars.addElement(new Bar(topNumber, bottomNumber));
        }
    }*/
    public void addBar(Bar bar)
    {
        bars.addElement(bar);
    }
    public void addBarToEnd()
    {
        try
        {
            Bar last = (Bar)bars.lastElement();
            bars.addElement(new Bar());
        }
        catch(Exception e) { bars.addElement(new Bar()); }
    }
    /** inserts a bar before a specified index
      * which has 2B > 0
      * //uhhh.. forget this: Bar Numbers are uglyly shifted over the newly formed line of bars
     */
    public void insertBar(int index, Bar bar)
    {
        //System.out.println("inserting a bar at index "+index);
        try
        {
            bars.add(index, bar);
        }
        catch(Exception e) {}
    }
    /** removes the bar at a specified index
     * NOTE: doesN'T add a blank bar to the end!!!
     */
    public void removeBar(int index)
    {//index is 0 BASED!!!!!!!!!
        bars.removeElementAt(index);
    }
    /*public void removeBar_ShiftNumbers(int index)
    {//well.......... ok!!!!!!
        int i = 0; //in the loop, i = curBar's index
        Bar first = (Bar)bars.firstElement();
        int prevTop = first.topNumber;
        int prevBot = first.bottomNumber;
        for(Enumeration en = bars.elements(); en.hasMoreElements();)
        {
            Bar curBar = (Bar)en.nextElement();
            if(i > index)
            {
                int topTmp = curBar.topNumber;
                int botTmp = curBar.bottomNumber;
                curBar.setBarNumbers(prevTop, prevBot);
                prevTop = topTmp;
                prevBot = botTmp;
            }
            else
            {
                prevTop = curBar.topNumber;
                prevBot = curBar.bottomNumber;
            }
            i++;
        }
        bars.removeElementAt(index);
    }*/
    public int getTotalBars() { return bars.size(); }
    public Vector getBars() { return bars; }
    public Bar getBar(int index)
    {
        try { return (Bar)bars.elementAt(index); }
        catch(Exception e) { return null; }
    }
    
    public void save(PrintStream stream, int indent, Vector refBars)
    {
        stream.println(MiscHelper.printBar(' ', indent)+"<part>");
        stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT)+"<bars>");
        for(Enumeration en = bars.elements(); en.hasMoreElements();)
        {
            Bar curBar = (Bar)en.nextElement();
            if(curBar.byRef)
            {
                stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT*2)+"<refBar>"+refBars.indexOf(curBar)+"</refBar>");
            }
            else curBar.save(stream, indent+Score.SAVE_INDENT*2, -1);
        }
        stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT)+"</bars>");
        stream.println(MiscHelper.printBar(' ', indent)+"</part>");
    }
}
