package eu.ttbox.ecard.util;


import java.io.UnsupportedEncodingException;

public class AscciHelper {
    //

    public static String toAscciString(byte[] bytes) {
        if (bytes==null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            char[] result = Character.toChars(b);
            sb.append(result);

        }
        return sb.toString();
    }

    public static byte[] ascciStringToBytes(String ascci) {
        byte[] bytes = new byte[0];
        try {
            bytes = ascci.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return bytes;

    }
}
