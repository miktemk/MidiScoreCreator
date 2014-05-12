/*
 * @(#)MidiSynth.java	1.15	99/12/03
 *
 * Copyright (c) 1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */
package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.sound.midi.*;
import java.util.Vector;

import java.io.File;
import java.io.IOException;


/**
 * Table for 128 general MIDI melody instuments.
 */
public class InstrumentsTable extends JPanel
{
    private String names[] = { 
       "Piano", "Chromatic Perc.", "Organ", "Guitar", 
       "Bass", "Strings", "Ensemble", "Brass", 
       "Reed", "Pipe", "Synth Lead", "Synth Pad",
       "Synth Effects", "Ethnic", "Percussive", "Sound Effects" };
    private int nRows = 8;
    private int nCols = names.length; // just show 128 instruments
    int curRow = 0,
        curCol = 0;
    int program = 0;
    Instrument[] instruments;
    InstrumentDialog id;
    JTable table;
    
    public InstrumentsTable(Instrument[] instruments, InstrumentDialog id)
    {
        this.instruments = instruments;
        this.id = id;
        
        setLayout(new BorderLayout());
        
        TableModel dataModel = new AbstractTableModel()
        {
            public int getColumnCount() { return nCols; }
            public int getRowCount() { return nRows;}
            public Object getValueAt(int r, int c)
            {
                return getInstName(c*nRows+r);
            }
            public String getColumnName(int c) { return names[c]; }
            public Class getColumnClass(int c) { return getValueAt(0, c).getClass(); }
            public boolean isCellEditable(int r, int c) {return false;}
            public void setValueAt(Object obj, int r, int c) {}
        };
        
        table = new JTable(dataModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Listener for row changes
        ListSelectionModel lsm = table.getSelectionModel();
        lsm.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                ListSelectionModel sm = (ListSelectionModel) e.getSource();
                if (!sm.isSelectionEmpty())
                {
                    curRow = sm.getMinSelectionIndex();
                }
                setProgram(curCol*nRows+curRow);
            }
        });
        
        // Listener for column changes
        lsm = table.getColumnModel().getSelectionModel();
        lsm.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                ListSelectionModel sm = (ListSelectionModel) e.getSource();
                if (!sm.isSelectionEmpty()) { curCol = sm.getMinSelectionIndex(); }
                setProgram(curCol*nRows+curRow);
            }
        });
        //table.clearSelection();
        table.setPreferredScrollableViewportSize(new Dimension(nCols*110, 200));
        table.setCellSelectionEnabled(true);
        table.setColumnSelectionAllowed(true);
        for (int i = 0; i < names.length; i++)
        {
            TableColumn column = table.getColumn(names[i]);
            column.setPreferredWidth(110);
        }
        table.setAutoResizeMode(table.AUTO_RESIZE_OFF);
        
        JScrollPane sp = new JScrollPane(table);
        sp.setVerticalScrollBarPolicy(sp.VERTICAL_SCROLLBAR_NEVER);
        sp.setHorizontalScrollBarPolicy(sp.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(sp);
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(800,170);
    }
    public Dimension getMaximumSize()
    {
        return new Dimension(800,170);
    }
    
    public void setProgram(int program)
    {
        this.program = program;
        if(program < 128)
        {
            id.otherLabel.setEnabled(false);
            id.curInstrument = program;
        }
        else
        {
            table.clearSelection();
            //null selection
        }
    }
    //subject to potential index out of bounds exception
    private String getInstName(int index)
    {
        if(instruments != null) return instruments[index].getName();
        else                    return Integer.toString(index);
    }
}
