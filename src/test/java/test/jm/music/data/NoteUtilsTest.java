package test.jm.music.data;

import jm.music.data.NoteUtils;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
public class NoteUtilsTest {

  @Test
  public void testFreqToMidiPitch() throws Exception {
    int pitch = NoteUtils.freqToMidiPitch(27);

    //  assertEquals(pitch, 64);
  }

  @Test
  public void testMidiPitchToFreq() throws Exception {
    System.out.println(NoteUtils.midiPitchToFreq(1));
    System.out.println(NoteUtils.pitchToFreq(1));
  }


}