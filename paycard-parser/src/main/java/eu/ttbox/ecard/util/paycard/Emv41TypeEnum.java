package eu.ttbox.ecard.util.paycard;


import com.jaccal.util.NumUtil;

public enum Emv41TypeEnum {

    UNNKOWN,
    TLV,
    STRING;



    public String toString( byte[] value) {
        return NumUtil.toHexString(value);
    }

}
