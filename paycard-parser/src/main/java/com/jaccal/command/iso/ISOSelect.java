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

import com.jaccal.CardException;
import com.jaccal.util.NumUtil;

/**
 * 
 * @author Thomas Tarpin-Lyonnet
 *
 */
public class ISOSelect extends ISOCommand {
	public final static int SELECT_FID = 0;
	public final static int SELECT_AID = 1;
	
	/**
	 * Construct a ISO APDU command that selects the file specified by fileId
	 * @param p1 Hexadecimal value of APDU command p1 parameter. Ex:"0C"
	 * @param p2 Hexadecimal value of APDU command p2 parameter. Ex:"0A"
	 * @param lc Hexadecimal value of APDU command lc parameter. Ex:"0C"
	 * @param fileId Hexadecimal value of the file identifier
	 * @throws CardException
	 */
	public ISOSelect(String p1, String p2, String lc, String fileId) throws CardException{
		super(ISO7816.CLA_ISO7816,ISO7816.INS_SELECT_FILE);

		if(p1 == null)
		    throw new CardException("(Select) p1 is null");
		if(p2 == null)
		    throw new CardException("(Select) p2 is null");
		if(lc == null)
		    throw new CardException("(Select) lc is null");
		if(fileId == null)
		    throw new CardException("(Select) fileId is null");
		if(p1.length() > 2)
		    throw new CardException("(Select) p1 is more than 1 byte long");
		if(p2.length() > 2)
		    throw new CardException("(Select) p2 is more than 1 byte long");
		if(lc.length() > 2)
		    throw new CardException("(Select) lc is more than 1 byte long");

		byte [] b = NumUtil.toStringHex(fileId);
		setData(b);
		
		byte [] c = NumUtil.toStringHex(p1);
		setP1(c[0]);
		
		c = NumUtil.toStringHex(p2);
		setP2(c[0]);
		
		c = NumUtil.toStringHex(lc);
		if(NumUtil.getUnsignedValue(c[0]) != b.length)
		    throw new CardException("(Select) fileId length and lc are different");
		setLc(c[0]);		
	}
  
	/**
	 * Construct a ISO APDU command that selects the file specified by fileId.
	 * @param selectType The type of file selection (by FID or AID)
	 * @param fileId Hexadecimal value of the file identifier to be selected
	 * @throws CardException
	 */
	public ISOSelect(int selectType, String fileId) throws CardException{
		super(ISO7816.CLA_ISO7816,ISO7816.INS_SELECT_FILE);
		
		if(selectType == SELECT_FID)
			setP1((byte)0x00);
		else if(selectType == SELECT_AID)
			setP1((byte)0x04);
		else
		    throw new CardException("(Select) selectType is invalid");

		if(fileId == null)
		    throw new CardException("(Select) fileId is null");
		fileId = fileId.replaceAll(" ","");
		byte [] b = NumUtil.toStringHex(fileId);
		setData(b);
		setLc((byte)b.length);
	}
 }
