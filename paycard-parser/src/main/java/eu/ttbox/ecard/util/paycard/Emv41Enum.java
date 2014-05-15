package eu.ttbox.ecard.util.paycard;


import com.jaccal.util.NumUtil;

import java.util.HashMap;

import eu.ttbox.ecard.model.RecvTag;

/**
 * http://www.eftlab.co.uk/index.php/site-map/knowledge-base/145-emv-nfc-tags
 *
 * EMV_v4.3_Book_3_Application_Specification_20120607062110791.pdf
 *   Table 33: Data Elements Dictionary
 */
public enum Emv41Enum {

    // TLV
    BIC("5F54",  Emv41TypeEnum.TLV),
    IBAN("5F53",  Emv41TypeEnum.TLV),

    READ_RECORD_Response_Message_Template("70",  Emv41TypeEnum.TLV),
    Response_Message_Template_Format_1("80",  Emv41TypeEnum.TLV),
    Response_Message_Template_Format_2("77",  Emv41TypeEnum.TLV),

    // TODO find ref
    PDOL("9F38", Emv41TypeEnum.UNNKOWN),
    FCI_Issuer_Discretionary_DATA("BF0C", Emv41TypeEnum.TLV),
    KERNEL_IDENTIFIER("9F2A",  Emv41TypeEnum.UNNKOWN),

    // refer to EMV 4.1 Book 1 - Section 11.3.4
    DF_FCI("6F", Emv41TypeEnum.TLV),
    DF_FCI_PROPRIETARY("A5", Emv41TypeEnum.TLV),
    DF_FCI_SFI("88", Emv41TypeEnum.UNNKOWN),
    DF_FCI_NAME("84", Emv41TypeEnum.STRING),
    DF_FCI_LANG("5F2D", Emv41TypeEnum.STRING),

    PSE_ENTRY("61", Emv41TypeEnum.TLV),

    // refer to EMV 4.1 Book 1 - Section 12.2.3
    DF_ADF_NAME("4F", Emv41TypeEnum.UNNKOWN),
    DF_ADF_LABEL("50", Emv41TypeEnum.UNNKOWN),
    DF_ADF_PREFERRED_NAME("9F12", Emv41TypeEnum.UNNKOWN),
    DF_ADF_PRIORITY("87", Emv41TypeEnum.UNNKOWN),

    // refer to EMV 4.1 Book 3 - Annex A - Data Elements Dictionary
    TRACK2_EQUIV_DATA("57", Emv41TypeEnum.UNNKOWN),
    CARDHOLDER_NAME("5F20", Emv41TypeEnum.UNNKOWN);



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
