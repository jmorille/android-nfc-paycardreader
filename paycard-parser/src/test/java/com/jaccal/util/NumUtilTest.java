package com.jaccal.util;


import org.junit.Assert;
import org.junit.Test;

public class NumUtilTest {

    @Test
    public void testToHexString() {
        byte[] cmd = new byte[]{0x00, (byte) 0xA4, 0x04, 0x00, (byte) 0xA0, 0x00, 0x00, 0x00, 0x04};
        String hex =  NumUtil.toHexString(cmd) ;
        System.out.println(hex);
        Assert.assertEquals("00 A4 04 00 A0 00 00 00 04", hex );
    }
}
