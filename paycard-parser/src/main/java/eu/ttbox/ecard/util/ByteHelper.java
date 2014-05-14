package eu.ttbox.ecard.util;


public class ByteHelper {

    public static  String byte2Hex(byte[] input) {
        return byte2Hex(input, " ");
    }

    public static  String byte2Hex(byte[] input, String space) {
        StringBuilder result = new StringBuilder();

        for (Byte inputbyte : input) {
            result.append(String.format("%02X" + space, inputbyte));
        }
        return result.toString();
    }

    public static byte[] hex2Byte(String hexstr)  {
        String[] hexbytes = hexstr.split("\\s");
        byte[] bytes = new byte[hexbytes.length];
        for (int i = 0; i < hexbytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexbytes[i], 16);
        }
        return bytes;
    }


}
