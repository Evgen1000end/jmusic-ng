package test.jm.music.data;

import static org.testng.Assert.*;

import jm.music.data.Note;
import jm.music.data.PitchType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
public class NoteTest {

  @Test
  public void defaultNoteInitTest() {
    Note note = Note.newBuilder().build();
    assertEquals(note.getDuration(), Note.DEFAULT_DURATION);
    assertEquals(note.getDynamic(),Note.DEFAULT_DYNAMIC);
    assertEquals(note.getPitch(),Note.DEFAULT_PITCH);
    assertEquals(note.getPitchValue(),0);
    assertEquals(note.getRhythmValue(), Note.DEFAULT_RHYTHM_VALUE);
    assertEquals(note.getSampleStartTime(), Note.DEFAULT_SAMPLE_START_TIME);
    assertEquals(note.getPitchType(), PitchType.MIDI_PITCH);
    assertEquals(note.getPan(), Note.DEFAULT_PAN);
    assertEquals(note.getOffset(), Note.DEFAULT_OFFSET);
    assertEquals(note.getNote(), "C");
    assertEquals(note.getFrequency(), 261.6255653006);
    assertEquals(note.getMyPhrase(),null);
  }

  @Test
  public void createNoteWithOffsetTest() {
    Note note = Note.newBuilder()
        .offset(12.0)
        .build();
    assertEquals(note.getDuration(), Note.DEFAULT_DURATION);
    assertEquals(note.getDynamic(),Note.DEFAULT_DYNAMIC);
    assertEquals(note.getPitch(),Note.DEFAULT_PITCH);
    assertEquals(note.getPitchValue(),0);
    assertEquals(note.getRhythmValue(), Note.DEFAULT_RHYTHM_VALUE);
    assertEquals(note.getSampleStartTime(), Note.DEFAULT_SAMPLE_START_TIME);
    assertEquals(note.getPitchType(), PitchType.MIDI_PITCH);
    assertEquals(note.getPan(), Note.DEFAULT_PAN);
    assertEquals(note.getOffset(), 12.0);
    assertEquals(note.getNote(), "C");
    assertEquals(note.getFrequency(), 261.6255653006);
    assertEquals(note.getMyPhrase(),null);
  }

  @Test
  public void createNoteWithOffsetAndPanTest() {
    Note note = Note.newBuilder()
        .offset(12.0)
        .pan(2.0)
        .build();
    assertEquals(note.getDuration(), Note.DEFAULT_DURATION);
    assertEquals(note.getDynamic(),Note.DEFAULT_DYNAMIC);
    assertEquals(note.getPitch(),Note.DEFAULT_PITCH);
    assertEquals(note.getPitchValue(),0);
    assertEquals(note.getRhythmValue(), Note.DEFAULT_RHYTHM_VALUE);
    assertEquals(note.getSampleStartTime(), Note.DEFAULT_SAMPLE_START_TIME);
    assertEquals(note.getPitchType(), PitchType.MIDI_PITCH);
    assertEquals(note.getPan(), 2.0);
    assertEquals(note.getOffset(), 12.0);
    assertEquals(note.getNote(), "C");
    assertEquals(note.getFrequency(), 261.6255653006);
    assertEquals(note.getMyPhrase(),null);
  }

  @Test
  public void getPitchStringTest() {
    Note note = Note.newBuilder().build();
    assertEquals(note.getNote(), "C");
  }



}
