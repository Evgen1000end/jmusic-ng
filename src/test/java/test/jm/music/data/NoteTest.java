package test.jm.music.data;

import org.testng.annotations.Test;

import jm.music.data.Note;

import static jm.JMC.A4;
import static jm.JMC.AF4;
import static jm.JMC.B4;
import static jm.JMC.BF4;
import static jm.JMC.C4;
import static jm.JMC.CS4;
import static jm.JMC.D4;
import static jm.JMC.E4;
import static jm.JMC.EF4;
import static jm.JMC.F4;
import static jm.JMC.FS4;
import static jm.JMC.G4;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
public class NoteTest {

  @Test
  public void defaultNoteInitTest() {
    Note note = Note.defaultNote();
    assertEquals(note.getDuration(), Note.DEFAULT_DURATION);
    assertEquals(note.getDynamic(), Note.DEFAULT_DYNAMIC);
    assertEquals(note.getPitch(), Note.DEFAULT_PITCH);
    assertEquals(note.getRhythm(), Note.DEFAULT_RHYTHM);
    assertEquals(note.getSampleStartTime(), Note.DEFAULT_SAMPLE_START_TIME);
    assertEquals(note.getPan(), Note.DEFAULT_PAN);
    assertEquals(note.getOffset(), Note.DEFAULT_OFFSET);
    assertEquals(note.getNote(), "C");
    assertEquals(note.getFrequency(), 261.6255653006);
    assertEquals(note.getPhrase(), null);
  }

  @Test
  public void createNoteWithOffsetTest() {
    Note note = Note.newBuilder()
      .offset(12.0)
      .build();
    assertEquals(note.getDuration(), Note.DEFAULT_DURATION);
    assertEquals(note.getDynamic(), Note.DEFAULT_DYNAMIC);
    assertEquals(note.getPitch(), Note.DEFAULT_PITCH);
    assertEquals(note.getRhythm(), Note.DEFAULT_RHYTHM);
    assertEquals(note.getSampleStartTime(), Note.DEFAULT_SAMPLE_START_TIME);
    assertEquals(note.getPan(), Note.DEFAULT_PAN);
    assertEquals(note.getOffset(), 12.0);
    assertEquals(note.getNote(), "C");
    assertEquals(note.getFrequency(), 261.6255653006);
    assertEquals(note.getPhrase(), null);
  }

  @Test
  public void createNoteWithOffsetAndPanTest() {
    Note note = Note.newBuilder()
      .offset(12.0)
      .pan(2.0)
      .build();
    assertEquals(note.getDuration(), Note.DEFAULT_DURATION);
    assertEquals(note.getDynamic(), Note.DEFAULT_DYNAMIC);
    assertEquals(note.getPitch(), Note.DEFAULT_PITCH);
    assertEquals(note.getRhythm(), Note.DEFAULT_RHYTHM);
    assertEquals(note.getSampleStartTime(), Note.DEFAULT_SAMPLE_START_TIME);
    assertEquals(note.getPan(), 2.0);
    assertEquals(note.getOffset(), 12.0);
    assertEquals(note.getNote(), "C");
    assertEquals(note.getFrequency(), 261.6255653006);
    assertEquals(note.getPhrase(), null);
  }

  @Test
  public void createNoteWithDynamicTest() {
    Note note = Note.newBuilder()
      .offset(12.0)
      .pan(2.0)
      .dynamic(90)
      .build();
    assertEquals(note.getDuration(), Note.DEFAULT_DURATION);
    assertEquals(note.getDynamic(), 90);
    assertEquals(note.getPitch(), Note.DEFAULT_PITCH);
    assertEquals(note.getRhythm(), Note.DEFAULT_RHYTHM);
    assertEquals(note.getSampleStartTime(), Note.DEFAULT_SAMPLE_START_TIME);
    assertEquals(note.getPan(), 2.0);
    assertEquals(note.getOffset(), 12.0);
    assertEquals(note.getNote(), "C");
    assertEquals(note.getFrequency(), 261.6255653006);
    assertEquals(note.getPhrase(), null);
  }

  @Test
  public void createNoteDurationAndRhythmTest() {
    Note note = Note.newBuilder()
      .offset(12.0)
      .pan(2.0)
      .dynamic(90)
      .duration(2.0)
      .rhythm(2.6)
      .build();
    assertEquals(note.getDuration(), 2.0);
    assertEquals(note.getDynamic(), 90);
    assertEquals(note.getPitch(), Note.DEFAULT_PITCH);
    assertEquals(note.getRhythm(), 2.6);
    assertEquals(note.getSampleStartTime(), Note.DEFAULT_SAMPLE_START_TIME);
    assertEquals(note.getPan(), 2.0);
    assertEquals(note.getOffset(), 12.0);
    assertEquals(note.getNote(), "C");
    assertEquals(note.getFrequency(), 261.6255653006);
    assertEquals(note.getPhrase(), null);
  }

  @Test
  public void createFactorDurationTest() {
    Note note = Note.newBuilder()
      .offset(12.0)
      .pan(2.0)
      .dynamic(90)
      .duration(2.0)
      .rhythm(3.0)
      .factorDuration(true)
      .build();
    assertEquals(note.getDuration(), 2.7);
    assertEquals(note.getDynamic(), 90);
    assertEquals(note.getPitch(), Note.DEFAULT_PITCH);
    assertEquals(note.getRhythm(), 3.0);
    assertEquals(note.getSampleStartTime(), Note.DEFAULT_SAMPLE_START_TIME);
    assertEquals(note.getPan(), 2.0);
    assertEquals(note.getOffset(), 12.0);
    assertEquals(note.getNote(), "C");
    assertEquals(note.getFrequency(), 261.6255653006);
    assertEquals(note.getPhrase(), null);
  }


  @Test
  public void createPitchTest() {
    Note note = Note.newBuilder()
      .offset(12.0)
      .pan(2.0)
      .dynamic(90)
      .duration(2.0)
      .rhythm(2.6)
      .pitch(22)
      .build();
    assertEquals(note.getDuration(), 2.0);
    assertEquals(note.getDynamic(), 90);
    assertEquals(note.getPitch(), 22);
    assertEquals(note.getRhythm(), 2.6);
    assertEquals(note.getSampleStartTime(), Note.DEFAULT_SAMPLE_START_TIME);
    assertEquals(note.getPan(), 2.0);
    assertEquals(note.getOffset(), 12.0);
    assertEquals(note.getNote(), "Bb");
    assertEquals(note.getFrequency(), 29.1352350949);
    assertEquals(note.getPhrase(), null);
  }

  @Test
  public void createFrequencyTest() {
    Note note = Note.newBuilder()
      .offset(12.0)
      .pan(2.0)
      .dynamic(90)
      .duration(2.0)
      .rhythm(2.6)
      .frequency(415.3046975799)
      .build();
    assertEquals(note.getDuration(), 2.0);
    assertEquals(note.getDynamic(), 90);
    assertEquals(note.getPitch(), 68);
    assertEquals(note.getRhythm(), 2.6);
    assertEquals(note.getSampleStartTime(), Note.DEFAULT_SAMPLE_START_TIME);
    assertEquals(note.getPan(), 2.0);
    assertEquals(note.getOffset(), 12.0);
    assertEquals(note.getNote(), "Ab");
    assertEquals(note.getFrequency(), 415.3046975799);
    assertEquals(note.getPhrase(), null);
  }

  @Test
  public void getPitchStringTest() {
    Note note = Note.newBuilder().build();
    assertEquals(note.getNote(), "C");
    Note note1 = Note.newBuilder().pitch(61).build();
    assertEquals(note1.getNote(), "C#");
    Note note2 = Note.newBuilder().pitch(62).build();
    assertEquals(note2.getNote(), "D");
    Note note3 = Note.newBuilder().pitch(63).build();
    assertEquals(note3.getNote(), "Eb");
    Note note4 = Note.newBuilder().pitch(64).build();
    assertEquals(note4.getNote(), "E");
    Note note5 = Note.newBuilder().pitch(65).build();
    assertEquals(note5.getNote(), "F");
    Note note6 = Note.newBuilder().pitch(66).build();
    assertEquals(note6.getNote(), "F#");
    Note note7 = Note.newBuilder().pitch(67).build();
    assertEquals(note7.getNote(), "G");
    Note note8 = Note.newBuilder().pitch(68).build();
    assertEquals(note8.getNote(), "Ab");
    Note note9 = Note.newBuilder().pitch(69).build();
    assertEquals(note9.getNote(), "A");
    Note note10 = Note.newBuilder().pitch(70).build();
    assertEquals(note10.getNote(), "Bb");
    Note note11 = Note.newBuilder().pitch(71).build();
    assertEquals(note11.getNote(), "B");
  }

  @Test
  public void isRestTest() {
    Note rest = Note.newBuilder().rest().build();
    assertTrue(rest.isRest());
    Note notRest = Note.newBuilder().pitch(12).build();
    assertFalse(notRest.isRest());
  }

  @Test
  public void isSharpTest() {
    assertTrue(Note.newBuilder().pitch(CS4).build().isSharp());
    assertTrue(Note.newBuilder().pitch(FS4).build().isSharp());
  }

  @Test
  public void isFlatTest() {
    assertTrue(Note.newBuilder().pitch(EF4).build().isFlat());
    assertTrue(Note.newBuilder().pitch(BF4).build().isFlat());
    assertTrue(Note.newBuilder().pitch(AF4).build().isFlat());
  }

  @Test
  public void isNaturalTest() {
    assertTrue(Note.newBuilder().pitch(A4).build().isNatural());
    assertTrue(Note.newBuilder().pitch(B4).build().isNatural());
    assertTrue(Note.newBuilder().pitch(C4).build().isNatural());
    assertTrue(Note.newBuilder().pitch(E4).build().isNatural());
    assertTrue(Note.newBuilder().pitch(F4).build().isNatural());
    assertTrue(Note.newBuilder().pitch(G4).build().isNatural());
    assertTrue(Note.newBuilder().pitch(D4).build().isNatural());
  }

  @Test
  public void copyTest() {
    Note note = Note.defaultNote();
    Note noteRef = note;
    note.setPitch(100);
    assertEquals(note.getPitch(), noteRef.getPitch());

    Note copy = note.copy();
    note.setPitch(10);
    assertEquals(note.getPitch(), 10);
    assertEquals(copy.getPitch(), 100);
  }

  @Test
  public void nextNoteTest() {
    Note note = Note.defaultNote();
    Note nextNote = note.nextNote();
    assertEquals(nextNote.getPitch(), 61);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void nextNoteOnRestTest() {
    Note note = Note.newBuilder().rest().build();
    note.nextNote();
  }
}
