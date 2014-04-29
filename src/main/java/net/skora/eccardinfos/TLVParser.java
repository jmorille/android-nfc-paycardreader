package net.skora.eccardinfos;

import java.util.HashMap;

//http://stackoverflow.com/questions/11473974/is-there-a-java-parser-for-ber-tlv
public class TLVParser {

    public static HashMap<Byte, byte[]> parseTVL(byte[] tlv ) {
        HashMap<Byte, byte[]> result = new HashMap<Byte, byte[]>();
        return parseTVL(tlv, result);
    }

    public static HashMap<Byte, byte[]> parseTVL(byte[] tlv,  HashMap<Byte, byte[]> result ) {
        int tlvSize = tlv!=null ? tlv.length : 0;
        if (  tlvSize %2!=0) {
            throw new RuntimeException("Invalid tlv, null or odd length");
        }
        for (int i=0; i<tlvSize ; ) {
            byte key = tlv[i=i++];
            byte length = tlv[i=i++];


        }



        return result;
    }

}
