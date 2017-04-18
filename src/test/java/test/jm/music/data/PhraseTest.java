package test.jm.music.data;

import org.testng.annotations.Test;

import jm.music.data.Note;
import jm.music.data.Phrase;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
public class PhraseTest {

  @Test
  public void emptyPhraseStateTest() {
    Phrase phrase = new Phrase();
    assertEquals(phrase.getStartTime(), 0.0);
    assertEquals(phrase.getTitle(), "Untitled Phrase");
    assertEquals(phrase.getNoteList().size(), 0);
    assertNull(phrase.getNote(22));
  }


  @Test
  public void phraseWithSeveralNotesTest() {
    Phrase phrase = new Phrase();
    Note note = Note.defaultNote();
    phrase.addNote(note);
    assertEquals(note.getPhrase(), phrase);
    phrase.addNote(Note.newBuilder().pitch(55).rhythm(2.0).build());
    assertEquals(phrase.length(), 2);
    assertEquals(phrase.getNote(0), note);
    assertEquals(phrase.getNote(1).getPitch(), 55);

    phrase.addNote(27, 3.0);
    assertEquals(phrase.getNote(2).getPitch(), 27);
    assertEquals(phrase.getNote(2).getRhythm(), 3.0);


  }
}
