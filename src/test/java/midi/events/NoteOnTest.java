package midi.events;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import jm.midi.event.NoteOn;
import jm.util.ConvertUtils;

import static jm.util.ConvertUtils.*;
import static org.testng.Assert.*;

/**
 * @author evgeny.demkin@moex.com
 */
public class NoteOnTest {

  @Test
  public void defaultNoteAsBytesTest()  throws Exception {
    NoteOn noteOn = new NoteOn();

    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(stream);

    noteOn.write(dataOutputStream);

    byte[] bytes = stream.toByteArray();

    assertEquals(bytes.length, 4);
    assertEquals(byteArrayToString(bytes), "00900000");
  }


}
