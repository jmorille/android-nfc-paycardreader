package eu.ttbox.ecard.model;


import com.jaccal.util.NumUtil;

import java.util.ArrayList;

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
            byte[] trans = new byte[pdolSize];
            int index = 0;
            for (RecvTag tag : parsedPdol) {
                System.out.println("generatePdolRequestData ===>  Request " + tag);
                // Copy Key
                String key = tag.getKeyHex2String();
                // http://www.eftlab.co.uk/index.php/site-map/knowledge-base/145-emv-nfc-tags
                if ("9F66".equals(key)) {
                    // 9F66	 Terminal transaction Qualifiers	 4 octets
                    // binary 32

                    //  PDOL (4 + 6 + 6 + 2 + 5 + 2 + 3 + 1 + 4)
                    // 80 A8 00 00
                    // 23 83 21 32 00 00
                    // 00 00 00 00 00 00
                    // 00 00
                    // 00 00 00 00 00
                    // 02 50
                    // 00 00 00
                    // 00
                    // 00 09 78 12
                    // 12 31 00 E4 EC 9E 52 00

 
                } else if ("9F02".equals(key)) {
                    // 9F02	 Amount, Authorized (Numeric)	 6 octets
                    // n 12 ==> 00 00 00 01 00 00
                 } else if ("9F03".equals(key)) {
                    //9F03	 Amount, Other (Numeric)	 6 octets
                    // n 12 ==> Always '00 00 00 00 00 00'
                 } else if ("9F1A".equals(key)) {
                    // 9F1A	 Terminal Country Code	 2 octets
                    // n 3 ==> Indicates the country of the terminal, represented according to ISO 3166-1
                 } else if ("9505".equals(key)) {
                    // 9505	 Terminal Verification Results	 5 octets
                 } else if ("5F2A".equals(key)) {
                    // 5F2A	 Transaction Currency Code	 2 octets
                    // n 3 ==> Indicates the currency code of the transaction according to ISO 4217 ==> 0978
                 } else if ("9A".equals(key)) {
                    // 9A   Transaction Date	 3 octets
                    // n 6 (YYMMDD) ==> Local date that the transaction was authorised
                 } else if ("9C".equals(key)) {
                    // 9C   Transaction Type	 1 octet
                    // n 2 ==> Always '00'
                    // indicates the type of financial transaction, represented by the first two digits of the ISO 8583:1993 Processing Code. The actual values to be used for the Transaction Type data element are defined by the relevant payment system.

                } else if ("9F37".equals(key)) {
                    // 9F37	 Unpredictable Number	 4 octets
                    // binary ==> Value to provide variability and uniqueness to the generation of a cryptogram
                 }
                // Copy Values
                index += tag.valueSize;
                System.out.println("generatePdolRequestData ===>  Request " + NumUtil.toHexString(trans) + " : Size of " + trans.length + " ==> hex 0x" + NumUtil.toHexString(new byte[]{(byte) trans.length}));

            }
            result = trans;
        }
        return result;
    }
}
