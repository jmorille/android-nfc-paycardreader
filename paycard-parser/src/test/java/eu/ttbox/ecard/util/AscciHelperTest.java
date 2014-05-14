package eu.ttbox.ecard.util;

import com.jaccal.util.NumUtil;

import org.junit.Assert;
import org.junit.Test;

public class AscciHelperTest {


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

    @Test
    public void testToStringHex() {
        String fileName = "1PAY.SYS.DDF01";
        byte[] text = NumUtil.toStringHex(fileName);
        String texthex = NumUtil.hex2String(text);
        Assert.assertEquals("315041592E5359532E4444463031", texthex);
        System.out.println("" +fileName+   " ==> " + texthex);
    }
}
