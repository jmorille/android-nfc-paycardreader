package eu.ttbox.ecard.util;


import com.jaccal.util.NumUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import eu.ttbox.ecard.model.AflRecord;
import eu.ttbox.ecard.model.GetAFL;
import eu.ttbox.ecard.model.RecvTag;

public class ApplicationFileLocatorParser {

    public static GetAFL parseAFL(byte[] recv) {
        if (recv==null && recv.length<=2) {
            return null;
        }
        GetAFL getAFL = new GetAFL();
        getAFL.recv = recv;
        // parse Recv
        HashMap<RecvTag, byte[]> parsedRecv = TLVParser.parseTVL(recv);
        byte[] raw =  TLVParser.getTlvValue(parsedRecv, "80");
        int rawSize = raw.length;
        getAFL.featuresSupported = Arrays.copyOfRange(raw, 0, 2);
        getAFL.afls = Arrays.copyOfRange(raw, 2, rawSize);

       // System.out.println( "features Supported = " +  NumUtil.toHexString(getAFL.featuresSupported));
       // System.out.println( "AFL = " +  NumUtil.toHexString(getAFL.afls));
        int aflSize = getAFL.afls.length;
        // Check group 4 bytes
        if (aflSize%4 != 0) {
            throw new RuntimeException("AFL not in group of 4 : " +  NumUtil.toHexString(getAFL.afls));
        }
        // Split in group
        ArrayList<byte[]> aflGroups = new ArrayList<byte[]>();
        for (int i =0; i<aflSize; i=i+4) {
            byte[] group = Arrays.copyOfRange(getAFL.afls, i, i+4);
            aflGroups.add(group);
        }
        // Print Group
        for (byte[] group : aflGroups) {
            System.out.println( "AFL Group = " +  NumUtil.toHexString(group));
            byte sfi = group[0];
            byte recordBegin = group[1];
            byte recordEnd = group[2];
            byte numberRecordForAuthentification = group[3];

            AflRecord record = new AflRecord();
            record.sfi =  (sfi>>3); //The five most significant bits indicate the SFI
            record.recordNumberBegin = recordBegin;
            record.recordNumberEnd = recordEnd;
            record.recordNumberForAuthentification = numberRecordForAuthentification;
            getAFL.records.add(record);

        }
        return getAFL;
    }
}
