package eu.ttbox.ecard.util;


import com.jaccal.util.NumUtil;
import com.jaccal.util.TLV;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.HashMap;
 import org.junit.Assert;

public class TLVParserTest {

   // @Test
    public void parseTLVTest() {
        byte[] recv = ByteHelper.hex2Byte("6F 1A 84 0E 31 50 41 59 2E 53 59 53 2E 44 44 46 30 31 A5 08 88 01 01 5F 2D 02 65 6E");
    //    TLV tlv = new TLV(recv);
    //  System.out.print(   tlv.valueAsString()  );

        // Parse One
        HashMap<ByteBuffer, byte[]> parseOne = TLVParser.parseTVL(recv);
        ByteBuffer keyOne = ByteBuffer.wrap( new byte[]{(byte)0x6F});
        byte[] valOne = parseOne.get(keyOne);
        Assert.assertTrue(parseOne.containsKey(keyOne));
        Assert.assertEquals(26, valOne.length);

        // Parse Two
        HashMap<ByteBuffer, byte[]> parseTwo= TLVParser.parseTVL(valOne);
        ByteBuffer keyTwo = ByteBuffer.wrap(new byte[]{(byte)0x84});
        byte[] valTow = parseTwo.get(keyTwo);
        System.out.println(  "===> Key " + NumUtil.hex2String(keyTwo.array()) + " : " +  NumUtil.toHexString(valTow));
        Assert.assertTrue(parseTwo.containsKey(keyTwo));
        Assert.assertEquals(14, valTow.length);
     }
    @Test
    public void testParse2() {
        byte[] recv = ByteHelper.hex2Byte("84 0E 31 50 41 59 2E 53 59 53 2E 44 44 46 30 31 A5 0C 88 01 01 5F 2D 02 66 72 9F 11 01 01");
        HashMap<ByteBuffer, byte[]> parseOne = TLVParser.parseTVL(recv);
        ByteBuffer keyOne =  ByteBuffer.wrap(NumUtil.toStringHex("A5")); //ByteBuffer.wrap( new byte[]{(byte)0xA5});
        byte[] valOne = parseOne.get(keyOne);

        System.out.println(  "Key " + NumUtil.hex2String(keyOne.array()) + " = " +  NumUtil.toHexString(valOne));
    }

    //@Test
    public void testKeyDouble() {
        byte[] recv = ByteHelper.hex2Byte("88 01 01 5F 2D 02 65 6E");
        HashMap<ByteBuffer, byte[]> parseOne = TLVParser.parseTVL(recv);

        ByteBuffer keyOne = ByteBuffer.wrap(new byte[]{(byte)0x88});
        byte[] valOne = parseOne.get(keyOne);

        ByteBuffer keyTwo = ByteBuffer.wrap(new byte[]{(byte)0x5F, 0x2D});
        byte[] valTow = parseOne.get(keyTwo);
        String text = AscciHelper.toAscciString(valTow);
        System.out.println(NumUtil.toHexString(valTow) + " = " + text);

        for (ByteBuffer key : parseOne.keySet()) {
            System.out.println(  "===> Map Key " + NumUtil.hex2String(key.array()) + " : " +  NumUtil.toHexString(parseOne.get(key)));
        }
    }

    @Test
    public void testKey() {
//        byte testByte = 0x5f;
        byte testByte = 0x5f;
        if ((testByte & 0x1F) == 0x1F) {
           System.out.println("test 0x1F  ---");
        } else {

        }
    }

}
