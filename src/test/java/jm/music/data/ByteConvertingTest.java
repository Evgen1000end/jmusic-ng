package jm.music.data;

import static jm.util.ConvertUtils.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
public class ByteConvertingTest {

  @Test
  public void intToByteArrayTest(){
    assertEquals(intToByteArray(1), new byte[]{0,0,0,1});
    assertEquals(intToByteArray(Integer.MAX_VALUE), new byte[]{0x7F,(byte)0xFF,(byte)0xFF,(byte)0xFF});
  }

  @Test
  public void byteArrayToStringTest(){
    assertEquals(byteArrayToString(new byte[]{0x1A,0x2B,0x00, 0x12}), "1A2B0012");
  }

  @Test
  public void intToByteStringWithFormat(){
    assertEquals(intToByteArrayFormat(1, "0x%02X"), "ds");
  }
}