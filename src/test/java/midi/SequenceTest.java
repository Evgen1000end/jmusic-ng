package midi;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import jm.midi.MidiParser;
import jm.midi.SMF;
import jm.midi.Track;
import jm.midi.event.Event;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.ConvertUtils;

import static org.testng.Assert.*;

/**
 * @author evgeny.demkin@moex.com
 */
public class SequenceTest {



  @Test
  public void smfOneNoteTest() {
    Score score = new Score();
    score.add(new Part(new Phrase(Note.defaultNote())));
    SMF smf = new SMF();
    MidiParser.scoreToSMF(score, smf);

    assertEquals(smf.getPPQN(), 480);

    assertEquals(smf.getTrackList().size(), 2);
    assertEquals(((Track)smf.getTrackList().get(0)).getEvtList().size(), 4);
    //assertEquals(((Event)((Track)smf.getTrackList().get(0)).getEvtList().get(0)).read(), 4);
    assertEquals(((Track)smf.getTrackList().get(1)).getEvtList().size(), 5);
   // assertEquals(smf.print(););
  }

  @Test
  public void smfToSequenceTest()  throws Exception {
    Score score = new Score();
    score.add(new Part(new Phrase(Note.defaultNote())));
    SMF smf = new SMF();
    smf.clearTracks();

    MidiParser.scoreToSMF(score, smf);

    OutputStream os = new ByteArrayOutputStream();
    smf.write(os);
    Sequence sq = MidiSystem
      .getSequence(new ByteArrayInputStream(((ByteArrayOutputStream) os)
        .toByteArray()));

    assertEquals(sq.getPatchList().length, 0);
    assertEquals(sq.getDivisionType(), 0.0f);
    assertEquals(sq.getMicrosecondLength(), 900000);
    assertEquals(sq.getResolution(), 480);
    assertEquals(sq.getTickLength(), 432L);
    assertEquals(sq.getTracks().length, 2);
    assertEquals(sq.getTracks()[0].ticks(), 0);
    assertEquals(sq.getTracks()[0].size(), 4);
    assertEquals(sq.getTracks()[0].get(0).getTick(), 0);
    assertEquals(sq.getTracks()[0].get(0).getMessage().getLength(), 6);
    assertEquals(sq.getTracks()[0].get(0).getMessage().getStatus(), 255);
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[0].get(0).getMessage().getMessage()), "FF51030F4240");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[0].get(1).getMessage().getMessage()), "FF580404021808");

    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[0].get(2).getMessage().getMessage()), "FF59020000");

    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[0].get(3).getMessage().getMessage()), "FF2F00");

    assertEquals(sq.getTracks()[1].ticks(), 432);
    assertEquals(sq.getTracks()[1].size(), 5);

    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(0).getMessage().getMessage()), "C000");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(1).getMessage().getMessage()), "B00A3F");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(2).getMessage().getMessage()), "903C55");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(3).getMessage().getMessage()), "903C00");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(4).getMessage().getMessage()), "FF2F00");
  }

  @Test
  public void smfToSequenceTwoNotesTest() throws Exception {
    Phrase phrase = new Phrase();
    phrase.addNote(Note.defaultNote());
    phrase.addNote(Note.newBuilder().pitch(61).build());
    Score score = new Score(new Part(phrase));

    SMF smf = new SMF();
    smf.clearTracks();

    MidiParser.scoreToSMF(score, smf);

    OutputStream os = new ByteArrayOutputStream();
    smf.write(os);
    Sequence sq = MidiSystem
      .getSequence(new ByteArrayInputStream(((ByteArrayOutputStream) os)
        .toByteArray()));

    assertEquals(sq.getPatchList().length, 0);
    assertEquals(sq.getDivisionType(), 0.0f);
    assertEquals(sq.getMicrosecondLength(), 1900000);
    assertEquals(sq.getResolution(), 480);
    assertEquals(sq.getTickLength(), 912L);
    assertEquals(sq.getTracks().length, 2);
    assertEquals(sq.getTracks()[0].ticks(), 0);
    assertEquals(sq.getTracks()[0].size(), 4);
    assertEquals(sq.getTracks()[0].get(0).getTick(), 0);
    assertEquals(sq.getTracks()[0].get(0).getMessage().getLength(), 6);
    assertEquals(sq.getTracks()[0].get(0).getMessage().getStatus(), 255);
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[0].get(0).getMessage().getMessage()), "FF51030F4240");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[0].get(1).getMessage().getMessage()), "FF580404021808");

    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[0].get(2).getMessage().getMessage()), "FF59020000");

    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[0].get(3).getMessage().getMessage()), "FF2F00");

    assertEquals(sq.getTracks()[1].ticks(), 912);
    assertEquals(sq.getTracks()[1].size(), 7);

    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(0).getMessage().getMessage()), "C000");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(1).getMessage().getMessage()), "B00A3F");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(2).getMessage().getMessage()), "903C55");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(3).getMessage().getMessage()), "903C00");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(4).getMessage().getMessage()), "903D55");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(5).getMessage().getMessage()), "903D00");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(6).getMessage().getMessage()), "FF2F00");
  }

  @Test
  public void smfTwoPhrasesTest() throws Exception {
    Phrase phrase1 = new Phrase(Note.defaultNote());
    Phrase phrase2 = new Phrase(Note.newBuilder().pitch(61).build());

    Part part = new Part();
    part.add(phrase1);
    part.add(phrase2);

    Score score = new Score(part);

    SMF smf = new SMF();
    smf.clearTracks();

    MidiParser.scoreToSMF(score, smf);

    OutputStream os = new ByteArrayOutputStream();
    smf.write(os);
    Sequence sq = MidiSystem
      .getSequence(new ByteArrayInputStream(((ByteArrayOutputStream) os)
        .toByteArray()));


    assertEquals(sq.getPatchList().length, 0);
    assertEquals(sq.getDivisionType(), 0.0f);
    assertEquals(sq.getMicrosecondLength(), 1900000);
    assertEquals(sq.getResolution(), 480);
    assertEquals(sq.getTickLength(), 912L);
    assertEquals(sq.getTracks().length, 2);
    assertEquals(sq.getTracks()[0].ticks(), 0);
    assertEquals(sq.getTracks()[0].size(), 4);
    assertEquals(sq.getTracks()[0].get(0).getTick(), 0);
    assertEquals(sq.getTracks()[0].get(0).getMessage().getLength(), 6);
    assertEquals(sq.getTracks()[0].get(0).getMessage().getStatus(), 255);
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[0].get(0).getMessage().getMessage()), "FF51030F4240");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[0].get(1).getMessage().getMessage()), "FF580404021808");

    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[0].get(2).getMessage().getMessage()), "FF59020000");

    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[0].get(3).getMessage().getMessage()), "FF2F00");

    assertEquals(sq.getTracks()[1].ticks(), 912);
    assertEquals(sq.getTracks()[1].size(), 8);

    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(0).getMessage().getMessage()), "C000");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(1).getMessage().getMessage()), "B00A3F");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(2).getMessage().getMessage()), "903C55");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(3).getMessage().getMessage()), "903C00");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(4).getMessage().getMessage()), "B00A3F");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(5).getMessage().getMessage()), "903D55");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(6).getMessage().getMessage()), "903D00");
    assertEquals(
      ConvertUtils.byteArrayToString(
        sq.getTracks()[1].get(7).getMessage().getMessage()), "FF2F00");

  }

  @Test
  public void plainMidiTest() throws  Exception {
    Score score = new Score();

    score.add(new Part(new Phrase(Note.defaultNote())));

  }
}
