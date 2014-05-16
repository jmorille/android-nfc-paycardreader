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
public enum Emv41TagEnum {

    CARD_Issuer_Identification_Number_IIN("42","Issuer Identification Number (IIN)"
            ,"The number that identifies the major industry and the card issuer and that forms the first part of the Primary Account Number (PAN)"
            ,"Card","n 6","'BF0C' or '73'","3","3"),
    CARD_Application_Identifier_ADF_Name("4F","Application Identifier (ADF Name)","The ADF Name identifies the application as described in [ISO 7816-5]. The AID is made up of the Registered Application Provider Identifier (RID) and the Proprietary Identifier Extension (PIX).","Card","binary 40-128","'61'","5","16");




    public final RecvTag tag;

    //public final Emv41TypeEnum type;
    public final String name;


    Emv41TagEnum(String tag, String name, String Description, String Source, String Format, String Template, String min, String max) {
        this.tag = new RecvTag(tag);
        this.name = name;
    }

//    public String toString( byte[] value) {
//        return type.toString(value);
//    }


    private static   HashMap<RecvTag, Emv41TagEnum> byTag;

    static {
        HashMap<RecvTag, Emv41TagEnum> localByTag = new HashMap<RecvTag, Emv41TagEnum>();
        for (Emv41TagEnum emv : Emv41TagEnum.values()) {
            localByTag.put(emv.tag, emv);
        }
        byTag = localByTag;
    }

    public static Emv41TagEnum getByTag(RecvTag tag) {
        return byTag.get(tag);
    }

}
