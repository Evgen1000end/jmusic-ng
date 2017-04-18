package test.jm.music.data;

import org.testng.annotations.Test;

import static jm.music.data.NoteUtils.frequencyToPitch;
import static jm.music.data.NoteUtils.getNote;
import static jm.music.data.NoteUtils.pitchToFrequency;
import static jm.music.data.NoteUtils.pitchValue;
import static org.testng.Assert.assertEquals;

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
public class NoteUtilsTest {

  @Test
  public void testFreqToMidiPitch() throws Exception {
    assertEquals(frequencyToPitch(27), 21);
    assertEquals(frequencyToPitch(127), 47);
    assertEquals(frequencyToPitch(227), 58);
    assertEquals(frequencyToPitch(2227), 97);
    assertEquals(frequencyToPitch(5627), 113);
    assertEquals(frequencyToPitch(1127), 85);
    assertEquals(frequencyToPitch(100), 43);
    assertEquals(frequencyToPitch(200), 55);
    assertEquals(frequencyToPitch(500), 71);
    assertEquals(frequencyToPitch(279), 61);
    assertEquals(frequencyToPitch(11272), 125);

  }

  @Test
  public void testMidiPitchToFreq() throws Exception {
    assertEquals(pitchToFrequency(0), 8.1757989156);
    assertEquals(pitchToFrequency(1), 8.6619572180);
    assertEquals(pitchToFrequency(2), 9.1770239974);
    assertEquals(pitchToFrequency(3), 9.7227182413);
    assertEquals(pitchToFrequency(4), 10.3008611535);
    assertEquals(pitchToFrequency(5), 10.9133822323);
    assertEquals(pitchToFrequency(6), 11.5623257097);
    assertEquals(pitchToFrequency(7), 12.2498573744);
    assertEquals(pitchToFrequency(8), 12.9782717994);
    assertEquals(pitchToFrequency(9), 13.7500000000);
    assertEquals(pitchToFrequency(10), 14.5676175474);
    assertEquals(pitchToFrequency(11), 15.4338531643);
    assertEquals(pitchToFrequency(12), 16.3515978313);
    assertEquals(pitchToFrequency(13), 17.3239144361);
    assertEquals(pitchToFrequency(14), 18.3540479948);
    assertEquals(pitchToFrequency(15), 19.4454364826);
    assertEquals(pitchToFrequency(16), 20.6017223071);

  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void convertToFreqShouldThrowExceptionIfPitchLessThatShouldBe() {
    pitchToFrequency(-1);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void convertToFreqShouldThrowExceptionIfPitchGreaterThatShouldBe() {
    pitchToFrequency(200);
  }

  @Test
  public void getNoteTest() {
    assertEquals(getNote(24), getNote(36));
    assertEquals(getNote(24), "C");
    assertEquals(getNote(25), "C#");
    assertEquals(getNote(26), "D");
    assertEquals(getNote(27), "Eb");
    assertEquals(getNote(28), "E");
    assertEquals(getNote(29), "F");
    assertEquals(getNote(30), "F#");
    assertEquals(getNote(31), "G");
    assertEquals(getNote(32), "Ab");
    assertEquals(getNote(33), "A");
    assertEquals(getNote(34), "Bb");
    assertEquals(getNote(35), "B");
  }

  @Test
  public void pitchValueTest() {
    assertEquals(pitchValue("C"), 60);
    assertEquals(pitchValue("C#"), 61);
    assertEquals(pitchValue("D"), 62);
    assertEquals(pitchValue("Eb"), 63);
    assertEquals(pitchValue("E"), 64);
    assertEquals(pitchValue("F"), 65);
    assertEquals(pitchValue("F#"), 66);
    assertEquals(pitchValue("G"), 67);
    assertEquals(pitchValue("Ab"), 68);
    assertEquals(pitchValue("A"), 69);
    assertEquals(pitchValue("Bb"), 70);
    assertEquals(pitchValue("B"), 71);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void invalidPitchValue() {
    pitchValue("BAR");
  }
}
