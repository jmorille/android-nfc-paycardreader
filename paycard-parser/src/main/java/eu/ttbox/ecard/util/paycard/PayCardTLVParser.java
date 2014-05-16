package eu.ttbox.ecard.util.paycard;

import com.jaccal.CardResponse;

import java.util.HashMap;
import java.util.Map;

import eu.ttbox.ecard.model.RecvTag;
import eu.ttbox.ecard.util.TLVParser;

public class PayCardTLVParser {


    private HashMap<RecvTag, byte[]> parsed;


    // ===========================================================
    // Constructor
    // ===========================================================

    public PayCardTLVParser(CardResponse card) {
        this.parsed = parsePayCardTVLInDept(card.getData());
    }

    public PayCardTLVParser(byte[] data) {
        this.parsed = parsePayCardTVLInDept(data);
    }


    // ===========================================================
    // Accessors
    // ===========================================================

    public HashMap<RecvTag, byte[]> getParsed() {
        return parsed;
    }


    // ===========================================================
    // TLV Accessors
    // ===========================================================

    public byte[] getTlvValue(String key) {
        return TLVParser.getTlvValue(parsed, key);
    }

    public byte[] getTlvValue(byte[] key) {
        return TLVParser.getTlvValue(parsed, key);
    }

    public byte[] getTlvValue(Emv41Enum keyMap) {
        return TLVParser.getTlvValue(parsed, keyMap);
    }

    public byte[] getTlvValue(RecvTag keyMap) {
        return TLVParser.getTlvValue(parsed, keyMap);
    }


    // ===========================================================
    // Static
    // ===========================================================


    public static HashMap<RecvTag, byte[]> parsePayCardTVLInDept(CardResponse card) {
        byte[] tlv = card.getData();
        return parsePayCardTVLInDept(tlv, null, null);
    }

    public static HashMap<RecvTag, byte[]> parsePayCardTVLInDept(byte[] tlv) {
        return parsePayCardTVLInDept(tlv, null, null);
    }

    private static HashMap<RecvTag, byte[]> parsePayCardTVLInDept(byte[] tlv, RecvTag parentKey, HashMap<RecvTag, byte[]> presult) {
        HashMap<RecvTag, byte[]> result = presult == null ? new HashMap<RecvTag, byte[]>() : presult;

        HashMap<RecvTag, byte[]> one = TLVParser.parseTVL(tlv, null);
        for (Map.Entry<RecvTag, byte[]> entry : one.entrySet()) {
            RecvTag key = entry.getKey();
            byte[] value = entry.getValue();
            // Parent
            key.parentKey = parentKey;
            // Search type
            Emv41Enum emv = Emv41Enum.getByTag(key);
            if (emv == null) {
                // Not found add
                result.put(key, value);
            } else {
                switch (emv.type) {
                    case TLV: {
                        HashMap<RecvTag, byte[]> sub = parsePayCardTVLInDept(value, key, result);
                        key.addAllChildKey(sub.keySet());
                        result.putAll(sub);
                    }
                    break;
                    default: {
                        result.put(key, value);
                    }
                }
            }
        }
        return result;
    }


    // ===========================================================
    // Other
    // ===========================================================


}
