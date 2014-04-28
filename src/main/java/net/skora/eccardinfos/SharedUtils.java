package net.skora.eccardinfos;

import java.io.IOException;

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
}
