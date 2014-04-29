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
public class ISOGetChallenge extends ISOCommand{

    /**
     * Construct a ISO APDU command that asks a random to the card
     * @param le Length of the random to be returned
     * @throws CardException
     */
    public ISOGetChallenge(String le) throws CardException {
        super(ISO7816.CLA_ISO7816,ISO7816.INS_GET_CHAL);
        
        if(le == null)
            throw new CardException("(GetChallenge) le is null");
        if(le.length() > 2)
            throw new CardException("(GetChallenge) le is more than 1 byte long");
        
        byte [] b = NumUtil.toStringHex(le);
        
        if(b.length == 0)
            setLc((byte)0);
        else
            setLc(b[0]);
    }
}
