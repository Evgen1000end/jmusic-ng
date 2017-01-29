package jm.util;

import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:evgen1000end@gmail.com">demkinev</a>
 */
public final class ConvertUtils {

  /**
    255 in Deximal
   */
  public static final int MAX_UNSIGNED_BYTE = 0xFF;
  /**
   * 15 in Deximal
   */
  public static final int HEX_15 = 0x0F;

  /**
   @param value - int value for converting in byte array
   @return - byte array
   */
  public static byte[] intToByteArray(final int value) {
    return ByteBuffer.allocate(Integer.BYTES).putInt(value).array();
  }

  /**
   @param value - long value for converting in byte array
   @return - byte array
   */
  public static byte[] longToByteArray(final long value) {
    return ByteBuffer.allocate(Long.BYTES).putLong(value).array();
  }

  /**
   @param value - byte array for converting to String
   @return - String representation of byte array
   */
  public static String byteArrayToString(final byte[] value) {
    char[] hexArray = "0123456789ABCDEF".toCharArray();
    char[] hexChars = new char[value.length * 2];
    for (int j = 0; j < value.length; j++) {
      int v = value[j] & MAX_UNSIGNED_BYTE;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & HEX_15];
    }
    return new String(hexChars);
  }

  public static String intToByteArrayFormat(final int value, final String pattern) {
    StringBuilder builder = new StringBuilder();
    for (byte b : intToByteArray(value)) {
      builder.append(pattern);
    }
    return builder.toString();
  }

  /**
   * Default constructor for preventing creating class instance
   */
  private ConvertUtils() {

  }

}
