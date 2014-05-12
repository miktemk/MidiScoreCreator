package DataStructure;

import java.util.*;
import java.io.*;
import NoteModifiers.*;
import Features.*;
import java.lang.*;
import java.lang.reflect.*;

public class Note
{
    public class MiniNote
    {
        public int value = 0;
        public MiniNote(int value)
        {
            this.value = value;
            if(value > 127)    value = 127;
            else if(value < 0) value = 0;
        }
        public int getValue() { return value; }
        public void setTranspose(int delta)
        {
            value += delta;
            if(value > 127)    value = 127;
            else if(value < 0) value = 0;
        }
    }
    public static final byte NONE = 0,
                             SLURED = 1,
                             HELD = 2,
                             STACATTO = 3;
    public Vector notes, mods;
    public int volume = 90;
    public double length = 0.25;
    public byte slurm = NONE;  //this is not from planet wormulone! this is NONE/SLURED/STACATTO/HELD
    
    public void initialize()
    {
        notes = new Vector();
        mods = new Vector();
    }
    public Note()
    {
        initialize();
    }
    public Note(int value, double length)
    {
        initialize();
        addNote(value);
        this.length = length;
    }
    public Note(int value, double length, int volume)
    {
        initialize();
        addNote(value);
        this.length = length;
        this.volume = volume;
    }
    public Note(int value, double length, int volume, byte slurm)
    {
        initialize();
        addNote(value);
        this.length = length;
        this.volume = volume;
        this.slurm = slurm;
    }
    public Note(double length, int volume, byte slurm)
    {
        initialize();
        this.length = length;
        this.volume = volume;
        this.slurm = slurm;
    }
    public void addNote(int value)
    {
        boolean clear = true;
        MiniNote cur = null;
        for(Enumeration e = notes.elements(); e.hasMoreElements();)
        {
            cur = (MiniNote)e.nextElement();
            if(cur.getValue() == value)
            {
                clear = false;
                break;
            }
        }
        if(clear) notes.addElement(new MiniNote(value));
        else if(cur != null) notes.remove(cur);
    }
    public void removeNote(int value)
    {
        for(Enumeration e = notes.elements(); e.hasMoreElements();)
        {
            MiniNote cur = (MiniNote)e.nextElement();
            if(cur.getValue() == value)
            {
                notes.removeElement(cur);
                break;
            }
        }
    }
    public void addNoteToEdge(int delta, boolean above)
    {
        if(above)
        {// pick MAX
            int max = -1;
            for(Enumeration e = notes.elements(); e.hasMoreElements();)
            {
                MiniNote curMiniNote = (MiniNote)e.nextElement();
                if(max < curMiniNote.getValue()) max = curMiniNote.getValue();
            }
                 if(max+delta < 0)   addNote(12 + (max+delta)%12);
            else if(max+delta > 127) addNote(115 + (max+delta)%12);
            else addNote(max + delta);
        }
        else // pick MIN
        {
            int min = 128;
            for(Enumeration e = notes.elements(); e.hasMoreElements();)
            {
                MiniNote curMiniNote = (MiniNote)e.nextElement();
                if(min > curMiniNote.getValue()) min = curMiniNote.getValue();
            }
                 if(min-delta < 0)   addNote(12 + (min-delta)%12);
            else if(min-delta > 127) addNote(115 + (min-delta)%12);
            else addNote(min - delta);
        }
    }
    public Vector getNotes() { return notes; }
    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }
    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }
    public boolean slured()   { return slurm == SLURED; }
    public boolean held()     { return slurm == HELD; }
    public boolean stacatto() { return slurm == STACATTO; }
    public void setSlurm(byte slurm) { this.slurm = slurm; }
    
    public void addModifier(NoteEvents ne)
    {
      if(ne != null)
      {
        String className = ne.getClass().getName();
        for(Enumeration e = mods.elements(); e.hasMoreElements();)
        {
            NoteEvents cur = (NoteEvents)e.nextElement();
            if(className.equals(cur.getClass().getName()))
            {
                mods.removeElement(cur);
                //System.out.println("Note, line 116: removed something called "+className);
                break;
            }
        }
        mods.addElement(ne);
      }
    }
    public Note makeCopy()
    {
        Note newNote = new Note(length, volume, slurm);
        for(Enumeration e = notes.elements(); e.hasMoreElements();)
        {
            MiniNote curMiniNote = (MiniNote)e.nextElement();
            newNote.addNote(curMiniNote.getValue());
        }
        for(Enumeration e = mods.elements(); e.hasMoreElements();)
        {
            NoteEvents cur = (NoteEvents)e.nextElement();
            newNote.addModifier(cur);
        }
        return newNote;
    }
    public Note makeCopy_RefNotes()
    {
        Note newNote = new Note(length, volume, slurm);
        newNote.notes = notes;
        for(Enumeration e = mods.elements(); e.hasMoreElements();)
        {
            NoteEvents cur = (NoteEvents)e.nextElement();
            newNote.addModifier(cur);
        }
        return newNote;
    }
    public int getExtremeTransposition(int delta)
    {
        for(Enumeration e = notes.elements(); e.hasMoreElements();)
        {
            MiniNote curMiniNote = (MiniNote)e.nextElement();
            int newVal = curMiniNote.getValue()+delta;
                 if(newVal < 0)   delta = -curMiniNote.getValue();
            else if(newVal > 127) delta = 127-curMiniNote.getValue();
        }
        return delta;
    }
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Note)) return false;
        Note note = (Note)obj;
        if(note.volume != volume) return false;
        if(note.length != length) return false;
        if(note.slurm  != slurm)  return false;
        if(note.notes.size() != notes.size()) return false;
        for(Enumeration en = note.notes.elements(); en.hasMoreElements();)
        {
            MiniNote curMiniNote = (MiniNote)en.nextElement();
            boolean in = false;
            for(Enumeration en2 = notes.elements(); en2.hasMoreElements();)
            {
                MiniNote curMiniNote2 = (MiniNote)en2.nextElement();
                if(curMiniNote2.value == curMiniNote.value)
                {
                    in = true;
                    break;
                }
            }
            if(!in) return false;
        }
        if(note.mods.size() != mods.size()) return false;
        for(Enumeration en = note.mods.elements(); en.hasMoreElements();)
        {
            NoteEvents cur = (NoteEvents)en.nextElement();
            boolean in = false;
            for(Enumeration en2 = mods.elements(); en2.hasMoreElements();)
            {
                NoteEvents cur2 = (NoteEvents)en2.nextElement();
                if(modsEqual(cur, cur2))
                {
                    in = true;
                    break;
                }
            }
            if(!in) return false;
        }
        return true;
    }
    /** determines if 2 NoteEvent objects are equal */
    private boolean modsEqual(NoteEvents ev1, NoteEvents ev2)
    {
        if(ev1 == null && ev2 == null) return true;
        Class c1 = ev1.getClass();    if(!c1.isInstance(ev2)) return false;
        Class c2 = ev2.getClass();
        Field[] f1 = c1.getFields();
        for(int i = 0; i < f1.length; i++)
        {
            try
            {
                if(!f1[i].get(ev1).equals(f1[i].get(ev2))) return false;
            }
            catch(Exception ex) { return false; }
        }
        return true;
    }
    
    public void save(PrintStream stream, int indent)
    {
        stream.println(MiscHelper.printBar(' ', indent)+"<note length=\""+length+"\" volume=\""+volume+"\"  slurm=\""+slurm+"\">");
        stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT)+"<chord>");
        for(Enumeration e = notes.elements(); e.hasMoreElements();)
        {
            MiniNote curMiniNote = (MiniNote)e.nextElement();
            stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT*2)+"<chordNote>"+curMiniNote.getValue()+"</chordNote>");
        }
        stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT)+"</chord>");
        stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT)+"<modifiers>");
        for(Enumeration e = mods.elements(); e.hasMoreElements();)
        {
            NoteEvents cur = (NoteEvents)e.nextElement();
            Class curClass = cur.getClass();
            stream.println(MiscHelper.printBar(' ', indent+2*Score.SAVE_INDENT)+"<"+curClass.getName()+">");
            cur.saveConstructorData(stream, indent+3*Score.SAVE_INDENT);
            stream.println(MiscHelper.printBar(' ', indent+2*Score.SAVE_INDENT)+"</"+curClass.getName()+">");
        }
        stream.println(MiscHelper.printBar(' ', indent+Score.SAVE_INDENT)+"</modifiers>");
        stream.println(MiscHelper.printBar(' ', indent)+"</note>");
    }
}
