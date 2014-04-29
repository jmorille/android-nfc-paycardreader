package net.skora.eccardinfos;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.BitSet;

public class SharedUtils {

    public static   String formatBCDAmount(byte[] amount) {
		StringBuilder res = new StringBuilder(); 
		if (amount[0] != 0) res.append(Integer.toHexString(amount[0] >= 0 ? amount[0] : 256 + amount[0]));
		if (amount[1] == 0) {
			if (res.length() > 0) {
				res.append("00");
			} else {
				res.append("0");
			}
		} else {
			if (res.length() > 0 && amount[1] <= 9) {
				res.append("0");
			}
			res.append(Integer.toHexString(amount[1] >= 0 ? amount[1] : 256 + amount[1]));
		}
		res.append(",");
		String cents = Integer.toHexString(amount[2] >= 0 ? amount[2] : 256 + amount[2]);
		if (cents.length() == 1) res.append("0");
		res.append(cents);
		res.append("€");
		return res.toString();
    }

    public static   String parseLogState(byte logstate) {
		switch (logstate & 0x60 >> 5) {
		case 0: return new String("Laden"); // chargé
		case 1: return new String("Entladen"); // déchargé
		case 2: return new String("Abbuchen"); //débit
		case 3: return new String("Rückbuchen"); // Backposting
		}
		return new String("");
    }

	public static  String byte2Hex(byte[] input) {
		return byte2Hex(input, " ");
	}

    public static  String byte2Hex(byte[] input, String space) {
		StringBuilder result = new StringBuilder();
		
		for (Byte inputbyte : input) {
			result.append(String.format("%02X" + space, inputbyte));
		}
		return result.toString();
	}

    public static byte[] hex2Byte(String hexstr)  {
        String[] hexbytes = hexstr.split("\\s");
        byte[] bytes = new byte[hexbytes.length];
        for (int i = 0; i < hexbytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexbytes[i], 16);
        }
        return bytes;
    }

    public static byte[] getData( byte[]  recv) {
        int recvSize = recv.length;
        if (recvSize>=2) {
            return Arrays.copyOfRange(recv, 0, recvSize - 2);
        }
        return new  byte[0];
    }

    public static String getDataAsString(byte[] recv) {
        byte[] data = getData(recv);
        try {
            String result =   new String(data,  "ISO-8859-1");
            return result;
        } catch (UnsupportedEncodingException e) {
           return "Error : " + e.getMessage();
        }

    }

    public static String convertByteAsBitString(byte b1) {
        String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF));
        s1 = s1.replace(' ', '0');
        return s1;
    }


}
