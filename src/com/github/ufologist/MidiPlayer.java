package com.github.ufologist;

import java.io.IOException;
import java.net.URL;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

/**
 * http://www.java2s.com/Code/Java/Development-Class/AnexamplethatplaysaMidisequence.htm
 * 
 * @version 2014-12-11
 */
public class MidiPlayer implements MetaEventListener {
    // Midi meta event
    public static final int END_OF_TRACK_MESSAGE = 47;
    private static Sequencer sequencer;

    private static MidiPlayer instance;
    
    /**
     * Creates a new MidiPlayer object.
     */
    private MidiPlayer() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.addMetaEventListener(this);
        } catch (MidiUnavailableException ex) {
            sequencer = null;
        }
    }

    public static MidiPlayer getInstance() {
        if (instance == null) {
            instance = new MidiPlayer();
        }
        return instance;
    }

    /**
     * Plays a sequence, optionally looping. This method returns immediately.
     * The sequence is not played if it is invalid.
     */
    public void play(Sequence sequence) {
        if (sequencer != null && sequence != null && sequencer.isOpen()) {
            try {
                sequencer.setSequence(sequence);
                sequencer.start();
            } catch (InvalidMidiDataException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void play(final URL url) {
        if (sequencer != null && sequencer.isOpen()) {
            try {
                Sequence sequence = MidiSystem.getSequence(url);
                sequencer.setSequence(sequence);
                sequencer.start();
            } catch (InvalidMidiDataException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is called by the sound system when a meta event occurs. In
     * this case, when the end-of-track meta event is received, close sequencer.
     */
    public void meta(MetaMessage event) {
        if (event.getType() == END_OF_TRACK_MESSAGE) {
            if (sequencer != null && sequencer.isOpen()) {
                sequencer.close();
            }
        }
    }

    /**
     * Stops the sequencer and resets its position to 0.
     */
    public void stop() {
        if (sequencer != null && sequencer.isOpen()) {
            sequencer.stop();
            sequencer.setMicrosecondPosition(0);
        }
    }

    /**
     * Closes the sequencer.
     */
    public void close() {
        if (sequencer != null && sequencer.isOpen()) {
            sequencer.close();
        }
    }

    /**
     * Gets the sequencer.
     */
    public Sequencer getSequencer() {
        return sequencer;
    }
}
