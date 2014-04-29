/*
 * Copyright (c) 2005 Chang Sau Sheong, Thomas Tarpin-Lyonnet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jaccal.util;

/**
 * A utility class mainly used for display of bytes to string and vice versa
 *
 * @author Chang Sau Sheong
 */
public class NumUtil {

  /**
   * Convert a byte array into its hexadecimal string representation.
   * A space character is added in between each byte value.
   * Example: byte [] b = {0x00,0x11,0x22,0xAA}, the returned String will be "00 11 22 AA"
   * @param b the byte array to convert into a String
   * @return The String representation of the byte array
   */  
  public static String toHexString(byte[] b) {
      if (b ==null ) {
          return null;
      }
    StringBuffer sb = new StringBuffer(b.length * 2);
    for (int i = 0; i < b.length; i++) {
      // look up high nibble char
      sb.append(hexChar[(b[i] & 0xf0) >>> 4]);

      // look up low nibble char
      sb.append(hexChar[b[i] & 0x0f]);

      sb.append(" ");
    }
    return sb.toString().toUpperCase().trim();
  }

  /**
   * Convert a byte array into its hexadecimal string representation.
   * No space character is added in between each byte value.
   * Example: byte [] b = {0x00,0x11,0x22,0xAA}, the returned String will be "001122AA"
   * @param b
   * @return  The hexadecimal representation of the byte array
   */
  public static String hex2String(byte[] b) {
      StringBuffer sb = new StringBuffer(b.length * 2);
      for (int i = 0; i < b.length; i++) {
        // look up high nibble char
        sb.append(hexChar[(b[i] & 0xf0) >>> 4]);

        // look up low nibble char
        sb.append(hexChar[b[i] & 0x0f]);
      }
      return sb.toString().toUpperCase();
    }
  
  /**
   * Convert a byte array into its hexadecimal string representation.
   * No space character is added in between each byte value.
   * Example: byte b = 0xAA, the returned String will be "AA"
   * @param b
   * @return  The hexadecimal representation of the byte array
   */
  public static String hex2String(byte b) {
	  StringBuffer sb = new StringBuffer(2);
	  
	  // look up high nibble char
	  sb.append(hexChar[(b & 0xf0) >>> 4]);
	  // look up low nibble char
	  sb.append(hexChar[b & 0x0f]);
	  
	  return sb.toString().toUpperCase();
  }
  
   // table to convert a nibble to a hex char.
   private static char[] hexChar = {
    '0', '1', '2', '3',
    '4', '5', '6', '7',
    '8', '9', 'a', 'b',
    'c', 'd', 'e', 'f'};

  /**
   * Convert from hexadecimal base to decimal base.
   * @param hex
   * @return The integer representation of the hexadecimal number
   */
  public static int HexToDec(int hex) {
    return Integer.valueOf(Integer.toString(hex), 16).intValue();
  }

  private static String hexits = "0123456789abcdef";
  
  /**
   * Convert a String (hexadecimal representation of a bytes) into a byte array.
   * Example: "112233AA", the returned byte[] will be: {0x11,0x22,0x33,0xAA}
   * @param s the String to convert
   * @return The String representation of the bytes
   */
  public static byte[] toStringHex(String s){
    s = s.toLowerCase();
    
    if((s.length() % 2) != 0)
        s = "0" + s;
    
    byte[] b = new byte[s.length() / 2];
    int j = 0;
    int h;
    int nybble = -1;
    for (int i = 0; i < s.length(); ++i) {
        h = hexits.indexOf(s.charAt(i));
        if (h >= 0) {
            if (nybble < 0) {
                nybble = h;
            } else {
                b[j++] = (byte) ((nybble << 4) + h);
                nybble = -1;
            }
        }
    }
    if (nybble >= 0) {
        b[j++] = (byte) (nybble << 4);
    }
    if (j < b.length) {
        byte[] b2 = new byte[j];
        System.arraycopy(b, 0, b2, 0, j);
        b = b2;
    }
    return b;  	
  }
 
  /**
   * Convert an integer into a byte array. The returned byte array is 4 byte length.
   * @param inCode
   * @return A byte array
   */
  public static byte[] int2ByteArray(int inCode){
      byte[] byteArrayVal = new byte[4];
      
      byteArrayVal[0] = (byte)((inCode & 0xFF000000)>>24);
      byteArrayVal[1] = (byte)((inCode & 0x00FF0000)>>16);
      byteArrayVal[2] = (byte)((inCode & 0x0000FF00)>>8);
      byteArrayVal[3] = (byte)((inCode & 0x000000FF));

      return byteArrayVal;
  }  
  
  /**
   * Convert an integer into its hexadecimal String representation.
   * Example: in = 0xAA11EECC, the returned String will be "AA11EECC"
   * @param in
   * @return The String version of the integer
   */
  public static String int2HexString(int in){
      return NumUtil.hex2String(NumUtil.int2ByteArray(in));
  } 
  
  /**
   * Get the unsigned value of a byte.
   * @param b
   * @return  An unsigned integer of the value
   */
  public static int getUnsignedValue(byte b){
      return (int)(b & 0x00FF);
  }
}

