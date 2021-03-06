/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

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

// updates by Sean Hayes to complete key signature and time signature parsing
// fix to parsing of concurrent events by Guan Yin

package jm.midi;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;

import jm.JMC;
import jm.midi.event.CChange;
import jm.midi.event.EndTrack;
import jm.midi.event.Event;
import jm.midi.event.KeySig;
import jm.midi.event.NoteOn;
import jm.midi.event.PChange;
import jm.midi.event.TempoEvent;
import jm.midi.event.TimeSig;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

/**
 * A MIDI parser
 *
 * @author Andrew Sorensen and Sean T. Hayes
 */
public final class MidiParser implements JMC {

  //-----------------------------------------------------------
  //Converts a SMF into jMusic Score data
  //-----------------------------------------------------------

  private static double tickRemainder = 0.0;

  /**
   * Convert a SMF into the jMusic data type
   */
  public static void SMFToScore(Score score, SMF smf) {
    System.out.println("Convert SMF to JM");
    Enumeration aEnum = smf.getTrackList().elements();
    //Go through tracks
    while (aEnum.hasMoreElements()) {
      Part part = new Part();
      Track smfTrack = (Track) aEnum.nextElement();
      Vector evtList = smfTrack.getEvtList();
      Vector phrVct = new Vector();
      sortEvents(score, evtList, phrVct, smf, part);
      for (int i = 0; i < phrVct.size(); i++) {
        part.addPhrase((Phrase) phrVct.elementAt(i));
      }
      score.addPart(part);
      score.clean();
    }
  }

  private static void sortEvents(Score score, Vector evtList, Vector phrVct, SMF smf, Part part) {
    double startTime = 0.0;
    double[] currentLength = new double[100];
    Note[] curNote = new Note[100];
    int numOfPhrases = 0;
    double oldTime = 0.0;
    int phrIndex = 0;
    //Go through evts
    for (int i = 0; i < evtList.size(); i++) {
      Event evt = (Event) evtList.elementAt(i);
      startTime += (double) evt.getTime() / (double) smf.getPPQN();
      if (evt.getID() == 007) {
        PChange pchg = (PChange) evt;
        part.setInstrument(pchg.getValue());
        //if this event is a NoteOn event go on
      } else if (evt.getID() == 020) {
        TempoEvent t = (TempoEvent) evt;
        score.setTempo(t.getTempo());
      } else if (evt.getID() == 005) {
        NoteOn noteOn = (NoteOn) evt;
        part.setChannel(noteOn.getMidiChannel());
        short pitch = noteOn.getPitch();
        int dynamic = noteOn.getVelocity();
        short midiChannel = noteOn.getMidiChannel();
        //if you're a true NoteOn
        if (dynamic > 0) {
          noteOn(phrIndex, curNote, smf, i,
            currentLength, startTime,
            phrVct, midiChannel,
            pitch, dynamic, evtList);
        }
      } else if (evt instanceof TimeSig) {
        TimeSig timeSig = (TimeSig) evt;
        score.setNumerator(timeSig.getNumerator());
        score.setDenominator(timeSig.getDenominator());
      } else if (evt instanceof KeySig) {
        KeySig keySig = (KeySig) evt;
        score.setKeySignature(keySig.getKeySig());
        score.setKeyQuality(keySig.getKeyQuality());
      }
//                        else
//                        {
//                            System.out.printf( "Unused event: %o\n", evt.getID() );
//                            if( evt instanceof CChange )
//                            {
//                                CChange cChange = (CChange)evt;
//                                System.out.printf( "\tUnused event: %d\t%d\t%d\t%d\n",
//                                        cChange.getControllerNum(),
//                                        cChange.getMidiChannel(),
//                                        cChange.getTime(),
//                                        cChange.getValue() );
//                            }
//                            else if( evt.getID() == 027)
//                            {
//                                System.out.printf( "\tUnused event: %d\t%X\t%o\n",
//                                        evt.getTime(),
//                                        evt.getTime(),
//                                        evt.getTime() );
//                            }
//                        }
    }
  }

  //------------------------------------------------------------------
  // Converts a score into a SMF
  //------------------------------------------------------------------
  // MODIFIED 6/12/2003 Ron Legere to avoid use of magic note values for Program CHanges
  // Etc.

  private static void noteOn(int phrIndex, Note[] curNote, SMF smf, int i,
                             double[] currentLength, double startTime, Vector phrVct,
                             short midiChannel, short pitch, int dynamic, Vector evtList) {

    phrIndex = -1;
    //work out what phrase is ready to accept a note
    for (int p = 0; p < phrVct.size(); p++) {
      //Warning 0.02 should really be fixed
      if (currentLength[p] <= (startTime + 0.08)) {
        phrIndex = p;
        break;
      }
    }
    //need to create new phrase for a new voice?
    if (phrIndex == -1) {
      phrIndex = phrVct.size();
      phrVct.addElement(new Phrase(startTime));
      currentLength[phrIndex] = startTime;
    }
    //Do we need to add a rest ?
    if ((startTime > currentLength[phrIndex]) &&
      (curNote[phrIndex] != null)) {
      double newTime = startTime - currentLength[phrIndex];
      //perform a level of quantisation first
      if (newTime < 0.25) {
        double length =
          curNote[phrIndex].getRhythm();
        curNote[phrIndex].setRhythm(
          length + newTime);
      } else {
        Note restNote = Note.newBuilder()
          .rest()
          .rhythm(newTime)
          .dynamic(0)
          .build();
        restNote.setPan(midiChannel);
        restNote.setDuration(newTime);
        restNote.setOffset(0.0);
        ((Phrase) phrVct.elementAt(phrIndex)).
          addNote(restNote);
      }
      currentLength[phrIndex] += newTime;
    }
    // get end time
    double time = MidiUtil.getEndEvt(pitch, evtList, i) /
      (double) smf.getPPQN();
    // create the new note
    Note tempNote = Note.newBuilder()
      .pitch(pitch)
      .rhythm(time)
      .dynamic(dynamic)
      .build();
    tempNote.setDuration(time);
    curNote[phrIndex] = tempNote;
    ((Phrase) phrVct.elementAt(phrIndex)).addNote(curNote[phrIndex]);
    currentLength[phrIndex] += curNote[phrIndex].getRhythm();
  }


  public static Sequence scoreToSequence(Score score) throws InvalidMidiDataException {
    return null;
//    System.out.println("PPQN = " + m_ppqn);
//    Sequence sequence = new Sequence(Sequence.PPQ, m_ppqn);
//
//    m_masterTempo = m_currentTempo =
//      new Float(score.getTempo()).floatValue();
//
//    System.out.println("Начальный темп: master " + m_masterTempo + " текущий " + m_currentTempo);
//
//    javax.sound.midi.Track longestTrack = null;
//    double longestTime = 0.0;
//    double longestRatio = 1.0;
//
//    Enumeration parts = score.getPartList().elements();
//    System.out.println("Начинаю обработку частей: ");
//    while (parts.hasMoreElements()) {
//      Part inst = (Part) parts.nextElement();
//
//      int currChannel = inst.getChannel();
//      if (currChannel > 16) {
//        throw new
//          InvalidMidiDataException(inst.getTitle() +
//          " - Invalid Channel Number: " +
//          currChannel);
//      }
//
//      m_tempoHistory.push(m_currentTempo);
//
//      float tempo = new Float(inst.getTempo()).floatValue();
//
//      System.out.println("Part " + inst.getChannel() + " tempo " + tempo);
//
//      if (tempo != Part.DEFAULT_TEMPO) {
//        m_currentTempo = tempo;
//      } else if (tempo < Part.DEFAULT_TEMPO) {
//        System.out.println("jMusic MidiSynth error: Part TempoEvent (BPM) too low = " + tempo);
//      }
//
//      trackTempoRatio = m_masterTempo / m_currentTempo;
//
//      System.out.println("TrackTempoRatio " + trackTempoRatio);
//
//      int instrument = inst.getInstrument();
//      if (instrument == NO_INSTRUMENT) {
//        instrument = 0;
//      }
//
//      Enumeration phrases = inst.getPhraseList().elements();
//      double max = 0;
//      double currentTime = 0.0;
//
//      javax.sound.midi.Track currTrack = sequence.createTrack();
//      System.out.println("Начинаю обработку фраз ... ");
//      while (phrases.hasMoreElements()) {
//        /////////////////////////////////////////////////
//        // Each phrase represents a new Track element
//        // Err no
//        // There is a 65? track limit
//        // ////////////////////////////
//        Phrase phrase = (Phrase) phrases.nextElement();
//
//        //Track currTrack = sequence.createTrack();
//
//        currentTime = phrase.getStartTime();
//        long phraseTick = (long) (currentTime * m_ppqn * trackTempoRatio);
//        MidiEvent evt;
//
//        if (phrase.getInstrument() != NO_INSTRUMENT) {
//          instrument = phrase.getInstrument();
//        }
//        evt = createProgramChangeEvent(currChannel, instrument, phraseTick);
//        currTrack.add(evt);
//
//        m_tempoHistory.push(m_currentTempo);
//
//        tempo = new Float(phrase.getTempo()).floatValue();
//        if (tempo != Phrase.DEFAULT_TEMPO) {
//          m_currentTempo = tempo;
//        }
//
//        elementTempoRatio = m_masterTempo / m_currentTempo;
//
//        double lastPanPosition = -1.0;
//        int offSetTime = 0;
//
//        for (Note note : phrase.getNoteList()) {
//          // deal with offset
//          offSetTime = (int) (note.getOffset() * m_ppqn * elementTempoRatio);
//
//          //handle frequency pitch types
//          int pitch = note.getPitch();
//
//          int dynamic = note.getDynamic();
//
//          if (pitch == Note.REST) {
//            phraseTick += note.getRhythm() * m_ppqn * elementTempoRatio;
//            continue;
//          }
//
//          long onTick = phraseTick;
//          // pan
//          if (note.getPan() != lastPanPosition) {
//            evt = createCChangeEvent(currChannel, 10, (int) (note.getPan() * 127), onTick);
//            currTrack.add(evt);
//            lastPanPosition = note.getPan();
//          }
//
//          evt = createNoteOnEvent(currChannel, pitch, dynamic, onTick + offSetTime);
//          currTrack.add(evt);
//
//          long offTick = (long) (phraseTick + note.getDuration() * m_ppqn * elementTempoRatio);
//
//          evt = createNoteOffEvent(currChannel, pitch, dynamic, offTick + offSetTime);
//          currTrack.add(evt);
//
//          phraseTick += note.getRhythm() * m_ppqn * elementTempoRatio;
//
//          // TODO:  Should this be ticks since we have tempo stuff
//          // to worry about
//          //System.out.println("offtick = " + offTick + " ppq = " +
//          //	       m_ppqn + " score length = " + score.getEndTime() +
//          //	       " length * ppq = " + (m_ppqn * score.getEndTime()));
//          if ((double) offTick > longestTime) {
//            longestTime = (double) offTick;
//            longestTrack = currTrack;
//            //longestRatio = trackTempoRatio;
//          }
//        }
//
//        Float d = (Float) m_tempoHistory.pop();
//        m_currentTempo = d.floatValue();
//      }
//
//      Float d = (Float) m_tempoHistory.pop();
//      m_currentTempo = d.floatValue();
//
//    }
//
//    // add a meta event to indicate the end of the sequence.
//    if (longestTime > 0.0 && longestTrack != null) {
//      MetaMessage msg = new MetaMessage();
//      byte[] data = new byte[0];
//      msg.setMessage(STOP_TYPE, data, 0);
//      MidiEvent evt = new MidiEvent(msg,
//        (long) longestTime); //+ 100 if you want leave some space for reverb tail
//      longestTrack.add(evt);
//    }
//
//    //sequence.
//
//    System.out.println(
//      "[SEQUENCE] Length: " + sequence.getMicrosecondLength() + " tick length " + sequence
//        .getTickLength());
//
//    for (javax.sound.midi.Track track : sequence.getTracks()) {
//      System.out.println("  [TRACK] ticks: " + track.ticks());
//
//      for (int i = 0; i < track.size(); i++) {
//        MidiEvent event = track.get(i);
//        System.out.println("    [EVENT] ticks: " + event.getTick());
//        System.out.println("    [EVENT-MESSAGE] length: " + event.getMessage().getLength());
//        System.out.println("     |||Butes: " + bytesToHex(event.getMessage().getMessage()));
//      }
//    }
//
//    return sequence;
  }

  public static String bytesToHex(byte[] bytes) {

    char[] hexArray = "0123456789ABCDEF".toCharArray();
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }


  /**
   * Converts jmusic score data into SMF  data
   *
   * @param score - Basic Jmusic storage
   * @param smf   - Standart Midi File object
   */
  public static void scoreToSMF(Score score, SMF smf) {

    double scoreTempo = score.getTempo();
    double partTempoMultiplier = 1.0;
    double phraseTempoMultiplier = 1.0;
    int phraseNumb;
    Phrase phrase1, phrase2;

    //Add a tempo track at the start of top of the list
    //Add time sig to the tempo track
    Track smfT = new Track();
    smfT.addEvent(new TempoEvent(0, score.getTempo()));
    smfT.addEvent(new TimeSig(0, score.getNumerator(), score.getDenominator()));
    smfT.addEvent(new KeySig(0, score.getKeySignature()));
    smfT.addEvent(new EndTrack());
    smf.getTrackList().addElement(smfT);
    //---------------------------------------------------
    int partCount = 0;
    Enumeration aEnum = score.getPartList().elements();
    while (aEnum.hasMoreElements()) {
      Track smfTrack = new Track();
      Part inst = (Part) aEnum.nextElement();
      System.out.print("Part " + partCount + " '" + inst.getTitle() +
        "' to SMF Track on Channel. " + inst.getChannel() + ": ");
      partCount++;

      // set up tempo difference between score and track - if any
      if (inst.getTempo() != Part.DEFAULT_TEMPO) {
        partTempoMultiplier =
          scoreTempo / inst.getTempo();
      } else {
        partTempoMultiplier = 1.0;
      }
      //System.out.println("partTempoMultiplier = " + partTempoMultiplier);

      //order phrases based on their startTimes
      phraseNumb = inst.getPhraseList().size();
      for (int i = 0; i < phraseNumb; i++) {
        phrase1 = (Phrase) inst.getPhraseList().elementAt(i);
        for (int j = 0; j < phraseNumb; j++) {
          phrase2 = (Phrase) inst.getPhraseList().elementAt(j);
          if (phrase2.getStartTime() > phrase1.getStartTime()) {
            inst.getPhraseList().setElementAt(phrase2, i);
            inst.getPhraseList().setElementAt(phrase1, j);
            break;
          }
        }
      }
      //break Note objects into NoteStart's and NoteEnd's
      //as well as combining all phrases into one list
//			HashMap midiEvents = new HashMap();

      class EventPair {

        public double time;
        public Event ev;

        public EventPair(double t, Event e) {
          time = t;
          ev = e;
        }
      }

      LinkedList<EventPair> midiEvents = new LinkedList<>();

      //if this part has a Program Change value then set it
      if (inst.getInstrument() != NO_INSTRUMENT) {
        //System.out.println("Instrument change no. " + inst.getInstrument());
        midiEvents.add(new EventPair(0,
          new PChange((short) inst.getInstrument(), (short) inst.getChannel(), 0)));
      }

      if (inst.getNumerator() != NO_NUMERATOR) {
        midiEvents.add(new EventPair(0, new TimeSig(inst.getNumerator(), inst.getDenominator())));
      }

      if (inst.getKeySignature() != NO_KEY_SIGNATURE) {
        midiEvents.add(new EventPair(0, new KeySig(inst.getKeySignature(), inst.getKeyQuality())));
      }

      Enumeration partEnum = inst.getPhraseList().elements();
      double max = 0;
      double startTime = 0.0;
      double offsetValue = 0.0;
      int phraseCounter = 0;
      while (partEnum.hasMoreElements()) {
        Phrase phrase = (Phrase) partEnum.nextElement();

        startTime = phrase.getStartTime() * partTempoMultiplier;
        if (phrase.getInstrument() != NO_INSTRUMENT) {
          midiEvents.add(new EventPair(0,
            new PChange((short) phrase.getInstrument(), (short) inst.getChannel(), 0)));
        }
        if (phrase.getTempo() != Phrase.DEFAULT_TEMPO) {
          phraseTempoMultiplier = scoreTempo / phrase
            .getTempo(); //(scoreTempo * partTempoMultiplier) / phrase.getTempo();
        } else {
          phraseTempoMultiplier = partTempoMultiplier;
        }

        int noteCounter = 0;
        System.out.print(" Phrase " + phraseCounter++ + ":");
        // set a silly starting value to force and initial pan cc event
        double pan = -1.0;
        resetTicker(); // zero the ppqn error calculator

        for (Note note : phrase.getNoteList()) {
          offsetValue = note.getOffset();
          // add a pan control change if required
          if (note.getPan() != pan) {
            pan = note.getPan();
            midiEvents.add(new EventPair(startTime + offsetValue,
              new CChange((short) 10, (short) (pan * 127), (short) inst.getChannel(), 0)));
          }
          //check for frequency rather than MIDI notes
          int pitch = note.getPitch();

          if (pitch != REST) {
            midiEvents.add(new EventPair(startTime + offsetValue,
              new NoteOn((short) pitch, (short) note.getDynamic(), (short) inst.getChannel(),
                0)));

            // Add a NoteOn for the END of the note with 0 dynamic, as recommended.
            //create a timing event at the end of the notes duration
            double endTime = startTime + (note.getDuration() * phraseTempoMultiplier);
            // Add the note-off time to the list
            midiEvents.add(new EventPair(endTime + offsetValue,
              new NoteOn((short) pitch, (short) 0, (short) inst.getChannel(), 0)));
          }
          // move the note-on time forward by the rhythmic value
          startTime += tickRounder(
            note.getRhythm() * phraseTempoMultiplier); //time between start times
          System.out.print("."); // completed a note
        }
      }

      //Sort the hashmap by starttime (key value)
      class CompareKey implements Comparator {

        public int compare(Object a, Object b) {
          EventPair ae = (EventPair) a;
          EventPair be = (EventPair) b;
          if (ae.time - be.time < 0) {
            return -1;
          } else if (ae.time - be.time > 0) {
            return 1;
          } else {
            return 0;
          }
        }
      }
      midiEvents.sort(new CompareKey());
      //Add times to events, now that things are sorted
      double st = 0.0; //start time
      double sortStart; // start time from list of notes ons and offs.
      int time; // the start time as ppqn value
      resetTicker();

      for (int index = 0; index < midiEvents.size(); index++) {
        EventPair ep = midiEvents.get(index);
        Event event = ep.ev;
        sortStart = ep.time;
        time = (int) (((((sortStart - st) * (double) smf.getPPQN()))) + 0.5);
        st = sortStart;
        event.setTime(time);
        smfTrack.addEvent(event);
      }
      smfTrack.addEvent(new EndTrack());
      //add this track to the SMF
      smf.getTrackList().addElement(smfTrack);
      System.out.println();
    }
  }

//  public Sequence scoreToSeq(Score score)
//      throws InvalidMidiDataException {
//
//    Sequence sequence = new Sequence(Sequence.PPQ, m_ppqn);
//    if (null == sequence) {
//      return null;
//    }
//
//    m_masterTempo = m_currentTempo =
//        new Float(score.getTempo()).floatValue();
//
//    javax.sound.midi.Track longestTrack = null;
//    double longestTime = 0.0;
//    double longestRatio = 1.0;
//
//    Enumeration parts = score.getPartList().elements();
//    while (parts.hasMoreElements()) {
//      Part inst = (Part) parts.nextElement();
//
//      int currChannel = inst.getChannel();
//      if (currChannel > 16) {
//        throw new
//            InvalidMidiDataException(inst.getTitle() +
//            " - Invalid Channel Number: " +
//            currChannel);
//      }
//
//      m_tempoHistory.push(new Float(m_currentTempo));
//
//      float tempo = new Float(inst.getTempo()).floatValue();
//      //System.out.println("jMusic MidiSynth notification: Part TempoEvent (BPM) = " + tempo);
//      if (tempo != Part.DEFAULT_TEMPO) {
//        m_currentTempo = tempo;
//      } else if (tempo < Part.DEFAULT_TEMPO) {
//        System.out.println("jMusic MidiSynth error: Part TempoEvent (BPM) too low = " + tempo);
//      }
//
//      trackTempoRatio = m_masterTempo / m_currentTempo;
//
//      int instrument = inst.getInstrument();
//      if (instrument == NO_INSTRUMENT) {
//        instrument = 0;
//      }
//
//      Enumeration phrases = inst.getPhraseList().elements();
//      double max = 0;
//      double currentTime = 0.0;
//
//      //
//      // One track per Part
//      /////////////
//      javax.sound.midi.Track currTrack = sequence.createTrack();
//      while (phrases.hasMoreElements()) {
//        /////////////////////////////////////////////////
//        // Each phrase represents a new Track element
//        // Err no
//        // There is a 65? track limit
//        // ////////////////////////////
//        Phrase phrase = (Phrase) phrases.nextElement();
//
//        //Track currTrack = sequence.createTrack();
//
//        currentTime = phrase.getStartTime();
//        long phraseTick = (long) (currentTime * m_ppqn * trackTempoRatio);
//        MidiEvent evt;
//
//        if (phrase.getInstrument() != NO_INSTRUMENT) {
//          instrument = phrase.getInstrument();
//        }
//        evt = createProgramChangeEvent(currChannel, instrument, phraseTick);
//        currTrack.add(evt);
//
//        m_tempoHistory.push(new Float(m_currentTempo));
//
//        tempo = new Float(phrase.getTempo()).floatValue();
//        if (tempo != Phrase.DEFAULT_TEMPO) {
//          m_currentTempo = tempo;
//          //System.out.println("jMusic MidiSynth notification: Phrase TempoEvent (BPM) = " + tempo);
//        }
//
//        elementTempoRatio = m_masterTempo / m_currentTempo;
//
//        double lastPanPosition = -1.0;
//        int offSetTime = 0;
//        /// Each note
//        Enumeration notes = phrase.getNoteList().elements();
//        while (notes.hasMoreElements()) {
//          Note note = (Note) notes.nextElement();
//          // deal with offset
//          offSetTime = (int) (note.getOffset() * m_ppqn * elementTempoRatio);
//
//          //handle frequency pitch types
//          int pitch = -1;
//          if (note.getPitchType() == Note.MIDI_PITCH) {
//            pitch = note.getPitch();
//          } else {
//            pitch = Note.frequencyToPitch(note.getFrequency());
//          }
//
//          int dynamic = note.getDynamic();
//
//          if (pitch == Note.REST) {
//            phraseTick += note.getRhythm() * m_ppqn * elementTempoRatio;
//            continue;
//          }
//
//          long onTick = (long) (phraseTick);
//          // pan
//          if (note.getPan() != lastPanPosition) {
//            evt = createCChangeEvent(currChannel, 10, (int) (note.getPan() * 127), onTick);
//            currTrack.add(evt);
//            lastPanPosition = note.getPan();
//          }
//
//          evt = createNoteOnEvent(currChannel, pitch, dynamic, onTick + offSetTime);
//          currTrack.add(evt);
//
//          long offTick = (long) (phraseTick + note.getDuration() * m_ppqn * elementTempoRatio);
//
//          evt = createNoteOffEvent(currChannel, pitch, dynamic, offTick + offSetTime);
//          currTrack.add(evt);
//
//          phraseTick += note.getRhythm() * m_ppqn * elementTempoRatio;
//
//          // TODO:  Should this be ticks since we have tempo stuff
//          // to worry about
//          //System.out.println("offtick = " + offTick + " ppq = " +
//          //	       m_ppqn + " score length = " + score.getEndTime() +
//          //	       " length * ppq = " + (m_ppqn * score.getEndTime()));
//          if ((double) offTick > longestTime) {
//            longestTime = (double) offTick;
//            longestTrack = currTrack;
//            //longestRatio = trackTempoRatio;
//          }
//        }
//
//        Float d = (Float) m_tempoHistory.pop();
//        m_currentTempo = d.floatValue();
//
//      } // while(phrases.hasMoreElements())
//
//      Float d = (Float) m_tempoHistory.pop();
//      m_currentTempo = d.floatValue();
//
//    } // while(parts.hasMoreElements())
//
//    // add a meta event to indicate the end of the sequence.
//    if (longestTime > 0.0 && longestTrack != null) {
//      MetaMessage msg = new MetaMessage();
//      byte[] data = new byte[0];
//      msg.setMessage(StopType, data, 0);
//      MidiEvent evt = new MidiEvent(msg,
//          (long) longestTime); //+ 100 if you want leave some space for reverb tail
//      longestTrack.add(evt);
//    }
//
//    return sequence;
//  }


  // Helper function
  //
  private static boolean zeroVelEventQ(Event e) {
    if (e.getID() == 5) {
      // its a NoteOn
      if (((NoteOn) e).getVelocity() == 0) {
        return true;
      }
    }
    // most commonly:
    return false;
  }

  private static void resetTicker() {
    tickRemainder = 0.0;
  }

  /**
   * We need to call this any time we calculate unusual time values,
   * to prevent time creep due to the MIDI tick roundoff error.
   * This method wriiten by Bob Lee.
   */
  private static double tickRounder(double timeValue) {
    final double tick = 1. / 480.;
    final double halfTick = 1. / 960.;
    int ticks = (int) (timeValue * 480.);
    double rounded = ((double) ticks) * tick;
    tickRemainder += timeValue - rounded;
    if (tickRemainder > halfTick) {
      rounded += tick;
      tickRemainder -= tick;
    }
    return rounded;
  }

}
