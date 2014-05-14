package eu.ttbox.ecard.model;


import com.jaccal.util.NumUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import eu.ttbox.ecard.util.ByteHelper;
import eu.ttbox.ecard.util.TLVParser;

public class SelectApplication {

    public byte[] pdol = null;

    public byte[] generatePdolRequestData() {
        byte[] result = null;
        if (this.pdol != null) {
            ArrayList<RecvTag> parsedPdol = TLVParser.parseDataObjectList(this.pdol);
            int pdolSize = 0;
            for (RecvTag recvTag : parsedPdol) {
                pdolSize += recvTag.valueSize;
            }
            // Create result
            byte[] dest = new byte[pdolSize];
            int index = 0;
            for (RecvTag tag : parsedPdol) {
                System.out.println("generatePdolRequestData ===>  Request " + tag);
                // Copy Key
                String key = tag.getKeyHex2String();
                // http://www.eftlab.co.uk/index.php/site-map/knowledge-base/145-emv-nfc-tags
                //  PDOL (4 + 6 + 6 + 2 + 5 + 2 + 3 + 1 + 4)
                // 80 A8 00 00 23 83 ==> 21
                if ("9F66".equals(key)) {
                    // 9F66	 Terminal transaction Qualifiers	 4 octets
                    // binary 32
                     // 32 00 00 00
                    writeRecvTag(dest, index, tag,"32 00 00 00");
                } else if ("9F02".equals(key)) {
                    // 9F02	 Amount, Authorized (Numeric)	 6 octets
                    // n 12 ==> 00 00 00 01 00 00
                    // 00 00 00 00 00 00
                } else if ("9F03".equals(key)) {
                    //9F03	 Amount, Other (Numeric)	 6 octets
                    // n 12 ==> Always '00 00 00 00 00 00'
                    // 00 00 00 00 00 00
                 } else if ("9F1A".equals(key)) {
                    // 9F1A	 Terminal Country Code	 2 octets
                    // n 3 ==> Indicates the country of the terminal, represented according to ISO 3166-1
                    // 02 50
                    writeRecvTag(dest, index, tag,"02 50");
                 } else if ("9505".equals(key)) {
                    // 9505	 Terminal Verification Results	 5 octets
                    // 00 00 00 00 00
                 } else if ("5F2A".equals(key)) {
                    // 5F2A	 Transaction Currency Code	 2 octets
                    // n 3 ==> Indicates the currency code of the transaction according to ISO 4217 ==> 0978
                    // 09 78
                    writeRecvTag(dest, index, tag,"09 78");
                 } else if ("9A".equals(key)) {
                    // 9A   Transaction Date	 3 octets
                    // n 6 (YYMMDD) ==> Local date that the transaction was authorised
                    // 12 12 31
 // TODO                   Calendar now = Calendar.getInstance();
                     writeRecvTag(dest, index, tag, "12 12 31");
                 } else if ("9C".equals(key)) {
                    // 9C   Transaction Type	 1 octet
                    // n 2 ==> Always '00'
                    // indicates the type of financial transaction, represented by the first two digits of the ISO 8583:1993 Processing Code. The actual values to be used for the Transaction Type data element are defined by the relevant payment system.
                    // 00
                } else if ("9F37".equals(key)) {
                    // 9F37	 Unpredictable Number	 4 octets
                    // binary ==> Value to provide variability and uniqueness to the generation of a cryptogram
                    // E4 EC 9E 52 00
                    writeRecvTag(dest, index, tag, "E4 EC 9E 52");
                }
                // Copy Values
                // Rest  00
                index += tag.valueSize;
                System.out.println("generatePdolRequestData ===>  Request " + NumUtil.toHexString(dest) + " : Size of " + dest.length + " ==> hex 0x" + NumUtil.toHexString(new byte[]{(byte) dest.length}));

            }
            result = dest;
        }
        return result;
    }

    private void writeRecvTag( byte[] dest , int index, RecvTag tag, String value) {
        byte[] src = ByteHelper.hex2Byte(value);
        writeRecvTag(dest, index, tag, src);
    }


    private void writeRecvTag( byte[] dest , int index, RecvTag tag, byte[] value) {
        // Check Validity
        int maxSize = tag.valueSize;
        int copySize = value.length;
        if (copySize>maxSize) {
            throw new IllegalArgumentException("Error Copy Tag " + tag + " for the value " +  NumUtil.toHexString(value) );
        }
        // Do Copy
        System.arraycopy(value, 0, dest, index, copySize);
    }

}



