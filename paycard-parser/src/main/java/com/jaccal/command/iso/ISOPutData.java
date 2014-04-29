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
public class ISOPutData extends ISOCommand{
    
    /**
     * Construct a ISO APDU command that stores one primitive data object or one or <br>
     * more data objects contained in a constructed data object within the current context <br>
     * (e.g., application-specific environment or current DF). 
     * @param p1p2
     * @param dataObject
     * @throws CardException
     */
    public ISOPutData(String p1p2, String dataObject) throws CardException{
        super(ISO7816.CLA_ISO7816, ISO7816.INS_PUT_DATA);
        
        if(dataObject == null)
            throw new CardException("(GetData) dataObject is null");
        if(p1p2 == null)
            throw new CardException("(GetData) p1p2 is null");        
        if(p1p2.length() > 4)
            throw new CardException("(GetData) p1p2 is more than 2 byte long");
        
    	byte [] b = NumUtil.toStringHex(p1p2);
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
    	
		b = NumUtil.toStringHex(dataObject);
		setData(b);
		setLc((byte)b.length);
    }

}
