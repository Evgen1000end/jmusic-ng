/*

< This Java Class is part of the jMusic API>

Copyright (C) 2000 Andrew Sorensen & Andrew Brown

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or any
later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/
package jm.midi;

import java.util.Enumeration;
import java.util.Stack;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

import jm.JMC;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

/**
 * MidiSynth.java
 * <p/>
 * Created: Mon May 07 11:21:30 2001
 *
 * @author Mark Elston (enhanced by Andrew Brown)
 */

public class MidiSynth implements JMC, MetaEventListener {

  private static final int STOP_TYPE = 47; // End of track
  /**
   * Pulses per quarter note value
   */
  private short m_ppqn;
  /**
   * The Synthesizer we are using
   */
  private Synthesizer m_synth;
  /**
   * The Sequence we are using
   */
  private Sequence m_seq;
  /**
   * The Sequencer we are using
   */
  private Sequencer m_sequencer;
  /**
   * The current tempo value
   */
  private float m_currentTempo;
  /**
   * The overall (Score) tempo value
   */
  private float m_masterTempo;
  /**
   * All previous tempos
   */
  private Stack m_tempoHistory;
  /**
   * The diff. beteen the score and part tempi
   */
  private double trackTempoRatio = 1.0;
  /**
   * The diff. between the score and phrase tempi
   */
  private double elementTempoRatio = 1.0;
  /**
   * The name of the jMusic score
   */
  private String scoreTitle;
  /**
   * Sequence is playiong flag
   */
  private boolean isPlaying = false;
  private Boolean msCycle = false;
  private Boolean update = false;
  private Score updateScore;


  public MidiSynth() {
    this((short) 480);
  }

  public MidiSynth(short ppqn) {
    m_ppqn = ppqn;
    m_synth = null;
    m_tempoHistory = new Stack();
  }

  /**
   * Create a Note On Event
   *
   * @param int  channel is the channel to change
   * @param int  pitch is the pitch of the note
   * @param int  velocity is the velocity of the note
   * @param long tick is the time this event occurs
   */
  protected static MidiEvent createNoteOnEvent(int channel,
                                               int pitch, int velocity,
                                               long tick)
    throws InvalidMidiDataException {

    ShortMessage msg = new ShortMessage();
    msg.setMessage(0x90 + channel, pitch, velocity);
    MidiEvent evt = new MidiEvent(msg, tick);

    return evt;
  }

  /**
   * Create a Note Off Event
   *
   * @param int  channel is the channel to change
   * @param int  pitch is the pitch of the note
   * @param int  velocity is the velocity of the note
   * @param long tick is the time this event occurs
   */
  protected static MidiEvent createNoteOffEvent(int channel,
                                                int pitch, int velocity,
                                                long tick)
    throws InvalidMidiDataException {

    ShortMessage msg = new ShortMessage();
    msg.setMessage(0x80 + channel, pitch, velocity);
    MidiEvent evt = new MidiEvent(msg, tick);

    return evt;
  }

  /**
   * Create a Program Change Event
   *
   * @param int  channel is the channel to change
   * @param int  value is the new value to use
   * @param long tick is the time this event occurs
   */
  protected static MidiEvent createProgramChangeEvent(int channel,
                                                      int value,
                                                      long tick)
    throws InvalidMidiDataException {

    ShortMessage msg = new ShortMessage();
    msg.setMessage(0xC0 + channel, value, 0);
    MidiEvent evt = new MidiEvent(msg, tick);

    return evt;
  }

  /**
   * Create a Control Change event
   *
   * @param int channel is the channel to use
   * @param int controlNum is the control change number to use
   * @param int value is the value of the control change
   */
  protected static MidiEvent createCChangeEvent(int channel,
                                                int controlNum,
                                                int value,
                                                long tick)
    throws InvalidMidiDataException {

    ShortMessage msg = new ShortMessage();
    msg.setMessage(0xB0 + channel, controlNum, value);
    MidiEvent evt = new MidiEvent(msg, tick);
    return evt;
  }

  public boolean isPlaying() {
    return isPlaying;
  }

  /**
   * Plays the jMusic score data via the JavaSound MIDI synthesizer
   *
   * @param score - data to change to SMF
   */
  public void play(Score score) throws InvalidMidiDataException {
    if (null == m_sequencer) {
      if (!initSynthesizer()) {
        return;
      }
    }
    scoreTitle = score.getTitle();
    m_masterTempo = (float) score.getTempo();
    if (null == m_seq) {
      m_seq = scoreToSeq(score);
    } else {
      // empty seq and refill
      Track[] tracks = m_seq.getTracks();
      int t_len = tracks.length;
      for (int i = 0; i < t_len; i++) {
        m_seq.deleteTrack(tracks[i]);
      }
      m_seq = scoreToSeq(score);
    }
    if (null != m_seq) {
      try {
        m_sequencer.open();
      } catch (MidiUnavailableException e) {
        System.err.println("MIDI System Unavailable:" + e);
        return;
      }
      m_sequencer.setSequence(m_seq);
      m_sequencer.addMetaEventListener(this);
      m_sequencer.setMicrosecondPosition(0l);
      m_sequencer.setTempoInBPM(m_masterTempo);

      //printSeqInfo(seq);
      m_sequencer.start();
      isPlaying = true;
    }
  }

  public void updateSeq(Score score) throws InvalidMidiDataException {
    update = true;
    updateScore = score;
  }

  public void setCycle(Boolean val) {
    msCycle = val;
  }

  /**
   * Plays back the already computed sequencer via a MIDI synthesizer
   */
  private void rePlay() {
    if (null == m_sequencer) {
      if (!initSynthesizer()) {
        return;
      }
    }
    if (update) {
      System.out.println("Updating playback sequence");
      //Sequence seq;
      try {
        m_seq = scoreToSeq(updateScore);
        if (null != m_seq) {
          try {
            m_sequencer.open();
          } catch (MidiUnavailableException e) {
            System.err.println("MIDI System Unavailable:" + e);
            return;
          }
          m_sequencer.setSequence(m_seq);
        }
      } catch (InvalidMidiDataException e) {
        System.err.println("MIDISynth updating sequence error:" + e);
        return;
      }
      update = false;
    }
    m_sequencer.setMicrosecondPosition(0l);
    m_sequencer.setTempoInBPM(m_masterTempo);
    m_sequencer.start();
  }

  protected void printSeqInfo(Sequence seq) {
    //System.out.println("Score Title: " + scoreTitle);
    //System.out.println("Score TempoEvent: " + m_currentTempo + " BPM");
    //System.out.print("Sequence Division Type = ");
    float type = seq.getDivisionType();
        /*
        if (Sequence.PPQ == type)
            System.out.println("PPQ");
        else if (Sequence.SMPTE_24 == type)
            System.out.println("SMPTE 24 (24 fps)");
        else if (Sequence.SMPTE_25 == type)
            System.out.println("SMPTE 25 (25 fps)");
        else if (Sequence.SMPTE_30 == type)
            System.out.println("SMPTE 30 (30 fps)");
        else if (Sequence.SMPTE_30DROP == type)
            System.out.println("SMPTE 30 Drop (29.97 fps)");
        else
            System.out.println("Unknown");

        System.out.println("Sequence Resolution = " +
                           seq.getResolution());
        System.out.println("Sequence TickLength = " +
                           seq.getTickLength());
        System.out.println("Sequence Microsecond Length = " +
                           seq.getMicrosecondLength());
        System.out.println("Sequencer TempoEvent (BPM) = " +
                           m_sequencer.getTempoInBPM());
        System.out.println("Sequencer TempoEvent (MPQ) = " +
                           m_sequencer.getTempoInMPQ());
        System.out.println("Sequencer TempoFactor = " +
                           m_sequencer.getTempoFactor());
        */
  }

  /**
   * Invoked when a Sequencer has encountered and processed a MetaMessage
   * in the Sequence it is processing.
   *
   * @param MetaMessage meta - the meta-message that the sequencer encountered
   */
  public void meta(MetaMessage metaEvent) {
    //System.out.println("JavaSound sequencer sent meta event");
    if (metaEvent.getType() == STOP_TYPE) {
      if (msCycle) {
        rePlay();
      } else {
        stop();
      }
    }
  }

  /**
   * Close JavaSound sequencer and synthesizer objects
   */
  public void stop() {
    msCycle = false;
    isPlaying = false;
    if (m_sequencer != null & m_sequencer.isOpen()) {
      m_sequencer.stop();
    }
    System.out.println("jMusic MidiSynth: Stopped JavaSound MIDI playback");
  }

  /**
   * Converts jmusic score data into a MIDI Sequence
   *
   * @param Score score - data to play
   * @return Sequence to be played
   */
  protected Sequence scoreToSeq(Score score)
    throws InvalidMidiDataException {
    System.out.println("PPQN = " + m_ppqn);
    Sequence sequence = new Sequence(Sequence.PPQ, m_ppqn);

    m_masterTempo = m_currentTempo =
      new Float(score.getTempo()).floatValue();

    System.out.println("Начальный темп: master " + m_masterTempo + " текущий " + m_currentTempo);

    Track longestTrack = null;
    double longestTime = 0.0;
    double longestRatio = 1.0;

    Enumeration parts = score.getPartList().elements();
    System.out.println("Начинаю обработку частей: ");
    while (parts.hasMoreElements()) {
      Part inst = (Part) parts.nextElement();

      int currChannel = inst.getChannel();
      if (currChannel > 16) {
        throw new
          InvalidMidiDataException(inst.getTitle() +
          " - Invalid Channel Number: " +
          currChannel);
      }

      m_tempoHistory.push(m_currentTempo);

      float tempo = new Float(inst.getTempo()).floatValue();

      System.out.println("Part " + inst.getChannel() + " tempo " + tempo);

      if (tempo != Part.DEFAULT_TEMPO) {
        m_currentTempo = tempo;
      } else if (tempo < Part.DEFAULT_TEMPO) {
        System.out.println("jMusic MidiSynth error: Part TempoEvent (BPM) too low = " + tempo);
      }

      trackTempoRatio = m_masterTempo / m_currentTempo;

      System.out.println("TrackTempoRatio " + trackTempoRatio);

      int instrument = inst.getInstrument();
      if (instrument == NO_INSTRUMENT) {
        instrument = 0;
      }

      Enumeration phrases = inst.getPhraseList().elements();
      double max = 0;
      double currentTime = 0.0;

      Track currTrack = sequence.createTrack();
      System.out.println("Начинаю обработку фраз ... ");
      while (phrases.hasMoreElements()) {
        /////////////////////////////////////////////////
        // Each phrase represents a new Track element
        // Err no
        // There is a 65? track limit
        // ////////////////////////////
        Phrase phrase = (Phrase) phrases.nextElement();

        //Track currTrack = sequence.createTrack();

        currentTime = phrase.getStartTime();
        long phraseTick = (long) (currentTime * m_ppqn * trackTempoRatio);
        MidiEvent evt;

        if (phrase.getInstrument() != NO_INSTRUMENT) {
          instrument = phrase.getInstrument();
        }
        evt = createProgramChangeEvent(currChannel, instrument, phraseTick);
        currTrack.add(evt);

        m_tempoHistory.push(m_currentTempo);

        tempo = new Float(phrase.getTempo()).floatValue();
        if (tempo != Phrase.DEFAULT_TEMPO) {
          m_currentTempo = tempo;
        }

        elementTempoRatio = m_masterTempo / m_currentTempo;

        double lastPanPosition = -1.0;
        int offSetTime = 0;

        for (Note note : phrase.getNoteList()) {
          // deal with offset
          offSetTime = (int) (note.getOffset() * m_ppqn * elementTempoRatio);

          //handle frequency pitch types
          int pitch = note.getPitch();

          int dynamic = note.getDynamic();

          if (pitch == Note.REST) {
            phraseTick += note.getRhythm() * m_ppqn * elementTempoRatio;
            continue;
          }

          long onTick = phraseTick;
          // pan
          if (note.getPan() != lastPanPosition) {
            evt = createCChangeEvent(currChannel, 10, (int) (note.getPan() * 127), onTick);
            currTrack.add(evt);
            lastPanPosition = note.getPan();
          }

          evt = createNoteOnEvent(currChannel, pitch, dynamic, onTick + offSetTime);
          currTrack.add(evt);

          long offTick = (long) (phraseTick + note.getDuration() * m_ppqn * elementTempoRatio);

          evt = createNoteOffEvent(currChannel, pitch, dynamic, offTick + offSetTime);
          currTrack.add(evt);

          phraseTick += note.getRhythm() * m_ppqn * elementTempoRatio;

          // TODO:  Should this be ticks since we have tempo stuff
          // to worry about
          //System.out.println("offtick = " + offTick + " ppq = " +
          //	       m_ppqn + " score length = " + score.getEndTime() +
          //	       " length * ppq = " + (m_ppqn * score.getEndTime()));
          if ((double) offTick > longestTime) {
            longestTime = (double) offTick;
            longestTrack = currTrack;
            //longestRatio = trackTempoRatio;
          }
        }

        Float d = (Float) m_tempoHistory.pop();
        m_currentTempo = d.floatValue();
      }

      Float d = (Float) m_tempoHistory.pop();
      m_currentTempo = d.floatValue();

    }

    // add a meta event to indicate the end of the sequence.
    if (longestTime > 0.0 && longestTrack != null) {
      MetaMessage msg = new MetaMessage();
      byte[] data = new byte[0];
      msg.setMessage(STOP_TYPE, data, 0);
      MidiEvent evt = new MidiEvent(msg,
        (long) longestTime); //+ 100 if you want leave some space for reverb tail
      longestTrack.add(evt);
    }

    //sequence.

    System.out.println(
      "[SEQUENCE] Length: " + sequence.getMicrosecondLength() + " tick length " + sequence
        .getTickLength());

    for (Track track : sequence.getTracks()) {
      System.out.println("  [TRACK] ticks: " + track.ticks());

      for (int i = 0; i < track.size(); i++) {
        MidiEvent event = track.get(i);
        System.out.println("    [EVENT] ticks: " + event.getTick());
        System.out.println("    [EVENT-MESSAGE] length: " + event.getMessage().getLength());
        System.out.println("     |||Butes: " + bytesToHex(event.getMessage().getMessage()));
      }
    }

    return sequence;
  }


  public String bytesToHex(byte[] bytes) {

    char[] hexArray = "0123456789ABCDEF".toCharArray();
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }

  private boolean initSynthesizer() {
    if (null == m_synth) {
      try {
        if (MidiSystem.getSequencer() == null) {
          System.err.println("MidiSystem Sequencer Unavailable");
          return false;
        }

        m_synth = MidiSystem.getSynthesizer();
        m_synth.open();
        m_sequencer = MidiSystem.getSequencer();
        //m_sequencer.open();
      } catch (MidiUnavailableException e) {
        System.err.println("Midi System Unavailable:" + e);
        return false;
      }
    }
    return true;
  }

  /**
   * Close off all open JavaSound playback objects
   */
  public void finalize() {
    m_seq = null;
    m_sequencer.close();
    m_synth.close();
  }

}// MidiSynth
