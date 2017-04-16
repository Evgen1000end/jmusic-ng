package jm.music.data;

import static jm.constants.Frequencies.FRQ;

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
public class NoteUtils {

  /**
   * Convert a frequency into a MIDI note pitch.
   * Assumes A440 and equal tempered intonation.
   * Adapted from C code written by Andrew Botros.
   *
   * @param frequency The frequency value to convert.
   * @return int The MIDI pitch number closest to the input frequency.
   */
  public static int frequencyToPitch(double frequency) {
    if ((frequency < 26.73) || (frequency > 14496.0)) {
      System.err.println("frequencyToPitch error: "
          + "Frequency " + frequency + " is not within the MIDI note range.");
      return -1;
    }
    double r = Math.pow(2, 1.0 / 12.0);
    double cent = Math.pow(2, 1.0 / 1200.0);
    int index = 0;
    int centIndex = 0;
    double referenceFreq = 440.0;
    if (frequency >= referenceFreq) {
      while (frequency > r * referenceFreq) {
        referenceFreq = r * referenceFreq;
        index++;
      }
      while (frequency > cent * referenceFreq) {
        referenceFreq = cent * referenceFreq;
        centIndex++;
      }
      if ((cent * referenceFreq - frequency) < (frequency - referenceFreq)) {
        centIndex++;
      }
      if (centIndex > 50) {
        index++;
      }
    } else {
      while (frequency < referenceFreq / r) {
        referenceFreq = referenceFreq / r;
        index--;
      }
      while (frequency < referenceFreq / cent) {
        referenceFreq = referenceFreq / cent;
        centIndex++;
      }
      if ((frequency - referenceFreq / cent) < (referenceFreq - frequency)) {
        centIndex++;
      }
      if (centIndex >= 50) {
        index--;
      }
    }
    return 69 + index;
  }

  /**
   * Calculate the frequency in hertz of a MIDI note pitch.
   * Assumes an A440.0 reference and equal tempered intonation.
   * Written by Andrew Brown based on C code by Andrew Botros.
   *
   * @param pitch The note pitch value to convert.
   * @return double The frequency equivalent in cycles per second.
   */
  @Deprecated
  public static double pitchToFrequencyByNumericalMethod(int pitch) {
    if (pitch < 0 || pitch > 127) {
      throw new IllegalArgumentException(pitch + " should be in 0-127 interval");
    }
    double r = Math.pow(2, 1.0 / 12.0);
    double freq = 440.0;
    if (pitch > 69) {
      for (int i = 69; i < pitch; i++) {
        freq = freq * r;
      }
    } else {
      for (int i = 69; i > pitch; i--) {
        freq = freq / r;
      }
    }
    freq = Math.round(freq * 1000.0) / 1000.0;
    return freq;
  }

  /**
   * Calculate the frequency in hertz of a MIDI note pitch.
   * @param pitch - Midi pitch value.
   * @return frequency.
   */
  public static double pitchToFrequency(int pitch) {
    if (pitch < 0 || pitch > 127) {
      throw new IllegalArgumentException(pitch + " should be in 0-127 interval");
    }
    return FRQ[pitch];
  }

  /**
   * Calculate the frequency in hertz of a MIDI note pitch.
   * @param pitch - Midi pitch value.
   * @return frequency.
   */
  @Deprecated
  public static double pitchToFrequencyByApproximate(int pitch) {
    return Math.pow(2, (pitch - 69) / 12) * 440;
  }

  /**
   * Gets the string representation for a note for a given MIDI pitch (0-127)
   * @param pitch - Midi pitch value.
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
