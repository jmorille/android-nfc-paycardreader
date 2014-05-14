package eu.ttbox.ecard.util.paycard;


import com.jaccal.util.NumUtil;

import eu.ttbox.ecard.util.AscciHelper;

public enum Emv41TypeEnum {

    UNNKOWN,
    TLV,
    STRING(new Emv41TypeToString() {
        public String toString( byte[] value) {
            return AscciHelper.toAscciString(value);
        }
    });

    Emv41TypeEnum() {
        this(null);
    }

    Emv41TypeEnum(Emv41TypeToString typeToString) {
        this.typeToString = typeToString;
    }

    Emv41TypeToString typeToString;

    public String toString(byte[] value) {
        if (typeToString!=null) {
           try {
               return typeToString.toString(value);
           } catch (Exception e) {
               e.printStackTrace();
               return "Error in " + name() + " toString of : " +  NumUtil.toHexString(value);
           }
        }
        return NumUtil.toHexString(value);
    }

}
