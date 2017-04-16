package test.jm.music.data.utils;

import jm.music.data.NoteUtils;

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
public class NoteImitator {

  public final static int REST = Integer.MIN_VALUE;

  private double freq;
  private int pitch;

  public double getFreq() {
    return freq < (REST+2) ? REST : freq;
  }

  public void setFreq(double freq) {
    if (freq == REST) {
      this.freq = REST;
      this.pitch = REST;
    } else {
      this.freq = (freq < 0.00000000000000001) ? 0.00000000000000001 : freq;
      pitch = NoteUtils.frequencyToPitch(this.freq);
    }
  }

  public int getPitch() {
    return pitch < (REST + 2) ? REST : pitch;
  }

  public void setPitch(int pitch) {
    if (pitch == REST) {
      this.pitch = REST;
      this.freq = REST;
    } else {
      this.pitch = (pitch < 0) ? 0 : ((pitch > 127) ? 127 : pitch);
      freq = NoteUtils.pitchToFrequency(this.pitch);
    }
  }


}
