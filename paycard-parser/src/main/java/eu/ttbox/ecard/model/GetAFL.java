package eu.ttbox.ecard.model;


import com.jaccal.util.NumUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import eu.ttbox.ecard.util.ByteHelper;
import eu.ttbox.ecard.util.TLVParser;

public class GetAFL {

    public byte[] recv;
    // Parse 1
    public byte[] featuresSupported;
    public byte[] afls;

    // Parse 2
    public ArrayList<AflRecord> records = new ArrayList<AflRecord>();




}
