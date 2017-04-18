package test.jm.music.data;

import org.testng.annotations.Test;

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

  }


}
