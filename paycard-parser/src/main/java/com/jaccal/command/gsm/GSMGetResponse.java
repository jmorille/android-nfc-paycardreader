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
package com.jaccal.command.gsm;

import com.jaccal.CardException;
import com.jaccal.CardResponse;
import com.jaccal.StatusWord;
import com.jaccal.command.iso.ISOGetResponse;
import com.jaccal.util.NumUtil;

import java.io.IOException;

/**
 * @author Thomas Tarpin-Lyonnet
 *
 */
public class GSMGetResponse extends ISOGetResponse{
	
	public GSMGetResponse(CardResponse cr) throws CardException{
		super(cr);
		super.setCla(GSM11_11.CLA_GSM11_11);
	}
	
	/**
	 * Check SW1 is proper for a GSM Get Response (SW1 should be 0x9F)
	 */
    public void checkSW(StatusWord sw) throws CardException{
		try {
	    	if(NumUtil.getUnsignedValue(sw.getSw1()) != 0x9F)
	    		throw new CardException("(GetResponse) SWs (0x" + NumUtil.hex2String(sw.getBytes()) + ") has an invalid value for a Get Response. SWs should be equal to 0x9F**.");			
		} catch (IOException e) {
			throw new CardException(e);
		}
    }
    
    /* (non-Javadoc)
     * @see com.jaccal.command.Command#setCla(byte)
     */
    public void setCla(byte cla){
    }    
}
