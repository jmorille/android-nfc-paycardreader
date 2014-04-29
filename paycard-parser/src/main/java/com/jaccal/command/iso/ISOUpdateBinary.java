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
 * @author Thomas Tarpin-Lyonnet
 *
 */
public class ISOUpdateBinary extends ISOCommand{

    /**
     * Construct a ISO APDU command updates the current selected file<br> 
     * from the offset specified in offset
     * @param offset Hexadecimal value of the offset from where the data have to be updated
     * @param dataToUpdate Hexadecimal value of the data to be updated
     * @throws CardException
     */
    public ISOUpdateBinary(String offset, String dataToUpdate) throws CardException{
        super(ISO7816.CLA_ISO7816,ISO7816.INS_UPDATE_BIN);

        if(offset == null)
            throw new CardException("(UpdateBinary) offset is null");
        if(dataToUpdate == null)
            throw new CardException("(UpdateBinary) dataToUpdate is null");
        if(offset.length() > 4)
            throw new CardException("(UpdateBinary) offset is more than 2 byte long");

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
    	
		b = NumUtil.toStringHex(dataToUpdate);
		setData(b);
		setLc((byte)b.length);        
    }
    
    /**
     * Construct a ISO APDU command updates the file specified by fileId<br> 
     * from the offset specified by offset
     * @param offset Hexadecimal value of the offset from where the data have to be updated
     * @param fileSFI Hexadecimal value of the short file identifier
     * @param dataToUpdate Hexadecimal value of the data to be updated
     * @throws CardException
     */
    public ISOUpdateBinary(String fileSFI, String offset, String dataToUpdate)throws CardException{
        super(ISO7816.CLA_ISO7816,ISO7816.INS_UPDATE_BIN);
 
        if(offset == null)
            throw new CardException("(UpdateBinary) offset is null");
        if(dataToUpdate == null)
            throw new CardException("(UpdateBinary) dataField is null");
        if(fileSFI == null)
            throw new CardException("(UpdateBinary) fileSFI is null");      
        if(offset.length() > 2)
            throw new CardException("(UpdateBinary) offset is more than 1 byte long");
        if(fileSFI.length() > 2)
            throw new CardException("(UpdateBinary) fileSFI is more than 1 byte long");
        
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
                throw new CardException("(UpdateBinary) fileSFI is invalid");
            c = (byte)(0x80 | b[0]);
            setP1(c);
        }
 
		b = NumUtil.toStringHex(dataToUpdate);
		setData(b);
		setLc((byte)b.length);        
    }

}
