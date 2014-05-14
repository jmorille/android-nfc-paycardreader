package eu.ttbox.ecard.util.paycard;

import java.util.HashMap;

import eu.ttbox.ecard.model.RecvTag;
import eu.ttbox.ecard.util.TLVParser;

public class PayCardTLVParser {

    public static HashMap<RecvTag, byte[]> parsePayCardTVLInDept(byte[] tlv) {
        HashMap<RecvTag, byte[]> one =  TLVParser.parseTVL(tlv, null);
        for (Emv41Enum emv : Emv41Enum.values()) {
            byte[] value = one.get(emv.tag);
            if (value!=null) {
                switch (emv.type) {
                    case TLV:  {
                        HashMap<RecvTag, byte[]> sub = parsePayCardTVLInDept(value);
                        one.remove(emv.tag);
                        one.putAll(sub);
                    }
                    case STRING:  {

                    }
                    break;
                    default: {

                    }
                    break;
                }

            }
        }
        return one;
    }
}
