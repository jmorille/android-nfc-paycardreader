package eu.ttbox.ecard.util;

import com.google.common.base.Charsets;
import com.jaccal.util.HexString;
import com.jaccal.util.NumUtil;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;

public class AscciHelperTest {


    @Test
    public void testencodegHex() {
        String fileName = "1PAY.SYS.DDF01";
        byte[] text= AscciHelper.encodeHex(fileName, AscciHelper.STANDARD_DECODE_TABLE_A);
        String texthex  = NumUtil.toStringHex(fileName);

        Assert.assertEquals("315041592E5359532E4444463031", texthex);
        System.out.println("" +fileName+   " ==> " + texthex);
    }

    @Test
    public void testHex2Navigo() {
        String hexString = "2000";
        byte[] data = HexString.parseHexString(hexString);
        String text = AscciHelper.toAscciString(data);

        Assert.assertEquals("1ADDF010", text);
        System.out.println("" +hexString +  " ==> " +text);
    }

    @Test
    public void testHex2AscciV2() {
        String hexString = "315041592E5359532E4444463031";
        byte[] data = HexString.parseHexString(hexString);
        String text = HexString.hexify(data);

        Assert.assertEquals("1PAY.SYS.DDF01", text);
        System.out.println("" +hexString +  " ==> " +text);
    }

    @Test
    public void testHex2Ascci() {
        String hexString = "315041592E5359532E4444463031";
        byte[] data = NumUtil.toStringHex(hexString);
        String text = AscciHelper.toAscciString(data);

        Assert.assertEquals("1PAY.SYS.DDF01", text);
        System.out.println("" +hexString +  " ==> " +text);
    }

    @Test
    public void testAscci2Hex() {
        String fileName = "1PAY.SYS.DDF01";
        byte[] text = AscciHelper.ascciStringToBytes(fileName);
        String texthex = NumUtil.hex2String(text);
        Assert.assertEquals("315041592E5359532E4444463031", texthex);
        System.out.println("" +fileName+   " ==> " + texthex);
    }


}
