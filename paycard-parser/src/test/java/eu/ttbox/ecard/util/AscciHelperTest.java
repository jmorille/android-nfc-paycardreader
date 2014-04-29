package eu.ttbox.ecard.util;

import com.jaccal.util.NumUtil;

import org.junit.Assert;
import org.junit.Test;

public class AscciHelperTest {


    @Test
    public void testHex2Ascci() {
        byte[] data = NumUtil.toStringHex("315041592E5359532E4444463031");
        String text = AscciHelper.toAscciString(data);

        Assert.assertEquals("1PAY.SYS.DDF01", text);
        System.out.println("315041592E5359532E4444463031 ==> " +text);
    }

    @Test
    public void testAscci2Hex() {
        byte[] text = AscciHelper.ascciStringToBytes("1PAY.SYS.DDF01");
        String texthex = NumUtil.hex2String(text);
        Assert.assertEquals("315041592E5359532E4444463031", texthex);
        System.out.println("1PAY.SYS.DDF01 ==> " + texthex);
    }
}
