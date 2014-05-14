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
 * @author Thomas
 *
 */
public class ISOReadBinary extends ISOCommand {

    /**
     * Construct a ISO APDU command that reads the current selected file<br>
     * from the offset specified by offset
     * 
     * @param offset Hexadecimal value of the offset from where to read. Ex: "01AB"
     * @param le Hexadecimal value of the expected length. Ex: "1A"
     */
  public ISOReadBinary(String offset, String le) throws CardException{
    super(ISO7816.CLA_ISO7816,ISO7816.INS_READ_BIN);

    if(offset == null)
        throw new CardException("(ReadBinary) offset is null");
    if(le == null)
        throw new CardException("(ReadBinary) le is null");
    if(offset.length() > 4)
        throw new CardException("(ReadBinary) offset is more than 2 byte long");
    if(le.length() > 2)
        throw new CardException("(ReadBinary) le is more than 1 byte long");
    
	byte [] b = NumUtil.toStringHex(offset);
	if(b.length == 0){
		setP1((byte)0);
		setP2((byte)0);	    
	}
	else if(b.length == 1){
		setP1((byte)0);
		setP2(b[0]);
	}
	else if(b.length == 2){
	    setP1(b[0]);
	    setP2(b[1]);	    
	}

	b = NumUtil.toStringHex(le);
	if(b.length == 0)
	    setLc((byte)0);
	else if(b.length == 1)
	    setLc(b[0]);
  }

  /**
   * Construct a ISO APDU command that reads the file specified<br> 
   * by fileSFI from the offset specified by offset 
   * 
   * @param offset Hexadecimal value of the offset from where to read. Ex: "AB"
   * @param fileSFI Hexadecimal value of the file SFI. Ex: "0C"
   * @param le Hexadecimal value of the expected length. Ex: "1A"
   * @throws CardException
   */
  public ISOReadBinary(String fileSFI, String offset, String le) throws CardException{
      super(ISO7816.CLA_ISO7816,ISO7816.INS_READ_BIN);

      if(offset == null)
          throw new CardException("(ReadBinary) offset is null");
      if(le == null)
          throw new CardException("(ReadBinary) le is null");
      if(fileSFI == null)
          throw new CardException("(ReadBinary) fileSFI is null");      
      if(offset.length() > 2)
          throw new CardException("(ReadBinary) offset is more than 1 byte long");
      if(le.length() > 2)
          throw new CardException("(ReadBinary) le is more than 1 byte long");
      if(fileSFI.length() > 2)
          throw new CardException("(ReadBinary) fileSFI is more than 1 byte long");
    	
      byte c;   
  	  byte [] b = NumUtil.toStringHex(offset);

	  if(b.length == 0)
	      setP2((byte)0);	    
	  else if(b.length == 1)
	      setP2(b[0]);

      if(fileSFI.length() == 0)
          setP1((byte)0);
      else{
          b = NumUtil.toStringHex(fileSFI);
          if(NumUtil.getUnsignedValue(b[0]) > (byte)0x1F)
              throw new CardException("(ReadBinary) fileSFI is invalid");
          c = (byte)(0x80 | b[0]);
          setP1(c);
      }
	
	  b = NumUtil.toStringHex(le);
	  if(b.length == 0)
	      setLc((byte)0);
	  else if(b.length == 1)
	      setLc(b[0]);      
  }

 }
