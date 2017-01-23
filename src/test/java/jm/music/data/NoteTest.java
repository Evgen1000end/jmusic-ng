package jm.music.data;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
public class NoteTest {

  @Test
  public void testFreqToMidiPitch() throws Exception {
    int pitch = Note.freqToMidiPitch(27);

  //  assertEquals(pitch, 64);
  }

  @Test
  public void testMidiPitchToFreq() throws  Exception {
    System.out.println(Note.midiPitchToFreq(1));
    System.out.println(Note.pitchToFreq(1));
  }


}