package jm.music.data;

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
public class NoteUtils {

  /**
   * Convert a frequency into a MIDI note pitch.
   * Assumes A440 and equal tempered intonation.
   * Adapted from C code written by Andrew Botros.
   *
   * @param freq The frequency value to convert.
   * @return int The MIDI pitch number closest to the input frequency.
   */
  public static int freqToMidiPitch(double freq) {
    if ((freq < 26.73) || (freq > 14496.0)) {
      System.err.println("freqToMidiPitch error: " +
          "Frequency " + freq + " is not within the MIDI note range.");
      return -1;
    }
    // A semitone higher than a given frequency
    // is 2^(1/12) times the frequency.
    double r = Math.pow(2, 1.0 / 12.0);
    // A cent higher than a given frequency
    // is 2^(1/1200) times the frequency
    double cent = Math.pow(2, 1.0 / 1200.0);
    int r_index = 0;
    int cent_index = 0;
    int side;
        /* search for input ratio against A4 to the nearest cent
           in range -49 to +50 cents around closest note */
    double referenceFreq = 440.0;
    if (freq >= referenceFreq) {
      while (freq > r * referenceFreq) {
        referenceFreq = r * referenceFreq;
        r_index++;
      }
      while (freq > cent * referenceFreq) {
        referenceFreq = cent * referenceFreq;
        cent_index++;
      }
      if ((cent * referenceFreq - freq) < (freq - referenceFreq)) {
        cent_index++;
      }
      if (cent_index > 50) {
        r_index++;
        cent_index = 100 - cent_index;
      }
    } else {
      while (freq < referenceFreq / r) {
        referenceFreq = referenceFreq / r;
        r_index--;
      }
      while (freq < referenceFreq / cent) {
        referenceFreq = referenceFreq / cent;
        cent_index++;
      }
      if ((freq - referenceFreq / cent) < (referenceFreq - freq)) {
        cent_index++;
      }
      if (cent_index >= 50) {
        r_index--;
        cent_index = 100 - cent_index;
      }
    }

    return 69 + r_index;
  }

  /**
   * Calculate the frequency in hertz of a MIDI note pitch.
   * Assumes an A440.0 reference and equal tempered intonation.
   * Written by Andrew Brown based on C code by Andrew Botros.
   *
   * @param midiPitch The note pitch value to convert.
   * @return double The frequency equivalent in cycles per second.
   */
  public static double midiPitchToFreq(int midiPitch) {
    //range OK
    if (midiPitch < 0 || midiPitch > 127) {
      System.err.println("jMusic Note.midiPitchToFreq error:" +
          "midiPitch of " + midiPitch + " is out side valid range.");
      return -1.0;
    }
    // A semitone higher than a given frequency
    // is 2^(1/12) times the frequency.
    double r = Math.pow(2, 1.0 / 12.0);
    int pitchOffset = midiPitch - 69;
    double freq = 440.0;
    if (midiPitch > 69) {
      for (int i = 69; i < midiPitch; i++) {
        freq = freq * r;
      }
    } else {
      for (int i = 69; i > midiPitch; i--) {
        freq = freq / r;
      }
    }
    // rounding to get more reasonable values
    freq = Math.round(freq * 1000.0) / 1000.0;

    return freq;
  }

  public static double pitchToFreq(int midiPitch) {
    return Math.pow(2, (midiPitch - 69) / 12) * 440;
  }

  /**
   * gets the string representation for a note for a given MIDI pitch (0-127)
   */
  public static String getNote(int pitch) {
    String noteString;
    if (pitch % 12 == 0) {
      noteString = "C";
    } else if (pitch % 12 == 1) {
      noteString = "C#";
    } else if (pitch % 12 == 2) {
      noteString = "D";
    } else if (pitch % 12 == 3) {
      noteString = "Eb";
    } else if (pitch % 12 == 4) {
      noteString = "E";
    } else if (pitch % 12 == 5) {
      noteString = "F";
    } else if (pitch % 12 == 6) {
      noteString = "F#";
    } else if (pitch % 12 == 7) {
      noteString = "G";
    } else if (pitch % 12 == 8) {
      noteString = "Ab";
    } else if (pitch % 12 == 9) {
      noteString = "A";
    } else if (pitch % 12 == 10) {
      noteString = "Bb";
    } else if (pitch % 12 == 11) {
      noteString = "B";
    } else {
      noteString = "N/A";
    }
    return noteString;
  }

}
