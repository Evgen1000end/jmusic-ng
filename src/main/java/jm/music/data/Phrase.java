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

package jm.music.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jm.JMC;

/**
 * The Phrase class is representative of a single musical phrase.
 * Phrases are held in Parts and can be played at any time
 * based on there start times. They may be played sequentially or in parallel.
 * Phrases can be added to an Part like this...
 * <pre>
 *     Part inst = new Part("Flute");
 *     //Phrase for the right hand
 *     Phrase rightHand = new Phrase(0.0) //start this phrase on the first beat
 *     //Phrase for the left hand
 *     Phrase leftHane = new Phrase(4.0) //start this phrase on the fifth beat
 *     inst.addPhrase(rightHand);
 *     inst.addPhrase(leftHand);
 * </pre>
 *
 * @author Andrew Sorensen and Andrew Brown
 * @version 1.0, Sun Feb 25 18:43:32  2001
 * @see Note
 * @see Part
 */

public class Phrase implements JMC, Cloneable, Serializable {
  /**
   * The smallest start time in beats
   */
  public static final double MIN_START_TIME = 0.0;

  //----------------------------------------------
  // Default constants
  //----------------------------------------------
  public static final double DEFAULT_START_TIME = MIN_START_TIME;
  public static final String DEFAULT_TITLE = "Untitled Phrase";
  public static final int DEFAULT_INSTRUMENT = NO_INSTRUMENT;

  public static final boolean DEFAULT_APPEND = false;

  public static final double DEFAULT_TEMPO = -1.0;

  public static final double DEFAULT_PAN = Note.DEFAULT_PAN;
  public static final int DEFAULT_NUMERATOR = 4;
  //----------------------------------------------
  // Attributes
  //----------------------------------------------
  public static final int DEFAULT_DENOMINATOR = 4;
  /**
   * The pan position for notes in this phrase.
   * This must be set delibertley to override a note's pan position.
   */
  private double pan = DEFAULT_PAN;
  /**
   * An array containing mutiple voices
   */
  private List<Note> noteList;

  //	/** The phrases start time in beats */
  //	private double startTime;
  /**
   * The title/name given to this phrase
   */
  private String title = "Unnamed Phrase";
  private Position position;
  /**
   * instrumet / MIDI program change number for this phrase
   */
  private int instrument;
  /**
   * speed in beats per minute for this phrase
   */
  private double tempo;
  /**
   * Setting the phrase to append when added to a part
   * rather than use its start time.
   */
  private boolean append = false;
  /**
   * A phrase to have a relative start time with if required.
   */
  private Phrase linkedPhrase;
  /**
   * the top number of the time signature
   */
  private int numerator;
  /**
   * the bottom number of the time signature
   */
  private int denominator;
  /**
   * A reference to this phrases part
   */
  private Part myPart = null;
  /**
   * Weather the phrase should play or not
   */
  private boolean mute = false;
  //----------------------------------------------
  // Constructors
  //----------------------------------------------

  /**
   * Creates an empty Phrase.
   * The default start time is a flag which means the phrase will be
   * appended to the end of any part it is added to.
   */
  public Phrase() {
    this(DEFAULT_START_TIME);
    this.append = true;
  }

  /**
   * Creates an empty Phrase starting at the specified beat.
   *
   * @param startTime The beat at which the phrase will be positioned in its part.
   */
  public Phrase(double startTime) {
    this(startTime, DEFAULT_INSTRUMENT);
  }

  /**
   * Creates an empty Phrase
   *
   * @param startTime  The beat at which the phrase will be positioned in its part.
   * @param instrument The sound or instrument number to be used for this phrase.
   */
  public Phrase(double startTime, int instrument) {
    this(DEFAULT_TITLE, startTime, instrument);
  }

  /**
   * Creates an empty Phrase
   *
   * @param title The name for the phrase.
   */
  public Phrase(String title) {
    this(title, DEFAULT_START_TIME);
    this.append = true;
  }

  /**
   * Creates an empty Phrase.
   *
   * @param title     The name for the phrase.
   * @param startTime The beat at which the phrase will be positioned in its part.
   */
  public Phrase(String title, double startTime) {
    this(title, startTime, DEFAULT_INSTRUMENT);
  }

  /**
   * Creates an empty Phrase.
   *
   * @param title      The name for the phrase.
   * @param startTime  The beat at which the phrase will be positioned in its part.
   * @param instrument The sound or instrument number to be used for this phrase.
   */
  public Phrase(String title, double startTime, int instrument) {
    this(title, startTime, instrument, DEFAULT_APPEND);
  }

  /**
   * Creates an empty Phrase.
   *
   * @param title      The name for the phrase.
   * @param startTime  The beat at which the phrase will be positioned in its part.
   * @param instrument The sound or instrument number to be used for this phrase.
   * @param append     A flag specifying wheather or not this phrase should be added to the end of
   *                   the part it is added too, or should use its start time value.
   */
  public Phrase(String title, double startTime, int instrument, boolean append) {
    this.title = title;
    //		this.startTime = startTime;
    this.position = new Position(startTime, this);
    this.append = append;
    if (instrument < NO_INSTRUMENT) {
      System.err.println(new Exception("jMusic EXCEPTION: instrument " +
        "value must be greater than 0"));
      (new Exception()).printStackTrace();
      System.exit(1); //crash ungracefully
    }
    this.instrument = instrument;
    this.noteList = new ArrayList<>();
    this.numerator = DEFAULT_NUMERATOR;
    this.denominator = DEFAULT_DENOMINATOR;
    this.tempo = DEFAULT_TEMPO;
  }

  /**
   * Constructs a new Phrase containing the specified <CODE>note</CODE>.
   *
   * @param note Note to be containing by the phrase.
   */
  public Phrase(Note note) {
    this();
    addNote(note);
  }

  /**
   * Constructs a new Phrase containing the specified <CODE>notes</CODE>.
   *
   * @param notes array of Note to be contained by the phrase.
   */
  public Phrase(Note[] notes) {
    this();
    addNoteList(notes);
  }

  /**
   * Constructs a new Phrase containing the specified <CODE>note</CODE> with
   * the specified <CODE>title</CODE>.
   *
   * @param note  Note to be containing by the phrase.
   * @param title String describing the title of the Phrase.
   */
  public Phrase(Note note, String title) {
    this(title);
    addNote(note);
  }

  /**
   * Constructs a new Phrase containing the specified <CODE>notes</CODE> with
   * the specified <CODE>title</CODE>.
   *
   * @param notes array of Note to be contained by the phrase.
   * @param title String describing the title of the Phrase.
   */
  public Phrase(Note[] notes, String title) {
    this(title);
    this.addNoteList(notes);
  }

  /**
   * Constructs a new Phrase containing the specified <CODE>note</CODE> with
   * the specified <CODE>title</CODE>.
   *
   * @param note      Note to be containing by the phrase.
   * @param startTime String describing the title of the Phrase.
   */
  public Phrase(Note note, double startTime) {
    this(note);
    this.setStartTime(startTime);
  }

  //----------------------------------------------
  // Data Methods
  //----------------------------------------------

  /**
   * Return the program change assigned by this phrase
   *
   * @return int
   */
  public int getInstrument() {
    return this.instrument;
  }

  /**
   * Sets the program change value
   *
   * @param value program change
   */
  public void setInstrument(int value) {
    this.instrument = value;
  }

  /**
   * Add a note to this Phrase
   *
   * @param note - add a note to this phrase
   */
  public void addNote(Note note) {
    note.setPhrase(this);
    noteList.add(note);
  }


  /**
   * Add a note to this Phrase
   *
   * @param pitch -the pitch of the note
   * @param rv    - the rhythm of the note
   */
  public void addNote(int pitch, double rv) {
    Note note = Note.newBuilder().pitch(pitch).rhythm(rv).build();
    this.addNote(note);
  }

  /**
   * Appends the specified notes to the end of this Phrase.
   *
   * @param notes of Notes to append.
   */
  public void addNoteList(Note[] notes) {
    for (Note note : notes) {
      addNote(note);
    }
  }

  /**
   * Adds a vector of notes to the phrase.
   * A boolean option when true appends the notes to the end of the list,
   * if false the notes in noteVector will replace the notes currently in the phrase.
   *
   * @param noteVector the vector of notes to add
   * @param append     do we append or not?
   */
  public void addNoteList(List<Note> noteVector, boolean append) {
    if (!append) {
      this.noteList.clear();
    }
    for (Note note : noteVector) {
      addNote(note);
    }
  }

  /**
   * Adds an array of notes to the phrase.
   * A boolean option when true appends the notes to the end of the list.
   *
   * @param noteArray the array of notes to add
   * @param append    do we append or not?
   */
  public void addNoteList(Note[] noteArray, boolean append) {
    if (!append) {
      this.noteList.clear();
    }
    for (Note note : noteArray) {
      addNote(note);
    }
  }

  /**
   * Adds Multiple notes to the phrase from a pitch array and rhythm value
   *
   * @param pitchArray  array of pitch values
   * @param rhythmValue a rhythmic value
   */
  public void addNoteList(int[] pitchArray, double rhythmValue) {
    double[] rvArray = new double[pitchArray.length];
    for (int i = 0; i < rvArray.length; i++) {
      rvArray[i] = rhythmValue;
    }
    addNoteList(pitchArray, rvArray);
  }

  /**
   * Adds Multiple notes to the phrase from a pitch array, rhythm value, and dynmaic value
   *
   * @param pitchArray  - An array of pitch values
   * @param rhythmValue - A rhythmic value
   * @param dynamic     - A dynmaic value (1-127)
   */
  public void addNoteList(int[] pitchArray, double rhythmValue, int dynamic) {
    double[] rvArray = new double[pitchArray.length];
    int[] dynArray = new int[pitchArray.length];
    for (int i = 0; i < rvArray.length; i++) {
      rvArray[i] = rhythmValue;
      dynArray[i] = dynamic;
    }
    addNoteList(pitchArray, rvArray, dynArray);
  }

  /**
   * Adds Multiple notes to the phrase from an array of frequency values
   *
   * @param freqArray   array of freequency values
   * @param rhythmValue a rhythmic value
   */
  public void addNoteList(double[] freqArray, double rhythmValue) {
    double[] rvArray = new double[freqArray.length];
    for (int i = 0; i < rvArray.length; i++) {
      rvArray[i] = rhythmValue;
    }
    addNoteList(freqArray, rvArray);
  }


  /**
   * Adds Multiple notes to the phrase from several arrays
   *
   * @param pitchArray  array of pitch values
   * @param rhythmArray array of rhythmic values
   */
  public void addNoteList(int[] pitchArray, double[] rhythmArray) {
    int[] dynamic = new int[pitchArray.length];
    for (int i = 0; i < pitchArray.length; i++) {
      dynamic[i] = Note.DEFAULT_DYNAMIC;
    }
    addNoteList(pitchArray, rhythmArray, dynamic);
  }

  /**
   * Adds Multiple notes to the phrase from several arrays
   *
   * @param freqArray   array of frequency values
   * @param rhythmArray array of rhythmic values
   */
  public void addNoteList(double[] freqArray, double[] rhythmArray) {
    int[] dynamic = new int[freqArray.length];
    for (int i = 0; i < freqArray.length; i++) {
      dynamic[i] = Note.DEFAULT_DYNAMIC;
    }
    addNoteList(freqArray, rhythmArray, dynamic);
  }

  /**
   * Adds Multiple notes to the phrase from several arrays
   *
   * @param pitchArray  array of pitch values
   * @param rhythmArray array of rhythmic values
   * @param dynamic     array of dynamic values
   */
  public void addNoteList(int[] pitchArray, double[] rhythmArray,
                          int[] dynamic) {
    addNoteList(pitchArray, rhythmArray, dynamic, true);
  }

  /**
   * Adds Multiple notes to the phrase from several arrays
   *
   * @param freqArray   array of frequency values
   * @param rhythmArray array of rhythmic values
   * @param dynamic     array of dynamic values
   */
  public void addNoteList(double[] freqArray, double[] rhythmArray,
                          int[] dynamic) {
    addNoteList(freqArray, rhythmArray, dynamic, true);
  }

  /**
   * Adds Multiple notes to the phrase from several arrays
   * A boolean option when true appends the notes to the end of the list
   * if non true the current list is errased and replaced by the new notes
   *
   * @param pitchArray  array of pitch values
   * @param rhythmArray array of rhythmic values
   * @param dynamic     int
   * @param append      do we append or not?
   */
  public void addNoteList(int[] pitchArray, double[] rhythmArray,
                          int[] dynamic, boolean append) {
    if (!append) {
      this.noteList.clear();
    }
    for (int i = 0; i < pitchArray.length; i++) {
      try {
        Note knote = Note.newBuilder()
          .pitch(pitchArray[i])
          .rhythm(rhythmArray[i])
          .dynamic(dynamic[i])
          .build();
        this.addNote(knote);
      } catch (RuntimeException re) {
        System.err.println("You must enter arrays of even length");
      }
    }
  }

  /**
   * Adds Multiple notes to the phrase from several arrays
   * A boolean option when true appends the notes to the end of the list
   * if non true the current list is errased and replaced by the new notes
   *
   * @param freqArray   array of frequency values
   * @param rhythmArray array of rhythmic values
   * @param dynamic     int
   * @param append      do we append or not?
   */
  public void addNoteList(double[] freqArray, double[] rhythmArray,
                          int[] dynamic, boolean append) {
    if (!append) {
      this.noteList.clear();
    }
    for (int i = 0; i < freqArray.length; i++) {
      try {
        Note knote = Note.newBuilder()
          .frequency(freqArray[i])
          .rhythm(rhythmArray[i])
          .dynamic(dynamic[i])
          .build();
        this.addNote(knote);
      } catch (RuntimeException re) {
        System.err.println("jMusic Phrase error: You must enter arrays of even length");
      }
    }
  }

  /**
   * Adds Multiple notes to the phrase from one array of pitch, rhythm pairs
   *
   * @param pitchAndRhythmArray - an array of pitch and rhythm values
   */
  public void addNoteList(double[] pitchAndRhythmArray) {
    for (int i = 0; i < pitchAndRhythmArray.length; i += 2) {
      try {
        Note knote = Note.newBuilder()
          .pitch((int) pitchAndRhythmArray[i])
          .rhythm(pitchAndRhythmArray[i + 1])
          .build();
        this.addNote(knote);
      } catch (RuntimeException re) {
        System.err.println(
          "Error adding note list: Possibly the wrong number of values in the pitch and rhythm array.");
      }
    }
  }

  /**
   * Adds Multiple notes to the phrase from one pitch and an array of rhythm values
   *
   * @param pitch   The pitch values for the notes
   * @param rhythms An array of rhythm values
   */
  public void addNoteList(int pitch, double[] rhythms) {
    for (int i = 0; i < rhythms.length; i++) {
      this.addNote(Note.newBuilder().pitch(pitch).rhythm(rhythms[i]).build());
    }
  }

  /**
   * Adds Multiple notes to the phrase from one pitch and an array of rhythm values
   *
   * @param frequency The pitch values for the notes in hertz
   * @param rhythms   An array of rhythm values
   */
  public void addNoteList(double frequency, double[] rhythms) {
    for (int i = 0; i < rhythms.length; i++) {
      this.addNote(Note.newBuilder().frequency(frequency).rhythm(rhythms[i]).build());
    }
  }


  /**
   * Adds Multiple notes to the phrase all of which start at the same time
   * and share the same duration.
   *
   * @param pitches An array of pitch values
   * @param rv      the rhythm
   */
  public void addChord(int[] pitches, double rv) {
    for (int i = 0; i < pitches.length - 1; i++) {
      Note n = Note.newBuilder().pitch(pitches[i]).rhythm(0.0).build();
      n.setDuration(rv * Note.DEFAULT_DURATION_MULTIPLIER);
      this.addNote(n);
    }
    this.addNote(pitches[pitches.length - 1], rv);
  }

  public int[] getPitchArray() {
    Note[] notes = this.getNoteArray();
    int[] pitches = new int[notes.length];
    for (int i = 0; i < notes.length; i++) {
      pitches[i] = notes[i].getPitch();
    }
    return pitches;
  }

  public double[] getRhythmArray() {
    Note[] notes = this.getNoteArray();
    double[] rhythms = new double[notes.length];
    for (int i = 0; i < notes.length; i++) {
      rhythms[i] = notes[i].getRhythm();
    }
    return rhythms;
  }

  public int[] getDynamicArray() {
    Note[] notes = this.getNoteArray();
    int[] dynamics = new int[notes.length];
    for (int i = 0; i < notes.length; i++) {
      dynamics[i] = notes[i].getPitch();
    }
    return dynamics;
  }

  /**
   * Deletes the specified note in the phrase
   *
   * @param noteNumb noteNumb the index of the note to be deleted
   */
  public void removeNote(int noteNumb) {
    noteList.remove(noteNumb);
  }

//  /**
//   * Deletes the first occurence of the specified note in the phrase
//   *
//   * @param note the note object to be deleted.
//   */
//  public void removeNote(Note note) {
//    noteList.removeElement(note);
//  }

  /**
   * Deletes the last note in the phrase
   */
  public void removeLastNote() {
    noteList.remove(noteList.size() - 1);
  }

  /**
   * Returns the entire note list contained in a single voice
   *
   * @return Vector A vector containing all Note objects in this phrase
   */
  public List<Note> getNoteList() {
    return noteList;
  }

  /**
   * Replaces the entire note list with a new note list vector
   *
   * @param newNoteList of notes
   */
  public void setNoteList(List<Note> newNoteList) {
    noteList = newNoteList;
  }

  /**
   * Returns the all notes in the phrase as a array of notes
   *
   * @return Note[] An array containing all Note objects in this phrase
   */
  public Note[] getNoteArray() {
    //Vector vct = this.noteList;
    Note[] noteArray = new Note[noteList.size()];
    for (int i = 0; i < noteArray.length; i++) {
      noteArray[i] = noteList.get(i);
    }
    return noteArray;
  }

  /**
   * Return the phrase's startTime
   *
   * @return double The phrases startTime in beats from the beginning of the part or score.
   */
  public double getStartTime() {
    return position.getStartTime();
  }


  /**
   * Sets the phrases startTime
   * <p/>
   * <p>This positions the phrase absolutely.  If this phrase is currently
   * positioned relative to another phrase that anchoring will be lost.
   * <p/>
   * <p>To position this relative to another class use the
   * <code>anchor</code> method instead.
   *
   * @param startTime the time at which to start the phrase
   */
  public void setStartTime(double startTime) {
    if (startTime >= MIN_START_TIME) {
      position.setStartTime(startTime);
      //            this.startTime = startTime;
      this.setAppend(false);
    } else {
      System.err.println(
        "Error setting phrase start time value: You must enter values greater than "
          + MIN_START_TIME);
    }
  }

  /**
   * <p>The positions tries the phrase relative to another using the
   * alignment specified.  If the arrangement causes this class to start
   * before a start time of 0.0, the repositioning is considered invalid
   * and will fail. The original positioning will be restored and this
   * method will return false.
   * <p/>
   * If successful, the previous positioning whether absolute or relative
   * will be lost.
   * <p/>
   * <p>To position this absolutely use the <code>setStartTime</code>
   * method instead.
   *
   * @param anchor    the phrase against which this should be positioned
   * @param alignment how this should be positioned relative to anchor
   * @returns false if anchoring failed due to being positioned before the 0.0 start time barrier.
   * True otherwise.
   */
  public boolean attemptAnchoringTo(final Phrase anchor,
                                    final Alignment alignment,
                                    final double offset) {
    Position newPosition = new Position(anchor.position, alignment,
      offset, this);
    if (newPosition.getStartTime() < 0.0) {
      return false;
    } else {
      position = newPosition;
      return true;
    }

  }

  /**
   * Returns details of how this is aligned relative to another phrase.
   * Alternatively, if this phrase is aligned absolutely returns null.
   *
   * @returns null if aligned with setStartTime(), or details of alignment if aligned with
   * attemptAnchoringTo()
   */
  public Anchoring getAnchoring() {
    return position.getAnchoring();
  }

  /**
   * Return the phrases endTime
   *
   * @return double the phrases endTime
   */
  public double getEndTime() {
    double endTime = (getStartTime() < MIN_START_TIME) ? MIN_START_TIME : getStartTime();
    for (Note note : noteList) {
      endTime += note.getRhythm();
    }
    return endTime;
  }

  /**
   * Returns the length of the whole phrase in beats.
   *
   * @return double duration in beats
   */
  final double getTotalDuration() {
    double cumulativeLength = 0.0;
    for (Note note : noteList) {
      cumulativeLength += note.getRhythm();
    }
    return cumulativeLength;
  }

  /**
   * Return this phrases title
   *
   * @return String the phrases title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Gives the Phrase a new title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Return this phrases append status
   *
   * @return boolean the phrases append value
   */
  public boolean getAppend() {
    return this.append;
  }

  /**
   * Gives the Phrase a new append status
   *
   * @param append the append status
   */
  public void setAppend(boolean append) {
    this.append = append;
  }

  /**
   * Return this phrases this phrase is linked to
   *
   * @return Phrase the phrases linked to
   */
  public Phrase getLinkedPhrase() {
    return this.linkedPhrase;
  }

  /**
   * Make a link from this phrase to another
   *
   * @param link the phrase to link to
   */
  public void setLinkedPhrase(Phrase link) {
    this.linkedPhrase = link;
  }

  /**
   * Return the pan position for this phrase
   *
   * @return double the phrases pan setting
   */
  public double getPan() {
    return this.pan;
  }

  /**
   * Determine the pan position for all notes in this phrase.
   *
   * @param pan the phrase's pan setting
   */
  public void setPan(double pan) {
    this.pan = pan;
    for (Note note : noteList) {
      note.setPan(pan);
    }
  }

  /**
   * Return the tempo in beats per minute for this phrase
   *
   * @return double the phrase's tempo setting
   */
  public double getTempo() {
    return this.tempo;
  }

  /**
   * Determine the tempo in beats per minute for this phrase
   *
   * @param newTempo the phrase's tempo
   */
  public void setTempo(double newTempo) {
    this.tempo = newTempo;
  }

  /**
   * Get an individual note object by its number
   *
   * @param number - the number of the Track to return
   * @return Note answer - the note object to return
   */
  public Note getNote(int number) {
    int counter = 0;
    for (Note note : noteList) {
      if (counter == number) {
        return note;
      }
      counter++;
    }
    return null;
  }


  /**
   * Get the number of notes in this phrase
   *
   * @return int  The number of notes
   */
  public int length() {
    return size();
  }

  /**
   * Get the number of notes in this phrase
   *
   * @return int  length - the number of notes
   */
  public int size() {
    return (noteList.size());
  }

  /**
   * Get the number of notes in this phrase
   *
   * @return int  length - the number of notes
   */
  public int getSize() {
    return (noteList.size());
  }


  /**
   * Returns the numerator of the Phrase's time signature
   *
   * @return int time signature numerator
   */
  public int getNumerator() {
    return this.numerator;
  }

  /**
   * Specifies the numerator of the Phrase's time signature
   *
   * @param num time signature numerator
   */
  public void setNumerator(int num) {
    this.numerator = num;
  }

  /**
   * Returns the denominator of the Phrase's time signature
   *
   * @return int time signature denominator
   */
  public int getDenominator() {
    return this.denominator;
  }

  /**
   * Specifies the denominator of the Phrase's time signature
   *
   * @param dem time signature denominator
   */
  public void setDenominator(int dem) {
    this.denominator = dem;
  }

  /**
   * returns a reference to the part that contains this phrase
   */
  public Part getMyPart() {
    return myPart;
  }

  /**
   * Sets a reference to the part containing this phrase
   */
  public void setMyPart(Part part) {
    this.myPart = part;
  }

  /**
   * Returns a copy of the entire Phrase
   *
   * @return Phrase a copy of the Phrase
   */
  public Phrase copy() {
    Phrase phr = new Phrase();
    copyAttributes(phr);
    for (Note note : noteList) {
      phr.addNote(note.copy());
    }
    return phr;
  }

  private void copyAttributes(Phrase phr) {
    // NB: start time now covered by position
    phr.position = this.position.copy(phr);
    phr.setTitle(this.title + " copy");
    phr.setInstrument(this.instrument);
    phr.setAppend(this.append);
    phr.setPan(this.pan);
    phr.setLinkedPhrase(this.linkedPhrase);
    phr.setMyPart(this.getMyPart());
    phr.setTempo(this.tempo);
    phr.setNumerator(this.numerator);
    phr.setDenominator(this.denominator);
  }

  /**
   * Returns a copy of a specified section of the Phrase,
   * pads beginning and end with shortedend notes and rests
   * if notes or phrase boundaries don't align with locations.
   *
   * @param startLoc start location
   * @param endLoc   end location
   * @return Phrase a copy of the Phrase
   */
  public Phrase copy(double startLoc, double endLoc) {
    return this.copy(startLoc, endLoc, true);
  }

  /**
   * Returns a copy of a specified section of the Phrase,
   * pads beginning and end with shortedend notes and rests
   * if notes or phrase boundaries don't align with locations.
   *
   * @param startLoc         start location
   * @param endLoc           end location
   * @param requireNoteStart If true, only notes that start inside the copy range are included in
   *                         the copy. Notes starting prior but overlapping are replaced by rests.
   *                         Otherwise sections of notes inseide the bounds are included.
   * @return Phrase a copy of the Phrase
   */

  public Phrase copy(double startLoc, double endLoc, boolean requireNoteStart) {
    // are the arguments valid?
    if (startLoc >= endLoc || endLoc < this.getStartTime()) {
      return null;
    }
    Phrase tempPhr = new Phrase(0.0);
    copyAttributes(tempPhr);
    double beatCounter = this.getStartTime();
    if (beatCounter < 0.0) {
      beatCounter = 0.0;
    }
    //is it before the phrase?
    if (startLoc < beatCounter) {
      Note r = Note.newBuilder().pitch(REST).rhythm(beatCounter - startLoc).build();
      tempPhr.addNote(r);
      endLoc += beatCounter - startLoc;
    }

    // are there notes before the startLoc to pass up?
    for (int i = 0; i < this.size(); i++) {

      if (beatCounter < startLoc) {
        if ((beatCounter + this.getNote(i).getRhythm() > startLoc) &&
          (beatCounter + this.getNote(i).getRhythm() <= endLoc)) {
          if (requireNoteStart) {
            Note n = Note.newBuilder().pitch(REST).rhythm(beatCounter +
              this.getNote(i).getRhythm() - startLoc).build();
            tempPhr.addNote(n);
          } else {
            Note n = Note.newBuilder()
              .pitch(this.getNote(i).getPitch())
              .rhythm(beatCounter + this.getNote(i).getRhythm() - startLoc)
              .dynamic(this.getNote(i).getDynamic())
              .build();

            tempPhr.addNote(n);
          }
        }
        if (beatCounter + this.getNote(i).getRhythm() > endLoc) {
          if (requireNoteStart) {
            Note n = Note.newBuilder()
              .rest()
              .rhythm(beatCounter + this.getNote(i).getRhythm() - startLoc)
              .dynamic(this.getNote(i).getDynamic())
              .build();
            tempPhr.addNote(n);
          } else {

            Note n = Note.newBuilder()
              .pitch(this.getNote(i).getPitch())
              .rhythm(beatCounter + endLoc - startLoc)
              .dynamic(this.getNote(i).getDynamic())
              .build();
            tempPhr.addNote(n);

          }
        }
      }

      if (beatCounter >= startLoc && beatCounter < endLoc) { // this note starts in the space
        if (beatCounter + this.getNote(i).getRhythm() <= endLoc) { // also ends in it
          tempPhr.addNote(this.getNote(i));
        } else { //ends after the end. Make up last note.
          Note n = Note.newBuilder()
            .pitch(this.getNote(i).getPitch())
            .rhythm(endLoc - beatCounter)
            .dynamic(this.getNote(i).getDynamic())
            .build();
          tempPhr.addNote(n);
        }
      }
      beatCounter += this.getNote(i).getRhythm();
    }
    // is there more space past the end of the phrase?
    if (beatCounter < endLoc) { // make up a rest to fill the space
      Note r = Note.newBuilder().pitch(REST).rhythm(endLoc - beatCounter).build();
      tempPhr.addNote(r);
    }
    // done!
    return tempPhr;
  }


  /**
   * Returns a copy of a specified section of the Phrase,
   * pads beginning and end with shortedend notes and rests
   * if notes or phrase boundaries don't align with locations.
   *
   * @param trimmed         wether to truncte notes (as per the other versions of copy) or not
   * @param startTimeShifts wether to shift the start time or to add a rest if if the start is
   *                        afte startloc
   * @param startLoc        start location
   * @param endLoc          end location
   * @return Phrase a copy of the Phrase
   */
  public Phrase copy(double startLoc, double endLoc,
                     boolean trimmed, boolean truncated, boolean startTimeShifts) {
    // are the arguments valid?
    if (startLoc >= endLoc || endLoc < this.getStartTime()) {
      System.out.println("invalid arguments in Phrase.copy");
      return null;
    }
    Phrase tempPhr = new Phrase("", startLoc, this.instrument);
    //this.title + " copy", startLoc, this.instrument);
    tempPhr.setAppend(this.append);
    tempPhr.setPan(this.pan);
    tempPhr.setLinkedPhrase(this.linkedPhrase);
    tempPhr.setMyPart(this.getMyPart());
    double beatCounter = this.getStartTime();
    if (beatCounter < 0.0) {
      beatCounter = 0.0;
    }
    //is it before the phrase?

    //make beatCounter add up to the right amount before going though the segment

    for (Note note : noteList) {
      beatCounter += note.getRhythm();
      if (!(startLoc > beatCounter)) {
        break;
      }
    }

    // now it is in the segment, should a rest be added in the begining because
    // a note overlaps?
    if (startLoc < beatCounter) {
      if (beatCounter < endLoc) {
        if (startTimeShifts) {
          tempPhr.setStartTime(beatCounter + this.getStartTime());
        } else {
          Note r = Note.newBuilder().rest().rhythm(beatCounter - startLoc).build();
          tempPhr.addNote(r);
        }
      } else {
        Note r = Note.newBuilder().rest().rhythm(endLoc - startLoc).build();
        tempPhr.addNote(r);
        return tempPhr;
      }
    }
    double addedCounter = 0.0;

    for (Note note : noteList) {
      Note n = note.copy();
      if ((n.getRhythm() + beatCounter) > endLoc && trimmed) {
        n.setRhythmValue(endLoc - beatCounter, truncated);
      }
      tempPhr.addNote(n);
      addedCounter += n.getRhythm();
      beatCounter += n.getRhythm();
      if (!(beatCounter < endLoc)) {
        break;
      }
    }

    if (beatCounter < endLoc) {
      Note r = Note.newBuilder().rest().rhythm(endLoc - beatCounter).build();
      tempPhr.addNote(r);
    } else if (addedCounter == 0.0) {
      Note r = Note.newBuilder().rest().rhythm(endLoc - startLoc).build();
      tempPhr.addNote(r);
    }
    return tempPhr;
  }

  /**
   * Returns a copy of the entire Phrase only ontaining notes
   * between highest and lowset specified pitch.
   *
   * @return Phrase a partical copy of the Phrase
   * @ param highestPitch The top MIDI pitch to include in the copy
   * @ param lowestPitch The bottom MIDI pitch to include in the copy
   */
  public Phrase copy(int highestPitch, int lowestPitch) {
    if (lowestPitch >= highestPitch) {
      System.err.println("jMusic Phrase copy error: " +
        "lowset pitch is not lower than highest pitch");
      System.exit(0);
    }
    Phrase phr = new Phrase(this.title + " copy");
    //		phr.setStartTime(this.startTime);
    phr.position = this.position.copy(phr);
    phr.setInstrument(this.instrument);
    phr.setAppend(this.append);
    phr.setPan(this.pan);
    phr.setLinkedPhrase(this.linkedPhrase);
    phr.setMyPart(this.getMyPart());

    for (Note note : noteList) {
      Note n = note.copy();
      if (n.getPitch() > highestPitch && n.getPitch() < lowestPitch) {
        n.setPitch(REST);
      }
      phr.addNote(n);
    }
    return phr;
  }


  /**
   * Prints the tracks attributes to stdout
   */
  public String toString() {
    StringBuilder phraseData = new StringBuilder("-------- jMusic PHRASE: '" +
      title + "' contains " + this.size() + " notes.  Start time: " +
      getStartTime() + " --------" + '\n');
    if (this.tempo > 0) {
      phraseData.append("Phrase Tempo = ").append(this.tempo).append('\n');
    }
    int counter = 0;
    for (Note note : noteList) {
      phraseData.append(note.toString()).append('\n');
    }
    return phraseData.toString();
  }

  /**
   * Empty removes all elements in the note list vector
   */
  public void empty() {
    noteList.clear();
  }

  /**
   * Returns a carbon copy of a specified Phrase
   * Changes to notes in the original or the alias will be echoed in the other.
   * Note: that for this to work other phrase classes must to change the
   * noteList attribute to point to another object, but instead
   * should always update the noteList itself. See shuffle() as an example.
   */
  public Phrase alias() {
    Phrase phr = new Phrase(this.title + " alias", this.getStartTime(), this.instrument);
    phr.setTempo(this.tempo);
    phr.setAppend(this.append);
    phr.noteList = this.noteList;
    return phr;
  }


  /**
   * Return the pitch value of the highest note in the phrase.
   */
  public int getHighestPitch() {
    int max = -1;
    for (Note note : noteList) {
      if (note.getPitch() > max) {
        max = note.getPitch();
      }
    }
    return max;
  }

  /**
   * Return the pitch value of the lowest note in the phrase.
   */
  public int getLowestPitch() {
    int min = 128;
    for (Note note : noteList) {
      if (note.getPitch() < min && note.getPitch() >= 0) {
        min = note.getPitch();
      }
    }
    return min;
  }

  /**
   * Return the value of the longest rhythm value in the phrase.
   */
  public double getLongestRhythmValue() {
    double max = 0.0;
    for (Note note : noteList) {
      if (note.getRhythm() > max) {
        max = note.getRhythm();
      }
    }
    return max;
  }

  /**
   * Return the value of the shortest rhythm value in the phrase.
   */
  public double getShortestRhythmValue() {
    double min = 1000.0;
    for (Note note : noteList) {
      if (note.getRhythm() < min) {
        min = note.getRhythm();
      }
    }
    return min;
  }

  /**
   * Change the dynamic value of each note in the phrase.
   */
  public void setDynamic(int dyn) {
    for (Note note : noteList) {
      note.setDynamic(dyn);
    }
  }

  /**
   * Change the pitch value of each note in the phrase.
   */
  public void setPitch(int val) {
    for (Note note : noteList) {
      note.setPitch(val);
    }
  }

  /**
   * Change the rhythm value of each note in the phrase.
   */
  public void setRhythmValue(double val) {
    for (Note note : noteList) {
      note.setRhythm(val);
    }
  }

  /**
   * Change the Duration value of each note in the phrase.
   */
  public void setDuration(double val) {
    for (Note note : noteList) {
      note.setDuration(val);
    }
  }

  /**
   * Return the Duration of the phrase in beats.
   */
  public double getBeatLength() {
    return getEndTime();
  }

  /**
   * Generates and returns a new note with default values
   * and adds it to this phrase.
   */
  public Note createNote() {
    Note n = Note.newBuilder().build();
    this.addNote(n);
    return n;
  }

  /*
   * Replace one note with another.
   * @param Note the new note
   * @param index the phrase position to replace
   */
  public void setNote(Note n, int index) {
    if (index >= getSize()) {
      System.out.println("jMusic error: Phrase setNote index is too large.");
      return;
    }
    noteList.set(index, n);
  }

  /**
   * Retrieve the current mute status.
   *
   * @return boolean True or False, muted ot not.
   */
  public boolean getMute() {
    return this.mute;
  }

  /**
   * Specify the mute status of this phrase.
   *
   * @param state True or False, muted or not.
   */
  public void setMute(boolean state) {
    this.mute = state;
  }

  /**
   * Change both the rhythm and duration of each note in the phrase.
   *
   * @param newLength The new rhythm for the note (Duration is a proportion of this value)
   */
  public void setLength(double newLength) {
    this.setRhythmValue(newLength);
    this.setDuration(newLength * Note.DEFAULT_DURATION_MULTIPLIER);
  }

  /**
   * Calculate the start time, in beats, of the note at the specified index.
   *
   * @param noteIndex The note's position in the phrase.
   * @return double The absolute (taking into account phrase start time) beat position where the
   * note starts. -1 is returned when an index out of range is encounterd.
   */
  public double getNoteStartTime(int noteIndex) {
    if (noteIndex >= this.size()) {
      return -1.0;
    }
    double startLoc = this.getStartTime();
    for (int i = 0; i < noteIndex; i++) {
      startLoc += this.getNote(i).getRhythm();
    }
    return startLoc;
  }

  private final class Position implements Serializable {

    private final Phrase phrase;
    private double startTime = 0.0;
    private boolean isAbsolute = false;

    private Position anchor;

    private Alignment alignment = Alignment.AFTER;

    private double offset;

    private Position(final double startTime, final Phrase phrase) {
      this.isAbsolute = true;
      this.startTime = startTime;
      this.phrase = phrase;
    }

    private Position(final Position anchor,
                     final Alignment alignment,
                     final double offset,
                     final Phrase phrase) {
      this.isAbsolute = false;
      this.anchor = anchor;
      this.alignment = alignment;
      this.offset = offset;
      this.phrase = phrase;
    }

    private Anchoring getAnchoring() {
      if (isAbsolute) {
        return null;
      }
      return new Anchoring(anchor.phrase, alignment, offset);
    }

    private double getStartTime() {
      if (isAbsolute) {
        return startTime;
      } else {
        return alignment.determineStartTime(
          phrase.getTotalDuration(),
          anchor.getStartTime(),
          anchor.getEndTime())
          + offset;
      }
    }

    private void setStartTime(final double startTime) {
      this.isAbsolute = true;
      this.startTime = startTime;
    }

    private double getEndTime() {
      return phrase.getEndTime();
    }

    private Position copy(final Phrase newCopy) {
      return (isAbsolute)
        ? new Position(startTime, newCopy)
        : new Position(anchor, alignment, offset,
        newCopy);
    }
  }
}
