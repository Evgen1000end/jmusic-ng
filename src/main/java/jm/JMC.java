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
package jm;

import jm.constants.Alignments;
import jm.constants.DrumMap;
import jm.constants.Dynamics;
import jm.constants.Frequencies;
import jm.constants.Noises;
import jm.constants.Panning;
import jm.constants.Pitches;
import jm.constants.ProgramChanges;
import jm.constants.RhythmValues;
import jm.constants.Scales;
import jm.constants.Tunings;
import jm.constants.Waveforms;
import jm.music.data.Note;

/**
 * JMConstants holds constant values used across the whole
 * jMusic system.
 *
 * @author Andrew Sorensen with lots of help from Andrew Brown and Andrew Troedson
 * @see Note
 */
public interface JMC extends RhythmValues, Pitches, Frequencies, Tunings,
  Dynamics, Panning, ProgramChanges, DrumMap, Scales,
  Waveforms, Noises, Alignments {

  /**
   * A constant used to toggle debugging information.
   */
  boolean DEBUG = false;
  /**
   * A constant used to toggle the verbosity of output.
   */
  boolean VERBOSE = true;

  /**
   * Constant for 8 bit.
   */
  int EIGHT_BIT = 127;
  /**
   * Constant for 16 bit.
   */
  int SIXTEEN_BIT = 32767;
  /**
   * Constant for 32 bit.
   */
  int THIRTY_TWO_BIT = 214748647;
  /**
   * Constant for program changes.
   */
  int PROG_EVT = 748394; //Integer.MIN_VALUE + 1;
  /**
   * Constant for tempo changes.
   */
  int TEMP_EVT = PROG_EVT + 1;
  /**
   * Constant for key signature events
   */
  int KEY_SIG_EVT = TEMP_EVT + 1;
  /**
   * Constant for time signature events
   */
  int TIME_SIG_EVT = KEY_SIG_EVT + 1;
  /**
   * Constant for no key signature
   */
  int NO_KEY_SIGNATURE = Integer.MIN_VALUE;
  /**
   * Constant for no key quality
   */
  int NO_KEY_QUALITY = Integer.MIN_VALUE;
  /**
   * Constant for no numerator
   */
  int NO_NUMERATOR = Integer.MIN_VALUE;
  /**
   * Constant for no denominator
   */
  int NO_DENOMINATOR = Integer.MIN_VALUE;
  /**
   * Constant for no instrument
   */
  int NO_INSTRUMENT = -1;

  //----------------------------------------------
  // Audio constants
  //----------------------------------------------
        /* modulation sources */
  int AMPLITUDE = 0;
  int FREQUENCY = 1;

  /* channels */
  int MONO = 1;
  int STEREO = 2;
  int QUADRAPHONIC = 4;
  int OCTAPHONIC = 8;

  //----------------------------------------------
  // Data type constants
  //----------------------------------------------
  int PITCH = 0;
  int RHYTHM = 1;
  int DYNAMIC = 2;
  int PAN = 3;

}
