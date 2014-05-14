package eu.ttbox.ecard.util;


import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

import eu.ttbox.ecard.model.AflRecord;
import eu.ttbox.ecard.model.GetAFL;
import eu.ttbox.ecard.model.RecvTag;

public class ApplicationFileLocatorParserTest {

    @Test
    public void parseSelectPseDirectory() {
        byte[] recv = ByteHelper.hex2Byte("80 0E 7C 00 08 01 01 00 10 01 05 00 18 01 02 01 90 00");
        GetAFL afl =  ApplicationFileLocatorParser.parseAFL(recv);

        Assert.assertEquals(3, afl.records.size());
        AflRecord rc1 = afl.records.get(0);
        AflRecord rc2 = afl.records.get(1);
        AflRecord rc3 = afl.records.get(2);
        // Fsi
        Assert.assertEquals(1, rc1.sfi);
        Assert.assertEquals(2, rc2.sfi);
        Assert.assertEquals(3, rc3.sfi);
        // Record 1
        Assert.assertEquals(1, rc1.recordNumberBegin);
        Assert.assertEquals(1, rc1.recordNumberEnd);
        // Record 2
        Assert.assertEquals(1, rc2.recordNumberBegin);
        Assert.assertEquals(5, rc2.recordNumberEnd);
        // Record 3
        Assert.assertEquals(1, rc3.recordNumberBegin);
        Assert.assertEquals(2, rc3.recordNumberEnd);

    }
}
