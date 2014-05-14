package eu.ttbox.ecard.util.paycard;


import com.jaccal.util.NumUtil;

import java.util.HashMap;

import eu.ttbox.ecard.model.RecvTag;

public enum Emv41Enum {

    // TODO find ref
    PDOL("9F38", Emv41TypeEnum.UNNKOWN),

    // refer to EMV 4.1 Book 1 - Section 11.3.4
    DF_FCI(new byte[]{(byte) 0x6F}, Emv41TypeEnum.TLV),
    DF_FCI_NAME(new byte[]{(byte) 0x84}, Emv41TypeEnum.STRING),
    DF_FCI_PROPRIETARY(new byte[]{(byte) 0xA5}, Emv41TypeEnum.TLV),
    DF_FCI_SFI(new byte[]{(byte) 0x88}, Emv41TypeEnum.UNNKOWN),
    DF_FCI_LANG(new byte[]{(byte) 0x5F, (byte) 0x2D}, Emv41TypeEnum.UNNKOWN),

    PSE_ENTRY(new byte[]{(byte) 0x61}, Emv41TypeEnum.UNNKOWN),

    // refer to EMV 4.1 Book 1 - Section 12.2.3
    DF_ADF_NAME(new byte[]{(byte) 0x4F}, Emv41TypeEnum.UNNKOWN),
    DF_ADF_LABEL(new byte[]{(byte) 0x50}, Emv41TypeEnum.UNNKOWN),
    DF_ADF_PREFERRED_NAME(new byte[]{(byte) 0x9F, (byte) 0x12}, Emv41TypeEnum.UNNKOWN),
    DF_ADF_PRIORITY(new byte[]{(byte) 0x87}, Emv41TypeEnum.UNNKOWN),

    // refer to EMV 4.1 Book 3 - Annex A - Data Elements Dictionary
    TRACK2_EQUIV_DATA(new byte[]{(byte) 0x57}, Emv41TypeEnum.UNNKOWN),
    CARDHOLDER_NAME(new byte[]{(byte) 0x5F, (byte) 0x20}, Emv41TypeEnum.UNNKOWN);



    public final RecvTag tag;

    public final Emv41TypeEnum type;

    Emv41Enum(String tag, Emv41TypeEnum type) {
        this(NumUtil.toStringHex(tag),  type);
    }


    Emv41Enum(byte[] tag, Emv41TypeEnum type) {
        this.tag = new RecvTag(tag);
        this.type = type;
    }

    public String toString( byte[] value) {
        return type.toString(value);
    }


    private static   HashMap<RecvTag, Emv41Enum> byTag;

    static {
        HashMap<RecvTag, Emv41Enum> localByTag = new HashMap<RecvTag, Emv41Enum>();
        for (Emv41Enum emv : Emv41Enum.values()) {
            localByTag.put(emv.tag, emv);
        }
        byTag = localByTag;
    }

    public static  Emv41Enum getByTag(RecvTag tag) {
        return byTag.get(tag);
    }

}
