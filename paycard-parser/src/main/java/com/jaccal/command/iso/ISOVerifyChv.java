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
public class ISOVerifyChv extends ISOCommand{

    /**
     * Construct a ISO APDU command that verifies a CHV. 
     * @param chvRefId The Id of the CHV to be verified
     * @param chv The CHV value
     * @throws CardException
     */
    public ISOVerifyChv(String chvRefId, String chv) throws CardException{
        super(ISO7816.CLA_ISO7816,ISO7816.INS_VERIFY);
        
        if(chvRefId == null)
            throw new CardException("(VerifyChv) chvRefId is null");
        if(chv == null)
            throw new CardException("(VerifyChv) chv is null");
        if(chvRefId.length() > 2)
            throw new CardException("(VerifyChv) chvRefId is more than 1 byte long");
 
        byte [] b;
        
        b = NumUtil.toStringHex(chvRefId);
        if(b.length == 0)
            setP2((byte)0);
        else{
            b = NumUtil.toStringHex(chvRefId);
            if(b[0] > (byte)0x1F)
                throw new CardException("(VerifyChv) chvRefId is invalid");
            setP2(b[0]);
        }
            
		b = NumUtil.toStringHex(chv);
		setData(b);
		setLc((byte)b.length);
    }

    /**
     * Construct a ISO APDU command that verifies a CHV. 
     * @param chv The CHV value
     * @throws CardException
     */
    public ISOVerifyChv(String chv) throws CardException{
        super(ISO7816.CLA_ISO7816,ISO7816.INS_VERIFY);
        
        if(chv == null)
            throw new CardException("(VerifyChv) chv is null");
 
        byte [] b = NumUtil.toStringHex(chv);
		setData(b);
		setLc((byte)b.length);
    }

}
