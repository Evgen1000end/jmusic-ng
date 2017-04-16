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
You should have received a copy of the GNU General Public Licens
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
package jm.music.data;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Note class is representative of notes in standard western
 * music notation. It contains data relavent to music note information
 * such as time, duration and pitch. Notes get contained in Phrase
 * objects. Like in tradtional music notation (CPN) notes get played
 * one after the other in the order in which they are added to the
 * Phrase. <br>
 * !IMPORTANT: notes with a pitch of the minimum integer are rests,
 * those between 0 <> 127 are exactly the same as normal sounding
 * notes numbered like the MIDI specification. Notes with a pitch
 * specified as a double value, e.g., 440.0, are frequency values
 * for the note pitch. In general, note pitch refers to the chromatic
 * key number (as per MIDI) as an int and the note frequency
 * refer to the value in hertz as a double value. <br>
 * Notes can be added to a phrase like this...
 * <pre>
 *     Phrase phrase = new Phrase(0.0);
 *     Note note = new Note(C4, CROTCHET, MF);
 *     phrase.addNote(note);
 * </pre>
 * The note also has the option to create notes with reasonable default values.
 * This allows a user who is not interested in any performance details to work
 * with notes solely using pitch value and rythmValue like this....
 * <pre>
 *     Note note = new Note(C4, CROTCHET);
 * </pre>
 * The final option for building a note also includes the dynamic
 * parameter for minimal performace information.
 * <pre>
 *     Note note = new Note(C4, CROTCHET, F);
 * </pre>
 * <p/>
 * <B>Comments about the offset parameter:</B>
 * The intention of offset was to allow for 'feel' in score playback.
 * For example, a snare drum might be played slightly ahead or behind
 * the beat, or a sound with a slow attack might need to be triggered
 * early in order to sound in time with other parts.
 * <p/>
 * With this in mind, offset should have no influence on rhythm or
 * duration calculations within the jMusic data structure, only when
 * translated to another format (eg MIDI). In your
 * example (offset 0.1, duration 0.8, and rv 1.0) the rendering
 * (MIDI or other that does not support the offset concept) should
 * consider the start time of the note 0.1 bpm later than
 * 'normal' but the duration of the note will be 0.8 bpm from that
 * offset start time. The next note should start 1.0 bpm after the
 * 'normal' startTime (ignoring offset). This allows for
 * offset to be used in cases where performance of music varies
 * from beat to beat, eg., the downbeat is anticipated slightly. In
 * extreme cases this could cause overlapping notes in
 * MIDI translation which need to be 'handled' but we consider this a
 * weakness of the MIDI data specification rather than a problem for
 * jMusic : )
 * <p/>
 * In short, think of offset as "A displacement from the normal start
 * time of a note that allows performance interpretation without distorting
 * the score for compositional or analysis purposes."
 * ---------------
 *
 * @author Andrew Sorensen
 * @version 1.0, Sun Feb 25 18:43:31  2001
 * @see Phrase
 */

public class Note implements Cloneable, Serializable {

  /**
   * Default pitch value.
   */
  public static final int DEFAULT_PITCH = 60;
  /**
   * Default rhythm.
   */
  public static final double DEFAULT_RHYTHM = 1.0;
  /**
   * Default dynamic.
   */
  public static final int DEFAULT_DYNAMIC = 85;

  public static final double DEFAULT_FREQUENCY = 261.6255653006;

  /**
   * default pan value.
   */
  public static final double DEFAULT_PAN = 0.5;
  /**
   * default duration multiplier.
   */
  // should be deprecated in favour of the more musically meaning term
  // DEFAULT_ARTICULATION
  public static final double DEFAULT_DURATION_MULTIPLIER = 0.9;
  /**
   * default duration
   */
  public static final double DEFAULT_DURATION =
      DEFAULT_RHYTHM * DEFAULT_DURATION_MULTIPLIER;
  /**
   * Default articulation.  This field is meant to replace
   * DEFAULT_DURATION_MULTIPLIER.
   */
  public static final double DEFAULT_ARTICULATION = 0.9;
  /**
   * default offset value
   */
  public static final double DEFAULT_OFFSET = 0.0;
  /**
   * The number of seconds into a sample to begin reading data
   */
  public static final double DEFAULT_SAMPLE_START_TIME = 0.0;
  /**
   * The smallest value for a pitch.
   */
  public static final int MIN_PITCH = 0;
  /**
   * The smallest value for a frequency.
   */
  public static final double MIN_FREQUENCY = 0.00000000000000001;
  /**
   * The largest value for a pitch.
   */
  public static final int MAX_PITCH = 127;
  /**
   * The smallest value for a rhythm.
   */
  public static final double MIN_RHYTHM = 0.0;
  /**
   * The largest value for a rhythValue.
   */
  public static final double MAX_RHYTHM = Double.MAX_VALUE;
  /**
   * The smallest value for a dynamic.
   */
  public static final int MIN_DYNAMIC = 0;
  /**
   * The largest value for a dynamic.
   */
  public static final int MAX_DYNAMIC = 127;
  /**
   * The smallest pan value for a note.
   */
  public static final double MIN_PAN = 0.0;
  /**
   * The largest pan value for a note.
   */
  public static final double MAX_PAN = Double.MAX_VALUE;
  /**
   * The smallest value for a note's duration.
   */
  public static final double MIN_DURATION = 0.0;
  /**
   * The largest value for a note's duration.
   */
  public static final double MAX_DURATION = Double.MAX_VALUE;
  /**
   * The pitch value which indicates a rest.
   */
  public static final int REST = Integer.MIN_VALUE;
  /**
   * Specified envelope break point value indexes
   */
  public static final int AMP_ENV = 0;
  public static final int PITCH_ENV = 1;
  public static final int FILTER_ENV = 2;
  public static final int PAN_ENV = 3;
  /**
   * string constants for keys on the keyboard
   */

  private Logger log = LoggerFactory.getLogger(Note.class);
  /**
   * the string that this note maps to being one of the 12 string constants
   */
  private String note = "";

  /**
   * Pitch/frequency value of the note
   */
  private int pitch;

  private double frequency;

  /**
   * Dynamic value ranging from 0-127 (0 = off; 1 = quiet; 127=loud)
   */
  private int dynamic;
  /**
   * The length of the note. Constant values representing standard note
   * lengths are defined in JMC
   */
  private double rhythm;
  /**
   * 0.0 (full left) 0.5 (center) 1.0 (full right)
   */
  private double pan;
  /**
   * A notes duration of time in beats
   */
  private double duration;
  /**
   * A note's offset time. How far in front or behind of the rhythmic value
   * should we place this note
   */
  private double offset;
  /**
   * The time into a sample for playback to begin in milliseconds - for audio only
   */
  private double sampleStartTime;
  /**
   * The type of value that pitch is representing - frequency (true) or MIDI note (false)
   */
  //private boolean pitchType;

  //private PitchType pitchType;
  /**
   * The phrase that this note has been added to.
   */
  private Phrase phrase = null;
  /**
   * An array of break point envelope values to be used by this note
   * - see Note constants for specified index allocations
   */
  private double[][] breakPoints = new double[64][];

  private Note(Builder builder) {
    if (!builder.initFrequence && !builder.initPitch) {
      setPitch(Note.DEFAULT_PITCH);
    }
    if (builder.initFrequence) {
      setFrequency(builder.frequency);
    }
    if (builder.initPitch) {
      setPitch(builder.pitch);
    }
    setRhythm(builder.rhythm);
    if (builder.factor) {
      setDuration(builder.rhythm * DEFAULT_DURATION_MULTIPLIER);
    } else {
      setDuration(builder.duration);
    }
    setOffset(builder.offset);
    setPan(builder.pan);
    setDynamic(builder.dynamic);
    note = NoteUtils.getNote(this.getPitch());
  }

  public static Note defaultNote() {
    return newBuilder().build();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Retrieve the note's pitch as a frequency
   *
   * @return double note's pitch
   */
  public double getFrequency() {
    return frequency < (REST + 2) ? REST : frequency;
  }

  /**
   * Assign notes pitch as a frequency.  If the parameter <CODE>pitch</CODE> is less than
   * {@link #MIN_FREQUENCY} then the pitch of this note will be set to MIN_FREQUENCY.
   *
   * @param frequency note pitch as a frequency in hertz
   */
  public void setFrequency(double frequency) {
    if (frequency == REST) {
      this.frequency = REST;
      this.pitch = REST;
      note = "N/A";
    } else {
      this.frequency = (frequency < MIN_FREQUENCY) ? MIN_FREQUENCY : frequency;
      pitch = NoteUtils.frequencyToPitch(this.frequency);
      note = NoteUtils.getNote(pitch);
    }
  }

  /**
   * Retrieve the note's pitch as an integer.
   * Useful for working with pitch as MIDI note numbers.
   *
   * @return int note's pitch
   */
  public int getPitch() {
    return pitch < (REST + 2) ? REST : pitch;
  }

  /**
   * Assign notes pitch.  If the parameter <CODE>pitch</CODE> is less than
   * {@link #MIN_PITCH} then the pitch of this note will be set to MIN_PITCH.
   * Likewise, if <CODE>pitch</CODE> is greater than {@link #MAX_PITCH},
   * pitch will be set to MAX_MIDI_PITCH.
   *
   * @param pitch notes pitch
   */
  public void setPitch(int pitch) {
    if (pitch == REST) {
      this.pitch = REST;
      this.frequency = REST;
      note = "N/A";
    } else {
      this.pitch = (pitch < MIN_PITCH) ? MIN_PITCH : ((pitch > MAX_PITCH) ? MAX_PITCH : pitch);
      frequency = NoteUtils.pitchToFrequency(this.pitch);
      note = NoteUtils.getNote(this.getPitch());
    }
  }

  /**
   * Retrieve note's rhythm
   *
   * @return float notes rhythm
   */
  public double getRhythm() {
    return this.rhythm;
  }

  /**
   * Assign notes rhythm
   *
   * @param rhythm notes rhythm
   */
  public void setRhythm(double rhythm) {
    this.rhythm = (rhythm < MIN_RHYTHM)
        ? MIN_RHYTHM
        : ((rhythm > MAX_RHYTHM)
            ? MAX_RHYTHM
            : rhythm);
  }

  /**
   * Retrieve notes dynamic
   *
   * @return int notes dynamic
   */
  public int getDynamic() {
    return this.dynamic;
  }

  /**
   * Assign notes dynamic
   *
   * @param dynamic notes dynamic
   */
  public void setDynamic(int dynamic) {
    this.dynamic = (dynamic < MIN_DYNAMIC)
        ? MIN_DYNAMIC
        : ((dynamic > MAX_DYNAMIC) ? MAX_DYNAMIC : dynamic);
  }

  /**
   * Retrieves note's pan. 0.0 (full left) 0.5 (center) 1.0 (full right)
   *
   * @return notes pan
   */
  public double getPan() {
    return this.pan;
  }

  /**
   * Assign notes pan. 0.0 (full left) 0.5 (center) 1.0 (full right).
   *
   * @param pan note's pan
   */
  public void setPan(double pan) {
    this.pan = (pan < MIN_PAN)
        ? MIN_PAN
        : ((pan > MAX_PAN) ? MAX_PAN : pan);
  }

  /**
   * Return note duration. 1.0 = Crotchet (Quater Note), 0.5 = Quaver (Eighth Note), etc...
   *
   * @return double note's duration
   */
  public double getDuration() {
    return this.duration;
  }

  /**
   * Set notes duration. 1.0 = Crotchet (Quater Note), 0.5 = Quaver (Eighth Note), etc...
   *
   * @param duration note's duration
   */
  public void setDuration(double duration) {
    this.duration = (duration < MIN_DURATION)
        ? MIN_DURATION
        : ((duration > MAX_DURATION) ? MAX_DURATION : duration);
  }

  /**
   * Return note offset.
   * The range is 0 = no change, positive number delay the note, negative values rush (advance) it
   *
   * @return double note's offset.
   */
  public double getOffset() {
    return this.offset;
  }

  /**
   * Set notes offset.
   * The range is 0 = no change, positive number delay the note, negative values rush (advance) it.
   *
   * @param offset note's offset.
   */
  public void setOffset(double offset) {
    this.offset = offset;
  }

  /**
   * Return note sampleStartTime.
   *
   * @return int note's sampleStartTime.
   */
  public double getSampleStartTime() {
    return this.sampleStartTime;
  }

  /**
   * Set notes sampleStartTime.
   *
   * @param sampleStartTime note's sampleStartTime
   */
  public void setSampleStartTime(double sampleStartTime) {
    this.sampleStartTime = sampleStartTime;
  }

  /**
   * Return a reference to the phrase containing this note.
   */
  public Phrase getPhrase() {
    return this.phrase;
  }

  /**
   * Sets a reference to the phrase that contains this note.
   */
  public void setPhrase(Phrase phr) {
    this.phrase = phr;
  }

  /**
   * Returns a copy of this note
   *
   * @return Note
   */
  public Note copy() {
    Note note;
    note = Note.newBuilder()
        .pitch(this.getPitch())
        .rhythm(this.rhythm)
        .dynamic(this.dynamic)
        .pan(this.pan)
        .duration(this.duration)
        .offset(this.offset)
        .build();
    note.setSampleStartTime(this.sampleStartTime);
    note.setPhrase(this.phrase);
    for (int i = 0; i < breakPoints.length; i++) {
      if (this.breakPoints[i] != null) {
        note.setBreakPoints(i, this.getBreakPoints(i));
      }
    }
    return note;
  }

  /**
   * Specify the values for a break point envelope.
   * Some indexes of the breakPoints array are reserved for
   * specific purposes. See the Note constants for a list of these.
   *
   * @param index The specific breakPoint number to set.
   * @param points The values for this break point array.
   */
  public void setBreakPoints(final int index, final double[] points) {
    if (index < 0 || index > breakPoints.length) {
      throw new IllegalArgumentException("jMusic Note error: BreakPoint index " + index +
          " is out of range when setting.");
    }
    this.breakPoints[index] = points;
  }

  /**
   * Retrieve the break point envelope values.
   * Some indexes of the breakPoints array are reserved for
   * specific purposes. See the Note constants for a list of these.
   *
   * @return double[] The break oint values for the specified index
   */
  public double[] getBreakPoints(final int index) {
    if (index < 0 || index > breakPoints.length) {
      throw new IllegalArgumentException("jMusic Note error: BreakPoint index " + index +
          "is out of range when getting.");
    }
    if (breakPoints[index] == null) {
      throw new IllegalArgumentException(
          "jMusic Note error: Breakpoint index " + index + " is empty.");
    }
    return this.breakPoints[index];
  }

  /**
   * Collects a string of the notes attributes
   */
  @Override
  public String toString() {
    return "jMusic NOTE: " +
        "[Frequency = " + frequency +
        "[Pitch = " + pitch +
        "][RhythmValue = " + rhythm +
        "][Dynamic = " + dynamic +
        "][Duration = " + duration +
        "][Pan = " + pan + "]";
  }



  /**
   * Checks if the note is within a particular scale
   * There are a number of scale constants specified in the JMC
   * which can be used with this method,
   * these include MAJOR_SCALE, MINOR_SCALE, and PENTATONIC_SCALE
   *
   * @param scale - an array of scale degrees
   * @return boolean - true means it is in the scale
   */
  public boolean isScale(final int[] scale) {
    for (int scaleItem : scale) {
      if (this.pitch % 12 == scaleItem) {
        return true;
      }
    }
    return false;
  }

  /**
   * Sets the rhythm, and optionally change the duration
   * at the same time.
   *
   * @param factorDuration wether or not to change the duration to be a multiple of the rhythm value
   * as well
   */
  public void setRhythmValue(final double rv, final boolean factorDuration) {
    setRhythm(rv);
    if (factorDuration) {
      setDuration(rv * DEFAULT_DURATION_MULTIPLIER);
    }
  }

  /**
   * Check if the note is a rest or a pitched note.
   *
   * @return boolean True if the note is a rest otherwise false.
   */
  public boolean isRest() {
    return this.getPitch() == REST;
  }

  /**
   * Change both the rhythm and duration of a note in the one step.
   *
   * @param newLength The new rhythm for the note (Duration is a proportion of this value)
   */
  public void setLength(final double newLength) {
    setRhythm(newLength);
    setDuration(newLength * DEFAULT_DURATION_MULTIPLIER);
  }

  /**
   * tells whether the note is a sharp or not
   */
  public boolean isSharp() {
    return (note.equals("C#") || note.equals("F#"));
  }

  /**
   * tells whether the note is a sharp or not by using its string value.
   */
  public boolean isFlat() {
    return note.equals("Eb")
        || note.equals("Ab")
        || note.equals("Bb");
  }

  /**
   * tells whether the note is natural or not
   */
  public boolean isNatural() {
    return !isSharp() && !isFlat();
  }

  /**
   *
   * @param note
   * @return note has same duration and pitch
   */
  public boolean equals(Note note) {
    return this.getPitch() == note.getPitch() && this.getDuration() == note.getDuration();
  }

  /**
   * gives the next note in the scale
   * <p/>
   * For example, if inputting a D note for a DMinor scale, the next note returned is an E
   * if inputing a Bb for a C
   *
   * @param scale a constant from Scales.java goes here
   */
  public Note nextNote(int[] scale) {
    Note nextNote = null;
    for (int i = 0; i < scale.length; i++) {
      if (NoteUtils.pitchValue(note) % 12 == 0) {
        nextNote = Note.newBuilder().pitch(this.getPitch() + scale[i]).build();
      }
    }
    int nextpitch = this.getPitch() + scale[1];
    log.info("Next pitch {} {} {}", nextpitch, getPitch(), scale[1]);
    return Note.newBuilder().pitch(nextpitch).build();
  }

  public String getNote() {
    return note;
  }

  public static class Builder {

    private double offset = DEFAULT_OFFSET;
    private double pan = DEFAULT_PAN;
    private int dynamic = DEFAULT_DYNAMIC;
    private double rhythm = DEFAULT_RHYTHM;
    private double duration = DEFAULT_DURATION;
    private boolean factor = false;
    private int pitch = DEFAULT_PITCH;
    private double frequency = DEFAULT_FREQUENCY;
    private boolean initPitch = false;
    private boolean initFrequence = false;

    public Builder offset(final double offset) {
      this.offset = offset;
      return this;
    }

    public Builder pan(final double pan) {
      this.pan = pan;
      return this;
    }

    public Builder dynamic(final int dynamic) {
      this.dynamic = dynamic;
      return this;
    }

    public Builder duration(final double duration) {
      this.duration = duration;
      return this;
    }

    public Builder rhythm(final double rhythm) {
      this.rhythm = rhythm;
      return this;
    }

    public Builder frequency(final double frequency) {
      this.frequency = frequency;
      this.initFrequence = true;
      return this;
    }

    public Builder pitch(final int pitch) {
      this.pitch = pitch;
      this.initPitch = true;
      return this;
    }

    public Builder rest() {
      this.pitch = REST;
      this.initPitch = true;
      return this;
    }

    public Builder factorDuration(final boolean factor) {
      this.factor = factor;
      return this;
    }

    public Note build() {
      return new Note(this);
    }
  }

}