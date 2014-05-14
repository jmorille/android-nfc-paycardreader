/*
 * Copyright (c) 2005 Chang Sau Sheong, Thomas Tarpin-Lyonnet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jaccal.command.iso;

/**
 * @author Thomas Tarpin-Lyonnet
 * ISO7816 encapsulates constants related to ISO 7816-4.
 * ISO7816 interface contains only static fields.
 */
public interface ISO7816{
	
/**
 * The offset of the APDU command CLA byte. OFFSET_CLA = 0
 */ 
	public static final byte OFFSET_CLA = 0;
	
/**
 * The offset of the APDU command INS byte. OFFSET_INS = 1
 */ 
	public static final byte OFFSET_INS = 1;	
	
/**
 * The offset of the APDU command INS byte. OFFSET_P1 = 2
 */ 
	public static final byte OFFSET_P1 = 2;	
	
/**
 * The offset of the APDU command INS byte. OFFSET_P2 = 3
 */ 
	public static final byte OFFSET_P2 = 3;	

/**
 * The offset of the APDU command INS byte. OFFSET_LC = 4
 */ 
	public static final byte OFFSET_P3 = 4;	
	
/**
 * The offset of the first byte of the APDU command data field. OFFSET_DATA_FIELD = 5
 */	
	public static final byte OFFSET_DATA_FIELD = 5;

/**
 * The offset of the first byte of the APDU command data field. OFFSET_DATA_FIELD = 5
 */	
	public static final byte LENGTH_APDU_HEADER = 5;
	
/**
 * ISO7816 CLA byte value. CLA_ISO7816 = 0x00
 */	
	public static final byte CLA_ISO7816 = (byte)0x00;

/**
 * INS byte of Erase Binary APDU command. INS_ERASE_BINARY = 0x0E
 */	
	public static final byte INS_ERASE_BIN = (byte)0x0E;
	
/**
 * INS byte of Verify CHV APDU command. INS_VERIFY = 0x20
 */	
	public static final byte INS_VERIFY = (byte)0x20;
	
/**
 * INS byte of Manage Channel APDU command. INS_MANAGE_CHANNEL = 0x70
 */	
	public static final byte INS_MANAGE_CHANNEL = (byte)0x70;
	
/**
 * INS byte of External Authenticate APDU command. INS_EXTERNAL_AUTH = 0x82
 */	
	public static final byte INS_EXTERNAL_AUTH = (byte)0x82;

/**
 * INS byte of Get Challenge APDU command. INS_GET_CHAL = 0x84
 */	
	public static final byte INS_GET_CHAL = (byte)0x84;

/**
 * INS byte of Internal Authenticate APDU command. INS_INTERNAL_AUTH = 0x88
 */	
	public static final byte INS_INTERNAL_AUTH = (byte)0x88;
	
/**
 * INS byte of Select File APDU command. INS_SELECT_FILE = 0xA4
 */	
	public static final byte INS_SELECT_FILE = (byte)0xA4;
	
/**
 * INS byte of Read Binary APDU command. INS_READ_BIN = 0xB0
 */	
	public static final byte INS_READ_BIN = (byte)0xB0;

/**
 * INS byte of Read Record APDU command. INS_READ_REC = 0xB2
 */	
	public static final byte INS_READ_REC = (byte)0xB2;
	
/**
 * INS byte of Get Response APDU command. INS_GET_RESP = 0xC0
 */	
	public static final byte INS_GET_RESP = (byte)0xC0;
	
/**
 * INS byte of Envelope APDU command. INS_ENVELOPE = 0xC2
 */	
	public static final byte INS_ENVELOPE = (byte)0xC2;	
	
/**
 * INS byte of Get Data APDU command. INS_GET_DATA = 0xCA
 */	
	public static final byte INS_GET_DATA = (byte)0xCA;	
	
/**
 * INS byte of Write Binary APDU command. INS_WRITE_BIN = 0xD0
 */	
	public static final byte INS_WRITE_BIN = (byte)0xD0;	

/**
 * INS byte of Write Record APDU command. INS_WRITE_REC = 0xD2
 */	
	public static final byte INS_WRITE_REC = (byte)0xD2;		
	
/**
 * INS byte of Update Binary APDU command. INS_UPDATE_BIN = 0xD6
 */	
	public static final byte INS_UPDATE_BIN = (byte)0xD6;	

/**
 * INS byte of Update Binary APDU command. INS_UPDATE_REC = 0xDC
 */	
	public static final byte INS_UPDATE_REC = (byte)0xDC;	

/**
 * INS byte of Put Data APDU command. INS_PUT_DATA = 0xDA
 */	
	public static final byte INS_PUT_DATA = (byte)0xDA;		
	
/**
 * INS byte of Update Data APDU command. INS_UPDATE_DATA = 0xDC
 */	
	public static final byte INS_UPDATE_DATA = (byte)0xDC;		
	
/**
 * INS byte of Append Record APDU command. INS_APPEND_REC = 0xE2
 */	
	public static final byte INS_APPEND_REC = (byte)0xE2;		
	
}