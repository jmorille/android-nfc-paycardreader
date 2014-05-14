package eu.ttbox.ecard.util.paycard;


import com.jaccal.util.NumUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import eu.ttbox.ecard.model.RecvTag;
import eu.ttbox.ecard.util.ByteHelper;
import eu.ttbox.ecard.util.TLVParser;

public class PayCardTLVParserTest {

    @Test
    public void parseSelectPseDirectory() {
        byte[] recv = ByteHelper.hex2Byte("6F 1E 84 0E 31 50 41 59 2E 53 59 53 2E 44 44 46 30 31 A5 0C 88 01 01 5F 2D 02 66 72 9F 11 01 01 90 00");
        HashMap<RecvTag, byte[]> parsedRecv = PayCardTLVParser.parsePayCardTVLInDept(recv);
        Assert.assertTrue(parsedRecv != null);
       // Assert.assertEquals(2, parsedRecv.size());
        print("parseSelectPseDirectory", parsedRecv);


        Assert.assertTrue(parsedRecv.containsKey(Emv41Enum.DF_FCI.tag));
        Assert.assertTrue(parsedRecv.containsKey(Emv41Enum.DF_FCI_LANG.tag));
        Assert.assertTrue(parsedRecv.containsKey(Emv41Enum.DF_FCI_NAME.tag));
    }


    @Test
    public void parseSelectApplication() {
        byte[] recv = ByteHelper.hex2Byte("6F 53 84 07 A0 00 00 00 42 10 10 A5 48 50 02 43 42 87 01 01 9F 38 18 9F 66 04 9F 02 06 9F 03 06 9F 1A 02 95 05 5F 2A 02 9A 03 9C 01 9F 37 04 5F 2D 02 66 72 9F 11 01 01 9F 12 0E 54 52 41 4E 53 41 43 54 49 4F 4E 20 43 42 BF 0C 09 DF 61 01 03 9F 4D 02 11 32 90 00");
        HashMap<RecvTag, byte[]> parsedRecv = PayCardTLVParser.parsePayCardTVLInDept(recv);
        Assert.assertTrue(parsedRecv != null);
        // Assert.assertEquals(2, parsedRecv.size());
        print("parseSelectApplication", parsedRecv);

    }


    private void print(String prefix,  HashMap<RecvTag, byte[]> parsedRecv) {
        if (parsedRecv==null) {
            return;
        }
        for (Map.Entry<RecvTag, byte[]> entry : parsedRecv.entrySet()){
            RecvTag tag = entry.getKey();
            Emv41Enum emv = Emv41Enum.getByTag(tag);
            String emvLabel = emv==null ? "???" : emv.name() + "()";

            System.out.println( " " +prefix+
                    " ===> Key " + emvLabel +
                    "  " + NumUtil.hex2String(tag.key) + " = " +  NumUtil.toHexString(entry.getValue()));

        }
    }

}
