package Features;

import java.lang.*;
import java.util.*;
import javax.sound.midi.*;
import DataStructure.*;
import NoteModifiers.*;

public class MidiScoreUtil
{
    public static final int resolution = 1000;
    public static final int maxPBs = 30;
    public static MidiEvent createShortEvent_Safe(int command, int channel, int data1, int data2, long tick)
    {
             if(data1 < 0)   data1 = 0;
        else if(data1 > 127) data1 = 127;
             if(data2 < 0)   data2 = 0;
        else if(data2 > 127) data2 = 127;
        ShortMessage mes = new ShortMessage();
        try{ mes.setMessage(command, channel, data1, data2); }
        catch (Exception e)
        {
            System.out.println("can not set Message: "+e.getMessage());
            return null;
        }
        return (new MidiEvent(mes, tick));
    }
    public static MidiEvent createShortEvent(int command, int channel, int data1, int data2, long tick)
    {
        ShortMessage mes = new ShortMessage();
        try{ mes.setMessage(command, channel, data1, data2); }
        catch (Exception e)
        {
            System.out.println("can not set Message: "+e.getMessage());
            return null;
        }
        return (new MidiEvent(mes, tick));
    }
    public static Sequence encodeSequence(Score score, int startingBar, Sequencer seq, Instrument[] inst)
    {
        Sequence sequence;
        try{sequence = new Sequence(Sequence.PPQ, resolution);}
        catch (Exception e)
        {
            System.out.println("can not init sequence");
            return null;
        }
        float hisTempo = seq.getTempoInBPM();
        Vector heldNotes = new Vector();
        BandSection[] sections = score.getSections();
        for(int i = 0; i < sections.length; i++)
        {
            Track programTrack = sequence.createTrack();
            int partNumber = 0;
            for(Enumeration e = sections[i].getParts().elements(); e.hasMoreElements();)
            {
                Part curPart = (Part)e.nextElement();
                Track curTrack = sequence.createTrack();
                Note prevNote = null;
                long curTick = 0;
                int barNumber = 0;
                int prevInstNumber = -1;
                for(Enumeration e2 = curPart.getBars().elements(); e2.hasMoreElements();)
                {
                  Bar curBar = (Bar)e2.nextElement();
                  if(barNumber >= startingBar)
                  {
                    double inBarPosit = 0;
                    //-----adding Program changes-----------------
                    if(partNumber == 0)
                    {
                        BandSection.InstrumentChangeEvent ev = sections[i].getInstrumentChangeEventAtBar(barNumber);
                        int newInst = ev.instrumentNumber;
                        if(newInst != prevInstNumber)
                        {
                            prevInstNumber = newInst;
                            Patch p = inst[prevInstNumber].getPatch();
                            programTrack.add(createShortEvent(ShortMessage.CONTROL_CHANGE, i, 0, p.getBank(), curTick));
                            programTrack.add(createShortEvent(ShortMessage.PROGRAM_CHANGE, i, p.getProgram(), 0, curTick));
                        }
                    }
                    for(Enumeration e3 = curBar.getNotes().elements(); e3.hasMoreElements();)
                    {
                        //adding actual Notes;
                        Note curNote = (Note)e3.nextElement();
                        if(prevNote != null)
                        {
                            if(prevNote.slured())
                            {
                                for(Enumeration e4 = prevNote.getNotes().elements(); e4.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                    //System.out.println(""+curMiniNote.getValue()+"   "+curNote.getNotes().size());
                                    boolean stopNote = true;
                                    for(Enumeration e5 = curNote.getNotes().elements(); e5.hasMoreElements();)
                                    {
                                        Note.MiniNote curMiniNote2 = (Note.MiniNote)e5.nextElement();
                                        if(curMiniNote.getValue() == curMiniNote2.getValue())
                                        {
                                            stopNote = false;
                                            break;
                                        }
                                    }
                                    if(stopNote) curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  i, curMiniNote.getValue(), 0, curTick));
                                }
                                for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                    boolean startNote = true;
                                    for(Enumeration e5 = prevNote.getNotes().elements(); e5.hasMoreElements();)
                                    {
                                        Note.MiniNote curMiniNote2 = (Note.MiniNote)e5.nextElement();
                                        if(curMiniNote.getValue() == curMiniNote2.getValue())
                                        {
                                            startNote = false;
                                            break;
                                        }
                                    }
                                    if(startNote)   curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  i, curMiniNote.getValue(), curNote.getVolume(), curTick));
                                }
                            }
                            else if(prevNote.held())
                            {
                                for(Enumeration e4 = prevNote.getNotes().elements(); e4.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                    heldNotes.addElement(new Integer(curMiniNote.getValue()));
                                }
                                if(curNote.getVolume() > 0)
                                {
                                    for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                                    {
                                        Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                        curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  i, curMiniNote.getValue(), curNote.getVolume(), curTick));
                                    }
                                }
                                else
                                {//turn all notes off
                                    for(Enumeration e4 = heldNotes.elements(); e4.hasMoreElements();)
                                    {
                                        int curNoteValue = ((Integer)e4.nextElement()).intValue();
                                        curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  i, curNoteValue, 0, curTick));
                                    }
                                    //curTrack.add(createShortEvent(ShortMessage.CONTROL_CHANGE,  i, 123, 0, curTick));
                                }
                            }
                            else if(prevNote.stacatto())
                            {
                                long prevL = (long)((double)hisTempo/score.getTempoAtBar(barNumber)*prevNote.getLength()*resolution*4);
                                for(Enumeration e4 = prevNote.getNotes().elements(); e4.hasMoreElements();)
                                {//end earlier
                                    Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  i, curMiniNote.getValue(), 0, (long)(curTick-prevL*0.9)));
                                }
                                for(Enumeration e4 = heldNotes.elements(); e4.hasMoreElements();)
                                {
                                    int curNoteValue = ((Integer)e4.nextElement()).intValue();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  i, curNoteValue, 0, (long)(curTick-prevL*0.9)));
                                }
                                for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  i, curMiniNote.getValue(), curNote.getVolume(), curTick));
                                }
                            }
                            else
                            {// prev.slurm = NONE
                                //curTrack.add(createShortEvent(ShortMessage.CONTROL_CHANGE,  i, 123, 0, curTick-1));
                                for(Enumeration e4 = heldNotes.elements(); e4.hasMoreElements();)
                                {
                                    int curNoteValue = ((Integer)e4.nextElement()).intValue();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  i, curNoteValue, 0, curTick));
                                }
                                for(Enumeration e4 = prevNote.getNotes().elements(); e4.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  i, curMiniNote.getValue(), 0, curTick));
                                }//*/
                                for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  i, curMiniNote.getValue(), curNote.getVolume(), curTick));
                                }
                            }
                        }
                        else
                        {
                            for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                            {
                                Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  i, curMiniNote.getValue(), curNote.getVolume(), curTick));
                            }
                        }
                        for(Enumeration enMod = curNote.mods.elements(); enMod.hasMoreElements();)
                        {
                            NoteEvents ne = (NoteEvents)enMod.nextElement();
                            ne.generateEvents(curTrack, i, curTick, (long)((double)hisTempo/score.getTempoAtBar(barNumber)*curNote.getLength()*resolution*4), curNote);
                        }
                        
                        
                        curTick += hisTempo/score.getTempoAtBar(barNumber)*curNote.getLength()*resolution*4;
                        prevNote = curNote;
                        inBarPosit += curNote.getLength();
                    }
                    if(!isFull(score, curBar, barNumber))
                    {
                        if(prevNote != null)
                        {
                            if(prevNote.stacatto())
                            {
                                long prevL = (long)((double)hisTempo/score.getTempoAtBar(barNumber)*prevNote.getLength()*resolution*4);
                                for(Enumeration ep = prevNote.getNotes().elements(); ep.hasMoreElements();)
                                {//end earlier
                                    Note.MiniNote curMiniNote = (Note.MiniNote)ep.nextElement();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  i, curMiniNote.getValue(), 0, (long)(curTick-prevL*0.85)));
                                }
                                for(Enumeration e4 = heldNotes.elements(); e4.hasMoreElements();)
                                {
                                    int curNoteValue = ((Integer)e4.nextElement()).intValue();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  i, curNoteValue, 0, (long)(curTick-prevL*0.85)));
                                }
                            }
                            else
                            {
                                for(Enumeration e4 = heldNotes.elements(); e4.hasMoreElements();)
                                {
                                    int curNoteValue = ((Integer)e4.nextElement()).intValue();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  i, curNoteValue, 0, curTick));
                                }
                                for(Enumeration e4 = prevNote.getNotes().elements(); e4.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  i, curMiniNote.getValue(), 0, curTick));
                                }
                            }
                            prevNote = null;
                            //System.out.println("Not FULL!!!!!!!! #of notes = "+curBar.getNotes().size()+"  Bar# = "+(barNumber+1));
                        }
                        curTick += hisTempo/score.getTempoAtBar(barNumber)*(score.getBarMaxLength(barNumber) - inBarPosit)*resolution*4;
                    }
                  }
                  barNumber++;
                }
                partNumber++;
            }
        }
        return sequence;
    }
    public static Sequence encodeSoloSequence(Score score, BandSection section, Part curPart, int startingBar, int channel, Sequencer seq, Instrument[] inst)
    {
        Sequence sequence;
        try{sequence = new Sequence(Sequence.PPQ, resolution);}
        catch (Exception e)
        {
            System.out.println("can not init sequence");
            return null;
        }
        float hisTempo = seq.getTempoInBPM();
        Vector heldNotes = new Vector();
        BandSection[] sections = score.getSections();
        Track programTrack = sequence.createTrack();
        Track curTrack = sequence.createTrack();
        Note prevNote = null;
        long curTick = 0;
        int barNumber = 0;
        int prevInstNumber = -1;
        for(Enumeration e2 = curPart.getBars().elements(); e2.hasMoreElements();)
        {
            Bar curBar = (Bar)e2.nextElement();
            if(barNumber >= startingBar)
            {
                double inBarPosit = 0;
                //-----adding Program changes-----------------
                BandSection.InstrumentChangeEvent ev = section.getInstrumentChangeEventAtBar(barNumber);
                int newInst = ev.instrumentNumber;
                if(newInst != prevInstNumber)
                {
                    prevInstNumber = newInst;
                    Patch p = inst[prevInstNumber].getPatch();
                    programTrack.add(createShortEvent(ShortMessage.CONTROL_CHANGE, channel, 0, p.getBank(), curTick));
                    programTrack.add(createShortEvent(ShortMessage.PROGRAM_CHANGE, channel, p.getProgram(), 0, curTick));
                }
                for(Enumeration e3 = curBar.getNotes().elements(); e3.hasMoreElements();)
                {
                    //adding actual Notes;
                    Note curNote = (Note)e3.nextElement();
                    if(prevNote != null)
                    {
                        if(prevNote.slured())
                        {
                            for(Enumeration e4 = prevNote.getNotes().elements(); e4.hasMoreElements();)
                            {
                                Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                //System.out.println(""+curMiniNote.getValue()+"   "+curNote.getNotes().size());
                                boolean stopNote = true;
                                for(Enumeration e5 = curNote.getNotes().elements(); e5.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote2 = (Note.MiniNote)e5.nextElement();
                                    if(curMiniNote.getValue() == curMiniNote2.getValue())
                                    {
                                        stopNote = false;
                                        break;
                                    }
                                }
                                if(stopNote) curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  channel, curMiniNote.getValue(), 0, curTick));
                            }
                            for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                            {
                                Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                boolean startNote = true;
                                for(Enumeration e5 = prevNote.getNotes().elements(); e5.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote2 = (Note.MiniNote)e5.nextElement();
                                    if(curMiniNote.getValue() == curMiniNote2.getValue())
                                    {
                                        startNote = false;
                                        break;
                                    }
                                }
                                if(startNote)   curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  channel, curMiniNote.getValue(), curNote.getVolume(), curTick));
                            }
                        }
                        else if(prevNote.held())
                        {
                            for(Enumeration e4 = prevNote.getNotes().elements(); e4.hasMoreElements();)
                            {
                                Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                heldNotes.addElement(new Integer(curMiniNote.getValue()));
                            }
                            if(curNote.getVolume() > 0)
                            {
                                for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                                {
                                    Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  channel, curMiniNote.getValue(), curNote.getVolume(), curTick));
                                }
                            }
                            else
                            {//turn all notes off
                                for(Enumeration e4 = heldNotes.elements(); e4.hasMoreElements();)
                                {
                                    int curNoteValue = ((Integer)e4.nextElement()).intValue();
                                    curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  channel, curNoteValue, 0, curTick));
                                }
                                //curTrack.add(createShortEvent(ShortMessage.CONTROL_CHANGE,  i, 123, 0, curTick));
                            }
                        }
                        else if(prevNote.stacatto())
                        {
                            long prevL = (long)((double)hisTempo/score.getTempoAtBar(barNumber)*prevNote.getLength()*resolution*4);
                            for(Enumeration e4 = prevNote.getNotes().elements(); e4.hasMoreElements();)
                            {//end earlier
                                Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  channel, curMiniNote.getValue(), 0, (long)(curTick-prevL*0.9)));
                            }
                            for(Enumeration e4 = heldNotes.elements(); e4.hasMoreElements();)
                            {
                                int curNoteValue = ((Integer)e4.nextElement()).intValue();
                                curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  channel, curNoteValue, 0, (long)(curTick-prevL*0.9)));
                            }
                            for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                            {
                                Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  channel, curMiniNote.getValue(), curNote.getVolume(), curTick));
                            }
                        }
                        else
                        {// prev.slurm = NONE
                            //curTrack.add(createShortEvent(ShortMessage.CONTROL_CHANGE,  i, 123, 0, curTick-1));
                            for(Enumeration e4 = heldNotes.elements(); e4.hasMoreElements();)
                            {
                                int curNoteValue = ((Integer)e4.nextElement()).intValue();
                                curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  channel, curNoteValue, 0, curTick));
                            }
                            for(Enumeration e4 = prevNote.getNotes().elements(); e4.hasMoreElements();)
                            {
                                Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  channel, curMiniNote.getValue(), 0, curTick));
                            }//*/
                            for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                            {
                                Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  channel, curMiniNote.getValue(), curNote.getVolume(), curTick));
                            }
                        }
                    }
                    else
                    {
                        for(Enumeration e4 = curNote.getNotes().elements(); e4.hasMoreElements();)
                        {
                            Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                            curTrack.add(createShortEvent(ShortMessage.NOTE_ON,  channel, curMiniNote.getValue(), curNote.getVolume(), curTick));
                        }
                    }
                    for(Enumeration enMod = curNote.mods.elements(); enMod.hasMoreElements();)
                    {
                        NoteEvents ne = (NoteEvents)enMod.nextElement();
                        ne.generateEvents(curTrack, channel, curTick, (long)((double)hisTempo/score.getTempoAtBar(barNumber)*curNote.getLength()*resolution*4), curNote);
                    }
                    
                    
                    curTick += hisTempo/score.getTempoAtBar(barNumber)*curNote.getLength()*resolution*4;
                    prevNote = curNote;
                    inBarPosit += curNote.getLength();
                }
                if(!isFull(score, curBar, barNumber))
                {
                    if(prevNote != null)
                    {
                        if(prevNote.stacatto())
                        {
                            long prevL = (long)((double)hisTempo/score.getTempoAtBar(barNumber)*prevNote.getLength()*resolution*4);
                            for(Enumeration ep = prevNote.getNotes().elements(); ep.hasMoreElements();)
                            {//end earlier
                                Note.MiniNote curMiniNote = (Note.MiniNote)ep.nextElement();
                                curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  channel, curMiniNote.getValue(), 0, (long)(curTick-prevL*0.85)));
                            }
                            for(Enumeration e4 = heldNotes.elements(); e4.hasMoreElements();)
                            {
                                int curNoteValue = ((Integer)e4.nextElement()).intValue();
                                curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  channel, curNoteValue, 0, (long)(curTick-prevL*0.85)));
                            }
                        }
                        else
                        {
                            for(Enumeration e4 = heldNotes.elements(); e4.hasMoreElements();)
                            {
                                int curNoteValue = ((Integer)e4.nextElement()).intValue();
                                curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  channel, curNoteValue, 0, curTick));
                            }
                            for(Enumeration e4 = prevNote.getNotes().elements(); e4.hasMoreElements();)
                            {
                                Note.MiniNote curMiniNote = (Note.MiniNote)e4.nextElement();
                                curTrack.add(createShortEvent(ShortMessage.NOTE_OFF,  channel, curMiniNote.getValue(), 0, curTick));
                            }
                        }
                        prevNote = null;
                        //System.out.println("Not FULL!!!!!!!! #of notes = "+curBar.getNotes().size()+"  Bar# = "+(barNumber+1));
                    }
                    curTick += hisTempo/score.getTempoAtBar(barNumber)*(score.getBarMaxLength(barNumber) - inBarPosit)*resolution*4;
                }
            }
            barNumber++;
        }
        return sequence;
    }
    public static Sequence encodeScale(int[] scale, Sequencer seq, float tempo)
    {
        Sequence sequence;
        try{sequence = new Sequence(Sequence.PPQ, resolution);}
        catch (Exception e)
        {
            System.out.println("can not init sequence");
            return null;
        }
        Track track = sequence.createTrack();
        track.add(createShortEvent(ShortMessage.CONTROL_CHANGE, 0, 0, 0, 0));
        track.add(createShortEvent(ShortMessage.PROGRAM_CHANGE, 0, 0, 0, 0));
        float hisTempo = seq.getTempoInBPM();
        int curTick = 1;
        for(int i = 0; i <= scale.length; i++)
        {
            if(i > 0)            track.add(createShortEvent(ShortMessage.NOTE_OFF,  0, 60+scale[i-1], 100, curTick));
            if(i < scale.length) track.add(createShortEvent(ShortMessage.NOTE_ON,   0, 60+scale[i],   100, curTick));
            curTick += (hisTempo/tempo)*0.0625*resolution*4;
        }
        return sequence;
    }
    private static boolean isFull(Score score, Bar bar, int barPosit)
    {
        return (bar.getCurLength() > score.getBarMaxLength(barPosit));
    }
}
