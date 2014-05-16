package eu.ttbox.io7816;


import java.util.HashMap;

import eu.ttbox.ecard.model.RecvTag;
import eu.ttbox.ecard.util.TLVParser;
import eu.ttbox.ecard.util.paycard.Emv41Enum;

public class PseDirectory {

    public String dfName;

    public String lang;

    public  int fsi;

    public final HashMap<RecvTag, byte[]> parsedRecv;

    public PseDirectory(HashMap<RecvTag, byte[]> parsedRecv) {
        this.parsedRecv = parsedRecv;
    }

    public byte[] getAid() {
        return  TLVParser.getTlvValue(parsedRecv, Emv41Enum.DF_ADF_NAME);
    }

    public Byte  getSfi() {
        Byte sfiByte = null;
        byte[] fciSfi =  TLVParser.getTlvValue(parsedRecv, Emv41Enum.DF_FCI_SFI);
        if (fciSfi!=null) {
            int sfi = fciSfi[0];
             sfiByte = (byte) ((fsi << 3) | 4);
        }
        return sfiByte;
    }

    @Override
    public String toString() {
        return "PseDirectory{" +
                "dfName='" + dfName + '\'' +
                ", lang='" + lang + '\'' +
                ", fsi=" + fsi +
                '}';
    }
}
