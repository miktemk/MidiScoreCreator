package Misc;

import java.awt.*;
import java.lang.*;

public class ExtraProc
{
    public ExtraProc()
    {
        //if(third(1.0/12)) System.out.println("1/12 is a third");
        //else             System.out.println("1/12 is NOT a thirdd");
        System.out.println("# of sticks in 1/16 is "+countTails(1.5/32));
    }
    public boolean isPwrOfTwo(double num)
    {
        double pwr = Math.log(num)/Math.log(2);
        return (pwr == Math.round(pwr));
    }
    public boolean dotted(double length)
    {
        return isPwrOfTwo(length/3);
    }
    public boolean third(double length)
    {
        return isPwrOfTwo(length*3);
    }
    public int countTails(double length)
    {
        return -(int)Math.floor(Math.log(length)/Math.log(2)) - 2;
    }
    public static void main(String[] args)
    {
        ExtraProc e = new ExtraProc();
    }
}
