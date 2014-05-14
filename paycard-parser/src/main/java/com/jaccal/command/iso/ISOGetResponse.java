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
import com.jaccal.CardResponse;
import com.jaccal.StatusWord;
import com.jaccal.util.NumUtil;

import java.io.IOException;

/**
 * @author Thomas Tarpin-Lyonnet
 *
 */
public class ISOGetResponse extends ISOCommand{
    
    /**
     * 
     * @param le
     * @throws CardException
     */
    public ISOGetResponse(String le) throws CardException{
        super(ISO7816.CLA_ISO7816, ISO7816.INS_GET_RESP);
        
        if(le == null)
            throw new CardException("(GetResponse) le is null");
        if(le.length() > 2)
            throw new CardException("(GetResponse) le is more than 1 byte long");
        
        byte [] b = NumUtil.toStringHex(le);
        if(b.length == 0)
           setLc((byte)0);
        else
            setLc(b[0]);
    }
    
    public ISOGetResponse(CardResponse cr) throws CardException{
        super(ISO7816.CLA_ISO7816, ISO7816.INS_GET_RESP);
        
        if(cr == null)
            throw new CardException("(GetResponse) cr is null");
        
        checkSW(cr.getStatusWord());
        
        setLc(cr.getStatusWord().getSw2());
    } 
    
    /**
     * Check SW1 is proper Status Word for a ISO Get Response (SW1 should be 0x61)
     * @param sw
     * @throws CardException
     */
    public void checkSW(StatusWord sw) throws CardException{
    	try {
			if(NumUtil.getUnsignedValue(sw.getSw1()) != 0x61)
				throw new CardException("(GetResponse) SWs (0x" + NumUtil.hex2String(sw.getBytes()) + ") have an invalid value for a Get Response. SWs should be equal to 0x61**.");
		} catch (IOException e) {
			throw new CardException(e);
		}
    }
}
