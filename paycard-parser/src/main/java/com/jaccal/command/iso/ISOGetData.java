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
public class ISOGetData extends ISOCommand{
    
    /**
     * Construct a ISO APDU command that permits to retrieve one primitive data object <br>
     * or to retrieve one or more data objects contained in a constructed data object, within <br>
     * the current context.
     * @param p1p2
     * @param le
     * @throws CardException
     */
    public ISOGetData(String p1p2, String le) throws CardException{
        super(ISO7816.CLA_ISO7816, ISO7816.INS_GET_DATA);
        
        if(le == null)
            throw new CardException("(GetData) le is null");
        if(p1p2 == null)
            throw new CardException("(GetData) p1p2 is null");        
        if(le.length() > 2)
            throw new CardException("(GetData) le is more than 1 byte long");
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
    	
        b = NumUtil.toStringHex(le);
        if(b.length == 0)
           setLc((byte)0);
        else
            setLc(b[0]);    	
    }
    
}
