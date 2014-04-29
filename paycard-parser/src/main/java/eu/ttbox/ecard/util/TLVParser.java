package eu.ttbox.ecard.util;

import com.jaccal.util.NumUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

//http://stackoverflow.com/questions/11473974/is-there-a-java-parser-for-ber-tlv
public class TLVParser {

    public static byte[] getData(byte[] recv) {
        int recvSize = recv.length;
        if (recvSize >= 2) {
            return Arrays.copyOfRange(recv, 0, recvSize - 2);
        }
        return new byte[0];
    }


    public static HashMap<ByteBuffer, byte[]> parseTVL(byte[] tlv) {
        HashMap<ByteBuffer, byte[]> result = new HashMap<ByteBuffer, byte[]>();
        return parseTVL(tlv, result);
    }

    // http://stackoverflow.com/questions/11473974/is-there-a-java-parser-for-ber-tlv
    public static HashMap<ByteBuffer, byte[]> parseTVL(byte[] tlv, HashMap<ByteBuffer, byte[]> result) {
        int tlvSize = tlv != null ? tlv.length : 0;
        if (tlvSize % 2 != 0) {
  //          throw new RuntimeException("Invalid tlv, null or odd length");
        }
        for (int i = 0; i < tlvSize; ) {
            byte[] key = new byte[]{tlv[i++]};
            if ((key[0] & 0x1F) == 0x1F) {
                key = new byte[]{key[0], tlv[i++]};
            }
            byte len = tlv[i++];
            int length = len;
            byte[] val = Arrays.copyOfRange(tlv, i, i = i + length);
            System.out.println("parseTVL key " + NumUtil.hex2String(key) + "("+ length + ") ==> " + NumUtil.toHexString(val));
            result.put(ByteBuffer.wrap(key), val);
        }

        return result;
    }

    public static byte[] getTlvValue(HashMap<ByteBuffer, byte[]> parsed, String key) {
        return getTlvValue(parsed, NumUtil.toStringHex(key));
    }
    public static byte[] getTlvValue(HashMap<ByteBuffer, byte[]> parsed, byte[] key) {
        ByteBuffer keyMap = ByteBuffer.wrap(key);
        return parsed.get(keyMap);

    }

}
